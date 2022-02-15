package org.fife.rsta.ac.java;

import java.awt.Graphics;
import org.fife.ui.autocomplete.Completion;

public interface JavaSourceCompletion extends Completion {
  boolean equals(Object paramObject);
  
  void rendererText(Graphics paramGraphics, int paramInt1, int paramInt2, boolean paramBoolean);
}
