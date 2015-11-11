/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private final List<GLTask> tasks = new ArrayList<>();        
    
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