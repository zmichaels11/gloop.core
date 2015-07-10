/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLBuffer;
import com.longlinkislong.gloop.GLClear;
import static com.longlinkislong.gloop.GLDepthFunc.GL_LESS;
import com.longlinkislong.gloop.GLDepthTest;
import static com.longlinkislong.gloop.GLDrawMode.GL_TRIANGLES;
import static com.longlinkislong.gloop.GLEnableStatus.GL_ENABLED;
import com.longlinkislong.gloop.GLErrorType;
import com.longlinkislong.gloop.GLException;
import com.longlinkislong.gloop.GLIndexElementType;
import com.longlinkislong.gloop.GLKeyAction;
import com.longlinkislong.gloop.GLKeyModifier;
import com.longlinkislong.gloop.GLMat4F;
import com.longlinkislong.gloop.GLProgram;
import com.longlinkislong.gloop.GLShader;
import static com.longlinkislong.gloop.GLShaderType.GL_FRAGMENT_SHADER;
import static com.longlinkislong.gloop.GLShaderType.GL_VERTEX_SHADER;
import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLTexture;
import static com.longlinkislong.gloop.GLTextureFormat.GL_BGRA;
import static com.longlinkislong.gloop.GLTextureInternalFormat.GL_RGBA8;
import com.longlinkislong.gloop.GLTextureMagFilter;
import com.longlinkislong.gloop.GLTextureMinFilter;
import com.longlinkislong.gloop.GLTextureParameters;
import static com.longlinkislong.gloop.GLTextureWrap.GL_CLAMP_TO_EDGE;
import com.longlinkislong.gloop.GLTools;
import static com.longlinkislong.gloop.GLTools.readAll;
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
import java.util.Optional;
import java.util.Set;
import javax.imageio.ImageIO;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public class NeHe07 {

    final GLWindow window;
    final GLTask drawTask;
    float xRot, yRot, zRot;
    final GLProgram program;
    int filter = 0;
    float z = -5f;
    float xSpeed, ySpeed;

    public NeHe07() throws IOException {
        this.window = new GLWindow(640, 480, "NeHe07");

        final GLClear clear = this.window.getGLThread().currentClear();
        final GLDepthTest depthTest = new GLDepthTest()
                .withEnabled(GL_ENABLED)
                .withDepthFunc(GL_LESS);

        depthTest.applyDepthFunc();

        final GLVertexAttributes vAttribs = new GLVertexAttributes();
        vAttribs.setAttribute("vPos", 0);
        vAttribs.setAttribute("vTex", 1);

        program = new GLProgram();

        program.setVertexAttributes(vAttribs);
        {
            final InputStream inVsh = this.getClass().getResourceAsStream("texture.vs");
            final InputStream inFsh = this.getClass().getResourceAsStream("texture.fs");
            final GLShader vSh = new GLShader(GL_VERTEX_SHADER, readAll(inVsh));
            final GLShader fSh = new GLShader(GL_FRAGMENT_SHADER, readAll(inFsh));

            program.linkShaders(vSh, fSh);
            vSh.delete();
            fSh.delete();
        }

        final GLBuffer vCube = new GLBuffer();
        {
            final List<GLVec3> vPos = new ArrayList<>();
            vPos.add(GLVec3F.create(-1f, -1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));
            vPos.add(GLVec3F.create(1f, 1f, 1f));
            vPos.add(GLVec3F.create(-1f, 1f, 1f));

            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, 1f, 1f));
            vPos.add(GLVec3F.create(1f, 1f, -1f));
            vPos.add(GLVec3F.create(1f, -1f, -1f));

            vPos.add(GLVec3F.create(-1f, 1f, -1f));
            vPos.add(GLVec3F.create(-1f, 1f, 1f));
            vPos.add(GLVec3F.create(1f, 1f, 1f));
            vPos.add(GLVec3F.create(1f, 1f, -1f));

            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(1f, -1f, -1f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));

            vPos.add(GLVec3F.create(1f, -1f, -1f));
            vPos.add(GLVec3F.create(1f, 1f, -1f));
            vPos.add(GLVec3F.create(1f, 1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));

            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));
            vPos.add(GLVec3F.create(-1f, 1f, 1f));
            vPos.add(GLVec3F.create(-1f, 1f, -1f));

            vCube.upload(GLTools.wrapVec3F(vPos));
        }

        final GLBuffer tCube = new GLBuffer();
        {
            final List<GLVec2> vTex = new ArrayList<>();

            vTex.add(GLVec2F.create(1f, 0f));
            vTex.add(GLVec2F.create(0f, 0f));
            vTex.add(GLVec2F.create(0f, 1f));
            vTex.add(GLVec2F.create(1f, 1f));

            vTex.add(GLVec2F.create(0f, 0f));
            vTex.add(GLVec2F.create(0f, 1f));
            vTex.add(GLVec2F.create(1f, 1f));
            vTex.add(GLVec2F.create(1f, 0f));

            vTex.add(GLVec2F.create(1f, 1f));
            vTex.add(GLVec2F.create(1f, 0f));
            vTex.add(GLVec2F.create(0f, 0f));
            vTex.add(GLVec2F.create(0f, 1f));

            vTex.add(GLVec2F.create(0f, 1f));
            vTex.add(GLVec2F.create(1f, 1f));
            vTex.add(GLVec2F.create(1f, 0f));
            vTex.add(GLVec2F.create(0f, 0f));

            vTex.add(GLVec2F.create(0f, 0f));
            vTex.add(GLVec2F.create(0f, 1f));
            vTex.add(GLVec2F.create(1f, 1f));
            vTex.add(GLVec2F.create(1f, 0f));

            vTex.add(GLVec2F.create(1f, 0f));
            vTex.add(GLVec2F.create(0f, 0f));
            vTex.add(GLVec2F.create(0f, 1f));
            vTex.add(GLVec2F.create(1f, 1f));

            tCube.upload(GLTools.wrapVec2F(vTex));
        }

        final GLBuffer iCube = new GLBuffer();
        iCube.upload(GLTools.wrapInt(
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                8, 9, 10, 10, 11, 8,
                12, 13, 14, 14, 15, 12,
                16, 17, 18, 18, 19, 16,
                20, 21, 22, 22, 23, 20));
        final int cubeVerts = 36;

        final GLVertexArray vaoCube = new GLVertexArray();
        vaoCube.attachIndexBuffer(iCube);
        vaoCube.attachBuffer(
                vAttribs.getLocation("vPos"),
                vCube,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        vaoCube.attachBuffer(
                vAttribs.getLocation("vTex"),
                tCube,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC2);

        program.setUniformMatrixF("proj", GLMat4F.perspective(45, (float) window.getAspectRatio(), 0.1f));

        final GLProgram.SetUniformMatrixFTask setTr
                = program.new SetUniformMatrixFTask("tr", GLMat4F.create());

        program.setUniformI("texture", 0);

        final GLTexture texture[] = new GLTexture[5];
        try (final InputStream inImg = this.getClass().getResourceAsStream("data/lesson07/crate.bmp")) {
            final BufferedImage bImg = ImageIO.read(inImg);
            final ByteBuffer pBuf = ByteBuffer.allocateDirect(
                    bImg.getWidth() * bImg.getHeight() * 4)
                    .order(ByteOrder.nativeOrder());
            final int[] pixels = new int[bImg.getWidth() * bImg.getHeight()];

            bImg.getRGB(0, 0, bImg.getWidth(), bImg.getHeight(), pixels, 0, bImg.getWidth());
            Arrays.stream(pixels).forEach(pBuf::putInt);
            pBuf.flip();

            final GLTextureParameters baseParams = new GLTextureParameters()
                    .withWrap(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);

            final int mipmaps = GLTools.recommendedMipmaps(bImg.getWidth(), bImg.getHeight());

            System.out.println("default mipmaps: " + mipmaps);

            texture[0] = new GLTexture(1, GL_RGBA8, bImg.getWidth(), bImg.getHeight());
            {
                texture[0]
                        .setAttributes(baseParams
                                .withFilter(GLTextureMinFilter.GL_NEAREST, GLTextureMagFilter.GL_NEAREST))
                        .updateImage(0, 0, 0, bImg.getWidth(), bImg.getHeight(), GL_BGRA, GLType.GL_UNSIGNED_BYTE, pBuf);
            }

            texture[1] = new GLTexture(1, GL_RGBA8, bImg.getWidth(), bImg.getHeight());
            {
                texture[1]
                        .setAttributes(baseParams
                                .withFilter(GLTextureMinFilter.GL_LINEAR, GLTextureMagFilter.GL_LINEAR))
                        .updateImage(0, 0, 0, bImg.getWidth(), bImg.getHeight(), GL_BGRA, GLType.GL_UNSIGNED_BYTE, pBuf);
            }

            texture[2] = new GLTexture(mipmaps, GL_RGBA8, bImg.getWidth(), bImg.getHeight());
            {
                texture[2]
                        .setAttributes(baseParams
                                .withFilter(GLTextureMinFilter.GL_LINEAR_MIPMAP_NEAREST, GLTextureMagFilter.GL_LINEAR))
                        .updateImage(0, 0, 0, bImg.getWidth(), bImg.getHeight(), GL_BGRA, GLType.GL_UNSIGNED_BYTE, pBuf)
                        .generateMipmap();
            }

            texture[3] = new GLTexture(mipmaps, GL_RGBA8, bImg.getWidth(), bImg.getHeight());
            {
                texture[3]
                        .setAttributes(baseParams
                                .withFilter(GLTextureMinFilter.GL_LINEAR_MIPMAP_LINEAR, GLTextureMagFilter.GL_LINEAR))
                        .updateImage(0, 0, 0, bImg.getWidth(), bImg.getHeight(), GL_BGRA, GLType.GL_UNSIGNED_BYTE, pBuf)
                        .generateMipmap();
            }
            texture[4] = new GLTexture(mipmaps, GL_RGBA8, bImg.getWidth(), bImg.getHeight());
            {
                texture[4]
                        .setAttributes(baseParams.withFilter(GLTextureMinFilter.GL_LINEAR_MIPMAP_LINEAR, GLTextureMagFilter.GL_LINEAR).withAnisotropic(GLTextureParameters.getTextureMaxAnisotropyLevel()))
                        .updateImage(0, 0, 0, bImg.getWidth(), bImg.getHeight(), GL_BGRA, GLType.GL_UNSIGNED_BYTE, pBuf)
                        .generateMipmap();
            }
        }

        texture[0].bind(0);
        texture[1].bind(1);
        texture[2].bind(2);
        texture[3].bind(3);
        texture[4].bind(4);

        this.drawTask = GLTask.create(() -> {
            final Optional<GLException> optEx = GLErrorType.getGLError().map(GLErrorType::toGLException);

            if (optEx.isPresent()) {
                throw optEx.get();
            }

            clear.clear();

            final GLMat4F trCube;
            {
                final GLMat4F rx = GLMat4F.rotateX(xRot);
                final GLMat4F ry = GLMat4F.rotateY(yRot);
                final GLMat4F rz = GLMat4F.rotateZ(zRot);
                final GLMat4F tr = GLMat4F.translation(0f, 0f, this.z);

                trCube = rz.multiply(ry.multiply(rx.multiply(tr)));
            }

            program.use();

            setTr.set(trCube).glRun();
            vaoCube.drawElements(GL_TRIANGLES, cubeVerts, GLIndexElementType.GL_UNSIGNED_INT, 0);

            xRot += xSpeed;
            yRot += ySpeed;
            this.window.update();
        });
    }

    public void start() {
        this.window.getGLThread().scheduleGLTask(this.drawTask);
        this.window.getKeyboard().addKeyListener(this::input);
        this.window.setVisible(true);
    }

    private void input(GLWindow window,
            int key, int scancode,
            GLKeyAction action,
            Set<GLKeyModifier> mods) {

        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE:
                window.stop();
                break;
            case GLFW.GLFW_KEY_F:
                if (action == GLKeyAction.KEY_RELEASE) {
                    this.filter = (++filter) % 5;
                    window.getGLThread().submitGLTask(GLTask.create(() -> {
                        switch (filter) {
                            case 0:
                                System.out.println("Set filter to nearest/nearest");
                                break;
                            case 1:
                                System.out.println("Set filter to linear/linear");
                                break;
                            case 2:
                                System.out.println("Set filter to mipmap nearest/linear");
                                break;
                            case 3:
                                System.out.println("Set filter to mipmap linear/linear");
                                break;
                            case 4:
                                System.out.println("Enabled anisotropic filter");
                                break;
                        }
                        program.setUniformI("texture", filter);
                    }));
                }
                break;
            case GLFW.GLFW_KEY_PAGE_UP:
                this.z -= 0.02f;
                break;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                this.z += 0.02f;
                break;
            case GLFW.GLFW_KEY_UP:
                this.xSpeed -= 0.01f;
                break;
            case GLFW.GLFW_KEY_DOWN:
                this.xSpeed += 0.01f;
                break;
            case GLFW.GLFW_KEY_RIGHT:
                this.ySpeed += 0.01f;
                break;
            case GLFW.GLFW_KEY_LEFT:
                this.ySpeed -= 0.01f;
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        NeHe07 test = new NeHe07();

        test.start();
    }
}
