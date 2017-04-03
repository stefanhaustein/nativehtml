package org.kobjects.nativehtml.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import java.net.URI;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.Platform;

public class AndroidPlatform implements Platform {
    @Override
    public float getPixelPerDp() {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.density;
    }

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
            case "input":
                return new AndroidInputElement(context, document);
            case "select":
                return new AndroidSelectElement(context, document);
            case "text-component":
                return new AndroidTextComponent(context, document);
            default:
                return new AndroidContainerElement(context, document, name);
        }
    }

    @Override
    public void openInBrowser(URI url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url.toString()));
        context.startActivity(intent);
    }

    public Bitmap getImage(Element element, URI uri) {
        return null;
    }
}
