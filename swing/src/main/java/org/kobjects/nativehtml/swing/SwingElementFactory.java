package org.kobjects.nativehtml.swing;

import org.kobjects.nativehtml.dom.HtmlElementFactory;
import org.kobjects.nativehtml.dom.HtmlDocument;
import org.kobjects.nativehtml.dom.HtmlElement;
import org.kobjects.nativehtml.dom.HtmlElementType;

public class SwingElementFactory implements HtmlElementFactory {

	@Override
	public HtmlElement createElement(HtmlDocument document, HtmlElementType elementType, String elementName) {
		switch (elementType) {
			
		case COMPONENT:
			if (elementName.equals("select")) {
				return new SwingHtmlSelectComponent(document, elementName);
			} 
			if (elementName.equals("input")) {
				return new SwingHtmlInputElement(document, elementName);
			}
			if (elementName.equals("text-component")) {
				return new SwingHtmlTextComponent(document);
			}
			return new SwingComponentContainer(document, elementName);
			
		default:
			return null;
		}
	}
	
}