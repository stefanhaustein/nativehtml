package org.kobjects.nativehtml.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.LinkedHashMap;

import org.kobjects.nativehtml.util.HtmlElementImpl;

public class HtmlDocument {
	
    private static final LinkedHashMap<String, HtmlElementType> ELEMENT_TYPES = new LinkedHashMap<>();
    private static final LinkedHashMap<String, HtmlContentType> CONTENT_TYPES = new LinkedHashMap<>();
    
    static void add(String name, HtmlElementType elementType, HtmlContentType contentType) {
    	ELEMENT_TYPES.put(name, elementType);
    	CONTENT_TYPES.put(name, contentType);
    }
    
    static {
        add("a", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("b", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("big", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("br", HtmlElementType.FORMATTED_TEXT, HtmlContentType.EMPTY);
        add("del", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("em", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("font", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("head", HtmlElementType.DATA, HtmlContentType.DATA_ELEMENTS);
        add("html", HtmlElementType.SKIP, HtmlContentType.MIXED);
        add("i", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("img", HtmlElementType.INLINE_IMAGE, HtmlContentType.EMPTY);  // Might be an image (=LEAF_COMPONENT), too; will get adjusted
        add("input", HtmlElementType.COMPONENT, HtmlContentType.EMPTY);
        add("ins", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("link", HtmlElementType.DATA, HtmlContentType.EMPTY);
        add("meta", HtmlElementType.DATA, HtmlContentType.EMPTY);
        add("option", HtmlElementType.DATA, HtmlContentType.TEXT_ONLY);
        add("script", HtmlElementType.DATA, HtmlContentType.TEXT_ONLY);
        add("select", HtmlElementType.COMPONENT, HtmlContentType.DATA_ELEMENTS);
        add("small", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("span", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("strike", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("strong", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("style", HtmlElementType.DATA, HtmlContentType.TEXT_ONLY);
        add("sub", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("sup", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("tbody", HtmlElementType.SKIP, HtmlContentType.COMPONENTS);
        add("text-component", HtmlElementType.COMPONENT, HtmlContentType.FORMATTED_TEXT);
        add("thead", HtmlElementType.SKIP, HtmlContentType.COMPONENTS);
        add("title", HtmlElementType.DATA, HtmlContentType.TEXT_ONLY);
        add("tt", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
        add("u", HtmlElementType.FORMATTED_TEXT, HtmlContentType.FORMATTED_TEXT);
    }

    public static HtmlElementType getElementType(String name) {
        HtmlElementType result = ELEMENT_TYPES.get(name);
        return result == null ? HtmlElementType.COMPONENT : result;
    }

    private static HtmlContentType getContentType(String name) {
        HtmlContentType result = CONTENT_TYPES.get(name);
        return result == null ? HtmlContentType.COMPONENTS : result;
    }

	private URI baseURI;
	private final HtmlElementFactory elementFactory;

	public HtmlDocument(HtmlElementFactory elementFactory) {
		this.elementFactory = elementFactory;
		try {
			baseURI = new URI("file:///");
		} catch (URISyntaxException e) {
			throw new RuntimeException();
		}
	}
    
    public HtmlElement createElement(String name) {
    	HtmlElementType elementType = getElementType(name);
        HtmlElement result = elementFactory.createElement(this, elementType, name);
    	HtmlContentType contentType = getContentType(name);
        return result == null ? new HtmlElementImpl(this, name, elementType, contentType) : result;
    }

    public void setBaseURI(URI baseURI) {
    	this.baseURI = baseURI;
    }

	public URI getBaseURI() {
		return baseURI;
	}


}
