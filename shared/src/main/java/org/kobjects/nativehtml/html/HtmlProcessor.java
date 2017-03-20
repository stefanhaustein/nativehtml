package org.kobjects.nativehtml.html;

import org.kobjects.nativehtml.css.CssStyleSheet;
import org.kobjects.nativehtml.dom.HtmlContentType;
import org.kobjects.nativehtml.dom.HtmlDocument;
import org.kobjects.nativehtml.dom.HtmlElement;
import org.kobjects.nativehtml.dom.HtmlElementFactory;
import org.kobjects.nativehtml.dom.HtmlElementType;
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
  private final HtmlElementFactory elementFactory;
  private CssStyleSheet styleSheet;
  private HtmlDocument document;

  public HtmlProcessor(HtmlElementFactory elementFactory) {
    this.elementFactory = elementFactory;
    try {
      this.parser = new HtmlParser();
    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    }
  }

  public HtmlElement parse(Reader reader) {
    try {
      parser.setInput(reader);
      parser.next();

      document = new HtmlDocument(elementFactory);
      styleSheet = CssStyleSheet.createDefault(16);
      
      // Skip insignificant
      while (parser.getEventType() != XmlPullParser.START_TAG) {
        parser.next();
      }

      HtmlElement result = parseElement();

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
  
  private HtmlElement parseElement() throws IOException, XmlPullParserException {
    assert parser.getEventType() == XmlPullParser.START_TAG;

    String elementName = parser.getName();
    
    System.out.println(indent + "Entering " + elementName);
    indent += "  ";
    
    HtmlElement element = document.createElement(elementName);

    for (int i = 0; i < parser.getAttributeCount(); i++) {
    	String attributeName = parser.getAttributeName(i);
    	String attributeValue = parser.getAttributeValue(i);
        element.setAttribute(attributeName, attributeValue);
    }

    parser.next();
    
    if (element.getElemnetContentType() == HtmlContentType.TEXT_ONLY || element.getElemnetContentType() == HtmlContentType.EMPTY) {
      String textContent = parseTextContentToString();
      if (element.getElemnetContentType() == HtmlContentType.TEXT_ONLY) {
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
  HtmlElement recreate(HtmlElement original, HtmlElement oldRoot, HtmlElement newRoot) {
	  HtmlElement parent = original.getParentElement() == oldRoot ? newRoot : recreate(original.getParentElement(), oldRoot, newRoot);
	  HtmlElement clone = document.createElement(original.getLocalName());
	  // TODO: copy attributes;
	  parent.insertBefore(clone, null);
	  return clone;
  }
  

  private void processTextContent(HtmlElement parent) throws IOException, XmlPullParserException {
	  HtmlElement textComponent = document.createElement("text-component");
	  boolean preserveLeadingSpace = false;
	  parent.insertBefore(textComponent, null);
	  HtmlElement current = textComponent;
	  
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
		  	    HtmlElementType elementType = HtmlDocument.getElementType(parser.getName());
		  		if (elementType == HtmlElementType.FORMATTED_TEXT ||
		  		    elementType == HtmlElementType.INLINE_IMAGE) {
		  			if (parser.getName().equals("br")) {
		  				preserveLeadingSpace = false;
		  			}
		  			HtmlElement child = document.createElement(parser.getName());
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
		  			HtmlElement newTextComponent = document.createElement("text-component");
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
			  HtmlElement child = document.createElement("span");
			  child.setTextContent(sb.toString());
		  	  // AddText could auto-elevate
			  current.insertBefore(child, null);
			  break;

		  	default:
				parser.next();
		  }
	  }
  }
  
  
  private void parseChildren(HtmlElement parent) throws IOException, XmlPullParserException {
    while (parser.getEventType() != XmlPullParser.END_TAG && parser.getEventType() != XmlPullParser.END_DOCUMENT) {
    	if ((parser.getEventType() != XmlPullParser.START_TAG && parser.getEventType() != XmlPullParser.TEXT) 
     		   || parser.getEventType() == XmlPullParser.TEXT && parser.getText().trim().isEmpty()) {
    		// Skippable stuff
     	   	parser.next();
        } else if (parser.getEventType() == XmlPullParser.START_TAG 
    		   && HtmlDocument.getElementType(parser.getName()) != HtmlElementType.FORMATTED_TEXT) {
        	// Children
    	   HtmlElement child = parseElement();
    	   parent.insertBefore(child, null);
           assert parser.getEventType() == XmlPullParser.END_TAG;
           parser.next();
       } else {
    	   processTextContent(parent);
      }
    }
  }
}
