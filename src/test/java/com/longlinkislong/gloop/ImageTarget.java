/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class ImageTarget extends GLObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTarget.class);
    private final GLFramebuffer renderSurface;
    private final GLRenderbuffer color;
    private final GLRenderbuffer depthStencil;
    private final int width;
    private final int height;
    private final GLViewport viewport;
    private final GLClear clear;
    private final File dest;
    private final String format;

    public ImageTarget(final String imageName, final int width, final int height) {
        this(GLThread.getAny(), imageName, width, height);
    }

    public ImageTarget(final GLThread thread, final String imageName, final int width, final int height) {
        super(thread);

        this.renderSurface = new GLFramebuffer(thread);
        this.color = new GLRenderbuffer(thread, GLTextureInternalFormat.GL_RGBA8, this.width = width, this.height = height);
        this.depthStencil = new GLRenderbuffer(thread, GLTextureInternalFormat.GL_DEPTH24_STENCIL8, width, height);

        this.renderSurface.addRenderbufferAttachment("color", color);
        this.renderSurface.addRenderbufferAttachment("depthStencil", depthStencil);
        this.viewport = new GLViewport(thread, 0, 0, this.width, this.height);
        this.clear = new GLClear(thread)
                .withClearBits(GLFramebufferMode.GL_COLOR_BUFFER_BIT, GLFramebufferMode.GL_DEPTH_BUFFER_BIT, GLFramebufferMode.GL_STENCIL_BUFFER_BIT)
                .withClearColor(0, 0, 0, 0)
                .withClearDepth(1.0);

        this.dest = new File(imageName);

        final int ext = imageName.lastIndexOf(".");
        this.format = imageName.substring(ext + 1).toUpperCase();
    }

    public void bind() {
        final GLThread currentThread = GLThread.getAny();

        currentThread.pushViewport();
        viewport.applyViewport();
        this.renderSurface.bind();
        currentThread.popViewport();
    }

    public void update() {
        final BufferedImage bImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        final int imgSize = width * height;
        final ByteBuffer readPixels = MemoryUtil.memAlloc(imgSize * Integer.BYTES);
        final int[] writePixels = new int[imgSize];

        this.renderSurface.readPixels(0, 0, width, height, GLTextureFormat.GL_RGBA, GLType.GL_UNSIGNED_BYTE, readPixels);

        readPixels.asIntBuffer().get(writePixels);

        MemoryUtil.memFree(readPixels);

        bImg.setRGB(0, 0, width, height, writePixels, 0, width);

        try {
            ImageIO.write(bImg, this.format, this.dest);
        } catch (IOException ex) {
            LOGGER.error("Unable to write to file: " + this.dest);
            LOGGER.debug(ex.getMessage(), ex);
        }
    }

    public void delete() {
        this.renderSurface.delete();
        this.color.delete();
        this.depthStencil.delete();
    }
}
