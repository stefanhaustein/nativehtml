package org.kobjects.nativehtml.html;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class HtmlSerializer {

	public static void htmlEscape(String text, StringBuilder sb) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch(c) {
			case '<':
				sb.append("&lt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			default:
				sb.append(c);
			}
		}
	}
	
	
	public static String toString(Element element) {
		String name = element.getLocalName();
		StringBuilder sb = new StringBuilder();
		if (name.equals("#TEXT")) {
			htmlEscape(element.getTextContent(), sb);
		} else {
			sb.append('<').append(name).append('>');
			HtmlCollection children = element.getChildren();
			if (children == null) {
				System.out.println("ERROR: children null for '" + element + "'");
			} else {
				sb.append(HtmlSerializer.toString(element.getChildren()));
			}
			sb.append("</").append(name).append('>');
		}
		return sb.toString();
	}


	public static String toString(HtmlCollection list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.getLength(); i++) {
			sb.append(toString(list.item(i)));
		}
		return sb.toString();
	}
	
	
}
