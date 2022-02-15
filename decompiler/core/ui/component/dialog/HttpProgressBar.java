package core.ui.component.dialog;
import core.EasyI18N;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import util.Log;
import util.functions;

public class HttpProgressBar extends JFrame {
  private static final String CURRENT_VALUE_FORMAT = EasyI18N.getI18nString("已完成  %s Mb");
  private static final String MAX_VALUE_FORMAT = EasyI18N.getI18nString("共  %s Mb");
  
  private final JPanel panel;
  
  private final JLabel currentValueLabel;
  private final JLabel maxValueLabel;
  private final JProgressBar progressBar;
  private boolean isClose;
  
  public HttpProgressBar(String title, int MaxValue) {
    this.panel = new JPanel();
    this.currentValueLabel = new JLabel();
    this.maxValueLabel = new JLabel();
    this.progressBar = new JProgressBar(0, 0, MaxValue);
    
    this.panel.add(this.progressBar);
    this.panel.add(this.maxValueLabel);
    this.panel.add(this.currentValueLabel);
    
    this.maxValueLabel.setText(String.format(MAX_VALUE_FORMAT, new Object[] { String.format("%.4f", new Object[] { Float.valueOf(MaxValue / Float.valueOf(1048576.0F).floatValue()) }) }));
    this.currentValueLabel.setText(String.format(CURRENT_VALUE_FORMAT, new Object[] { Integer.valueOf(0) }));


    
    add(this.panel);
    
    setTitle(title);
    this.progressBar.setStringPainted(true);
    
    setDefaultCloseOperation(2);
    setLocationRelativeTo((Component)null);
    functions.setWindowSize(this, 430, 90);
    
    this.progressBar.updateUI();
    
    EasyI18N.installObject(this);
    
    setVisible(true);
  }
  public void setValue(int value) {
    this.progressBar.setValue(value);
    this.currentValueLabel.setText(String.format(CURRENT_VALUE_FORMAT, new Object[] { String.format("%.4f", new Object[] { Float.valueOf(this.progressBar.getValue() / Float.valueOf(1048576.0F).floatValue()) }) }));
    Log.log(this.maxValueLabel.getText() + "\t" + this.currentValueLabel.getText(), new Object[0]);
    if (this.progressBar.getMaximum() <= this.progressBar.getValue()) {
      close();
    }
  }
  
  public boolean isClose() {
    return this.isClose;
  }
  
  public void close() {
    this.isClose = true;
    dispose();
  }

  
  public void dispose() {
    this.isClose = true;
    super.dispose();
  }
}
