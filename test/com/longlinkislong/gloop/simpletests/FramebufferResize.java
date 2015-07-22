/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.simpletests;

import com.longlinkislong.gloop.GLBuffer;
import com.longlinkislong.gloop.GLClear;
import com.longlinkislong.gloop.GLFramebufferMode;
import com.longlinkislong.gloop.GLDrawMode;
import com.longlinkislong.gloop.GLFramebuffer;
import com.longlinkislong.gloop.GLMat4F;
import com.longlinkislong.gloop.GLProgram;
import com.longlinkislong.gloop.GLShader;
import com.longlinkislong.gloop.GLShaderType;
import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLTexture;
import com.longlinkislong.gloop.GLTextureInternalFormat;
import com.longlinkislong.gloop.GLTextureMagFilter;
import com.longlinkislong.gloop.GLTextureMinFilter;
import com.longlinkislong.gloop.GLTextureParameters;
import com.longlinkislong.gloop.GLThread;
import com.longlinkislong.gloop.GLTools;
import com.longlinkislong.gloop.GLVec2;
import com.longlinkislong.gloop.GLVec2D;
import com.longlinkislong.gloop.GLVec2F;
import com.longlinkislong.gloop.GLVec3;
import com.longlinkislong.gloop.GLVec3F;
import com.longlinkislong.gloop.GLVertexArray;
import com.longlinkislong.gloop.GLVertexAttributeSize;
import com.longlinkislong.gloop.GLVertexAttributeType;
import com.longlinkislong.gloop.GLVertexAttributes;
import com.longlinkislong.gloop.GLViewport;
import com.longlinkislong.gloop.GLWindow;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zmichaels
 */
public class FramebufferResize {

    final int width = 640;
    final int height = 480;
    final GLWindow window = new GLWindow(width, height, "Framebuffer Resize Test");
    final GLFramebuffer framebuffer;
    final GLTexture colorAttachment;

    private class ColorQuad {

        final GLProgram program;
        final GLVertexArray vao;

        ColorQuad() {
            final GLBuffer vPos = new GLBuffer();
            {
                final List<GLVec3> verts = new ArrayList<>();

                verts.add(GLVec3F.create(-0.5f, -0.5f, 0f));
                verts.add(GLVec3F.create(0.5f, -0.5f, 0f));
                verts.add(GLVec3F.create(0.5f, 0.5f, 0f));

                verts.add(GLVec3F.create(-0.5f, -0.5f, 0f));
                verts.add(GLVec3F.create(0.5f, 0.5f, 0f));
                verts.add(GLVec3F.create(-0.5f, 0.5f, 0f));

                vPos.upload(GLTools.wrapVec3F(verts));
                verts.clear();
            }

            final GLBuffer vColor = new GLBuffer();
            {
                final List<GLVec3> verts = new ArrayList<>();

                verts.add(GLVec3F.create(1f, 0f, 0f));
                verts.add(GLVec3F.create(0f, 1f, 0f));
                verts.add(GLVec3F.create(0f, 0f, 1f));
                verts.add(GLVec3F.create(1f, 0f, 0f));
                verts.add(GLVec3F.create(0f, 0f, 1f));
                verts.add(GLVec3F.create(1f, 1f, 1f));

                vColor.upload(GLTools.wrapVec3F(verts));
                verts.clear();
            }

            final GLVertexAttributes vAttribs = new GLVertexAttributes();
            vAttribs.setAttribute("vPos", 0);
            vAttribs.setAttribute("vColor", 1);

            this.program = new GLProgram();
            this.program.setVertexAttributes(vAttribs);

            try (InputStream inVsh = this.getClass().getResourceAsStream("colorquad.vs");
                    InputStream inFsh = this.getClass().getResourceAsStream("colorquad.fs")) {

                final GLShader shVsh = new GLShader(GLShaderType.GL_VERTEX_SHADER, GLTools.readAll(inVsh));
                final GLShader shFsh = new GLShader(GLShaderType.GL_FRAGMENT_SHADER, GLTools.readAll(inFsh));

                this.program.linkShaders(shVsh, shFsh);
                shVsh.delete();
                shFsh.delete();
            } catch (IOException ioex) {
                throw new RuntimeException("Cannot load shaders!", ioex);
            }

            this.vao = new GLVertexArray();
            this.vao.attachBuffer(vAttribs.getLocation("vPos"), vPos, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC3);
            this.vao.attachBuffer(vAttribs.getLocation("vColor"), vColor, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC3);
        }
    }

    private class TexQuad {

        final GLProgram program;
        final GLVertexArray vao;

