package org.kobjects.nativehtml.swing;

import java.util.EnumSet;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.dom.HtmlInputElement;

public class SwingHtmlInputElement extends SwingComponentWrapperElement<JComponent> implements HtmlInputElement {
	
	SwingHtmlInputElement(Document document, String name) {
		super(document, name, new JTextField("", 20));
	}
	
	public void setAttribute(String name, String value) {
		if (name.equals("type") && !name.equals(getAttribute(name))) {
			String text = getAttribute("value");
			if ("checkbox".equalsIgnoreCase(value)) {
				setComponent(new JCheckBox(text));
			} else if ("submit".equalsIgnoreCase(value)) {
				setComponent(new JButton(text == null ? "submit" : text));
			} else if ("radio".equals(value)) {
				setComponent(new JRadioButton(text));
			}
		}
		if (name.equals("value")) {
			if (component instanceof AbstractButton) {
				((AbstractButton) component).setText(value);
			} else if (component instanceof JTextField) {
				((JTextField) component).setText(value);
			}
		}
		
		super.setAttribute(name, value);
	}
	
	@Override
	public HtmlCollection getChildren() {
		return HtmlCollection.EMPTY;
	}

	@Override
	public ContentType getElemnetContentType() {
		return ContentType.EMPTY;
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		// Unsupported... throw??
		
	}

	

}
