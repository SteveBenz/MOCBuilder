package Connectivity;

import java.util.ArrayList;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;

//depreciated too slow.
public class LDrawCollisionDetectionUtility {

	// public static void main(String args[]){
	// Vector3f[] a = new Vector3f[3];
	// Vector3f[] b = new Vector3f[3];
	// a[0] = new Vector3f(-100, 11, 0);
	// a[1] = new Vector3f(100, 0, 00);
	// a[2] = new Vector3f(0, 0, -100);
	//
	// b[0] = new Vector3f(-100, 10, 0);
	// b[1] = new Vector3f(100, 10, 0);
	// b[2] = new Vector3f(0, 10, -100);
	// System.out.println(NoDivTriTriIsect(a, b));
	// }
	public static boolean isCollide(LDrawPart partA, Matrix4 transformA, LDrawPart partB, Matrix4 transformB) {
		LDrawVertexCollector vcA = new LDrawVertexCollector(partA, transformA);
		LDrawVertexCollector vcB = new LDrawVertexCollector(partB, transformB);

		ArrayList<Vector3f> triListA = vcA.getTriList();
		ArrayList<Vector3f> quadListA = vcA.getQuadList();

		ArrayList<Vector3f> triListB = vcB.getTriList();
		ArrayList<Vector3f> quadListB = vcB.getQuadList();

		Vector3f[] a = new Vector3f[3];
		Vector3f[] b = new Vector3f[3];

		for (int i = 0; i < triListA.size(); i += 3) {
			for (int t = 0; t < 3; t++)
				a[t] = triListA.get(i + t);
			
			for (int j = 0; j < triListB.size(); j += 3) {
				for (int t = 0; t < 3; t++)
					b[t] = triListB.get(j + t);
				if (NoDivTriTriIsect(a, b))
					return true;
			}
			
			for (int j = 0; j < quadListB.size(); j += 4) {
				for (int t = 0; t < 3; t++)
					b[t] = quadListB.get(j + t);
				if (NoDivTriTriIsect(a, b))
					return true;

				for (int t = 0; t < 3; t++)
					b[t] = quadListB.get(j + ((t + 2) % 4));
				if (NoDivTriTriIsect(a, b))
					return true;
			}
		}

		for (int i = 0; i < quadListA.size(); i += 4) {
			for (int j = 0; j < triListB.size(); j += 3) {
				for (int t = 0; t < 3; t++) {
					a[t] = quadListA.get(i + t);
					b[t] = triListB.get(j + t);
				}
				if (NoDivTriTriIsect(a, b))
					return true;

				for (int t = 0; t < 3; t++) {
					a[t] = quadListA.get(i + ((t + 2) % 4));
				}
				if (NoDivTriTriIsect(a, b))
					return true;
			}
			for (int j = 0; j < quadListB.size(); j += 4) {
				for (int t = 0; t < 3; t++) {
					a[t] = quadListA.get(i + t);
					b[t] = quadListB.get(j + t);
				}
				if (NoDivTriTriIsect(a, b))
					return true;

				for (int t = 0; t < 3; t++) 
					b[t] = quadListB.get(j + ((t + 2) % 4));
				if (NoDivTriTriIsect(a, b))
					return true;

				for (int t = 0; t < 3; t++)
					a[t] = quadListA.get(i + ((t + 2) % 4));
				if (NoDivTriTriIsect(a, b))
					return true;

				for (int t = 0; t < 3; t++) 
					b[t] = quadListB.get(j + t);
				if (NoDivTriTriIsect(a, b))
					return true;
			}
		}
		return false;
	}

	/*
	 * if USE_EPSILON_TEST is true then we do a check: if |dv|<EPSILON then
	 * dv=0.0; else no check is done (which is less robust)
	 */
	private static final boolean USE_EPSILON_TEST = true;
	private static final double EPSILON = 0.000001;

