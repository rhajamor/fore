package org.fore;

import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import org.fore.impl.BufferError;

public interface IData
{

	public enum DataType
	{
		DT_INDEX, // normal, 3 reals per vertex
		DT_POSITION, // Position, 3 reals per vertex.
		DT_NORMAL, // Normal, 3 reals per vertex.
		DT_COLOR, // colour
		DT_TEXTURE_COORDINATES, // Texture coordinates(uv)
		DT_EDGE
	};

	public enum BufferType
	{
		// buffer types
		BT_LONG,
		BT_FLOAT,
		BT_SHORT,
		BT_INT,
		BT_BYTE,
		BT_CHAR
	};

	void setDataBuffer(DataType dataType, Buffer buffer);

	Buffer getDataBuffer(DataType dataType, BufferType bufferType);

	void clearDataBuffer(DataType dataType);

	/**
	 * 
	 * @param gl
	 * @throws BufferError
	 */
	void bind(GL gl) throws BufferError;

	/**
	 * 
	 * @param gl
	 * @throws BufferError
	 */
	void unBind(GL gl) throws BufferError;

	boolean isBound();

	void destroy();

}