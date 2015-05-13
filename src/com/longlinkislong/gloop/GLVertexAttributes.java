package com.longlinkislong.gloop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zmichaels
 */
public class GLVertexAttributes {

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
        return this.nameMap.getOrDefault(aName.toString(), -1);
    }   
}
