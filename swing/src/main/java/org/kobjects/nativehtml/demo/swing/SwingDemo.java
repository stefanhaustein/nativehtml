package org.kobjects.nativehtml.demo.swing;

import java.awt.BorderLayout;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementFactory;
import org.kobjects.nativehtml.html.HtmlProcessor;
import org.kobjects.nativehtml.swing.SwingElementFactory;
import org.kobjects.nativehtml.util.DebugDump;

public class SwingDemo {

	
    public static void main(String[] args) throws UnsupportedEncodingException {
    	ElementFactory elementFactory = new SwingElementFactory();
    	HtmlProcessor processor = new HtmlProcessor(elementFactory);
    	
    	InputStream is = SwingDemo.class.getResourceAsStream("snippet.xml");
    	Reader reader = new InputStreamReader(is, "utf-8");
    	
    	JComponent content = (JComponent) processor.parse(reader);
    	
    	DebugDump.dump((Element) content, "");
    	
    	JFrame frame = new JFrame("NativeHtml");
    	frame.setLayout(new BorderLayout());
    	frame.getContentPane().add(content, BorderLayout.CENTER);
    	
    	frame.pack();
    	
    	frame.setVisible(true);

    }

}