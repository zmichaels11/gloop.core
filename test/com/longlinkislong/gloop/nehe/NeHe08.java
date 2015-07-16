/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLBlendFunc;
import com.longlinkislong.gloop.GLBlending;
import com.longlinkislong.gloop.GLBuffer;
import com.longlinkislong.gloop.GLClear;
import com.longlinkislong.gloop.GLClearBufferMode;
import com.longlinkislong.gloop.GLDepthFunc;
import com.longlinkislong.gloop.GLDepthTest;
import com.longlinkislong.gloop.GLDrawMode;
import com.longlinkislong.gloop.GLEnableStatus;
import com.longlinkislong.gloop.GLIndexElementType;
import com.longlinkislong.gloop.GLKeyAction;
import com.longlinkislong.gloop.GLKeyModifier;
import com.longlinkislong.gloop.GLMat4F;
import com.longlinkislong.gloop.GLProgram;
import com.longlinkislong.gloop.GLShader;
import com.longlinkislong.gloop.GLShaderType;
import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLTexture;
import com.longlinkislong.gloop.GLTextureFormat;
import com.longlinkislong.gloop.GLTextureInternalFormat;
import com.longlinkislong.gloop.GLTextureMagFilter;
import com.longlinkislong.gloop.GLTextureMinFilter;
import com.longlinkislong.gloop.GLTextureParameters;
import com.longlinkislong.gloop.GLTextureWrap;
import com.longlinkislong.gloop.GLTools;
import com.longlinkislong.gloop.GLType;
import com.longlinkislong.gloop.GLVec2;
import com.longlinkislong.gloop.GLVec2F;
import com.longlinkislong.gloop.GLVec3;
import com.longlinkislong.gloop.GLVec3F;
import com.longlinkislong.gloop.GLVertexArray;
import com.longlinkislong.gloop.GLVertexAttributeSize;
import com.longlinkislong.gloop.GLVertexAttributeType;
import com.longlinkislong.gloop.GLVertexAttributes;
import com.longlinkislong.gloop.GLWindow;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public class NeHe08 {

    final GLWindow window = new GLWindow(640, 480, "NeHe08");
    final GLClear clear = new GLClear()
            .withClearBits(GLClearBufferMode.GL_COLOR_BUFFER_BIT, GLClearBufferMode.GL_DEPTH_BUFFER_BIT)
            .withClearColor(0f, 0f, 0f, 0f)
            .withClearDepth(1.0);
    final GLDepthTest depthTest = new GLDepthTest()
            .withEnabled(GLEnableStatus.GL_ENABLED)
            .withDepthFunc(GLDepthFunc.GL_LESS);
    final GLBlending blend = new GLBlending()
            .withEnabled(GLEnableStatus.GL_ENABLED)
            .withBlendFunc(GLBlendFunc.GL_SRC_ALPHA, GLBlendFunc.GL_ONE_MINUS_SRC_ALPHA, GLBlendFunc.GL_SRC_ALPHA, GLBlendFunc.GL_ONE_MINUS_SRC_ALPHA);    

    final GLVertexAttributes vAttribs = new GLVertexAttributes();

    float speedX;
    float speedY;
    float rotx;
    float roty;
    float z = -5f;

    public NeHe08() {
        depthTest.applyDepthFunc();
        blend.applyBlending();
        clear.clear();        

        final GLBuffer vBuf = new GLBuffer();
        {
            final List<GLVec3> verts = new ArrayList<>();

            verts.add(GLVec3F.create(-1f, -1f, 1f));
            verts.add(GLVec3F.create(1f, -1f, 1f));
            verts.add(GLVec3F.create(1f, 1f, 1f));
            verts.add(GLVec3F.create(-1f, 1f, 1f));

            verts.add(GLVec3F.create(-1f, -1f, -1f));
            verts.add(GLVec3F.create(-1f, 1f, -1f));
            verts.add(GLVec3F.create(1f, 1f, -1f));
            verts.add(GLVec3F.create(1f, -1f, -1f));

            verts.add(GLVec3F.create(1f, -1f, -1f));
            verts.add(GLVec3F.create(1f, 1f, -1f));
            verts.add(GLVec3F.create(1f, 1f, 1f));
            verts.add(GLVec3F.create(1f, -1f, 1f));

            verts.add(GLVec3F.create(-1f, -1f, 1f));
            verts.add(GLVec3F.create(-1f, 1f, 1f));
            verts.add(GLVec3F.create(-1f, 1f, -1f));
            verts.add(GLVec3F.create(-1f, -1f, -1f));

            verts.add(GLVec3F.create(1f, 1f, 1f));
            verts.add(GLVec3F.create(1f, 1f, -1f));
            verts.add(GLVec3F.create(-1f, 1f, -1f));
            verts.add(GLVec3F.create(-1f, 1f, 1f));

            verts.add(GLVec3F.create(1f, -1f, -1f));
            verts.add(GLVec3F.create(1f, -1f, 1f));
            verts.add(GLVec3F.create(-1f, -1f, 1f));
            verts.add(GLVec3F.create(-1f, -1f, -1f));

            vBuf.upload(GLTools.wrapVec3F(verts));
            verts.clear();
        }

        final GLBuffer tBuf = new GLBuffer();
        {
            final List<GLVec2> verts = new ArrayList<>();

            verts.add(GLVec2F.create(0f, 0f));
            verts.add(GLVec2F.create(1f, 0f));
            verts.add(GLVec2F.create(1f, 1f));
            verts.add(GLVec2F.create(0f, 1f));

            verts.add(GLVec2F.create(1f, 0f));
            verts.add(GLVec2F.create(1f, 1f));
            verts.add(GLVec2F.create(0f, 1f));
            verts.add(GLVec2F.create(0f, 0f));

            verts.add(GLVec2F.create(1f, 0f));
            verts.add(GLVec2F.create(1f, 1f));
            verts.add(GLVec2F.create(0f, 1f));
            verts.add(GLVec2F.create(0f, 0f));

            verts.add(GLVec2F.create(1f, 0f));
            verts.add(GLVec2F.create(1f, 1f));
            verts.add(GLVec2F.create(0f, 1f));
            verts.add(GLVec2F.create(0f, 0f));

            verts.add(GLVec2F.create(1f, 0f));
            verts.add(GLVec2F.create(1f, 1f));
            verts.add(GLVec2F.create(0f, 1f));
            verts.add(GLVec2F.create(0f, 0f));

            verts.add(GLVec2F.create(1f, 0f));
            verts.add(GLVec2F.create(1f, 1f));
            verts.add(GLVec2F.create(0f, 1f));
            verts.add(GLVec2F.create(0f, 0f));

            tBuf.upload(GLTools.wrapVec2F(verts));
            verts.clear();
        }

        final GLBuffer iBuf = new GLBuffer();
        iBuf.upload(GLTools.wrapInt(
                0, 1, 2, 0, 2, 3,
                4, 5, 6, 4, 6, 7,
                8, 9, 10, 8, 10, 11,
                12, 13, 14, 12, 14, 15,
                16, 17, 18, 16, 18, 19,
                20, 21, 22, 20, 22, 23));

        vAttribs.setAttribute("vPos", 0);
        vAttribs.setAttribute("vTex", 1);

        final GLVertexArray vaoCube = new GLVertexArray();

        vaoCube.attachBuffer(0, vBuf, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC3);
        vaoCube.attachBuffer(1, tBuf, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC2);
        vaoCube.attachIndexBuffer(iBuf);

        final int cubeVerts = 36;

        final GLTexture texture = new GLTexture();
        {
            try (final InputStream inImg = this.getClass().getResourceAsStream("data/lesson08/glass.bmp")) {
                final BufferedImage bImg = ImageIO.read(inImg);
                final int[] pixels = new int[bImg.getWidth() * bImg.getHeight()];
                final ByteBuffer pBuf = ByteBuffer.allocateDirect(pixels.length * Integer.BYTES).order(ByteOrder.nativeOrder());

                bImg.getRGB(0, 0, bImg.getWidth(), bImg.getHeight(), pixels, 0, bImg.getWidth());

                Arrays.stream(pixels).forEach(pBuf::putInt);
                pBuf.flip();

                texture.allocate(GLTools.recommendedMipmaps(bImg.getWidth(), bImg.getHeight()), GLTextureInternalFormat.GL_RGBA8, bImg.getWidth(), bImg.getHeight())
                        .updateImage(0, 0, 0, bImg.getWidth(), bImg.getHeight(), GLTextureFormat.GL_BGRA, GLType.GL_UNSIGNED_BYTE, pBuf)
                        .generateMipmap()
                        .setAttributes(new GLTextureParameters()
                                .withAnisotropic(GLTextureParameters.getTextureMaxAnisotropyLevel())
                                .withFilter(GLTextureMinFilter.GL_LINEAR_MIPMAP_LINEAR, GLTextureMagFilter.GL_LINEAR)
                                .withWrap(GLTextureWrap.GL_CLAMP_TO_EDGE, GLTextureWrap.GL_CLAMP_TO_EDGE, GLTextureWrap.GL_CLAMP_TO_EDGE));

            } catch (IOException ioex) {
                throw new RuntimeException("Unable to load texture!", ioex);
            }
        }

        final GLProgram program = new GLProgram();

        program.setVertexAttributes(vAttribs);

        try (
                final InputStream vIn = this.getClass().getResourceAsStream("texture.vs");
                final InputStream fIn = this.getClass().getResourceAsStream("texture.fs")) {

            final GLShader vSh = new GLShader(GLShaderType.GL_VERTEX_SHADER, GLTools.readAll(vIn));
            final GLShader fSh = new GLShader(GLShaderType.GL_FRAGMENT_SHADER, GLTools.readAll(fIn));

            program.linkShaders(vSh, fSh);

            vSh.delete();
            fSh.delete();
        } catch (final IOException ioex) {
            throw new RuntimeException("Unable to initialize shaders!", ioex);
        }

        program.setUniformMatrixF("proj", GLMat4F.perspective(45f, (float) window.getAspectRatio(), 0.1f));
        program.setUniformI("texture", 0);
        texture.bind(0);
        program.use();

        final GLTask drawTask = GLTask.create(() -> {
            clear.clear();
            final GLMat4F tr = GLMat4F.translation(0f, 0f, z);
            final GLMat4F rotX = GLMat4F.rotateX(rotx);
            final GLMat4F rotY = GLMat4F.rotateY(roty);
            final GLMat4F cat = rotY.multiply(rotX.multiply(tr));

            program.setUniformMatrixF("tr", cat);

            vaoCube.drawElements(GLDrawMode.GL_TRIANGLES, cubeVerts, GLIndexElementType.GL_UNSIGNED_INT, 0);
            
            rotx += speedX;
            roty += speedY;
        });

        this.window.getGLThread().scheduleGLTask(drawTask);
        this.window.getGLThread().scheduleGLTask(this.window.new UpdateTask());
    }

    boolean blending = true;

    void input(GLWindow window, int key, int scanCode, GLKeyAction action, Set<GLKeyModifier> mods) {
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE:
                window.stop();
                break;
            case GLFW.GLFW_KEY_B:
                blending = !blending;
                if (!blending) {
                    new GLBlending().withEnabled(GLEnableStatus.GL_DISABLED).applyBlending();
                    new GLDepthTest().withEnabled(GLEnableStatus.GL_DISABLED).applyDepthFunc();
                } else {
                    blend.applyBlending();
                    depthTest.applyDepthFunc();
                }
                break;
            case GLFW.GLFW_KEY_PAGE_UP:
                z -= 0.2f;
                break;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                z += 0.2f;
                break;
            case GLFW.GLFW_KEY_UP:
                speedX -= 0.01f;
                break;
            case GLFW.GLFW_KEY_DOWN:
                speedX += 0.01f;
                break;
            case GLFW.GLFW_KEY_LEFT:
                speedY -= 0.01f;
                break;
            case GLFW.GLFW_KEY_RIGHT:
                speedY += 0.01f;
                break;
        }
    }

    void start() {
        this.window.setVisible(true);
        this.window.getKeyboard().addKeyListener(this::input);
    }

    public static void main(String[] args) {
        NeHe08 app = new NeHe08();

        app.start();
    }
}
