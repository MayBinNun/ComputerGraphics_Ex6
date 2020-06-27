package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

import java.util.LinkedList;
import java.util.List;

public class Center implements IRenderable, IIntersectable {

	private SkewedBox bodyBase = new SkewedBox(Specification.C_BASE_LENGTH, Specification.C_BASE_HEIGHT,
			Specification.C_BASE_HEIGHT, Specification.C_DEPTH, Specification.C_DEPTH);
	private SkewedBox backBox = new SkewedBox(Specification.C_BACK_LENGTH, Specification.C_BACK_HEIGHT_1,
			Specification.C_BACK_HEIGHT_2, Specification.C_BACK_DEPTH, Specification.C_BACK_DEPTH);
	private SkewedBox frontBox = new SkewedBox(Specification.C_FRONT_LENGTH, Specification.C_FRONT_HEIGHT_1,
			Specification.C_FRONT_HEIGHT_2, Specification.C_FRONT_DEPTH_1, Specification.C_FRONT_DEPTH_2);
	private SkewedBox sideBox = new SkewedBox(Specification.C_SIDE_LENGTH, Specification.C_SIDE_HEIGHT_1,
			Specification.C_SIDE_HEIGHT_2, Specification.C_SIDE_DEPTH_1, Specification.C_SIDE_DEPTH_2);

	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();
		Materials.SetBlackMetalMaterial(gl);
		bodyBase.render(gl);
		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(Specification.C_BASE_LENGTH / 2.0 - Specification.C_FRONT_LENGTH / 2.0,
				Specification.C_BASE_HEIGHT, 0.0);
		frontBox.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(-Specification.C_BASE_LENGTH / 2.0 + Specification.C_FRONT_LENGTH / 2.0,
				Specification.C_BASE_HEIGHT, 0.0);
		gl.glRotated(180, 0.0, 1.0, 0.0);
		frontBox.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(0.0, Specification.C_BASE_HEIGHT,
				Specification.C_SIDE_LENGTH / 2 + Specification.C_FRONT_DEPTH_1 / 2.0);
		gl.glRotated(90, 0.0, 1.0, 0.0);
		sideBox.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(0.0, Specification.C_BASE_HEIGHT,
				-Specification.C_SIDE_LENGTH / 2 - Specification.C_FRONT_DEPTH_1 / 2.0);
		gl.glRotated(-90, 0.0, 1.0, 0.0);
		sideBox.render(gl);
		gl.glPopMatrix();
		Materials.SetBlackMetalMaterial(gl);
		gl.glPushMatrix();
		gl.glTranslated(
				-Specification.C_BASE_LENGTH / 2.0 + Specification.C_FRONT_LENGTH + Specification.C_BACK_LENGTH / 2.0,
				Specification.C_BASE_HEIGHT, 0.0);
		backBox.render(gl);
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
		double x = Specification.C_LENGTH;
		double y = Specification.C_HIEGHT;
		double z = Specification.C_DEPTH;

		Point point = new Point(0.0, y/2, 0.0);
		double radius= getRadius();

		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();
		BoundingSphere boundingSphere = new BoundingSphere(radius, point);
		boundingSphere.setSphereColore3d(0,1,0);
		res.add(boundingSphere);

		return res;
	}

	private static double getRadius(){
		double x = Specification.C_LENGTH;
		double y = Specification.C_HIEGHT;
		double z = Specification.C_DEPTH;
		return Math.sqrt(Math.pow(x/2, 2.0) + Math.pow(y/2, 2.0) + Math.pow(z/2, 2.0));
	}
}
