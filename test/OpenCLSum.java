
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_GPU;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.CL_MEM_WRITE_ONLY;
import static org.lwjgl.opencl.CL10.CL_QUEUE_PROFILING_ENABLE;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clEnqueueReadBuffer;
import static org.lwjgl.opencl.CL10.clEnqueueWriteBuffer;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clReleaseCommandQueue;
import static org.lwjgl.opencl.CL10.clReleaseContext;
import static org.lwjgl.opencl.CL10.clReleaseKernel;
import static org.lwjgl.opencl.CL10.clReleaseMemObject;
import static org.lwjgl.opencl.CL10.clReleaseProgram;
import static org.lwjgl.opencl.CL10.clSetKernelArg1p;
import org.lwjgl.opencl.CLCreateContextCallback;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLUtil;
import static org.lwjgl.opencl.CLUtil.checkCLError;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memDecodeUTF8;

public class OpenCLSum {

    // The OpenCL kernel
    static final String source
            = "kernel void sum(global const float *a, global const float *b, global float *answer) { "
            + "  unsigned int xid = get_global_id(0); "
            + "  answer[xid] = a[xid] + b[xid];"
            + "}";

    // Data buffers to store the input and result data in
    static final FloatBuffer a = toFloatBuffer(new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f});
    static final FloatBuffer b = toFloatBuffer(new float[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
    static final FloatBuffer answer = BufferUtils.createFloatBuffer(a.capacity());

    private static final CLCreateContextCallback CREATE_CONTEXT_CALLBACK = new CLCreateContextCallback() {
        @Override
        public void invoke(long errinfo, long private_info, long cb, long user_data) {
            System.err.println("[LWJGL] cl_create_context_callback");
            System.err.println("\tInfo: " + memDecodeUTF8(errinfo));
        }
    };

    public static void main(String[] args) throws Exception {

        System.setProperty("org.lwjgl.opencl.explicitInit", "true");

        // Initialize OpenCL and create a context and command queue
        CL.create();
        System.out.println("CL created");

        CLPlatform platform = CLPlatform.getPlatforms().get(0);
        System.out.println("Platform created");

        PointerBuffer ctxProps = BufferUtils.createPointerBuffer(3);
        ctxProps.put(CL_CONTEXT_PLATFORM).put(platform).put(0).flip();
        System.out.println("CTX created");

        IntBuffer errcode_ret = BufferUtils.createIntBuffer(1);
        System.out.println("ERRCODE created");

        List<CLDevice> devices = platform.getDevices(CL_DEVICE_TYPE_GPU);

        // long context = clCreateContext(platform, devices, null, null, null);
        long context = clCreateContext(ctxProps, devices.get(0).getPointer(), CREATE_CONTEXT_CALLBACK, NULL, errcode_ret);
        System.out.println("CONTEXT created");

        checkCLError(errcode_ret);
        //CLCommandQueue queue = clCreateCommandQueue(context, devices.get(0), CL_QUEUE_PROFILING_ENABLE, null);
        long queue = clCreateCommandQueue(context, devices.get(0).getPointer(), CL_QUEUE_PROFILING_ENABLE, errcode_ret);
        System.out.println("Command Q created");

        // Allocate memory for our two input buffers and our result buffer
        //long aMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, a, null);        
        //long buffer = clCreateBuffer(context, CL_MEM_READ_ONLY, 128, errcode_ret);
        long aMem = CL10.clCreateBuffer(context, CL_MEM_READ_ONLY, 40, errcode_ret);
        
        System.out.println("A Buffer created");        
        clEnqueueWriteBuffer(queue, aMem, 1, 0, a, null, null);
        //long bMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, b, null);
        long bMem = CL10.clCreateBuffer(context, CL_MEM_READ_ONLY, 40, errcode_ret);
        
        System.out.println("B Buffer created");
        clEnqueueWriteBuffer(queue, bMem, 1, 0, b, null, null);
        //long answerMem = clCreateBuffer(context, CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, answer, null);
        long answerMem = CL10.clCreateBuffer(context, CL_MEM_WRITE_ONLY, 40, errcode_ret);
        System.out.println("OUTPUT Buffer created");
        clFinish(queue);

        // Create our program and kernel
        long program = clCreateProgramWithSource(context, source, null);
        System.out.println("PROGRAM created");

        //public static int clBuildProgram(long program, long device, CharSequence options, CLProgramCallback pfn_notify, long user_data) {
        CLUtil.checkCLError(clBuildProgram(program, devices.get(0).getPointer(), "", null, 0L));
        // sum has to match a kernel method name in the OpenCL source
        long kernel = clCreateKernel(program, "sum", null);
        System.out.println("KERNEL created");

        // Execution our kernel
        //PointerBuffer kernel1DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
        ByteBuffer kernel1DGlobalWorkSize = ByteBuffer.allocateDirect(Long.BYTES).order(ByteOrder.nativeOrder());
        System.out.println("KERNEL work size created");
        kernel1DGlobalWorkSize.putLong(0, a.capacity());
        System.out.println("KERNEL work size copied");

        clSetKernelArg1p(kernel, 0, aMem);
        clSetKernelArg1p(kernel, 1, bMem);
        clSetKernelArg1p(kernel, 2, answerMem);

        System.out.println("Args send to kernel");

        //clEnqueueNDRangeKernel(queue, kernel, 1, null, kernel1DGlobalWorkSize, null, null, null);
        CL10.clEnqueueNDRangeKernel(queue, kernel, 1, null, kernel1DGlobalWorkSize, null, 0, null, null);
        System.out.println("KERNEL queued created");

        // Read the results memory back into our result buffer
        clEnqueueReadBuffer(queue, answerMem, 1, 0, answer, null, null);
        System.out.println("and output ... created");

        clFinish(queue);
        // Print the result memory
        print(a);        
        System.out.println("+");
        print(b);
        System.out.println("=");
        print(answer);

        // Clean up OpenCL resources
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseMemObject(aMem);
        clReleaseMemObject(bMem);
        clReleaseMemObject(answerMem);
        clReleaseCommandQueue(queue);
        clReleaseContext(context);
        CL.destroy();
    }

    /**
     * Utility method to convert float array to float buffer
     *
     * @param floats - the float array to convert
     * @return a float buffer containing the input float array
     */
    static FloatBuffer toFloatBuffer(float[] floats) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(floats.length).put(floats);
        buf.rewind();
        return buf;
    }

    /**
     * Utility method to print a float buffer
     *
     * @param buffer - the float buffer to print to System.out
     */
    static void print(FloatBuffer buffer) {
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.print(buffer.get(i) + " ");
        }
        System.out.println("");
    }

}
