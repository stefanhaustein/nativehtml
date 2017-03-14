package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlComponent;

public class BlockLayout implements Layout {

	@Override
	public void layout(Element parent, int xOfs, int yOfs, int containingBoxWidth, boolean measureOnly, int[] result) {
		HtmlCollection children = parent.getChildren();
		int y = yOfs;
		int marginBase = containingBoxWidth;
		
		if (containingBoxWidth == -1) {
			marginBase = containingBoxWidth = 0;
			for (int i = 0; i < children.getLength(); i++) {
				HtmlComponent child = (HtmlComponent) parent.getChildren().item(i);
			    CssStyleDeclaration childStyle = child.getComputedStyle();
				int childMinWidth = childStyle.getPx(CssProperty.MARGIN_LEFT, 0) 
						+ ElementLayoutHelper.getBorderBoxMinWidth(child)
						+ childStyle.getPx(CssProperty.MARGIN_RIGHT, 0);
				
				containingBoxWidth = Math.max(containingBoxWidth, childMinWidth);
			}
		}
		
		int pendingMargin = 0;
		
		for (int i = 0; i < parent.getChildren().getLength(); i++) {
			HtmlComponent child = (HtmlComponent) parent.getChildren().item(i);
		    CssStyleDeclaration childStyle = child.getComputedStyle();
			
			y += Math.max(pendingMargin, childStyle.getPx(CssProperty.MARGIN_TOP, containingBoxWidth));

			int marginLeft = childStyle.getPx(CssProperty.MARGIN_LEFT, marginBase);
			int marginRight = childStyle.getPx(CssProperty.MARGIN_RIGHT, marginBase);

			int h = ElementLayoutHelper.getBorderBoxHeight(child, containingBoxWidth - marginLeft - marginRight, containingBoxWidth);
			
			if (!measureOnly) {
				child.setBorderBoxBounds(xOfs + marginLeft, y, 
						containingBoxWidth - marginLeft - marginRight, h, containingBoxWidth);
			}
			
			y += h;
			pendingMargin = childStyle.getPx(CssProperty.MARGIN_BOTTOM, containingBoxWidth);
		}
		
		if (result != null) {
			result[0] = containingBoxWidth;
			result[1] = y - yOfs;
		}
	}


}
