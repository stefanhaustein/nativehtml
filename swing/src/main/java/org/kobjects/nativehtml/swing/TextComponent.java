package org.kobjects.nativehtml.swing;

import javax.swing.JLabel;
import javax.swing.text.View;

import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.html.HtmlSerializer;
import org.kobjects.nativehtml.util.HtmlCollectionImpl;

/**
 * Artificially inserted element -- can't have any box styling.
 */
public class TextComponent extends JLabel implements org.kobjects.nativehtml.html.HtmlComponent {
	private static final CssStyleDeclaration EMTPY_STYLE = new CssStyleDeclaration();

	private static void serialize(Element element, StringBuilder sb) {
		
		String name = element.getLocalName().equals("a") ? "a href=\"#\"" : "span";
			
		sb.append("<").append(name).append(" style=\"");
		sb.append(element.getComputedStyle());
		sb.append("\">");
		
		HtmlCollection children = element.getChildren();
		if (children.getLength() == 0) {
			HtmlSerializer.htmlEscape(element.getTextContent(), sb);
		} else {
			for (int i = 0; i < children.getLength(); i++) {
				serialize(children.item(i), sb);
			}
		}
		sb.append("</").append(name).append(">");
	}
	
	private final Document document;
	private boolean dirty;
	private HtmlCollectionImpl children = new HtmlCollectionImpl();
	private CssStyleDeclaration computedStyle;
	
	public TextComponent(Document document) {
		this.document = document;
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
		return ElementType.TEXT_COMPONENT;
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
		dirty = true;
	}
	
	
	private void check() {
		if (!dirty) {
			return;
		}
		StringBuilder sb = new StringBuilder("<HTML>");
		serialize(this, sb);
		sb.append("</HTML>");
		String htmlContent = sb.toString();
		System.out.println("Update: "+ htmlContent);
		setText(htmlContent); 
		dirty = false;
	}

	public int getIntrinsicMinimumBorderBoxWidth() {
		check();
		return getMinimumSize().width;
	}
	
	public int getIntrinsicBorderBoxHeightForWidth(int width) {
		check();
		if (width == getWidth()) {
			return getPreferredSize().height;
		}
		
		JLabel resizer = new JLabel();
		resizer.setText(getText());
		 
	    View view = (View) resizer.getClientProperty(
	                javax.swing.plaf.basic.BasicHTML.propertyKey);
	 
        view.setSize(width, 0);
        float h = view.getPreferredSpan(View.Y_AXIS);
		return Math.round(h);
		
	}
	
	
	@Override
	public void setBorderBoxBoundsDp(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
		check();
	}

	
	
}
