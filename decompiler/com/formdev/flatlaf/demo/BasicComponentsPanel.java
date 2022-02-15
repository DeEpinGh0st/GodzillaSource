package com.formdev.flatlaf.demo;

import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;
import net.miginfocom.swing.MigLayout;


class BasicComponentsPanel
  extends JPanel
{
  BasicComponentsPanel() {
    initComponents();
  }

  
  private void initComponents() {
    JLabel labelLabel = new JLabel();
    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JLabel buttonLabel = new JLabel();
    JButton button1 = new JButton();
    JButton button2 = new JButton();
    JButton button5 = new JButton();
    JButton button6 = new JButton();
    JButton button3 = new JButton();
    JButton button4 = new JButton();
    JButton button13 = new JButton();
    JButton button14 = new JButton();
    JButton button15 = new JButton();
    JButton button16 = new JButton();
    JLabel checkBoxLabel = new JLabel();
    JCheckBox checkBox1 = new JCheckBox();
    JCheckBox checkBox2 = new JCheckBox();
    JCheckBox checkBox3 = new JCheckBox();
    JCheckBox checkBox4 = new JCheckBox();
    JLabel radioButtonLabel = new JLabel();
    JRadioButton radioButton1 = new JRadioButton();
    JRadioButton radioButton2 = new JRadioButton();
    JRadioButton radioButton3 = new JRadioButton();
    JRadioButton radioButton4 = new JRadioButton();
    JLabel comboBoxLabel = new JLabel();
    JComboBox<String> comboBox1 = new JComboBox<>();
    JComboBox<String> comboBox2 = new JComboBox<>();
    JComboBox<String> comboBox3 = new JComboBox<>();
    JComboBox<String> comboBox4 = new JComboBox<>();
    JComboBox<String> comboBox5 = new JComboBox<>();
    JLabel spinnerLabel = new JLabel();
    JSpinner spinner1 = new JSpinner();
    JSpinner spinner2 = new JSpinner();
    JComboBox<String> comboBox6 = new JComboBox<>();
    JLabel textFieldLabel = new JLabel();
    JTextField textField1 = new JTextField();
    JTextField textField2 = new JTextField();
    JTextField textField3 = new JTextField();
    JTextField textField4 = new JTextField();
    JTextField textField6 = new JTextField();
    JLabel formattedTextFieldLabel = new JLabel();
    JFormattedTextField formattedTextField1 = new JFormattedTextField();
    JFormattedTextField formattedTextField2 = new JFormattedTextField();
    JFormattedTextField formattedTextField3 = new JFormattedTextField();
    JFormattedTextField formattedTextField4 = new JFormattedTextField();
    JFormattedTextField formattedTextField5 = new JFormattedTextField();
    JLabel passwordFieldLabel = new JLabel();
    JPasswordField passwordField1 = new JPasswordField();
    JPasswordField passwordField2 = new JPasswordField();
    JPasswordField passwordField3 = new JPasswordField();
    JPasswordField passwordField4 = new JPasswordField();
    JPasswordField passwordField5 = new JPasswordField();
    JLabel textAreaLabel = new JLabel();
    JScrollPane scrollPane1 = new JScrollPane();
    JTextArea textArea1 = new JTextArea();
    JScrollPane scrollPane2 = new JScrollPane();
    JTextArea textArea2 = new JTextArea();
    JScrollPane scrollPane3 = new JScrollPane();
    JTextArea textArea3 = new JTextArea();
    JScrollPane scrollPane4 = new JScrollPane();
    JTextArea textArea4 = new JTextArea();
    JTextArea textArea5 = new JTextArea();
    JLabel editorPaneLabel = new JLabel();
    JScrollPane scrollPane5 = new JScrollPane();
    JEditorPane editorPane1 = new JEditorPane();
    JScrollPane scrollPane6 = new JScrollPane();
    JEditorPane editorPane2 = new JEditorPane();
    JScrollPane scrollPane7 = new JScrollPane();
    JEditorPane editorPane3 = new JEditorPane();
    JScrollPane scrollPane8 = new JScrollPane();
    JEditorPane editorPane4 = new JEditorPane();
    JEditorPane editorPane5 = new JEditorPane();
    JLabel textPaneLabel = new JLabel();
    JScrollPane scrollPane9 = new JScrollPane();
    JTextPane textPane1 = new JTextPane();
    JScrollPane scrollPane10 = new JScrollPane();
    JTextPane textPane2 = new JTextPane();
    JScrollPane scrollPane11 = new JScrollPane();
    JTextPane textPane3 = new JTextPane();
    JScrollPane scrollPane12 = new JScrollPane();
    JTextPane textPane4 = new JTextPane();
    JTextPane textPane5 = new JTextPane();
    JLabel errorHintsLabel = new JLabel();
    JTextField errorHintsTextField = new JTextField();
    JComboBox<String> errorHintsComboBox = new JComboBox<>();
    JSpinner errorHintsSpinner = new JSpinner();
    JLabel warningHintsLabel = new JLabel();
    JTextField warningHintsTextField = new JTextField();
    JComboBox<String> warningHintsComboBox = new JComboBox<>();
    JSpinner warningHintsSpinner = new JSpinner();
    JPopupMenu popupMenu1 = new JPopupMenu();
    JMenuItem cutMenuItem = new JMenuItem();
    JMenuItem copyMenuItem = new JMenuItem();
    JMenuItem pasteMenuItem = new JMenuItem();

    
    setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[sizegroup 1][sizegroup 1][sizegroup 1][sizegroup 1][][]", "[][][][][][][][][][][][]para[][]"));
























    
    labelLabel.setText("JLabel:");
    add(labelLabel, "cell 0 0");

    
    label1.setText("Enabled");
    label1.setDisplayedMnemonic('E');
    add(label1, "cell 1 0");

    
    label2.setText("Disabled");
    label2.setDisplayedMnemonic('D');
    label2.setEnabled(false);
    add(label2, "cell 2 0");

    
    buttonLabel.setText("JButton:");
    add(buttonLabel, "cell 0 1");

    
    button1.setText("Enabled");
    button1.setDisplayedMnemonicIndex(0);
    add(button1, "cell 1 1");

    
    button2.setText("Disabled");
    button2.setDisplayedMnemonicIndex(0);
    button2.setEnabled(false);
    add(button2, "cell 2 1");

    
    button5.setText("Square");
    button5.putClientProperty("JButton.buttonType", "square");
    add(button5, "cell 3 1");

    
    button6.setText("Round");
    button6.putClientProperty("JButton.buttonType", "roundRect");
    add(button6, "cell 4 1");

    
    button3.setText("Help");
    button3.putClientProperty("JButton.buttonType", "help");
    add(button3, "cell 4 1");

    
    button4.setText("Help");
    button4.putClientProperty("JButton.buttonType", "help");
    button4.setEnabled(false);
    add(button4, "cell 4 1");

    
    button13.setIcon(UIManager.getIcon("Tree.closedIcon"));
    add(button13, "cell 5 1");

    
    button14.setText("...");
    add(button14, "cell 5 1");

    
    button15.setText("…");
    add(button15, "cell 5 1");

    
    button16.setText("#");
    add(button16, "cell 5 1");

    
    checkBoxLabel.setText("JCheckBox");
    add(checkBoxLabel, "cell 0 2");

    
    checkBox1.setText("Enabled");
    checkBox1.setMnemonic('A');
    add(checkBox1, "cell 1 2");

    
    checkBox2.setText("Disabled");
    checkBox2.setEnabled(false);
    checkBox2.setMnemonic('D');
    add(checkBox2, "cell 2 2");

    
    checkBox3.setText("Selected");
    checkBox3.setSelected(true);
    add(checkBox3, "cell 3 2");

    
    checkBox4.setText("Selected disabled");
    checkBox4.setSelected(true);
    checkBox4.setEnabled(false);
    add(checkBox4, "cell 4 2");

    
    radioButtonLabel.setText("JRadioButton:");
    add(radioButtonLabel, "cell 0 3");

    
    radioButton1.setText("Enabled");
    radioButton1.setMnemonic('N');
    add(radioButton1, "cell 1 3");

    
    radioButton2.setText("Disabled");
    radioButton2.setEnabled(false);
    radioButton2.setMnemonic('S');
    add(radioButton2, "cell 2 3");

    
    radioButton3.setText("Selected");
    radioButton3.setSelected(true);
    add(radioButton3, "cell 3 3");

    
    radioButton4.setText("Selected disabled");
    radioButton4.setSelected(true);
    radioButton4.setEnabled(false);
    add(radioButton4, "cell 4 3");

    
    comboBoxLabel.setText("JComboBox:");
    comboBoxLabel.setDisplayedMnemonic('C');
    comboBoxLabel.setLabelFor(comboBox1);
    add(comboBoxLabel, "cell 0 4");

    
    comboBox1.setEditable(true);
    comboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "Editable", "a", "bb", "ccc" }));




    
    add(comboBox1, "cell 1 4,growx");

    
    comboBox2.setEditable(true);
    comboBox2.setEnabled(false);
    comboBox2.setModel(new DefaultComboBoxModel<>(new String[] { "Disabled", "a", "bb", "ccc" }));




    
    add(comboBox2, "cell 2 4,growx");

    
    comboBox3.setModel(new DefaultComboBoxModel<>(new String[] { "Not editable", "a", "bb", "ccc" }));




    
    add(comboBox3, "cell 3 4,growx");

    
    comboBox4.setModel(new DefaultComboBoxModel<>(new String[] { "Not editable disabled", "a", "bb", "ccc" }));




    
    comboBox4.setEnabled(false);
    add(comboBox4, "cell 4 4,growx");

    
    comboBox5.setModel(new DefaultComboBoxModel<>(new String[] { "Wide popup if text is longer", "aa", "bbb", "cccc" }));




    
    add(comboBox5, "cell 5 4,growx,wmax 100");

    
    spinnerLabel.setText("JSpinner:");
    spinnerLabel.setLabelFor(spinner1);
    spinnerLabel.setDisplayedMnemonic('S');
    add(spinnerLabel, "cell 0 5");
    add(spinner1, "cell 1 5,growx");

    
    spinner2.setEnabled(false);
    add(spinner2, "cell 2 5,growx");

    
    comboBox6.setEditable(true);
    comboBox6.putClientProperty("JTextField.placeholderText", "Placeholder");
    add(comboBox6, "cell 5 5,growx");

    
    textFieldLabel.setText("JTextField:");
    textFieldLabel.setDisplayedMnemonic('T');
    textFieldLabel.setLabelFor(textField1);
    add(textFieldLabel, "cell 0 6");

    
    textField1.setText("Editable");
    textField1.setComponentPopupMenu(popupMenu1);
    add(textField1, "cell 1 6,growx");

    
    textField2.setText("Disabled");
    textField2.setEnabled(false);
    add(textField2, "cell 2 6,growx");

    
    textField3.setText("Not editable");
    textField3.setEditable(false);
    add(textField3, "cell 3 6,growx");

    
    textField4.setText("Not editable disabled");
    textField4.setEnabled(false);
    textField4.setEditable(false);
    add(textField4, "cell 4 6,growx");

    
    textField6.putClientProperty("JTextField.placeholderText", "Placeholder");
    add(textField6, "cell 5 6,growx");

    
    formattedTextFieldLabel.setText("JFormattedTextField:");
    formattedTextFieldLabel.setLabelFor(formattedTextField1);
    formattedTextFieldLabel.setDisplayedMnemonic('O');
    add(formattedTextFieldLabel, "cell 0 7");

    
    formattedTextField1.setText("Editable");
    formattedTextField1.setComponentPopupMenu(popupMenu1);
    add(formattedTextField1, "cell 1 7,growx");

    
    formattedTextField2.setText("Disabled");
    formattedTextField2.setEnabled(false);
    add(formattedTextField2, "cell 2 7,growx");

    
    formattedTextField3.setText("Not editable");
    formattedTextField3.setEditable(false);
    add(formattedTextField3, "cell 3 7,growx");

    
    formattedTextField4.setText("Not editable disabled");
    formattedTextField4.setEnabled(false);
    formattedTextField4.setEditable(false);
    add(formattedTextField4, "cell 4 7,growx");

    
    formattedTextField5.putClientProperty("JTextField.placeholderText", "Placeholder");
    add(formattedTextField5, "cell 5 7,growx");

    
    passwordFieldLabel.setText("JPasswordField:");
    add(passwordFieldLabel, "cell 0 8");

    
    passwordField1.setText("Editable");
    add(passwordField1, "cell 1 8,growx");

    
    passwordField2.setText("Disabled");
    passwordField2.setEnabled(false);
    add(passwordField2, "cell 2 8,growx");

    
    passwordField3.setText("Not editable");
    passwordField3.setEditable(false);
    add(passwordField3, "cell 3 8,growx");

    
    passwordField4.setText("Not editable disabled");
    passwordField4.setEnabled(false);
    passwordField4.setEditable(false);
    add(passwordField4, "cell 4 8,growx");

    
    passwordField5.putClientProperty("JTextField.placeholderText", "Placeholder");
    add(passwordField5, "cell 5 8,growx");

    
    textAreaLabel.setText("JTextArea:");
    add(textAreaLabel, "cell 0 9");


    
    scrollPane1.setVerticalScrollBarPolicy(21);
    scrollPane1.setHorizontalScrollBarPolicy(31);

    
    textArea1.setText("Editable");
    textArea1.setRows(2);
    scrollPane1.setViewportView(textArea1);
    
    add(scrollPane1, "cell 1 9,growx");


    
    scrollPane2.setVerticalScrollBarPolicy(21);
    scrollPane2.setHorizontalScrollBarPolicy(31);

    
    textArea2.setText("Disabled");
    textArea2.setRows(2);
    textArea2.setEnabled(false);
    scrollPane2.setViewportView(textArea2);
    
    add(scrollPane2, "cell 2 9,growx");


    
    scrollPane3.setVerticalScrollBarPolicy(21);
    scrollPane3.setHorizontalScrollBarPolicy(31);

    
    textArea3.setText("Not editable");
    textArea3.setRows(2);
    textArea3.setEditable(false);
    scrollPane3.setViewportView(textArea3);
    
    add(scrollPane3, "cell 3 9,growx");


    
    scrollPane4.setVerticalScrollBarPolicy(21);
    scrollPane4.setHorizontalScrollBarPolicy(31);

    
    textArea4.setText("Not editable disabled");
    textArea4.setRows(2);
    textArea4.setEditable(false);
    textArea4.setEnabled(false);
    scrollPane4.setViewportView(textArea4);
    
    add(scrollPane4, "cell 4 9,growx");

    
    textArea5.setRows(2);
    textArea5.setText("No scroll pane");
    add(textArea5, "cell 5 9,growx");

    
    editorPaneLabel.setText("JEditorPane");
    add(editorPaneLabel, "cell 0 10");


    
    scrollPane5.setVerticalScrollBarPolicy(21);
    scrollPane5.setHorizontalScrollBarPolicy(31);

    
    editorPane1.setText("Editable");
    scrollPane5.setViewportView(editorPane1);
    
    add(scrollPane5, "cell 1 10,growx");


    
    scrollPane6.setVerticalScrollBarPolicy(21);
    scrollPane6.setHorizontalScrollBarPolicy(31);

    
    editorPane2.setText("Disabled");
    editorPane2.setEnabled(false);
    scrollPane6.setViewportView(editorPane2);
    
    add(scrollPane6, "cell 2 10,growx");


    
    scrollPane7.setVerticalScrollBarPolicy(21);
    scrollPane7.setHorizontalScrollBarPolicy(31);

    
    editorPane3.setText("Not editable");
    editorPane3.setEditable(false);
    scrollPane7.setViewportView(editorPane3);
    
    add(scrollPane7, "cell 3 10,growx");


    
    scrollPane8.setVerticalScrollBarPolicy(21);
    scrollPane8.setHorizontalScrollBarPolicy(31);

    
    editorPane4.setText("Not editable disabled");
    editorPane4.setEditable(false);
    editorPane4.setEnabled(false);
    scrollPane8.setViewportView(editorPane4);
    
    add(scrollPane8, "cell 4 10,growx");

    
    editorPane5.setText("No scroll pane");
    add(editorPane5, "cell 5 10,growx");

    
    textPaneLabel.setText("JTextPane:");
    add(textPaneLabel, "cell 0 11");


    
    scrollPane9.setVerticalScrollBarPolicy(21);
    scrollPane9.setHorizontalScrollBarPolicy(31);

    
    textPane1.setText("Editable");
    scrollPane9.setViewportView(textPane1);
    
    add(scrollPane9, "cell 1 11,growx");


    
    scrollPane10.setVerticalScrollBarPolicy(21);
    scrollPane10.setHorizontalScrollBarPolicy(31);

    
    textPane2.setText("Disabled");
    textPane2.setEnabled(false);
    scrollPane10.setViewportView(textPane2);
    
    add(scrollPane10, "cell 2 11,growx");


    
    scrollPane11.setVerticalScrollBarPolicy(21);
    scrollPane11.setHorizontalScrollBarPolicy(31);

    
    textPane3.setText("Not editable");
    textPane3.setEditable(false);
    scrollPane11.setViewportView(textPane3);
    
    add(scrollPane11, "cell 3 11,growx");


    
    scrollPane12.setVerticalScrollBarPolicy(21);
    scrollPane12.setHorizontalScrollBarPolicy(31);

    
    textPane4.setText("Not editable disabled");
    textPane4.setEditable(false);
    textPane4.setEnabled(false);
    scrollPane12.setViewportView(textPane4);
    
    add(scrollPane12, "cell 4 11,growx");

    
    textPane5.setText("No scroll pane");
    add(textPane5, "cell 5 11,growx");

    
    errorHintsLabel.setText("Error hints:");
    add(errorHintsLabel, "cell 0 12");

    
    errorHintsTextField.putClientProperty("JComponent.outline", "error");
    add(errorHintsTextField, "cell 1 12,growx");

    
    errorHintsComboBox.putClientProperty("JComponent.outline", "error");
    errorHintsComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Editable" }));

    
    errorHintsComboBox.setEditable(true);
    add(errorHintsComboBox, "cell 2 12,growx");

    
    errorHintsSpinner.putClientProperty("JComponent.outline", "error");
    add(errorHintsSpinner, "cell 3 12,growx");

    
    warningHintsLabel.setText("Warning hints:");
    add(warningHintsLabel, "cell 0 13");

    
    warningHintsTextField.putClientProperty("JComponent.outline", "warning");
    add(warningHintsTextField, "cell 1 13,growx");

    
    warningHintsComboBox.putClientProperty("JComponent.outline", "warning");
    warningHintsComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Not editable" }));

    
    add(warningHintsComboBox, "cell 2 13,growx");

    
    warningHintsSpinner.putClientProperty("JComponent.outline", "warning");
    add(warningHintsSpinner, "cell 3 13,growx");




    
    cutMenuItem.setText("Cut");
    cutMenuItem.setMnemonic('C');
    popupMenu1.add(cutMenuItem);

    
    copyMenuItem.setText("Copy");
    copyMenuItem.setMnemonic('O');
    popupMenu1.add(copyMenuItem);

    
    pasteMenuItem.setText("Paste");
    pasteMenuItem.setMnemonic('P');
    popupMenu1.add(pasteMenuItem);


    
    cutMenuItem.addActionListener(new DefaultEditorKit.CutAction());
    copyMenuItem.addActionListener(new DefaultEditorKit.CopyAction());
    pasteMenuItem.addActionListener(new DefaultEditorKit.PasteAction());
    
    if (FlatLafDemo.screenshotsMode) {
      Component[] components = { button13, button14, button15, button16, comboBox5, comboBox6, textField6, passwordField5, formattedTextFieldLabel, formattedTextField1, formattedTextField2, formattedTextField3, formattedTextField4, formattedTextField5, textAreaLabel, scrollPane1, scrollPane2, scrollPane3, scrollPane4, textArea5, editorPaneLabel, scrollPane5, scrollPane6, scrollPane7, scrollPane8, editorPane5, textPaneLabel, scrollPane9, scrollPane10, scrollPane11, scrollPane12, textPane5, errorHintsLabel, errorHintsTextField, errorHintsComboBox, errorHintsSpinner, warningHintsLabel, warningHintsTextField, warningHintsComboBox, warningHintsSpinner };











      
      for (Component c : components) {
        c.setVisible(false);
      }
      
      Component[] formattedTextFields = { formattedTextFieldLabel, formattedTextField1, formattedTextField2, formattedTextField3, formattedTextField4 };
      Component[] passwordFields = { passwordFieldLabel, passwordField1, passwordField2, passwordField3, passwordField4 };
      MigLayout layout = (MigLayout)getLayout();
      for (int i = 0; i < passwordFields.length; i++) {
        Object cons = layout.getComponentConstraints(formattedTextFields[i]);
        layout.setComponentConstraints(passwordFields[i], cons);
      } 
    } 
  }
}
