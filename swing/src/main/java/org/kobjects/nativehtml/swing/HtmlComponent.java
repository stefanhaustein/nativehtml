package org.kobjects.nativehtml.swing;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.Platform;
import org.kobjects.nativehtml.io.DefaultRequestHandler;
import org.kobjects.nativehtml.io.HtmlParser;
import org.kobjects.nativehtml.io.InternalLinkHandler;

/**
 * Convenience Swing HTML component 
 */
public class HtmlComponent extends JComponent implements InternalLinkHandler {
  private final SwingPlatform platform = new SwingPlatform();
  private DefaultRequestHandler requestHandler = new DefaultRequestHandler(platform);
  private HtmlParser htmlProcessor = new HtmlParser(platform, requestHandler, null);
  
  
  public HtmlComponent() {
    this.setLayout(new BorderLayout());
    requestHandler.setInternalLinkHandler(this);
  }

  public void addInternalLinkPrefix(String s) {
    requestHandler.addInternalLinkPrefix(s);
  }

  public void loadHtml(URI url) {
    requestHandler.openInternalLink(url);
  }

  public void loadHtml(String html, URI baseUrl) {
    loadHtml(new StringReader(html), baseUrl);
  }
  
  public void loadHtml(Reader reader, URI baseUrl) {
    Element element = htmlProcessor.parse(reader, baseUrl);
    
    removeAll();
    if (element instanceof AbstractSwingComponentElement) {
      add((AbstractSwingComponentElement) element, BorderLayout.CENTER);
      ((AbstractSwingComponentElement) element).invalidate();
    }
    revalidate();
  }

  
}
