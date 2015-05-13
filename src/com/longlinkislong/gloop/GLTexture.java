/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

/**
 *
 * @author zmichaels
 */
public class GLTexture {

    private static final int INVALID_TEXTURE_ID = -1;
    private int textureId = INVALID_TEXTURE_ID;
    private int width = 0;
    private int height = 0;
    private int depth = 0;   

    public int getWidth() {
        if (this.width == 0) {
            throw new GLException("Width has not been set!");
        } else {
            return this.width;
        }
    }

    public int getHeight() {
        if (this.height == 0) {
            throw new GLException("Height has not been set!");
        } else {
            return this.height;
        }
    }

    public int getDepth() {
        if (this.depth == 0) {
            throw new GLException("Depth has not been set!");
        } else {
            return this.depth;
        }
    }    

    public boolean isValid() {
        return textureId != INVALID_TEXTURE_ID;
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                throw new GLException("Cannot reinit GLTexture!");
            }

            GLTexture.this.textureId = GL11.glGenTextures();
        }
    }

    public class BindTask extends GLTask {

        final GLTextureTarget target;
        final int activeTexture;

        public BindTask(final GLTextureTarget target, final int activeTexture) {
            this.target = target;
            this.activeTexture = activeTexture;

            Objects.requireNonNull(target);
            if (activeTexture < 0) {
                throw new GLException("Invalid active texture!");
            }
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GL13.glActiveTexture(this.activeTexture);
            GL11.glBindTexture(this.target.value, GLTexture.this.textureId);
        }
    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                GL11.glDeleteTextures(GLTexture.this.textureId);
                GLTexture.this.textureId = INVALID_TEXTURE_ID;
            } else {
                throw new GLException("GLTexture is not valid!");
            }
        }
    }

    private void setSize(final int width, final int height, final int depth) {
        if (width < 0) {
            throw new GLException("Invalid texture width: " + width);
        } else if (height < 0) {
            throw new GLException("Invalid texture height: " + height);
        } else if (depth < 0) {
            throw new GLException("Invalid texture depth: " + depth);
        }

        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public class UpdateImage3DTask extends GLTask {
        final int level;
        final int xOffset;
        final int yOffset;
        final int zOffset;
        final int width;
        final int height;
        final int depth;
        final GLTextureFormat format;
        final GLType type;
        final ByteBuffer data;
        
        public UpdateImage3DTask(
        final int level,
                final int xOffset, final int yOffset, final int zOffset,
                final int width, final int height, final int depth,
                final GLTextureFormat format,
                final GLType type, final ByteBuffer data) {
            
            if(level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }
            
            if(xOffset < 0 || yOffset < 0 || zOffset < 0) {
                throw new GLException("Offsets cannot be less than 0!");
            } else {
                this.xOffset = xOffset;
                this.yOffset = yOffset;
                this.zOffset = zOffset;
            }
            
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);
            
            GLTools.checkBuffer(this.data = data);
            GLTexture.this.setSize(
                    this.width = width, 
                    this.height = height, 
                    this.depth = depth);
        }
        
        @Override
        public void run() {
            if(!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }
            
            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_3D.value, 
                    GLTexture.this.textureId);
            GL12.glTexSubImage3D(
                    GLTextureTarget.GL_TEXTURE_3D.value,
                    this.level,
                    this.xOffset, this.yOffset, this.zOffset,
                    this.width, this.height, this.depth,
                    this.format.value,
                    this.type.value, this.data);
        }
    }
    
    public class UpdateImage2DTask extends GLTask {

        final int level;
        final int xOffset;
        final int yOffset;
        final int width;
        final int height;
        final GLTextureFormat format;
        final GLType type;
        final ByteBuffer data;

        public UpdateImage2DTask(
                final int level,
                final int xOffset, final int yOffset,
                final int width, final int height,
                final GLTextureFormat format,
                final GLType type, final ByteBuffer data) {

            if(level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }
            
            if(xOffset < 0 || yOffset < 0) {
                throw new GLException("Offset values cannot be less than 0!");
            } else {
                this.xOffset = xOffset;
                this.yOffset = yOffset;
            }
            
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);
            
            GLTools.checkBuffer(this.data = data);
            
            GLTexture.this.setSize(
                    this.width = width, 
                    this.height = height, 
                    1);            
        }
        
        @Override
        public void run() {
            if(!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }
            
            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_2D.value,
                    GLTexture.this.textureId);
            GL11.glTexSubImage2D(
                    GLTextureTarget.GL_TEXTURE_2D.value,
                    GLTexture.this.textureId,
                    this.xOffset, this.yOffset,
                    this.width, this.height,
                    this.format.value,
                    this.type.value, this.data);            
        }

    }

    public class UpdateImage1DTask extends GLTask {

        final int level;
        final int xOffset;
        final int width;
        final GLTextureFormat format;
        final GLType type;
        final ByteBuffer data;

        public UpdateImage1DTask(
                final int xOffset, final int width,
                final GLTextureFormat format,
                final GLType type, final ByteBuffer data) {

            this(0, xOffset, width, format, type, data);
        }

        public UpdateImage1DTask(
                final int level,
                final int xOffset, final int width,
                final GLTextureFormat format,
                final GLType type,
                final ByteBuffer data) {

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            if (xOffset < 0) {
                throw new GLException("X-Offset cannot be less than 0!");
            } else {
                this.xOffset = xOffset;
            }

            if (width < 0) {
                throw new GLException("Width cannot be less than 0!");
            } else {
                this.width = width;
            }

            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);
            GLTools.checkBuffer(this.data = data);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_1D.value,
                    GLTexture.this.textureId);
            GL11.glTexSubImage1D(
                    GLTextureTarget.GL_TEXTURE_1D.value,
                    this.level,
                    this.xOffset, this.width,
                    this.format.value,
                    this.type.value,
                    this.data);
        }
    }

    public class AllocateImage3DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final int height;
        final int depth;
        final GLType type;

        public AllocateImage3DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width, final int height, final int depth,
                final GLType type) {

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            Objects.requireNonNull(this.internalFormat = internalFormat);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            GLTexture.this.setSize(
                    this.width = width,
                    this.height = height,
                    this.depth = depth);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_3D.value,
                    GLTexture.this.textureId);
            GL12.glTexImage3D(
                    GLTextureTarget.GL_TEXTURE_3D.value,
                    this.level,
                    this.internalFormat.value,
                    this.width, this.height, this.depth,
                    0,
                    this.format.value,
                    this.type.value,
                    0);
        }
    }

    public class AllocateImage2DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final int height;
        final GLType type;

        public AllocateImage2DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width, final int height,
                final GLType type) {

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            Objects.requireNonNull(this.internalFormat = internalFormat);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            GLTexture.this.setSize(this.width = width, this.height = height, 1);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_2D.value,
                    GLTexture.this.textureId);

            GL11.glTexImage2D(
                    GLTextureTarget.GL_TEXTURE_2D.value,
                    this.level,
                    this.internalFormat.value,
                    this.width, this.height,
                    0,
                    this.format.value,
                    this.type.value,
                    0);
        }
    }

    public class AllocateImage1DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final GLType type;

        public AllocateImage1DTask(final int level,
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width,
                final GLType type) {

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            Objects.requireNonNull(this.internalFormat = internalFormat);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            GLTexture.this.setSize(this.width = width, 1, 1);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_1D.value,
                    GLTexture.this.textureId);
            GL11.glTexImage1D(
                    GLTextureTarget.GL_TEXTURE_1D.value,
                    this.level,
                    this.internalFormat.value,
                    this.width,
                    0,
                    this.format.value,
                    this.type.value,
                    0);
        }
    }

    public class SetImage3DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final int height;
        final int depth;
        final GLType type;
        final ByteBuffer data;

        public SetImage3DTask(
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width, final int height, final int depth,
                final GLType type, final ByteBuffer data) {

            this(0, internalFormat, format, width, height, depth, type, data);
        }

        public SetImage3DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width, final int height, final int depth,
                final GLType type, final ByteBuffer data) {

            Objects.requireNonNull(this.internalFormat = internalFormat);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            GLTexture.this.setSize(
                    this.width = width,
                    this.height = height,
                    this.depth = depth);
            GLTools.checkBuffer(this.data = data);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_3D.value,
                    GLTexture.this.textureId);
            GL12.glTexImage3D(
                    GLTextureTarget.GL_TEXTURE_3D.value,
                    this.level,
                    this.internalFormat.value,
                    this.width, this.height, this.depth,
                    0,
                    this.format.value,
                    this.type.value,
                    this.data);
        }
    }

    public class SetImage2DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final int height;
        final GLType type;
        final ByteBuffer data;

        public SetImage2DTask(
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width, final int height,
                final GLType type, final ByteBuffer data) {

            this(0, internalFormat, format, width, height, type, data);
        }

        public SetImage2DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format,
                final int width, final int height,
                final GLType type, final ByteBuffer data) {

            Objects.requireNonNull(this.internalFormat = internalFormat);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            GLTools.checkBuffer(this.data = data);
            GLTexture.this.setSize(
                    this.width = width,
                    this.height = height, 1);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is invalid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_2D.value,
                    GLTexture.this.textureId);
            GL11.glTexImage2D(
                    GLTextureTarget.GL_TEXTURE_2D.value, level,
                    this.internalFormat.value,
                    this.width, this.height, 0,
                    this.format.value,
                    this.type.value,
                    data);
        }
    }

    public class SetImage1DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final GLType type;
        final ByteBuffer data;

        public SetImage1DTask(
                final GLTextureInternalFormat interalFormat,
                final GLTextureFormat format,
                final int width,
                final GLType type, final ByteBuffer data) {

            this(0, interalFormat, format, width, type, data);
        }

        public SetImage1DTask(
                final int level, final GLTextureInternalFormat internalFormat,
                final GLTextureFormat format, final int width,
                final GLType type,
                final ByteBuffer data) {

            Objects.requireNonNull(this.internalFormat = internalFormat);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            GLTools.checkBuffer(this.data = data);
            GLTexture.this.setSize(this.width = width, 1, 1);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is invalid!");
            }

            GL11.glBindTexture(
                    GLTextureTarget.GL_TEXTURE_1D.value,
                    GLTexture.this.textureId);
            GL11.glTexSubImage1D(
                    GLTextureTarget.GL_TEXTURE_1D.value,
                    GLTexture.this.textureId,
                    this.width,
                    0,
                    this.format.value,
                    this.type.value,
                    this.data);
        }
    }
}
