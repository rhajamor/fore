package org.fore;

import android.graphics.Bitmap;

/*
 * OpenGL ES support  GL10.GL_TEXTURE_2D
 */
public interface ITexture extends IResource {

	/*
	 * to be passed to prepare_texture;
	 */
	public enum TextureUsage {
		TU_RENDERTARGET, TU_AUTOMIPMAP, TU_DEFAULT
	};

	/*
	 * binds texture
	 */
	public void setActive(boolean active);

	public boolean isActive();

	public int getNumMipmaps();

	public void prepareTexture(TextureUsage usage, int mipmap, int format);

	public void loadImage(TextureUsage usage, Bitmap bitmap);

	public void unloadImage();

}
