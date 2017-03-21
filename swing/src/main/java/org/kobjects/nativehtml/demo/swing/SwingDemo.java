package org.kobjects.nativehtml.demo.swing;

import java.awt.BorderLayout;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementFactory;
import org.kobjects.nativehtml.io.HtmlProcessor;
import org.kobjects.nativehtml.swing.HtmlComponent;
import org.kobjects.nativehtml.swing.SwingElementFactory;
import org.kobjects.nativehtml.util.DebugDump;

public class SwingDemo {

	
    public static void main(String[] args) throws UnsupportedEncodingException, URISyntaxException {
    	
    	HtmlComponent htmlComponent = new HtmlComponent();
    	
    	URI url = SwingDemo.class.getResource("snippet.html").toURI();
    	System.out.println("URL: " + url);
    	
    	htmlComponent.loadHtml(
    	    new InputStreamReader(SwingDemo.class.getResourceAsStream("snippet.html"), "utf-8"), 
    	   url);
    	
    	JFrame frame = new JFrame("NativeHtml");
    	frame.setLayout(new BorderLayout());
    	frame.getContentPane().add(htmlComponent, BorderLayout.CENTER);
    	
    	frame.pack();
    	
    	frame.setVisible(true);

    }

}