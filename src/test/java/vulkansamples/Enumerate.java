/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vulkansamples;

import com.longlinkislong.gloop.GLWindow;
import java.nio.IntBuffer;
import org.junit.Test;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import vulkansamples.util.InitUtils;
import vulkansamples.util.SampleInfo;
import vulkansamples.util.VkResult;

/**
 *
 * @author zmichaels
 */
public class Enumerate {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("com.longlinkislong.gloop.client_api", "Vulkan");
    }

    @Test
    public void test() {
        final SampleInfo info = new SampleInfo();
        
        info.window = new GLWindow(640, 480, "vulkansamples_enumerate");

        VK.create();
        
        InitUtils.initGlobalLayerProperties(info);
        InitUtils.initInstance(info, "vulkansamples_enumerate");

        try (MemoryStack mem = MemoryStack.stackPush()) {
            final IntBuffer pGPUCount = mem.callocInt(1);

            VkResult res = VkResult.of(VK10.vkEnumeratePhysicalDevices(info.inst, pGPUCount, null)).get();

            assert pGPUCount.get(0) > 0;

            final PointerBuffer ppGPUs = mem.callocPointer(pGPUCount.get(0));


            res = VkResult.of(VK10.vkEnumeratePhysicalDevices(info.inst, pGPUCount, ppGPUs)).get();

            assert (res == VkResult.SUCCESS && pGPUCount.get(0) >= 1);

            VK10.vkDestroyInstance(info.inst, null);
        }
    }
}
