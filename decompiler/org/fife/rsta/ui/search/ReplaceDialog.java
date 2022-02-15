package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ui.AssistanceIconPanel;
import org.fife.rsta.ui.ResizableFrameContentPane;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rtextarea.SearchContext;

















































public class ReplaceDialog
  extends AbstractFindReplaceDialog
{
  private static final long serialVersionUID = 1L;
  private JButton replaceButton;
  private JButton replaceAllButton;
  private JLabel replaceFieldLabel;
  private SearchComboBox replaceWithCombo;
  private String lastSearchString;
  private String lastReplaceString;
  private Component superMainComponent;
  protected SearchListener searchListener;
  
  public ReplaceDialog(Dialog owner, SearchListener listener) {
    super(owner);
    this.superMainComponent = owner;
    init(listener);
  }







  
  public ReplaceDialog(Frame owner, SearchListener listener) {
    super(owner);
    this.superMainComponent = owner;
    init(listener);
  }



  
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    
    if (SearchEvent.Type.REPLACE.name().equals(command) || SearchEvent.Type.REPLACE_ALL
      .name().equals(command)) {
      
      this.context.setSearchFor(getSearchString());
      this.context.setReplaceWith(this.replaceWithCombo.getSelectedString());
      
      JTextComponent tc = UIUtil.getTextComponent((JComboBox)this.findTextCombo);
      this.findTextCombo.addItem(tc.getText());
      
      tc = UIUtil.getTextComponent((JComboBox)this.replaceWithCombo);
      String replaceText = tc.getText();
      if (replaceText.length() != 0) {
        this.replaceWithCombo.addItem(replaceText);
      }

      
      fireSearchEvent(SearchEvent.Type.valueOf(command), (SearchContext)null);
    
    }
    else {
      
      super.actionPerformed(e);
      if (SearchEvent.Type.FIND.name().equals(command)) {
        handleToggleButtons();
      }
    } 
  }













  
  protected void escapePressed() {
    if (this.replaceWithCombo.hideAutoCompletePopups()) {
      return;
    }
    super.escapePressed();
  }







  
  public final String getReplaceButtonText() {
    return this.replaceButton.getText();
  }







  
  public final String getReplaceAllButtonText() {
    return this.replaceAllButton.getText();
  }







  
  public String getReplaceString() {
    String text = this.replaceWithCombo.getSelectedString();
    if (text == null) {
      text = "";
    }
    return text;
  }







  
  public final String getReplaceWithLabelText() {
    return this.replaceFieldLabel.getText();
  }






  
  protected void handleRegExCheckBoxClicked() {
    super.handleRegExCheckBoxClicked();
    
    boolean b = this.regexCheckBox.isSelected();
    
    this.replaceWithCombo.setAutoCompleteEnabled(b);
  }



  
  protected void handleSearchContextPropertyChanged(PropertyChangeEvent e) {
    String prop = e.getPropertyName();
    
    if ("Search.replaceWith".equals(prop)) {
      String newValue = (String)e.getNewValue();
      if (newValue == null) {
        newValue = "";
      }
      String oldValue = getReplaceString();
      
      if (!newValue.equals(oldValue)) {
        setReplaceString(newValue);
      }
    }
    else {
      
      super.handleSearchContextPropertyChanged(e);
    } 
  }




  
  protected FindReplaceButtonsEnableResult handleToggleButtons() {
    FindReplaceButtonsEnableResult er = super.handleToggleButtons();
    boolean shouldReplace = er.getEnable();
    this.replaceAllButton.setEnabled(shouldReplace);


    
    if (shouldReplace) {
      String text = this.searchListener.getSelectedText();
      shouldReplace = matchesSearchFor(text);
    } 
    this.replaceButton.setEnabled(shouldReplace);
    
    return er;
  }








  
  private void init(SearchListener listener) {
    this.searchListener = listener;

    
    ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());

    
    JPanel searchPanel = new JPanel(new SpringLayout());

    
    ReplaceFocusAdapter replaceFocusAdapter = new ReplaceFocusAdapter();
    ReplaceDocumentListener replaceDocumentListener = new ReplaceDocumentListener();

    
    JTextComponent textField = UIUtil.getTextComponent((JComboBox)this.findTextCombo);
    textField.addFocusListener(replaceFocusAdapter);
    textField.getDocument().addDocumentListener(replaceDocumentListener);

    
    this.replaceWithCombo = new SearchComboBox(null, true);
    textField = UIUtil.getTextComponent((JComboBox)this.replaceWithCombo);
    textField.addFocusListener(replaceFocusAdapter);
    textField.getDocument().addDocumentListener(replaceDocumentListener);

    
    this.replaceFieldLabel = UIUtil.newLabel(getBundle(), "ReplaceWith", (Component)this.replaceWithCombo);

    
    JPanel temp = new JPanel(new BorderLayout());
    temp.add((Component)this.findTextCombo);
    AssistanceIconPanel aip = new AssistanceIconPanel((JComponent)this.findTextCombo);
    temp.add((Component)aip, "Before");
    JPanel temp2 = new JPanel(new BorderLayout());
    temp2.add((Component)this.replaceWithCombo);
    AssistanceIconPanel aip2 = new AssistanceIconPanel((JComponent)this.replaceWithCombo);
    temp2.add((Component)aip2, "Before");

    
    if (orientation.isLeftToRight()) {
      searchPanel.add(this.findFieldLabel);
      searchPanel.add(temp);
      searchPanel.add(this.replaceFieldLabel);
      searchPanel.add(temp2);
    } else {
      
      searchPanel.add(temp);
      searchPanel.add(this.findFieldLabel);
      searchPanel.add(temp2);
      searchPanel.add(this.replaceFieldLabel);
    } 
    
    UIUtil.makeSpringCompactGrid(searchPanel, 2, 2, 0, 0, 6, 6);




    
    JPanel bottomPanel = new JPanel(new BorderLayout());
    temp = new JPanel(new BorderLayout());
    bottomPanel.setBorder(UIUtil.getEmpty5Border());
    temp.add(this.searchConditionsPanel, "Before");
    temp.add(this.searchConditionsPanel, "Before");
    temp2 = new JPanel(new BorderLayout());
    temp2.add(this.dirPanel, "North");
    temp.add(temp2);
    bottomPanel.add(temp, "Before");

    
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, 1));
    leftPanel.add(searchPanel);
    leftPanel.add(bottomPanel);

    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(4, 1, 5, 5));
    ResourceBundle msg = getBundle();
    this.replaceButton = UIUtil.newButton(msg, "Replace");
    this.replaceButton.setActionCommand(SearchEvent.Type.REPLACE.name());
    this.replaceButton.addActionListener(this);
    this.replaceButton.setEnabled(false);
    this.replaceButton.setIcon((Icon)null);
    this.replaceButton.setToolTipText((String)null);
    this.replaceAllButton = UIUtil.newButton(msg, "ReplaceAll");
    this.replaceAllButton.setActionCommand(SearchEvent.Type.REPLACE_ALL.name());
    this.replaceAllButton.addActionListener(this);
    this.replaceAllButton.setEnabled(false);
    this.replaceAllButton.setIcon((Icon)null);
    this.replaceAllButton.setToolTipText((String)null);
    buttonPanel.add(this.findNextButton);
    buttonPanel.add(this.replaceButton);
    buttonPanel.add(this.replaceAllButton);
    buttonPanel.add(this.cancelButton);
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(buttonPanel, "North");

    
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    contentPane.add(leftPanel);
    contentPane.add(rightPanel, "After");
    ResizableFrameContentPane resizableFrameContentPane = new ResizableFrameContentPane(new BorderLayout());
    resizableFrameContentPane.add(contentPane, "North");
    setContentPane((Container)resizableFrameContentPane);
    getRootPane().setDefaultButton(this.findNextButton);
    setTitle(getString("ReplaceDialogTitle"));
    setResizable(true);
    pack();
    setLocationRelativeTo(getParent());
    
    setSearchContext(new SearchContext());
    addSearchListener(listener);
    
    applyComponentOrientation(orientation);
  }



  
  public void setContentAssistImage(Image image) {
    super.setContentAssistImage(image);
    this.replaceWithCombo.setContentAssistImage(image);
  }







  
  public final void setReplaceButtonText(String text) {
    this.replaceButton.setText(text);
  }







  
  public final void setReplaceAllButtonText(String text) {
    this.replaceAllButton.setText(text);
  }







  
  public final void setReplaceWithLabelText(String text) {
    this.replaceFieldLabel.setText(text);
  }







  
  public void setReplaceString(String newReplaceString) {
    this.replaceWithCombo.addItem(newReplaceString);
  }









  
  public void setVisible(boolean visible) {
    setLocationRelativeTo(this.superMainComponent);
    
    if (visible) {

      
      String text = this.searchListener.getSelectedText();
      if (text != null) {
        this.findTextCombo.addItem(text);
      }
      
      String selectedItem = this.findTextCombo.getSelectedString();
      if (selectedItem == null || selectedItem.length() == 0) {
        this.findNextButton.setEnabled(false);
        this.replaceButton.setEnabled(false);
        this.replaceAllButton.setEnabled(false);
      } else {
        
        handleToggleButtons();
      } 
      
      super.setVisible(true);
      focusFindTextField();
    
    }
    else {
      
      super.setVisible(false);
    } 
  }











  
  public void updateUI() {
    SwingUtilities.updateComponentTreeUI((Component)this);
    pack();

    
    ReplaceFocusAdapter replaceFocusAdapter = new ReplaceFocusAdapter();
    ReplaceDocumentListener replaceDocumentListener = new ReplaceDocumentListener();

    
    JTextComponent textField = UIUtil.getTextComponent((JComboBox)this.findTextCombo);
    textField.addFocusListener(replaceFocusAdapter);
    textField.getDocument().addDocumentListener(replaceDocumentListener);

    
    textField = UIUtil.getTextComponent((JComboBox)this.replaceWithCombo);
    textField.addFocusListener(replaceFocusAdapter);
    textField.getDocument().addDocumentListener(replaceDocumentListener);
  }


  
  private class ReplaceDocumentListener
    implements DocumentListener
  {
    private ReplaceDocumentListener() {}


    
    public void insertUpdate(DocumentEvent e) {
      JTextComponent findWhatTextField = UIUtil.getTextComponent((JComboBox)ReplaceDialog.this.findTextCombo);
      if (e.getDocument().equals(findWhatTextField.getDocument())) {
        ReplaceDialog.this.handleToggleButtons();
      }
    }


    
    public void removeUpdate(DocumentEvent e) {
      JTextComponent findWhatTextField = UIUtil.getTextComponent((JComboBox)ReplaceDialog.this.findTextCombo);
      if (e.getDocument().equals(findWhatTextField.getDocument()) && e
        .getDocument().getLength() == 0) {
        ReplaceDialog.this.findNextButton.setEnabled(false);
        ReplaceDialog.this.replaceButton.setEnabled(false);
        ReplaceDialog.this.replaceAllButton.setEnabled(false);
      } else {
        
        ReplaceDialog.this.handleToggleButtons();
      } 
    }


    
    public void changedUpdate(DocumentEvent e) {}
  }


  
  private class ReplaceFocusAdapter
    extends FocusAdapter
  {
    private ReplaceFocusAdapter() {}


    
    public void focusGained(FocusEvent e) {
      JTextComponent textField = (JTextComponent)e.getSource();
      textField.selectAll();
      
      if (textField == UIUtil.getTextComponent((JComboBox)ReplaceDialog.this.findTextCombo)) {
        
        ReplaceDialog.this.lastSearchString = ReplaceDialog.this.findTextCombo.getSelectedString();
      }
      else {
        
        ReplaceDialog.this.lastReplaceString = ReplaceDialog.this.replaceWithCombo.getSelectedString();
      } 

      
      ReplaceDialog.this.handleToggleButtons();
    }
  }
}
