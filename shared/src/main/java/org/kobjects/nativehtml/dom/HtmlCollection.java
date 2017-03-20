package org.kobjects.nativehtml.dom;

public interface HtmlCollection {

    HtmlCollection EMPTY = new HtmlCollection() {
        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public HtmlElement item(int index) {
            return null;
        }
    };

    int getLength();
    HtmlElement item(int index);
}
