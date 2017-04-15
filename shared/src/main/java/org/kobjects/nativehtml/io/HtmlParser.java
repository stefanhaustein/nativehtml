package org.kobjects.nativehtml.io;

import org.kobjects.nativehtml.css.CssStyleSheet;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.Platform;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.layout.WebSettings;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;



/**
 * Uses a HtmlParser to generate a widget tree that corresponds to the HTML code.
 *
 * Can be re-used, but is not thread safe.
 */
public class HtmlParser {
  private final HtmlNormalizer input;
  private final Platform elementFactory;
  private CssStyleSheet styleSheet;
  private Document document;
  private WebSettings webSettings;
  private RequestHandler requestHandler;

  public HtmlParser(Platform elementFactory, RequestHandler requestHandler, WebSettings webSettings) {
    this.elementFactory = elementFactory;
    this.requestHandler = requestHandler;
    this.webSettings = webSettings;
    try {
      this.input = new HtmlNormalizer();
    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses html from the given reader and returns the body or root element.
   */
  public Element parse(Reader reader, URI baseUri) {
    try {
      input.setInput(reader);
      input.next();

      document = new Document(elementFactory, requestHandler, webSettings, baseUri);
      styleSheet = CssStyleSheet.createDefault(16);
      
      Element result = document.createElement("body");
      parseComponentContent(result);
      if (result.getChildren().getLength() == 1) {
        result = result.getChildren().item(0);
        result.setParentElement(null);
      }
      document.setBody(result);

      System.out.println("applying stylesheet: " + styleSheet);

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
  private void parseTextContentToString(StringBuilder sb) throws IOException, XmlPullParserException {
    while (input.getEventType() != XmlPullParser.END_TAG) {
      switch(input.getEventType()) {
        case XmlPullParser.START_TAG:
          input.next();
          parseTextContentToString(sb);
          input.next();
          break;

        case XmlPullParser.TEXT:
          if (sb != null) {
            sb.append(input.getText());
          }
          input.next();
          break;

        default:
          throw new RuntimeException("Unexpected event: " + input.getPositionDescription());
      }
    }
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
    assert input.getEventType() == XmlPullParser.START_TAG;

    String elementName = input.getName();
    
    System.out.println(indent + "Entering " + elementName);
    indent += "  ";
    
    Element element = document.createElement(elementName);

    for (int i = 0; i < input.getAttributeCount(); i++) {
    	String attributeName = input.getAttributeName(i);
    	String attributeValue = input.getAttributeValue(i);
        element.setAttribute(attributeName, attributeValue);
    }

    input.next();
    
    switch(element.getElementContentType()) {
      case COMPONENTS:
        parseComponentContent(element);
        break;
      case DATA_ELEMENTS:
        parseDataContent(element);
        break;
      case TEXT_ONLY: {
        StringBuilder sb = new StringBuilder();
        parseTextContentToString(sb);
        String textContent = sb.toString();
        if ("style".equals(element.getLocalName())) {
            styleSheet.read(textContent, document.getBaseURI(), new int[0], null, null);
        }
        element.setTextContent(textContent);
        break;
      }
      default:
        parseTextContentToString(null);
        break;
    }

    assert input.getEventType() == XmlPullParser.END_TAG;
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

  
  private void parseDataContent(Element parent) throws IOException, XmlPullParserException {
      while (input.getEventType() != XmlPullParser.END_TAG 
          && input.getEventType() != XmlPullParser.END_DOCUMENT) {
        if (input.getEventType() == XmlPullParser.START_TAG) {
          parent.insertBefore(parseElement(), null);
        }
        input.next();
      }
  }

  private void processTextContent(Element parent) throws IOException, XmlPullParserException {
	  Element textComponent = document.createElement("text-component");
	  boolean preserveLeadingSpace = false;
	  parent.insertBefore(textComponent, null);
	  Element current = textComponent;

      if (current == null) {
          throw new RuntimeException();
      }

      loop:
	  while (true) {
		  switch(input.getEventType()) {
		  	case XmlPullParser.END_DOCUMENT:
		  		break loop;
			
		  	case XmlPullParser.END_TAG:
		  		if (current == textComponent) {
		  			break loop;
		  		}
                if (current.getParentElement() == null) {
                    throw new RuntimeException("null parent for " + current + current.getClass());
                }
		  		current = current.getParentElement();
		  		input.next();
		  		break;
		  		
		  	case XmlPullParser.START_TAG:
		  	    ElementType elementType = Document.getElementType(input.getName());
		  		if (elementType == ElementType.FORMATTED_TEXT ||
		  		    elementType == ElementType.INLINE_IMAGE) {
		  			if (input.getName().equals("br")) {
		  				preserveLeadingSpace = false;
		  			}
		  			Element child = document.createElement(input.getName());
		  			for (int i = 0; i < input.getAttributeCount(); i++) {
		  				child.setAttribute(input.getAttributeName(i), input.getAttributeValue(i));
		  			}
		  			current.insertBefore(child, null);
		  			current = child;
		  			input.next();
		  		} else if (current == textComponent) {
		  			break loop;
		  		} else {
		  			parent.insertBefore(parseElement(), null);
		  			Element newTextComponent = document.createElement("text-component");
		  			current = recreate(current, textComponent, newTextComponent);
		  			textComponent = newTextComponent;
		  			preserveLeadingSpace = false;
		  			parent.insertBefore(textComponent, null);
		  			input.next();
		  		}
		  		break;
		  
		  	case XmlPullParser.TEXT: 
			  StringBuilder sb = new StringBuilder();
			  do {
				  sb.append(normalizeText(input.getText(), preserveLeadingSpace));
				  preserveLeadingSpace = sb.length() > 0 && sb.charAt(sb.length() - 1) > ' ';
				  input.next();
			  } while (input.getEventType() == XmlPullParser.TEXT);
			  Element child = document.createElement("span");
			  child.setTextContent(sb.toString());
		  	  // AddText could auto-elevate
			  current.insertBefore(child, null);
			  break;

		  	default:
				input.next();
		  }
	  }
  }
  
  
  private void parseComponentContent(Element parent) throws IOException, XmlPullParserException {
    while (input.getEventType() != XmlPullParser.END_TAG && input.getEventType() != XmlPullParser.END_DOCUMENT) {
    	if ((input.getEventType() != XmlPullParser.START_TAG && input.getEventType() != XmlPullParser.TEXT) 
     		   || input.getEventType() == XmlPullParser.TEXT && input.getText().trim().isEmpty()) {
    		// Skippable stuff
     	   	input.next();
        } else if (input.getEventType() == XmlPullParser.START_TAG 
    		   && Document.getElementType(input.getName()) != ElementType.FORMATTED_TEXT) {
        	// Children
          
          if (Document.getElementType(input.getName()).equals(ElementType.SKIP)) {
            input.next();
            parseComponentContent(parent);
            input.next();
          } else {
            Element child = parseElement();
            if (child.getElementType() == ElementType.COMPONENT) {
              parent.insertBefore(child, null);
            } else if (child.getLocalName().equals("head") && document.getHead() == null) {
              document.setHead(child);
            }
            assert input.getEventType() == XmlPullParser.END_TAG;
            input.next();
          }
       } else {
    	   processTextContent(parent);
      }
    }
  }
}
