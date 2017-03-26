package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.layout.Layout.Directive;

public class ElementLayoutHelper {
  
	static int getContentBoxWidth(ComponentElement element, Layout.Directive directive, int parentContentBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		if (style.isSet(CssProperty.WIDTH)) {
			return style.getPx(CssProperty.WIDTH, parentContentBoxWidth);
		}
		// TODO: Take box model into account.
		int available = parentContentBoxWidth 
				- style.getPx(CssProperty.MARGIN_LEFT, parentContentBoxWidth)
				- style.getPx(CssProperty.BORDER_LEFT_WIDTH, parentContentBoxWidth)
				- style.getPx(CssProperty.PADDING_LEFT, parentContentBoxWidth)
				- style.getPx(CssProperty.PADDING_RIGHT, parentContentBoxWidth)
				- style.getPx(CssProperty.BORDER_RIGHT_WIDTH, parentContentBoxWidth)
				- style.getPx(CssProperty.MARGIN_RIGHT, parentContentBoxWidth);
		
		if (style.getEnum(CssProperty.DISPLAY) == CssEnum.BLOCK && directive == Directive.STRETCH) {
			return available;
		}
		return Math.min(available, element.getIntrinsicContentBoxWidth(directive, available));
	}
	
	static int getBorderBoxWidth(ComponentElement element, Layout.Directive directive, int parentContentBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_LEFT, parentContentBoxWidth)
				+ getContentBoxWidth(element, directive, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_RIGHT, parentContentBoxWidth)
				+ style.getPx(CssProperty.BORDER_RIGHT_WIDTH, parentContentBoxWidth);
	}


	static int getContentBoxHeight(ComponentElement component, int contentBoxWidth, int parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		if (style.isSet(CssProperty.HEIGHT)) {
			return style.getPx(CssProperty.WIDTH, parentContentBoxWidth);
		}
		return component.getIntrinsicContentBoxHeightForWidth(contentBoxWidth, parentContentBoxWidth);
	}

	static int getBorderBoxHeight(ComponentElement component, int contentBoxWidth, int parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		return style.getPx(CssProperty.BORDER_TOP_WIDTH, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_TOP, parentContentBoxWidth)
				+ getContentBoxHeight(component, contentBoxWidth, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_BOTTOM, parentContentBoxWidth)
				+ style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, parentContentBoxWidth);
	}


	public static boolean needsSpecialHandling(ComponentElement child) {
	    CssStyleDeclaration childStyle = child.getComputedStyle();
	    CssEnum display = childStyle.getEnum(CssProperty.DISPLAY);
	    return display == CssEnum.NONE 
	        || ((display == CssEnum.BLOCK || display == CssEnum.TABLE) 
	            && childStyle.getEnum(CssProperty.POSITION) == CssEnum.ABSOLUTE);
	  }
	
  public static boolean specialHandling(ComponentElement parent, int xOfs, int yOfs, int containingBoxWidth,
      boolean measureOnly, ComponentElement child) {
    boolean result = needsSpecialHandling(child);
    if (!result || measureOnly || child.getComputedStyle().getEnum(CssProperty.DISPLAY) == CssEnum.NONE ) {
      return result;
    }
    
    CssStyleDeclaration childStyle = child.getComputedStyle();

    int childLeft = childStyle.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
    
    int childRight = childStyle.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
    
    int childTop = childStyle.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
    
    int childBottom = childStyle.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);

    int contentWidth = ElementLayoutHelper.getContentBoxWidth(child, Directive.MINIMUM, containingBoxWidth);
    int contentHeight = ElementLayoutHelper.getContentBoxHeight(child, contentWidth, containingBoxWidth);
    
    int boxWidth = childLeft + contentWidth + childRight;
    int boxHeight = childTop + contentHeight + childBottom;
    
    int measuredX;
    if (childStyle.isSet(CssProperty.LEFT)) {
      measuredX = xOfs + childStyle.getPx(CssProperty.LEFT, containingBoxWidth);
    } else if (childStyle.isSet(CssProperty.RIGHT)) {
      measuredX = xOfs + containingBoxWidth - boxWidth - childStyle.getPx(CssProperty.RIGHT, containingBoxWidth) ;
    } else {
      measuredX = xOfs;
    }

    int measuredY;
    if (childStyle.isSet(CssProperty.TOP)) {
      measuredY = yOfs + childStyle.getPx(CssProperty.TOP, containingBoxWidth);
  //  } else if (childParams.computedStyle.isSet(CssProperty.BOTTOM)) {
      // TODO
      // measuredY = container.getMeasuredHeight() - child.getMeasuredHeight() - childBottom - Math.round(htmlContext.scale * (childParams.computedStyle.get(CssProperty.BOTTOM, CssUnit.PX, container.cssContentWidth)));
    } else {
      measuredY = childTop;
    }

    child.setBorderBoxBounds(measuredX, measuredY, boxWidth, boxHeight, containingBoxWidth);
    return true;
  }



}
