package org.kobjects.nativehtml.dom;

import java.util.HashMap;

public abstract class AbstractElement implements Element {
    protected Element parent;
    protected String name;
    private HashMap<String, String> attributes;

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
    public CSSStyleDeclaration getStyle() {
        return null;
    }

    @Override
    public CSSStyleDeclaration getComputedStyle() {
        return null;
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
