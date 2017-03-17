package org.kobjects.nativehtml.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class ComponentWrapper<T extends JComponent> extends AbstractHtmlComponent {

	protected T component;
	
	ComponentWrapper(Document document, String name, T component) {
		super(document, name);
		setLayout(new BorderLayout());
		this.component = component;
		add(component, BorderLayout.CENTER);
	}
	
	
	@Override
	public int getIntrinsicContentBoxWidth(boolean minimal) {
		return component.getPreferredSize().width;
	}

	@Override
	public int getIntrinsicContentBoxHeightForWidth(int contentBoxWidth) {
		return component.getPreferredSize().height;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.LEAF_COMPONENT;
	}

	@Override
	public HtmlCollection getChildren() {
		return HtmlCollection.EMPTY;
	}

}
