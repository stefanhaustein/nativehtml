package org.kobjects.nativehtml.demo.swing;

import java.awt.BorderLayout;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.html.HtmlProcessor;
import org.kobjects.nativehtml.html.HtmlSerializer;
import org.kobjects.nativehtml.swing.SwingDocument;

public class SwingDemo {

	static final String CONTENT = "<div>Hello World 1<div>Hello World</div>Hello World 2</div>";
	
	
    public static void main(String[] args) {
    	SwingDocument document = new SwingDocument();
    	HtmlProcessor processor = new HtmlProcessor(document);
    	
    	JComponent content = (JComponent) processor.parse(new StringReader(CONTENT));
    	
    	System.out.println(HtmlSerializer.toString((Element) content));
    	
    	JFrame frame = new JFrame("NativeHtml");
    	frame.setLayout(new BorderLayout());
    	frame.getContentPane().add(content, BorderLayout.CENTER);
    	
    	frame.pack();
    	
    	frame.setVisible(true);

    }

}