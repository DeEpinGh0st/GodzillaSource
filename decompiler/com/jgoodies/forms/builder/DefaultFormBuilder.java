package com.jgoodies.forms.builder;

import com.jgoodies.common.internal.StringResourceAccessor;
import com.jgoodies.forms.internal.AbstractBuilder;
import com.jgoodies.forms.layout.ConstantSize;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Color;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


















































































































































































































@Deprecated
public final class DefaultFormBuilder
  extends I15dPanelBuilder
{
  private RowSpec defaultRowSpec = FormSpecs.PREF_ROWSPEC;






  
  private RowSpec lineGapSpec = FormSpecs.LINE_GAP_ROWSPEC;






  
  private RowSpec paragraphGapSpec = FormSpecs.PARAGRAPH_GAP_ROWSPEC;







  
  private int leadingColumnOffset = 0;








  
  private boolean rowGroupingEnabled = false;









  
  public DefaultFormBuilder(FormLayout layout) {
    this(layout, new JPanel(null));
  }










  
  public DefaultFormBuilder(FormLayout layout, JPanel container) {
    this(layout, (StringResourceAccessor)null, container);
  }











  
  public DefaultFormBuilder(FormLayout layout, ResourceBundle bundle) {
    super(layout, bundle);
  }












  
  public DefaultFormBuilder(FormLayout layout, ResourceBundle bundle, JPanel container) {
    super(layout, bundle, container);
  }










  
  public DefaultFormBuilder(FormLayout layout, StringResourceAccessor localizer) {
    super(layout, localizer);
  }











  
  public DefaultFormBuilder(FormLayout layout, StringResourceAccessor localizer, JPanel container) {
    super(layout, localizer, container);
  }




  
  public DefaultFormBuilder background(Color background) {
    super.background(background);
    return this;
  }


  
  public DefaultFormBuilder border(Border border) {
    super.border(border);
    return this;
  }


  
  public DefaultFormBuilder border(String emptyBorderSpec) {
    super.border(emptyBorderSpec);
    return this;
  }


  
  public DefaultFormBuilder padding(EmptyBorder padding) {
    super.padding(padding);
    return this;
  }


  
  public DefaultFormBuilder padding(String paddingSpec, Object... args) {
    super.padding(paddingSpec, new Object[0]);
    return this;
  }


  
  public DefaultFormBuilder opaque(boolean b) {
    super.opaque(b);
    return this;
  }









  
  public DefaultFormBuilder defaultRowSpec(RowSpec defaultRowSpec) {
    this.defaultRowSpec = defaultRowSpec;
    return this;
  }














  
  public DefaultFormBuilder lineGapSize(ConstantSize lineGapSize) {
    RowSpec rowSpec = RowSpec.createGap(lineGapSize);
    this.lineGapSpec = rowSpec;
    return this;
  }














  
  public DefaultFormBuilder paragraphGapSize(ConstantSize paragraphGapSize) {
    RowSpec rowSpec = RowSpec.createGap(paragraphGapSize);
    this.paragraphGapSpec = rowSpec;
    return this;
  }






  
  public DefaultFormBuilder leadingColumnOffset(int columnOffset) {
    this.leadingColumnOffset = columnOffset;
    return this;
  }






  
  public DefaultFormBuilder rowGroupingEnabled(boolean enabled) {
    this.rowGroupingEnabled = enabled;
    return this;
  }









  
  public final void appendLineGapRow() {
    appendRow(this.lineGapSpec);
  }









  
  public void append(Component component) {
    append(component, 1);
  }








  
  public void append(Component component, int columnSpan) {
    ensureCursorColumnInGrid();
    ensureHasGapRow(this.lineGapSpec);
    ensureHasComponentLine();
    
    add(component, createLeftAdjustedConstraints(columnSpan));
    nextColumn(columnSpan + 1);
  }








  
  public void append(Component c1, Component c2) {
    append(c1);
    append(c2);
  }









  
  public void append(Component c1, Component c2, Component c3) {
    append(c1);
    append(c2);
    append(c3);
  }









  
  public JLabel append(String textWithMnemonic) {
    JLabel label = getComponentFactory().createLabel(textWithMnemonic);
    append(label);
    return label;
  }












  
  public JLabel append(String textWithMnemonic, Component component) {
    return append(textWithMnemonic, component, 1);
  }















  
  public JLabel append(String textWithMnemonic, Component c, boolean nextLine) {
    JLabel label = append(textWithMnemonic, c);
    if (nextLine) {
      nextLine();
    }
    return label;
  }














  
  public JLabel append(String textWithMnemonic, Component c, int columnSpan) {
    JLabel label = append(textWithMnemonic);
    label.setLabelFor(c);
    append(c, columnSpan);
    return label;
  }













  
  public JLabel append(String textWithMnemonic, Component c1, Component c2) {
    JLabel label = append(textWithMnemonic, c1);
    append(c2);
    return label;
  }














  
  public JLabel append(String textWithMnemonic, Component c1, Component c2, int colSpan) {
    JLabel label = append(textWithMnemonic, c1);
    append(c2, colSpan);
    return label;
  }














  
  public JLabel append(String textWithMnemonic, Component c1, Component c2, Component c3) {
    JLabel label = append(textWithMnemonic, c1, c2);
    append(c3);
    return label;
  }















  
  public JLabel append(String textWithMnemonic, Component c1, Component c2, Component c3, Component c4) {
    JLabel label = append(textWithMnemonic, c1, c2, c3);
    append(c4);
    return label;
  }










  
  public JLabel appendI15d(String resourceKey) {
    return append(getResourceString(resourceKey));
  }












  
  public JLabel appendI15d(String resourceKey, Component component) {
    return append(getResourceString(resourceKey), component, 1);
  }














  
  public JLabel appendI15d(String resourceKey, Component component, boolean nextLine) {
    return append(getResourceString(resourceKey), component, nextLine);
  }















  
  public JLabel appendI15d(String resourceKey, Component c, int columnSpan) {
    return append(getResourceString(resourceKey), c, columnSpan);
  }














  
  public JLabel appendI15d(String resourceKey, Component c1, Component c2) {
    return append(getResourceString(resourceKey), c1, c2);
  }















  
  public JLabel appendI15d(String resourceKey, Component c1, Component c2, int colSpan) {
    return append(getResourceString(resourceKey), c1, c2, colSpan);
  }















  
  public JLabel appendI15d(String resourceKey, Component c1, Component c2, Component c3) {
    return append(getResourceString(resourceKey), c1, c2, c3);
  }
















  
  public JLabel appendI15d(String resourceKey, Component c1, Component c2, Component c3, Component c4) {
    return append(getResourceString(resourceKey), c1, c2, c3, c4);
  }









  
  public JLabel appendTitle(String textWithMnemonic) {
    JLabel titleLabel = getComponentFactory().createTitle(textWithMnemonic);
    append(titleLabel);
    return titleLabel;
  }








  
  public JLabel appendI15dTitle(String resourceKey) {
    return appendTitle(getResourceString(resourceKey));
  }








  
  public JComponent appendSeparator() {
    return appendSeparator("");
  }







  
  public JComponent appendSeparator(String text) {
    ensureCursorColumnInGrid();
    ensureHasGapRow(this.paragraphGapSpec);
    ensureHasComponentLine();
    
    setColumn(super.getLeadingColumn());
    int columnSpan = getColumnCount();
    setColumnSpan(getColumnCount());
    JComponent titledSeparator = addSeparator(text);
    setColumnSpan(1);
    nextColumn(columnSpan);
    return titledSeparator;
  }








  
  public JComponent appendI15dSeparator(String resourceKey) {
    return appendSeparator(getResourceString(resourceKey));
  }










  
  protected int getLeadingColumn() {
    int column = super.getLeadingColumn();
    return column + this.leadingColumnOffset * getColumnIncrementSign();
  }








  
  private void ensureCursorColumnInGrid() {
    if ((isLeftToRight() && getColumn() > getColumnCount()) || (!isLeftToRight() && getColumn() < 1))
    {
      nextLine();
    }
  }








  
  private void ensureHasGapRow(RowSpec gapRowSpec) {
    if (getRow() == 1 || getRow() <= getRowCount()) {
      return;
    }
    
    if (getRow() <= getRowCount()) {
      RowSpec rowSpec = getCursorRowSpec();
      if (rowSpec == gapRowSpec) {
        return;
      }
    } 
    appendRow(gapRowSpec);
    nextLine();
  }





  
  private void ensureHasComponentLine() {
    if (getRow() <= getRowCount()) {
      return;
    }
    appendRow(this.defaultRowSpec);
    if (this.rowGroupingEnabled) {
      getLayout().addGroupedRow(getRow());
    }
  }






  
  private RowSpec getCursorRowSpec() {
    return getLayout().getRowSpec(getRow());
  }
}
