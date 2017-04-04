package org.kobjects.nativehtml.android;

import android.content.Context;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.HtmlInputElement;
import org.w3c.dom.Text;

public class AndroidInputElement extends AndroidWrapperElement implements HtmlInputElement{

    public AndroidInputElement(Context context, Document document) {
        super(context, document, "input", new EditText(context));
        ((EditText) child).setMinEms(20);
    }

    public void setAttribute(String name, String value) {
        if ("type".equals(name)) {
            removeAllViews();
            switch (value.toLowerCase()) {
                case "checkbox":
                    child = new CheckBox(getContext());
                    break;
                case "submit":
                    child = new Button(getContext());
                    break;
                case "radio":
                    child = new RadioButton(getContext());
                    break;
                default: {
                    EditText editText = new EditText(getContext());
                    editText.setMinEms(20);
                    child = editText;
                    break;
                }
            }
            addView(child);
            setAttribute("value", getAttribute(value));
        } else if ("value".equals(name) && !(child instanceof CompoundButton)) {
            ((TextView) child).setText(value);
        }
        super.setAttribute(name, value);
    }

    @Override
    public ContentType getElementContentType() {
        return ContentType.EMPTY;
    }


}
