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
public class NeHe02 {
    private final GLWindow window;
    private final GLTask drawTask;

    public NeHe02() throws IOException {
        this.window = new GLWindow();

        final InputStream inVsh = this.getClass()
                .getResourceAsStream("basic.vs");
        final InputStream inFsh = this.getClass()
                .getResourceAsStream("basic.fs");

        final GLShader vSh = new GLShader(
                GLShaderType.GL_VERTEX_SHADER, 
                GLTools.readAll(inVsh));
        
        final GLShader fSh = new GLShader(
                GLShaderType.GL_FRAGMENT_SHADER, 
                GLTools.readAll(inFsh));                

        final GLVertexAttributes vAttribs = new GLVertexAttributes();
        vAttribs.setAttribute("vPos", 0);

        final GLProgram program = new GLProgram();

        program.setVertexAttributes(vAttribs);
        program.linkShaders(vSh, fSh);

        final List<GLVec3> vListTriangles = new ArrayList<>();
        vListTriangles.add(GLVec3F.create(0f, 1f, 0f));
        vListTriangles.add(GLVec3F.create(-1f, -1f, 0f));
        vListTriangles.add(GLVec3F.create(1f, -1f, 0f));

        final GLBuffer vTriangles = new GLBuffer();

        vTriangles.upload(GLTools.wrapVec3F(vListTriangles));
        vListTriangles.clear();

        final GLVertexArray vaoTriangle = new GLVertexArray();

        vaoTriangle.attachBuffer(
                vAttribs.getLocation("vPos"),
                vTriangles,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);                

        final List<GLVec3> vListSquares = new ArrayList<>();
        vListSquares.add(GLVec3F.create(-1f, 1f, 0f));
        vListSquares.add(GLVec3F.create(1f, 1f, 0f));
        vListSquares.add(GLVec3F.create(1f, -1f, 0f));
        vListSquares.add(GLVec3F.create(-1f, -1f, 0f));

        final GLBuffer vSquares = new GLBuffer();

        vSquares.upload(GLTools.wrapVec3F(vListSquares));        
        vListSquares.clear();

        final GLBuffer vSquareIndex = new GLBuffer();
                
        vSquareIndex.upload(GLTools.wrapInt(0, 1, 2, 0, 2, 3));

        final GLVertexArray vaoSquare = new GLVertexArray();

        vaoSquare.attachIndexBuffer(vSquareIndex);
        vaoSquare.attachBuffer(
                vAttribs.getLocation("vPos"),
                vSquares,
                GLVertexAttributeType.GL_FLOAT,
                GLVertexAttributeSize.VEC3);
                        
        
        this.drawTask = GLTask.create(()-> {
            program.setUniformMatrixF("proj", GLMat4F.perspective(45, window.getAspectRatio(), 0.1f));
            
            program.setUniformMatrixF("tr", GLMat4F.translation(-1.5f, 0.0f, -6.0f));
            vaoTriangle.drawArrays(program, GLDrawMode.GL_TRIANGLES, 0, 3);
            
            program.setUniformMatrixF("tr", GLMat4F.translation(1.5f, 0.0f, -6.0f));
            vaoSquare.drawElements(program, GLDrawMode.GL_TRIANGLES, 6, GLIndexElementType.GL_UNSIGNED_INT, 0);
            
            window.update();
        });                        
    }
    
    public void start() {
        this.window.getThread().scheduleGLTask(drawTask);
        this.window.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        NeHe02 test = new NeHe02();
        
        test.start();
    }
}
