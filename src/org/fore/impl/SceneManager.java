/**
 * 
 */
package org.fore.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.ICamera;
import org.fore.IColor;
import org.fore.ILight;
import org.fore.ILight.LightType;
import org.fore.IMovableObject;
import org.fore.INode;
import org.fore.IRenderable;
import org.fore.IResource;
import org.fore.ISceneManager;
import org.fore.impl.Math.Quaternion;
import org.fore.impl.Math.Vector3;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @author riadh
 * 
 */
public class SceneManager implements ISceneManager
{

	private static SceneManager instance;
	private Map<String, IResource> resourcesMap;
	private INode rootNode;

	/**
	 * 
	 */
	private SceneManager()
	{
		resourcesMap = new HashMap<String, IResource>();
		rootNode = new Node("Root", null, Quaternion.getIdentity(),
				Vector3.getZero(), Vector3.getUnit());
	}

	public static SceneManager getInstance()
	{
		if (instance == null)
			instance = new SceneManager();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#create()
	 */
	@Override
	public IResource create()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#create(java.lang.String, int)
	 */
	@Override
	public IResource create(String name, int id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#create(java.lang.String)
	 */
	@Override
	public IResource create(String name)
	{
		// XXX use hashcode
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#destroy()
	 */
	@Override
	public boolean destroy()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#destroyAll()
	 */
	@Override
	public boolean destroyAll()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#bind()
	 */
	@Override
	public boolean bind()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#unBind()
	 */
	@Override
	public boolean unBind()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#load(int)
	 */
	@Override
	public IResource load(int id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#load(java.lang.String, int)
	 */
	@Override
	public IResource load(String name, int id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#unload(int)
	 */
	@Override
	public boolean unload(int id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#unload(java.lang.String)
	 */
	@Override
	public boolean unload(String name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#unloadAll()
	 */
	@Override
	public boolean unloadAll()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#getResourceByName(java.lang.String)
	 */
	@Override
	public IResource getResourceByName(String name)
	{
		return resourcesMap.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#getResourceById(int)
	 */
	@Override
	public IResource getResourceById(final int id)
	{
		List<IResource> rc = Lists.newArrayList(Collections2.filter(
				resourcesMap.values(), new Predicate<IResource>()
				{
					public boolean apply(IResource arg0)
					{
						return arg0.getId() == id;
					}
				}));
		if (rc.size() > 0)
			return rc.get(0);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#getResourcesByType(java.lang.String)
	 */
	@Override
	public List<IResource> getResourcesByType(final String type)
	{
		return Lists.newArrayList(Collections2.filter(resourcesMap.values(),
				new Predicate<IResource>()
				{
					public boolean apply(IResource arg0)
					{
						return arg0.getType().equals(type);
					}
				}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#register(java.lang.String)
	 */
	@Override
	public void register(String type)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IResourceManager#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String type)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createLight(java.lang.String)
	 */
	@Override
	public ILight createLight(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createLight(java.lang.String,
	 * org.fore.ILight.LightType)
	 */
	@Override
	public ILight createLight(String name, LightType lightType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createLight(java.lang.String,
	 * javax.vecmath.Vector3f, javax.vecmath.Vector3f)
	 */
	@Override
	public ILight createLight(String name, Vector3f pos, Vector3f dir)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createLight(java.lang.String,
	 * org.fore.ILight.LightType, javax.vecmath.Vector3f,
	 * javax.vecmath.Vector3f)
	 */
	@Override
	public ILight createLight(String name, LightType lightType, Vector3f pos,
			Vector3f dir)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createLight(java.lang.String,
	 * org.fore.ILight.LightType, javax.vecmath.Vector3f,
	 * javax.vecmath.Vector3f, org.fore.IColor)
	 */
	@Override
	public ILight createLight(String name, LightType lightType, Vector3f pos,
			Vector3f dir, IColor color)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getLight(java.lang.String)
	 */
	@Override
	public ILight getLight(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getLightsList()
	 */
	@Override
	public List<ILight> getLightsList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getLightsByType(org.fore.ILight.LightType)
	 */
	@Override
	public List<ILight> getLightsByType(LightType lightType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createCamera(java.lang.String)
	 */
	@Override
	public ICamera createCamera(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createCamera(java.lang.String,
	 * javax.vecmath.Quat4f, javax.vecmath.Vector3f)
	 */
	@Override
	public ICamera createCamera(String name, Quat4f o, Vector3f p)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createCamera(java.lang.String,
	 * javax.vecmath.Quat4f, javax.vecmath.Vector3f, javax.vecmath.Vector3f)
	 */
	@Override
	public ICamera createCamera(String name, Quat4f o, Vector3f pos, Vector3f d)
	{
		// TODO Auto-generated method stub
		return new Camera(name, parent, pos, dir, lookAt, fov, zN, zF, aspect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getCamera(java.lang.String)
	 */
	@Override
	public ICamera getCamera(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getCamerasList()
	 */
	@Override
	public List<ICamera> getCamerasList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createMovableObject(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IMovableObject createMovableObject(String name, String type)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createMovableObject(java.lang.String,
	 * java.lang.String, javax.vecmath.Quat4f, javax.vecmath.Vector3f)
	 */
	@Override
	public IMovableObject createMovableObject(String name, String type,
			Quat4f o, Vector3f p)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createMovableObject(java.lang.String,
	 * java.lang.String, javax.vecmath.Quat4f, javax.vecmath.Vector3f,
	 * javax.vecmath.Vector3f)
	 */
	@Override
	public IMovableObject createMovableObject(String name, String type,
			Quat4f o, Vector3f p, Vector3f s)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getMovableObject(java.lang.String)
	 */
	@Override
	public IMovableObject getMovableObject(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getMovableObjectsList()
	 */
	@Override
	public List<IMovableObject> getMovableObjectsList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getMovableObjectsList(java.lang.String)
	 */
	@Override
	public List<IMovableObject> getMovableObjectsList(String type)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createNode(java.lang.String)
	 */
	@Override
	public INode createNode(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createNode(java.lang.String, org.fore.INode,
	 * javax.vecmath.Quat4f, javax.vecmath.Vector3f)
	 */
	@Override
	public INode createNode(String name, INode parent, Quat4f o, Vector3f p)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createNode(java.lang.String, org.fore.INode)
	 */
	@Override
	public INode createNode(String name, INode parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getNode(java.lang.String)
	 */
	@Override
	public INode getNode(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getNodesList()
	 */
	@Override
	public List<INode> getNodesList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createRenderable(java.lang.String)
	 */
	@Override
	public IRenderable createRenderable(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createRenderable(java.lang.String,
	 * org.fore.INode, javax.vecmath.Quat4f, javax.vecmath.Vector3f)
	 */
	@Override
	public IRenderable createRenderable(String name, INode parent, Quat4f o,
			Vector3f p)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#createRenderable(java.lang.String,
	 * org.fore.INode)
	 */
	@Override
	public IRenderable createRenderable(String name, INode parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getRenderable(java.lang.String)
	 */
	@Override
	public IRenderable getRenderable(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#getRenderablesList()
	 */
	@Override
	public List<IRenderable> getRenderablesList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#destroyResource(org.fore.IResource)
	 */
	@Override
	public void destroyResource(IResource resource)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#destroyResource(java.lang.String)
	 */
	@Override
	public void destroyResource(String name)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.ISceneManager#destroyResourcesByType(java.lang.String)
	 */
	@Override
	public void destroyResourcesByType(String type)
	{
		// TODO Auto-generated method stub

	}

}
