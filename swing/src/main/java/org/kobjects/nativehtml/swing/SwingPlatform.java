package org.kobjects.nativehtml.swing;

import org.kobjects.nativehtml.dom.Platform;
import org.kobjects.nativehtml.util.ElementImpl;

import java.awt.Desktop;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;

public class SwingPlatform implements Platform {
  
  static String fakeDataUrl(String url) {
    if (url.startsWith("data:")) {
      int cut = url.indexOf(',') + 1;
      return "http://240.0.0.0/base64hash" + url.substring(cut).hashCode();
    }
    return url;
  }
  
  Dictionary<URL, Image> imageCache = new Hashtable<URL, Image>();

	@Override
	public Element createElement(Document document, ElementType elementType, String elementName) {
		switch (elementType) {
			
		case COMPONENT:
			if (elementName.equals("select")) {
				return new SwingHtmlSelectElement(document, elementName);
			} 
			if (elementName.equals("input")) {
				return new SwingHtmlInputElement(document, elementName);
			}
			if (elementName.equals("text-component")) {
				return new SwingTextComponent(document);
			}
			return new SwingComponentContainerElement(document, elementName);
			
		default:
			return null;
		}
	}
	
	
	public Image getImage(Element element, URI uri) {
	  try {
	      String s = uri.toString();
	      boolean isDataUrl = s.startsWith("data:");
	      final URL url = new URL(fakeDataUrl(s));
	      Image cached = imageCache.get(url);
	      if (cached != null) {
	        return cached;
	      } 
	      if (isDataUrl) {
	        String base64 = s.substring(s.indexOf(',') + 1);
	        byte[] data = Base64.getDecoder().decode(base64);
	        Image image = ImageIO.read(new ByteArrayInputStream(data));
	        imageCache.put(url, image);
	        return image;
	      } else {      
	        new Thread(new Runnable() {
	          @Override
	          public void run() {
	            try {
	              Image image = ImageIO.read(uri.toURL().openStream());
	              imageCache.put(url, image);
	              setImage(element, url, image);
	            } catch (IOException e) {
	              throw new RuntimeException(e);
	            }
	          }
	        }).start();
	      }
	    } catch (IOException e) {
	      throw new RuntimeException(e);
	    }
	  return null;
	}
	
	

	  void setImage(Element element, URL url, Image image) {
	    while (element instanceof ElementImpl) {
	      element = element.getParentElement();
	    }
	    if (element instanceof SwingTextComponent) {
	      ((SwingTextComponent) element).notifyContentChanged();
	    }
	  }
	
	  @Override
	  public void openInBrowser(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	          desktop.browse(uri);
	        } catch (IOException e) {
	        }
	    }
	  }

}