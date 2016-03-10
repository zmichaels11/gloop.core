/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.Texture;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLTexture represents an immutable OpenGL texture object. Currently only
 * supports 1D, 2D, or 3D textures.
 *
 * @author zmichaels
 * @since 15.07.08
 */
public class GLTexture extends GLObject {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLTexture");

    transient volatile Texture texture;

    private volatile int width = 0;
    private volatile int height = 0;
    private volatile int depth = 0;

    private volatile boolean isSparse;
    private String name = "";

    private volatile int vpageWidth;
    private volatile int vpageHeight;
    private volatile int vpageDepth;

    /**
     * Retrieves the virtual page width. This only has meaningful data if
     * GL_ARB_sparse_texture is supported and the texture was allocated as a
     * sparse texture.
     *
     * @return the width of the virtual page.
     * @since 16.01.05
     */
    public int getVirtualPageWidth() {
        return this.vpageWidth;
    }

    /**
     * Retrieves the virtual page height. This only has meaningful data if
     * GL_ARB_sparse_texture is supported and the texture was allocated as a
     * sparse texture.
     *
     * @return the height of the virtual page.
     * @since 16.01.05
     */
    public int getVirtualPageHeight() {
        return this.vpageHeight;
    }

    /**
     * Retrieves the virtual page depth. This only has meaningful data if
     * GL_ARB_sparse_texture is supported and the texture was allocated as a
     * sparse texture.
     *
     * @return the depth of the virtual page.
     * @since 16.01.05
     */
    public int getVirtualPageDepth() {
        return this.vpageDepth;
    }

    /**
     * Checks if the GLTexture is marked as sparse. This may return an invalid
     * state if the texture is not complete yet.
     *
     * @return true if the texture is sparse.
     * @since 16.01.05
     */
    public boolean isSparse() {
        return this.isSparse;
    }

