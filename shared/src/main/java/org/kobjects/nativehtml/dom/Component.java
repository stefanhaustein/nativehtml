package org.kobjects.nativehtml.dom;

public interface Component extends Element {
	
	public void setBorderBoxBounds(int x, int y, int width, int height);
	
	public int getIntrinsicMinimumWidth();
	public int getIntrinsicHeightForWidth(int width);

}
