package org.kobjects.nativehtml.html;

import org.kobjects.nativehtml.css.CssStyleSheet;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementFactory;
import org.kobjects.nativehtml.dom.ElementType;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;


/**
 * Uses a HtmlParser to generate a widget tree that corresponds to the HTML code.
 *
 * Can be re-used, but is not thread safe.
 */
public class HtmlProcessor {
  private final HtmlParser parser;
  private final ElementFactory elementFactory;
  private CssStyleSheet styleSheet;
  private Document document;

  public HtmlProcessor(ElementFactory elementFactory) {
    this.elementFactory = elementFactory;
    try {
      this.parser = new HtmlParser();
    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    }
  }

  public Element parse(Reader reader) {
    try {
      parser.setInput(reader);
      parser.next();

      document = new Document(elementFactory);
      styleSheet = CssStyleSheet.createDefault(16);
      
      // Skip insignificant
      while (parser.getEventType() != XmlPullParser.START_TAG) {
        parser.next();
      }

      Element result = parseElement();

      // Skip insignificant
      while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
        parser.next();
      }
      
      styleSheet.apply(result, document.getBaseURI());

      return result;

    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parse the text content of an element.
   * Precondition: behind the opening tag
   * Postcondition: on the closing tag
   */
  private String parseTextContent() throws IOException, XmlPullParserException {
    StringBuilder sb = new StringBuilder();
    while (parser.getEventType() != XmlPullParser.END_TAG) {
      switch(parser.getEventType()) {
        case XmlPullParser.START_TAG:
          parser.next();
          sb.append(parseTextContent());
          parser.next();
          break;

        case XmlPullParser.TEXT:
          sb.append(parser.getText());
          parser.next();
          break;

        default:
          throw new RuntimeException("Unexpected event: " + parser.getPositionDescription());
      }
    }
    return sb.toString();
  }


  private String normalizeText(String s, boolean preserveLeadingSpace) {
    boolean spaceSeen = !preserveLeadingSpace;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c <= ' ') {
        if (!spaceSeen) {
          sb.append(' ');
          spaceSeen = true;
        }
      } else {
        sb.append(c);
        spaceSeen = false;
      }
    }
    return sb.toString();
  }

  private Element parseElement() throws IOException, XmlPullParserException {
    assert parser.getEventType() == XmlPullParser.START_TAG;

    String elementName = parser.getName();
    Element element = document.createElement(elementName);
    ElementType elementType = element.getElementType();

    for (int i = 0; i < parser.getAttributeCount(); i++) {
      element.setAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
    }

    parser.next();
    
    if (elementType == ElementType.LEAF_COMPONENT || elementType == ElementType.TEXT_DATA
            || elementType == ElementType.SKIP) {
      String textContent = parseTextContent();
      element.setTextContent(textContent);
    } else {
      parseChildren(element);
    }
    return element;
  }

  private void parseChildren(Element parent) throws IOException, XmlPullParserException {
    Element pendingTextComponent = null;

    while (parser.getEventType() != XmlPullParser.END_TAG) {
      Element child = null;

      switch (parser.getEventType()) {

        case XmlPullParser.START_TAG:
          child = parseElement();
          parser.next();
          break;

        case XmlPullParser.TEXT: {
          StringBuilder sb = new StringBuilder();
          sb.append(normalizeText(parser.getText(), false));
          /*
          boolean textContainer = (container instanceof Hv2DomElement) &&
                  ((Hv2DomElement) container).componentType == ElementType.LEAF_TEXT;
          boolean preceedingText = container.getLastChild() != null && (container.getLastChild() instanceof Text ||
                  (container.getLastChild() instanceof Hv2DomElement && ((Hv2DomElement) container.getLastChild()).componentType ==
                          ElementType.LEAF_TEXT));
          boolean preceedingBr = container.getLastChild() instanceof Hv2DomElement && ((Hv2DomElement) container.getLastChild()).getLocalName().equals("br");

          String text = normalizeText(parser.getText(), !preceedingBr && (textContainer || preceedingText));
          if (text.length() > 0) {
            container.appendChild(document.createTextNode(text));
          }*/

          parser.next();
          while (parser.getEventType() == XmlPullParser.TEXT
                  || (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("br"))) {
            if (parser.getEventType() == XmlPullParser.TEXT) {
              sb.append(normalizeText(parser.getText(), false));
            } else {
              parseTextContent();
              sb.append("\n");
            }
            parser.next();
          }

          if (sb.length() > 0) {
            child = document.createElement("span");
            child.setTextContent(sb.toString());
          }

          break;
        }

        default:
          throw new RuntimeException("Unexpected token: " + parser.getPositionDescription());
      }

      if (child == null) {
        continue;
      }

      if (child.getElementType() == ElementType.TEXT
    		  && parent.getElementType() != ElementType.TEXT && parent.getElementType() != ElementType.TEXT_COMPONENT) {
        if (pendingTextComponent == null) {
          pendingTextComponent = document.createElement("text-component");
          parent.insertBefore(pendingTextComponent, null);
        }
        pendingTextComponent.insertBefore(child, null);
      } else {
        pendingTextComponent = null;
        parent.insertBefore(child, null);
      }
    }
  }
}
