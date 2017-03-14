package org.kobjects.nativehtml.demo.swing;

import java.awt.BorderLayout;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementFactory;
import org.kobjects.nativehtml.html.HtmlProcessor;
import org.kobjects.nativehtml.swing.SwingElementFactory;
import org.kobjects.nativehtml.util.DebugDump;

public class SwingDemo {

	static final String CONTENT = "<div>"
			+ "<h2>Character Styles</h2>"
			+ "<p>HtmlLayout supports <b>bold</b>, <big>big</big>, <del>deleted</del>, <em>emphasized</em>, "
			+ "<i>italics</i>, <ins>inserted</ins>, <small>small</small>, <strong>strong</strong>, "
			+ "<sub>subscript</sub>, <sup>superscript</sup>, <tt>typewriter</tt> and <u>underlined</u> "
			+ "text using the corresponding html tags."
			+ "<p>All text attributes are also supported using "
			+ "<span style='color: red'>style attributes</span>."
			+ "<p>This is a <a href='http://heise.de'>clickable link</a>.</p>"
			+ "</div>";
	
	
    public static void main(String[] args) {
    	ElementFactory elementFactory = new SwingElementFactory();
    	HtmlProcessor processor = new HtmlProcessor(elementFactory);
    	
    	JComponent content = (JComponent) processor.parse(new StringReader(CONTENT));
    	
    	DebugDump.dump((Element) content, "");
    	
    	JFrame frame = new JFrame("NativeHtml");
    	frame.setLayout(new BorderLayout());
    	frame.getContentPane().add(content, BorderLayout.CENTER);
    	
    	frame.pack();
    	
    	frame.setVisible(true);

    }

}