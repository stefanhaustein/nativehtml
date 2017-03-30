package org.kobjects.nativehtml.android;

import android.content.Context;
import java.net.URI;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.Platform;

public class AndroidPlatform implements Platform {
    private final Context context;

    public AndroidPlatform(Context context) {
        this.context = context;
    }

    @Override
    public Element createElement(Document document, ElementType elementType, String name) {
        switch (elementType) {
            case COMPONENT:
                return new AndroidContainerElement(context, document, name);
            default:
                return null;

        }
    }

    @Override
    public void openInBrowser(URI url) {
        throw new RuntimeException("NYI: open url " + url);
    }
}
