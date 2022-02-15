package core.ui.component.frame;
import core.Db;
import core.EasyI18N;
import core.shell.ShellEntity;
import core.ui.MainActivity;
import core.ui.component.DataView;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.dialog.ShellSetting;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class LiveScan extends JDialog {
  private DataView shellView;
  private JButton addShellButton;
  private JButton removeFailShellButton;
  private JButton scanButton;
  private ComponentRenderer COMPONENT_RENDERER = new ComponentRenderer(); private JButton refreshButton;
  private Vector<String> columnVector;
  private static JLabel OK_LABEL = new JLabel("Succes"); private JSplitPane splitPane; private boolean isRuning; private String groupName;
  private static JLabel FAIL_LABEL = new JLabel("Fail");
  private static JLabel WAIT_LABEL = new JLabel("wait");
  private static JLabel DELETE_LABEL = new JLabel("deleted");
  
  static {
    OK_LABEL.setOpaque(true);
    FAIL_LABEL.setOpaque(true);
    WAIT_LABEL.setOpaque(true);
    DELETE_LABEL.setOpaque(true);
    
    DELETE_LABEL.setBackground(Color.DARK_GRAY);
    WAIT_LABEL.setBackground(Color.CYAN);
    OK_LABEL.setBackground(Color.GREEN);
    FAIL_LABEL.setBackground(Color.RED);
  }
  
  public LiveScan() {
    this("/");
  }
  public LiveScan(String groupId) {
    super((Frame)MainActivity.getFrame(), "LiveScan", true);
    this.groupName = groupId;
    
    this.addShellButton = new JButton("添加Shell");
    this.removeFailShellButton = new JButton("移除所有失败");
    this.refreshButton = new JButton("刷新");
    this.scanButton = new JButton("扫描");
    this.splitPane = new JSplitPane();


    
    Vector<Vector<String>> allShellVector = new Vector<>();
    
    allShellVector.addAll(Db.getAllShell(this.groupName));
    
    this.columnVector = allShellVector.remove(0);
    
    this.columnVector.add("Status");
    
    this.shellView = new DataView(null, this.columnVector, -1, -1);

    
    refreshshellView();
    
    JPanel bottomPanel = new JPanel();
    
    bottomPanel.add(this.addShellButton);
    bottomPanel.add(this.scanButton);
    bottomPanel.add(this.refreshButton);
    bottomPanel.add(this.removeFailShellButton);

    
    this.splitPane.setOrientation(0);
    this.splitPane.setTopComponent(new JScrollPane((Component)this.shellView));
    this.splitPane.setBottomComponent(bottomPanel);
    
    this.splitPane.addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            LiveScan.this.splitPane.setDividerLocation(0.85D);
          }
        });

    
    JMenuItem removeShellMenuItem = new JMenuItem("删除");
    removeShellMenuItem.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            int selectedRow = LiveScan.this.shellView.getSelectedRow();
            int lastColumn = LiveScan.this.shellView.getColumnCount() - 1;
            if (selectedRow != -1) {
              String shellId = (String)LiveScan.this.shellView.getValueAt(selectedRow, 0);
              if (shellId != null) {
                ShellEntity shellEntity = Db.getOneShell(shellId);
                Log.log("removeShell -> " + shellEntity.toString(), new Object[0]);
                if (Db.removeShell(shellId) > 0) {
                  GOptionPane.showMessageDialog(null, "删除成功");
                } else {
                  GOptionPane.showMessageDialog(null, "删除失败");
                } 
                LiveScan.this.shellView.setValueAt(LiveScan.DELETE_LABEL, selectedRow, lastColumn);
              } 
            } 
          }
        });
    this.shellView.getRightClickMenu().add(removeShellMenuItem);
    
    automaticBindClick.bindJButtonClick(this, this);
    
    add(this.splitPane);
    
    functions.setWindowSize(this, 510, 430);
    
    setLocationRelativeTo((Component)MainActivity.getFrame());
    setDefaultCloseOperation(2);
    this.shellView.getColumn("Status").setCellRenderer(this.COMPONENT_RENDERER);
    EasyI18N.installObject(this);
    EasyI18N.installObject(this.shellView);
    setVisible(true);
  }
  protected void refreshshellView() {
    Vector<Vector<String>> rows = Db.getAllShell(this.groupName);
    rows.remove(0);
    
    rows.forEach(oneRow -> oneRow.add("WAIT_LABEL"));

    
    this.shellView.AddRows(rows);
    
    int max = rows.size();
    int lastColumn = this.shellView.getColumnCount() - 1;
    
    for (int i = 0; i < max; i++) {
      this.shellView.setValueAt(WAIT_LABEL, i, lastColumn);
    }
    this.shellView.getModel().fireTableDataChanged();
  }
  protected void addShellButtonClick(ActionEvent actionEvent) {
    ShellSetting setting = new ShellSetting(null);
    refreshshellView();
  }
  private void removeFailShellButtonClick(ActionEvent actionEvent) {
    int max = this.shellView.getRowCount();
    int lastColumn = this.shellView.getColumnCount() - 1;
    Object valueObject = null;
    int removeNum = 0;
    for (int i = 0; i < max; i++) {
      valueObject = this.shellView.getValueAt(i, lastColumn);
      if (FAIL_LABEL.equals(valueObject)) {
        String shellId = (String)this.shellView.getValueAt(i, 0);
        if (shellId != null) {
          ShellEntity shellEntity = Db.getOneShell(shellId);
          Db.removeShell(shellId);
          Log.log("removeShell -> " + shellEntity.toString(), new Object[0]);
          this.shellView.setValueAt(DELETE_LABEL, i, lastColumn);
          removeNum++;
        } 
      } 
    } 
    GOptionPane.showMessageDialog(this, String.format(EasyI18N.getI18nString("共删除%s条Shell"), new Object[] { Integer.valueOf(removeNum) }));
  }
  protected synchronized void scanButtonClick(ActionEvent actionEvent) {
    if (!this.isRuning) {
      (new Thread(new Runnable()
          {
            public void run() {
              try {
                LiveScan.this.scanStrart();
              } catch (Exception e) {
                Log.error(e);
              } finally {
                LiveScan.this.isRuning = false;
              } 
            }
          })).start();
      GOptionPane.showMessageDialog(this, "已开始存活检测");
    } else {
      GOptionPane.showMessageDialog(this, "正在检测");
    } 
  }
  protected void scanStrart() {
    long startTime = System.currentTimeMillis();
    int max = this.shellView.getRowCount();
    int lastColumn = this.shellView.getColumnCount() - 1;
    Object valueObject = null;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(30, 50, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    Log.log(String.format("LiveScanStart startTime:%s", new Object[] { (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Long.valueOf(System.currentTimeMillis())) }), new Object[0]);
    for (int i = 0; i < max; i++) {
      this.shellView.setValueAt(WAIT_LABEL, i, lastColumn);
      String shellId = (String)this.shellView.getValueAt(i, 0);
      executor.execute(new ScanShellRunnable(shellId, this.shellView, i, lastColumn));
    } 
    while (executor.getActiveCount() != 0);

    
    executor.shutdown();
    long endTime = System.currentTimeMillis();
    Log.log(String.format("LiveScanComplete completeTime:%s", new Object[] { (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Long.valueOf(System.currentTimeMillis())) }), new Object[0]);
    int succes = 0;
    int fail = 0;
    for (int j = 0; j < max; j++) {
      valueObject = this.shellView.getValueAt(j, lastColumn);
      if (OK_LABEL.equals(valueObject)) {
        succes++;
      } else if (FAIL_LABEL.equals(valueObject)) {
        fail++;
      } 
    } 
    Log.log(String.format("LiveScanComplete: 用时:%sms", new Object[] { Long.valueOf(endTime - startTime) }), new Object[0]);
    setTitle(String.format("LiveScan all:%s succes:%s fail:%s", new Object[] { Integer.valueOf(max), Integer.valueOf(succes), Integer.valueOf(fail) }));
    GOptionPane.showMessageDialog(this, "Scan complete!");
    Log.log("Scan complete!", new Object[0]);
  }
  protected void refreshButtonClick(ActionEvent actionEvent) {
    refreshshellView();
    this.shellView.getColumn("Status").setCellRenderer(this.COMPONENT_RENDERER);
  }
  
  class ScanShellRunnable implements Runnable { private String shellId;
    private DataView dataView;
    private int rowId;
    private int columnId;
    
    public ScanShellRunnable(String shellId, DataView dataView, int rowId, int columnId) {
      this.shellId = shellId;
      this.dataView = dataView;
      this.rowId = rowId;
      this.columnId = columnId;
    }

    
    public void run() {
      boolean ok = false;
      try {
        ShellEntity shellEntity = Db.getOneShell(this.shellId);
        ok = shellEntity.initShellOpertion();
        try {
          if (ok) {
            shellEntity.getPayloadModule().close();
          }
        } catch (Exception e) {
          Log.error(e);
        } 
      } catch (Exception e) {
        Log.error(e);
      } 
      final boolean finalOk = ok;
      try {
        SwingUtilities.invokeAndWait(new Runnable()
            {
              public void run() {
                if (finalOk) {
                  LiveScan.ScanShellRunnable.this.dataView.setValueAt(LiveScan.OK_LABEL, LiveScan.ScanShellRunnable.this.rowId, LiveScan.ScanShellRunnable.this.columnId);
                } else {
                  LiveScan.ScanShellRunnable.this.dataView.setValueAt(LiveScan.FAIL_LABEL, LiveScan.ScanShellRunnable.this.rowId, LiveScan.ScanShellRunnable.this.columnId);
                } 
              }
            });
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } 
    } }



  
  class ComponentRenderer
    implements TableCellRenderer
  {
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
