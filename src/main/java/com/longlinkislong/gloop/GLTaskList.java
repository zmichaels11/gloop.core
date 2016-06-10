/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * A list of GLTasks.
 * @author zmichaels
 * @since 15.05.27
 */
public class GLTaskList extends GLTask implements List<GLTask>, RandomAccess{
    private final List<GLTask> tasks = new ArrayList<>(0);
    
    @Override
    public void run() {
        this.forEach(GLTask::run);
    }

    @Override
    public int size() {
        return this.tasks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.tasks.contains(o);
    }

    @Override
    public Iterator<GLTask> iterator() {
        return this.tasks.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.tasks.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.tasks.toArray(a);
    }

    @Override
    public boolean add(GLTask e) {
        return this.tasks.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.tasks.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.tasks.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends GLTask> c) {
        return this.tasks.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends GLTask> c) {
        return this.tasks.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.tasks.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.tasks.retainAll(c);
    }

    @Override
    public void clear() {
        this.tasks.clear();
    }

    @Override
    public GLTask get(int index) {
        return this.tasks.get(index);
    }

    @Override
    public GLTask set(int index, GLTask element) {
        return this.tasks.set(index, element);
    }

    @Override
    public void add(int index, GLTask element) {
        this.tasks.add(index, element);
    }

    @Override
    public GLTask remove(int index) {
        return this.tasks.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.tasks.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.tasks.lastIndexOf(o);
    }

    @Override
    public ListIterator<GLTask> listIterator() {
        return this.tasks.listIterator();
    }

    @Override
    public ListIterator<GLTask> listIterator(int index) {
        return this.tasks.listIterator(index);
    }

    @Override
    public List<GLTask> subList(int fromIndex, int toIndex) {
        return this.tasks.subList(fromIndex, toIndex);
    }
    
}