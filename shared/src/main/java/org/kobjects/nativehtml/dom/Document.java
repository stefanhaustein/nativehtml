package org.kobjects.nativehtml.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import org.kobjects.nativehtml.util.ElementImpl;

public class Document {
	
    private static final LinkedHashMap<String, ElementType> ELEMENT_TYPES = new LinkedHashMap<>();
    static {
        ELEMENT_TYPES.put("#TEXT", ElementType.LEAF_TEXT);
        ELEMENT_TYPES.put("a", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("b", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("big", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("br", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("del", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("em", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("font", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("head", ElementType.ELEMENT_DATA);
        ELEMENT_TYPES.put("html", ElementType.SKIP);
        ELEMENT_TYPES.put("i", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("img", ElementType.INLINE_IMAGE);  // Might be an image (=LEAF_COMPONENT), too; will get adjusted
        ELEMENT_TYPES.put("input", ElementType.LEAF_COMPONENT);
        ELEMENT_TYPES.put("ins", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("link", ElementType.TEXT_DATA);
        ELEMENT_TYPES.put("meta", ElementType.TEXT_DATA);
        ELEMENT_TYPES.put("option", ElementType.TEXT_DATA);
        ELEMENT_TYPES.put("script", ElementType.TEXT_DATA);
        ELEMENT_TYPES.put("select", ElementType.LEAF_COMPONENT);
        ELEMENT_TYPES.put("small", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("span", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("strike", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("strong", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("style", ElementType.TEXT_DATA);
        ELEMENT_TYPES.put("sub", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("sup", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("tbody", ElementType.SKIP);
        ELEMENT_TYPES.put("text-component", ElementType.TEXT_COMPONENT);
        ELEMENT_TYPES.put("thead", ElementType.SKIP);
        ELEMENT_TYPES.put("title", ElementType.TEXT_DATA);
        ELEMENT_TYPES.put("tt", ElementType.TEXT_CONTAINER);
        ELEMENT_TYPES.put("u", ElementType.TEXT_CONTAINER);
    }

    private static ElementType getElementType(String name) {
        ElementType result = ELEMENT_TYPES.get(name);
        return result == null ? ElementType.COMPONENT_CONTAINER : result;
    }

	private URI baseURI;
	private final ElementFactory elementFactory;

	public Document(ElementFactory elementFactory) {
		this.elementFactory = elementFactory;
		try {
			baseURI = new URI("file:///");
		} catch (URISyntaxException e) {
			throw new RuntimeException();
		}
	}
    
    public Element createElement(String name) {
    	ElementType elementType = getElementType(name);
        Element result = elementFactory.createElement(this, elementType, name);
        return result == null ? new ElementImpl(this, elementType, name) : result;
    }

    public void setBaseURI(URI baseURI) {
    	this.baseURI = baseURI;
    }

	public URI getBaseURI() {
		return baseURI;
	}


}
