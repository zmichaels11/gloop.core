/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * NativeTools is a container for native data structures.
 *
 * @author zmichaels
 * @since 15.08.05
 */
public final class NativeTools {
    private static final Marker SYSTEM_MARKER = MarkerFactory.getMarker("SYSTEM");
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeTools.class);
    public static final OperatingSystem OPERATING_SYSTEM;
    public static final Architecture ARCHITECTURE;

    static {
        final String os = System.getProperty("os.name", "unknown").toLowerCase();

        if (os.contains("windows")) {
            LOGGER.trace("Windows OS detected!");
            OPERATING_SYSTEM = OperatingSystem.WINDOWS;
        } else if (os.contains("linux")) {
            LOGGER.trace("Linux OS detected!");
            OPERATING_SYSTEM = OperatingSystem.LINUX;
        } else if (os.contains("mac")) {
            LOGGER.trace("Mac OSX detected!");
            OPERATING_SYSTEM = OperatingSystem.OSX;
        } else {
            LOGGER.warn("Unknown OS: {}! Natives will not be loaded.", os);
            OPERATING_SYSTEM = OperatingSystem.UNSUPPORTED;
        }

        final String arch = System.getProperty("os.arch", "unknown").toLowerCase();

        switch (arch) {
            case "amd64":
            case "x86_64":
                LOGGER.trace("64bit runtime detected!");
                ARCHITECTURE = Architecture.X86_64;
                break;
            case "x86":
            case "i386":
                LOGGER.trace("32bit runtime detected!");
                ARCHITECTURE = Architecture.X86;
                break;
            default:
                LOGGER.warn("Unknown architecture {}! Natives will not be loaded.", arch);
                ARCHITECTURE = Architecture.UNSUPPORTED;
                break;
        }

        final String jvm = System.getProperty("java.version");

        LOGGER.debug(SYSTEM_MARKER, "JVM version: {}", jvm);        
    }

    private static final class Holder {

        private static final NativeTools INSTANCE = new NativeTools();
    }

    /**
     * Retrieves the instance of the NativeTools structure.
     *
     * @return the NativeTools.
     * @since 15.08.05
     */
    public static NativeTools getInstance() {
        return Holder.INSTANCE;
    }

    private final ByteBuffer[] wordPool = new ByteBuffer[256];  // 4 bytes    
    private final ByteBuffer[] dwordPool = new ByteBuffer[128];  // 8 bytes    
    private final ByteBuffer[] qwordPool = new ByteBuffer[64];  // 16 bytes
    private final ByteBuffer[] owordPool = new ByteBuffer[32];  // 32 bytes    
    private final ByteBuffer[] qvwordPool = new ByteBuffer[16];  // 64 bytes
    private final ByteBuffer[] ovwordPool = new ByteBuffer[8];  // 128 bytes

    private int wId;
    private int dwId;
    private int qwId;
    private int owId;
    private int qvwId;
    private int ovwId;

    /**
     * All supported Operating Systems
     *
     * @since 15.08.20
     */
    public static enum OperatingSystem {

        WINDOWS,
        LINUX,
        OSX,
        UNSUPPORTED;
    }

    /**
     * All supported architectures
     *
     * @since 15.08.20
     */
    public static enum Architecture {

        X86,
        X86_64,
        UNSUPPORTED;
    }

    private NativeTools() {
        ByteBuffer data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < wordPool.length; i++) {
            data.position(i * 4);
            wordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < dwordPool.length; i++) {
            data.position(i * 8);
            dwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < qwordPool.length; i++) {
            data.position(i * 16);
            qwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < owordPool.length; i++) {
            data.position(i * 32);
            owordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < qvwordPool.length; i++) {
            data.position(i * 64);
            qvwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < ovwordPool.length; i++) {
            data.position(i * 128);
            ovwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }
    }

    private synchronized int nextWId() {
        final int id = this.wId;

        this.wId = (id + 1) % this.wordPool.length;

        return id;
    }

    private synchronized int nextDWId() {
        final int id = this.dwId;

        this.dwId = (id + 1) % this.dwordPool.length;

        return id;
    }

    private synchronized int nextQWId() {
        final int id = this.qwId;

        this.qwId = (id + 1) % this.qwordPool.length;

        return id;
    }

    private synchronized int nextOWId() {
        final int id = this.owId;

        this.owId = (id + 1) % this.owordPool.length;

        return id;
    }

    private synchronized int nextQVWId() {
        final int id = this.qvwId;

        this.qvwId = (id + 1) % this.qvwordPool.length;

        return id;
    }

    private synchronized int nextOVWId() {
        final int id = this.ovwId;

        this.ovwId = (id + 1) % this.ovwordPool.length;

        return id;
    }

    /**
     * Retrieves the next 32bit word from the object pool.
     *
     * @return the 32bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextWord() {
        final int id = this.nextWId();
        final ByteBuffer out = this.wordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 64bit word from the object pool.
     *
     * @return the 64bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextDWord() {
        final int id = this.nextDWId();
        final ByteBuffer out = this.dwordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 128bit word from the object pool.
     *
     * @return the 128bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextQWord() {
        final int id = this.nextQWId();
        final ByteBuffer out = this.qwordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 256bit word from the object pool.
     *
     * @return the 256bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextOWord() {
        final int id = this.nextOWId();
        final ByteBuffer out = this.owordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 512bit vector word from the object pool.
     *
     * @return the 512bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextQVWord() {
        final int id = this.nextQVWId();
        final ByteBuffer out = this.qvwordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 1024bit vector word from the object pool.
     *
     * @return the 1028bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextOVWord() {
        final int id = this.nextOVWId();
        final ByteBuffer out = this.ovwordPool[id];

        out.clear();

        return out;
    }

    private String mapLibraryName(final String libraryName) {
        switch (OPERATING_SYSTEM) {
            case WINDOWS:
                switch (ARCHITECTURE) {
                    case X86:
                        LOGGER.trace(SYSTEM_MARKER, "Using library: {}32.dll", libraryName);
                        return libraryName + "32.dll";
                    case X86_64:
                        LOGGER.trace(SYSTEM_MARKER, "Using library: {}.dll", libraryName);
                        return libraryName + ".dll";
                    default:
                        throw new UnsupportedOperationException("Unsupported Windows Architecture: " + ARCHITECTURE);
                }
            case LINUX:
                switch (ARCHITECTURE) {
                    case X86:
                        LOGGER.trace(SYSTEM_MARKER, "Using library: {}32.so", libraryName);
                        return libraryName + "32.so";
                    case X86_64:
                        LOGGER.trace(SYSTEM_MARKER, "Using library: {}.so", libraryName);
                        return libraryName + ".so";
                    default:
                        throw new UnsupportedOperationException("Unsupported Linux Architecture: " + ARCHITECTURE);
                }
            case OSX:
                switch (ARCHITECTURE) {
                    case X86:
                        LOGGER.trace(SYSTEM_MARKER, "Using library: {}32.dylib", libraryName);
                        return libraryName + "32.dylib";
                    case X86_64:
                        LOGGER.trace(SYSTEM_MARKER, "Using library: {}.dylib", libraryName);
                        return libraryName + ".dylib";
                    default:
                        throw new UnsupportedOperationException("Unsupported Mac OSX Architecture: " + ARCHITECTURE);
                }
            default:
                throw new UnsupportedOperationException("Unsupported Operating System: " + OPERATING_SYSTEM);
        }
    }

    private volatile boolean isLoaded = false;

    void autoLoad() {
        if (System.getProperty("org.lwjgl.librarypath") == null) {
            try {
                this.loadNatives();
            } catch (RuntimeException ex) {
                System.err.println("Unable to autoload natives! " + ex.getMessage());
            }
        }
    }

    public synchronized void loadNatives() {
        if (isLoaded) {
            return;
        }

        final String libLWJGL = mapLibraryName(OPERATING_SYSTEM == OperatingSystem.WINDOWS ? "lwjgl" : "liblwjgl");
        final String libOpenAL = mapLibraryName(OPERATING_SYSTEM == OperatingSystem.WINDOWS ? "OpenAL" : "libopenal");
        final Path tempRoot;

        LOGGER.debug(SYSTEM_MARKER, "Loading natives: {}, {}", libLWJGL, libOpenAL);        

        try {            
            tempRoot = Files.createTempDirectory("com.longlinkislong.gloop.natives");
            LOGGER.trace(SYSTEM_MARKER, "Created temp folder: {}", tempRoot);
            tempRoot.toFile().deleteOnExit();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to create temp directory!", ex);
        }

        try (final InputStream inLibLWJGL = NativeTools.class.getResourceAsStream("/" + libLWJGL)) {
            final Path pLibLWJGL = tempRoot.resolve(Paths.get(libLWJGL));

            LOGGER.trace(SYSTEM_MARKER, "Resolving {} as {}", libLWJGL, NativeTools.class.getResource("/" + libLWJGL));
            Files.copy(inLibLWJGL, pLibLWJGL);
            pLibLWJGL.toFile().deleteOnExit();
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Unable to copy [%s] to temp directory!", libLWJGL), ex);
        }

        try (final InputStream inLibOpenAL = NativeTools.class.getResourceAsStream("/" + libOpenAL)) {
            final Path pLibOpenAL = tempRoot.resolve(Paths.get(libOpenAL));            

            LOGGER.trace(SYSTEM_MARKER, "Resolving {} as {}", libOpenAL, NativeTools.class.getResource("/" + libOpenAL));
            Files.copy(inLibOpenAL, pLibOpenAL);
            pLibOpenAL.toFile().deleteOnExit();
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Unable to copy [%s] to temp directory!", libOpenAL), ex);
        }

        System.setProperty("org.lwjgl.librarypath", tempRoot.toString());
        LOGGER.trace(SYSTEM_MARKER, "Set system property: org.lwjgl.librarypath={}", System.getProperty("org.lwjgl.librarypath"));

        this.isLoaded = true;
    }
}
