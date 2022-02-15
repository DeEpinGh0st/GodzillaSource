package com.intellij.uiDesigner.lw;

import org.jdom.Element;

















public class LwIntroFontProperty
  extends LwIntrospectedProperty
{
  public LwIntroFontProperty(String name) {
    super(name, "java.awt.Font");
  }
  
  public Object read(Element element) throws Exception {
    return LwXmlReader.getFontDescriptor(element);
  }
}
