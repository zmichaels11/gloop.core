/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Lazy is an object wrapper utility for implementing lazy initialization.
 *
 * @author zmichaels
 * @param <T> the type of the object.
 * @since 15.07.30
 */
class Lazy<T> implements Supplier<T> {

    private T instance;
    private final Supplier<T> constructor;
    private boolean isInitialized = false;

    /**
     * Constructs a new Lazy initialization object.
     *
     * @param constructor the constructor for the object.
     * @since 15.07.29
     */
    public Lazy(final Supplier<T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }

    @Override
    public T get() {
        return this.isInitialized
                ? this.instance
                : this.restore();
    }
            
    public T restore() {
        this.instance = this.constructor.get();
        this.isInitialized = true;
        return this.instance;
    }
    
    public void ifPresent(final Consumer<T> callback) {
        if(this.isInitialized) {
            callback.accept(this.instance);
        }
    }
    
    /**
     * Checks of the object has been initialized.
     * @return true if it has been initialized.
     * @since 15.09.01
     */
    public boolean isInitialized() {
        return this.isInitialized;
    }
}
