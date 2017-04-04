package org.kobjects.nativehtml.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import java.net.URI;
import java.util.HashMap;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.Platform;

public class AndroidPlatform implements Platform {

    HashMap<URI, Bitmap> imageCache = new HashMap<>();

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
        // TODO: fingerprint data urls!
        Bitmap result = imageCache.get(uri);
        if (result == null) {
            String s = uri.toString();
            if (s.startsWith("data:")) {
                int cut = s.indexOf(",");
                String data = s.substring(cut + 1);
                byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);
                result = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageCache.put(uri, result);
            }
        }
        return result;
    }
}
