package org.fore.impl;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.fore.IData;

public abstract class Data implements IData
{
	protected Buffer vertexBuffer;
	protected Buffer indexBuffer;
	protected Buffer textCoordBuffer;
	protected Buffer colorBuffer;
	protected Buffer normalsBuffer;
	protected Buffer edgesBuffer;
	protected boolean bound;

	protected Data()
	{

	}

	@Override
	public final void setDataBuffer(DataType dataType, Buffer buffer)
	{
		switch (dataType)
		{
		case DT_INDEX:
			setIndexBuffer(buffer);
			break;
		case DT_POSITION:
			setVertexBuffer(buffer);
			break;
		case DT_TEXTURE_COORDINATES:
			setTextCoordBuffer(buffer);
			break;
		case DT_NORMAL:
			setNormalBuffer(buffer);
			break;
		case DT_COLOR:
			setColorBuffer(buffer);
			break;
		}
	}

	@Override
	public final Buffer getDataBuffer(DataType dataType, BufferType bufferType)
	{
		switch (dataType)
		{
		case DT_INDEX:
			return getIndexBuffer(bufferType);
		case DT_POSITION:
			return getVertexBuffer(bufferType);
		case DT_TEXTURE_COORDINATES:
			return getTextCoordBuffer(bufferType);
		case DT_NORMAL:
			return getNormalBuffer(bufferType);
		case DT_COLOR:
			return getColorBuffer(bufferType);

		}
		return null;
	}

	@Override
	public final void clearDataBuffer(DataType dataType)
	{
		switch (dataType)
		{
		case DT_INDEX:
			indexBuffer.clear();
			break;
		case DT_POSITION:
			vertexBuffer.clear();
			break;
		case DT_TEXTURE_COORDINATES:
			textCoordBuffer.clear();
			break;
		case DT_NORMAL:
			normalsBuffer.clear();
			break;
		case DT_COLOR:
			colorBuffer.clear();
			break;
		}
	}

	/**
	 * 
	 * @param buffer
	 * @param bufferType
	 * @return a buffer with the specified type (cast it)
	 */
	private Buffer getBuffer(ByteBuffer buffer, BufferType bufferType)
	{
		switch (bufferType)
		{
		case BT_FLOAT:
			return buffer.asFloatBuffer();
		case BT_BYTE:
			return buffer;
		case BT_CHAR:
			return buffer.asCharBuffer();
		case BT_INT:
			return buffer.asIntBuffer();
		case BT_SHORT:
			return buffer.asShortBuffer();
		case BT_LONG:
			return buffer.asLongBuffer();
		}
		return null;
	}

	protected void prepareBuffer(int capacity, Buffer buffer)
	{
		if (buffer == null)
			buffer = ByteBuffer.allocateDirect(capacity);
		else
		{
			if (buffer.capacity() < capacity || buffer.capacity() > capacity)
				buffer = ByteBuffer.allocateDirect(capacity);
			else
				buffer.clear();
		}
	}

	protected Buffer getVertexBuffer(BufferType bufferType)
	{
		return getBuffer((ByteBuffer) vertexBuffer, bufferType);
	}

	protected void setVertexBuffer(Buffer vertexBuffer)
	{
		this.vertexBuffer = vertexBuffer;
	}

	protected Buffer getIndexBuffer(BufferType bufferType)
	{
		return getBuffer((ByteBuffer) indexBuffer, bufferType);
	}

	protected void setIndexBuffer(Buffer indexBuffer)
	{
		this.indexBuffer = indexBuffer;
	}

	protected Buffer getTextCoordBuffer(BufferType bufferType)
	{
		return getBuffer((ByteBuffer) textCoordBuffer, bufferType);
	}

	protected void setTextCoordBuffer(Buffer textCoordBuffer)
	{
		this.textCoordBuffer = textCoordBuffer;
	}

	protected Buffer getColorBuffer(BufferType bufferType)
	{
		return getBuffer((ByteBuffer) colorBuffer, bufferType);
	}

	protected void setColorBuffer(Buffer colorBuffer)
	{
		this.colorBuffer = colorBuffer;
	}

	protected Buffer getNormalBuffer(BufferType bufferType)
	{
		return getBuffer((ByteBuffer) normalsBuffer, bufferType);
	}

	protected void setNormalBuffer(Buffer normalBuffer)
	{
		this.normalsBuffer = normalBuffer;
	}
	
	@Override
	public boolean isBound()
	{
		return bound;
	}

}
