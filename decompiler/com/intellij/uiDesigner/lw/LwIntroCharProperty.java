package com.intellij.uiDesigner.lw;

import org.jdom.Element;



public final class LwIntroCharProperty
  extends LwIntrospectedProperty
{
  public LwIntroCharProperty(String name) {
    super(name, Character.class.getName());
  }
  
  public Object read(Element element) throws Exception {
    return Character.valueOf(LwXmlReader.getRequiredString(element, "value").charAt(0));
  }
}
