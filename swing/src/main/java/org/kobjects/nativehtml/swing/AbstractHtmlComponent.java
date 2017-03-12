package org.kobjects.nativehtml.swing;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.CSSStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;

public abstract class AbstractHtmlComponent extends JComponent implements org.kobjects.nativehtml.dom.Component {
	private final String name;
	private HashMap<String, String> attributes;
	
	protected AbstractHtmlComponent(String name) {
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
	public CSSStyleDeclaration getStyle() {
		return null;
	}

	@Override
	public CSSStyleDeclaration getComputedStyle() {
		return null;
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
	

	public void setBorderBoxBounds(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
		
	}
}
