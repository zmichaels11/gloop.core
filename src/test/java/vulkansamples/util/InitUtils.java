/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vulkansamples.util;

import java.nio.IntBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkLayerProperties;

/**
 *
 * @author zmichaels
 */
public final class InitUtils {

    public static VkResult initGlobalExtensionProperties(final LayerProperties layerProps) {
        VkResult res;
        final String layerName = layerProps.properties.layerNameString();

        do {
            try (final MemoryStack mem = MemoryStack.stackPush()) {
                final IntBuffer pInstanceExtensionCount = mem.callocInt(1);

                res = VkResult.of(VK10.vkEnumerateInstanceExtensionProperties(layerName, pInstanceExtensionCount, null)).get();

                if (res != VkResult.SUCCESS) {
                    if (res.isError()) {
                        throw res.toException().get();
                    } else {
                        return res;
                    }
                }

                final int instanceExtensionCount = pInstanceExtensionCount.get(0);

                if (instanceExtensionCount == 0) {
                    return VkResult.SUCCESS;
                }

                layerProps.extensions.free();
                layerProps.extensions = VkExtensionProperties.calloc(instanceExtensionCount);

                res = VkResult.of(VK10.vkEnumerateInstanceExtensionProperties(layerName, pInstanceExtensionCount, layerProps.extensions)).get();
            }
        } while (res == VkResult.INCOMPLETE);

        return res;
    }

    public static VkResult initGlobalLayerProperties(final SampleInfo info) {
        VkLayerProperties.Buffer vkProps = VkLayerProperties.calloc(0);
        VkResult res;
        int instanceLayerCount;

        do {
            try (final MemoryStack mem = MemoryStack.stackPush()) {
                final IntBuffer pInstanceLayerCount = mem.callocInt(1);

                res = VkResult.of(VK10.vkEnumerateInstanceLayerProperties(pInstanceLayerCount, null)).get();

                if (res != VkResult.SUCCESS) {
                    if (res.isError()) {
                        throw res.toException().get();
                    } else {
                        return res;
                    }
                }

                instanceLayerCount = pInstanceLayerCount.get();

                vkProps.free();
                vkProps = VkLayerProperties.calloc(instanceLayerCount);

                res = VkResult.of(VK10.vkEnumerateInstanceLayerProperties(pInstanceLayerCount, vkProps)).get();
            }
        } while (res == VkResult.INCOMPLETE);

        for (int i = 0; i < instanceLayerCount; i++) {
            final LayerProperties layerProps = new LayerProperties(vkProps.get(i));

            res = initGlobalExtensionProperties(layerProps);

            if (res != VkResult.SUCCESS) {
                if (res.isError()) {
                    throw res.toException().get();
                } else {
                    return res;
                }
            }

            info.instanceLayerProperties.add(layerProps);
        }

        //TODO: determine if this kills all the vkProps
        vkProps.free();
        return res;
    }

    public static VkResult initInstance(final SampleInfo info, final String appShortName) {
        try (final MemoryStack mem = MemoryStack.stackPush()) {
            final VkApplicationInfo appInfo = VkApplicationInfo.callocStack(mem)
                    .sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pNext(NULL)
                    .pApplicationName(mem.ASCII(appShortName))
                    .applicationVersion(1)
                    .pEngineName(mem.ASCII(appShortName))
                    .engineVersion(1)
                    .apiVersion(VK10.VK_API_VERSION_1_0);

            final PointerBuffer ppNames = mem.callocPointer(info.instanceLayerNames.size());

            info.instanceLayerNames.stream()
                    .map(mem::ASCII)
                    .forEach(ppNames::put);

            ppNames.flip();

            final PointerBuffer ppExtensions = mem.callocPointer(info.instanceExtensionNames.size());

            info.instanceExtensionNames.stream()
                    .map(mem::ASCII)
                    .forEach(ppExtensions::put);

            ppExtensions.flip();

            final VkInstanceCreateInfo instInfo = VkInstanceCreateInfo.callocStack(mem)
                    .sType(VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(NULL)
                    .flags(0)
                    .pApplicationInfo(appInfo)
                    .ppEnabledLayerNames(ppNames)
                    .ppEnabledExtensionNames(ppExtensions);

            final PointerBuffer inst = mem.callocPointer(1);

            inst.put(info.inst.address()).flip();

            final VkResult res = VkResult.of(VK10.vkCreateInstance(instInfo, null, inst)).get();

            assert res == VkResult.SUCCESS;

            return res;
        }
    }

    private InitUtils() {
    }
}
