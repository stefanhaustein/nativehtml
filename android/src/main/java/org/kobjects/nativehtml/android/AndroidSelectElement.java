package org.kobjects.nativehtml.android;

import android.content.Context;
import android.widget.Spinner;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlSelectElement;
import org.kobjects.nativehtml.util.HtmlCollectionImpl;

public class AndroidSelectElement extends AndroidWrapperElement implements HtmlSelectElement {
    HtmlCollectionImpl children = new HtmlCollectionImpl();

    AndroidSelectElement(Context context, Document document) {
        super(context, document, "select", new Spinner(context));
    }

    @Override
    public ContentType getElementContentType() {
        return ContentType.DATA_ELEMENTS;
    }


    @Override
    public void insertBefore(Element newChild, Element referenceChild) {
        children.insertBefore(this, newChild, referenceChild);
    }

}
