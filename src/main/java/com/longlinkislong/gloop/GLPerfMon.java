/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.lwjgl.opengl.AMDPerformanceMonitor;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public final class GLPerfMon extends GLObject {

    private static final boolean PERF_MON_ENABLED;
    private static final int INVALID_MONITOR_ID = -1;
    private static final int INVALID_GROUP_ID = -1;
    private static final int INVALID_COUNTER_ID = -1;
    private static final Logger LOGGER = LoggerFactory.getLogger("GLPerfMon");
    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final int MAX_NAME_LENGTH = 256;

    static {
        PERF_MON_ENABLED = Boolean.getBoolean("com.longlinkislong.gloop.glperfmon.enabled");
    }

    private int monitorId = INVALID_MONITOR_ID;

    private Map<String, Group> groups;

    private Optional<Group> getGroupById(final int id) {
        return this.groups.values()
                .stream()
                .filter(g -> g.groupId == id)
                .findFirst();
    }

    public final class Group extends GLObject {

        private String name;
        private int groupId = INVALID_GROUP_ID;
        private Map<String, Counter> counters;
        private int maxActiveCounters;

        Group(GLThread thread) {
            super(thread);
        }

        public int getMaxActiveCounters() {
            return this.maxActiveCounters;
        }

        public String getName() {
            return this.name;
        }

        public Counter getCounter(final String name) {
            return this.counters.get(name);
        }

        private Optional<Counter> getCounterById(final int id) {
            return counters.values()
                    .stream()
                    .filter(c -> c.counterId == id)
                    .findFirst();
        }

        public Set<String> getCounterNames() {
            return this.counters.keySet();
        }

        public void selectCounters(final String... names) {
            new SelectCountersTask(names).glRun(this.getThread());
        }

        public class SelectCountersTask extends GLTask {

            private final IntBuffer counterIDs;

            public SelectCountersTask(String... names) {
                this.counterIDs = ByteBuffer.allocateDirect(names.length * Integer.BYTES)
                        .order(ByteOrder.nativeOrder())
                        .asIntBuffer();

                for (String name : names) {
                    if (counters.containsKey(name)) {
                        this.counterIDs.put(counters.get(name).counterId);
                    }
                }

                this.counterIDs.flip();
            }

            @Override
            public void run() {
                AMDPerformanceMonitor.glSelectPerfMonitorCountersAMD(
                        GLPerfMon.this.monitorId,
                        true /* enable */,
                        Group.this.groupId,
                        counterIDs);
            }
        }

        public class CounterRangeQuery<Type> extends GLQuery<CounterRange<Type>> {

            private final Counter counter;

            public CounterRangeQuery(final String name) {
                this.counter = counters.get(name);
            }

            @Override
            public CounterRange<Type> call() throws Exception {
                final ByteBuffer data;
                switch (counter.type) {
                    case GL_UNSIGNED_INT:
                    case GL_FLOAT:
                    case GL_PERCENTAGE_AMD:
                        data = NativeTools.getInstance().nextDWord();
                        break;
                    case GL_UNSIGNED_INT64_AMD:
                        data = NativeTools.getInstance().nextQWord();
                        break;
                    default:
                        throw new GLException("Invalid Counter type: " + counter.type);
                }

                AMDPerformanceMonitor.glGetPerfMonitorCounterInfoAMD(
                        groupId,
                        counter.counterId,
                        AMDPerformanceMonitor.GL_COUNTER_RANGE_AMD,
                        data);

                switch (counter.type) {
                    case GL_UNSIGNED_INT: {
                        final int min = data.getInt(0);
                        final int max = data.getInt(Integer.BYTES);

                        return (CounterRange<Type>) new CounterRange<>(min, max);
                    }
                    case GL_FLOAT: {
                        final float min = data.getFloat(0);
                        final float max = data.getFloat(Float.BYTES);

                        return (CounterRange<Type>) new CounterRange<>(min, max);
                    }
                    case GL_UNSIGNED_INT64_AMD: {
                        final long min = data.getLong(0);
                        final long max = data.getLong(Long.BYTES);

                        return (CounterRange<Type>) new CounterRange<>(min, max);
                    }
                    case GL_PERCENTAGE_AMD: {
                        return (CounterRange<Type>) new CounterRange(0f, 100f);
                    }
                    default:
                        throw new GLException("Invalid counter type: " + counter.type);
                }
            }
        }
    }

    public static final class CounterRange<Type> {

        public final Type min;
        public final Type max;

        CounterRange(final Type min, final Type max) {
            this.min = min;
            this.max = max;
        }
    }

    public final class Counter extends GLObject {

        private String name;
        private int counterId = INVALID_COUNTER_ID;
        private GLPerfMonCounterType type;

        Counter(GLThread thread) {
            super(thread);
        }

        public String getName() {
            return this.name;
        }

        public GLPerfMonCounterType getType() {
            return this.type;
        }
    }

    public GLPerfMon(final GLThread thread) {
        super(thread);
        this.init();
    }

    public GLPerfMon() {
        this(GLThread.getAny());
    }

    public void init() {
        new InitTask().glRun(this.getThread());
    }

    public final class InitTask extends GLTask {

        @Override
        public void run() {
            if (!PERF_MON_ENABLED) {
                LOGGER.info(GLOOP_MARKER, "Performance monitor is disabled. All performance queries will be ignored.");
            } else if (!GL.getCapabilities().GL_AMD_performance_monitor) {
                LOGGER.warn(GL_MARKER, "GL_AMD_performance_monitor is not supported. All performance queries will be ignored.");
            } else if (GLPerfMon.this.monitorId != INVALID_MONITOR_ID) {
                throw new GLException("GLPerfMon object is already initialized!");
            } else {
                GLPerfMon.this.monitorId = AMDPerformanceMonitor.glGenPerfMonitorsAMD();
                LOGGER.trace(GL_MARKER, "glGenPerfMonitorsAMD()");
                assert checkGLError() : glErrorMsg("glGenPerfMonitorsAMD() failed!");
                this.identifyGroupsAndCounters();
            }
        }

        private void identifyGroupsAndCounters() {
            final Map<String, Group> groups = new HashMap<>();
            final int numGroups;
            {
                final IntBuffer n = NativeTools.getInstance()
                        .nextWord()
                        .asIntBuffer();

                AMDPerformanceMonitor.glGetPerfMonitorGroupsAMD(n, null);
                numGroups = n.get(0);
            }

            {
                final IntBuffer groupIds = ByteBuffer.allocateDirect(numGroups * Integer.BYTES)
                        .order(ByteOrder.nativeOrder())
                        .asIntBuffer();

                AMDPerformanceMonitor.glGetPerfMonitorGroupsAMD(null, groupIds);

                for (int i = 0; i < numGroups; i++) {
                    final Group group = new Group(GLPerfMon.this.getThread());

                    group.groupId = groupIds.get(i);

                    {
                        final IntBuffer nameLen = NativeTools.getInstance()
                                .nextWord()
                                .asIntBuffer();

                        nameLen.put(0, MAX_NAME_LENGTH);

                        final ByteBuffer rawNameBytes = ByteBuffer.allocateDirect(MAX_NAME_LENGTH)
                                .order(ByteOrder.nativeOrder());

                        AMDPerformanceMonitor.glGetPerfMonitorGroupStringAMD(
                                group.groupId,
                                nameLen,
                                rawNameBytes);

                        final byte[] nameBytes = new byte[MAX_NAME_LENGTH];

                        for (int j = 0; j < MAX_NAME_LENGTH; j++) {
                            nameBytes[j] = rawNameBytes.get(j);
                        }

                        group.name = new String(nameBytes);
                    }

                    {
                        final int numCounters;
                        final IntBuffer rawNumCounters = NativeTools.getInstance()
                                .nextWord()
                                .asIntBuffer();

                        final IntBuffer rawMaxActiveCounters = NativeTools.getInstance()
                                .nextWord()
                                .asIntBuffer();

                        AMDPerformanceMonitor.glGetPerfMonitorCountersAMD(
                                group.groupId,
                                rawNumCounters,
                                rawMaxActiveCounters,
                                null);

                        group.maxActiveCounters = rawMaxActiveCounters.get(0);

                        numCounters = rawNumCounters.get(0);

                        final IntBuffer counterIds = ByteBuffer.allocateDirect(numCounters * Integer.BYTES)
                                .order(ByteOrder.nativeOrder())
                                .asIntBuffer();

                        AMDPerformanceMonitor.glGetPerfMonitorCountersAMD(
                                group.groupId,
                                null,
                                null,
                                counterIds);

                        final Map<String, Counter> counters = new HashMap<>();

                        for (int j = 0; j < numCounters; j++) {
                            final Counter counter = new Counter(GLPerfMon.this.getThread());

                            counter.counterId = counterIds.get(j);

                            final IntBuffer nameLen = NativeTools.getInstance().nextWord().asIntBuffer();
                            nameLen.put(0, MAX_NAME_LENGTH);

                            final ByteBuffer rawNameBytes = ByteBuffer.allocateDirect(MAX_NAME_LENGTH)
                                    .order(ByteOrder.nativeOrder());

                            AMDPerformanceMonitor.glGetPerfMonitorCounterStringAMD(
                                    group.groupId,
                                    counter.counterId,
                                    nameLen,
                                    rawNameBytes);

                            final byte[] nameBytes = new byte[MAX_NAME_LENGTH];

                            for (int k = 0; i < MAX_NAME_LENGTH; k++) {
                                nameBytes[k] = rawNameBytes.get(k);
                            }

                            counter.name = new String(nameBytes);

                            {
                                final IntBuffer rawType = NativeTools.getInstance()
                                        .nextWord()
                                        .asIntBuffer();

                                AMDPerformanceMonitor.glGetPerfMonitorCounterInfoAMD(
                                        group.groupId,
                                        counter.counterId,
                                        AMDPerformanceMonitor.GL_COUNTER_TYPE_AMD,
                                        rawType);

                                counter.type = GLPerfMonCounterType.of(rawType.get(0)).get();
                            }

                            counters.put(counter.name, counter);
                        }

                        group.counters = Collections.unmodifiableMap(counters);
                    }

                    groups.put(group.name, group);
                }
            }

            GLPerfMon.this.groups = Collections.unmodifiableMap(groups);
        }
    }

    public void begin() {
        new BeginTask().glRun(this.getThread());
    }

    public class BeginTask extends GLTask {

        @Override
        public void run() {
            if (!PERF_MON_ENABLED || !GL.getCapabilities().GL_AMD_performance_monitor) {
                LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; BeginTask ignored.");
            } else if (GLPerfMon.this.monitorId == INVALID_MONITOR_ID) {
                throw new GLException("GLPerfMon object is invalid!");
            } else {
                AMDPerformanceMonitor.glBeginPerfMonitorAMD(GLPerfMon.this.monitorId);
                LOGGER.trace(GL_MARKER, "glBeginPerfMonitorAMD({})", GLPerfMon.this.monitorId);
                assert checkGLError() : glErrorMsg("glBeginPerfMonitorAMD(I) failed!");
            }
        }
    }

    public void end() {
        new EndTask().glRun(this.getThread());
    }

    public final class EndTask extends GLTask {

        @Override
        public void run() {
            if (!PERF_MON_ENABLED || !GL.getCapabilities().GL_AMD_performance_monitor) {
                LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; EndTask ignored.");
            } else if (GLPerfMon.this.monitorId == INVALID_MONITOR_ID) {
                throw new GLException("GLPerfMon object is invalid!");
            } else {
                AMDPerformanceMonitor.glEndPerfMonitorAMD(GLPerfMon.this.monitorId);
                LOGGER.trace(GL_MARKER, "glEndPerfMonitorAMD({})", GLPerfMon.this.monitorId);
                assert checkGLError() : glErrorMsg("glEndPerfMonitorAMD(I) failed!");
            }
        }
    }

    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    public final class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!PERF_MON_ENABLED || !GL.getCapabilities().GL_AMD_performance_monitor) {
                LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; DeleteTask ignored.");
            } else {
                LOGGER.trace(GL_MARKER, "glDeletePerfMonitorAMD({})", GLPerfMon.this.monitorId);
                AMDPerformanceMonitor.glDeletePerfMonitorsAMD(GLPerfMon.this.monitorId);
                assert checkGLError() : glErrorMsg("glDeletePerfMonitorAMD(I) failed!");
                GLPerfMon.this.monitorId = INVALID_MONITOR_ID;
                GLPerfMon.this.groups = null;
            }
        }
    }

    public List<String> getData() {
        return new DataQuery().glCall(this.getThread());
    }

    public void logData() {
        this.getData().forEach(line -> LOGGER.debug(GLOOP_MARKER, line));
    }

    public final class DataQuery extends GLQuery<List<String>> {

        @Override
        public List<String> call() throws Exception {
            final int resultSize;
            {
                final IntBuffer rawResultSize = NativeTools.getInstance()
                        .nextWord()
                        .asIntBuffer();

                AMDPerformanceMonitor.glGetPerfMonitorCounterDataAMD(
                        monitorId,
                        AMDPerformanceMonitor.GL_PERFMON_RESULT_SIZE_AMD,
                        rawResultSize, null);
                resultSize = rawResultSize.get(0);
            }

            final ByteBuffer rawData = ByteBuffer.allocateDirect(resultSize * Integer.BYTES)
                    .order(ByteOrder.nativeOrder());

            final int bytesWritten;
            {
                final ByteBuffer rawBytesWritten = NativeTools.getInstance().nextWord();

                AMDPerformanceMonitor.glGetPerfMonitorCounterDataAMD(
                        monitorId,
                        AMDPerformanceMonitor.GL_PERFMON_RESULT_AMD,
                        resultSize,
                        rawData,
                        rawBytesWritten);

                bytesWritten = rawBytesWritten.get(0);
            }

            final List<String> out = new ArrayList<>();

            int byteCount = 0;

            while (byteCount < bytesWritten) {
                final int groupId = rawData.getInt(byteCount);

                byteCount += Integer.BYTES;

                final int counterId = rawData.getInt(byteCount);

                byteCount += Integer.BYTES;

                final Group group = getGroupById(groupId).get();
                final Counter counter = group.getCounterById(counterId).get();

                final StringBuilder line = new StringBuilder();

                line.append("[");
                line.append(group.getName());
                line.append("/");
                line.append(counter.getName());
                line.append("] :");

                switch (counter.type) {
                    case GL_UNSIGNED_INT:
                        line.append(rawData.getInt(byteCount));
                        byteCount += Integer.BYTES;
                        break;
                    case GL_UNSIGNED_INT64_AMD:
                        line.append(rawData.getLong(byteCount));
                        byteCount += Long.BYTES;
                        break;
                    case GL_FLOAT:
                    case GL_PERCENTAGE_AMD:
                        line.append(rawData.getFloat(byteCount));
                        byteCount += Float.BYTES;
                        break;
                    default:
                        throw new GLException("Unknown counter type: " + counter.type);
                }

                out.add(line.toString());
            }

            return out;
        }

    }
}