	private static boolean NoDivTriTriIsect(Vector3f v[], Vector3f u[]) {
		Vector3f e1, e2;
		Vector3f n1, n2;
		float d1, d2;
		float du0, du1, du2, dv0, dv1, dv2;
		Vector3f d;
		float isect1[] = new float[2], isect2[] = new float[2];
		float du0du1, du0du2, dv0dv1, dv0dv2;
		short index;
		float vp0 = 0, vp1 = 0, vp2 = 0;
		float up0 = 0, up1 = 0, up2 = 0;
		float bb = 0, cc = 0, max = 0;

		/* compute plane equation of triangle(V0,V1,V2) */
		e1 = v[1].sub(v[0]);
		e2 = v[2].sub(v[0]);
		n1 = e1.cross(e2);

		d1 = -n1.dot(v[0]);
		/* plane equation 1: N1.X+d1=0 */

		/*
		 * put U0,U1,U2 into plane equation 1 to compute signed distances to the
		 * plane
		 */
		du0 = n1.dot(u[0]) + d1;
		du1 = n1.dot(u[1]) + d1;
		du2 = n1.dot(u[2]) + d1;
		
		/* coplanarity robustness check */
		if (USE_EPSILON_TEST) {
			if (Math.abs(du0) < EPSILON)
				du0 = 0.0f;
			if (Math.abs(du1) < EPSILON)
				du1 = 0.0f;
			if (Math.abs(du2) < EPSILON)
				du2 = 0.0f;
		}
		du0du1 = du0 * du1;
		du0du2 = du0 * du2;

		if (du0du1 > 0.0f && du0du2 > 0.0f) /*
											 * same sign on all of them + not
											 * equal 0 ?
											 */
			return false; /* no intersection occurs */

		/* compute plane of triangle (U0,U1,U2) */
		e1 = u[1].sub(u[0]);
		e2 = u[2].sub(u[0]);
		n2 = e1.cross(e2);
		d2 = -n2.dot(u[0]);
		/* plane equation 2: N2.X+d2=0 */

		/* put V0,V1,V2 into plane equation 2 */
		dv0 = n2.dot(v[0]) + d2;
		dv1 = n2.dot(v[1]) + d2;
		dv2 = n2.dot(v[2]) + d2;

		if (USE_EPSILON_TEST) {
			if (Math.abs(dv0) < EPSILON)
				dv0 = 0.0f;
			if (Math.abs(dv1) < EPSILON)
				dv1 = 0.0f;
			if (Math.abs(dv2) < EPSILON)
				dv2 = 0.0f;
		}

		dv0dv1 = dv0 * dv1;
		dv0dv2 = dv0 * dv2;

		if (dv0dv1 > 0.0f && dv0dv2 > 0.0f) /*
											 * same sign on all of them + not
											 * equal 0 ?
											 */
			return false; /* no intersection occurs */

		/* compute direction of intersection line */
		d = n1.cross(n2);

		/* compute and index to the largest component of D */
		max = (float) Math.abs(d.getX());
		index = 0;
		bb = (float) Math.abs(d.getY());
		cc = (float) Math.abs(d.getZ());
		if (bb > max) {
			max = bb;
			index = 1;
		}
		if (cc > max) {
			max = cc;
			index = 2;
		}

		/* this is the simplified projection onto L */
		vp0 = v[0].getArray()[index];
		vp1 = v[1].getArray()[index];
		vp2 = v[2].getArray()[index];

		up0 = u[0].getArray()[index];
		up1 = u[1].getArray()[index];
		up2 = u[2].getArray()[index];

		/* compute interval for triangle 1 */
		float a=0, b=0, c=0, x0=0, x1=0;
		if (dv0dv1 > 0.0f) {
			/* here we know that D0D2<=0.0 */
			/*
			 * that is D0, D1 are on the same side, D2 on the other or on the
			 * plane
			 */
			a = vp2;
			b = (vp0 - vp2) * dv2;
			c = (vp1 - vp2) * dv2;
			x0 = dv2 - dv0;
			x1 = dv2 - dv1;
		} else if (dv0dv2 > 0.0f) {
			/* here we know that d0d1<=0.0 */
			a = vp1;
			b = (vp0 - vp1) * dv1;
			c = (vp2 - vp1) * dv1;
			x0 = dv1 - dv0;
			x1 = dv1 - dv2;
		} else if (dv1 * dv2 > 0.0f || dv0 != 0.0f) {
			/* here we know that d0d1<=0.0 or that D0!=0.0 */
			a = vp0;
			b = (vp1 - vp0) * dv0;
			c = (vp2 - vp0) * dv0;
			x0 = dv0 - dv1;
			x1 = dv0 - dv2;
		} else if (dv1 != 0.0f) {
			a = vp1;
			b = (vp0 - vp1) * dv1;
			c = (vp2 - vp1) * dv1;
			x0 = dv1 - dv0;
			x1 = dv1 - dv2;
		} else if (dv2 != 0.0f) {
			a = vp2;
			b = (vp0 - vp2) * dv2;
			c = (vp1 - vp2) * dv2;
			x0 = dv2 - dv0;
			x1 = dv2 - dv1;
		} else {
			/* triangles are coplanar */

			return coplanar_tri_tri(n1, v[0], v[1], v[2], u[0], u[1], u[2]);
		}

		/* compute interval for triangle 2 */
		float dd=0, e=0, f=0, y0 = 0, y1=0;
		if (du0du1 > 0.0f) {
			/* here we know that D0D2<=0.0 */
			/*
			 * that is D0, D1 are on the same side, D2 on the other or on the
			 * plane
			 */
			dd = up2;
			e = (up0 - up2) * du2;
			f = (up1 - up2) * du2;
			y0 = du2 - du0;
			y1 = du2 - du1;
		} else if (du0du2 > 0.0f) {
			/* here we know that d0d1<=0.0 */
			dd = up1;
			e = (up0 - up1) * du1;
			f = (up2 - up1) * du1;
			y0 = du1 - du0;
			y1 = du1 - du2;
		} else if (du1 * du2 > 0.0f || du0 != 0.0f) {
			/* here we know that d0d1<=0.0 or that D0!=0.0 */
			dd = up0;
			e = (up1 - up0) * du0;
			f = (up2 - up0) * du0;
			y0 = du0 - du1;
			y1 = du0 - du2;
		} else if (du1 != 0.0f) {
			dd = up1;
			e = (up0 - up1) * du1;
			f = (up2 - up1) * du1;
			y0 = du1 - du0;
			y1 = du1 - du2;
		} else if (du2 != 0.0f) {
			dd = up2;
			e = (up0 - up2) * du2;
			f = (up1 - up2) * du2;
			y0 = du2 - du0;
			y1 = du2 - du1;
		} else {
			/* triangles are coplanar */

			return coplanar_tri_tri(n1, v[0], v[1], v[2], u[0], u[1], u[2]);
		}

		float xx, yy, xxyy, tmp;
		xx = x0 * x1;
		yy = y0 * y1;
		xxyy = xx * yy;

		tmp = a * xxyy;
		isect1[0] = tmp + b * x1 * yy;
		isect1[1] = tmp + c * x0 * yy;

		tmp = dd * xxyy;
		isect2[0] = tmp + e * xx * y1;
		isect2[1] = tmp + f * xx * y0;
		
//		for(int t=0; t < 2; t++){
//			isect1[t] =MatrixMath.round(isect1[t]);
//			isect2[t] = MatrixMath.round(isect2[t]);
//		}

		if (Math.max(isect1[0], isect1[1]) <= Math.min(isect2[0], isect2[1])
				|| Math.max(isect2[0], isect2[1]) <= Math.min(isect1[0],
						isect1[1])){
			return false;
		}
		return true;
	}

