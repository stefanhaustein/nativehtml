package org.kobjects.nativehtml.swing;

import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.text.Element;

public class HtmlEditorKitExt extends HTMLEditorKit {

  static final String BASE64_PREFIX = "data:image/png;base64,";

  @Override
  public ViewFactory getViewFactory() {
    return new HtmlFactoryExt();
  }
  
  
  static class ImageViewExt extends ImageView {
    String base64;
    
    public ImageViewExt(Element element) {
      super(element);
      Dictionary<URL, Image> cache = (Dictionary<URL, Image>) getDocument()
          .getProperty("imageCache");
      if (cache == null) {
        cache = new Hashtable<URL, Image>();
        getDocument().putProperty("imageCache", cache);
      }

      URL src = getImageURL();
      
      if (base64 != null) {
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
          BufferedImage newImage = ImageIO.read(bais);
          cache.put(src, newImage); 
        } catch (Throwable ex) {
        }
      }
    }


    @Override
    public URL getImageURL() {
      String src = (String) getElement().getAttributes()
            .getAttribute(HTML.Attribute.SRC);
      if (src.startsWith(BASE64_PREFIX)) {
        base64 = src.substring(BASE64_PREFIX.length());
        try {
          return new URL("file:///dev/nul/" + base64);
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
       // return HtmlEditorKitExt.class.getProtectionDomain()
         //       .getCodeSource().getLocation();

      }
      return super.getImageURL();
    }
  }
  

  static class HtmlFactoryExt extends HTMLFactory {
    public View create(Element element) {
      Object obj = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (obj instanceof HTML.Tag) {
        HTML.Tag tag = (HTML.Tag) obj;
        if (tag == HTML.Tag.IMG)
          return new ImageViewExt(element);
        }
        return super.create( element );
      }
  }
  
}
