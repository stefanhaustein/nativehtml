package org.kobjects.nativehtml.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.LinkedHashMap;

import org.kobjects.nativehtml.util.ElementImpl;

public class Document {
	
    private static final LinkedHashMap<String, ElementType> ELEMENT_TYPES = new LinkedHashMap<>();
    private static final LinkedHashMap<String, EnumSet<ElementType>> CONTENT_TYPES = new LinkedHashMap<>();
    private static final EnumSet<ElementType> CONTENT_NONE = EnumSet.noneOf(ElementType.class);
    private static final EnumSet<ElementType> CONTENT_COMPONENTS = EnumSet.of(ElementType.COMPONENT);
    private static final EnumSet<ElementType> CONTENT_FORMATTED_TEXT = EnumSet.of(ElementType.FORMATTED_TEXT);
    private static final EnumSet<ElementType> CONTENT_TEXT_ONLY = EnumSet.of(ElementType.TEXT_ONLY);
    
    static void add(String name, ElementType elementType, EnumSet<ElementType> contentType) {
    	ELEMENT_TYPES.put(name, elementType);
    	CONTENT_TYPES.put(name, contentType);
    }
    
    static {
        add("a", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("b", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("big", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("br", ElementType.FORMATTED_TEXT, CONTENT_NONE);
        add("del", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("em", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("font", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("head", ElementType.DATA, EnumSet.of(ElementType.DATA));
        add("html", ElementType.SKIP, EnumSet.of(ElementType.DATA, ElementType.COMPONENT));
        add("i", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("img", ElementType.INLINE_IMAGE, CONTENT_NONE);  // Might be an image (=LEAF_COMPONENT), too; will get adjusted
        add("input", ElementType.COMPONENT, CONTENT_NONE);
        add("ins", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("link", ElementType.DATA, CONTENT_NONE);
        add("meta", ElementType.DATA, CONTENT_NONE);
        add("option", ElementType.DATA, CONTENT_TEXT_ONLY);
        add("script", ElementType.DATA, CONTENT_TEXT_ONLY);
        add("select", ElementType.COMPONENT, EnumSet.of(ElementType.DATA));
        add("small", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("span", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("strike", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("strong", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("style", ElementType.DATA, CONTENT_TEXT_ONLY);
        add("sub", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("sup", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("tbody", ElementType.SKIP, CONTENT_COMPONENTS);
        add("text-component", ElementType.COMPONENT, CONTENT_FORMATTED_TEXT);
        add("thead", ElementType.SKIP, CONTENT_COMPONENTS);
        add("title", ElementType.DATA, CONTENT_TEXT_ONLY);
        add("tt", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
        add("u", ElementType.FORMATTED_TEXT, CONTENT_FORMATTED_TEXT);
    }

    public static ElementType getElementType(String name) {
        ElementType result = ELEMENT_TYPES.get(name);
        return result == null ? ElementType.COMPONENT : result;
    }

    private static EnumSet<ElementType> getContentType(String name) {
        EnumSet<ElementType> result = CONTENT_TYPES.get(name);
        return result == null ? CONTENT_COMPONENTS : result;
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
    	EnumSet<ElementType> contentType = getContentType(name);
        Element result = elementFactory.createElement(this, elementType, name);
        return result == null ? new ElementImpl(this, name, elementType, contentType) : result;
    }

    public void setBaseURI(URI baseURI) {
    	this.baseURI = baseURI;
    }

	public URI getBaseURI() {
		return baseURI;
	}


}
