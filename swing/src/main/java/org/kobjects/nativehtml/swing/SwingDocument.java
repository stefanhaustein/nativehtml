package org.kobjects.nativehtml.swing;

import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementDataElement;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.TextDataElement;

public class SwingDocument extends Document {

	@Override
	protected Element createElement(ElementType elementType, String elementName) {
		switch (elementType) {
			
		case COMPONENT_CONTAINER:
			return new ComponentContainer(elementName);
			
		case ELEMENT_DATA:
			return new ElementDataElement(elementName);
			
		case LEAF_TEXT:
			return new TextDataElement(elementName) {
				public ElementType getElementType() {
					return ElementType.LEAF_TEXT;
				}
			};
			
		case TEXT_COMPONENT:
			return new TextComponent();

		case TEXT_CONTAINER:
			return new ElementDataElement(elementName) {
				public ElementType getElementType() {
					return ElementType.TEXT_CONTAINER;
				}
			};
			
		case TEXT_DATA:
			return new TextDataElement(elementName);
			
		default:
			throw new RuntimeException("Unrecognized: " + elementType + ": <" + elementName + ">");
		}
	}
	
}