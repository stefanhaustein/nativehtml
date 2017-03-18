package org.kobjects.nativehtml.html;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.layout.Layout;

public interface HtmlComponent extends Element {
	
	public void setBorderBoxBounds(int x, int y, int width, int height, int parentContentBoxWidth);
	
	public void moveRelative(int dx, int dy);
	
	public int getIntrinsicContentBoxWidth(Layout.Directive directive, int parentContentBoxWidth);
	public int getIntrinsicContentBoxHeightForWidth(int contentBoxWidth, int parentContentBoxWidth);

}
