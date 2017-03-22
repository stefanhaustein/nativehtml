package org.kobjects.nativehtml.util;

import java.util.HashMap;

import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class ElementImpl implements Element {
	protected ElementType elementType;
    protected Element parent;
    protected String name;
    protected Document document;
    protected HashMap<String, String> attributes;
    protected CssStyleDeclaration style;
    protected CssStyleDeclaration computedStyle;
    protected String textContent;
    protected HtmlCollectionImpl children;
    protected ContentType contentType;
    
    public ElementImpl(Document document, String name, ElementType elementType, ContentType contentType) {
    	this.document = document;
        this.elementType = elementType;
        this.name = name;
        this.contentType = contentType;
    }

    @Override
    public String getLocalName() {
        return name;
    }

    @Override
    public HtmlCollection getChildren() {
        return children == null ? HtmlCollection.EMPTY : children;
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
        if (name.equals("style")) {
        	style = CssStyleDeclaration.fromString(value);
        }
    }

    @Override
    public String getAttribute(String name) {
        return attributes == null ? null : attributes.get(name);
    }
    
	@Override
	public void setTextContent(String textContent) {
		this.textContent = textContent;
		this.children = null;
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		int index;
		if (children == null) {
			children = new HtmlCollectionImpl();
			index = 0;
		} else if (referenceChild != null && children != null) {
			index = children.indexOf(referenceChild);
			if (index == -1) {
				index = children.getLength();
			}
		} else {
			index = children.getLength();
		}
		children.add(index, newChild);
		newChild.setParentElement(this);
	}

	@Override
	public ElementType getElementType() {
		return elementType;
	}

	@Override
	public String getTextContent() {
		if (textContent != null) {
			return textContent;
		}
		if (children == null || children.getLength() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < children.size(); i++) {
			sb.append(children.get(i).getTextContent());
		}
		return sb.toString();
	}

	@Override
	public ContentType getElementContentType() {
		return contentType;
	}

  @Override
  public Document getOwnerDocument() {
    return document;
  }
}
