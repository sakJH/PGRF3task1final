import java.sql.SQLOutput;
import java.util.Arrays;

public class Grid {

    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     */

    float[] vertices;
    int[] indices;

    //generateGrid
    public Grid(final int m, final int n) {
        //Grid == List
        vertices = new float[2 * m * n];
        indices = new int[3 * 2 * (m - 1) * (n - 1)];

        // vertices <0;1>
        int idx1 = 0;

        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {
                System.out.println("x: " + j / (float) (n - 1));
                System.out.println("y: " + i / (float) (m - 1));
                System.out.println("-");
                // TODO: Naplnit vertices
                vertices[idx1++] = (j / (float) (n - 1));
                vertices[idx1++] = (i / (float) (m - 1));   // TAK????
            }
            System.out.println("----------");
        }

        //System.out.println("Vertices");
        // Indices
        // TODO: pro i přes m - 1; i++

        int idx2 = 0;

        for (int i = 0; i < m - 1; i++ )
        {
            for (int j = 0; j < n - 1; j++)
            {
                /*System.out.println(j);
                System.out.println(j + n);
                System.out.println(j + 1);

                System.out.println("-");
                System.out.println(j + 1);
                System.out.println(j + n);
                System.out.println(j + n + 1);

                System.out.println("-----");
*/
                indices[idx2++] = j;
                indices[idx2++] = j+n;
                indices[idx2++] = j+1;

                indices[idx2++] = j+1;
                indices[idx2++] = j+n;
                indices[idx2++] = j+n+1;
            }
        }

        System.out.println("Vertices");
        System.out.println(Arrays.toString(vertices));
        System.out.println("Indices");
        System.out.println(Arrays.toString(indices));
    }

    public static void GridTriangleStrip(int m, int n){

        float[] vertexBuffer = new float[m * n * 2];

        int idx1 = 0;

        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < m; i++)
            {
                vertexBuffer[idx1++] = j / (float) (m-1);
                vertexBuffer[idx1++] = i / (float) (n-1);
            }
        }
//-----------------------------------------------------------------------------------------------
        int idx2 = 0;
        int[] indexBuffer = new int [2 * m * (n - 1)];  // 2m(n −1)   (m+1)*(n-1)*2

        for (int i = 0; i < m - 1; i++)
        {
            for (int j = 0; j <= n; j++)
            {
                if (i % 2 == 0)
                {
                    if (j == n)
                    {
                        indexBuffer[idx2++] = (j - 1) + (j + 1) * n;
                        indexBuffer[idx2++] = (j - 1) + (j + 1) * n;
                    }
                    else
                    {
                        indexBuffer[idx2++] = j * i * n;
                        indexBuffer[idx2++] = j * (i + 1) * j;
                    }
                }
                else {
                    if (j == n)
                    {
                        indexBuffer[idx2++] = n - j + (i + 1) * n;
                        indexBuffer[idx2++] = n - j + (i + 1) * n;
                    }
                    else
                    {
                        indexBuffer[idx2++] = (n - 1) - j + (i + 1) * n;
                        indexBuffer[idx2++] = i * n + ( n - 1) - j;
                    }
                }
            }
        }

    }
}
