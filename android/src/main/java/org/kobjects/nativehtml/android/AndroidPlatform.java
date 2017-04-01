package org.kobjects.nativehtml.android;

import android.content.Context;
import java.net.URI;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.Platform;
import org.kobjects.nativehtml.util.ElementImpl;

public class AndroidPlatform implements Platform {
    private final Context context;

    public AndroidPlatform(Context context) {
        this.context = context;
    }

    @Override
    public Element createElement(Document document, ElementType elementType, String name) {
        if (elementType != ElementType.COMPONENT) {
            return null;
        }
        switch (name) {
            case "text-component":
                return new AndroidTextComponent(context, document);
            default:
                return new AndroidContainerElement(context, document, name);
        }
    }

    @Override
    public void openInBrowser(URI url) {
        throw new RuntimeException("NYI: open url " + url);
    }
}
