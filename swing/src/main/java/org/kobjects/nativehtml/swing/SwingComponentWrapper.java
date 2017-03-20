package org.kobjects.nativehtml.swing;

import java.awt.BorderLayout;
import java.util.EnumSet;

import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.HtmlDocument;
import org.kobjects.nativehtml.dom.HtmlElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.Layout;

public abstract class SwingComponentWrapper<T extends JComponent> extends AbstractSwingHtmlComponentElement {
	protected T component;
	
	SwingComponentWrapper(HtmlDocument document, String name, T component) {
		super(document, name);
		setLayout(new BorderLayout());
		setComponent(component);
	}
	
	
	public void setComponent(T component) {
		if (this.component != null) {
			remove(this.component);
		}
		this.component = component;
		if (component != null) {
			add(component, BorderLayout.CENTER);
		}
	}
	
	
	@Override
	public int getIntrinsicContentBoxWidth(Layout.Directive directive, int parentContentBoxWidth) {
		return (directive == Layout.Directive.MINIMUM ? component.getMinimumSize() : component.getPreferredSize()).width;
	}

	@Override
	public int getIntrinsicContentBoxHeightForWidth(int contentBoxWidth, int parentContentBoxWidth) {
		return component.getPreferredSize().height;
	}

	@Override
	public HtmlElementType getElementType() {
		return HtmlElementType.COMPONENT;
	}

}
