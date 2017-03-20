package org.kobjects.nativehtml.util;

import java.util.ArrayList;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class HtmlCollectionImpl extends ArrayList<Element> implements HtmlCollection {
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