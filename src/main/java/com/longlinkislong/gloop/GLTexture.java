/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import com.longlinkislong.gloop.dsa.DSADriver;
import com.longlinkislong.gloop.dsa.EXTDSADriver;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.lwjgl.opengl.ARBInternalformatQuery;
import org.lwjgl.opengl.ARBInternalformatQuery2;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

/**
 * GLTexture represents an immutable OpenGL texture object. Currently only
 * supports 1D, 2D, or 3D textures.
 *
 * @author zmichaels
 * @since 15.07.08
 */
public class GLTexture extends GLObject {

    private static final int INVALID_TEXTURE_ID = -1;
    protected volatile int textureId = INVALID_TEXTURE_ID;
    private volatile int width = 0;
    private volatile int height = 0;
    private volatile int depth = 0;
    private GLTextureTarget target;
    public static final int GENERATE_MIPMAP = -1;

    /**
     * Constructs a new GLTexture on the default OpenGL thread.
     *
     * @since 15.07.08
     */
    public GLTexture() {
        this(GLThread.getDefaultInstance());
    }

    /**
     * Constructs a new GLTexture on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread.
     * @since 15.07.08
     */
    public GLTexture(final GLThread thread) {
        super(thread);
    }

    /**
     * Constructs a new GLTexture on the default OpenGL thread and allocates a
     * 3D texture object. This constructor is the same as using the constructor
     * GLTexture() and immediately calling allocate(int, format, width, height,
     * depth).
     *
     * @param mipmaps the number of mipmap levels to allocate.
     * @param pixelFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the height of the base mipmap level.
     * @param depth the depth of the base mipmap level.
     * @since 15.07.08
     */
    public GLTexture(final int mipmaps, final GLTextureInternalFormat pixelFormat, final int width, final int height, final int depth) {
        this();
        this.allocate(mipmaps, pixelFormat, width, height, depth);
    }

    /**
     * Constructs a new GLTexture on the default OpenGL thread and allocates a
     * 2D texture object. This constructor is the same as using the constructor
     * GLTexture() and immediately calling allocate(int, format, width, height).
     *
     * @param mipmaps the number of mipmap levels to allocate.
     * @param pixelFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the height of the base mipmap level.
     * @since 15.07.08
     */
    public GLTexture(final int mipmaps, final GLTextureInternalFormat pixelFormat, final int width, final int height) {
        this();
        this.allocate(mipmaps, pixelFormat, width, height);
    }

    /**
     * Constructs a new GLTexture on the default OpenGL thread and allocates a
     * 1D texture object. This constructor is the same as using the constructor
     * GLTexture() and immediately calling allocate(int, format, width).
     *
     * @param mipmaps the number of mipmap levels to allocate.
     * @param pixelFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @since 15.07.08
     */
    public GLTexture(final int mipmaps, final GLTextureInternalFormat pixelFormat, final int width) {
        this();
        this.allocate(mipmaps, pixelFormat, width);
    }

    /**
     * Constructs a new GLTexture on the specified OpenGL thread and allocates a
     * 3D texture object. This constructor is the same as using the constructor
     * GLTexture(thread) and immediately calling allocate(int, format, width,
     * height, depth).
     *
     * @param thread the OpenGL thread to construct the object on.
     * @param mipmaps the number of mipmap levels to allocate.
     * @param pixelFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the height of the base mipmap level.
     * @param depth the depth of the base mipmap level.
     * @since 15.07.08
     */
    public GLTexture(final GLThread thread, final int mipmaps, final GLTextureInternalFormat pixelFormat, final int width, final int height, final int depth) {
        this(thread);
        this.allocate(mipmaps, pixelFormat, width, height, depth);
    }

    /**
     * Constructs a new GLTexture on the specified OpenGL thread and allocates a
     * 2D texture object. This constructor is the same as using the constructor
     * GLTexture(thread) and immediately calling allocate(int, format, width,
     * height).
     *
     * @param thread the OpenGL thread to construct the object on.
     * @param mipmaps the number of mipmap levels to allocate.
     * @param pixelFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the height of the base mipmap level.
     * @since 15.07.08
     */
    public GLTexture(final GLThread thread, final int mipmaps, final GLTextureInternalFormat pixelFormat, final int width, final int height) {
        this(thread);
        this.allocate(mipmaps, pixelFormat, width, height);
    }

