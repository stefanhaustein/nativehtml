package org.kobjects.nativehtml.swing;

import java.awt.FlowLayout;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HTMLCollection;
import org.kobjects.nativehtml.layout.BlockLayout;

public class ComponentContainer extends AbstractHtmlComponent implements HTMLCollection {

	public ComponentContainer(String elementName) {
		super(elementName);
		setLayout(new LayoutAdapter(new BlockLayout()));
	}

	@Override
	public ElementType getElementType() {
		return ElementType.COMPONENT_CONTAINER;
	}

	@Override
	public HTMLCollection getChildren() {
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

}
