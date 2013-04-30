/**
 * 
 */
package org.fore.impl;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GLBase;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.fore.IFOREEngine;
import org.fore.IRenderable;
import org.fore.IResourceManager;

/**
 * @author riadh
 * 
 */
public class FOREEngine implements IFOREEngine
{
	private Queue<IRenderable> renderQueue;

	private GL renderSystem;

	/**
	 * 
	 */
	public FOREEngine()
	{

		renderQueue = new LinkedBlockingQueue<IRenderable>();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IFOREEngine#start()
	 */
	@Override
	public boolean start()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IFOREEngine#stop()
	 */
	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IFOREEngine#renderOnce()
	 */
	@Override
	public void renderOnce()
	{
		// clear buffers
		renderSystem.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		((GLMatrixFunc) renderSystem).glLoadIdentity();
		
		Iterator<IRenderable> renderables = renderQueue.iterator();
		while (renderables.hasNext())
			renderables.next().render(getRenderSystem());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fore.IFOREEngine#startRendering()
	 */
	@Override
	public void startRendering()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void register(IResourceManager manager)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unRegister(IResourceManager manager)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Queue<IRenderable> getRenderingQueue()
	{
		return renderQueue;
	}

	@Override
	public GLBase getRenderSystem()
	{
		return renderSystem;
	}
}
