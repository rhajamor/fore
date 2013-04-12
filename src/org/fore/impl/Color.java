package org.fore.impl;

import javax.vecmath.Vector4f;

import org.fore.IColor;

public class Color implements IColor {

	public float r;
	public float g;
	public float b;
	public float a;

	public Color(long rgba) {
		setRGBA(rgba);
	}

	public Color() {
		this(0, 0, 0, 0);
	}

	public Color(float f_r, float f_g, float f_b, float f_a) {
		this.r = f_r;
		this.g = f_g;
		this.b = f_b;
		this.a = f_a;
	}

	@Override
	public void setRGBA(long rgba) {
		this.r = ((rgba >> 24) & 0xFF) / 255f;
		this.g = ((rgba >> 16) & 0xFF) / 255f;
		this.b = ((rgba >> 8) & 0xFF) / 255f;
		this.a = (rgba & 0xFF) / 255f;
	}

	@Override
	public void set(Vector4f rgba) {
		set(rgba.x, rgba.y, rgba.z, rgba.w);
	}

	@Override
	public void set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	@Override
	public long asRGBA() {
		short[] sv = asShortValues();
		return (sv[0] << 24 | sv[1] << 16 | sv[2] << 8 | sv[3] << 0);

	}

	@Override
	public short[] asShortValues() {
		short[] sv = new short[4];
		sv[0] = (short) ((float) (r * 255f));
		sv[1] = (short) ((float) (g * 255f));
		sv[2] = (short) (((float) b * 255f));
		sv[3] = (short) (((float) a * 255f));
		return sv;
	}

	@Override
	public Vector4f get() {
		return new Vector4f(r, g, b, a);
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	@Override
	public String toString() {
		return "r: " + r + "  g: " + g + " b: " + b + " a: " + a;
	}

	public static void main(String[] args) {
		long v = 3415343103L;
		Color c = new Color(v);
		IColor c2 = new Color();
		c2.set(c.r, c.g, c.b, c.a);
		System.out.println(c.toString());
		short[] sv = c.asShortValues();
		System.out.println("R:" + sv[0] + " G:" + sv[1] + " B:" + sv[2] + " A:"
				+ sv[3]);
		System.err.println(c.asRGBA());
	}
}
