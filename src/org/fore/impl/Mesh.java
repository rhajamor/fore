package org.fore.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fore.IMesh;
import org.fore.ISubMesh;

public class Mesh extends MovableObject implements IMesh {

	private Map<String, ISubMesh> subMeshesMap;

	protected Mesh(String name) {
		super(name, "Mesh");
	}

	@Override
	public int getNumSubmeshes() {
		return subMeshesMap.size();
	}

	@Override
	public List<ISubMesh> getSubmeshList() {
		return new ArrayList<ISubMesh>(subMeshesMap.values());
	}

	@Override
	public void addSubMesh(ISubMesh subMesh) {
		subMeshesMap.put(subMesh.getName(), subMesh);
	}

	@Override
	public void update() {
		super.update();
		Iterator<ISubMesh> i = subMeshesMap.values().iterator();
		while (i.hasNext())
			i.next().update();
	}

	@Override
	public void destroy() {
		super.destroy();
		Iterator<ISubMesh> i = subMeshesMap.values().iterator();
		while (i.hasNext())
			i.next().destroy();
	}
}
