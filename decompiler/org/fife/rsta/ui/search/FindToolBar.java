package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ui.AssistanceIconPanel;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rtextarea.SearchContext;





















public class FindToolBar
  extends JPanel
{
  private SearchContext context;
  protected ToolBarListener listener;
  protected FindFieldListener findFieldListener;
  protected SearchComboBox findCombo;
  protected SearchComboBox replaceCombo;
  protected JButton findButton;
  protected JButton findPrevButton;
  protected JCheckBox matchCaseCheckBox;
  protected JCheckBox wholeWordCheckBox;
  protected JCheckBox regexCheckBox;
  protected JCheckBox markAllCheckBox;
  protected JCheckBox wrapCheckBox;
  private JLabel infoLabel;
  private Timer markAllTimer;
  private boolean settingFindTextFromEvent;
  protected static final ResourceBundle SEARCH_MSG = ResourceBundle.getBundle("org.fife.rsta.ui.search.Search");
  
  protected static final ResourceBundle MSG = ResourceBundle.getBundle("org.fife.rsta.ui.search.SearchToolBar");








  
  public FindToolBar(SearchListener listener) {
    setFocusCycleRoot(true);
    
    this.markAllTimer = new Timer(300, new MarkAllEventNotifier());
    this.markAllTimer.setRepeats(false);
    
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
    addSearchListener(listener);
    this.listener = new ToolBarListener();


    
    setSearchContext(new SearchContext());

    
    ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());
    
    add(Box.createHorizontalStrut(5));
    
    add(createFieldPanel());
    
    Box rest = new Box(2);
    add(rest, "After");
    
    rest.add(Box.createHorizontalStrut(5));
    rest.add(createButtonPanel());
    rest.add(Box.createHorizontalStrut(15));
    
    JLabel infoLabel = new JLabel();
    rest.add(infoLabel);
    
    rest.add(Box.createHorizontalGlue());

    
    applyComponentOrientation(orientation);
  }








  
  public void addSearchListener(SearchListener l) {
    this.listenerList.add(SearchListener.class, l);
  }


  
  protected Container createButtonPanel() {
    Box panel = new Box(2);
    createFindButtons();


    
    JPanel filler = new JPanel(new BorderLayout());
    filler.setBorder(BorderFactory.createEmptyBorder());
    filler.add(this.findButton);
    panel.add(filler);
    panel.add(Box.createHorizontalStrut(6));
    
    this.matchCaseCheckBox = createCB("MatchCase");
    panel.add(this.matchCaseCheckBox);
    
    this.regexCheckBox = createCB("RegEx");
    panel.add(this.regexCheckBox);
    
    this.wholeWordCheckBox = createCB("WholeWord");
    panel.add(this.wholeWordCheckBox);
    
    this.markAllCheckBox = createCB("MarkAll");
    panel.add(this.markAllCheckBox);
    
    this.wrapCheckBox = createCB("Wrap");
    panel.add(this.wrapCheckBox);
    
    return panel;
  }


  
  protected JCheckBox createCB(String key) {
    JCheckBox cb = new JCheckBox(SEARCH_MSG.getString(key));
    cb.addActionListener(this.listener);
    cb.addMouseListener(this.listener);
    return cb;
  }








  
  protected Container createContentAssistablePanel(JComponent comp) {
    JPanel temp = new JPanel(new BorderLayout());
    temp.add(comp);
    AssistanceIconPanel aip = new AssistanceIconPanel(comp);
    temp.add((Component)aip, "Before");
    return temp;
  }


  
  protected Container createFieldPanel() {
    this.findFieldListener = new FindFieldListener();
    JPanel temp = new JPanel(new BorderLayout());
    
    this.findCombo = new SearchComboBox(this, false);
    JTextComponent findField = UIUtil.getTextComponent((JComboBox)this.findCombo);
    this.findFieldListener.install(findField);
    temp.add(createContentAssistablePanel((JComponent)this.findCombo));
    
    return temp;
  }





  
  protected void createFindButtons() {
    this.findPrevButton = new JButton(MSG.getString("FindPrev"));
    makeEnterActivateButton(this.findPrevButton);
    this.findPrevButton.setActionCommand("FindPrevious");
    this.findPrevButton.addActionListener(this.listener);
    this.findPrevButton.setEnabled(false);
    
    this.findButton = new JButton(SEARCH_MSG.getString("Find"))
      {
        public Dimension getPreferredSize() {
          return FindToolBar.this.findPrevButton.getPreferredSize();
        }
      };
    makeEnterActivateButton(this.findButton);
    this.findButton.setToolTipText(MSG.getString("Find.ToolTip"));
    this.findButton.setActionCommand("FindNext");
    this.findButton.addActionListener(this.listener);
    this.findButton.setEnabled(false);
  }







  
  protected void doMarkAll(boolean delay) {
    if (this.context.getMarkAll() && !this.settingFindTextFromEvent) {
      if (delay) {
        this.markAllTimer.restart();
      } else {
        
        fireMarkAllEvent();
      } 
    }
  }

  
  void doSearch(boolean forward) {
    if (forward) {
      this.findButton.doClick(0);
    } else {
      
      this.findPrevButton.doClick(0);
    } 
  }




  
  private void fireMarkAllEvent() {
    SearchEvent se = new SearchEvent(this, SearchEvent.Type.MARK_ALL, this.context);
    
    fireSearchEvent(se);
  }












  
  protected void fireSearchEvent(SearchEvent e) {
    SearchListener[] listeners = this.listenerList.<SearchListener>getListeners(SearchListener.class);
    int count = (listeners == null) ? 0 : listeners.length;
    for (int i = count - 1; i >= 0; i--) {
      listeners[i].searchEvent(e);
    }
  }

  
  protected String getFindText() {
    return UIUtil.getTextComponent((JComboBox)this.findCombo).getText();
  }








  
  public int getMarkAllDelay() {
    return this.markAllTimer.getInitialDelay();
  }

  
  protected String getReplaceText() {
    if (this.replaceCombo == null) {
      return null;
    }
    return UIUtil.getTextComponent((JComboBox)this.replaceCombo).getText();
  }







  
  public SearchContext getSearchContext() {
    return this.context;
  }






  
  protected void handleRegExCheckBoxClicked() {
    handleToggleButtons();
    
    boolean b = this.regexCheckBox.isSelected();
    this.findCombo.setAutoCompleteEnabled(b);
  }







  
  protected void handleSearchAction(ActionEvent e) {
    SearchEvent.Type type = null;
    boolean forward = true;
    String action = e.getActionCommand();
    
    int allowedModifiers = 195;


    
    if ("FindNext".equals(action)) {
      type = SearchEvent.Type.FIND;
      int mods = e.getModifiers();
      forward = ((mods & allowedModifiers) == 0);
      
      JTextComponent tc = UIUtil.getTextComponent((JComboBox)this.findCombo);
      this.findCombo.addItem(tc.getText());
    }
    else if ("FindPrevious".equals(action)) {
      type = SearchEvent.Type.FIND;
      forward = false;
      
      JTextComponent tc = UIUtil.getTextComponent((JComboBox)this.findCombo);
      this.findCombo.addItem(tc.getText());
    }
    else if ("Replace".equals(action)) {
      type = SearchEvent.Type.REPLACE;
      int mods = e.getModifiers();
      forward = ((mods & allowedModifiers) == 0);
      
      JTextComponent tc = UIUtil.getTextComponent((JComboBox)this.findCombo);
      this.findCombo.addItem(tc.getText());
      tc = UIUtil.getTextComponent((JComboBox)this.replaceCombo);
      this.replaceCombo.addItem(tc.getText());
    }
    else if ("ReplaceAll".equals(action)) {
      type = SearchEvent.Type.REPLACE_ALL;
      
      JTextComponent tc = UIUtil.getTextComponent((JComboBox)this.findCombo);
      this.findCombo.addItem(tc.getText());
      tc = UIUtil.getTextComponent((JComboBox)this.replaceCombo);
      this.replaceCombo.addItem(tc.getText());
    } 
    
    this.context.setSearchFor(getFindText());
    if (this.replaceCombo != null) {
      this.context.setReplaceWith(this.replaceCombo.getSelectedString());
    }




    
    SearchContext contextToFire = this.context;
    if (forward != this.context.getSearchForward()) {
      contextToFire = this.context.clone();
      contextToFire.setSearchForward(forward);
    } 
    
    SearchEvent se = new SearchEvent(this, type, contextToFire);
    fireSearchEvent(se);
    handleToggleButtons();
  }











  
  protected FindReplaceButtonsEnableResult handleToggleButtons() {
    FindReplaceButtonsEnableResult result = new FindReplaceButtonsEnableResult(true, null);

    
    String text = getFindText();
    if (text.length() == 0) {
      result = new FindReplaceButtonsEnableResult(false, null);
    }
    else if (this.regexCheckBox.isSelected()) {
      try {
        Pattern.compile(text);
      } catch (PatternSyntaxException pse) {
        
        result = new FindReplaceButtonsEnableResult(false, pse.getMessage());
      } 
    } 
    
    boolean enable = result.getEnable();
    this.findButton.setEnabled(enable);
    this.findPrevButton.setEnabled(enable);



    
    JTextComponent tc = UIUtil.getTextComponent((JComboBox)this.findCombo);
    tc.setForeground(enable ? UIManager.getColor("TextField.foreground") : 
        UIUtil.getErrorTextForeground());
    
    String tooltip = SearchUtil.getToolTip(result);
    tc.setToolTipText(tooltip);
    
    return result;
  }







  
  private void initUIFromContext() {
    if (this.findCombo == null) {
      return;
    }
    setFindText(this.context.getSearchFor());
    if (this.replaceCombo != null) {
      setReplaceText(this.context.getReplaceWith());
    }
    this.matchCaseCheckBox.setSelected(this.context.getMatchCase());
    this.wholeWordCheckBox.setSelected(this.context.getWholeWord());
    this.regexCheckBox.setSelected(this.context.isRegularExpression());
    this.markAllCheckBox.setSelected(this.context.getMarkAll());
    this.wrapCheckBox.setSelected(this.context.getSearchWrap());
  }













  
  protected void makeEnterActivateButton(JButton button) {
    InputMap im = button.getInputMap();

    
    im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
    im.put(KeyStroke.getKeyStroke("released ENTER"), "released");





    
    im.put(KeyStroke.getKeyStroke(10, 1, false), "pressed");
    
    im.put(KeyStroke.getKeyStroke(10, 1, true), "released");
  }









  
  public void removeSearchListener(SearchListener l) {
    this.listenerList.remove(SearchListener.class, l);
  }






  
  public boolean requestFocusInWindow() {
    JTextComponent findField = UIUtil.getTextComponent((JComboBox)this.findCombo);
    findField.selectAll();
    return findField.requestFocusInWindow();
  }







  
  void searchComboUpdateUICallback(SearchComboBox combo) {
    this.findFieldListener.install(UIUtil.getTextComponent((JComboBox)combo));
  }









  
  public void setContentAssistImage(Image image) {
    this.findCombo.setContentAssistImage(image);
  }

  
  protected void setFindText(String text) {
    UIUtil.getTextComponent((JComboBox)this.findCombo).setText(text);
  }









  
  public void setMarkAllDelay(int millis) {
    this.markAllTimer.setInitialDelay(millis);
  }

  
  protected void setReplaceText(String text) {
    if (this.replaceCombo != null) {
      UIUtil.getTextComponent((JComboBox)this.replaceCombo).setText(text);
    }
  }










  
  public void setSearchContext(SearchContext context) {
    if (this.context != null) {
      this.context.removePropertyChangeListener(this.listener);
    }
    this.context = context;
    this.context.addPropertyChangeListener(this.listener);
    initUIFromContext();
  }


  
  private class ToolBarListener
    extends MouseAdapter
    implements ActionListener, PropertyChangeListener
  {
    private ToolBarListener() {}


    
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      
      if (source == FindToolBar.this.matchCaseCheckBox) {
        FindToolBar.this.context.setMatchCase(FindToolBar.this.matchCaseCheckBox.isSelected());
        if (FindToolBar.this.markAllCheckBox.isSelected()) {
          FindToolBar.this.doMarkAll(false);
        }
      }
      else if (source == FindToolBar.this.wholeWordCheckBox) {
        FindToolBar.this.context.setWholeWord(FindToolBar.this.wholeWordCheckBox.isSelected());
        if (FindToolBar.this.markAllCheckBox.isSelected()) {
          FindToolBar.this.doMarkAll(false);
        }
      }
      else if (source == FindToolBar.this.regexCheckBox) {
        FindToolBar.this.context.setRegularExpression(FindToolBar.this.regexCheckBox.isSelected());
        if (FindToolBar.this.markAllCheckBox.isSelected()) {
          FindToolBar.this.doMarkAll(false);
        }
      }
      else if (source == FindToolBar.this.markAllCheckBox) {
        FindToolBar.this.context.setMarkAll(FindToolBar.this.markAllCheckBox.isSelected());
        FindToolBar.this.fireMarkAllEvent();
      }
      else if (source == FindToolBar.this.wrapCheckBox) {
        FindToolBar.this.context.setSearchWrap(FindToolBar.this.wrapCheckBox.isSelected());
      } else {
        
        FindToolBar.this.handleSearchAction(e);
      } 
    }


    
    public void mouseClicked(MouseEvent e) {
      if (e.getSource() instanceof JCheckBox) {
        FindToolBar.this.findFieldListener.selectAll = false;
        FindToolBar.this.findCombo.requestFocusInWindow();
      } 
    }



    
    public void propertyChange(PropertyChangeEvent e) {
      String prop = e.getPropertyName();
      
      if ("Search.MatchCase".equals(prop)) {
        boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
        FindToolBar.this.matchCaseCheckBox.setSelected(newValue);
      }
      else if ("Search.MatchWholeWord".equals(prop)) {
        boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
        FindToolBar.this.wholeWordCheckBox.setSelected(newValue);







      
      }
      else if ("Search.UseRegex".equals(prop)) {
        boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
        FindToolBar.this.regexCheckBox.setSelected(newValue);
        FindToolBar.this.handleRegExCheckBoxClicked();
      }
      else if ("Search.MarkAll".equals(prop)) {
        boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
        FindToolBar.this.markAllCheckBox.setSelected(newValue);

      
      }
      else if ("Search.searchFor".equals(prop)) {
        String newValue = (String)e.getNewValue();
        String oldValue = FindToolBar.this.getFindText();
        
        if (!newValue.equals(oldValue)) {
          FindToolBar.this.settingFindTextFromEvent = true;
          FindToolBar.this.setFindText(newValue);
          FindToolBar.this.settingFindTextFromEvent = false;
        }
      
      } else if ("Search.replaceWith".equals(prop)) {
        String newValue = (String)e.getNewValue();
        String oldValue = FindToolBar.this.getReplaceText();
        
        if (!newValue.equals(oldValue)) {
          FindToolBar.this.settingFindTextFromEvent = true;
          FindToolBar.this.setReplaceText(newValue);
          FindToolBar.this.settingFindTextFromEvent = false;
        }
      
      } else if ("Search.Wrap".equals(prop)) {
        boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
        FindToolBar.this.wrapCheckBox.setSelected(newValue);
      } 
    }
  }



  
  protected class FindFieldListener
    extends KeyAdapter
    implements DocumentListener, FocusListener
  {
    protected boolean selectAll;



    
    public void changedUpdate(DocumentEvent e) {}



    
    public void focusGained(FocusEvent e) {
      JTextField field = (JTextField)e.getComponent();
      if (this.selectAll) {
        field.selectAll();
      }
      this.selectAll = true;
    }

    
    public void focusLost(FocusEvent e) {}

    
    protected void handleDocumentEvent(DocumentEvent e) {
      FindToolBar.this.handleToggleButtons();
      if (!FindToolBar.this.settingFindTextFromEvent) {
        JTextComponent findField = UIUtil.getTextComponent((JComboBox)FindToolBar.this.findCombo);
        if (e.getDocument() == findField.getDocument()) {
          FindToolBar.this.context.setSearchFor(findField.getText());
          if (FindToolBar.this.context.getMarkAll()) {
            FindToolBar.this.doMarkAll(true);
          }
        } else {
          
          JTextComponent replaceField = UIUtil.getTextComponent((JComboBox)FindToolBar.this.replaceCombo);
          
          FindToolBar.this.context.setReplaceWith(replaceField.getText());
        } 
      } 
    }


    
    public void insertUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }
    
    public void install(JTextComponent field) {
      field.getDocument().addDocumentListener(this);
      field.addKeyListener(this);
      field.addFocusListener(this);
    }

    
    public void keyTyped(KeyEvent e) {
      if (e.getKeyChar() == '\n') {
        int mod = e.getModifiers();
        int ctrlShift = 3;
        boolean forward = ((mod & ctrlShift) == 0);
        FindToolBar.this.doSearch(forward);
      } 
    }

    
    public void removeUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }
  }



  
  private class MarkAllEventNotifier
    implements ActionListener
  {
    private MarkAllEventNotifier() {}


    
    public void actionPerformed(ActionEvent e) {
      FindToolBar.this.fireMarkAllEvent();
    }
  }
}
