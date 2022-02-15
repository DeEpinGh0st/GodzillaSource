package core.ui.component.dialog;

import core.ApplicationContext;
import core.Db;
import core.EasyI18N;
import core.ui.MainActivity;
import core.ui.component.RTextArea;
import core.ui.component.SimplePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import util.Log;
import util.OpenC;
import util.automaticBindClick;
import util.functions;

















public class ShellSuperRequest
  extends JDialog
{
  private final JTabbedPane tabbedPane;
  private final SimplePanel basicConfigPanel;
  private final SimplePanel randomUaPanel;
  private final SimplePanel requestParameterNamePanel;
  private JLabel openRandomRequestParameterLabel;
  private JCheckBox openRandomRequestParameterCheckBox;
  private JLabel randomRequestParameterNumLabel;
  private JTextField randomRequestParameterNumTextField;
  private JLabel randomRequestParameterNameSizeLabel;
  private JTextField randomRequestParameterNameSizeTextField;
  private JLabel randomRequestParameterSizeLabel;
  private JTextField randomRequestParameterSizeTextField;
  private JLabel openRandomUaLabel;
  private JCheckBox openRandomUaCheckBox;
  private JLabel openRandomReqParameterLabel;
  private JCheckBox openRandomReqParameterCheckBox;
  private JLabel randomReqParameterSizeLabel;
  private JTextField randomReqParameterSizeTextField;
  private RTextArea uaTextArea;
  private JButton basicConfigUpdateButton;
  private JButton uaUpdateButton;
  private JButton requestParameterNameUpdateButton;
  
  public ShellSuperRequest() {
    super((Frame)MainActivity.getFrame(), "ShellSuperRequest", true);
    
    this.tabbedPane = new JTabbedPane();
    
    this.basicConfigPanel = new SimplePanel();
    this.randomUaPanel = new SimplePanel(new BorderLayout(1, 1));
    this.requestParameterNamePanel = new SimplePanel(new BorderLayout(1, 1));
    
    initbasicConfigPanel();
    initRandomUaPanel();
    
    this.tabbedPane.addTab("配置", (Component)this.basicConfigPanel);
    this.tabbedPane.addTab("随机UA", (Component)this.randomUaPanel);
    
    add(this.tabbedPane);
    
    automaticBindClick.bindJButtonClick(this, this);
    functions.setWindowSize(this, 650, 500);
    setLocationRelativeTo((Component)MainActivity.getFrame());
    EasyI18N.installObject(this);
    setVisible(true);
  }
  
  private void initbasicConfigPanel() {
    this.openRandomRequestParameterLabel = new JLabel("随机请求参数: ");
    this.openRandomUaLabel = new JLabel("随机Ua: ");
    this.randomRequestParameterSizeLabel = new JLabel("随机请求参数值大小: ");
    this.randomRequestParameterNumLabel = new JLabel("随机请求参数数量: ");
    this.randomRequestParameterNameSizeLabel = new JLabel("随机请求参数名大小: ");
    this.openRandomReqParameterLabel = new JLabel("随机ReqParameter: ");
    this.randomReqParameterSizeLabel = new JLabel("随机ReqParameter大小: ");
    
    this.openRandomRequestParameterCheckBox = new JCheckBox("开启", ApplicationContext.isOpenC("openRandomRequestParameter"));
    this.openRandomUaCheckBox = new JCheckBox("开启", ApplicationContext.isOpenC("openRandomUa"));
    this.openRandomReqParameterCheckBox = new JCheckBox("开启", ApplicationContext.isOpenC("openRandomReqParameter"));
    
    String v = Db.getSetingValue("RandomRequestParameterNum");
    
    this.randomRequestParameterNumTextField = new JTextField((v == null) ? "1-5" : v);
    
    v = Db.getSetingValue("RandomRequestParameterSize");
    
    this.randomRequestParameterSizeTextField = new JTextField((v == null) ? "10-30" : v);
    
    v = Db.getSetingValue("RandomRequestParameterNameSize");
    
    this.randomRequestParameterNameSizeTextField = new JTextField((v == null) ? "3-7" : v);
    
    v = Db.getSetingValue("RandomReqParameterSize");
    
    this.randomReqParameterSizeTextField = new JTextField((v == null) ? "10-20" : v);
    
    this.randomRequestParameterNumTextField.setColumns(8);
    this.randomRequestParameterSizeTextField.setColumns(8);
    this.randomRequestParameterNameSizeTextField.setColumns(8);
    this.randomReqParameterSizeTextField.setColumns(8);
    
    this.basicConfigUpdateButton = new JButton("修改");
    
    this.openRandomRequestParameterCheckBox.addActionListener((ActionListener)new OpenC("openRandomRequestParameter", this.openRandomRequestParameterCheckBox));
    this.openRandomUaCheckBox.addActionListener((ActionListener)new OpenC("openRandomUa", this.openRandomUaCheckBox));
    this.openRandomReqParameterCheckBox.addActionListener((ActionListener)new OpenC("openRandomReqParameter", this.openRandomReqParameterCheckBox));
    
    this.basicConfigPanel.setSetup(-270);
    
    this.basicConfigPanel.addX(new Component[] { this.openRandomRequestParameterLabel, this.openRandomRequestParameterCheckBox });
    this.basicConfigPanel.addX(new Component[] { this.openRandomUaLabel, this.openRandomUaCheckBox });
    this.basicConfigPanel.addX(new Component[] { this.openRandomReqParameterLabel, this.openRandomReqParameterCheckBox });
    this.basicConfigPanel.addX(new Component[] { this.randomRequestParameterNumLabel, this.randomRequestParameterNumTextField });
    this.basicConfigPanel.addX(new Component[] { this.randomRequestParameterNameSizeLabel, this.randomRequestParameterNameSizeTextField });
    this.basicConfigPanel.addX(new Component[] { this.randomRequestParameterSizeLabel, this.randomRequestParameterSizeTextField });
    this.basicConfigPanel.addX(new Component[] { this.randomReqParameterSizeLabel, this.randomReqParameterSizeTextField });
    this.basicConfigPanel.addX(new Component[] { this.basicConfigUpdateButton });
  }
  
  private void initRandomUaPanel() {
    this.uaTextArea = new RTextArea();
    this.uaUpdateButton = new JButton("修改");
    this.uaTextArea.setText(Db.getSetingValue("RandomUa"));
    Dimension dimension = new Dimension();
    dimension.height = 30;
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(0);
    JPanel bottomPanel = new JPanel();
    splitPane.setTopComponent(new JScrollPane((Component)this.uaTextArea));
    bottomPanel.add(this.uaUpdateButton);
    bottomPanel.setMaximumSize(dimension);
    bottomPanel.setMinimumSize(dimension);
    splitPane.setBottomComponent(bottomPanel);
    
    splitPane.setResizeWeight(0.9D);
    
    this.randomUaPanel.add(splitPane);
  }



  
  private void basicConfigUpdateButtonClick(ActionEvent actionEvent) {
    String randomRequestParameterNum = this.randomRequestParameterNumTextField.getText();
    
    String randomRequestParameterSize = this.randomRequestParameterSizeTextField.getText();
    String randomRequestParameterNameSize = this.randomRequestParameterNameSizeTextField.getText();
    String randomReqParameterSize = this.randomReqParameterSizeTextField.getText();
    
    if (Db.updateSetingKV("RandomReqParameterSize", randomReqParameterSize) && Db.updateSetingKV("RandomRequestParameterNameSize", randomRequestParameterNameSize) && Db.updateSetingKV("RandomRequestParameterSize", randomRequestParameterSize) && Db.updateSetingKV("RandomRequestParameterNum", randomRequestParameterNum)) {
      GOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
    } 
  }

  
  private void uaUpdateButtonClick(ActionEvent actionEvent) {
    String ua = this.uaTextArea.getText();
    if (Db.updateSetingKV("RandomUa", ua)) {
      GOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
    } 
  }
  
  public static String randomUa() {
    String ret = null;
    try {
      String allUaString = Db.getSetingValue("RandomUa");
      if (allUaString != null && ApplicationContext.isOpenC("openRandomUa")) {
        String[] uas = allUaString.split("\n");
        int index = functions.random(0, uas.length);
        ret = uas[index].trim();
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    return ret;
  }
  
  public static String randomReqParameter() {
    String ret = null;
    try {
      if (ApplicationContext.isOpenC("openRandomReqParameter")) {
        String[] pms = Db.getSetingValue("RandomReqParameterSize").split("-");
        if (pms.length == 2) {
          int startIndex = Integer.valueOf(pms[0]).intValue();
          int endIndex = Integer.valueOf(pms[1]).intValue();
          int r = functions.random(startIndex, endIndex);
          ret = functions.getRandomString(r);
        } 
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    return ret;
  }
  
  public static String randomRequestParameter(int num) {
    String ret = "";
    
    try {
      if (ApplicationContext.isOpenC("openRandomRequestParameter")) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
          builder.append(randomOneRequestParameter());
          builder.append("&");
        } 
        if (builder.length() > 1) {
          builder.deleteCharAt(builder.length() - 1);
        }
        ret = builder.toString();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 

    
    return ret;
  }
  
  public static int getRandomRequestParameterNum() {
    int ret = -1;
    
    try {
      String[] pms = Db.getSetingValue("RandomRequestParameterNum").split("-");
      int s = Integer.valueOf(pms[0]).intValue();
      int e = Integer.valueOf(pms[1]).intValue();
      
      ret = functions.random(s, e);
    }
    catch (Exception e) {
      Log.error(e);
    } 

    
    return ret;
  }
  
  private static String randomOneRequestParameter() {
    String[] v = Db.getSetingValue("RandomRequestParameterNameSize").split("-");
    int nameStartIndex = 0;
    int nameEndIndex = 0;
    
    int valueStartIndex = 0;
    int valueEndIndex = 0;
    
    if (v.length == 2) {
      nameStartIndex = Integer.valueOf(v[0]).intValue();
      nameEndIndex = Integer.valueOf(v[1]).intValue();
    } 
    v = Db.getSetingValue("RandomRequestParameterSize").split("-");
    if (v.length == 2) {
      valueStartIndex = Integer.valueOf(v[0]).intValue();
      valueEndIndex = Integer.valueOf(v[1]).intValue();
    } 
    
    return functions.getRandomString(functions.random(nameStartIndex, nameEndIndex)) + "=" + functions.getRandomString(functions.random(valueStartIndex, valueEndIndex));
  }
}