        TexQuad() {
            final GLBuffer vPos = new GLBuffer();
            {
                final List<GLVec3> verts = new ArrayList<>();

                verts.add(GLVec3F.create(-0.5f, -0.5f, 0f));
                verts.add(GLVec3F.create(0.5f, -0.5f, 0f));
                verts.add(GLVec3F.create(0.5f, 0.5f, 0f));

                verts.add(GLVec3F.create(-0.5f, -0.5f, 0f));
                verts.add(GLVec3F.create(0.5f, 0.5f, 0f));
                verts.add(GLVec3F.create(-0.5f, 0.5f, 0f));

                vPos.upload(GLTools.wrapVec3F(verts));
                verts.clear();
            }

            final GLBuffer vUV = new GLBuffer();
            {
                final List<GLVec2> verts = new ArrayList<>();

                verts.add(GLVec2F.create(0f, 0f));
                verts.add(GLVec2F.create(1f, 0f));
                verts.add(GLVec2F.create(1f, 1f));
                verts.add(GLVec2F.create(0f, 0f));
                verts.add(GLVec2F.create(1f, 1f));
                verts.add(GLVec2F.create(0f, 1f));

                vUV.upload(GLTools.wrapVec2F(verts));
                verts.clear();
            }

            final GLVertexAttributes vAttribs = new GLVertexAttributes();
            vAttribs.setAttribute("vPos", 0);
            vAttribs.setAttribute("vUV", 1);

            this.program = new GLProgram();
            this.program.setVertexAttributes(vAttribs);

            try (InputStream inVsh = this.getClass().getResourceAsStream("texquad.vs");
                    InputStream inFsh = this.getClass().getResourceAsStream("texquad.fs")) {

                final GLShader shVsh = new GLShader(GLShaderType.GL_VERTEX_SHADER, GLTools.readAll(inVsh));
                final GLShader shFsh = new GLShader(GLShaderType.GL_FRAGMENT_SHADER, GLTools.readAll(inFsh));

                this.program.linkShaders(shVsh, shFsh);

                shVsh.delete();
                shFsh.delete();
            } catch (IOException ioex) {
                throw new RuntimeException("Could not load shaders!", ioex);
            }

            this.vao = new GLVertexArray();
            this.vao.attachBuffer(vAttribs.getLocation("vPos"), vPos, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC3);
            this.vao.attachBuffer(vAttribs.getLocation("vUV"), vUV, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC2);
        }
    }

    private final ColorQuad quad;
    private final TexQuad texQuad;

    public FramebufferResize() {
        this.colorAttachment = new GLTexture()
                .allocate(1, GLTextureInternalFormat.GL_RGBA8, width / 2, height / 2)
                .setAttributes(new GLTextureParameters().withFilter(GLTextureMinFilter.GL_NEAREST, GLTextureMagFilter.GL_NEAREST));

        this.framebuffer = new GLFramebuffer()
                .addColorAttachment("color", colorAttachment);

        this.quad = new ColorQuad();
        this.texQuad = new TexQuad();
    }

    void resizeFramebuffer(int width, int height) {
        width = Math.min(this.width, Math.max(1, width));
        height = Math.min(this.height, Math.max(1, height));

        this.colorAttachment.delete();
        this.colorAttachment.allocate(1, GLTextureInternalFormat.GL_RGBA8, width, height)
                .setAttributes(new GLTextureParameters().withFilter(GLTextureMinFilter.GL_NEAREST, GLTextureMagFilter.GL_NEAREST));

        this.framebuffer.delete();
        this.framebuffer.init();
        this.framebuffer.addColorAttachment("color", colorAttachment);
    }

    public void start() {
        this.window.setVisible(true);
        this.window.getGLThread().scheduleGLTask(GLTask.create(this::render));
        this.window.getGLThread().scheduleGLTask(this.window.new UpdateTask());
    }

    void render() {
        final float t = System.nanoTime() / 1000000000.0f;
        final GLVec2D mousePos = this.window.getMouse().getMousePosition();
        final GLThread thread = GLThread.getCurrent().orElseThrow(RuntimeException::new);

        this.resizeFramebuffer((int) mousePos.x(), (int) mousePos.y());

        this.framebuffer.bind("color");
        thread.pushViewport();
        new GLViewport(0, 0, this.colorAttachment.getWidth(), this.colorAttachment.getHeight()).applyViewport();

        new GLClear()
                .withClearBits(GLFramebufferMode.GL_COLOR_BUFFER_BIT, GLFramebufferMode.GL_DEPTH_BUFFER_BIT)
                .withClearColor(0.5f, 0.5f, 0.5f, 1f)
                .clear();

        quad.program.use();
        quad.program.setUniformMatrixF("tr", GLMat4F.rotateZ(t));
        quad.vao.drawArrays(GLDrawMode.GL_TRIANGLES, 0, 6);

        thread.popViewport();
        GLFramebuffer.getDefaultFramebuffer().bind();

        new GLClear()
                .withClearBits(GLFramebufferMode.GL_COLOR_BUFFER_BIT, GLFramebufferMode.GL_DEPTH_BUFFER_BIT)
                .withClearColor(0.8f, 0.8f, 0.8f, 0f)
                .clear();

        this.colorAttachment.bind(0);
        this.texQuad.program.use();
        this.texQuad.program.setUniformMatrixF("tr", GLMat4F.rotateZ(-t));
        this.texQuad.program.setUniformI("tex", 0);

        this.texQuad.vao.drawArrays(GLDrawMode.GL_TRIANGLES, 0, 6);
    }

    public static void main(String[] args) {
        FramebufferResize test = new FramebufferResize();
        test.start();
    }
}
