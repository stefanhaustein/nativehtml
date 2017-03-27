package org.kobjects.nativehtml.dom;

import java.net.URI;

public interface Platform {

	public Element createElement(Document document, ElementType elementType, String name);
	
	
	public void openInBrowser(URI url);
}
