package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

public class FrontBumber implements IRenderable {
    private SkewedBox bumperWing = new SkewedBox(Specification.F_BUMPER_LENGTH, Specification.F_BUMPER_WINGS_HEIGHT_1, Specification.F_BUMPER_HEIGHT_2, Specification.F_BUMPER_WINGS_DEPTH, Specification.F_BUMPER_WINGS_DEPTH);
    private SkewedBox bumperBase = new SkewedBox(Specification.F_BUMPER_LENGTH, Specification.F_BUMPER_HEIGHT_1, Specification.F_BUMPER_HEIGHT_2, Specification.F_BUMPER_DEPTH, Specification.F_BUMPER_DEPTH);


    @Override
    public void render(GL2 gl) {

        gl.glPushMatrix();
        Materials.SetBlackMetalMaterial(gl);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glTranslated(0, 0, Specification.F_BUMPER_DEPTH / 2 + (Specification.F_BUMPER_WINGS_DEPTH / 2));
        bumperWing.render(gl);
        gl.glColor3d(1, 1, 0);
        renderHeadlight(gl);

        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslated(0, 0, -Specification.F_BUMPER_DEPTH / 2 - (Specification.F_BUMPER_WINGS_DEPTH / 2));
        Materials.SetBlackMetalMaterial(gl);
        bumperWing.render(gl);
        gl.glColor3d(1, 1, 0);
        renderHeadlight(gl);
        gl.glPopMatrix();

        Materials.SetRedMetalMaterial(gl);
        bumperBase.render(gl);

    }

    @Override
    public void init(GL2 gl) {
    }

    @Override
    public void destroy(GL2 gl) {

    }

    public void renderHeadlight(GL2 gl) {
        GLU glu = new GLU();
        GLUquadric sphere = glu.gluNewQuadric();
        gl.glTranslated(0, Specification.F_BUMPER_LENGTH / 3, 0);
        gl.glColor3d(1, 1, 0);
        glu.gluSphere(sphere, 0.03, 20, 20);

    }

    @Override
    public String toString() {
        return "FrontBumper";
    }

}
