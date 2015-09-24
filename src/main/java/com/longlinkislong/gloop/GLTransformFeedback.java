/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author zmichaels
 */
public class GLTransformFeedback extends GLObject {

    private static final int INVALID_TFBID = -1;
    protected int tfbId = INVALID_TFBID;

    public GLTransformFeedback() {
        super();

        this.init();
    }

    public GLTransformFeedback(final GLThread thread) {
        super(thread);

        this.init();
    }

    public boolean isValid() {
        return this.tfbId != INVALID_TFBID;
    }

    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLTransformFeedback.this.isValid()) {
                throw new GLException("GLTransformFeedback already exists!");
            }

            GLTransformFeedback.this.tfbId = GL40.glGenTransformFeedbacks();
        }
    }

    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!GLTransformFeedback.this.isValid()) {
                throw new GLException("GLTransformFeedback is invalid!");
            }

            GL40.glDeleteTransformFeedbacks(GLTransformFeedback.this.tfbId);
            assert checkGLError() : glErrorMsg("glDeleteTransformFeedbacks(I)", GLTransformFeedback.this.tfbId);
        }
    }    
    
    public class FeedbackTask extends GLTask {
        final GLDrawTask drawTask;
        final GLDrawMode drawMode;
        
        public FeedbackTask(
                final GLDrawMode drawMode, final GLDrawTask drawTask) {
            
            Objects.requireNonNull(this.drawTask = drawTask);
            Objects.requireNonNull(this.drawMode = drawMode);
        }
        
        @Override
        public void run() {
            GL40.glBindTransformFeedback(GL40.GL_TRANSFORM_FEEDBACK, GLTransformFeedback.this.tfbId);
            assert checkGLError() : glErrorMsg("glBindTransformFeedback(II)", "GL_TRANSFORM_FEEDBACK", GLTransformFeedback.this.tfbId);
            
            GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
            assert checkGLError() : glErrorMsg("glEnable(I)", "GL_RASTERIZER_DISCARD");
            
            GL30.glBeginTransformFeedback(this.drawMode.value);            
            assert checkGLError() : glErrorMsg("glBeginTransformFeedback(I)", this.drawMode);
            
            drawTask.run();
            GL30.glEndTransformFeedback();
            assert checkGLError() : glErrorMsg("glEndTransformFeedback(void)");
            
            GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
            assert checkGLError() : glErrorMsg("glDisable(I)", "GL_RASTERIZER_DISCARD");
        }
    }

    public class AddBufferTask extends GLTask {

        final int index;
        final GLBuffer buffer;

        public AddBufferTask(final int index, final GLBuffer buffer) {
            Objects.requireNonNull(this.buffer = buffer);
            this.index = index;
        }

        @Override
        public void run() {
            if (!GLTransformFeedback.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL40.glBindTransformFeedback(GL40.GL_TRANSFORM_FEEDBACK, GLTransformFeedback.this.tfbId);
            assert checkGLError() : glErrorMsg("glBindTransformFeedback(II)", "GL_TRANSFORM_FEEDBACK", GLTransformFeedback.this.tfbId);
            
            GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, this.index, this.buffer.bufferId);
            assert checkGLError() : glErrorMsg("glBindBufferBase(III)", "GL_TRANSFORM_FEEDBACK_BUFFER", this.index, this.buffer.bufferId);
            
            GL40.glBindTransformFeedback(GL40.GL_TRANSFORM_FEEDBACK, 0);
            assert checkGLError() : glErrorMsg("glBindTransformFeedback(II)", "GL_TRANSFORM_FEEDBACK", 0);
        }
    }
}
