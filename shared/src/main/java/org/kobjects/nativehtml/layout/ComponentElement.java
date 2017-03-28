package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.dom.Element;

public interface ComponentElement extends Element {
	
	public void setBorderBoxBounds(float x, float y, float width, float height, float parentContentBoxWidth);
	
	public void moveRelative(float dx, float dy);
	
	public float getIntrinsicContentBoxWidth(Layout.Directive directive, float parentContentBoxWidth);
	public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth);

}
