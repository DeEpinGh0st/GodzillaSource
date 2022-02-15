package com.jgoodies.forms.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;



















































































public abstract class AbstractButtonPanelBuilder<B extends AbstractButtonPanelBuilder<B>>
  extends AbstractBuilder<B>
{
  private boolean leftToRight;
  protected boolean focusGrouped = false;
  
  protected AbstractButtonPanelBuilder(FormLayout layout, JPanel container) {
    super(layout, container);
    opaque(false);
    ComponentOrientation orientation = container.getComponentOrientation();
    this.leftToRight = (orientation.isLeftToRight() || !orientation.isHorizontal());
  }











  
  public final JPanel build() {
    if (!this.focusGrouped) {
      List<AbstractButton> buttons = new ArrayList<AbstractButton>();
      for (Component component : getPanel().getComponents()) {
        if (component instanceof AbstractButton) {
          buttons.add((AbstractButton)component);
        }
      } 
      FocusTraversalUtilsAccessor.tryToBuildAFocusGroup(buttons.<AbstractButton>toArray(new AbstractButton[0]));
      this.focusGrouped = true;
    } 
    return getPanel();
  }












  
  @Deprecated
  public final void setBackground(Color background) {
    getPanel().setBackground(background);
    opaque(true);
  }










  
  @Deprecated
  public final void setBorder(Border border) {
    getPanel().setBorder(border);
  }












  
  @Deprecated
  public final void setOpaque(boolean b) {
    getPanel().setOpaque(b);
  }














  
  public final boolean isLeftToRight() {
    return this.leftToRight;
  }











  
  public final void setLeftToRight(boolean b) {
    this.leftToRight = b;
  }






  
  protected final void nextColumn() {
    nextColumn(1);
  }






  
  private void nextColumn(int columns) {
    this.currentCellConstraints.gridX += columns * getColumnIncrementSign();
  }

  
  protected final int getColumn() {
    return this.currentCellConstraints.gridX;
  }






  
  protected final int getRow() {
    return this.currentCellConstraints.gridY;
  }




  
  protected final void nextRow() {
    nextRow(1);
  }






  
  private void nextRow(int rows) {
    this.currentCellConstraints.gridY += rows;
  }








  
  protected final void appendColumn(ColumnSpec columnSpec) {
    getLayout().appendColumn(columnSpec);
  }







  
  protected final void appendGlueColumn() {
    appendColumn(FormSpecs.GLUE_COLSPEC);
  }







  
  protected final void appendRelatedComponentsGapColumn() {
    appendColumn(FormSpecs.RELATED_GAP_COLSPEC);
  }







  
  protected final void appendUnrelatedComponentsGapColumn() {
    appendColumn(FormSpecs.UNRELATED_GAP_COLSPEC);
  }








  
  protected final void appendRow(RowSpec rowSpec) {
    getLayout().appendRow(rowSpec);
  }







  
  protected final void appendGlueRow() {
    appendRow(FormSpecs.GLUE_ROWSPEC);
  }







  
  protected final void appendRelatedComponentsGapRow() {
    appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
  }







  
  protected final void appendUnrelatedComponentsGapRow() {
    appendRow(FormSpecs.UNRELATED_GAP_ROWSPEC);
  }











  
  protected final Component add(Component component) {
    getPanel().add(component, this.currentCellConstraints);
    this.focusGrouped = false;
    return component;
  }














  
  protected abstract AbstractButtonPanelBuilder addButton(JComponent paramJComponent);














  
  protected AbstractButtonPanelBuilder addButton(JComponent... buttons) {
    Preconditions.checkNotNull(buttons, "The button array must not be null.");
    Preconditions.checkArgument((buttons.length > 0), "The button array must not be empty.");
    boolean needsGap = false;
    for (JComponent button : buttons) {
      if (button == null) {
        addUnrelatedGap();
        needsGap = false;
      } else {
        
        if (needsGap) {
          addRelatedGap();
        }
        addButton(button);
        needsGap = true;
      } 
    }  return this;
  }







  
  protected AbstractButtonPanelBuilder addButton(Action... actions) {
    Preconditions.checkNotNull(actions, "The Action array must not be null.");
    int length = actions.length;
    Preconditions.checkArgument((length > 0), "The Action array must not be empty.");
    JButton[] buttons = new JButton[length];
    for (int i = 0; i < length; i++) {
      Action action = actions[i];
      buttons[i] = (action == null) ? null : createButton(action);
    } 
    return addButton((JComponent[])buttons);
  }









  
  protected abstract AbstractButtonPanelBuilder addRelatedGap();








  
  protected abstract AbstractButtonPanelBuilder addUnrelatedGap();








  
  protected JButton createButton(Action action) {
    return getComponentFactory().createButton(action);
  }









  
  private int getColumnIncrementSign() {
    return isLeftToRight() ? 1 : -1;
  }
}
