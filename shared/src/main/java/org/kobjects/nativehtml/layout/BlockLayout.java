package org.kobjects.nativehtml.layout;


import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class BlockLayout implements Layout {

	@Override 
	public float measureWidth(ComponentElement parent, Directive directive, float parentContentBoxWidth) {
	  HtmlCollection children = parent.getChildren();
      float width = 0;
      float lineWidth = 0;
      
      for (int i = 0; i < children.getLength(); i++) {
        ComponentElement child = (ComponentElement) parent.getChildren().item(i);
        CssStyleDeclaration childStyle = child.getComputedStyle();

        CssEnum display = childStyle.getEnum(CssProperty.DISPLAY);
        
        if (ElementLayoutHelper.needsSpecialHandling(child)) {
          continue;
        }

        float childMarginLeft = childStyle.getPx(CssProperty.MARGIN_LEFT, parentContentBoxWidth);
        float childMarginRight = childStyle.getPx(CssProperty.MARGIN_RIGHT, parentContentBoxWidth);
            
        if (display == CssEnum.BLOCK || display == CssEnum.TABLE || display == CssEnum.LIST_ITEM 
            || directive == Directive.MINIMUM)  {
          if (lineWidth > 0) {
            width = Math.max(lineWidth, width);
          }
          
          float childMinWidth = childMarginLeft 
              + ElementLayoutHelper.getBorderBoxWidth(child, directive, parentContentBoxWidth)
              + childMarginRight;
               
          width = Math.max(width, childMinWidth);
        } else {
          // MIN is handled above...
          lineWidth += childMarginLeft 
              + ElementLayoutHelper.getBorderBoxWidth(child, Directive.FIT_CONTENT, parentContentBoxWidth)
              + childMarginRight;
        }
      }
      return Math.max(lineWidth, width);
	}
	
	@Override
	public float layout(ComponentElement parent, float xOfs, float yOfs, float containingBoxWidth, boolean measureOnly) {
	  HtmlCollection children = parent.getChildren();
	  float x = 0;
	  float y = 0;
	  float pendingMargin = 0;
	  float lineHeight = 0;
	  StringBuilder indicesAndHeights = measureOnly ? null : new StringBuilder();
		
	  for (int i = 0; i < parent.getChildren().getLength(); i++) {
	    ComponentElement child = (ComponentElement) children.item(i);
	    CssStyleDeclaration childStyle = child.getComputedStyle();
		    
	    CssEnum display = childStyle.getEnum(CssProperty.DISPLAY);
	    
	    if (ElementLayoutHelper.specialHandling(parent, xOfs, yOfs, containingBoxWidth, measureOnly, child)) {
	      continue;
	    }

	    float childMarginLeft = childStyle.getPx(CssProperty.MARGIN_LEFT, containingBoxWidth);
	    float childMarginRight = childStyle.getPx(CssProperty.MARGIN_RIGHT, containingBoxWidth);
		    
	    if (display == CssEnum.BLOCK || display == CssEnum.TABLE || display == CssEnum.LIST_ITEM) {
	      if (x > 0) {
	        adjustLastLine(parent, indicesAndHeights, x - xOfs, containingBoxWidth, lineHeight);
	        y += lineHeight;
	        x = 0;
	        lineHeight = 0;
	      }
	      y += Math.max(pendingMargin, childStyle.getPx(CssProperty.MARGIN_TOP, containingBoxWidth));
	      
	      float childContentBoxWidth = ElementLayoutHelper.getContentBoxWidth(child, Layout.Directive.STRETCH, containingBoxWidth);
	      float childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, Layout.Directive.STRETCH, containingBoxWidth);
	      float childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childContentBoxWidth, containingBoxWidth);
			
	      if (!measureOnly) {
	        child.setBorderBoxBounds(xOfs + childMarginLeft, y + xOfs, 
	            childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
	      }
	      
	      y += childBorderBoxHeight;
	      pendingMargin = childStyle.getPx(CssProperty.MARGIN_BOTTOM, containingBoxWidth); 
	    } else {
	      if (x == 0) {
	        y += pendingMargin;
	        pendingMargin = 0;
	      }
	      float childContentBoxWidth = ElementLayoutHelper.getContentBoxWidth(child, Layout.Directive.FIT_CONTENT, containingBoxWidth);
	      float childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, Layout.Directive.FIT_CONTENT, containingBoxWidth);
	      float childMarginBoxWidth = childMarginLeft + childBorderBoxWidth + childMarginRight;
	      float childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childContentBoxWidth, containingBoxWidth);
	      if (x > 0 && x + childMarginBoxWidth > containingBoxWidth) {
	        adjustLastLine(parent, indicesAndHeights, x, containingBoxWidth, lineHeight);
	        y += lineHeight;
	        x = 0;
	      }
	      if (!measureOnly) {
	        child.setBorderBoxBounds(x + xOfs + childMarginLeft, y + yOfs, childBorderBoxWidth, childBorderBoxHeight, containingBoxWidth);
	        indicesAndHeights.append((char) i);
	        indicesAndHeights.append((char) childBorderBoxHeight);
	      }
	      lineHeight = Math.max(lineHeight, childBorderBoxHeight);
	      x += childMarginBoxWidth;	
	    }
		    
	    // Common child processing code
		    
	    if (childStyle.getEnum(CssProperty.POSITION) == CssEnum.RELATIVE) {
	      float dx = childStyle.getPx(CssProperty.LEFT, containingBoxWidth)
	          - childStyle.getPx(CssProperty.RIGHT, containingBoxWidth);
	      float dy = childStyle.getPx(CssProperty.TOP, containingBoxWidth)
	          - childStyle.getPx(CssProperty.BOTTOM, containingBoxWidth);
		        
	      if (dx != 0 || dy != 0) {
	        child.moveRelative(dx, dy);
	      }
	    }   
	  }
		
	  if (x > 0) {
	    adjustLastLine(parent, indicesAndHeights, x, containingBoxWidth, lineHeight);
	    y += lineHeight;
	  }
	  return y + pendingMargin;
	}
	

    private static void adjustLastLine(Element parent, StringBuilder indicesAndHeights, float usedSpace, float availableSpace, float lineHeight) {
        if (indicesAndHeights == null) {
            return;
        }
        CssEnum align = parent.getComputedStyle().getEnum(CssProperty.TEXT_ALIGN);
        CssEnum vAlign = parent.getComputedStyle().getEnum(CssProperty.VERTICAL_ALIGN);
        float addXOffset = 0;
        if (align == CssEnum.RIGHT) {
          addXOffset = availableSpace - usedSpace;
        } else if (align == CssEnum.CENTER) {
          addXOffset = (availableSpace - usedSpace) / 2;
        }
        for (int i = 0; i < indicesAndHeights.length(); i += 2) {
            int index = indicesAndHeights.charAt(i);
            float h = indicesAndHeights.charAt(i + 1);
            float addYOffset;
            switch(vAlign) {
            case TOP:
                addYOffset = 0;
                break;
            case BOTTOM:
                addYOffset = lineHeight - h;
                break;
            default:
                addYOffset = (lineHeight - h) / 2;
            }
            ((ComponentElement) parent.getChildren().item(index)).moveRelative(addXOffset, addYOffset);
        }
        indicesAndHeights.setLength(0);
    }

}
