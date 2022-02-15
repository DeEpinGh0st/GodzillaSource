package com.kitfox.svg.app.beans;

import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;










































public class ProportionalLayoutPanel
  extends JPanel
{
  public static final long serialVersionUID = 1L;
  float topMargin;
  float bottomMargin;
  float leftMargin;
  float rightMargin;
  private JPanel jPanel1;
  
  public ProportionalLayoutPanel() {
    initComponents();
  }


  
  public void addNotify() {
    super.addNotify();
    
    Rectangle rect = getBounds();
    JOptionPane.showMessageDialog(this, "" + rect);
  }








  
  private void initComponents() {
    this.jPanel1 = new JPanel();
    
    setLayout((LayoutManager)null);
    
    addComponentListener(new ComponentAdapter()
        {
          
          public void componentResized(ComponentEvent evt)
          {
            ProportionalLayoutPanel.this.formComponentResized(evt);
          }

          
          public void componentShown(ComponentEvent evt) {
            ProportionalLayoutPanel.this.formComponentShown(evt);
          }
        });
    
    add(this.jPanel1);
    this.jPanel1.setBounds(80, 90, 280, 160);
  }



  
  private void formComponentShown(ComponentEvent evt) {
    JOptionPane.showMessageDialog(this, "" + getWidth() + ", " + getHeight());
  }
  
  private void formComponentResized(ComponentEvent evt) {}
}
