package com.formdev.flatlaf.demo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractListModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import net.miginfocom.swing.MigLayout;

class DataComponentsPanel extends JPanel {
  private JList<String> list1;
  private JList<String> list2;
  private JTree tree1;
  private JTree tree2;
  private JTable table1;
  private JCheckBox showHorizontalLinesCheckBox;
  
  DataComponentsPanel() {
    initComponents();
  }
  private JCheckBox showVerticalLinesCheckBox; private JCheckBox intercellSpacingCheckBox; private JCheckBox redGridColorCheckBox; private JCheckBox rowSelectionCheckBox; private JCheckBox columnSelectionCheckBox; private JCheckBox dndCheckBox;
  private void dndChanged() {
    boolean dnd = this.dndCheckBox.isSelected();
    this.list1.setDragEnabled(dnd);
    this.list2.setDragEnabled(dnd);
    this.tree1.setDragEnabled(dnd);
    this.tree2.setDragEnabled(dnd);
    this.table1.setDragEnabled(dnd);
    
    DropMode dropMode = dnd ? DropMode.ON_OR_INSERT : DropMode.USE_SELECTION;
    this.list1.setDropMode(dropMode);
    this.tree1.setDropMode(dropMode);
    this.table1.setDropMode(dropMode);
    
    String key = "FlatLaf.oldTransferHandler";
    if (dnd) {
      this.list1.putClientProperty(key, this.list1.getTransferHandler());
      this.list1.setTransferHandler(new DummyTransferHandler());
      
      this.tree1.putClientProperty(key, this.tree1.getTransferHandler());
      this.tree1.setTransferHandler(new DummyTransferHandler());
      
      this.table1.putClientProperty(key, this.table1.getTransferHandler());
      this.table1.setTransferHandler(new DummyTransferHandler());
    } else {
      this.list1.setTransferHandler((TransferHandler)this.list1.getClientProperty(key));
      this.tree1.setTransferHandler((TransferHandler)this.tree1.getClientProperty(key));
      this.table1.setTransferHandler((TransferHandler)this.table1.getClientProperty(key));
    } 
  }
  
  private void rowSelectionChanged() {
    this.table1.setRowSelectionAllowed(this.rowSelectionCheckBox.isSelected());
  }
  
  private void columnSelectionChanged() {
    this.table1.setColumnSelectionAllowed(this.columnSelectionCheckBox.isSelected());
  }
  
  private void showHorizontalLinesChanged() {
    this.table1.setShowHorizontalLines(this.showHorizontalLinesCheckBox.isSelected());
  }
  
  private void showVerticalLinesChanged() {
    this.table1.setShowVerticalLines(this.showVerticalLinesCheckBox.isSelected());
  }
  
  private void intercellSpacingChanged() {
    this.table1.setIntercellSpacing(this.intercellSpacingCheckBox.isSelected() ? new Dimension(1, 1) : new Dimension());
  }
  
