package org.kobjects.nativehtml.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;
import org.kobjects.nativehtml.dom.ContentType;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.dom.HtmlSelectElement;
import org.kobjects.nativehtml.util.HtmlCollectionImpl;

public class AndroidSelectElement extends AndroidWrapperElement implements HtmlSelectElement {
    HtmlCollectionImpl children = new HtmlCollectionImpl();
    Spinner spinner;

    AndroidSelectElement(final Context context, Document document) {
        super(context, document, "select", new Spinner(context));
        spinner = (Spinner) child;
        spinner.setAdapter(new SelectElementAdapter());
     /*   spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) throw new RuntimeException("YAY");
                spinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }); */
    }

    @Override
    public ContentType getElementContentType() {
        return ContentType.DATA_ELEMENTS;
    }


    @Override
    public void insertBefore(Element newChild, Element referenceChild) {
        children.insertBefore(this, newChild, referenceChild);
        ((SelectElementAdapter) spinner.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
    }

    @Override
    public HtmlCollection getChildren() {
        return children;
    }

    private class SelectElementAdapter extends BaseAdapter {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount() {
            return children.getLength();
        }

        @Override
        public Object getItem(int i) {
            return children.item(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        private View getResourceView(int position, View convertView, ViewGroup parent, int resId) {
            TextView textView = (TextView) (convertView == null
                    ? inflater.inflate(resId, parent, false)
                    : convertView);
            textView.setText(((Element) getItem(position)).getTextContent());
            return textView;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getResourceView(position, convertView, parent, android.R.layout.simple_spinner_item);
        }

        @Override
        public View getDropDownView(final int position, View convertView, ViewGroup parent) {
            return getResourceView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
        }


    }
}
