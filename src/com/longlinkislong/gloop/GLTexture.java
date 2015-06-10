/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

/**
 *
 * @author zmichaels
 */
public class GLTexture extends GLObject {

    private static final int INVALID_TEXTURE_ID = -1;
    protected int textureId = INVALID_TEXTURE_ID;
    private int width = 0;
    private int height = 0;
    private int depth = 0;
    private GLTextureTarget target;
    public static final int GENERATE_MIPMAP = -1;

    public GLTexture() {
        super();
        this.init();
    }

    public GLTexture(final GLThread thread) {
        super(thread);
        this.init();
    }

    public GLTextureTarget getTarget() {
        return this.target;
    }

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

    private final InitTask initTask = new InitTask();

    public final void init() {
        this.initTask.glRun(this.getThread());
    }

    public void setMipmapRange(final int base, final int max) {
        new SetMipmapRangeTask(base, max).glRun(this.getThread());
    }

    public class SetMipmapRangeTask extends GLTask {

        final int baseLevel;
        final int maxLevel;

        public SetMipmapRangeTask(final int baseLevel, final int maxLevel) {
            this.baseLevel = baseLevel;
            this.maxLevel = maxLevel;
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, GLTexture.this.textureId);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, this.baseLevel);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, this.maxLevel);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
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

    private BindTask lastBind = null;

    public void bind(final int activeTexture) {
        if (this.lastBind != null
                && this.lastBind.activeTexture == activeTexture) {

            this.lastBind.glRun(this.getThread());
        } else {
            this.lastBind = new BindTask(activeTexture);

            this.lastBind.glRun(this.getThread());
        }
    }

    public class BindTask extends GLTask {

        final int activeTexture;

        public BindTask(final int activeTexture) {
            this.activeTexture = activeTexture;

            if (activeTexture < 0) {
                throw new GLException("Invalid active texture!");
            }
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            //TODO: skip if already bound            
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + this.activeTexture);
            GL11.glBindTexture(GLTexture.this.target.value, GLTexture.this.textureId);
        }
    }

    private final DeleteTask deleteTask = new DeleteTask();

    public void delete() {
        this.deleteTask.glRun(this.getThread());
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

    private UpdateImage3DTask lastUpdateImage3D = null;

    public void updateImage(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth,
            final GLTextureFormat format,
            final GLType type, final ByteBuffer data) {

        if (this.lastUpdateImage3D != null
                && this.lastUpdateImage3D.level == level
                && this.lastUpdateImage3D.xOffset == xOffset
                && this.lastUpdateImage3D.yOffset == yOffset
                && this.lastUpdateImage3D.zOffset == zOffset
                && this.lastUpdateImage3D.width == width
                && this.lastUpdateImage3D.height == height
                && this.lastUpdateImage3D.depth == depth
                && this.lastUpdateImage3D.format == format
                && this.lastUpdateImage3D.data == data) {

            this.lastUpdateImage3D.glRun(this.getThread());
        } else {
            this.lastUpdateImage3D = new UpdateImage3DTask(
                    level,
                    xOffset, yOffset, zOffset,
                    width, height, depth,
                    format,
                    type, data);

            this.lastUpdateImage3D.glRun(this.getThread());
        }
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

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            if (xOffset < 0 || yOffset < 0 || zOffset < 0) {
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
            if (!GLTexture.this.isValid()) {
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
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_3D.value, 0);

            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_3D;
        }
    }

    private UpdateImage2DTask lastSetSubImage2D = null;

    public void updateImage(
            final int level,
            final int xOffset, final int yOffset,
            final int width, final int height,
            final GLTextureFormat format,
            final GLType type, final ByteBuffer data) {

        if (this.lastSetSubImage2D != null
                && this.lastSetSubImage2D.level == level
                && this.lastSetSubImage2D.xOffset == xOffset
                && this.lastSetSubImage2D.yOffset == yOffset
                && this.lastSetSubImage2D.width == width
                && this.lastSetSubImage2D.height == height
                && this.lastSetSubImage2D.format == format
                && this.lastSetSubImage2D.type == type
                && this.lastSetSubImage2D.data == data) {

            this.lastSetSubImage2D.glRun(this.getThread());
        } else {
            this.lastSetSubImage2D = new UpdateImage2DTask(
                    level,
                    xOffset, yOffset,
                    width, height,
                    format,
                    type, data);

            this.lastSetSubImage2D.glRun(this.getThread());
        }

        GLTexture.this.target = GLTextureTarget.GL_TEXTURE_2D;
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

            if (level < 0) {
                throw new GLException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            if (xOffset < 0 || yOffset < 0) {
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
            if (!GLTexture.this.isValid()) {
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
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_2D.value, 0);
        }

    }

    private Update1DTask lastSetSubImage1D = null;

    public void updateImage(
            final int level,
            final int xOffset, final int width,
            final GLTextureFormat format,
            final GLType type, final ByteBuffer data) {

        if (lastSetSubImage1D != null
                && this.lastSetSubImage1D.level == level
                && this.lastSetSubImage1D.xOffset == xOffset
                && this.lastSetSubImage1D.width == width
                && this.lastSetSubImage1D.format == format
                && this.lastSetSubImage1D.type == type
                && this.lastSetSubImage1D.data == data) {

            this.lastSetSubImage1D.glRun(this.getThread());
        } else {
            this.lastSetSubImage1D = new Update1DTask(
                    level,
                    xOffset, width,
                    format,
                    type, data);

            this.lastSetSubImage2D.glRun(this.getThread());
        }
    }

    public class Update1DTask extends GLTask {

        final int level;
        final int xOffset;
        final int width;
        final GLTextureFormat format;
        final GLType type;
        final ByteBuffer data;

        public Update1DTask(
                final int xOffset, final int width,
                final GLTextureFormat format,
                final GLType type, final ByteBuffer data) {

            this(0, xOffset, width, format, type, data);
        }

        public Update1DTask(
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
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_1D.value, 0);
        }
    }

    public void allocateImage(
            final int level,
            final GLTextureInternalFormat internalFormat,
            final GLTextureFormat format,
            final int width, final int height, final int depth,
            final GLType type) {

        new AllocateImage3DTask(
                level,
                internalFormat, format,
                width, height, depth,
                type).glRun(this.getThread());
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
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_3D.value, 0);
        }
    }

    public void allocate(
            final int level,
            final GLTextureInternalFormat internalFormat,
            final GLTextureFormat format,
            final int width, final int height,
            final GLType type) {

        new AllocateImage2DTask(
                level,
                internalFormat, format,
                width, height,
                type).glRun(this.getThread());
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
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_2D.value, 0);
        }
    }

    public void allocate(
            final int level,
            final GLTextureInternalFormat internalFormat,
            final GLTextureFormat format,
            final int width,
            final GLType type) {

        new AllocateImage1DTask(level, internalFormat, format, width, type).glRun(this.getThread());
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
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_1D.value, 0);
        }
    }

    public void setImage(
            final int level,
            final GLTextureInternalFormat internalFormat,
            final GLTextureFormat format,
            final int width, final int height, final int depth,
            final GLType type, final ByteBuffer data) {

        new SetImage3DTask(
                level,
                internalFormat, format,
                width, height, depth,
                type, data).glRun(this.getThread());
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
        final boolean generateMipmap;

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

            if (level == GLTexture.GENERATE_MIPMAP) {
                this.level = 0;
                this.generateMipmap = true;
            } else if ((this.level = level) < 0) {
                throw new GLException("Invalid mipmap level: " + level);
            } else {
                this.generateMipmap = false;
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
            if (this.generateMipmap) {
                GL30.glGenerateMipmap(GLTextureTarget.GL_TEXTURE_3D.value);
            }
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_3D.value, 0);
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_3D;
        }
    }

    public void setImage(
            final int level,
            final GLTextureInternalFormat internalFormat,
            final GLTextureFormat format,
            final int width, final int height,
            final GLType type, final ByteBuffer data) {

        new SetImage2DTask(
                level,
                internalFormat, format,
                width, height,
                type, data).glRun(this.getThread());
    }

    public class SetImage2DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final int height;
        final GLType type;
        final ByteBuffer data;
        final boolean generateMipmap;

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

            if (level == GLTexture.GENERATE_MIPMAP) {
                this.level = 0;
                this.generateMipmap = true;
            } else if ((this.level = level) < 0) {
                throw new GLException("Invalid mipmap level: " + level);
            } else {
                this.generateMipmap = false;
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
            if (this.generateMipmap) {
                GL30.glGenerateMipmap(GLTextureTarget.GL_TEXTURE_2D.value);
            }
            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_2D.value, 0);
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_2D;
        }
    }

    public void setImage(
            final int level,
            final GLTextureInternalFormat internalFormat,
            final GLTextureFormat format,
            final int width,
            final GLType type, final ByteBuffer data) {

        new SetImage1DTask(
                level,
                internalFormat, format,
                width,
                type, data).glRun(this.getThread());
    }

    public class SetImage1DTask extends GLTask {

        final int level;
        final GLTextureInternalFormat internalFormat;
        final GLTextureFormat format;
        final int width;
        final GLType type;
        final ByteBuffer data;
        final boolean generateMipmap;

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

            if (level == GLTexture.GENERATE_MIPMAP) {
                this.level = 0;
                this.generateMipmap = true;
            } else if ((this.level = level) < 0) {
                throw new GLException("Invalid mipmap level: " + level);
            } else {
                this.generateMipmap = false;
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

            if (this.generateMipmap) {
                GL30.glGenerateMipmap(GLTextureTarget.GL_TEXTURE_1D.value);
            }

            GL11.glBindTexture(GLTextureTarget.GL_TEXTURE_1D.value, 0);
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_1D;
        }
    }

    private SetTextureBufferTask lastSetTexBufferTask = null;

    public void setTextureBuffer(
            final GLTextureInternalFormat internalFormat,
            final GLBuffer buffer) {

        if (this.lastSetTexBufferTask != null
                && this.lastSetTexBufferTask.internalFormat == internalFormat
                && this.lastSetTexBufferTask.buffer == buffer) {

            this.lastSetTexBufferTask.glRun(this.getThread());
        } else {
            this.lastSetTexBufferTask = new SetTextureBufferTask(
                    internalFormat,
                    buffer);
            this.lastSetTexBufferTask.glRun(this.getThread());
        }
    }

    public class SetPixelBuffer2DTask extends GLTask {

        final GLBuffer buffer;
        final int level;
        final int xOffset;
        final int yOffset;
        final int width;
        final int height;
        final GLTextureFormat format;
        final GLType type;

        public SetPixelBuffer2DTask(
                final int level,
                final int xOffset, final int yOffset,
                final int width, final int height,
                final GLTextureFormat format,
                final GLType type,
                final GLBuffer buffer) {

            Objects.requireNonNull(this.buffer = buffer);
            Objects.requireNonNull(this.format = format);
            Objects.requireNonNull(this.type = type);

            if ((this.level = level) < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
            } else if ((this.xOffset = xOffset) < 0) {
                throw new GLException("X-offset cannot be less than 0!");
            } else if ((this.yOffset = yOffset) < 0) {
                throw new GLException("Y-offset cannot be less than 0!");
            } else if ((this.width = width) < 1) {
                throw new GLException("Width cannot be less than 1!");
            } else if ((this.height = height) < 1) {
                throw new GLException("Height cannot be less than 1!");
            }
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else if (!this.buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, GLTexture.this.textureId);
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, this.buffer.bufferId);

            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D,
                    this.level,
                    this.xOffset, this.yOffset,
                    this.width, this.height,
                    this.format.value,
                    this.type.value, 0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
    }

    public class SetTextureBufferTask extends GLTask {

        final GLBuffer buffer;
        final GLTextureInternalFormat internalFormat;

        public SetTextureBufferTask(
                final GLTextureInternalFormat internalFormat,
                final GLBuffer buffer) {

            Objects.requireNonNull(this.buffer = buffer);
            Objects.requireNonNull(this.internalFormat = internalFormat);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is invalid!");
            }

            if (!this.buffer.isValid()) {
                throw new GLException("GLBuffer is invalid!");
            }

            GL11.glBindTexture(
                    GLBufferTarget.GL_TEXTURE_BUFFER.value,
                    GLTexture.this.textureId);
            GL31.glTexBuffer(
                    GLBufferTarget.GL_TEXTURE_BUFFER.value,
                    this.internalFormat.value,
                    this.buffer.bufferId);

            GLTexture.this.width = GL11.glGetTexLevelParameteri(
                    GL11.GL_TEXTURE_1D, 0, GL11.GL_TEXTURE_WIDTH);
            GLTexture.this.height = GLTexture.this.depth = 1;
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_1D;
            GL11.glBindTexture(
                    GLBufferTarget.GL_TEXTURE_BUFFER.value,
                    0);
        }
    }

    public void setAttributes(final GLTextureParameters params) {
        new SetAttributesTask(params).glRun(this.getThread());
    }

    public class SetAttributesTask extends GLTask {

        final GLTextureParameters params;

        public SetAttributesTask(final GLTextureParameters params) {
            Objects.requireNonNull(this.params = params);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            }

            final int target = GL11.GL_TEXTURE_2D;

            GL11.glBindTexture(target, GLTexture.this.textureId);

            GL11.glTexParameteri(
                    target,
                    GL11.GL_TEXTURE_MIN_FILTER,
                    this.params.minFilter.value);

            GL11.glTexParameteri(
                    target,
                    GL11.GL_TEXTURE_MAG_FILTER,
                    this.params.magFilter.value);

            GL11.glTexParameteri(
                    target,
                    GL11.GL_TEXTURE_WRAP_S,
                    this.params.wrapS.value);

            GL11.glTexParameteri(
                    target,
                    GL11.GL_TEXTURE_WRAP_T,
                    this.params.wrapT.value);

            GL11.glTexParameteri(
                    target,
                    GL12.GL_TEXTURE_WRAP_R,
                    this.params.wrapR.value);

            GL11.glTexParameterf(
                    target,
                    GL12.GL_TEXTURE_MIN_LOD,
                    this.params.minLOD);

            GL11.glTexParameterf(
                    target,
                    GL12.GL_TEXTURE_MAX_LOD,
                    this.params.maxLOD);

            GL11.glTexParameterf(
                    target,
                    EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                    this.params.anisotropicLevel);

            GL11.glBindTexture(target, 0);

        }
    }
}
