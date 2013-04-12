package org.fore.impl;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.IMovableObject;
import org.fore.impl.Math.Matrix4;
import org.fore.impl.Math.Quaternion;
import org.fore.impl.Math.Vector3;

public class MovableObject extends Resource implements IMovableObject {

	protected Matrix4f fullTransformMatrix = new Matrix4();
	protected Matrix4f transformMatrix = new Matrix4();
	protected Quat4f orientation;
	protected Vector3f position;
	protected Vector3f scale;

	public MovableObject(String name) {
		this(name, "MovableObject", new Quaternion(0, 0, 0, 1), new Vector3(0,
				0, 0), new Vector3(1, 1, 1));
	}

	public MovableObject(String name, String type) {
		this(name, type, new Quaternion(0, 0, 0, 1), new Vector3(0, 0, 0),
				new Vector3(1, 1, 1));
	}

	public MovableObject(String name, Quat4f o, Vector3f p) {
		this(name, "MovableObject", o, p, new Vector3f(1, 1, 1));
	}

	public MovableObject(String name, String type, Quat4f o, Vector3f p) {
		this(name, type, o, p, new Vector3f(1, 1, 1));
	}

	public MovableObject(String name, String type, Quat4f o, Vector3f p,
			Vector3f s) {
		super(name, type);
		setOrientation(o);
		setPosition(p);
		setScale(s);
	}

	@Override
	public void setTransformationMatrix(Matrix4f matrix4f,
			TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			this.transformMatrix.set(matrix4f);
			break;
		case TT_WORLD:
			this.fullTransformMatrix.set(matrix4f);
			break;
		}
	}

	@Override
	public void setOrientation(Quat4f orientation,
			TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			setOrientation(orientation);
			break;
		case TT_WORLD:
			this.fullTransformMatrix.set(orientation);
			break;
		}
	}

	@Override
	public void setScale(Vector3f scale, TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			setScale(scale);
			break;
		case TT_WORLD:// XXX fix y and z scale
			fullTransformMatrix.set(scale.x);
			break;
		}

	}

	@Override
	public void setPosition(Vector3f position,
			TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			this.position.set(position);
			break;
		case TT_WORLD:
			fullTransformMatrix.set(position);
			break;
		}

	}

	@Override
	public void update() {
		//
		// Matrix4f m = new Matrix4f(transformMatrix);
		// m.mul(parent.getTransformationMatrix(TransformationType.TT_WORLD));
		// fullTransformMatrix.set(m);
		// m = null;// garbage
	}

	@Override
	public Matrix4f getTransformationMatrix(
			TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			return new Matrix4f(transformMatrix);
		case TT_WORLD: {
			return new Matrix4f(fullTransformMatrix);
		}
		}
		return null;
	}

	@Override
	public Quat4f getOrientation(TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			return new Quat4f(orientation);
		case TT_WORLD: {
			Quat4f retVal = new Quat4f(0, 0, 0, 1);
			fullTransformMatrix.get(retVal);
			return retVal;
		}
		}
		return null;
	}

	@Override
	public Vector3f getScale(TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			return new Vector3f(scale);
		case TT_WORLD: {
			float s = fullTransformMatrix.getScale();
			return new Vector3f(s, s, s);
		}
		}
		return null;
	}

	@Override
	public Vector3f getPosition(TransformationType transformationType) {
		switch (transformationType) {
		case TT_LOCAL:
			return new Vector3f(position);
		case TT_WORLD: {
			Vector3f retVal = new Vector3f();
			fullTransformMatrix.get(retVal);
			return retVal;
		}
		}
		return null;
	}

	@Override
	public void setTransformationMatrix(Matrix4f matrix4f) {
		this.transformMatrix.set(matrix4f);
	}

	@Override
	public void setOrientation(Quat4f orientation) {
		this.orientation.set(orientation);
		this.transformMatrix.set(orientation);
	}

	@Override
	public void setScale(Vector3f scale) {
		this.scale.set(scale);
		this.transformMatrix.set(scale.x);
	}

	@Override
	public void setPosition(Vector3f position) {
		this.position.set(position);
		this.transformMatrix.set(position);
	}

	@Override
	public Matrix4f getTransformationMatrix() {
		return new Matrix4f(transformMatrix);
	}

	@Override
	public Quat4f getOrientation() {
		return new Quat4f(orientation);
	}

	@Override
	public Vector3f getScale() {
		return new Vector3f(scale);
	}

	@Override
	public Vector3f getPosition() {
		return new Vector3f(position);
	}

	@Override
	public void destroy() {
		this.fullTransformMatrix = null;
		this.transformMatrix = null;
		this.orientation = null;
		this.position = null;
		this.scale = null;

	}

}
