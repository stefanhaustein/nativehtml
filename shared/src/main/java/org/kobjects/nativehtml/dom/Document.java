package org.kobjects.nativehtml.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import org.kobjects.nativehtml.io.RequestHandler;
import org.kobjects.nativehtml.layout.WebSettings;
import org.kobjects.nativehtml.util.ElementImpl;

public class Document {
    private static final LinkedHashMap<String, ElementType> ELEMENT_TYPES = new LinkedHashMap<>();
    private static final LinkedHashMap<String, ContentType> CONTENT_TYPES = new LinkedHashMap<>();
    
    static void add(String name, ElementType elementType, ContentType contentType) {
    	ELEMENT_TYPES.put(name, elementType);
    	CONTENT_TYPES.put(name, contentType);
    }
    
    static {
        add("a", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("b", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("big", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("br", ElementType.FORMATTED_TEXT, ContentType.EMPTY);
        add("del", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("em", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("font", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("head", ElementType.DATA, ContentType.DATA_ELEMENTS);
        add("html", ElementType.SKIP, ContentType.COMPONENTS);  // head gets special handling
        add("i", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("img", ElementType.INLINE_IMAGE, ContentType.EMPTY);  // Might be an image (=LEAF_COMPONENT), too; will get adjusted
        add("input", ElementType.COMPONENT, ContentType.EMPTY);
        add("ins", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("link", ElementType.DATA, ContentType.EMPTY);
        add("meta", ElementType.DATA, ContentType.EMPTY);
        add("option", ElementType.DATA, ContentType.TEXT_ONLY);
        add("script", ElementType.DATA, ContentType.TEXT_ONLY);
        add("select", ElementType.COMPONENT, ContentType.DATA_ELEMENTS);
        add("small", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("span", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("strike", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("strong", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("style", ElementType.DATA, ContentType.TEXT_ONLY);
        add("sub", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("sup", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("tbody", ElementType.SKIP, ContentType.COMPONENTS);
        add("text-component", ElementType.COMPONENT, ContentType.FORMATTED_TEXT);
        add("thead", ElementType.SKIP, ContentType.COMPONENTS);
        add("title", ElementType.DATA, ContentType.TEXT_ONLY);
        add("tt", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
        add("u", ElementType.FORMATTED_TEXT, ContentType.FORMATTED_TEXT);
    }

    public static ElementType getElementType(String name) {
        ElementType result = ELEMENT_TYPES.get(name);
        return result == null ? ElementType.COMPONENT : result;
    }

    private static ContentType getContentType(String name) {
        ContentType result = CONTENT_TYPES.get(name);
        return result == null ? ContentType.COMPONENTS : result;
    }

	private final Platform platform;
    private final WebSettings webSettings;
    private RequestHandler requestHandler;
    private URI url;
    private Element head;
    private Element body;

	public Document(Platform elementFactory, RequestHandler requestHandler, WebSettings webSettings, URI uri) {
		this.platform = elementFactory;
		this.requestHandler = requestHandler;
		if (webSettings == null) {
		    this.webSettings = new WebSettings();
		    this.webSettings.setScale(platform.getPixelPerDp());
        } else {
            this.webSettings = webSettings;
        }
		this.url = uri;
	}
    
    public Element createElement(String name) {
    	ElementType elementType = getElementType(name);
        Element result = platform.createElement(this, elementType, name);
    	ContentType contentType = getContentType(name);
        return result == null ? new ElementImpl(this, name, elementType, contentType) : result;
    }


	public URI getUrl() {
		return url;
	}

	public URI resolveUrl(String url) {
	    if (this.url.isOpaque()) {
            try {
                URI uri = new URI(url);
                if (uri.isAbsolute()) {
                    return uri;
                }
                String s = this.url.toString();
                int cut;
                if (url.startsWith("#")) {
                    cut = s.indexOf('#');
                    if (cut == -1) {
                        cut = s.length();
                    }
                } else {
                    cut = s.lastIndexOf('/') + 1;
                }
                return new URI(s.substring(0, cut) + url);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return this.url.resolve(url);
    }

  public RequestHandler getRequestHandler() {
    return requestHandler;
  }

  public void setHead(Element head) {
    this.head = head;
  }

  public void setBody(Element body) {
    this.body = body;
  }
  
  public Element getBody() {
    return body;
  }

  public Element getHead() {
    return head;
  }

  public Platform getPlatform() {
    return platform;
  }
  
  public WebSettings getSettings() {
    return webSettings;
  }
}
