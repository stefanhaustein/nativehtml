package org.kobjects.nativehtml.android;

import android.content.Context;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.layout.Layout;

public class AndroidContainerElement extends AbstractAndroidComponentElement {


    public AndroidContainerElement(Context context, Document document, String name) {
        super(context, document, name);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    public float getIntrinsicContentBoxWidth(Layout.Directive directive, float parentContentBoxWidth) {
        return 0;
    }

    @Override
    public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
        return 0;
    }


    @Override
    public ContentType getElementContentType() {
        return null;
    }
}