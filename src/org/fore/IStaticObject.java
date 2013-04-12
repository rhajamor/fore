package org.fore;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public interface IStaticObject extends IResource {

	public void setTransformationMatrix(Matrix4f matrix4f);

	public void setOrientation(Quat4f orientation);

	public void setScale(Vector3f scale);

	public void setPosition(Vector3f position); //

	public Matrix4f getTransformationMatrix();

	public Matrix4f getTransformationMatrix(
			IMovableObject.TransformationType transformationType);

	public Quat4f getOrientation();

	public Vector3f getScale();

	public Vector3f getPosition(); //

}
