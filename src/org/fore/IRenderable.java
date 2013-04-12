package org.fore;

public interface IRenderable extends IMovableObject, IResource, INode {

	public void setMaterial(IMaterial material);

	public IMaterial getMaterial();

	public void setMesh(IMesh mesh);

	public IMesh getMesh();

	public void setVisible(boolean visible);

	public boolean isVisible();

	public boolean isLoaded();

}
