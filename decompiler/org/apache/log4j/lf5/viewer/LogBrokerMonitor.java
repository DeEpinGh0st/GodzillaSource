package org.apache.log4j.lf5.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.LogRecord;
import org.apache.log4j.lf5.LogRecordFilter;
import org.apache.log4j.lf5.util.DateFormatManager;
import org.apache.log4j.lf5.util.LogFileParser;
import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree;
import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath;
import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.apache.log4j.lf5.viewer.configure.MRUFileManager;









































public class LogBrokerMonitor
{
  public static final String DETAILED_VIEW = "Detailed";
  protected JFrame _logMonitorFrame;
  protected int _logMonitorFrameWidth = 550;
  protected int _logMonitorFrameHeight = 500;
  protected LogTable _table;
  protected CategoryExplorerTree _categoryExplorerTree;
  protected String _searchText;
  protected String _NDCTextFilter = "";
  protected LogLevel _leastSevereDisplayedLogLevel = LogLevel.DEBUG;
  
  protected JScrollPane _logTableScrollPane;
  protected JLabel _statusLabel;
  protected Object _lock = new Object();
  
  protected JComboBox _fontSizeCombo;
  protected int _fontSize = 10;
  protected String _fontName = "Dialog";
  protected String _currentView = "Detailed";
  
  protected boolean _loadSystemFonts = false;
  protected boolean _trackTableScrollPane = true;
  protected Dimension _lastTableViewportSize;
  protected boolean _callSystemExitOnClose = false;
  protected List _displayedLogBrokerProperties = new Vector();
  
  protected Map _logLevelMenuItems = new HashMap();
  protected Map _logTableColumnMenuItems = new HashMap();
  
  protected List _levels = null;
  protected List _columns = null;
  
