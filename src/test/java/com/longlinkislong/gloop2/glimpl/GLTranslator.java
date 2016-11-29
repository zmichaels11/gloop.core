/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.DataFormat;
import static com.longlinkislong.gloop2.DataFormat.SHORT;
import com.longlinkislong.gloop2.ImageAccess;
import com.longlinkislong.gloop2.ImageFormat;
import com.longlinkislong.gloop2.SamplerEdgeSampling;
import com.longlinkislong.gloop2.SamplerMagFilter;
import com.longlinkislong.gloop2.SamplerMinFilter;
import com.longlinkislong.gloop2.TextureFormat;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_R3_G3_B2;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB10;
import static org.lwjgl.opengl.GL11.GL_RGB10_A2;
import static org.lwjgl.opengl.GL11.GL_RGB12;
import static org.lwjgl.opengl.GL11.GL_RGB4;
import static org.lwjgl.opengl.GL11.GL_RGB5;
import static org.lwjgl.opengl.GL11.GL_RGB5_A1;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA12;
import static org.lwjgl.opengl.GL11.GL_RGBA16;
import static org.lwjgl.opengl.GL11.GL_RGBA2;
import static org.lwjgl.opengl.GL11.GL_RGBA4;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_INDEX;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_BYTE_3_3_2;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_10_10_10_2;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_4_4_4_4;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_5_5_1;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_6_5;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_R11F_G11F_B10F;
import static org.lwjgl.opengl.GL30.GL_R16;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_R16I;
import static org.lwjgl.opengl.GL30.GL_R16UI;
import static org.lwjgl.opengl.GL30.GL_R32F;
import static org.lwjgl.opengl.GL30.GL_R32I;
import static org.lwjgl.opengl.GL30.GL_R32UI;
import static org.lwjgl.opengl.GL30.GL_R8;
import static org.lwjgl.opengl.GL30.GL_R8I;
import static org.lwjgl.opengl.GL30.GL_R8UI;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG16;
import static org.lwjgl.opengl.GL30.GL_RG16F;
import static org.lwjgl.opengl.GL30.GL_RG32F;
import static org.lwjgl.opengl.GL30.GL_RG32I;
import static org.lwjgl.opengl.GL30.GL_RG32UI;
import static org.lwjgl.opengl.GL30.GL_RG8;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGB16I;
import static org.lwjgl.opengl.GL30.GL_RGB16UI;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.GL_RGB32I;
import static org.lwjgl.opengl.GL30.GL_RGB32UI;
import static org.lwjgl.opengl.GL30.GL_RGB8I;
import static org.lwjgl.opengl.GL30.GL_RGB8UI;
import static org.lwjgl.opengl.GL30.GL_RGB9_E5;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16I;
import static org.lwjgl.opengl.GL30.GL_RGBA16UI;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32I;
import static org.lwjgl.opengl.GL30.GL_RGBA32UI;
import static org.lwjgl.opengl.GL30.GL_RGBA8I;
import static org.lwjgl.opengl.GL30.GL_RGBA8UI;
import static org.lwjgl.opengl.GL31.GL_R16_SNORM;
import static org.lwjgl.opengl.GL31.GL_R8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RG16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RG8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGB16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGB8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGBA8_SNORM;
import static org.lwjgl.opengl.GL33.GL_RGB10_A2UI;
import static org.lwjgl.opengl.GL44.GL_MIRROR_CLAMP_TO_EDGE;

/**
 *
 * @author zmichaels
 */
public final class GLTranslator {
    public static int toGLenum(ImageFormat fmt) {
        switch(fmt) {
            case RED:
                return GL_RED;
            case RG:
                return GL_RG;
            case RGB:
                return GL_RGB;
            case RGBA:
                return GL_RGBA;
            case BGR:
                return GL_BGR;
            case BGRA:
                return GL_BGRA;
            case DEPTH_COMPONENT:
                return GL_DEPTH_COMPONENT;
            case STENCIL_INDEX:
                return GL_STENCIL_INDEX;
            default:
                throw new UnsupportedOperationException("Format: " + fmt + " is not supported!");
        }
    }
    
