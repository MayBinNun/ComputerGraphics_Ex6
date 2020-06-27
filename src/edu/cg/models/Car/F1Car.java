package edu.cg.models.Car;

import com.jogamp.opengl.GL2;

import java.util.LinkedList;
import java.util.List;

import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;

/**
 * A F1 Racing Car.
 */
public class F1Car implements IRenderable, IIntersectable {
    // Remember to include a ReadMe file specifying what you implemented.
    Center carCenter = new Center();
    Back carBack = new Back();
    Front carFront = new Front();

    @Override
    public void render(GL2 gl) {
        carCenter.render(gl);
        gl.glPushMatrix();
        gl.glTranslated(-Specification.B_LENGTH / 2.0 - Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
        carBack.render(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslated(Specification.F_LENGTH / 2.0 + Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
        carFront.render(gl);
        gl.glPopMatrix();

    }

    @Override
    public String toString() {
        return "F1Car";
    }

    @Override
    public void init(GL2 gl) {

    }

    @Override
    public void destroy(GL2 gl) {

    }

    @Override
    public List<BoundingSphere> getBoundingSpheres() {
        // s1 -> s2 -> s3 -> s4
        LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();


        // s1 - sphere bounding the whole car
        BoundingSphere carBoundingSphere = createCarBoundingSphere();
        res.add(carBoundingSphere);

        // s2 - sphere bounding the car front
        for (BoundingSphere boundingSphere : carFront.getBoundingSpheres()) {
            boundingSphere.translateCenter(Specification.F_LENGTH / 2 + (Specification.C_LENGTH / 2), 0, 0);
            res.add(boundingSphere);
        }

        // s3 - sphere bounding the car center
        for (BoundingSphere boundingSphere : carCenter.getBoundingSpheres()) {
            res.add(boundingSphere);
        }

        // s4 - sphere bounding the car back
        for (BoundingSphere boundingSphere : carBack.getBoundingSpheres()) {
            boundingSphere.translateCenter(-Specification.B_LENGTH / 2 - (Specification.C_LENGTH / 2), 0, 0);
            res.add(boundingSphere);
        }


        return res;
    }

    public static BoundingSphere createCarBoundingSphere() {
        Double x = Specification.B_LENGTH + Specification.C_LENGTH + Specification.F_LENGTH;
        Double y = Math.max(Specification.B_HEIGHT, Math.max(Specification.C_HIEGHT, Specification.F_HEIGHT));
        Double z = Math.max(Specification.B_DEPTH, Math.max(Specification.F_DEPTH, Specification.F_DEPTH));
        Point s1Center = new Point(0, y / 2, 0);
        Double radius = Math.sqrt(Math.pow(x / 2, 2.0) + Math.pow(y / 2, 2.0) + Math.pow(z / 2, 2.0));
        BoundingSphere carBoundingSphere = new BoundingSphere(radius, s1Center);
        carBoundingSphere.setSphereColore3d(0, 0, 0);
        return carBoundingSphere;
    }
}
