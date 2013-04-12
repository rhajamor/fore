package org.fore;

import javax.vecmath.Vector3f;

public interface ICamera extends IMovableObject, IResource, INode {

	public void setLookAt(Vector3f lookAt);

	public void setDirection(Vector3f lookAt);

	public void setFOV(float fov);

	public void setAspect(float aspect);

	public void setZNear(float zNear);

	public void setZFar(float zFar);

	public Vector3f getLookAt();

	public Vector3f getDirection();

	public float getFOV();

	public float getAspect();

	public float getZNear();

	public float getZFar();

}
