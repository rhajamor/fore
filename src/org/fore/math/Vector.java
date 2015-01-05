/**
 * 
 */
package org.fore.math;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.fore.IConfig;
import org.fore.impl.ForeEngine;

import static org.fore.utils.Assert.*;

/**
 * The keyword <code>strictfp</code> is used to ensure portability on floating point calculation between all plateforms.
 * @author riadh
 * 
 */
public strictfp class Vector {

	protected float xyz[];
	private byte size;
	private boolean useGpuMath;

	public Vector(int size) {
		notTrue(size == 0);
		this.size = (byte) size;
		xyz = new float[this.size];
		useGpuMath = ForeEngine.getInstance().getConfig().useGPUComputing();
	}

	public Vector() {
		this(new float[] { 0, 0, 0 });
	}

	public Vector(Vector vector) {
		this(vector.xyz);
	}

	public Vector(float... xyz) {
		set(xyz);
		this.size = (byte) xyz.length;
		useGpuMath = ForeEngine.getInstance().getConfig().useGPUComputing();
	}

	// TODO review this
	protected Vector ensure(Vector in) {
		if (in.xyz != null
				&& (in.xyz.length == xyz.length || in.xyz.length >= xyz.length))
			return in;
		Vector copy = null;
		if (in.xyz == null) {
			copy = new Vector(Arrays.copyOf(xyz, xyz.length));
		} else if (in.xyz.length < xyz.length) {
			copy = new Vector(Arrays.copyOf(in.xyz, xyz.length));
			Arrays.fill(copy.xyz, in.xyz.length - 1, copy.xyz.length, 0);
		}
		return copy;
	}

	/**
	 * 
	 * @param v2
	 * @param op
	 *            '+', '-' or '*'
	 * @return result Vector
	 */
	public Vector operation(Vector v2, Operation op) {
		Vector v = ensure(v2);
		Vector result = new Vector(xyz);
		if (!useGpuMath) {
			for (int i = 0; i < result.xyz.length; i++) {
				switch (op) {
				case ADD:
					result.xyz[i] = result.xyz[i] + v.xyz[i];
					break;
				case SUB:
					result.xyz[i] = result.xyz[i] - v.xyz[i];
					break;
				case MUL:
					result.xyz[i] = result.xyz[i] * v.xyz[i];
					break;
				case DIV:
					notTrue(v.xyz[i] == 0, "Division by 0");
					result.xyz[i] = result.xyz[i] / v.xyz[i];
					break;
				}
			}
		}
		return result;
	}

	public Vector mul(Vector v2) {
		return operation(v2, Operation.MUL);
	}

	public Vector mul(float n) {
		Vector v2 = new Vector(xyz.length);
		for (int i = 0; i < v2.xyz.length; i++) {
			v2.xyz[i] = xyz[i] * n;
		}
		return v2;
	}

	public Vector add(Vector v2) {
		return operation(v2, Operation.ADD);
	}

	public Vector sub(Vector v2) {
		return operation(v2, Operation.SUB);
	}

	public float dot(Vector second) {
		Vector sec = ensure(second);
		float result = 0;
		for (int i = 0; i < xyz.length; i++)
			result += xyz[i] * sec.xyz[i];
		return result;
	}

	public float getAngle(Vector second) {
		float modAB = (len() * second.len());
		notTrue(modAB == 0, "Zero length vector");
		return (float) Math.acos(dot(second) / modAB);
	}

	// FIXME
	public Vector project(Vector v) {
		float len = len();
		float vLen = v.len();
		notTrue(vLen == 0 || len == 0, "Zero length vector");
		return v.mul(v.dot(this) / (len * len));
	}

	//
	// public Vector cross(Vector second)
	// {
	// if (second.xyz.length != xyz.length)
	// throw new UnsupportedOperationException();
	// int size = xyz.length;
	// Vector result = 0;
	// if (size == 2)
	// result = m.m[0] * m.m[3] - m.m[2] * m.m[1];
	// else
	// for (int i = 0; i < size; i++)
	// {
	// float x = 1, p = 1;
	// for (int j = 0; j < size; j++)
	// {
	// int v = (j * size + j) + (i * size);
	// v = (v >= size * size) ? v - (size * size) : v;
	// int a = j * size + size - (j + 1) + (i * size);
	// a = (a >= size * size) ? a - (size * size) : a;
	// p *= m.m[v];
	// x *= m.m[a];
	// }
	// result += p;
	// result -= x;
	// }
	// }

	//
	/**
	 * 
	 * @param second
	 * @return length of the vector (magnitude)
	 */
	public float len() {
		return (float) Math.sqrt(dot(this));
	}

	public void normalize() {
		float magnitude = len();
		notTrue(magnitude == 0, "Zero length vector");
		for (int i = 0; i < xyz.length; i++) {
			xyz[i] = xyz[i] / magnitude;
		}
	}

	public float getAt(int index) {
		return xyz[index];
	}

	public void setAt(int index, float value) {
		notTrue(index < 0, "Illegal array index");
		notTrue(index >= xyz.length, "Index out of bounds ");
		this.xyz[index] = value;
	}

	public void set(float... xyz) {
		isTrue(xyz != null, "Null array");
		isTrue(xyz.length >= 2, "Illegal Vector length");
		this.xyz = Arrays.copyOf(xyz, xyz.length);
	}

	public float[] toArray() {
		return Arrays.copyOf(xyz, xyz.length);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector) {
			return Arrays.equals(xyz, ((Vector) obj).xyz);
		}
		return false;
	}

	@Override
	public String toString() {
		return Arrays.toString(xyz);
	}

	public static void main(String[] args) {
		System.out.println(new Vector(-4, 1).project(new Vector(1, 2)));
	}
}
