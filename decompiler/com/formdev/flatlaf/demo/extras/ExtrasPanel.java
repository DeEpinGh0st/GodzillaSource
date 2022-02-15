package com.formdev.flatlaf.demo.extras;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;







public class ExtrasPanel
  extends JPanel
{
  private JLabel label4;
  private JLabel label1;
  private FlatTriStateCheckBox triStateCheckBox1;
  private JLabel triStateLabel1;
  private JLabel label2;
  private JPanel svgIconsPanel;
  private JLabel label3;
  
  public ExtrasPanel() {
    initComponents();
    
    this.triStateLabel1.setText(this.triStateCheckBox1.getState().toString());
    
    addSVGIcon("actions/copy.svg");
    addSVGIcon("actions/colors.svg");
    addSVGIcon("actions/execute.svg");
    addSVGIcon("actions/suspend.svg");
    addSVGIcon("actions/intentionBulb.svg");
    addSVGIcon("actions/quickfixOffBulb.svg");
    
    addSVGIcon("objects/abstractClass.svg");
    addSVGIcon("objects/abstractMethod.svg");
    addSVGIcon("objects/annotationtype.svg");
    addSVGIcon("objects/annotationtype.svg");
    addSVGIcon("objects/css.svg");
    addSVGIcon("objects/javaScript.svg");
    addSVGIcon("objects/xhtml.svg");
    
    addSVGIcon("errorDialog.svg");
    addSVGIcon("informationDialog.svg");
    addSVGIcon("warningDialog.svg");
  }
  
  private void addSVGIcon(String name) {
    this.svgIconsPanel.add(new JLabel((Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/extras/svg/" + name)));
  }
  
  private void triStateCheckBox1Changed() {
    this.triStateLabel1.setText(this.triStateCheckBox1.getState().toString());
  }

  
  private void initComponents() {
    this.label4 = new JLabel();
    this.label1 = new JLabel();
    this.triStateCheckBox1 = new FlatTriStateCheckBox();
    this.triStateLabel1 = new JLabel();
    this.label2 = new JLabel();
    this.svgIconsPanel = new JPanel();
    this.label3 = new JLabel();

    
    setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[][][left]", "[]para[][][]"));











    
    this.label4.setText("Note: Components on this page require the flatlaf-extras library.");
    add(this.label4, "cell 0 0 3 1");

    
    this.label1.setText("TriStateCheckBox:");
    add(this.label1, "cell 0 1");

    
    this.triStateCheckBox1.setText("Three States");
    this.triStateCheckBox1.addActionListener(e -> triStateCheckBox1Changed());
    add((Component)this.triStateCheckBox1, "cell 1 1");

    
    this.triStateLabel1.setText("text");
    this.triStateLabel1.setEnabled(false);
    add(this.triStateLabel1, "cell 2 1,gapx 30");

    
    this.label2.setText("SVG Icons:");
    add(this.label2, "cell 0 2");


    
    this.svgIconsPanel.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[fill]", "[grow,center]"));





    
    add(this.svgIconsPanel, "cell 1 2 2 1");

    
    this.label3.setText("The icons may change colors when switching to another theme.");
    add(this.label3, "cell 1 3 2 1");
  }
}
