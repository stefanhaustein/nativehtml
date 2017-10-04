package org.kobjects.nativehtml.dom;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

public interface Platform {
	Element createElement(Document document, ElementType elementType, String name);
	void openInBrowser(URI url);
	InputStream openInputStream(URI url) throws IOException;
    float getPixelPerDp();
}
