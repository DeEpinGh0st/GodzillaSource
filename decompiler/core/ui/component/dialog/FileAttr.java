package core.ui.component.dialog;

import core.EasyI18N;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;


public class FileAttr
  extends JDialog
{
  private final Payload payload;
  private final ShellEntity shellEntity;
  private final JLabel fileLabel;
  private final JTextField fileTextField;
  private final JLabel filePermissionLabel;
  private final JTextField filePermissionTextField;
  private final JButton updateFilePermissionButton;
  private final JLabel fileTimeLabel;
  private final JTextField fileTimeTextField;
  private final JButton updateFileTimeButton;
  
  public FileAttr(ShellEntity shellEntity, String file, String filePermission, String fileTime) {
    super((Frame)shellEntity.getFrame(), "FileAttr", true);
    
    this.fileLabel = new JLabel("文件路径: ");
    this.fileTextField = new JTextField(20);
    
    this.filePermissionLabel = new JLabel("文件权限: ");
    this.filePermissionTextField = new JTextField(5);
    this.updateFilePermissionButton = new JButton("修改");
    
    this.fileTimeLabel = new JLabel("文件修改时间: ");
    this.fileTimeTextField = new JTextField(15);
    this.updateFileTimeButton = new JButton("修改");
    
    GBC gbcFileLabel = new GBC(0, 0);
    GBC gbcFileTextField = (new GBC(1, 0)).setInsets(0, 10, 0, 10);
    GBC gbcFilePermissionLabel = new GBC(0, 1);
    GBC gbcFilePermissionTextField = (new GBC(1, 1)).setInsets(0, 20, 0, 20);
    GBC gbcUpdateFilePermissionButton = (new GBC(2, 1)).setInsets(0, 20, 0, 20);
    GBC gbcFileTimeLabel = new GBC(0, 2);
    GBC gbcFileTimeTextField = (new GBC(1, 2)).setInsets(0, 10, 0, 10);
    GBC gbcUpdateFileTimeButton = (new GBC(2, 2)).setInsets(0, 20, 0, 20);
    
    this.fileTextField.setText(file);
    this.filePermissionTextField.setText(filePermission);
    this.fileTimeTextField.setText(fileTime);

    
    setLayout(new GridBagLayout());
    Container container = getContentPane();

    
    container.add(this.fileLabel, gbcFileLabel);
    container.add(this.fileTextField, gbcFileTextField);
    container.add(this.filePermissionLabel, gbcFilePermissionLabel);
    container.add(this.filePermissionTextField, gbcFilePermissionTextField);
    container.add(this.updateFilePermissionButton, gbcUpdateFilePermissionButton);
    container.add(this.fileTimeLabel, gbcFileTimeLabel);
    container.add(this.fileTimeTextField, gbcFileTimeTextField);
    container.add(this.updateFileTimeButton, gbcUpdateFileTimeButton);

    
    this.shellEntity = shellEntity;
    this.payload = shellEntity.getPayloadModule();
    
    automaticBindClick.bindJButtonClick(this, this);

    
    functions.setWindowSize(this, 520, 130);
    setLocationRelativeTo((Component)shellEntity.getFrame());
    EasyI18N.installObject(this);
    setVisible(true);
  }
  
  public void updateFilePermissionButtonClick(ActionEvent e) {
    boolean state = false;
    String fileName = this.fileTextField.getText().trim();
    String filePermission = this.filePermissionTextField.getText().trim();
    try {
      state = this.payload.setFileAttr(fileName, "fileBasicAttr", filePermission);
    } catch (Exception ex) {
      Log.error(ex);
    } finally {
      
      if (state) {
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "修改成功", "提示", 1);
        dispose();
      } else {
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "修改失败", "提示", 2);
      } 
    } 
  }
  public void updateFileTimeButtonClick(ActionEvent e) {
    boolean state = false;
    String fileName = this.fileTextField.getText().trim();
    String fileTime = this.fileTimeTextField.getText().trim();
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String timestampString = Long.toString(simpleDateFormat.parse(fileTime).getTime());
      if (timestampString.length() > 10) {
        timestampString = timestampString.substring(0, 10);
      }
      state = this.payload.setFileAttr(fileName, "fileTimeAttr", timestampString);
    } catch (Exception ex) {
      Log.error(ex);
    } finally {
      
      if (state) {
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "修改成功", "提示", 1);
        dispose();
      } else {
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "修改失败", "提示", 2);
      } 
    } 
  }
}
