package org.fore.impl;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.IData.BufferType;
import org.fore.IData.DataType;
import org.fore.IIndexData;
import org.fore.IMesh;
import org.fore.ISubMesh;
import org.fore.IVertexData;
import org.fore.impl.Math.Matrix4;

public class SubMesh extends MovableObject implements ISubMesh {

	private IVertexData vertexData;
	private IIndexData indexData;
	private IMesh parent;

	protected SubMesh(String name) {
		super(name, "SubMesh");
	}

	protected SubMesh(String name, IMesh parent) {
		super(name, "SubMesh");
		this.parent = parent;
	}

	protected SubMesh(String name, IMesh parent, Quat4f o, Vector3f p,
			Vector3f s) {
		super(name, "SubMesh", o, p, s);
		this.parent = parent;
	}

	@Override
	public void setParent(IMesh mesh) {
		this.parent = mesh;
	}

	@Override
	public IMesh getParent() {
		return parent;
	}

	@Override
	public void setVertexData(IVertexData data) {
		this.vertexData = data;
	}

	@Override
	public IVertexData getVertexData() {
		return vertexData;
	}

	@Override
	public void setIndexData(IIndexData indexData) {
		this.indexData = indexData;
	}

	@Override
	public IIndexData getIndexData() {
		return this.indexData;
	}

	@Override
	public void update() {
		super.update();
		if (vertexData != null) {
			FloatBuffer vBuffer = (FloatBuffer) vertexData.getDataBuffer(
					DataType.DT_POSITION, BufferType.BT_FLOAT);
			FloatBuffer nBuffer = (FloatBuffer) vertexData.getDataBuffer(
					DataType.DT_NORMAL, BufferType.BT_FLOAT);
			float[] pValue = new float[3];
			int pos = 0;
			boolean hasNormals = nBuffer.limit() > 0;
			while (pos < (vBuffer.limit() - 3)) {
				vBuffer.get(pValue);
				Vector3f vr = ((Matrix4) fullTransformMatrix).mul(new Vector3f(
						pValue));
				vr.get(pValue);
				vBuffer.put(pValue, pos, 3);// update buffer
				if (hasNormals) {
					nBuffer.get(pValue);
					vr = ((Matrix4) fullTransformMatrix).mul(new Vector3f(
							pValue));
					vr.normalize();
					vr.get(pValue);
					nBuffer.put(pValue, pos, 3);
				}
				pos += 3;
			}
			vBuffer.position(0);
			nBuffer.position(0);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		if (vertexData != null) {
			vertexData.destroy();
			vertexData = null;
		}
		if (indexData != null) {
			indexData.destroy();
			indexData = null;
		}
	}

}
