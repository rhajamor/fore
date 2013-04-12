package org.fore;

public interface IResource {
	/*
	 * must be unique
	 */
	public void setName(String name);

	/*
	 * @see ResourceType
	 */
	public void setType(String type);

	/*
	 * the id (in android is the ressourceID to be used to retrieve the
	 * resources)
	 */
	public void setId(int id);

	public String getName();

	public int getId();

	public String getType();

	public void destroy();

}