    public static int toGLenum(final DataFormat fmt) {
        switch (fmt) {
            case UNSIGNED_BYTE:
                return GL_UNSIGNED_BYTE;
            case BYTE:
                return GL_BYTE;
            case UNSIGNED_SHORT:
                return GL_UNSIGNED_SHORT;
            case SHORT:
                return GL_SHORT;
            case UNSIGNED_INT:
                return GL_INT;
            case FLOAT:
                return GL_FLOAT;
            case UNSIGNED_BYTE_3_3_2:
                return GL_UNSIGNED_BYTE_3_3_2;
            case UNSIGNED_SHORT_5_6_5:
                return GL_UNSIGNED_SHORT_5_6_5;
            case UNSIGNED_SHORT_4_4_4_4:
                return GL_UNSIGNED_SHORT_4_4_4_4;
            case UNSIGNED_SHORT_5_5_5_1:
                return GL_UNSIGNED_SHORT_5_5_5_1;            
            case UNSIGNED_INT_8_8_8_8:
                return GL_UNSIGNED_INT_8_8_8_8;
            case UNSIGNED_INT_10_10_10_2:
                return GL_UNSIGNED_INT_10_10_10_2;
            default:
                throw new UnsupportedOperationException("Format: " + fmt + " is not supported!");
        }
    }
    
    public static int toGLenum(ImageAccess access) {
        switch (access) {
            case READ:
                   return GL_READ_ONLY;
            case WRITE:
                return GL_WRITE_ONLY;
            case READ_WRITE:
                return GL_READ_WRITE;
            default:
                throw new UnsupportedOperationException("Access: " + access + " is not supported!");
        }
    }
    
    public static ImageFormat toImageFormat(TextureFormat fmt) {
        switch (fmt) {
            case R8:
                return ImageFormat.RED;
            case R8_SNORM:
                return ImageFormat.RED;
            case R16:
                return ImageFormat.RED;
            case R16_SNORM:
                return ImageFormat.RED;
            case RG8:
                return ImageFormat.RG;
            case RG8_SNORM:
                return ImageFormat.RG;
            case RG16:
                return ImageFormat.RG;
            case RG16_SNORM:
                return ImageFormat.RG;
            case R3_G3_B2:
                return ImageFormat.RGB;
            case RGB4:
                return ImageFormat.RGB;
            case RGB5:
                return ImageFormat.RGB;
            case RGB8:
                return ImageFormat.RGB;
            case RGB8_SNORM:
                return ImageFormat.RGB;
            case RGB10:
                return ImageFormat.RGB;
            case RGB12:
                return ImageFormat.RGB;
            case RGB16_SNORM:
                return ImageFormat.RGB;
            case RGBA2:
                return ImageFormat.RGBA;
            case RGBA4:
                return ImageFormat.RGBA;
            case RGB5_A1:
                return ImageFormat.RGBA;
            case RGBA8:                
                return ImageFormat.RGBA;
            case RGBA8_SNORM:
                return ImageFormat.RGBA;
            case RGB10_A2:
                return ImageFormat.RGBA;
            case RGB10_A2UI:
                return ImageFormat.RGBA;
            case RGBA12:
                return ImageFormat.RGBA;
            case RGBA16:
                return ImageFormat.RGBA;
            case R16F:
                return ImageFormat.RED;
            case RG16F:
                return ImageFormat.RG;
            case RGB16F:
                return ImageFormat.RGB;
            case RGBA16F:
                return ImageFormat.RGBA;
            case R32F:
                return ImageFormat.RED;
            case RG32F:
                return ImageFormat.RG;
            case RGB32F:
                return ImageFormat.RGB;
            case RGBA32F:
                return ImageFormat.RGBA;
            case R11F_G11F_B10F:
                return ImageFormat.RGB;
            case RGB9_E5:
                return ImageFormat.RGB;
            case R8I:
                return ImageFormat.RED;
            case R8UI:
                return ImageFormat.RED;
            case R16I:
                return ImageFormat.RED;
            case R16UI:
                return ImageFormat.RED;
            case R32I:
                return ImageFormat.RED;
            case R32UI:
                return ImageFormat.RED;
            case RG16I:
                return ImageFormat.RG;
            case RG16UI:
                return ImageFormat.RG;
            case RG32I:
                return ImageFormat.RG;
            case RG32UI:
                return ImageFormat.RG;
            case RGB8I:
                return ImageFormat.RGB;
            case RGB8UI:
                return ImageFormat.RGB;
            case RGB16I:
                return ImageFormat.RGB;
            case RGB16UI:
                return ImageFormat.RGB;
            case RGB32I:
                return ImageFormat.RGB;
            case RGB32UI:
                return ImageFormat.RGB;
            case RGBA8I:
                return ImageFormat.RGBA;
            case RGBA8UI:
                return ImageFormat.RGBA;
            case RGBA16I:
                return ImageFormat.RGBA;
            case RGBA16UI:
                return ImageFormat.RGBA;
            case RGBA32I:
                return ImageFormat.RGBA;
            case RGBA32UI:
                return ImageFormat.RGBA;
            default:
                throw new UnsupportedOperationException("Format: " + fmt + " is not supported!");
        }
    }
    
