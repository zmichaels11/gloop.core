package com.longlinkislong.gloop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public class GLVertexAttributes {

    private final Map<String, Integer> nameMap = new HashMap<>();
    private final Set<String> feedbackVaryings = new HashSet<>();

    public void setAttribute(final CharSequence name, final int index) {
        final String sName = name.toString();

        this.nameMap.put(sName, index);
    }

    public void registerFeedbackVarying(final CharSequence vName) {
        this.feedbackVaryings.add(vName.toString());
    }

    public boolean isFeedbackVarying(final CharSequence vName) {
        return this.feedbackVaryings.contains(vName.toString());
    }

    public int getLocation(final CharSequence aName) {
        return this.nameMap.getOrDefault(aName.toString(), -1);
    }

    public class SetTransformFeedbacksTask extends GLTask {

        private final boolean interleaved;
        private final GLProgram program;

        public SetTransformFeedbacksTask(final GLProgram program) {
            this(program, false);
        }

        public SetTransformFeedbacksTask(
                final GLProgram program, final boolean isInterleaved) {

            Objects.requireNonNull(this.program = program);
            this.interleaved = isInterleaved;
        }

        @Override
        public void run() {
            if (!this.program.isValid()) {
                throw new GLException("Invalid GLProgram!");
            }

            final Set<String> varyingSet = GLVertexAttributes.this.feedbackVaryings;

            if (!varyingSet.isEmpty()) {
                final CharSequence[] varyings = new CharSequence[varyingSet.size()];

                GL30.glTransformFeedbackVaryings(
                        this.program.programId,
                        varyings,
                        this.interleaved 
                                ? GL30.GL_INTERLEAVED_ATTRIBS 
                                : GL30.GL_SEPARATE_ATTRIBS);
            }
        }
    }

    public class SetAttributeLocationsTask extends GLTask {

        private final GLProgram program;

        public SetAttributeLocationsTask(final GLProgram program) {
            Objects.requireNonNull(this.program = program);
        }

        @Override
        public void run() {
            if (!this.program.isValid()) {
                throw new GLException("Invalid GLProgram!");
            }

            GLVertexAttributes.this.nameMap.forEach((name, index) -> {
                GL20.glBindAttribLocation(program.programId, index, name);
            });
        }
    }
}
