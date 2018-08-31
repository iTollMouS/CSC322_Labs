import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
// Group 3
// Tariq Almazyad
// Jon Mierzwa
// Nick Esposito
// Tyler Debrino

// =====================================================================================================================
public class Lab_04_Mesh_Normals_Starter {
// =====================================================================================================================

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Compute the face normals of the given mesh using the normalized cross product of triangle edges.
     *
     * @param mesh A mesh.
     */
    private static void computeFaceNormals(Mesh mesh) {

        // Group 3
        // Tariq Almazyad
        // Jon Mierzwa
        // Nick Esposito
        // Tyler Debrino
        mesh.nf = new float[mesh.f.length][3]; // 3 vertices

        for(int fIndex = 0; fIndex < mesh.f.length; ++fIndex){

            // normalized cross product

            float[] z = new float[3];

            z[0] = mesh.v[mesh.f[fIndex][2]][0] - mesh.v[mesh.f[fIndex][0]][0];
            z[1] = mesh.v[mesh.f[fIndex][2]][1] - mesh.v[mesh.f[fIndex][0]][1];
            z[2] = mesh.v[mesh.f[fIndex][2]][2] - mesh.v[mesh.f[fIndex][0]][2];

            float[] z1 = new float[3];

            z1[0] = mesh.v[mesh.f[fIndex][1]][0] - mesh.v[mesh.f[fIndex][0]][0];
            z1[1] = mesh.v[mesh.f[fIndex][1]][1] - mesh.v[mesh.f[fIndex][0]][1];
            z1[2] = mesh.v[mesh.f[fIndex][1]][2] - mesh.v[mesh.f[fIndex][0]][2];

            float[] z2 = new float[3];

            z2[0] = z1[1] * z1[2] - z[2] * z[1];
            z2[1] = z1[2] * z1[0] - z[0] * z[2];
            z2[2] = z1[0] * z1[1] - z[1] * z[0];

            float[] x = z2;

            // get the given vector unit length

            float[] y = new float[3];

            float l = (x[0] * x[0] + x[1] * x[1] + x[2] * x[2]) * (x[0] * x[0] + x[1] * x[1] + x[2] * x[2]);

            y[0] = x[0] / l;
            y[1] = x[1] / l;
            y[2] = x[2] / l;

            mesh.nf[fIndex] = y;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Compute the vertex normals of the given mesh using the normalized sum of all face normals incident on each
     * vertex.
     *
     * @param mesh A mesh.
     */
    private static void computeVertexNormals(Mesh mesh) {

        // Group 3
        // Tariq Almazyad
        // Jon Mierzwa
        // Nick Esposito
        // Tyler Debrino

        mesh.nv = new float[mesh.v.length][3];

        // vertex normals
        for(int i = 0; i < mesh.nv.length; ++i){

            mesh.nv[i][0] = 0.0f;
            mesh.nv[i][1] = 0.0f;
            mesh.nv[i][2] = 0.0f;

        }

        // count to collect the normals of the faces incident to the vertex
        int[] cnt = new int[mesh.f.length];

        for(int i = 0; i < cnt.length; ++i){

            cnt[i] = 0; // starts at 0
        }

        for(int fIndex = 0; fIndex < mesh.f.length; ++fIndex){

            int vIndex;

            // first face normal to first vertex normal
            vIndex = mesh.f[fIndex][0];
            mesh.nv[vIndex][0] = mesh.nv[vIndex][0] + mesh.nf[fIndex][0];
            mesh.nv[vIndex][1] = mesh.nv[vIndex][1] + mesh.nf[fIndex][1];
            mesh.nv[vIndex][2] = mesh.nv[vIndex][2] + mesh.nf[fIndex][2];

            // increment count
            cnt[vIndex]++;

            // second face normal to second vertex normal
            vIndex = mesh.f[fIndex][1];
            mesh.nv[vIndex][0] = mesh.nv[vIndex][0] + mesh.nf[fIndex][0];
            mesh.nv[vIndex][1] = mesh.nv[vIndex][1] + mesh.nf[fIndex][1];
            mesh.nv[vIndex][2] = mesh.nv[vIndex][2] + mesh.nf[fIndex][2];

            // increment count
            cnt[vIndex]++;

            // third face normal to third vertex normal
            vIndex = mesh.f[fIndex][2];
            mesh.nv[vIndex][0] = mesh.nv[vIndex][0] + mesh.nf[fIndex][0];
            mesh.nv[vIndex][1] = mesh.nv[vIndex][1] + mesh.nf[fIndex][1];
            mesh.nv[vIndex][2] = mesh.nv[vIndex][2] + mesh.nf[fIndex][2];

            // increment count
            cnt[vIndex]++;

            // average of the three vertex normals
            for(int i = 0; i < mesh.nv.length; ++i){

                mesh.nv[i][0] = mesh.nv[i][0] / cnt[i];
                mesh.nv[i][1] = mesh.nv[i][1] / cnt[i];
                mesh.nv[i][2] = mesh.nv[i][2] / cnt[i];
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Plot the given mesh using flat shading.
     *
     * @param mesh A mesh.
     */
    private static void plotMeshFlatShaded(Mesh mesh) {

        // Group 3
        // Tariq Almazyad
        // Jon Mierzwa
        // Nick Esposito
        // Tyler Debrino

        glBegin(GL_TRIANGLES);
        for(int fIndex = 0; fIndex < mesh.f.length; ++fIndex){

            // normal is applied to all vertices
            glNormal3f(mesh.nf[fIndex][0], mesh.nf[fIndex][1], mesh.nf[fIndex][2]);
            glVertex3f(mesh.v[mesh.f[fIndex][0]][0], mesh.v[mesh.f[fIndex][0]][1], mesh.v[mesh.f[fIndex][0]][2]);
            glVertex3f(mesh.v[mesh.f[fIndex][1]][0], mesh.v[mesh.f[fIndex][1]][1], mesh.v[mesh.f[fIndex][1]][2]);
            glVertex3f(mesh.v[mesh.f[fIndex][2]][0], mesh.v[mesh.f[fIndex][2]][1], mesh.v[mesh.f[fIndex][2]][2]);
        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Plot the given mesh using smooth shading.
     *
     * @param mesh A mesh.
     */
    private static void plotMeshSmoothShaded(Mesh mesh) {

        // Group 3
        // Tariq Almazyad
        // Jon Mierzwa
        // Nick Esposito
        // Tyler Debrino
        glBegin(GL_TRIANGLES);
        for(int fIndex = 0; fIndex < mesh.f.length; ++fIndex){

            // normal is applied to each vertex
            glNormal3f(mesh.v[mesh.f[fIndex][0]][0], mesh.v[mesh.f[fIndex][0]][1], mesh.v[mesh.f[fIndex][0]][2]);
            glVertex3f(mesh.v[mesh.f[fIndex][0]][0], mesh.v[mesh.f[fIndex][0]][1], mesh.v[mesh.f[fIndex][0]][2]);
            glNormal3f(mesh.v[mesh.f[fIndex][1]][0], mesh.v[mesh.f[fIndex][1]][1], mesh.v[mesh.f[fIndex][1]][2]);
            glVertex3f(mesh.v[mesh.f[fIndex][1]][0], mesh.v[mesh.f[fIndex][1]][1], mesh.v[mesh.f[fIndex][1]][2]);
            glNormal3f(mesh.v[mesh.f[fIndex][2]][0], mesh.v[mesh.f[fIndex][2]][1], mesh.v[mesh.f[fIndex][2]][2]);
            glVertex3f(mesh.v[mesh.f[fIndex][2]][0], mesh.v[mesh.f[fIndex][2]][1], mesh.v[mesh.f[fIndex][2]][2]);
        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A simple indexed mesh.
     */
    private static class Mesh {

        /**
         * Mesh vertices. The entry v[i][j] contains the i-th vertex's j-th coordinate value.
         */
        public float[][] v = null;

        /**
         * Mesh faces. The entry f[i][j] contains the index of the i-th face's j-th vertex.
         */
        public int[][] f = null;

        /**
         * Face normals. The entry nf[i][j] contains the i-th face normal's j-th coordinate value.
         */
        public float[][] nf = null;

        /**
         * Vertex normals. The entry nv[i][j] contains the i-th vertex normal's j-th coordinate value.
         */
        public float[][] nv = null;

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Application Title
     */
    private static final String APP_TITLE = "Lab 04: Mesh Normals";

    /**
     * Desired framerate.
     */
    private static final int FRAMERATE = 60;

    /**
     * 3D location of light.
     */
    private static final FloatBuffer lightPosition = floatBuffer(3.0f, 4.0f, 5.0f, 1.0f);

    /**
     * Ambient light RGB intensities.
     */
    private static final FloatBuffer lightAmbient  = floatBuffer(0.2f, 0.2f, 0.2f, 1.0f);

    /**
     * Diffuse light RGB intensities.
     */
    private static final FloatBuffer lightDiffuse  = floatBuffer(0.5f, 0.5f, 0.5f, 1.0f);

    /**
     * Specular light RGB intensities.
     */
    private static final FloatBuffer lightSpecular = floatBuffer(0.1f, 0.1f, 0.1f, 1.0f);

    /**
     * Ambient material properties.
     */
    private static final FloatBuffer materialAmbient  = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Diffuse material properties.
     */
    private static final FloatBuffer materialDiffuse  = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Specular material properties.
     */
    private static final FloatBuffer materialSpecular = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Material shininess.
     */
    private static final float materialShininess = 8.0f;

    /**
     * Exit flag for main rendering loop.
     */
    private static boolean finished;

    /**
     * Camera azimuth degrees.
     */
    private static float cameraAzimuth   =  37.5f;

    /**
     * Camera elevation degrees.
     */
    private static float cameraElevation = -30.0f;

    /**
     * Camera distance from origin.
     */
    private static float cameraDistance  =   6.0f;

    /**
     * Selection of which model to render.
     */
    private static int option = 1;

    /**
     * The file from which to load the mesh. Taken from first program argument.
     */
    private static String meshFile = null;

    /**
     * Mesh to be loaded and viewed.
     */
    private static Mesh mesh = null;

    /**
     * The centroid of the mesh vertices.
     */
    private static float[] centroid = null;

    /**
     * Initialized based on the inverse of the axis-aligned bounding box diagonal length of the mesh.
     */
    private static float scale = 1.0f;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Lock out use of constructor. This class is static.
     */
    private Lab_04_Mesh_Normals_Starter() { }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Initialize display and opengl properties.
     *
     * @throws Exception
     */
    private static void init() throws Exception {

        // initialize the display
        Display.setTitle(APP_TITLE);
        Display.setFullscreen(false);
        Display.setVSyncEnabled(true);
        Display.create();

        // set up light
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLight(GL_LIGHT0, GL_AMBIENT, lightAmbient);
        glLight(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
        glLight(GL_LIGHT0, GL_SPECULAR, lightSpecular);

        // set up material
        glMaterial(GL_FRONT, GL_AMBIENT, materialAmbient);
        glMaterial(GL_FRONT, GL_DIFFUSE, materialDiffuse);
        glMaterial(GL_FRONT, GL_SPECULAR, materialSpecular);
        glMaterialf(GL_FRONT, GL_SHININESS, materialShininess);

        // allow changing colors while keeping the above material
        glEnable(GL_COLOR_MATERIAL);

        // set gl functionality
        glEnable(GL_DEPTH_TEST);

        // set background color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // get display size
        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        // set the viewport
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);


        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(2.0f);

        glEnable(GL_NORMALIZE);

        mesh = loadObj(meshFile);
        computeFaceNormals(mesh);
        computeVertexNormals(mesh);

        centroid = centroid(mesh.v);
        scale = 200.0f/diagonal(mesh.v);

        // perspective transformation
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = ((float) width)/height;
        gluPerspective(45.0f, aspectRatio, scale*0.1f, scale*100.0f);

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Main loop.
     */
    private static void run() {

        while (!finished) {

            Display.update();

            if (Display.isCloseRequested()) {

                finished = true;

            } else if (Display.isActive()) {

                // The window is in the foreground, so we should play the game
                logic();
                render();
                Display.sync(FRAMERATE);

            } else {

                // The window is not in the foreground, so we can allow other
                // stuff to run and infrequently update
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }

                logic();

                // Only bother rendering if the window is visible and dirty
                if (Display.isVisible() && Display.isDirty()) {
                    render();
                }

            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Clean up before exit.
     */
    private static void cleanup() { Display.destroy(); }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Handle input.
     */
    private static void logic() {

        // escape to quit
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            option = 1;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            option = 2;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
            option = 3;
        }

        // mouse event catcher
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                cameraElevation += 0.5f*Mouse.getEventDY();
            }
            if (Mouse.isButtonDown(0)) {
                cameraAzimuth -= 0.5f*Mouse.getEventDX();
            }
            if (Mouse.isButtonDown(1)) {
                cameraDistance += 0.1*Mouse.getEventDY();
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Render model from current view.
     */
    private static void render() {

        // clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // viewing transformation (bottom of the model/view stack)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // perform transformation according to viewing angle; i.e. undo camera
        // transformation (in reverse order) and go into object space
        glPushMatrix();
        {
            glTranslatef(-centroid[0], -centroid[1], -centroid[2]);
            glTranslatef(0.0f, 0.0f, -cameraDistance*scale);
            glRotatef(-cameraElevation, 1.0f, 0.0f, 0.0f);
            glRotatef(-cameraAzimuth, 0.0f, 1.0f, 0.0f);

            // call method corresponding to option
            switch (option) {
                case 1:
                    glColor3f(1.0f, 0.0f, 0.0f);
                    plotMeshWireFrame(mesh);
                    break;
                case 2:
                    glColor3f(0.0f, 1.0f, 0.0f);
                    plotMeshFlatShaded(mesh);
                    break;
                case 3:
                    glColor3f(0.0f, 0.0f, 1.0f);
                    plotMeshSmoothShaded(mesh);
                    break;
            }

        }
        glPopMatrix();

        // place light in camera space
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);

        // flush the data
        glFlush();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Count the number of vertices and faces contained in an obj file with the given file name and return them in an
     * array of integers of length 2. The output's 0-th entry contains the number of vertices and the 1-st entry
     * contains the number of faces.
     *
     * @param filename A string.
     * @return An int array of length 2.
     * @throws IOException
     */
    private static int[] countVerticesAndFaces(String filename) throws IOException {

        // go through file and count appearances of "v" and "f"
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        int nVertices = 0;
        int nFaces = 0;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("v ")) {
                nVertices++;
            }
            else if (line.startsWith("f ")) {
                nFaces++;
            }
        }
        in.close();

        int[] count = new int[2];
        count[0] = nVertices;
        count[1] = nFaces;

        return count;

    }

    // -----------------------------------------------------------------------------------------------------------------

    private static Mesh loadObj(String filename) throws IOException {

        System.out.print("Loading mesh... ");

        // get vertex and face counts from file
        int[] counts = countVerticesAndFaces(filename);
        int nVertices = counts[0];
        int nFaces = counts[1];

        // allocate mesh vertex and face fields based on counts
        Mesh mesh = new Mesh();
        mesh.v = new float[nVertices][];
        mesh.f = new int[nFaces][];

        // read vertex and face values from file
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        int i = 0; // current vertex index
        int j = 0; // current face index
        while ((line = in.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens[0].equals("v")) {
                mesh.v[i] = new float[3];
                mesh.v[i][0] = Float.parseFloat(tokens[1]);
                mesh.v[i][1] = Float.parseFloat(tokens[2]);
                mesh.v[i][2] = Float.parseFloat(tokens[3]);
                i++;
            }
            else if (tokens[0].equals("f")) {
                mesh.f[j] = new int[3];
                mesh.f[j][0] = Integer.parseInt(tokens[1]) - 1;
                mesh.f[j][1] = Integer.parseInt(tokens[2]) - 1;
                mesh.f[j][2] = Integer.parseInt(tokens[3]) - 1;
                j++;
            }
        }
        in.close();

        System.out.println("Done. Loaded " + mesh.v.length + " vertices and " + mesh.f.length + " faces.");

        return mesh;

    }

    // -----------------------------------------------------------------------------------------------------------------

    private static void plotMeshWireFrame(Mesh mesh) {

        glLineWidth(0.5f);
        for (int i = 0; i < mesh.f.length; i++) {

            // unpack vertices
            float[] v0 = mesh.v[mesh.f[i][0]];
            float[] v1 = mesh.v[mesh.f[i][1]];
            float[] v2 = mesh.v[mesh.f[i][2]];

            // draw face edges
            glBegin(GL_LINE_LOOP);
            {
                glVertex3f(v0[0], v0[1], v0[2]);
                glVertex3f(v1[0], v1[1], v1[2]);
                glVertex3f(v2[0], v2[1], v2[2]);
            }
            glEnd();

        }

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Compute the centroid of an array of points.
     *
     * @param points An array of points.
     * @return A point.
     */
    private static float[] centroid(float[][] points) {

        // initialize to zero point
        float[] centroid = new float[3];
        centroid[0] = 0.0f;
        centroid[1] = 0.0f;
        centroid[2] = 0.0f;

        // compute coordinate-wise sum of points
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < 3; j++) {
                centroid[j] += points[i][j];
            }
        }

        // divide by the number of points
        for (int j = 0; j < 3; j++) {
            centroid[j] /= points.length;
        }

        return centroid;

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Compute the axis-aligned bounding box diagonal of a set of points.
     *
     * @param points An array of points.
     * @return A float.
     */
    private static float diagonal(float[][] points) {

        // initialize bounds
        float[] lower = new float[3];
        float[] upper = new float[3];
        lower[0] = Float.POSITIVE_INFINITY;
        lower[1] = Float.POSITIVE_INFINITY;
        lower[2] = Float.POSITIVE_INFINITY;
        upper[0] = Float.NEGATIVE_INFINITY;
        upper[1] = Float.NEGATIVE_INFINITY;
        upper[2] = Float.NEGATIVE_INFINITY;

        // compute coordinate-wise lower and upper bounds
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < 3; j++) {
                if (points[i][j] < lower[j]) {
                    lower[j] = points[i][j];
                }
                if (points[i][j] > upper[j]) {
                    upper[j] = points[i][j];
                }
            }
        }

        // compute distance between box corners
        float dx = upper[0] - lower[0];
        float dy = upper[0] - lower[0];
        float dz = upper[0] - lower[0];
        float d = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);

        return d;

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Utility function to easily create float buffers.
     *
     * @param f1 A float.
     * @param f2 A float.
     * @param f3 A float.
     * @param f4 A float.
     * @return A float buffer.
     */
    private static FloatBuffer floatBuffer(float f1, float f2, float f3, float f4) {

        FloatBuffer fb = BufferUtils.createFloatBuffer(4);
        fb.put(f1).put(f2).put(f3).put(f4).flip();
        return fb;

    }

    // =================================================================================================================
    //                                                       MAIN
    // =================================================================================================================

    public static void main(String[] args) {

        try {
            meshFile = args[0];
            System.out.println("Mesh file: " + meshFile);
            init();
            run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Sys.alert(APP_TITLE, "An error occurred and the program will exit.");
        } finally {
            cleanup();
        }

        System.exit(0);

    }

// =====================================================================================================================
}
// =====================================================================================================================