    public static int toGLenum(TextureFormat fmt) {
        switch (fmt) {
            case R8:
                return GL_R8;
            case R8_SNORM:
                return GL_R8_SNORM;
            case R16:
                return GL_R16;
            case R16_SNORM:
                return GL_R16_SNORM;
            case RG8:
                return GL_RG8;
            case RG8_SNORM:
                return GL_RG8_SNORM;
            case RG16:
                return GL_RG16;
            case RG16_SNORM:
                return GL_RG16_SNORM;
            case R3_G3_B2:
                return GL_R3_G3_B2;
            case RGB4:
                return GL_RGB4;
            case RGB5:
                return GL_RGB5;
            case RGB8:
                return GL_RGB8;
            case RGB8_SNORM:
                return GL_RGB8_SNORM;
            case RGB10:
                return GL_RGB10;
            case RGB12:
                return GL_RGB12;
            case RGB16_SNORM:
                return GL_RGB16_SNORM;
            case RGBA2:
                return GL_RGBA2;
            case RGBA4:
                return GL_RGBA4;
            case RGB5_A1:
                return GL_RGB5_A1;
            case RGBA8:                
                return GL_RGBA8;
            case RGBA8_SNORM:
                return GL_RGBA8_SNORM;
            case RGB10_A2:
                return GL_RGB10_A2;
            case RGB10_A2UI:
                return GL_RGB10_A2UI;
            case RGBA12:
                return GL_RGBA12;
            case RGBA16:
                return GL_RGBA16;
            case R16F:
                return GL_R16F;
            case RG16F:
                return GL_RG16F;
            case RGB16F:
                return GL_RGB16F;
            case RGBA16F:
                return GL_RGBA16F;
            case R32F:
                return GL_R32F;
            case RG32F:
                return GL_RG32F;
            case RGB32F:
                return GL_RGB32F;
            case RGBA32F:
                return GL_RGBA32F;
            case R11F_G11F_B10F:
                return GL_R11F_G11F_B10F;
            case RGB9_E5:
                return GL_RGB9_E5;
            case R8I:
                return GL_R8I;
            case R8UI:
                return GL_R8UI;
            case R16I:
                return GL_R16I;
            case R16UI:
                return GL_R16UI;
            case R32I:
                return GL_R32I;
            case R32UI:
                return GL_R32UI;
            case RG16I:
                return GL_R16I;
            case RG16UI:
                return GL_R16UI;
            case RG32I:
                return GL_RG32I;
            case RG32UI:
                return GL_RG32UI;
            case RGB8I:
                return GL_RGB8I;
            case RGB8UI:
                return GL_RGB8UI;
            case RGB16I:
                return GL_RGB16I;
            case RGB16UI:
                return GL_RGB16UI;
            case RGB32I:
                return GL_RGB32I;
            case RGB32UI:
                return GL_RGB32UI;
            case RGBA8I:
                return GL_RGBA8I;
            case RGBA8UI:
                return GL_RGBA8UI;
            case RGBA16I:
                return GL_RGBA16I;
            case RGBA16UI:
                return GL_RGBA16UI;
            case RGBA32I:
                return GL_RGBA32I;
            case RGBA32UI:
                return GL_RGBA32UI;                                
            default:
                throw new UnsupportedOperationException("Format: " + fmt + " is not supported!");
        }
    }
    
    public static int toGLenum(SamplerMinFilter filter) {
        switch (filter) {
            case LINEAR:
                return GL_LINEAR;
            case NEAREST:
                return GL_NEAREST;
            case DEFAULT:
            case LINEAR_MIPMAP_LINEAR:
                return GL_LINEAR_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_NEAREST:
                return GL_LINEAR_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR:
                return GL_NEAREST_MIPMAP_LINEAR;
            case NEAREST_MIPMAP_NEAREST:
                return GL_NEAREST_MIPMAP_NEAREST;
            default:
                throw new UnsupportedOperationException("Filter: " + filter + " is not supported!");
        }
    }
    
    public static int toGLenum(SamplerMagFilter filter) {
        switch(filter) {
            case DEFAULT:
            case LINEAR:
                return GL_LINEAR;
            case NEAREST:
                return GL_NEAREST;
            default:
                throw new UnsupportedOperationException("Filter: " + filter + " is not supported!");
        }
    }
    
    public static int toGLenum(SamplerEdgeSampling edge) {
        switch(edge) {
            case DEFAULT:
            case REPEAT:
                return GL_REPEAT;
            case MIRRORED_REPEAT:
                return GL_MIRRORED_REPEAT;
            case CLAMP_TO_EDGE:
                return GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER:
                return GL_CLAMP_TO_BORDER;
            case MIRROR_CLAMP_TO_EDGE:
                return GL_MIRROR_CLAMP_TO_EDGE;
            default:
                throw new UnsupportedOperationException("Edge sampling: " + edge + " is not supported!");
        }
    }
}
