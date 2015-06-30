
import com.longlinkislong.gloop.GLBlendFunc;
import com.longlinkislong.gloop.GLBlending;
import com.longlinkislong.gloop.GLClear;
import com.longlinkislong.gloop.GLClearBufferMode;
import com.longlinkislong.gloop.GLEnableStatus;
import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLWindow;

public class HelloWorld {

    private final GLWindow window;

    public HelloWorld() {
        window = new GLWindow(640, 480, "Hello GLOOP!");
        window.setVisible(true); // lazily show window before we draw to it!

        final GLClear clear = new GLClear(window.getGLThread(), 0.5f, 0.5f, 0.5f, 0f, 1.0);
        final GLBlending blend = new GLBlending()
                .withEnabled(GLEnableStatus.GL_ENABLED)
                .withBlendFunc(
                        GLBlendFunc.GL_SRC_ALPHA, GLBlendFunc.GL_ONE_MINUS_SRC_ALPHA,
                        GLBlendFunc.GL_SRC_ALPHA, GLBlendFunc.GL_ONE_MINUS_SRC_ALPHA);

        // set the clear color and enable blending (runs on the openGL thread)
        clear.applyClear();
        blend.applyBlending();

        final GLTask drawTask = GLTask.join(
                clear.new ClearTask(GLClearBufferMode.GL_COLOR_BUFFER_BIT),
                GLTask.create(this::draw),
                window.new UpdateTask()
        );

        window.getGLThread().scheduleGLTask(drawTask);
    }

    public void draw() {
        // draw here
    }

    public static void main(String[] args) {
        new HelloWorld();
    }

}
