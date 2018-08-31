
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.input.Mouse;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import static org.lwjgl.util.glu.GLU.*;
import static org.lwjgl.opengl.GL11.*;

public class Viewer {

    // window title
    public static final String APP_TITLE = "Viewer";

    // desired frame rate
    private static final int FRAMERATE = 60;

    // light position and attributes
    private static final FloatBuffer lightPosition = floatBuffer(3.0f, 4.0f, 5.0f, 1.0f);
    private static final FloatBuffer lightAmbient  = floatBuffer(0.2f, 0.2f, 0.2f, 1.0f);
    private static final FloatBuffer lightDiffuse  = floatBuffer(0.5f, 0.5f, 0.5f, 1.0f);
    private static final FloatBuffer lightSpecular = floatBuffer(0.1f, 0.1f, 0.1f, 1.0f);

    // material properties
    private static final FloatBuffer materialAmbient   = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);
    private static final FloatBuffer materialDiffuse   = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);
    private static final FloatBuffer materialSpecular  = floatBuffer(1.0f, 1.0f, 1.0f, 1.0f);
    private static final float       materialShininess = 8.0f;

    // exit flag
    private static boolean finished;

    // camera positition
    private static float cameraAzimuth   =  37.5f;
    private static float cameraElevation = -30.0f;
    private static float cameraDistance  =   6.0f;

    // no constructor needed - this class is static
    private Viewer() {
    }

    public static void main(String[] args) {
        try {
            init();
            run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Sys.alert(APP_TITLE, "An error occured and the program will exit.");
        } finally {
            cleanup();
        }

        System.exit(0);
    }

    // Initialize display and opengl properties.
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
        glMaterial(GL_FRONT, GL_AMBIENT,  materialAmbient);
        glMaterial(GL_FRONT, GL_DIFFUSE,  materialDiffuse);
        glMaterial(GL_FRONT, GL_SPECULAR, materialSpecular);
        glMaterialf(GL_FRONT, GL_SHININESS, materialShininess);

        // allow changing colors while keeping the above material
        glEnable(GL_COLOR_MATERIAL);

        // set gl functionality
        glEnable(GL_DEPTH_TEST);

        // set background color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // get display size
        int width  = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        // perspective transformation
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = ((float) width)/height;
        gluPerspective(45.0f, aspectRatio, 0.1f, 100.0f);

        // set the viewport
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);

    }

    // Main loop.
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

    // Clean up before exit.
    private static void cleanup() {

        // Close the window
        Display.destroy();

    }

    // Handle input.
    private static void logic() {

        // escape to quit
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
        }

        // mouse event catcher
        while (Mouse.next())
        {
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

    // Render model from current view.
    private static void render() {

        // clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // viewing transformation (bottom of the modelview stack)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // perform transformation according to viewing angle; i.e. undo camera
        // transformation (in reverse order) and go into object space
        glPushMatrix();

        glTranslatef(0.0f, 0.0f, -cameraDistance);
        glRotatef(-cameraElevation, 1.0f, 0.0f, 0.0f);
        glRotatef(-cameraAzimuth,   0.0f, 1.0f, 0.0f);

        // set drawing color
        glColor3f(1.0f, 0.0f, 0.0f);

        renderunitSphere();

        // pop out of object space and back into camera space
        glPopMatrix();

        // place light in camera space
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);

        // flush the data
        glFlush();
    }
    private static void renderunitSphere(){

        glShadeModel(GL_FLAT);

        glEnable(GL_POINT_SMOOTH);
        glBegin(GL_QUADS);
        int n = 300;
        int m = 150;
        point3f p = new point3f();
        point3f[] unitSpherePOints = p.pointCloudSphere(n, m);

        for(int i = 0; i < unitSpherePOints.length; i++)
        {
                  glVertex3f(unitSpherePOints[i].getX(), unitSpherePOints[i].gety(), unitSpherePOints[i].getz());
        }

        glEnd();

    }
    // Render a unit cube.
    private static void renderUnitCube() {

        // set flat shading
        glShadeModel(GL_FLAT);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(7.0f);

        // drawing quads (squares)
        glBegin(GL_POINTS);

        // front x face
        glNormal3f( 1.0f,  0.0f,  0.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f,  1.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);

        // back x face
        glNormal3f(-1.0f,  0.0f,  0.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);

        // front y face
        glNormal3f( 0.0f,  1.0f,  0.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);

        // back y face
        glNormal3f( 0.0f, -1.0f,  0.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);

        // front z face
        glNormal3f( 0.0f,  0.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);
        glVertex3f( 1.0f,  1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);

        // back z face
        glNormal3f( 0.0f,  0.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);

        glEnd();
    }

    // Render the basis vectors of the coordinate frame: x (red), y (blue), and
    // z (green). Can be useful for debugging purposes.
    private static void renderCoordinateFrame() {

        // temporarily disable lighting
        glDisable(GL_LIGHTING);

        // draw thicker lines for clarity
        glLineWidth(3.0f);

        glBegin(GL_LINES);

        // x basis vector (red)
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 0.0f, 0.0f);

        // y basis vector (green)
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 1.0f, 0.0f);

        // z basis vector (blue)
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 1.0f);

        glEnd();

        // renable lighting
        glEnable(GL_LIGHTING);
    }

    // Utility function to easily create float buffers.
    private static FloatBuffer floatBuffer(float f1, float f2, float f3, float f4) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(4);
        fb.put(f1).put(f2).put(f3).put(f4).flip();
        return fb;
    }
}
