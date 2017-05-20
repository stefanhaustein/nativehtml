package org.kobjects.nativehtml.android;

import android.content.Context;
import android.view.View;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.ElementLayoutHelper;
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
        float scale = getOwnerDocument().getSettings().getScale();

        int left = Math.round(scale * (style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth)));
        int top = Math.round(scale * (style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_TOP, containingBoxWidth)));

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(left, top, child.getMeasuredWidth(), child.getMeasuredHeight());
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

    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        float scale = document.getSettings().getScale();
        float[] size = ElementLayoutHelper.getBorderBoxSize(this, Layout.Directive.FIT_CONTENT, containingBoxWidth);
        setMeasuredDimension(Math.round(size[0] * scale), Math.round(size[1] * scale));

    }

}