  protected boolean _isDisposed = false;
  protected ConfigurationManager _configurationManager = null;
  protected MRUFileManager _mruFileManager = null;
  protected File _fileLocation = null;












  
  public LogBrokerMonitor(List logLevels) {
    this._levels = logLevels;
    this._columns = LogTableColumn.getLogTableColumns();


    
    String callSystemExitOnClose = System.getProperty("monitor.exit");
    
    if (callSystemExitOnClose == null) {
      callSystemExitOnClose = "false";
    }
    callSystemExitOnClose = callSystemExitOnClose.trim().toLowerCase();
    
    if (callSystemExitOnClose.equals("true")) {
      this._callSystemExitOnClose = true;
    }
    
    initComponents();

    
    this._logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));
  }










  
  public void show(final int delay) {
    if (this._logMonitorFrame.isVisible()) {
      return;
    }
    
    SwingUtilities.invokeLater(new Runnable() { private final int val$delay; private final LogBrokerMonitor this$0;
          public void run() {
            Thread.yield();
            LogBrokerMonitor.this.pause(delay);
            LogBrokerMonitor.this._logMonitorFrame.setVisible(true);
          } }
      );
  }
  
  public void show() {
    show(0);
  }



  
  public void dispose() {
    this._logMonitorFrame.dispose();
    this._isDisposed = true;
    
    if (this._callSystemExitOnClose == true) {
      System.exit(0);
    }
  }



  
  public void hide() {
    this._logMonitorFrame.setVisible(false);
  }



  
  public DateFormatManager getDateFormatManager() {
    return this._table.getDateFormatManager();
  }



  
  public void setDateFormatManager(DateFormatManager dfm) {
    this._table.setDateFormatManager(dfm);
  }




  
  public boolean getCallSystemExitOnClose() {
    return this._callSystemExitOnClose;
  }




  
  public void setCallSystemExitOnClose(boolean callSystemExitOnClose) {
    this._callSystemExitOnClose = callSystemExitOnClose;
  }





  
  public void addMessage(final LogRecord lr) {
    if (this._isDisposed == true) {
      return;
    }


    
    SwingUtilities.invokeLater(new Runnable() { private final LogRecord val$lr; private final LogBrokerMonitor this$0;
          public void run() {
            LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().addLogRecord(lr);
            LogBrokerMonitor.this._table.getFilteredLogTableModel().addLogRecord(lr);
            LogBrokerMonitor.this.updateStatusLabel();
          } }
      );
  }
  
  public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords) {
    this._table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
  }
  
  public JFrame getBaseFrame() {
    return this._logMonitorFrame;
  }
  
  public void setTitle(String title) {
    this._logMonitorFrame.setTitle(title + " - LogFactor5");
  }
  
  public void setFrameSize(int width, int height) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    if (0 < width && width < screen.width) {
      this._logMonitorFrameWidth = width;
    }
    if (0 < height && height < screen.height) {
      this._logMonitorFrameHeight = height;
    }
    updateFrameSize();
  }
  
  public void setFontSize(int fontSize) {
    changeFontSizeCombo(this._fontSizeCombo, fontSize);
  }


  
  public void addDisplayedProperty(Object messageLine) {
    this._displayedLogBrokerProperties.add(messageLine);
  }
  
  public Map getLogLevelMenuItems() {
    return this._logLevelMenuItems;
  }
  
  public Map getLogTableColumnMenuItems() {
    return this._logTableColumnMenuItems;
  }
  
  public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
    return getLogTableColumnMenuItem(column);
  }
  
  public CategoryExplorerTree getCategoryExplorerTree() {
    return this._categoryExplorerTree;
  }


  
  public String getNDCTextFilter() {
    return this._NDCTextFilter;
  }



  
  public void setNDCLogRecordFilter(String textFilter) {
    this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(textFilter));
  }




  
  protected void setSearchText(String text) {
    this._searchText = text;
  }



  
  protected void setNDCTextFilter(String text) {
    if (text == null) {
      this._NDCTextFilter = "";
    } else {
      this._NDCTextFilter = text;
    } 
  }



  
  protected void sortByNDC() {
    String text = this._NDCTextFilter;
    if (text == null || text.length() == 0) {
      return;
    }

    
    this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(text));
  }

  
  protected void findSearchText() {
    String text = this._searchText;
    if (text == null || text.length() == 0) {
      return;
    }
    int startRow = getFirstSelectedRow();
    int foundRow = findRecord(startRow, text, this._table.getFilteredLogTableModel().getFilteredRecords());



    
    selectRow(foundRow);
  }
  
  protected int getFirstSelectedRow() {
    return this._table.getSelectionModel().getMinSelectionIndex();
  }
  
  protected void selectRow(int foundRow) {
    if (foundRow == -1) {
      String message = this._searchText + " not found.";
      JOptionPane.showMessageDialog(this._logMonitorFrame, message, "Text not found", 1);


      
      return;
    } 

    
    LF5SwingUtils.selectRow(foundRow, this._table, this._logTableScrollPane);
  }




  
  protected int findRecord(int startRow, String searchText, List records) {
    if (startRow < 0) {
      startRow = 0;
    } else {
      startRow++;
    } 
    int len = records.size();
    int i;
    for (i = startRow; i < len; i++) {
      if (matches(records.get(i), searchText)) {
        return i;
      }
    } 
    
    len = startRow;
    for (i = 0; i < len; i++) {
      if (matches(records.get(i), searchText)) {
        return i;
      }
    } 
    
    return -1;
  }




  
  protected boolean matches(LogRecord record, String text) {
    String message = record.getMessage();
    String NDC = record.getNDC();
    
    if ((message == null && NDC == null) || text == null) {
      return false;
    }
    if (message.toLowerCase().indexOf(text.toLowerCase()) == -1 && NDC.toLowerCase().indexOf(text.toLowerCase()) == -1)
    {
      return false;
    }
    
    return true;
  }





  
  protected void refresh(JTextArea textArea) {
    String text = textArea.getText();
    textArea.setText("");
    textArea.setText(text);
  }
  
  protected void refreshDetailTextArea() {
    refresh(this._table._detailTextArea);
  }
  
  protected void clearDetailTextArea() {
    this._table._detailTextArea.setText("");
  }





  
  protected int changeFontSizeCombo(JComboBox box, int requestedSize) {
    int len = box.getItemCount();

    
    Object selectedObject = box.getItemAt(0);
    int selectedValue = Integer.parseInt(String.valueOf(selectedObject));
    for (int i = 0; i < len; i++) {
      Object currentObject = box.getItemAt(i);
      int currentValue = Integer.parseInt(String.valueOf(currentObject));
      if (selectedValue < currentValue && currentValue <= requestedSize) {
        selectedValue = currentValue;
        selectedObject = currentObject;
      } 
    } 
    box.setSelectedItem(selectedObject);
    return selectedValue;
  }



  
  protected void setFontSizeSilently(int fontSize) {
    this._fontSize = fontSize;
    setFontSize(this._table._detailTextArea, fontSize);
    selectRow(0);
    setFontSize(this._table, fontSize);
  }
  
  protected void setFontSize(Component component, int fontSize) {
    Font oldFont = component.getFont();
    Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), fontSize);
    
    component.setFont(newFont);
  }
  
  protected void updateFrameSize() {
    this._logMonitorFrame.setSize(this._logMonitorFrameWidth, this._logMonitorFrameHeight);
    centerFrame(this._logMonitorFrame);
  }
  
  protected void pause(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {}
  }





  
  protected void initComponents() {
    this._logMonitorFrame = new JFrame("LogFactor5");
    
    this._logMonitorFrame.setDefaultCloseOperation(0);
    
    String resource = "/org/apache/log4j/lf5/viewer/images/lf5_small_icon.gif";
    
    URL lf5IconURL = getClass().getResource(resource);
    
    if (lf5IconURL != null) {
      this._logMonitorFrame.setIconImage((new ImageIcon(lf5IconURL)).getImage());
    }
    updateFrameSize();



    
    JTextArea detailTA = createDetailTextArea();
    JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
    this._table = new LogTable(detailTA);
    setView(this._currentView, this._table);
    this._table.setFont(new Font(this._fontName, 0, this._fontSize));
    this._logTableScrollPane = new JScrollPane(this._table);
    
    if (this._trackTableScrollPane) {
      this._logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
    }






    
    JSplitPane tableViewerSplitPane = new JSplitPane();
    tableViewerSplitPane.setOneTouchExpandable(true);
    tableViewerSplitPane.setOrientation(0);
    tableViewerSplitPane.setLeftComponent(this._logTableScrollPane);
    tableViewerSplitPane.setRightComponent(detailTAScrollPane);






    
    tableViewerSplitPane.setDividerLocation(350);




    
    this._categoryExplorerTree = new CategoryExplorerTree();
    
    this._table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());
    
    JScrollPane categoryExplorerTreeScrollPane = new JScrollPane((Component)this._categoryExplorerTree);
    
    categoryExplorerTreeScrollPane.setPreferredSize(new Dimension(130, 400));

    
    this._mruFileManager = new MRUFileManager();




    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOneTouchExpandable(true);
    splitPane.setRightComponent(tableViewerSplitPane);
    splitPane.setLeftComponent(categoryExplorerTreeScrollPane);
    
    splitPane.setDividerLocation(130);



    
    this._logMonitorFrame.getRootPane().setJMenuBar(createMenuBar());
    this._logMonitorFrame.getContentPane().add(splitPane, "Center");
    this._logMonitorFrame.getContentPane().add(createToolBar(), "North");
    
    this._logMonitorFrame.getContentPane().add(createStatusArea(), "South");

    
    makeLogTableListenToCategoryExplorer();
    addTableModelProperties();



    
    this._configurationManager = new ConfigurationManager(this, this._table);
  }

  
  protected LogRecordFilter createLogRecordFilter() {
    LogRecordFilter result = new LogRecordFilter() { private final LogBrokerMonitor this$0;
        public boolean passes(LogRecord record) {
          CategoryPath path = new CategoryPath(record.getCategory());
          return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected() && LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
        } }
      ;

    
    return result;
  }


  
  protected LogRecordFilter createNDCLogRecordFilter(String text) {
    this._NDCTextFilter = text;
    LogRecordFilter result = new LogRecordFilter() { private final LogBrokerMonitor this$0;
        public boolean passes(LogRecord record) {
          String NDC = record.getNDC();
          CategoryPath path = new CategoryPath(record.getCategory());
          if (NDC == null || LogBrokerMonitor.this._NDCTextFilter == null)
            return false; 
          if (NDC.toLowerCase().indexOf(LogBrokerMonitor.this._NDCTextFilter.toLowerCase()) == -1) {
            return false;
          }
          return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected() && LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
        } }
      ;


    
    return result;
  }

  
  protected void updateStatusLabel() {
    this._statusLabel.setText(getRecordsDisplayedMessage());
  }
  
  protected String getRecordsDisplayedMessage() {
    FilteredLogTableModel model = this._table.getFilteredLogTableModel();
    return getStatusText(model.getRowCount(), model.getTotalRowCount());
  }
  
  protected void addTableModelProperties() {
    final FilteredLogTableModel model = this._table.getFilteredLogTableModel();
    
    addDisplayedProperty(new Object() { private final LogBrokerMonitor this$0;
          public String toString() {
            return LogBrokerMonitor.this.getRecordsDisplayedMessage();
          } }
      );
    addDisplayedProperty(new Object() { private final FilteredLogTableModel val$model; private final LogBrokerMonitor this$0;
          public String toString() {
            return "Maximum number of displayed LogRecords: " + model._maxNumberOfLogRecords;
          } }
      );
  }

  
  protected String getStatusText(int displayedRows, int totalRows) {
    StringBuffer result = new StringBuffer();
    result.append("Displaying: ");
    result.append(displayedRows);
    result.append(" records out of a total of: ");
    result.append(totalRows);
    result.append(" records.");
    return result.toString();
  }
  
  protected void makeLogTableListenToCategoryExplorer() {
    ActionListener listener = new ActionListener() { private final LogBrokerMonitor this$0;
        public void actionPerformed(ActionEvent e) {
          LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
          LogBrokerMonitor.this.updateStatusLabel();
        } }
      ;
    this._categoryExplorerTree.getExplorerModel().addActionListener(listener);
  }
  
  protected JPanel createStatusArea() {
    JPanel statusArea = new JPanel();
    JLabel status = new JLabel("No log records to display.");
    
    this._statusLabel = status;
    status.setHorizontalAlignment(2);
    
    statusArea.setBorder(BorderFactory.createEtchedBorder());
    statusArea.setLayout(new FlowLayout(0, 0, 0));
    statusArea.add(status);
    
    return statusArea;
  }
  
  protected JTextArea createDetailTextArea() {
    JTextArea detailTA = new JTextArea();
    detailTA.setFont(new Font("Monospaced", 0, 14));
    detailTA.setTabSize(3);
    detailTA.setLineWrap(true);
    detailTA.setWrapStyleWord(false);
    return detailTA;
  }
  
  protected JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(createFileMenu());
    menuBar.add(createEditMenu());
    menuBar.add(createLogLevelMenu());
    menuBar.add(createViewMenu());
    menuBar.add(createConfigureMenu());
    menuBar.add(createHelpMenu());
    
    return menuBar;
  }
  
  protected JMenu createLogLevelMenu() {
    JMenu result = new JMenu("Log Level");
    result.setMnemonic('l');
    Iterator levels = getLogLevels();
    while (levels.hasNext()) {
      result.add(getMenuItem(levels.next()));
    }
    
    result.addSeparator();
    result.add(createAllLogLevelsMenuItem());
    result.add(createNoLogLevelsMenuItem());
    result.addSeparator();
    result.add(createLogLevelColorMenu());
    result.add(createResetLogLevelColorMenuItem());
    
    return result;
  }
  
  protected JMenuItem createAllLogLevelsMenuItem() {
    JMenuItem result = new JMenuItem("Show all LogLevels");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.selectAllLogLevels(true);
            LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
            LogBrokerMonitor.this.updateStatusLabel();
          } }
      );
    return result;
  }
  
  protected JMenuItem createNoLogLevelsMenuItem() {
    JMenuItem result = new JMenuItem("Hide all LogLevels");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.selectAllLogLevels(false);
            LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
            LogBrokerMonitor.this.updateStatusLabel();
          } }
      );
    return result;
  }
  
  protected JMenu createLogLevelColorMenu() {
    JMenu colorMenu = new JMenu("Configure LogLevel Colors");
    colorMenu.setMnemonic('c');
    Iterator levels = getLogLevels();
    while (levels.hasNext()) {
      colorMenu.add(createSubMenuItem(levels.next()));
    }
    
    return colorMenu;
  }
  
  protected JMenuItem createResetLogLevelColorMenuItem() {
    JMenuItem result = new JMenuItem("Reset LogLevel Colors");
    result.setMnemonic('r');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            LogLevel.resetLogLevelColorMap();

            
            LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
          } }
      );
    return result;
  }
  
  protected void selectAllLogLevels(boolean selected) {
    Iterator levels = getLogLevels();
    while (levels.hasNext()) {
      getMenuItem(levels.next()).setSelected(selected);
    }
  }
  
  protected JCheckBoxMenuItem getMenuItem(LogLevel level) {
    JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logLevelMenuItems.get(level);
    if (result == null) {
      result = createMenuItem(level);
      this._logLevelMenuItems.put(level, result);
    } 
    return result;
  }
  
  protected JMenuItem createSubMenuItem(LogLevel level) {
    final JMenuItem result = new JMenuItem(level.toString());
    final LogLevel logLevel = level;
    result.setMnemonic(level.toString().charAt(0));
    result.addActionListener(new ActionListener() { private final JMenuItem val$result;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.showLogLevelColorChangeDialog(result, logLevel);
          }
          private final LogLevel val$logLevel; private final LogBrokerMonitor this$0; }
      );
    return result;
  }

  
  protected void showLogLevelColorChangeDialog(JMenuItem result, LogLevel level) {
    JMenuItem menuItem = result;
    Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose LogLevel Color", result.getForeground());



    
    if (newColor != null) {
      
      level.setLogLevelColorMap(level, newColor);
      this._table.getFilteredLogTableModel().refresh();
    } 
  }

  
  protected JCheckBoxMenuItem createMenuItem(LogLevel level) {
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
    result.setSelected(true);
    result.setMnemonic(level.toString().charAt(0));
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
            LogBrokerMonitor.this.updateStatusLabel();
          } }
      );
    return result;
  }

  
  protected JMenu createViewMenu() {
    JMenu result = new JMenu("View");
    result.setMnemonic('v');
    Iterator columns = getLogTableColumns();
    while (columns.hasNext()) {
      result.add(getLogTableColumnMenuItem(columns.next()));
    }
    
    result.addSeparator();
    result.add(createAllLogTableColumnsMenuItem());
    result.add(createNoLogTableColumnsMenuItem());
    return result;
  }
  
  protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
    JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logTableColumnMenuItems.get(column);
    if (result == null) {
      result = createLogTableColumnMenuItem(column);
      this._logTableColumnMenuItems.put(column, result);
    } 
    return result;
  }
  
  protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());
    
    result.setSelected(true);
    result.setMnemonic(column.toString().charAt(0));
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            List selectedColumns = LogBrokerMonitor.this.updateView();
            LogBrokerMonitor.this._table.setView(selectedColumns);
          } }
      );
    return result;
  }
  
  protected List updateView() {
    ArrayList updatedList = new ArrayList();
    Iterator columnIterator = this._columns.iterator();
    while (columnIterator.hasNext()) {
      LogTableColumn column = columnIterator.next();
      JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);
      
      if (result.isSelected()) {
        updatedList.add(column);
      }
    } 
    
    return updatedList;
  }
  
  protected JMenuItem createAllLogTableColumnsMenuItem() {
    JMenuItem result = new JMenuItem("Show all Columns");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.selectAllLogTableColumns(true);
            
            List selectedColumns = LogBrokerMonitor.this.updateView();
            LogBrokerMonitor.this._table.setView(selectedColumns);
          } }
      );
    return result;
  }
  
  protected JMenuItem createNoLogTableColumnsMenuItem() {
    JMenuItem result = new JMenuItem("Hide all Columns");
    result.setMnemonic('h');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.selectAllLogTableColumns(false);
            
            List selectedColumns = LogBrokerMonitor.this.updateView();
            LogBrokerMonitor.this._table.setView(selectedColumns);
          } }
      );
    return result;
  }
  
  protected void selectAllLogTableColumns(boolean selected) {
    Iterator columns = getLogTableColumns();
    while (columns.hasNext()) {
      getLogTableColumnMenuItem(columns.next()).setSelected(selected);
    }
  }
  
  protected JMenu createFileMenu() {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('f');
    
    fileMenu.add(createOpenMI());
    fileMenu.add(createOpenURLMI());
    fileMenu.addSeparator();
    fileMenu.add(createCloseMI());
    createMRUFileListMI(fileMenu);
    fileMenu.addSeparator();
    fileMenu.add(createExitMI());
    return fileMenu;
  }




  
  protected JMenuItem createOpenMI() {
    JMenuItem result = new JMenuItem("Open...");
    result.setMnemonic('o');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.requestOpen();
          } }
      );
    return result;
  }




  
  protected JMenuItem createOpenURLMI() {
    JMenuItem result = new JMenuItem("Open URL...");
    result.setMnemonic('u');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.requestOpenURL();
          } }
      );
    return result;
  }
  
  protected JMenuItem createCloseMI() {
    JMenuItem result = new JMenuItem("Close");
    result.setMnemonic('c');
    result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.requestClose();
          } }
      );
    return result;
  }





  
  protected void createMRUFileListMI(JMenu menu) {
    String[] files = this._mruFileManager.getMRUFileList();
    
    if (files != null) {
      menu.addSeparator();
      for (int i = 0; i < files.length; i++) {
        JMenuItem result = new JMenuItem((i + 1) + " " + files[i]);
        result.setMnemonic(i + 1);
        result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
              public void actionPerformed(ActionEvent e) {
                LogBrokerMonitor.this.requestOpenMRU(e);
              } }
          );
        menu.add(result);
      } 
    } 
  }
  
  protected JMenuItem createExitMI() {
    JMenuItem result = new JMenuItem("Exit");
    result.setMnemonic('x');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.requestExit();
          } }
      );
    return result;
  }
  
  protected JMenu createConfigureMenu() {
    JMenu configureMenu = new JMenu("Configure");
    configureMenu.setMnemonic('c');
    configureMenu.add(createConfigureSave());
    configureMenu.add(createConfigureReset());
    configureMenu.add(createConfigureMaxRecords());
    
    return configureMenu;
  }
  
  protected JMenuItem createConfigureSave() {
    JMenuItem result = new JMenuItem("Save");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.saveConfiguration();
          } }
      );
    
    return result;
  }
  
  protected JMenuItem createConfigureReset() {
    JMenuItem result = new JMenuItem("Reset");
    result.setMnemonic('r');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.resetConfiguration();
          } }
      );
    
    return result;
  }
  
  protected JMenuItem createConfigureMaxRecords() {
    JMenuItem result = new JMenuItem("Set Max Number of Records");
    result.setMnemonic('m');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.setMaxRecordConfiguration();
          } }
      );
    
    return result;
  }

  
  protected void saveConfiguration() {
    this._configurationManager.save();
  }
  
  protected void resetConfiguration() {
    this._configurationManager.reset();
  }
  
  protected void setMaxRecordConfiguration() {
    LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);

    
    String temp = inputDialog.getText();
    
    if (temp != null) {
      try {
        setMaxNumberOfLogRecords(Integer.parseInt(temp));
      } catch (NumberFormatException e) {
        LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");

        
        setMaxRecordConfiguration();
      } 
    }
  }

  
  protected JMenu createHelpMenu() {
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('h');
    helpMenu.add(createHelpProperties());
    return helpMenu;
  }
  
  protected JMenuItem createHelpProperties() {
    String title = "LogFactor5 Properties";
    JMenuItem result = new JMenuItem("LogFactor5 Properties");
    result.setMnemonic('l');
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.showPropertiesDialog("LogFactor5 Properties");
          } }
      );
    return result;
  }
  
  protected void showPropertiesDialog(String title) {
    JOptionPane.showMessageDialog(this._logMonitorFrame, this._displayedLogBrokerProperties.toArray(), title, -1);
  }





  
  protected JMenu createEditMenu() {
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('e');
    editMenu.add(createEditFindMI());
    editMenu.add(createEditFindNextMI());
    editMenu.addSeparator();
    editMenu.add(createEditSortNDCMI());
    editMenu.add(createEditRestoreAllNDCMI());
    return editMenu;
  }
  
  protected JMenuItem createEditFindNextMI() {
    JMenuItem editFindNextMI = new JMenuItem("Find Next");
    editFindNextMI.setMnemonic('n');
    editFindNextMI.setAccelerator(KeyStroke.getKeyStroke("F3"));
    editFindNextMI.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this.findSearchText();
          } }
      );
    return editFindNextMI;
  }
  
  protected JMenuItem createEditFindMI() {
    JMenuItem editFindMI = new JMenuItem("Find");
    editFindMI.setMnemonic('f');
    editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));
    
    editFindMI.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Find text: ", "Search Record Messages", 3);





            
            LogBrokerMonitor.this.setSearchText(inputValue);
            LogBrokerMonitor.this.findSearchText();
          } }
      );

    
    return editFindMI;
  }



  
  protected JMenuItem createEditSortNDCMI() {
    JMenuItem editSortNDCMI = new JMenuItem("Sort by NDC");
    editSortNDCMI.setMnemonic('s');
    editSortNDCMI.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Sort by this NDC: ", "Sort Log Records by NDC", 3);





            
            LogBrokerMonitor.this.setNDCTextFilter(inputValue);
            LogBrokerMonitor.this.sortByNDC();
            LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
            LogBrokerMonitor.this.updateStatusLabel();
          } }
      );

    
    return editSortNDCMI;
  }


  
  protected JMenuItem createEditRestoreAllNDCMI() {
    JMenuItem editRestoreAllNDCMI = new JMenuItem("Restore all NDCs");
    editRestoreAllNDCMI.setMnemonic('r');
    editRestoreAllNDCMI.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this._table.getFilteredLogTableModel().setLogRecordFilter(LogBrokerMonitor.this.createLogRecordFilter());
            
            LogBrokerMonitor.this.setNDCTextFilter("");
            LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
            LogBrokerMonitor.this.updateStatusLabel();
          } }
      );
    
    return editRestoreAllNDCMI;
  }
  protected JToolBar createToolBar() {
    String[] fonts;
    JToolBar tb = new JToolBar();
    tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    JComboBox fontCombo = new JComboBox();
    JComboBox fontSizeCombo = new JComboBox();
    this._fontSizeCombo = fontSizeCombo;
    
    ClassLoader cl = getClass().getClassLoader();
    if (cl == null) {
      cl = ClassLoader.getSystemClassLoader();
    }
    URL newIconURL = cl.getResource("org/apache/log4j/lf5/viewer/images/channelexplorer_new.gif");

    
    ImageIcon newIcon = null;
    
    if (newIconURL != null) {
      newIcon = new ImageIcon(newIconURL);
    }
    
    JButton newButton = new JButton("Clear Log Table");
    
    if (newIcon != null) {
      newButton.setIcon(newIcon);
    }
    
    newButton.setToolTipText("Clear Log Table.");

    
    newButton.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            LogBrokerMonitor.this._table.clearLogRecords();
            LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().resetAllNodeCounts();
            LogBrokerMonitor.this.updateStatusLabel();
            LogBrokerMonitor.this.clearDetailTextArea();
            LogRecord.resetSequenceNumber();
          } }
      );

    
    Toolkit tk = Toolkit.getDefaultToolkit();



    
    if (this._loadSystemFonts) {
      fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    } else {
      
      fonts = tk.getFontList();
    } 
    
    for (int j = 0; j < fonts.length; j++) {
      fontCombo.addItem(fonts[j]);
    }
    
    fontCombo.setSelectedItem(this._fontName);
    
    fontCombo.addActionListener(new ActionListener() {
          private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox)e.getSource();
            String font = (String)box.getSelectedItem();
            LogBrokerMonitor.this._table.setFont(new Font(font, 0, LogBrokerMonitor.this._fontSize));
            LogBrokerMonitor.this._fontName = font;
          }
        });

    
    fontSizeCombo.addItem("8");
    fontSizeCombo.addItem("9");
    fontSizeCombo.addItem("10");
    fontSizeCombo.addItem("12");
    fontSizeCombo.addItem("14");
    fontSizeCombo.addItem("16");
    fontSizeCombo.addItem("18");
    fontSizeCombo.addItem("24");
    
    fontSizeCombo.setSelectedItem(String.valueOf(this._fontSize));
    fontSizeCombo.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          
          public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox)e.getSource();
            String size = (String)box.getSelectedItem();
            int s = Integer.valueOf(size).intValue();
            
            LogBrokerMonitor.this.setFontSizeSilently(s);
            LogBrokerMonitor.this.refreshDetailTextArea();
            LogBrokerMonitor.this._fontSize = s;
          } }
      );

    
    tb.add(new JLabel(" Font: "));
    tb.add(fontCombo);
    tb.add(fontSizeCombo);
    tb.addSeparator();
    tb.addSeparator();
    tb.add(newButton);
    
    newButton.setAlignmentY(0.5F);
    newButton.setAlignmentX(0.5F);
    
    fontCombo.setMaximumSize(fontCombo.getPreferredSize());
    fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());

    
    return tb;
  }














  
  protected void setView(String viewString, LogTable table) {
    if ("Detailed".equals(viewString)) {
      table.setDetailedView();
    } else {
      String message = viewString + "does not match a supported view.";
      throw new IllegalArgumentException(message);
    } 
    this._currentView = viewString;
  }
  
  protected JComboBox createLogLevelCombo() {
    JComboBox result = new JComboBox();
    Iterator levels = getLogLevels();
    while (levels.hasNext()) {
      result.addItem(levels.next());
    }
    result.setSelectedItem(this._leastSevereDisplayedLogLevel);
    
    result.addActionListener(new ActionListener() { private final LogBrokerMonitor this$0;
          public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox)e.getSource();
            LogLevel level = (LogLevel)box.getSelectedItem();
            LogBrokerMonitor.this.setLeastSevereDisplayedLogLevel(level);
          } }
      );
    result.setMaximumSize(result.getPreferredSize());
    return result;
  }
  
  protected void setLeastSevereDisplayedLogLevel(LogLevel level) {
    if (level == null || this._leastSevereDisplayedLogLevel == level) {
      return;
    }
    this._leastSevereDisplayedLogLevel = level;
    this._table.getFilteredLogTableModel().refresh();
    updateStatusLabel();
  }






  
  protected void trackTableScrollPane() {}






  
  protected void centerFrame(JFrame frame) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension comp = frame.getSize();
    
    frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
  }







  
  protected void requestOpen() {
    JFileChooser chooser;
    if (this._fileLocation == null) {
      chooser = new JFileChooser();
    } else {
      chooser = new JFileChooser(this._fileLocation);
    } 
    
    int returnVal = chooser.showOpenDialog(this._logMonitorFrame);
    if (returnVal == 0) {
      File f = chooser.getSelectedFile();
      if (loadLogFile(f)) {
        this._fileLocation = chooser.getSelectedFile();
        this._mruFileManager.set(f);
        updateMRUList();
      } 
    } 
  }




  
  protected void requestOpenURL() {
    LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Open URL", "URL:");
    
    String temp = inputDialog.getText();
    
    if (temp != null) {
      if (temp.indexOf("://") == -1) {
        temp = "http://" + temp;
      }
      
      try {
        URL url = new URL(temp);
        if (loadLogFile(url)) {
          this._mruFileManager.set(url);
          updateMRUList();
        } 
      } catch (MalformedURLException e) {
        LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL.");
      } 
    } 
  }





  
  protected void updateMRUList() {
    JMenu menu = this._logMonitorFrame.getJMenuBar().getMenu(0);
    menu.removeAll();
    menu.add(createOpenMI());
    menu.add(createOpenURLMI());
    menu.addSeparator();
    menu.add(createCloseMI());
    createMRUFileListMI(menu);
    menu.addSeparator();
    menu.add(createExitMI());
  }
  
  protected void requestClose() {
    setCallSystemExitOnClose(false);
    closeAfterConfirm();
  }



  
  protected void requestOpenMRU(ActionEvent e) {
    String file = e.getActionCommand();
    StringTokenizer st = new StringTokenizer(file);
    String num = st.nextToken().trim();
    file = st.nextToken("\n");
    
    try {
      int index = Integer.parseInt(num) - 1;
      
      InputStream in = this._mruFileManager.getInputStream(index);
      LogFileParser lfp = new LogFileParser(in);
      lfp.parse(this);
      
      this._mruFileManager.moveToTop(index);
      updateMRUList();
    }
    catch (Exception me) {
      LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "Unable to load file " + file);
    } 
  }


  
  protected void requestExit() {
    this._mruFileManager.save();
    setCallSystemExitOnClose(true);
    closeAfterConfirm();
  }
  
  protected void closeAfterConfirm() {
    StringBuffer message = new StringBuffer();
    
    if (!this._callSystemExitOnClose) {
      message.append("Are you sure you want to close the logging ");
      message.append("console?\n");
      message.append("(Note: This will not shut down the Virtual Machine,\n");
      message.append("or the Swing event thread.)");
    } else {
      message.append("Are you sure you want to exit?\n");
      message.append("This will shut down the Virtual Machine.\n");
    } 
    
    String title = "Are you sure you want to dispose of the Logging Console?";

    
    if (this._callSystemExitOnClose == true) {
      title = "Are you sure you want to exit?";
    }
    int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(), title, 2, 3, null);







    
    if (value == 0) {
      dispose();
    }
  }
  
  protected Iterator getLogLevels() {
    return this._levels.iterator();
  }
  
  protected Iterator getLogTableColumns() {
    return this._columns.iterator();
  }



  
  protected boolean loadLogFile(File file) {
    boolean ok = false;
    try {
      LogFileParser lfp = new LogFileParser(file);
      lfp.parse(this);
      ok = true;
    } catch (IOException e) {
      LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading " + file.getName());
    } 

    
    return ok;
  }



  
  protected boolean loadLogFile(URL url) {
    boolean ok = false;
    try {
      LogFileParser lfp = new LogFileParser(url.openStream());
      lfp.parse(this);
      ok = true;
    } catch (IOException e) {
      LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL:" + url.getFile());
    } 
    
    return ok;
  }


  
  class LogBrokerMonitorWindowAdaptor
    extends WindowAdapter
  {
    protected LogBrokerMonitor _monitor;
    
    private final LogBrokerMonitor this$0;

    
    public LogBrokerMonitorWindowAdaptor(LogBrokerMonitor monitor) {
      this._monitor = monitor;
    }
    
    public void windowClosing(WindowEvent ev) {
      this._monitor.requestClose();
    }
  }
}
