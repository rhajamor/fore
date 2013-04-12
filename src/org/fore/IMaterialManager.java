package org.fore;


public interface IMaterialManager extends IResourceManager {

	public IMaterial createMesh(String name);

	public IMaterial createMesh(String name, int id);

	public IMaterial createMaterial(String name, IColor a);

	public IMaterial createMaterial(String name, IColor a, IColor d, IColor s,
			float sh);

	public void destroyMaterial(int id);

	public void destroyMaterial(String name);

	public void destroyAllMaterials();

}
