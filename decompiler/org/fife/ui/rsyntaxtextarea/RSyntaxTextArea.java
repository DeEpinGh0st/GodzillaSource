package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.rsta.ui.search.SearchListenerImpl;
import org.fife.ui.rsyntaxtextarea.focusabletip.FocusableTip;
import org.fife.ui.rsyntaxtextarea.folding.DefaultFoldManager;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ToolTipInfo;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaBase;
import org.fife.ui.rtextarea.RTextAreaUI;
import org.fife.ui.rtextarea.RecordableTextAction;























































public class RSyntaxTextArea
  extends RTextArea
  implements SyntaxConstants
{
  public static final String ANIMATE_BRACKET_MATCHING_PROPERTY = "RSTA.animateBracketMatching";
  public static final String ANTIALIAS_PROPERTY = "RSTA.antiAlias";
  public static final String AUTO_INDENT_PROPERTY = "RSTA.autoIndent";
  public static final String BRACKET_MATCHING_PROPERTY = "RSTA.bracketMatching";
  public static final String CLEAR_WHITESPACE_LINES_PROPERTY = "RSTA.clearWhitespaceLines";
  public static final String CLOSE_CURLY_BRACES_PROPERTY = "RSTA.closeCurlyBraces";
  public static final String CLOSE_MARKUP_TAGS_PROPERTY = "RSTA.closeMarkupTags";
  public static final String CODE_FOLDING_PROPERTY = "RSTA.codeFolding";
  public static final String EOL_VISIBLE_PROPERTY = "RSTA.eolMarkersVisible";
  public static final String FOCUSABLE_TIPS_PROPERTY = "RSTA.focusableTips";
  public static final String FRACTIONAL_FONTMETRICS_PROPERTY = "RSTA.fractionalFontMetrics";
  public static final String HIGHLIGHT_SECONDARY_LANGUAGES_PROPERTY = "RSTA.highlightSecondaryLanguages";
  public static final String HYPERLINKS_ENABLED_PROPERTY = "RSTA.hyperlinksEnabled";
  public static final String MARK_OCCURRENCES_PROPERTY = "RSTA.markOccurrences";
  public static final String MARKED_OCCURRENCES_CHANGED_PROPERTY = "RSTA.markedOccurrencesChanged";
  public static final String PAINT_MATCHED_BRACKET_PAIR_PROPERTY = "RSTA.paintMatchedBracketPair";
  public static final String PARSER_NOTICES_PROPERTY = "RSTA.parserNotices";
  public static final String SYNTAX_SCHEME_PROPERTY = "RSTA.syntaxScheme";
  public static final String SYNTAX_STYLE_PROPERTY = "RSTA.syntaxStyle";
  public static final String TAB_LINE_COLOR_PROPERTY = "RSTA.tabLineColor";
  public static final String TAB_LINES_PROPERTY = "RSTA.tabLines";
  public static final String USE_SELECTED_TEXT_COLOR_PROPERTY = "RSTA.useSelectedTextColor";
  public static final String VISIBLE_WHITESPACE_PROPERTY = "RSTA.visibleWhitespace";
  private static final Color DEFAULT_BRACKET_MATCH_BG_COLOR = new Color(234, 234, 255);
  private static final Color DEFAULT_BRACKET_MATCH_BORDER_COLOR = new Color(0, 0, 128);
  private static final Color DEFAULT_SELECTION_COLOR = new Color(200, 200, 255);


  
  private static final String MSG = "org.fife.ui.rsyntaxtextarea.RSyntaxTextArea";


  
  private JMenu foldingMenu;


  
  private static RecordableTextAction toggleCurrentFoldAction;


  
  private static RecordableTextAction collapseAllCommentFoldsAction;


  
  private static RecordableTextAction collapseAllFoldsAction;

  
  private static RecordableTextAction expandAllFoldsAction;

  
  private String syntaxStyleKey;

  
  private SyntaxScheme syntaxScheme;

  
  private static CodeTemplateManager codeTemplateManager;

  
  private static boolean templatesEnabled;

  
  private Rectangle match;

  
  private Rectangle dotRect;

  
  private Point bracketInfo;

  
  private Color matchedBracketBGColor;

  
  private Color matchedBracketBorderColor;

  
  private int lastBracketMatchPos;

  
  private boolean bracketMatchingEnabled;

  
  private boolean animateBracketMatching;

  
  private boolean paintMatchedBracketPair;

  
  private BracketMatchingTimer bracketRepaintTimer;

  
  private MatchedBracketPopupTimer matchedBracketPopupTimer;

  
  private boolean metricsNeverRefreshed;

  
  private boolean autoIndentEnabled;

  
  private boolean closeCurlyBraces;

  
  private boolean closeMarkupTags;

  
  private boolean clearWhitespaceLines;

  
  private boolean whitespaceVisible;

  
  private boolean eolMarkersVisible;

  
  private boolean paintTabLines;

  
  private Color tabLineColor;

  
  private boolean hyperlinksEnabled;

  
  private Color hyperlinkFG;

  
  private int linkScanningMask;

  
  private boolean highlightSecondaryLanguages;

  
  private boolean useSelectedTextColor;

  
  private MarkOccurrencesSupport markOccurrencesSupport;

  
  private Color markOccurrencesColor;

  
  private int markOccurrencesDelay;

  
  private boolean paintMarkOccurrencesBorder;

  
  private FontMetrics defaultFontMetrics;

  
  private ParserManager parserManager;

  
  private String cachedTip;

  
  private Point cachedTipLoc;

  
  private boolean isScanningForLinks;

  
  private int hoveredOverLinkOffset;

  
  private LinkGenerator linkGenerator;

  
  private LinkGeneratorResult linkGeneratorResult;

  
  private int rhsCorrection;

  
  private FoldManager foldManager;

  
  private boolean useFocusableTips;

  
  private FocusableTip focusableTip;

  
  private Map<?, ?> aaHints;

  
  private TokenPainter tokenPainter;

  
  private boolean showMatchedBracketPopup;

  
  private int lineHeight;

  
  private int maxAscent;

  
  private boolean fractionalFontMetricsEnabled;

  
  private Color[] secondaryLanguageBackgrounds;


  
  public RSyntaxTextArea() {
    registerReplaceDialog();
  }






  
  public RSyntaxTextArea(RSyntaxDocument doc) {
    super((AbstractDocument)doc);
    setSyntaxEditingStyle(doc.getSyntaxStyle());
    registerReplaceDialog();
    registerGoToDialog();
  }





  
  public RSyntaxTextArea(String text) {
    super(text);
    registerReplaceDialog();
  }









  
  public RSyntaxTextArea(int rows, int cols) {
    super(rows, cols);
    registerReplaceDialog();
  }










  
  public RSyntaxTextArea(String text, int rows, int cols) {
    super(text, rows, cols);
    registerReplaceDialog();
  }











  
  public RSyntaxTextArea(RSyntaxDocument doc, String text, int rows, int cols) {
    super((AbstractDocument)doc, text, rows, cols);
    registerReplaceDialog();
  }







  
  public RSyntaxTextArea(int textMode) {
    super(textMode);
    registerReplaceDialog();
  }







  
  public void addActiveLineRangeListener(ActiveLineRangeListener l) {
    this.listenerList.add(ActiveLineRangeListener.class, l);
  }












  
  public void addHyperlinkListener(HyperlinkListener l) {
    this.listenerList.add(HyperlinkListener.class, l);
  }






  
  public void addNotify() {
    super.addNotify();




    
    if (this.metricsNeverRefreshed) {
      Window parent = SwingUtilities.getWindowAncestor((Component)this);
      if (parent != null && parent.getWidth() > 0 && parent.getHeight() > 0) {
        refreshFontMetrics(getGraphics2D(getGraphics()));
        this.metricsNeverRefreshed = false;
      } 
    } 


    
    if (this.parserManager != null) {
      this.parserManager.restartParsing();
    }
  }













  
  public void addParser(Parser parser) {
    if (this.parserManager == null) {
      this.parserManager = new ParserManager(this);
    }
    this.parserManager.addParser(parser);
  }








  
  protected void appendFoldingMenu(JPopupMenu popup) {
    popup.addSeparator();
    ResourceBundle bundle = ResourceBundle.getBundle("org.fife.ui.rsyntaxtextarea.RSyntaxTextArea");
    this.foldingMenu = new JMenu(bundle.getString("ContextMenu.Folding"));
    this.foldingMenu.add(createPopupMenuItem((Action)toggleCurrentFoldAction));
    this.foldingMenu.add(createPopupMenuItem((Action)collapseAllCommentFoldsAction));
    this.foldingMenu.add(createPopupMenuItem((Action)collapseAllFoldsAction));
    this.foldingMenu.add(createPopupMenuItem((Action)expandAllFoldsAction));
    popup.add(this.foldingMenu);
  }







  
  private void calculateLineHeight() {
    this.lineHeight = this.maxAscent = 0;

    
    for (int i = 0; i < this.syntaxScheme.getStyleCount(); i++) {
      Style ss = this.syntaxScheme.getStyle(i);
      if (ss != null && ss.font != null) {
        FontMetrics fontMetrics = getFontMetrics(ss.font);
        int j = fontMetrics.getHeight();
        if (j > this.lineHeight) {
          this.lineHeight = j;
        }
        int k = fontMetrics.getMaxAscent();
        if (k > this.maxAscent) {
          this.maxAscent = k;
        }
      } 
    } 

    
    Font temp = getFont();
    FontMetrics fm = getFontMetrics(temp);
    int height = fm.getHeight();
    if (height > this.lineHeight) {
      this.lineHeight = height;
    }
    int ascent = fm.getMaxAscent();
    if (ascent > this.maxAscent) {
      this.maxAscent = ascent;
    }
  }







  
  public void clearParsers() {
    if (this.parserManager != null) {
      this.parserManager.clearParsers();
    }
  }










  
  private TokenImpl cloneTokenList(Token t) {
    if (t == null) {
      return null;
    }
    
    TokenImpl clone = new TokenImpl(t);
    TokenImpl cloneEnd = clone;
    
    while ((t = t.getNextToken()) != null) {
      TokenImpl temp = new TokenImpl(t);
      cloneEnd.setNextToken(temp);
      cloneEnd = temp;
    } 
    
    return clone;
  }
















  
  protected void configurePopupMenu(JPopupMenu popupMenu) {
    super.configurePopupMenu(popupMenu);

    
    if (popupMenu != null && popupMenu.getComponentCount() > 0 && this.foldingMenu != null)
    {
      this.foldingMenu.setEnabled(this.foldManager
          .isCodeFoldingSupportedAndEnabled());
    }
  }













  
  public void copyAsStyledText(Theme theme) {
    if (theme == null) {
      copyAsStyledText();
      
      return;
    } 
    Theme origTheme = new Theme(this);
    
    theme.apply(this);
    try {
      copyAsStyledText();
    } finally {
      origTheme.apply(this);
    } 
  }








  
  public void copyAsStyledText() {
    int selStart = getSelectionStart();
    int selEnd = getSelectionEnd();
    if (selStart == selEnd) {
      return;
    }

    
    String html = HtmlUtil.getTextAsHtml(this, selStart, selEnd);

    
    byte[] rtfBytes = getTextAsRtf(selStart, selEnd);

    
    StyledTextTransferable contents = new StyledTextTransferable(html, rtfBytes);
    
    Clipboard cb = getToolkit().getSystemClipboard();
    try {
      cb.setContents(contents, null);
    } catch (IllegalStateException ise) {
      UIManager.getLookAndFeel().provideErrorFeedback(null);
    } 
  }







  
  protected Document createDefaultModel() {
    return (Document)new RSyntaxDocument("text/plain");
  }




  
  private HyperlinkEvent createHyperlinkEvent(HyperlinkEvent.EventType type) {
    if (type == HyperlinkEvent.EventType.EXITED) {
      return new HyperlinkEvent(this, type, null);
    }
    
    HyperlinkEvent he = null;
    
    if (this.linkGeneratorResult != null) {
      he = this.linkGeneratorResult.execute();
      this.linkGeneratorResult = null;
    } else {
      
      Token t = modelToToken(this.hoveredOverLinkOffset);
      if (t != null) {
        URL url = null;
        String desc = null;
        try {
          String temp = t.getLexeme();
          
          if (temp.startsWith("www.")) {
            temp = "http://" + temp;
          }
          url = new URL(temp);
        } catch (MalformedURLException mue) {
          desc = mue.getMessage();
        } 
        he = new HyperlinkEvent(this, type, url, desc);
      } 
    } 
    
    return he;
  }







  
  protected RTextAreaBase.RTAMouseListener createMouseListener() {
    return (RTextAreaBase.RTAMouseListener)new RSyntaxTextAreaMutableCaretEvent(this);
  }








  
  protected JPopupMenu createPopupMenu() {
    JPopupMenu popup = super.createPopupMenu();
    appendFoldingMenu(popup);
    return popup;
  }








  
  private static void createRstaPopupMenuActions() {
    ResourceBundle msg = ResourceBundle.getBundle("org.fife.ui.rsyntaxtextarea.RSyntaxTextArea");
    
    toggleCurrentFoldAction = new RSyntaxTextAreaEditorKit.ToggleCurrentFoldAction();
    
    toggleCurrentFoldAction.setProperties(msg, "Action.ToggleCurrentFold");
    
    collapseAllCommentFoldsAction = new RSyntaxTextAreaEditorKit.CollapseAllCommentFoldsAction();
    
    collapseAllCommentFoldsAction.setProperties(msg, "Action.CollapseCommentFolds");
    
    collapseAllFoldsAction = new RSyntaxTextAreaEditorKit.CollapseAllFoldsAction(true);
    expandAllFoldsAction = new RSyntaxTextAreaEditorKit.ExpandAllFoldsAction(true);
  }








  
  protected RTextAreaUI createRTextAreaUI() {
    return new RSyntaxTextAreaUI((JComponent)this);
  }








  
  protected final void doBracketMatching() {
    if (this.match != null) {
      repaint(this.match);
      if (this.dotRect != null) {
        repaint(this.dotRect);
      }
    } 

    
    int lastCaretBracketPos = (this.bracketInfo == null) ? -1 : this.bracketInfo.x;
    this.bracketInfo = RSyntaxUtilities.getMatchingBracketPosition(this, this.bracketInfo);
    
    if (this.bracketInfo.y > -1 && (this.bracketInfo.y != this.lastBracketMatchPos || this.bracketInfo.x != lastCaretBracketPos)) {

      
      try {
        this.match = modelToView(this.bracketInfo.y);
        if (this.match != null) {
          if (getPaintMatchedBracketPair()) {
            this.dotRect = modelToView(this.bracketInfo.x);
          } else {
            
            this.dotRect = null;
          } 
          if (getAnimateBracketMatching()) {
            this.bracketRepaintTimer.restart();
          }
          repaint(this.match);
          if (this.dotRect != null) {
            repaint(this.dotRect);
          }
          
          if (getShowMatchedBracketPopup()) {
            Container parent = getParent();
            if (parent instanceof javax.swing.JViewport) {
              Rectangle visibleRect = getVisibleRect();
              if ((this.match.y + this.match.height) < visibleRect.getY()) {
                if (this.matchedBracketPopupTimer == null) {
                  this.matchedBracketPopupTimer = new MatchedBracketPopupTimer();
                }
                
                this.matchedBracketPopupTimer.restart(this.bracketInfo.y);
              }
            
            } 
          } 
        } 
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      }
    
    } else if (this.bracketInfo.y == -1) {
      
      this.match = null;
      this.dotRect = null;
      this.bracketRepaintTimer.stop();
    } 
    this.lastBracketMatchPos = this.bracketInfo.y;
  }








  
  protected void fireCaretUpdate(CaretEvent e) {
    super.fireCaretUpdate(e);
    if (isBracketMatchingEnabled()) {
      doBracketMatching();
    }
  }







  
  private void fireActiveLineRangeEvent(int min, int max) {
    ActiveLineRangeEvent e = null;
    
    Object[] listeners = this.listenerList.getListenerList();

    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActiveLineRangeListener.class) {
        if (e == null) {
          e = new ActiveLineRangeEvent(this, min, max);
        }
        ((ActiveLineRangeListener)listeners[i + 1]).activeLineRangeChanged(e);
      } 
    } 
  }








  
  private void fireHyperlinkUpdate(HyperlinkEvent.EventType type) {
    HyperlinkEvent e = null;

    
    Object[] listeners = this.listenerList.getListenerList();


    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      
      if (listeners[i] == HyperlinkListener.class) {
        
        if (e == null) {
          e = createHyperlinkEvent(type);
          if (e == null) {
            return;
          }
        } 
        ((HyperlinkListener)listeners[i + 1]).hyperlinkUpdate(e);
      } 
    } 
  }





  
  void fireMarkedOccurrencesChanged() {
    firePropertyChange("RSTA.markedOccurrencesChanged", null, null);
  }






  
  void fireParserNoticesChange() {
    firePropertyChange("RSTA.parserNotices", null, null);
  }








  
  public void foldToggled(Fold fold) {
    this.match = null;
    this.dotRect = null;
    if (getLineWrap()) {




      
      SwingUtilities.invokeLater(this::possiblyUpdateCurrentLineHighlightLocation);
    } else {
      
      possiblyUpdateCurrentLineHighlightLocation();
    } 
    revalidate();
    repaint();
  }














  
  public void forceReparsing(int parser) {
    this.parserManager.forceReparsing(parser);
  }











  
  public boolean forceReparsing(Parser parser) {
    for (int i = 0; i < getParserCount(); i++) {
      if (getParser(i) == parser) {
        forceReparsing(i);
        return true;
      } 
    } 
    return false;
  }







  
  public boolean getAnimateBracketMatching() {
    return this.animateBracketMatching;
  }








  
  public boolean getAntiAliasingEnabled() {
    return (this.aaHints != null);
  }










  
  public Color getBackgroundForToken(Token token) {
    Color c = null;
    if (getHighlightSecondaryLanguages()) {
      
      int languageIndex = token.getLanguageIndex() - 1;
      if (languageIndex >= 0 && languageIndex < this.secondaryLanguageBackgrounds.length)
      {
        c = this.secondaryLanguageBackgrounds[languageIndex];
      }
    } 
    if (c == null) {
      c = (this.syntaxScheme.getStyle(token.getType())).background;
    }

    
    return c;
  }










  
  public boolean getCloseCurlyBraces() {
    return this.closeCurlyBraces;
  }









  
  public boolean getCloseMarkupTags() {
    return this.closeMarkupTags;
  }








  
  public static synchronized CodeTemplateManager getCodeTemplateManager() {
    if (codeTemplateManager == null) {
      codeTemplateManager = new CodeTemplateManager();
    }
    return codeTemplateManager;
  }







  
  public static Color getDefaultBracketMatchBGColor() {
    return DEFAULT_BRACKET_MATCH_BG_COLOR;
  }







  
  public static Color getDefaultBracketMatchBorderColor() {
    return DEFAULT_BRACKET_MATCH_BORDER_COLOR;
  }









  
  public static Color getDefaultSelectionColor() {
    return DEFAULT_SELECTION_COLOR;
  }










  
  public SyntaxScheme getDefaultSyntaxScheme() {
    return new SyntaxScheme(getFont());
  }








  
  public boolean getEOLMarkersVisible() {
    return this.eolMarkersVisible;
  }






  
  public FoldManager getFoldManager() {
    return this.foldManager;
  }








  
  public Font getFontForTokenType(int type) {
    Font f = (this.syntaxScheme.getStyle(type)).font;
    return (f != null) ? f : getFont();
  }








  
  public FontMetrics getFontMetricsForTokenType(int type) {
    FontMetrics fm = (this.syntaxScheme.getStyle(type)).fontMetrics;
    return (fm != null) ? fm : this.defaultFontMetrics;
  }









  
  public Color getForegroundForToken(Token t) {
    if (getHyperlinksEnabled() && this.hoveredOverLinkOffset == t.getOffset() && (t
      .isHyperlink() || this.linkGeneratorResult != null)) {
      return this.hyperlinkFG;
    }
    return getForegroundForTokenType(t.getType());
  }










  
  public Color getForegroundForTokenType(int type) {
    Color fg = (this.syntaxScheme.getStyle(type)).foreground;
    return (fg != null) ? fg : getForeground();
  }








  
  public boolean getFractionalFontMetricsEnabled() {
    return this.fractionalFontMetricsEnabled;
  }









  
  private Graphics2D getGraphics2D(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    if (this.aaHints != null) {
      g2d.addRenderingHints(this.aaHints);
    }
    if (this.fractionalFontMetricsEnabled) {
      g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
    
    return g2d;
  }













  
  public boolean getHighlightSecondaryLanguages() {
    return this.highlightSecondaryLanguages;
  }








  
  public Color getHyperlinkForeground() {
    return this.hyperlinkFG;
  }








  
  public boolean getHyperlinksEnabled() {
    return this.hyperlinksEnabled;
  }







  
  public int getLastVisibleOffset() {
    if (isCodeFoldingEnabled()) {
      int lastVisibleLine = this.foldManager.getLastVisibleLine();
      if (lastVisibleLine < getLineCount() - 1) {
        try {
          return getLineEndOffset(lastVisibleLine) - 1;
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        } 
      }
    } 
    return getDocument().getLength();
  }







  
  public int getLineHeight() {
    return this.lineHeight;
  }

  
  public LinkGenerator getLinkGenerator() {
    return this.linkGenerator;
  }







  
  public List<DocumentRange> getMarkAllHighlightRanges() {
    return ((RSyntaxTextAreaHighlighter)getHighlighter())
      .getMarkAllHighlightRanges();
  }







  
  public List<DocumentRange> getMarkedOccurrences() {
    return ((RSyntaxTextAreaHighlighter)getHighlighter())
      .getMarkedOccurrences();
  }







  
  public boolean getMarkOccurrences() {
    return (this.markOccurrencesSupport != null);
  }







  
  public Color getMarkOccurrencesColor() {
    return this.markOccurrencesColor;
  }








  
  public int getMarkOccurrencesDelay() {
    return this.markOccurrencesDelay;
  }









  
  boolean getMarkOccurrencesOfTokenType(int type) {
    RSyntaxDocument doc = (RSyntaxDocument)getDocument();
    return doc.getMarkOccurrencesOfTokenType(type);
  }









  
  public Color getMatchedBracketBGColor() {
    return this.matchedBracketBGColor;
  }








  
  public Color getMatchedBracketBorderColor() {
    return this.matchedBracketBorderColor;
  }










  
  Rectangle getDotRectangle() {
    return this.dotRect;
  }









  
  Rectangle getMatchRectangle() {
    return this.match;
  }







  
  public int getMaxAscent() {
    return this.maxAscent;
  }













  
  public boolean getPaintMatchedBracketPair() {
    return this.paintMatchedBracketPair;
  }








  
  public boolean getPaintTabLines() {
    return this.paintTabLines;
  }












  
  boolean getPaintTokenBackgrounds(int line, float y) {
    int iy = (int)y;
    int curCaretY = getCurrentCaretY();
    return (iy < curCaretY || iy >= curCaretY + getLineHeight() || 
      !getHighlightCurrentLine());
  }









  
  public Parser getParser(int index) {
    return this.parserManager.getParser(index);
  }







  
  public int getParserCount() {
    return (this.parserManager == null) ? 0 : this.parserManager.getParserCount();
  }








  
  public int getParserDelay() {
    return this.parserManager.getDelay();
  }









  
  public List<ParserNotice> getParserNotices() {
    if (this.parserManager == null) {
      return Collections.emptyList();
    }
    return this.parserManager.getParserNotices();
  }








  
  public int getRightHandSideCorrection() {
    return this.rhsCorrection;
  }



















  
  public boolean getShouldIndentNextLine(int line) {
    if (isAutoIndentEnabled()) {
      RSyntaxDocument doc = (RSyntaxDocument)getDocument();
      return doc.getShouldIndentNextLine(line);
    } 
    return false;
  }









  
  public boolean getShowMatchedBracketPopup() {
    return this.showMatchedBracketPopup;
  }









  
  public String getSyntaxEditingStyle() {
    return this.syntaxStyleKey;
  }









  
  public SyntaxScheme getSyntaxScheme() {
    return this.syntaxScheme;
  }









  
  public Color getTabLineColor() {
    return this.tabLineColor;
  }









  
  public boolean getPaintMarkOccurrencesBorder() {
    return this.paintMarkOccurrencesBorder;
  }












  
  public Color getSecondaryLanguageBackground(int index) {
    return this.secondaryLanguageBackgrounds[index - 1];
  }









  
  public int getSecondaryLanguageCount() {
    return this.secondaryLanguageBackgrounds.length;
  }
















  
  public static synchronized boolean getTemplatesEnabled() {
    return templatesEnabled;
  }


  
  private byte[] getTextAsRtf(int start, int end) {
    RtfGenerator gen = new RtfGenerator(getBackground());
    Token tokenList = getTokenListFor(start, end);
    for (Token t = tokenList; t != null; t = t.getNextToken()) {
      if (t.isPaintable()) {
        if (t.length() == 1 && t.charAt(0) == '\n') {
          gen.appendNewline();
        } else {
          Font font = getFontForTokenType(t.getType());
          Color bg = getBackgroundForToken(t);
          boolean underline = getUnderlineForToken(t);

          
          if (t.isWhitespace()) {
            gen.appendToDocNoFG(t.getLexeme(), font, bg, underline);
          } else {
            Color fg = getForegroundForToken(t);
            gen.appendToDoc(t.getLexeme(), font, fg, bg, underline);
          } 
        } 
      }
    } 

    
    return gen.getRtf().getBytes(StandardCharsets.UTF_8);
  }









  
  public Token getTokenListFor(int startOffs, int endOffs) {
    TokenImpl tokenList = null;
    TokenImpl lastToken = null;
    
    Element map = getDocument().getDefaultRootElement();
    int startLine = map.getElementIndex(startOffs);
    int endLine = map.getElementIndex(endOffs);
    
    for (int line = startLine; line <= endLine; line++) {
      TokenImpl t = (TokenImpl)getTokenListForLine(line);
      t = cloneTokenList(t);
      if (tokenList == null) {
        tokenList = t;
        lastToken = tokenList;
      } else {
        
        lastToken.setNextToken(t);
      } 
      while (lastToken.getNextToken() != null && lastToken
        .getNextToken().isPaintable()) {
        lastToken = (TokenImpl)lastToken.getNextToken();
      }
      if (line < endLine) {

        
        int docOffs = map.getElement(line).getEndOffset() - 1;
        t = new TokenImpl(new char[] { '\n' }, 0, 0, docOffs, 21, 0);
        
        lastToken.setNextToken(t);
        lastToken = t;
      } 
    } 









    
    if (startOffs >= tokenList.getOffset()) {
      while (!tokenList.containsPosition(startOffs)) {
        tokenList = (TokenImpl)tokenList.getNextToken();
      }
      tokenList.makeStartAt(startOffs);
    } 
    
    TokenImpl temp = tokenList;

    
    while (temp != null && !temp.containsPosition(endOffs)) {
      temp = (TokenImpl)temp.getNextToken();
    }
    if (temp != null) {
      temp.textCount = endOffs - temp.getOffset();
      temp.setNextToken(null);
    } 
    
    return tokenList;
  }








  
  public Token getTokenListForLine(int line) {
    return ((RSyntaxDocument)getDocument()).getTokenListForLine(line);
  }






  
  TokenPainter getTokenPainter() {
    return this.tokenPainter;
  }















  
  public String getToolTipText(MouseEvent e) {
    if (RSyntaxUtilities.getOS() == 2) {
      Point newLoc = e.getPoint();
      if (newLoc != null && newLoc.equals(this.cachedTipLoc)) {
        return this.cachedTip;
      }
      this.cachedTipLoc = newLoc;
    } 
    
    return this.cachedTip = getToolTipTextImpl(e);
  }










  
  protected String getToolTipTextImpl(MouseEvent e) {
    String text = null;
    URL imageBase = null;
    if (this.parserManager != null) {
      ToolTipInfo info = this.parserManager.getToolTipText(e);
      if (info != null) {
        text = info.getToolTipText();
        imageBase = info.getImageBase();
      } 
    } 
    if (text == null) {
      text = super.getToolTipText(e);
    }

    
    if (getUseFocusableTips()) {
      if (text != null) {
        if (this.focusableTip == null) {
          this.focusableTip = new FocusableTip((JTextArea)this, this.parserManager);
        }
        this.focusableTip.setImageBase(imageBase);
        this.focusableTip.toolTipRequested(e, text);

      
      }
      else if (this.focusableTip != null) {
        this.focusableTip.possiblyDisposeOfTipWindow();
      } 
      return null;
    } 
    
    return text;
  }










  
  public boolean getUnderlineForToken(Token t) {
    return ((getHyperlinksEnabled() && (t
      .isHyperlink() || (this.linkGeneratorResult != null && this.linkGeneratorResult
      
      .getSourceOffset() == t.getOffset()))) || 
      (this.syntaxScheme.getStyle(t.getType())).underline);
  }










  
  public boolean getUseFocusableTips() {
    return this.useFocusableTips;
  }












  
  public boolean getUseSelectedTextColor() {
    return this.useSelectedTextColor;
  }







  
  protected void init() {
    super.init();
    this.metricsNeverRefreshed = true;
    
    this.tokenPainter = new DefaultTokenPainter();






    
    if (toggleCurrentFoldAction == null) {
      createRstaPopupMenuActions();
    }

    
    this.syntaxStyleKey = "text/plain";
    setMatchedBracketBGColor(getDefaultBracketMatchBGColor());
    setMatchedBracketBorderColor(getDefaultBracketMatchBorderColor());
    setBracketMatchingEnabled(true);
    setAnimateBracketMatching(true);
    this.lastBracketMatchPos = -1;
    setSelectionColor(getDefaultSelectionColor());
    setTabLineColor((Color)null);
    setMarkOccurrencesColor(MarkOccurrencesSupport.DEFAULT_COLOR);
    setMarkOccurrencesDelay(1000);
    
    this.foldManager = (FoldManager)new DefaultFoldManager(this);

    
    setAutoIndentEnabled(true);
    setCloseCurlyBraces(true);
    setCloseMarkupTags(true);
    setClearWhitespaceLinesEnabled(true);
    
    setHyperlinksEnabled(true);
    setLinkScanningMask(128);
    setHyperlinkForeground(Color.BLUE);
    this.isScanningForLinks = false;
    setUseFocusableTips(true);

    
    setDefaultAntiAliasingState();
    restoreDefaultSyntaxScheme();
    
    setHighlightSecondaryLanguages(true);
    this.secondaryLanguageBackgrounds = new Color[3];
    this.secondaryLanguageBackgrounds[0] = new Color(16773324);
    this.secondaryLanguageBackgrounds[1] = new Color(14352090);
    this.secondaryLanguageBackgrounds[2] = new Color(16769264);
    
    setRightHandSideCorrection(0);
    setShowMatchedBracketPopup(true);
  }








  
  public boolean isAutoIndentEnabled() {
    return this.autoIndentEnabled;
  }







  
  public final boolean isBracketMatchingEnabled() {
    return this.bracketMatchingEnabled;
  }









  
  public boolean isClearWhitespaceLinesEnabled() {
    return this.clearWhitespaceLines;
  }









  
  public boolean isCodeFoldingEnabled() {
    return this.foldManager.isCodeFoldingEnabled();
  }








  
  public boolean isWhitespaceVisible() {
    return this.whitespaceVisible;
  }









  
  public Token modelToToken(int offs) {
    if (offs >= 0) {
      try {
        int line = getLineOfOffset(offs);
        Token t = getTokenListForLine(line);
        return RSyntaxUtilities.getTokenAtOffset(t, offs);
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
    return null;
  }













  
  protected void paintComponent(Graphics g) {
    if (this.metricsNeverRefreshed) {
      refreshFontMetrics(getGraphics2D(getGraphics()));
      this.metricsNeverRefreshed = false;
    } 
    
    super.paintComponent(getGraphics2D(g));
  }


  
  private void refreshFontMetrics(Graphics2D g2d) {
    this.defaultFontMetrics = g2d.getFontMetrics(getFont());
    this.syntaxScheme.refreshFontMetrics(g2d);
    if (!getLineWrap()) {

      
      SyntaxView sv = (SyntaxView)getUI().getRootView((JTextComponent)this).getView(0);
      sv.calculateLongestLine();
    } 
  }


  
  public void redoLastAction() {
    super.redoLastAction();






    
    ((RSyntaxTextAreaHighlighter)getHighlighter())
      .clearMarkOccurrencesHighlights();
  }







  
  public void removeActiveLineRangeListener(ActiveLineRangeListener l) {
    this.listenerList.remove(ActiveLineRangeListener.class, l);
  }









  
  public void removeHyperlinkListener(HyperlinkListener l) {
    this.listenerList.remove(HyperlinkListener.class, l);
  }





  
  public void removeNotify() {
    if (this.parserManager != null) {
      this.parserManager.stopParsing();
    }
    super.removeNotify();
  }










  
  public boolean removeParser(Parser parser) {
    boolean removed = false;
    if (this.parserManager != null) {
      removed = this.parserManager.removeParser(parser);
    }
    return removed;
  }








  
  public void restoreDefaultSyntaxScheme() {
    setSyntaxScheme(getDefaultSyntaxScheme());
  }
















  
  public static synchronized boolean saveTemplates() {
    if (!getTemplatesEnabled()) {
      return false;
    }
    return getCodeTemplateManager().saveTemplates();
  }




















  
  public void setActiveLineRange(int min, int max) {
    if (min == -1) {
      max = -1;
    }
    fireActiveLineRangeEvent(min, max);
  }








  
  public void setAnimateBracketMatching(boolean animate) {
    if (animate != this.animateBracketMatching) {
      this.animateBracketMatching = animate;
      if (animate && this.bracketRepaintTimer == null) {
        this.bracketRepaintTimer = new BracketMatchingTimer();
      }
      firePropertyChange("RSTA.animateBracketMatching", !animate, animate);
    } 
  }










  
  public void setAntiAliasingEnabled(boolean enabled) {
    boolean currentlyEnabled = (this.aaHints != null);
    
    if (enabled != currentlyEnabled) {
      
      if (enabled) {
        this.aaHints = RSyntaxUtilities.getDesktopAntiAliasHints();



        
        if (this.aaHints == null) {
          Map<RenderingHints.Key, Object> temp = new HashMap<>();
          
          temp.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          
          this.aaHints = temp;
        } 
      } else {
        
        this.aaHints = null;
      } 


      
      if (isDisplayable()) {
        refreshFontMetrics(getGraphics2D(getGraphics()));
      }
      firePropertyChange("RSTA.antiAlias", !enabled, enabled);
      repaint();
    } 
  }










  
  public void setAutoIndentEnabled(boolean enabled) {
    if (this.autoIndentEnabled != enabled) {
      this.autoIndentEnabled = enabled;
      firePropertyChange("RSTA.autoIndent", !enabled, enabled);
    } 
  }








  
  public void setBracketMatchingEnabled(boolean enabled) {
    if (enabled != this.bracketMatchingEnabled) {
      this.bracketMatchingEnabled = enabled;
      repaint();
      firePropertyChange("RSTA.bracketMatching", !enabled, enabled);
    } 
  }










  
  public void setClearWhitespaceLinesEnabled(boolean enabled) {
    if (enabled != this.clearWhitespaceLines) {
      this.clearWhitespaceLines = enabled;
      firePropertyChange("RSTA.clearWhitespaceLines", !enabled, enabled);
    } 
  }














  
  public void setCloseCurlyBraces(boolean close) {
    if (close != this.closeCurlyBraces) {
      this.closeCurlyBraces = close;
      firePropertyChange("RSTA.closeCurlyBraces", !close, close);
    } 
  }













  
  public void setCloseMarkupTags(boolean close) {
    if (close != this.closeMarkupTags) {
      this.closeMarkupTags = close;
      firePropertyChange("RSTA.closeMarkupTags", !close, close);
    } 
  }











  
  public void setCodeFoldingEnabled(boolean enabled) {
    if (enabled != this.foldManager.isCodeFoldingEnabled()) {
      this.foldManager.setCodeFoldingEnabled(enabled);
      firePropertyChange("RSTA.codeFolding", !enabled, enabled);
    } 
  }








  
  private void setDefaultAntiAliasingState() {
    this.aaHints = RSyntaxUtilities.getDesktopAntiAliasHints();
    if (this.aaHints == null) {
      
      Map<RenderingHints.Key, Object> temp = new HashMap<>();



      
      JLabel label = new JLabel();
      FontMetrics fm = label.getFontMetrics(label.getFont());
      Object hint = null;

      
      try {
        Method m = FontMetrics.class.getMethod("getFontRenderContext", new Class[0]);
        FontRenderContext frc = (FontRenderContext)m.invoke(fm, new Object[0]);
        m = FontRenderContext.class.getMethod("getAntiAliasingHint", new Class[0]);
        hint = m.invoke(frc, new Object[0]);
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception exception) {}







      
      if (hint == null) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
          hint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        } else {
          
          hint = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        } 
      } 
      temp.put(RenderingHints.KEY_TEXT_ANTIALIASING, hint);
      
      this.aaHints = temp;
    } 



    
    if (isDisplayable()) {
      refreshFontMetrics(getGraphics2D(getGraphics()));
    }
    repaint();
  }












  
  public void setDocument(Document document) {
    if (!(document instanceof RSyntaxDocument)) {
      throw new IllegalArgumentException("Documents for RSyntaxTextArea must be instances of RSyntaxDocument!");
    }

    
    if (this.markOccurrencesSupport != null) {
      this.markOccurrencesSupport.clear();
    }
    super.setDocument(document);
    setSyntaxEditingStyle(((RSyntaxDocument)document).getSyntaxStyle());
    if (this.markOccurrencesSupport != null) {
      this.markOccurrencesSupport.doMarkOccurrences();
    }
  }









  
  public void setEOLMarkersVisible(boolean visible) {
    if (visible != this.eolMarkersVisible) {
      this.eolMarkersVisible = visible;
      repaint();
      firePropertyChange("RSTA.eolMarkersVisible", !visible, visible);
    } 
  }












  
  public void setFont(Font font) {
    Font old = getFont();
    super.setFont(font);


    
    SyntaxScheme scheme = getSyntaxScheme();
    if (scheme != null && old != null) {
      scheme.changeBaseFont(old, font);
      calculateLineHeight();
    } 


    
    if (isDisplayable()) {
      refreshFontMetrics(getGraphics2D(getGraphics()));
      
      updateMarginLineX();

      
      forceCurrentLineHighlightRepaint();

      
      firePropertyChange("font", old, font);
      
      revalidate();
    } 
  }









  
  public void setFractionalFontMetricsEnabled(boolean enabled) {
    if (this.fractionalFontMetricsEnabled != enabled) {
      this.fractionalFontMetricsEnabled = enabled;

      
      if (isDisplayable()) {
        refreshFontMetrics(getGraphics2D(getGraphics()));
      }
      firePropertyChange("RSTA.fractionalFontMetrics", !enabled, enabled);
    } 
  }













  
  public void setHighlighter(Highlighter h) {
    RSyntaxTextAreaHighlighter rSyntaxTextAreaHighlighter;
    if (h == null) {
      rSyntaxTextAreaHighlighter = new RSyntaxTextAreaHighlighter();
    }
    
    if (!(rSyntaxTextAreaHighlighter instanceof RSyntaxTextAreaHighlighter)) {
      throw new IllegalArgumentException("RSyntaxTextArea requires an RSyntaxTextAreaHighlighter for its Highlighter");
    }
    
    super.setHighlighter((Highlighter)rSyntaxTextAreaHighlighter);
  }











  
  public void setHighlightSecondaryLanguages(boolean highlight) {
    if (this.highlightSecondaryLanguages != highlight) {
      this.highlightSecondaryLanguages = highlight;
      repaint();
      firePropertyChange("RSTA.highlightSecondaryLanguages", !highlight, highlight);
    } 
  }










  
  public void setHyperlinkForeground(Color fg) {
    if (fg == null) {
      throw new NullPointerException("fg cannot be null");
    }
    this.hyperlinkFG = fg;
  }











  
  public void setHyperlinksEnabled(boolean enabled) {
    if (this.hyperlinksEnabled != enabled) {
      this.hyperlinksEnabled = enabled;
      repaint();
      firePropertyChange("RSTA.hyperlinksEnabled", !enabled, enabled);
    } 
  }

  
  public void setLinkGenerator(LinkGenerator generator) {
    this.linkGenerator = generator;
  }



















  
  public void setLinkScanningMask(int mask) {
    mask &= 0x3C0;
    
    if (mask == 0) {
      throw new IllegalArgumentException("mask argument should be some combination of InputEvent.*_DOWN_MASK fields");
    }
    
    this.linkScanningMask = mask;
  }









  
  public void setMarkOccurrences(boolean markOccurrences) {
    if (markOccurrences) {
      if (this.markOccurrencesSupport == null) {
        this.markOccurrencesSupport = new MarkOccurrencesSupport();
        this.markOccurrencesSupport.install(this);
        firePropertyChange("RSTA.markOccurrences", false, true);
      }
    
    }
    else if (this.markOccurrencesSupport != null) {
      this.markOccurrencesSupport.uninstall();
      this.markOccurrencesSupport = null;
      firePropertyChange("RSTA.markOccurrences", true, false);
    } 
  }









  
  public void setMarkOccurrencesColor(Color color) {
    this.markOccurrencesColor = color;
    if (this.markOccurrencesSupport != null) {
      this.markOccurrencesSupport.setColor(color);
    }
  }









  
  public void setMarkOccurrencesDelay(int delay) {
    if (delay <= 0) {
      throw new IllegalArgumentException("Delay must be > 0");
    }
    if (delay != this.markOccurrencesDelay) {
      this.markOccurrencesDelay = delay;
      if (this.markOccurrencesSupport != null) {
        this.markOccurrencesSupport.setDelay(delay);
      }
    } 
  }










  
  public void setMatchedBracketBGColor(Color color) {
    this.matchedBracketBGColor = color;
    if (this.match != null) {
      repaint();
    }
  }








  
  public void setMatchedBracketBorderColor(Color color) {
    this.matchedBracketBorderColor = color;
    if (this.match != null) {
      repaint();
    }
  }









  
  public void setPaintMarkOccurrencesBorder(boolean paintBorder) {
    this.paintMarkOccurrencesBorder = paintBorder;
    if (this.markOccurrencesSupport != null) {
      this.markOccurrencesSupport.setPaintBorder(paintBorder);
    }
  }















  
  public void setPaintMatchedBracketPair(boolean paintPair) {
    if (paintPair != this.paintMatchedBracketPair) {
      this.paintMatchedBracketPair = paintPair;
      doBracketMatching();
      repaint();
      firePropertyChange("RSTA.paintMatchedBracketPair", !this.paintMatchedBracketPair, this.paintMatchedBracketPair);
    } 
  }










  
  public void setPaintTabLines(boolean paint) {
    if (paint != this.paintTabLines) {
      this.paintTabLines = paint;
      repaint();
      firePropertyChange("RSTA.tabLines", !paint, paint);
    } 
  }









  
  public void setParserDelay(int millis) {
    if (this.parserManager == null) {
      this.parserManager = new ParserManager(this);
    }
    this.parserManager.setDelay(millis);
  }











  
  public void setRightHandSideCorrection(int rhsCorrection) {
    if (rhsCorrection < 0) {
      throw new IllegalArgumentException("correction should be > 0");
    }
    if (rhsCorrection != this.rhsCorrection) {
      this.rhsCorrection = rhsCorrection;
      revalidate();
      repaint();
    } 
  }











  
  public void setSecondaryLanguageBackground(int index, Color color) {
    index--;
    Color old = this.secondaryLanguageBackgrounds[index];
    if ((color == null && old != null) || (color != null && !color.equals(old))) {
      this.secondaryLanguageBackgrounds[index] = color;
      if (getHighlightSecondaryLanguages()) {
        repaint();
      }
    } 
  }









  
  public void setShowMatchedBracketPopup(boolean show) {
    this.showMatchedBracketPopup = show;
  }











  
  public void setSyntaxEditingStyle(String styleKey) {
    if (styleKey == null) {
      styleKey = "text/plain";
    }
    if (!styleKey.equals(this.syntaxStyleKey)) {
      String oldStyle = this.syntaxStyleKey;
      this.syntaxStyleKey = styleKey;
      ((RSyntaxDocument)getDocument()).setSyntaxStyle(styleKey);
      firePropertyChange("RSTA.syntaxStyle", oldStyle, styleKey);
      setActiveLineRange(-1, -1);
    } 
  }




















  
  public void setSyntaxScheme(SyntaxScheme scheme) {
    SyntaxScheme old = this.syntaxScheme;
    this.syntaxScheme = scheme;



    
    calculateLineHeight();
    
    if (isDisplayable()) {
      refreshFontMetrics(getGraphics2D(getGraphics()));
    }

    
    updateMarginLineX();
    this.lastBracketMatchPos = -1;
    doBracketMatching();


    
    forceCurrentLineHighlightRepaint();

    
    revalidate();
    
    firePropertyChange("RSTA.syntaxScheme", old, this.syntaxScheme);
  }

















  
  public static synchronized boolean setTemplateDirectory(String dir) {
    if (getTemplatesEnabled() && dir != null) {
      File directory = new File(dir);
      if (directory.isDirectory()) {
        return 
          (getCodeTemplateManager().setTemplateDirectory(directory) > -1);
      }
      boolean created = directory.mkdir();
      if (created) {
        return 
          (getCodeTemplateManager().setTemplateDirectory(directory) > -1);
      }
    } 
    return false;
  }






























  
  public static synchronized void setTemplatesEnabled(boolean enabled) {
    templatesEnabled = enabled;
  }












  
  public void setTabLineColor(Color c) {
    if (c == null) {
      c = Color.gray;
    }
    
    if (!c.equals(this.tabLineColor)) {
      Color old = this.tabLineColor;
      this.tabLineColor = c;
      if (getPaintTabLines()) {
        repaint();
      }
      firePropertyChange("RSTA.tabLineColor", old, this.tabLineColor);
    } 
  }












  
  public void setUseFocusableTips(boolean use) {
    if (use != this.useFocusableTips) {
      this.useFocusableTips = use;
      firePropertyChange("RSTA.focusableTips", !use, use);
    } 
  }













  
  public void setUseSelectedTextColor(boolean use) {
    if (use != this.useSelectedTextColor) {
      this.useSelectedTextColor = use;
      firePropertyChange("RSTA.useSelectedTextColor", !use, use);
    } 
  }








  
  public void setWhitespaceVisible(boolean visible) {
    if (this.whitespaceVisible != visible) {
      this.whitespaceVisible = visible;
      this.tokenPainter = visible ? new VisibleWhitespaceTokenPainter() : new DefaultTokenPainter();
      
      repaint();
      firePropertyChange("RSTA.visibleWhitespace", !visible, visible);
    } 
  }






  
  private void stopScanningForLinks() {
    if (this.isScanningForLinks) {
      
      this.isScanningForLinks = false;
      this.linkGeneratorResult = null;
      this.hoveredOverLinkOffset = -1;
      
      Cursor c = getCursor();
      if (c != null && c.getType() == 12) {
        fireHyperlinkUpdate(HyperlinkEvent.EventType.EXITED);
        setCursor(Cursor.getPredefinedCursor(2));
        repaint();
      } 
    } 
  }


  
  public void undoLastAction() {
    super.undoLastAction();






    
    ((RSyntaxTextAreaHighlighter)getHighlighter())
      .clearMarkOccurrencesHighlights();
  }















  
  public Token viewToToken(Point p) {
    return modelToToken(viewToModel(p));
  }

  
  private final class MatchedBracketPopupTimer
    extends Timer
    implements ActionListener, CaretListener
  {
    private MatchedBracketPopup popup;
    
    private int origDot;
    
    private int matchedBracketOffs;
    
    private MatchedBracketPopupTimer() {
      super(350, null);
      addActionListener(this);
      setRepeats(false);
    }


    
    public void actionPerformed(ActionEvent e) {
      if (this.popup != null) {
        this.popup.dispose();
      }
      
      Window window = SwingUtilities.getWindowAncestor((Component)RSyntaxTextArea.this);
      this.popup = new MatchedBracketPopup(window, RSyntaxTextArea.this, this.matchedBracketOffs);
      this.popup.pack();
      this.popup.setVisible(true);
    }


    
    public void caretUpdate(CaretEvent e) {
      int dot = e.getDot();
      if (dot != this.origDot) {
        stop();
        RSyntaxTextArea.this.removeCaretListener(this);
        if (this.popup != null) {
          this.popup.dispose();
        }
      } 
    }





    
    public void restart(int matchedBracketOffs) {
      this.origDot = RSyntaxTextArea.this.getCaretPosition();
      this.matchedBracketOffs = matchedBracketOffs;
      restart();
    }

    
    public void start() {
      super.start();
      RSyntaxTextArea.this.addCaretListener(this);
    }
  }


  
  private class BracketMatchingTimer
    extends Timer
    implements ActionListener
  {
    private int pulseCount;

    
    BracketMatchingTimer() {
      super(20, null);
      addActionListener(this);
      setCoalesce(false);
    }

    
    public void actionPerformed(ActionEvent e) {
      if (RSyntaxTextArea.this.isBracketMatchingEnabled()) {
        if (RSyntaxTextArea.this.match != null) {
          updateAndInvalidate(RSyntaxTextArea.this.match);
        }
        if (RSyntaxTextArea.this.dotRect != null && RSyntaxTextArea.this.getPaintMatchedBracketPair()) {
          updateAndInvalidate(RSyntaxTextArea.this.dotRect);
        }
        if (++this.pulseCount == 8) {
          this.pulseCount = 0;
          stop();
        } 
      } 
    }
    
    private void init(Rectangle r) {
      r.x += 3;
      r.y += 3;
      r.width -= 6;
      r.height -= 6;
    }

    
    public void start() {
      init(RSyntaxTextArea.this.match);
      if (RSyntaxTextArea.this.dotRect != null && RSyntaxTextArea.this.getPaintMatchedBracketPair()) {
        init(RSyntaxTextArea.this.dotRect);
      }
      this.pulseCount = 0;
      super.start();
    }
    
    private void updateAndInvalidate(Rectangle r) {
      if (this.pulseCount < 5) {
        r.x--;
        r.y--;
        r.width += 2;
        r.height += 2;
        RSyntaxTextArea.this.repaint(r.x, r.y, r.width, r.height);
      }
      else if (this.pulseCount < 7) {
        r.x++;
        r.y++;
        r.width -= 2;
        r.height -= 2;
        RSyntaxTextArea.this.repaint(r.x - 2, r.y - 2, r.width + 5, r.height + 5);
      } 
    }
  }



  
  private class RSyntaxTextAreaMutableCaretEvent
    extends RTextArea.RTextAreaMutableCaretEvent
  {
    private Insets insets;


    
    protected RSyntaxTextAreaMutableCaretEvent(RTextArea textArea) {
      super(RSyntaxTextArea.this, textArea);
      this.insets = new Insets(0, 0, 0, 0);
    }

    
    private boolean equal(LinkGeneratorResult e1, LinkGeneratorResult e2) {
      return (e1.getSourceOffset() == e2.getSourceOffset());
    }

    
    public void mouseClicked(MouseEvent e) {
      if (RSyntaxTextArea.this.getHyperlinksEnabled() && RSyntaxTextArea.this.isScanningForLinks && RSyntaxTextArea.this
        .hoveredOverLinkOffset > -1) {
        RSyntaxTextArea.this.fireHyperlinkUpdate(HyperlinkEvent.EventType.ACTIVATED);
        RSyntaxTextArea.this.stopScanningForLinks();
      } 
    }


    
    public void mouseMoved(MouseEvent e) {
      super.mouseMoved(e);
      
      if (!RSyntaxTextArea.this.getHyperlinksEnabled()) {
        return;
      }

      
      if ((e.getModifiersEx() & RSyntaxTextArea.this.linkScanningMask) == RSyntaxTextArea.this.linkScanningMask) {
        Cursor c2;

        
        this.insets = RSyntaxTextArea.this.getInsets(this.insets);
        if (this.insets != null) {
          int x = e.getX();
          int y = e.getY();
          if (x <= this.insets.left || y < this.insets.top) {
            if (RSyntaxTextArea.this.isScanningForLinks) {
              RSyntaxTextArea.this.stopScanningForLinks();
            }
            
            return;
          } 
        } 
        RSyntaxTextArea.this.isScanningForLinks = true;
        Token t = RSyntaxTextArea.this.viewToToken(e.getPoint());
        if (t != null)
        {
          t = new TokenImpl(t);
        }
        
        if (t != null && t.isHyperlink()) {
          if (RSyntaxTextArea.this.hoveredOverLinkOffset == -1 || RSyntaxTextArea.this
            .hoveredOverLinkOffset != t.getOffset()) {
            RSyntaxTextArea.this.hoveredOverLinkOffset = t.getOffset();
            RSyntaxTextArea.this.repaint();
          } 
          c2 = Cursor.getPredefinedCursor(12);
        }
        else if (t != null && RSyntaxTextArea.this.linkGenerator != null) {
          int offs = RSyntaxTextArea.this.viewToModel(e.getPoint());
          
          LinkGeneratorResult newResult = RSyntaxTextArea.this.linkGenerator.isLinkAtOffset(RSyntaxTextArea.this, offs);
          if (newResult != null) {
            
            if (RSyntaxTextArea.this.linkGeneratorResult == null || 
              !equal(newResult, RSyntaxTextArea.this.linkGeneratorResult)) {
              RSyntaxTextArea.this.repaint();
            }
            RSyntaxTextArea.this.linkGeneratorResult = newResult;
            RSyntaxTextArea.this.hoveredOverLinkOffset = t.getOffset();
            c2 = Cursor.getPredefinedCursor(12);
          }
          else {
            
            if (RSyntaxTextArea.this.linkGeneratorResult != null) {
              RSyntaxTextArea.this.repaint();
            }
            c2 = Cursor.getPredefinedCursor(2);
            RSyntaxTextArea.this.hoveredOverLinkOffset = -1;
            RSyntaxTextArea.this.linkGeneratorResult = null;
          } 
        } else {
          
          c2 = Cursor.getPredefinedCursor(2);
          RSyntaxTextArea.this.hoveredOverLinkOffset = -1;
          if (RSyntaxTextArea.this.linkGeneratorResult != null) {
            RSyntaxTextArea.this.linkGeneratorResult = null;
          }
        } 
        if (RSyntaxTextArea.this.getCursor() != c2)
        {
          RSyntaxTextArea.this.setCursor(c2);
          
          RSyntaxTextArea.this.repaint();


          
          RSyntaxTextArea.this.fireHyperlinkUpdate((c2 == Cursor.getPredefinedCursor(12)) ? HyperlinkEvent.EventType.ENTERED : HyperlinkEvent.EventType.EXITED);
        
        }
      
      }
      else if (RSyntaxTextArea.this.isScanningForLinks) {
        RSyntaxTextArea.this.stopScanningForLinks();
      } 
    }
  }



  
  public void setActionForKeyStroke(KeyStroke keyStroke, Action action) {
    getActionMap().put(keyStroke.toString(), action);
    getInputMap().put(keyStroke, keyStroke.toString());
  }
  public void setActionForKey(String keyString, Action action) {
    setActionForKeyStroke(KeyStroke.getKeyStroke(keyString), action);
  }
  public void registerReplaceDialog() {
    final RSyntaxTextArea textArea = this;
    setActionForKey("ctrl pressed F", new AbstractAction() {
          ReplaceDialog replaceDialog = createReplaceDialog();
          
          public void actionPerformed(ActionEvent e) {
            this.replaceDialog.setVisible(true);
          }
          public ReplaceDialog createReplaceDialog() {
            Frame frame = RSyntaxTextArea.this.getParentFrame();
            SearchListenerImpl searchListenerImpl = new SearchListenerImpl(textArea);
            if (frame != null) {
              return new ReplaceDialog(frame, (SearchListener)searchListenerImpl);
            }
            Dialog dialog = RSyntaxTextArea.this.getParentDialog();
            if (dialog != null) {
              return new ReplaceDialog(dialog, (SearchListener)searchListenerImpl);
            }
            return new ReplaceDialog(frame, (SearchListener)searchListenerImpl);
          }
        });
  }
  public void registerGoToDialog() {
    setActionForKey("ctrl pressed G", new AbstractAction() {
          RSyntaxTextArea textArea = RSyntaxTextArea.this;
          
          public void actionPerformed(ActionEvent e) {
            GoToDialog dialog = new GoToDialog(RSyntaxTextArea.this.getParentFrame());
            dialog.setMaxLineNumberAllowed(this.textArea.getLineCount());
            dialog.setVisible(true);
            int line = dialog.getLineNumber();
            if (line > 0)
              try {
                this.textArea.setCaretPosition(this.textArea.getLineStartOffset(line - 1));
              } catch (BadLocationException ble) {
                UIManager.getLookAndFeel().provideErrorFeedback((Component)this.textArea);
                ble.printStackTrace();
              }  
          }
        });
  }
  
  public Frame getParentFrame() {
    RSyntaxTextArea rSyntaxTextArea = this; Container container;
    while ((container = rSyntaxTextArea.getParent()) != null) {
      if (Frame.class.isAssignableFrom(container.getClass())) {
        return (Frame)container;
      }
    } 
    return null;
  }
  public Dialog getParentDialog() {
    RSyntaxTextArea rSyntaxTextArea = this; Container container;
    while ((container = rSyntaxTextArea.getParent()) != null) {
      if (Dialog.class.isAssignableFrom(container.getClass())) {
        return (Dialog)container;
      }
    } 
    return null;
  }
}
