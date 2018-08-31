import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.nio.FloatBuffer;
import java.util.Random;

import static java.lang.Math.sqrt;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * CSC 322: Introduction to Computer Graphics, Fall 2017
 * Lab 7
 * Brad Taylor, ScD
 * Electrical Engineering and Computer Science
 * The Catholic University of America
 */
// Group 3
// Tariq Almazyad
// Jon Mierzwa
// Nick Esposito
// Tyler Debrino
// =============================================================================
public class ProceduralTerrain {
// =============================================================================


    /**
     * Application title (shown on window bar).
     */
    private static final String APP_TITLE = ProceduralTerrain.class.getName();

    /**
     * Initial setting of scale for the top-level call to plot terrain.
     */
    private static final float INIT_SCALE = 0.25f;

    /**
     * The maximum recursion depth for terrain refinement.
     */
    private static final int MAX_DEPTH = 8;

    /**
     * Target frame rate.
     */
    private static final int FRAME_RATE = 600;

    /**
     * Near plane for clipping.
     */
    private static final float NEAR_PLANE = 0.1f;

    /**
     * Far plane for clipping.
     */
    private static final float FAR_PLANE = 100.0f;

    /**
     * Camera's field of view.
     */
    private static final float FIELD_OF_VIEW = 450.0f;

    /**
     * Light position (in camera space).
     */
    private static final FloatBuffer lightPosition =
            floatBuffer(3.0f, 4.0f, 5.0f, 1.0f);

    /**
     * Ambient component of light.
     */
    private static final FloatBuffer lightAmbient  =
            floatBuffer(0.2f, 0.2f, 0.2f, 1.0f);

    /**
     * Diffuse component of light.
     */
    private static final FloatBuffer lightDiffuse  =
            floatBuffer(0.5f, 0.5f, 0.5f, 1.0f);

    /**
     * Specular component of light.
     */
    private static final FloatBuffer lightSpecular =
            floatBuffer(0.1f, 0.1f, 0.1f, 1.0f);

