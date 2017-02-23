/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.Future;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public final class GLContextBuilder {
    public static enum ClientAPI {
        OPENGL,
        OPENGLES,
        OTHER
    }
    
    public final ClientAPI clientAPI;
    
    public static final class VersionInfo {
        public final int major;
        public final int minor;
        
        public VersionInfo() {
            this(1, 0);
        }
        
        public VersionInfo(final int major, final int minor) {
            this.major = major;
            this.minor = minor;
        }
        
        public VersionInfo withMajor(final int major) {
            return new VersionInfo(major, minor);
        }
        
        public VersionInfo withMinor(final int minor) {
            return new VersionInfo(major, minor);
        }
    }
    
    public final VersionInfo versionInfo;
    public final int refreshRate;
    public final int swapInterval;
    public final int samples;
    
    public static final class SurfaceInfo {
        public final int redBits;
        public final int greenBits;
        public final int blueBits;
        public final int alphaBits;
        public final int depthBits;
        public final int stencilBits;
        
        public SurfaceInfo(final int redBits, final int greenBits, final int blueBits, final int alphaBits, final int depthBits, final int stencilBits) {
            this.redBits = redBits;
            this.greenBits = greenBits;
            this.blueBits = blueBits;
            this.alphaBits = alphaBits;
            this.depthBits = depthBits;
            this.stencilBits = stencilBits;
        }
        
        public SurfaceInfo() {
            this(8, 8, 8, 8, 24, 8);
        }
        
        public SurfaceInfo withRGBABits(final int redBits, final int greenBits, final int blueBits, final int alphaBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withDepthStencilBits(final int depthBits, final int stencilBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withRedBits(final int redBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withGreenBits(final int greenBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withBlueBits(final int blueBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withAlphaBits(final int alphaBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withDepthBits(final int depthBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
        
        public SurfaceInfo withStencilBits(final int stencilBits) {
            return new SurfaceInfo(redBits, greenBits, blueBits, alphaBits, depthBits, stencilBits);
        }
    }
    
    public final SurfaceInfo surfaceInfo;
    
    public static final class WindowInfo {
        public final boolean hidden;
        public final boolean resizable;
        public final int initialWidth;
        public final int initialHeight;
        public final String title;
        public final String monitorName;
        
        public WindowInfo(final boolean hidden, final int initialWidth, final int initialHeight, final String title, final String monitorName, final boolean resizable) {
            this.hidden = hidden;
            this.initialWidth = initialWidth;
            this.initialHeight = initialHeight;
            this.title = title;
            this.monitorName = monitorName;
            this.resizable = resizable;
        }
        
        public WindowInfo() {
            this(true, 640, 480, "GLWindow", "", true);
        }
        
        public WindowInfo withInitiallyHidden(final boolean hidden) {
            return new WindowInfo(hidden, initialWidth, initialHeight, title, monitorName, resizable);
        }
        
        public WindowInfo withInitialSize(final int initialWidth, final int initialHeight) {
            return new WindowInfo(hidden, initialWidth, initialHeight, title, monitorName, resizable);
        }
        
        public WindowInfo withTitle(final String title) {
            return new WindowInfo(hidden, initialWidth, initialHeight, title, monitorName, resizable);
        }
        
        public WindowInfo withMonitorName(final String monitorName) {
            return new WindowInfo(hidden, initialWidth, initialHeight, title, monitorName, resizable);
        }
        
        public WindowInfo withResizable(final boolean resizable) {
            return new WindowInfo(hidden, initialWidth, initialHeight, title, monitorName, resizable);
        }
    }
    
    public final WindowInfo windowInfo;    
    public final boolean useEGL;
    
    
    public GLContextBuilder(
        final ClientAPI clientAPI,
            final VersionInfo versionInfo,
            final int refreshRate, final int swapInterval, final int samples,
            final SurfaceInfo surfaceInfo,
            final WindowInfo windowInfo,
            final boolean useEGL) {
        
        this.clientAPI = clientAPI;
        this.versionInfo = versionInfo;
        this.refreshRate = refreshRate;
        this.swapInterval = swapInterval;
        this.samples = samples;
        this.surfaceInfo = surfaceInfo;
        this.windowInfo = windowInfo;
        this.useEGL = useEGL;
    }
    
    public GLContextBuilder() {
        this(ClientAPI.OPENGL, new VersionInfo(), -1, 1, -1, new SurfaceInfo(), new WindowInfo(), false);
    }
    
    public GLContextBuilder withClientAPI(final ClientAPI clientAPI) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
    
    public GLContextBuilder withVersionInfo(final VersionInfo versionInfo) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
    
    public GLContextBuilder withRefreshRate(final int refreshRate) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
    
    public GLContextBuilder withSwapInterval(final int swapInterval) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
    
    public GLContextBuilder withSamples(final int samples) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
    
    public GLContextBuilder withSurfaceInfo(final SurfaceInfo surfaceInfo) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
    
    public GLContextBuilder withWindowInfo(final WindowInfo windowInfo) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }   
    
    public GLContextBuilder withUseEGL(final boolean useEGL) {
        return new GLContextBuilder(clientAPI, versionInfo, refreshRate, swapInterval, samples, surfaceInfo, windowInfo, useEGL);
    }
        
    public Future<GLThread> build() {
        return build(GLThread.getDefaultInstance());
    }
    
    public Future<GLThread> build(final GLThread parent) {
        final GLThread thread = GLThread.create();
                
        return thread.submit(() -> {
            GLFW.glfwDefaultWindowHints();
            
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, windowInfo.hidden ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, windowInfo.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, versionInfo.major);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, versionInfo.minor);
            
            if (useEGL) {
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, GLFW.GLFW_EGL_CONTEXT_API);
            }
            
            switch (this.clientAPI) {
                case OPENGL:
                    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
                    
                    if (versionInfo.major == 3) {
                        if (versionInfo.minor == 2 || versionInfo.minor == 3) {
                            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
                        }
                    } else if (versionInfo.major > 3) {
                        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
                    }
                    break;
                case OPENGLES:
                    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_ES_API);
                    break;                
                default:
                    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
            }
            
            GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, this.samples);
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, this.refreshRate);            
            
            GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, this.surfaceInfo.redBits);
            GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, this.surfaceInfo.blueBits);
            GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, this.surfaceInfo.greenBits);
            GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, this.surfaceInfo.alphaBits);
           
            GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, this.surfaceInfo.stencilBits);
            GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, this.surfaceInfo.depthBits);
            
            final long selectedMonitor;
            if (this.windowInfo.monitorName != null && !this.windowInfo.monitorName.isEmpty()) {
                final PointerBuffer pMonitors = GLFW.glfwGetMonitors();
                long monitor = 0L;
                boolean monitorFound = false;
                
                while(pMonitors.hasRemaining()) {
                    final long pMonitor = pMonitors.get();
                    final String monitorName = GLFW.glfwGetMonitorName(pMonitor);
                    
                    if (monitorName.equalsIgnoreCase(this.windowInfo.monitorName)) {
                        monitor = pMonitor;
                        monitorFound = true;
                        break;
                    }                                        
                }
                
                if (monitorFound) {
                    selectedMonitor = monitor;
                } else {
                    selectedMonitor = GLFW.glfwGetPrimaryMonitor();
                }
            } else {
                selectedMonitor = GLFW.glfwGetPrimaryMonitor();
            }
            
            final long sharedCtx = (parent != null) ? parent.pHandle : 0L;
            final long handle = GLFW.glfwCreateWindow(
                    this.windowInfo.initialWidth, this.windowInfo.initialHeight, 
                    this.windowInfo.title, 
                    selectedMonitor, sharedCtx);
            
            GLFW.glfwMakeContextCurrent(handle);
            GLFW.glfwSwapBuffers(this.swapInterval);
            
            final int[] pFbWidth = {0};
            final int[] pFbHeight = {0};
            
            GLFW.glfwGetFramebufferSize(handle, pFbWidth, pFbHeight);
            
            thread.currentViewport = new GLViewport(0, 0, pFbWidth[0], pFbHeight[0]);
            thread.pHandle = handle;            
        }, thread);               
    }
}
