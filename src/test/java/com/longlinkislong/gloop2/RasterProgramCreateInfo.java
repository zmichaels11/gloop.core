/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author zmichaels
 * @param <ShaderT>
 */
public final class RasterProgramCreateInfo<ShaderT extends AbstractShader> extends ProgramCreateInfo<ShaderT> {

    public final ShaderT vertex;
    public final ShaderT tessControl;
    public final ShaderT tessEvaluation;
    public final ShaderT geometry;
    public final ShaderT fragment;

    public RasterProgramCreateInfo(
            final Map<String, Integer> attribs,
            final ShaderT vertex,
            final ShaderT tessControl,
            final ShaderT tessEvaluation,
            final ShaderT geometry,
            final ShaderT fragment) {

        super(attribs);

        this.vertex = vertex;
        this.tessControl = tessControl;
        this.tessEvaluation = tessEvaluation;
        this.geometry = geometry;
        this.fragment = fragment;
    }

    public RasterProgramCreateInfo() {
        this(Collections.emptyMap(), null, null, null, null, null);
    }

    public RasterProgramCreateInfo withVertexShader(final ShaderT shader) {
        return new RasterProgramCreateInfo(this.attributes, shader, this.tessControl, this.tessEvaluation, this.geometry, this.fragment);
    }

    public RasterProgramCreateInfo withTessControlShader(final ShaderT shader) {
        return new RasterProgramCreateInfo(this.attributes, this.vertex, shader, this.tessEvaluation, this.geometry, this.fragment);
    }

    public RasterProgramCreateInfo withTessEvaluationShader(final ShaderT shader) {
        return new RasterProgramCreateInfo(this.attributes, this.vertex, this.tessControl, shader, this.geometry, this.fragment);
    }

    public RasterProgramCreateInfo withGeometryShader(final ShaderT shader) {
        return new RasterProgramCreateInfo(this.attributes, this.vertex, this.tessControl, this.tessEvaluation, shader, this.fragment);
    }

    public RasterProgramCreateInfo withFragmentShader(final ShaderT shader) {
        return new RasterProgramCreateInfo(this.attributes, this.vertex, this.tessControl, this.tessEvaluation, this.geometry, shader);
    }

    public RasterProgramCreateInfo withAttribute(final String name, final int location) {
        final Map<String, Integer> attrib = new HashMap(this.attributes);

        attrib.put(name, location);

        return new RasterProgramCreateInfo(attrib, this.vertex, this.tessControl, this.tessEvaluation, this.geometry, this.fragment);
    }

    @Override
    List<ShaderT> getShaders() {        
        return Collections.unmodifiableList(
                Stream.of(this.vertex, this.tessControl, this.tessEvaluation, this.geometry, this.fragment)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
    }
}
