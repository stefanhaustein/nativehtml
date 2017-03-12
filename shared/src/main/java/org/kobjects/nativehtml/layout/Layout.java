package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.dom.Element;

public interface Layout {

	/**
	 * If the result is not null, elements will get measured only. 
	 * If width is -1, 
	 */
	void layout(Element parent, int xOfs, int yOfs, int contentWidth, boolean measureOnly, int[] result);
	
}
