package org.kobjects.nativehtml.swing;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumSet;
import java.util.jar.Attributes.Name;

import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.HtmlContentType;
import org.kobjects.nativehtml.dom.HtmlDocument;
import org.kobjects.nativehtml.dom.HtmlElement;
import org.kobjects.nativehtml.dom.HtmlElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlComponentElement;
import org.kobjects.nativehtml.layout.BlockLayout;
import org.kobjects.nativehtml.layout.Layout;
import org.kobjects.nativehtml.layout.TableLayout;

public class SwingComponentContainer extends AbstractSwingHtmlComponentElement implements HtmlCollection {
	private static final EnumSet<HtmlElementType> CONTENT_TYPE = EnumSet.of(HtmlElementType.COMPONENT);
	
	Layout layout;
	
	public SwingComponentContainer(HtmlDocument document, String elementName) {
		super(document, elementName);
		layout =  elementName.equals("table") ? new TableLayout() : elementName.equals("tr") ? null : new BlockLayout();
		
		if (layout != null) {
		  setLayout(new SwingLayoutAdapter(layout));
		}
	}

	@Override
	public HtmlElementType getElementType() {
		return HtmlElementType.COMPONENT;
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
	public HtmlElement item(int index) {
		return (HtmlElement) getComponent(index);
	}


	@Override
	public void insertBefore(HtmlElement newChild, HtmlElement referenceChild) {
		if (!(newChild instanceof HtmlComponentElement)) {
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
	public HtmlContentType getElemnetContentType() {
		return HtmlContentType.COMPONENTS;
	}

}
