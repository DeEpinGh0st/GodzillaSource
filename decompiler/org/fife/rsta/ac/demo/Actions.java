package org.fife.rsta.ac.demo;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultHighlighter;
























interface Actions
{
  public static class AboutAction
    extends AbstractAction
  {
    private DemoRootPane demo;
    
    public AboutAction(DemoRootPane demo) {
      this.demo = demo;
      putValue("Name", "About RSTALanguageSupport...");
    }


    
    public void actionPerformed(ActionEvent e) {
      AboutDialog ad = new AboutDialog((DemoApp)SwingUtilities.getWindowAncestor(this.demo));
      ad.setLocationRelativeTo(this.demo);
      ad.setVisible(true);
    }
  }


  
  public static class ExitAction
    extends AbstractAction
  {
    private static final long serialVersionUID = 1L;


    
    public ExitAction() {
      putValue("Name", "Exit");
      putValue("MnemonicKey", Integer.valueOf(120));
    }

    
    public void actionPerformed(ActionEvent e) {
      System.exit(0);
    }
  }


  
  public static class OpenAction
    extends AbstractAction
  {
    private static final long serialVersionUID = 1L;
    
    private DemoRootPane demo;
    
    private JFileChooser chooser;

    
    public OpenAction(DemoRootPane demo) {
      this.demo = demo;
      putValue("Name", "Open...");
      putValue("MnemonicKey", Integer.valueOf(79));
      int mods = demo.getToolkit().getMenuShortcutKeyMask();
      KeyStroke ks = KeyStroke.getKeyStroke(79, mods);
      putValue("AcceleratorKey", ks);
    }

    
    public void actionPerformed(ActionEvent e) {
      if (this.chooser == null) {
        this.chooser = new JFileChooser();
        this.chooser.setFileFilter(new ExtensionFileFilter("Java Source Files", "java"));
      } 
      
      int rc = this.chooser.showOpenDialog(this.demo);
      if (rc == 0) {
        this.demo.openFile(this.chooser.getSelectedFile());
      }
    }
  }


  
  public static class LookAndFeelAction
    extends AbstractAction
  {
    private UIManager.LookAndFeelInfo info;
    
    private DemoRootPane demo;

    
    public LookAndFeelAction(DemoRootPane demo, UIManager.LookAndFeelInfo info) {
      putValue("Name", info.getName());
      this.demo = demo;
      this.info = info;
    }

    
    public void actionPerformed(ActionEvent e) {
      try {
        UIManager.setLookAndFeel(this.info.getClassName());
        SwingUtilities.updateComponentTreeUI(this.demo);
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception ex) {
        ex.printStackTrace();
      } 
    }
  }


  
  public static class StyleAction
    extends AbstractAction
  {
    private DemoRootPane demo;
    
    private String res;
    
    private String style;

    
    public StyleAction(DemoRootPane demo, String name, String res, String style) {
      putValue("Name", name);
      this.demo = demo;
      this.res = res;
      this.style = style;
    }

    
    public void actionPerformed(ActionEvent e) {
      this.demo.setText(this.res, this.style);
    }
  }

  
  public static class ToggleLayeredHighlightsAction
    extends AbstractAction
  {
    private DemoRootPane demo;
    
    public ToggleLayeredHighlightsAction(DemoRootPane demo) {
      this.demo = demo;
      putValue("Name", "Layered Selection Highlights");
    }


    
    public void actionPerformed(ActionEvent e) {
      DefaultHighlighter h = (DefaultHighlighter)this.demo.getTextArea().getHighlighter();
      h.setDrawsLayeredHighlights(!h.getDrawsLayeredHighlights());
    }
  }
}
