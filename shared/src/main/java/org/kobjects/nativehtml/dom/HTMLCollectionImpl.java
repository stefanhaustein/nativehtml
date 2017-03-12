package org.kobjects.nativehtml.dom;

import java.util.ArrayList;

public class HTMLCollectionImpl extends ArrayList<Element> implements HTMLCollection {
    @Override
    public int getLength() {
        return this.size();
    }

    @Override
    public Element item(int index) {
        return get(index);
    }

	public void insertBefore(Element newChild, Element referenceChild) {
		add(newChild);
	}
}