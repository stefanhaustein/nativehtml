package org.kobjects.nativehtml.android;

import android.content.Context;
import android.widget.FrameLayout;
import java.io.Reader;
import java.net.URI;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.io.DefaultRequestHandler;
import org.kobjects.nativehtml.io.HtmlParser;
import org.kobjects.nativehtml.io.InternalLinkHandler;

public class HtmlView extends FrameLayout implements InternalLinkHandler {
    private final AndroidPlatform platform;
    private final HtmlParser htmlParser;
    private DefaultRequestHandler requestHandler;

    public HtmlView(Context context) {
        super(context);
        platform = new AndroidPlatform(context);
        requestHandler = new DefaultRequestHandler(platform);
        requestHandler.setInternalLinkHandler(this);
        htmlParser = new HtmlParser(platform, requestHandler, null);
    }

    public void addInternalLinkPrefix(String s) {
        requestHandler.addInternalLinkPrefix(s);
    }

    public void loadHtml(URI url) {
        requestHandler.openInternalLink(url);
    }

    public void loadHtml(Reader reader, URI baseUrl) {
        Element element = htmlParser.parse(reader, baseUrl);

        removeAllViews();
        if (element instanceof AbstractAndroidComponentElement) {
            addView((AbstractAndroidComponentElement) element);
        }
    }


}
