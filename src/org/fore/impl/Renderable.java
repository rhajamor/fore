package org.fore.impl;

import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLBase;
import javax.media.opengl.fixedfunc.GLPointerFunc;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.IData.BufferType;
import org.fore.IData.DataType;
import org.fore.IIndexData;
import org.fore.IMaterial;
import org.fore.IMesh;
import org.fore.INode;
import org.fore.IRenderable;
import org.fore.ISubMesh;
import org.fore.IVertexData;

public class Renderable extends Node implements IRenderable
{

	private IMaterial material;
	private IMesh mesh;
	private boolean visible;
	private boolean loaded;

	protected Renderable(String name, INode parent)
	{
		super(name, parent);
		setType("Renderable");
		this.name = name;
	}

	protected Renderable(String name, INode parent, Quat4f orientation,
			Vector3f pos)
	{
		super(name, parent, orientation, pos);
		setType("Renderable");
		this.name = name;
	}

	public IMaterial getMaterial()
	{
		return material;
	}

	public void setMaterial(IMaterial material)
	{
		this.material = material;
	}

	public IMesh getMesh()
	{
		return mesh;
	}

	public void setMesh(IMesh mesh)
	{
		this.mesh = mesh;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}

	@Override
	public void destroy()
	{
		super.destroy();
		if (mesh != null)
			mesh.destroy();
		if (material != null)
			material.destroy();
	}

	@Override
	public void update()
	{
		super.update();
		if (mesh != null)
			mesh.update();
	}

	@Override
	public void render(GLBase gl)
	{
		if (visible)
		{
			GLPointerFunc glFunc = (GLPointerFunc) gl;
			for (ISubMesh subMesh : mesh.getSubmeshList())
			{
				IVertexData vertexData = subMesh.getVertexData();
				IIndexData indexData = subMesh.getIndexData();
				if (!vertexData.isBound())
				{
					if (vertexData.getNumNormals() == 0)
						vertexData.computeNormals();
					Buffer fBuffer = vertexData.getDataBuffer(
							DataType.DT_POSITION, BufferType.BT_FLOAT);
					glFunc.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
					glFunc.glVertexPointer(3, GL.GL_FLOAT, 0, fBuffer);
					fBuffer = vertexData.getDataBuffer(DataType.DT_NORMAL,
							BufferType.BT_FLOAT);
					glFunc.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
					glFunc.glNormalPointer(GL.GL_FLOAT, 0, fBuffer);
					// textures
					if (vertexData.getNumTextureCoords() > 0)
					{
						fBuffer = vertexData.getDataBuffer(
								DataType.DT_TEXTURE_COORDINATES,
								BufferType.BT_SHORT);
						glFunc.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
						glFunc.glTexCoordPointer(2, GL.GL_SHORT, 0, fBuffer);
					}
					// per-vertex color
					// TODO check on this
					if (vertexData.getNumColors() > 0)
					{
						fBuffer = vertexData.getDataBuffer(DataType.DT_COLOR,
								BufferType.BT_INT);
						glFunc.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
						glFunc.glColorPointer(1, GL2ES2.GL_INT, 0, fBuffer);
					}
					if (indexData.getNumIndices() > 0)
					{
						// draw elements
						((GL) gl).glDrawElements(subMesh.getRenderingMode()
								.value(), vertexData.getNumVertices(),
								GL.GL_UNSIGNED_BYTE, indexData.getDataBuffer(
										DataType.DT_INDEX, BufferType.BT_BYTE));

					} else
					{
						((GL) gl).glDrawArrays(subMesh.getRenderingMode()
								.value(), 0, vertexData.getNumVertices());
					}

					glFunc.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
					glFunc.glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
					if (vertexData.getNumTextureCoords() > 0)
						glFunc.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
					if (vertexData.getNumColors() > 0)
						glFunc.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);

				}

			}
		}

	}
}
