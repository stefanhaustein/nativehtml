package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.html.HtmlComponentElement;

public interface Layout {

  enum Directive {
    MINIMUM, FIT_CONTENT, STRETCH
  }
  
  int measureWidth(HtmlComponentElement parent, Directive directive, int contentBoxWidth);
  
  
  int layout(HtmlComponentElement parent, int xOfs, int yOfs, int contentWidth, boolean measureOnly);
	
}
