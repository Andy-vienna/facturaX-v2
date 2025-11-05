package org.andy.fx.gui.misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

public class MyFlatTabbedPaneUI

extends FlatTabbedPaneUI
{
	public static ComponentUI createUI(JComponent c) {
		return new MyFlatTabbedPaneUI();
	}

	@Override
	protected void paintCardTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h) {
		float lineWidth = UIScale.scale(1f);
		float arc = UIScale.scale(10);

		Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
		path.append(FlatUIUtils.createRoundRectanglePath(x, y, w, h, arc, arc, 0, 0), false);
		path.append(FlatUIUtils.createRoundRectanglePath(x + lineWidth, y + lineWidth, w - (lineWidth * 2), h - lineWidth,
				arc - lineWidth, arc - lineWidth, 0, 0), false);

		g.setColor(Color.pink);
		((Graphics2D)g).fill(path);

	}

}
