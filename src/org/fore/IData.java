package org.fore;

import java.nio.Buffer;

import javax.microedition.khronos.opengles.GL;

import org.fore.impl.BufferError;

public interface IData {

	public enum DataType {
		DT_INDEX, // normal, 3 reals per vertex
		DT_POSITION, // Position, 3 reals per vertex.
		DT_NORMAL, // Normal, 3 reals per vertex.
		DT_COLOR, DT_TEXTURE_COORDINATES, // Texture coordinates.
	};

	public enum BufferType {
		BT_LONG, BT_FLOAT, BT_SHORT, BT_INT, BT_BYTE, BT_CHAR
	};

	public void setDataBuffer(DataType dataType, Buffer buffer);

	public Buffer getDataBuffer(DataType dataType, BufferType bufferType);

	public void clearDataBuffer(DataType dataType);

	public void bind(GL gl) throws BufferError;

	public void destroy();

}