package org.fife.rsta.ac.js.completion;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


public class JSClassCompletion
  extends BasicCompletion
  implements JSCompletion
{
  private ClassFile cf;
  private boolean qualified;
  
  public JSClassCompletion(CompletionProvider provider, ClassFile cf, boolean qualified) {
    super(provider, ((SourceCompletionProvider)provider).getTypesFactory().convertJavaScriptType(cf.getClassName(true), qualified));
    this.cf = cf;
    this.qualified = qualified;
    setRelevance(2);
  }







  
  public int compareTo(Completion c2) {
    if (c2 == this) {
      return 0;
    }
    
    if (c2.toString().equalsIgnoreCase(toString()) && 
      c2 instanceof JSClassCompletion) {
      JSClassCompletion jsc2 = (JSClassCompletion)c2;
      return getReplacementText().compareTo(jsc2.getReplacementText());
    } 
    
    return super.compareTo(c2);
  }


  
  public boolean equals(Object obj) {
    return (obj instanceof JSClassCompletion && ((JSClassCompletion)obj)
      .getReplacementText().equals(getReplacementText()));
  }

  
  public String getAlreadyEntered(JTextComponent comp) {
    String temp = getProvider().getAlreadyEnteredText(comp);
    
    int lastDot = JavaScriptHelper.findLastIndexOfJavaScriptIdentifier(temp);
    if (lastDot > -1) {
      return temp.substring(lastDot + 1);
    }
    if (temp.contains("new"))
    {
      return "";
    }
    
    return temp;
  }








  
  public String getClassName(boolean fullyQualified) {
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(this.cf.getClassName(fullyQualified), fullyQualified);
  }


  
  public Icon getIcon() {
    return IconFactory.getIcon("default_class");
  }







  
  public String getPackageName() {
    return this.cf.getPackageName();
  }



  
  public String getSummary() {
    SourceCompletionProvider scp = (SourceCompletionProvider)getProvider();
    SourceLocation loc = scp.getSourceLocForClass(this.cf.getClassName(true));
    
    if (loc != null) {
      
      CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, this.cf);
      if (cu != null) {
        Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
        while (i.hasNext()) {
          TypeDeclaration td = i.next();
          String typeName = td.getName();
          
          if (typeName.equals(this.cf.getClassName(false))) {
            String summary = td.getDocComment();
            
            if (summary != null && summary.startsWith("/**")) {
              return Util.docCommentToHtml(summary);
            }
          } 
        } 
      } 
    } 


    
    return ((SourceCompletionProvider)getProvider()).getTypesFactory().convertJavaScriptType(this.cf.getClassName(true), this.qualified);
  }



  
  public String getToolTipText() {
    return "type " + getReplacementText();
  }


  
  public int hashCode() {
    return getReplacementText().hashCode();
  }

  
  public void rendererText(Graphics g, int x, int y, boolean selected) {
    String s = this.cf.getClassName(false);
    g.drawString(s, x, y);
    FontMetrics fm = g.getFontMetrics();
    int newX = x + fm.stringWidth(s);
    if (this.cf.isDeprecated()) {
      int midY = y + fm.getDescent() - fm.getHeight() / 2;
      g.drawLine(x, midY, newX, midY);
    } 
    x = newX;
    
    if (this.qualified) {
      
      s = " - ";
      g.drawString(s, x, y);
      x += fm.stringWidth(s);
      
      String pkgName = this.cf.getClassName(true);
      int lastIndexOf = pkgName.lastIndexOf('.');
      if (lastIndexOf != -1) {
        pkgName = pkgName.substring(0, lastIndexOf);
        Color origColor = g.getColor();
        if (!selected) {
          g.setColor(Color.GRAY);
        }
        g.drawString(pkgName, x, y);
        if (!selected) {
          g.setColor(origColor);
        }
      } 
    } 
  }



  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.cf.getClassName(fullyQualified);
  }


  
  public String getLookupName() {
    return getReplacementText();
  }


  
  public String getType(boolean qualified) {
    return getClassName(qualified);
  }
}
