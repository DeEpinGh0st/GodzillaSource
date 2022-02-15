package shells.plugins.generic.seting;

import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.SettingsProvider;
import core.Db;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;


public class SuperTerminalSeting
  extends JPanel
{
  private static final String SHOW_STR = "你好 hello ❤❤❤";
  private JLabel fontLabel;
  private JLabel sizeLabel;
  private JLabel fontTypeLabel;
  private JLabel terminalStyleLabel;
  
  public SuperTerminalSeting() {
    super(new BorderLayout());
    this.fontLabel = new JLabel("字体: ");
    this.sizeLabel = new JLabel("字体大小: ");
    this.fontTypeLabel = new JLabel("字体类型 : ");
    this.terminalStyleLabel = new JLabel("终端配色: ");
    
    this.fontCombobox = new JComboBox<>(UiFunction.getAllFontName());
    this.fontSizeCombobox = new JComboBox((Object[])UiFunction.getAllFontSize());
    this.fontTypeComboBox = new JComboBox<>(UiFunction.getAllFontType());
    this.terminalStyleComboBox = new JComboBox<>(TerminalSettingsProvider.getTerminalStyles());
    this.saveButton = new JButton("保存配置");
    
    this.fontCombobox.setSelectedItem(TerminalSettingsProvider.getFontName());
    this.fontTypeComboBox.setSelectedItem(TerminalSettingsProvider.getFontType());
    this.fontSizeCombobox.setSelectedItem(String.valueOf(TerminalSettingsProvider.getFontSize()));
    this.terminalStyleComboBox.setSelectedItem(TerminalSettingsProvider.getTerminalStyle());
    
    this.jediTerminal = new JediTermWidget((SettingsProvider)new TerminalSettingsProvider());
    
    this.jediTerminal.getTerminal().writeCharacters("你好 hello ❤❤❤");
    this.jediTerminal.getTerminal().nextLine();




    
    JPanel topPanel = new JPanel();

    
    topPanel.add(this.fontLabel);
    topPanel.add(this.fontCombobox);
    topPanel.add(this.fontTypeLabel);
    topPanel.add(this.fontTypeComboBox);
    topPanel.add(this.sizeLabel);
    topPanel.add(this.fontSizeCombobox);
    topPanel.add(this.terminalStyleLabel);
    topPanel.add(this.terminalStyleComboBox);
    topPanel.add(this.saveButton);
    
    JSplitPane splitPane = new JSplitPane(0);
    
    splitPane.setTopComponent(topPanel);
    
    splitPane.setBottomComponent((Component)this.jediTerminal);
    
    ItemListener listener = e -> {
        this.jediTerminal = new JediTermWidget((SettingsProvider)new TerminalSettingsProvider(this.terminalStyleComboBox.getSelectedItem().toString())
            {
              public Font getTerminalFont() {
                return new Font(SuperTerminalSeting.this.fontCombobox.getSelectedItem().toString(), UiFunction.getFontType(SuperTerminalSeting.this.fontTypeComboBox.getSelectedItem().toString()), (int)getTerminalFontSize());
              }

              
              public float getTerminalFontSize() {
                return Integer.parseInt(SuperTerminalSeting.this.fontSizeCombobox.getSelectedItem().toString());
              }
            });
        
        try {
          this.jediTerminal.getTerminal().writeCharacters("你好 hello ❤❤❤");
          
          this.jediTerminal.setSize(1024, 1024);
        } catch (Exception e2) {
          Log.error(e2);
        } 
        
        splitPane.setBottomComponent((Component)this.jediTerminal);
      };
    this.fontCombobox.addItemListener(listener);
    this.fontTypeComboBox.addItemListener(listener);
    this.fontSizeCombobox.addItemListener(listener);
    this.terminalStyleComboBox.addItemListener(listener);
    
    add(splitPane);
    
    automaticBindClick.bindJButtonClick(this, this);
  }
  private JComboBox<String> fontCombobox; private JComboBox<Integer> fontSizeCombobox; private JComboBox<String> fontTypeComboBox; private JComboBox<String> terminalStyleComboBox; private JButton saveButton;
  private JediTermWidget jediTerminal;
  
  private void saveButtonClick(ActionEvent actionEvent) {
    String fontName = this.fontCombobox.getSelectedItem().toString();
    String fontType = this.fontTypeComboBox.getSelectedItem().toString();
    int fontSize = Integer.parseInt(this.fontSizeCombobox.getSelectedItem().toString());
    String terminalStyle = this.terminalStyleComboBox.getSelectedItem().toString();
    if (Db.updateSetingKV("Terminal-FontName", fontName) && Db.updateSetingKV("Terminal-FontType", fontType) && Db.updateSetingKV("Terminal-FontSize", String.valueOf(fontSize)) && Db.updateSetingKV("Terminal-FontStyle", terminalStyle)) {
      GOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
    } else {
      GOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
    } 
  }
  
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    
    frame.add(new SuperTerminalSeting());

    
    frame.setSize(1200, 1200);
    
    frame.setVisible(true);
    frame.setDefaultCloseOperation(3);
  }
}
