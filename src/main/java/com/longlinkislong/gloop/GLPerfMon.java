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
 * Implementation of GL_AMD_performance_monitor as an Object. This object will
 * quietly disable itself if it is unsupported.
 *
 * @author zmichaels
 * @since 16.01.04
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

    private boolean isEnabled;

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

    /**
     * Retrieves a monitor group by name.
     *
     * @param name the name of the monitor group.
     * @return the group if it exists.
     * @since 16.01.04
     */
    public Optional<Group> getGroup(final String name) {
        return Optional.of(this.groups.get(name));
    }

    /**
     * Retrieves the set of group names.
     *
     * @return the group names.
     * @since 16.01.04
     */
    public Set<String> getGroupNames() {
        return this.groups.keySet();
    }

    /**
     * An implementation of a performance monitor group. Each group can contain
     * multiple counters.
     *
     * @since 16.01.04
     */
    public final class Group {

        private String name;
        private int groupId = INVALID_GROUP_ID;
        private Map<String, Counter> counters;
        private int maxActiveCounters;

        /**
         * Retrieves the maximum number of active counters for the group.
         *
         * @return the maximum active counters.
         * @since 16.01.04
         */
        public int getMaxActiveCounters() {
            return this.maxActiveCounters;
        }

        /**
         * Retrieves the name of the group.
         *
         * @return the name.
         * @since 16.01.04
         */
        public String getName() {
            return this.name;
        }

        /**
         * Retrieves the counter by name if it exists.
         *
         * @param name the counter to search for.
         * @return the name of the counter.
         * @since 16.01.04
         */
        public Optional<Counter> getCounter(final String name) {
            return Optional.of(this.counters.get(name));
        }

        private Optional<Counter> getCounterById(final int id) {
            return counters.values()
                    .stream()
                    .filter(c -> c.counterId == id)
                    .findFirst();
        }

        /**
         * Retrieves the set of counter names.
         *
         * @return the counter names.
         * @since 16.01.04
         */
        public Set<String> getCounterNames() {
            return this.counters.keySet();
        }
    }

    /**
     * An implementation of a performance monitor counter.
     *
     * @since 16.01.04
     */
    public final class Counter {

        private String name;
        private int counterId = INVALID_COUNTER_ID;
        private GLPerfMonCounterType type;

        /**
         * Retrieves the name of the counter.
         *
         * @return the name of the counter.
         * @since 16.01.04
         */
        public String getName() {
            return this.name;
        }

        /**
         * Retrieves the counter type.
         *
         * @return the counter type.
         * @since 16.01.04
         */
        public GLPerfMonCounterType getType() {
            return this.type;
        }
    }

    /**
     * Constructs a new instance of GLPerfMon on the specified thread. No
     * operation will take place if GL_AMD_performance_monitor is not supported.
     *
     * @param thread the OpenGL thread to associate with the object.
     * @since 16.01.04
     */
    public GLPerfMon(final GLThread thread) {
        super(thread);
        this.init();
    }

    /**
     * Selects the specified counters for profiling.
     *
     * @param group the counter group to select.
     * @param counters the set of counters.
     * @since 16.01.04
     */
    public void selectCounters(final String group, final String... counters) {
        new SelectCountersTask(group, counters).glRun(this.getThread());
    }

    /**
     * A GLTask that selects counters to use for profiling.
     *
     * @since 16.01.04
     */
    public final class SelectCountersTask extends GLTask {

        private final String groupName;
        private final String[] counters;

        /**
         * Constructs a new SelectCountersTask.
         *
         * @param group the group to select the counters from.
         * @param counters the set of counters.
         * @since 16.01.04
         */
        public SelectCountersTask(final String group, final String... counters) {
            this.groupName = Objects.requireNonNull(group, "Group cannot be null!");
            this.counters = new String[counters.length];

            System.arraycopy(counters, 0, this.counters, 0, counters.length);
        }

        @Override
        public void run() {
            if (!GLPerfMon.this.isEnabled) {
                LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; SelectCountersTask ignored.");
            } else if (GLPerfMon.this.monitorId == INVALID_MONITOR_ID) {
                throw new GLException("GLPerfMon object is not initialized!");
            } else {
                final IntBuffer counterIds = ByteBuffer.allocateDirect(Integer.BYTES * this.counters.length)
                        .order(ByteOrder.nativeOrder())
                        .asIntBuffer();

                final Optional<Group> group = GLPerfMon.this.getGroup(this.groupName);

                if (!group.isPresent()) {
                    throw new GLException("Invalid group: " + this.groupName);
                } else {
                    for (String counterName : counters) {
                        final Optional<Counter> counter = group.get().getCounter(counterName);

                        if (!counter.isPresent()) {
                            throw new GLException("Invalid counter: " + counterName + " in group: " + this.groupName);
                        } else {
                            counterIds.put(counter.get().counterId);
                        }
                    }

                    LOGGER.trace(GL_MARKER, "glSelectPerfMonitorCountersAMD({}, true, {}, {})",
                            GLPerfMon.this.monitorId,
                            group.get().groupId,
                            counterIds);

                    AMDPerformanceMonitor.glSelectPerfMonitorCountersAMD(
                            GLPerfMon.this.monitorId,
                            true,
                            group.get().groupId,
                            counterIds);

                    assert checkGLError() : glErrorMsg("glSelectPerfMonitorCountersAMD(IBI*) failed!");
                }
            }
        }
    }

    /**
     * Initializes the GLPerfMon object. This will allocate a new monitor and
     * discover all groups and counters. No operation will take place if
     * GL_amd_performance_monitor is not available or if performance monitors
     * are disabled.
     *
     * @since 16.01.04
     */
    public void init() {
        new InitTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initializes the GLPerfMon object. This will allocate a new
     * monitor and discover all groups and counters. No operation will take
     * place if GL_amd_performance_monitor is not available or if performance
     * monitors are disabled.
     *
     * @since 16.01.04
     */
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
                GLPerfMon.this.isEnabled = true;
            }
        }

        private void identifyGroupsAndCounters() {
            final Map<String, Group> groups = new HashMap<>();
            final int numGroups;
            {
                final IntBuffer n = NativeTools.getInstance()
                        .nextWord()
                        .asIntBuffer();

                LOGGER.trace(GL_MARKER, "glGetPerfMonitorGroupsAMD({}, null)", n);
                AMDPerformanceMonitor.glGetPerfMonitorGroupsAMD(n, null);
                assert checkGLError() : glErrorMsg("glGetPerfMonitorGroupsAMD(**) failed!");

                numGroups = n.get(0);
                LOGGER.trace(GLOOP_MARKER, "Found {} group(s)!", numGroups);
            }

            {
                final IntBuffer groupIds = ByteBuffer.allocateDirect(numGroups * Integer.BYTES)
                        .order(ByteOrder.nativeOrder())
                        .asIntBuffer();

                LOGGER.trace(GL_MARKER, "glGetPerfMonitorGroupsAMD(null, {})", groupIds);
                AMDPerformanceMonitor.glGetPerfMonitorGroupsAMD(null, groupIds);
                assert checkGLError() : glErrorMsg("glGetPerfMonitorGroupsAMD(**) failed!");

                for (int i = 0; i < numGroups; i++) {
                    final Group group = new Group();

                    group.groupId = groupIds.get(i);
                    LOGGER.trace(GLOOP_MARKER, "Found group[id={}]", group.groupId);

                    {
                        final IntBuffer rawNameLen = NativeTools.getInstance()
                                .nextWord()
                                .asIntBuffer();

                        rawNameLen.put(0, MAX_NAME_LENGTH);

                        final ByteBuffer rawNameBytes = ByteBuffer.allocateDirect(MAX_NAME_LENGTH)
                                .order(ByteOrder.nativeOrder());

                        LOGGER.trace(GL_MARKER, "glGetPerfMonitorGroupStringAMD({}, {}, {})",
                                group.groupId,
                                rawNameLen,
                                rawNameBytes);

                        AMDPerformanceMonitor.glGetPerfMonitorGroupStringAMD(
                                group.groupId,
                                rawNameLen,
                                rawNameBytes);

                        assert checkGLError() : glErrorMsg("glGetPerfMonitorGroupStringAMD(I**) failed!");

                        final byte[] nameBytes = new byte[MAX_NAME_LENGTH];

                        for (int j = 0; j < MAX_NAME_LENGTH; j++) {
                            nameBytes[j] = rawNameBytes.get(j);
                        }

                        group.name = new String(nameBytes);
                        LOGGER.trace(GLOOP_MARKER, "group[id={}] is named: {}", group.groupId, group.name);
                    }

                    {
                        final int numCounters;
                        final IntBuffer rawNumCounters = NativeTools.getInstance()
                                .nextWord()
                                .asIntBuffer();

                        final IntBuffer rawMaxActiveCounters = NativeTools.getInstance()
                                .nextWord()
                                .asIntBuffer();

                        LOGGER.trace(GL_MARKER, "glGetPerfMonitorCountersAMD({}, {}, {}, null)",
                                group.groupId,
                                rawNumCounters,
                                rawMaxActiveCounters);

                        AMDPerformanceMonitor.glGetPerfMonitorCountersAMD(
                                group.groupId,
                                rawNumCounters,
                                rawMaxActiveCounters,
                                null);

                        assert checkGLError() : glErrorMsg("glGetPerfMonitorCountersAMD(I***)");

                        group.maxActiveCounters = rawMaxActiveCounters.get(0);
                        LOGGER.trace(GLOOP_MARKER, "Group[name={}] supports {} active counter(s).",
                                group.name,
                                group.maxActiveCounters);

                        numCounters = rawNumCounters.get(0);
                        LOGGER.trace(GLOOP_MARKER, "Found {} counter(s)!", numCounters);

                        final IntBuffer counterIds = ByteBuffer.allocateDirect(numCounters * Integer.BYTES)
                                .order(ByteOrder.nativeOrder())
                                .asIntBuffer();

                        LOGGER.trace(GL_MARKER, "glGetPerfMonitorCountersAMD({}, null, null, {})", counterIds);
                        AMDPerformanceMonitor.glGetPerfMonitorCountersAMD(
                                group.groupId,
                                null,
                                null,
                                counterIds);
                        assert checkGLError() : glErrorMsg("glGetPerfMonitorCounetersAMD(I***) failed!");

                        final Map<String, Counter> counters = new HashMap<>();

                        for (int j = 0; j < numCounters; j++) {
                            final Counter counter = new Counter();

                            counter.counterId = counterIds.get(j);

                            LOGGER.trace(GLOOP_MARKER, "Found counter[id={}]!", counter.counterId);

                            final IntBuffer nameLen = NativeTools.getInstance().nextWord().asIntBuffer();
                            nameLen.put(0, MAX_NAME_LENGTH);

                            final ByteBuffer rawNameBytes = ByteBuffer.allocateDirect(MAX_NAME_LENGTH)
                                    .order(ByteOrder.nativeOrder());

                            LOGGER.trace(GL_MARKER, "glGetPerfMonitorCounterStringAMD({}, {}, {}, {})",
                                    group.groupId,
                                    counter.counterId,
                                    nameLen,
                                    rawNameBytes);

                            AMDPerformanceMonitor.glGetPerfMonitorCounterStringAMD(
                                    group.groupId,
                                    counter.counterId,
                                    nameLen,
                                    rawNameBytes);

                            assert checkGLError() : glErrorMsg("glGetPerfMonitorCounterStringAMD(II**) failed!");

                            final byte[] nameBytes = new byte[MAX_NAME_LENGTH];

                            for (int k = 0; i < MAX_NAME_LENGTH; k++) {
                                nameBytes[k] = rawNameBytes.get(k);
                            }

                            counter.name = new String(nameBytes);
                            LOGGER.trace(GLOOP_MARKER, "Counter[id={}] is named {}.",
                                    counter.counterId,
                                    counter.name);

                            {
                                final IntBuffer rawType = NativeTools.getInstance()
                                        .nextWord()
                                        .asIntBuffer();

                                LOGGER.trace(GL_MARKER, "glGetPerfMonitorCounterInfoAMD({}, {}, GL_COUNTER_TYPE_AMD, {})",
                                        group.groupId,
                                        counter.counterId,
                                        rawType);

                                AMDPerformanceMonitor.glGetPerfMonitorCounterInfoAMD(
                                        group.groupId,
                                        counter.counterId,
                                        AMDPerformanceMonitor.GL_COUNTER_TYPE_AMD,
                                        rawType);

                                assert checkGLError() : glErrorMsg("glGetPerfMonitorCounterInfoAMD(III*) failed!");

                                final Optional<GLPerfMonCounterType> type = GLPerfMonCounterType.of(rawType.get(0));

                                if (!type.isPresent()) {
                                    throw new GLException("Unsupported counter type: value=" + rawType.get(0));
                                } else {
                                    counter.type = type.get();
                                    LOGGER.trace(GLOOP_MARKER, "Counter[name={}] is of type: {}",
                                            counter.name,
                                            counter.type);
                                }
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

    /**
     * Starts the profiling period.
     *
     * @since 16.01.04
     */
    public void begin() {
        if (!this.isEnabled) {
            LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled. Call to begin ignored.");
        } else {
            new BeginTask().glRun(this.getThread());
        }
    }

    /**
     * A GLTask that starts the profiling period. This will be ignored if
     * profiling is disabled.
     *
     * @since 16.01.04
     */
    public class BeginTask extends GLTask {

        @Override
        public void run() {
            if (!GLPerfMon.this.isEnabled) {
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

    /**
     * Ends the profiling period. This will be ignored if profiling is disabled.
     *
     * @since 16.01.04
     */
    public void end() {
        if (!this.isEnabled) {
            LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled. Call to end ignored.");
        } else {
            new EndTask().glRun(this.getThread());
        }
    }

    /**
     * A GLTask that ends the profiling period. This will be ignored if
     * profiling is disabled.
     *
     * @since 16.01.04
     */
    public final class EndTask extends GLTask {

        @Override
        public void run() {
            if (!GLPerfMon.this.isEnabled) {
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

    /**
     * Deletes the GLProfMon object. This will be ignored if profiling is
     * disabled.
     *
     * @since 16.01.04
     */
    public void delete() {
        if (!this.isEnabled) {
            LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled. Call to delete ignored.");
        } else {
            new DeleteTask().glRun(this.getThread());
        }
    }

    /**
     * A GLTask that deletes the GLPerfMon object. This will be ignored if
     * profiling is disabled.
     *
     * @since 16.01.04
     */
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

    /**
     * Retrieves data collected by the performance monitor. This will be ignored
     * if performance monitoring is disabled.
     *
     * @return the list of data formatted as Strings. Empty list will be
     * returned if profiling is disabled.
     * @since 16.01.04
     */
    public List<String> getData() {
        if (!this.isEnabled) {
            LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; Call to getData ignored.");
            return Collections.emptyList();
        } else {
            return new DataQuery().glCall(this.getThread());
        }
    }

    /**
     * Writes out the data collected by the performance monitor. This will be
     * ignored if performance monitoring is disabled.
     *
     * @since 16.01.04
     */
    public void logData() {
        if (!this.isEnabled) {
            LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; Call to logData ignored.");
        } else {
            this.getData().forEach(line -> LOGGER.debug(GLOOP_MARKER, line));
        }
    }

    /**
     * A GLQuery that retrieves data collected by the performance monitor. This
     * will be ignored if performance monitoring is disabled.
     *
     * @since 16.01.04
     */
    public final class DataQuery extends GLQuery<List<String>> {

        @Override
        public List<String> call() throws Exception {
            if (!GLPerfMon.this.isEnabled) {
                LOGGER.trace(GLOOP_MARKER, "OpenGL performance monitor is disabled; DataQuery ignored.");
                return Collections.emptyList();
            } else if (GLPerfMon.this.monitorId == INVALID_MONITOR_ID) {
                throw new GLException("GLPerfMon object is not initialized!");
            } else {
                final int resultSize;
                {
                    final IntBuffer rawResultSize = NativeTools.getInstance()
                            .nextWord()
                            .asIntBuffer();

                    LOGGER.trace(GL_MARKER, "glGetPerfMonitorCounterDataAMD({}, GL_PERFMON_RESULT_SIZE_AMD, {}, null)",
                            monitorId, rawResultSize);
                    
                    AMDPerformanceMonitor.glGetPerfMonitorCounterDataAMD(
                            monitorId,
                            AMDPerformanceMonitor.GL_PERFMON_RESULT_SIZE_AMD,
                            rawResultSize, null);
                    
                    assert checkGLError() : glErrorMsg("glGetPerfMonitorCounterDataAMD(II**) failed!");

                    resultSize = rawResultSize.get(0);
                    LOGGER.trace(GLOOP_MARKER, "Collected {} words of data!", resultSize);
                }

                final ByteBuffer rawData = ByteBuffer.allocateDirect(resultSize * Integer.BYTES)
                        .order(ByteOrder.nativeOrder());

                final int bytesWritten;
                {
                    final ByteBuffer rawBytesWritten = NativeTools.getInstance().nextWord();

                    LOGGER.trace(GL_MARKER, "glGetPerfMonitorCounterDataAMD({}, GL_PERFMON_RESULT_AMD, {}, {}, {})",
                            monitorId,
                            resultSize,
                            rawData,
                            rawBytesWritten);
                    
                    AMDPerformanceMonitor.glGetPerfMonitorCounterDataAMD(
                            monitorId,
                            AMDPerformanceMonitor.GL_PERFMON_RESULT_AMD,
                            resultSize,
                            rawData,
                            rawBytesWritten);

                    bytesWritten = rawBytesWritten.get(0);
                    LOGGER.trace(GLOOP_MARKER, "Retrieved {} byte(s) of data.", bytesWritten);
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
}
