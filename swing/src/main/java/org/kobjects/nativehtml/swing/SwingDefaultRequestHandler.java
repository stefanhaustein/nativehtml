package org.kobjects.nativehtml.swing;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.kobjects.nativehtml.io.RequestHandler;

public class SwingDefaultRequestHandler implements RequestHandler {

  @Override
  public void openLink(URI uri) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
          desktop.browse(uri);
        } catch (IOException e) {
        }
    }
  }

}
