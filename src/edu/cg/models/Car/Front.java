package edu.cg.models.Car;

import com.jogamp.opengl.GL2;

import java.util.LinkedList;
import java.util.List;

import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;

public class Front implements IRenderable, IIntersectable {
    private FrontHood hood = new FrontHood();
    private PairOfWheels wheels = new PairOfWheels();
    private FrontBumber frontBumber = new FrontBumber();

    @Override
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(Specification.F_BUMPER_LENGTH / 2.0 + 2 * Specification.TIRE_RADIUS, 0.0, 0.0);
        frontBumber.render(gl);
        gl.glPopMatrix();
        // the car.
        gl.glPushMatrix();
        // Render hood - Use Red Material.
        gl.glTranslated(-Specification.F_LENGTH / 2.0 + Specification.F_HOOD_LENGTH / 2.0, 0.0, 0.0);
        hood.render(gl);
        // Render the wheels.
        gl.glTranslated(Specification.F_HOOD_LENGTH / 2.0 - 1.25 * Specification.TIRE_RADIUS,
                0.5 * Specification.TIRE_RADIUS, 0.0);
        wheels.render(gl);
        gl.glPopMatrix();
    }

    @Override
    public void init(GL2 gl) {
    }

    @Override
    public void destroy(GL2 gl) {

    }

    @Override
    public List<BoundingSphere> getBoundingSpheres() {

        Point point = new Point(0.0, Specification.F_HEIGHT/2, 0.0);
        double radius= getRadius();

        LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();
        BoundingSphere boundingSphere = new BoundingSphere(radius, point);
        boundingSphere.setSphereColore3d(1, 0, 0);
        res.add(boundingSphere);

        return res;
    }

    private static double getRadius(){
        double x = Specification.F_LENGTH;
        double y = Specification.F_HEIGHT;
        double z = Specification.F_DEPTH;
        return Math.sqrt(Math.pow(x/2, 2.0) + Math.pow(y/2, 2.0) + Math.pow(z/2, 2.0));
    }

    @Override
    public String toString() {
        return "CarFront";
    }
}
