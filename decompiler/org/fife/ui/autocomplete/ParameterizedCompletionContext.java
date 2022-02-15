package org.fife.ui.autocomplete;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.ChangeableHighlightPainter;


































































































class ParameterizedCompletionContext
{
  private Window parentWindow;
  private AutoCompletion ac;
  private ParameterizedCompletion pc;
  private boolean active;
  private ParameterizedCompletionDescriptionToolTip tip;
  private Highlighter.HighlightPainter p;
  private Highlighter.HighlightPainter endingP;
  private Highlighter.HighlightPainter paramCopyP;
  private List<Object> tags;
  private List<ParamCopyInfo> paramCopyInfos;
  private transient boolean ignoringDocumentEvents;
  private Listener listener;
  private int minPos;
  private Position maxPos;
  private Position defaultEndOffs;
  private int lastSelectedParam;
  private ParameterizedCompletionChoicesWindow paramChoicesWindow;
  private String paramPrefix;
  private Object oldTabKey;
  private Action oldTabAction;
  private Object oldShiftTabKey;
  private Action oldShiftTabAction;
  private Object oldUpKey;
  private Action oldUpAction;
  private Object oldDownKey;
  private Action oldDownAction;
  private Object oldEnterKey;
  private Action oldEnterAction;
  private Object oldEscapeKey;
  private Action oldEscapeAction;
  private Object oldClosingKey;
  private Action oldClosingAction;
  private static final String IM_KEY_TAB = "ParamCompKey.Tab";
  private static final String IM_KEY_SHIFT_TAB = "ParamCompKey.ShiftTab";
  private static final String IM_KEY_UP = "ParamCompKey.Up";
  private static final String IM_KEY_DOWN = "ParamCompKey.Down";
  private static final String IM_KEY_ESCAPE = "ParamCompKey.Escape";
  private static final String IM_KEY_ENTER = "ParamCompKey.Enter";
  private static final String IM_KEY_CLOSING = "ParamCompKey.Closing";
  