    /**
     * Constructs a new GLTexture on the specified OpenGL thread and allocates
     * the 1D texture object. This constructor is the same as using the
     * constructor GLTexture(thread) and immediately calling allocate(int,
     * format, width).
     *
     * @param thread the OpenGL thread to construct the object on.
     * @param mipmaps the number of mipmap levels to allocate.
     * @param pixelFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @since 15.07.08
     */
    public GLTexture(final GLThread thread, final int mipmaps, final GLTextureInternalFormat pixelFormat, final int width) {
        this(thread);
        this.allocate(mipmaps, pixelFormat, width);
    }

    /**
     * Retrieves the type of texture.
     *
     * @return GL_TEXTURE1D, GL_TEXTURE_2D, or GL_TEXTURE_3D.
     * @since 15.07.08
     */
    public GLTextureTarget getTarget() {
        return this.target;
    }

    /**
     * Retrieves the width of the texture.
     *
     * @return the width.
     * @throws GLException if the texture has not been allocated yet.
     * @since 15.07.08
     */
    public int getWidth() throws GLException {
        if (this.width == 0) {
            throw new GLException("Width has not been set!");
        } else {
            return this.width;
        }
    }

    /**
     * Retrieves the height of the texture. This will be return 1 for 1D
     * textures.
     *
     * @return the height.
     * @throws GLException if the texture has not been allocated yet.
     * @since 15.07.08
     */
    public int getHeight() throws GLException {
        if (this.height == 0) {
            throw new GLException("Height has not been set!");
        } else {
            return this.height;
        }
    }

    /**
     * Retrieves the depth of the texture. This will return 1 for 1D or 2D
     * textures.
     *
     * @return the depth
     * @throws GLException if the texture has not been allocated yet.
     * @since 15.07.08
     */
    public int getDepth() throws GLException {
        if (this.depth == 0) {
            throw new GLException("Depth has not been set!");
        } else {
            return this.depth;
        }
    }

    /**
     * Checks if the texture has been allocated.
     *
     * @return true if the texture has been allocated and has not yet been
     * deleted.
     * @since 15.07.08
     */
    public boolean isValid() {
        return textureId != INVALID_TEXTURE_ID;
    }

    private BindTask lastBindTask = null;

    /**
     * Binds the GLTexture to the specified texture unit.
     *
     * @param textureUnit the texture unit to bind to.
     * @since 15.07.08
     */
    public void bind(final int textureUnit) {
        if (this.lastBindTask == null || this.lastBindTask.activeTexture != textureUnit) {
            this.lastBindTask = new BindTask(textureUnit);
        }

        this.lastBindTask.glRun(this.getThread());
    }

    /**
     * A GLTask that binds the texture to the specified texture unit.
     *
     * @since 15.07.08
     */
    public class BindTask extends GLTask {

        public final int activeTexture;

