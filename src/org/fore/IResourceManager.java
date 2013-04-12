package org.fore;

import java.util.List;

public interface IResourceManager {
	/*
	 * create new dynamic object with default name and id = -1
	 */
	public IResource create();

	/*
	 * create new dynamic object
	 */
	public IResource create(String name, int id);

	/*
	 * create new dynamic object;
	 */
	public IResource create(String name);

	public boolean destroy(); // delete dynamic object

	public boolean destroyAll();

	public boolean bind();

	public boolean unBind();

	public IResource load(int id);

	public IResource load(String name, int id);

	public boolean unload(int id);

	public boolean unload(String name);

	public boolean unloadAll();

	public IResource getResourceByName(String name);

	public IResource getResourceById(int id);

	public List<IResource> getResourcesByType(String type);

	public void register(String type);

	public void unregister(String type);
}
