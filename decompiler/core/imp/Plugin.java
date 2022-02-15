package core.imp;

import core.shell.ShellEntity;
import javax.swing.JPanel;

public interface Plugin {
  void init(ShellEntity paramShellEntity);
  
  JPanel getView();
}
