package org.fore.impl;

import org.fore.IResource;

public abstract class Resource implements IResource {

	protected String name;
	protected int id;
	protected String resourceType;

	protected Resource(String name, String resourceType) {
		this.name = name;
		this.resourceType = resourceType;
	}

	protected Resource(int id) {
		this.id = id;
	}

	protected Resource(String name) {
		this.name = name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setType(String type) {
		this.resourceType = type;

	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return resourceType;
	}

}
