package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.html.HtmlComponent;

public interface Layout {

  enum Directive {
    MINIMUM, FIT_CONTENT, STRETCH
  }
  
  int measureWidth(HtmlComponent parent, Directive directive, int contentBoxWidth);
  
  
  int layout(HtmlComponent parent, int xOfs, int yOfs, int contentWidth, boolean measureOnly);
	
}
