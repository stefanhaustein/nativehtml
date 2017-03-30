package org.kobjects.nativehtml.demo.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.kobjects.nativehtml.swing.HtmlComponent;

public class SwingDemo {

	
    public static void main(String[] args) throws IOException, URISyntaxException {
    	final HtmlComponent htmlComponent = new HtmlComponent();
    	final URI url = SwingDemo.class.getResource("/index.html").toURI();
    	String prefix = url.toString();
    	int cut = prefix.lastIndexOf('/');
    	prefix = prefix.substring(0, cut + 1);

    	JScrollPane scrollPane = new JScrollPane(htmlComponent);
    	
    	htmlComponent.addInternalLinkPrefix(prefix);
    	
    	Action loadIndexAction = new AbstractAction("Back") {
          @Override
          public void actionPerformed(ActionEvent event) {
            try {
              htmlComponent.loadHtml(
                  new InputStreamReader(url.toURL().openStream(), "utf-8"), url);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        };
        loadIndexAction.actionPerformed(null);
        
        JFrame frame = new JFrame("NativeHtml");
    	frame.setLayout(new BorderLayout());
    	frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    	
    	JButton back = new JButton("Back");
    	back.setAction(loadIndexAction);
    	
    	JPanel topPanel = new JPanel();
    	topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    	topPanel.add(back);
    	frame.getContentPane().add(topPanel, BorderLayout.NORTH);
    	
    	frame.pack();
    	
    	frame.setVisible(true);

    }

}