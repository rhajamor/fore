package org.fore.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fore.IColor;
import org.fore.IMaterial;
import org.fore.IScript;
import org.fore.ITexture;

public class Material extends Resource implements IMaterial {

	private IColor ambiant;
	private IColor specular;
	private IColor diffuse;
	private Map<String, ITexture> texturesMap = new HashMap<String, ITexture>();
	private Map<String, IScript> shadersMap = new HashMap<String, IScript>();
	private float shininess;

	protected Material(String name) {
		super(name);
	}

	
	public IColor getAmbiant() {
		return ambiant;
	}

	public void setAmbiant(IColor ambiant) {
		this.ambiant = ambiant;
	}

	public IColor getSpecular() {
		return specular;
	}

	public void setSpecular(IColor specular) {
		this.specular = specular;
	}

	public IColor getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(IColor diffuse) {
		this.diffuse = diffuse;
	}

	public List<ITexture> getTexturesList() {
		return new ArrayList<ITexture>(texturesMap.values());
	}

	public void addTexture(ITexture texture) {
		this.texturesMap.put(texture.getName(), texture);
	}

	@Override
	public void setShiniess(float f) {
		this.shininess = f;

	}

	@Override
	public float getShiniess() {
		return this.shininess;
	}

	public void destroy() {
		Iterator<ITexture> i = texturesMap.values().iterator();
		while (i.hasNext())
			i.next().destroy();
		texturesMap.clear();
		Iterator<IScript> j = shadersMap.values().iterator();
		while (j.hasNext())
			j.next().destroy();
		shadersMap.clear();
		this.ambiant = null;
		this.diffuse = null;
		this.specular = null;
	}

	@Override
	public void addShader(IScript tx) {
		shadersMap.put(tx.getName(), tx);
	}

	@Override
	public ITexture getTexture(String name) {
		return texturesMap.get(name);
	}

	@Override
	public IScript getShader(String name) {
		return shadersMap.get(name);
	}

	@Override
	public List<IScript> getShadersList() {
		return new ArrayList<IScript>(shadersMap.values());
	}

}
