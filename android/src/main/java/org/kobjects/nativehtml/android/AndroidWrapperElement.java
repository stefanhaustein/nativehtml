package org.kobjects.nativehtml.android;

import android.content.Context;
import android.view.View;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.Layout;

public abstract class AndroidWrapperElement extends AbstractAndroidComponentElement {

    View child;

    AndroidWrapperElement(Context context, Document document, String name, View child) {
        super(context, document, name);
        if (child != null) {
            addView(child);
            this.child = child;
        }
    }

    @Override
    protected void  onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    public float getIntrinsicContentBoxWidth(Layout.Directive directive, float parentContentBoxWidth) {
        if (child == null) {
            return 0;
        }
        float scale = document.getSettings().getScale();
        int widthSpec = Math.round(parentContentBoxWidth * scale) | AndroidCss.directiveToMeasureSpec(directive);
        child.measure(widthSpec, MeasureSpec.UNSPECIFIED);
        return child.getMeasuredWidth() / scale;
    }

    @Override
    public void insertBefore(Element newChild, Element referenceChild) {
    }

    @Override
    public HtmlCollection getChildren() {
        return HtmlCollection.EMPTY;
    }

    @Override
    public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
        if (child == null) {
            return 0;
        }
        float scale = document.getSettings().getScale();
        int widthSpec = Math.round(contentBoxWidth * scale) | MeasureSpec.EXACTLY;
        child.measure(widthSpec, MeasureSpec.UNSPECIFIED);
        return child.getMeasuredHeight() / scale;
    }

}