    /**
     * Ambient component of material.
     */
    private static final FloatBuffer materialAmbient  =
            floatBuffer( 1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Diffuse component of material.
     */
    private static final FloatBuffer materialDiffuse  =
            floatBuffer( 1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Specular component of material.
     */
    private static final FloatBuffer materialSpecular =
            floatBuffer( 1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Material shininess (specular exponent).
     */
    private static final float materialShininess = 8.0f;

    /**
     * Exit flag (application will finish when set to true).
     */
    private static boolean finished;

    /**
     * Camera azimuth in radians about the Y axis.
     */
    private static float cameraAzimuth =  37.5f;

    /**
     * Camera elevation in radians about the X axis.
     */
    private static float cameraElevation = -30.0f;

    /**
     * Camera distance from the origin.
     */
    private static float cameraDistance = 80.0f;

    /**
     * Random number generator.
     */
    private static Random random = new Random();

    /**
     * Random seed.
     */
    private static long seed = System.currentTimeMillis();

    /**
     * Locked out constructor; this class is static.
     */

    // -------------------------------------------------------------------------

    /**
     * Initialize display and OpenGL properties.
     *
     * @throws Exception
     */
    private static void init() throws Exception {

        // initialize the display
        Display.setTitle(APP_TITLE);
        Display.setFullscreen(false);
        Display.setVSyncEnabled(true);
        Display.setResizable(true);
        Display.create();

        // get display size
        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        // viewport
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);

        // perspective transformation
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = ((float) width)/height;
        gluPerspective(FIELD_OF_VIEW, aspectRatio, NEAR_PLANE, FAR_PLANE);

        // background color
        glClearColor( 1.0f, 1.0f, 1.0f, 1.0f);

        // lighting
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLight(GL_LIGHT0, GL_AMBIENT, lightAmbient);
        glLight(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
        glLight(GL_LIGHT0, GL_SPECULAR, lightSpecular);
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glEnable(GL_NORMALIZE);
        glEnable(GL_AUTO_NORMAL);

        // material
        glMaterial(GL_FRONT, GL_AMBIENT, materialAmbient);
        glMaterial(GL_FRONT, GL_DIFFUSE, materialDiffuse);
        glMaterial(GL_FRONT, GL_SPECULAR, materialSpecular);
        glMaterialf(GL_FRONT, GL_SHININESS, materialShininess);

        // allow changing colors while keeping the above material
        glEnable(GL_COLOR_MATERIAL);

        // depth testing
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        // transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // antialiasing
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);



    }

    // -------------------------------------------------------------------------

    /**
     * Main loop of the application. Repeats until finished variable takes on
     * true.
     */
    private static void run() {

        while (!finished) {

            // make sure display is updated
            Display.update();

            if (Display.isCloseRequested()) {
                finished = true;
            }
            else if (Display.isActive()) {

                // foreground window
                update();
                render();
                Display.sync(FRAME_RATE);

            }
            else {

                // background window
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) { }

                update();

                // render if visible and dirty
                if (Display.isVisible() && Display.isDirty()) {
                    render();
                }

            }
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Clean up before exit.
     */
    private static void cleanup() {

        // Close the window
        Display.destroy();

    }

    // -------------------------------------------------------------------------

    /**
     * Handle input and update scene.
     */
    private static void update() {

        // escape to quit
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
        }

        // set new seed
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            seed = System.currentTimeMillis();
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

    // -------------------------------------------------------------------------

    /*
     * Render the scene from the current view
     */
    private static void render() {

        // clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // viewing transformation (bottom of the model-view stack)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // perform transformation according to viewing angle
        // transformation (in reverse order) and go into object space
        glPushMatrix();
        {
            glTranslatef(0.0f, 0.0f, -cameraDistance);
            glRotatef(-cameraElevation, 1.0f, 0.0f, 0.0f);
            glRotatef(-cameraAzimuth, 0.0f, 1.0f, 0.0f);
            glColor3f(1.0f, 0.0f, 0.0f);

            plotWater();

            float[] p0 = {-FAR_PLANE, 0.0f, -FAR_PLANE};
            float[] p1 = {-FAR_PLANE, 0.0f, +FAR_PLANE};
            float[] p2 = {+FAR_PLANE, 0.0f, +FAR_PLANE};
            float[] p3 = {+FAR_PLANE, 0.0f, -FAR_PLANE};
            glColor3f(0.0f, 0.75f, 0.5f);
            plotProceduralTerrain(p0, p1, p2, p3, FAR_PLANE/4, 9);

        }
        glPopMatrix();

    }

    // -------------------------------------------------------------------------

    public static void plotWater() {

        glColor3f(0.0f, 0.5f, 1.0f);
        glBegin(GL_QUADS);
        {
            glVertex3f(-FAR_PLANE, -10.0f, -FAR_PLANE);
            glVertex3f(-FAR_PLANE, -10.0f, +FAR_PLANE);
            glVertex3f(+FAR_PLANE, -10.0f, +FAR_PLANE);
            glVertex3f(+FAR_PLANE, -10.0f, -FAR_PLANE);
        }
        glEnd();
    }


    // -------------------------------------------------------------------------

    public static void plotProceduralTerrain(float[] p0, float[] p1,
                                             float[] p2, float[] p3,
                                             float scale, int depth) {

        // base case...
        if (depth <= 0) {

            if (aboveWater(p0, p1, p2, p3)) {

                // TODO: compute quad normal
                // Group 3
                // Tariq Almazyad
                // Jon Mierzwa
                // Nick Esposito
                // Tyler Debrino
                // x = 0, y = 1, z = 2

                // subtract vectors
                float[] vec1 = new float[3];
                vec1[0] = p1[0] - p0[0];
                vec1[1] = p1[1] - p0[1];
                vec1[2] = p1[2] - p0[2];

                float[] vec2 = new float[3];
                vec2[0] = p3[0] - p0[0];
                vec2[1] = p3[1] - p0[1];
                vec2[2] = p3[2] - p0[2];

                // cross product of two vectors
                float[] cp = new float[3];
                cp[0] = vec1[1] * vec2[2] - vec1[2] * vec2[1];
                cp[1] = vec1[2] * vec2[0] - vec1[0] * vec2[2];
                cp[2] = vec1[0] * vec2[1] - vec1[1] * vec2[0];

                // vector unit length
                float length = (cp[0] * cp[0] + cp[1] * cp[1] + cp[2] * cp[2]) *
                        (cp[0] * cp[0] + cp[1] * cp[1] + cp[2] * cp[2]);

                // normalize it
                float[] norm = new float[3];
                norm[0] = cp[0] / length;
                norm[1] = cp[1] / length;
                norm[2] = cp[2] / length;

                // TODO: plot quad
                // Group 3
                // Tariq Almazyad
                // Jon Mierzwa
                // Nick Esposito
                // Tyler Debrino

                glBegin(GL_QUADS);

                glNormal3f(norm[0], norm[1], norm[2]);
                glVertex3f(p0[0], p0[1], p0[2]);
                glVertex3f(p1[0], p1[1], p1[2]);
                glVertex3f(p2[0], p2[1], p2[2]);
                glVertex3f(p3[0], p3[1], p3[2]);

                glEnd();
            }
        }
        else {

            // TODO: find midpoints along which to recurse
            // Group 3
            // Tariq Almazyad
            // Jon Mierzwa
            // Nick Esposito
            // Tyler Debrino

            // four midpoints are required in order to recurse
            // north midpoint
            float[] n = getMidPoint(p0, p3);
            // south midpoint
            float[] s = getMidPoint(p1, p2);
            // east midpoint
            float[] e = getMidPoint(p0, p1);
            // west midpoint
            float[] w = getMidPoint(p3, p2);
            // find the midpoint of two midpoints to get the center
            float[] center = getMidPoint(e, w); // or (n, s);

            // compute pseudo-random value r associated with XZ bounds
            long seed = getSeedFromBounds(p0, p2, p2, p3);
            random.setSeed(seed);
            float r = (float) random.nextGaussian();

            // TODO: displace midpoint according to r and scale
            // Group 3
            // Tariq Almazyad
            // Jon Mierzwa
            // Nick Esposito
            // Tyler Debrino

            center[0] = center[0] + scale * r;
            center[1] = center[1] + scale * r;
            center[2] = center[2] + scale * r;

            // TODO: update scale, increase depth, and recurse
            // Group 3
            // Tariq Almazyad
            // Jon Mierzwa
            // Nick Esposito
            // Tyler Debrino
            // decrease depth
            depth--;

            // update scale
            scale = scale / 2;

            plotProceduralTerrain(p0, w, center, n, scale, depth);
            plotProceduralTerrain(n, center, e, p3, scale, depth);
            plotProceduralTerrain(center, s, p2, e, scale, depth);
            plotProceduralTerrain(w, p1, s, center, scale, depth);

        }

    }

    // -------------------------------------------------------------------------
    public static boolean aboveWater(float[] p0, float[] p1,
                                     float[] p2, float[] p3) {

        return (p0[1] > -10.0f) || (p1[1] > -10.0f) ||
                (p2[1] > -10.0f) || (p3[1] > -10.0f);

    }

    // -------------------------------------------------------------------------

    /**
     * Return a new point holding the midpoint between p0 and p1.
     *
     * @param p0 A point.
     * @param p1 A point.
     * @return a point.
     */
    public static float[] getMidPoint(float[] p0, float[] p1) {

        float[] m =  new float[3];

        // TODO: set m to the midpoint between p0 and p1.

        // divide by 2 to get midpoint
        m[0] = (p0[0] + p1[0]) / 2;
        m[1] = (p0[1] + p1[1]) / 2;
        m[2] = (p0[2] + p1[2]) / 2;

        return m;

    }

    // -------------------------------------------------------------------------

    public static long getSeedFromBounds(float[] p0, float[] p1,
                                         float[] p2, float[] p3) {

        long seed = ProceduralTerrain.seed;
        for (int d = 0; d < 3; d++) {
            seed = 31*seed + Float.floatToIntBits(p0[d]);
            seed = 31*seed + Float.floatToIntBits(p1[d]);
            seed = 31*seed + Float.floatToIntBits(p2[d]);
            seed = 31*seed + Float.floatToIntBits(p3[d]);
        }
        return seed;

    }

    // -------------------------------------------------------------------------

    /**
     * Set vector v to p0->p1.
     *
     * @param p0 A point.
     * @param p1 A point.
     * @param v A vector.
     */
    public static void setVector(float[] p0, float[] p1, float[] v) {

        for (int d = 0; d < v.length; d++) {
            v[d] = p1[d] - p0[d];
        }

    }

    // -------------------------------------------------------------------------

    /**
     * Make v a unit vector by scaling it by the inverse of its norm.
     *
     * @param v A vector.
     */
    private static void makeUnit(float[] v) {

        float norm = 0.0f;
        for (int i = 0; i < v.length; i++) {
            norm += v[i]*v[i];
        }
        norm = (float) sqrt(norm);
        for (int i = 0; i < v.length; i++) {
            v[i] /= norm;
        }

    }

    // -------------------------------------------------------------------------

    /**
     * Set vector res to the cross product of vectors src1 and src2. Assumes
     * src1, src2, and res are 3D.
     *
     * @param src1 A vector.
     * @param src2 A vector.
     * @param res A vector.
     */
    private static void setCross(float[] src1, float[] src2, float[] res) {

        res[0] = src1[1]*src2[2] - src1[2]*src2[1];
        res[1] = src1[2]*src2[0] - src1[0]*src2[2];
        res[2] = src1[0]*src2[1] - src1[1]*src2[0];

    }

    // -------------------------------------------------------------------------

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

    // =========================================================================
    //                                  MAIN
    // =========================================================================

    public static void main(String[] args) {

        try {
            init();
            run();
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
        } finally {
            cleanup();
        }

    }

// =============================================================================
}
// =============================================================================
