package com.intellij.uiDesigner.lw;

import java.awt.FlowLayout;
import org.jdom.Element;




















public class FlowLayoutSerializer
  extends LayoutSerializer
{
  public static final FlowLayoutSerializer INSTANCE = new FlowLayoutSerializer();



  
  void readLayout(Element element, LwContainer container) {
    int hGap = LwXmlReader.getOptionalInt(element, "hgap", 5);
    int vGap = LwXmlReader.getOptionalInt(element, "vgap", 5);
    int flowAlign = LwXmlReader.getOptionalInt(element, "flow-align", 1);
    container.setLayout(new FlowLayout(flowAlign, hGap, vGap));
  }
  
  void readChildConstraints(Element constraintsElement, LwComponent component) {}
}
