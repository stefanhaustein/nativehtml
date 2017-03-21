package org.kobjects.nativehtml.swing.text;

import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class HtmlEditorKitExt extends HTMLEditorKit {

  static final String BASE64_PREFIX = "data:image/png;base64,";
  static Dictionary<URL, Image> sharedImageCache = new Hashtable<URL, Image>();

  @Override
  public ViewFactory getViewFactory() {
    return new HtmlFactoryExt();
  }
  
  URL url;
  public HtmlEditorKitExt(URI uri) {
    try {
      this.url = uri.toURL();
    } catch(MalformedURLException e) {
      e.printStackTrace();
    }
  }
  
  public Document createDefaultDocument() {
    HTMLDocument result = (HTMLDocument) super.createDefaultDocument();
        result.setBase(url);
        result.putProperty("imageCache", sharedImageCache);
        return result;
  }
  
  static class Base64ImageView extends ImageView {
    String base64;
    
    public Base64ImageView(Element element) {
      super(element);
/*      Dictionary<URL, Image> cache = (Dictionary<URL, Image>) getDocument()
          .getProperty("imageCache");
      if (cache == null) {
        cache = 
        getDocument().putProperty("imageCache", cache);
      }
*/
      String src = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
      if (src.startsWith(BASE64_PREFIX)) {
        String base64 = src.substring(BASE64_PREFIX.length());
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
          BufferedImage newImage = ImageIO.read(bais);
          sharedImageCache.put(getImageURL(), newImage); 
        } catch (Throwable ex) {
          ex.printStackTrace();
        }
      }
    }


    @Override
    public URL getImageURL() {
      String src = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
      if (src.startsWith(BASE64_PREFIX)) {
        try {
          return new URL("file:///dev/nul/" + src.substring(BASE64_PREFIX.length()));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
      return super.getImageURL();
    }
  }
  

  static class HtmlFactoryExt extends HTMLFactory {
    public View create(Element element) {
      Object obj = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (obj instanceof HTML.Tag) {
        HTML.Tag tag = (HTML.Tag) obj;
        if (tag == HTML.Tag.IMG) {
          String src = (String) element.getAttributes().getAttribute(HTML.Attribute.SRC);
          if (src != null && src.startsWith(BASE64_PREFIX)) {
            return new Base64ImageView(element);
          }
        }
      }
      return super.create( element );
    }
  }
}
