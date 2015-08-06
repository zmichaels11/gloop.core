
import com.longlinkislong.gloop.CLBuffer;
import com.longlinkislong.gloop.CLProgram;
import com.longlinkislong.gloop.CLThread;
import com.longlinkislong.gloop.GLTools;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opencl.CL10;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author zmichaels
 */
public class CLSum {

    static final String source
            = "kernel void sum(global const float *a, global const float *b, global float *answer) { "
            + "  unsigned int xid = get_global_id(0); "
            + "  answer[xid] = a[xid] + b[xid];"            
            + "}";

    public static void main(String[] args) {
        final CLThread thread = CLThread.getDefaultInstance();

        final CLBuffer aMem = new CLBuffer();
        final CLBuffer bMem = new CLBuffer();
        final CLBuffer cMem = new CLBuffer();

        aMem.allocate(Float.BYTES * 10, CL10.CL_MEM_READ_ONLY)
                .upload(GLTools.wrapFloat(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f), 0L, true);

        bMem.allocate(Float.BYTES * 10, CL10.CL_MEM_READ_ONLY)
                .upload(GLTools.wrapFloat(9f, 8f, 7f, 6f, 5f, 4f, 3f, 2f, 1f, 0f), 0L, true);

        cMem.allocate(Float.BYTES * 10, CL10.CL_MEM_WRITE_ONLY);        

        final CLProgram program = new CLProgram();

        program.compile(source);

        final CLProgram.Kernel kernel = program.new Kernel("sum", 1, new long[]{10});

        kernel.setArgument(0, aMem);
        kernel.setArgument(1, bMem);
        kernel.setArgument(2, cMem);

        kernel.execute();
        
        ByteBuffer a = aMem.download(ByteBuffer.allocateDirect(Float.BYTES * 10).order(ByteOrder.nativeOrder()), 0L, true);
        ByteBuffer b = bMem.download(ByteBuffer.allocateDirect(Float.BYTES * 10).order(ByteOrder.nativeOrder()), 0L, true);
        ByteBuffer res = cMem.download(ByteBuffer.allocateDirect(Float.BYTES * 10).order(ByteOrder.nativeOrder()), 0L, true);

        thread.finish();
        
        for(int i = 0; i < 10; i++) {
            System.out.printf("%f ", a.getFloat());
        }
        System.out.println("\n+");
        
        for(int i = 0; i < 10; i++) {
            System.out.printf("%f ", b.getFloat());
        }
        System.out.println("\n=");
        
        for (int i = 0; i < 10; i++) {
            System.out.printf("%f ", res.getFloat());
        }

        System.out.println();

        kernel.delete();
        program.delete();
        cMem.delete();
        bMem.delete();
        aMem.delete();

        thread.shutdown();
    }
}
