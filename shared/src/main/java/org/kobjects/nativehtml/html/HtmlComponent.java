package org.kobjects.nativehtml.html;

import org.kobjects.nativehtml.dom.Element;

public interface HtmlComponent extends Element {
	
	public void setBorderBoxBoundsDp(int x, int y, int width, int height);
	
	public int getIntrinsicMinimumBorderBoxWidth();
	public int getIntrinsicBorderBoxHeightForWidth(int width);

}
