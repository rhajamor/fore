package org.fore.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLBase;

import org.fore.IIndexData;

public class IndexData extends Data implements IIndexData {

	private List<Integer> indices;

	public IndexData() {
		indices = new ArrayList<Integer>();
	}

	@Override
	public void bind(GLBase gl) throws BufferError {
		if (indices.isEmpty())
			throw new BufferError("No index data found !");
		indexBuffer = ByteBuffer.allocateDirect(indices.size() * 2);
		((ByteBuffer) indexBuffer).order(ByteOrder.nativeOrder());
		ShortBuffer indBuffer = ((ByteBuffer) indexBuffer).asShortBuffer();
		indBuffer.put(getIndices());
		indBuffer.position(0);
		/*
		 * this is do rendering
		 */
		 gl.glDrawElements(renderingMode.value(), getNumIndices(),
				GL10.GL_UNSIGNED_SHORT, indBuffer);
	}

	@Override
	public int getNumIndices() {
		return indices.size();
	}

	@Override
	public short[] getIndices() {
		short[] data = new short[indices.size()];
		System.arraycopy(indices.toArray(), 0, data, 0, indices.size());
		return data;
	}

	@Override
	public void addIndex(int index) {
		indices.add(index);
	}

	@Override
	public void addIndex(short index) {
		indices.add((int) index);
	}

	@Override
	public void destroy() {
		clearDataBuffer(DataType.DT_INDEX);
		indices.clear();
		indexBuffer = null;
	}
}
