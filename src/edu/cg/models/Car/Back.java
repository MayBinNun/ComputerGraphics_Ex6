package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import java.util.LinkedList;
import java.util.List;

import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

public class Back implements IRenderable, IIntersectable {
    private SkewedBox baseBox = new SkewedBox(Specification.B_BASE_LENGTH, Specification.B_BASE_HEIGHT,
            Specification.B_BASE_HEIGHT, Specification.B_BASE_DEPTH, Specification.B_BASE_DEPTH);
    private SkewedBox backBox = new SkewedBox(Specification.B_LENGTH, Specification.B_HEIGHT_1,
            Specification.B_HEIGHT_2, Specification.B_DEPTH_1, Specification.B_DEPTH_2);
    private PairOfWheels wheels = new PairOfWheels();
    private Spolier spoiler = new Spolier();

    @Override
    public void render(GL2 gl) {
        gl.glPushMatrix();
        Materials.SetBlackMetalMaterial(gl);
        gl.glTranslated(Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0, 0.0, 0.0);
        baseBox.render(gl);
        Materials.SetRedMetalMaterial(gl);
        gl.glTranslated(-1.0 * (Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0),
                Specification.B_BASE_HEIGHT, 0.0);
        backBox.render(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslated(-Specification.B_LENGTH / 2.0 + Specification.TIRE_RADIUS, 0.5 * Specification.TIRE_RADIUS,
                0.0);
        wheels.render(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        renderExhaust(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslated(-Specification.B_LENGTH / 2.0 + 0.5 * Specification.S_LENGTH,
                0.5 * (Specification.B_HEIGHT_1 + Specification.B_HEIGHT_2), 0.0);
        spoiler.render(gl);
        gl.glPopMatrix();
    }

    @Override
    public void init(GL2 gl) {

    }

    @Override
    public void destroy(GL2 gl) {

    }

    public static void renderExhaust(GL2 gl) {
        GLU glu = new GLU();
        GLUquadric quad = glu.gluNewQuadric();
        Materials.SetBlackMetalMaterial(gl);
        gl.glTranslated(-Specification.B_BASE_LENGTH + 0.08, 0.02, 0.0);
        gl.glRotated(90, 0, 1, 0);

        glu.gluCylinder(quad, Specification.PAIR_OF_WHEELS_ROD_RADIUS, Specification.PAIR_OF_WHEELS_ROD_RADIUS, 0.05, 20, 1);

    }


    @Override
    public List<BoundingSphere> getBoundingSpheres() {
        Point point = new Point(0.0, Specification.B_HEIGHT / 2, 0.0);
        double radius = getRadius();

        LinkedList<BoundingSphere> res = new LinkedList<>();
        BoundingSphere boundingSphere = new BoundingSphere(radius, point);
        boundingSphere.setSphereColore3d(0, 0, 1);
        res.add(boundingSphere);

        return res;
    }

    private static double getRadius() {
        double x = Specification.B_LENGTH;
        double y = Specification.B_HEIGHT;
        double z = Specification.B_DEPTH;
        return Math.sqrt(Math.pow(x / 2, 2.0) + Math.pow(y / 2, 2.0) + Math.pow(z / 2, 2.0));
    }

}
