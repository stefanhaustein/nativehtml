package org.kobjects.nativehtml.swing;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumSet;
import java.util.jar.Attributes.Name;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.BlockLayout;
import org.kobjects.nativehtml.layout.ComponentElement;
import org.kobjects.nativehtml.layout.Layout;
import org.kobjects.nativehtml.layout.TableLayout;
import org.kobjects.nativehtml.util.Strings;

public class SwingComponentContainerElement extends AbstractSwingComponentElement implements HtmlCollection {
	private static final EnumSet<ElementType> CONTENT_TYPE = EnumSet.of(ElementType.COMPONENT);
	
	Layout layout;
	
	public SwingComponentContainerElement(Document document, String elementName) {
		super(document, elementName);
		layout =  elementName.equals("table") ? new TableLayout() : elementName.equals("tr") ? null : new BlockLayout();
		
		if (layout != null) {
		  setLayout(new SwingLayoutAdapter(layout));
		}
	}

	@Override
	public ElementType getElementType() {
		return ElementType.COMPONENT;
	}

	@Override
	public HtmlCollection getChildren() {
		return this;
	}

	@Override
	public int getLength() {
		return getComponentCount();
	}

	@Override
	public Element item(int index) {
		return (Element) getComponent(index);
	}


	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		if (!(newChild instanceof ComponentElement)) {
			System.out.println("Ignoring child " + newChild + " for " + this);
		} else {
			add((Component) newChild);
		}
	}
	
	@Override
	public void setBorderBoxBounds(float x, float y, float width, float height, float containingBoxWidth) {
		super.setBorderBoxBounds(x, y, width, height, containingBoxWidth);
		
		if (layout == null) {
		  return;
		}
		
		CssStyleDeclaration style = getComputedStyle();

		float bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
		float left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
		float top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
		float right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
		
		layout.layout(this, left, top, width - left - right, false);
	}

	
	@Override
	public float getIntrinsicContentBoxWidth(Layout.Directive directive, float contentBoxWidth) {
	  return layout.measureWidth(this, directive, contentBoxWidth);
	}

	@Override
	public void paint(Graphics g) {
	  paintComponent(g);
	  int listIndex = 1;
	  CssEnum listStyleType = getComputedStyle().getEnum(CssProperty.LIST_STYLE_TYPE);
	  int lastAbsoluteChild = -1;
	  float scale = getOwnerDocument().getSettings().getScale();    
	  
	  for (int i = 0; i < getComponentCount(); i++) {
	    Component component = getComponent(i);
	    Element element = (Element) component;
        CssStyleDeclaration childStyle = element.getComputedStyle();
        
        if (childStyle.getEnum(CssProperty.DISPLAY) == CssEnum.NONE) {
          continue;
        }
        if (childStyle.getEnum(CssProperty.POSITION) == CssEnum.ABSOLUTE) {
          lastAbsoluteChild = i;
        }
        
	    if (childStyle.getEnum(CssProperty.DISPLAY) == CssEnum.LIST_ITEM 
	        && listStyleType != CssEnum.NONE) {
	      String bullet = Strings.getBullet(listStyleType, listIndex++);

	      // TODO: Add util to translate colors, fonts etc. -- and use them properly!
	      g.setFont(new Font(element.getComputedStyle().getString(CssProperty.FONT_FAMILY), 0, 
	          Math.round(scale * element.getComputedStyle().getPx(CssProperty.FONT_SIZE, 0))));
	        
	      FontMetrics fontMetrics = g.getFontMetrics();
	      int dx = -fontMetrics.stringWidth(bullet);
	      int dy = fontMetrics.getAscent() 
	          + fontMetrics.getLeading() 
	          + Math.round(scale 
	              * (childStyle.getPx(CssProperty.PADDING_TOP, 0) 
	                  + childStyle.getPx(CssProperty.BORDER_TOP_WIDTH, 0)));
	      g.setColor(Color.BLACK);
	      g.drawString(bullet, component.getX() + dx, component.getY() + dy);
	    }
	      
        g.translate(component.getX(), component.getY());
        component.paint(g);
        g.translate(-component.getX(), -component.getY());
	  }
	  for (int i = 0; i <= lastAbsoluteChild; i++) {
        Component component = getComponent(i);
        Element element = (Element) component;
        CssStyleDeclaration childStyle = element.getComputedStyle();
        if (childStyle.getEnum(CssProperty.DISPLAY) == CssEnum.NONE || 
            childStyle.getEnum(CssProperty.POSITION) != CssEnum.ABSOLUTE) {
          continue;
        }
        g.translate(component.getX(), component.getY());
        component.paint(g);
        g.translate(-component.getX(), -component.getY());
	  }
	}
	
	@Override
	public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
		return layout.layout(this, 0, 0, contentBoxWidth, true /* measureOnly */);
	}

	@Override
	public ContentType getElementContentType() {
		return ContentType.COMPONENTS;
	}

}
