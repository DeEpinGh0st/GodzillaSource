package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.shared.XYLayoutManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import org.jdom.Element;


















public class XYLayoutSerializer
  extends LayoutSerializer
{
  static XYLayoutSerializer INSTANCE = new XYLayoutSerializer();



  
  void readLayout(Element element, LwContainer container) {
    container.setLayout((LayoutManager)new XYLayoutManager());
  }
  
  void readChildConstraints(Element constraintsElement, LwComponent component) {
    Element xyElement = LwXmlReader.getChild(constraintsElement, "xy");
    if (xyElement != null)
      component.setBounds(new Rectangle(LwXmlReader.getRequiredInt(xyElement, "x"), LwXmlReader.getRequiredInt(xyElement, "y"), LwXmlReader.getRequiredInt(xyElement, "width"), LwXmlReader.getRequiredInt(xyElement, "height"))); 
  }
}
