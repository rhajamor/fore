package org.fore.impl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.vecmath.Point2i;
import javax.vecmath.Vector3f;

import org.fore.IColor;
import org.fore.IVertexData;

public class VertexData extends Data implements IVertexData
{

	private IntBuffer bindings;
	private int numVertices;
	private int numNormals;
	private int numTextureCoords;
	private int numColors;

	public VertexData(int numVertices, int numNormal, int numTextureCoords,
			int numColors)
	{
		vertexBuffer = ByteBuffer.allocateDirect(numVertices * 3 * 4);
		normalsBuffer = ByteBuffer.allocateDirect(numNormal * 3 * 4);
		textCoordBuffer = ByteBuffer.allocateDirect(numTextureCoords * 2 * 2);
		// save as rgba
		colorBuffer = ByteBuffer.allocateDirect(numColors * 8);

	}

	public VertexData(List<Vector3f> vertices, List<Vector3f> normals,
			List<Point2i> textureCoords, List<IColor> colors)
	{
		setVertices(vertices);
		setNormals(normals);
		setTextureCoords(textureCoords);
		setColors(colors);
	}

	public VertexData()
	{

	}

	@Override
	public void bind(GL gl) throws BufferError
	{
		if (numVertices == 0)
			throw new BufferError("No vertex data found!");
		int numBuffers = 1;
		if (numNormals > 0)
			numBuffers++;
		if (numColors > 0)
			numBuffers++;
		if (numTextureCoords > 0)
			numBuffers++;
		bindings = IntBuffer.allocate(numBuffers);
		gl.glGenBuffers(numBuffers, bindings);
		// gl.glBindBuffer(target, buffer)
	}

	@Override
	public void unBind(GL gl) throws BufferError
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void normalizeNormals()
	{
		FloatBuffer normals = ((ByteBuffer) normalsBuffer).asFloatBuffer();
		float[] fNormal = new float[3];
		Vector3f vNormal = new Vector3f();
		for (int i = 0; i < normals.capacity(); i += 3)
		{
			normals.get(fNormal, i, 3);
			vNormal.set(vNormal);
			vNormal.normalize();
			vNormal.get(fNormal);
			normals.put(fNormal, i, 3);
		}

	}

	@Override
	public void computeNormals()
	{
		prepareBuffer(vertexBuffer.capacity(), normalsBuffer);
		vertexBuffer.flip();
		((ByteBuffer) normalsBuffer).put((ByteBuffer) vertexBuffer);
		normalizeNormals();
	}

	@Override
	public void setVertices(List<Vector3f> vertices)
	{
		if (vertices.size() == 0)
			new BufferError("Number of vertices must be > 0 !");
		prepareBuffer(vertices.size() * 3 * 4, vertexBuffer);
		for (Vector3f vertex : vertices)
			addVertex(vertex);
		// Prepare buffer for reading
		vertexBuffer.flip();
	}

	@Override
	public void setNormals(List<Vector3f> normals)
	{
		if (normals.size() >= 0)
		{
			prepareBuffer(normals.size() * 3 * 4, normalsBuffer);
			for (Vector3f normal : normals)
				addNormal(normal);
			// Prepare buffer for reading
			normalsBuffer.flip();
		}
	}

	@Override
	public void setTextureCoords(List<Point2i> textCoords)
	{
		if (textCoords.size() >= 0)
		{
			prepareBuffer(textCoords.size() * 2 * 2, textCoordBuffer);
			for (Point2i textCoord : textCoords)
				addTextCoord(textCoord);
			textCoordBuffer.flip();
		}
	}

	@Override
	public void setColors(List<IColor> colors)
	{
		if (colors.size() >= 0)
		{
			prepareBuffer(colors.size() * 8, colorBuffer);
			for (IColor color : colors)
				addColor(color);
			colorBuffer.flip();
		}
	}

