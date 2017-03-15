package org.kobjects.nativehtml.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;

import javax.swing.JComponent;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.html.HtmlComponent;

public abstract class AbstractHtmlComponent extends JComponent implements org.kobjects.nativehtml.html.HtmlComponent {
	private final Document document;
	private final String name;
	private HashMap<String, String> attributes;
	private CssStyleDeclaration style;
	protected CssStyleDeclaration computedStyle;
	private int containingBoxWidth;
	
	protected AbstractHtmlComponent(Document document, String name) {
		this.document = document;
		this.name = name;
	}
	
	@Override
	public String getLocalName() {
		return name;
	}

	@Override
	public void setAttribute(String name, String value) {
		if (attributes == null) {
			this.attributes = new HashMap<>();
		}
		attributes.put(name, value);
        if (name.equals("style")) {
        	style = CssStyleDeclaration.fromString(value);
        }
	}

	@Override
	public String getAttribute(String name) {
		return attributes == null ? null : attributes.get(name);
	}

	@Override
	public Element getParentElement() {
		return getParent() instanceof Element ? ((Element) getParent()) : null;
	}

	@Override
	public void setParentElement(Element parent) {
	}


	@Override
	public CssStyleDeclaration getStyle() {
		if (style == null) {
			style = new CssStyleDeclaration();
		}
		return style;
	}

	@Override
	public CssStyleDeclaration getComputedStyle() {
		return computedStyle;
	}

	@Override
	public void setComputedStyle(CssStyleDeclaration computedStyle) {
		this.computedStyle = computedStyle;
	}
	
	@Override
	public String getTextContent() {
		return "";
	}

	@Override
	public void setTextContent(String textContent) {
		throw new RuntimeException("not permitted for " + getElementType());
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
		setBounds(x, y, width, height);	
		this.containingBoxWidth = containingBoxWidth;
	}
	
	@Override
	public void moveRelative(int dx, int dy) {
		setBounds(getX() + dx, getY() + dy, getWidth(), getHeight());
	}
	
	static private Color createColor(int argb) {
		return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int borderLeft = computedStyle.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth);
		int borderRight = computedStyle.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth);
		int borderTop = computedStyle.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth);
		int borderBottom = computedStyle.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth);

		int w = getWidth() ;
		int h = getHeight();

		if (computedStyle.isSet(CssProperty.BACKGROUND_COLOR)) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BACKGROUND_COLOR)));
			g2d.fillRect(0, 0, w, h);
		}
		
		/*
        // Background paint area is specified using 'background-clip' property, and default value of it
	    // is 'border-box'
		    drawBackgroud(canvas, childParams, x0 - borderLeft, y0 - borderTop,
		        x1 + borderRight, y1 + borderBottom);
        */

		
		if (borderTop > 0 && computedStyle.getEnum(CssProperty.BORDER_TOP_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_TOP_COLOR)));
			int dLeft = (borderLeft << 8) / borderTop;
			int dRight = (borderRight << 8) / borderTop;
			for (int i = 0; i < borderTop; i++) {
				g2d.drawLine(
						((i * dLeft) >> 8), i,
						w - ((i * dRight) >> 8), i);
		      	}
		}
		if (borderRight > 0 && computedStyle.getEnum(CssProperty.BORDER_RIGHT_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_RIGHT_COLOR)));
			int dTop = (borderTop << 8) / borderRight;
			int dBottom = (borderBottom << 8) / borderRight;
			for (int i = 0; i < borderRight; i++) {
				g2d.drawLine(
						w - i - 1, ((i * dTop) >> 8),
						w - i - 1, h - ((i * dBottom) >> 8));
		    }
		}
		if (borderBottom > 0 && computedStyle.getEnum(CssProperty.BORDER_BOTTOM_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_BOTTOM_COLOR)));
			int dLeft = (borderLeft << 8) / borderBottom;
		    int dRight = (borderRight << 8) / borderBottom;
		    for (int i = 0; i < borderBottom; i++) {
		       g2d.drawLine(
		            ((i * dLeft) >> 8) + 1, h - i - 1,
		            w - ((i * dRight) >> 8) + 1, h - i - 1);
		    }
		}
		if (borderLeft > 0 && computedStyle.getEnum(CssProperty.BORDER_LEFT_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_LEFT_COLOR)));
		    int dTop = (borderTop << 8) / borderLeft;
		    int dBottom = (borderBottom << 8) / borderLeft;
		    for (int i = 0; i < borderLeft; i++) {
		        g2d.drawLine(
		            i, ((i * dTop) >> 8) + 1,
		            i, h - ((i * dBottom) >> 8) + 1);
	      }
	    }
		
	}

}