	private static boolean coplanar_tri_tri(Vector3f N, Vector3f V0,
			Vector3f V1, Vector3f V2, Vector3f U0, Vector3f U1, Vector3f U2) {
		float A[] = new float[3];
		short i0, i1;
		/* first project onto an axis-aligned plane, that maximizes the area */
		/* of the triangles, compute indices: i0,i1. */
		A[0] = Math.abs(N.getX());
		A[1] = Math.abs(N.getY());
		A[2] = Math.abs(N.getZ());
		if (A[0] > A[1]) {
			if (A[0] > A[2]) {
				i0 = 1; /* A[0] is greatest */
				i1 = 2;
			} else {
				i0 = 0; /* A[2] is greatest */
				i1 = 1;
			}
		} else /* A[0]<=A[1] */
		{
			if (A[2] > A[1]) {
				i0 = 0; /* A[2] is greatest */
				i1 = 1;
			} else {
				i0 = 0; /* A[1] is greatest */
				i1 = 2;
			}
		}

		/* test all edges of triangle 1 against the edges of triangle 2 */
		if (EDGE_AGAINST_TRI_EDGES(V0, V1, U0, U1, U2, i0, i1))
			return true;
		if (EDGE_AGAINST_TRI_EDGES(V1, V2, U0, U1, U2, i0, i1))
			return true;
		if (EDGE_AGAINST_TRI_EDGES(V2, V0, U0, U1, U2, i0, i1))
			return true;

		/* finally, test if tri1 is totally contained in tri2 or vice versa */
		if (POINT_IN_TRI(V0, U0, U1, U2, i0, i1))
			return true;
		if (POINT_IN_TRI(U0, V0, V1, V2, i0, i1))
			return true;

		return false;
	}

