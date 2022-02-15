package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.Insets;
import java.awt.LayoutManager;
import org.jdom.Element;






















public class GridLayoutSerializer
  extends LayoutSerializer
{
  public static GridLayoutSerializer INSTANCE = new GridLayoutSerializer();
  
  void readLayout(Element element, LwContainer container) {
    int rowCount = LwXmlReader.getRequiredInt(element, "row-count");
    int columnCount = LwXmlReader.getRequiredInt(element, "column-count");
    
    int hGap = LwXmlReader.getOptionalInt(element, "hgap", -1);
    int vGap = LwXmlReader.getOptionalInt(element, "vgap", -1);

    
    boolean sameSizeHorizontally = LwXmlReader.getOptionalBoolean(element, "same-size-horizontally", false);
    boolean sameSizeVertically = LwXmlReader.getOptionalBoolean(element, "same-size-vertically", false);
    
    Element marginElement = LwXmlReader.getRequiredChild(element, "margin");
    Insets margin = new Insets(LwXmlReader.getRequiredInt(marginElement, "top"), LwXmlReader.getRequiredInt(marginElement, "left"), LwXmlReader.getRequiredInt(marginElement, "bottom"), LwXmlReader.getRequiredInt(marginElement, "right"));





    
    GridLayoutManager layout = new GridLayoutManager(rowCount, columnCount);
    layout.setMargin(margin);
    layout.setVGap(vGap);
    layout.setHGap(hGap);
    layout.setSameSizeHorizontally(sameSizeHorizontally);
    layout.setSameSizeVertically(sameSizeVertically);
    container.setLayout((LayoutManager)layout);
  }

  
  void readChildConstraints(Element constraintsElement, LwComponent component) {
    Element gridElement = LwXmlReader.getChild(constraintsElement, "grid");
    if (gridElement != null) {
      GridConstraints constraints = new GridConstraints();
      
      constraints.setRow(LwXmlReader.getRequiredInt(gridElement, "row"));
      constraints.setColumn(LwXmlReader.getRequiredInt(gridElement, "column"));
      constraints.setRowSpan(LwXmlReader.getRequiredInt(gridElement, "row-span"));
      constraints.setColSpan(LwXmlReader.getRequiredInt(gridElement, "col-span"));
      constraints.setVSizePolicy(LwXmlReader.getRequiredInt(gridElement, "vsize-policy"));
      constraints.setHSizePolicy(LwXmlReader.getRequiredInt(gridElement, "hsize-policy"));
      constraints.setAnchor(LwXmlReader.getRequiredInt(gridElement, "anchor"));
      constraints.setFill(LwXmlReader.getRequiredInt(gridElement, "fill"));
      constraints.setIndent(LwXmlReader.getOptionalInt(gridElement, "indent", 0));
      constraints.setUseParentLayout(LwXmlReader.getOptionalBoolean(gridElement, "use-parent-layout", false));

      
      Element minSizeElement = LwXmlReader.getChild(gridElement, "minimum-size");
      if (minSizeElement != null) {
        constraints.myMinimumSize.width = LwXmlReader.getRequiredInt(minSizeElement, "width");
        constraints.myMinimumSize.height = LwXmlReader.getRequiredInt(minSizeElement, "height");
      } 

      
      Element prefSizeElement = LwXmlReader.getChild(gridElement, "preferred-size");
      if (prefSizeElement != null) {
        constraints.myPreferredSize.width = LwXmlReader.getRequiredInt(prefSizeElement, "width");
        constraints.myPreferredSize.height = LwXmlReader.getRequiredInt(prefSizeElement, "height");
      } 

      
      Element maxSizeElement = LwXmlReader.getChild(gridElement, "maximum-size");
      if (maxSizeElement != null) {
        constraints.myMaximumSize.width = LwXmlReader.getRequiredInt(maxSizeElement, "width");
        constraints.myMaximumSize.height = LwXmlReader.getRequiredInt(maxSizeElement, "height");
      } 
      
      component.getConstraints().restore(constraints);
    } 
  }
}
