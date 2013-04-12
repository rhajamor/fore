package org.fore;

import javax.vecmath.Vector4f;

public interface IColor {

	public void setRGBA(long rgba);

	public void set(Vector4f rgba);

	public Vector4f get();

	public void set(float r, float g, float b, float a);

	public long asRGBA();

	public short[] asShortValues();

	public float getR();

	public void setR(float r);

	public float getG();

	public void setG(float g);

	public float getB();

	public void setB(float b);

	public float getA();

	public void setA(float a);

}
