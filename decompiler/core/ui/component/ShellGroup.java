package core.ui.component;

import core.Db;
import core.EasyI18N;
import core.ui.MainActivity;
import core.ui.component.dialog.GOptionPane;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;

public class ShellGroup
  extends DataTree {
  public ShellGroup() {
    this("", new DefaultMutableTreeNode(EasyI18N.getI18nString("分组")));
  } protected JPopupMenu childPopupMenu;
  public ShellGroup(String fileString, DefaultMutableTreeNode root_Node) {
    super(fileString, root_Node);
    this.childPopupMenu = new JPopupMenu();
    
    JMenuItem newGroupItem = new JMenuItem("新建组");
    newGroupItem.setActionCommand("newGroup");
    
    JMenuItem renameItem = new JMenuItem("移动/重命名");
    renameItem.setActionCommand("rename");
    
    JMenuItem copyPathItem = new JMenuItem("复制当前组路径");
    copyPathItem.setActionCommand("copyPath");
    
    JMenuItem refreshItem = new JMenuItem("刷新");
    refreshItem.setActionCommand("refresh");
    
    JMenuItem deleteCurrentGroupItem = new JMenuItem("删除当前组");
    deleteCurrentGroupItem.setActionCommand("deleteGroup");
    
    JMenuItem deleteCurrentGroupAndDeleteWebshellItem = new JMenuItem("删除当前组并删除所有成员");
    deleteCurrentGroupAndDeleteWebshellItem.setActionCommand("deleteCurrentGroupAndDeleteWebshell");
    
    JMenuItem deleteCurrentGroupDontDeleteWebshellItem = new JMenuItem("删除当前组但不删除所有成员");
    deleteCurrentGroupDontDeleteWebshellItem.setActionCommand("deleteCurrentGroupDontDeleteWebshell");
    
    this.childPopupMenu.add(newGroupItem);
    this.childPopupMenu.add(renameItem);
    this.childPopupMenu.add(copyPathItem);
    this.childPopupMenu.add(refreshItem);
    this.childPopupMenu.add(deleteCurrentGroupItem);
    this.childPopupMenu.add(deleteCurrentGroupAndDeleteWebshellItem);
    this.childPopupMenu.add(deleteCurrentGroupDontDeleteWebshellItem);
    
    setChildPopupMenu(this.childPopupMenu);
    setParentPopupMenu(this.childPopupMenu);
    
    automaticBindClick.bindMenuItemClick(this.childPopupMenu, null, this);
    
    setLeafIcon(new ImageIcon(getClass().getResource("/images/folder.png")));
    
    refreshMenuItemClick((ActionEvent)null);
    
    EasyI18N.installObject(this);
  }
  
  protected void newGroupMenuItemClick(ActionEvent e) {
    String inputString = GOptionPane.showInputDialog("请输入新组名", "newGroup");
    if (inputString != null && !"/".equals(inputString.trim())) {
      String selectedString = GetSelectFile();
      String newGroup = parseFile2(selectedString + "/" + inputString);
      if (Db.addGroup(newGroup) > 0) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "添加成功!");
        refreshMenuItemClick(e);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "添加失败 请检查组是否存在 控制台是否有报错!");
      } 
    } else {
      Log.error("用户未输入数据");
    } 
  }
  protected void renameMenuItemClick(ActionEvent e) {
    String inputString = GOptionPane.showInputDialog("请输入新组名", GetSelectFile());
    if (inputString != null && !"/".equals(inputString.trim())) {
      String oldGroup = GetSelectFile();
      String newGroup = parseFile2("/" + inputString);
      if (Db.renameGroup(oldGroup, newGroup) > 0) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "移动成功!");
        refreshMenuItemClick(e);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "移动失败 请检查新组是否存在 控制台是否有报错!");
      } 
    } else {
      Log.error("用户未输入数据");
    } 
  }
  protected void refreshMenuItemClick(ActionEvent e) {
    removeAll();
    AddNote("/");
    Db.getAllGroup().forEach(id -> AddNote(id.toString()));
  }
  protected void deleteGroupMenuItemClick(ActionEvent e) {
    String groupId = GetSelectFile();
    if (groupId != null && !"/".equals(groupId.trim())) {
      if (0 != GOptionPane.showConfirmDialog((Component)MainActivity.getMainActivityFrame(), String.format("确定删除组：%s 吗?", new Object[] { groupId, "警告", Integer.valueOf(0) }))) {
        return;
      }
      if (Db.removeGroup(groupId, "/") > 0) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "删除成功! 成员已移动到 / ");
        refreshMenuItemClick(e);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "删除失败 请检查组是否存在 控制台是否有报错!");
      } 
    } else {
      Log.error("group是空的");
    } 
  }
  protected void copyPathMenuItemClick(ActionEvent e) {
    String groupId = GetSelectFile();
    if (groupId != null && !"/".equals(groupId.trim())) {
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(groupId), null);
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "复制成功");
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "group是空的");
    } 
  }
  protected void deleteCurrentGroupAndDeleteWebshellMenuItemClick(ActionEvent e) {
    String groupId = GetSelectFile();
    if (groupId != null && !"/".equals(groupId.trim())) {
      if (0 != GOptionPane.showConfirmDialog((Component)MainActivity.getMainActivityFrame(), String.format(EasyI18N.getI18nString("确定删除组：%s 并删除所有组成员(包括子组)吗?"), new Object[] { groupId, "警告", Integer.valueOf(0) }))) {
        return;
      }
      Db.removeShellByGroup(groupId);
      if (Db.removeGroup(groupId, "/") > 0) {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "删除成功! 子组与成员已全部删除");
        refreshMenuItemClick(e);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this), "删除失败 请检查组是否存在 控制台是否有报错!");
      } 
    } else {
      Log.error("group是空的");
    } 
  }
  protected void deleteCurrentGroupDontDeleteWebshellMenuItemClick(ActionEvent e) {
    deleteGroupMenuItemClick(e);
  }
  public String getSelectedGroupName() {
    String groupId = GetSelectFile();
    return groupId;
  }
}
