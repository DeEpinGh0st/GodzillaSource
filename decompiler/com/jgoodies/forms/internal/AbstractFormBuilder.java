package com.jgoodies.forms.internal;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Component;
import java.awt.ComponentOrientation;
import javax.swing.JPanel;





































































public abstract class AbstractFormBuilder<B extends AbstractFormBuilder<B>>
  extends AbstractBuilder<B>
{
  private boolean leftToRight;
  
  protected AbstractFormBuilder(FormLayout layout, JPanel panel) {
    super(layout, panel);
    ComponentOrientation orientation = panel.getComponentOrientation();
    this.leftToRight = (orientation.isLeftToRight() || !orientation.isHorizontal());
  }















  
  public final boolean isLeftToRight() {
    return this.leftToRight;
  }











  
  public final void setLeftToRight(boolean b) {
    this.leftToRight = b;
  }








  
  public final int getColumn() {
    return this.currentCellConstraints.gridX;
  }






  
  public final void setColumn(int column) {
    this.currentCellConstraints.gridX = column;
  }






  
  public final int getRow() {
    return this.currentCellConstraints.gridY;
  }






  
  public final void setRow(int row) {
    this.currentCellConstraints.gridY = row;
  }






  
  public final void setColumnSpan(int columnSpan) {
    this.currentCellConstraints.gridWidth = columnSpan;
  }






  
  public final void setRowSpan(int rowSpan) {
    this.currentCellConstraints.gridHeight = rowSpan;
  }







  
  public final void setOrigin(int column, int row) {
    setColumn(column);
    setRow(row);
  }







  
  public final void setExtent(int columnSpan, int rowSpan) {
    setColumnSpan(columnSpan);
    setRowSpan(rowSpan);
  }










  
  public final void setBounds(int column, int row, int columnSpan, int rowSpan) {
    setColumn(column);
    setRow(row);
    setColumnSpan(columnSpan);
    setRowSpan(rowSpan);
  }








  
  public final void setHAlignment(CellConstraints.Alignment alignment) {
    (cellConstraints()).hAlign = alignment;
  }





  
  public final void setVAlignment(CellConstraints.Alignment alignment) {
    (cellConstraints()).vAlign = alignment;
  }








  
  public final void setAlignment(CellConstraints.Alignment hAlign, CellConstraints.Alignment vAlign) {
    setHAlignment(hAlign);
    setVAlignment(vAlign);
  }






  
  public final void nextColumn() {
    nextColumn(1);
  }






  
  public final void nextColumn(int columns) {
    (cellConstraints()).gridX += columns * getColumnIncrementSign();
  }




  
  public final void nextRow() {
    nextRow(1);
  }






  
  public final void nextRow(int rows) {
    (cellConstraints()).gridY += rows;
  }





  
  public final void nextLine() {
    nextLine(1);
  }







  
  public final void nextLine(int lines) {
    nextRow(lines);
    setColumn(getLeadingColumn());
  }










  
  public final void appendColumn(ColumnSpec columnSpec) {
    getLayout().appendColumn(columnSpec);
  }









  
  public final void appendColumn(String encodedColumnSpec) {
    appendColumn(ColumnSpec.decode(encodedColumnSpec));
  }








  
  public final void appendGlueColumn() {
    appendColumn(FormSpecs.GLUE_COLSPEC);
  }











  
  public final void appendLabelComponentsGapColumn() {
    appendColumn(FormSpecs.LABEL_COMPONENT_GAP_COLSPEC);
  }








  
  public final void appendRelatedComponentsGapColumn() {
    appendColumn(FormSpecs.RELATED_GAP_COLSPEC);
  }








  
  public final void appendUnrelatedComponentsGapColumn() {
    appendColumn(FormSpecs.UNRELATED_GAP_COLSPEC);
  }










  
  public final void appendRow(RowSpec rowSpec) {
    getLayout().appendRow(rowSpec);
  }









  
  public final void appendRow(String encodedRowSpec) {
    appendRow(RowSpec.decode(encodedRowSpec));
  }








  
  public final void appendGlueRow() {
    appendRow(FormSpecs.GLUE_ROWSPEC);
  }








  
  public final void appendRelatedComponentsGapRow() {
    appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
  }








  
  public final void appendUnrelatedComponentsGapRow() {
    appendRow(FormSpecs.UNRELATED_GAP_ROWSPEC);
  }










  
  public final void appendParagraphGapRow() {
    appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
  }










  
  public Component add(Component component, CellConstraints cellConstraints) {
    getPanel().add(component, cellConstraints);
    return component;
  }








  
  public final Component add(Component component, String encodedCellConstraints) {
    getPanel().add(component, new CellConstraints(encodedCellConstraints));
    return component;
  }














  
  public final Component add(Component component) {
    add(component, cellConstraints());
    return component;
  }









  
  protected final CellConstraints cellConstraints() {
    return this.currentCellConstraints;
  }









  
  protected int getLeadingColumn() {
    return isLeftToRight() ? 1 : getColumnCount();
  }







  
  protected final int getColumnIncrementSign() {
    return isLeftToRight() ? 1 : -1;
  }









  
  protected final CellConstraints createLeftAdjustedConstraints(int columnSpan) {
    int firstColumn = isLeftToRight() ? getColumn() : (getColumn() + 1 - columnSpan);

    
    return new CellConstraints(firstColumn, getRow(), columnSpan, (cellConstraints()).gridHeight);
  }
}
