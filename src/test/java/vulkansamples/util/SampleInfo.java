/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vulkansamples.util;

import com.longlinkislong.gloop.GLMat4F;
import com.longlinkislong.gloop.GLWindow;
import com.longlinkislong.gloop.NativeTools;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkViewport;

/**
 *
 * @author zmichaels
 */
public class SampleInfo {
    static {
        NativeTools.getInstance().loadNatives();
    }

    public GLWindow window;

    public long surface;
    public boolean prepared;
    public boolean useStagingBuffer;
    public boolean saveImages;
    public List<String> instanceLayerNames = new ArrayList<>(0);
    public List<String> instanceExtensionNames = new ArrayList<>(0);
    public List<LayerProperties> instanceLayerProperties = new ArrayList<>(0);
    public List<VkExtensionProperties> instanceExtensionProperties = new ArrayList<>(0);

    public VkInstance inst;

    public List<String> deviceExtensionNames = new ArrayList<>(0);
    public List<VkExtensionProperties> deviceExtensionProperties = new ArrayList<>(0);
    public List<VkPhysicalDevice> gpus = new ArrayList<>(0);
    public VkDevice device;
    public VkQueue queue;
    public int graphicsQueueFamilyIndex;
    public VkPhysicalDeviceProperties gpuProps;
    public List<VkQueueFamilyProperties> queueProps = new ArrayList<>(0);
    public VkPhysicalDeviceMemoryProperties memoryProperties;

    public long[] framebuffers;
    public int width;
    public int height;
    long format;

    public int swapchainImageCount;
    public long swapChain;
    public List<SwapChainBuffer> buffers = new ArrayList<>(0);
    public long imageAcquiredSemaphore;

    public long cmdPool;

    public class Depth {

        public long format;
        public long image;
        public long mem;
        public long view;
    }

    public final Depth depth = new Depth();

    public List<Texture> textures = new ArrayList<>(0);

    public class UniformData {

        public long buf;
        public long mem;
        public VkDescriptorBufferInfo bufferInfo;
    }

    public final UniformData uniformData = new UniformData();

    public class TextureData {

        public VkDescriptorImageInfo imageInfo;
    }

    public final TextureData textureData = new TextureData();

    public long stagingMemory;
    public long stagingImage;

    public class VertexBuffer {

        public long buf;
        public long mem;
        public VkDescriptorBufferInfo bufferInfo;
    }

    public VertexBuffer vertexBuffer = new VertexBuffer();

    public long viBinding;
    public VkVertexInputAttributeDescription[] viAttribs = new VkVertexInputAttributeDescription[2];

    public GLMat4F projection;
    public GLMat4F view;
    public GLMat4F model;
    public GLMat4F clip;
    public GLMat4F mvp;

    public VkCommandBuffer cmd;
    public long pipelineLayout;
    public List<Long> descLayout = new ArrayList<>(0);
    public long pipelineCache;
    public long renderPass;
    public long pipeline;

    public VkPipelineShaderStageCreateInfo[] shaderStages = new VkPipelineShaderStageCreateInfo[2];

    public long descPool;
    public List<Long> descSet = new ArrayList<>(0);

    public long dbgCreateDebugReportCallback;
    public long dbgDestroyDebugReportCallback;
    public long dbgBreakCallback;
    public List<Long> debugReportCallbacks = new ArrayList<>(0);

    public int currentBuffer;
    public int queueCount;

    public VkViewport viewport;
    public VkRect2D scissor;
}
