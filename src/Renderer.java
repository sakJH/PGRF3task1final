import lwjglutils.ShaderUtils;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private final int[] indices;

    public Renderer() {
        int shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        glUseProgram(shaderProgram);

        // 3 x vertex: positions (2 x float)
        float[] vertices = {
                -1.f, -1.f,   1.0f, 0.0f, 0.0f, // 1
                 1.f,  0.f,   0.0f, 1.0f, 0.0f, // 2
                 0.f,  1.f,   0.0f, 0.0f, 1.0f // 3
        };

        // 1 x GL_TRIANGLES
        this.indices = new int[] {
                0, 1, 2
        };

        // OpenGL: VertexBuffer
        int vb = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vb);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // OpenGL: IndexBuffer
        int ib = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ib);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Positions
        int loc_inPosition = glGetAttribLocation(shaderProgram, "inPosition");
        glVertexAttribPointer(loc_inPosition, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(loc_inPosition);
        // Color
        int loc_inColor = glGetAttribLocation(shaderProgram, "inColor");
        glVertexAttribPointer(loc_inColor, 3, GL_FLOAT, false,5 * Float.BYTES,2 * Float.BYTES);
        glEnableVertexAttribArray(loc_inColor);

        int loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        glUniform1f(loc_uColorR, 1.f);

        Grid grid = new Grid(4, 4);


        //GridTriangleStrip gridTriangleStrip = new Grid(4,4);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }
}

