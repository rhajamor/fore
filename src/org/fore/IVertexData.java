package org.fore;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

public interface IVertexData extends IData {
	
	
	public void addVertex(Vector3f vertex);

	public void addVertex(float x, float y, float z);

	public void addTextCoord(Point2f uv);

	public void addTextCoord(float u, float v);

	public void addTextCoord(short u, short v);

	public void addNormal(Vector3f normal);

	public void addNormal(float x, float y, float z);

	public void addColor(IColor color);

	public void addColor(float r, float g, float b, float a);

}
