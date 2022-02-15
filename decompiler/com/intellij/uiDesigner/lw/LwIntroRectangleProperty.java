package com.intellij.uiDesigner.lw;

import java.awt.Rectangle;
import org.jdom.Element;


















public final class LwIntroRectangleProperty
  extends LwIntrospectedProperty
{
  public LwIntroRectangleProperty(String name) {
    super(name, "java.awt.Rectangle");
  }
  
  public Object read(Element element) throws Exception {
    int x = LwXmlReader.getRequiredInt(element, "x");
    int y = LwXmlReader.getRequiredInt(element, "y");
    int width = LwXmlReader.getRequiredInt(element, "width");
    int height = LwXmlReader.getRequiredInt(element, "height");
    return new Rectangle(x, y, width, height);
  }
}
