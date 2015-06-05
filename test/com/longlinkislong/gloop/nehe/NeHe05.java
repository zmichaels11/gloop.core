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
import com.longlinkislong.gloop.GLIndexElementType;
import com.longlinkislong.gloop.GLMat4F;
import com.longlinkislong.gloop.GLProgram;
import com.longlinkislong.gloop.GLShader;
import com.longlinkislong.gloop.GLShaderType;
import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLTools;
import com.longlinkislong.gloop.GLVec3;
import com.longlinkislong.gloop.GLVec3F;
import com.longlinkislong.gloop.GLVertexArray;
import com.longlinkislong.gloop.GLVertexAttributeSize;
import com.longlinkislong.gloop.GLVertexAttributeType;
import com.longlinkislong.gloop.GLVertexAttributes;
import com.longlinkislong.gloop.GLWindow;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author zmichaels
 */
public class NeHe05 {

    private final GLWindow window;
    private final GLTask drawTask;
    private float rTetra = 0f;
    private float rCube = 0f;

    public NeHe05() throws IOException {
        this.window = new GLWindow();

        final GLClear clear = this.window.getThread().currentClear();

        this.window.getThread().pushDepthTest(new GLDepthTest(true, GLDepthFunc.GL_LESS));
        
        final GLVertexAttributes vAttribs = new GLVertexAttributes();
        vAttribs.setAttribute("vPos", 0);
        vAttribs.setAttribute("vCol", 1);                
        
        final GLProgram program = new GLProgram();

        program.setVertexAttributes(vAttribs);
        {
            final InputStream inVsh = this.getClass().getResourceAsStream("color.vs");
            final InputStream inFsh = this.getClass().getResourceAsStream("color.fs");
            final GLShader vSh = new GLShader(GLShaderType.GL_VERTEX_SHADER, GLTools.readAll(inVsh));
            final GLShader fSh = new GLShader(GLShaderType.GL_FRAGMENT_SHADER, GLTools.readAll(inFsh));

            program.linkShaders(vSh, fSh);
            vSh.delete();
            fSh.delete();
        }

        final GLBuffer vTetra = new GLBuffer();
        final int tetraVerts;
        {
            final List<GLVec3> vPos = new ArrayList<>();
            vPos.add(GLVec3F.create(0f, 1f, 0f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));

            vPos.add(GLVec3F.create(0f, 1f, 0f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, -1f));

            vPos.add(GLVec3F.create(0f, 1f, 0f));
            vPos.add(GLVec3F.create(1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, -1f, -1f));

            vPos.add(GLVec3F.create(0f, 1f, 0f));
            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));

