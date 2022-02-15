package com.jgoodies.forms.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.forms.FormsSetup;
import com.jgoodies.forms.factories.ComponentFactory;
import com.jgoodies.forms.factories.Forms;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.forms.internal.InternalFocusSetupUtils;
import com.jgoodies.forms.util.FocusTraversalType;
import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;







































































public final class ListViewBuilder
{
  private ComponentFactory factory;
  private JComponent label;
  private JComponent filterView;
  private JComponent listView;
  private JComponent listBarView;
  private JComponent listExtrasView;
  private JComponent detailsView;
  private JComponent listStackView;
  private Border border;
  private boolean honorsVisibility = true;
  private Component initialComponent;
  private FocusTraversalType focusTraversalType;
  private FocusTraversalPolicy focusTraversalPolicy;
  private String namePrefix = "ListView";
  private String filterViewColSpec = "[100dlu, p]";
  private String listViewRowSpec = "fill:[100dlu, d]:grow";












  
  private JComponent panel;












  
  public static ListViewBuilder create() {
    return new ListViewBuilder();
  }









  
  public ListViewBuilder border(Border border) {
    this.border = border;
    invalidatePanel();
    return this;
  }









  
  public ListViewBuilder padding(EmptyBorder padding) {
    border(padding);
    return this;
  }





















  
  public ListViewBuilder padding(String paddingSpec, Object... args) {
    padding((EmptyBorder)Paddings.createPadding(paddingSpec, args));
    return this;
  }











  
  public ListViewBuilder initialComponent(JComponent initialComponent) {
    Preconditions.checkNotNull(initialComponent, "The %1$s must not be null.", new Object[] { "initial component" });
    Preconditions.checkState((this.initialComponent == null), "The initial component must be set once only.");
    
    checkValidFocusTraversalSetup();
    this.initialComponent = initialComponent;
    return this;
  }








  
  public ListViewBuilder focusTraversalType(FocusTraversalType focusTraversalType) {
    Preconditions.checkNotNull(focusTraversalType, "The %1$s must not be null.", new Object[] { "focus traversal type" });
    Preconditions.checkState((this.focusTraversalType == null), "The focus traversal type must be set once only.");
    
    checkValidFocusTraversalSetup();
    this.focusTraversalType = focusTraversalType;
    return this;
  }
















  
  public ListViewBuilder focusTraversalPolicy(FocusTraversalPolicy policy) {
    Preconditions.checkNotNull(policy, "The %1$s must not be null.", new Object[] { "focus traversal policy" });
    Preconditions.checkState((this.focusTraversalPolicy == null), "The focus traversal policy must be set once only.");
    
    checkValidFocusTraversalSetup();
    this.focusTraversalPolicy = policy;
    return this;
  }


























  
  public ListViewBuilder honorVisibility(boolean b) {
    this.honorsVisibility = b;
    invalidatePanel();
    return this;
  }









  
  public ListViewBuilder namePrefix(String namePrefix) {
    this.namePrefix = namePrefix;
    return this;
  }











  
  public ListViewBuilder factory(ComponentFactory factory) {
    this.factory = factory;
    return this;
  }










  
  public ListViewBuilder label(JComponent labelView) {
    this.label = labelView;
    overrideNameIfBlank(labelView, "label");
    invalidatePanel();
    return this;
  }

















  
  public ListViewBuilder labelText(String markedText, Object... args) {
    label(getFactory().createLabel(Strings.get(markedText, args)));
    return this;
  }

















  
  public ListViewBuilder headerText(String markedText, Object... args) {
    label(getFactory().createHeaderLabel(Strings.get(markedText, args)));
    return this;
  }









  
  public ListViewBuilder filterView(JComponent filterView) {
    this.filterView = filterView;
    overrideNameIfBlank(filterView, "filter");
    invalidatePanel();
    return this;
  }
















  
  public ListViewBuilder filterViewColumn(String colSpec, Object... args) {
    Preconditions.checkNotNull(colSpec, "The %1$s must not be null, empty, or whitespace.", new Object[] { "filter view column specification" });
    this.filterViewColSpec = Strings.get(colSpec, args);
    invalidatePanel();
    return this;
  }












  
  public ListViewBuilder listView(JComponent listView) {
    Preconditions.checkNotNull(listView, "The %1$s must not be null, empty, or whitespace.", new Object[] { "list view" });
    if (listView instanceof javax.swing.JTable || listView instanceof javax.swing.JList || listView instanceof javax.swing.JTree) {
      this.listView = new JScrollPane(listView);
    } else {
      this.listView = listView;
    } 
    overrideNameIfBlank(listView, "listView");
    invalidatePanel();
    return this;
  }























  
  public ListViewBuilder listViewRow(String rowSpec, Object... args) {
    Preconditions.checkNotNull(rowSpec, "The %1$s must not be null, empty, or whitespace.", new Object[] { "list view row specification" });
    this.listViewRowSpec = Strings.get(rowSpec, args);
    invalidatePanel();
    return this;
  }










  
  public ListViewBuilder listBarView(JComponent listBarView) {
    this.listBarView = listBarView;
    overrideNameIfBlank(listBarView, "listBarView");
    invalidatePanel();
    return this;
  }
















  
  public ListViewBuilder listBar(JComponent... buttons) {
    listBarView(Forms.buttonBar(buttons));
    return this;
  }










  
  public ListViewBuilder listStackView(JComponent listStackView) {
    this.listStackView = listStackView;
    overrideNameIfBlank(listStackView, "listStackView");
    invalidatePanel();
    return this;
  }
















  
  public ListViewBuilder listStack(JComponent... buttons) {
    listStackView(Forms.buttonStack(buttons));
    return this;
  }








  
  public ListViewBuilder listExtrasView(JComponent listExtrasView) {
    this.listExtrasView = listExtrasView;
    overrideNameIfBlank(listExtrasView, "listExtrasView");
    invalidatePanel();
    return this;
  }








  
  public ListViewBuilder detailsView(JComponent detailsView) {
    this.detailsView = detailsView;
    overrideNameIfBlank(detailsView, "detailsView");
    invalidatePanel();
    return this;
  }






  
  public JComponent build() {
    if (this.panel == null) {
      this.panel = buildPanel();
    }
    return this.panel;
  }



  
  private ComponentFactory getFactory() {
    if (this.factory == null) {
      this.factory = FormsSetup.getComponentFactoryDefault();
    }
    return this.factory;
  }

  
  private void invalidatePanel() {
    this.panel = null;
  }

  
  private JComponent buildPanel() {
    Preconditions.checkNotNull(this.listView, "The list view must be set before #build is invoked.");
    String stackGap = hasStack() ? "$rg" : "0";
    String detailsGap = hasDetails() ? "14dlu" : "0";
    FormBuilder builder = FormBuilder.create().columns("fill:default:grow, %s, p", new Object[] { stackGap }).rows("p, %1$s, p, %2$s, p", new Object[] { this.listViewRowSpec, detailsGap }).honorsVisibility(this.honorsVisibility).border(this.border).add(hasHeader(), buildHeader()).xy(1, 1).add(true, this.listView).xy(1, 2).add(hasOperations(), buildOperations()).xy(1, 3).add(hasStack(), this.listStackView).xy(3, 2).add(hasDetails(), this.detailsView).xy(1, 5);










    
    if (this.label instanceof JLabel) {
      JLabel theLabel = (JLabel)this.label;
      if (theLabel.getLabelFor() == null) {
        theLabel.setLabelFor(this.listView);
      }
    } 
    InternalFocusSetupUtils.setupFocusTraversalPolicyAndProvider(builder.getPanel(), this.focusTraversalPolicy, this.focusTraversalType, this.initialComponent);



    
    return builder.build();
  }

  
  private JComponent buildHeader() {
    if (!hasHeader()) {
      return null;
    }
    String columnSpec = hasFilter() ? "default:grow, 9dlu, %s" : "default:grow, 0,    0";

    
    return FormBuilder.create().columns(columnSpec, new Object[] { this.filterViewColSpec }).rows("[14dlu, p], $lcg", new Object[0]).labelForFeatureEnabled(false).add(hasLabel(), this.label).xy(1, 1).add(hasFilter(), this.filterView).xy(3, 1).build();
  }







  
  private JComponent buildOperations() {
    if (!hasOperations()) {
      return null;
    }
    String gap = hasListExtras() ? "9dlu" : "0";
    return FormBuilder.create().columns("left:default, %s:grow, right:pref", new Object[] { gap }).rows("$rgap, p", new Object[0]).honorsVisibility(this.honorsVisibility).add(hasListBar(), this.listBarView).xy(1, 2).add(hasListExtras(), this.listExtrasView).xy(3, 2).build();
  }









  
  private boolean hasLabel() {
    return (this.label != null);
  }

  
  private boolean hasFilter() {
    return (this.filterView != null);
  }

  
  private boolean hasHeader() {
    return (hasLabel() || hasFilter());
  }

  
  private boolean hasListBar() {
    return (this.listBarView != null);
  }

  
  private boolean hasListExtras() {
    return (this.listExtrasView != null);
  }

  
  private boolean hasOperations() {
    return (hasListBar() || hasListExtras());
  }

  
  private boolean hasStack() {
    return (this.listStackView != null);
  }

  
  private boolean hasDetails() {
    return (this.detailsView != null);
  }

  
  private void overrideNameIfBlank(JComponent component, String suffix) {
    if (component != null && Strings.isBlank(component.getName())) {
      component.setName(this.namePrefix + '.' + suffix);
    }
  }





  
  private void checkValidFocusTraversalSetup() {
    InternalFocusSetupUtils.checkValidFocusTraversalSetup(this.focusTraversalPolicy, this.focusTraversalType, this.initialComponent);
  }
}
