package org.kobjects.nativehtml.layout;


import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlComponent;

public class BlockLayout implements Layout {

	private static void adjustLastLine(Element parent, StringBuilder indices, int usedSpace, int availableSpace, int lineHeight, StringBuilder heights) {
		if (indices == null) {
			return;
		}
		CssEnum align = parent.getComputedStyle().getEnum(CssProperty.TEXT_ALIGN);
		CssEnum vAlign = parent.getComputedStyle().getEnum(CssProperty.VERTICAL_ALIGN);
	    int addXOffset = 0;
	    if (align == CssEnum.RIGHT) {
	      addXOffset = availableSpace - usedSpace;
	    } else if (align == CssEnum.CENTER) {
	      addXOffset = (availableSpace - usedSpace) / 2;
	    }
    	for (int i = 0; i < indices.length(); i++) {
    		int index = indices.charAt(i);
    		int h = heights.charAt(i);
    		int addYOffset;
    		switch(vAlign) {
    		case TOP:
    			addYOffset = 0;
    			break;
    		case BOTTOM:
    			addYOffset = lineHeight - h;
    		default:
    			addYOffset = (lineHeight - h) / 2;
    		}
    		((HtmlComponent) parent.getChildren().item(index)).moveRelative(addXOffset, addYOffset);
    	}
    	heights.setLength(0);
    	indices.setLength(0);
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
		StringBuilder heights = measureOnly ? null : new StringBuilder();
		StringBuilder indices = measureOnly ? null : new StringBuilder();
		
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
	    			adjustLastLine(parent, indices, x - xOfs, containingBoxWidth, lineHeight, heights);
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
	    			adjustLastLine(parent, indices, x, containingBoxWidth, lineHeight, heights);
		    		y += lineHeight;
		    		x = 0;
		    	}
		    	if (!measureOnly) {
		    		child.setBorderBoxBounds(x + xOfs + childMarginLeft, y + yOfs, childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
		    		heights.append((char) childBorderBoxHeight);
		    		indices.append((char) i);
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
  			adjustLastLine(parent, indices, x, containingBoxWidth, lineHeight, heights);
		    y += lineHeight;
		}
		y += pendingMargin;
		
		if (result != null) {
			result[0] = containingBoxWidth;
			result[1] = y;
		}
	}


}
