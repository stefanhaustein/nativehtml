package org.kobjects.nativehtml.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.layout.Layout;

public class LayoutAdapter implements LayoutManager {

	Layout layout;
	
	LayoutAdapter(Layout layout) {
		this.layout = layout;
	}
	
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int[] result = new int[2];
		layout.layout((Element) parent, 0, 0, 320, true, result);
		return new Dimension(result[0], result[1]);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int[] result = new int[2];
		layout.layout((Element) parent, 0, 0, -1, true, result);
		return new Dimension(result[0], result[1]);
	}

	@Override
	public void layoutContainer(Container parent) {
		int[] result = new int[2];
		layout.layout((Element) parent, 0, 0, parent.getWidth(), false, result);
	}

}
