/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLBuffer;
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
public class NeHe03 {

    private final GLWindow window;
    private final GLTask drawTask;
    private final GLProgram program;
    private final GLVertexArray vaoTriangle;
    private final GLVertexArray vaoSquare;

    public NeHe03() throws IOException {
        this.window = new GLWindow();

        final InputStream inVsh = this.getClass()
                .getResourceAsStream("color.vs");
        final InputStream inFsh = this.getClass()
                .getResourceAsStream("color.fs");

        final GLShader vSh = new GLShader(
                GLShaderType.GL_VERTEX_SHADER,
                GLTools.readAll(inVsh));

        final GLShader fSh = new GLShader(
                GLShaderType.GL_FRAGMENT_SHADER,
                GLTools.readAll(inFsh));

        final GLVertexAttributes vAttribs = new GLVertexAttributes();
        //note: this are out of order on purpose.
        vAttribs.setAttribute("vPos", 1);
        vAttribs.setAttribute("vCol", 0);

        this.program = new GLProgram();

        this.program.setVertexAttributes(vAttribs);
        this.program.linkShaders(vSh, fSh);

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
            vCol.add(GLVec3F.create(0f, 1f, 0f));
            vCol.add(GLVec3F.create(1f, 0f, 0f));
            vCol.add(GLVec3F.create(0f, 0f, 1f));

            cTriangles.upload(GLTools.wrapVec3F(vCol));
            vCol.clear();
        }

        this.vaoTriangle = new GLVertexArray();

        this.vaoTriangle.attachBuffer(
                vAttribs.getLocation("vPos"),
                vTriangles,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        this.vaoTriangle.attachBuffer(
                vAttribs.getLocation("vCol"),
                cTriangles,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

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

        final GLBuffer cSquares = new GLBuffer();
        {
            final List<GLVec3> vCol = new ArrayList<>();
            vCol.add(GLVec3F.create(1f, 0f, 0f));
            vCol.add(GLVec3F.create(0f, 1f, 0f));
            vCol.add(GLVec3F.create(0f, 0f, 1f));
            vCol.add(GLVec3F.create(0.3f, 0.3f, 0.3f));

            cSquares.upload(GLTools.wrapVec3F(vCol));
            vCol.clear();
        }

        final GLBuffer vSquareIndex = new GLBuffer();

        vSquareIndex.upload(GLTools.wrapInt(0, 1, 2, 0, 2, 3));

        this.vaoSquare = new GLVertexArray();

        this.vaoSquare.attachIndexBuffer(vSquareIndex);
        this.vaoSquare.attachBuffer(
                vAttribs.getLocation("vPos"),
                vSquares,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);
        this.vaoSquare.attachBuffer(
                vAttribs.getLocation("vCol"),
                cSquares,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);

        this.drawTask = GLTask.create(() -> {
            program.use();
            program.setUniformMatrixF("proj", GLMat4F.perspective(45, (float) window.getAspectRatio(), 0.1f));

            program.setUniformMatrixF("tr", GLMat4F.translation(-1.5f, 0.0f, -6.0f));
            vaoTriangle.drawArrays(GLDrawMode.GL_TRIANGLES, 0, 3);

            program.setUniformMatrixF("tr", GLMat4F.translation(1.5f, 0.0f, -6.0f));
            vaoSquare.drawElements(GLDrawMode.GL_TRIANGLES, 6, GLIndexElementType.GL_UNSIGNED_INT, 0);

            window.update();
        });
    }

    public void start() {
        this.window.getGLThread().scheduleGLTask(drawTask);
        this.window.waitForInit().setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        NeHe03 test = new NeHe03();

        test.start();
    }
}
