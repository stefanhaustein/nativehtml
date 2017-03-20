package org.kobjects.nativehtml.dom;

import java.util.EnumSet;

import org.kobjects.nativehtml.css.CssStyleDeclaration;

public interface HtmlElement {
  String getLocalName();
  void setAttribute(String name, String value);
  String getAttribute(String name);

  HtmlElement getParentElement();
  void setComputedStyle(CssStyleDeclaration style);
  
  /**
   * Used internally
   */
  HtmlElementType getElementType();
  HtmlContentType getElemnetContentType();

  /**
   * Used internally in insertBefore.
   */
  void setParentElement(HtmlElement parent);

  HtmlCollection getChildren();

  CssStyleDeclaration getStyle();
  CssStyleDeclaration getComputedStyle();

  String getTextContent();

  void setTextContent(String textContent);

  void insertBefore(HtmlElement newChild, HtmlElement referenceChild);
  
}
