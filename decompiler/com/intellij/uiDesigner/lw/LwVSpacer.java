package com.intellij.uiDesigner.lw;

import org.jdom.Element;


















public final class LwVSpacer
  extends LwAtomicComponent
{
  public LwVSpacer() throws Exception {
    super("com.intellij.uiDesigner.core.Spacer");
  }
  
  public void read(Element element, PropertiesProvider provider) throws Exception {
    readBase(element);
    readConstraints(element);
  }
}