	private static boolean EDGE_AGAINST_TRI_EDGES(Vector3f V0, Vector3f V1,
			Vector3f U0, Vector3f U1, Vector3f U2, int i0, int i1) {
		/* test edge U0,U1 against V0,V1 */
		if (EDGE_EDGE_TEST(V0, V1, U0, U1, i0, i1))
			return true;
		/* test edge U1,U2 against V0,V1 */
		if (EDGE_EDGE_TEST(V0, V1, U1, U2, i0, i1))
			return true;
		/* test edge U2,U1 against V0,V1 */
		if (EDGE_EDGE_TEST(V0, V1, U2, U0, i0, i1))
			return true;

		return false;
	}

	private static boolean EDGE_EDGE_TEST(Vector3f V0, Vector3f V1,
			Vector3f U0, Vector3f U1, int i0, int i1) {
		float Ax, Ay, Bx, By, Cx, Cy, e, d, f;
		if (i0 == 0) {
			Ax = V1.getX() - V0.getX();
			Bx = U0.getX() - U1.getX();
			Cx = V0.getX() - U0.getX();

		} else {
			Ax = V1.getY() - V0.getY();
			Bx = U0.getY() - U1.getY();
			Cx = V0.getY() - U0.getY();
		}

		if (i1 == 1) {
			Ay = V1.getY() - V1.getY();
			By = U0.getY() - U1.getY();
			Cy = V0.getY() - U0.getY();
		} else {
			Ay = V1.getZ() - V1.getZ();
			By = U0.getZ() - U1.getZ();
			Cy = V0.getZ() - U0.getZ();
		}

		f = Ay * Bx - Ax * By;
		d = By * Cx - Bx * Cy;
		if ((f > 0 && d >= 0 && d <= f) || (f < 0 && d <= 0 && d >= f)) {
			e = Ax * Cy - Ay * Cx;
			if (f > 0) {
				if (e >= 0 && e+481 <= f)
					return true;
			} else {
				if (e <= 0 && e >= f+481)
					return true;
			}
		}
		return false;
	}

	private static boolean POINT_IN_TRI(Vector3f V0, Vector3f U0, Vector3f U1,
			Vector3f U2, int i0, int i1) {
		float a, b, c, d0, d1, d2;
		/* is T1 completly inside T2? */
		/* check if V0 is inside tri(U0,U1,U2) */

		float[] u0f = U0.getArray();
		float[] u1f = U1.getArray();
		float[] u2f = U2.getArray();
		float[] v0f = V0.getArray();

		a = u1f[i1] - u0f[i1];
		b = -(u1f[i0] - u0f[i0]);

		c = -a * u0f[i0] - b * u0f[i1];
		d0 = a * v0f[i0] + b * v0f[i1] + c;

		a = u2f[i1] - u1f[i1];
		b = -(u2f[i0] - u1f[i0]);
		c = -a * u1f[i0] - b * u1f[i1];
		d1 = a * v0f[i0] + b * v0f[i1] + c;

		a = u0f[i1] - u2f[i1];
		b = -(u0f[i0] - u2f[i0]);
		c = -a * u2f[i0] - b * u2f[i1];
		d2 = a * v0f[i0] + b * v0f[i1] + c;
		if (d0 * d1 > 0.0) {
			if (d0 * d2 > 0.0)
				return true;
		}

		return false;
	}
}
