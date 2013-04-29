package org.fore;

import java.util.List;

import javax.vecmath.Point2i;
import javax.vecmath.Vector3f;

public interface IVertexData extends IData
{

	void setVertices(List<Vector3f> vertices);

	void setNormals(List<Vector3f> normals);

	void normalizeNormals();

	void computeNormals();

	void setTextureCoords(List<Point2i> textCoords);

	void setColors(List<IColor> colors);

	void addVertex(Vector3f vertex);

	void addVertex(float x, float y, float z);

	void addTextCoord(Point2i uv);

	void addTextCoord(short u, short v);

	void addNormal(Vector3f normal);

	void addNormal(float x, float y, float z);

	void addColor(IColor color);

	void addColor(float r, float g, float b, float a);

	void addColor(long rgba);

	int getNumVertices();

	int getNumNormals();

	int getNumTextureCoords();

	int getNumColors();
}
