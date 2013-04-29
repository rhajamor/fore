package org.fore;

import javax.media.opengl.GL;

public interface ISubMesh extends IResource, IMovableObject
{
	public enum RenderingMode
	{
		RM_POINTS(GL.GL_POINTS),
		RM_LINES(GL.GL_LINES),
		RM_LINE_LOOP(GL.GL_LINE_LOOP),
		RM_LINE_STRIP(GL.GL_LINE_STRIP),
		RM_TRIANGLES(GL.GL_TRIANGLES),
		RM_TRIANGLE_STRIP(GL.GL_TRIANGLE_STRIP),
		RM_TRIANGLE_FAN(GL.GL_TRIANGLE_FAN),
		// OpenGL 3.0 render modes.
		RM_QUADS(0x0007),
		RM_QUADS_STRIP(0x0008),
		RM_POLYGON(0x0009);

		private final int value;

		private RenderingMode(int value)
		{
			this.value = value;
		}

		public int value()
		{
			return this.value;
		}
	};

	void setParent(IMesh mesh);

	IMesh getParent();

	void setVertexData(IVertexData data);

	IVertexData getVertexData();

	void setIndexData(IIndexData indexData);

	IIndexData getIndexData();

	RenderingMode getRenderingMode();

	void setRenderingMode(RenderingMode renderingMode);

}
