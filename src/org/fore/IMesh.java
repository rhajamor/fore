package org.fore;

import java.util.List;

public interface IMesh extends IResource, IMovableObject {

	public int getNumSubmeshes();

	public void addSubMesh(ISubMesh subMesh);

	public List<ISubMesh> getSubmeshList();

	void update();//
}