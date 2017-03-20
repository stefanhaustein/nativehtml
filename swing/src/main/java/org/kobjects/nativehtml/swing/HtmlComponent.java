package org.kobjects.nativehtml.swing;

import java.awt.BorderLayout;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

import javax.swing.JComponent;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.io.HtmlProcessor;

/**
 * Convenience Swing HTML component 
 */
public class HtmlComponent extends JComponent {
  HtmlProcessor htmlProcessor = new HtmlProcessor(new SwingElementFactory(), null, null);
  
  
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
    }
  }
}
