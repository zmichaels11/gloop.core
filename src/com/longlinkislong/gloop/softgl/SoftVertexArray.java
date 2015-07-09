/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.softgl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author zmichaels
 */
public class SoftVertexArray {

    private final Map<Integer, VertexAttribs> enabledVertexAttribs = new LinkedHashMap<>();
    private int indexBuffer = 0;
    private int saveArrayBuffer = 0;
    private int saveIndexBuffer = 0;

    void apply() {
        this.saveArrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        this.saveIndexBuffer = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER);

        this.enabledVertexAttribs.forEach((attribId, attrib) -> {
            attrib.apply();
        });
        if (this.indexBuffer > 0) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer);
        }
    }

    public static int glGenVertexArrays() {
        final int newId = SoftVertexArrayManager.getInstance().nextId();
        final SoftVertexArray vao = new SoftVertexArray();

        SoftVertexArrayManager.getInstance().vertexArrays.put(newId, vao);

        return newId;
    }

    public static void glBindVertexArray(int id) {
        if (id == 0) {
            final int indexBuffer = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            final SoftVertexArray current = SoftVertexArrayManager.getInstance().current;

            current.indexBuffer = indexBuffer;            
            SoftVertexArrayManager.getInstance().current = null;
            
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, current.saveIndexBuffer);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, current.saveArrayBuffer);
        } else {
            SoftVertexArrayManager.getInstance().current = SoftVertexArrayManager.getInstance().vertexArrays.get(id);
            SoftVertexArrayManager.getInstance().current.apply();
        }
    }

    public static void glEnableVertexAttribArray(int id) {
        SoftVertexArrayManager.getInstance().current.enabledVertexAttribs.put(id, new VertexAttribs(id));

    }

    public static void glDisableVertexAttribArray(int id) {
        SoftVertexArrayManager.getInstance().current.enabledVertexAttribs.remove(id);
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long ptr) {
        VertexAttribs attribs = SoftVertexArrayManager.getInstance().current.enabledVertexAttribs.get(index);
        attribs.size = size;
        attribs.type = type;
        attribs.normalized = normalized;
        attribs.stride = stride;
        attribs.ptr = ptr;
        attribs.buffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        attribs.bufferTarget = GL15.GL_ARRAY_BUFFER;
    }

    static class VertexAttribs {

        final int index;
        int size;
        int type;
        boolean normalized = false;
        int stride = 0;
        long ptr = 0;
        int buffer = 0;
        int bufferTarget = GL15.GL_ARRAY_BUFFER;

        VertexAttribs(int index) {
            this.index = index;
        }

        void apply() {
            GL15.glBindBuffer(this.bufferTarget, this.buffer);
            GL20.glEnableVertexAttribArray(this.index);
            GL20.glVertexAttribPointer(this.index, size, this.type, this.normalized, this.stride, this.ptr);
        }
    }
}

class SoftVertexArrayManager {

    int lastAssignedId = 0;
    final Map<Integer, SoftVertexArray> vertexArrays = new TreeMap<>();
    SoftVertexArray current = null;

    int nextId() {
        return ++lastAssignedId;
    }

    static SoftVertexArrayManager getInstance() {
        return Holder.INSTANCE;
    }

    static class Holder {

        private static final SoftVertexArrayManager INSTANCE = new SoftVertexArrayManager();
    }
}
