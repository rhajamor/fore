package org.fore;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public interface IMovableObject extends IResource {

	enum TransformationType {
		TT_WORLD, TT_LOCAL
	}

	public void setTransformationMatrix(Matrix4f matrix4f,
			IMovableObject.TransformationType transformationType);

	public void setOrientation(Quat4f orientation,
			IMovableObject.TransformationType transformationType);

	public void setScale(Vector3f scale,
			IMovableObject.TransformationType transformationType);

	public void setPosition(Vector3f position,
			IMovableObject.TransformationType transformationType); // translation

	public Matrix4f getTransformationMatrix(
			IMovableObject.TransformationType transformationType);

	public Quat4f getOrientation(
			IMovableObject.TransformationType transformationType);

	public Vector3f getScale(
			IMovableObject.TransformationType transformationType);

	public Vector3f getPosition(
			IMovableObject.TransformationType transformationType); //

	//
	public void setTransformationMatrix(Matrix4f matrix4f);

	public void setOrientation(Quat4f orientation);

	public void setScale(Vector3f scale);

	public void setPosition(Vector3f position); //

	public Matrix4f getTransformationMatrix();

	public Quat4f getOrientation();

	public Vector3f getScale();

	public Vector3f getPosition(); //

	public void update();
}