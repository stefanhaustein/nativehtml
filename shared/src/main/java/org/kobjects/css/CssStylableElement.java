package org.kobjects.css;

import java.util.Iterator;

public interface CssStylableElement {

  String getAttribute(String name);

  String getLocalName();

  Iterator<? extends CssStylableElement> getChildElementIterator();
  
  void setComputedStyle(CssStyleDeclaration style);
  CssStyleDeclaration getStyle();
}
