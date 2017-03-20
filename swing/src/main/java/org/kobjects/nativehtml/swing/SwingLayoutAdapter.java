package org.kobjects.nativehtml.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.layout.ComponentElement;
import org.kobjects.nativehtml.layout.Layout;

public class SwingLayoutAdapter implements LayoutManager {

  Layout layout;
	  
  SwingLayoutAdapter(Layout layout) {
    this.layout = layout;   
  }
	
  @Override
  public void addLayoutComponent(String name, Component comp) {
  }

  @Override
  public void removeLayoutComponent(Component comp) {
  }

  @Override
  public Dimension preferredLayoutSize(Container container) {
    ComponentElement parent = (ComponentElement) container;
    CssStyleDeclaration style = ((ComponentElement) parent).getComputedStyle();
    int containingBoxWidth = 300;
		
    int bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
    int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
    int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
    int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
		
    int contentHeight = layout.layout(parent, left, top, containingBoxWidth - left - right, true);
    return new Dimension(left + containingBoxWidth + right, top + contentHeight + bottom);
  }

  @Override
  public Dimension minimumLayoutSize(Container container) {
    ComponentElement parent = (ComponentElement) container;
    CssStyleDeclaration style = parent.getComputedStyle();
    int containingBoxWidth = 300;

    int bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
    int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);    
    int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
    int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);

    int width = layout.measureWidth(parent, Layout.Directive.MINIMUM, containingBoxWidth);
      int contentHeight = layout.layout(parent, left, top, width, true);
      return new Dimension(left + width + right, top + contentHeight + bottom); 
  }

  @Override
  public void layoutContainer(Container container) {
    ComponentElement parent = (ComponentElement) container;
    CssStyleDeclaration style = ((ComponentElement) parent).getComputedStyle();

    int containingBoxWidth = container.getWidth();
		
    int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
    int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
    int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
    
    layout.layout(parent, left, top, container.getWidth() - left - right, false);
  }
}
