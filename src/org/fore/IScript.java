package org.fore;

import java.io.InputStream;

public interface IScript extends IResource {

	public void loadFromStream(InputStream stream);

	public void loadFromString(String script);

	public void addParameters(Object... p);

	public Object getParameter(Object o);

	public void update();
}
