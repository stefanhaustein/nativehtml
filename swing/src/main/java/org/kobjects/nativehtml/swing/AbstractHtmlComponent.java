package org.kobjects.nativehtml.swing;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JComponent;

import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;

public abstract class AbstractHtmlComponent extends JComponent implements org.kobjects.nativehtml.html.HtmlComponent {
	private final Document document;
	private final String name;
	private HashMap<String, String> attributes;
	private CssStyleDeclaration style;
	private CssStyleDeclaration computedStyle;
	
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
		add((Component) newChild);
	}
	

	public void setBorderBoxBoundsDp(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
		
	}
}
