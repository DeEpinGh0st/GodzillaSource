package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;











































































public class FlatTreeUI
  extends BasicTreeUI
{
  protected Color selectionBackground;
  protected Color selectionForeground;
  protected Color selectionInactiveBackground;
  protected Color selectionInactiveForeground;
  protected Color selectionBorderColor;
  protected boolean wideSelection;
  protected boolean showCellFocusIndicator;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatTreeUI();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    LookAndFeel.installBorder(this.tree, "Tree.border");
    
    this.selectionBackground = UIManager.getColor("Tree.selectionBackground");
    this.selectionForeground = UIManager.getColor("Tree.selectionForeground");
    this.selectionInactiveBackground = UIManager.getColor("Tree.selectionInactiveBackground");
    this.selectionInactiveForeground = UIManager.getColor("Tree.selectionInactiveForeground");
    this.selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
    this.wideSelection = UIManager.getBoolean("Tree.wideSelection");
    this.showCellFocusIndicator = UIManager.getBoolean("Tree.showCellFocusIndicator");

    
    int rowHeight = FlatUIUtils.getUIInt("Tree.rowHeight", 16);
    if (rowHeight > 0)
      LookAndFeel.installProperty(this.tree, "rowHeight", Integer.valueOf(UIScale.scale(rowHeight))); 
    setLeftChildIndent(UIScale.scale(getLeftChildIndent()));
    setRightChildIndent(UIScale.scale(getRightChildIndent()));
  }

  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    LookAndFeel.uninstallBorder(this.tree);
    
    this.selectionBackground = null;
    this.selectionForeground = null;
    this.selectionInactiveBackground = null;
    this.selectionInactiveForeground = null;
    this.selectionBorderColor = null;
  }

  
  protected MouseListener createMouseListener() {
    return new BasicTreeUI.MouseHandler()
      {
        public void mousePressed(MouseEvent e) {
          super.mousePressed(handleWideMouseEvent(e));
        }

        
        public void mouseReleased(MouseEvent e) {
          super.mouseReleased(handleWideMouseEvent(e));
        }

        
        public void mouseDragged(MouseEvent e) {
          super.mouseDragged(handleWideMouseEvent(e));
        }
        
        private MouseEvent handleWideMouseEvent(MouseEvent e) {
          if (!FlatTreeUI.this.isWideSelection() || !FlatTreeUI.this.tree.isEnabled() || !SwingUtilities.isLeftMouseButton(e) || e.isConsumed()) {
            return e;
          }
          int x = e.getX();
          int y = e.getY();
          TreePath path = FlatTreeUI.this.getClosestPathForLocation(FlatTreeUI.this.tree, x, y);
          if (path == null || FlatTreeUI.this.isLocationInExpandControl(path, x, y)) {
            return e;
          }
          Rectangle bounds = FlatTreeUI.this.getPathBounds(FlatTreeUI.this.tree, path);
          if (bounds == null || y < bounds.y || y >= bounds.y + bounds.height) {
            return e;
          }
          int newX = Math.max(bounds.x, Math.min(x, bounds.x + bounds.width - 1));
          if (newX == x) {
            return e;
          }
          
          return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e
              .getModifiers() | e.getModifiersEx(), newX, e.getY(), e
              .getClickCount(), e.isPopupTrigger(), e.getButton());
        }
      };
  }

  
  protected PropertyChangeListener createPropertyChangeListener() {
    return new BasicTreeUI.PropertyChangeHandler()
      {
        public void propertyChange(PropertyChangeEvent e) {
          super.propertyChange(e);
          
          if (e.getSource() == FlatTreeUI.this.tree) {
            switch (e.getPropertyName()) {
              case "JTree.wideSelection":
              case "JTree.paintSelection":
                FlatTreeUI.this.tree.repaint();
                break;
              
              case "dropLocation":
                if (FlatTreeUI.this.isWideSelection()) {
                  JTree.DropLocation oldValue = (JTree.DropLocation)e.getOldValue();
                  repaintWideDropLocation(oldValue);
                  repaintWideDropLocation(FlatTreeUI.this.tree.getDropLocation());
                } 
                break;
            } 
          }
        }
        
        private void repaintWideDropLocation(JTree.DropLocation loc) {
          if (loc == null || FlatTreeUI.this.isDropLine(loc)) {
            return;
          }
          Rectangle r = FlatTreeUI.this.tree.getPathBounds(loc.getPath());
          if (r != null) {
            FlatTreeUI.this.tree.repaint(0, r.y, FlatTreeUI.this.tree.getWidth(), r.height);
          }
        }
      };
  }






  
  protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
    boolean isEditing = (this.editingComponent != null && this.editingRow == row);
    boolean isSelected = this.tree.isRowSelected(row);
    boolean isDropRow = isDropRow(row);
    boolean needsSelectionPainting = ((isSelected || isDropRow) && isPaintSelection());

    
    if (isEditing && !needsSelectionPainting) {
      return;
    }
    boolean hasFocus = FlatUIUtils.isPermanentFocusOwner(this.tree);
    boolean cellHasFocus = (hasFocus && row == getLeadSelectionRow());


    
    if (!hasFocus && isSelected && this.tree.getParent() instanceof javax.swing.CellRendererPane) {
      hasFocus = FlatUIUtils.isPermanentFocusOwner(this.tree.getParent().getParent());
    }
    
    Component rendererComponent = this.currentCellRenderer.getTreeCellRendererComponent(this.tree, path
        .getLastPathComponent(), isSelected, isExpanded, isLeaf, row, cellHasFocus);

    
    Color oldBackgroundSelectionColor = null;
    if (isSelected && !hasFocus && !isDropRow) {
      if (rendererComponent instanceof DefaultTreeCellRenderer) {
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)rendererComponent;
        if (renderer.getBackgroundSelectionColor() == this.selectionBackground) {
          oldBackgroundSelectionColor = renderer.getBackgroundSelectionColor();
          renderer.setBackgroundSelectionColor(this.selectionInactiveBackground);
        }
      
      } else if (rendererComponent.getBackground() == this.selectionBackground) {
        rendererComponent.setBackground(this.selectionInactiveBackground);
      } 
      
      if (rendererComponent.getForeground() == this.selectionForeground) {
        rendererComponent.setForeground(this.selectionInactiveForeground);
      }
    } 
    
    Color oldBorderSelectionColor = null;
    if (isSelected && hasFocus && (!this.showCellFocusIndicator || this.tree
      .getMinSelectionRow() == this.tree.getMaxSelectionRow()) && rendererComponent instanceof DefaultTreeCellRenderer) {

      
      DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)rendererComponent;
      if (renderer.getBorderSelectionColor() == this.selectionBorderColor) {
        oldBorderSelectionColor = renderer.getBorderSelectionColor();
        renderer.setBorderSelectionColor(null);
      } 
    } 

    
    if (needsSelectionPainting) {
      
      Color oldColor = g.getColor();
      g.setColor(isDropRow ? 
          UIManager.getColor("Tree.dropCellBackground") : ((rendererComponent instanceof DefaultTreeCellRenderer) ? ((DefaultTreeCellRenderer)rendererComponent)
          
          .getBackgroundSelectionColor() : (hasFocus ? this.selectionBackground : this.selectionInactiveBackground)));

      
      if (isWideSelection()) {
        
        g.fillRect(0, bounds.y, this.tree.getWidth(), bounds.height);


        
        if (shouldPaintExpandControl(path, row, isExpanded, hasBeenExpanded, isLeaf)) {
          paintExpandControl(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
        }
      }
      else {
        
        int xOffset = 0;
        int imageOffset = 0;
        
        if (rendererComponent instanceof JLabel) {
          JLabel label = (JLabel)rendererComponent;
          Icon icon = label.getIcon();
          
          imageOffset = (icon != null && label.getText() != null) ? (icon.getIconWidth() + Math.max(label.getIconTextGap() - 1, 0)) : 0;
          
          xOffset = label.getComponentOrientation().isLeftToRight() ? imageOffset : 0;
        } 
        
        g.fillRect(bounds.x + xOffset, bounds.y, bounds.width - imageOffset, bounds.height);
      } 


      
      g.setColor(oldColor);
    } 

    
    if (!isEditing) {
      this.rendererPane.paintComponent(g, rendererComponent, this.tree, bounds.x, bounds.y, bounds.width, bounds.height, true);
    }
    
    if (oldBackgroundSelectionColor != null)
      ((DefaultTreeCellRenderer)rendererComponent).setBackgroundSelectionColor(oldBackgroundSelectionColor); 
    if (oldBorderSelectionColor != null) {
      ((DefaultTreeCellRenderer)rendererComponent).setBorderSelectionColor(oldBorderSelectionColor);
    }
  }



  
  private boolean isDropRow(int row) {
    JTree.DropLocation dropLocation = this.tree.getDropLocation();
    return (dropLocation != null && dropLocation
      .getChildIndex() == -1 && this.tree
      .getRowForPath(dropLocation.getPath()) == row);
  }

  
  protected Rectangle getDropLineRect(JTree.DropLocation loc) {
    Rectangle r = super.getDropLineRect(loc);
    return isWideSelection() ? new Rectangle(0, r.y, this.tree.getWidth(), r.height) : r;
  }
  
  protected boolean isWideSelection() {
    return FlatClientProperties.clientPropertyBoolean(this.tree, "JTree.wideSelection", this.wideSelection);
  }
  
  protected boolean isPaintSelection() {
    return FlatClientProperties.clientPropertyBoolean(this.tree, "JTree.paintSelection", true);
  }
}
