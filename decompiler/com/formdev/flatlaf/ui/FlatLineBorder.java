package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;


























public class FlatLineBorder
  extends FlatEmptyBorder
{
  private final Color lineColor;
  private final float lineThickness;
  
  public FlatLineBorder(Insets insets, Color lineColor) {
    this(insets, lineColor, 1.0F);
  }
  
  public FlatLineBorder(Insets insets, Color lineColor, float lineThickness) {
    super(insets);
    this.lineColor = lineColor;
    this.lineThickness = lineThickness;
  }
  
  public Color getLineColor() {
    return this.lineColor;
  }
  
  public float getLineThickness() {
    return this.lineThickness;
  }

  
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Graphics2D g2 = (Graphics2D)g.create();
    try {
      FlatUIUtils.setRenderingHints(g2);
      g2.setColor(this.lineColor);
      FlatUIUtils.paintComponentBorder(g2, x, y, width, height, 0.0F, UIScale.scale(this.lineThickness), 0.0F);
    } finally {
      g2.dispose();
    } 
  }
}
