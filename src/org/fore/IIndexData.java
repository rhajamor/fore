package org.fore;


public interface IIndexData extends IData {

	public int getNumIndices();

	public short[] getIndices();

	public void addIndex(int index);

	public void addIndex(short index);

}
