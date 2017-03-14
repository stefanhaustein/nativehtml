package org.kobjects.nativehtml.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.html.HtmlComponent;
import org.kobjects.nativehtml.layout.Layout;

public class LayoutAdapter implements LayoutManager {

	Layout layout;
	
	LayoutAdapter(Layout layout) {
		this.layout = layout;
	}
	
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int containingBoxWidth = 320;
		CssStyleDeclaration style = ((HtmlComponent) parent).getComputedStyle();
		
		int bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
		int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
		int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
		int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
		
		int[] result = new int[2];
		layout.layout((Element) parent, left, top, containingBoxWidth - left - right, true, result);
		return new Dimension(left + result[0] + right, top + result[1] + bottom);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int containingBoxWidth = 0;
		CssStyleDeclaration style = ((HtmlComponent) parent).getComputedStyle();

		int bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
		int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
		int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
		int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);

		int[] result = new int[2];
		layout.layout((Element) parent, left, top, -1, true, result);
		return new Dimension(left + result[0] + right, top + result[1] + bottom);
	}

	@Override
	public void layoutContainer(Container parent) {
		CssStyleDeclaration style = ((HtmlComponent) parent).getComputedStyle();

		int containingBoxWidth = parent.getWidth();
		
		int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
		int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
		int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);

		layout.layout((Element) parent, left, top, parent.getWidth() - left - right, false, null);
	}

}
