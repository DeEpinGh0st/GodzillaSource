package org.fife.ui.autocomplete;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import org.fife.ui.rsyntaxtextarea.PopupWindowDecorator;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;













































class ParameterizedCompletionDescriptionToolTip
{
  private AutoCompletion ac;
  private JWindow tooltip;
  private JLabel descLabel;
  private ParameterizedCompletion pc;
  private boolean overflow;
  
  ParameterizedCompletionDescriptionToolTip(Window owner, ParameterizedCompletionContext context, AutoCompletion ac, ParameterizedCompletion pc) {
    this.tooltip = new JWindow(owner);
    
    this.ac = ac;
    this.pc = pc;
    
    this.descLabel = new JLabel();
    this.descLabel.setBorder(BorderFactory.createCompoundBorder(
          TipUtil.getToolTipBorder(), 
          BorderFactory.createEmptyBorder(2, 5, 2, 5)));
    this.descLabel.setOpaque(true);
    this.descLabel.setBackground(TipUtil.getToolTipBackground());





    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(this.descLabel);
    this.tooltip.setContentPane(panel);

    
    PopupWindowDecorator decorator = PopupWindowDecorator.get();
    if (decorator != null) {
      decorator.decorate(this.tooltip);
    }
    
    updateText(0);
    
    this.tooltip.setFocusableWindowState(false);
  }








  
  public boolean isVisible() {
    return this.tooltip.isVisible();
  }












  
  public void setLocationRelativeTo(Rectangle r) {
    Rectangle screenBounds = Util.getScreenBoundsForPoint(r.x, r.y);


    
    int y = r.y - 5 - this.tooltip.getHeight();
    if (y < 0) {
      y = r.y + r.height + 5;
    }


    
    int x = r.x;
    if (x < screenBounds.x) {
      x = screenBounds.x;
    }
    else if (x + this.tooltip.getWidth() > screenBounds.x + screenBounds.width) {
      x = screenBounds.x + screenBounds.width - this.tooltip.getWidth();
    } 
    
    this.tooltip.setLocation(x, y);
    EventQueue.invokeLater(this.tooltip::pack);
  }







  
  public void setVisible(boolean visible) {
    this.tooltip.setVisible(visible);
  }









  
  public boolean updateText(int selectedParam) {
    StringBuilder sb = new StringBuilder("<html>");
    int paramCount = this.pc.getParamCount();
    
    if (this.overflow) {
      if (selectedParam < paramCount) {
        String temp = this.pc.getParam(Math.min(paramCount - 1, selectedParam)).toString();
        sb.append("...<b>")
          .append(RSyntaxUtilities.escapeForHtml(temp, "<br>", false))
          .append("</b>...");


        
        if (!isVisible()) {
          setVisible(true);
        }
      } else {
        
        setVisible(false);
      }
    
    } else {
      
      for (int i = 0; i < paramCount; i++) {
        
        if (i == selectedParam) {
          sb.append("<b>");
        }



        
        String temp = this.pc.getParam(i).toString();
        sb.append(RSyntaxUtilities.escapeForHtml(temp, "<br>", false));
        
        if (i == selectedParam) {
          sb.append("</b>");
        }
        if (i < paramCount - 1) {
          sb.append(this.pc.getProvider().getParameterListSeparator());
        }
      } 
    } 

    
    if (selectedParam >= 0 && selectedParam < paramCount) {
      
      ParameterizedCompletion.Parameter param = this.pc.getParam(selectedParam);
      String desc = param.getDescription();
      if (desc != null) {
        sb.append("<br>");
        sb.append(desc);
      } 
    } 
    
    this.descLabel.setText(sb.toString());
    if (!this.overflow && sb.length() > this.ac.getParameterDescriptionTruncateThreshold()) {
      this.overflow = true;
      updateText(selectedParam);
    } else {
      
      this.overflow = false;
      this.tooltip.pack();
    } 
    
    return true;
  }






  
  public void updateUI() {
    SwingUtilities.updateComponentTreeUI(this.tooltip);
  }
}
