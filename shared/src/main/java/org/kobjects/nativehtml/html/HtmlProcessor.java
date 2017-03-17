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
  private String parseTextContentToString() throws IOException, XmlPullParserException {
    StringBuilder sb = new StringBuilder();
    while (parser.getEventType() != XmlPullParser.END_TAG) {
      switch(parser.getEventType()) {
        case XmlPullParser.START_TAG:
          parser.next();
          sb.append(parseTextContentToString());
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

  String indent = "";
  
  private Element parseElement() throws IOException, XmlPullParserException {
    assert parser.getEventType() == XmlPullParser.START_TAG;

    String elementName = parser.getName();
    
    System.out.println(indent + "Entering " + elementName);
    indent += "  ";
    
    Element element = document.createElement(elementName);
    ElementType elementType = element.getElementType();

    for (int i = 0; i < parser.getAttributeCount(); i++) {
    	String attributeName = parser.getAttributeName(i);
    	String attributeValue = parser.getAttributeValue(i);
        element.setAttribute(attributeName, attributeValue);
    }

    parser.next();
    
    if (element.getContentType().contains(ElementType.TEXT_ONLY) || element.getContentType().isEmpty()) {
      String textContent = parseTextContentToString();
      if (!element.getContentType().isEmpty()) {
    	  element.setTextContent(textContent);
      }
    } else {
      parseChildren(element);
    }

    assert parser.getEventType() == XmlPullParser.END_TAG;
    indent = indent.substring(2);
    System.out.println(indent + "Leaving " + elementName);

    
    return element;
  }
  
  /**
   * Re-creates the given descendant of oldRoot as a new descendant of newRoot. Used when text elements are interrupted by 
   * components.
   */
  Element recreate(Element original, Element oldRoot, Element newRoot) {
	  Element parent = original.getParentElement() == oldRoot ? newRoot : recreate(original.getParentElement(), oldRoot, newRoot);
	  Element clone = document.createElement(original.getLocalName());
	  // TODO: copy attributes;
	  parent.insertBefore(clone, null);
	  return clone;
  }
  

  private void processTextContent(Element parent) throws IOException, XmlPullParserException {
	  Element textComponent = document.createElement("text-component");
	  boolean preserveLeadingSpace = false;
	  parent.insertBefore(textComponent, null);
	  Element current = textComponent;
	  
	  loop:
	  while (true) {
		  switch(parser.getEventType()) {
		  	case XmlPullParser.END_DOCUMENT:
		  		break loop;
			
		  	case XmlPullParser.END_TAG:
		  		if (current == textComponent) {
		  			break loop;
		  		} 
		  		current = current.getParentElement();
		  		parser.next();
		  		break;
		  		
		  	case XmlPullParser.START_TAG:
		  		if (Document.getElementType(parser.getName()) == ElementType.FORMATTED_TEXT) {
		  			Element child = document.createElement(parser.getName());
		  			for (int i = 0; i < parser.getAttributeCount(); i++) {
		  				child.setAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
		  			}
		  			current.insertBefore(child, null);
		  			current = child;
		  			parser.next();
		  		} else if (current == textComponent) {
		  			break loop;
		  		} else {
		  			parent.insertBefore(parseElement(), null);
		  			Element newTextComponent = document.createElement("text-component");
		  			current = recreate(current, textComponent, newTextComponent);
		  			textComponent = newTextComponent;
		  			preserveLeadingSpace = false;
		  			parent.insertBefore(textComponent, null);
		  			assert current != null;
		  			parser.next();
		  		}
		  		break;
		  
		  	case XmlPullParser.TEXT: 
			  StringBuilder sb = new StringBuilder();
			  do {
				  sb.append(normalizeText(parser.getText(), preserveLeadingSpace));
				  preserveLeadingSpace = sb.length() > 0 && sb.charAt(sb.length() - 1) > ' ';
				  parser.next();
			  } while (parser.getEventType() == XmlPullParser.TEXT);
			  Element child = document.createElement("span");
			  child.setTextContent(sb.toString());
		  	  // AddText could auto-elevate
			  current.insertBefore(child, null);
			  break;

		  	default:
				parser.next();
		  }
	  }
  }
  
  
  private void parseChildren(Element parent) throws IOException, XmlPullParserException {
    while (parser.getEventType() != XmlPullParser.END_TAG && parser.getEventType() != XmlPullParser.END_DOCUMENT) {
    	if ((parser.getEventType() != XmlPullParser.START_TAG && parser.getEventType() != XmlPullParser.TEXT) 
     		   || parser.getEventType() == XmlPullParser.TEXT && parser.getText().trim().isEmpty()) {
    		// Skippable stuff
     	   	parser.next();
        } else if (parser.getEventType() == XmlPullParser.START_TAG 
    		   && Document.getElementType(parser.getName()) != ElementType.FORMATTED_TEXT) {
        	// Children
    	   Element child = parseElement();
    	   parent.insertBefore(child, null);
           assert parser.getEventType() == XmlPullParser.END_TAG;
           parser.next();
       } else {
    	   processTextContent(parent);
      }
    }
  }
}
