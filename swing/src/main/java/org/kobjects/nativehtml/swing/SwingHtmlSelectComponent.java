package org.kobjects.nativehtml.swing;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ListDataListener;

import org.kobjects.nativehtml.dom.HtmlContentType;
import org.kobjects.nativehtml.dom.HtmlDocument;
import org.kobjects.nativehtml.dom.HtmlElement;
import org.kobjects.nativehtml.dom.HtmlElementType;
import org.kobjects.nativehtml.dom.HtmlCollection;
import org.kobjects.nativehtml.util.HtmlCollectionImpl;

public class SwingHtmlSelectComponent extends SwingComponentWrapper<JComboBox<String>> {
	private HtmlCollectionImpl children = new HtmlCollectionImpl();
	
	SwingHtmlSelectComponent(HtmlDocument document, String name) {
		super(document, name, new JComboBox<String>());
		component.setModel(new ComboBoxModel<String>() {
			ArrayList<ListDataListener> listeners = new ArrayList<>(); 
			Object selected;
			
			@Override
			public int getSize() {
				return children.size();
			}

			@Override
			public String getElementAt(int index) {
				return children.get(index).getTextContent();
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				listeners.add(l);
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				listeners.remove(l);
			}

			@Override
			public void setSelectedItem(Object anItem) {
				selected = anItem;
			}

			@Override
			public Object getSelectedItem() {
				return selected;
			}	
		});
	}

	@Override
	public HtmlContentType getElemnetContentType() {
		return HtmlContentType.DATA_ELEMENTS;
	}

	@Override
	public HtmlCollection getChildren() {
		return children;
	}

	@Override
	public void insertBefore(HtmlElement newChild, HtmlElement referenceChild) {
		children.insertBefore(newChild, referenceChild);
	}
}
