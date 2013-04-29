package org.fore;

import java.util.List;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.ILight.LightType;

public interface ISceneManager extends IResourceManager {
	
	public ILight createLight(String name);

	public ILight createLight(String name, LightType lightType);

	public ILight createLight(String name, Vector3f pos, Vector3f dir);

	public ILight createLight(String name, LightType lightType, Vector3f pos,
			Vector3f dir);

	public ILight createLight(String name, LightType lightType, Vector3f pos,
			Vector3f dir, IColor color);

	public ILight getLight(String name);

	public List<ILight> getLightsList();

	public List<ILight> getLightsByType(LightType lightType);

	//
	public ICamera createCamera(String name);

	public ICamera createCamera(String name, Quat4f o, Vector3f p);

	public ICamera createCamera(String name, Quat4f o, Vector3f pos, Vector3f d);

	public ICamera getCamera(String name);

	public List<ICamera> getCamerasList();

	//
	public IMovableObject createMovableObject(String name, String type);

	public IMovableObject createMovableObject(String name, String type,
			Quat4f o, Vector3f p);

	public IMovableObject createMovableObject(String name, String type,
			Quat4f o, Vector3f p, Vector3f s);

	public IMovableObject getMovableObject(String name);

	public List<IMovableObject> getMovableObjectsList();

	public List<IMovableObject> getMovableObjectsList(String type);

	//
	public INode createNode(String name);

	public INode createNode(String name, INode parent, Quat4f o, Vector3f p);

	public INode createNode(String name, INode parent);

	public INode getNode(String name);

	public List<INode> getNodesList();

	//
	public IRenderable createRenderable(String name);

	public IRenderable createRenderable(String name, INode parent, Quat4f o,
			Vector3f p);

	public IRenderable createRenderable(String name, INode parent);

	public IRenderable getRenderable(String name);

	public List<IRenderable> getRenderablesList();

	public void destroyResource(IResource resource);

	public void destroyResource(String name);

	public void destroyResourcesByType(String type);

	public void render();

	public void startRendering();

}
