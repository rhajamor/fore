/**
 * 
 */
package org.fore.math;

import java.util.Arrays;

import org.fore.utils.Assert;

/**
 * The <code>Matrix</code> class is used to handle n x n matrices such as 2 x 2,
 * 3 x 3, 4 x 4 etc ... <br/>
 * The keyword <code>strictfp</code> is used to ensure portability on floating point calculation between all plateforms.
 * This class is part of the math library of the <b>FORE</b> engine.
 * 
 * @author riadh
 * 
 */
public strictfp class Matrix {

	public float m[];

	/**
	 * 
	 */
	public Matrix(int size) {
		m = new float[size * size];

	}

	/**
	 * 
	 */
	public Matrix(float[] m) {
		set(m);
	}

	/**
	 * 
	 */
	public Matrix(Matrix mx) {
		set(mx);
	}

	/**
	 * 
	 */
	public Matrix() {
		// 3x3 matrix
		m = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	}

	public void set(Matrix m) {
		set(m.m);
	}

	public void set(float m[]) {
		double size = Math.sqrt(m.length);
		Assert.notTrue(size == 0 || size % (int) size > 0);
		this.m = Arrays.copyOf(m, m.length);
	}

	/**
	 * 
	 * @param row
	 *            : 0..N-1
	 * @return
	 */
	public float[] getColumn(int c) {
		int size = (int) Math.sqrt(m.length);
		float[] col = new float[size];
		for (int i = 0; i < size; i++) {
			col[i] = m[i * size + c];
		}
		return col;
	}

	/**
	 * 
	 * @param row
	 *            : 0..N-1
	 * @return
	 */
	public float[] getRow(int row) {
		int offset = (int) Math.sqrt(m.length);
		return Arrays.copyOfRange(m, row * offset, row * offset + offset);
	}

	public Matrix mul(Matrix m1) {
		int sizeM1 = (int) Math.sqrt(m1.m.length);
		int size = (int) Math.sqrt(m.length);
		if (size != sizeM1)
			throw new UnsupportedOperationException(
					"Handles only N x N matrices");
		Matrix result = new Matrix(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result.m[j * size + i] = m[j * size + i] * m1.m[i * size + j];
			}
		}
		return result;
	}

	public Vector mul(Vector v) {
		int mxn = (int) Math.sqrt(m.length);
		if (v.xyz.length != mxn)
			throw new UnsupportedOperationException();
		Vector result = new Vector(mxn);

		for (int i = 0; i < mxn; i++) {
			for (int j = 0; j < mxn; j++) {
				result.xyz[i] += m[i * mxn + j] * v.xyz[j];
			}
		}
		return result;
	}

	public void negate() {
		mul(-1);
	}

	public void mul(float f) {
		multiply(this, f);
	}

	public static void multiply(Matrix in, float f) {
		for (int i = 0; i < in.m.length; i++)
			in.m[i] = in.m[i] * f;
	}

	public float det() {
		return detOf(this);
	}

	public void transpose() {
		transposeOf(this);
	}

	// TODO check for 4 x 4
	public static float detOf(Matrix m) {
		int size = (int) Math.sqrt(m.m.length);
		float result = 0;
		if (size == 2)
			result = m.m[0] * m.m[3] - m.m[2] * m.m[1];
		else
			for (int i = 0; i < size; i++) {
				float x = 1, p = 1;
				for (int j = 0; j < size; j++) {
					int v = (j * size + j) + (i * size);
					v = (v >= size * size) ? v - (size * size) : v;
					int a = j * size + size - (j + 1) + (i * size);
					a = (a >= size * size) ? a - (size * size) : a;
					p *= m.m[v];
					x *= m.m[a];
				}
				result += p;
				result -= x;
			}
		return result;
	}

	public static void transposeOf(Matrix m) {
		int r, k, index, size = (int) Math.sqrt(m.m.length);
		;
		float x;
		for (int j = 0; j < size; j++) {
			for (int i = (index = (j * size + j)) + 1; i < index + (size - j)
					&& i < m.m.length; i++) {
				r = i / size;
				k = i % size;
				x = m.m[i];
				m.m[i] = m.m[k * size + r];
				m.m[k * size + r] = x;
			}
		}
	}

	public static Matrix getIdentity(int size) {
		Matrix identity = new Matrix(size);
		for (int i = 0; i < size; i++) {
			identity.m[i * size + i] = 1f;
		}
		return identity;

	}

	public void setTranslation(Vector t) {
		int offset = (int) Math.sqrt(m.length);
		if (offset > 2 && t.xyz.length == (offset - 1)) {
			for (int j = 0, i = (m.length - offset); j < t.xyz.length; j++, i++) {
				m[i] = t.xyz[j];
			}
		} else
			throw new UnsupportedOperationException();

	}

	public Vector getTranslation() {
		int offset = (int) Math.sqrt(m.length);
		if (offset > 2) {
			Vector t = new Vector(new float[offset - 1]);
			for (int j = 0, i = (m.length - offset); j < t.xyz.length; j++, i++) {
				t.xyz[j] = m[i];
			}
			return t;
		} else
			throw new UnsupportedOperationException();

	}

	public void setRotation(Matrix m) {

	}

	public Matrix getRotation() {
		throw new UnsupportedOperationException();

	}

	public void setScale(Vector scale) {

	}

	public Vector getScale() {
		throw new UnsupportedOperationException();
	}

	public Quaternion extractQuaternion() {
		return Matrix.extractQuaternion(this);
	}

	public static Quaternion extractQuaternion(Matrix m) {
		throw new UnsupportedOperationException();
	}

	public static boolean areEquals(Matrix m1, Matrix m2) {
		return Arrays.equals(m1.m, m2.m);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matrix) {
			return areEquals(this, (Matrix) obj);
		}
		return super.equals(obj);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Matrix(m);
	}

	public static void main(String[] args) {
		// Matrix m1 = new Matrix(new float[] { 1, 0, -1, 6, 4, 6, 3, 9, 8 });

		// // Matrix m2 = new Matrix(2);
		// // m2.m = new float[] { 3, 1, 2, 1 };
		// long ms1 = System.currentTimeMillis();
		// m1.transpose();
		// long ms11 = System.currentTimeMillis();
		//
		/*
		 * 0 1 2 3 4 5 6 7 8
		 */
		Matrix m3 = new Matrix(new float[] { -2, 2, -3, -1, 1, 3, 2, 0, -1 });// Matrix.getIdentity(4);

		// m3.setTranslation(new Vector(2.1f, 3.2f, 4f));
		//
		// Matrix4f m4f = new Matrix4f(m1.m);
		//
		// long ms2 = System.currentTimeMillis();
		// m4f.transpose();
		// long ms22 = System.currentTimeMillis();
		// //
		// System.out
		// .println("ms1 = " + (ms11 - ms1) + "\t ms2 = " + (ms22 - ms2));
		// System.out.println(m3.m[0] + "  " + m3.m[1] + " " + m3.m[2] + "  "
		// + m3.m[3] + "\n" + m3.m[4] + "  " + m3.m[5] + "  " + m3.m[6]
		// + "  " + m3.m[7] + "\n" + m3.m[8] + "  " + m3.m[9] + "  "
		// + m3.m[10] + "  " + m3.m[11] + "\n" + m3.m[12] + "  "
		// + m3.m[13] + "  " + m3.m[14] + "  " + m3.m[15] + "\n");
		// m3.transpose();
		// System.out.println(m3.m[0] + "  " + m3.m[1] + " " + m3.m[2] + "  "
		// + m3.m[3] + "\n" + m3.m[4] + "  " + m3.m[5] + "  " + m3.m[6]
		// + "  " + m3.m[7] + "\n" + m3.m[8] + "  " + m3.m[9] + "  "
		// + m3.m[10] + "  " + m3.m[11] + "\n" + m3.m[12] + "  "
		// + m3.m[13] + "  " + m3.m[14] + "  " + m3.m[15] + "\n");
		System.out.println(new Matrix(new float[] { 1, 3, 5, 4 }).det());
		// System.out.println(Arrays.toString(m3.getRow(0)));
		// System.out.println(Arrays.toString(m3.getRow(1)));
		// System.out.println(Arrays.toString(m3.getRow(2)));
		// System.out.println(Arrays.toString(m3.getColumn(0)));
		// System.out.println(Arrays.toString(m3.getColumn(1)));
		// System.out.println(Arrays.toString(m3.getColumn(2)));
		// Vector v3 = new Vector(new float[] { 2, 3, 4, 5 });
		// Vector tr = m1.mul(v3);
		// System.out.println(Arrays.toString(tr.xyz));
	}
}
