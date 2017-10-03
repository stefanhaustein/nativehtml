package org.kobjects.nativehtml.android;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import org.kobjects.nativehtml.io.DefaultRequestHandler;

public class AndroidDefaultRequestHandler extends DefaultRequestHandler {
    private final Context context;
    public AndroidDefaultRequestHandler(AndroidPlatform platform) {
        super(platform);
        this.context = platform.context;
    }

    public void openInternalLink(URI url) {
        // TODO: Async
        String s = url.toString();

        InputStream is;
        try {
            if (s.startsWith("file:/android_asset/") ||
                    s.startsWith("file:///android_asset/")) {
                int cut = s.indexOf('/', 10);
                is = context.getAssets().open(s.substring(cut + 1));
            } else {
                is = url.toURL().openStream();
            }
            internalLinkHandler.loadHtml(new InputStreamReader(is, "utf-8"), url);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
