package org.kobjects.nativehtml.swing;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.Document;

public class HtmlSelectComponent extends ComponentWrapper {

	HtmlSelectComponent(Document document, String name) {
		super(document, name, new JComboBox<>());
		// TODO Auto-generated constructor stub
	}

}
