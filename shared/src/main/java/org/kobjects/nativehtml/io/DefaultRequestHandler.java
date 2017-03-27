package org.kobjects.nativehtml.io;

import java.net.URI;

import org.kobjects.nativehtml.dom.Platform;

public class DefaultRequestHandler implements RequestHandler {

  private final Platform platform;
  
  public DefaultRequestHandler(Platform platform) {
    this.platform = platform;
  }
  
  @Override
  public void openLink(URI uri) {
    platform.openInBrowser(uri);
  }


}
