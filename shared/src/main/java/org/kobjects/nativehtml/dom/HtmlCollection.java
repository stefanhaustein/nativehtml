package org.kobjects.nativehtml.dom;

public interface HtmlCollection {

    HtmlCollection EMPTY = new HtmlCollection() {
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
