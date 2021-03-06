package edu.cg;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.models.BoundingSphere;
import edu.cg.models.Car.F1Car;
import edu.cg.models.Track;
import edu.cg.models.TrackSegment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static edu.cg.models.Car.Specification.*;
import static edu.cg.models.Car.Specification.F_BUMPER_LENGTH;

/**
 * An OpenGL 3D Game.
 */
public class NeedForSpeed implements GLEventListener {
    private GameState gameState = null; // Tracks the car movement and orientation
    private F1Car car = null; // The F1 car we want to render
    private Vec carCameraTranslation = null; // The accumulated translation that should be applied on the car, camera
    // and light sources
    private Track gameTrack = null; // The game track we want to render
    private FPSAnimator ani; // This object is responsible to redraw the model with a constant FPS
    private Component glPanel; // The canvas we draw on.
    private boolean isModelInitialized = false; // Whether model.init() was called.
    private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
    private boolean isBirdseyeView = false; // Indicates whether the camera is looking from above on the scene or
    // looking
    // towards the car direction.
    // TODO: add fields as you want. For example:
    // - Car initial position (should be fixed).
    // - Camera initial position (should be fixed)
    // - Different camera settings
    // - Light colors
    // Or in short anything reusable - this make it easier for your to keep track of your implementation.
    private double[] carInitialPosition;

