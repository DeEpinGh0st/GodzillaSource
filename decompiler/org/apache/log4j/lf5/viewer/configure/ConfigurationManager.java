package org.apache.log4j.lf5.viewer.configure;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.LogLevelFormatException;
import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
import org.apache.log4j.lf5.viewer.LogTable;
import org.apache.log4j.lf5.viewer.LogTableColumn;
import org.apache.log4j.lf5.viewer.LogTableColumnFormatException;
import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel;
import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree;
import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNode;
import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;






































public class ConfigurationManager
{
  private static final String CONFIG_FILE_NAME = "lf5_configuration.xml";
  private static final String NAME = "name";
  private static final String PATH = "path";
  private static final String SELECTED = "selected";
  private static final String EXPANDED = "expanded";
  private static final String CATEGORY = "category";
  private static final String FIRST_CATEGORY_NAME = "Categories";
  private static final String LEVEL = "level";
  private static final String COLORLEVEL = "colorlevel";
  private static final String RED = "red";
  private static final String GREEN = "green";
  private static final String BLUE = "blue";
  private static final String COLUMN = "column";
  private static final String NDCTEXTFILTER = "searchtext";
  private LogBrokerMonitor _monitor = null;
  private LogTable _table = null;




  
  public ConfigurationManager(LogBrokerMonitor monitor, LogTable table) {
    this._monitor = monitor;
    this._table = table;
    load();
  }



  
  public void save() {
    CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
    CategoryNode root = model.getRootCategoryNode();
    
    StringBuffer xml = new StringBuffer(2048);
    openXMLDocument(xml);
    openConfigurationXML(xml);
    processLogRecordFilter(this._monitor.getNDCTextFilter(), xml);
    processLogLevels(this._monitor.getLogLevelMenuItems(), xml);
    processLogLevelColors(this._monitor.getLogLevelMenuItems(), LogLevel.getLogLevelColorMap(), xml);
    
    processLogTableColumns(LogTableColumn.getLogTableColumns(), xml);
    processConfigurationNode(root, xml);
    closeConfigurationXML(xml);
    store(xml.toString());
  }
  
  public void reset() {
    deleteConfigurationFile();
    collapseTree();
    selectAllNodes();
  }

  
  public static String treePathToString(TreePath path) {
    StringBuffer sb = new StringBuffer();
    CategoryNode n = null;
    Object[] objects = path.getPath();
    for (int i = 1; i < objects.length; i++) {
      n = (CategoryNode)objects[i];
      if (i > 1) {
        sb.append(".");
      }
      sb.append(n.getTitle());
    } 
    return sb.toString();
  }



  
  protected void load() {
    File file = new File(getFilename());
    if (file.exists()) {
      try {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        processRecordFilter(doc);
        processCategories(doc);
        processLogLevels(doc);
        processLogLevelColors(doc);
        processLogTableColumns(doc);
      } catch (Exception e) {

        
        System.err.println("Unable process configuration file at " + getFilename() + ". Error Message=" + e.getMessage());
      } 
    }
  }







  
  protected void processRecordFilter(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("searchtext");

    
    Node n = nodeList.item(0);

    
    if (n == null) {
      return;
    }
    
    NamedNodeMap map = n.getAttributes();
    String text = getValue(map, "name");
    
    if (text == null || text.equals("")) {
      return;
    }
    this._monitor.setNDCLogRecordFilter(text);
  }
  
