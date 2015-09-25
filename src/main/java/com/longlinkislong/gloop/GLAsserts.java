/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLTools.pixelSize;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class GLAsserts {

    public static String invalidWidthMsg(int width) {
        return "Invalid width [" + width + "]! Must be at least 1.";
    }

    public static String invalidHeightMsg(int height) {
        return "Invalid height [" + height + "]! Must be at least 1.";
    }

    public static String invalidDepthMsg(int depth) {
        return "Invalid depth [" + depth + "]! Must be at least 1.";
    }

    public static boolean checkDimension(int dim) {
        return dim >= 1;
    }

    public static String invalidXOffsetMsg(int xOffset) {
        return "Invalid xOffset [" + xOffset + "]! Must be at least 0.";
    }

    public static boolean checkOffset(long offset) {
        return offset >= 0;
    }

    public static String invalidYOffsetMsg(int yOffset) {
        return "Invalid yOffset [" + yOffset + "]! Must be at least 0.";
    }

    public static String invalidZOffsetMsg(int zOffset) {
        return "Invalid zOffset [" + zOffset + "]! Must be at least 0.";
    }

    public static boolean checkGLenum(int glEnum, Function<Integer, Optional<?>> enumGetter) {
        return enumGetter.apply(glEnum).isPresent();
    }

    public static String invalidGLenumMsg(int glEnum) {
        return "Invalid GLenum [" + glEnum + "]!";
    }

    public static boolean checkMipmapLevel(int level) {
        return level >= 0;
    }

    public static String invalidMipmapLevelMsg(int level) {
        return "Invalid mipmap level [" + level + "]! Must be at least 0.";
    }

    public static boolean checkMipmapDefine(int levels) {
        return levels >= 1;
    }

    public static String invalidMipmapDefineMsg(int levels) {
        return "Invalid texture mipmap levels [" + levels + "]! At least 1 mipmap must be defined.";
    }

    public static boolean checkNullableId(int id) {
        return id >= 0;
    }

    public static String invalidNullableTextureIdMsg(int texId) {
        return "Invalid texture id [" + texId + "]!";
    }

    public static boolean checkId(int id) {
        return id >= 1;
    }

    public static String invalidTextureIdMsg(int texId) {
        return "Invalid texture id [" + texId + "]! Texture ID must refer to a valid, non-null texture.";
    }

    public static String invalidNullableBufferIdMsg(int bufferId) {
        return "Invalid buffer id [" + bufferId + "]!";
    }

    public static String invalidBufferIdMsg(int bufferId) {
        return "Invalid buffer id [" + bufferId + "]! Buffer ID must refer to a valid, non-null buffer.";
    }

    public static String NON_DIRECT_BUFFER_MSG = "ByteBuffer must be direct!";

    public static boolean checkBufferIsNative(ByteBuffer buffer) {
        return buffer.order() == ByteOrder.nativeOrder();
    }

    public static String bufferIsNotNativeMsg(ByteBuffer buffer) {
        return String.format("ByteBuffer [order=%s] must be in native order [order=%s]!", buffer.order(), ByteOrder.nativeOrder());
    }

    public static String glErrorMsg(String functionDef, Object... params) {
        return "OpenGL API call: [" + functionDef + "] with parameters: " + Arrays.toString(params) + " failed!";
    }

    public static boolean checkGLError() {
        return GL11.glGetError() == GL11.GL_NO_ERROR;
    }

    public static boolean checkFloat(float val) {
        return Float.isFinite(val);
    }

    public static String invalidFloatMsg(float val) {
        return "Float value [" + val + "] is not finite!";
    }

    public static boolean checkDouble(double val) {
        return Double.isFinite(val);
    }

    public static String invalidDoubleMsg(double val) {
        return "Double value [" + val + "] is not finite!";
    }

    public static String invalidFramebufferIdMsg(int framebufferId) {
        return "Invalid framebuffer id [" + framebufferId + "]! Framebuffer ID must refer to a valid, non-null framebuffer!";
    }

    public static String invalidNullableFramebufferIdMsg(int framebufferId) {
        return "Invalid framebfufer id [" + framebufferId + "]! Framebuffer ID must refer to either a valid framebuffer or the default framebuffer.";
    }

    public static boolean checkSize(long size) {
        return size >= 0L;
    }

    public static String invalidSizeMsg(long size) {
        return "Invalid size [" + size + "]! Size cannot be less than 0.";
    }

    public static String invalidOffsetMsg(long offset) {
        return "Invalid offset [" + offset + "]! Offset cannot be less than 0.";
    }

    public static String invalidProgramIdMsg(int programId) {
        return "Invalid program id [" + programId + "]! Program ID must refer to a valid, non-null program.";
    }

    public static String invalidUniformLocationMsg(int loc) {
        return "Invalid uniform location [" + loc + "]! Uniform locations must be at least 0.";
    }

    public static String invalidTextureUnitMsg(int unit) {
        return "Invalid texture unit [" + unit + "]! Texture unit must be at least 0.";
    }

    public static boolean checkBufferSize(int width, int height, int depth, int format, int type, ByteBuffer buffer) {
        return pixelSize(width, height, depth, format, type) <= buffer.limit();
    }

    public static String bufferTooSmallMsg(int width, int height, int depth, int format, int type, ByteBuffer buffer) {
        return "ByteBuffer is too small! Operation requires [" + pixelSize(width, height, depth, format, type) + "] bytes but buffer contains [" + buffer.limit() + "]!";
    }
}
