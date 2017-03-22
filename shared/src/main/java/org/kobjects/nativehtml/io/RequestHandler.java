package org.kobjects.nativehtml.io;

import java.net.URI;

import org.kobjects.nativehtml.dom.Element;

public interface RequestHandler {
  void openLink(URI uri);

  void requestImage(Element element, URI url);
}
