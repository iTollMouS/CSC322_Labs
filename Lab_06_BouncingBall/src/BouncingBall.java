import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * CSC 322: Introduction to Computer Graphics, Fall 2017
 * Lab 6: Bouncing Ball (Basic Physics)
 *
 * Brad Taylor, D.Sc.
 * Electrical Engineering and Computer Science
 * The Catholic University of America
 */


//
// Group 3
// Tariq Almazyad
// Jon Mierzwa
// Nick Esposito
// Tyler Debrino
//=============================================================================
public class BouncingBall {
// =============================================================================

    /**
     * Float value of a complete circumference in radians.
     */
    private static final float TURN = (float) (2.0d*Math.PI);

    /**
     * Application title (shown on window bar).
     */
    private static final String APP_TITLE = BouncingBall.class.getName();

    /**
     * Target frame rate.
     */
    private static final int FRAME_RATE = 60;

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
    private static final float FIELD_OF_VIEW = 45.0f;

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
    private static float cameraAzimuth   =  37.5f;

    /**
     * Camera elevation in radians about the X axis.
     */
    private static float cameraElevation = -30.0f;

    /**
     * Camera distance from the origin.
     */
    private static float cameraDistance  =   30.0f;

    /**
     * The gravity constant for this virtual space (in meters/seconds^2).
     */
    private final static float GRAVITY = -9.8f;

    /**
     * Radius of the ball.
     */
    private final static float BALL_RADIUS = 1.0f;

    /**
     * The x, y, z location of the ball in space.
     */
    private static float[] ballPosition = {0.0f, 10.0f, 0.0f};

    /**
     * The dx, dy, dz velocity of the ball (in metersseconds).
     */
    private static float[] ballVelocity =  {5.0f, 0.0f, 7.0f};

    /**
     * Locked out constructor; this class is static.
     */
    private BouncingBall() { }

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

