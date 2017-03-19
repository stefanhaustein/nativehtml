package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.html.HtmlComponent;
import org.kobjects.nativehtml.layout.Layout.Directive;

public class ElementLayoutHelper {
	

	static int getContentBoxWidth(HtmlComponent element, Layout.Directive directive, int parentContentBoxWidth) {
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
		return Math.min(available, element.getIntrinsicContentBoxWidth(directive, available));
	}
	
	static int getBorderBoxWidth(HtmlComponent element, Layout.Directive directive, int parentContentBoxWidth) {
		CssStyleDeclaration style = element.getComputedStyle();
		return style.getPx(CssProperty.BORDER_LEFT_WIDTH, parentContentBoxWidth)
				+ style.getPx(CssProperty.PADDING_LEFT, parentContentBoxWidth)
				+ getContentBoxWidth(element, directive, parentContentBoxWidth)
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
