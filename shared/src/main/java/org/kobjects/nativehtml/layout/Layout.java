package org.kobjects.nativehtml.layout;

public interface Layout {

  enum Directive {
    MINIMUM, FIT_CONTENT, STRETCH
  }
  
  float measureWidth(ComponentElement parent, Directive directive, float contentBoxWidth);
  
  
  float layout(ComponentElement parent, float xOfs, float yOfs, float contentWidth, boolean measureOnly);
	
}
