package org.kobjects.nativehtml.io;

import java.io.Reader;
import java.net.URI;

public interface InternalLinkHandler {

  void loadHtml(Reader reader, URI url);

}
