package org.kobjects.nativehtml.html;

import org.kobjects.nativehtml.dom.Element;

public interface HtmlComponent extends Element {
	
	public void setBorderBoxBounds(int x, int y, int width, int height, int containingBoxWidth);
	
	public void moveRelative(int dx, int dy);
	
	public int getIntrinsicContentBoxWidth(boolean minimal);
	public int getIntrinsicContentBoxHeightForWidth(int contentBoxWidth);

}
