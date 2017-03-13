package org.kobjects.nativehtml.util;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class TextDataElement extends AbstractElement {

    String textContent;

    public TextDataElement(String name) {
        super(name);
    }


	public String getTextContent() {
        return textContent;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.TEXT_DATA;
    }

    public HtmlCollection getChildren() {
        return HtmlCollection.EMPTY;
    }

	@Override
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		throw new RuntimeException("Can't append children to text data element <" + name + ">");
	}
	
}
