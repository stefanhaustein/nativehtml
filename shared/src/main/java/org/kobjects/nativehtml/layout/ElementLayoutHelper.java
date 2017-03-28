package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.layout.Layout.Directive;

public class ElementLayoutHelper {
  
	static float getContentBoxWidth(ComponentElement element, Layout.Directive directive, float parentContentBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		if (style.isSet(CssProperty.WIDTH)) {
			return style.getPx(CssProperty.WIDTH, parentContentBoxWidth);
		}
		// TODO: Take box model into account.
		float available = parentContentBoxWidth 
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
	
	static float getBorderBoxWidth(ComponentElement element, Layout.Directive directive, float parentContentBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_LEFT, parentContentBoxWidth)
				+ getContentBoxWidth(element, directive, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_RIGHT, parentContentBoxWidth)
				+ style.getPx(CssProperty.BORDER_RIGHT_WIDTH, parentContentBoxWidth);
	}


	static float getContentBoxHeight(ComponentElement component, float contentBoxWidth, float parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		if (style.isSet(CssProperty.HEIGHT)) {
			return style.getPx(CssProperty.WIDTH, parentContentBoxWidth);
		}
		return component.getIntrinsicContentBoxHeightForWidth(contentBoxWidth, parentContentBoxWidth);
	}

	static float getBorderBoxHeight(ComponentElement component, float contentBoxWidth, float parentContentBoxWidth) {
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
	
	
  /** 
   * Returns true if the given child element requires special handling in the layout process, e.g. if it has 
   * display set to none or needs absolute positioning.
   */
  public static boolean specialHandling(ComponentElement parent, float xOfs, float yOfs, float containingBoxWidth,
      boolean measureOnly, ComponentElement child) {
    boolean result = needsSpecialHandling(child);
    if (!result || measureOnly || child.getComputedStyle().getEnum(CssProperty.DISPLAY) == CssEnum.NONE ) {
      return result;
    }
    
    CssStyleDeclaration childStyle = child.getComputedStyle();

    float childLeft = childStyle.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
    
    float childRight = childStyle.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
    
    float childTop = childStyle.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
    
    float childBottom = childStyle.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth)
        + childStyle.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);

    float contentWidth = ElementLayoutHelper.getContentBoxWidth(child, Directive.MINIMUM, containingBoxWidth);
    float contentHeight = ElementLayoutHelper.getContentBoxHeight(child, contentWidth, containingBoxWidth);
    
    float boxWidth = childLeft + contentWidth + childRight;
    float boxHeight = childTop + contentHeight + childBottom;
    
    float measuredX;
    if (childStyle.isSet(CssProperty.LEFT)) {
      measuredX = xOfs + childStyle.getPx(CssProperty.LEFT, containingBoxWidth);
    } else if (childStyle.isSet(CssProperty.RIGHT)) {
      measuredX = xOfs + containingBoxWidth - boxWidth - childStyle.getPx(CssProperty.RIGHT, containingBoxWidth) ;
    } else {
      measuredX = xOfs;
    }

    float measuredY;
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
