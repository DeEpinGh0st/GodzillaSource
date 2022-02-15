package org.mozilla.javascript.tools.shell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import org.mozilla.javascript.SecurityUtilities;





public class JSConsole
  extends JFrame
  implements ActionListener
{
  static final long serialVersionUID = 2551225560631876300L;
  private File CWD;
  private JFileChooser dlg;
  private ConsoleTextArea consoleTextArea;
  
  public String chooseFile() {
    if (this.CWD == null) {
      String dir = SecurityUtilities.getSystemProperty("user.dir");
      if (dir != null) {
        this.CWD = new File(dir);
      }
    } 
    if (this.CWD != null) {
      this.dlg.setCurrentDirectory(this.CWD);
    }
    this.dlg.setDialogTitle("Select a file to load");
    int returnVal = this.dlg.showOpenDialog(this);
    if (returnVal == 0) {
      String result = this.dlg.getSelectedFile().getPath();
      this.CWD = new File(this.dlg.getSelectedFile().getParent());
      return result;
    } 
    return null;
  }
  
  public static void main(String[] args) {
    new JSConsole(args);
  }
  
  public void createFileChooser() {
    this.dlg = new JFileChooser();
    FileFilter filter = new FileFilter()
      {
        public boolean accept(File f)
        {
          if (f.isDirectory()) {
            return true;
          }
          String name = f.getName();
          int i = name.lastIndexOf('.');
          if (i > 0 && i < name.length() - 1) {
            String ext = name.substring(i + 1).toLowerCase();
            if (ext.equals("js")) {
              return true;
            }
          } 
          return false;
        }

        
        public String getDescription() {
          return "JavaScript Files (*.js)";
        }
      };
    this.dlg.addChoosableFileFilter(filter);
  }

  
  public JSConsole(String[] args) {
    super("Rhino JavaScript Console");
    JMenuBar menubar = new JMenuBar();
    createFileChooser();
    String[] fileItems = { "Load...", "Exit" };
    String[] fileCmds = { "Load", "Exit" };
    char[] fileShortCuts = { 'L', 'X' };
    String[] editItems = { "Cut", "Copy", "Paste" };
    char[] editShortCuts = { 'T', 'C', 'P' };
    String[] plafItems = { "Metal", "Windows", "Motif" };
    boolean[] plafState = { true, false, false };
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    JMenu plafMenu = new JMenu("Platform");
    plafMenu.setMnemonic('P'); int i;
    for (i = 0; i < fileItems.length; i++) {
      JMenuItem item = new JMenuItem(fileItems[i], fileShortCuts[i]);
      
      item.setActionCommand(fileCmds[i]);
      item.addActionListener(this);
      fileMenu.add(item);
    } 
    for (i = 0; i < editItems.length; i++) {
      JMenuItem item = new JMenuItem(editItems[i], editShortCuts[i]);
      
      item.addActionListener(this);
      editMenu.add(item);
    } 
    ButtonGroup group = new ButtonGroup();
    for (int j = 0; j < plafItems.length; j++) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(plafItems[j], plafState[j]);
      
      group.add(item);
      item.addActionListener(this);
      plafMenu.add(item);
    } 
    menubar.add(fileMenu);
    menubar.add(editMenu);
    menubar.add(plafMenu);
    setJMenuBar(menubar);
    this.consoleTextArea = new ConsoleTextArea(args);
    JScrollPane scroller = new JScrollPane(this.consoleTextArea);
    setContentPane(scroller);
    this.consoleTextArea.setRows(24);
    this.consoleTextArea.setColumns(80);
    addWindowListener(new WindowAdapter()
        {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        });
    pack();
    setVisible(true);


    
    Main.setIn(this.consoleTextArea.getIn());
    Main.setOut(this.consoleTextArea.getOut());
    Main.setErr(this.consoleTextArea.getErr());
    Main.main(args);
  }
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    String plaf_name = null;
    if (cmd.equals("Load")) {
      String f = chooseFile();
      if (f != null) {
        f = f.replace('\\', '/');
        this.consoleTextArea.eval("load(\"" + f + "\");");
      } 
    } else if (cmd.equals("Exit")) {
      System.exit(0);
    } else if (cmd.equals("Cut")) {
      this.consoleTextArea.cut();
    } else if (cmd.equals("Copy")) {
      this.consoleTextArea.copy();
    } else if (cmd.equals("Paste")) {
      this.consoleTextArea.paste();
    } else {
      if (cmd.equals("Metal")) {
        plaf_name = "javax.swing.plaf.metal.MetalLookAndFeel";
      } else if (cmd.equals("Windows")) {
        plaf_name = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
      } else if (cmd.equals("Motif")) {
        plaf_name = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
      } 
      if (plaf_name != null)
        try {
          UIManager.setLookAndFeel(plaf_name);
          SwingUtilities.updateComponentTreeUI(this);
          this.consoleTextArea.postUpdateUI();

          
          createFileChooser();
        } catch (Exception exc) {
          JOptionPane.showMessageDialog(this, exc.getMessage(), "Platform", 0);
        }  
    } 
  }
}
