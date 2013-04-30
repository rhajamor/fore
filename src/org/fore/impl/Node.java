package org.fore.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.fore.INode;

public class Node extends MovableObject implements INode
{

	protected INode parent;
	protected Map<String, INode> children = new HashMap<String, INode>();

	protected Node(String name, INode parent)
	{
		super(name, "Node");
		this.parent = parent;
	}

	protected Node(String name, INode parent, Quat4f o, Vector3f p)
	{
		super(name, "Node", o, p);
		this.parent = parent;
	}

	protected Node(String name, INode parent, Quat4f o, Vector3f p, Vector3f s)
	{
		super(name, "Node", o, p, s);
		this.parent = parent;
	}

	@Override
	public void setParent(INode parent)
	{
		if (this.parent != null)
			this.parent.removeChild(this.name);
		this.parent = parent;
	}

	@Override
	public INode getParent()
	{
		return parent;
	}

	@Override
	public void addChild(INode child)
	{
		children.put(child.getName(), child);
		child.setParent(this);
	}

	@Override
	public void addChildren(Collection<INode> children)
	{
		for (INode node : children)
			addChild(node);
	}

	@Override
	public List<INode> getChildren()
	{
		return new ArrayList<INode>(children.values());
	}

	public void removeChild(String name)
	{
		INode child = children.remove(name);
		if (child != null)
			child.setParent(null);

	}

	public void removeAllChildren()
	{
		for (String key : children.keySet())
			removeChild(key);
	}

	@Override
	public void removeChild(INode object)
	{
		removeChild(object.getName());
	}

	@Override
	public void removeAndDestroyAllChildren()
	{
		super.destroy();
		Iterator<INode> i = children.values().iterator();
		while (i.hasNext())
			i.next().destroy();
		children.clear();
	}

	@Override
	public void destroy()
	{
		super.destroy();
		children.clear();
	}

	@Override
	public void update()
	{
		super.update();
		Iterator<INode> i = children.values().iterator();
		while (i.hasNext())
		{
			INode iNode = i.next();
			Matrix4f m = iNode.getTransformationMatrix();
			m.mul(getTransformationMatrix(TransformationType.TT_WORLD));
			iNode.setTransformationMatrix(m, TransformationType.TT_WORLD);
			m = null;// garbage
			iNode.update();
		}
	}
}
