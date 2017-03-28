package org.kobjects.nativehtml.swing;

import java.awt.BorderLayout;
import java.util.EnumSet;

import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.Layout;

public abstract class SwingComponentWrapperElement<T extends JComponent> extends AbstractSwingComponentElement {
	protected T component;
	
	SwingComponentWrapperElement(Document document, String name, T component) {
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
	public float getIntrinsicContentBoxWidth(Layout.Directive directive, float parentContentBoxWidth) {
	  float scale = getOwnerDocument().getSettings().getScale();
      return (directive == Layout.Directive.MINIMUM ? component.getMinimumSize() : component.getPreferredSize()).width / scale;
	}

	@Override
	public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
	  float scale = getOwnerDocument().getSettings().getScale();
		return component.getPreferredSize().height / scale;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.COMPONENT;
	}

}
