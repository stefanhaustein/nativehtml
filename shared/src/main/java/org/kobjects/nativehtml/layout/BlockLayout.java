package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlComponent;
import org.kobjects.nativehtml.util.DebugDump;

public class BlockLayout implements Layout {

	private static void adjustLastLine(Element parent, int firstChildIndex, int to, int usedSpace, int availableSpace) {
		CssEnum align = parent.getComputedStyle().getEnum(CssProperty.TEXT_ALIGN);
	    int addOffset = 0;
	    if (align == CssEnum.RIGHT) {
	      addOffset = availableSpace - usedSpace;
	    } else if (align == CssEnum.CENTER) {
	      addOffset = (availableSpace - usedSpace) / 2;
	    }
	    if (addOffset != 0) {
	    	for (int i = firstChildIndex; i < to; i++) {
	    		((HtmlComponent) parent.getChildren().item(i)).moveRelative(addOffset, 0);
	    	}
	    }
	}
	
	@Override
	public void layout(Element parent, int xOfs, int yOfs, int containingBoxWidth, boolean measureOnly, int[] result) {
		HtmlCollection children = parent.getChildren();
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
		
		int x = 0;
		int y = 0;
		int pendingMargin = 0;
		int lineHeight = 0;
		int firstChildIndex = 0;
		
		for (int i = 0; i < parent.getChildren().getLength(); i++) {
			HtmlComponent child = (HtmlComponent) parent.getChildren().item(i);
		    CssStyleDeclaration childStyle = child.getComputedStyle();
		    
		    CssEnum display = childStyle.getEnum(CssProperty.DISPLAY);
		    if (display == CssEnum.NONE || childStyle.getEnum(CssProperty.POSITION) == CssEnum.ABSOLUTE) {
		    	continue;
		    }

	    	int childMarginLeft = childStyle.getPx(CssProperty.MARGIN_LEFT, marginBase);
	    	int childMarginRight = childStyle.getPx(CssProperty.MARGIN_RIGHT, marginBase);
		    
		    if (display == CssEnum.BLOCK) {
		    	if (x > 0) {
		    		adjustLastLine(parent, firstChildIndex, i, x - xOfs, containingBoxWidth);
		    		y += lineHeight;
		    		x = 0;
		    		lineHeight = 0;
		    	}
		    	y += Math.max(pendingMargin, childStyle.getPx(CssProperty.MARGIN_TOP, containingBoxWidth));

		    	int childContentBoxWidth = ElementLayoutHelper.getContentBoxWidth(child, containingBoxWidth);
		    	int childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, containingBoxWidth);
		    	int childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childContentBoxWidth, containingBoxWidth);
			
		    	if (!measureOnly) {
		    		child.setBorderBoxBounds(xOfs + childMarginLeft, y + xOfs, 
		    				childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
		    	}
			
		    	y += childBorderBoxHeight;
		    	pendingMargin = childStyle.getPx(CssProperty.MARGIN_BOTTOM, containingBoxWidth);
		    } else {
		    	if (x == 0) {
		    		firstChildIndex = i;
		    		y += pendingMargin;
		    		pendingMargin = 0;
		    	}
		    	int childContentBoxWidth = ElementLayoutHelper.getContentBoxWidth(child, containingBoxWidth);
		    	int childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, containingBoxWidth);
		    	int childMarginBoxWidth = childMarginLeft + childBorderBoxWidth + childMarginRight;
		    	int childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childContentBoxWidth, containingBoxWidth);
		    	if (x > 0 && x + childMarginBoxWidth > containingBoxWidth) {
		    		adjustLastLine(parent, firstChildIndex, i, x, containingBoxWidth);
		    		y += lineHeight;
		    		x = 0;
		    	}
		    	if (!measureOnly) {
		    		child.setBorderBoxBounds(x + xOfs + childMarginLeft, y + yOfs, childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
		    	}
		    	lineHeight = Math.max(lineHeight, childBorderBoxHeight);
		    	x += childMarginBoxWidth;	
		    }
		    
		    // Common child processing code
		    
		    if (childStyle.getEnum(CssProperty.POSITION) == CssEnum.RELATIVE) {
		        int dx = childStyle.getPx(CssProperty.LEFT, containingBoxWidth)
		            - childStyle.getPx(CssProperty.RIGHT, containingBoxWidth);
		        int dy = childStyle.getPx(CssProperty.TOP, containingBoxWidth)
		            - childStyle.getPx(CssProperty.BOTTOM, containingBoxWidth);
		        
		        if (dx != 0 || dy != 0) {
		        	child.moveRelative(dx, dy);
		        }
		    }
		    
		}
		
		if (x > 0) {
    		adjustLastLine(parent, firstChildIndex, parent.getChildren().getLength(), x, containingBoxWidth);
		    y += lineHeight;
		}
		y += pendingMargin;
		
		if (result != null) {
			result[0] = containingBoxWidth;
			result[1] = y;
		}
	}


}