        /**
         * Constructs a new bind task using the specified texture unit.
         *
         * @param activeTexture the texture unit to bind to. Must be greater
         * than 0 and less than the maximum supported texture units.
         * @throws GLException if the specified texture unit is less than 0.
         * @since 15.07.08
         */
        public BindTask(final int activeTexture) {
            this.activeTexture = activeTexture;

            if (activeTexture < 0) {
                throw new GLException("Invalid active texture!");
            }
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid! You must allocate a texture prior to binding it.");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            if (dsa instanceof EXTDSADriver) {
                ((EXTDSADriver) dsa).glBindTextureUnit(GL13.GL_TEXTURE0 + this.activeTexture, GLTexture.this.target.value, textureId);
            } else {
                dsa.glBindTextureUnit(this.activeTexture, GLTexture.this.textureId);
            }
        }
    }

    /**
     * Deletes the texture and reverts the GLTexture object back to its initial
     * state.
     *
     * @since 15.07.08
     */
    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the texture object and restores the GLTexture to
     * its initial state.
     *
     * @since 15.07.08
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                GL11.glDeleteTextures(GLTexture.this.textureId);
                assert checkGLError() : glErrorMsg("glDeleteTextures(I)", GLTexture.this.textureId);

                GLTexture.this.textureId = INVALID_TEXTURE_ID;
                GLTexture.this.width = GLTexture.this.height = GLTexture.this.depth = 0;
            } else {
                throw new GLException("GLTexture is not valid! Cannot delete a texture if it does not exist.");
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

    /**
     * Updates a 3D segment of the specified mipmap level.
     *
     * @param level the mipmap level to write.
     * @param xOffset the x-offset to write to.
     * @param yOffset the y-offset to write to.
     * @param zOffset the z-offset to write to.
     * @param width the number of pixels along the x-axis to write.
     * @param height the number of pixels along the y-axis to write.
     * @param depth the number of pixels along the z-axis to write.
     * @param format the pixel format the data is stored in.
     * @param type the data type the pixel data is stored as.
     * @param data the pixel data.
     * @return self reference.
     * @since 15.07.08
     */
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth,
            final GLTextureFormat format,
            final GLType type, final ByteBuffer data) {

        new UpdateImage3DTask(level, xOffset, yOffset, zOffset, width, height, depth, format, type, data).glRun(this.getThread());        

        return this;
    }

    /**
     * A GLTask that updates a 3D image.
     *
     * @since 15.07.08
     */
    public class UpdateImage3DTask extends GLTask {

        public final int level;
        public final int xOffset;
        public final int yOffset;
        public final int zOffset;
        public final int width;
        public final int height;
        public final int depth;
        public final GLTextureFormat format;
        public final GLType type;
        public final ByteBuffer data;

        /**
         * Constructs a new UpdateImage3DTask.
         *
         * @param level the mipmap level.
         * @param xOffset the x-offset to begin writing to.
         * @param yOffset the y-offset to begin writing to.
         * @param zOffset the z-offset to begin writing to.
         * @param width the number of pixels along the x-axis to write.
         * @param height the number of pixels along the y-axis to write.
         * @param depth the number of pixels along the z-axis to write.
         * @param format the pixel format type.
         * @param type the data type.
         * @param data the pixel data.
         * @throws GLException if the level is less than 0.
         * @throws GLException if xOffset is less than 0.
         * @throws GLException if yOffset is less than 0.
         * @throws GLException if zOffset is less than 0.
         * @throws GLException if data is not direct.
         * @throws GLException if data is not in native byte order.
         * @throws NullPointerException if format is null.
         * @throws NullPointerException if type is null.
         * @throws NullPointerException if data is null.
         * @since 15.07.08
         */
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

            this.data = GLTools.checkBuffer(
                    data.asReadOnlyBuffer().order(ByteOrder.nativeOrder()));

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

            GLTools.getDSAInstance().glTextureSubImage3d(
                    GLTexture.this.textureId,
                    this.level,
                    this.xOffset, this.yOffset, this.zOffset,
                    this.width, this.height, this.depth, this.format.value,
                    this.type.value, this.data);
        }
    }    

    /**
     * Updates a 2D segment of the specified mipmap level.
     *
     * @param level the mipmap level to write.
     * @param xOffset the x-offset to write to.
     * @param yOffset the y-offset to write to.
     * @param width the number of pixels along the x-axis to write.
     * @param height the number of pixels along the y-axis to write.
     * @param format the pixel format the data is stored in.
     * @param type the data type the pixel data is stored as.
     * @param data the pixel data.
     * @return self reference.
     * @since 15.07.08
     */
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset,
            final int width, final int height,
            final GLTextureFormat format,
            final GLType type, final ByteBuffer data) {

        new UpdateImage2DTask(level, xOffset, yOffset, width, height, format, type, data).glRun(this.getThread());        

        return this;
    }

    /**
     * A GLTask that updates a 3D image.
     *
     * @since 15.07.08
     */
    public class UpdateImage2DTask extends GLTask {

        public final int level;
        public final int xOffset;
        public final int yOffset;
        public final int width;
        public final int height;
        public final GLTextureFormat format;
        public final GLType type;
        public final ByteBuffer data;

        /**
         * Constructs a new UpdateImage2DTask
         *
         * @param level the mipmap level to write to.
         * @param xOffset the x-offset to begin writing to.
         * @param yOffset the y-offset to begin writing to.
         * @param width the number of pixels along the x-axis to write.
         * @param height the number of pixels along the y-axis to write.
         * @param format the pixel format the data is stored as.
         * @param type the data type the data is stored as.
         * @param data the pixel data.
         * @throws GLException if mipmaps is less than 0.
         * @throws GLException if xOffset is less than 0.
         * @throws GLException if yOffset is less than 0.
         * @throws GLException if data is not direct.
         * @throws GLException if data is not in native byte order.
         * @throws NullPointerException if format is null.
         * @throws NullPointerException if type is null.
         * @throws NullPointerException if data is null.
         * @since 15.07.08
         */
        public UpdateImage2DTask(
                final int level,
                final int xOffset, final int yOffset,
                final int width, final int height,
                final GLTextureFormat format,
                final GLType type, final ByteBuffer data) {

            if (level < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
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

            this.data = GLTools.checkBuffer(data.asReadOnlyBuffer().order(ByteOrder.nativeOrder()));

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

            GLTools.getDSAInstance().glTextureSubImage2d(
                    GLTexture.this.textureId,
                    this.level, this.xOffset, this.yOffset,
                    this.width, this.height, this.format.value,
                    this.type.value, this.data);
        }

    }    

    /**
     * Updates a 1D segment of the specified mipmap level.
     *
     * @param level the mipmap level to write.
     * @param xOffset the x-offset to write to.
     * @param width the number of pixels along the x-axis to write.
     * @param format the pixel format the data is stored as.
     * @param type the data type the data is stored as.
     * @param data the pixel data.
     * @return self reference.
     * @since 15.07.08
     */
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int width,
            final GLTextureFormat format,
            final GLType type, final ByteBuffer data) {

        
        new UpdateImage1DTask(level, xOffset, width, format, type, data).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that updates a 1D segment of a texture.
     *
     * @since 15.07.08
     */
    public class UpdateImage1DTask extends GLTask {

        public final int level;
        public final int xOffset;
        public final int width;
        public final GLTextureFormat format;
        public final GLType type;
        public final ByteBuffer data;

        /**
         * Constructs a new Update1DTask.
         *
         * @param level the mipmap level to update.
         * @param xOffset the x-offset to write to.
         * @param width the number of pixels along the x-axis to write.
         * @param format the pixel format the data is stored in.
         * @param type the data type the data is stored in.
         * @param data the pixel data.
         * @throws GLException if mipmaps is less than 0.
         * @throws GLException if xOffset is less than 0.
         * @throws GLException if width is less than 0.
         * @throws GLException if data is not direct.
         * @throws GLException if data is not in native byte order.
         * @throws NullPointerException if format is null.
         * @throws NullPointerException if type is null.
         * @throws NullPointerException if data is null.
         * @since 15.07.08
         */
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
            this.data = GLTools.checkBuffer(data.asReadOnlyBuffer().order(ByteOrder.nativeOrder()));
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GLTools.getDSAInstance().glTextureSubImage1d(
                    GLTexture.this.textureId,
                    this.level, this.xOffset, this.width, this.format.value,
                    this.type.value, this.data);
        }
    }

    /**
     * Allocates the texture object as a 3D texture. Each mipmap level allocated
     * is half the dimensions of the previous level.
     *
     * @param mipmaps the number of mipmaps to allocate.
     * @param internalFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the height of the base mipmap level.
     * @param depth the depth of the base mipmap level.
     * @return self reference.
     * @since 15.07.08
     */
    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width, final int height, final int depth) {

        new AllocateImage3DTask(
                mipmaps,
                internalFormat,
                width, height, depth).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that allocates data for the GLTexture.
     *
     * @since 15.07.08
     */
    public class AllocateImage3DTask extends GLTask {

        public final int mipmaps;
        public final GLTextureInternalFormat internalFormat;
        public final int width;
        public final int height;
        public final int depth;

        /**
         * Constructs a new AllocateImage3DTask.
         *
         * @param level the number of mipmaps to allocate.
         * @param internalFormat the sized pixel format.
         * @param width the width of the base mipmap level.
         * @param height the height of the base mipmap level.
         * @param depth the depth of the base mipmap level.
         * @throws GLException if mipmaps is less than 1.
         * @throws GLException if width is less than 1.
         * @throws GLException if height is less than 1.
         * @throws GLException if depth is less than 1.
         * @throws NullPointerException if internalFormat is null.
         * @since 15.07.08
         */
        public AllocateImage3DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final int width, final int height, final int depth) {

            if ((this.mipmaps = level) < 1) {
                throw new GLException("Mipmap levels cannot be less than 1!");
            }

            Objects.requireNonNull(this.internalFormat = internalFormat);

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

            final DSADriver dsa = GLTools.getDSAInstance();

            GLTexture.this.textureId = dsa.glCreateTextures(GL12.GL_TEXTURE_3D);
            dsa.glTextureStorage3d(textureId, mipmaps, this.internalFormat.value, width, height, depth);
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_3D;
        }
    }

    /**
     * Allocates the texture object as a 2D texture. Each mipmap level allocated
     * is half the dimensions of the previous level.
     *
     * @param mipmaps the number of mipmaps to allocate.
     * @param internalFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the depth of the base mipmap level.
     * @return self reference.
     * @since 15.07.08
     */
    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width, final int height) {

        new AllocateImage2DTask(
                mipmaps,
                internalFormat,
                width, height).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that allocates the data for the GLTexture.
     *
     * @since 15.07.08
     */
    public class AllocateImage2DTask extends GLTask {

        public final int mipmaps;
        public final GLTextureInternalFormat internalFormat;
        public final int width;
        public final int height;

        /**
         * Constructs a new AllocateImage2DTask.
         *
         * @param level the number of mipmap levels to allocate.
         * @param internalFormat the sized pixel format.
         * @param width the width of the base mipmap level.
         * @param height the height of the base mipmap level.
         * @throws GLException if mipmaps is less than 1.
         * @throws GLException if width is less than 1.
         * @throws GLException if height is less than 1.
         * @throws NullPointerException if internalFormat is null.
         * @since 15.07.08
         */
        public AllocateImage2DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final int width, final int height) {

            if ((this.mipmaps = level) < 1) {
                throw new GLException("Mipmap levels cannot be less than 1!");
            }

            Objects.requireNonNull(this.internalFormat = internalFormat);
            GLTexture.this.setSize(this.width = width, this.height = height, 1);
        }

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                throw new GLException("GLTexture has already been allocated!");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            GLTexture.this.textureId = dsa.glCreateTextures(GL11.GL_TEXTURE_2D);
            dsa.glTextureStorage2d(textureId, mipmaps, this.internalFormat.value, width, height);
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_2D;
        }
    }

    /**
     * Allocates the texture object as a 1D texture. Each mipmap level will
     * allocated is half the dimensions of the previous level.
     *
     * @param mipmaps the number of mipmaps to allocate.
     * @param internalFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @return self reference.
     * @since 15.07.08
     */
    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width) {

        new AllocateImage1DTask(mipmaps, internalFormat, width).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that allocates the texture object as a 1D texture.
     *
     * @since 15.07.08
     */
    public class AllocateImage1DTask extends GLTask {

        public final int mipmaps;
        public final GLTextureInternalFormat internalFormat;
        public final int width;

        /**
         * Constructs a new AllocateImage1D task.
         *
         * @param mipmapLevels the number of mipmaps to allocate.
         * @param internalFormat the sized pixel format.
         * @param width the width of the base mipmap level.
         * @throws GLException if mipmapLevels is less than 1.
         * @throws GLException if width is less than 1.
         * @throws NullPointerException if internalFormat is null.
         * @since 15.07.08
         */
        public AllocateImage1DTask(final int mipmapLevels,
                final GLTextureInternalFormat internalFormat,
                final int width) {

            if ((this.mipmaps = mipmapLevels) < 1) {
                throw new GLException("Mipmap levels cannot be less than 1!");
            }

            Objects.requireNonNull(this.internalFormat = internalFormat);
            GLTexture.this.setSize(this.width = width, 1, 1);
        }

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                throw new GLException("GLTexture has already been allocated!");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            GLTexture.this.textureId = dsa.glCreateTextures(GL11.GL_TEXTURE_1D);
            dsa.glTextureStorage1d(textureId, mipmaps, this.internalFormat.value, width);
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_1D;
        }
    }

    /**
     * Generates mipmaps for the GLTexture.
     *
     * @return self reference.
     * @since 15.07.08
     */
    public GLTexture generateMipmap() {
        new GenerateMipmapTask().glRun(this.getThread());
        return this;
    }

    /**
     * A GLTask that generates mipmap levels.
     *
     * @since 15.07.08
     */
    public class GenerateMipmapTask extends GLTask {

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture object!");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            if (dsa instanceof EXTDSADriver) {
                ((EXTDSADriver) dsa).glGenerateTextureMipmap(textureId, GLTexture.this.target.value);
            } else {
                dsa.glGenerateTextureMipmap(textureId);
            }
        }

    }    

    public void setTextureBuffer(
            final GLTextureInternalFormat internalFormat,
            final GLBuffer buffer) {

        new SetTextureBufferTask(internalFormat, buffer).glRun(this.getThread());        
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
            assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_2D", GLTexture.this.textureId);

            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, this.buffer.bufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_PIXEL_UNPACK_BUFFER", this.buffer.bufferId);

            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, this.level, this.xOffset, this.yOffset, this.width, this.height, this.format.value, this.type.value, 0);
            assert checkGLError() : glErrorMsg("glTexSubImage2D(IIIIIIII*)", "GL_TEXTURE_2D", this.level, this.xOffset, this.yOffset, this.width, this.height, this.format, this.type, 0);

            //TODO: this should instead restore the previous state.
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_2D", 0);
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
                throw new GLException("GLTexture is invalid! You must allocate a texture prior to assigning a texture buffer!");
            }

            if (!this.buffer.isValid()) {
                throw new GLException("GLBuffer is invalid!");
            }

            GL11.glBindTexture(GLBufferTarget.GL_TEXTURE_BUFFER.value, GLTexture.this.textureId);
            assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_BUFFER", GLTexture.this.textureId);

            GL31.glTexBuffer(GLBufferTarget.GL_TEXTURE_BUFFER.value, this.internalFormat.value, this.buffer.bufferId);
            assert checkGLError() : glErrorMsg("glTexBuffer(III)", "GL_TEXTURE_BUFFER", this.internalFormat, this.buffer.bufferId);

            GLTexture.this.width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_1D, 0, GL11.GL_TEXTURE_WIDTH);
            assert checkGLError() : glErrorMsg("glGetTexLevelParemetri(III)", "GL_TEXTURE_1D", 0, "GL_TEXTURE_WIDTH");

            GLTexture.this.height = GLTexture.this.depth = 1;
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_1D;

            GL11.glBindTexture(GLBufferTarget.GL_TEXTURE_BUFFER.value, 0);
            assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_BUFFER", 0);
        }
    }

    /**
     * Sets the texture parameters for the GLTexture object.
     *
     * @param params the texture parameters.
     * @return self reference.
     * @since 15.07.08
     */
    public GLTexture setAttributes(final GLTextureParameters params) {
        new SetAttributesTask(params).glRun(this.getThread());
        return this;
    }

    /**
     * A GLTask that sets the texture parameters.
     *
     * @since 15.07.08
     */
    public class SetAttributesTask extends GLTask {

        public final GLTextureParameters params;

        /**
         * Constructs a new SetAttributesTask.
         *
         * @param params the texture parameters.
         * @throws NullPointerException if params is null.
         * @since 15.07.08
         */
        public SetAttributesTask(final GLTextureParameters params) {
            Objects.requireNonNull(this.params = params);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture! GLTexture must be allocated prior to setting attributes.");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            if (dsa instanceof EXTDSADriver) {
                final EXTDSADriver patch = (EXTDSADriver) dsa;
                final int target = GLTexture.this.target.value;

                patch.glTextureParameteri(textureId, target, GL11.GL_TEXTURE_MIN_FILTER, this.params.minFilter.value);
                patch.glTextureParameteri(textureId, target, GL11.GL_TEXTURE_MAG_FILTER, this.params.magFilter.value);
                patch.glTextureParameteri(textureId, target, GL11.GL_TEXTURE_WRAP_S, this.params.wrapS.value);
                patch.glTextureParameteri(textureId, target, GL11.GL_TEXTURE_WRAP_T, this.params.wrapT.value);
                patch.glTextureParameteri(textureId, target, GL12.GL_TEXTURE_WRAP_R, this.params.wrapR.value);
                patch.glTextureParameterf(textureId, target, GL12.GL_TEXTURE_MIN_LOD, this.params.minLOD);
                patch.glTextureParameterf(textureId, target, GL12.GL_TEXTURE_MAX_LOD, this.params.maxLOD);
                patch.glTextureParameterf(textureId, target, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.params.anisotropicLevel);
            } else {
                dsa.glTextureParameteri(textureId, GL11.GL_TEXTURE_MIN_FILTER, this.params.minFilter.value);
                dsa.glTextureParameteri(textureId, GL11.GL_TEXTURE_MAG_FILTER, this.params.magFilter.value);
                dsa.glTextureParameteri(textureId, GL11.GL_TEXTURE_WRAP_S, this.params.wrapS.value);
                dsa.glTextureParameteri(textureId, GL11.GL_TEXTURE_WRAP_T, this.params.wrapT.value);
                dsa.glTextureParameteri(textureId, GL12.GL_TEXTURE_WRAP_R, this.params.wrapR.value);
                dsa.glTextureParameterf(textureId, GL12.GL_TEXTURE_MIN_LOD, this.params.minLOD);
                dsa.glTextureParameterf(textureId, GL12.GL_TEXTURE_MAX_LOD, this.params.maxLOD);
                dsa.glTextureParameterf(textureId, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.params.anisotropicLevel);
            }

        }
    }

    private static final GLQuery<Integer> MAX_TEX_UNIT_QUERY = new MaxTextureUnitQuery();

    /**
     * Retrieves the maximum number of textures that can be bound at a time.
     *
     * @return the max number of textures
     * @since 15.07.06
     */
    public static int getMaxSupportedTextureUnits() {
        return MAX_TEX_UNIT_QUERY.glCall();
    }

    /**
     * A GLQuery that checks how many textures can be bound at a time.
     *
     * @since 15.07.06
     */
    public static final class MaxTextureUnitQuery extends GLQuery<Integer> {

        boolean checked = false;
        int maxUnits;

        @Override
        public Integer call() throws Exception {
            if (this.checked) {
                return this.maxUnits;
            }

            this.maxUnits = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS) = %d failed!", this.maxUnits);
            assert this.maxUnits > 0;

            return this.maxUnits;
        }

        @Override
        protected Integer handleInterruption() {
            // return the minimum specification for OpenGL 3.0
            return 8;
        }
    }

    private static final GLQuery<Integer> MAX_TEX_SIZE_QUERY = new MaxTextureSizeQuery();

    /**
     * Retrieves the largest sized texture that is supported.
     *
     * @return the largest texture size.
     * @since 15.07.06
     */
    public static int getMaxTextureSize() {
        return MAX_TEX_SIZE_QUERY.glCall();
    }

    /**
     * A GLQuery that checks the largest texture size supported.
     *
     * @since 15.07.06
     */
    public static final class MaxTextureSizeQuery extends GLQuery<Integer> {

        boolean checked = false;
        int maxUnits;

        @Override
        public Integer call() throws Exception {
            if (this.checked) {
                return this.maxUnits;
            }

            this.maxUnits = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
            assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_MAX_TEXTURE_SIZE");

            return this.maxUnits;
        }

        @Override
        protected Integer handleInterruption() {
            // return the minimum specification for OpenGL 3.0
            return 1 << 12;
        }
    }

    private static Map<GLTextureInternalFormat, GLTextureFormat> MEM_PREFERRED_FORMATS = new HashMap<>();

    /**
     * Retrieves the preferred GLTextureFormat for the specified
     * GLTextureInternalFormat.
     *
     * @param format the format to check.
     * @return the preferred format.
     * @since 15.07.20
     */
    public static GLTextureFormat getPreferredTextureFormat(final GLTextureInternalFormat format) {
        if (MEM_PREFERRED_FORMATS.containsKey(format)) {
            return MEM_PREFERRED_FORMATS.get(format);
        } else {
            final GLTextureFormat res = new PreferredInternalFormatQuery(format).glCall();

            MEM_PREFERRED_FORMATS.put(format, res);
            return res;
        }
    }

    /**
     * A GLQuery that checks the preferred texture format for the specified
     * GLTextureInternalFormat.
     *
     * @since 15.07.20
     */
    public static final class PreferredInternalFormatQuery extends GLQuery<GLTextureFormat> {

        final static Map<GLTextureInternalFormat, GLTextureFormat> MEM_QUERIES = new HashMap<>();
        final GLTextureInternalFormat testFormat;

        /**
         * Constructs a new PreferredInternalFormatQuery.
         *
         * @param format the GLTextureInternalFormat to query on.
         * @since 15.07.20
         */
        public PreferredInternalFormatQuery(final GLTextureInternalFormat format) {
            this.testFormat = Objects.requireNonNull(format);
        }

        @Override
        public GLTextureFormat call() throws Exception {
            final ContextCapabilities cap = GL.getCapabilities();

            final int raw;
            if (cap.OpenGL43) {
                raw = GL42.glGetInternalformati(GL11.GL_TEXTURE_2D, this.testFormat.value, GL43.GL_TEXTURE_IMAGE_FORMAT);
            } else if (cap.GL_ARB_internalformat_query2) {
                raw = ARBInternalformatQuery.glGetInternalformati(GL11.GL_TEXTURE_2D, this.testFormat.value, ARBInternalformatQuery2.GL_TEXTURE_IMAGE_FORMAT);
            } else {
                throw new UnsupportedOperationException("Querying preferred texture format requires either an OpenGL4.3 context or ARB_internal_format_query2 support.");
            }

            return GLTextureFormat.of(raw).orElseThrow(() -> new GLException("Unknown texture format: " + raw));
        }

        @Override
        protected GLTextureFormat handleInterruption() {
            // todo: this should probably return a simple safe  value.
            throw new GLException("PreferredInternalFormatQuery was interrupted!");
        }
    }

    public ByteBuffer downloadImage(final int level, final GLTextureFormat format, final GLType type) {
        return new DownloadImageQuery(level, format, type).glCall(this.getThread());
    }

    public ByteBuffer downloadImage(final int level, final GLTextureFormat format, final GLType type, final ByteBuffer buffer) {
        return new DownloadImageQuery(level, format, type, buffer).glCall(this.getThread());
    }

    public class DownloadImageQuery extends GLQuery<ByteBuffer> {

        final int level;
        final GLTextureFormat format;
        final GLType type;
        final int bufferSize;
        final ByteBuffer buffer;

        public DownloadImageQuery(final int level, final GLTextureFormat format, final GLType type) {
            if ((this.level = level) < 0) {
                throw new GLException("Level cannot be less than 0!");
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            final int pixelSize;

            switch (format) {
                case GL_RED:
                case GL_GREEN:
                case GL_BLUE:
                case GL_ALPHA:
                case GL_DEPTH_COMPONENT:
                case GL_STENCIL_INDEX:
                    pixelSize = 1;
                    break;
                case GL_RGB:
                case GL_BGR:
                    pixelSize = 3;
                    break;
                case GL_BGRA:
                case GL_RGBA:
                    pixelSize = 4;
                    break;
                default:
                    throw new GLException("Unable to infer pixel width! Invalid pixel format for operation: " + format);
            }
            final int pixels = GLTexture.this.getDepth() * GLTexture.this.getHeight() * GLTexture.this.getWidth();

            switch (type) {
                case GL_UNSIGNED_BYTE:
                case GL_BYTE:
                    this.bufferSize = pixels * pixelSize;
                    break;
                case GL_UNSIGNED_SHORT:
                case GL_SHORT:
                    this.bufferSize = pixels * pixelSize * 2;
                    break;
                case GL_UNSIGNED_INT:
                case GL_INT:
                case GL_FLOAT:
                    this.bufferSize = pixels * pixelSize * 4;
                    break;
                case GL_UNSIGNED_BYTE_3_3_2:
                case GL_UNSIGNED_BYTE_2_3_3_REV:
                    this.bufferSize = pixels;
                    break;
                case GL_UNSIGNED_SHORT_5_6_5:
                case GL_UNSIGNED_SHORT_5_6_5_REV:
                case GL_UNSIGNED_SHORT_4_4_4_4:
                case GL_UNSIGNED_SHORT_4_4_4_4_REV:
                case GL_UNSIGNED_SHORT_5_5_5_1:
                case GL_UNSIGNED_SHORT_1_5_5_5_REV:
                    this.bufferSize = pixels * 2;
                    break;
                case GL_UNSIGNED_INT_8_8_8_8:
                case GL_UNSIGNED_INT_8_8_8_8_REV:
                case GL_UNSIGNED_INT_10_10_10_2:
                case GL_UNSIGNED_INT_2_10_10_10_REV:
                    this.bufferSize = pixels * 4;
                    break;
                default:
                    throw new GLException("Unable to infer image size! Invalid type for operation: " + type);
            }

            this.buffer = ByteBuffer.allocateDirect(this.bufferSize).order(ByteOrder.nativeOrder());
        }

        public DownloadImageQuery(final int level, final GLTextureFormat format, final GLType type, final ByteBuffer buffer) {
            if ((this.level = level) < 0) {
                throw new GLException("Level cannot be less than 0!");
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
            this.buffer = Objects.requireNonNull(buffer);
            this.bufferSize = buffer.limit();
        }

        @Override
        public ByteBuffer call() throws Exception {
            DSADriver driver = GLTools.getDSAInstance();

            if (driver instanceof EXTDSADriver) {
                ((EXTDSADriver) driver).glGetTextureImage(
                        GLTexture.this.textureId,
                        GLTexture.this.target.value,
                        this.level,
                        this.format.value,
                        this.type.value,
                        this.bufferSize, this.buffer);
            } else {
                driver.glGetTextureImage(
                        GLTexture.this.textureId,
                        this.level,
                        this.format.value, this.type.value,
                        this.bufferSize, this.buffer);
            }

            return this.buffer; // does this need flip?
        }

    }
}