  private void redGridColorChanged() {
    this.table1.setGridColor(this.redGridColorCheckBox.isSelected() ? Color.red : UIManager.getColor("Table.gridColor"));
  }

  
  public void updateUI() {
    super.updateUI();
    
    EventQueue.invokeLater(() -> {
          showHorizontalLinesChanged();
          showVerticalLinesChanged();
          intercellSpacingChanged();
        });
  }


  
  private void initComponents() {
    JLabel listLabel = new JLabel();
    JScrollPane scrollPane1 = new JScrollPane();
    this.list1 = new JList<>();
    JScrollPane scrollPane2 = new JScrollPane();
    this.list2 = new JList<>();
    JLabel treeLabel = new JLabel();
    JScrollPane scrollPane3 = new JScrollPane();
    this.tree1 = new JTree();
    JScrollPane scrollPane4 = new JScrollPane();
    this.tree2 = new JTree();
    JLabel tableLabel = new JLabel();
    JScrollPane scrollPane5 = new JScrollPane();
    this.table1 = new JTable();
    JPanel tableOptionsPanel = new JPanel();
    this.showHorizontalLinesCheckBox = new JCheckBox();
    this.showVerticalLinesCheckBox = new JCheckBox();
    this.intercellSpacingCheckBox = new JCheckBox();
    this.redGridColorCheckBox = new JCheckBox();
    this.rowSelectionCheckBox = new JCheckBox();
    this.columnSelectionCheckBox = new JCheckBox();
    this.dndCheckBox = new JCheckBox();
    JPopupMenu popupMenu2 = new JPopupMenu();
    JMenuItem menuItem3 = new JMenuItem();
    JMenuItem menuItem4 = new JMenuItem();
    JMenuItem menuItem5 = new JMenuItem();
    JMenuItem menuItem6 = new JMenuItem();

    
    setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[][200,fill][200,fill][fill]", "[150,grow,sizegroup 1,fill][150,grow,sizegroup 1,fill][150,grow,sizegroup 1,fill]"));











    
    listLabel.setText("JList:");
    add(listLabel, "cell 0 0,aligny top,growy 0");




    
    this.list1.setModel(new AbstractListModel<String>() {
          String[] values = new String[] { "item 1", "item 2", "item 3", "item 4", "item 5", "item 6", "item 7", "item 8", "item 9", "item 10", "item 11", "item 12", "item 13", "item 14", "item 15" };















          
          public int getSize() {
            return this.values.length;
          }
          public String getElementAt(int i) { return this.values[i]; }
        });
    this.list1.setComponentPopupMenu(popupMenu2);
    scrollPane1.setViewportView(this.list1);
    
    add(scrollPane1, "cell 1 0");




    
    this.list2.setModel(new AbstractListModel<String>() {
          String[] values = new String[] { "item 1", "item 2", "item 3", "item 4", "item 5", "item 6", "item 7", "item 8", "item 9", "item 10", "item 11", "item 12", "item 13", "item 14", "item 15" };















          
          public int getSize() {
            return this.values.length;
          }
          public String getElementAt(int i) { return this.values[i]; }
        });
    this.list2.setEnabled(false);
    scrollPane2.setViewportView(this.list2);
    
    add(scrollPane2, "cell 2 0");

    
    treeLabel.setText("JTree:");
    add(treeLabel, "cell 0 1,aligny top,growy 0");




    
    this.tree1.setShowsRootHandles(true);
    this.tree1.setEditable(true);
    this.tree1.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("JTree")
          {
          
          }));





















    
    this.tree1.setComponentPopupMenu(popupMenu2);
    scrollPane3.setViewportView(this.tree1);
    
    add(scrollPane3, "cell 1 1");




    
    this.tree2.setEnabled(false);
    scrollPane4.setViewportView(this.tree2);
    
    add(scrollPane4, "cell 2 1");

    
    tableLabel.setText("JTable:");
    add(tableLabel, "cell 0 2,aligny top,growy 0");




    
    this.table1.setModel(new DefaultTableModel(new Object[][] { { "item 1", "item 1b", "January", "July", 
              
              Integer.valueOf(123), null }, , { "item 2", "item 2b", "February", "August", 
              Integer.valueOf(456), Boolean.valueOf(true) }, , { "item 3", null, "March", null, null, null }, , { "item 4", null, "April", null, null, null }, , { "item 5", null, "May", null, null, null }, , { "item 6", null, "June", null, null, null }, , { "item 7", null, "July", null, null, null }, , { "item 8", null, "August", null, null, null }, , { "item 9", null, "September", null, null, null }, , { "item 10", null, "October", null, null, null }, , { "item 11", null, "November", null, null, null }, , { "item 12", null, "December", null, null, null },  }, (Object[])new String[] { "Not editable", "Text", "Combo", "Combo Editable", "Integer", "Boolean" })
        {













          
          Class<?>[] columnTypes = new Class[] { Object.class, Object.class, String.class, String.class, Integer.class, Boolean.class };

          
          boolean[] columnEditable = new boolean[] { false, true, true, true, true, true };


          
          public Class<?> getColumnClass(int columnIndex) {
            return this.columnTypes[columnIndex];
          }
          
          public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.columnEditable[columnIndex];
          }
        });
    
    TableColumnModel cm = this.table1.getColumnModel();
    cm.getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox(new DefaultComboBoxModel((Object[])new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }))));













    
    cm.getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox(new DefaultComboBoxModel((Object[])new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }))));














    
    this.table1.setAutoCreateRowSorter(true);
    this.table1.setComponentPopupMenu(popupMenu2);
    scrollPane5.setViewportView(this.table1);
    
    add(scrollPane5, "cell 1 2 2 1,width 300");


    
    tableOptionsPanel.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[]", "[]0[]0[]0[]0[]0[]0[]0"));












    
    this.showHorizontalLinesCheckBox.setText("show horizontal lines");
    this.showHorizontalLinesCheckBox.addActionListener(e -> showHorizontalLinesChanged());
    tableOptionsPanel.add(this.showHorizontalLinesCheckBox, "cell 0 0");

    
    this.showVerticalLinesCheckBox.setText("show vertical lines");
    this.showVerticalLinesCheckBox.addActionListener(e -> showVerticalLinesChanged());
    tableOptionsPanel.add(this.showVerticalLinesCheckBox, "cell 0 1");

    
    this.intercellSpacingCheckBox.setText("intercell spacing");
    this.intercellSpacingCheckBox.addActionListener(e -> intercellSpacingChanged());
    tableOptionsPanel.add(this.intercellSpacingCheckBox, "cell 0 2");

    
    this.redGridColorCheckBox.setText("red grid color");
    this.redGridColorCheckBox.addActionListener(e -> redGridColorChanged());
    tableOptionsPanel.add(this.redGridColorCheckBox, "cell 0 3");

    
    this.rowSelectionCheckBox.setText("row selection");
    this.rowSelectionCheckBox.setSelected(true);
    this.rowSelectionCheckBox.addActionListener(e -> rowSelectionChanged());
    tableOptionsPanel.add(this.rowSelectionCheckBox, "cell 0 4");

    
    this.columnSelectionCheckBox.setText("column selection");
    this.columnSelectionCheckBox.addActionListener(e -> columnSelectionChanged());
    tableOptionsPanel.add(this.columnSelectionCheckBox, "cell 0 5");

    
    this.dndCheckBox.setText("enable drag and drop");
    this.dndCheckBox.setMnemonic('D');
    this.dndCheckBox.addActionListener(e -> dndChanged());
    tableOptionsPanel.add(this.dndCheckBox, "cell 0 6");
    
    add(tableOptionsPanel, "cell 3 2");




    
    menuItem3.setText("Some Action");
    popupMenu2.add(menuItem3);

    
    menuItem4.setText("More Action");
    popupMenu2.add(menuItem4);
    popupMenu2.addSeparator();

    
    menuItem5.setText("No Action");
    popupMenu2.add(menuItem5);

    
    menuItem6.setText("Noop Action");
    popupMenu2.add(menuItem6);


    
    ((JComboBox)((DefaultCellEditor)this.table1.getColumnModel().getColumn(3).getCellEditor()).getComponent()).setEditable(true);
  }








  
  private static class DummyTransferHandler
    extends TransferHandler
  {
    private DummyTransferHandler() {}








    
    protected Transferable createTransferable(JComponent c) {
      if (c instanceof JList && ((JList)c).isSelectionEmpty())
        return null; 
      if (c instanceof JTree && ((JTree)c).isSelectionEmpty())
        return null; 
      if (c instanceof JTable && ((JTable)c).getSelectionModel().isSelectionEmpty()) {
        return null;
      }
      return new StringSelection("dummy");
    }

    
    public int getSourceActions(JComponent c) {
      return 1;
    }

    
    public boolean canImport(TransferHandler.TransferSupport support) {
      return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    
    public boolean importData(TransferHandler.TransferSupport support) {
      String message = String.valueOf(support.getDropLocation());
      SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Drop", -1));

      
      return false;
    }
  }
}
