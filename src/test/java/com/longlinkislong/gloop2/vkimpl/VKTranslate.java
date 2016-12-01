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
import com.longlinkislong.gloop2.FrontFace;
import com.longlinkislong.gloop2.TextureFormat;
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
                return VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
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
    
    public static int toVKenum(final FrontFace frontFace) {
        switch (frontFace) {
            case CLOCKWISE:
                return VK10.VK_FRONT_FACE_CLOCKWISE;
            case COUNTER_CLOCKWISE:
                return VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE;
            default:
                throw new UnsupportedOperationException("Unsupported front face mode: " + frontFace);
        }
    }        
    
    public static int toVKenum(final TextureFormat fmt) {
        switch (fmt) {
            case R8:
                return VK10.VK_FORMAT_R8_UNORM;
            case R8_SNORM:
                return VK10.VK_FORMAT_R8_SNORM;
            case R16:
                return VK10.VK_FORMAT_R16_UNORM;
            case R16_SNORM:
                return VK10.VK_FORMAT_R16_SNORM;
            case RG8:
                return VK10.VK_FORMAT_R8G8_UNORM;
            case RG8_SNORM:
                return VK10.VK_FORMAT_R8G8_SNORM;
            case RG16:
                return VK10.VK_FORMAT_R16G16_UNORM;
            case RG16_SNORM:
                return VK10.VK_FORMAT_R16G16_SNORM;
            case RGB8:
                return VK10.VK_FORMAT_R8G8B8_UNORM;
            case RGB8_SNORM:
                return VK10.VK_FORMAT_R8G8B8_SNORM;
            case RGB16:
                return VK10.VK_FORMAT_R16G16B16_UNORM;
            case RGB16_SNORM:
                return VK10.VK_FORMAT_R16G16B16_SNORM;
            case RGBA8:
                return VK10.VK_FORMAT_R8G8B8A8_UNORM;
            case RGBA8_SNORM:
                return VK10.VK_FORMAT_R8G8B8A8_SNORM;
            case RGBA16:
                return VK10.VK_FORMAT_R16G16B16A16_UNORM;
            case RGBA16_SNORM:
                return VK10.VK_FORMAT_R16G16B16A16_SNORM;
            case R16F:
                return VK10.VK_FORMAT_R16_SFLOAT;
            case R32F:
                return VK10.VK_FORMAT_R32_SFLOAT;
            case RG16F:
                return VK10.VK_FORMAT_R16G16_SFLOAT;
            case RG32F:
                return VK10.VK_FORMAT_R32G32_SFLOAT;
            case RGB16F:
                return VK10.VK_FORMAT_R16G16B16_SFLOAT;
            case RGB32F:
                return VK10.VK_FORMAT_R32G32B32_SFLOAT;
            case RGBA16F:
                return VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
            case RGBA32F:
                return VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
            case R8I:
                return VK10.VK_FORMAT_R8_SINT;
            case R8UI:
                return VK10.VK_FORMAT_R8_UINT;
            case R16I:
                return VK10.VK_FORMAT_R16_SINT;
            case R16UI:
                return VK10.VK_FORMAT_R16_UINT;
            case R32I:
                return VK10.VK_FORMAT_R32_SINT;
            case R32UI:
                return VK10.VK_FORMAT_R32_UINT;
            case RG8I:
                return VK10.VK_FORMAT_R8G8_SINT;
            case RG8UI:
                return VK10.VK_FORMAT_R8G8_UINT;
            case RG16I:
                return VK10.VK_FORMAT_R16G16_SINT;
            case RG16UI:
                return VK10.VK_FORMAT_R16G16_UINT;
            case RG32I:
                return VK10.VK_FORMAT_R32G32_SINT;
            case RG32UI:
                return VK10.VK_FORMAT_R32G32_UINT;
            case RGB8I:
                return VK10.VK_FORMAT_R8G8B8_SINT;
            case RGB8UI:
                return VK10.VK_FORMAT_R8G8B8_UINT;
            case RGB16I:
                return VK10.VK_FORMAT_R16G16B16_SINT;
            case RGB16UI:
                return VK10.VK_FORMAT_R16G16B16_UINT;
            case RGB32I:
                return VK10.VK_FORMAT_R32G32B32_SINT;
            case RGB32UI:
                return VK10.VK_FORMAT_R32G32B32_UINT;
            case RGBA8I:
                return VK10.VK_FORMAT_R8G8B8A8_SINT;
            case RGBA8UI:
                return VK10.VK_FORMAT_R8G8B8A8_UINT;
            case RGBA16I:
                return VK10.VK_FORMAT_R16G16B16A16_SINT;
            case RGBA16UI:
                return VK10.VK_FORMAT_R16G16B16A16_UINT;
            case RGBA32I:
                return VK10.VK_FORMAT_R32G32B32A32_SINT;
            case RGBA32UI:
                return VK10.VK_FORMAT_R32G32B32A32_UINT;
            default:
                throw new UnsupportedOperationException("Unsupported Texture Format: " + fmt);
        }
    }
}
