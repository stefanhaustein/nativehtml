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
import org.kobjects.nativehtml.io.HtmlParser;

/**
 * Convenience Swing HTML component 
 */
public class HtmlComponent extends JComponent {
  private final HtmlParser htmlProcessor = 
      new HtmlParser(new SwingElementFactory(), new HtmlComponentRequestHandler(), null);
  
  private TreeSet<String> internalLinkPrefixSet = new TreeSet<>();
  
  public HtmlComponent() {
    this.setLayout(new BorderLayout());
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

  public void addInternalLinkPrefix(String s) {
    internalLinkPrefixSet.add(s);
  }
  
  class HtmlComponentRequestHandler extends SwingDefaultRequestHandler {
    @Override 
    public void openLink(URI url) {
      String s = url.toString();
      // TODO: Take advantage of order and start scan just before s.
      for (String prefix : internalLinkPrefixSet) {
        if (s.startsWith(prefix)) {
          // TODO: Async
          try {
            loadHtml(new InputStreamReader(url.toURL().openStream(), "utf-8"), url);
          } catch(IOException e) {
            throw new RuntimeException(e);
          }
          return;
        }
      }
      super.openLink(url);
    } 
  }
}