        // fog
        glEnable(GL_FOG);
        glFog(GL_FOG_COLOR, floatBuffer( 1.0f, 1.0f, 1.0f, 1.0f));
        glFogi(GL_FOG_MODE, GL_EXP2);
        glFogf(GL_FOG_DENSITY, 0.02f);

    }

    // -------------------------------------------------------------------------

    /**
     * Main loop of the application. Repeats until finished variable takes on
     * true.
     */
    private static void run() {

        long lastUpdate = System.currentTimeMillis();
        float dt;

        while (!finished) {

            // make sure display is updated
            Display.update();

            if (Display.isCloseRequested()) {
                finished = true;
            }
            // foreground window
            else if (Display.isActive()) {

                // update scene based on elapsed time
                dt = 0.001f*(System.currentTimeMillis() - lastUpdate);
                update(dt);
                lastUpdate = System.currentTimeMillis();

                render();
                Display.sync(FRAME_RATE);

            }
            // background window
            else {

                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) { }

                // update scene based on elapsed time
                dt = 0.001f*(System.currentTimeMillis() - lastUpdate);
                update(dt);
                lastUpdate = System.currentTimeMillis();

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
     * Handle input and update scene based on dt, which indicates the elapsed
     * time in seconds since the last update invocation ended.
     *
     * @param dt A float.
     */
    private static void update(float dt) {

        // escape to quit
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
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

        // Group 3
        // Tariq Almazyad
        // Jon Mierzwa
        // Nick Esposito
        // Tyler Debrino

        // update scene based on dt (elapsed time in seconds)
        ballVelocity[1] += dt*GRAVITY;
        ballPosition[1] += dt*ballVelocity[1];

        // move the ball in a certain direction
        ballPosition[0] += dt*ballVelocity[0]; // x value
        ballPosition[2] += dt*ballVelocity[2]; // z value
        // check for bounce and correct if needed

        // if ball extent is beyond floor and it is moving downward...
        if ((ballPosition[1] - BALL_RADIUS < 0.0f) & (ballVelocity[1] < 0.0f)) {

            // correct ball location in Y
            float correction = 0.0f - (ballPosition[1] - BALL_RADIUS);
            ballPosition[1] += 2.0f*correction;

            // flip Y velocity
            ballVelocity[1] *= -1.0f;
        }

        if (((ballPosition[0] > 10.0f - BALL_RADIUS) & (ballVelocity[0] > 0)) ||
                ((ballPosition[0] < -10.0f + BALL_RADIUS) & (ballVelocity[0] < 0))) {

            // flip X velocity
            ballVelocity[0] *= -1.0f;
        }

        if (((ballPosition[2] > 10.0f - BALL_RADIUS) & (ballVelocity[2] > 0)) ||
                ((ballPosition[2] < -10.0f + BALL_RADIUS) & (ballVelocity[2] < 0))) {

            // flip Z velocity
            ballVelocity[2] *= -1.0f;
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Render the scene from the current view
     */
    private static void render() {

        // clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // viewing transformation (bottom of the model-view stack)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // perform transformation according to viewing angle; i.e. undo camera
        // transformation (in reverse order), go into object space, and render
        // scene
        glPushMatrix();
        {
            glTranslatef(0.0f, 0.0f, -cameraDistance);
            glRotatef(-cameraElevation, 1.0f, 0.0f, 0.0f);
            glRotatef(-cameraAzimuth, 0.0f, 1.0f, 0.0f);
            plotScene();
        }
        glPopMatrix();

    }

    // -------------------------------------------------------------------------

    /**
     * Plot a unit cube (i.e, a cube spanning the [-1, 1] interval on the X, Y,
     * and Z axes).
     */
    private static void plotUnitCube() {

        // set flat shading
        glShadeModel(GL_FLAT);

        // drawing quads (squares)
        glBegin(GL_QUADS);
        {

            // front x face
            glNormal3f( 1.0f, 0.0f, 0.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, -1.0f, 1.0f);

            // back x face
            glNormal3f(-1.0f, 0.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);

            // front y face
            glNormal3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);

            // back y face
            glNormal3f(0.0f, -1.0f, 0.0f);
            glVertex3f( 1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);

            // front z face
            glNormal3f(0.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f( 1.0f, -1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);

            // back z face
            glNormal3f(0.0f, 0.0f, -1.0f);
            glVertex3f( 1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f( 1.0f, -1.0f, -1.0f);

        }
        glEnd();
    }

    // -------------------------------------------------------------------------

    /**
     * Plot a unit sphere using quads over a spherical parameterization having
     * n slices of azimuth and n/2 slices of elevation.
     *
     * @param n An int.
     */
    private static void plotUnitSphere(int n) {

        glBegin(GL_QUADS);
        {
            float[] s0 = new float[3];
            float[] s1 = new float[3];
            float[] s2 = new float[3];
            float[] s3 = new float[3];

            // radius 1 for all points
            s0[2] = s1[2] = s2[2] = s3[2] = 1.0f;

            float[] c0 = new float[3];
            float[] c1 = new float[3];
            float[] c2 = new float[3];
            float[] c3 = new float[3];

            int m = n/4;
            for (int i = 0; i < n; i++) {
                for (int j = -m; j < +m; j++) {

                    s0[0] = (TURN*i)/n;       s0[1] = (TURN*j)/n;
                    s1[0] = (TURN*(i + 1))/n; s1[1] = (TURN*j)/n;
                    s2[0] = (TURN*(i + 1))/n; s2[1] = (TURN*(j + 1))/n;
                    s3[0] = (TURN*i)/n;       s3[1] = (TURN*(j + 1))/n;

                    sphericalToCartesian(s0, c0);
                    sphericalToCartesian(s1, c1);
                    sphericalToCartesian(s2, c2);
                    sphericalToCartesian(s3, c3);

                    glNormal3f(c0[0], c0[1], c0[2]);
                    glVertex3f(c0[0], c0[1], c0[2]);

                    glNormal3f(c1[0], c1[1], c1[2]);
                    glVertex3f(c1[0], c1[1], c1[2]);

                    glNormal3f(c2[0], c2[1], c2[2]);
                    glVertex3f(c2[0], c2[1], c2[2]);

                    glNormal3f(c3[0], c3[1], c3[2]);
                    glVertex3f(c3[0], c3[1], c3[2]);

                }
            }
        }
        glEnd();

    }

    // -------------------------------------------------------------------------

    /**
     * Convert the spherical coordinates contained in s to cartesian
     * coordinates and set c accordingly.
     *
     * @param s An array of floats representing azimuth, elevation, and radius.
     * @param c An array of floats representing cartesian coordinates.
     */
    private static void sphericalToCartesian(float[] s, float[] c) {

        // s = (theta, phi, r)
        // c = (x, y, z)

        c[1] = (float) Math.sin(s[1])*s[2];
        float r_xz = (float) Math.cos(s[1])*s[2];

        c[0] = (float)  Math.cos(s[0])*r_xz;
        c[2] = (float) -Math.sin(s[0])*r_xz;

    }


    // -------------------------------------------------------------------------

    /**
     * Plot a grid on the XZ plane.
     */
    private static void plotGrid() {

        glDisable(GL_LIGHTING);
        {
            glColor4f(0.75f, 0.75f, 0.75f, 0.75f);
            glLineWidth(0.2f);
            glBegin(GL_LINES);
            {
                for (float x = -FAR_PLANE; x <= +FAR_PLANE; x++) {
                    glNormal3f(0.0f, 1.0f, 0.0f);
                    glVertex3f(x, 0.0f, -FAR_PLANE);
                    glVertex3f(x, 0.0f, +FAR_PLANE);
                }
                for (float z = -FAR_PLANE; z <= +FAR_PLANE; z++) {
                    glNormal3f(0.0f, 1.0f, 0.0f);
                    glVertex3f(-FAR_PLANE, 0.0f, z);
                    glVertex3f(+FAR_PLANE, 0.0f, z);
                }
            }
            glEnd();
        }
        glEnable(GL_LIGHTING);

    }

    // -------------------------------------------------------------------------

    /**
     * Plot bounds of animation on ground plane.
     */
    private static void plotBounds() {

        glLineWidth(3.0f);
        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_LINE_LOOP);
        {
            glVertex3f(-10.0f, 0.01f, -10.0f);
            glVertex3f(+10.0f, 0.01f, -10.0f);
            glVertex3f(+10.0f, 0.01f, +10.0f);
            glVertex3f(-10.0f, 0.01f, +10.0f);
        }
        glEnd();

    }

    // -------------------------------------------------------------------------

    /**
     * Plot ball according to its current location
     */
    private static void plotBall() {


        glPushMatrix();
        {
            glColor3f(1.0f, 0.0f, 0.0f);
            glTranslatef(ballPosition[0], ballPosition[1], ballPosition[2]);
            plotUnitSphere(20);
        }
        glPopMatrix();

    }

    // -------------------------------------------------------------------------

    /**
     * Invoke plot methods corresponding to all scene elements.
     */
    private static void plotScene() {

        plotGrid();
        plotBounds();
        plotBall();

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
