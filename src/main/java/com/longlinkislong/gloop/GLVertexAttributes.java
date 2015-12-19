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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLVertexAttributes represent the vertex attribute settings of an OpenGL
 * program. The should be set programmatically before linking the OpenGL
 * program.
 *
 * @author zmichaels
 * @since 15.06.24
 */
public class GLVertexAttributes {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLVertexAttributes");

    public static final int INVALID_VARYING_LOCATION = -1;
    public static final int INVALID_ATTRIBUTE_LOCATION = -1;
    final Map<String, Integer> nameMap = new HashMap<>();
    final Set<String> feedbackVaryings = new HashSet<>();

    private String name;

    /**
     * Assigns a human-readable name to the GLVertexAttributes object.
     *
     * @param newName the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        LOGGER.trace(
                GLOOP_MARKER,
                "Renamed GLVertexAttributes[{}] to GLVertexAttributes[{}]",
                this.name,
                newName);
        this.name = newName.toString();
    }

    /**
     * Retrieves the name of the GLVertexAttributes.
     * @return the name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Sets a vertex attribute position by name. This will overwrite any
     * bindings defined within the shader.
     *
     * @param name the name of the vertex attribute.
     * @param index the location it should bind to.
     * @since 15.06.24
     */
    public void setAttribute(final CharSequence name, final int index) {
        LOGGER.trace(
                GLOOP_MARKER, 
                "Set attribute[{}]=[{}] of GLVertexAttributes[{}]", 
                name, 
                index, 
                this.getName());
        
        final String sName = name.toString();

        this.nameMap.put(sName, index);
    }

    /**
     * Checks if the vertex attribute has been registered.
     *
     * @param name the attribute name to check.
     * @return true if the attribute has been registered.
     * @since 15.06.24
     */
    public boolean hasAttribute(final CharSequence name) {
        return this.nameMap.containsKey(name.toString());
    }

    /**
     * Registers a feedback varying. Feedback varyings are only used in
     * Transform Feedback programs.
     *
     * @param vName the OpenGL vertex attribute to capture as feedback.
     * @since 15.06.24
     */
    public void registerFeedbackVarying(final CharSequence vName) {
        LOGGER.trace(
                GLOOP_MARKER, 
                "Registered feedback varying: [{}] in GLVertexAttributes[{}]!", 
                vName,
                this.getName());
        
        this.feedbackVaryings.add(vName.toString());
    }

    /**
     * Checks if the feedback varying has been registered.
     *
     * @param vName the name of the feedback varying to check.
     * @return true if the feedback varying has been registered.
     * @since 15.06.24
     */
    public boolean isFeedbackVarying(final CharSequence vName) {
        return this.feedbackVaryings.contains(vName.toString());
    }

    /**
     * Retrieves the location bound by the vertex attribute. If the attribute
     * has not been defined, -1 will be returned.
     *
     * @param aName the vertex attribute name to check.
     * @return the location or -1 if the attribute has not been checked.
     * @since 15.06.24
     */
    public int getLocation(final CharSequence aName) {
        return this.nameMap.getOrDefault(
                aName.toString(),
                INVALID_ATTRIBUTE_LOCATION);
    }

    /**
     * Retrieves the set of registered vertex attributes.
     *
     * @return the set of vertex attributes.
     * @since 15.06.24
     */
    public Set<String> getAttributes() {
        return Collections.unmodifiableSet(this.nameMap.keySet());
    }

    /**
     * Retrieves the set of registered transform feedback varyings.
     *
     * @return the set of feedback varyings.
     * @since 15.06.24
     */
    public Set<String> getVaryings() {
        return Collections.unmodifiableSet(this.feedbackVaryings);
    }

    /**
     * Retrieves the index of the feedback varying.
     *
     * @param varyingName the name of the feedback varying.
     * @return the index of the feedback varying
     */
    public int getVaryingLocation(final CharSequence varyingName) {
        final String key = varyingName.toString();

        int i = 0;
        for (String varying : this.feedbackVaryings) {
            if (varying.equals(key)) {
                return i;
            } else {
                i++;
            }
        }

        return INVALID_VARYING_LOCATION;
    }
}
