package org.kobjects.nativehtml.dom;

public class ElementDataElement extends AbstractElement {
    private HTMLCollectionImpl children;

    public ElementDataElement(String name) {
        super(name);
    }

    @Override
    public HTMLCollection getChildren() {
        return children == null ? HTMLCollection.EMPTY : children;
    }

    @Override
    public String getTextContent() {
        if (getChildren().getLength() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getChildren().getLength(); i++) {
            sb.append(getChildren().item(i).getTextContent());
        }
        return sb.toString();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.ELEMENT_DATA;
    }


    @Override
	public void setTextContent(String textContent) {
		throw new RuntimeException("Text not allowed inside ElementDataElement");
		
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		int index;
		if (children == null) {
			children = new HTMLCollectionImpl();
			index = 0;
		} else if (referenceChild != null && children != null) {
			index = children.indexOf(referenceChild);
			if (index == -1) {
				index = children.getLength();
			}
		} else {
			index = children.getLength();
		}
		children.add(index, newChild);
	}
}
