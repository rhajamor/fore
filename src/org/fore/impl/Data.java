package org.fore.impl;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.media.opengl.GLBase;

import org.fore.IData;

public abstract class Data implements IData {
	enum RenderingMode {

		RM_POINTS(GLBase.GL_POINTS), RM_LINE_STRIP(GL10.GL_LINE_STRIP), RM_LINE_LOOP(
				GL10.GL_LINE_LOOP), RM_LINES(GL10.GL_LINES), RM_TRIANGLE_STRIP(
				GL10.GL_TRIANGLE_STRIP), RM_TRIANGLE_FAN(GL10.GL_TRIANGLE_FAN), RM_TRIANGLES(
				GL10.GL_TRIANGLES);

		private final int value;

		private RenderingMode(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	};

	protected Data() {
		renderingMode = RenderingMode.RM_TRIANGLES;
	}

	protected RenderingMode renderingMode;
	protected Buffer vertexBuffer;
	protected Buffer indexBuffer;
	protected Buffer textCoordBuffer;
	protected Buffer colorBuffer;
	protected Buffer normalBuffer;

	@Override
	public final void setDataBuffer(DataType dataType, Buffer buffer) {
		switch (dataType) {
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
	public final Buffer getDataBuffer(DataType dataType, BufferType bufferType) {
		switch (dataType) {
		case DT_INDEX:
			return getIndexBuffer(bufferType);
		case DT_POSITION:
			getVertexBuffer(bufferType);
		case DT_TEXTURE_COORDINATES:
			getTextCoordBuffer(bufferType);
		case DT_NORMAL:
			getNormalBuffer(bufferType);
		case DT_COLOR:
			getColorBuffer(bufferType);
		}
		return null;
	}

	@Override
	public final void clearDataBuffer(DataType dataType) {
		switch (dataType) {
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
			normalBuffer.clear();
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
	private Buffer getBuffer(ByteBuffer buffer, BufferType bufferType) {
		switch (bufferType) {
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

	protected Buffer getVertexBuffer(BufferType bufferType) {
		return getBuffer((ByteBuffer) vertexBuffer, bufferType);
	}

	protected void setVertexBuffer(Buffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
	}

	protected Buffer getIndexBuffer(BufferType bufferType) {
		return getBuffer((ByteBuffer) indexBuffer, bufferType);
	}

	protected void setIndexBuffer(Buffer indexBuffer) {
		this.indexBuffer = indexBuffer;
	}

	protected Buffer getTextCoordBuffer(BufferType bufferType) {
		return getBuffer((ByteBuffer) textCoordBuffer, bufferType);
	}

	protected void setTextCoordBuffer(Buffer textCoordBuffer) {
		this.textCoordBuffer = textCoordBuffer;
	}

	protected Buffer getColorBuffer(BufferType bufferType) {
		return getBuffer((ByteBuffer) colorBuffer, bufferType);
	}

	protected void setColorBuffer(Buffer colorBuffer) {
		this.colorBuffer = colorBuffer;
	}

	protected Buffer getNormalBuffer(BufferType bufferType) {
		return getBuffer((ByteBuffer) normalBuffer, bufferType);
	}

	protected void setNormalBuffer(Buffer normalBuffer) {
		this.normalBuffer = normalBuffer;
	}

	public RenderingMode getRenderingMode() {
		return renderingMode;
	}

	public void setRenderingMode(RenderingMode renderingMode) {
		this.renderingMode = renderingMode;
	}
}
