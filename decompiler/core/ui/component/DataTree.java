package core.ui.component;

import core.ui.component.listener.ActionDblClick;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;


public class DataTree
  extends JTree
{
  private RightClickEvent rightClickEvent;
  private DefaultMutableTreeNode rootNode = null;
  private ImageIcon leafIcon;
  
  public DataTree(String fileString, DefaultMutableTreeNode root_Node) {
    super(root_Node);
    this.rootNode = root_Node;
    initJTree();
  }
  private void initJTree() {
    this.rightClickEvent = new RightClickEvent(this);
    addMouseListener(this.rightClickEvent);
    
    getSelectionModel().setSelectionMode(1);
  }
  public void setActionDbclick(ActionDblClick actionDblClick) {
    this.rightClickEvent.setActionDblClick(actionDblClick);
  }
  public void setChildPopupMenu(JPopupMenu popupMenu) {
    this.rightClickEvent.setChildPopupMenu(popupMenu);
  }
  public void setParentPopupMenu(JPopupMenu popupMenu) {
    this.rightClickEvent.setParentPopupMenu(popupMenu);
  }
  public void deleteNote(String fileString) {
    DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
    String[] paths = parseFile(fileString);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)defaultMutableTreeNode.getPath()[0];
    DefaultMutableTreeNode lasTreeNode = null;
    for (int i = 0; i < paths.length; i++) {
      node = FindTreeNote(node, paths[i]);
      if (node == null)
        return; 
      if (i == paths.length - 2) {
        lasTreeNode = node;
      }
    } 
    if (lasTreeNode != null)
      lasTreeNode.remove(lasTreeNode); 
  }
  
  public void setLeafIcon(ImageIcon imageIcon) {
    this.leafIcon = imageIcon;
  }

  
  public void updateUI() {
    super.updateUI();
    if (this.leafIcon != null) {
      DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer)getCellRenderer();
      cellRenderer.setLeafIcon(cellRenderer.getClosedIcon());
    } 
  }



  
  public void removeAll() {
    super.removeAll();
    this.rootNode.removeAllChildren();
    updateUI();
  }
  public void MoveNoteName(String fileString, String rename) {
    DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
    String[] paths = parseFile(fileString);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)defaultMutableTreeNode.getPath()[0];
    for (int i = 0; i < paths.length; i++) {
      node = FindTreeNote(node, paths[i]);
      if (node == null) {
        return;
      }
    } 
    node.setUserObject(rename);
  }
  public String GetSelectFile() {
    TreePath paths = getSelectionPath();
    ArrayList<String> pathList = new ArrayList<>();
    TreePath lastTreePath = paths;
    DefaultMutableTreeNode lastNode = null;
    do {
      try {
        lastNode = (DefaultMutableTreeNode)lastTreePath.getLastPathComponent();
      } catch (Exception e) {
        
        return "";
      } 
      pathList.add((String)lastNode.getUserObject());
      lastTreePath = lastTreePath.getParentPath();
    } while (lastTreePath != null);


    
    pathList.remove(pathList.size() - 1);
    Collections.reverse(pathList);
    if (pathList.size() > 0) {
      return parseFile(pathList);
    }
    return "";
  }

  
  public void setSelectNote(String pathString) {
    if (pathString == null || pathString.trim().isEmpty()) {
      return;
    }
    DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
    String[] paths = parseFile(pathString);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)defaultMutableTreeNode.getPath()[0];
    DefaultMutableTreeNode lastTreeNode = node;
    
    for (int i = 0; i < paths.length; i++) {
      node = FindTreeNote(node, paths[i]);
      if (node != null) {
        lastTreeNode = node;
      } else {
        return;
      } 
    } 
    if (lastTreeNode != null) {
      setSelectionPath(new TreePath((Object[])lastTreeNode.getPath()));
    }
    updateUI();
  }

  
  public void AddNote(String pathString) {
    if (pathString == null || pathString.trim().isEmpty()) {
      return;
    }
    DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
    String[] paths = parseFile(pathString);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)defaultMutableTreeNode.getPath()[0];
    DefaultMutableTreeNode lastTreeNode = node;
    boolean findSate = true;
    for (int i = 0; i < paths.length; i++) {
      
      if (findSate) {
        node = FindTreeNote(node, paths[i]);
        if (node == null) {
          findSate = false;
          DefaultMutableTreeNode _note = new DefaultMutableTreeNode(paths[i]);
          lastTreeNode.add(_note);
          lastTreeNode = _note;
        } else {
          lastTreeNode = node;
        } 
      } else {
        DefaultMutableTreeNode _note = new DefaultMutableTreeNode(paths[i]);
        lastTreeNode.add(_note);
        lastTreeNode = _note;
      } 
    } 

    
    if (lastTreeNode != null) {
      expandPath(new TreePath((Object[])((DefaultMutableTreeNode)lastTreeNode.getParent()).getPath()));
    }
    
    updateUI();
  }
  protected String[] parseFile(String fileString) {
    fileString = replaceSpecial(fileString);
    String[] retStrings = null;
    if (fileString.charAt(0) == '/') {
      retStrings = fileString.split("/");
      if (retStrings.length == 0) {
        retStrings = new String[] { "/" };
      } else {
        retStrings[0] = "/";
      } 
    } else {
      retStrings = fileString.split("/");
    } 
    return retStrings;
  }
  protected String parseFile(List<String> pathList) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < pathList.size(); i++) {
      builder.append("/");
      builder.append(pathList.get(i));
    } 
    return replaceSpecial(builder.substring(1));
  }
  
  protected String parseFile2(String file) {
    return parseFile(new CopyOnWriteArrayList<>(parseFile(file)));
  }
  private String replaceSpecial(String string) {
    return string.replaceAll("\\\\+", "/").trim().replaceAll("/+", "/").trim();
  }
  private DefaultMutableTreeNode FindTreeNote(DefaultMutableTreeNode node, String noteString) {
    for (Enumeration<DefaultMutableTreeNode> e = node.children(); e.hasMoreElements(); ) {
      
      DefaultMutableTreeNode n = e.nextElement();
      if (n.getUserObject().equals(noteString)) {
        return n;
      }
    } 
    return null;
  }
  
  private class RightClickEvent
    extends MouseAdapter {
    private ActionDblClick actionDblClick;
    private JPopupMenu childPopupMenu;
    private JPopupMenu parentPopupMenu;
    private final DataTree jTree;
    
    public RightClickEvent(DataTree tree) {
      this.jTree = tree;
    }
    
    public ActionDblClick getActionDblClick() {
      return this.actionDblClick;
    }
    public void setActionDblClick(ActionDblClick actionDblClick) {
      this.actionDblClick = actionDblClick;
    }
    public void setChildPopupMenu(JPopupMenu popupMenu) {
      this.childPopupMenu = popupMenu;
    }
    
    public void setParentPopupMenu(JPopupMenu parentPopupMenu) {
      this.parentPopupMenu = parentPopupMenu;
    }

    
    public void mouseClicked(MouseEvent paramMouseEvent) {
      if (SwingUtilities.isRightMouseButton(paramMouseEvent)) {
        if ((this.childPopupMenu != null || this.parentPopupMenu != null) && 
          this.jTree.getSelectionPath() != null) {
          if (((DefaultMutableTreeNode)this.jTree.getLastSelectedPathComponent()).getChildCount() == 0 && this.childPopupMenu != null) {
            String selectedPath = this.jTree.GetSelectFile();
            if (selectedPath != null && !selectedPath.trim().isEmpty()) {
              this.childPopupMenu.show(this.jTree, paramMouseEvent.getX(), paramMouseEvent.getY());
            }
          } else if (this.parentPopupMenu != null) {
            String selectedPath = this.jTree.GetSelectFile();
            if (selectedPath != null && !selectedPath.trim().isEmpty()) {
              this.parentPopupMenu.show(this.jTree, paramMouseEvent.getX(), paramMouseEvent.getY());
            }
          }
        
        }
      } else if (paramMouseEvent.getClickCount() == 2 && 
        this.actionDblClick != null && !DataTree.this.GetSelectFile().trim().isEmpty()) {
        this.actionDblClick.dblClick(paramMouseEvent);
      } 
    }
  }
}
