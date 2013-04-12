package org.fore;

import java.util.List;

public interface IMaterial extends IResource {

	public void setAmbiant(IColor a);

	public void setShiniess(float f);

	public void setSpecular(IColor s);

	public void setDiffuse(IColor d);

	public void addTexture(ITexture tx);

	public void addShader(IScript tx);

	public IColor getAmbiant();

	public float getShiniess();

	public IColor getSpecular();

	public IColor getDiffuse();

	public ITexture getTexture(String name);

	public List<ITexture> getTexturesList();

	public IScript getShader(String name);

	public List<IScript> getShadersList();
}