            vTetra.upload(GLTools.wrapVec3F(vPos));
            tetraVerts = vPos.size();
        }

        final GLBuffer cTetra = new GLBuffer();
        {
            final List<GLVec3> cPos = new ArrayList<>();
            cPos.add(GLVec3F.create(0f, 1f, 0f));
            cPos.add(GLVec3F.create(-1f, -1f, 1f));
            cPos.add(GLVec3F.create(1f, -1f, 1f));

            cPos.add(GLVec3F.create(0f, 1f, 0f));
            cPos.add(GLVec3F.create(1f, -1f, 1f));
            cPos.add(GLVec3F.create(1f, -1f, -1f));

            cPos.add(GLVec3F.create(0f, 1f, 0f));
            cPos.add(GLVec3F.create(1f, -1f, -1f));
            cPos.add(GLVec3F.create(-1f, -1f, -1f));

            cPos.add(GLVec3F.create(0f, 1f, 0f));
            cPos.add(GLVec3F.create(-1f, -1f, -1f));
            cPos.add(GLVec3F.create(-1f, -1f, 1f));

            cTetra.upload(GLTools.wrapVec3F(cPos));
        }        
        
        final GLVertexArray vaoTetra = new GLVertexArray();

        vaoTetra.attachBuffer(
                vAttribs.getLocation("vPos"),
                vTetra,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);
        
        vaoTetra.attachBuffer(
                vAttribs.getLocation("vCol"),
                cTetra,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        final GLBuffer vCube = new GLBuffer();
        final int cubeVerts;
        {
            final List<GLVec3> vPos = new ArrayList<>();
            vPos.add(GLVec3F.create(1f, 1f, -1f));
            vPos.add(GLVec3F.create(-1f, 1f, -1f));
            vPos.add(GLVec3F.create(-1f, 1f, 1f));
            vPos.add(GLVec3F.create(1f, 1f, 1f));

            vPos.add(GLVec3F.create(1f, -1f, 1f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));
            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(1f, -1f, -1f));

            vPos.add(GLVec3F.create(1f, 1f, 1f));
            vPos.add(GLVec3F.create(-1f, 1f, 1f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));

            vPos.add(GLVec3F.create(1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, 1f, -1f));
            vPos.add(GLVec3F.create(1f, 1f, -1f));

            vPos.add(GLVec3F.create(-1f, 1f, 1f));
            vPos.add(GLVec3F.create(-1f, 1f, -1f));
            vPos.add(GLVec3F.create(-1f, -1f, -1f));
            vPos.add(GLVec3F.create(-1f, -1f, 1f));

            vPos.add(GLVec3F.create(1f, 1f, -1f));
            vPos.add(GLVec3F.create(1f, 1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, 1f));
            vPos.add(GLVec3F.create(1f, -1f, -1f));

            vCube.upload(GLTools.wrapVec3F(vPos));            
        }

        final GLBuffer cCube = new GLBuffer();
        {
            final List<GLVec3> vCol = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                vCol.add(GLVec3F.create(0f, 1f, 0f));
            }

            for (int i = 0; i < 4; i++) {
                vCol.add(GLVec3F.create(1f, 0.5f, 0f));
            }

            for (int i = 0; i < 4; i++) {
                vCol.add(GLVec3F.create(1f, 0f, 0f));
            }

            for (int i = 0; i < 4; i++) {
                vCol.add(GLVec3F.create(1f, 1f, 0f));
            }

            for (int i = 0; i < 4; i++) {
                vCol.add(GLVec3F.create(0f, 0f, 1f));
            }

            for (int i = 0; i < 4; i++) {
                vCol.add(GLVec3F.create(1f, 0f, 1f));
            }

            cCube.upload(GLTools.wrapVec3F(vCol));
        }

        final GLBuffer vCubeIndex = new GLBuffer();
        vCubeIndex.upload(GLTools.wrapInt(
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                8, 9, 10, 10, 11, 8,
                12, 13, 14, 14, 15, 12,
                16, 17, 18, 18, 19, 16,
                20, 21, 22, 22, 23, 20));
        cubeVerts = 36;

        final GLVertexArray vaoCube = new GLVertexArray();
        vaoCube.attachIndexBuffer(vCubeIndex);
        vaoCube.attachBuffer(
                vAttribs.getLocation("vPos"),
                vCube,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);
        vaoCube.attachBuffer(
                vAttribs.getLocation("vCol"),
                cCube,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);
        
        program.setUniformMatrixF("proj", GLMat4F.perspective(45, (float) window.getAspectRatio(), 0.1f));
        final GLProgram.SetUniformMatrixFTask setTr 
                = program.new SetUniformMatrixFTask("tr", GLMat4F.create());
        
        
        this.drawTask = GLTask.create(() -> {
            clear.clear(
                    GLClearBufferMode.GL_COLOR_BUFFER_BIT, 
                    GLClearBufferMode.GL_DEPTH_BUFFER_BIT);
            
            final GLMat4F trTetra;
            {
                final GLMat4F rotation = GLMat4F.rotateY(rTetra);
                final GLMat4F translation = GLMat4F.translation(-1.5f, 0f, -6f);
                
                trTetra = rotation.multiply(translation);
            }
            
            final GLMat4F trCube;
            {
                final GLMat4F rotation = GLMat4F.rotateX(rCube);
                final GLMat4F translation = GLMat4F.translation(1.5f, 0f, -6f);
                
                trCube = rotation.multiply(translation);
            }
                        
            program.use();
            setTr.set(trTetra).glRun();            
            vaoTetra.drawArrays(GLDrawMode.GL_TRIANGLES, 0, tetraVerts);
            
            setTr.set(trCube).glRun();
            vaoCube.drawElements(GLDrawMode.GL_TRIANGLES, cubeVerts, GLIndexElementType.GL_UNSIGNED_INT, 0);
            
            rTetra += 0.2f;
            rCube -= 0.15f;
            
            this.window.update();  
            
            SwingUtilities.invokeLater(() -> {
                System.out.println(window.getMouse().getMousePosition());
            });
        });
    }
    
    public void start() {
        this.window.getThread().scheduleGLTask(this.drawTask);
        this.window.waitForInit().setVisible(true);
    }
    
    public static void main(String[] args) throws Exception {
        NeHe05 test = new NeHe05();
        
        test.start();
    }
}
