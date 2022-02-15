package com.kitfox.svg.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;




























public class VersionDialog
  extends JDialog
{
  public static final long serialVersionUID = 1L;
  final boolean verbose;
  private JButton bn_close;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JTextPane textpane_text;
  
  public VersionDialog(Frame parent, boolean modal, boolean verbose) {
    super(parent, modal);
    initComponents();
    
    this.verbose = verbose;
    
    this.textpane_text.setContentType("text/html");
    
    StringBuffer sb = new StringBuffer();
    
    try {
      URL url = getClass().getResource("/res/help/about/about.html");
      if (verbose)
      {
        System.err.println("" + getClass() + " trying to load about html " + url);
      }
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      
      while (true) {
        String line = reader.readLine();
        if (line == null)
          break;  sb.append(line);
      } 
      
      this.textpane_text.setText(sb.toString());
    }
    catch (Exception e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
    } 
  }








  
  private void initComponents() {
    this.jPanel1 = new JPanel();
    this.textpane_text = new JTextPane();
    this.jPanel2 = new JPanel();
    this.bn_close = new JButton();
    
    setDefaultCloseOperation(2);
    setTitle("About SVG Salamander");
    this.jPanel1.setLayout(new BorderLayout());
    
    this.textpane_text.setEditable(false);
    this.textpane_text.setPreferredSize(new Dimension(400, 300));
    this.jPanel1.add(this.textpane_text, "Center");
    
    getContentPane().add(this.jPanel1, "Center");
    
    this.bn_close.setText("Close");
    this.bn_close.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent evt)
          {
            VersionDialog.this.bn_closeActionPerformed(evt);
          }
        });
    
    this.jPanel2.add(this.bn_close);
    
    getContentPane().add(this.jPanel2, "South");
    
    pack();
  }


  
  private void bn_closeActionPerformed(ActionEvent evt) {
    setVisible(false);
    dispose();
  }




  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable()
        {
          public void run()
          {
            (new VersionDialog(new JFrame(), true, true)).setVisible(true);
          }
        });
  }
}
