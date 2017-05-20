package org.kobjects.nativehtml.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;
import java.util.HashMap;
import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.layout.ComponentElement;
import org.kobjects.nativehtml.util.ElementImpl;

public abstract class AbstractAndroidComponentElement extends ViewGroup implements ComponentElement {
    final Document document;
    final String name;
    CssStyleDeclaration style;
    CssStyleDeclaration computedStyle;
    float containingBoxWidth;
    Paint paint = new Paint();
    private HashMap<String,String> attributes;
    float x;
    float y;

    public AbstractAndroidComponentElement(Context context, Document document, String name) {
        super(context);
        this.name = name;
        this.document = document;
        setWillNotDraw(false);
        setClipChildren(false);
    }


    @Override
    public String getLocalName() {
        return name;
    }


    @Override
    public void setAttribute(String name, String value) {
        if (attributes == null) {
            this.attributes = new HashMap<>();
        }
        attributes.put(name, value);
        if (name.equals("style")) {
            style = CssStyleDeclaration.fromString(value);
        }
    }

    @Override
    public String getAttribute(String name) {
        return attributes == null ? null : attributes.get(name);
    }


    @Override
    public Element getParentElement() {
        return getParent() instanceof Element ? (Element) getParent() : null;
    }

    @Override
    public void setComputedStyle(CssStyleDeclaration style) {
        this.computedStyle = style;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.COMPONENT;
    }


    @Override
    public void setParentElement(Element parent) {
        if (parent == null && getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    @Override
    public CssStyleDeclaration getStyle() {
        return style;
    }

    @Override
    public CssStyleDeclaration getComputedStyle() {
        return computedStyle;
    }

    @Override
    public String getTextContent() {
        return ElementImpl.getTextContent(this);
    }

    @Override
    public void setTextContent(String textContent) {

    }

    @Override
    public Document getOwnerDocument() {
        return document;
    }

    @Override
    public void setBorderBoxBounds(float x, float y, float width, float height, float parentContentBoxWidth) {
        this.containingBoxWidth = parentContentBoxWidth;
        this.x = x;
        this.y = y;
        float scale = document.getSettings().getScale();
        setMeasuredDimension(Math.round(width * scale), Math.round(height * scale));
    }

    @Override
    public void moveRelative(float dx, float dy) {
        x += dx;
        y += dy;
    }

    private void drawBackground(Canvas canvas, int x, int y, int w, int h) {
        CssStyleDeclaration style = getComputedStyle();
        if (style.isSet(CssProperty.BACKGROUND_COLOR)) {
            paint.setColor(style.getColor(CssProperty.BACKGROUND_COLOR));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x, y, x+ w, y + h, paint);
        }

        if (!style.isSet(CssProperty.BACKGROUND_IMAGE)) {
            return;
        }

        String bgImage = style.getString(CssProperty.BACKGROUND_IMAGE);
        Bitmap image = ((AndroidPlatform) getOwnerDocument().getPlatform()).getImage(this, getOwnerDocument().resolveUrl(bgImage));

        if (image == null) {
            return;
        }
        canvas.save();
        canvas.clipRect(x, y, x + w, y + h);
        CssEnum repeat = style.getEnum(CssProperty.BACKGROUND_REPEAT);
        int bgY = 0;
        int bgX = 0;
        if (repeat == CssEnum.REPEAT_Y || repeat == CssEnum.REPEAT) {
            do {
                if (repeat == CssEnum.REPEAT) {
                    int currentBgX = bgX;
                    do {
                        canvas.drawBitmap(image, x + currentBgX, y + bgY, null);
                        currentBgX += image.getWidth();
                    } while (currentBgX < w);
                } else {
                    canvas.drawBitmap(image, x + bgX, y + bgY, null);
                }
                bgY += image.getHeight();
            } while (bgY < h);
        } else if (repeat == CssEnum.REPEAT_X) {
            do {
                canvas.drawBitmap(image, x + bgX, y + bgY, null);
                bgX += image.getWidth();
            } while (bgX < w);
        } else {
            canvas.drawBitmap(image, x + bgX, y + bgY, null);
        }

        canvas.restore();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float scale = getOwnerDocument().getSettings().getScale();
        int borderLeft = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth));
        int borderRight = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth));
        int borderTop = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth));
        int borderBottom = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth));

        int w = getWidth() ;
        int h = getHeight();

        // Background paint area is specified using 'background-clip' property, and default value of it
        // is 'border-box'
        drawBackground(canvas, borderLeft, borderTop, getWidth() - borderRight, getHeight() - borderBottom);

        paint.setStyle(Paint.Style.STROKE);

        if (borderTop > 0 && computedStyle.getEnum(CssProperty.BORDER_TOP_STYLE) != CssEnum.NONE) {
            paint.setColor(computedStyle.getColor(CssProperty.BORDER_TOP_COLOR));
            int dLeft = (borderLeft << 8) / borderTop;
            int dRight = (borderRight << 8) / borderTop;
            for (int i = 0; i < borderTop; i++) {
                canvas.drawLine(
                        ((i * dLeft) >> 8), i,
                        w - 1 - ((i * dRight) >> 8), i, paint);
            }
        }
        if (borderRight > 0 && computedStyle.getEnum(CssProperty.BORDER_RIGHT_STYLE) != CssEnum.NONE) {
            paint.setColor(computedStyle.getColor(CssProperty.BORDER_RIGHT_COLOR));
            int dTop = (borderTop << 8) / borderRight;
            int dBottom = (borderBottom << 8) / borderRight;
            for (int i = 0; i < borderRight; i++) {
                canvas.drawLine(
                        w - 1 - i, ((i * dTop) >> 8),
                        w - 1 - i, h - 1 - ((i * dBottom) >> 8), paint);
            }
        }
        if (borderBottom > 0 && computedStyle.getEnum(CssProperty.BORDER_BOTTOM_STYLE) != CssEnum.NONE) {
            paint.setColor(computedStyle.getColor(CssProperty.BORDER_BOTTOM_COLOR));
            int dLeft = (borderLeft << 8) / borderBottom;
            int dRight = (borderRight << 8) / borderBottom;
            for (int i = 0; i < borderBottom; i++) {
                canvas.drawLine(
                        ((i * dLeft) >> 8), h - 1 - i,
                        w - 1 - ((i * dRight) >> 8) - 1, h - 1 - i, paint);
            }
        }
        if (borderLeft > 0 && computedStyle.getEnum(CssProperty.BORDER_LEFT_STYLE) != CssEnum.NONE) {
            paint.setColor(computedStyle.getColor(CssProperty.BORDER_LEFT_COLOR));
            int dTop = (borderTop << 8) / borderLeft;
            int dBottom = (borderBottom << 8) / borderLeft;
            for (int i = 0; i < borderLeft; i++) {
                canvas.drawLine(
                        i, ((i * dTop) >> 8),
                        i, h - 1 - ((i * dBottom) >> 8), paint);
            }
        }
    }
}
