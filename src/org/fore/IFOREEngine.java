/**
 * 
 */
package org.fore;

import java.util.Queue;

import javax.media.opengl.GLBase;

/**
 * @author riadh
 * 
 */
public interface IFOREEngine
{
	Queue<IRenderable> getRenderingQueue();

	boolean start(IConfig config);

	void stop();

	void register(IResourceManager manager);

	void unRegister(IResourceManager manager);

	void renderOnce();

	void startRendering();

	GLBase getRenderSystem();
}
