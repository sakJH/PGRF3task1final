import lwjglutils.OGLBuffers;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    private int shaderProgram;
    private Grid grid;

    private Camera camera;
    private Mat4 projection;


    private OGLTexture2D texture;

    private boolean orthoProjection = false;
    private JPanel panel;
    private OGLBuffers buff1;
    private OGLBuffers buff2;
    private OGLTextRenderer oglTextRenderer;


    public Renderer() {
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



        try {
            texture = new OGLTexture2D("./textures/bricks.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //
        grid = new Grid();
        buff1 = Grid.gridListTriangle(10, 10);
        buff2 = Grid.gridStripsTriangle(5, 5);


        oglTextRenderer = new OGLTextRenderer(600, 800);
        oglTextRenderer.addStr2D(10, 10, "Sakač");


        panel.addMouseListener();


    }

    public void draw() {
        texture.bind();
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);
    }
}

