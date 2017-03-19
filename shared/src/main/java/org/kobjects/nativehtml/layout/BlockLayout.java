package org.kobjects.nativehtml.layout;


import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlComponent;

public class BlockLayout implements Layout {

	@Override 
	public int measureWidth(HtmlComponent parent, Directive directive, int parentContentBoxWidth) {
	  HtmlCollection children = parent.getChildren();
      int width = 0;
      int lineWidth = 0;
      
      for (int i = 0; i < children.getLength(); i++) {
        HtmlComponent child = (HtmlComponent) parent.getChildren().item(i);
        CssStyleDeclaration childStyle = child.getComputedStyle();

        CssEnum display = childStyle.getEnum(CssProperty.DISPLAY);
        if (display == CssEnum.NONE || childStyle.getEnum(CssProperty.POSITION) == CssEnum.ABSOLUTE) {
          continue;
        }

        int childMarginLeft = childStyle.getPx(CssProperty.MARGIN_LEFT, parentContentBoxWidth);
        int childMarginRight = childStyle.getPx(CssProperty.MARGIN_RIGHT, parentContentBoxWidth);
            
        if (display == CssEnum.BLOCK || display == CssEnum.TABLE || directive == Directive.MINIMUM)  {
          if (lineWidth > 0) {
            width = Math.max(lineWidth, width);
          }
          
          int childMinWidth = childMarginLeft 
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
	public int layout(HtmlComponent parent, int xOfs, int yOfs, int containingBoxWidth, boolean measureOnly) {
	  HtmlCollection children = parent.getChildren();
	  int x = 0;
	  int y = 0;
	  int pendingMargin = 0;
	  int lineHeight = 0;
	  StringBuilder indicesAndHeights = measureOnly ? null : new StringBuilder();
		
	  for (int i = 0; i < parent.getChildren().getLength(); i++) {
	    HtmlComponent child = (HtmlComponent) children.item(i);
	    CssStyleDeclaration childStyle = child.getComputedStyle();
		    
	    CssEnum display = childStyle.getEnum(CssProperty.DISPLAY);
	    if (display == CssEnum.NONE || childStyle.getEnum(CssProperty.POSITION) == CssEnum.ABSOLUTE) {
	      continue;
	    }

	    int childMarginLeft = childStyle.getPx(CssProperty.MARGIN_LEFT, containingBoxWidth);
	    int childMarginRight = childStyle.getPx(CssProperty.MARGIN_RIGHT, containingBoxWidth);
		    
	    if (display == CssEnum.BLOCK || display == CssEnum.TABLE) {
	      if (x > 0) {
	        adjustLastLine(parent, indicesAndHeights, x - xOfs, containingBoxWidth, lineHeight);
	        y += lineHeight;
	        x = 0;
	        lineHeight = 0;
	      }
	      y += Math.max(pendingMargin, childStyle.getPx(CssProperty.MARGIN_TOP, containingBoxWidth));
	      
	      int childContentBoxWidth = ElementLayoutHelper.getContentBoxWidth(child, Layout.Directive.STRETCH, containingBoxWidth);
	      int childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, Layout.Directive.STRETCH, containingBoxWidth);
	      int childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childContentBoxWidth, containingBoxWidth);
			
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
	      int childContentBoxWidth = ElementLayoutHelper.getContentBoxWidth(child, Layout.Directive.FIT_CONTENT, containingBoxWidth);
	      int childBorderBoxWidth = ElementLayoutHelper.getBorderBoxWidth(child, Layout.Directive.FIT_CONTENT, containingBoxWidth);
	      int childMarginBoxWidth = childMarginLeft + childBorderBoxWidth + childMarginRight;
	      int childBorderBoxHeight = ElementLayoutHelper.getBorderBoxHeight(child, childContentBoxWidth, containingBoxWidth);
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
	    adjustLastLine(parent, indicesAndHeights, x, containingBoxWidth, lineHeight);
	    y += lineHeight;
	  }
	  return y + pendingMargin;
	}
	

    private static void adjustLastLine(Element parent, StringBuilder indicesAndHeights, int usedSpace, int availableSpace, int lineHeight) {
        if (indicesAndHeights == null) {
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
        for (int i = 0; i < indicesAndHeights.length(); i += 2) {
            int index = indicesAndHeights.charAt(i);
            int h = indicesAndHeights.charAt(i + 1);
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
        indicesAndHeights.setLength(0);
    }

}
