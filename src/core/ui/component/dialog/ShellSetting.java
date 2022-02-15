package core.ui.component.dialog;

import core.ui.MainActivity;
import java.awt.Frame;
import javax.swing.JDialog;

public class ShellSetting
  extends JDialog
{
  public ShellSetting(String id) {
    super((Frame)MainActivity.getFrame(), "Shell Setting", true);
    core.ui.component.frame.ShellSetting shellSetting = new core.ui.component.frame.ShellSetting(id, "/");
  }
}
