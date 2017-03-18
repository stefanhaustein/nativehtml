package org.kobjects.nativehtml.swing;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumSet;
import java.util.jar.Attributes.Name;

import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlComponent;
import org.kobjects.nativehtml.layout.BlockLayout;
import org.kobjects.nativehtml.layout.Layout;
import org.kobjects.nativehtml.layout.TableLayout;

public class ComponentContainer extends AbstractHtmlComponent implements HtmlCollection {
	private static final EnumSet<ElementType> CONTENT_TYPE = EnumSet.of(ElementType.COMPONENT);
	
	Layout layout;
	
	public ComponentContainer(Document document, String elementName) {
		super(document, elementName);
		layout =  elementName.equals("table") ? new TableLayout() : elementName.equals("tr") ? null : new BlockLayout();
		
		if (layout != null) {
		  setLayout(new LayoutAdapter(layout));
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
		if (!(newChild instanceof HtmlComponent)) {
			System.out.println("Ignoring child " + newChild + " for " + this);
		} else {
			add((Component) newChild);
		}
	}
	
	@Override
	public void setBorderBoxBounds(int x, int y, int width, int height, int containingBoxWidth) {
		super.setBorderBoxBounds(x, y, width, height, containingBoxWidth);
		
		if (layout == null) {
		  return;
		}
		
		CssStyleDeclaration style = getComputedStyle();

		int bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
		int left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
		int top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
		int right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) + 
				style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);
		
		layout.layout(this, left, top, width - left - right, false);
	}

	
	@Override
	public int getIntrinsicContentBoxWidth(Layout.Directive directive, int contentBoxWidth) {
	  return layout.measureWidth(this, directive, contentBoxWidth);
	}

	@Override
	public void paint(Graphics g) {
	  if (elementName.equals("table")) {
	    for (int i = 0; i < getComponentCount(); i++) {
	      Component component = getComponent(i);
	      g.translate(component.getX(), component.getY());
	      component.paint(g);
	      g.translate(-component.getX(), -component.getY());
	    }
	  } else {
	    super.paint(g);
	  }
	  
	}
	
	@Override
	public int getIntrinsicContentBoxHeightForWidth(int contentBoxWidth, int parentContentBoxWidth) {
		return layout.layout(this, 0, 0, contentBoxWidth, true /* measureOnly */);
	}

	@Override
	public ContentType getElemnetContentType() {
		return ContentType.COMPONENTS;
	}

}
