package org.fife.rsta.ui.search;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
























public class ReplaceToolBar
  extends FindToolBar
{
  private JButton replaceButton;
  private JButton replaceAllButton;
  protected SearchListener searchListener;
  
  public ReplaceToolBar(SearchListener listener) {
    super(listener);
    this.searchListener = listener;
  }


  
  public void addNotify() {
    super.addNotify();
    handleToggleButtons();
  }



  
  protected Container createButtonPanel() {
    Box panel = new Box(2);
    
    JPanel bp = new JPanel(new GridLayout(2, 2, 5, 5));
    panel.add(bp);
    
    createFindButtons();
    
    Component filler = Box.createRigidArea(new Dimension(5, 5));
    
    bp.add(this.findButton); bp.add(this.replaceButton);
    bp.add(this.replaceAllButton); bp.add(filler);
    panel.add(bp);
    
    JPanel optionPanel = new JPanel(new SpringLayout());
    this.matchCaseCheckBox = createCB("MatchCase");
    this.regexCheckBox = createCB("RegEx");
    this.wholeWordCheckBox = createCB("WholeWord");
    this.markAllCheckBox = createCB("MarkAll");
    this.wrapCheckBox = createCB("Wrap");


    
    Dimension spacing = new Dimension(1, 5);
    Component space1 = Box.createRigidArea(spacing);
    Component space2 = Box.createRigidArea(spacing);
    Component space3 = Box.createRigidArea(spacing);
    Component space4 = Box.createRigidArea(spacing);

    
    ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());
    
    if (orientation.isLeftToRight()) {
      optionPanel.add(this.matchCaseCheckBox); optionPanel.add(this.wholeWordCheckBox); optionPanel.add(this.wrapCheckBox);
      optionPanel.add(space1); optionPanel.add(space2); optionPanel.add(space3);
      optionPanel.add(this.regexCheckBox); optionPanel.add(this.markAllCheckBox); optionPanel.add(space4);
    } else {
      
      optionPanel.add(this.wrapCheckBox); optionPanel.add(this.wholeWordCheckBox); optionPanel.add(this.matchCaseCheckBox);
      optionPanel.add(space3); optionPanel.add(space2); optionPanel.add(space1);
      optionPanel.add(space4); optionPanel.add(this.markAllCheckBox); optionPanel.add(this.regexCheckBox);
    } 
    UIUtil.makeSpringCompactGrid(optionPanel, 3, 3, 0, 0, 0, 0);
    panel.add(optionPanel);
    
    return panel;
  }




  
  protected Container createFieldPanel() {
    this.findFieldListener = new ReplaceFindFieldListener();
    
    JPanel temp = new JPanel(new SpringLayout());
    
    JLabel findLabel = new JLabel(MSG.getString("FindWhat"));
    JLabel replaceLabel = new JLabel(MSG.getString("ReplaceWith"));
    
    this.findCombo = new SearchComboBox(this, false);
    JTextComponent findField = UIUtil.getTextComponent((JComboBox)this.findCombo);
    this.findFieldListener.install(findField);
    Container fcp = createContentAssistablePanel((JComponent)this.findCombo);
    
    this.replaceCombo = new SearchComboBox(this, true);
    JTextComponent replaceField = UIUtil.getTextComponent((JComboBox)this.replaceCombo);
    this.findFieldListener.install(replaceField);
    Container rcp = createContentAssistablePanel((JComponent)this.replaceCombo);



    
    Dimension spacing = new Dimension(1, 5);
    Component space1 = Box.createRigidArea(spacing);
    Component space2 = Box.createRigidArea(spacing);
    
    if (getComponentOrientation().isLeftToRight()) {
      temp.add(findLabel); temp.add(fcp);
      temp.add(space1); temp.add(space2);
      temp.add(replaceLabel); temp.add(rcp);
    } else {
      
      temp.add(fcp); temp.add(findLabel);
      temp.add(space2); temp.add(space1);
      temp.add(rcp); temp.add(replaceLabel);
    } 
    UIUtil.makeSpringCompactGrid(temp, 3, 2, 0, 0, 0, 0);
    
    return temp;
  }



  
  protected void createFindButtons() {
    super.createFindButtons();
    
    this.replaceButton = new JButton(SEARCH_MSG.getString("Replace"));
    makeEnterActivateButton(this.replaceButton);
    this.replaceButton.setToolTipText(MSG.getString("Replace.ToolTip"));
    this.replaceButton.setActionCommand("Replace");
    this.replaceButton.addActionListener(this.listener);
    this.replaceButton.setEnabled(false);
    
    this.replaceAllButton = new JButton(SEARCH_MSG.getString("ReplaceAll"));
    makeEnterActivateButton(this.replaceAllButton);
    this.replaceAllButton.setActionCommand("ReplaceAll");
    this.replaceAllButton.addActionListener(this.listener);
    this.replaceAllButton.setEnabled(false);
  }








  
  protected void handleRegExCheckBoxClicked() {
    super.handleRegExCheckBoxClicked();
    
    boolean b = this.regexCheckBox.isSelected();
    this.replaceCombo.setAutoCompleteEnabled(b);
  }


  
  protected void handleSearchAction(ActionEvent e) {
    String command = e.getActionCommand();
    super.handleSearchAction(e);
    if ("FindNext".equals(command) || "FindPrevious".equals(command)) {
      handleToggleButtons();
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

  
  private boolean matchesSearchFor(String text) {
    if (text == null || text.length() == 0) {
      return false;
    }
    String searchFor = this.findCombo.getSelectedString();
    if (searchFor != null && searchFor.length() > 0) {
      boolean matchCase = this.matchCaseCheckBox.isSelected();
      if (this.regexCheckBox.isSelected()) {
        Pattern pattern; int flags = 8;
        flags = RSyntaxUtilities.getPatternFlags(matchCase, flags);
        
        try {
          pattern = Pattern.compile(searchFor, flags);
        } catch (PatternSyntaxException pse) {
          pse.printStackTrace();
          return false;
        } 
        return pattern.matcher(text).matches();
      } 
      
      if (matchCase) {
        return searchFor.equals(text);
      }
      return searchFor.equalsIgnoreCase(text);
    } 
    
    return false;
  }





  
  public boolean requestFocusInWindow() {
    boolean result = super.requestFocusInWindow();
    handleToggleButtons();
    return result;
  }

  
  public void setContentAssistImage(Image image) {
    super.setContentAssistImage(image);
    this.replaceCombo.setContentAssistImage(image);
  }




  
  protected class ReplaceFindFieldListener
    extends FindToolBar.FindFieldListener
  {
    protected void handleDocumentEvent(DocumentEvent e) {
      super.handleDocumentEvent(e);
      JTextComponent findField = UIUtil.getTextComponent((JComboBox)ReplaceToolBar.this.findCombo);
      JTextComponent replaceField = UIUtil.getTextComponent((JComboBox)ReplaceToolBar.this.replaceCombo);
      if (e.getDocument().equals(findField.getDocument())) {
        ReplaceToolBar.this.handleToggleButtons();
      }
      if (e.getDocument() == replaceField.getDocument())
        ReplaceToolBar.this.getSearchContext().setReplaceWith(replaceField.getText()); 
    }
  }
}
