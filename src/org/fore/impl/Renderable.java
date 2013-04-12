package org.fore.impl;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.IMaterial;
import org.fore.IMesh;
import org.fore.INode;
import org.fore.IRenderable;

public class Renderable extends Node implements IRenderable {

	private IMaterial material;
	private IMesh mesh;
	private boolean visible;
	private boolean loaded;

	protected Renderable(String name, INode parent) {
		super(name, parent);
		setType("Renderable");
		this.name = name;
	}

	protected Renderable(String name, INode parent, Quat4f orientation,
			Vector3f pos) {
		super(name, parent, orientation, pos);
		setType("Renderable");
		this.name = name;
	}

	public IMaterial getMaterial() {
		return material;
	}

	public void setMaterial(IMaterial material) {
		this.material = material;
	}

	public IMesh getMesh() {
		return mesh;
	}

	public void setMesh(IMesh mesh) {
		this.mesh = mesh;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (mesh != null)
			mesh.destroy();
		if (material != null)
			material.destroy();
	}

	@Override
	public void update() {
		super.update();
		if (mesh != null)
			mesh.update();
	}

}
