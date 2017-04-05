package org.kobjects.nativehtml.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.BlockLayout;
import org.kobjects.nativehtml.layout.Layout;
import org.kobjects.nativehtml.layout.TableLayout;
import org.kobjects.nativehtml.util.Strings;

public class AndroidContainerElement extends AbstractAndroidComponentElement implements  HtmlCollection{
    Layout layout;

    public AndroidContainerElement(Context context, Document document, String name) {
        super(context, document, name);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        CssStyleDeclaration style = getComputedStyle();

        float left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
        float top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
        float right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);

        float scale = getOwnerDocument().getSettings().getScale();
        float contentBoxWidth = (r - l) / scale - left - right;
        getLayout().layout(this, left, top, contentBoxWidth, false);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof AbstractAndroidComponentElement) {
                ((AbstractAndroidComponentElement) child).containingBoxWidth = containingBoxWidth;
            }
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    private Layout getLayout() {
        if (layout == null) {
            if (getLocalName().equals("table")) {
                layout = new TableLayout();
            } else {
                layout = new BlockLayout();
            }
        }
        return layout;
    }


    @Override
    public float getIntrinsicContentBoxWidth(Layout.Directive directive, float contentBoxWidth) {
        return getLayout().measureWidth(this, directive, contentBoxWidth);
    }

    @Override
    public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
        return getLayout().layout(this, 0, 0, contentBoxWidth, true /* measureOnly */);
    }

    @Override
    public void onMeasure(int withSpec, int heightSpec) {
        int widthMode = MeasureSpec.getMode(withSpec);
        int width = MeasureSpec.getSize(withSpec);

        float scale = getOwnerDocument().getSettings().getScale();
        float containingBoxWidth = width / scale;
        CssStyleDeclaration style = getComputedStyle();

        float bottom = style.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_BOTTOM, containingBoxWidth);
        float left = style.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_LEFT, containingBoxWidth);
        float top = style.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_TOP, containingBoxWidth);
        float right = style.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth) +
                style.getPx(CssProperty.PADDING_RIGHT, containingBoxWidth);

        Layout.Directive directive;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                directive = Layout.Directive.MINIMUM;
                break;
            case MeasureSpec.EXACTLY:
                directive = Layout.Directive.STRETCH;
                break;
            case MeasureSpec.UNSPECIFIED:
                directive = Layout.Directive.FIT_CONTENT;
                break;
            default:
                directive = Layout.Directive.FIT_CONTENT;
        }

        float contentBoxWidth = getLayout().measureWidth(this, directive, containingBoxWidth);
        float contentBoxHeight = getLayout().layout(this, left, top, contentBoxWidth, true);

        float borderBoxWidth = left + contentBoxWidth + right;
        float borderBoxHeight = top + contentBoxHeight + bottom;

        setMeasuredDimension(Math.round(borderBoxWidth * scale), Math.round(borderBoxHeight * scale));
    }

    @Override
    public void insertBefore(Element newChild, Element referenceChild) {
        if (referenceChild == null) {
            addView((View) newChild);
        } else {
            int refIndex = indexOfChild((View) referenceChild);
            addView((View) newChild, refIndex);
        }
    }

    @Override
    public ContentType getElementContentType() {
        return ContentType.COMPONENTS;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int listIndex = 1;
        CssEnum listStyleType = getComputedStyle().getEnum(CssProperty.LIST_STYLE_TYPE);
        float scale = getOwnerDocument().getSettings().getScale();
        Paint.FontMetrics bulletMetrics = null;

        for (int i = 0; i < getChildCount(); i++) {
            View component = getChildAt(i);
            Element element = (Element) component;
            CssStyleDeclaration childStyle = element.getComputedStyle();

            if (childStyle.getEnum(CssProperty.DISPLAY) == CssEnum.LIST_ITEM
                    && listStyleType != CssEnum.NONE) {
                if (bulletMetrics == null) {
                    bulletMetrics = new Paint.FontMetrics();
                }
                String bullet = Strings.getBullet(listStyleType, listIndex++);
                AndroidCss.setTextPaint(childStyle, scale, paint);
                paint.getFontMetrics(bulletMetrics);

                float top = childStyle.getPx(CssProperty.BORDER_TOP_WIDTH, 0)
                        + childStyle.getPx(CssProperty.PADDING_TOP, 0);
                float left = childStyle.getPx(CssProperty.BORDER_LEFT_WIDTH, 0)
                        + childStyle.getPx(CssProperty.PADDING_LEFT, 0);

                canvas.drawText(
                        bullet,
                        component.getX() + left * scale - paint.measureText(bullet),
                        component.getY() + top * scale - bulletMetrics.top,
                        paint);
            }
        }
    }


    @Override
    public HtmlCollection getChildren() {
        return this;
    }



    @Override
    public int getLength() {
        return getChildCount();
    }


    @Override
    public Element item(int index) {
        return (Element) getChildAt(index);
    }

}