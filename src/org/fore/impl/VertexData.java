package org.fore.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.fore.IColor;
import org.fore.IVertexData;

public class VertexData extends Data implements IVertexData {

	private List<Vector3f> vertices;
	private List<Vector3f> normals;
	private List<Point2f> textCoords;
	private List<IColor> colors;
	private GL gl;// for internal usage

	public VertexData() {
		vertices = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		colors = new ArrayList<IColor>();
		textCoords = new ArrayList<Point2f>();
	}

	@Override
	public void bind(GL gl) throws BufferError {
		this.gl = gl;
		if (vertices.size() == 0)
			throw new BufferError("No vertex data found!");
		vertexBuffer = ByteBuffer.allocateDirect(vertices.size() * 3 * 4);
		((ByteBuffer) vertexBuffer).order(ByteOrder.nativeOrder());
		FloatBuffer fBuffer = ((ByteBuffer) vertexBuffer).asFloatBuffer();
		for (Vector3f v : vertices) {
			fBuffer.put(v.x);
			fBuffer.put(v.y);
			fBuffer.put(v.z);
		}
		fBuffer.position(0);
		((GL10) gl).glEnableClientState(GL10.GL_VERTEX_ARRAY);
		((GL10) gl).glVertexPointer(3, GL10.GL_FLOAT, 0, fBuffer);

		if (normals.size() > 0) {
			normalBuffer = ByteBuffer.allocateDirect(normals.size() * 3 * 4);
			((ByteBuffer) normalBuffer).order(ByteOrder.nativeOrder());
			fBuffer = ((ByteBuffer) normalBuffer).asFloatBuffer();
			for (Vector3f v : normals) {
				fBuffer.put(v.x);
				fBuffer.put(v.y);
				fBuffer.put(v.z);
			}
			fBuffer.position(0);
			((GL10) gl).glEnableClientState(GL10.GL_NORMAL_ARRAY);
			((GL10) gl).glNormalPointer(GL10.GL_FLOAT, 0, fBuffer);
		}

		if (textCoords.size() > 0) {
			textCoordBuffer = ByteBuffer
					.allocateDirect(textCoords.size() * 2 * 4);
			((ByteBuffer) textCoordBuffer).order(ByteOrder.nativeOrder());
			fBuffer = ((ByteBuffer) textCoordBuffer).asFloatBuffer();
			for (Point2f v : textCoords) {
				fBuffer.put(v.x);
				fBuffer.put(v.y);
			}
			fBuffer.position(0);

			((GL10) gl).glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			((GL10) gl).glTexCoordPointer(2, GL10.GL_FLOAT, 0, fBuffer);
		}

		if (colors.size() > 0) {
			colorBuffer = ByteBuffer.allocateDirect(colors.size() * 3 * 4);
			((ByteBuffer) colorBuffer).order(ByteOrder.nativeOrder());
			fBuffer = ((ByteBuffer) colorBuffer).asFloatBuffer();
			for (IColor v : colors) {
				fBuffer.put(v.getR());
				fBuffer.put(v.getG());
				fBuffer.put(v.getB());
				fBuffer.put(v.getA());
			}
			fBuffer.position(0);
			((GL10) gl).glEnableClientState(GL10.GL_COLOR_ARRAY);
			((GL10) gl).glColorPointer(4, GL10.GL_FLOAT, 0, fBuffer);
		}

	}

	public void addVertex(Vector3f vertex) {
		vertices.add(vertex);
	}

	public void addVertex(float x, float y, float z) {
		vertices.add(new Vector3f(x, y, z));
	}

	public void addTextCoord(Point2f uv) {
		textCoords.add(uv);
	}

	public void addTextCoord(float u, float v) {
		textCoords.add(new Point2f(u, v));
	}

	public void addTextCoord(short u, short v) {
		textCoords.add(new Point2f(u, v));
	}

	public void addNormal(Vector3f normal) {
		normals.add(normal);
	}

	public void addNormal(float x, float y, float z) {
		normals.add(new Vector3f(x, y, z));
	}

	public void addColor(IColor color) {
		colors.add(color);
	}

	public void addColor(float r, float g, float b, float a) {
		colors.add(new Color(r, g, b, a));
	}

	@Override
	public void destroy() {
		if (gl != null) {
			// XXX check on this
			((GL10) gl).glDisableClientState(GL10.GL_COLOR_ARRAY);
			((GL10) gl).glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			((GL10) gl).glDisableClientState(GL10.GL_VERTEX_ARRAY);
			((GL10) gl).glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		clearDataBuffer(DataType.DT_COLOR);
		clearDataBuffer(DataType.DT_NORMAL);
		clearDataBuffer(DataType.DT_POSITION);
		clearDataBuffer(DataType.DT_TEXTURE_COORDINATES);
		colorBuffer = null;
		normalBuffer = null;
		vertexBuffer = null;
		textCoordBuffer = null;
		colors.clear();
		colors = null;
		normals.clear();
		normals = null;
		vertices.clear();
		vertices = null;
		textCoords.clear();
		textCoords = null;
	}
}
