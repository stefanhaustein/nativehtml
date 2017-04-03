package org.kobjects.nativehtml.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.layout.ComponentElement;
import org.kobjects.nativehtml.layout.ElementLayoutHelper;
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
    return minimumLayoutSize(container);
  }

  @Override
  public Dimension minimumLayoutSize(Container container) {
    ComponentElement parent = (ComponentElement) container;
    CssStyleDeclaration style = parent.getComputedStyle();

    float [] borderBoxSize = ElementLayoutHelper.getBorderBoxSize(parent, Layout.Directive.FIT_CONTENT, 300);
    float scale = parent.getOwnerDocument().getSettings().getScale();
    return new Dimension(Math.round(scale * borderBoxSize[0]),
          Math.round(scale * borderBoxSize[1]));
  }

  @Override
  public void layoutContainer(Container container) {
    ComponentElement parent = (ComponentElement) container;
    CssStyleDeclaration style = ((ComponentElement) parent).getComputedStyle();

    float scale = parent.getOwnerDocument().getSettings().getScale();
    float containingBoxWidth = container.getWidth() / scale;
		
    float left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
    float top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
    float right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
        style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
    
    layout.layout(parent, left, top, container.getWidth() - left - right, false);
  }
}
