import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.event.*;
import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer{
    private int shaderProgram;

    private Camera camera;
    private Mat4 projection;

    private OGLTexture2D texture, textureNormale, textureParalax;
    OGLTexture2D.Viewer textureViewer;


    private boolean orthoProjection = false;

    private OGLTextRenderer txtRenderer;

    //
    int width, height, axisX, axisY;
    private OGLBuffers buffers;

    double camSpeed = 0.50;
    float time = 0;

    private boolean mouseButton1 = false;
    private double ox, oy;




    @Override
    public void init() {
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        glEnable(GL_DEPTH_TEST);

        camera = new Camera()
                .withPosition(new Vec3D(0.f, 0f, 0f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * - 0.125)
                .withFirstPerson(false)
                .withRadius(3);

        projection = new Mat4PerspRH(Math.PI / 3, 600 / (float)800, 0.1f, 50.f);

        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        // Color
        int loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColorR, 1.f);
        // View
        int loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());
        // Proj
        int loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());


        // Mapování textur
        try {
            texture = new OGLTexture2D("./textures/bricks.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            textureNormale = new OGLTexture2D("./textures/hypnotic.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            textureParalax = new OGLTexture2D("./textures/rocks.jpeg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textureViewer = new OGLTexture2D.Viewer();



        //grid = new Grid();
        //
        buffers = Grid.gridListTriangle(10, 10);  //Funkční
        //buffers = Grid.gridStripsTriangle(10, 10);  //Funkční

        //txtRenderer = new OGLTextRenderer(width, height);
        txtRenderer = new OGLTextRenderer(800, 600);

        txtRenderer.addStr2D(10, 10, "Sakač");
    }


    @Override
    public void display() {
        texture.bind();
        //grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);
        //
        buffers.draw(GL_TRIANGLES, shaderProgram);  //Funkční
        //buffers.draw(GL_TRIANGLE_STRIP,shaderProgram);  //Funkční
    }

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
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    protected GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
            if (dy < 0)
            {
                //TODO

            }

        }
    };

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }


    protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if (action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key){
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
                        camera = camera.up(1);
                        System.out.println("D");
                    }
                    case GLFW_KEY_LEFT_CONTROL -> {
                        camera = camera.down(1);
                        System.out.println("D");
                    }
                    // Perspektivní a ortogonální projekce
                    case GLFW_KEY_P -> camera = camera.withFirstPerson(!camera.getFirstPerson());

                }
            }


        }
    };



    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }


    public GLFWCursorPosCallback getCpCallbacknew() {
        return cpCallbacknew;
    }

    public void setCpCallbacknew(GLFWCursorPosCallback cpCallbacknew) {
        this.cpCallbacknew = cpCallbacknew;
    }

    public GLFWMouseButtonCallback getMbCallback() {
        return mbCallback;
    }

    public void setMbCallback(GLFWMouseButtonCallback mbCallback) {
        this.mbCallback = mbCallback;
    }
}

