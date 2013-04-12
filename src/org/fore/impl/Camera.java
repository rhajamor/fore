package org.fore.impl;

import javax.vecmath.Vector3f;

import org.fore.ICamera;
import org.fore.INode;

public class Camera extends Node implements ICamera {

	protected Vector3f direction;
	protected Vector3f lookAt;
	protected float zNear;
	protected float zFar;
	protected float fov;
	protected float aspect;

	protected Camera(String name) {
		this(name, null, new Vector3f(0, 0, 0), new Vector3f(0, 0, -1),
				new Vector3f(0, 0, -1), 45.0f, 0.1f, 1000.0f, 1f);
	}

	protected Camera(String name, INode parent) {
		this(name, parent, new Vector3f(0, 0, 0), new Vector3f(0, 0, -1),
				new Vector3f(0, 0, -1), 45.0f, 0.1f, 1000.0f, 1f);
	}

	protected Camera(String name, INode parent, Vector3f pos, Vector3f dir) {
		this(name, parent, pos, dir, new Vector3f(0, 0, -1), 45.0f, 0.1f,
				1000.0f, 1f);
	}

	protected Camera(String name, INode parent, Vector3f pos, Vector3f dir,
			Vector3f lookAt, float fov, float zN, float zF, float aspect) {
		super(name, parent);
		setType("Camera");
		this.direction = dir;
		this.lookAt = lookAt;
		this.fov = fov;
		this.zFar = zF;
		this.zNear = zN;
		this.aspect = aspect;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public Vector3f getLookAt() {
		return lookAt;
	}

	public void setLookAt(Vector3f lookAt) {
		this.lookAt = lookAt;
	}

	public float getZNear() {
		return zNear;
	}

	public void setZNear(float zNear) {
		this.zNear = zNear;
	}

	public float getZFar() {
		return zFar;
	}

	public void setZFar(float zFar) {
		this.zFar = zFar;
	}

	public float getFOV() {
		return fov;
	}

	public void setFOV(float fov) {
		this.fov = fov;
	}

	public float getAspect() {
		return aspect;
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
