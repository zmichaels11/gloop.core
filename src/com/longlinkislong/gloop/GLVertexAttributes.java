package com.longlinkislong.gloop;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zmichaels
 */
public class GLVertexAttributes {
    public static final int INVALID_ATTRIBUTE_LOCATION = -1;
    protected final Map<String, Integer> nameMap = new HashMap<>();
    protected final Set<String> feedbackVaryings = new HashSet<>();

    public void setAttribute(final CharSequence name, final int index) {
        final String sName = name.toString();

        this.nameMap.put(sName, index);
    }

    public void registerFeedbackVarying(final CharSequence vName) {
        this.feedbackVaryings.add(vName.toString());
    }

    public boolean isFeedbackVarying(final CharSequence vName) {
        return this.feedbackVaryings.contains(vName.toString());
    }

    public int getLocation(final CharSequence aName) {
        return this.nameMap.getOrDefault(
                aName.toString(), 
                INVALID_ATTRIBUTE_LOCATION);
    }
    
    public Set<String> getAttributes() {
        return Collections.unmodifiableSet(this.nameMap.keySet());
    }
    
    public Set<String> getVaryings() {
        return Collections.unmodifiableSet(this.feedbackVaryings);
    }
}
