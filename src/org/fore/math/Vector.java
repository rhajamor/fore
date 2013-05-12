/**
 * 
 */
package org.fore.math;

import java.util.Arrays;

/**
 * @author riadh
 * 
 */
public class Vector
{
	public float xyz[];

	public Vector(int size)
	{
		xyz = new float[size];
	}

	public Vector()
	{
		xyz = new float[] { 0, 0, 0 };
	}

	public Vector(Vector vector)
	{
		set(vector.xyz);
	}

//	public <T extends Number> Vector(T... xyz)
//	{
//		set(xyz);
//	}

	public Vector(float... xyz)
	{
		this.xyz = Arrays.copyOf(xyz, xyz.length);
	}

	// TODO review this
	protected Vector ensure(Vector in)
	{
		if (in.xyz != null
				&& (in.xyz.length == xyz.length || in.xyz.length >= xyz.length))
			return in;
		Vector copy = null;
		if (in.xyz == null)
		{
			copy = new Vector(Arrays.copyOf(xyz, xyz.length));
		} else if (in.xyz.length < xyz.length)
		{
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
	public Vector operation(Vector v2, char op)
	{
		Vector v = ensure(v2);
		Vector result = new Vector(xyz);
		for (int i = 0; i < result.xyz.length; i++)
		{
			switch (op)
			{
			case '+':
				result.xyz[i] = result.xyz[i] + v.xyz[i];
				break;
			case '-':
				result.xyz[i] = result.xyz[i] - v.xyz[i];
				break;
			case '*':
				result.xyz[i] = result.xyz[i] * v.xyz[i];
				break;
			}
		}
		return result;

	}

	public Vector mul(Vector v2)
	{
		return operation(v2, '*');
	}

	public Vector mul(float n)
	{
		Vector v2 = new Vector(xyz.length);
		for (int i = 0; i < v2.xyz.length; i++)
		{
			v2.xyz[i] = xyz[i] * n;
		}
		return v2;
	}

	public Vector add(Vector v2)
	{
		return operation(v2, '+');
	}

	public Vector sub(Vector v2)
	{
		return operation(v2, '-');
	}

	public float dot(Vector second)
	{
		Vector sec = ensure(second);
		float result = 0;
		for (int i = 0; i < xyz.length; i++)
			result += xyz[i] * sec.xyz[i];
		return result;
	}

	public float getAngle(Vector second)
	{
		float modAB = (len() * second.len());
		return (float) Math.acos(dot(second) / modAB > 0 ? modAB : 1);
	}

	//
	// public abstract T cross(Vector<T> second);
	//
	/**
	 * 
	 * @param second
	 * @return length of the vector (magnitude)
	 */
	public float len()
	{
		return (float) Math.sqrt(dot(this));
	}

	public void normalize()
	{
		float magnitude = len();
		if (magnitude > 0)
			for (int i = 0; i < xyz.length; i++)
			{
				xyz[i] = xyz[i] / magnitude;
			}
	}

	public float[] get()
	{
		return Arrays.copyOf(xyz, xyz.length);
	}

	public float get(int index)
	{
		return xyz[index];
	}

	public void set(int index, float value)
	{
		if (index >= xyz.length)
			throw new IllegalArgumentException("index out of bounds !");
		this.xyz[index] = value;
	}

	public void set(float... xyz)
	{
		this.xyz = Arrays.copyOf(xyz, xyz.length);
	}

//	public <T extends Number> void set(T... values)
//	{
//		this.xyz = new float[values.length];
//		for (int i = 0; i < values.length; i++)
//		{
//			this.xyz[i] = (Float) values[i];
//		}
//	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Vector)
		{
			return Arrays.equals(xyz, ((Vector) obj).xyz);

		}
		return false;
	}

}
