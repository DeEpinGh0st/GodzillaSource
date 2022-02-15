package com.intellij.uiDesigner.lw;

import java.awt.BorderLayout;
import org.jdom.Element;




















public class BorderLayoutSerializer
  extends LayoutSerializer
{
  public static final BorderLayoutSerializer INSTANCE = new BorderLayoutSerializer();



  
  void readLayout(Element element, LwContainer container) {
    int hGap = LwXmlReader.getOptionalInt(element, "hgap", 0);
    int vGap = LwXmlReader.getOptionalInt(element, "vgap", 0);
    container.setLayout(new BorderLayout(hGap, vGap));
  }
  
  void readChildConstraints(Element constraintsElement, LwComponent component) {
    component.setCustomLayoutConstraints(LwXmlReader.getRequiredString(constraintsElement, "border-constraint"));
  }
}
