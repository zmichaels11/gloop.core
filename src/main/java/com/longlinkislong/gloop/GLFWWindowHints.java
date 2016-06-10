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

import java.util.function.IntPredicate;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public final class GLFWWindowHints {

    public final int resizable;
    public final int visible;
    public final int decorated;
    public final int focused;
    public final int autoIconify;
    public final int floating;
    public final int redBits;
    public final int greenBits;
    public final int blueBits;
    public final int alphaBits;
    public final int depthBits;
    public final int stencilBits;
    public final int stereoRendering;
    public final int samples;
    public final int srgbCapable;
    public final int doubleBuffer;

    public final int refreshRate;
    public final int clientAPI;
    public final int contextVersionMajor;
    public final int contextVersionMinor;
    public final int openglForwardCompat;
    public final int openglDebugContext;
    public final int openglProfile;
    public final int contextRobustness;
    public final int contextReleaseBehavior;

    public GLFWWindowHints withResizable(boolean resizable) {
        return new GLFWWindowHints(resizable ? GL11.GL_TRUE : GL11.GL_FALSE, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withVisible(boolean visible) {
        return new GLFWWindowHints(this.resizable, visible ? GL11.GL_TRUE : GL11.GL_FALSE, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withDecorated(boolean decorated) {
        return new GLFWWindowHints(this.resizable, this.visible, decorated ? GL11.GL_TRUE : GL11.GL_FALSE, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withFocused(boolean focused) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, focused ? GL11.GL_TRUE : GL11.GL_FALSE,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withAutoIconify(boolean autoIconify) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                autoIconify ? GL11.GL_TRUE : GL11.GL_FALSE, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withFloating(boolean floating) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, floating ? GL11.GL_TRUE : GL11.GL_FALSE, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withRedBits(int redBits) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withGreenBits(int greenBits) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withBlueBits(int blueBits) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withAlphaBits(int alphaBits) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withDepthBits(int depthBits) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withStencilBits(int stencilBits) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withSamples(int samples) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withRefreshRate(int refreshRate) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withStereoRendering(boolean stereoRendering) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                stereoRendering ? GL11.GL_TRUE : GL11.GL_FALSE, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withSRGBCapable(boolean srgbCapable) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, srgbCapable ? GL11.GL_TRUE : GL11.GL_FALSE, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withDoubleBuffer(boolean doubleBuffer) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, doubleBuffer ? GL11.GL_TRUE : GL11.GL_FALSE, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withClientAPI(int clientAPI) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withContextVersion(int contextMajorVersion, int contextMinorVersion) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                contextVersionMajor, contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withContextRobustness(boolean contextRobustness) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, contextRobustness ? GL11.GL_TRUE : GL11.GL_FALSE,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withContextReleaseBehavior(int contextReleaseBehavior) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withOpenGLForwardCompat(boolean openglForwardCompat) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, openglForwardCompat ? GL11.GL_TRUE : GL11.GL_FALSE, this.openglDebugContext,
                this.openglProfile);
    }

    public GLFWWindowHints withOpenGLDebugContext(boolean openglDebugContext) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, openglDebugContext ? GL11.GL_TRUE : GL11.GL_FALSE,
                this.openglProfile);
    }

    public GLFWWindowHints withOpenGLProfile(int openglProfile) {
        return new GLFWWindowHints(this.resizable, this.visible, this.decorated, this.focused,
                this.autoIconify, this.floating, this.redBits, this.greenBits, this.blueBits,
                this.alphaBits, this.depthBits, this.stencilBits, this.samples, this.refreshRate,
                this.stereoRendering, this.srgbCapable, this.doubleBuffer, this.clientAPI,
                this.contextVersionMajor, this.contextVersionMinor, this.contextRobustness,
                this.contextReleaseBehavior, this.openglForwardCompat, this.openglDebugContext,
                openglProfile);
    }

    public GLFWWindowHints() {
        this(GL11.GL_TRUE, GL11.GL_TRUE, GL11.GL_TRUE, GL11.GL_TRUE,
                GL11.GL_TRUE, GL11.GL_FALSE, 8, 8, 8,
                8, 24, 8, 0, GLFW.GLFW_DONT_CARE,
                GL11.GL_FALSE, GL11.GL_FALSE, GL11.GL_TRUE, GLFW.GLFW_OPENGL_API,
                1, 0, GLFW.GLFW_NO_ROBUSTNESS,
                GLFW.GLFW_ANY_RELEASE_BEHAVIOR, GL11.GL_FALSE, GL11.GL_FALSE,
                GLFW.GLFW_OPENGL_ANY_PROFILE);
    }

    public GLFWWindowHints(int resizable, int visible, int decorated, int focused,
            int autoIconify, int floating, int redBits, int greenBits, int blueBits,
            int alphaBits, int depthBits, int stencilBits, int samples, int refreshRate,
            int stereoRendering, int srgbCapable, int doubleBuffer, int clientAPI,
            int contextVersionMajor, int contextVersionMinor, int contextRobustness,
            int contextReleaseBehavior, int openglForwardCompat, int openglDebugContext,
            int openglProfile) {

        final IntPredicate isTrueOrFalse = param -> {
            switch (param) {
                case GL11.GL_TRUE:
                case GL11.GL_FALSE:
                    return true;
                default:
                    return false;
            }
        };

        if (!isTrueOrFalse.test(this.resizable = resizable)) {
            throw new IllegalArgumentException("Unsupported resize parameter: [" + resizable + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.visible = visible)) {
            throw new IllegalArgumentException("Unsupported visible parameter: [" + visible + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.decorated = decorated)) {
            throw new IllegalArgumentException("Unsupported decorated parameter: [" + decorated + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.focused = focused)) {
            throw new IllegalArgumentException("Unsupported focused parameter: [" + focused + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.autoIconify = autoIconify)) {
            throw new IllegalArgumentException("Unsupported auto iconify parameter: [" + autoIconify + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.floating = floating)) {
            throw new IllegalArgumentException("Unsupported floating parameter: [" + floating + "]! Expected GL_TRUE or GL_FALSE!");
        }

        final IntPredicate isInteger = param -> (param == GLFW.GLFW_DONT_CARE || (param >= 0 && param <= Integer.MAX_VALUE));

        if (!isInteger.test(this.redBits = redBits)) {
            throw new IllegalArgumentException("Unsupported red bits parameter: [" + redBits + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.greenBits = greenBits)) {
            throw new IllegalArgumentException("Unsupported green bits parameter: [" + greenBits + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.blueBits = blueBits)) {
            throw new IllegalArgumentException("Unsupported blue bits parameter: [" + blueBits + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.alphaBits = alphaBits)) {
            throw new IllegalArgumentException("Unsupported alpha bits parameter: [" + alphaBits + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.depthBits = depthBits)) {
            throw new IllegalArgumentException("Unsupported depth bits parameter: [" + depthBits + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.stencilBits = stencilBits)) {
            throw new IllegalArgumentException("Unsupported stencil bits parameter: [" + stencilBits + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.samples = samples)) {
            throw new IllegalArgumentException("Unsupported samples parameters: [" + samples + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        } else if (!isInteger.test(this.refreshRate = refreshRate)) {
            throw new IllegalArgumentException("Unsupported refresh rate: [" + refreshRate + "]! Expected 0 to INT_MAX or GLFW_DONT_CARE!");
        }

        if (!isTrueOrFalse.test(this.stereoRendering = stereoRendering)) {
            throw new IllegalArgumentException("Unsupported stereo rendering parameter: [" + stereoRendering + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.srgbCapable = srgbCapable)) {
            throw new IllegalArgumentException("Unsupported srgbCapable parameter: [" + srgbCapable + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.doubleBuffer = doubleBuffer)) {
            throw new IllegalArgumentException("Unsupported doubleBuffer parameter: [" + doubleBuffer + "]! Expected GL_TRUE or GL_FALSE!");
        }

        switch (clientAPI) {
            case GLFW.GLFW_OPENGL_API:
            case GLFW.GLFW_OPENGL_ES_API:
            case GLFW.GLFW_NO_API:
                this.clientAPI = clientAPI;
                break;
            default:
                throw new IllegalArgumentException("Unsupported client API: [" + clientAPI + "]! Only GLFW_OPENGL_API, GLFW_OPENGLES_API, and GLFW_NO_API are supported!");
        }

        this.contextVersionMajor = contextVersionMajor;
        this.contextVersionMinor = contextVersionMinor;

        switch (contextRobustness) {
            case GLFW.GLFW_NO_ROBUSTNESS:
            case GLFW.GLFW_NO_RESET_NOTIFICATION:
            case GLFW.GLFW_LOSE_CONTEXT_ON_RESET:
                this.contextRobustness = contextRobustness;
                break;
            default:
                throw new IllegalArgumentException("Unsupported context robustness parameter: [" + contextRobustness + "]! Expected GLFW_NO_ROBUSTNESS, GLFW_NO_RESET_NOTIFICATION, or GLFW_LOSE_CONTEXT_ON_RESET!");
        }

        switch (contextReleaseBehavior) {
            case GLFW.GLFW_ANY_RELEASE_BEHAVIOR:
            case GLFW.GLFW_RELEASE_BEHAVIOR_FLUSH:
            case GLFW.GLFW_RELEASE_BEHAVIOR_NONE:
                this.contextReleaseBehavior = contextReleaseBehavior;
                break;
            default:
                throw new IllegalArgumentException("Unsupported context release behavior: [" + contextReleaseBehavior + "]! Expected GLFW_ANY_RELEASE_BEHAVIOR, GLFW_RELEASE_BEHAVIOR_FLUSH, or GLFW_RELEASE_BEHAVIOR_NONE!");
        }

        if (!isTrueOrFalse.test(this.openglForwardCompat = openglForwardCompat)) {
            throw new IllegalArgumentException("Unsupported opengl forward compat parameter: [" + openglForwardCompat + "]! Expected GL_TRUE or GL_FALSE!");
        } else if (!isTrueOrFalse.test(this.openglDebugContext = openglDebugContext)) {
            throw new IllegalArgumentException("Unsupported opengl debug context parameter: [" + openglDebugContext + "]! Expected GL_TRUE or GL_FALSE!");
        }

        switch (openglProfile) {
            case GLFW.GLFW_OPENGL_ANY_PROFILE:
            case GLFW.GLFW_OPENGL_COMPAT_PROFILE:
            case GLFW.GLFW_OPENGL_CORE_PROFILE:
                this.openglProfile = openglProfile;
                break;
            default:
                throw new IllegalArgumentException("Unsupported opengl profile parameter: [" + openglProfile + "]! Expected GLFW_OPENGL_ANY_PROFILE, GLFW_OPENGL_COMPAT_PROFILE, or GLFW_OPENGL_CORE_PROFILE!");
        }
    }
}
