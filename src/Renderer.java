import lwjglutils.OGLBuffers;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private int shaderProgram;
    //private Grid grid;

    private Camera camera;
    private Mat4 projection;
    private OGLTexture2D texture;
    private boolean mouseButton1;
    private double ox, oy;

    double camSpeed = 0.25;
    float time = 0;
    private Main main;

    int loc_uColorR, loc_uProj, loc_uView, loc_uSelectedModel, loc_lightMode;

    OGLBuffers buffers;
    int lightModeValue = 0;

    @Override
    public void init() {
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_DEPTH_TEST);

        buffers = Grid.gridListTriangle(20, 20);
        //buffers = Grid.gridStripsTriangle(20,20);


        camera = new Camera()
                .withPosition(new Vec3D(0.f, 0f, 0f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125)
                .withFirstPerson(false)
                .withRadius(3);
        projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);


        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        // Color
        loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColorR, 1.f);
        // Proj
        loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        loc_lightMode = glGetUniformLocation(shaderProgram, "lightMode");

        //loc_uSelectedModel = glGetUniformLocation(shaderProgram, "selectedModel");

        //grid = new Grid(20, 20);

        try {
            texture = new OGLTexture2D("./textures/bricks.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void display() {
        // View
        loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());

        texture.bind();
        //grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);

        loc_uSelectedModel = glGetUniformLocation(shaderProgram, "selectedModel");

        //TODO - Vybrání modelů (6x)
        glUniform1i(loc_uSelectedModel, 1);
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        //buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);

        glUniform1i(loc_uSelectedModel, 2); //
        glUniformMatrix4fv(loc_uProj, false, new Mat4Scale(10).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);

        //TODO Něco špatně   L3 -> specular spožku neřešíme -> To jsme měli dodělat???
        glUniform1i(loc_lightMode, lightModeValue);

    }

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / Main.getWidth())
                        .addZenith((double) Math.PI * (oy - y) / Main.getWidth());
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / Main.getWidth())
                        .addZenith((double) Math.PI * (oy - y) / Main.getWidth());
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0)
                camera = camera.mulRadius(1.1f);
            else
                camera = camera.mulRadius(0.9f);
        }
    };

    protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_W -> {
                        camera = camera.forward(camSpeed);
                        System.out.println("S");
                    }
                    case GLFW_KEY_S -> {
                        camera = camera.backward(camSpeed);
                        System.out.println("S");
                    }
                    case GLFW_KEY_A -> {
                        camera = camera.left(camSpeed);
                        System.out.println("A");
                    }
                    case GLFW_KEY_D -> {
                        camera = camera.right(camSpeed);
                        System.out.println("D");
                    }
                    //Up, Down
                    case GLFW_KEY_LEFT_SHIFT -> {
                        camera = camera.up(camSpeed);
                        System.out.println("L-Shift");
                    }
                    case GLFW_KEY_LEFT_CONTROL -> {
                        camera = camera.down(camSpeed);
                        System.out.println("L-CTRL");
                    }
                    // Perspektivní a ortogonální projekce
                    //case GLFW_KEY_P -> camera = camera.withFirstPerson(!camera.getFirstPerson());
                    case GLFW_KEY_Q -> {
                        projection = new Mat4PerspRH(
                                Math.PI / 3,
                                height / (float) width,
                                0.1,
                                20
                        );
                        System.out.println("Q");
                    }
                    case GLFW_KEY_E-> {
                        projection = new Mat4OrthoRH(2.5, 2.5, 0.1, 20);
                        System.out.println("E");
                    }
                    //Osvětlovací model
                    case GLFW_KEY_L -> {
                        if (lightModeValue == 3 ) {
                            lightModeValue = 0; System.out.println("L " + lightModeValue);}
                        else {
                            lightModeValue++; System.out.println("L " + lightModeValue);}
                    }
                    //Objekty
                    case GLFW_KEY_1 -> {

                    }
                }
            }
        }
    };

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }
}

