package org.kobjects.nativehtml.swing;

import javax.swing.JLabel;
import javax.swing.text.View;

import org.kobjects.nativehtml.dom.CSSStyleDeclaration;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.HTMLCollection;
import org.kobjects.nativehtml.dom.HTMLCollectionImpl;
import org.kobjects.nativehtml.html.HtmlSerializer;

public class TextComponent extends JLabel implements org.kobjects.nativehtml.dom.Component {

	HTMLCollectionImpl children = new HTMLCollectionImpl();
	
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
	public HTMLCollection getChildren() {
		return children;
	}

	@Override
	public CSSStyleDeclaration getStyle() {
		return null;
	}

	@Override
	public CSSStyleDeclaration getComputedStyle() {
		return null;
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
		update();
	}

	private void update() {
		String htmlContent = "<HTML>" + HtmlSerializer.toString(children) + "</HTML>";
		System.out.println("Notify: " + htmlContent);
		setText(htmlContent); 
	
	}

	public int getIntrinsicMinimumWidth() {
		return getMinimumSize().width;
	}
	
	public int getIntrinsicHeightForWidth(int width) {
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
	public void setBorderBoxBounds(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
	}

	
	
}
