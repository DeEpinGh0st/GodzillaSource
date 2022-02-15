package com.formdev.flatlaf.demo;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

class NewDialog extends JDialog {
  private JPanel dialogPane;
  private JPanel contentPanel;
  private JLabel label1;
  private JTextField textField1;
  private JLabel label3;
  private JComboBox<String> comboBox2;
  private JLabel label2;
  private JComboBox<String> comboBox1;
  private JPanel buttonBar;
  private JButton okButton;
  private JButton cancelButton;
  private JMenuBar menuBar1;
  private JMenu menu1;
  private JMenuItem menuItem8;
  private JMenuItem menuItem7;
  private JMenuItem menuItem6;
  private JMenuItem menuItem5;
  private JMenuItem menuItem4;
  private JMenuItem menuItem3;
  private JMenuItem menuItem2;
  private JMenuItem menuItem1;
  private JMenu menu2;
  
  NewDialog(Window owner) {
    super(owner);
    initComponents();

    
    this.menuBar1.setVisible(false);
    
    getRootPane().setDefaultButton(this.okButton);

    
    ((JComponent)getContentPane()).registerKeyboardAction(e -> dispose(), 
        
        KeyStroke.getKeyStroke(27, 0, false), 1);
  }
  private JMenuItem menuItem18; private JMenuItem menuItem17; private JMenuItem menuItem16; private JMenuItem menuItem15; private JMenuItem menuItem14; private JMenuItem menuItem13; private JMenuItem menuItem12; private JMenuItem menuItem11; private JMenuItem menuItem10; private JMenuItem menuItem9; private JMenu menu3; private JMenuItem menuItem25; private JMenuItem menuItem26; private JMenuItem menuItem24; private JMenuItem menuItem23; private JMenuItem menuItem22; private JMenuItem menuItem21; private JMenuItem menuItem20; private JMenuItem menuItem19; private JPopupMenu popupMenu1; private JMenuItem cutMenuItem; private JMenuItem copyMenuItem; private JMenuItem pasteMenuItem;
  
  private void okActionPerformed() {
    dispose();
  }
  
