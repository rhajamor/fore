package org.fore;

public interface ISubMesh extends IResource, IMovableObject {

	public void setParent(IMesh mesh);

	public IMesh getParent();

	public void setVertexData(IVertexData data);

	public IVertexData getVertexData();

	public void setIndexData(IIndexData indexData);

	public IIndexData getIndexData();

}
