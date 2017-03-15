package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.html.HtmlComponent;

public class ElementLayoutHelper {
	
	static int getBorderBoxMinWidth(HtmlComponent component) {
		CssStyleDeclaration style = component.getComputedStyle();
		// TODO: Take box model into account.
		if (style.isSet(CssProperty.WIDTH) && style.isLenghtFixed(CssProperty.WIDTH)) {
			return style.getPx(CssProperty.WIDTH, 0);
		}
		return component.getIntrinsicBorderBoxWidth(true); 
	}

	static int getBorderBoxWidth(HtmlComponent element, int containingBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		// TODO: Take box model into account.
		if (style.isSet(CssProperty.WIDTH)) {
			return style.getPx(CssProperty.WIDTH, containingBoxWidth);
		}
		
		int fullWidth = containingBoxWidth 
				- style.getPx(CssProperty.MARGIN_LEFT, containingBoxWidth)
				- style.getPx(CssProperty.MARGIN_RIGHT, containingBoxWidth);
		
		return style.getEnum(CssProperty.DISPLAY) == CssEnum.BLOCK ? fullWidth 
				: Math.min(element.getIntrinsicBorderBoxWidth(false), fullWidth); 
	}

	
	static int getBorderBoxHeight(HtmlComponent component, int borderBoxWidth, int containingBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		if (style.isSet(CssProperty.HEIGHT)) {
			return style.getPx(CssProperty.WIDTH, containingBoxWidth);
		}
		return component.getIntrinsicBorderBoxHeightForWidth(borderBoxWidth);
	}


}
