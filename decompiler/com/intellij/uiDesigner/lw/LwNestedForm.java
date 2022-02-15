package com.intellij.uiDesigner.lw;

import org.jdom.Element;



















public class LwNestedForm
  extends LwComponent
{
  private String myFormFileName;
  
  public LwNestedForm() {
    super("");
  }
  
  public void read(Element element, PropertiesProvider provider) throws Exception {
    this.myFormFileName = LwXmlReader.getRequiredString(element, "form-file");
    readBase(element);
    readConstraints(element);
  }
  
  public String getFormFileName() {
    return this.myFormFileName;
  }
}
