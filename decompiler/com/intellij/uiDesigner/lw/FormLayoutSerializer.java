package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.compiler.Utils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.LayoutManager;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;






















public class FormLayoutSerializer
  extends GridLayoutSerializer
{
  public static FormLayoutSerializer INSTANCE = new FormLayoutSerializer();
  
  public static CellConstraints.Alignment[] ourHorizontalAlignments = new CellConstraints.Alignment[] { CellConstraints.LEFT, CellConstraints.CENTER, CellConstraints.RIGHT, CellConstraints.FILL };

  
  public static CellConstraints.Alignment[] ourVerticalAlignments = new CellConstraints.Alignment[] { CellConstraints.TOP, CellConstraints.CENTER, CellConstraints.BOTTOM, CellConstraints.FILL };


  
  void readLayout(Element element, LwContainer container) {
    FormLayout layout = new FormLayout();
    List rowSpecs = element.getChildren("rowspec", element.getNamespace());
    for (Iterator iterator = rowSpecs.iterator(); iterator.hasNext(); ) {
      Element rowSpecElement = iterator.next();
      String spec = LwXmlReader.getRequiredString(rowSpecElement, "value");
      layout.appendRow(new RowSpec(spec));
    } 
    
    List colSpecs = element.getChildren("colspec", element.getNamespace());
    for (Iterator iterator1 = colSpecs.iterator(); iterator1.hasNext(); ) {
      Element colSpecElement = iterator1.next();
      String spec = LwXmlReader.getRequiredString(colSpecElement, "value");
      layout.appendColumn(new ColumnSpec(spec));
    } 
    
    int[][] rowGroups = readGroups(element, "rowgroup");
    int[][] colGroups = readGroups(element, "colgroup");
    if (rowGroups != null) {
      layout.setRowGroups(rowGroups);
    }
    if (colGroups != null) {
      layout.setColumnGroups(colGroups);
    }
    container.setLayout((LayoutManager)layout);
  }
  
  private static int[][] readGroups(Element element, String elementName) {
    List groupElements = element.getChildren(elementName, element.getNamespace());
    if (groupElements.size() == 0) return (int[][])null; 
    int[][] groups = new int[groupElements.size()][];
    for (int i = 0; i < groupElements.size(); i++) {
      Element groupElement = groupElements.get(i);
      List groupMembers = groupElement.getChildren("member", element.getNamespace());
      groups[i] = new int[groupMembers.size()];
      for (int j = 0; j < groupMembers.size(); j++) {
        groups[i][j] = LwXmlReader.getRequiredInt((Element)groupMembers.get(j), "index");
      }
    } 
    return groups;
  }
  
  void readChildConstraints(Element constraintsElement, LwComponent component) {
    super.readChildConstraints(constraintsElement, component);
    CellConstraints cc = new CellConstraints();
    Element formsElement = LwXmlReader.getChild(constraintsElement, "forms");
    if (formsElement != null) {
      if (formsElement.getAttributeValue("top") != null) {
        cc.insets = LwXmlReader.readInsets(formsElement);
      }
      if (!LwXmlReader.getOptionalBoolean(formsElement, "defaultalign-horz", true)) {
        cc.hAlign = ourHorizontalAlignments[Utils.alignFromConstraints(component.getConstraints(), true)];
      }
      if (!LwXmlReader.getOptionalBoolean(formsElement, "defaultalign-vert", true)) {
        cc.vAlign = ourVerticalAlignments[Utils.alignFromConstraints(component.getConstraints(), false)];
      }
    } 
    component.setCustomLayoutConstraints(cc);
  }
}
