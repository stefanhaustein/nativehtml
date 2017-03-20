package org.kobjects.nativehtml.layout;

import org.kobjects.nativehtml.html.ComponentElement;

public interface Layout {

  enum Directive {
    MINIMUM, FIT_CONTENT, STRETCH
  }
  
  int measureWidth(ComponentElement parent, Directive directive, int contentBoxWidth);
  
  
  int layout(ComponentElement parent, int xOfs, int yOfs, int contentWidth, boolean measureOnly);
	
}
