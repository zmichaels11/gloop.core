package com.longlinkislong.gloop;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * GLVertexAttributes represent the vertex attribute settings of an OpenGL
 * program. The should be set programmatically before linking the OpenGL
 * program.
 *
 * @author zmichaels
 * @since 15.06.24
 */
public class GLVertexAttributes {
    public static final int INVALID_VARYING_LOCATION = -1;
    public static final int INVALID_ATTRIBUTE_LOCATION = -1;
    protected final Map<String, Integer> nameMap = new HashMap<>();
    protected final Set<String> feedbackVaryings = new HashSet<>();

    /**
     * Sets a vertex attribute position by name. This will overwrite any
     * bindings defined within the shader.
     *
     * @param name the name of the vertex attribute.
     * @param index the location it should bind to.
     * @since 15.06.24
     */
    public void setAttribute(final CharSequence name, final int index) {
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
     * @return the set of vertex attributes.
     * @since 15.06.24
     */
    public Set<String> getAttributes() {
        return Collections.unmodifiableSet(this.nameMap.keySet());
    }

    /**
     * Retrieves the set of registered transform feedback varyings.
     * @return the set of feedback varyings.
     * @since 15.06.24
     */
    public Set<String> getVaryings() {
        return Collections.unmodifiableSet(this.feedbackVaryings);
    }
    
    /**
     * Retrieves the index of the feedback varying.
     * @param varyingName the name of the feedback varying.
     * @return the index of the feedback varying
     */
    public int getVaryingLocation(final CharSequence varyingName) {
        final String key = varyingName.toString();
        
        int i = 0;
        for(String varying : this.feedbackVaryings) {
            if(varying.equals(key)) {
                return i;
            } else {
                i++;
            }            
        }
        
        return INVALID_VARYING_LOCATION;
    }
}
