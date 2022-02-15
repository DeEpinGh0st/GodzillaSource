package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;
import org.fife.ui.autocomplete.CompletionProvider;




















class PackageNameCompletion
  extends AbstractJavaSourceCompletion
{
  public PackageNameCompletion(CompletionProvider provider, String text, String alreadyEntered) {
    super(provider, text.substring(text.lastIndexOf('.') + 1));
  }


  
  public boolean equals(Object obj) {
    return (obj instanceof PackageNameCompletion && ((PackageNameCompletion)obj)
      .getReplacementText().equals(getReplacementText()));
  }


  
  public Icon getIcon() {
    return IconFactory.get().getIcon("packageIcon");
  }


  
  public int hashCode() {
    return getReplacementText().hashCode();
  }


  
  public void rendererText(Graphics g, int x, int y, boolean selected) {
    g.drawString(getInputText(), x, y);
  }
}
