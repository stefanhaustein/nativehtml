package org.kobjects.nativehtml.util;

import java.util.HashMap;

import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;

public abstract class AbstractElement implements Element {
    protected Element parent;
    protected String name;
    private HashMap<String, String> attributes;
    private CssStyleDeclaration style;
    private CssStyleDeclaration computedStyle;

    protected AbstractElement(String name) {
        this.name = name;
    }

    @Override
    public String getLocalName() {
        return name;
    }


    @Override
    public Element getParentElement() {
        return parent;
    }

    @Override
    public void setParentElement(Element parent) {
        this.parent = parent;
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
    public void setComputedStyle(CssStyleDeclaration computedStyle) {
    	this.computedStyle = computedStyle;
    }

    @Override
    public void setAttribute(String name, String value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    @Override
    public String getAttribute(String name) {
        return attributes == null ? null : attributes.get(name);
    }
}
