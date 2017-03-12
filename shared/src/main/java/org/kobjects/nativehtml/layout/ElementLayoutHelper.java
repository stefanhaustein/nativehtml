package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.dom.Component;

public class ElementLayoutHelper {
	
	static int getMinWidth(Component component) {
		return component.getIntrinsicMinimumWidth();		
	}
	
	static int getHeight(Component component, int width) {
		return component.getIntrinsicHeightForWidth(width);
	}

}
