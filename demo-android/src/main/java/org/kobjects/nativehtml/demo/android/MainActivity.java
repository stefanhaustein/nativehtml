package org.kobjects.nativehtml.demo.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import java.net.URI;
import java.net.URISyntaxException;
import org.kobjects.nativehtml.android.HtmlView;
import org.kobjects.nativehtml.dom.HtmlCollection;


public class MainActivity extends Activity {
    URI indexUrl;
    HtmlView htmlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        htmlView = new HtmlView(this);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(htmlView);
        setContentView(scrollView);

        try {
            indexUrl = MainActivity.class.getResource("/index.html").toURI();
            String prefix = indexUrl.toString();
            int cut = prefix.lastIndexOf('/');
            prefix = prefix.substring(0, cut + 1);
            htmlView.addInternalLinkPrefix(prefix);

            htmlView.loadHtml(indexUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onBackPressed() {
        htmlView.loadHtml(indexUrl);
    }
}