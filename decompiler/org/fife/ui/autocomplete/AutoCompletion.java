package org.fife.ui.autocomplete;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;






































































































































































public class AutoCompletion
{
  private JTextComponent textComponent;
  private Window parentWindow;
  private AutoCompletePopupWindow popupWindow;
  private Dimension preferredChoicesWindowSize;
  private Dimension preferredDescWindowSize;
  private Color descWindowColor;
  private ParameterizedCompletionContext pcc;
  private CompletionProvider provider;
  private ListCellRenderer<Object> renderer;
  private ExternalURLHandler externalURLHandler;
  private static LinkRedirector linkRedirector;
  private boolean showDescWindow;
  private boolean autoCompleteEnabled;
  private boolean autoActivationEnabled;
  private boolean autoCompleteSingleChoices;
  private boolean parameterAssistanceEnabled;
  private int parameterDescriptionTruncateThreshold;
  private ListCellRenderer<Object> paramChoicesRenderer;
  private KeyStroke trigger;
  private Object oldTriggerKey;
  private Action oldTriggerAction;
  private Object oldParenKey;
  private Action oldParenAction;
  private ParentWindowListener parentWindowListener;
  private TextComponentListener textComponentListener;
  private AutoActivationListener autoActivationListener;
  private LookAndFeelChangeListener lafListener;
  private PopupWindowListener popupWindowListener;
  private EventListenerList listeners;
  private boolean hideOnNoText;
  private boolean hideOnCompletionProviderChange;
  private static final String PARAM_TRIGGER_KEY = "AutoComplete";
  private static final String PARAM_COMPLETE_KEY = "AutoCompletion.FunctionStart";
  private static final AutoCompletionStyleContext STYLE_CONTEXT = new AutoCompletionStyleContext();




  
  private static final boolean DEBUG = initDebug();







  
  public AutoCompletion(CompletionProvider provider) {
    setChoicesWindowSize(350, 200);
    setDescriptionWindowSize(350, 250);
    
    setCompletionProvider(provider);
    setTriggerKey(getDefaultTriggerKey());
    setAutoCompleteEnabled(true);
    setAutoCompleteSingleChoices(true);
    setAutoActivationEnabled(false);
    setShowDescWindow(false);
    setHideOnCompletionProviderChange(true);
    setHideOnNoText(true);
    setParameterDescriptionTruncateThreshold(300);
    this.parentWindowListener = new ParentWindowListener();
    this.textComponentListener = new TextComponentListener();
    this.autoActivationListener = new AutoActivationListener();
    this.lafListener = new LookAndFeelChangeListener();
    this.popupWindowListener = new PopupWindowListener();
    this.listeners = new EventListenerList();
  }








  
  public void addAutoCompletionListener(AutoCompletionListener l) {
    this.listeners.add(AutoCompletionListener.class, l);
  }





  
  public void doCompletion() {
    refreshPopupWindow();
  }








  
  protected void fireAutoCompletionEvent(AutoCompletionEvent.Type type) {
    Object[] listeners = this.listeners.getListenerList();
    AutoCompletionEvent e = null;


    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == AutoCompletionListener.class) {
        if (e == null) {
          e = new AutoCompletionEvent(this, type);
        }
        ((AutoCompletionListener)listeners[i + 1]).autoCompleteUpdate(e);
      } 
    } 
  }









  
  public int getAutoActivationDelay() {
    return this.autoActivationListener.timer.getDelay();
  }








  
  public boolean getAutoCompleteSingleChoices() {
    return this.autoCompleteSingleChoices;
  }






  
  public CompletionProvider getCompletionProvider() {
    return this.provider;
  }






  
  static boolean getDebug() {
    return DEBUG;
  }








  
  public static KeyStroke getDefaultTriggerKey() {
    int mask = 2;
    return KeyStroke.getKeyStroke(32, mask);
  }









  
  public ExternalURLHandler getExternalURLHandler() {
    return this.externalURLHandler;
  }

  
  int getLineOfCaret() {
    Document doc = this.textComponent.getDocument();
    Element root = doc.getDefaultRootElement();
    return root.getElementIndex(this.textComponent.getCaretPosition());
  }







  
  public static LinkRedirector getLinkRedirector() {
    return linkRedirector;
  }








  
  public ListCellRenderer getListCellRenderer() {
    return this.renderer;
  }











  
  public ListCellRenderer<Object> getParamChoicesRenderer() {
    return this.paramChoicesRenderer;
  }















  
  protected String getReplacementText(Completion c, Document doc, int start, int len) {
    return c.getReplacementText();
  }








  
  public boolean getShowDescWindow() {
    return this.showDescWindow;
  }







  
  public static AutoCompletionStyleContext getStyleContext() {
    return STYLE_CONTEXT;
  }






  
  public Color getDescWindowColor() {
    return this.descWindowColor;
  }








  
  public int getParameterDescriptionTruncateThreshold() {
    return this.parameterDescriptionTruncateThreshold;
  }








  
  public JTextComponent getTextComponent() {
    return this.textComponent;
  }







  
  ComponentOrientation getTextComponentOrientation() {
    return (this.textComponent == null) ? null : this.textComponent
      .getComponentOrientation();
  }







  
  public KeyStroke getTriggerKey() {
    return this.trigger;
  }







  
  public boolean hideChildWindows() {
    boolean res = hidePopupWindow();
    res |= hideParameterCompletionPopups();
    return res;
  }






  
  private boolean hideParameterCompletionPopups() {
    if (this.pcc != null) {
      this.pcc.deactivate();
      this.pcc = null;
      return true;
    } 
    return false;
  }






  
  protected boolean hidePopupWindow() {
    if (this.popupWindow != null && 
      this.popupWindow.isVisible()) {
      setPopupVisible(false);
      return true;
    } 
    
    return false;
  }








  
  private static boolean initDebug() {
    boolean debug;
    try {
      debug = Boolean.getBoolean("AutoCompletion.debug");
    } catch (SecurityException se) {
      debug = false;
    } 
    return debug;
  }







  
  protected final void insertCompletion(Completion c) {
    insertCompletion(c, false);
  }











  
  protected void insertCompletion(Completion c, boolean typedParamListStartChar) {
    JTextComponent textComp = getTextComponent();
    String alreadyEntered = c.getAlreadyEntered(textComp);
    hidePopupWindow();
    Caret caret = textComp.getCaret();
    
    int dot = caret.getDot();
    int len = alreadyEntered.length();
    int start = dot - len;
    String replacement = getReplacementText(c, textComp.getDocument(), start, len);

    
    caret.setDot(start);
    caret.moveDot(dot);
    textComp.replaceSelection(replacement);
    
    if (isParameterAssistanceEnabled() && c instanceof ParameterizedCompletion) {
      
      ParameterizedCompletion pc = (ParameterizedCompletion)c;
      startParameterizedCompletionAssistance(pc, typedParamListStartChar);
    } 
  }











  
  public void install(JTextComponent c) {
    if (this.textComponent != null) {
      uninstall();
    }
    
    this.textComponent = c;
    installTriggerKey(getTriggerKey());






    
    char start = this.provider.getParameterListStart();
    if (start != '\000' && start != ' ') {
      InputMap im = c.getInputMap();
      ActionMap am = c.getActionMap();
      KeyStroke ks = KeyStroke.getKeyStroke(start);
      this.oldParenKey = im.get(ks);
      im.put(ks, "AutoCompletion.FunctionStart");
      this.oldParenAction = am.get("AutoCompletion.FunctionStart");
      am.put("AutoCompletion.FunctionStart", new ParameterizedCompletionStartAction(start));
    } 

    
    this.textComponentListener.addTo(this.textComponent);
    
    this.textComponentListener.hierarchyChanged(null);
    
    if (isAutoActivationEnabled()) {
      this.autoActivationListener.addTo(this.textComponent);
    }
    
    UIManager.addPropertyChangeListener(this.lafListener);
    updateUI();
  }








  
  private void installTriggerKey(KeyStroke ks) {
    InputMap im = this.textComponent.getInputMap();
    this.oldTriggerKey = im.get(ks);
    im.put(ks, "AutoComplete");
    ActionMap am = this.textComponent.getActionMap();
    this.oldTriggerAction = am.get("AutoComplete");
    am.put("AutoComplete", createAutoCompleteAction());
  }










  
  protected Action createAutoCompleteAction() {
    return new AutoCompleteAction();
  }












  
  public boolean isAutoActivationEnabled() {
    return this.autoActivationEnabled;
  }







  
  public boolean isAutoCompleteEnabled() {
    return this.autoCompleteEnabled;
  }










  
  protected boolean isHideOnCompletionProviderChange() {
    return this.hideOnCompletionProviderChange;
  }









  
  protected boolean isHideOnNoText() {
    return this.hideOnNoText;
  }







  
  public boolean isParameterAssistanceEnabled() {
    return this.parameterAssistanceEnabled;
  }






  
  public boolean isPopupVisible() {
    return (this.popupWindow != null && this.popupWindow.isVisible());
  }













  
  protected int refreshPopupWindow() {
    String text = this.provider.getAlreadyEnteredText(this.textComponent);
    if (text == null && !isPopupVisible()) {
      return getLineOfCaret();
    }




    
    int textLen = (text == null) ? 0 : text.length();
    if (textLen == 0 && isHideOnNoText() && 
      isPopupVisible()) {
      hidePopupWindow();
      return getLineOfCaret();
    } 


    
    List<Completion> completions = this.provider.getCompletions(this.textComponent);
    int count = (completions == null) ? 0 : completions.size();
    
    if (count > 1 || (count == 1 && (isPopupVisible() || textLen == 0)) || (count == 1 && 
      !getAutoCompleteSingleChoices())) {
      
      if (this.popupWindow == null) {
        this.popupWindow = new AutoCompletePopupWindow(this.parentWindow, this);
        this.popupWindowListener.install(this.popupWindow);


        
        this.popupWindow
          .applyComponentOrientation(getTextComponentOrientation());
        if (this.renderer != null) {
          this.popupWindow.setListCellRenderer(this.renderer);
        }
        if (this.preferredChoicesWindowSize != null) {
          this.popupWindow.setSize(this.preferredChoicesWindowSize);
        }
        if (this.preferredDescWindowSize != null) {
          this.popupWindow
            .setDescriptionWindowSize(this.preferredDescWindowSize);
        }
      } 
      
      this.popupWindow.setCompletions(completions);
      
      if (!this.popupWindow.isVisible()) {
        Rectangle r;
        try {
          r = this.textComponent.modelToView(this.textComponent
              .getCaretPosition());
        } catch (BadLocationException ble) {
          ble.printStackTrace();
          return -1;
        } 
        Point p = new Point(r.x, r.y);
        SwingUtilities.convertPointToScreen(p, this.textComponent);
        r.x = p.x;
        r.y = p.y;
        this.popupWindow.setLocationRelativeTo(r);
        setPopupVisible(true);
      
      }
    
    }
    else if (count == 1) {
      SwingUtilities.invokeLater(() -> insertCompletion(paramList.get(0)));
    }
    else {
      
      hidePopupWindow();
    } 
    
    return getLineOfCaret();
  }








  
  public void removeAutoCompletionListener(AutoCompletionListener l) {
    this.listeners.remove(AutoCompletionListener.class, l);
  }








  
  public void setAutoActivationDelay(int ms) {
    ms = Math.max(0, ms);
    this.autoActivationListener.timer.stop();
    this.autoActivationListener.timer.setInitialDelay(ms);
  }









  
  public void setAutoActivationEnabled(boolean enabled) {
    if (enabled != this.autoActivationEnabled) {
      this.autoActivationEnabled = enabled;
      if (this.textComponent != null) {
        if (this.autoActivationEnabled) {
          this.autoActivationListener.addTo(this.textComponent);
        } else {
          
          this.autoActivationListener.removeFrom(this.textComponent);
        } 
      }
    } 
  }







  
  public void setAutoCompleteEnabled(boolean enabled) {
    if (enabled != this.autoCompleteEnabled) {
      this.autoCompleteEnabled = enabled;
      hidePopupWindow();
    } 
  }








  
  public void setAutoCompleteSingleChoices(boolean autoComplete) {
    this.autoCompleteSingleChoices = autoComplete;
  }









  
  public void setCompletionProvider(CompletionProvider provider) {
    if (provider == null) {
      throw new IllegalArgumentException("provider cannot be null");
    }
    this.provider = provider;
    if (isHideOnCompletionProviderChange()) {
      hidePopupWindow();
    }
  }








  
  public void setChoicesWindowSize(int w, int h) {
    this.preferredChoicesWindowSize = new Dimension(w, h);
    if (this.popupWindow != null) {
      this.popupWindow.setSize(this.preferredChoicesWindowSize);
    }
  }








  
  public void setDescriptionWindowSize(int w, int h) {
    this.preferredDescWindowSize = new Dimension(w, h);
    if (this.popupWindow != null) {
      this.popupWindow.setDescriptionWindowSize(this.preferredDescWindowSize);
    }
  }





  
  public void setDescriptionWindowColor(Color c) {
    this.descWindowColor = c;
    if (this.popupWindow != null) {
      this.popupWindow.setDescriptionWindowColor(this.descWindowColor);
    }
  }












  
  public void setExternalURLHandler(ExternalURLHandler handler) {
    this.externalURLHandler = handler;
  }











  
  protected void setHideOnCompletionProviderChange(boolean hideOnCompletionProviderChange) {
    this.hideOnCompletionProviderChange = hideOnCompletionProviderChange;
  }









  
  protected void setHideOnNoText(boolean hideOnNoText) {
    this.hideOnNoText = hideOnNoText;
  }










  
  public static void setLinkRedirector(LinkRedirector linkRedirector) {
    AutoCompletion.linkRedirector = linkRedirector;
  }









  
  public void setListCellRenderer(ListCellRenderer<Object> renderer) {
    this.renderer = renderer;
    if (this.popupWindow != null) {
      this.popupWindow.setListCellRenderer(renderer);
      hidePopupWindow();
    } 
  }











  
  public void setParamChoicesRenderer(ListCellRenderer<Object> r) {
    this.paramChoicesRenderer = r;
  }











  
  public void setParameterAssistanceEnabled(boolean enabled) {
    this.parameterAssistanceEnabled = enabled;
  }








  
  protected void setPopupVisible(boolean visible) {
    if (visible != this.popupWindow.isVisible()) {
      this.popupWindow.setVisible(visible);
    }
  }








  
  public void setShowDescWindow(boolean show) {
    hidePopupWindow();
    this.showDescWindow = show;
  }









  
  public void setTriggerKey(KeyStroke ks) {
    if (ks == null) {
      throw new IllegalArgumentException("trigger key cannot be null");
    }
    if (!ks.equals(this.trigger)) {
      if (this.textComponent != null) {
        
        uninstallTriggerKey();
        
        installTriggerKey(ks);
      } 
      this.trigger = ks;
    } 
  }











  
  private void startParameterizedCompletionAssistance(ParameterizedCompletion pc, boolean typedParamListStartChar) {
    hideParameterCompletionPopups();


    
    if (pc.getParamCount() == 0 && !(pc instanceof TemplateCompletion)) {
      CompletionProvider p = pc.getProvider();
      char end = p.getParameterListEnd();
      String text = (end == '\000') ? "" : Character.toString(end);
      if (typedParamListStartChar) {
        String template = "${}" + text + "${cursor}";
        this.textComponent.replaceSelection(Character.toString(p
              .getParameterListStart()));
        TemplateCompletion tc = new TemplateCompletion(p, null, null, template);
        
        pc = tc;
      } else {
        
        text = p.getParameterListStart() + text;
        this.textComponent.replaceSelection(text);
        
        return;
      } 
    } 
    this.pcc = new ParameterizedCompletionContext(this.parentWindow, this, pc);
    this.pcc.activate();
  }









  
  public void uninstall() {
    if (this.textComponent != null) {
      
      hidePopupWindow();
      
      uninstallTriggerKey();

      
      char start = this.provider.getParameterListStart();
      if (start != '\000') {
        KeyStroke ks = KeyStroke.getKeyStroke(start);
        InputMap im = this.textComponent.getInputMap();
        im.put(ks, this.oldParenKey);
        ActionMap am = this.textComponent.getActionMap();
        am.put("AutoCompletion.FunctionStart", this.oldParenAction);
      } 
      
      this.textComponentListener.removeFrom(this.textComponent);
      if (this.parentWindow != null) {
        this.parentWindowListener.removeFrom(this.parentWindow);
      }
      
      if (isAutoActivationEnabled()) {
        this.autoActivationListener.removeFrom(this.textComponent);
      }
      
      UIManager.removePropertyChangeListener(this.lafListener);
      
      this.textComponent = null;
      this.popupWindowListener.uninstall(this.popupWindow);
      this.popupWindow = null;
    } 
  }









  
  private void uninstallTriggerKey() {
    InputMap im = this.textComponent.getInputMap();
    im.put(this.trigger, this.oldTriggerKey);
    ActionMap am = this.textComponent.getActionMap();
    am.put("AutoComplete", this.oldTriggerAction);
  }






  
  private void updateUI() {
    if (this.popupWindow != null) {
      this.popupWindow.updateUI();
    }
    if (this.pcc != null) {
      this.pcc.updateUI();
    }
    
    if (this.paramChoicesRenderer instanceof JComponent) {
      ((JComponent)this.paramChoicesRenderer).updateUI();
    }
  }








  
  public void setParameterDescriptionTruncateThreshold(int truncateThreshold) {
    this.parameterDescriptionTruncateThreshold = truncateThreshold;
  }


  
  private class AutoActivationListener
    extends FocusAdapter
    implements DocumentListener, CaretListener, ActionListener
  {
    private Timer timer;
    
    private boolean justInserted;

    
    AutoActivationListener() {
      this.timer = new Timer(200, this);
      this.timer.setRepeats(false);
    }

    
    public void actionPerformed(ActionEvent e) {
      AutoCompletion.this.doCompletion();
    }
    
    public void addTo(JTextComponent tc) {
      tc.addFocusListener(this);
      tc.getDocument().addDocumentListener(this);
      tc.addCaretListener(this);
    }

    
    public void caretUpdate(CaretEvent e) {
      if (this.justInserted) {
        this.justInserted = false;
      } else {
        
        this.timer.stop();
      } 
    }


    
    public void changedUpdate(DocumentEvent e) {}


    
    public void focusLost(FocusEvent e) {
      this.timer.stop();
    }


    
    public void insertUpdate(DocumentEvent e) {
      this.justInserted = false;
      if (AutoCompletion.this.isAutoCompleteEnabled() && AutoCompletion.this.isAutoActivationEnabled() && e
        .getLength() == 1) {
        if (AutoCompletion.this.textComponent != null && AutoCompletion.this.provider.isAutoActivateOkay(AutoCompletion.this.textComponent)) {
          this.timer.restart();
          this.justInserted = true;
        } else {
          
          this.timer.stop();
        } 
      } else {
        
        this.timer.stop();
      } 
    }
    
    public void removeFrom(JTextComponent tc) {
      tc.removeFocusListener(this);
      tc.getDocument().removeDocumentListener(this);
      tc.removeCaretListener(this);
      this.timer.stop();
      this.justInserted = false;
    }

    
    public void removeUpdate(DocumentEvent e) {
      this.timer.stop();
    }
  }





  
  protected class AutoCompleteAction
    extends AbstractAction
  {
    public void actionPerformed(ActionEvent e) {
      if (AutoCompletion.this.isAutoCompleteEnabled()) {
        AutoCompletion.this.refreshPopupWindow();
      }
      else if (AutoCompletion.this.oldTriggerAction != null) {
        AutoCompletion.this.oldTriggerAction.actionPerformed(e);
      } 
    }
  }


  
  private class LookAndFeelChangeListener
    implements PropertyChangeListener
  {
    private LookAndFeelChangeListener() {}

    
    public void propertyChange(PropertyChangeEvent e) {
      String name = e.getPropertyName();
      if ("lookAndFeel".equals(name)) {
        AutoCompletion.this.updateUI();
      }
    }
  }


  
  private class ParameterizedCompletionStartAction
    extends AbstractAction
  {
    private String start;

    
    ParameterizedCompletionStartAction(char ch) {
      this.start = Character.toString(ch);
    }



    
    public void actionPerformed(ActionEvent e) {
      boolean wasVisible = AutoCompletion.this.hidePopupWindow();

      
      if (!wasVisible || !AutoCompletion.this.isParameterAssistanceEnabled()) {
        AutoCompletion.this.textComponent.replaceSelection(this.start);
        
        return;
      } 
      Completion c = AutoCompletion.this.popupWindow.getSelection();
      if (c instanceof ParameterizedCompletion)
      {
        AutoCompletion.this.insertCompletion(c, true);
      }
    }
  }


  
  private class ParentWindowListener
    extends ComponentAdapter
    implements WindowFocusListener
  {
    private ParentWindowListener() {}

    
    public void addTo(Window w) {
      w.addComponentListener(this);
      w.addWindowFocusListener(this);
    }

    
    public void componentHidden(ComponentEvent e) {
      AutoCompletion.this.hideChildWindows();
    }

    
    public void componentMoved(ComponentEvent e) {
      AutoCompletion.this.hideChildWindows();
    }

    
    public void componentResized(ComponentEvent e) {
      AutoCompletion.this.hideChildWindows();
    }
    
    public void removeFrom(Window w) {
      w.removeComponentListener(this);
      w.removeWindowFocusListener(this);
    }


    
    public void windowGainedFocus(WindowEvent e) {}

    
    public void windowLostFocus(WindowEvent e) {
      AutoCompletion.this.hideChildWindows();
    }
  }


  
  private class PopupWindowListener
    extends ComponentAdapter
  {
    private PopupWindowListener() {}

    
    public void componentHidden(ComponentEvent e) {
      AutoCompletion.this.fireAutoCompletionEvent(AutoCompletionEvent.Type.POPUP_HIDDEN);
    }

    
    public void componentShown(ComponentEvent e) {
      AutoCompletion.this.fireAutoCompletionEvent(AutoCompletionEvent.Type.POPUP_SHOWN);
    }
    
    public void install(AutoCompletePopupWindow popupWindow) {
      popupWindow.addComponentListener(this);
    }
    
    public void uninstall(AutoCompletePopupWindow popupWindow) {
      if (popupWindow != null) {
        popupWindow.removeComponentListener(this);
      }
    }
  }

  
  private class TextComponentListener
    extends FocusAdapter
    implements HierarchyListener
  {
    private TextComponentListener() {}

    
    void addTo(JTextComponent tc) {
      tc.addFocusListener(this);
      tc.addHierarchyListener(this);
    }




    
    public void focusLost(FocusEvent e) {
      AutoCompletion.this.hideChildWindows();
    }












    
    public void hierarchyChanged(HierarchyEvent e) {
      Window oldParentWindow = AutoCompletion.this.parentWindow;
      AutoCompletion.this.parentWindow = SwingUtilities.getWindowAncestor(AutoCompletion.this.textComponent);
      if (AutoCompletion.this.parentWindow != oldParentWindow) {
        if (oldParentWindow != null) {
          AutoCompletion.this.parentWindowListener.removeFrom(oldParentWindow);
        }
        if (AutoCompletion.this.parentWindow != null) {
          AutoCompletion.this.parentWindowListener.addTo(AutoCompletion.this.parentWindow);
        }
      } 
    }

    
    public void removeFrom(JTextComponent tc) {
      tc.removeFocusListener(this);
      tc.removeHierarchyListener(this);
    }
  }
}
