package org.kobjects.nativehtml.dom;

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

    public HTMLCollection getChildren() {
        return HTMLCollection.EMPTY;
    }

	@Override
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		throw new RuntimeException("Can't append children to text data elements");
	}
	
}
