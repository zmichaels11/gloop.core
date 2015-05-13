/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLBuffer;
import com.longlinkislong.gloop.GLBufferTarget;
import com.longlinkislong.gloop.GLDrawMode;
import com.longlinkislong.gloop.GLIndexElementType;
import com.longlinkislong.gloop.GLMat4F;
import com.longlinkislong.gloop.GLProgram;
import com.longlinkislong.gloop.GLShader;
import com.longlinkislong.gloop.GLShaderType;
import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLTaskList;
import com.longlinkislong.gloop.GLThread;
import com.longlinkislong.gloop.GLTools;
import com.longlinkislong.gloop.GLVec3F;
import com.longlinkislong.gloop.GLVertexArray;
import com.longlinkislong.gloop.GLVertexAttributeSize;
import com.longlinkislong.gloop.GLVertexAttributeType;
import com.longlinkislong.gloop.GLVertexAttributes;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zmichaels
 */
public class NeHe02 {

    public NeHe02() throws IOException {
        final GLThread thread = GLThread.getDefaultInstance();

        thread.new OpenDisplayTask().glRun();

        final InputStream inVsh = this.getClass().getResourceAsStream("basic.vs");
        final InputStream inFsh = this.getClass().getResourceAsStream("basic.fs");

        final GLShader vSh = new GLShader(GLShaderType.GL_VERTEX_SHADER, GLTools.readAll(inVsh));
        final GLShader fSh = new GLShader(GLShaderType.GL_FRAGMENT_SHADER, GLTools.readAll(inFsh));

        final GLTaskList initTasks = new GLTaskList();

        initTasks.add(vSh.new CompileTask());
        initTasks.add(fSh.new CompileTask());

        final GLVertexAttributes vAttribs = new GLVertexAttributes();
        vAttribs.setAttribute("vPos", 0);

        final GLProgram program = new GLProgram();

        initTasks.add(program.new InitTask());
        initTasks.add(program.new SetVertexAttributesTask(vAttribs));
        initTasks.add(program.new LinkShadersTask(vSh, fSh));

        final List<GLVec3F> vListTriangles = new ArrayList<>();
        vListTriangles.add(GLVec3F.create(0f, 1f, 0f));
        vListTriangles.add(GLVec3F.create(-1f, -1f, 0f));
        vListTriangles.add(GLVec3F.create(1f, -1f, 0f));

        final GLBuffer vTriangles = new GLBuffer();

        initTasks.add(vTriangles.new InitTask());
        initTasks.add(vTriangles.new UploadTask(
                GLBufferTarget.GL_ARRAY_BUFFER,
                GLTools.wrapVec3F(vListTriangles)));

        final GLVertexArray vaoTriangle = new GLVertexArray();

        initTasks.add(vaoTriangle.new InitTask());
        initTasks.add(vaoTriangle.new AttachBufferTask(
                vAttribs.getLocation("vPos"),
                vTriangles,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3));

        final List<GLVec3F> vListSquares = new ArrayList<>();
        vListSquares.add(GLVec3F.create(-1f, 1f, 0f));
        vListSquares.add(GLVec3F.create(1f, 1f, 0f));
        vListSquares.add(GLVec3F.create(1f, -1f, 0f));
        vListSquares.add(GLVec3F.create(-1f, -1f, 0f));

        final GLBuffer vSquares = new GLBuffer();

        initTasks.add(vSquares.new InitTask());
        initTasks.add(vSquares.new UploadTask(
                GLBufferTarget.GL_ARRAY_BUFFER,
                GLTools.wrapVec3F(vListSquares)
        ));

        final GLBuffer vSquareIndex = new GLBuffer();

        initTasks.add(vSquareIndex.new InitTask());
        initTasks.add(vSquareIndex.new UploadTask(
                GLBufferTarget.GL_ELEMENT_ARRAY_BUFFER,
                GLTools.wrapInt(0, 1, 2, 0, 2, 3)));

        final GLVertexArray vaoSquare = new GLVertexArray();

        initTasks.add(vaoSquare.new InitTask());
        initTasks.add(vaoSquare.new AttachBufferTask(
                vAttribs.getLocation("vPos"),
                vSquares,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3));

        initTasks.add(vaoSquare.new AttachIndexBufferTask(vSquareIndex));        
        
        final GLTask drawTriangleTask = GLTask.join(
                program.new SetUniformMatrixFTask("proj", GLMat4F.perspective(45f, 4f / 3f, 0.1f)),
                program.new SetUniformMatrixFTask("tr", GLMat4F.translation(-1.5f, 0f, -6f)),
                vaoTriangle.new DrawArraysTask(program));

        final GLTask drawSquareTask = GLTask.join(
                program.new SetUniformMatrixFTask("tr", GLMat4F.translation(1.5f, 0f, -6f)),
                vaoSquare.new DrawElementsTask(
                        program, 
                        GLDrawMode.GL_TRIANGLES,
                        6,
                        GLIndexElementType.GL_UNSIGNED_INT, 
                        0));
                
        thread.submitGLTask(initTasks);
        thread.scheduleGLTask(drawTriangleTask);
        thread.scheduleGLTask(drawSquareTask);
        thread.scheduleGLTask(thread.new SyncedUpdateTask(60));
    }

    public static void main(String[] args) throws Exception {
        NeHe02 test = new NeHe02();
    }
}
