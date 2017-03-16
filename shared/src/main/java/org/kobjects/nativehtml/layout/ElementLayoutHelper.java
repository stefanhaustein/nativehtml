package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.html.HtmlComponent;

public class ElementLayoutHelper {
	
	static int getContentBoxMinWidth(HtmlComponent component) {
		CssStyleDeclaration style = component.getComputedStyle();
		// TODO: Take box model into account.
		return style.isSet(CssProperty.WIDTH) && style.isLenghtFixed(CssProperty.WIDTH) 
				? style.getPx(CssProperty.WIDTH, 0) 
				: component.getIntrinsicContentBoxWidth(true); 
	}
	
	static int getBorderBoxMinWidth(HtmlComponent component) {
		CssStyleDeclaration style = component.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, 0) 
				+ style.getPx(CssProperty.PADDING_LEFT, 0)
				+ getContentBoxMinWidth(component)
				+ style.getPx(CssProperty.PADDING_RIGHT, 0)
				+ style.getPx(CssProperty.BORDER_LEFT_WIDTH, 0);
	}

	static int getContentBoxWidth(HtmlComponent element, int containingBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		if (style.isSet(CssProperty.WIDTH)) {
			return style.getPx(CssProperty.WIDTH, containingBoxWidth);
		}
		// TODO: Take box model into account.
		int available = containingBoxWidth 
				- style.getPx(CssProperty.MARGIN_LEFT, containingBoxWidth)
				- style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth)
				- style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth)
				- style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth)
				- style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth)
				- style.getPx(CssProperty.MARGIN_RIGHT, containingBoxWidth);
		
		if (style.getEnum(CssProperty.DISPLAY) == CssEnum.BLOCK) {
			return available;
		}
		return Math.min(available, element.getIntrinsicContentBoxWidth(false));
	}
	
	static int getBorderBoxWidth(HtmlComponent element, int containingBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth)
				+ style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth)
				+ getContentBoxWidth(element, containingBoxWidth)
				+ style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth)
				+ style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth);
	}


	static int getContentBoxHeight(HtmlComponent component, int contentBoxWidth, int containingBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		if (style.isSet(CssProperty.HEIGHT)) {
			return style.getPx(CssProperty.WIDTH, containingBoxWidth);
		}
		return component.getIntrinsicContentBoxHeightForWidth(contentBoxWidth);
	}

	static int getBorderBoxHeight(HtmlComponent component, int contentBoxWidth, int containingBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		return style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth)
				+ style.getPx(CssProperty.PADDING_TOP, containingBoxWidth)
				+ getContentBoxHeight(component, contentBoxWidth, containingBoxWidth)
				+ style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth)
				+ style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth);
	}


}
