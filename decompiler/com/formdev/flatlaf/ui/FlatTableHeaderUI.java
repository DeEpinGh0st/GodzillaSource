package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;





































public class FlatTableHeaderUI
  extends BasicTableHeaderUI
{
  protected Color separatorColor;
  protected Color bottomSeparatorColor;
  protected int height;
  protected int sortIconPosition;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatTableHeaderUI();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    this.separatorColor = UIManager.getColor("TableHeader.separatorColor");
    this.bottomSeparatorColor = UIManager.getColor("TableHeader.bottomSeparatorColor");
    this.height = UIManager.getInt("TableHeader.height");
    switch (Objects.toString(UIManager.getString("TableHeader.sortIconPosition"), "right"))
    { default:
        this.sortIconPosition = 4; return;
      case "left": this.sortIconPosition = 2; return;
      case "top": this.sortIconPosition = 1; return;
      case "bottom": break; }  this.sortIconPosition = 3;
  }


  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    this.separatorColor = null;
    this.bottomSeparatorColor = null;
  }

  
  public void paint(Graphics g, JComponent c) {
    if (this.header.getColumnModel().getColumnCount() <= 0) {
      return;
    }
    
    TableCellRenderer defaultRenderer = this.header.getDefaultRenderer();
    boolean paintBorders = isSystemDefaultRenderer(defaultRenderer);
    if (!paintBorders) {
      
      Component rendererComponent = defaultRenderer.getTableCellRendererComponent(this.header
          .getTable(), "", false, false, -1, 0);
      paintBorders = isSystemDefaultRenderer(rendererComponent);
    } 
    
    if (paintBorders) {
      paintColumnBorders(g, c);
    }
    
    FlatTableCellHeaderRenderer sortIconRenderer = null;
    if (this.sortIconPosition != 4) {
      sortIconRenderer = new FlatTableCellHeaderRenderer(this.header.getDefaultRenderer());
      this.header.setDefaultRenderer(sortIconRenderer);
    } 

    
    super.paint(g, c);

    
    if (sortIconRenderer != null) {
      sortIconRenderer.reset();
      this.header.setDefaultRenderer(sortIconRenderer.delegate);
    } 
    
    if (paintBorders)
      paintDraggedColumnBorders(g, c); 
  }
  
  private boolean isSystemDefaultRenderer(Object headerRenderer) {
    String rendererClassName = headerRenderer.getClass().getName();
    return (rendererClassName.equals("sun.swing.table.DefaultTableCellHeaderRenderer") || rendererClassName
      .equals("sun.swing.FilePane$AlignableTableHeaderRenderer"));
  }
  
  protected void paintColumnBorders(Graphics g, JComponent c) {
    int width = c.getWidth();
    int height = c.getHeight();
    float lineWidth = UIScale.scale(1.0F);
    float topLineIndent = lineWidth;
    float bottomLineIndent = lineWidth * 3.0F;
    TableColumnModel columnModel = this.header.getColumnModel();
    int columnCount = columnModel.getColumnCount();
    int sepCount = columnCount;
    if (hideLastVerticalLine()) {
      sepCount--;
    }
    Graphics2D g2 = (Graphics2D)g.create();
    try {
      FlatUIUtils.setRenderingHints(g2);

      
      g2.setColor(this.bottomSeparatorColor);
      g2.fill(new Rectangle2D.Float(0.0F, height - lineWidth, width, lineWidth));

      
      g2.setColor(this.separatorColor);
      
      float y = topLineIndent;
      float h = height - bottomLineIndent;
      
      if (this.header.getComponentOrientation().isLeftToRight()) {
        int x = 0;
        for (int i = 0; i < sepCount; i++) {
          x += columnModel.getColumn(i).getWidth();
          g2.fill(new Rectangle2D.Float(x - lineWidth, y, lineWidth, h));
        } 

        
        if (!hideTrailingVerticalLine())
          g2.fill(new Rectangle2D.Float(this.header.getWidth() - lineWidth, y, lineWidth, h)); 
      } else {
        Rectangle cellRect = this.header.getHeaderRect(0);
        int x = cellRect.x + cellRect.width;
        for (int i = 0; i < sepCount; i++) {
          x -= columnModel.getColumn(i).getWidth();
          g2.fill(new Rectangle2D.Float(x - ((i < sepCount - 1) ? lineWidth : 0.0F), y, lineWidth, h));
        } 

        
        if (!hideTrailingVerticalLine())
          g2.fill(new Rectangle2D.Float(0.0F, y, lineWidth, h)); 
      } 
    } finally {
      g2.dispose();
    } 
  }
  
  private void paintDraggedColumnBorders(Graphics g, JComponent c) {
    TableColumn draggedColumn = this.header.getDraggedColumn();
    if (draggedColumn == null) {
      return;
    }
    
    TableColumnModel columnModel = this.header.getColumnModel();
    int columnCount = columnModel.getColumnCount();
    int draggedColumnIndex = -1;
    for (int i = 0; i < columnCount; i++) {
      if (columnModel.getColumn(i) == draggedColumn) {
        draggedColumnIndex = i;
        
        break;
      } 
    } 
    if (draggedColumnIndex < 0) {
      return;
    }
    float lineWidth = UIScale.scale(1.0F);
    float topLineIndent = lineWidth;
    float bottomLineIndent = lineWidth * 3.0F;
    Rectangle r = this.header.getHeaderRect(draggedColumnIndex);
    r.x += this.header.getDraggedDistance();
    
    Graphics2D g2 = (Graphics2D)g.create();
    try {
      FlatUIUtils.setRenderingHints(g2);

      
      g2.setColor(this.bottomSeparatorColor);
      g2.fill(new Rectangle2D.Float(r.x, (r.y + r.height) - lineWidth, r.width, lineWidth));

      
      g2.setColor(this.separatorColor);
      g2.fill(new Rectangle2D.Float(r.x, topLineIndent, lineWidth, r.height - bottomLineIndent));
      g2.fill(new Rectangle2D.Float((r.x + r.width) - lineWidth, r.y + topLineIndent, lineWidth, r.height - bottomLineIndent));
    } finally {
      g2.dispose();
    } 
  }

  
  public Dimension getPreferredSize(JComponent c) {
    Dimension size = super.getPreferredSize(c);
    if (size.height > 0)
      size.height = Math.max(size.height, UIScale.scale(this.height)); 
    return size;
  }
  
  protected boolean hideLastVerticalLine() {
    Container viewport = this.header.getParent();
    Container viewportParent = (viewport != null) ? viewport.getParent() : null;
    if (!(viewportParent instanceof JScrollPane)) {
      return false;
    }
    Rectangle cellRect = this.header.getHeaderRect(this.header.getColumnModel().getColumnCount() - 1);

    
    JScrollPane scrollPane = (JScrollPane)viewportParent;
    return scrollPane.getComponentOrientation().isLeftToRight() ? (
      (cellRect.x + cellRect.width >= viewport.getWidth())) : ((cellRect.x <= 0));
  }

  
  protected boolean hideTrailingVerticalLine() {
    Container viewport = this.header.getParent();
    Container viewportParent = (viewport != null) ? viewport.getParent() : null;
    if (!(viewportParent instanceof JScrollPane)) {
      return false;
    }
    JScrollPane scrollPane = (JScrollPane)viewportParent;
    return (viewport == scrollPane.getColumnHeader() && scrollPane
      .getCorner("UPPER_TRAILING_CORNER") == null);
  }



  
  private class FlatTableCellHeaderRenderer
    implements TableCellRenderer, Border, UIResource
  {
    private final TableCellRenderer delegate;

    
    private JLabel l;

    
    private int oldHorizontalTextPosition = -1;
    private Border origBorder;
    private Icon sortIcon;
    
    FlatTableCellHeaderRenderer(TableCellRenderer delegate) {
      this.delegate = delegate;
    }



    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = this.delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!(c instanceof JLabel)) {
        return c;
      }
      this.l = (JLabel)c;
      
      if (FlatTableHeaderUI.this.sortIconPosition == 2) {
        if (this.oldHorizontalTextPosition < 0)
          this.oldHorizontalTextPosition = this.l.getHorizontalTextPosition(); 
        this.l.setHorizontalTextPosition(4);
      } else {
        
        this.sortIcon = this.l.getIcon();
        this.origBorder = this.l.getBorder();
        this.l.setIcon((Icon)null);
        this.l.setBorder(this);
      } 
      
      return this.l;
    }
    
    void reset() {
      if (this.l != null && FlatTableHeaderUI.this.sortIconPosition == 2 && this.oldHorizontalTextPosition >= 0) {
        this.l.setHorizontalTextPosition(this.oldHorizontalTextPosition);
      }
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      if (this.origBorder != null) {
        this.origBorder.paintBorder(c, g, x, y, width, height);
      }
      if (this.sortIcon != null) {
        int xi = x + (width - this.sortIcon.getIconWidth()) / 2;



        
        int yi = (FlatTableHeaderUI.this.sortIconPosition == 1) ? (y + UIScale.scale(1)) : (y + height - this.sortIcon.getIconHeight() - 1 - (int)(1.0F * UIScale.getUserScaleFactor()));
        this.sortIcon.paintIcon(c, g, xi, yi);
      } 
    }

    
    public Insets getBorderInsets(Component c) {
      return (this.origBorder != null) ? this.origBorder.getBorderInsets(c) : new Insets(0, 0, 0, 0);
    }

    
    public boolean isBorderOpaque() {
      return (this.origBorder != null) ? this.origBorder.isBorderOpaque() : false;
    }
  }
}