    /**
     * Sets the texture's name.
     *
     * @param name the new name of the texture.
     * @since 15.12.18
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLTexture[{}] to GLTexture[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the texture.
     *
     * @return the texture's name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLTexture on the default OpenGL thread.
     *
     * @since 15.07.08
     */
    public GLTexture() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLTexture on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread.
     * @since 15.07.08
     */
    public GLTexture(final GLThread thread) {
        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Construct GLTexture object on thread: {}",
                thread);
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
    public GLTexture(
            final int mipmaps,
            final GLTextureInternalFormat pixelFormat,
            final int width, final int height, final int depth) {

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
    public GLTexture(
            final int mipmaps,
            final GLTextureInternalFormat pixelFormat,
            final int width, final int height) {

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
    public GLTexture(
            final int mipmaps,
            final GLTextureInternalFormat pixelFormat,
            final int width) {

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
    public GLTexture(
            final GLThread thread,
            final int mipmaps,
            final GLTextureInternalFormat pixelFormat,
            final int width, final int height, final int depth) {

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
    public GLTexture(
            final GLThread thread,
            final int mipmaps,
            final GLTextureInternalFormat pixelFormat,
            final int width, final int height) {

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
    public GLTexture(
            final GLThread thread,
            final int mipmaps,
            final GLTextureInternalFormat pixelFormat,
            final int width) {

        this(thread);
        this.allocate(mipmaps, pixelFormat, width);
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
            throw new GLException("GLTexture has not yet been initialized!");
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
            throw new GLException("GLTexture has not yet been initialized!");
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
            throw new GLException("GLTexture has not yet been initialized!");
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
        return texture != null && texture.isValid();
    }

    /**
     * Binds the GLTexture to the specified texture unit.
     *
     * @param textureUnit the texture unit to bind to.
     * @since 15.07.08
     */
    public void bind(final int textureUnit) {
        new BindTask(textureUnit).glRun(this.getThread());
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

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Bind Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tBinding GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tActive texture: {}", this.activeTexture);

            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid! You must allocate a texture prior to binding it.");
            }

            GLTools.getDriverInstance().textureBind(texture, activeTexture);
            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Bind Task ###############");
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

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Delete Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDeleting GLTexture[{}]", getName());

            if (GLTexture.this.isValid()) {
                GLTools.getDriverInstance().textureDelete(texture);
                texture = null;

                GLTexture.this.width = GLTexture.this.height = GLTexture.this.depth = 0;
                GLTexture.this.isSparse = false;
                GLTexture.this.name = "";
            } else {
                throw new GLException("GLTexture is not valid! Cannot delete a texture if it does not exist.");
            }

            LOGGER.trace(
                    GLOOP_MARKER,
                    "############### End GLTexture Delete Task ###############");
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

        new UpdateImage3DTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                format,
                type,
                data).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that updates a 3D image.
     *
     * @since 15.07.08
     */
    public final class UpdateImage3DTask extends GLTask {

        private final int level;
        private final int xOffset;
        private final int yOffset;
        private final int zOffset;
        private final int width;
        private final int height;
        private final int depth;
        private final GLTextureFormat format;
        private final GLType type;
        private final ByteBuffer data;

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

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            this.data = GLTools.checkBuffer(
                    data.asReadOnlyBuffer().order(ByteOrder.nativeOrder()));

            if ((this.width = width) < 0) {
                throw new GLException("Width cannot be less than 0!");
            }

            if ((this.height = height) < 0) {
                throw new GLException("Height cannot be less than 0!");
            }

            if ((this.depth = depth) < 0) {
                throw new GLException("Depth cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Update Image 3D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tUpdating GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tMipmap level: {}", level);
            LOGGER.trace(GLOOP_MARKER, "\tOffsets: <{}, {}, {}>", xOffset, yOffset, zOffset);
            LOGGER.trace(GLOOP_MARKER, "\tSize: <{}, {}, {}>", width, height, depth);
            LOGGER.trace(GLOOP_MARKER, "\tFormat: {}", format);
            LOGGER.trace(GLOOP_MARKER, "\tType: {}", this.type);

            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GLTools.getDriverInstance().textureSetData(
                    texture,
                    level,
                    xOffset, yOffset, zOffset,
                    width, height, depth,
                    format.value, type.value, data);

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Update Image 3D Task ###############");
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

        new UpdateImage2DTask(
                level,
                xOffset, yOffset,
                width, height,
                format, type, data).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that updates a 3D image.
     *
     * @since 15.07.08
     */
    public final class UpdateImage2DTask extends GLTask {

        private final int level;
        private final int xOffset;
        private final int yOffset;
        private final int width;
        private final int height;
        private final GLTextureFormat format;
        private final GLType type;
        private final ByteBuffer data;

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

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            this.data = GLTools.checkBuffer(
                    data.asReadOnlyBuffer()
                    .order(ByteOrder.nativeOrder()));

            if ((this.width = width) < 0) {
                throw new GLException("Width cannot be less than 0!");
            }

            if ((this.height = height) < 0) {
                throw new GLException("Height cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Update Image 2D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tUpdating GLTexture: {}", getName());
            LOGGER.trace(GLOOP_MARKER, "\tMipmap level: {}", level);
            LOGGER.trace(GLOOP_MARKER, "\tOffsets: <{}, {}>", xOffset, yOffset);
            LOGGER.trace(GLOOP_MARKER, "\tSize: <{}, {}>", width, height);
            LOGGER.trace(GLOOP_MARKER, "\tFormat: {}", format);
            LOGGER.trace(GLOOP_MARKER, "\tType: {}", type);

            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GLTools.getDriverInstance().textureSetData(
                    texture,
                    level,
                    xOffset, yOffset, 1,
                    width, height, 1,
                    format.value, type.value,
                    data);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "############### End GLTexture Update Image 2D Task ###############");
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

        new UpdateImage1DTask(
                level,
                xOffset,
                width,
                format, type, data).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that updates a 1D segment of a texture.
     *
     * @since 15.07.08
     */
    public final class UpdateImage1DTask extends GLTask {

        private final int level;
        private final int xOffset;
        private final int width;
        private final GLTextureFormat format;
        private final GLType type;
        private final ByteBuffer data;

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

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            this.data = GLTools.checkBuffer(data.asReadOnlyBuffer().order(ByteOrder.nativeOrder()));
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Update 1D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tUpdating GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tMipmap level: {}", level);
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {}", xOffset);
            LOGGER.trace(GLOOP_MARKER, "\tWidth: {}", width);
            LOGGER.trace(GLOOP_MARKER, "\tFormat: {}", format);
            LOGGER.trace(GLOOP_MARKER, "\tType: {}", this.type);

            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            GLTools.getDriverInstance().textureSetData(
                    texture,
                    level,
                    xOffset, 1, 1,
                    width, 1, 1,
                    format.value, type.value, data);

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Update 1D Task ###############");
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
    public final class AllocateImage3DTask extends GLTask {

        private final int mipmaps;
        private final GLTextureInternalFormat internalFormat;
        private final int width;
        private final int height;
        private final int depth;

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

            this.internalFormat = Objects.requireNonNull(internalFormat);

            GLTexture.this.setSize(
                    this.width = width,
                    this.height = height,
                    this.depth = depth);
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Allocate Image 3D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tMipmap Levels: {}", mipmaps);
            LOGGER.trace(GLOOP_MARKER, "\tInternal texture format: {}", internalFormat);
            LOGGER.trace(GLOOP_MARKER, "\tSize: <{}, {}, {}>", this.width, this.height, this.depth);

            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is not valid!");
            }

            texture = GLTools.getDriverInstance().textureAllocate(mipmaps, internalFormat.value, width, height, depth);
            GLTexture.this.name = "id=" + texture.hashCode();

            LOGGER.trace(GLOOP_MARKER, "Initialized GLTexture[{}]!", GLTexture.this.name);
            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Allocate Image 3D Task ###############");
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
    public final class AllocateImage2DTask extends GLTask {

        private final int mipmaps;
        private final GLTextureInternalFormat internalFormat;
        private final int width;
        private final int height;

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

            this.internalFormat = Objects.requireNonNull(internalFormat);
            GLTexture.this.setSize(this.width = width, this.height = height, 1);
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Allocate Image 2D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tMipmap Levels: {}", mipmaps);
            LOGGER.trace(GLOOP_MARKER, "\tInternal texture format: {}", internalFormat);
            LOGGER.trace(GLOOP_MARKER, "\tSize: <{}, {}>", this.width, this.height);

            if (GLTexture.this.isValid()) {
                throw new GLException("GLTexture has already been allocated!");
            }

            texture = GLTools.getDriverInstance().textureAllocate(mipmaps, internalFormat.value, width, height, 1);

            GLTexture.this.name = "id=" + texture.hashCode();

            LOGGER.trace(GLOOP_MARKER, "Initialized GLTexture[{}]!", GLTexture.this.getName());
            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Allocate 2D Task ###############");
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
    public final class AllocateImage1DTask extends GLTask {

        private final int mipmaps;
        private final GLTextureInternalFormat internalFormat;
        private final int width;

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

            this.internalFormat = Objects.requireNonNull(internalFormat);

            GLTexture.this.setSize(this.width = width, 1, 1);
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Allocate Image 1D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tMipmap levels: {}", mipmaps);
            LOGGER.trace(GLOOP_MARKER, "\tInternal texture format: {}", internalFormat);
            LOGGER.trace(GLOOP_MARKER, "\tWidth: {}", this.width);

            if (GLTexture.this.isValid()) {
                throw new GLException("GLTexture has already been allocated!");
            }

            texture = GLTools.getDriverInstance().textureAllocate(mipmaps, internalFormat.value, width, 1, 1);
            GLTexture.this.name = "id=" + texture.hashCode();

            LOGGER.trace(GLOOP_MARKER, "Initialized GLTexture[{}]!", GLTexture.this.getName());
            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Allocate Image 1D Task ###############");
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
    public final class GenerateMipmapTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Generate Mipmap Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tGenerating mipmaps for GLTexture[{}]", getName());

            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture object!");
            }

            GLTools.getDriverInstance().textureGenerateMipmap(texture);

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Generate Mipmap Task ###############");
        }

    }

    public void setTextureBuffer(
            final GLTextureInternalFormat internalFormat,
            final GLBuffer buffer) {

        new SetTextureBufferTask(internalFormat, buffer).glRun(this.getThread());
    }

    public final class SetPixelBuffer2DTask extends GLTask {

        private final GLBuffer buffer;
        private final int level;
        private final int xOffset;
        private final int yOffset;
        private final int width;
        private final int height;
        private final GLTextureFormat format;
        private final GLType type;

        public SetPixelBuffer2DTask(
                final int level,
                final int xOffset, final int yOffset,
                final int width, final int height,
                final GLTextureFormat format,
                final GLType type,
                final GLBuffer buffer) {

            this.buffer = Objects.requireNonNull(buffer);
            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

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
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Set Pixel Buffer 2D Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tSetting pixel buffer of GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tMipmap level: {}", level);
            LOGGER.trace(GLOOP_MARKER, "\tOffsets: <{}, {}>", xOffset, yOffset);
            LOGGER.trace(GLOOP_MARKER, "\tSize: <{}, {}>", width, height);
            LOGGER.trace(GLOOP_MARKER, "\tFormat: {}", format);
            LOGGER.trace(GLOOP_MARKER, "\tType: {}", type);

            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else if (!this.buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            throw new UnsupportedOperationException("not implemented...");
            /*
            final DSADriver dsa = GLTools.getDSAInstance();

            dsa.glBindTexture(
                    3553 /* GL_TEXTURE_2D /,
                    GLTexture.this.textureId);

            dsa.glBindBuffer(
                    35052 /* GL_PIXEL_UNPACK_BUFFER /,
                    this.buffer.bufferId);

            dsa.glTexSubImage2D(
                    3553 /* GL_TEXTURE_2D /,
                    this.level,
                    this.xOffset, this.yOffset,
                    this.width, this.height,
                    this.format.value,
                    this.type.value,
                    0);

            //TODO: this should instead restore the previous state.
            dsa.glBindTexture(
                    3553 /* GL_TEXTURE_2D /,
                    0);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "############### End GLTexture Set Pixel Buffer 2D Task ###############");
             */
        }
    }

    public final class SetTextureBufferTask extends GLTask {

        private final GLBuffer buffer;
        private final GLTextureInternalFormat internalFormat;

        public SetTextureBufferTask(
                final GLTextureInternalFormat internalFormat,
                final GLBuffer buffer) {

            this.buffer = Objects.requireNonNull(buffer);
            this.internalFormat = Objects.requireNonNull(internalFormat);
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Set Texture Buffer Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tSetting texture buffer for GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tTexture buffer: GLBuffer[{}]", buffer.getName());
            LOGGER.trace(GLOOP_MARKER, "\tFormat: {}", this.internalFormat);

            throw new UnsupportedOperationException("not yet implemented.");
            /*
            if (!GLTexture.this.isValid()) {
                throw new GLException("GLTexture is invalid! You must allocate a texture prior to assigning a texture buffer!");
            }

            if (!this.buffer.isValid()) {
                throw new GLException("GLBuffer is invalid!");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            dsa.glBindTexture(
                    GLBufferTarget.GL_TEXTURE_BUFFER.value,
                    GLTexture.this.textureId);

            dsa.glTexBuffer(
                    GLBufferTarget.GL_TEXTURE_BUFFER.value,
                    this.internalFormat.value,
                    this.buffer.bufferId);

            GLTexture.this.width = dsa.glGetTexLevelParameteri(
                    3552 /* GL_TEXTURE_1D /,
                    0,
                    4096 /*GL_TEXTURE_WIDTH /);

            GLTexture.this.height = GLTexture.this.depth = 1;
            GLTexture.this.target = GLTextureTarget.GL_TEXTURE_1D;

            dsa.glBindTexture(GLBufferTarget.GL_TEXTURE_BUFFER.value, 0);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "############### End GLTexture Set Texture Buffer Task ###############");
             */
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
    public final class SetAttributesTask extends GLTask {

        private final GLTextureParameters params;

        /**
         * Constructs a new SetAttributesTask.
         *
         * @param params the texture parameters.
         * @throws NullPointerException if params is null.
         * @since 15.07.08
         */
        public SetAttributesTask(final GLTextureParameters params) {
            this.params = Objects.requireNonNull(params);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Set Attributes Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tSetting attributes of GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tAttributes: GLTextureParameters[{}]", this.params.getName());

            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture! GLTexture must be allocated prior to setting attributes.");
            }

            final Driver driver = GLTools.getDriverInstance();

            driver.textureSetParameter(texture, 10241 /* GL_TEXTURE_MIN_FILTER */, params.minFilter.value);
            driver.textureSetParameter(texture, 10240 /* GL_TEXTURE_MAG_FILTER */, params.magFilter.value);
            driver.textureSetParameter(texture, 10242 /* GL_TEXTURE_WRAP_S */, params.wrapS.value);
            driver.textureSetParameter(texture, 10243 /* GL_TEXTURE_WRAP_T */, params.wrapT.value);
            driver.textureSetParameter(texture, 32882 /* GL_TEXTURE_WRAP_R */, params.wrapR.value);
            driver.textureSetParameter(texture, 33082 /* GL_TEXTURE_MIN_LOD */, params.minLOD);
            driver.textureSetParameter(texture, 33083 /* GL_TEXTURE_MAX_LOD */, params.maxLOD);
            driver.textureSetParameter(texture, 34046 /* GL_TEXTURE_MAX_ANISOTROPY_EXT */, params.anisotropicLevel);

            if (params.isSparse) {
                driver.textureSetParameter(texture, 37286 /* GL_SPARSE_TEXTURE_ARB */, 1);
                isSparse = true;
            }

            if (GLTexture.this.isSparse) {
                GLTexture.this.vpageWidth = (int) driver.textureGetPageWidth(texture);
                GLTexture.this.vpageHeight = (int) driver.textureGetPageHeight(texture);
                GLTexture.this.vpageDepth = (int) driver.textureGetPageDepth(texture);
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Set Attributes Task ###############");
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
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Max Texture Unit Query ###############");

            if (this.checked) {
                return this.maxUnits;
            }

            this.maxUnits = (int) GLTools.getDriverInstance().textureGetMaxBoundTextures();

            assert this.maxUnits > 0;

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Max Texture Unit Query ###############");

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
        int maxSize;

        @Override
        public Integer call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Max Texture Size Query ###############");

            if (this.checked) {
                return this.maxSize;
            }

            this.maxSize = (int) GLTools.getDriverInstance().textureGetMaxSize();

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Max Texture Size Query ###############");

            return this.maxSize;
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
            return GLTextureFormat.GL_RGBA;
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

    public final class DownloadImageQuery extends GLQuery<ByteBuffer> {

        private final int level;
        private final GLTextureFormat format;
        private final GLType type;
        private final int bufferSize;
        private final ByteBuffer buffer;

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

        @SuppressWarnings("unchecked")
        @Override
        public ByteBuffer call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLTexture Download Image Query ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDownloading GLTexture[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tMipmap level: {}", level);
            LOGGER.trace(GLOOP_MARKER, "\tFormat: {}", format);
            LOGGER.trace(GLOOP_MARKER, "\tType: {}", type);
            LOGGER.trace(GLOOP_MARKER, "\tBuffer size: {} bytes", this.bufferSize);

            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            }

            GLTools.getDriverInstance().textureGetData(texture, level, format.value, type.value, buffer);
            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Download Image Query ###############");

            return this.buffer; // does this need flip?
        }

    }

    /**
     * Invalidates the specified mipmap level of the texture.
     *
     * @param level the mipmap level to invalidate.
     * @since 16.01.04
     */
    public void invalidate(final int level) {
        new InvalidateTask(level).glRun(this.getThread());
    }

    /**
     * A GLTask that invalidates a mipmap level.
     *
     * @since 16.01.04
     */
    public final class InvalidateTask extends GLTask {

        private final int level;

        /**
         * Constructs a new InvalidateTask.
         *
         * @param level the mipmap level to invalidate.
         * @since 16.01.04
         */
        public InvalidateTask(final int level) {
            if ((this.level = level) < 0) {
                throw new GLException("Level cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER,
                    "############### Begin GLTexture Invalidate Task ###############");
            LOGGER.trace(GLOOP_MARKER, "Mipmap level: {}", this.level);

            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else {
                GLTools.getDriverInstance().textureInvalidateData(texture, level);
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLTexture Invalidate Task ###############");
        }
    }

    /**
     * Invalidates a section of the GLTexture.
     *
     * @param level the level to invalidate.
     * @param xOffset the x-offset to invalidate.
     * @param yOffset the y-offset to invalidate.
     * @param zOffset the z-offset to invalidate.
     * @param width the width
     * @param height the height.
     * @param depth the depth.
     */
    public void invalidateSubImage(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth) {

        new InvalidateSubImageTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth)
                .glRun(this.getThread());
    }

    /**
     * A GLTask that invalidates a section of a GLTexture.
     *
     * @since 16.01.04
     */
    public final class InvalidateSubImageTask extends GLTask {

        private final int level;
        private final int xOffset, yOffset, zOffset;
        private final int width, height, depth;

        /**
         * Constructs a new InvalidateSubImageTask
         *
         * @param level the mipmap level
         * @param xOffset the x-offset
         * @param yOffset the y-offset
         * @param zOffset the z-offset
         * @param width the width
         * @param height the height
         * @param depth the depth.
         * @since 16.01.04
         */
        public InvalidateSubImageTask(
                final int level,
                final int xOffset, final int yOffset, final int zOffset,
                final int width, final int height, final int depth) {

            if ((this.level = level) < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
            } else if ((this.xOffset = xOffset) < 0) {
                throw new GLException("XOffset cannot be less than 0!");
            } else if ((this.yOffset = yOffset) < 0) {
                throw new GLException("YOffset cannot be less than 0!");
            } else if ((this.zOffset = zOffset) < 0) {
                throw new GLException("ZOffset cannot be less than 0!");
            } else if ((this.width = width) < 0) {
                throw new GLException("Width cannot be less than 0!");
            } else if ((this.height = height) < 0) {
                throw new GLException("Height cannot be less than 0!");
            } else if ((this.depth = depth) < 0) {
                throw new GLException("Depth cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else {
                GLTools.getDriverInstance().textureInvalidateRange(
                        texture, level,
                        this.xOffset, this.yOffset, this.zOffset,
                        this.width, this.height, this.depth);
            }
        }
    }

    /**
     * Marks a section of the texture for deallocation. This is only applicable
     * to sparse textures.
     *
     * @param level the mipmap level.
     * @param xOffset the xOffset in virtual page units.
     * @param yOffset the yOffset in virtual page units.
     * @param zOffset the zOffset in virtual page units.
     * @param width the width in virtual page units.
     * @param height the height in virtual page units.
     * @param depth the depth in virtual page units.
     * @return self reference.
     * @since 16.01.05
     */
    public GLTexture deallocateRegion(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth) {

        new DeallocateRegionTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth).glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that marks a section of the texture for deallocation. This is
     * only applicable to sparse textures.
     *
     * @since 16.01.05
     */
    public final class DeallocateRegionTask extends GLTask {

        private final int level;
        private final int xOffset;
        private final int yOffset;
        private final int zOffset;
        private final int width;
        private final int height;
        private final int depth;

        /**
         * Constructs a new DeleteRegionTask.
         *
         * @param level the mipmap level.
         * @param xOffset the xoffset
         * @param yOffset the yoffset
         * @param zOffset the zoffset
         * @param width the width.
         * @param height the height.
         * @param depth the depth.
         * @since 16.01.05
         */
        public DeallocateRegionTask(
                final int level,
                final int xOffset, final int yOffset, final int zOffset,
                final int width, final int height, final int depth) {

            if ((this.level = level) < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
            } else if ((this.xOffset = xOffset) < 0) {
                throw new GLException("XOffset cannot be less than 0!");
            } else if ((this.yOffset = yOffset) < 0) {
                throw new GLException("YOffset cannot be less than 0!");
            } else if ((this.zOffset = zOffset) < 0) {
                throw new GLException("ZOffset cannot be less than 0!");
            } else if ((this.width = width) < 0) {
                throw new GLException("Width cannot be less than 0!");
            } else if ((this.height = height) < 0) {
                throw new GLException("Height cannot be less than 0!");
            } else if ((this.depth = depth) < 0) {
                throw new GLException("Depth cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else if (GLTexture.this.isSparse) {
                GLTools.getDriverInstance().textureDeallocatePage(
                        texture, level,
                        xOffset, yOffset, zOffset,
                        width, height, depth);
            }
        }
    }

    /**
     * Allocates all of the pages required for the specified region of the
     * texture. No operations are performed if the section is already allocated.
     * This call is ignored if the texture is not allocated as a sparse texture.
     *
     * @param level the mipmap level.
     * @param xOffset the xOffset in virtual page units.
     * @param yOffset the yOffset in virtual page units.
     * @param zOffset the zOffset in virtual page units.
     * @param width the width of the region in virtual page units.
     * @param height the height of the region in virtual page units.
     * @param depth the depth of the region in virtual page units.
     * @return self reference.
     * @since 16.01.05
     */
    public GLTexture allocateRegion(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth) {

        new AllocateRegionTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth).glRun(this.getThread());
        return this;
    }

    /**
     * Allocates all the pages required for the specified region of the sparse
     * texture.
     *
     * @since 16.01.05
     */
    public final class AllocateRegionTask extends GLTask {

        private final int level;
        private final int xOffset;
        private final int yOffset;
        private final int zOffset;
        private final int width;
        private final int height;
        private final int depth;

        /**
         * Constructs a new AllocateRegionTask.
         *
         * @param level the mipmap level.
         * @param xOffset the x offset.
         * @param yOffset the y offset
         * @param zOffset the z offset
         * @param width the width.
         * @param height the height.
         * @param depth the depth.
         * @since 16.01.05
         */
        public AllocateRegionTask(
                final int level,
                final int xOffset, final int yOffset, final int zOffset,
                final int width, final int height, final int depth) {

            if ((this.level = level) < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
            } else if ((this.xOffset = xOffset) < 0) {
                throw new GLException("XOffset cannot be less than 0!");
            } else if ((this.yOffset = yOffset) < 0) {
                throw new GLException("YOffset cannot be less than 0!");
            } else if ((this.zOffset = zOffset) < 0) {
                throw new GLException("ZOffset cannot be less than 0!");
            } else if ((this.width = width) < 0) {
                throw new GLException("Width cannot be less than 0!");
            } else if ((this.height = height) < 0) {
                throw new GLException("Height cannot be less than 0!");
            } else if ((this.depth = depth) < 0) {
                throw new GLException("Depth cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else if (GLTexture.this.isSparse) {
                GLTools.getDriverInstance().textureAllocatePage(
                        texture, level,
                        xOffset, yOffset, zOffset,
                        width, height, depth);
            }
        }

    }

    /**
     * Retrieves the virtual page size for the GLTexture object.
     *
     * @return the dimensions of the virtual page size. All dimensions will be 0
     * if GL_ARB_sparse_texture is not supported.
     *
     * @since 16.01.05
     */
    public int[] getVirtualPageSize() {
        return new int[]{this.vpageWidth, this.vpageHeight, this.vpageDepth};
    }
}
