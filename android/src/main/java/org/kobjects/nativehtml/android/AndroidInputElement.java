package org.kobjects.nativehtml.android;

import android.content.Context;
import android.widget.EditText;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.HtmlInputElement;

public class AndroidInputElement extends AndroidWrapperElement implements HtmlInputElement{

    public AndroidInputElement(Context context, Document document) {
        super(context, document, "input", new EditText(context));
    }

    @Override
    public ContentType getElementContentType() {
        return ContentType.EMPTY;
    }


}
