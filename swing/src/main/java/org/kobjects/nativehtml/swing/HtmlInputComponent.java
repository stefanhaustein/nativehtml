package org.kobjects.nativehtml.swing;

import java.util.EnumSet;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.dom.HtmlInputElement;

public class HtmlInputComponent extends ComponentWrapper<JTextField> implements HtmlInputElement {
	private static final EnumSet<ElementType> CONTENT_TYPE = EnumSet.noneOf(ElementType.class);
	
	HtmlInputComponent(Document document, String name) {
		super(document, name, new JTextField("", 20));
		
	}
	
	@Override
	public HtmlCollection getChildren() {
		return HtmlCollection.EMPTY;
	}

	@Override
	public EnumSet<ElementType> getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		// Unsupported... throw??
		
	}

	

}
