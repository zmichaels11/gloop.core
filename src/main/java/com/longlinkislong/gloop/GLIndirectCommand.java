/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 * A wrapper for the indirect struct.
 *
 * @author zmichaels
 * @since 16.07.05
 */
public final class GLIndirectCommand {

    /**
     * The size in bytes of the struct.
     *
     * @since 16.07.05
     */
    public static final int WIDTH = Integer.BYTES * 4;

    /**
     * The number of vertices for the indirect draw.
     *
     * @since 16.07.05
     */
    public final int count;
    /**
     * The number of primitives for the indirect draw.
     *
     * @since 16.07.05
     */
    public final int primCount;
    /**
     * The index of the first vertex.
     *
     * @since 16.07.05
     */
    public final int first;

    /**
     * The offset for the base instance. Must be 0 on older hardware.
     *
     * @since 16.07.05
     */
    public final int baseInstance;

    /**
     * Constructs a new GLIndirectCommand object.
     *
     * @param count the number of vertices.
     * @param primCount the number of primitives.
     * @param first the index for the first vertex.
     * @param baseInstance the offset for the base instance.
     * @since 16.07.05
     */
    public GLIndirectCommand(
            final int count, final int primCount,
            final int first,
            final int baseInstance) {

        this.count = count;
        this.primCount = primCount;
        this.first = first;
        this.baseInstance = baseInstance;
    }

    /**
     * Constructs a new GLIndirectCommand object with all values set to 0.
     *
     * @since 16.07.05
     */
    public GLIndirectCommand() {
        this(0, 0, 0, 0);
    }

    /**
     * Builds a new GLIndirectCommand based on the current GLIndirectCommand
     * with the specified vertex count.
     *
     * @param count the new vertex count.
     * @return the new GLIndirectCommand.
     * @since 16.07.05
     */
    public GLIndirectCommand withCount(final int count) {
        return new GLIndirectCommand(count, primCount, first, baseInstance);
    }

    /**
     * Builds a new GLIndirectCommand based on the current GLIndirectCommand
     * with the specified primitive count.
     *
     * @param primCount the new primitive count.
     * @return the new GLIndirectCommand.
     * @since 16.07.05
     */
    public GLIndirectCommand withPrimCount(final int primCount) {
        return new GLIndirectCommand(count, primCount, first, baseInstance);
    }

    /**
     * Builds a new GLIndirectCommand based on the current GLIndirectCommand
     * with the specified first index.
     *
     * @param first the new first vertex.
     * @return the new GLIndirectCommand.
     * @since 16.07.05
     */
    public GLIndirectCommand withFirst(final int first) {
        return new GLIndirectCommand(count, primCount, first, baseInstance);
    }

    /**
     * Builds a new GLIndirectCommand based on the current GLIndirectCommand
     * with the specified base instance.
     *
     * @param baseInstance the new base instance.
     * @return the new GLIndirectCommand.
     * @since 16.07.05
     */
    public GLIndirectCommand withBaseInstance(final int baseInstance) {
        return new GLIndirectCommand(count, primCount, first, baseInstance);
    }
}
