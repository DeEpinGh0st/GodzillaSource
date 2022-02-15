package core.ui.component.dialog;

import core.ApplicationContext;
import core.Db;
import core.EasyI18N;
import core.ui.MainActivity;
import core.ui.component.DataView;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.Log;
import util.automaticBindClick;
import util.functions;





public class PluginManage
  extends JDialog
{
  private final DataView pluginView;
  private final JButton addPluginButton;
  private final JButton removeButton;
  private final JButton cancelButton;
  private final JButton refreshButton;
  private final Vector<String> columnVector;
  private final JSplitPane splitPane;
  
  public PluginManage() {
    super((Frame)MainActivity.getFrame(), "PluginManage", true);
    
    this.addPluginButton = new JButton("添加");
    this.removeButton = new JButton("移除");
    this.refreshButton = new JButton("刷新");
    this.cancelButton = new JButton("取消");
    this.splitPane = new JSplitPane();

    
    this.columnVector = new Vector<>();
    this.columnVector.add("pluginJarFile");
    
    this.pluginView = new DataView(null, this.columnVector, -1, -1);
    refreshPluginView();
    
    JPanel bottomPanel = new JPanel();
    
    bottomPanel.add(this.addPluginButton);
    bottomPanel.add(this.removeButton);
    bottomPanel.add(this.refreshButton);
    bottomPanel.add(this.cancelButton);
    
    this.splitPane.setOrientation(0);
    this.splitPane.setTopComponent(new JScrollPane((Component)this.pluginView));
    this.splitPane.setBottomComponent(bottomPanel);
    
    this.splitPane.addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            PluginManage.this.splitPane.setDividerLocation(0.85D);
          }
        });
    
    automaticBindClick.bindJButtonClick(this, this);
    
    add(this.splitPane);
    
    functions.setWindowSize(this, 420, 420);
    
    setLocationRelativeTo((Component)MainActivity.getFrame());
    setDefaultCloseOperation(2);
    
    EasyI18N.installObject(this);
    
    setVisible(true);
  }
  private void refreshPluginView() {
    String[] pluginStrings = Db.getAllPlugin();
    Vector<Vector<String>> rows = new Vector<>();
    
    for (int i = 0; i < pluginStrings.length; i++) {
      String string = pluginStrings[i];
      Vector<String> rowVector = new Vector<>();
      rowVector.add(string);
      rows.add(rowVector);
    } 
    this.pluginView.AddRows(rows);
    this.pluginView.getModel().fireTableDataChanged();
  }
  private void addPluginButtonClick(ActionEvent actionEvent) {
    GFileChooser chooser = new GFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("*.jar", new String[] { "jar" }));
    chooser.setFileSelectionMode(0);
    boolean flag = (0 == chooser.showDialog(new JLabel(), "选择"));
    File selectdFile = chooser.getSelectedFile();
    if (flag && selectdFile != null) {
      if (Db.addPlugin(selectdFile.getAbsolutePath()) == 1) {
        ApplicationContext.init();
        GOptionPane.showMessageDialog(this, "添加插件成功", "提示", 1);
      } else {
        GOptionPane.showMessageDialog(this, "添加插件失败", "提示", 2);
      } 
    } else {
      Log.log("用户取消选择.....", new Object[0]);
    } 
    refreshPluginView();
  }
  
  private void removeButtonClick(ActionEvent actionEvent) {
    int rowIndex = this.pluginView.getSelectedRow();
    if (rowIndex != -1) {
      Object selectedItem = this.pluginView.getValueAt(rowIndex, 0);
      if (Db.removePlugin((String)selectedItem) == 1) {
        GOptionPane.showMessageDialog(this, "移除插件成功", "提示", 1);
      } else {
        GOptionPane.showMessageDialog(this, "移除插件失败", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(this, "没有选中插件", "提示", 2);
    } 
    refreshPluginView();
  }
  private void cancelButtonClick(ActionEvent actionEvent) {
    dispose();
  }
  private void refreshButtonClick(ActionEvent actionEvent) {
    refreshPluginView();
  }
}
