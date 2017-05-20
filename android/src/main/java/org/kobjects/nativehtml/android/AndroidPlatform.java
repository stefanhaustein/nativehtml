package org.kobjects.nativehtml.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.ElementType;
import org.kobjects.nativehtml.dom.Platform;

public class AndroidPlatform implements Platform {

    HashMap<URI, CacheEntry> imageCache = new HashMap<>();

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

    public Bitmap getImage(Element element, final URI uri) {
        // TODO: fingerprint data urls!
        CacheEntry cacheEntry = imageCache.get(uri);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry();
            imageCache.put(uri, cacheEntry);
            String s = uri.toString();
            if (s.startsWith("data:")) {
                int cut = s.indexOf(",");
                String data = s.substring(cut + 1);
                byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);
                cacheEntry.bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } else {
                cacheEntry.elements = new ArrayList<>();
                cacheEntry.elements.add(element);
                new AsyncTask<CacheEntry, CacheEntry, Void>() {
                    @Override
                    protected Void doInBackground(CacheEntry... cacheEntries) {
                        for (CacheEntry cacheEntry: cacheEntries) {
                            try {
                                InputStream is = uri.toURL().openStream();
                                cacheEntry.bitmap = BitmapFactory.decodeStream(is);
                                synchronized (cacheEntry.elements) {
                                    for (Element element : cacheEntry.elements) {
                                        Element e = element;
                                        while (e != null && !(e instanceof View)) {
                                            e = e.getParentElement();
                                        }
                                        if (e != null) {
                                            final View view = (View) e;
                                            view.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    view.requestLayout();
                                                }
                                            });
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                }.execute(cacheEntry);
            }
        } else if (cacheEntry.bitmap == null && cacheEntry.elements != null) {
            synchronized (cacheEntry.elements) {
                cacheEntry.elements.add(element);
            }
        }
        return cacheEntry.bitmap;
    }

    static class CacheEntry {
        Bitmap bitmap;
        ArrayList<Element> elements;
    }
}
