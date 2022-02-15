package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.ui.autocomplete.CompletionProvider;


















class LocalVariableCompletion
  extends AbstractJavaSourceCompletion
{
  private LocalVariable localVar;
  private static final int RELEVANCE = 4;
  
  public LocalVariableCompletion(CompletionProvider provider, LocalVariable localVar) {
    super(provider, localVar.getName());
    this.localVar = localVar;
    setRelevance(4);
  }


  
  public boolean equals(Object obj) {
    return (obj instanceof LocalVariableCompletion && ((LocalVariableCompletion)obj)
      .getReplacementText()
      .equals(getReplacementText()));
  }


  
  public Icon getIcon() {
    return IconFactory.get().getIcon("localVariableIcon");
  }


  
  public String getToolTipText() {
    return this.localVar.getType() + " " + this.localVar.getName();
  }


  
  public int hashCode() {
    return getReplacementText().hashCode();
  }


  
  public void rendererText(Graphics g, int x, int y, boolean selected) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.localVar.getName());
    sb.append(" : ");
    sb.append(this.localVar.getType());
    g.drawString(sb.toString(), x, y);
  }
}
