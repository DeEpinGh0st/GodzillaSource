package com.jgoodies.forms.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.forms.internal.AbstractButtonPanelBuilder;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.ConstantSize;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Size;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;











































































public final class ButtonStackBuilder
  extends AbstractButtonPanelBuilder<ButtonStackBuilder>
{
  private static final ColumnSpec[] COL_SPECS = new ColumnSpec[] { FormSpecs.BUTTON_COLSPEC };




  
  private static final RowSpec[] ROW_SPECS = new RowSpec[0];








  
  public ButtonStackBuilder() {
    this(new JPanel(null));
  }







  
  public ButtonStackBuilder(JPanel panel) {
    super(new FormLayout(COL_SPECS, ROW_SPECS), panel);
  }







  
  public static ButtonStackBuilder create() {
    return new ButtonStackBuilder();
  }

















  
  public ButtonStackBuilder addButton(JComponent button) {
    Preconditions.checkNotNull(button, "The button must not be null.");
    getLayout().appendRow(FormSpecs.PREF_ROWSPEC);
    add(button);
    nextRow();
    return this;
  }


  
  public ButtonStackBuilder addButton(JComponent... buttons) {
    super.addButton(buttons);
    return this;
  }




  
  public ButtonStackBuilder addButton(Action... actions) {
    super.addButton(actions);
    return this;
  }






  
  public ButtonStackBuilder addFixed(JComponent component) {
    getLayout().appendRow(FormSpecs.PREF_ROWSPEC);
    add(component);
    nextRow();
    return this;
  }







  
  public ButtonStackBuilder addGlue() {
    appendGlueRow();
    nextRow();
    return this;
  }


  
  public ButtonStackBuilder addRelatedGap() {
    appendRelatedComponentsGapRow();
    nextRow();
    return this;
  }


  
  public ButtonStackBuilder addUnrelatedGap() {
    appendUnrelatedComponentsGapRow();
    nextRow();
    return this;
  }






  
  public ButtonStackBuilder addStrut(ConstantSize size) {
    getLayout().appendRow(new RowSpec(RowSpec.TOP, (Size)size, 0.0D));

    
    nextRow();
    return this;
  }
}
