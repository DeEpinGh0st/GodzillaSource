package core.ui.component.dialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import core.EasyI18N;
import core.ui.component.ShellGroup;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import util.functions;

public class ChooseGroup extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  
  public ChooseGroup(Window parentWindow, String defaultGroup) {
    this.parentWindow = parentWindow;
    
    $$$setupUI$$$();
    
    this.groupTree.setSelectNote(defaultGroup);
    setContentPane(this.contentPane);
    setModal(true);
    getRootPane().setDefaultButton(this.buttonOK);
    
    this.buttonOK.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ChooseGroup.this.onOK();
          }
        });
    
    this.buttonCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ChooseGroup.this.onCancel();
          }
        });

    
    setDefaultCloseOperation(0);
    addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            ChooseGroup.this.onCancel();
          }
        });

    
    this.contentPane.registerKeyboardAction(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ChooseGroup.this.onCancel();
          }
        },  KeyStroke.getKeyStroke(27, 0), 1);
    
    EasyI18N.installObject(this);
  }
  public JPanel groupPanel; public ShellGroup groupTree; private String groupId; private Window parentWindow;
  private void onOK() {
    this.groupId = this.groupTree.getSelectedGroupName();
    if (this.groupId.isEmpty()) {
      this.groupId = null;
      GOptionPane.showMessageDialog(UiFunction.getParentWindow((Container)this.groupTree), "未选中组!");
      
      return;
    } 
    dispose();
  }
  
  private void onCancel() {
    this.groupId = null;
    
    dispose();
  }
  
  public String getChooseGroup() {
    setTitle("选择分组");
    pack();
    functions.setWindowSize(this, 600, 630);
    setLocationRelativeTo(this.parentWindow);
    EasyI18N.installObject(this);
    setVisible(true);
    return this.groupId;
  }







  
  private void $$$setupUI$$$() {
    this.contentPane = new JPanel();
    this.contentPane.setLayout((LayoutManager)new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
    JPanel panel1 = new JPanel();
    panel1.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    this.contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 1, null, null, null, 0, false));
    Spacer spacer1 = new Spacer();
    panel1.add((Component)spacer1, new GridConstraints(0, 0, 1, 1, 0, 1, 4, 1, null, null, null, 0, false));
    JPanel panel2 = new JPanel();
    panel2.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
    panel1.add(panel2, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
    this.buttonOK = new JButton();
    this.buttonOK.setText("OK");
    panel2.add(this.buttonOK, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    this.buttonCancel = new JButton();
    this.buttonCancel.setText("Cancel");
    panel2.add(this.buttonCancel, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    this.groupPanel = new JPanel();
    this.groupPanel.setLayout((LayoutManager)new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    this.contentPane.add(this.groupPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
    JScrollPane scrollPane1 = new JScrollPane();
    this.groupPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, 0, 3, 5, 5, null, null, null, 0, false));
    this.groupTree = new ShellGroup();
    scrollPane1.setViewportView((Component)this.groupTree);
  }



  
  public JComponent $$$getRootComponent$$$() {
    return this.contentPane;
  }
  
  private void createUIComponents() {}
}
