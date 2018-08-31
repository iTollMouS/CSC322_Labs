import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.nio.FloatBuffer;

import static com.intellij.ide.SwingCleanuper.cleanup;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * CSC 322: Introduction to Computer Graphics, Fall 2017
 * Lab 05: OpenGL Transformations
 * Starter Code
 * <p>
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
// =====================================================================================================================
public class Lab_05_OpenGL_Transformations {
// =====================================================================================================================

    // -----------------------------------------------------------------------------------------------------------------


    private static void plotModel() {

        plotPine();

    }

    private static void plotPine() {


        glColor3f(0.0f, 1.0f, 0.0f);

        // 1st base
        glPushMatrix();
        {
            glScalef(11.0f, 1.0f, 11.0f);
            glScalef(0.5f, 0.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f); // move above the origin
            plotUnitCube();
        }
        glPopMatrix();

        // 2nd base
        glPushMatrix();
        {
            glTranslatef(0.0f, 1.0f, 0.0f);
            glScalef(9.0f, 1.0f, 9.0f);
            glScalef(0.5f, 0.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCube();
        }
        glPopMatrix();

        // 3rd base
        glPushMatrix();
        {
            glTranslatef(0.0f, 2.0f, 0.0f);
            glScalef(7.0f, 1.0f, 7.0f);
            glScalef(0.5f, 0.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCube();
        }
        glPopMatrix();

        // column 1 middle
        glPushMatrix();
        {
            glTranslatef(0.0f, 3.0f, 0.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 2 middle
        glPushMatrix();
        {
            glTranslatef(0.0f, 3.0f, 2.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 3 middle
        glPushMatrix();
        {
            glTranslatef(0.0f, 3.0f, -2.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 4 right
        glPushMatrix();
        {
            glTranslatef(2.0f, 3.0f, 0.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 5 right
        glPushMatrix();
        {
            glTranslatef(2.0f, 3.0f, 2.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 6 right
        glPushMatrix();
        {
            glTranslatef(2.0f, 3.0f, -2.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 7 left
        glPushMatrix();
        {
            glTranslatef(-2.0f, 3.0f, 0.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 8 left
        glPushMatrix();
        {
            glTranslatef(-2.0f, 3.0f, 2.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // column 9 left
        glPushMatrix();
        {
            glTranslatef(-2.0f, 3.0f, -2.0f);
            glScalef(0.5f, 2.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f);
            plotUnitCylinder(16);
        }
        glPopMatrix();

        // top square before dome
        glPushMatrix();
        {
            glTranslatef(0.0f, 8.0f, 0.0f);
            glScalef(7.0f, 1.0f, 7.0f);
            glScalef(0.5f, 0.5f, 0.5f);
            glTranslatef(0.0f, 1.0f, 0.0f); // one move above the origin
            plotUnitCube();
        }
        glPopMatrix();

        // dome
        glPushMatrix();
        {
            glTranslatef(0.0f, 9.0f, 0.0f);
            glScalef(3.0f, 3.0f, 3.0f);
            plotUnitHemisphere(16);
        }
        glPopMatrix();
    }


    // -----------------------------------------------------------------------------------------------------------------


    /**
     * Plot a regular polygon of n sides lying on the XZ plane and having vertices that are a distance of 1 from the
     * origin.
     *
     * @param n An int.
     */
    private static void plotUnitPolygon(int n) {

        final float inc = (float) ((Math.PI*2.0d)/n);
        float[] p = new float[3];
        glNormal3f(0.0f, 1.0f, 0.0f);
        glBegin(GL_POLYGON);
        {
            // generate n vertices
            float ang = 0.0f;
            for (int i = 0; i < n; i++) {
                setSpherical(ang, 0.0f, 1.0f, p);
                glVertex3f(p[0], p[1], p[2]);
                ang += inc;
            }
        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Render a unit cube.
     */
    private static void plotUnitCube() {

        // drawing quads (squares)
        glBegin(GL_QUADS);

        // front x face
        glNormal3f(1.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);

        // back x face
        glNormal3f(-1.0f, 0.0f, 0.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);

        // front y face
        glNormal3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);

        // back y face
        glNormal3f(0.0f, -1.0f, 0.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);

        // front z face
        glNormal3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);

        // back z face
        glNormal3f(0.0f, 0.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);

        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Plot a cone of height and radius 1 made up of n triangular faces.
     *
     * @param n An int.
     */
    private static void plotUnitCone(int n) {

        // p->q will represent the current base edge we are on
        final float angleIncrement = (float) ((Math.PI*2.0d)/n);
        float angle = angleIncrement;
        float[] p = new float[3];
        float[] q = new float[3];
        setSpherical(0.0f, 0.0f, 1.0f, p);
        setSpherical(angle, 0.0f, 1.0f, q);

        // plot triangle faces
        glShadeModel(GL_SMOOTH);
        glBegin(GL_TRIANGLES);
        {
            for (int i = 0; i < n; i++) {

                // plot current triangle
                glNormal3f(p[0], p[1], p[2]);
                glVertex3f(p[0], p[1], p[2]);
                glNormal3f(q[0], q[1], q[2]);
                glVertex3f(q[0], q[1], q[2]);
                glNormal3f(0.0f, 1.0f, 0.0f);
                glVertex3f(0.0f, 1.0f, 0.0f);

                // go to next base edge
                set(q, p);
                angle += angleIncrement;
                setSpherical(angle, 0.0f, 1.0f, q);

            }
        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Plot an uncapped unit cylinder with n sides. The extrema of the cylinder will be at Y = +/- 1.
     *
     * @param n An int.
     */
    private static void plotUnitCylinder(int n) {

        // p->q will represent the current base edge we are on
        float[] p = new float[3];
        float[] q = new float[3];
        setSpherical(0.0f, 0.0f, 1.0f, q);

        // plot triangle faces
        glBegin(GL_QUADS);
        for (int i = 1; i <= n; i++) {

            // go to next base edge
            set(q, p);
            setSpherical((TURN*i)/n, 0.0f, 1.0f, q);

            // plot current quad
            glNormal3f(p[0], 0.0f, p[2]); glVertex3f(p[0], -1.0f, p[2]);
            glNormal3f(q[0], 0.0f, q[2]); glVertex3f(q[0], -1.0f, q[2]);
            glNormal3f(q[0], 0.0f, q[2]); glVertex3f(q[0], +1.0f, q[2]);
            glNormal3f(p[0], 0.0f, p[2]); glVertex3f(p[0], +1.0f, p[2]);

        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Plot a unit sphere with n bands of azimuth and n/2 bands of elevation.
     *
     * @param n Number of azimuth bands.
     */
    private static void plotUnitSphere(int n) {

        // p->q will represent the current edge we are on
        float[] p = new float[3];
        float[] q = new float[3];

        float theta, phi;

        // north pole cap
        glBegin(GL_TRIANGLES);
        {
            phi = TURN/4 - TURN/n;
            setSpherical(0.0f,  phi, 1.0f, q);
            for (int i = 1; i <= n; i++) {

                // set up edge
                theta = (TURN*i)/n;
                set(q, p);
                setSpherical(theta, phi, 1.0f, q);

                // plot triangle
                glNormal3f(p[0], p[1], p[2]); glVertex3f(p[0], p[1], p[2]);
                glNormal3f(q[0], q[1], q[2]); glVertex3f(q[0], q[1], q[2]);
                glNormal3f(0.0f, 1.0f, 0.0f); glVertex3f(0.0f, 1.0f, 0.0f);

            }

        }
        glEnd();

        // middle bands
        glBegin(GL_QUADS);
        {

            float[] r = new float[3];
            float[] s = new float[3];
            for (int i = 2; i < (n/2); i++) {
                for (int j = 0; j < n; j++) {

                    // update theta phi
                    phi = TURN/4 - (TURN*i)/n;
                    theta = (TURN*j)/n;

                    // set point locations
                    setSpherical(theta,          phi,          1.0f, p);
                    setSpherical(theta + TURN/n, phi,          1.0f, q);
                    setSpherical(theta + TURN/n, phi + TURN/n, 1.0f, r);
                    setSpherical(theta,          phi + TURN/n, 1.0f, s);

                    // plot quad
                    glNormal3f(p[0], p[1], p[2]); glVertex3f(p[0], p[1], p[2]);
                    glNormal3f(q[0], q[1], q[2]); glVertex3f(q[0], q[1], q[2]);
                    glNormal3f(r[0], r[1], r[2]); glVertex3f(r[0], r[1], r[2]);
                    glNormal3f(s[0], s[1], s[2]); glVertex3f(s[0], s[1], s[2]);

                }

            }

        }
        glEnd();

        // south pole cap
        glBegin(GL_TRIANGLES);
        {
            phi = -TURN/4 + TURN/n;
            setSpherical(0.0f,  phi, 1.0f, q);
            for (int i = 1; i <= n; i++) {

                // set up edge
                theta = (TURN*i)/n;
                set(q, p);
                setSpherical(theta, phi, 1.0f, q);

                // plot triangle
                glNormal3f(0.0f, -1.0f, 0.0f); glVertex3f(0.0f, -1.0f, 0.0f);
                glNormal3f(q[0], q[1], q[2]); glVertex3f(q[0], q[1], q[2]);
                glNormal3f(p[0], p[1], p[2]); glVertex3f(p[0], p[1], p[2]);

            }

        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Plot a unit sphere with n bands of azimuth and n/2 bands of elevation.
     *
     * @param n Number of azimuth bands.
     */
    private static void plotUnitHemisphere(int n) {

        // p->q will represent the current edge we are on
        float[] p = new float[3];
        float[] q = new float[3];

        float theta, phi;

        // north pole cap
        glBegin(GL_TRIANGLES);
        {
            phi = TURN/4 - TURN/n;
            setSpherical(0.0f,  phi, 1.0f, q);
            for (int i = 1; i <= n; i++) {

                // set up edge
                theta = (TURN*i)/n;
                set(q, p);
                setSpherical(theta, phi, 1.0f, q);

                // plot triangle
                glNormal3f(p[0], p[1], p[2]); glVertex3f(p[0], p[1], p[2]);
                glNormal3f(q[0], q[1], q[2]); glVertex3f(q[0], q[1], q[2]);
                glNormal3f(0.0f, 1.0f, 0.0f); glVertex3f(0.0f, 1.0f, 0.0f);

            }

        }
        glEnd();

        // middle bands
        glBegin(GL_QUADS);
        {

            float[] r = new float[3];
            float[] s = new float[3];
            for (int i = 2; i <= (n/4); i++) {
                for (int j = 0; j < n; j++) {

                    // update theta phi
                    phi = TURN/4 - (TURN*i)/n;
                    theta = (TURN*j)/n;

                    // set point locations
                    setSpherical(theta,          phi,          1.0f, p);
                    setSpherical(theta + TURN/n, phi,          1.0f, q);
                    setSpherical(theta + TURN/n, phi + TURN/n, 1.0f, r);
                    setSpherical(theta,          phi + TURN/n, 1.0f, s);

                    // plot quad
                    glNormal3f(p[0], p[1], p[2]); glVertex3f(p[0], p[1], p[2]);
                    glNormal3f(q[0], q[1], q[2]); glVertex3f(q[0], q[1], q[2]);
                    glNormal3f(r[0], r[1], r[2]); glVertex3f(r[0], r[1], r[2]);
                    glNormal3f(s[0], s[1], s[2]); glVertex3f(s[0], s[1], s[2]);

                }

            }

        }
        glEnd();

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Set the components of point dest based on the spherical parameters theta, phi, and r.
     *
     * @param theta The azimuth about the Y axis in radians.
     * @param phi   The elevation above the XZ plane in radians.
     * @param r     The distance from the origin.
     * @param dest  Destination point.
     */
    private static void setSpherical(float theta, float phi, float r, float[] dest) {

        dest[1] =    (float) sin(phi)*r;
        float r_xz = (float) cos(phi)*r;
        dest[0] =    (float) cos(theta)*r_xz;
        dest[2] =    (float) sin(theta)*r_xz;

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Set the coordinate values of dest to x, y, and z.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @param dest The destination point.
     */
    private static void set(float x, float y, float z, float[] dest) { dest[0] = x; dest[1] = y; dest[2] = z; }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Set the values of dest to those of src.
     *
     * @param src  Source tuple.
     * @param dest Destination tuple.
     */
    private static void set(float[] src, float[] dest) { System.arraycopy(src, 0, dest, 0, src.length); }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Float version of PI.
     */
    private static final float PI = (float) Math.PI;

    /**
     * A full turn around the unit circumference.
     */
    private static final float TURN = (float) (2.0d*Math.PI);

    /**
     * Application Title
     */
    private static final String APP_TITLE = "Lab_05_OpenGL_Transformations";

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
    private static final FloatBuffer lightAmbient = floatBuffer(0.2f, 0.2f, 0.2f, 1.0f);

    /**
     * Diffuse light RGB intensities.
     */
    private static final FloatBuffer lightDiffuse = floatBuffer(0.5f, 0.5f, 0.5f, 1.0f);

    /**
     * Specular light RGB intensities.
     */
    private static final FloatBuffer lightSpecular = floatBuffer(0.1f, 0.1f, 0.1f, 1.0f);

    /**
     * Ambient material properties.
     */
    private static final FloatBuffer materialAmbient = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Diffuse material properties.
     */
    private static final FloatBuffer materialDiffuse = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);

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
    private static float cameraAzimuth = 37.5f;

    /**
     * Camera elevation degrees.
     */
    private static float cameraElevation = -30.0f;

    /**
     * Camera distance from origin.
     */
    private static float cameraDistance = 50.0f;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Lock out use of constructor. This class is static.
     */
    private Lab_05_OpenGL_Transformations() { }

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

        // set background color
        glClearColor(1.0f, 1.0f, 1.0f, 10.0f);

        // get display size
        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        // set the viewport
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);

        // perspective transformation
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = ((float) width)/height;
        gluPerspective(45.0f, aspectRatio, 0.1f, 100.0f);

        glShadeModel(GL_SMOOTH);

        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(1.0f);

        glEnable(GL_AUTO_NORMAL);
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

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

                if (Display.isVisible() && Display.isDirty()) {
                    // Only bother rendering if the window is visible and dirty
                    render();
                }

            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Clean up before exit.
     */
    //private static void cleanup() { Display.destroy(); }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Handle input.
     */
    private static void logic() {

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

        glTranslatef(0.0f, 0.0f, -cameraDistance);
        glRotatef(-cameraElevation, 1.0f, 0.0f, 0.0f);
        glRotatef(-cameraAzimuth, 0.0f, 1.0f, 0.0f);

        plotModel();

        // pop out of object space and back into camera space
        glPopMatrix();

        // place light in camera space
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);

        // flush the data
        glFlush();
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


