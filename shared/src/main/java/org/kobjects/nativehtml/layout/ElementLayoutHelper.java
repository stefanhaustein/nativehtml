package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.html.HtmlComponent;
import org.kobjects.nativehtml.layout.Layout.Directive;

public class ElementLayoutHelper {
	
	static int getContentBoxMinWidth(HtmlComponent component, int parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		// TODO: Take box model into account.
		return style.isSet(CssProperty.WIDTH) && style.isLenghtFixed(CssProperty.WIDTH) 
				? style.getPx(CssProperty.WIDTH, 0) 
				: component.getIntrinsicContentBoxWidth(Directive.MINIMUM, parentContentBoxWidth); 
	}
	
	static int getBorderBoxMinWidth(HtmlComponent component, int parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, 0) 
				+ style.getPx(CssProperty.PADDING_LEFT, 0)
				+ getContentBoxMinWidth(component, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_RIGHT, 0)
				+ style.getPx(CssProperty.BORDER_LEFT_WIDTH, 0);
	}

	static int getContentBoxWidth(HtmlComponent element, int parentContentBoxWidth) {
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
		
		if (style.getEnum(CssProperty.DISPLAY) == CssEnum.BLOCK) {
			return available;
		}
		return Math.min(available, element.getIntrinsicContentBoxWidth(Directive.FIT_CONTENT, available));
	}
	
	static int getBorderBoxWidth(HtmlComponent element, int parentContentBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_LEFT, parentContentBoxWidth)
				+ getContentBoxWidth(element, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_RIGHT, parentContentBoxWidth)
				+ style.getPx(CssProperty.BORDER_RIGHT_WIDTH, parentContentBoxWidth);
	}


	static int getContentBoxHeight(HtmlComponent component, int contentBoxWidth, int parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		if (style.isSet(CssProperty.HEIGHT)) {
			return style.getPx(CssProperty.WIDTH, parentContentBoxWidth);
		}
		return component.getIntrinsicContentBoxHeightForWidth(contentBoxWidth, parentContentBoxWidth);
	}

	static int getBorderBoxHeight(HtmlComponent component, int contentBoxWidth, int parentContentBoxWidth) {
		CssStyleDeclaration style = component.getComputedStyle();
		return style.getPx(CssProperty.BORDER_TOP_WIDTH, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_TOP, parentContentBoxWidth)
				+ getContentBoxHeight(component, contentBoxWidth, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_BOTTOM, parentContentBoxWidth)
				+ style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, parentContentBoxWidth);
	}


}
