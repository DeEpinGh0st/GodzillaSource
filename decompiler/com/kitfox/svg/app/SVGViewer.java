package com.kitfox.svg.app;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGDisplayPanel;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;


























public class SVGViewer
  extends JFrame
{
  public static final long serialVersionUID = 1L;
  final JFileChooser fileChooser;
  private JCheckBoxMenuItem CheckBoxMenuItem_anonInputStream;
  private JCheckBoxMenuItem cmCheck_verbose;
  SVGDisplayPanel svgDisplayPanel = new SVGDisplayPanel();
  private JMenuItem cm_800x600;
  private JMenuItem cm_about;
  private JMenuItem cm_loadFile;
  
  public SVGViewer() {
    JFileChooser fc = null;
    
    try {
      fc = new JFileChooser();
      fc.setFileFilter(new FileFilter()
          {
            final Matcher matchLevelFile = Pattern.compile(".*\\.svg[z]?").matcher("");


            
            public boolean accept(File file) {
              if (file.isDirectory()) return true;
              
              this.matchLevelFile.reset(file.getName());
              return this.matchLevelFile.matches();
            }
            
            public String getDescription() {
              return "SVG file (*.svg, *.svgz)";
            }
          });
    }
    catch (AccessControlException accessControlException) {}


    
    this.fileChooser = fc;


















    
    initComponents();
    
    setSize(800, 600);
    
    this.svgDisplayPanel.setBgColor(Color.white);
    
    this.svgDisplayPanel.setPreferredSize(getSize());
    this.panel_svgArea.add((Component)this.svgDisplayPanel, "Center");
  }
  private JMenuItem cm_loadUrl;
  
  private void loadURL(URL url) {
    URI uri;
    boolean verbose = this.cmCheck_verbose.isSelected();

    
    SVGUniverse universe = SVGCache.getSVGUniverse();
    SVGDiagram diagram = null;

    
    if (!this.CheckBoxMenuItem_anonInputStream.isSelected()) {

      
      uri = universe.loadSVG(url);
      
      if (verbose) System.err.println("Loading document " + uri.toString());
      
      diagram = universe.getDiagram(uri);
    } else {

      
      try {

        
        InputStream is = url.openStream();
        uri = universe.loadSVG(is, "defaultName");
        
        if (verbose) System.err.println("Loading document " + uri.toString());
      
      } catch (Exception e) {
        
        Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);





        
        return;
      } 
    } 





    
    diagram = universe.getDiagram(uri);
    
    this.svgDisplayPanel.setDiagram(diagram);
    repaint();
  }
  
  private JMenuBar jMenuBar1;
  private JMenu menu_file;
  private JMenu menu_help;
  private JMenu menu_window;
  private JPanel panel_svgArea;
  private JScrollPane scrollPane_svgArea;
  
  private void initComponents() {
    this.scrollPane_svgArea = new JScrollPane();
    this.panel_svgArea = new JPanel();
    this.jMenuBar1 = new JMenuBar();
    this.menu_file = new JMenu();
    this.cm_loadFile = new JMenuItem();
    this.cm_loadUrl = new JMenuItem();
    this.menu_window = new JMenu();
    this.cm_800x600 = new JMenuItem();
    this.CheckBoxMenuItem_anonInputStream = new JCheckBoxMenuItem();
    this.cmCheck_verbose = new JCheckBoxMenuItem();
    this.menu_help = new JMenu();
    this.cm_about = new JMenuItem();
    
    setTitle("SVG Viewer - Salamander Project");
    addWindowListener(new WindowAdapter()
        {
          
          public void windowClosing(WindowEvent evt)
          {
            SVGViewer.this.exitForm(evt);
          }
        });
    
    this.panel_svgArea.setLayout(new BorderLayout());
    
    this.panel_svgArea.addMouseListener(new MouseAdapter()
        {
          
          public void mousePressed(MouseEvent evt)
          {
            SVGViewer.this.panel_svgAreaMousePressed(evt);
          }

          
          public void mouseReleased(MouseEvent evt) {
            SVGViewer.this.panel_svgAreaMouseReleased(evt);
          }
        });
    
    this.scrollPane_svgArea.setViewportView(this.panel_svgArea);
    
    getContentPane().add(this.scrollPane_svgArea, "Center");
    
    this.menu_file.setMnemonic('f');
    this.menu_file.setText("File");
    this.cm_loadFile.setMnemonic('l');
    this.cm_loadFile.setText("Load File...");
    this.cm_loadFile.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent evt)
          {
            SVGViewer.this.cm_loadFileActionPerformed(evt);
          }
        });
    
    this.menu_file.add(this.cm_loadFile);
    
    this.cm_loadUrl.setText("Load URL...");
    this.cm_loadUrl.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent evt)
          {
            SVGViewer.this.cm_loadUrlActionPerformed(evt);
          }
        });
    
    this.menu_file.add(this.cm_loadUrl);
    
    this.jMenuBar1.add(this.menu_file);
    
    this.menu_window.setText("Window");
    this.cm_800x600.setText("800 x 600");
    this.cm_800x600.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent evt)
          {
            SVGViewer.this.cm_800x600ActionPerformed(evt);
          }
        });
    
    this.menu_window.add(this.cm_800x600);
    
    this.CheckBoxMenuItem_anonInputStream.setText("Anonymous Input Stream");
    this.menu_window.add(this.CheckBoxMenuItem_anonInputStream);
    
    this.cmCheck_verbose.setText("Verbose");
    this.cmCheck_verbose.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent evt)
          {
            SVGViewer.this.cmCheck_verboseActionPerformed(evt);
          }
        });
    
    this.menu_window.add(this.cmCheck_verbose);
    
    this.jMenuBar1.add(this.menu_window);
    
    this.menu_help.setText("Help");
    this.cm_about.setText("About...");
    this.cm_about.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent evt)
          {
            SVGViewer.this.cm_aboutActionPerformed(evt);
          }
        });
    
    this.menu_help.add(this.cm_about);
    
    this.jMenuBar1.add(this.menu_help);
    
    setJMenuBar(this.jMenuBar1);
    
    pack();
  }

  
  private void cm_loadUrlActionPerformed(ActionEvent evt) {
    String urlStrn = JOptionPane.showInputDialog(this, "Enter URL of SVG file");
    if (urlStrn == null) {
      return;
    }
    try {
      URL url = new URL(URLEncoder.encode(urlStrn, "UTF-8"));
      loadURL(url);
    }
    catch (Exception e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
    } 
  }

  
  private void panel_svgAreaMouseReleased(MouseEvent evt) {
    List<List<SVGElement>> pickedElements;
    SVGDiagram diagram = this.svgDisplayPanel.getDiagram();

    
    try {
      pickedElements = diagram.pick(new Point(evt.getX(), evt.getY()), null);
    }
    catch (SVGException e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, (Throwable)e);
      
      return;
    } 
    System.out.println("Pick results:");
    for (List<SVGElement> path : pickedElements) {
      System.out.print("  Path: ");
      
      for (SVGElement ele : path) {
        System.out.print("" + ele.getId() + "(" + ele.getClass().getName() + ") ");
      }
      System.out.println();
    } 
  }


  
  private void panel_svgAreaMousePressed(MouseEvent evt) {}


  
  private void cmCheck_verboseActionPerformed(ActionEvent evt) {
    SVGCache.getSVGUniverse().setVerbose(this.cmCheck_verbose.isSelected());
  }


  
  private void cm_aboutActionPerformed(ActionEvent evt) {
    VersionDialog dlg = new VersionDialog(this, true, this.cmCheck_verbose.isSelected());
    dlg.setVisible(true);
  }
  
  private void cm_800x600ActionPerformed(ActionEvent evt) {
    setSize(800, 600);
  }


  
  private void cm_loadFileActionPerformed(ActionEvent evt) {
    try {
      int retVal = this.fileChooser.showOpenDialog(this);
      if (retVal == 0)
      {
        File chosenFile = this.fileChooser.getSelectedFile();
        
        URL url = chosenFile.toURI().toURL();
        
        loadURL(url);
      }
    
    } catch (Exception e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
    } 
  }




  
  private void exitForm(WindowEvent evt) {
    System.exit(0);
  }



  
  public static void main(String[] args) {
    (new SVGViewer()).setVisible(true);
  }
}