    public NeedForSpeed(Component glPanel) {
        this.glPanel = glPanel;
        gameState = new GameState();
        gameTrack = new Track();
        carCameraTranslation = new Vec(0.0);
        car = new F1Car();
        carInitialPosition = new double[]{0.0, 1.25, -5.0};
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        if (!isModelInitialized) {
            initModel(gl);
        }
        if (isDayMode) {
            gl.glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
        } else {
            gl.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);

        }
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        // TODO: This is the flow in which we render the scene.
        // Step (1) Update the accumulated translation that needs to be
        // applied on the car, camera and light sources.
        updateCarCameraTranslation(gl);
        // Step (2) Position the camera and setup its orientation
        setupCamera(gl);
        // Step (3) setup the lights.
        setupLights(gl);
        // Step (4) render the car.
        renderCar(gl);
        // Step (5) render the track.
        renderTrack(gl);
        // Step (6) check collision. Note this has nothing to do with OpenGL.
        if (checkCollision()) {
            JOptionPane.showMessageDialog(this.glPanel, "Game is Over");
            this.gameState.resetGameState();
            this.carCameraTranslation = new Vec(0.0);
        }

    }

	/**
	 * @return Checks if the car intersects the one of the boxes on the track.
	 */
	private boolean checkCollision() {
		// You can get the bounding spheres of the track by invoking:
		List<BoundingSphere> trackBoundingSpheres = gameTrack.getBoundingSpheres();
		List<BoundingSphere> carBoundingSpheres = car.getBoundingSpheres();
		boolean isIntersect = false;

		//set each part of the car position.
		double dx,dy,dz;
		for (BoundingSphere carSpherePart : carBoundingSpheres){
			//scale the radius
			carSpherePart.setRadius(carSpherePart.getRadius() * 4.0);
			double degreeToRotate = 90.0 + this.gameState.getCarRotation();
			carSpherePart.rotateTheCenterToY(degreeToRotate);
			//translate the center point
			dx = this.carInitialPosition[0] + this.carCameraTranslation.x;
			dy = this.carInitialPosition[1] + this.carCameraTranslation.y;
			dz = this.carInitialPosition[2] + this.carCameraTranslation.z;
			carSpherePart.translateCenter(dx,dy,dz);
		}

		//check if the car collides into one of the boxes.
		BoundingSphere wholeCar,frontCar,centerCar, backCar;
		for (BoundingSphere boxInTrack : trackBoundingSpheres){
			wholeCar = carBoundingSpheres.get(0);
			frontCar = carBoundingSpheres.get(1);
			centerCar = carBoundingSpheres.get(2);
			backCar = carBoundingSpheres.get(3);
			boolean isIntersectWholeCar = boxInTrack.checkIntersection(wholeCar);
			if (isIntersectWholeCar){
				if (boxInTrack.checkIntersection(frontCar) || boxInTrack.checkIntersection(centerCar) || boxInTrack.checkIntersection(backCar)){
					isIntersect = true;
				}
			}
		}

		return isIntersect;
	}

    private void updateCarCameraTranslation(GL2 gl) {
        // Update the car and camera translation values (not the ModelView-Matrix).
        // - Always keep track of the car offset relative to the starting
        // point.
        // - Change the track segments here.
        Vec ret = gameState.getNextTranslation();
        carCameraTranslation = carCameraTranslation.add(ret);
        double dx = Math.max(carCameraTranslation.x, -TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 - 2);
        carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 + 2);
        if (Math.abs(carCameraTranslation.z) >= TrackSegment.TRACK_LENGTH + 10.0) {
            carCameraTranslation.z = -(float) (Math.abs(carCameraTranslation.z) % TrackSegment.TRACK_LENGTH);
            gameTrack.changeTrack(gl);
        }
    }

    private void setupCamera(GL2 gl) {
        GLU glu = new GLU();

        double ctx,cty,crz;
        ctx = this.carCameraTranslation.x;

        if (isBirdseyeView) {
            cty = this.carCameraTranslation.y;
            crz = this.carCameraTranslation.z -22;
            glu.gluLookAt(ctx, cty+37.0, crz, ctx, cty-10.0, crz, 0.0, 0.0, -1.0);
        } else {
            cty = this.carCameraTranslation.y + 3.0;
            crz = this.carCameraTranslation.z + 6.0;
            glu.gluLookAt(ctx, cty, crz-2.0, ctx, cty + 1.0, crz - 15.0, 0.0, 1.0, 0.0);
        }
    }

    private void setupLights(GL2 gl) {
        if (isDayMode) {
            gl.glDisable(GL2.GL_LIGHT0);
            gl.glDisable(GL2.GL_LIGHT1);

            float[] colorOfSun = {1.0f, 1.0f, 1.0f, 1.0f};
            Vec sunDirection = new Vec(0.0, 1.0, 1.0).normalize();
            gl.glDisable(GL2.GL_LIGHT1);

            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, colorOfSun, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, colorOfSun, 0);

            final float[] position = {sunDirection.x, sunDirection.y, sunDirection.z, 0.0f};
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, colorOfSun, 0);

            gl.glEnable(GL2.GL_LIGHT1);
        } else {

            gl.glDisable(GL2.GL_LIGHT1);

            float xSL = (float)this.carInitialPosition[0] +this.carCameraTranslation.x ;
            float ySL = (float)this.carInitialPosition[1] +this.carCameraTranslation.y + 0.16f;
            float zSL = (float)this.carInitialPosition[2] +this.carCameraTranslation.z - (float)(5 * ((C_LENGTH / 2) + F_LENGTH)- (F_BUMPER_LENGTH / 2))-1.5f;;

            float[] hLight1 = new float[]{xSL+0.25f,ySL,zSL, 1.0f};
            initialSpotLight(gl,GL2.GL_LIGHT0,hLight1);
            float[] hLight2 = new float[]{xSL-0.25f,ySL,zSL, 1.0f};
            initialSpotLight(gl,GL2.GL_LIGHT1,hLight2);
        }

    }

    private void initialSpotLight(GL2 gl, int light , float[] position ){
        double angle = gameState.getCarRotation();
        float[] direction = new float[]{((float)Math.sin(Math.toRadians(angle))), -2.0f, -(float)Math.cos((Math.toRadians(angle)))};
        float[] lightColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightfv(light, GL2.GL_POSITION, position, 0);
        gl.glLightfv(light, GL2.GL_SPECULAR, lightColor, 0);
        gl.glLightfv(light, GL2.GL_DIFFUSE, lightColor, 0);
        gl.glLightfv(light, GL2.GL_SPOT_DIRECTION,direction, 0);
        gl.glLightf(light, GL2.GL_SPOT_CUTOFF, 80.0f);
        gl.glEnable(light);
    }

    private void renderTrack(GL2 gl) {
        // * Note: the track is not translated. It should be fixed.
        gl.glPushMatrix();
        gameTrack.render(gl);
        gl.glPopMatrix();
    }

    private void renderCar(GL2 gl) {
        double dx, dy, dz;
        double newCarRotation = 90.0 - this.gameState.getCarRotation();
        dx = this.carInitialPosition[0] + this.carCameraTranslation.x;
        dy = this.carInitialPosition[1] + this.carCameraTranslation.y;
        dz = this.carInitialPosition[2] + this.carCameraTranslation.z;
        gl.glPushMatrix();

        gl.glTranslated(dx, dy, dz);
        //rotate and scale the car
        gl.glRotated(newCarRotation, 0.0, 1.0, 0.0);
        gl.glScaled(4.0,4.0,4.0);
        this.car.render(gl);

        gl.glPopMatrix();
    }

    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Initialize display callback timer
        ani = new FPSAnimator(30, true);
        ani.add(drawable);
        glPanel.repaint();

        initModel(gl);
        ani.start();
    }

    public void initModel(GL2 gl) {
        gl.glCullFace(GL2.GL_BACK);
        gl.glEnable(GL2.GL_CULL_FACE);

        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_SMOOTH);

        car.init(gl);
        gameTrack.init(gl);
        isModelInitialized = true;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU glu = new GLU();
        double aspect = width / height;

        if (this.isBirdseyeView) {
            glu.gluPerspective(60.0, aspect, 2.0, 500.0);

        } else {
            glu.gluPerspective(60.0, aspect, 2.0, 500.0);

        }


    }

    /**
     * Start redrawing the scene with 30 FPS
     */
    public void startAnimation() {
        if (!ani.isAnimating())
            ani.start();
    }

    /**
     * Stop redrawing the scene with 30 FPS
     */
    public void stopAnimation() {
        if (ani.isAnimating())
            ani.stop();
    }

    public void toggleNightMode() {
        isDayMode = !isDayMode;
    }

    public void changeViewMode() {
        isBirdseyeView = !isBirdseyeView;
    }

}
