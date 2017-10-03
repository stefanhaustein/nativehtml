package org.kobjects.nativehtml.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.kobjects.nativehtml.dom.Platform;

public class DefaultRequestHandler implements RequestHandler {

  private final Platform platform;
  private TreeMap<String,String> internalLinkPrefixMap = new TreeMap<>();
  private InternalLinkHandler internalLinkHandler;
  
  public DefaultRequestHandler(Platform platform) {
    this.platform = platform;
  }
  
  public void setInternalLinkHandler(InternalLinkHandler internalLinkHandler) {
    this.internalLinkHandler = internalLinkHandler;
  }
  
  @Override
  public void openLink(URI url) {
    if (internalLinkHandler != null) {
      String s = url.toString();
      // TODO: Take advantage of order and start scan just before s.
      for (Map.Entry<String,String> entry : internalLinkPrefixMap.entrySet()) {
        if (s.startsWith(entry.getKey())) {
          openInternalLink(URI.create(entry.getValue() + s.substring(entry.getKey().length())));
          return;
        }
      }
    }
    platform.openInBrowser(url);
  }

  public void openInternalLink(URI url) {
    // TODO: Async
    try {
      internalLinkHandler.loadHtml(new InputStreamReader(url.toURL().openStream(), "utf-8"), url);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void addInternalLinkPrefix(String prefix) {
    internalLinkPrefixMap.put(prefix, prefix);
  }

  public void addInternalLinkPrefix(String prefix, String replacement) {
    internalLinkPrefixMap.put(prefix, replacement);
  }

}
