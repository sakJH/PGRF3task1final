import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.*;

import java.awt.event.KeyEvent;
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
    private boolean mouseButton1, mouseButton2, mouseButton3;
    private double ox, oy;

    double camSpeed = 0.25;
    float time = 0;
    private Main main;

    int loc_uColorR, loc_uProj, loc_uView, loc_uSelectedModel, loc_lightMode, loc_uModel;

    private OGLBuffers buffers;
    int lightModeValue = 0;

    private OGLTexture2D.Viewer texture2D;
    private OGLTextRenderer textHelper;

    private int selectedModel = 0;

    private int gridM = 20; private int gridN = 20;

    Mat4 model, rotation, translation, scale;

    private int scaleVal = 0;

    private boolean orthoProjection = false;

    private int button;

    @Override
    public void init() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_DEPTH_TEST);

        buffers = Grid.gridListTriangle(gridM, gridN);
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


        loc_uModel = glGetUniformLocation(shaderProgram, "u_Model");


        loc_lightMode = glGetUniformLocation(shaderProgram, "lightMode");

        texture2D = new OGLTexture2D.Viewer();
        textHelper = new OGLTextRenderer(Main.getWidth(), Main.getHeight());

        try {
            texture = new OGLTexture2D("./textures/bricks.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model = new Mat4Identity();
        rotation = new Mat4Identity();
        translation = new Mat4Identity();
        scale = new Mat4Identity();


    }

    @Override
    public void display() {
        // View
        loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());

        texture.bind();

        loc_uSelectedModel = glGetUniformLocation(shaderProgram, "selectedModel");

        //TODO - Vybrání modelů (6x)
        glUniform1i(loc_uSelectedModel, selectedModel);
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());


        //TODO - specular spožku
        glUniform1i(loc_lightMode, lightModeValue);

        //Model
        glUniformMatrix4fv(loc_uModel, false, ToFloatArray.convert(model));


        //TODO
        //texture2D.view();

        //textHelper.addStr2D(5, 15, "Task 1");

        buffers.draw(GL_TRIANGLES, shaderProgram);
    }

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                if (button == GLFW_MOUSE_BUTTON_LEFT)
                {
                    //Pohyb s kamerou
                    camera = camera.addAzimuth((double) Math.PI * (ox - x) / Main.getWidth())
                            .addZenith((double) Math.PI * (oy - y) / Main.getWidth());
                    ox = x;
                    oy = y;
                }
            }
            //
            if (mouseButton2)
            {
                if (button == GLFW_MOUSE_BUTTON_1)
                {
                    System.out.println("Right Mouse rotation");
                    double rotX = (ox - x) / 50.0;
                    double rotY = (oy - y) / 50.0;
                    rotation = rotation.mul(new Mat4RotXYZ(rotX, 0, rotY));
                    model = rotation.mul(translation);
                    ox = x;
                    oy = y;
                }
            }
            if (mouseButton3)
            {
                System.out.println(mouseButton3 + " mb3");
                System.out.println(button + " mb3");  //TODO opravit -> button == 0, ta ale GLFW_MOUSE_BUTTON_... neexistuje!!!
                System.out.println(GLFW_MOUSE_BUTTON_MIDDLE);
                //Translation
                if (button == GLFW_MOUSE_BUTTON_2)
                {
                    System.out.println("middle §§§!!!!!§§§");
                    double trX = (ox - x) / 50;
                    double trY = (oy - y) / 50;
                    translation = translation.mul(new Mat4Transl(trX, trY, 0));
                    model = rotation.mul(translation);
                    ox = x;
                    oy = y;
                }
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
            mouseButton2 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS;
            mouseButton3 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_MIDDLE) == GLFW_PRESS;
            //Left Mouse
            if (button==GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE){
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
            //Right Mouse
            if (button==GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS){
                mouseButton2 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }
            if (button==GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_RELEASE){
                mouseButton2 = false;
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
            //Midle Mouse
            if (button==GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW_PRESS){
                mouseButton3 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }
            if (button==GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW_RELEASE){
                mouseButton3 = false;
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

                    //TODO - reset
                    case GLFW_KEY_R -> {
                        model = new Mat4Identity();
                        rotation = new Mat4Identity();
                        translation = new Mat4Identity();
                    }

                    // Perspektivní a ortogonální projekce
                    //case GLFW_KEY_P -> camera = camera.withFirstPerson(!camera.getFirstPerson());
                    case GLFW_KEY_P -> {
                        projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);
                        System.out.println("P");
                    }
                    case GLFW_KEY_O-> {
                        projection = new Mat4OrthoRH(2.3, 2.3, 0.1, 20);
                        System.out.println("O");
                    }

                    //Scale
                    case GLFW_KEY_Z -> {
                        projection = projection.mul(new Mat4Scale(0.9,0.9,0.9));
                        System.out.println("scale --");
                    }
                    case GLFW_KEY_X -> {
                        projection = projection.mul(new Mat4Scale(1.1,1.1,1.1));
                        System.out.println("scale++");
                    }

                    //List / Strip
                    case GLFW_KEY_I -> {
                        buffers = Grid.gridListTriangle(gridM, gridM);
                        buffers.draw(GL_TRIANGLES, shaderProgram);
                    }
                    //TODO -> Problém - zůstává GL_TRIANGLES (asi)
                    case GLFW_KEY_U -> {
                        buffers = Grid.gridStripsTriangle(gridM, gridN);
                        buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
                    }

                    //Osvětlovací model
                    case GLFW_KEY_L -> {
                        if (lightModeValue == 3 ) {
                            lightModeValue = 0; System.out.println("L " + lightModeValue);}
                        else {
                            lightModeValue++; System.out.println("L " + lightModeValue);}
                    }
                    //Objekty
                    case GLFW_KEY_M -> {
                        if (selectedModel >= 6) {selectedModel = 0 ;}
                        selectedModel++;

                        System.out.println("Object " + selectedModel);
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


    private void setProjection(boolean ortho) {
        if(ortho) {
            projection = new Mat4OrthoRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);
            return;
        }
        projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.01f, 50.f);
    }
}


