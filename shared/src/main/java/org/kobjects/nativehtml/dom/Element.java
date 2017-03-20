package org.kobjects.nativehtml.dom;

import org.kobjects.nativehtml.css.CssStyleDeclaration;

public interface Element {
  String getLocalName();
  void setAttribute(String name, String value);
  String getAttribute(String name);

  Element getParentElement();
  void setComputedStyle(CssStyleDeclaration style);
  
  /**
   * Used internally
   */
  ElementType getElementType();
  ContentType getElemnetContentType();

  /**
   * Used internally in insertBefore.
   */
  void setParentElement(Element parent);

  HtmlCollection getChildren();

  CssStyleDeclaration getStyle();
  CssStyleDeclaration getComputedStyle();

  String getTextContent();

  void setTextContent(String textContent);

  void insertBefore(Element newChild, Element referenceChild);
  
}
