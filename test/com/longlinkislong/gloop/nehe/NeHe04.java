/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLBuffer;
import com.longlinkislong.gloop.GLClear;
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

/**
 *
 * @author zmichaels
 */
public class NeHe04 {

    private final GLWindow window;
    private final GLTask drawTask;
    private float rotTri = 0f;
    private float rotQuad = 0f;

    public NeHe04() throws IOException {
        this.window = new GLWindow();

        final GLClear clear = this.window.getGLThread().currentClear();

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

        final GLBuffer vTriangles = new GLBuffer();
        {
            final List<GLVec3> vPos = new ArrayList<>();
            vPos.add(GLVec3F.create(0f, 1f, 0f));
            vPos.add(GLVec3F.create(-1f, -1f, 0f));
            vPos.add(GLVec3F.create(1f, -1f, 0f));

            vTriangles.upload(GLTools.wrapVec3F(vPos));
            vPos.clear();
        }

        final GLBuffer cTriangles = new GLBuffer();
        {
            final List<GLVec3> vCol = new ArrayList<>();
            vCol.add(GLVec3F.create(1f, 0f, 0f));
            vCol.add(GLVec3F.create(0f, 1f, 0f));
            vCol.add(GLVec3F.create(0f, 0f, 1f));

            cTriangles.upload(GLTools.wrapVec3F(vCol));
            vCol.clear();
        }

        final GLVertexArray vaoTriangle = new GLVertexArray();

        vaoTriangle.attachBuffer(
                vAttribs.getLocation("vPos"),
                vTriangles,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        vaoTriangle.attachBuffer(
                vAttribs.getLocation("vCol"),
                cTriangles,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        final GLBuffer cSquares = new GLBuffer();
        {
            final List<GLVec3> vCol = new ArrayList<>();
            vCol.add(GLVec3F.create(1f, 0f, 0f));
            vCol.add(GLVec3F.create(0f, 1f, 0f));
            vCol.add(GLVec3F.create(0f, 0f, 1f));
            vCol.add(GLVec3F.create(0.3f, 0.6f, 0.3f));

            cSquares.upload(GLTools.wrapVec3F(vCol));
            vCol.clear();
        }

        final GLBuffer vSquares = new GLBuffer();
        {
            final List<GLVec3> vPos = new ArrayList<>();
            vPos.add(GLVec3F.create(-1f, 1f, 0f));
            vPos.add(GLVec3F.create(1f, 1f, 0f));
            vPos.add(GLVec3F.create(1f, -1f, 0f));
            vPos.add(GLVec3F.create(-1f, -1f, 0f));

            vSquares.upload(GLTools.wrapVec3F(vPos));
            vPos.clear();
        }

        final GLBuffer vSquareIndex = new GLBuffer();
        vSquareIndex.upload(GLTools.wrapInt(0, 1, 2, 0, 2, 3));

        final GLVertexArray vaoSquare = new GLVertexArray();

        vaoSquare.attachIndexBuffer(vSquareIndex);
        vaoSquare.attachBuffer(
                vAttribs.getLocation("vPos"),
                vSquares,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);
        vaoSquare.attachBuffer(
                vAttribs.getLocation("vCol"),
                cSquares,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        this.drawTask = GLTask.create(() -> {
            clear.clear();

            final GLMat4F trTri;
            {
                final GLMat4F rotation = GLMat4F.rotateY(rotTri);
                final GLMat4F translation = GLMat4F.translation(-1.5f, 0f, -6f);

                trTri = rotation.multiply(translation);
            }

            final GLMat4F trQuad;
            {
                final GLMat4F rotation = GLMat4F.rotateX(rotQuad);
                final GLMat4F translation = GLMat4F.translation(1.5f, 0f, -6f);

                trQuad = rotation.multiply(translation);
            }

            program.use();
            program.setUniformMatrixF("proj", GLMat4F.perspective(45, (float) window.getAspectRatio(), 0.1f));
            program.setUniformMatrixF("tr", trTri);
            vaoTriangle.drawArrays(GLDrawMode.GL_TRIANGLES, 0, 3);

            program.setUniformMatrixF("tr", trQuad);
            vaoSquare.drawElements(GLDrawMode.GL_TRIANGLES, 6, GLIndexElementType.GL_UNSIGNED_INT, 0);

            rotTri += 0.2f;
            rotQuad -= 0.15f;

            this.window.update();
        });
    }

    public void start() {
        this.window.getGLThread().scheduleGLTask(this.drawTask);
        this.window.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        NeHe04 test = new NeHe04();

        test.start();
    }
}
