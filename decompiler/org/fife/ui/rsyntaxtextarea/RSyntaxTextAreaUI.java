package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaUI;


















public class RSyntaxTextAreaUI
  extends RTextAreaUI
{
  private static final String SHARED_ACTION_MAP_NAME = "RSyntaxTextAreaUI.actionMap";
  private static final String SHARED_INPUT_MAP_NAME = "RSyntaxTextAreaUI.inputMap";
  private static final EditorKit DEFAULT_KIT = (EditorKit)new RSyntaxTextAreaEditorKit();

  
  public static ComponentUI createUI(JComponent ta) {
    return (ComponentUI)new RSyntaxTextAreaUI(ta);
  }




  
  public RSyntaxTextAreaUI(JComponent rSyntaxTextArea) {
    super(rSyntaxTextArea);
  }








  
  public View create(Element elem) {
    RTextArea c = getRTextArea();
    if (c instanceof RSyntaxTextArea) {
      View v; RSyntaxTextArea area = (RSyntaxTextArea)c;
      
      if (area.getLineWrap()) {
        v = new WrappedSyntaxView(elem);
      } else {
        
        v = new SyntaxView(elem);
      } 
      return v;
    } 
    return null;
  }







  
  protected Highlighter createHighlighter() {
    return (Highlighter)new RSyntaxTextAreaHighlighter();
  }









  
  protected String getActionMapName() {
    return "RSyntaxTextAreaUI.actionMap";
  }









  
  public EditorKit getEditorKit(JTextComponent tc) {
    return DEFAULT_KIT;
  }










  
  protected InputMap getRTextAreaInputMap() {
    RSyntaxTextAreaDefaultInputMap rSyntaxTextAreaDefaultInputMap;
    InputMap map = new InputMapUIResource();
    InputMap shared = (InputMap)UIManager.get("RSyntaxTextAreaUI.inputMap");
    if (shared == null) {
      rSyntaxTextAreaDefaultInputMap = new RSyntaxTextAreaDefaultInputMap();
      UIManager.put("RSyntaxTextAreaUI.inputMap", rSyntaxTextAreaDefaultInputMap);
    } 


    
    map.setParent((InputMap)rSyntaxTextAreaDefaultInputMap);
    return map;
  }


  
  protected void paintEditorAugmentations(Graphics g) {
    super.paintEditorAugmentations(g);
    paintMatchedBracket(g);
  }






  
  protected void paintMatchedBracket(Graphics g) {
    RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
    if (rsta.isBracketMatchingEnabled()) {
      Rectangle match = rsta.getMatchRectangle();
      if (match != null) {
        paintMatchedBracketImpl(g, rsta, match);
      }
      if (rsta.getPaintMatchedBracketPair()) {
        Rectangle dotRect = rsta.getDotRectangle();
        if (dotRect != null) {
          paintMatchedBracketImpl(g, rsta, dotRect);
        }
      } 
    } 
  }




  
  protected void paintMatchedBracketImpl(Graphics g, RSyntaxTextArea rsta, Rectangle r) {
    if (rsta.getAnimateBracketMatching()) {
      Color bg = rsta.getMatchedBracketBGColor();
      int arcWH = 5;
      if (bg != null) {
        g.setColor(bg);
        g.fillRoundRect(r.x, r.y, r.width, r.height - 1, 5, 5);
      } 
      g.setColor(rsta.getMatchedBracketBorderColor());
      g.drawRoundRect(r.x, r.y, r.width, r.height - 1, 5, 5);
    } else {
      
      Color bg = rsta.getMatchedBracketBGColor();
      if (bg != null) {
        g.setColor(bg);
        g.fillRect(r.x, r.y, r.width, r.height - 1);
      } 
      g.setColor(rsta.getMatchedBracketBorderColor());
      g.drawRect(r.x, r.y, r.width, r.height - 1);
    } 
  }









  
  protected void propertyChange(PropertyChangeEvent e) {
    String name = e.getPropertyName();


    
    if (name.equals("RSTA.syntaxScheme")) {
      modelChanged();
    
    }
    else {
      
      super.propertyChange(e);
    } 
  }






  
  public void refreshSyntaxHighlighting() {
    modelChanged();
  }









  
  public int yForLine(int line) throws BadLocationException {
    Rectangle alloc = getVisibleEditorRect();
    if (alloc != null) {
      RSTAView view = (RSTAView)getRootView((JTextComponent)this.textArea).getView(0);
      return view.yForLine(alloc, line);
    } 
    return -1;
  }








  
  public int yForLineContaining(int offs) throws BadLocationException {
    Rectangle alloc = getVisibleEditorRect();
    if (alloc != null) {
      RSTAView view = (RSTAView)getRootView((JTextComponent)this.textArea).getView(0);
      return view.yForLineContaining(alloc, offs);
    } 
    return -1;
  }
}
