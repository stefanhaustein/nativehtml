package org.kobjects.nativehtml.dom;

public interface Element {
  String getLocalName();
  void setAttribute(String name, String value);
  String getAttribute(String name);

  Element getParentElement();

  /**
   * Used internally
   */
  ElementType getElementType();

  /**
   * Used internally in insertBefore.
   */
  void setParentElement(Element parent);

  HTMLCollection getChildren();

  CSSStyleDeclaration getStyle();
  CSSStyleDeclaration getComputedStyle();

  String getTextContent();

  void setTextContent(String textContent);

  void insertBefore(Element newChild, Element referenceChild);
  
}
