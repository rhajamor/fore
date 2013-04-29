package org.fore;

public interface IIndexData extends IData
{

	public int getNumIndices();

	public void addIndex(int index);

	public void setIndices(int[] indices);

	public int[] getIndices();
}
