/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.junit.Test;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengles.GLES20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class NeHe01 {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NeHe01.class);

    @Test
    public void testWindowCreate() {
        new TestFramework("NeHe 01", this::printInfo, null).showWindow().runFor(1);
    }

    private void printInfo() {
        switch (GLWindow.CLIENT_API) {
            case OPENGL:
                printInfoGL();
                break;
            case OPENGLES:
                printInfoGLES();
                break;
            case VULKAN:
                printInfoVulkan();
                break;
            default:
                LOGGER.warn("Unknown API!");
        }
    }

    private void printInfoGL() {
        final String version = GL11.glGetString(GL11.GL_VERSION);
        final String renderer = GL11.glGetString(GL11.GL_RENDERER);
        final String shaderVersion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
        final String ext = GL11.glGetString(GL11.GL_EXTENSIONS);

        LOGGER.info("OpenGL version: {}", version);
        LOGGER.info("OpenGL shader version: {}", shaderVersion);
        LOGGER.info("OpenGL renderer: {}", renderer);
        LOGGER.info("OpenGL extensions: {}", ext);

        TestFramework.assertNoGLError();
    }

    private void printInfoGLES() {
        final String version = GLES20.glGetString(GLES20.GL_VERSION);
        final String renderer = GLES20.glGetString(GLES20.GL_RENDERER);
        final String shaderVersion = GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION);
        final String ext = GLES20.glGetString(GLES20.GL_EXTENSIONS);

        LOGGER.info("OpenGLES version: {}", version);
        LOGGER.info("OpenGLES shader version: {}", shaderVersion);
        LOGGER.info("OpenGLES renderer: {}", renderer);
        LOGGER.info("OpenGLES extensions: {}", ext);

        TestFramework.assertNoGLError();
    }

    private void printInfoVulkan() {
        //TODO?
    }
}