  private void cancelActionPerformed() {
    dispose();
  }

  
  private void initComponents() {
    this.dialogPane = new JPanel();
    this.contentPanel = new JPanel();
    this.label1 = new JLabel();
    this.textField1 = new JTextField();
    this.label3 = new JLabel();
    this.comboBox2 = new JComboBox<>();
    this.label2 = new JLabel();
    this.comboBox1 = new JComboBox<>();
    this.buttonBar = new JPanel();
    this.okButton = new JButton();
    this.cancelButton = new JButton();
    this.menuBar1 = new JMenuBar();
    this.menu1 = new JMenu();
    this.menuItem8 = new JMenuItem();
    this.menuItem7 = new JMenuItem();
    this.menuItem6 = new JMenuItem();
    this.menuItem5 = new JMenuItem();
    this.menuItem4 = new JMenuItem();
    this.menuItem3 = new JMenuItem();
    this.menuItem2 = new JMenuItem();
    this.menuItem1 = new JMenuItem();
    this.menu2 = new JMenu();
    this.menuItem18 = new JMenuItem();
    this.menuItem17 = new JMenuItem();
    this.menuItem16 = new JMenuItem();
    this.menuItem15 = new JMenuItem();
    this.menuItem14 = new JMenuItem();
    this.menuItem13 = new JMenuItem();
    this.menuItem12 = new JMenuItem();
    this.menuItem11 = new JMenuItem();
    this.menuItem10 = new JMenuItem();
    this.menuItem9 = new JMenuItem();
    this.menu3 = new JMenu();
    this.menuItem25 = new JMenuItem();
    this.menuItem26 = new JMenuItem();
    this.menuItem24 = new JMenuItem();
    this.menuItem23 = new JMenuItem();
    this.menuItem22 = new JMenuItem();
    this.menuItem21 = new JMenuItem();
    this.menuItem20 = new JMenuItem();
    this.menuItem19 = new JMenuItem();
    this.popupMenu1 = new JPopupMenu();
    this.cutMenuItem = new JMenuItem();
    this.copyMenuItem = new JMenuItem();
    this.pasteMenuItem = new JMenuItem();

    
    setTitle("New");
    setDefaultCloseOperation(2);
    setModal(true);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());


    
    this.dialogPane.setLayout(new BorderLayout());


    
    this.contentPanel.setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[fill][grow,fill]", "[][][]"));









    
    this.label1.setText("Name:");
    this.contentPanel.add(this.label1, "cell 0 0");

    
    this.textField1.setComponentPopupMenu(this.popupMenu1);
    this.contentPanel.add(this.textField1, "cell 1 0");

    
    this.label3.setText("Package:");
    this.contentPanel.add(this.label3, "cell 0 1");

    
    this.comboBox2.setEditable(true);
    this.comboBox2.setModel(new DefaultComboBoxModel<>(new String[] { "com.myapp", "com.myapp.core", "com.myapp.ui", "com.myapp.util", "com.myapp.extras", "com.myapp.components", "com.myapp.dialogs", "com.myapp.windows" }));








    
    this.contentPanel.add(this.comboBox2, "cell 1 1");

    
    this.label2.setText("Type:");
    this.contentPanel.add(this.label2, "cell 0 2");

    
    this.comboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "Class", "Interface", "Package", "Annotation", "Enum", "Record", "Java Project", "Project", "Folder", "File" }));










    
    this.contentPanel.add(this.comboBox1, "cell 1 2");
    
    this.dialogPane.add(this.contentPanel, "Center");


    
    this.buttonBar.setLayout((LayoutManager)new MigLayout("insets dialog,alignx right", "[button,fill][button,fill]", null));







    
    this.okButton.setText("OK");
    this.okButton.addActionListener(e -> okActionPerformed());
    this.buttonBar.add(this.okButton, "cell 0 0");

    
    this.cancelButton.setText("Cancel");
    this.cancelButton.addActionListener(e -> cancelActionPerformed());
    this.buttonBar.add(this.cancelButton, "cell 1 0");
    
    this.dialogPane.add(this.buttonBar, "South");





    
    this.menu1.setText("text");

    
    this.menuItem8.setText("text");
    this.menu1.add(this.menuItem8);

    
    this.menuItem7.setText("text");
    this.menu1.add(this.menuItem7);

    
    this.menuItem6.setText("text");
    this.menu1.add(this.menuItem6);

    
    this.menuItem5.setText("text");
    this.menu1.add(this.menuItem5);

    
    this.menuItem4.setText("text");
    this.menu1.add(this.menuItem4);

    
    this.menuItem3.setText("text");
    this.menu1.add(this.menuItem3);

    
    this.menuItem2.setText("text");
    this.menu1.add(this.menuItem2);

    
    this.menuItem1.setText("text");
    this.menu1.add(this.menuItem1);
    
    this.menuBar1.add(this.menu1);


    
    this.menu2.setText("text");

    
    this.menuItem18.setText("text");
    this.menu2.add(this.menuItem18);

    
    this.menuItem17.setText("text");
    this.menu2.add(this.menuItem17);

    
    this.menuItem16.setText("text");
    this.menu2.add(this.menuItem16);

    
    this.menuItem15.setText("text");
    this.menu2.add(this.menuItem15);

    
    this.menuItem14.setText("text");
    this.menu2.add(this.menuItem14);

    
    this.menuItem13.setText("text");
    this.menu2.add(this.menuItem13);

    
    this.menuItem12.setText("text");
    this.menu2.add(this.menuItem12);

    
    this.menuItem11.setText("text");
    this.menu2.add(this.menuItem11);

    
    this.menuItem10.setText("text");
    this.menu2.add(this.menuItem10);

    
    this.menuItem9.setText("text");
    this.menu2.add(this.menuItem9);
    
    this.menuBar1.add(this.menu2);


    
    this.menu3.setText("text");

    
    this.menuItem25.setText("text");
    this.menu3.add(this.menuItem25);

    
    this.menuItem26.setText("text");
    this.menu3.add(this.menuItem26);

    
    this.menuItem24.setText("text");
    this.menu3.add(this.menuItem24);

    
    this.menuItem23.setText("text");
    this.menu3.add(this.menuItem23);

    
    this.menuItem22.setText("text");
    this.menu3.add(this.menuItem22);

    
    this.menuItem21.setText("text");
    this.menu3.add(this.menuItem21);

    
    this.menuItem20.setText("text");
    this.menu3.add(this.menuItem20);

    
    this.menuItem19.setText("text");
    this.menu3.add(this.menuItem19);
    
    this.menuBar1.add(this.menu3);
    
    this.dialogPane.add(this.menuBar1, "North");
    
    contentPane.add(this.dialogPane, "Center");
    pack();
    setLocationRelativeTo(getOwner());




    
    this.cutMenuItem.setText("Cut");
    this.cutMenuItem.setMnemonic('C');
    this.popupMenu1.add(this.cutMenuItem);

    
    this.copyMenuItem.setText("Copy");
    this.copyMenuItem.setMnemonic('O');
    this.popupMenu1.add(this.copyMenuItem);

    
    this.pasteMenuItem.setText("Paste");
    this.pasteMenuItem.setMnemonic('P');
    this.popupMenu1.add(this.pasteMenuItem);
  }
}
