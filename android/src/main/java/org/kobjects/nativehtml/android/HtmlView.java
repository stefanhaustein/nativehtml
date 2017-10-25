package org.kobjects.nativehtml.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.io.DefaultRequestHandler;
import org.kobjects.nativehtml.io.HtmlParser;
import org.kobjects.nativehtml.io.InternalLinkHandler;

import java.io.Reader;
import java.net.URI;

public class HtmlView extends FrameLayout implements InternalLinkHandler {
    private HtmlParser htmlParser;
    private DefaultRequestHandler requestHandler;

    public HtmlView(Context context) {
        super(context);
        init(context);
    }

    public HtmlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HtmlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        AndroidPlatform platform = new AndroidPlatform(context);
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
