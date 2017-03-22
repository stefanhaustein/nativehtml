package org.kobjects.nativehtml.swing;

import java.awt.Image;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Dictionary;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.io.HtmlSerializer;
import org.kobjects.nativehtml.io.RequestHandler;
import org.kobjects.nativehtml.layout.Layout.Directive;
import org.kobjects.nativehtml.util.HtmlCollectionImpl;

/**
 * Artificially inserted element -- can't have any box styling.
 */
public class SwingTextComponent extends JTextPane implements org.kobjects.nativehtml.layout.ComponentElement {
	private static final CssStyleDeclaration EMTPY_STYLE = new CssStyleDeclaration();
	private static final int HEIGHT_CORRECTION = -3;

	private boolean imagesRequested;
	private final Document document;
	private boolean dirty;
	private HtmlCollectionImpl children = new HtmlCollectionImpl();
	private CssStyleDeclaration computedStyle;
	private JEditorPane resizer;
	
	public SwingTextComponent(Document document)  {
	  //super("text/html");
		this.document = document;
		
		configureEditor(this);
		addHyperlinkListener(new HyperlinkListener() {
          
          @Override
          public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
              RequestHandler requestHandler = document.getRequestHandler();
              if (requestHandler != null) {
                try {
                  requestHandler.openLink(event.getURL().toURI());
                } catch (URISyntaxException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        });
	}
	
	
	
	@Override
	public String getLocalName() {
		return "text-container";
	}

	@Override
	public void setAttribute(String name, String value) {
		throw new RuntimeException("TextContainer doesn't support attributes");
	}

	@Override
	public String getAttribute(String name) {
		return null;
	}

	@Override
	public Element getParentElement() {
		return (Element) getParent();
	}

	@Override
	public ElementType getElementType() {
		return ElementType.COMPONENT;
	}

	@Override
	public void setParentElement(Element parent) {
	}

	@Override
	public HtmlCollection getChildren() {
		return children;
	}

	@Override
	public CssStyleDeclaration getStyle() {
		return EMTPY_STYLE;
	}

	@Override
	public CssStyleDeclaration getComputedStyle() {
		return computedStyle;
	}
	
	@Override
	public void setComputedStyle(CssStyleDeclaration computedStyle) {
		this.computedStyle = computedStyle;
	}

	@Override
	public String getTextContent() {
		return null;
	}

	@Override
	public void setTextContent(String textContent) {
		throw new RuntimeException("unsupported");
	}

	@Override
	public void insertBefore(Element newChild, Element referenceChild) {
		children.insertBefore(newChild, referenceChild);
		newChild.setParentElement(this);
		notifyContentChanged();
	}
	
	
	private void check() {
		if (!dirty) {
			return;
		}
		String htmlContent = serialize();
		System.out.println("Update: "+ htmlContent);
		setText(htmlContent); 
		imagesRequested = true;
		dirty = false;
	}


	  @Override
	  public int getIntrinsicContentBoxWidth(Directive directive, int parentContentBoxWidth) {
        check();
        
        // It's ok to use the outer size here because this element can't have borders or padding.
        int result = (directive == Directive.MINIMUM ? getMinimumSize() : getPreferredSize()).width;
        // System.out.println("TexComponent width (min=" + min + "):" + result);
        return result;
	  }

	  @Override
	  public int getIntrinsicContentBoxHeightForWidth(int contentBoxWidth, int parentContentBoxWidth) {
		check();
		if (contentBoxWidth == getWidth()) {
			return getPreferredSize().height;
		}
		
		String html = serialize();
		if (resizer == null) {
		  
	                
			resizer = new JEditorPane();
			configureEditor(resizer);
		} else {
			resizer.setText(html);
		}
		 
	 /*   View view = (View) resizer.getClientProperty(
	                javax.swing.plaf.basic.BasicHTML.propertyKey);
	 
        view.setSize(width, 0);
        float h = view.getPreferredSpan(View.Y_AXIS);
        System.out.println("TexComponent height (w=" + width + "):" + h);
        */
		
		resizer.setSize(contentBoxWidth, Short.MAX_VALUE);
		float h= resizer.getPreferredSize().height;
		return Math.round(h) + HEIGHT_CORRECTION;	
	}
	
	void configureEditor(JEditorPane editor) {
      final Dictionary<URL, Image> imageCache =
          (document.getRequestHandler() instanceof SwingDefaultRequestHandler) ?
              ((SwingDefaultRequestHandler) document.getRequestHandler()).imageCache : null;
              
      editor.setEditorKit(new HTMLEditorKit() {
        @Override
        public javax.swing.text.Document createDefaultDocument() {
          HTMLDocument result = (HTMLDocument) super.createDefaultDocument();
              try {
                result.setBase(document.getBaseURI().toURL());
              } catch (MalformedURLException e) {
                e.printStackTrace();
              }
              result.putProperty("imageCache", imageCache);
              return result;
        }
      });
      editor.setMargin(new Insets(0,0,0,0));
      editor.setOpaque(false);
      editor.setEditable(false);
	}
	  
	  
	@Override
	public void setBorderBoxBounds(int x, int y, int width, int height, int containingBoxWidth) {
		setBounds(x, y, width, height);
		check();
	}

	@Override
	public void moveRelative(int dx, int dy) {
		setBounds(getX() + dx, getY() + dy, getWidth(), getHeight());
	}

	@Override
	public ContentType getElementContentType() {
		return ContentType.FORMATTED_TEXT;
	}
	
	private String serialize() {
	  StringBuilder sb = new StringBuilder("<div");
	  CssEnum align = getComputedStyle().getEnum(CssProperty.TEXT_ALIGN);
	  if (align == CssEnum.RIGHT) {
	    sb.append(" align='right'>");
	  } else if (align == CssEnum.CENTER) {
	    sb.append(" align='center'>");
	  } else {
	    sb.append('>');
	  }
	  serializeInner(this, sb);
	  sb.append("</div>");
	  return sb.toString();
	}

  void notifyContentChanged() {
    if (!dirty) {
      dirty = true;
      invalidate();
    }
  }

  @Override
  public Document getOwnerDocument() {
    return document;
  }

  private void serializeInner(Element element, StringBuilder sb) {
    String name = element.getLocalName();
    if (name.equals("br")) {
        sb.append("<br>");
        return;
    }
    
    if (name.equals("img")) {
      String src = element.getAttribute("src");
      if (src != null && !src.isEmpty()) {
        if (!imagesRequested) {
          Document document = element.getOwnerDocument();
          document.getRequestHandler().requestImage(element, 
              document.getBaseURI().resolve(src));
        }
        src = SwingDefaultRequestHandler.fakeDataUrl(src);
      } else {
        src = "#";
      }
      sb.append("<img src=\"");
      HtmlSerializer.htmlEscape(src, sb);
      sb.append("\">");
      return;
    }
    
    if (name.equals("a") && element.getAttribute("href") != null) {
        name = "a href=\"" + HtmlSerializer.htmlEscape(element.getAttribute("href")) + "\"";
    } else {
        name = "span";
    }
    sb.append("<").append(name).append(" style=\"");
    sb.append(element.getComputedStyle());
    sb.append("\">");
    
    HtmlCollection children = element.getChildren();
    if (children.getLength() == 0) {
        HtmlSerializer.htmlEscape(element.getTextContent(), sb);
    } else {
        for (int i = 0; i < children.getLength(); i++) {
            serializeInner(children.item(i), sb);
        }
    }
    sb.append("</").append(name).append(">");
}

}
