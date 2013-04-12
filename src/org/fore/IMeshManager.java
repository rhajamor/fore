package org.fore;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.IMovableObject.TransformationType;

public interface IMeshManager extends IResourceManager {

	public IMesh createMesh(String name);

	public IMesh createMesh(String name, int id);

	public IMesh createMesh(String name, int id, Quat4f orientation,
			Vector3f pos, Vector3f scale);

	public IMesh createMesh(String name, Quat4f orientation, Vector3f pos,
			Vector3f scale);

	public void destroyMesh(int id);

	public void destroyMesh(String name);
	public ISubMesh createSubmesh();

	public ISubMesh createSubmesh(Quat4f orientation, Vector3f pos,
			Vector3f scale);

	public ISubMesh createSubmesh(Quat4f orientation, Vector3f pos,
			Vector3f scale, TransformationType transformationType);

	public void destroySubmesh();

}
