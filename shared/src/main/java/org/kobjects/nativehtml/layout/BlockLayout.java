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
		int lineHeight = 0;
		int x = xOfs;
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
		    	if (x > xOfs) {
		    		adjustLastLine(parent, firstChildIndex, i, x - xOfs, containingBoxWidth);
		    		y += lineHeight;
		    		x = xOfs;
		    		lineHeight = 0;
		    	}
		    	y += Math.max(pendingMargin, childStyle.getPx(CssProperty.MARGIN_TOP, containingBoxWidth));

		    	int childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, containingBoxWidth);
		    	int childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childBorderBoxWidth, containingBoxWidth);
			
		    	if (!measureOnly) {
		    		child.setBorderBoxBounds(xOfs + childMarginLeft, y, 
		    				childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
		    	}
			
		    	y += childBorderBoxHeight;
		    	pendingMargin = childStyle.getPx(CssProperty.MARGIN_BOTTOM, containingBoxWidth);
		    } else {
		    	if (x == xOfs) {
		    		firstChildIndex = i;
		    		y += pendingMargin;
		    		pendingMargin = 0;
		    	}
		    	int childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, containingBoxWidth);
		    	int childMarginBoxWidth = childMarginLeft + childBorderBoxWidth + childMarginRight;
		    	int childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childBorderBoxWidth, containingBoxWidth);
		    	if (x > xOfs && x + childMarginBoxWidth > xOfs + containingBoxWidth) {
		    		adjustLastLine(parent, firstChildIndex, i, x - xOfs, containingBoxWidth);
		    		y += lineHeight;
		    		x = xOfs;
		    	}
		    	if (!measureOnly) {
		    		child.setBorderBoxBounds(x + childMarginLeft, y, childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
		    	}
		    	lineHeight = Math.max(lineHeight, childBorderBoxHeight);
		    	x += childBorderBoxHeight;	
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
		
		if (x > xOfs) {
    		adjustLastLine(parent, firstChildIndex, parent.getChildren().getLength(), x - xOfs, containingBoxWidth);
		    y += lineHeight;
		}
		y += pendingMargin;
		
		if (result != null) {
			result[0] = containingBoxWidth;
			result[1] = y - yOfs;
		}
	}


}
