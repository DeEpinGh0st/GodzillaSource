package org.fife.rsta.ac;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;




































public class GoToMemberAction
  extends TextAction
{
  private Class<?> outlineTreeClass;
  
  public GoToMemberAction(Class<?> outlineTreeClass) {
    super("GoToType");
    this.outlineTreeClass = outlineTreeClass;
  }


  
  public void actionPerformed(ActionEvent e) {
    AbstractSourceTree tree = createTree();
    if (tree == null) {
      UIManager.getLookAndFeel().provideErrorFeedback(null);
      return;
    } 
    JTextComponent tc = getTextComponent(e);
    if (tc instanceof RSyntaxTextArea) {
      RSyntaxTextArea textArea = (RSyntaxTextArea)tc;
      Window parent = SwingUtilities.getWindowAncestor((Component)textArea);
      GoToMemberWindow gtmw = new GoToMemberWindow(parent, textArea, tree);
      setLocationBasedOn(gtmw, textArea);
      gtmw.setVisible(true);
    } else {
      
      UIManager.getLookAndFeel().provideErrorFeedback(null);
    } 
  }






  
  private AbstractSourceTree createTree() {
    AbstractSourceTree tree = null;
    try {
      tree = (AbstractSourceTree)this.outlineTreeClass.newInstance();
      tree.setSorted(true);
    } catch (RuntimeException re) {
      throw re;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return tree;
  }








  
  private void setLocationBasedOn(GoToMemberWindow gtmw, RSyntaxTextArea textArea) {
    Rectangle visibleRect = textArea.getVisibleRect();
    Dimension gtmwPS = gtmw.getPreferredSize();
    int x = visibleRect.x + (visibleRect.width - gtmwPS.width) / 2;
    int y = visibleRect.y + (visibleRect.height - gtmwPS.height) / 2;
    Point p = new Point(x, y);
    SwingUtilities.convertPointToScreen(p, (Component)textArea);
    gtmw.setLocation(p);
  }
}
