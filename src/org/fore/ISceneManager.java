package org.fore;

import java.util.List;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.ILight.LightType;

public interface ISceneManager extends IResourceManager
{


	ILight createLight(String name);

	ILight createLight(String name, LightType lightType);

	ILight createLight(String name, Vector3f pos, Vector3f dir);

	ILight createLight(String name, LightType lightType, Vector3f pos,
			Vector3f dir);

	ILight createLight(String name, LightType lightType, Vector3f pos,
			Vector3f dir, IColor color);

	ILight getLight(String name);

	List<ILight> getLightsList();

	List<ILight> getLightsByType(LightType lightType);

	//
	ICamera createCamera(String name);

	ICamera createCamera(String name, Quat4f o, Vector3f p);

	ICamera createCamera(String name, Quat4f o, Vector3f pos, Vector3f d);

	ICamera getCamera(String name);

	List<ICamera> getCamerasList();

	//
	IMovableObject createMovableObject(String name, String type);

	IMovableObject createMovableObject(String name, String type, Quat4f o,
			Vector3f p);

	IMovableObject createMovableObject(String name, String type, Quat4f o,
			Vector3f p, Vector3f s);

	IMovableObject getMovableObject(String name);

	List<IMovableObject> getMovableObjectsList();

	List<IMovableObject> getMovableObjectsList(String type);

	//
	INode createNode(String name);

	INode createNode(String name, INode parent, Quat4f o, Vector3f p);

	INode createNode(String name, INode parent);

	INode getNode(String name);

	List<INode> getNodesList();

	//
	IRenderable createRenderable(String name);

	IRenderable createRenderable(String name, INode parent, Quat4f o, Vector3f p);

	IRenderable createRenderable(String name, INode parent);

	IRenderable getRenderable(String name);

	List<IRenderable> getRenderablesList();

	void destroyResource(IResource resource);

	void destroyResource(String name);

	void destroyResourcesByType(String type);

}
