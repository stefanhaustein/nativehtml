package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.dom.Component;
import org.kobjects.nativehtml.dom.Element;

public class BlockLayout implements Layout {

	@Override
	public void layout(Element parent, int xOfs, int yOfs, int contentWidth, boolean measureOnly, int[] result) {
		
		int y = yOfs;
		
		for (int i = 0; i < parent.getChildren().getLength(); i++) {
			Component child = (Component) parent.getChildren().item(i);

			int h = ElementLayoutHelper.getHeight(child, contentWidth);
			
			if (!measureOnly) {
				child.setBorderBoxBounds(xOfs, y, contentWidth, h);
			}
			
			y += h;
			
		}
		
		if (result != null) {
			result[0] = contentWidth;
			result[1] = y - yOfs;
		}
	}


}
