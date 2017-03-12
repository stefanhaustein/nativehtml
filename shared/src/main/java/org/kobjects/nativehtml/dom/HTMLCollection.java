package org.kobjects.nativehtml.dom;

public interface HTMLCollection {

    HTMLCollection EMPTY = new HTMLCollection() {
        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public Element item(int index) {
            return null;
        }
    };

    int getLength();
    Element item(int index);
}
