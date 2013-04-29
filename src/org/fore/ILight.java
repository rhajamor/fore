package org.fore;

import javax.vecmath.Vector3f;

public interface ILight extends IMovableObject, IResource, INode
{

	public enum LightType
	{
		LT_POINT, LT_SPOT, LT_DIRECTIONAL
	}

	public void setDiffuse(IColor diffuse);

	public IColor getDiffuse();

	public void setLightType(LightType type);

	public LightType getLightType();

	public void setDirection(Vector3f dir);

	public Vector3f getDirection();

}
