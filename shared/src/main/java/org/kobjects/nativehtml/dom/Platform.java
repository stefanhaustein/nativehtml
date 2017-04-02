package org.kobjects.nativehtml.dom;

import java.net.URI;

public interface Platform {
	Element createElement(Document document, ElementType elementType, String name);
	void openInBrowser(URI url);
    float getPixelPerDp();
}
