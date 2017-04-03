package org.kobjects.nativehtml.android;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

    public AndroidTextComponent(Context context, Document document) {
        super(context);
        this.document = document;
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
    public Document getOwnerDocument() {
        return document;
    }

    @Override
    public void setBorderBoxBounds(float x, float y, float width, float height, float parentContentBoxWidth) {
        float scale = document.getSettings().getScale();
        setX(x * scale);
        setY(y * scale);
        setMeasuredDimension(Math.round(width * scale), Math.round(height * scale));
    }

    @Override
    public void moveRelative(float dx, float dy) {
        float scale = document.getSettings().getScale();
        setX(getX() + dx * scale);
        setY(getY() + dy * scale);
    }

    @Override
    public float getIntrinsicContentBoxWidth(Layout.Directive directive, float parentContentBoxWidth) {
        validateContent();
        float scale = document.getSettings().getScale();
        int widthSpec = Math.round(parentContentBoxWidth * scale);

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
        HtmlCollection children = element.getChildren();

        CssStyleDeclaration computedStyle = element.getComputedStyle();
        int start = content.length();
        if (children.getLength() != 0) {
            for (int i = 0; i < children.getLength(); i++) {
                updateChild(children.item(i), computedStyle);
            }
        } else if (element.getLocalName().equals("br")) {
            content.append("\n");
        } else {
            content.append(element.getTextContent());
        }
        int end = content.length();
        /*
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            float imageWidth = bitmap.getWidth();
            float imageHeight = bitmap.getHeight();
            int cssContentWidth = ((HtmlViewGroup) htmlTextView.getParent()).cssContentWidth;
            if (element.computedStyle.isSet(CssProperty.WIDTH)) {
                imageWidth = element.computedStyle.get(CssProperty.WIDTH, CssUnit.PX, cssContentWidth);
                if (element.computedStyle.isSet((CssProperty.HEIGHT))) {
                    imageHeight = element.computedStyle.get(CssProperty.WIDTH, CssUnit.PX, cssContentWidth);
                } else {
                    imageHeight *= imageWidth / bitmap.getWidth();
                }
            } else if (element.computedStyle.isSet(CssProperty.HEIGHT)) {
                imageHeight = element.computedStyle.get(CssProperty.HEIGHT, CssUnit.PX, cssContentWidth);
                imageWidth *= imageHeight / bitmap.getHeight();
            }
            drawable.setBounds(0, 0, Math.round(imageWidth * htmlTextView.htmlView.scale),
                    Math.round(imageHeight * htmlTextView.htmlView.scale));

            spans.add(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE));
        }*/
        ArrayList<Object> spans = new ArrayList<Object>();
        float scale = document.getSettings().getScale();

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
