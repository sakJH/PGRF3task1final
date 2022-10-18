import lwjglutils.*;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.event.*;
import java.io.IOException;

import static org.lwjgl.opengl.GL33.*;

public class Renderer implements MouseListener, MouseMotionListener, KeyListener {
    private int shaderProgram;
    private Grid grid;
    private Main main;

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


    public Renderer () {
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        camera = new Camera()
                .withPosition(new Vec3D(0.5f, -2f, 1.5f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));

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
        
        txtRenderer = new OGLTextRenderer(width, height);
        txtRenderer.addStr2D(10, 10, "Sakač");
    }

    public void draw() {
        texture.bind();
        //grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);
        //
        buffers.draw(GL_TRIANGLES, shaderProgram);  //Funkční
        //buffers.draw(GL_TRIANGLE_STRIP,shaderProgram);  //Funkční
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            // WSAD
            /*case GLFW_KEY_W:
                camera = camera.forward(camSpeed);
                System.out.println("W");*/
            case KeyEvent.VK_W -> {
                camera = camera.forward(camSpeed);
                System.out.println("W");
            }
            case KeyEvent.VK_S -> {
                camera = camera.backward(camSpeed);
                System.out.println("S");
            }
            case KeyEvent.VK_A -> {
                camera = camera.left(camSpeed);
                System.out.println("A");
            }
            case KeyEvent.VK_D -> {
                camera = camera.right(camSpeed);
                System.out.println("D");
            }
            //Up, Down
            case KeyEvent.VK_SHIFT -> camera = camera.up(1);
            case KeyEvent.VK_CONTROL -> camera = camera.down(1);

            // Perspektivní a ortogonální projekce
            case KeyEvent.VK_P -> camera = camera.withFirstPerson(!camera.getFirstPerson());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        axisX = e.getX();
        axisY = e.getY();
        System.out.println("X " + axisX + "  Y " + axisY);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        camera = camera
                .addAzimuth(
                (double) Math.PI * (axisX - e.getX()) / width)
                .addZenith((double) Math.PI * (e.getY() - axisY) / width);

        axisX = e.getX();
        axisY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

