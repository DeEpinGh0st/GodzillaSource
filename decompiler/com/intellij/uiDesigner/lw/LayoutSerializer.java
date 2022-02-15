package com.intellij.uiDesigner.lw;

import org.jdom.Element;

public abstract class LayoutSerializer {
  abstract void readLayout(Element paramElement, LwContainer paramLwContainer);
  
  abstract void readChildConstraints(Element paramElement, LwComponent paramLwComponent);
}
