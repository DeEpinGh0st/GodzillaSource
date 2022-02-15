package com.intellij.uiDesigner.lw;

import java.awt.Dimension;
import org.jdom.Element;


















public final class LwIntroDimensionProperty
  extends LwIntrospectedProperty
{
  public LwIntroDimensionProperty(String name) {
    super(name, "java.awt.Dimension");
  }
  
  public Object read(Element element) throws Exception {
    int width = LwXmlReader.getRequiredInt(element, "width");
    int height = LwXmlReader.getRequiredInt(element, "height");
    return new Dimension(width, height);
  }
}
