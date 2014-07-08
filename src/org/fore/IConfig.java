package org.fore;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.swt.GLCanvas;

/*
 * no implementation for this interface
 * it is intended to be used as customization interface 
 */
public interface IConfig {

	boolean useGPUComputing();

	void setUpConfig();

	void setWindow(GLWindow window);

	void setCanvas(GLCanvas canvas);

}
