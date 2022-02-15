package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
















































public class FindDialog
  extends AbstractFindReplaceDialog
{
  private static final long serialVersionUID = 1L;
  private String lastSearchString;
  protected SearchListener searchListener;
  
  public FindDialog(Dialog owner, SearchListener listener) {
    super(owner);
    init(listener);
  }







  
  public FindDialog(Frame owner, SearchListener listener) {
    super(owner);
    init(listener);
  }







  
  private void init(SearchListener listener) {
    this.searchListener = listener;

    
    ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());

    
    JPanel enterTextPane = new JPanel(new SpringLayout());
    enterTextPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    JTextComponent textField = UIUtil.getTextComponent((JComboBox)this.findTextCombo);
    textField.addFocusListener(new FindFocusAdapter());
    textField.getDocument().addDocumentListener(new FindDocumentListener());
    JPanel temp = new JPanel(new BorderLayout());
    temp.add((Component)this.findTextCombo);
    AssistanceIconPanel aip = new AssistanceIconPanel((JComponent)this.findTextCombo);
    temp.add((Component)aip, "Before");
    if (orientation.isLeftToRight()) {
      enterTextPane.add(this.findFieldLabel);
      enterTextPane.add(temp);
    } else {
      
      enterTextPane.add(temp);
      enterTextPane.add(this.findFieldLabel);
    } 
    
    UIUtil.makeSpringCompactGrid(enterTextPane, 1, 2, 0, 0, 6, 6);




    
    JPanel bottomPanel = new JPanel(new BorderLayout());
    temp = new JPanel(new BorderLayout());
    bottomPanel.setBorder(UIUtil.getEmpty5Border());
    temp.add(this.searchConditionsPanel, "Before");
    JPanel temp2 = new JPanel(new BorderLayout());
    temp2.add(this.dirPanel, "North");
    temp.add(temp2);
    bottomPanel.add(temp, "Before");

    
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, 1));
    leftPanel.add(enterTextPane);
    leftPanel.add(bottomPanel);

    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(2, 1, 5, 5));
    buttonPanel.add(this.findNextButton);
    buttonPanel.add(this.cancelButton);
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(buttonPanel, "North");

    
    JPanel contentPane = new JPanel(new BorderLayout());
    if (orientation.isLeftToRight()) {
      contentPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
    } else {
      
      contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
    } 
    contentPane.add(leftPanel);
    contentPane.add(rightPanel, "After");
    ResizableFrameContentPane resizableFrameContentPane = new ResizableFrameContentPane(new BorderLayout());
    resizableFrameContentPane.add(contentPane, "North");
    setContentPane((Container)resizableFrameContentPane);
    getRootPane().setDefaultButton(this.findNextButton);
    setTitle(getString("FindDialogTitle"));
    setResizable(true);
    pack();
    setLocationRelativeTo(getParent());
    
    setSearchContext(new SearchContext());
    addSearchListener(listener);
    
    applyComponentOrientation(orientation);
  }










  
  public void setVisible(boolean visible) {
    if (visible) {

      
      String text = this.searchListener.getSelectedText();
      if (text != null) {
        this.findTextCombo.addItem(text);
      }
      
      String selectedItem = this.findTextCombo.getSelectedString();
      boolean nonEmpty = (selectedItem != null && selectedItem.length() > 0);
      this.findNextButton.setEnabled(nonEmpty);
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
    JTextComponent textField = UIUtil.getTextComponent((JComboBox)this.findTextCombo);
    textField.addFocusListener(new FindFocusAdapter());
    textField.getDocument().addDocumentListener(new FindDocumentListener());
  }

  
  private class FindDocumentListener
    implements DocumentListener
  {
    private FindDocumentListener() {}

    
    public void insertUpdate(DocumentEvent e) {
      FindDialog.this.handleToggleButtons();
    }

    
    public void removeUpdate(DocumentEvent e) {
      JTextComponent comp = UIUtil.getTextComponent((JComboBox)FindDialog.this.findTextCombo);
      if (comp.getDocument().getLength() == 0) {
        FindDialog.this.findNextButton.setEnabled(false);
      } else {
        
        FindDialog.this.handleToggleButtons();
      } 
    }


    
    public void changedUpdate(DocumentEvent e) {}
  }


  
  private class FindFocusAdapter
    extends FocusAdapter
  {
    private FindFocusAdapter() {}


    
    public void focusGained(FocusEvent e) {
      UIUtil.getTextComponent((JComboBox)FindDialog.this.findTextCombo).selectAll();
      
      FindDialog.this.lastSearchString = (String)FindDialog.this.findTextCombo.getSelectedItem();
    }
  }
}
