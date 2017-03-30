package org.kobjects.nativehtml.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.layout.ComponentElement;
import org.kobjects.nativehtml.util.ElementImpl;

public abstract class AbstractAndroidComponentElement extends ViewGroup implements ComponentElement, HtmlCollection {
    private final Document document;
    private final String name;
    private CssStyleDeclaration style;
    private CssStyleDeclaration computedStyle;


    public AbstractAndroidComponentElement(Context context, Document document, String name) {
        super(context);
        this.name = name;
        this.document = document;
    }


    @Override
    public String getLocalName() {
        return name;
    }


    @Override
    public void setAttribute(String name, String value) {

    }

    @Override
    public String getAttribute(String name) {
        return null;
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

    }

    @Override
    public HtmlCollection getChildren() {
        return this;
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
    public void insertBefore(Element newChild, Element referenceChild) {
        if (referenceChild == null) {
            addView((View) newChild);
        } else {
            int refIndex = indexOfChild((View) referenceChild);
            addView((View) newChild, refIndex);
        }
    }

    @Override
    public Document getOwnerDocument() {
        return document;
    }

    @Override
    public void setBorderBoxBounds(float x, float y, float width, float height, float parentContentBoxWidth) {

    }

    @Override
    public void moveRelative(float dx, float dy) {

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
