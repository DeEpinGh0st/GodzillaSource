package com.jgoodies.forms.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.forms.internal.AbstractButtonPanelBuilder;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.ConstantSize;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
























































































public final class ButtonBarBuilder
  extends AbstractButtonPanelBuilder<ButtonBarBuilder>
{
  private static final ColumnSpec[] COL_SPECS = new ColumnSpec[0];




  
  private static final RowSpec[] ROW_SPECS = new RowSpec[] { RowSpec.decode("center:pref") };







  
  public ButtonBarBuilder() {
    this(new JPanel(null));
  }






  
  public ButtonBarBuilder(JPanel panel) {
    super(new FormLayout(COL_SPECS, ROW_SPECS), panel);
  }






  
  public static ButtonBarBuilder create() {
    return new ButtonBarBuilder();
  }

















  
  public ButtonBarBuilder addButton(JComponent button) {
    Preconditions.checkNotNull(button, "The button to add must not be null.");
    getLayout().appendColumn(FormSpecs.BUTTON_COLSPEC);
    add(button);
    nextColumn();
    return this;
  }


  
  public ButtonBarBuilder addButton(JComponent... buttons) {
    super.addButton(buttons);
    return this;
  }


  
  public ButtonBarBuilder addButton(Action... actions) {
    super.addButton(actions);
    return this;
  }













  
  public ButtonBarBuilder addFixed(JComponent component) {
    getLayout().appendColumn(FormSpecs.PREF_COLSPEC);
    add(component);
    nextColumn();
    return this;
  }










  
  public ButtonBarBuilder addGrowing(JComponent component) {
    getLayout().appendColumn(FormSpecs.GROWING_BUTTON_COLSPEC);
    add(component);
    nextColumn();
    return this;
  }









  
  public ButtonBarBuilder addGlue() {
    appendGlueColumn();
    nextColumn();
    return this;
  }









  
  public ButtonBarBuilder addRelatedGap() {
    appendRelatedComponentsGapColumn();
    nextColumn();
    return this;
  }









  
  public ButtonBarBuilder addUnrelatedGap() {
    appendUnrelatedComponentsGapColumn();
    nextColumn();
    return this;
  }












  
  public ButtonBarBuilder addStrut(ConstantSize width) {
    getLayout().appendColumn(ColumnSpec.createGap(width));
    nextColumn();
    return this;
  }
}
