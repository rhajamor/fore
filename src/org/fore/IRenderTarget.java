package org.fore;

public interface IRenderTarget extends IResource {
	public enum RenderTargetType {
		RTT_TEXTURE, RTT_WINDOW
	}
	
	public void setBounds(int x, int y, int w, int h);

	public int[] getBounds();

	public void setRenderTargetType(RenderTargetType type);

	public RenderTargetType getRenderTargetType();

	public void setWidth(int w);

	public void setHeight(int h);

	public int getWidth();

	public int getHeight();

	public void setX(int x);

	public void setY(int x);

	public int getX();

	public int getY();

	public void update();

}
