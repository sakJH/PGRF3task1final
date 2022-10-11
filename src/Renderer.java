import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private int shaderProgram;

    private Grid grid;

    private Camera camera;
    private Mat4 projection;

    public Renderer() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        camera = new Camera()
                .withPosition(new Vec3D(0.5f,-2f,1.5f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));

        projection = new Mat4PerspRH(Math.PI/3,600/(float)800, 0.1f,50.f);

        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        int loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColorR, 1.f);

        int loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        glUniformMatrix2x4fv(loc_uView, false, projection.floatArray());

        int loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        glUniformMatrix2x4fv(loc_uProj, false, camera.getViewMatrix().floatArray());

        grid = new Grid(20, 20);

    }

    public void draw() {
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);
    }
}