  protected void processCategories(Document doc) {
    CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
    CategoryExplorerModel model = tree.getExplorerModel();
    NodeList nodeList = doc.getElementsByTagName("category");

    
    NamedNodeMap map = nodeList.item(0).getAttributes();
    int j = getValue(map, "name").equalsIgnoreCase("Categories") ? 1 : 0;

    
    for (int i = nodeList.getLength() - 1; i >= j; i--) {
      Node n = nodeList.item(i);
      map = n.getAttributes();
      CategoryNode chnode = model.addCategory(new CategoryPath(getValue(map, "path")));
      chnode.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
      if (getValue(map, "expanded").equalsIgnoreCase("true"));
      tree.expandPath(model.getTreePathToRoot(chnode));
    } 
  }

  
  protected void processLogLevels(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("level");
    Map menuItems = this._monitor.getLogLevelMenuItems();
    
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      NamedNodeMap map = n.getAttributes();
      String name = getValue(map, "name");
      try {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(LogLevel.valueOf(name));
        
        item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
      } catch (LogLevelFormatException e) {}
    } 
  }


  
  protected void processLogLevelColors(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("colorlevel");
    LogLevel.getLogLevelColorMap();
    
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);

      
      if (n == null) {
        return;
      }
      
      NamedNodeMap map = n.getAttributes();
      String name = getValue(map, "name");
      try {
        LogLevel level = LogLevel.valueOf(name);
        int red = Integer.parseInt(getValue(map, "red"));
        int green = Integer.parseInt(getValue(map, "green"));
        int blue = Integer.parseInt(getValue(map, "blue"));
        Color c = new Color(red, green, blue);
        if (level != null) {
          level.setLogLevelColorMap(level, c);
        }
      }
      catch (LogLevelFormatException e) {}
    } 
  }


  
  protected void processLogTableColumns(Document doc) {
    NodeList nodeList = doc.getElementsByTagName("column");
    Map menuItems = this._monitor.getLogTableColumnMenuItems();
    List selectedColumns = new ArrayList();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);

      
      if (n == null) {
        return;
      }
      NamedNodeMap map = n.getAttributes();
      String name = getValue(map, "name");
      try {
        LogTableColumn column = LogTableColumn.valueOf(name);
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(column);
        
        item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
        
        if (item.isSelected()) {
          selectedColumns.add(column);
        }
      } catch (LogTableColumnFormatException e) {}


      
      if (selectedColumns.isEmpty()) {
        this._table.setDetailedView();
      } else {
        this._table.setView(selectedColumns);
      } 
    } 
  }

  
  protected String getValue(NamedNodeMap map, String attr) {
    Node n = map.getNamedItem(attr);
    return n.getNodeValue();
  }

  
  protected void collapseTree() {
    CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
    for (int i = tree.getRowCount() - 1; i > 0; i--) {
      tree.collapseRow(i);
    }
  }
  
  protected void selectAllNodes() {
    CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
    CategoryNode root = model.getRootCategoryNode();
    Enumeration all = root.breadthFirstEnumeration();
    CategoryNode n = null;
    while (all.hasMoreElements()) {
      n = all.nextElement();
      n.setSelected(true);
    } 
  }

  
  protected void store(String s) {
    try {
      PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));
      writer.print(s);
      writer.close();
    } catch (IOException e) {
      
      e.printStackTrace();
    } 
  }

  
  protected void deleteConfigurationFile() {
    try {
      File f = new File(getFilename());
      if (f.exists()) {
        f.delete();
      }
    } catch (SecurityException e) {
      System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
    } 
  }

  
  protected String getFilename() {
    String home = System.getProperty("user.home");
    String sep = System.getProperty("file.separator");
    
    return home + sep + "lf5" + sep + "lf5_configuration.xml";
  }



  
  private void processConfigurationNode(CategoryNode node, StringBuffer xml) {
    CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
    
    Enumeration all = node.breadthFirstEnumeration();
    CategoryNode n = null;
    while (all.hasMoreElements()) {
      n = all.nextElement();
      exportXMLElement(n, model.getTreePathToRoot(n), xml);
    } 
  }

  
  private void processLogLevels(Map logLevelMenuItems, StringBuffer xml) {
    xml.append("\t<loglevels>\r\n");
    Iterator it = logLevelMenuItems.keySet().iterator();
    while (it.hasNext()) {
      LogLevel level = it.next();
      JCheckBoxMenuItem item = (JCheckBoxMenuItem)logLevelMenuItems.get(level);
      exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
    } 
    
    xml.append("\t</loglevels>\r\n");
  }
  
  private void processLogLevelColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
    xml.append("\t<loglevelcolors>\r\n");
    
    Iterator it = logLevelMenuItems.keySet().iterator();
    while (it.hasNext()) {
      LogLevel level = it.next();
      
      Color color = (Color)logLevelColors.get(level);
      exportLogLevelColorXMLElement(level.getLabel(), color, xml);
    } 
    
    xml.append("\t</loglevelcolors>\r\n");
  }

  
  private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml) {
    xml.append("\t<logtablecolumns>\r\n");
    Iterator it = logTableColumnMenuItems.iterator();
    while (it.hasNext()) {
      LogTableColumn column = it.next();
      JCheckBoxMenuItem item = this._monitor.getTableColumnMenuItem(column);
      exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), xml);
    } 
    
    xml.append("\t</logtablecolumns>\r\n");
  }


  
  private void processLogRecordFilter(String text, StringBuffer xml) {
    xml.append("\t<").append("searchtext").append(" ");
    xml.append("name").append("=\"").append(text).append("\"");
    xml.append("/>\r\n");
  }
  
  private void openXMLDocument(StringBuffer xml) {
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
  }
  
  private void openConfigurationXML(StringBuffer xml) {
    xml.append("<configuration>\r\n");
  }
  
  private void closeConfigurationXML(StringBuffer xml) {
    xml.append("</configuration>\r\n");
  }
  
  private void exportXMLElement(CategoryNode node, TreePath path, StringBuffer xml) {
    CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
    
    xml.append("\t<").append("category").append(" ");
    xml.append("name").append("=\"").append(node.getTitle()).append("\" ");
    xml.append("path").append("=\"").append(treePathToString(path)).append("\" ");
    xml.append("expanded").append("=\"").append(tree.isExpanded(path)).append("\" ");
    xml.append("selected").append("=\"").append(node.isSelected()).append("\"/>\r\n");
  }
  
  private void exportLogLevelXMLElement(String label, boolean selected, StringBuffer xml) {
    xml.append("\t\t<").append("level").append(" ").append("name");
    xml.append("=\"").append(label).append("\" ");
    xml.append("selected").append("=\"").append(selected);
    xml.append("\"/>\r\n");
  }
  
  private void exportLogLevelColorXMLElement(String label, Color color, StringBuffer xml) {
    xml.append("\t\t<").append("colorlevel").append(" ").append("name");
    xml.append("=\"").append(label).append("\" ");
    xml.append("red").append("=\"").append(color.getRed()).append("\" ");
    xml.append("green").append("=\"").append(color.getGreen()).append("\" ");
    xml.append("blue").append("=\"").append(color.getBlue());
    xml.append("\"/>\r\n");
  }
  
  private void exportLogTableColumnXMLElement(String label, boolean selected, StringBuffer xml) {
    xml.append("\t\t<").append("column").append(" ").append("name");
    xml.append("=\"").append(label).append("\" ");
    xml.append("selected").append("=\"").append(selected);
    xml.append("\"/>\r\n");
  }
}
