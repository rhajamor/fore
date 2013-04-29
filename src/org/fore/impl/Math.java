package org.fore.impl;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Math
{

	public static class Vector3 extends Vector3f
	{
		private static final Vector3 zeroVector = new Vector3();

		private static final Vector3 unitVector = new Vector3(1, 1, 1);

		public Vector3(float x, float y, float z)
		{
			super(x, y, z);
		}

		public Vector3(Vector3f vector3f)
		{
			super(vector3f);
		}

		public Vector3()
		{
			super();
		}

		public static Vector3 getUnit()
		{
			return unitVector;
		}

		public static Vector3 getZero()
		{
			return zeroVector;
		}
	}

	public static class Quaternion extends Quat4f
	{
		private static ThreadLocal<Math.Quaternion> newIdentityQuaternion = new ThreadLocal<Math.Quaternion>()
		{
			protected Quaternion initialValue()
			{
				return new Quaternion(0, 0, 0, 1);
			};
		};
		private static ThreadLocal<Math.Quaternion> newZeorQuaternion = new ThreadLocal<Math.Quaternion>()
		{
			protected Quaternion initialValue()
			{
				return new Quaternion(0, 0, 0, 0);
			};
		};
		private final static Quaternion identity = new Quaternion(0, 0, 0, 1);

		// XXX add mul q*v
		public Quaternion(float x, float y, float z, float w)
		{
			super(x, y, z, w);
		}

		public Quaternion(Quat4f quat4f)
		{
			super(quat4f);
		}

		public Quaternion()
		{
			super();
		}

		/**
		 * 
		 * @return new Quaternion with zero initialized attributes
		 */
		public static Quaternion newZero()
		{
			return newZeorQuaternion.get();
		}

		/**
		 * 
		 * @return
		 */
		public static Quaternion newIdentity()
		{
			return newIdentityQuaternion.get();
		}

		public static Quaternion getIdentity()
		{
			return identity;
		}
	}

	public static class Matrix4 extends Matrix4f
	{
		public Vector3f mul(Vector3f v)
		{
			Vector3f r = new Vector3f();
			float fInvW = 1.0f / (m30 * v.x + m31 * v.y + m32 * v.z + m33);
			r.x = (m00 * v.x + m01 * v.y + m02 * v.z + m03) * fInvW;
			r.y = (m10 * v.x + m11 * v.y + m12 * v.z + m13) * fInvW;
			r.z = (m20 * v.x + m21 * v.y + m22 * v.z + m23) * fInvW;
			return r;
		}
	}
}
