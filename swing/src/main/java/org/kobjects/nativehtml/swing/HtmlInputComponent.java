package org.kobjects.nativehtml.swing;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.HtmlInputElement;

public class HtmlInputComponent extends ComponentWrapper<JTextField> implements HtmlInputElement {

	HtmlInputComponent(Document document, String name) {
		super(document, name, new JTextField());
		
	}

}
