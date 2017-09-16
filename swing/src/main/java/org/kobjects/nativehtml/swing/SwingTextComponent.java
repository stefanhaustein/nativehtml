package org.kobjects.nativehtml.swing;

import java.awt.Image;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Dictionary;

import java.util.HashMap;
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

	public SwingTextComponent(final Document document)  {
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
		children.insertBefore(this, newChild, referenceChild);
		notifyContentChanged();
	}
	
	
	private void check() {
		if (!dirty) {
			return;
		}
		String htmlContent = serialize();
		setText(htmlContent);
		imagesRequested = true;
		dirty = false;
	}


	  @Override
	  public float getIntrinsicContentBoxWidth(Directive directive, float parentContentBoxWidth) {
        check();
        
        // It's ok to use the outer size here because this element can't have borders or padding.
        int result = (directive == Directive.MINIMUM ? getMinimumSize() : getPreferredSize()).width;
        // System.out.println("TexComponent width (min=" + min + "):" + result);
        return result;
	  }

	  @Override
	  public float getIntrinsicContentBoxHeightForWidth(float contentBoxWidth, float parentContentBoxWidth) {
		check();
		float scale = document.getSettings().getScale();
		String html = serialize();
		if (resizer == null) {
			resizer = new JEditorPane();
			configureEditor(resizer);
		}
		resizer.setText(html);

		 
	 /*   View view = (View) resizer.getClientProperty(
	                javax.swing.plaf.basic.BasicHTML.propertyKey);
	 
        view.setSize(width, 0);
        float h = view.getPreferredSpan(View.Y_AXIS);
        System.out.println("TexComponent height (w=" + width + "):" + h);
        */
		
		resizer.setSize(Math.round(contentBoxWidth * scale), Short.MAX_VALUE);
		float h= resizer.getPreferredSize().height;
		return Math.round((h + HEIGHT_CORRECTION) / scale) ;	
	}
	
	void configureEditor(JEditorPane editor) {
      final Dictionary<URL, Image> imageCache = ((SwingPlatform) document.getPlatform()).imageCache;
              
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
	public void setBorderBoxBounds(float x, float y, float width, float height, float containingBoxWidth) {
	  float scale = document.getSettings().getScale();
		setBounds(Math.round(x * scale), Math.round(y * scale), Math.round(width * scale), Math.round(height * scale));
		check();
	}

	@Override
	public void moveRelative(float dx, float dy) {
	  float scale = document.getSettings().getScale();
		setLocation(getX() + Math.round(dx * scale), getY() + Math.round(dy * scale));
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
          ((SwingPlatform) document.getPlatform()).getImage(element, document.getBaseURI().resolve(src));
        }
        src = SwingPlatform.fakeDataUrl(src);
      } else {
        src = "#";
      }
      sb.append("<img src=\"");
      HtmlSerializer.htmlEscape(src, sb);
      sb.append("\">");
      return;
    }

	HtmlCollection children = element.getChildren();
    String textContent = children.getLength() == 0 ? element.getTextContent() : "";
    boolean writeTag = textContent != null && !"".equals(textContent);
    if (name.equals("a") && element.getAttribute("href") != null) {
        name = "a href=\"" + HtmlSerializer.htmlEscape(element.getAttribute("href")) + "\"";
        writeTag = true;
    } else {
        name = "span";
    }

	if (writeTag) {
		sb.append("<").append(name).append(" style=\"");
		// px seems to scale up
		sb.append("font-size:").append(Math.round(element.getComputedStyle().getPx(CssProperty.FONT_SIZE, 0))).append(";");
		sb.append("line-height:").append(Math.round(element.getComputedStyle().getPx(CssProperty.LINE_HEIGHT, 0))).append(";");

		String color = "00000" + Integer.toHexString(element.getComputedStyle().getColor(CssProperty.COLOR) & 0x0ffffff);
		color = color.substring(color.length() - 6);

		sb.append("color:#").append(color).append(';');
		//		sb.append(element.getComputedStyle());
		sb.append("\">");
	}
    if (children.getLength() == 0) {
        HtmlSerializer.htmlEscape(element.getTextContent(), sb);
    } else {
        for (int i = 0; i < children.getLength(); i++) {
            serializeInner(children.item(i), sb);
        }
    }
    if (writeTag) {
		sb.append("</").append(element.getLocalName()).append(">");
	}
}

}
