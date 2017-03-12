package org.kobjects.nativehtml.android;

import org.kobjects.nativehtml.dom.Element;


/**
 *
 */
public class ComponentContainer extends HtmlView implements HTMLCollection {

    ComponentContainer(Context context, String name) {
        super(context, name);
    }

    public ElementType getElementType() {
        return ElementType.COMPONENT_CONTAINER;
    }

}