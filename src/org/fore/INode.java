package org.fore;

import java.util.Collection;
import java.util.List;

public interface INode extends IMovableObject {

	public void setParent(INode parent);

	public INode getParent();

	public void addChild(INode child);

	public void addChildren(Collection<INode> child);

	public void removeChild(String name);

	public void removeChild(INode object);

	public void removeAllChildren();

	public void removeAndDestroyAllChildren();

	public List<INode> getChildren();

}