	// public void internalBind(GL gl) throws BufferError
	// {
	// this.gl = gl;
	// if (vertices.size() == 0)
	// throw new BufferError("No vertex data found!");
	// vertexBuffer = ByteBuffer.allocateDirect(vertices.size() * 3 * 4);
	// ((ByteBuffer) vertexBuffer).order(ByteOrder.nativeOrder());
	// FloatBuffer fBuffer = ((ByteBuffer) vertexBuffer).asFloatBuffer();
	// for (Vector3f v : vertices)
	// {
	// fBuffer.put(v.x);
	// fBuffer.put(v.y);
	// fBuffer.put(v.z);
	// }
	// fBuffer.position(0);
	// ((GLPointerFunc) gl).glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
	// ((GLPointerFunc) gl).glVertexPointer(3, GL.GL_FLOAT, 0, fBuffer);
	//
	// if (normals.size() > 0)
	// {
	// normalBuffer = ByteBuffer.allocateDirect(normals.size() * 3 * 4);
	// ((ByteBuffer) normalBuffer).order(ByteOrder.nativeOrder());
	// fBuffer = ((ByteBuffer) normalBuffer).asFloatBuffer();
	// for (Vector3f v : normals)
	// {
	// fBuffer.put(v.x);
	// fBuffer.put(v.y);
	// fBuffer.put(v.z);
	// }
	// fBuffer.position(0);
	// ((GLPointerFunc) gl)
	// .glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
	// ((GLPointerFunc) gl).glNormalPointer(GL.GL_FLOAT, 0, fBuffer);
	// }
	//
	// if (textCoords.size() > 0)
	// {
	// textCoordBuffer = ByteBuffer
	// .allocateDirect(textCoords.size() * 2 * 4);
	// ((ByteBuffer) textCoordBuffer).order(ByteOrder.nativeOrder());
	// fBuffer = ((ByteBuffer) textCoordBuffer).asFloatBuffer();
	// for (Point2f v : textCoords)
	// {
	// fBuffer.put(v.x);
	// fBuffer.put(v.y);
	// }
	// fBuffer.position(0);
	//
	// ((GLPointerFunc) gl)
	// .glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
	// ((GLPointerFunc) gl).glTexCoordPointer(2, GL.GL_FLOAT, 0, fBuffer);
	// }
	//
	// if (colors.size() > 0)
	// {
	// colorBuffer = ByteBuffer.allocateDirect(colors.size() * 3 * 4);
	// ((ByteBuffer) colorBuffer).order(ByteOrder.nativeOrder());
	// fBuffer = ((ByteBuffer) colorBuffer).asFloatBuffer();
	// for (IColor v : colors)
	// {
	// fBuffer.put(v.getR());
	// fBuffer.put(v.getG());
	// fBuffer.put(v.getB());
	// fBuffer.put(v.getA());
	// }
	// fBuffer.position(0);
	// ((GLPointerFunc) gl)
	// .glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);
	// ((GLPointerFunc) gl).glColorPointer(4, GL.GL_FLOAT, 0, fBuffer);
	// }
	//
	// }

	public void addVertex(Vector3f vertex)
	{
		((ByteBuffer) vertexBuffer).putFloat(vertex.x).putFloat(vertex.y)
				.putFloat(vertex.z);
		numVertices++;
	}

	public void addVertex(float x, float y, float z)
	{
		((ByteBuffer) vertexBuffer).putFloat(x).putFloat(y).putFloat(z);
		numVertices++;
	}

	public void addTextCoord(Point2i uv)
	{
		((ByteBuffer) textCoordBuffer).putShort((short) uv.x).putShort(
				(short) uv.y);
		numTextureCoords++;
	}

	public void addTextCoord(short u, short v)
	{
		((ByteBuffer) textCoordBuffer).putShort(u).putShort(v);
		numTextureCoords++;
	}

	public void addNormal(Vector3f normal)
	{
		((ByteBuffer) normalsBuffer).putFloat(normal.x).putFloat(normal.y)
				.putFloat(normal.z);
		numNormals++;
	}

	public void addNormal(float x, float y, float z)
	{
		((ByteBuffer) normalsBuffer).putFloat(x).putFloat(y).putFloat(z);
		numNormals++;
	}

	public void addColor(IColor color)
	{
		((ByteBuffer) colorBuffer).putLong(color.asRGBA());
		numColors++;
	}

	public void addColor(float r, float g, float b, float a)
	{
		((ByteBuffer) colorBuffer).putLong(new Color(r, g, b, a).asRGBA());
		numColors++;
	}

	@Override
	public void addColor(long rgba)
	{
		((ByteBuffer) colorBuffer).putLong(rgba);
		numColors++;
	}

	public int getNumVertices()
	{
		return numVertices;
	}

	public int getNumNormals()
	{
		return numNormals;
	}

	public int getNumTextureCoords()
	{
		return numTextureCoords;
	}

	public int getNumColors()
	{
		return numColors;
	}

	@Override
	public void destroy()
	{
		// if (gl != null)
		// {
		// // XXX check on this
		// ((GLPointerFunc) gl)
		// .glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		// ((GLPointerFunc) gl)
		// .glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
		// ((GLPointerFunc) gl)
		// .glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
		// ((GLPointerFunc) gl)
		// .glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
		// }
		clearDataBuffer(DataType.DT_COLOR);
		clearDataBuffer(DataType.DT_NORMAL);
		clearDataBuffer(DataType.DT_POSITION);
		clearDataBuffer(DataType.DT_TEXTURE_COORDINATES);
		colorBuffer = null;
		normalsBuffer = null;
		vertexBuffer = null;
		textCoordBuffer = null;
	}

}
