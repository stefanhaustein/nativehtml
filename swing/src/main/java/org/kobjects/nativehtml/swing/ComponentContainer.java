package org.kobjects.nativehtml.swing;


import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HTMLCollection;
import org.kobjects.nativehtml.layout.BlockLayout;
import org.kobjects.nativehtml.layout.Layout;

public class ComponentContainer extends AbstractHtmlComponent implements HTMLCollection {

	Layout layout = new BlockLayout();
	
	public ComponentContainer(String elementName) {
		super(elementName);
		setLayout(new LayoutAdapter(layout));
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

	@Override
	public int getIntrinsicMinimumWidth() {
		return getMinimumSize().width;
	}

	@Override
	public int getIntrinsicHeightForWidth(int width) {
		int[] result = new int[2];
		layout.layout(this, 0, 0, width, true, result);
		return result[1];
	}

}