  ParameterizedCompletionContext(Window owner, AutoCompletion ac, ParameterizedCompletion pc) {
    this.parentWindow = owner;
    this.ac = ac;
    this.pc = pc;
    this.listener = new Listener();
    
    AutoCompletionStyleContext sc = AutoCompletion.getStyleContext();
    this.p = new OutlineHighlightPainter(sc.getParameterOutlineColor());
    this
      .endingP = new OutlineHighlightPainter(sc.getParameterizedCompletionCursorPositionColor());
    this.paramCopyP = (Highlighter.HighlightPainter)new ChangeableHighlightPainter(sc.getParameterCopyColor());
    this.tags = new ArrayList(1);
    this.paramCopyInfos = new ArrayList<>(1);
  }








  
  public void activate() {
    if (this.active) {
      return;
    }
    
    this.active = true;
    JTextComponent tc = this.ac.getTextComponent();
    this.lastSelectedParam = -1;
    
    if (this.pc.getShowParameterToolTip()) {
      this.tip = new ParameterizedCompletionDescriptionToolTip(this.parentWindow, this, this.ac, this.pc);
      
      try {
        int dot = tc.getCaretPosition();
        Rectangle r = tc.modelToView(dot);
        Point p = new Point(r.x, r.y);
        SwingUtilities.convertPointToScreen(p, tc);
        r.x = p.x;
        r.y = p.y;
        this.tip.setLocationRelativeTo(r);
        this.tip.setVisible(true);
      } catch (BadLocationException ble) {
        UIManager.getLookAndFeel().provideErrorFeedback(tc);
        ble.printStackTrace();
        this.tip = null;
      } 
    } 
    
    this.listener.install(tc);
    
    if (this.paramChoicesWindow == null) {
      this.paramChoicesWindow = createParamChoicesWindow();
    }
    this.lastSelectedParam = getCurrentParameterIndex();
    prepareParamChoicesWindow();
    this.paramChoicesWindow.setVisible(true);
  }







  
  private ParameterizedCompletionChoicesWindow createParamChoicesWindow() {
    ParameterizedCompletionChoicesWindow pcw = new ParameterizedCompletionChoicesWindow(this.parentWindow, this.ac, this);

    
    pcw.initialize(this.pc);
    return pcw;
  }







  
  public void deactivate() {
    if (!this.active) {
      return;
    }
    this.active = false;
    this.listener.uninstall();
    if (this.tip != null) {
      this.tip.setVisible(false);
    }
    if (this.paramChoicesWindow != null) {
      this.paramChoicesWindow.setVisible(false);
    }
  }









  
  public String getArgumentText(int offs) {
    List<Highlighter.Highlight> paramHighlights = getParameterHighlights();
    if (paramHighlights == null || paramHighlights.size() == 0) {
      return null;
    }
    for (Highlighter.Highlight h : paramHighlights) {
      if (offs >= h.getStartOffset() && offs <= h.getEndOffset()) {
        int start = h.getStartOffset() + 1;
        int len = h.getEndOffset() - start;
        JTextComponent tc = this.ac.getTextComponent();
        Document doc = tc.getDocument();
        try {
          return doc.getText(start, len);
        } catch (BadLocationException ble) {
          UIManager.getLookAndFeel().provideErrorFeedback(tc);
          ble.printStackTrace();
          return null;
        } 
      } 
    } 
    return null;
  }









  
  private Highlighter.Highlight getCurrentParameterHighlight() {
    JTextComponent tc = this.ac.getTextComponent();
    int dot = tc.getCaretPosition();
    if (dot > 0) {
      dot--;
    }
    
    List<Highlighter.Highlight> paramHighlights = getParameterHighlights();
    for (Highlighter.Highlight h : paramHighlights) {
      if (dot >= h.getStartOffset() && dot < h.getEndOffset()) {
        return h;
      }
    } 
    
    return null;
  }



  
  private int getCurrentParameterIndex() {
    JTextComponent tc = this.ac.getTextComponent();
    int dot = tc.getCaretPosition();
    if (dot > 0) {
      dot--;
    }
    
    List<Highlighter.Highlight> paramHighlights = getParameterHighlights();
    for (int i = 0; i < paramHighlights.size(); i++) {
      Highlighter.Highlight h = paramHighlights.get(i);
      if (dot >= h.getStartOffset() && dot < h.getEndOffset()) {
        return i;
      }
    } 
    
    return -1;
  }









  
  private int getCurrentParameterStartOffset() {
    Highlighter.Highlight h = getCurrentParameterHighlight();
    return (h != null) ? (h.getStartOffset() + 1) : -1;
  }











  
  private static int getFirstHighlight(List<Highlighter.Highlight> highlights) {
    int first = -1;
    Highlighter.Highlight firstH = null;
    for (int i = 0; i < highlights.size(); i++) {
      Highlighter.Highlight h = highlights.get(i);
      if (firstH == null || h.getStartOffset() < firstH.getStartOffset()) {
        firstH = h;
        first = i;
      } 
    } 
    return first;
  }











  
  private static int getLastHighlight(List<Highlighter.Highlight> highlights) {
    int last = -1;
    Highlighter.Highlight lastH = null;
    for (int i = highlights.size() - 1; i >= 0; i--) {
      Highlighter.Highlight h = highlights.get(i);
      if (lastH == null || h.getStartOffset() > lastH.getStartOffset()) {
        lastH = h;
        last = i;
      } 
    } 
    return last;
  }

  
  public List<Highlighter.Highlight> getParameterHighlights() {
    List<Highlighter.Highlight> paramHighlights = new ArrayList<>(2);
    JTextComponent tc = this.ac.getTextComponent();
    Highlighter.Highlight[] highlights = tc.getHighlighter().getHighlights();
    for (Highlighter.Highlight highlight : highlights) {
      Highlighter.HighlightPainter painter = highlight.getPainter();
      if (painter == this.p || painter == this.endingP) {
        paramHighlights.add(highlight);
      }
    } 
    return paramHighlights;
  }







  
  boolean insertSelectedChoice() {
    if (this.paramChoicesWindow != null && this.paramChoicesWindow.isVisible()) {
      String choice = this.paramChoicesWindow.getSelectedChoice();
      if (choice != null) {
        JTextComponent tc = this.ac.getTextComponent();
        Highlighter.Highlight h = getCurrentParameterHighlight();
        if (h != null) {
          
          tc.setSelectionStart(h.getStartOffset() + 1);
          tc.setSelectionEnd(h.getEndOffset());
          tc.replaceSelection(choice);
          moveToNextParam();
        } else {
          
          UIManager.getLookAndFeel().provideErrorFeedback(tc);
        } 
        return true;
      } 
    } 
    return false;
  }








  
  private void installKeyBindings() {
    if (AutoCompletion.getDebug()) {
      System.out.println("CompletionContext: Installing keybindings");
    }
    
    JTextComponent tc = this.ac.getTextComponent();
    InputMap im = tc.getInputMap();
    ActionMap am = tc.getActionMap();
    
    KeyStroke ks = KeyStroke.getKeyStroke(9, 0);
    this.oldTabKey = im.get(ks);
    im.put(ks, "ParamCompKey.Tab");
    this.oldTabAction = am.get("ParamCompKey.Tab");
    am.put("ParamCompKey.Tab", new NextParamAction());
    
    ks = KeyStroke.getKeyStroke(9, 1);
    this.oldShiftTabKey = im.get(ks);
    im.put(ks, "ParamCompKey.ShiftTab");
    this.oldShiftTabAction = am.get("ParamCompKey.ShiftTab");
    am.put("ParamCompKey.ShiftTab", new PrevParamAction());
    
    ks = KeyStroke.getKeyStroke(38, 0);
    this.oldUpKey = im.get(ks);
    im.put(ks, "ParamCompKey.Up");
    this.oldUpAction = am.get("ParamCompKey.Up");
    am.put("ParamCompKey.Up", new NextChoiceAction(-1, this.oldUpAction));
    
    ks = KeyStroke.getKeyStroke(40, 0);
    this.oldDownKey = im.get(ks);
    im.put(ks, "ParamCompKey.Down");
    this.oldDownAction = am.get("ParamCompKey.Down");
    am.put("ParamCompKey.Down", new NextChoiceAction(1, this.oldDownAction));
    
    ks = KeyStroke.getKeyStroke(10, 0);
    this.oldEnterKey = im.get(ks);
    im.put(ks, "ParamCompKey.Enter");
    this.oldEnterAction = am.get("ParamCompKey.Enter");
    am.put("ParamCompKey.Enter", new GotoEndAction());
    
    ks = KeyStroke.getKeyStroke(27, 0);
    this.oldEscapeKey = im.get(ks);
    im.put(ks, "ParamCompKey.Escape");
    this.oldEscapeAction = am.get("ParamCompKey.Escape");
    am.put("ParamCompKey.Escape", new HideAction());
    
    char end = this.pc.getProvider().getParameterListEnd();
    ks = KeyStroke.getKeyStroke(end);
    this.oldClosingKey = im.get(ks);
    im.put(ks, "ParamCompKey.Closing");
    this.oldClosingAction = am.get("ParamCompKey.Closing");
    am.put("ParamCompKey.Closing", new ClosingAction());
  }








  
  private void moveToNextParam() {
    JTextComponent tc = this.ac.getTextComponent();
    int dot = tc.getCaretPosition();
    int tagCount = this.tags.size();
    if (tagCount == 0) {
      tc.setCaretPosition(this.maxPos.getOffset());
      deactivate();
    } 
    
    Highlighter.Highlight currentNext = null;
    int pos = -1;
    List<Highlighter.Highlight> highlights = getParameterHighlights();
    for (int i = 0; i < highlights.size(); i++) {
      Highlighter.Highlight hl = highlights.get(i);


      
      if (currentNext == null || currentNext.getStartOffset() < dot || (hl
        .getStartOffset() > dot && hl
        .getStartOffset() <= currentNext.getStartOffset())) {
        currentNext = hl;
        pos = i;
      } 
    } 

    
    if (currentNext.getStartOffset() + 1 <= dot) {
      int nextIndex = getFirstHighlight(highlights);
      currentNext = highlights.get(nextIndex);
      pos = 0;
    } 

    
    tc.setSelectionStart(currentNext.getStartOffset() + 1);
    tc.setSelectionEnd(currentNext.getEndOffset());
    updateToolTipText(pos);
  }








  
  private void moveToPreviousParam() {
    JTextComponent tc = this.ac.getTextComponent();
    
    int tagCount = this.tags.size();
    if (tagCount == 0) {
      tc.setCaretPosition(this.maxPos.getOffset());
      deactivate();
    } 
    
    int dot = tc.getCaretPosition();
    int selStart = tc.getSelectionStart() - 1;
    Highlighter.Highlight currentPrev = null;
    int pos = 0;
    List<Highlighter.Highlight> highlights = getParameterHighlights();
    
    for (int i = 0; i < highlights.size(); i++) {
      Highlighter.Highlight h = highlights.get(i);
      if (currentPrev == null || currentPrev.getStartOffset() >= dot || (h
        .getStartOffset() < selStart && (h
        .getStartOffset() > currentPrev.getStartOffset() || pos == this.lastSelectedParam))) {
        
        currentPrev = h;
        pos = i;
      } 
    } 

    
    int firstIndex = getFirstHighlight(highlights);
    
    if (pos == firstIndex && this.lastSelectedParam == firstIndex && highlights.size() > 1) {
      pos = getLastHighlight(highlights);
      currentPrev = highlights.get(pos);
      
      tc.setSelectionStart(currentPrev.getStartOffset() + 1);
      tc.setSelectionEnd(currentPrev.getEndOffset());
      updateToolTipText(pos);
    }
    else if (currentPrev != null && dot > currentPrev.getStartOffset()) {
      
      tc.setSelectionStart(currentPrev.getStartOffset() + 1);
      tc.setSelectionEnd(currentPrev.getEndOffset());
      updateToolTipText(pos);
    } else {
      
      tc.setCaretPosition(this.maxPos.getOffset());
      deactivate();
    } 
  }



  
  private void possiblyUpdateParamCopies(Document doc) {
    int index = getCurrentParameterIndex();
    
    if (index > -1 && index < this.pc.getParamCount()) {

      
      ParameterizedCompletion.Parameter param = this.pc.getParam(index);
      if (param.isEndParam()) {
        deactivate();
        
        return;
      } 
      
      List<Highlighter.Highlight> paramHighlights = getParameterHighlights();
      Highlighter.Highlight h = paramHighlights.get(index);
      int start = h.getStartOffset() + 1;
      int len = h.getEndOffset() - start;
      String replacement = null;
      try {
        replacement = doc.getText(start, len);
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 


      
      for (ParamCopyInfo pci : this.paramCopyInfos) {
        if (pci.paramName.equals(param.getName())) {
          pci.h = replaceHighlightedText(doc, pci.h, replacement);
        }
      }
    
    }
    else {
      
      deactivate();
    } 
  }







  
  private void prepareParamChoicesWindow() {
    if (this.paramChoicesWindow != null) {
      
      int offs = getCurrentParameterStartOffset();
      if (offs == -1) {
        this.paramChoicesWindow.setVisible(false);
        
        return;
      } 
      JTextComponent tc = this.ac.getTextComponent();
      try {
        Rectangle r = tc.modelToView(offs);
        Point p = new Point(r.x, r.y);
        SwingUtilities.convertPointToScreen(p, tc);
        r.x = p.x;
        r.y = p.y;
        this.paramChoicesWindow.setLocationRelativeTo(r);
      } catch (BadLocationException ble) {
        UIManager.getLookAndFeel().provideErrorFeedback(tc);
        ble.printStackTrace();
      } 

      
      this.paramChoicesWindow.setParameter(this.lastSelectedParam, this.paramPrefix);
    } 
  }






  
  private void removeParameterHighlights() {
    JTextComponent tc = this.ac.getTextComponent();
    Highlighter h = tc.getHighlighter();
    for (Object tag : this.tags) {
      h.removeHighlight(tag);
    }
    this.tags.clear();
    for (ParamCopyInfo pci : this.paramCopyInfos) {
      h.removeHighlight(pci.h);
    }
    this.paramCopyInfos.clear();
  }














  
  private Highlighter.Highlight replaceHighlightedText(Document doc, Highlighter.Highlight h, String replacement) {
    try {
      int start = h.getStartOffset();
      int len = h.getEndOffset() - start;
      Highlighter highlighter = this.ac.getTextComponent().getHighlighter();
      highlighter.removeHighlight(h);
      
      if (doc instanceof AbstractDocument) {
        ((AbstractDocument)doc).replace(start, len, replacement, null);
      } else {
        
        doc.remove(start, len);
        doc.insertString(start, replacement, null);
      } 
      
      int newEnd = start + replacement.length();
      h = (Highlighter.Highlight)highlighter.addHighlight(start, newEnd, this.paramCopyP);
      return h;
    }
    catch (BadLocationException ble) {
      ble.printStackTrace();

      
      return null;
    } 
  }







  
  private void uninstallKeyBindings() {
    if (AutoCompletion.getDebug()) {
      System.out.println("CompletionContext Uninstalling keybindings");
    }
    
    JTextComponent tc = this.ac.getTextComponent();
    InputMap im = tc.getInputMap();
    ActionMap am = tc.getActionMap();
    
    KeyStroke ks = KeyStroke.getKeyStroke(9, 0);
    im.put(ks, this.oldTabKey);
    am.put("ParamCompKey.Tab", this.oldTabAction);
    
    ks = KeyStroke.getKeyStroke(9, 1);
    im.put(ks, this.oldShiftTabKey);
    am.put("ParamCompKey.ShiftTab", this.oldShiftTabAction);
    
    ks = KeyStroke.getKeyStroke(38, 0);
    im.put(ks, this.oldUpKey);
    am.put("ParamCompKey.Up", this.oldUpAction);
    
    ks = KeyStroke.getKeyStroke(40, 0);
    im.put(ks, this.oldDownKey);
    am.put("ParamCompKey.Down", this.oldDownAction);
    
    ks = KeyStroke.getKeyStroke(10, 0);
    im.put(ks, this.oldEnterKey);
    am.put("ParamCompKey.Enter", this.oldEnterAction);
    
    ks = KeyStroke.getKeyStroke(27, 0);
    im.put(ks, this.oldEscapeKey);
    am.put("ParamCompKey.Escape", this.oldEscapeAction);
    
    char end = this.pc.getProvider().getParameterListEnd();
    ks = KeyStroke.getKeyStroke(end);
    im.put(ks, this.oldClosingKey);
    am.put("ParamCompKey.Closing", this.oldClosingAction);
  }










  
  private String updateToolTipText() {
    JTextComponent tc = this.ac.getTextComponent();
    int dot = tc.getSelectionStart();
    int mark = tc.getSelectionEnd();
    int index = -1;
    String paramPrefix = null;
    
    List<Highlighter.Highlight> paramHighlights = getParameterHighlights();
    for (int i = 0; i < paramHighlights.size(); i++) {
      Highlighter.Highlight h = paramHighlights.get(i);
      
      int start = h.getStartOffset() + 1;
      if (dot >= start && dot <= h.getEndOffset()) {

        
        try {
          if (dot != start || mark != h.getEndOffset()) {
            paramPrefix = tc.getText(start, dot - start);
          }
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        } 
        index = i;
        
        break;
      } 
    } 
    updateToolTipText(index);
    return paramPrefix;
  }


  
  private void updateToolTipText(int selectedParam) {
    if (selectedParam != this.lastSelectedParam) {
      if (this.tip != null) {
        this.tip.updateText(selectedParam);
      }
      this.lastSelectedParam = selectedParam;
    } 
  }





  
  public void updateUI() {
    if (this.tip != null) {
      this.tip.updateUI();
    }
    if (this.paramChoicesWindow != null) {
      this.paramChoicesWindow.updateUI();
    }
  }



  
  private class GotoEndAction
    extends AbstractAction
  {
    private GotoEndAction() {}


    
    public void actionPerformed(ActionEvent e) {
      if (ParameterizedCompletionContext.this.paramChoicesWindow != null && ParameterizedCompletionContext.this.paramChoicesWindow.isVisible() && 
        ParameterizedCompletionContext.this.insertSelectedChoice()) {
        return;
      }


      
      ParameterizedCompletionContext.this.deactivate();
      JTextComponent tc = ParameterizedCompletionContext.this.ac.getTextComponent();
      int dot = tc.getCaretPosition();
      if (dot != ParameterizedCompletionContext.this.defaultEndOffs.getOffset()) {
        tc.setCaretPosition(ParameterizedCompletionContext.this.defaultEndOffs.getOffset());
      }
      else {
        
        Action a = getDefaultEnterAction(tc);
        if (a != null) {
          a.actionPerformed(e);
        } else {
          
          tc.replaceSelection("\n");
        } 
      } 
    }

    
    private Action getDefaultEnterAction(JTextComponent tc) {
      ActionMap am = tc.getActionMap();
      return am.get("insert-break");
    }
  }



  
  private class ClosingAction
    extends AbstractAction
  {
    private ClosingAction() {}


    
    public void actionPerformed(ActionEvent e) {
      JTextComponent tc = ParameterizedCompletionContext.this.ac.getTextComponent();
      int dot = tc.getCaretPosition();
      char end = ParameterizedCompletionContext.this.pc.getProvider().getParameterListEnd();

      
      if (dot >= ParameterizedCompletionContext.this.maxPos.getOffset() - 2) {


        
        String text = ParameterizedCompletionContext.this.getArgumentText(dot);
        if (text != null) {
          char start = ParameterizedCompletionContext.this.pc.getProvider().getParameterListStart();
          int startCount = getCount(text, start);
          int endCount = getCount(text, end);
          if (startCount > endCount) {
            tc.replaceSelection(Character.toString(end));
            
            return;
          } 
        } 
        tc.setCaretPosition(Math.min(tc.getCaretPosition() + 1, tc
              .getDocument().getLength()));
        
        ParameterizedCompletionContext.this.deactivate();
      
      }
      else {

        
        tc.replaceSelection(Character.toString(end));
      } 
    }

    
    public int getCount(String text, char ch) {
      int count = 0;
      int old = 0;
      int pos;
      while ((pos = text.indexOf(ch, old)) > -1) {
        count++;
        old = pos + 1;
      } 
      
      return count;
    }
  }




  
  private class HideAction
    extends AbstractAction
  {
    private HideAction() {}



    
    public void actionPerformed(ActionEvent e) {
      if (ParameterizedCompletionContext.this.paramChoicesWindow != null && ParameterizedCompletionContext.this.paramChoicesWindow.isVisible()) {
        ParameterizedCompletionContext.this.paramChoicesWindow.setVisible(false);
        ParameterizedCompletionContext.this.paramChoicesWindow = null;
      } else {
        
        ParameterizedCompletionContext.this.deactivate();
      } 
    }
  }




  
  private class Listener
    implements FocusListener, CaretListener, DocumentListener
  {
    private boolean markOccurrencesEnabled;



    
    private Listener() {}



    
    public void caretUpdate(CaretEvent e) {
      if (ParameterizedCompletionContext.this.maxPos == null) {
        ParameterizedCompletionContext.this.deactivate();
        return;
      } 
      int dot = e.getDot();
      if (dot < ParameterizedCompletionContext.this.minPos || dot > ParameterizedCompletionContext.this.maxPos.getOffset()) {
        ParameterizedCompletionContext.this.deactivate();
        return;
      } 
      ParameterizedCompletionContext.this.paramPrefix = ParameterizedCompletionContext.this.updateToolTipText();
      if (ParameterizedCompletionContext.this.active) {
        ParameterizedCompletionContext.this.prepareParamChoicesWindow();
      }
    }







    
    public void changedUpdate(DocumentEvent e) {}






    
    public void focusGained(FocusEvent e) {}






    
    public void focusLost(FocusEvent e) {
      ParameterizedCompletionContext.this.deactivate();
    }

    
    private void handleDocumentEvent(DocumentEvent e) {
      if (!ParameterizedCompletionContext.this.ignoringDocumentEvents) {
        ParameterizedCompletionContext.this.ignoringDocumentEvents = true;
        SwingUtilities.invokeLater(() -> {
              ParameterizedCompletionContext.this.possiblyUpdateParamCopies(param1DocumentEvent.getDocument());
              ParameterizedCompletionContext.this.ignoringDocumentEvents = false;
            });
      } 
    }


    
    public void insertUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }








    
    public void install(JTextComponent tc) {
      boolean replaceTabs = false;
      if (tc instanceof RSyntaxTextArea) {
        RSyntaxTextArea textArea = (RSyntaxTextArea)tc;
        this.markOccurrencesEnabled = textArea.getMarkOccurrences();
        textArea.setMarkOccurrences(false);
        replaceTabs = textArea.getTabsEmulated();
      } 
      
      Highlighter h = tc.getHighlighter();



      
      try {
        ParameterizedCompletionInsertionInfo info = ParameterizedCompletionContext.this.pc.getInsertionInfo(tc, replaceTabs);
        tc.replaceSelection(info.getTextToInsert());

        
        int replacementCount = info.getReplacementCount(); int i;
        for (i = 0; i < replacementCount; i++) {
          DocumentRange dr = info.getReplacementLocation(i);
          Highlighter.HighlightPainter painter = (i < replacementCount - 1) ? ParameterizedCompletionContext.this.p : ParameterizedCompletionContext.this.endingP;
          
          ParameterizedCompletionContext.this.tags.add(h.addHighlight(dr
                .getStartOffset() - 1, dr.getEndOffset(), painter));
        } 
        for (i = 0; i < info.getReplacementCopyCount(); i++) {
          ParameterizedCompletionInsertionInfo.ReplacementCopy rc = info.getReplacementCopy(i);
          ParameterizedCompletionContext.this.paramCopyInfos.add(new ParameterizedCompletionContext.ParamCopyInfo(rc.getId(), (Highlighter.Highlight)h
                .addHighlight(rc.getStart(), rc.getEnd(), ParameterizedCompletionContext.this
                  .paramCopyP)));
        } 

        
        tc.setCaretPosition(info.getSelectionStart());
        if (info.hasSelection()) {
          tc.moveCaretPosition(info.getSelectionEnd());
        }
        
        ParameterizedCompletionContext.this.minPos = info.getMinOffset();
        ParameterizedCompletionContext.this.maxPos = info.getMaxOffset();
        try {
          Document doc = tc.getDocument();
          if (ParameterizedCompletionContext.this.maxPos.getOffset() == 0)
          {

            
            ParameterizedCompletionContext.this.maxPos = doc.createPosition(info
                .getTextToInsert().length());
          }
          ParameterizedCompletionContext.this.defaultEndOffs = doc.createPosition(info
              .getDefaultEndOffs());
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        } 

        
        tc.getDocument().addDocumentListener(this);
      }
      catch (BadLocationException ble) {
        ble.printStackTrace();
      } 

      
      tc.addCaretListener(this);
      tc.addFocusListener(this);
      ParameterizedCompletionContext.this.installKeyBindings();
    }



    
    public void removeUpdate(DocumentEvent e) {
      handleDocumentEvent(e);
    }





    
    public void uninstall() {
      JTextComponent tc = ParameterizedCompletionContext.this.ac.getTextComponent();
      tc.removeCaretListener(this);
      tc.removeFocusListener(this);
      tc.getDocument().removeDocumentListener(this);
      ParameterizedCompletionContext.this.uninstallKeyBindings();
      
      if (this.markOccurrencesEnabled) {
        ((RSyntaxTextArea)tc).setMarkOccurrences(this.markOccurrencesEnabled);
      }

      
      ParameterizedCompletionContext.this.maxPos = null;
      ParameterizedCompletionContext.this.minPos = -1;
      ParameterizedCompletionContext.this.removeParameterHighlights();
    }
  }



  
  private class NextChoiceAction
    extends AbstractAction
  {
    private Action oldAction;

    
    private int amount;


    
    NextChoiceAction(int amount, Action oldAction) {
      this.amount = amount;
      this.oldAction = oldAction;
    }

    
    public void actionPerformed(ActionEvent e) {
      if (ParameterizedCompletionContext.this.paramChoicesWindow != null && ParameterizedCompletionContext.this.paramChoicesWindow.isVisible()) {
        ParameterizedCompletionContext.this.paramChoicesWindow.incSelection(this.amount);
      }
      else if (this.oldAction != null) {
        this.oldAction.actionPerformed(e);
      } else {
        
        ParameterizedCompletionContext.this.deactivate();
      } 
    }
  }


  
  private class NextParamAction
    extends AbstractAction
  {
    private NextParamAction() {}

    
    public void actionPerformed(ActionEvent e) {
      ParameterizedCompletionContext.this.moveToNextParam();
    }
  }


  
  private static class ParamCopyInfo
  {
    private String paramName;

    
    private Highlighter.Highlight h;

    
    ParamCopyInfo(String paramName, Highlighter.Highlight h) {
      this.paramName = paramName;
      this.h = h;
    }
  }


  
  private class PrevParamAction
    extends AbstractAction
  {
    private PrevParamAction() {}

    
    public void actionPerformed(ActionEvent e) {
      ParameterizedCompletionContext.this.moveToPreviousParam();
    }
  }
}
