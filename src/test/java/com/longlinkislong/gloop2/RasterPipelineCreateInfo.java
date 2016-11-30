/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author zmichaels
 */
public final class RasterPipelineCreateInfo {

    public final VertexInputs vertexInputs;
    public final List<Shader> shaderStages;
    public final PrimitiveType primitiveType;
    public final PolygonMode polygonMode;
    public final CullMode cullMode;
    public final FrontFace frontFace;

    public final ColorMask colorMask;
    public final boolean blendEnabled;

    public final boolean depthTestEnabled;
    public final boolean stencilTestEnabled;

    public final Viewport viewport;
    public final Scissor scissor;

    public RasterPipelineCreateInfo withScissor(final Scissor scissor) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withViewport(final Viewport viewport) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withColorMask(final ColorMask colorMask) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withFrontFace(final FrontFace frontFace) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withCullMode(final CullMode cullMode) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withPolygonMode(final PolygonMode polygonMode) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withPrimitiveType(final PrimitiveType primitiveType) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withVertexInputs(final VertexInputs vertexInputs) {
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                shaderStages, 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo withShaderStage(final Shader newShader) {            
        return new RasterPipelineCreateInfo(
                vertexInputs, 
                Stream.concat(shaderStages.stream(), Stream.of(newShader)).collect(Collectors.toList()), 
                primitiveType, 
                polygonMode, 
                cullMode, 
                frontFace, 
                colorMask, 
                blendEnabled, 
                depthTestEnabled,
                stencilTestEnabled, 
                viewport, 
                scissor);
    }
    
    public RasterPipelineCreateInfo() {
        this(
                new VertexInputs(),
                Collections.emptyList(),
                PrimitiveType.TRIANGLES,
                PolygonMode.FILL,
                CullMode.NONE,
                FrontFace.CLOCKWISE,
                new ColorMask(),
                false,
                false,
                false,
                new Viewport(0, 0, 640, 480, 0.0, 1.0),
                new Scissor(0, 0, 640, 480));
    }        

    public RasterPipelineCreateInfo(
            VertexInputs vertexInputs,
            List<Shader> shaderStages,
            PrimitiveType primitiveType,
            PolygonMode polygonMode, CullMode cullMode,
            FrontFace frontFace,
            ColorMask colorMask,
            boolean blendEnabled,
            boolean depthTestEnabled,
            boolean stencilTestEnabled,
            Viewport viewport,
            Scissor scissor) {

        this.vertexInputs = vertexInputs;
        this.shaderStages = Collections.unmodifiableList(new ArrayList<>(shaderStages));
        this.primitiveType = primitiveType;
        this.polygonMode = polygonMode;
        this.cullMode = cullMode;
        this.frontFace = frontFace;
        this.colorMask = colorMask;
        this.blendEnabled = blendEnabled;
        this.depthTestEnabled = depthTestEnabled;
        this.stencilTestEnabled = stencilTestEnabled;
        this.viewport = viewport;
        this.scissor = scissor;
    }

    //TODO: support attachment customization. Currently assuming only color attachment...
    //TODO: support multisample. Currently assuming only 1 sample...
    public RasterPipeline allocate() {
        return ObjectFactoryManager.getInstance().getPipelineFactory().allocate(this);
    }
}
