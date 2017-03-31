package org.kobjects.nativehtml.demo.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import java.net.URI;
import java.net.URISyntaxException;
import org.kobjects.nativehtml.android.HtmlView;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtmlView htmlView = new HtmlView(this);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(htmlView);
        setContentView(scrollView);

        try {
            final URI url = MainActivity.class.getResource("/index.html").toURI();
            String prefix = url.toString();
            int cut = prefix.lastIndexOf('/');
            prefix = prefix.substring(0, cut + 1);
            htmlView.addInternalLinkPrefix(prefix);

            htmlView.loadHtml(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}