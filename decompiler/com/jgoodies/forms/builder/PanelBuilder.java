package com.jgoodies.forms.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.forms.FormsSetup;
import com.jgoodies.forms.internal.AbstractFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



















































































































@Deprecated
public class PanelBuilder
  extends AbstractFormBuilder<PanelBuilder>
{
  private static final String LABELED_BY_PROPERTY = "labeledBy";
  private boolean labelForFeatureEnabled;
  private WeakReference mostRecentlyAddedLabelReference = null;












  
  public PanelBuilder(FormLayout layout) {
    this(layout, new JPanel(null));
  }









  
  public PanelBuilder(FormLayout layout, JPanel panel) {
    super(layout, panel);
    opaque(FormsSetup.getOpaqueDefault());
    this.labelForFeatureEnabled = FormsSetup.getLabelForFeatureEnabledDefault();
  }



















  
  public PanelBuilder focusTraversal(FocusTraversalPolicy policy) {
    getPanel().setFocusTraversalPolicy(policy);
    getPanel().setFocusTraversalPolicyProvider(true);
    return this;
  }









  
  public PanelBuilder labelForFeatureEnabled(boolean b) {
    this.labelForFeatureEnabled = b;
    return this;
  }












  
  public final JPanel build() {
    return getPanel();
  }



















  
  public final JLabel addLabel(String textWithMnemonic) {
    return addLabel(textWithMnemonic, cellConstraints());
  }


















  
  public final JLabel addLabel(String textWithMnemonic, CellConstraints constraints) {
    JLabel label = getComponentFactory().createLabel(textWithMnemonic);
    add(label, constraints);
    return label;
  }


















  
  public final JLabel addLabel(String textWithMnemonic, String encodedConstraints) {
    return addLabel(textWithMnemonic, new CellConstraints(encodedConstraints));
  }




















































  
  public final JLabel addLabel(String textWithMnemonic, CellConstraints labelConstraints, Component component, CellConstraints componentConstraints) {
    if (labelConstraints == componentConstraints) {
      throw new IllegalArgumentException("You must provide two CellConstraints instances, one for the label and one for the component.\nConsider using the CC class. See the JavaDocs for details.");
    }



    
    JLabel label = addLabel(textWithMnemonic, labelConstraints);
    add(component, componentConstraints);
    label.setLabelFor(component);
    return label;
  }




















  
  public final JLabel addROLabel(String textWithMnemonic) {
    return addROLabel(textWithMnemonic, cellConstraints());
  }



















  
  public final JLabel addROLabel(String textWithMnemonic, CellConstraints constraints) {
    JLabel label = getComponentFactory().createReadOnlyLabel(textWithMnemonic);
    add(label, constraints);
    return label;
  }



















  
  public final JLabel addROLabel(String textWithMnemonic, String encodedConstraints) {
    return addROLabel(textWithMnemonic, new CellConstraints(encodedConstraints));
  }




















































  
  public final JLabel addROLabel(String textWithMnemonic, CellConstraints labelConstraints, Component component, CellConstraints componentConstraints) {
    checkConstraints(labelConstraints, componentConstraints);
    JLabel label = addROLabel(textWithMnemonic, labelConstraints);
    add(component, componentConstraints);
    label.setLabelFor(component);
    return label;
  }



















  
  public final JLabel addTitle(String textWithMnemonic) {
    return addTitle(textWithMnemonic, cellConstraints());
  }


















  
  public final JLabel addTitle(String textWithMnemonic, CellConstraints constraints) {
    JLabel titleLabel = getComponentFactory().createTitle(textWithMnemonic);
    add(titleLabel, constraints);
    return titleLabel;
  }


















  
  public final JLabel addTitle(String textWithMnemonic, String encodedConstraints) {
    return addTitle(textWithMnemonic, new CellConstraints(encodedConstraints));
  }

















  
  public final JComponent addSeparator(String textWithMnemonic) {
    return addSeparator(textWithMnemonic, getLayout().getColumnCount());
  }
















  
  public final JComponent addSeparator(String textWithMnemonic, CellConstraints constraints) {
    int titleAlignment = isLeftToRight() ? 2 : 4;

    
    JComponent titledSeparator = getComponentFactory().createSeparator(textWithMnemonic, titleAlignment);
    
    add(titledSeparator, constraints);
    return titledSeparator;
  }
















  
  public final JComponent addSeparator(String textWithMnemonic, String encodedConstraints) {
    return addSeparator(textWithMnemonic, new CellConstraints(encodedConstraints));
  }
















  
  public final JComponent addSeparator(String textWithMnemonic, int columnSpan) {
    return addSeparator(textWithMnemonic, createLeftAdjustedConstraints(columnSpan));
  }

















































  
  public final JLabel add(JLabel label, CellConstraints labelConstraints, Component component, CellConstraints componentConstraints) {
    checkConstraints(labelConstraints, componentConstraints);
    add(label, labelConstraints);
    add(component, componentConstraints);
    label.setLabelFor(component);
    return label;
  }

















  
  public Component add(Component component, CellConstraints cellConstraints) {
    Component result = super.add(component, cellConstraints);
    manageLabelsAndComponents(component);
    return result;
  }



  
  private void manageLabelsAndComponents(Component c) {
    if (!this.labelForFeatureEnabled) {
      return;
    }
    if (c instanceof JLabel) {
      JLabel label = (JLabel)c;
      if (label.getLabelFor() == null) {
        setMostRecentlyAddedLabel(label);
      } else {
        clearMostRecentlyAddedLabel();
      } 
      return;
    } 
    JLabel mostRecentlyAddedLabel = getMostRecentlyAddedLabel();
    if (mostRecentlyAddedLabel != null && isLabelForApplicable(mostRecentlyAddedLabel, c)) {
      
      setLabelFor(mostRecentlyAddedLabel, c);
      clearMostRecentlyAddedLabel();
    } 
  }















  
  protected boolean isLabelForApplicable(JLabel label, Component component) {
    if (label.getLabelFor() != null) {
      return false;
    }

    
    if (!component.isFocusable()) {
      return false;
    }

    
    if (!(component instanceof JComponent)) {
      return true;
    }
    JComponent c = (JComponent)component;
    return (c.getClientProperty("labeledBy") == null);
  }











  
  protected void setLabelFor(JLabel label, Component component) {
    Component labeledComponent;
    if (component instanceof JScrollPane) {
      JScrollPane scrollPane = (JScrollPane)component;
      labeledComponent = scrollPane.getViewport().getView();
    } else {
      labeledComponent = component;
    } 
    label.setLabelFor(labeledComponent);
  }













  
  private JLabel getMostRecentlyAddedLabel() {
    if (this.mostRecentlyAddedLabelReference == null) {
      return null;
    }
    JLabel label = this.mostRecentlyAddedLabelReference.get();
    if (label == null) {
      return null;
    }
    return label;
  }






  
  private void setMostRecentlyAddedLabel(JLabel label) {
    this.mostRecentlyAddedLabelReference = new WeakReference<JLabel>(label);
  }




  
  private void clearMostRecentlyAddedLabel() {
    this.mostRecentlyAddedLabelReference = null;
  }

  
  private static void checkConstraints(CellConstraints c1, CellConstraints c2) {
    Preconditions.checkArgument((c1 != c2), "You must provide two CellConstraints instances, one for the label and one for the component.\nConsider using the CC factory. See the JavaDocs for details.");
  }
}
