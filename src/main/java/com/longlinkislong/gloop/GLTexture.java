/* 
 * Copyright (c) 2014-2016, longlinkislong.com
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

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Texture;
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

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLTexture");

    transient volatile Texture texture;

    private volatile int width = 0;
    private volatile int height = 0;
    private volatile int depth = 0;

    private String name = "";

    private GLTextureInternalFormat internalFormat;

    /**
     * Retrieves the internal format of the texture. The texture must be
     * allocated AND the allocation task must be processed for this method to
     * succeed.
     *
     * @return the internal format.
     * @throws IllegalStateException if the texture has not been allocated.
     * @since 16.07.07
     */
    public GLTextureInternalFormat getInternalFormat() {
        if (this.internalFormat == null) {
            throw new IllegalStateException("GLTexture has not been allocated yet!");
        }

        return this.internalFormat;
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
     * Retrieves the width of the texture.
     *
     * @return the width.
     * @throws GLException if the texture has not been allocated yet.
     * @since 15.07.08
     */
    public int getWidth() throws GLException {
        if (this.width == 0) {
            throw new GLException.InvalidStateException("GLTexture has not yet been initialized!");
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
            throw new GLException.InvalidStateException("GLTexture has not yet been initialized!");
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
            throw new GLException.InvalidStateException("GLTexture has not yet been initialized!");
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
                throw new GLException.InvalidStateException("Invalid active texture!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture is not valid! You must allocate a texture prior to binding it.");
            }

            GLTools.getDriverInstance().textureBind(texture, activeTexture);
            GLTexture.this.updateTimeUsed();
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
            if (GLTexture.this.isValid()) {
                GLTools.getDriverInstance().textureDelete(texture);
                GLTexture.this.lastUsedTime = 0L;
                GLTexture.this.texture = null;
                GLTexture.this.width = GLTexture.this.height = GLTexture.this.depth = 0;
                GLTexture.this.name = "";
            } else {
                LOGGER.warn(GLOOP_MARKER, "Attempted to delete invalid GLTexture!");
            }
        }
    }

    private void setSize(final int width, final int height, final int depth) {
        if (width < 0) {
            throw new GLException.InvalidValueException("Invalid texture width: " + width);
        } else if (height < 0) {
            throw new GLException.InvalidValueException("Invalid texture height: " + height);
        } else if (depth < 0) {
            throw new GLException.InvalidValueException("Invalid texture depth: " + depth);
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
                format, type, 
                data, null, null, null).glRun(this.getThread());

        return this;
    }

    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth,
            final GLTextureFormat format,
            final GLType type, final GLBuffer pbo) {

        new UpdateImage3DTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                format, type, 
                null, pbo, null, null).glRun(this.getThread());

        return this;
    }
    
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth,
            final GLTextureFormat format,
            final GLType type, final int[] data) {

        new UpdateImage3DTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                format, type, 
                null, null, data, null).glRun(this.getThread());

        return this;
    }
    
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset, final int zOffset,
            final int width, final int height, final int depth,
            final GLTextureFormat format,
            final GLType type, final float[] data) {

        new UpdateImage3DTask(
                level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                format, type, 
                null, null, null, data).glRun(this.getThread());

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
        private final GLBuffer pbo;
        private final int[] iData;
        private final float[] fData;

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
         * @param pbo the pixel buffer object
         * @param iData the pixel data (int array)
         * @param fData the pixel data (float array)
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
                final GLType type, final ByteBuffer data,
                final GLBuffer pbo, final int[] iData, final float[] fData) {

            if (level < 0) {
                throw new GLException.InvalidValueException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            if (xOffset < 0 || yOffset < 0 || zOffset < 0) {
                throw new GLException.InvalidValueException("Offsets cannot be less than 0!");
            } else {
                this.xOffset = xOffset;
                this.yOffset = yOffset;
                this.zOffset = zOffset;
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            if (pbo != null) {
                this.pbo = pbo;
                this.iData = null;
                this.fData = null;
                this.data = null;
            } else if (iData != null) {
                this.pbo = null;
                this.iData = iData;
                this.fData = null;
                this.data = null;
            } else if (fData != null) {
                this.pbo = null;
                this.iData = null;
                this.fData = fData;
                this.data = null;
            } else if (data != null) {
                this.pbo = null;
                this.iData = null;
                this.fData = null;
                this.data = data;
            } else {
                this.pbo = null;
                this.iData = null;
                this.fData = null;
                this.data = null;
            }
            
            if ((this.width = width) < 0) {
                throw new GLException.InvalidValueException("Width cannot be less than 0!");
            }

            if ((this.height = height) < 0) {
                throw new GLException.InvalidValueException("Height cannot be less than 0!");
            }

            if ((this.depth = depth) < 0) {
                throw new GLException.InvalidValueException("Depth cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {

            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture is not valid!");
            }
            
            if (this.pbo != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, zOffset,
                        width, height, depth,
                        format.value, type.value,
                        pbo.buffer, 0);
            } else if (this.iData != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, zOffset,
                        width, height, depth,
                        format.value, type.value,
                        iData);
            } else if (this.fData != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, zOffset,
                        width, height, depth,
                        format.value, type.value,
                        fData);
            } else if (this.data != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, zOffset,
                        width, height, depth,
                        format.value, type.value,
                        data);
            } else {
                LOGGER.warn("No data passed to texture upload!");
            }

            GLTexture.this.updateTimeUsed();
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
                format, type, 
                data, null, null, null).glRun(this.getThread());

        return this;
    }

    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset,
            final int width, final int height,
            final GLTextureFormat format,
            final GLType type, final GLBuffer pbo) {

        new UpdateImage2DTask(
                level,
                xOffset, yOffset,
                width, height,
                format, type, 
                null, pbo, null, null).glRun(this.getThread());

        return this;
    }
    
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset,
            final int width, final int height,
            final GLTextureFormat format,
            final GLType type, final int[] data) {

        new UpdateImage2DTask(
                level,
                xOffset, yOffset,
                width, height,
                format, type, 
                null, null, null, data).glRun(this.getThread());

        return this;
    }
    
    public GLTexture updateImage(
            final int level,
            final int xOffset, final int yOffset,
            final int width, final int height,
            final GLTextureFormat format,
            final GLType type, final float[] data) {

        new UpdateImage2DTask(
                level,
                xOffset, yOffset,
                width, height,
                format, type, 
                null, null, data, null).glRun(this.getThread());

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
        private final GLBuffer pbo;
        private final float[] fData;
        private final int[] iData;

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
         * @param pbo the pixel buffer object
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
                final GLType type, final ByteBuffer data,
                final GLBuffer pbo, final float[] fData, final int[] iData) {

            if (level < 0) {
                throw new GLException.InvalidValueException("Mipmap level cannot be less than 0!");
            } else {
                this.level = level;
            }

            if (xOffset < 0 || yOffset < 0) {
                throw new GLException.InvalidValueException("Offset values cannot be less than 0!");
            } else {
                this.xOffset = xOffset;
                this.yOffset = yOffset;
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            if (pbo != null) {
                this.pbo = pbo;
                this.data = null;
                this.iData = null;
                this.fData = null;
            } else if (iData != null) {
                this.pbo = null;
                this.data = null;
                this.iData = iData;
                this.fData = null;
            } else if (fData != null) {
                this.pbo = null;
                this.data = null;
                this.iData = null;
                this.fData = fData;
            } else if (data != null) {
                this.pbo = null;
                this.data = data;
                this.iData = null;
                this.fData = null;
            } else {
                this.pbo = null;
                this.data = null;
                this.iData = null;
                this.fData = null;
            }

            if ((this.width = width) < 0) {
                throw new GLException.InvalidValueException("Width cannot be less than 0!");
            }

            if ((this.height = height) < 0) {
                throw new GLException.InvalidValueException("Height cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture is not valid!");
            }

            if (this.pbo != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, 1,
                        width, height, 1,
                        format.value, type.value,
                        pbo.buffer, 0L);
            } else if (this.iData != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, 1,
                        width, height, 1,
                        format.value, type.value,
                        iData);
            } else if (this.fData != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, 1,
                        width, height, 1,
                        format.value, type.value,
                        fData);
            } else if (this.data != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, yOffset, 1,
                        width, height, 1,
                        format.value, type.value,
                        data);
            } else {
                LOGGER.warn("No data passed to texture upload!");
            }

            GLTexture.this.updateTimeUsed();
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
                format, type, data, null, null, null).glRun(this.getThread());

        return this;
    }

    public GLTexture updateImage(
            final int level,
            final int xOffset, final int width,
            final GLTextureFormat format,
            final GLType type, final GLBuffer pbo) {

        new UpdateImage1DTask(
                level, xOffset, width, format, type, null, pbo, null, null).glRun(this.getThread());

        return this;
    }

    public GLTexture updateImage(
            final int level,
            final int xOffset, final int width,
            final GLTextureFormat format,
            final GLType type, final int[] data) {

        new UpdateImage1DTask(
                level, xOffset, width, format, type, null, null, null, data).glRun(this.getThread());

        return this;
    }

    public GLTexture updateImage(
            final int level,
            final int xOffset, final int width,
            final GLTextureFormat format,
            final GLType type, final float[] data) {

        new UpdateImage1DTask(
                level, xOffset, width, format, type, null, null, data, null).glRun(this.getThread());

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
        private final int[] iData;
        private final float[] fData;
        private final GLBuffer pbo;

        /**
         * Constructs a new Update1DTask.
         *
         * @param level the mipmap level to update.
         * @param xOffset the x-offset to write to.
         * @param width the number of pixels along the x-axis to write.
         * @param format the pixel format the data is stored in.
         * @param type the data type the data is stored in.
         * @param data the pixel data.
         * @param pbo the pixel buffer object.
         * @param fData the pixel data (float array)
         * @param iData the pixel data (int array)
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
                final ByteBuffer data, final GLBuffer pbo,
                final float[] fData, final int[] iData) {

            if (level < 0) {
                throw new GLException.InvalidValueException("Level cannot be less than 0!");
            } else {
                this.level = level;
            }

            if (xOffset < 0) {
                throw new GLException.InvalidValueException("X-Offset cannot be less than 0!");
            } else {
                this.xOffset = xOffset;
            }

            if (width < 0) {
                throw new GLException.InvalidValueException("Width cannot be less than 0!");
            } else {
                this.width = width;
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);

            if (pbo != null) {
                this.pbo = pbo;
                this.data = null;
                this.iData = null;
                this.fData = null;
            } else if (iData != null) {
                this.pbo = null;
                this.data = null;
                this.iData = iData;
                this.fData = null;
            } else if (fData != null) {
                this.pbo = null;
                this.data = null;
                this.iData = null;
                this.fData = fData;
            } else {
                this.pbo = null;
                this.data = null;
                this.iData = null;
                this.fData = null;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture is not valid!");
            }

            if (this.pbo != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level,
                        xOffset, 1, 1,
                        width, 1, 1,
                        format.value, type.value,
                        pbo.buffer, 0L);
            } else if (this.iData != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level, 
                        xOffset, 1, 1, 
                        width, 1, 1, 
                        format.value, type.value, 
                        iData);
            } else if (this.fData != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture, level, 
                        xOffset, 1, 1, 
                        width, 1, 1, 
                        format.value, type.value, 
                        fData);
            } else if (this.data != null) {
                GLTools.getDriverInstance().textureSetData(
                        texture,
                        level,
                        xOffset, 1, 1,
                        width, 1, 1,
                        format.value, type.value, 
                        data);
            } else {
                LOGGER.warn("No data was passed for texture upload!");
            }

            GLTexture.this.updateTimeUsed();
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
     * @param type the data type.
     * @return self reference.
     * @since 15.07.08
     */
    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width, final int height, final int depth,
            final GLType type) {

        new AllocateImage3DTask(
                mipmaps,
                internalFormat,
                width, height, depth,
                type).glRun(this.getThread());

        return this;
    }

    public final GLTexture allocate(final int mipmaps, final GLTextureInternalFormat internalFormat, final int width, final int height, final int depth) {
        return allocate(mipmaps, internalFormat, width, height, depth, GLType.GL_UNSIGNED_BYTE);
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
        private final GLType dataType;

        /**
         * Constructs a new AllocateImage3DTask.
         *
         * @param level the number of mipmaps to allocate.
         * @param internalFormat the sized pixel format.
         * @param width the width of the base mipmap level.
         * @param height the height of the base mipmap level.
         * @param depth the depth of the base mipmap level.
         * @param dataType the data type
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
                final int width, final int height, final int depth,
                final GLType dataType) {

            if ((this.mipmaps = level) < 1) {
                throw new GLException.InvalidValueException("Mipmap levels cannot be less than 1!");
            }

            this.internalFormat = Objects.requireNonNull(internalFormat);
            this.dataType = Objects.requireNonNull(dataType);

            GLTexture.this.setSize(
                    this.width = width,
                    this.height = height,
                    this.depth = depth);
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture is not valid!");
            }

            GLTexture.this.internalFormat = this.internalFormat;
            GLTexture.this.texture = GLTools.getDriverInstance().textureAllocate(mipmaps, internalFormat.value, width, height, depth, dataType.value);
            GLTexture.this.setAttributes(GLTextureParameters.DEFAULT_PARAMETERS);
            GLTexture.this.name = "id=" + texture.hashCode();
            GLTexture.this.updateTimeUsed();
        }
    }

    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width, final int height) {

        return allocate(mipmaps, internalFormat, width, height, GLType.GL_UNSIGNED_BYTE);
    }

    /**
     * Allocates the texture object as a 2D texture. Each mipmap level allocated
     * is half the dimensions of the previous level.
     *
     * @param mipmaps the number of mipmaps to allocate.
     * @param internalFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param height the depth of the base mipmap level.
     * @param dataType the data type.
     * @return self reference.
     * @since 15.07.08
     */
    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width, final int height, final GLType dataType) {

        new AllocateImage2DTask(
                mipmaps,
                internalFormat,
                width, height,
                dataType).glRun(this.getThread());

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
        private final GLType dataType;
        private final int width;
        private final int height;

        /**
         * Constructs a new AllocateImage2DTask.
         *
         * @param level the number of mipmap levels to allocate.
         * @param internalFormat the sized pixel format.
         * @param width the width of the base mipmap level.
         * @param height the height of the base mipmap level.
         * @param dataType the data type
         * @throws GLException if mipmaps is less than 1.
         * @throws GLException if width is less than 1.
         * @throws GLException if height is less than 1.
         * @throws NullPointerException if internalFormat is null.
         * @since 15.07.08
         */
        public AllocateImage2DTask(
                final int level,
                final GLTextureInternalFormat internalFormat,
                final int width, final int height,
                final GLType dataType) {

            if ((this.mipmaps = level) < 1) {
                throw new GLException.InvalidStateException("Mipmap levels cannot be less than 1!");
            }

            this.dataType = Objects.requireNonNull(dataType);
            this.internalFormat = Objects.requireNonNull(internalFormat);
            GLTexture.this.setSize(this.width = width, this.height = height, 1);
        }

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture has already been allocated!");
            }

            GLTexture.this.internalFormat = this.internalFormat;
            GLTexture.this.texture = GLTools.getDriverInstance().textureAllocate(mipmaps, internalFormat.value, width, height, 1, dataType.value);
            GLTexture.this.setAttributes(GLTextureParameters.DEFAULT_PARAMETERS);
            GLTexture.this.name = "id=" + texture.hashCode();
            GLTexture.this.updateTimeUsed();
        }
    }

    /**
     * Allocates the texture object as a 1D texture. Each mipmap level will
     * allocated is half the dimensions of the previous level.
     *
     * @param mipmaps the number of mipmaps to allocate.
     * @param internalFormat the sized pixel format.
     * @param width the width of the base mipmap level.
     * @param dataType the data type
     * @return self reference.
     * @since 15.07.08
     */
    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width,
            final GLType dataType) {

        new AllocateImage1DTask(mipmaps, internalFormat, width, dataType).glRun(this.getThread());

        return this;
    }

    public final GLTexture allocate(
            final int mipmaps,
            final GLTextureInternalFormat internalFormat,
            final int width) {
        return allocate(mipmaps, internalFormat, width, GLType.GL_UNSIGNED_BYTE);
    }

    /**
     * A GLTask that allocates the texture object as a 1D texture.
     *
     * @since 15.07.08
     */
    public final class AllocateImage1DTask extends GLTask {

        private GLType dataType;

        private final int mipmaps;
        private final GLTextureInternalFormat internalFormat;
        private final int width;

        /**
         * Constructs a new AllocateImage1D task.
         *
         * @param mipmapLevels the number of mipmaps to allocate.
         * @param internalFormat the sized pixel format.
         * @param width the width of the base mipmap level.
         * @param dataType the data type
         * @throws GLException if mipmapLevels is less than 1.
         * @throws GLException if width is less than 1.
         * @throws NullPointerException if internalFormat is null.
         * @since 15.07.08
         */
        public AllocateImage1DTask(final int mipmapLevels,
                final GLTextureInternalFormat internalFormat,
                final int width,
                final GLType dataType) {

            if ((this.mipmaps = mipmapLevels) < 1) {
                throw new GLException("Mipmap levels cannot be less than 1!");
            }

            this.internalFormat = Objects.requireNonNull(internalFormat);
            this.dataType = Objects.requireNonNull(dataType);

            GLTexture.this.setSize(this.width = width, 1, 1);
        }

        @Override
        public void run() {
            if (GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("GLTexture has already been allocated!");
            }

            GLTexture.this.internalFormat = this.internalFormat;
            GLTexture.this.texture = GLTools.getDriverInstance().textureAllocate(mipmaps, internalFormat.value, width, 1, 1, dataType.value);
            GLTexture.this.setAttributes(GLTextureParameters.DEFAULT_PARAMETERS);
            GLTexture.this.name = "id=" + texture.hashCode();
            GLTexture.this.updateTimeUsed();
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
            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidStateException("Invalid GLTexture object!");
            }

            GLTools.getDriverInstance().textureGenerateMipmap(texture);
            GLTexture.this.updateTimeUsed();
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
                throw new GLException.InvalidValueException("Mipmap level cannot be less than 0!");
            } else if ((this.xOffset = xOffset) < 0) {
                throw new GLException.InvalidValueException("X-offset cannot be less than 0!");
            } else if ((this.yOffset = yOffset) < 0) {
                throw new GLException.InvalidValueException("Y-offset cannot be less than 0!");
            } else if ((this.width = width) < 1) {
                throw new GLException.InvalidValueException("Width cannot be less than 1!");
            } else if ((this.height = height) < 1) {
                throw new GLException.InvalidValueException("Height cannot be less than 1!");
            }
        }

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException.InvalidValueException("Invalid GLTexture!");
            } else if (!this.buffer.isValid()) {
                throw new GLException.InvalidValueException("Invalid GLBuffer!");
            }

            throw new UnsupportedOperationException("not implemented...");
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
            throw new UnsupportedOperationException("not yet implemented.");
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

            GLTexture.this.updateTimeUsed();
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

            this.maxUnits = GLTools.getDriverInstance().textureGetMaxBoundTextures();

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
        int maxSize;

        @Override
        public Integer call() throws Exception {
            if (this.checked) {
                return this.maxSize;
            }

            this.maxSize = GLTools.getDriverInstance().textureGetMaxSize();

            return this.maxSize;
        }

        @Override
        protected Integer handleInterruption() {
            // return the minimum specification for OpenGL 3.0
            return 1 << 12;
        }
    }

    /**
     * Retrieves the preferred GLTextureFormat for the specified
     * GLTextureInternalFormat.
     *
     * @param format the format to check.
     * @return the preferred format.
     * @since 15.07.20
     */
    public static GLTextureFormat getPreferredTextureFormat(final GLTextureInternalFormat format) {
        return new PreferredInternalFormatQuery(format).glCall();
    }

    /**
     * A GLQuery that checks the preferred texture format for the specified
     * GLTextureInternalFormat. This query values returning a compatible format
     * over the true preferred format.
     *
     * @since 15.07.20
     */
    public static final class PreferredInternalFormatQuery extends GLQuery<GLTextureFormat> {

        final static Map<GLTextureInternalFormat, GLTextureFormat> MEM_QUERIES = new HashMap<>(GLTextureInternalFormat.values().length);
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
            return MEM_QUERIES.computeIfAbsent(testFormat, fmt -> {
                //TODO: use the OpenGL call to actually check.
                switch (fmt) {
                    case GL_COMPRESSED_RGB_S3TC_DXT1:
                    case GL_RGB:
                    case GL_R3_G3_B2:
                    case GL_RGB4:
                    case GL_RGB5:
                    case GL_RGB8:
                    case GL_RGB10:
                    case GL_RGB16:
                    case GL_RGB16F:
                    case GL_RGB32F:
                    case GL_R11F_G11F_B10F:
                        return GLTextureFormat.GL_RGB;
                    default:
                        return GLTextureFormat.GL_RGBA;
                }

            });
        }

        @Override
        protected GLTextureFormat handleInterruption() {
            // todo: this should probably return a simple safe  value.
            throw new GLException("PreferredInternalFormatQuery was interrupted!");
        }
    }

    /**
     * Copies the data from a GLTexture object into a ByteBuffer.
     *
     * @param level the mipmap level to copy.
     * @param format the texture format to read as.
     * @param type the data type to store the pixels as.
     * @return the ByteBuffer.
     * @since 16.06.10
     */
    public ByteBuffer downloadImage(final int level, final GLTextureFormat format, final GLType type) {
        return new DownloadImageQuery(level, format, type).glCall(this.getThread());
    }

    /**
     * Copies the data from a GLTexture object into the supplied ByteBuffer.
     *
     * @param level the mipmap level to copy.
     * @param format the texture format to read as.
     * @param type the data type to store the pixels as.
     * @param buffer the ByteBuffer to write the data to.
     * @return the ByteBuffer containing the data. It may be a new ByteBuffer if
     * the supplied ByteBuffer could not hold all of the data.
     * @since 16.06.10
     */
    public ByteBuffer downloadImage(final int level, final GLTextureFormat format, final GLType type, final ByteBuffer buffer) {
        return new DownloadImageQuery(level, format, type, buffer).glCall(this.getThread());
    }

    public void downloadImage(final int level, final GLTextureFormat format, final GLType type, final GLBuffer pbo) {
        new DownloadImageQuery(level, format, type, pbo).glCall(this.getThread());
    }

    /**
     * A GLQuery that copies the data of a GLTexture into a ByteBuffer.
     *
     * @since 16.06.10
     */
    public final class DownloadImageQuery extends GLQuery<ByteBuffer> {

        private final int level;
        private final GLTextureFormat format;
        private final GLType type;
        private final int bufferSize;
        private final ByteBuffer buffer;
        private final GLBuffer pbo;

        public DownloadImageQuery(final int level, final GLTextureFormat format, final GLType type, final GLBuffer pbo) {
            assert (level >= 0) : "Level cannot be less than 0!";
            assert (format != null) : "Format cannot be null!";
            assert (type != null) : "Type cannot be null!";
            assert (pbo != null) : "Pixel Buffer Object cannot be null!";

            this.level = level;
            this.format = format;
            this.type = type;
            this.buffer = null;
            this.pbo = pbo;

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
        }

        /**
         * Constructs a new DownloadImageQuery. The storage ByteBuffer will be
         * allocated based on the needed size.
         *
         * @param level the mipmap level.
         * @param format the texture format.
         * @param type the data type.
         * @throws NullPointerException if format is null.
         * @throws NullPointerException if type is null.
         * @throws GLException if an unsupported GLTextureFormat is supplied.
         * @throws GLException if an unsupported GLType is supplied.
         * @since 16.06.10
         */
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
            this.pbo = null;
        }

        /**
         * Constructs a new GLDownloadImageQuery. The output ByteBuffer is
         * supplied.
         *
         * @param level the mipmap level to read.
         * @param format the format of the texture.
         * @param type the pixel data type.
         * @param buffer the buffer to copy the data to.
         * @since 16.06.10
         */
        public DownloadImageQuery(final int level, final GLTextureFormat format, final GLType type, final ByteBuffer buffer) {
            if ((this.level = level) < 0) {
                throw new GLException("Level cannot be less than 0!");
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
            this.buffer = Objects.requireNonNull(buffer);
            this.bufferSize = buffer.limit();
            this.pbo = null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ByteBuffer call() throws Exception {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            }

            if (this.pbo == null) {
                GLTools.getDriverInstance().textureGetData(texture, level, format.value, type.value, buffer);
            } else {
                GLTools.getDriverInstance().textureGetData(texture, level, format.value, type.value, pbo.buffer, 0, this.bufferSize);
            }
            GLTexture.this.updateTimeUsed();

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
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            } else {
                GLTools.getDriverInstance().textureInvalidateData(texture, level);
            }

            GLTexture.this.updateTimeUsed();
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
                GLTexture.this.updateTimeUsed();
                GLTools.getDriverInstance().textureInvalidateRange(
                        texture, level,
                        this.xOffset, this.yOffset, this.zOffset,
                        this.width, this.height, this.depth);
            }
        }
    }

    /**
     * Retrieves the pointer to the texture. This can be passed into shaders as
     * a bindless textures.
     *
     * @return the pointer.
     * @since 16.07.06
     */
    public long map() {
        return new MapTextureQuery().glCall(this.getThread());
    }

    /**
     * Unmaps the texture.
     *
     * @since 16.07.06
     */
    public void unmap() {
        new UnmapTextureTask().glRun(this.getThread());
    }

    /**
     * A GLTask that unmaps the texture.
     *
     * @since 16.07.06
     */
    public class UnmapTextureTask extends GLTask {

        @Override
        public void run() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLTexture!");
            }

            GLTexture.this.updateTimeUsed();

            GLTools.getDriverInstance().textureUnmap(GLTexture.this.texture);
        }
    }

    /**
     * A GLQuery that maps a pointer to the texture's data.
     *
     * @since 16.07.06
     */
    public class MapTextureQuery extends GLQuery<Long> {

        @Override
        public Long call() {
            if (!GLTexture.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTexture.this.updateTimeUsed();

            return GLTools.getDriverInstance().textureMap(GLTexture.this.texture);
        }
    }

    @Override
    public long getTimeSinceLastUsed() {
        return (System.nanoTime() - this.lastUsedTime);
    }
}
