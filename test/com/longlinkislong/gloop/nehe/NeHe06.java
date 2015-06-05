/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLBuffer;
import com.longlinkislong.gloop.GLClear;
import com.longlinkislong.gloop.GLClearBufferMode;
import com.longlinkislong.gloop.GLDepthFunc;
import com.longlinkislong.gloop.GLDepthTest;
import com.longlinkislong.gloop.GLDrawMode;
import com.longlinkislong.gloop.GLException;
import com.longlinkislong.gloop.GLIndexElementType;
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
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class NeHe06 {

    final GLWindow window;
    final GLTask drawTask;
    float xRot, yRot, zRot;

    public NeHe06() throws IOException {
        this.window = new GLWindow(640, 480, "NeHe06");

        
        final GLClear clear = this.window.getThread().currentClear();

        this.window.getThread().pushDepthTest(new GLDepthTest(true, GLDepthFunc.GL_LESS));

        final GLVertexAttributes vAttribs = new GLVertexAttributes();
        vAttribs.setAttribute("vPos", 0);
        vAttribs.setAttribute("vTex", 1);

        final GLProgram program = new GLProgram();

        program.setVertexAttributes(vAttribs);
        {
            final InputStream inVsh = this.getClass().getResourceAsStream("texture.vs");
            final InputStream inFsh = this.getClass().getResourceAsStream("texture.fs");
            final GLShader vSh = new GLShader(GLShaderType.GL_VERTEX_SHADER, GLTools.readAll(inVsh));
            final GLShader fSh = new GLShader(GLShaderType.GL_FRAGMENT_SHADER, GLTools.readAll(inFsh));

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
        
        final GLTexture texture = new GLTexture();
        try(final InputStream inImg = this.getClass().getResourceAsStream("data/lesson06/NeHe.bmp")){
            final BufferedImage bImg = ImageIO.read(inImg);
            final ByteBuffer pBuf = ByteBuffer.allocateDirect(
                    bImg.getWidth() * bImg.getHeight() * 4)
                    .order(ByteOrder.nativeOrder());
            final int[] pixels = new int[bImg.getWidth() * bImg.getHeight()];
            
            
            final GLTextureParameters attribs = new GLTextureParameters()
                    .withFilter(GLTextureMinFilter.GL_LINEAR_MIPMAP_LINEAR, GLTextureMagFilter.GL_LINEAR)
                    .withWrap(GLTextureWrap.GL_CLAMP_TO_EDGE, GLTextureWrap.GL_CLAMP_TO_EDGE, GLTextureWrap.GL_CLAMP_TO_EDGE);                        
                        
            bImg.getRGB(0, 0, bImg.getWidth(), bImg.getHeight(), pixels, 0, bImg.getWidth());
            
            Arrays.stream(pixels).forEach(pBuf::putInt);
            
            pBuf.flip(); 
                        
            texture.setAttributes(attribs);
            texture.setImage(
                    GLTexture.GENERATE_MIPMAP, 
                    GLTextureInternalFormat.GL_RGBA8, 
                    GLTextureFormat.GL_BGRA, 
                    bImg.getWidth(), bImg.getHeight(),
                    GLType.GL_UNSIGNED_BYTE, pBuf);            
            
            
        }
                
        this.drawTask = GLTask.create(()->{
            if(GL11.glGetError() != 0) {
                throw new GLException();
            }
            clear.clear(
                    GLClearBufferMode.GL_COLOR_BUFFER_BIT,
                    GLClearBufferMode.GL_DEPTH_BUFFER_BIT);
            
            final GLMat4F trCube;
            {
                final GLMat4F rx = GLMat4F.rotateX(xRot);
                final GLMat4F ry = GLMat4F.rotateY(yRot);
                final GLMat4F rz = GLMat4F.rotateZ(zRot);
                final GLMat4F tr = GLMat4F.translation(0f, 0f, -5f);
                
                trCube = rz.multiply(ry.multiply(rx.multiply(tr)));
            }
            
            setTr.set(trCube).glRun();
            texture.bind(0);
            vaoCube.drawElements(program, GLDrawMode.GL_TRIANGLES, cubeVerts, GLIndexElementType.GL_UNSIGNED_INT, 0);
            
            xRot += 0.015f;
            yRot += 0.015f;
            zRot += 0.015f;
            
            this.window.update();
            
        });                
    }
    
    public void start() {
        this.window.getThread().scheduleGLTask(drawTask);
        this.window.waitForInit().setVisible(true);
    }
    
    public static void main(String[] args) throws Exception {
        NeHe06 test = new NeHe06();
        
        test.start();
    }
}
