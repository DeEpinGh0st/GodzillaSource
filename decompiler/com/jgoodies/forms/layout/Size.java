package com.jgoodies.forms.layout;

import java.awt.Container;
import java.util.List;

public interface Size {
  int maximumSize(Container paramContainer, List paramList, FormLayout.Measure paramMeasure1, FormLayout.Measure paramMeasure2, FormLayout.Measure paramMeasure3);
  
  boolean compressible();
  
  String encode();
}
