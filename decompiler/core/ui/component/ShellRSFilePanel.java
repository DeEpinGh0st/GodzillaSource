package core.ui.component;

import core.ApplicationContext;
import core.EasyI18N;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.frame.EditFileFrame;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;

public class ShellRSFilePanel
  extends JPanel
{
  private JComboBox<String> encodingComboBox;
  private JButton saveButton;
  private JButton refreshButton;
  private JButton backButton;
  private JTextField readFileTextField;
  private RTextArea fileDataTextArea;
  private JPanel parentPanel;
  private Payload payload;
  private CardLayout cardLayout = null;
  private JSplitPane splitPane;
  private ShellEntity shellContext;
  private byte[] fileData;
  private String containerName = null;
  
  private JPanel topPanel;
  private RTextScrollPane scrollPane;
  
  public ShellRSFilePanel(ShellEntity shellContext, JPanel parentPanel, String containerName) {
    super(new BorderLayout());
    
    this.parentPanel = parentPanel;
    if (parentPanel != null) {
      this.cardLayout = (CardLayout)parentPanel.getLayout();
      this.containerName = containerName;
    } 
    this.payload = shellContext.getPayloadModule();
    this.shellContext = shellContext;
    
    this.topPanel = new JPanel();
    this.encodingComboBox = new JComboBox<>(ApplicationContext.getAllEncodingTypes());
    this.saveButton = new JButton("保存");
    this.refreshButton = new JButton("刷新");
    this.backButton = new JButton("返回");
    this.splitPane = new JSplitPane();
    this.readFileTextField = new JTextField(80);
    this.fileDataTextArea = new RTextArea();
    this.scrollPane = new RTextScrollPane((RTextArea)this.fileDataTextArea, true);
    this.scrollPane.setIconRowHeaderEnabled(true);
    this.scrollPane.getGutter().setBookmarkingEnabled(true);
    
    this.topPanel.add(this.readFileTextField, (new GBC(1, 1)).setFill(2).setWeight(1.0D, 0.0D));
    this.topPanel.add(this.encodingComboBox);
    this.topPanel.add(this.saveButton);
    this.topPanel.add(this.refreshButton);
    this.topPanel.add(this.backButton);
    
    this.splitPane.setOrientation(0);
    this.splitPane.setTopComponent(this.topPanel);
    this.splitPane.setBottomComponent((Component)this.scrollPane);


    
    this.encodingComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent paramActionEvent)
          {
            if (ShellRSFilePanel.this.fileData != null) {
              ShellRSFilePanel.this.encodingTypeString = (String)ShellRSFilePanel.this.encodingComboBox.getSelectedItem();
              ShellRSFilePanel.this.refreshData();
            } 
          }
        });

    
    automaticBindClick.bindJButtonClick(this, this);
    
    this.encodingTypeString = (String)this.encodingComboBox.getSelectedItem();
    
    add(this.splitPane);
    EasyI18N.installObject(this);
  }
  private String encodingTypeString; private String currentFile;
  
  public void rsFile(String file) {
    if (this.payload.isAlive() || this.shellContext.isUseCache()) {
      this.currentFile = file;
      this.readFileTextField.setText(file);
      this.fileData = this.payload.downloadFile(file);
      UiFunction.setSyntaxEditingStyle(this.fileDataTextArea, file);
      refreshData();
    } else {
      this.readFileTextField.setText(this.currentFile);
      GOptionPane.showMessageDialog(this, "刷新失败 有效载荷已经销毁", "提示", 2);
    } 
  }
  
  private void refreshData() {
    try {
      this.fileDataTextArea.setText(new String(this.fileData, this.encodingTypeString));
    } catch (Exception e) {
      this.fileDataTextArea.setText(new String(this.fileData));
      Log.error(e);
    } 
  }
  
  public void saveButtonClick(ActionEvent e) {
    if (this.payload.isAlive()) {
      String fileString = this.readFileTextField.getText();
      boolean uploadState = this.payload.uploadFile(fileString, functions.stringToByteArray(this.fileDataTextArea.getText(), this.encodingTypeString));
      if (uploadState) {
        GOptionPane.showMessageDialog(this, "保存成功", "提示", 1);
      } else {
        GOptionPane.showMessageDialog(this, "保存失败", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(this, "保存失败 有效载荷已经销毁", "提示", 2);
    } 
  }
  public void refreshButtonClick(ActionEvent e) {
    rsFile(this.readFileTextField.getText());
  }
  public void backButtonClick(ActionEvent e) {
    if (this.cardLayout != null) {
      this.fileData = null;
      this.fileDataTextArea.setText("");
      this.readFileTextField.setText("");
      this.cardLayout.show(this.parentPanel, this.containerName);
    } 
  }

  
  public String getFile() {
    return this.currentFile;
  }
  public String getShellId() {
    return this.shellContext.getId();
  }
  
  public void openThisToEditFileFrame() {
    EditFileFrame.OpenNewEdit(this);
  }
}
