package org.kobjects.nativehtml.util;

import java.util.ArrayList;

import org.kobjects.nativehtml.dom.HtmlElement;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class HtmlCollectionImpl extends ArrayList<HtmlElement> implements HtmlCollection {
    @Override
    public int getLength() {
        return this.size();
    }

    @Override
    public HtmlElement item(int index) {
        return get(index);
    }

	public void insertBefore(HtmlElement newChild, HtmlElement referenceChild) {
		add(newChild);
	}
}