package com.formdev.flatlaf.demo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

class MoreComponentsPanel extends JPanel {
  private JProgressBar progressBar3;
  private JProgressBar progressBar4;
  private JSlider slider3;
  
  MoreComponentsPanel() {
    initComponents();
  }
  private JProgressBar progressBar1; private JProgressBar progressBar2; private JCheckBox indeterminateCheckBox;
  private void changeProgress() {
    int value = this.slider3.getValue();
    this.progressBar1.setValue(value);
    this.progressBar2.setValue(value);
    this.progressBar3.setValue(value);
    this.progressBar4.setValue(value);
  }
  
  private void indeterminateCheckBoxActionPerformed() {
    boolean indeterminate = this.indeterminateCheckBox.isSelected();
    this.progressBar1.setIndeterminate(indeterminate);
    this.progressBar2.setIndeterminate(indeterminate);
    this.progressBar3.setIndeterminate(indeterminate);
    this.progressBar4.setIndeterminate(indeterminate);
  }

  
  private void initComponents() {
    JLabel scrollPaneLabel = new JLabel();
    JScrollPane scrollPane13 = new JScrollPane();
    JPanel panel1 = new JPanel();
    JScrollBar scrollBar2 = new JScrollBar();
    JScrollBar scrollBar3 = new JScrollBar();
    JScrollBar scrollBar7 = new JScrollBar();
    JScrollBar scrollBar8 = new JScrollBar();
    JSeparator separator2 = new JSeparator();
    JSlider slider2 = new JSlider();
    JSlider slider4 = new JSlider();
    this.progressBar3 = new JProgressBar();
    this.progressBar4 = new JProgressBar();
    JToolBar toolBar2 = new JToolBar();
    JButton button9 = new JButton();
    JButton button10 = new JButton();
    JButton button11 = new JButton();
    JToggleButton toggleButton7 = new JToggleButton();
    JPanel panel2 = new JPanel();
    JLabel scrollBarLabel = new JLabel();
    JScrollBar scrollBar1 = new JScrollBar();
    JScrollBar scrollBar4 = new JScrollBar();
    JPanel panel3 = new JPanel();
    JLabel label4 = new JLabel();
    JLabel label3 = new JLabel();
    JScrollPane scrollPane15 = new JScrollPane();
    JEditorPane editorPane6 = new JEditorPane();
    JScrollPane scrollPane16 = new JScrollPane();
    JTextPane textPane6 = new JTextPane();
    JScrollBar scrollBar5 = new JScrollBar();
    JScrollBar scrollBar6 = new JScrollBar();
    JLabel separatorLabel = new JLabel();
    JSeparator separator1 = new JSeparator();
    JLabel sliderLabel = new JLabel();
    JSlider slider1 = new JSlider();
    JSlider slider6 = new JSlider();
    this.slider3 = new JSlider();
    JSlider slider5 = new JSlider();
    JLabel progressBarLabel = new JLabel();
    this.progressBar1 = new JProgressBar();
    this.progressBar2 = new JProgressBar();
    this.indeterminateCheckBox = new JCheckBox();
    JLabel toolTipLabel = new JLabel();
    JToolTip toolTip1 = new JToolTip();
    JToolTip toolTip2 = new JToolTip();
    JLabel toolBarLabel = new JLabel();
    JToolBar toolBar1 = new JToolBar();
    JButton button4 = new JButton();
    JButton button6 = new JButton();
    JButton button7 = new JButton();
    JButton button8 = new JButton();
    JToggleButton toggleButton6 = new JToggleButton();
    JButton button1 = new JButton();
    JLabel splitPaneLabel = new JLabel();
    JSplitPane splitPane3 = new JSplitPane();
    JSplitPane splitPane1 = new JSplitPane();
    JPanel panel10 = new JPanel();
    JLabel label1 = new JLabel();
    JPanel panel11 = new JPanel();
    JLabel label2 = new JLabel();
    JSplitPane splitPane2 = new JSplitPane();
    JPanel panel12 = new JPanel();
    JLabel label5 = new JLabel();
    JPanel panel13 = new JPanel();
    JLabel label6 = new JLabel();

    
    setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[][][][][]", "[][][][][][][][][][][][100,top]"));





















    
    scrollPaneLabel.setText("JScrollPane:");
    add(scrollPaneLabel, "cell 0 0");


    
    scrollPane13.setHorizontalScrollBarPolicy(32);
    scrollPane13.setVerticalScrollBarPolicy(22);


    
    panel1.setPreferredSize(new Dimension(200, 200));
    panel1.setLayout(new BorderLayout());
    
    scrollPane13.setViewportView(panel1);
    
    add(scrollPane13, "cell 1 0,grow,width 70,height 40");
    add(scrollBar2, "cell 2 0 1 6,growy");

    
    scrollBar3.setEnabled(false);
    add(scrollBar3, "cell 2 0 1 6,growy");

    
    scrollBar7.putClientProperty("JScrollBar.showButtons", Boolean.valueOf(true));
    add(scrollBar7, "cell 2 0 1 6,growy");

    
    scrollBar8.setEnabled(false);
    scrollBar8.putClientProperty("JScrollBar.showButtons", Boolean.valueOf(true));
    add(scrollBar8, "cell 2 0 1 6,growy");

    
    separator2.setOrientation(1);
    add(separator2, "cell 2 0 1 6,growy");

    
    slider2.setOrientation(1);
    slider2.setValue(30);
    add(slider2, "cell 2 0 1 6,growy,height 100");

    
    slider4.setMinorTickSpacing(10);
    slider4.setPaintTicks(true);
    slider4.setMajorTickSpacing(50);
    slider4.setPaintLabels(true);
    slider4.setOrientation(1);
    slider4.setValue(30);
    add(slider4, "cell 2 0 1 6,growy,height 100");

    
    this.progressBar3.setOrientation(1);
    this.progressBar3.setValue(60);
    add(this.progressBar3, "cell 2 0 1 6,growy");

    
    this.progressBar4.setOrientation(1);
    this.progressBar4.setValue(60);
    this.progressBar4.setStringPainted(true);
    add(this.progressBar4, "cell 2 0 1 6,growy");


    
    toolBar2.setOrientation(1);

    
    button9.setIcon(UIManager.getIcon("Tree.closedIcon"));
    toolBar2.add(button9);

    
    button10.setIcon(UIManager.getIcon("Tree.openIcon"));
    toolBar2.add(button10);
    toolBar2.addSeparator();

    
    button11.setIcon(UIManager.getIcon("Tree.leafIcon"));
    toolBar2.add(button11);

    
    toggleButton7.setIcon(UIManager.getIcon("Tree.closedIcon"));
    toolBar2.add(toggleButton7);
    
    add(toolBar2, "cell 2 0 1 6,growy");


    
    panel2.setBorder(new TitledBorder("TitledBorder"));
    panel2.setLayout(new FlowLayout());
    
    add(panel2, "cell 3 0 1 6,grow");

    
    scrollBarLabel.setText("JScrollBar:");
    add(scrollBarLabel, "cell 0 1");

    
    scrollBar1.setOrientation(0);
    add(scrollBar1, "cell 1 1,growx");

    
    scrollBar4.setOrientation(0);
    scrollBar4.setEnabled(false);
    add(scrollBar4, "cell 1 2,growx");


    
    panel3.setOpaque(false);
    panel3.setLayout((LayoutManager)new MigLayout("ltr,insets 0,hidemode 3", "[]", "[][][][]"));









    
    label4.setText("HTML:");
    panel3.add(label4, "cell 0 0");

    
    label3.setText("<html>JLabel HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a></html>");
    panel3.add(label3, "cell 0 1");




    
    editorPane6.setContentType("text/html");
    editorPane6.setText("JEditorPane HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a>");
    scrollPane15.setViewportView(editorPane6);
    
    panel3.add(scrollPane15, "cell 0 2,grow");




    
    textPane6.setContentType("text/html");
    textPane6.setText("JTextPane HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a>");
    scrollPane16.setViewportView(textPane6);
    
    panel3.add(scrollPane16, "cell 0 3,grow");
    
    add(panel3, "cell 4 0 1 8,aligny top,growy 0");

    
    scrollBar5.setOrientation(0);
    scrollBar5.putClientProperty("JScrollBar.showButtons", Boolean.valueOf(true));
    add(scrollBar5, "cell 1 3,growx");

    
    scrollBar6.setOrientation(0);
    scrollBar6.setEnabled(false);
    scrollBar6.putClientProperty("JScrollBar.showButtons", Boolean.valueOf(true));
    add(scrollBar6, "cell 1 4,growx");

    
    separatorLabel.setText("JSeparator:");
    add(separatorLabel, "cell 0 5");
    add(separator1, "cell 1 5,growx");

    
    sliderLabel.setText("JSlider:");
    add(sliderLabel, "cell 0 6");

    
    slider1.setValue(30);
    add(slider1, "cell 1 6 3 1,aligny top,grow 100 0");

    
    slider6.setEnabled(false);
    slider6.setValue(30);
    add(slider6, "cell 1 6 3 1,aligny top,growy 0");

    
    this.slider3.setMinorTickSpacing(10);
    this.slider3.setPaintTicks(true);
    this.slider3.setMajorTickSpacing(50);
    this.slider3.setPaintLabels(true);
    this.slider3.setValue(30);
    this.slider3.addChangeListener(e -> changeProgress());
    add(this.slider3, "cell 1 7 3 1,aligny top,grow 100 0");

    
    slider5.setMinorTickSpacing(10);
    slider5.setPaintTicks(true);
    slider5.setMajorTickSpacing(50);
    slider5.setPaintLabels(true);
    slider5.setEnabled(false);
    slider5.setValue(30);
    add(slider5, "cell 1 7 3 1,aligny top,growy 0");

    
    progressBarLabel.setText("JProgressBar:");
    add(progressBarLabel, "cell 0 8");

    
    this.progressBar1.setValue(60);
    add(this.progressBar1, "cell 1 8 3 1,growx");

    
    this.progressBar2.setStringPainted(true);
    this.progressBar2.setValue(60);
    add(this.progressBar2, "cell 1 8 3 1,growx");

    
    this.indeterminateCheckBox.setText("indeterminate");
    this.indeterminateCheckBox.addActionListener(e -> indeterminateCheckBoxActionPerformed());
    add(this.indeterminateCheckBox, "cell 4 8");

    
    toolTipLabel.setText("JToolTip:");
    add(toolTipLabel, "cell 0 9");

    
    toolTip1.setTipText("Some text in tool tip.");
    add(toolTip1, "cell 1 9 3 1");

    
    toolTip2.setTipText("Tool tip with\nmultiple\nlines.");
    add(toolTip2, "cell 1 9 3 1");

    
    toolBarLabel.setText("JToolBar:");
    add(toolBarLabel, "cell 0 10");




    
    button4.setIcon(UIManager.getIcon("Tree.closedIcon"));
    toolBar1.add(button4);

    
    button6.setIcon(UIManager.getIcon("Tree.openIcon"));
    toolBar1.add(button6);
    toolBar1.addSeparator();

    
    button7.setIcon(UIManager.getIcon("Tree.leafIcon"));
    toolBar1.add(button7);
    toolBar1.addSeparator();

    
    button8.setText("Text");
    button8.setIcon(UIManager.getIcon("Tree.expandedIcon"));
    toolBar1.add(button8);

    
    toggleButton6.setText("Toggle");
    toggleButton6.setIcon(UIManager.getIcon("Tree.leafIcon"));
    toggleButton6.setSelected(true);
    toolBar1.add(toggleButton6);

    
    button1.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/demo/icons/intellij-showWriteAccess.png")));
    button1.setEnabled(false);
    toolBar1.add(button1);
    
    add(toolBar1, "cell 1 10 3 1,growx");

    
    splitPaneLabel.setText("JSplitPane:");
    add(splitPaneLabel, "cell 0 11");


    
    splitPane3.setResizeWeight(0.5D);


    
    splitPane1.setResizeWeight(0.5D);


    
    panel10.setBackground(new Color(217, 163, 67));
    panel10.setLayout(new BorderLayout());

    
    label1.setText("LEFT");
    label1.setHorizontalAlignment(0);
    label1.setForeground(Color.white);
    panel10.add(label1, "Center");
    
    splitPane1.setLeftComponent(panel10);


    
    panel11.setBackground(new Color(98, 181, 67));
    panel11.setLayout(new BorderLayout());

    
    label2.setText("RIGHT");
    label2.setHorizontalAlignment(0);
    label2.setForeground(Color.white);
    panel11.add(label2, "Center");
    
    splitPane1.setRightComponent(panel11);
    
    splitPane3.setLeftComponent(splitPane1);


    
    splitPane2.setOrientation(0);
    splitPane2.setResizeWeight(0.5D);


    
    panel12.setBackground(new Color(242, 101, 34));
    panel12.setLayout(new BorderLayout());

    
    label5.setText("TOP");
    label5.setHorizontalAlignment(0);
    label5.setForeground(Color.white);
    panel12.add(label5, "Center");
    
    splitPane2.setTopComponent(panel12);


    
    panel13.setBackground(new Color(64, 182, 224));
    panel13.setLayout(new BorderLayout());

    
    label6.setText("BOTTOM");
    label6.setHorizontalAlignment(0);
    label6.setForeground(Color.white);
    panel13.add(label6, "Center");
    
    splitPane2.setBottomComponent(panel13);
    
    splitPane3.setRightComponent(splitPane2);
    
    add(splitPane3, "cell 1 11 4 1,grow");

    
    if (FlatLafDemo.screenshotsMode) {
      Component[] components = { this.indeterminateCheckBox, toolTipLabel, toolTip1, toolTip2, toolBarLabel, toolBar1, toolBar2, splitPaneLabel, splitPane3 };





      
      for (Component c : components)
        c.setVisible(false); 
    } 
  }
}
