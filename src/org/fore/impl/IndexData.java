package org.fore.impl;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;

import org.fore.IIndexData;

public class IndexData extends Data implements IIndexData
{

	private int numIndices;
	private int bindID;

	public IndexData()
	{

	}

	public IndexData(int indices[])
	{
		setIndices(indices);

	}

	public IndexData(int numIndices)
	{
		prepareBuffer(numIndices * 4, indexBuffer);
	}

	@Override
	public void bind(GL gl) throws BufferError
	{
		// if (numIndices == 0)
		// throw new BufferError("No index data found !");
		// indexBuffer = ByteBuffer.allocateDirect(indices.size() * 2);
		// ((ByteBuffer) indexBuffer).order(ByteOrder.nativeOrder());
		// ShortBuffer indBuffer = ((ByteBuffer) indexBuffer).asShortBuffer();
		// indBuffer.put(getIndices());
		// indBuffer.position(0);
		// /*
		// * this is do rendering
		// */
		// gl.glDrawElements(renderingMode.value(), getNumIndices(),
		// GL.GL_UNSIGNED_SHORT, indBuffer);
	}

	@Override
	public void unBind(GL gl) throws BufferError
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setIndices(int[] indices)
	{
		prepareBuffer(indices.length * 4, indexBuffer);
		for (int index : indices)
			addIndex(index);

		indexBuffer.flip();

	}

	@Override
	public int getNumIndices()
	{
		return numIndices;
	}

	public int[] getIndices()
	{
		int[] indices = new int[numIndices];
		((ByteBuffer) indexBuffer).asIntBuffer().get(indices);
		return indices;
	}

	@Override
	public void addIndex(int index)
	{
		((ByteBuffer) indexBuffer).putInt(index);
	}

	@Override
	public void destroy()
	{
		clearDataBuffer(DataType.DT_INDEX);
		indexBuffer = null;
	}
}
