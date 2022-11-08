import lwjglutils.*;
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
    private int shaderProgramPost;
    private Camera camera;
    private Mat4 projection;
    private OGLTexture2D textureBase;
    private OGLTexture2D textureNormal;

    private boolean mouseButton1, mouseButton2, mouseButton3;
    private double ox, oy;

    double camSpeed = 0.25;
    float timeChange = 0;
    private Main main;

    int loc_uColorR, loc_uProj, loc_uView, loc_uSelectedModel, loc_lightMode, loc_uModel, loc_secondObj, loc_time;

    private OGLBuffers buffers;

    //Pro funkci
    private OGLBuffers buffer;
    private boolean gridModeList = true;

    int lightModeValue = 0;

    private OGLTexture2D.Viewer texture2D;
    private OGLTextRenderer textHelper;

    private int selectedModel = 0;

    private int gridM = 20; private int gridN = 20;

    Mat4 model, rotation, translation, scale;

    private int scaleVal = 0;

    private boolean orthoProjection = false;

    private int button;

    //texture setting
    private boolean texturePNG = false;

    //Second Object
    private Mat4 secondObjMove;
    private Vec3D secondObjPos;
    private int secondObjModel = 0;
    private float secondObjPosX; private float secondObjPosY;

    //Cv8 - Post processing
    private OGLRenderTarget renderTarget;

    private Grid gridPost;

    @Override
    public void init() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_DEPTH_TEST);

        buffers = Grid.gridListTriangle(gridM, gridN);

        camera = new Camera()
                .withPosition(new Vec3D(0.f, 0f, 0f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125)
                .withFirstPerson(false)
                .withRadius(3);
        projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);



        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        shaderProgramPost = ShaderUtils.loadProgram("/shaders/Post");
        glUseProgram(shaderProgramPost);

        // Color - Černevá barba
        loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColorR, 1.f);

        // Proj
        loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());


        loc_uModel = glGetUniformLocation(shaderProgram, "u_Model");

        loc_lightMode = glGetUniformLocation(shaderProgram, "lightMode");

        //Second Obj
        loc_secondObj = glGetUniformLocation(shaderProgram, "u_secondObj");
        loc_time = glGetUniformLocation(shaderProgram, "u_time");

        texture2D = new OGLTexture2D.Viewer();
        textHelper = new OGLTextRenderer(Main.getWidth(), Main.getHeight());

        try {
            textureBase = new OGLTexture2D("./textures/bricks.jpg");
            textureNormal = new OGLTexture2D("./textures/bricksn.png");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        model = new Mat4Identity();
        rotation = new Mat4Identity();
        translation = new Mat4Identity();
        scale = new Mat4Identity();

        secondObjPos = new Vec3D(5,5,5);
        secondObjMove = new Mat4Transl(secondObjPos);

        //Cv8
        gridPost = new Grid(20, 20);
        //renderTarget = new OGLRenderTarget(width, height);
    }

    @Override
    public void display() {
        // View
        loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());

        textureBase.bind(shaderProgram, "textureBase", 0);
        textureBase.bind(shaderProgram, "textureNormal", 1);

        loc_uSelectedModel = glGetUniformLocation(shaderProgram, "selectedModel");

        glUniform1i(loc_uSelectedModel, selectedModel);
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        glUniform1i(loc_lightMode, lightModeValue);

        //Model
        glUniformMatrix4fv(loc_uModel, false, ToFloatArray.convert(model));

        //Vykreslení pro modely
        buffersMode(buffers);

        //Time
        timeChange += 0.01;
        glUniform1f(loc_time, timeChange);

        //Animace
        secondObjPosX = (float) (Math.sin(timeChange));
        secondObjPosY = (float) (Math.cos(timeChange));
        secondObjPos = new Vec3D(secondObjPosX, secondObjPosY, 1);

        secondObjMove = new Mat4Transl(secondObjPos);

        glUniform1f(loc_uSelectedModel, 7);
        glUniform1f(loc_lightMode, 7);

        glUniformMatrix4fv(loc_uModel, false, ToFloatArray.convert(secondObjMove));
        //Vykreslení pro secondObject
        buffersMode(buffers);

        //renderMain();
        //renderPost();
    }

    //Cv8
    private void renderMain(){
        glUseProgram(shaderProgram);
        //renderTarget.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }

    private void renderPost(){
        glUseProgram(shaderProgramPost);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //Načíst texturu z renderTarget
        //renderTarget.getColorTexture().bind(shaderProgramPost, "textureBase",0);
        buffersMode(buffers);
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
                System.out.println(button + " mb3");  //TODO opravit
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
                        buffers = Grid.gridStripsTriangle(gridM, gridN);
                        gridModeList = false;
                        System.out.println("List");
                    }
                    case GLFW_KEY_U -> {
                        buffers = Grid.gridListTriangle(gridM, gridN);
                        gridModeList = true;
                        System.out.println("Strip");
                    }
                    //Osvětlovací model
                    case GLFW_KEY_L -> {
                        if (lightModeValue == 7 ) {
                            lightModeValue = 0; System.out.println("L " + lightModeValue);}
                        else {
                            lightModeValue++; System.out.println("L " + lightModeValue);}
                    }
                    //Objekty
                    case GLFW_KEY_M -> {
                        if (selectedModel >= 7) {selectedModel = 0 ;}
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

    private void buffersMode(OGLBuffers buffers) {
        if (gridModeList) {
            buffers.draw(GL_TRIANGLES, shaderProgram);
        } else {
            buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
        }
    }

    private void textureMode(){
        if (!texturePNG) {
            textureBase.bind(shaderProgram, "textureBase", 0);
        } else {
            textureNormal.bind(shaderProgram, "textureNormal", 0);
        }
    }
}


