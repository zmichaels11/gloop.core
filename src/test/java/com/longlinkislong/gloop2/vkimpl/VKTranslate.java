/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.CullMode;
import com.longlinkislong.gloop2.PolygonMode;
import com.longlinkislong.gloop2.PrimitiveType;
import com.longlinkislong.gloop2.ShaderType;
import com.longlinkislong.gloop2.VertexAttributeFormat;
import com.longlinkislong.gloop2.VertexOrder;
import org.lwjgl.vulkan.VK10;

/**
 *
 * @author zmichaels
 */
public final class VKTranslate {
    private VKTranslate() {}
    
    public static int toVKenum(final ShaderType type) {
        switch(type) {
            case VERTEX:
                return VK10.VK_SHADER_STAGE_VERTEX_BIT;
            case TESS_EVALUATION:
                return VK10.VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT;
            case TESS_CONTROL:
                return VK10.VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT;
            case GEOMETRY:
                return VK10.VK_SHADER_STAGE_GEOMETRY_BIT;
            case FRAGMENT:
                return VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
            case COMPUTE:
                return VK10.VK_SHADER_STAGE_COMPUTE_BIT;
            default:
                throw new UnsupportedOperationException("Unsupported shader stage: " + type);
        }
    }
    
    public static int toVKenum(final VertexAttributeFormat fmt) {
        switch (fmt) {
            case X_32F:
                return VK10.VK_FORMAT_R32_SFLOAT;
            case XY_32F:
                return VK10.VK_FORMAT_R32G32_SFLOAT;
            case XYZ_32F:
                return VK10.VK_FORMAT_R32G32B32_SFLOAT;
            case XYZW_32F:
                return VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
            default:
                throw new UnsupportedOperationException("Unsupported vertex format: " + fmt);
                
        }
    }
    
    public static int toVKenum(final PrimitiveType type) {
        switch(type) {
            case POINTS:
                return VK10.VK_PRIMITIVE_TOPOLOGY_POINT_LIST;
            case LINES:
                return VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST;
            case TRIANGLES:
                return VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST;
            default:
                throw new UnsupportedOperationException("Unsupported primitive type: "+ type);
        }
    }
    
    public static int toVKenum(final PolygonMode mode) {
        switch(mode) {
            case FILL:
                return VK10.VK_POLYGON_MODE_FILL;
            case LINE:
                return VK10.VK_POLYGON_MODE_LINE;
            case POINT:
                return VK10.VK_POLYGON_MODE_POINT;
            default:
                throw new UnsupportedOperationException("Unsupported polygon mode: " + mode);
        }
    }
    
    public static int toVKenum(final CullMode mode) {
        switch (mode) {
            case FRONT:
                return VK10.VK_CULL_MODE_FRONT_BIT;
            case BACK:
                return VK10.VK_CULL_MODE_BACK_BIT;
            case FRONT_AND_BACK:
                return VK10.VK_CULL_MODE_FRONT_AND_BACK;
            case NONE:
                return VK10.VK_CULL_MODE_NONE;
            default:
                throw new UnsupportedOperationException("Unsupported cull mode: " + mode);
        }
    }
    
    public static int toVKenum(final VertexOrder frontFace) {
        switch (frontFace) {
            case CLOCKWISE:
                return VK10.VK_FRONT_FACE_CLOCKWISE;
            case COUNTER_CLOCKWISE:
                return VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE;
            default:
                throw new UnsupportedOperationException("Unsupported front face mode: " + frontFace);
        }
    }
}
