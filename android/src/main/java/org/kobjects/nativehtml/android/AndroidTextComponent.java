package org.kobjects.nativehtml.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import java.net.URI;
import java.util.ArrayList;
import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.ComponentElement;
import org.kobjects.nativehtml.layout.Layout;
import org.kobjects.nativehtml.util.ElementImpl;
import org.kobjects.nativehtml.util.HtmlCollectionImpl;

public class AndroidTextComponent extends TextView implements ComponentElement {
    private static final String TAG = "AndroidTextComponent";
    private static final CssStyleDeclaration EMTPY_STYLE = new CssStyleDeclaration();
    static final int PAINT_MASK = ~(Paint.STRIKE_THRU_TEXT_FLAG | Paint.UNDERLINE_TEXT_FLAG);

    private final Document document;
    private CssStyleDeclaration computedStyle;
    private HtmlCollectionImpl children = new HtmlCollectionImpl();
    private boolean dirty;
    SpannableStringBuilder content = new SpannableStringBuilder("");
    float contentBoxWidth;
    float x;
    float y;

    public AndroidTextComponent(Context context, Document document) {
        super(context);
        this.document = document;
        setTextIsSelectable(true);
    }


    @Override
    public String getLocalName() {
        return "text-container";
    }

    @Override
    public void setAttribute(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    @Override
    public Element getParentElement() {
        return (Element) getParent();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.COMPONENT;
    }

    @Override
    public ContentType getElementContentType() {
        return ContentType.FORMATTED_TEXT;
    }

    @Override
    public void setParentElement(Element parent) {
    }

    @Override
    public HtmlCollection getChildren() {
        return children;
    }

    @Override
    public CssStyleDeclaration getStyle() {
        return EMTPY_STYLE;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertBefore(Element newChild, Element referenceChild) {
        children.insertBefore(this, newChild, referenceChild);
        dirty = true;
    }

    @Override
    public void requestLayout() {
        dirty = true;
        super.requestLayout();
    }

    @Override
    public Document getOwnerDocument() {
        return document;
    }

    @Override
    public void setBorderBoxBounds(float x, float y, float width, float height, float parentContentBoxWidth) {
        this.contentBoxWidth = parentContentBoxWidth;
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

    @Override
    public float getIntrinsicContentBoxWidth(Layout.Directive directive, float parentContentBoxWidth) {
        validateContent();
        float scale = document.getSettings().getScale();
        int widthSpec = Math.round(parentContentBoxWidth * scale) | MeasureSpec.AT_MOST;

        measure(widthSpec, MeasureSpec.UNSPECIFIED);
        return getMeasuredWidth() / scale;
    }

    @Override
    public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
        validateContent();
        float scale = document.getSettings().getScale();
        measure(Math.round(contentBoxWidth * scale) | MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED);
        return getMeasuredHeight() / scale;
    }

    private void validateContent() {
        if (dirty) {
            dirty = false;
            content.clear();
            content.clearSpans();
            for (int i = 0; i < children.getLength(); i++) {
                updateChild(children.item(i), computedStyle);
            }
            setText(content);
        }
    }

    @Override
    public void setComputedStyle(CssStyleDeclaration computedStyle) {
        this.computedStyle = computedStyle;
        // System.out.println("applyRootStyle to '" + content + "': " + computedStyle);
        float scale = document.getSettings().getScale();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, computedStyle.getPx(CssProperty.FONT_SIZE, 0) * scale);
        setTextColor(computedStyle.getColor(CssProperty.COLOR));
        setTypeface(AndroidCss.getTypeface(computedStyle)); // , AndroidCss.getTextStyle(this.computedStyle));
        setPaintFlags((getPaintFlags() & PAINT_MASK) | AndroidCss.getPaintFlags(computedStyle));
        setTextIsSelectable(computedStyle.getEnum(CssProperty.USER_SELECT) != CssEnum.NONE);
        switch (this.computedStyle.getEnum(CssProperty.TEXT_ALIGN)) {
            case RIGHT:
                setGravity(Gravity.RIGHT);
                break;
            case CENTER:
                setGravity(Gravity.CENTER);
                break;
            default:
                setGravity(Gravity.LEFT);
                break;
        }
        dirty = true;
    }

    void updateChild(final Element element, CssStyleDeclaration parentStyle) {
        int start = content.length();

        HtmlCollection children = element.getChildren();

        CssStyleDeclaration computedStyle = element.getComputedStyle();
        if (element.getLocalName().equals("br")) {
            content.append("\n");
        } else if (element.getLocalName().equals("img")) {
            content.append("\u25a1");
        } else if (children.getLength() != 0) {
            for (int i = 0; i < children.getLength(); i++) {
                updateChild(children.item(i), computedStyle);
            }
        }  else {
            content.append(element.getTextContent());
        }
        int end = content.length();

        ArrayList<Object> spans = new ArrayList<Object>();
        float scale = document.getSettings().getScale();

        if (element.getLocalName().equals("img")) {
            String src = element.getAttribute("src");
            if (src != null && !src.isEmpty()) {
                URI uri = document.resolveUrl(src);
                Bitmap bitmap = ((AndroidPlatform) document.getPlatform()).getImage(element, uri);
                if (bitmap != null) {
                    BitmapDrawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                    float imageWidth = bitmap.getWidth();
                    float imageHeight = bitmap.getHeight();
                    if (computedStyle.isSet(CssProperty.WIDTH)) {
                        imageWidth = computedStyle.getPx(CssProperty.WIDTH, contentBoxWidth);
                        if (computedStyle.isSet((CssProperty.HEIGHT))) {
                            imageHeight = computedStyle.getPx(CssProperty.WIDTH, contentBoxWidth);
                        } else {
                            imageHeight *= imageWidth / bitmap.getWidth();
                        }
                    } else if (computedStyle.isSet(CssProperty.HEIGHT)) {
                        imageHeight = computedStyle.getPx(CssProperty.HEIGHT, contentBoxWidth);
                        imageWidth *= imageHeight / bitmap.getHeight();
                    }
                    drawable.setBounds(0, 0,
                            Math.round(imageWidth * scale),
                            Math.round(imageHeight * scale));

                    spans.add(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE));
                }
            }
        }

        String typefaceName = AndroidCss.getFontFamilyName(computedStyle);
        if (!typefaceName.equals(AndroidCss.getFontFamilyName(parentStyle))) {
            spans.add(new TypefaceSpan(typefaceName));
        }

        int typefaceFlags = AndroidCss.getTextStyle(computedStyle);
        if (typefaceFlags != AndroidCss.getTextStyle(parentStyle)) {
            spans.add(new StyleSpan(typefaceFlags));
        }

        float size = parentStyle.getPx(CssProperty.FONT_SIZE, 0);
        if (size != computedStyle.getPx(CssProperty.FONT_SIZE, 0)) {
            spans.add(new AbsoluteSizeSpan(Math.round(size * scale)));
        }

        int color = computedStyle.getColor(CssProperty.COLOR);
        if (color != parentStyle.getColor(CssProperty.COLOR)) {
            spans.add(new ForegroundColorSpan(color));
        }
        CssEnum textDecoration = computedStyle.getEnum(CssProperty.TEXT_DECORATION);
        if (textDecoration != parentStyle.getEnum(CssProperty.TEXT_DECORATION)) {
            switch (textDecoration) {
                case UNDERLINE:
                    spans.add(new UnderlineSpan());
                    break;
                case LINE_THROUGH:
                    spans.add(new StrikethroughSpan());
                    break;
            }
        }
        CssEnum verticalAlign = computedStyle.getEnum(CssProperty.VERTICAL_ALIGN);
        if (verticalAlign != parentStyle.getEnum(CssProperty.VERTICAL_ALIGN)) {
            switch (verticalAlign) {
                case SUB:
                    spans.add(new SubscriptSpan());
                    break;
                case SUPER:
                    spans.add(new SuperscriptSpan());
                    break;
            }
        }

        if (element.getLocalName().equals("a") && element.getAttribute("href") != null) {
            setTextIsSelectable(false);
            setMovementMethod(LinkMovementMethod.getInstance());
            spans.add(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    document.getRequestHandler().openLink(document.resolveUrl(element.getAttribute("href")));
                }
            });
        }
        if (spans.size() > 0) {
            for (Object span : spans) {
                content.setSpan(span, start, end, 0);
            }
        }
    }

}
