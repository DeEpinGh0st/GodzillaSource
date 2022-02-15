package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.StyleContext;


























public abstract class RTextAreaBase
  extends JTextArea
{
  public static final String BACKGROUND_IMAGE_PROPERTY = "background.image";
  public static final String CURRENT_LINE_HIGHLIGHT_COLOR_PROPERTY = "RTA.currentLineHighlightColor";
  public static final String CURRENT_LINE_HIGHLIGHT_FADE_PROPERTY = "RTA.currentLineHighlightFade";
  public static final String HIGHLIGHT_CURRENT_LINE_PROPERTY = "RTA.currentLineHighlight";
  public static final String ROUNDED_SELECTION_PROPERTY = "RTA.roundedSelection";
  private boolean tabsEmulatedWithSpaces;
  private boolean highlightCurrentLine;
  private Color currentLineColor;
  private boolean marginLineEnabled;
  private Color marginLineColor;
  private int marginLineX;
  private int marginSizeInChars;
  private boolean fadeCurrentLineHighlight;
  private boolean roundedSelectionEdges;
  private int previousCaretY;
  int currentCaretY;
  private BackgroundPainterStrategy backgroundPainter;
  private RTAMouseListener mouseListener;
  private static final Color DEFAULT_CARET_COLOR = new ColorUIResource(255, 51, 51);
  private static final Color DEFAULT_CURRENT_LINE_HIGHLIGHT_COLOR = new Color(255, 255, 170);
  private static final Color DEFAULT_MARGIN_LINE_COLOR = new Color(255, 224, 224);

  
  private static final int DEFAULT_TAB_SIZE = 4;
  
  private static final int DEFAULT_MARGIN_LINE_POSITION = 80;

  
  public RTextAreaBase() {
    init();
  }






  
  public RTextAreaBase(AbstractDocument doc) {
    super(doc);
    init();
  }







  
  public RTextAreaBase(String text) {
    init();
    setText(text);
  }









  
  public RTextAreaBase(int rows, int cols) {
    super(rows, cols);
    init();
  }












  
  public RTextAreaBase(String text, int rows, int cols) {
    super(rows, cols);
    init();
    setText(text);
  }













  
  public RTextAreaBase(AbstractDocument doc, String text, int rows, int cols) {
    super(doc, (String)null, rows, cols);
    init();
    setText(text);
  }







  
  private void addCurrentLineHighlightListeners() {
    boolean add = true;
    MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners();
    for (MouseMotionListener mouseMotionListener : mouseMotionListeners) {
      if (mouseMotionListener == this.mouseListener) {
        add = false;
        break;
      } 
    } 
    if (add)
    {
      addMouseMotionListener(this.mouseListener);
    }
    MouseListener[] mouseListeners = getMouseListeners();
    for (MouseListener listener : mouseListeners) {
      if (listener == this.mouseListener) {
        add = false;
        break;
      } 
    } 
    if (add)
    {
      addMouseListener(this.mouseListener);
    }
  }

  
  public void addNotify() {
    super.addNotify();


    
    if (getCaretPosition() != 0) {
      SwingUtilities.invokeLater(this::possiblyUpdateCurrentLineHighlightLocation);
    }
  }






















  
  public void convertSpacesToTabs() {
    int caretPosition = getCaretPosition();
    int tabSize = getTabSize();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < tabSize; i++) {
      stringBuilder.append(" ");
    }
    String text = getText();
    setText(text.replaceAll(stringBuilder.toString(), "\t"));
    int newDocumentLength = getDocument().getLength();

    
    if (caretPosition < newDocumentLength) {
      setCaretPosition(caretPosition);
    } else {
      
      setCaretPosition(newDocumentLength - 1);
    } 
  }















  
  public void convertTabsToSpaces() {
    int caretPosition = getCaretPosition();
    int tabSize = getTabSize();
    StringBuilder tabInSpaces = new StringBuilder();
    for (int i = 0; i < tabSize; i++) {
      tabInSpaces.append(' ');
    }
    String text = getText();
    setText(text.replaceAll("\t", tabInSpaces.toString()));

    
    setCaretPosition(caretPosition);
  }









  
  protected abstract RTAMouseListener createMouseListener();









  
  protected abstract RTextAreaUI createRTextAreaUI();








  
  protected void forceCurrentLineHighlightRepaint() {
    if (isShowing()) {
      
      this.previousCaretY = -1;

      
      fireCaretUpdate(this.mouseListener);
    } 
  }










  
  public final Color getBackground() {
    Object bg = getBackgroundObject();
    return (bg instanceof Color) ? (Color)bg : null;
  }









  
  public final Image getBackgroundImage() {
    Object bg = getBackgroundObject();
    return (bg instanceof Image) ? (Image)bg : null;
  }










  
  public final Object getBackgroundObject() {
    if (this.backgroundPainter == null) {
      return null;
    }
    return (this.backgroundPainter instanceof ImageBackgroundPainterStrategy) ? ((ImageBackgroundPainterStrategy)this.backgroundPainter)
      .getMasterImage() : ((ColorBackgroundPainterStrategy)this.backgroundPainter)
      .getColor();
  }






  
  public final int getCaretLineNumber() {
    try {
      return getLineOfOffset(getCaretPosition());
    } catch (BadLocationException ble) {
      return 0;
    } 
  }








  
  public final int getCaretOffsetFromLineStart() {
    try {
      int pos = getCaretPosition();
      return pos - getLineStartOffset(getLineOfOffset(pos));
    } catch (BadLocationException ble) {
      return 0;
    } 
  }






  
  protected int getCurrentCaretY() {
    return this.currentCaretY;
  }











  
  public Color getCurrentLineHighlightColor() {
    return this.currentLineColor;
  }






  
  public static Color getDefaultCaretColor() {
    return DEFAULT_CARET_COLOR;
  }








  
  public static Color getDefaultCurrentLineHighlightColor() {
    return DEFAULT_CURRENT_LINE_HIGHLIGHT_COLOR;
  }









  
  public static Font getDefaultFont() {
    StyleContext sc = StyleContext.getDefaultStyleContext();
    Font font = null;
    
    if (isOSX()) {

      
      font = sc.getFont("Menlo", 0, 12);
      if (!"Menlo".equals(font.getFamily())) {
        font = sc.getFont("Monaco", 0, 12);
        if (!"Monaco".equals(font.getFamily())) {
          font = sc.getFont("Monospaced", 0, 13);
        }
      }
    
    } else {
      
      font = sc.getFont("Consolas", 0, 13);
      if (!"Consolas".equals(font.getFamily())) {
        font = sc.getFont("Monospaced", 0, 13);
      }
    } 

    
    return font;
  }







  
  public static Color getDefaultForeground() {
    return Color.BLACK;
  }








  
  public static Color getDefaultMarginLineColor() {
    return DEFAULT_MARGIN_LINE_COLOR;
  }








  
  public static int getDefaultMarginLinePosition() {
    return 80;
  }






  
  public static int getDefaultTabSize() {
    return 4;
  }







  
  public boolean getFadeCurrentLineHighlight() {
    return this.fadeCurrentLineHighlight;
  }









  
  public boolean getHighlightCurrentLine() {
    return this.highlightCurrentLine;
  }







  
  public final int getLineEndOffsetOfCurrentLine() {
    try {
      return getLineEndOffset(getCaretLineNumber());
    } catch (BadLocationException ble) {
      return 0;
    } 
  }






  
  public int getLineHeight() {
    return getRowHeight();
  }







  
  public final int getLineStartOffsetOfCurrentLine() {
    try {
      return getLineStartOffset(getCaretLineNumber());
    } catch (BadLocationException ble) {
      return 0;
    } 
  }







  
  public Color getMarginLineColor() {
    return this.marginLineColor;
  }









  
  public int getMarginLinePixelLocation() {
    return this.marginLineX;
  }









  
  public int getMarginLinePosition() {
    return this.marginSizeInChars;
  }







  
  public boolean getRoundedSelectionEdges() {
    return this.roundedSelectionEdges;
  }









  
  public boolean getTabsEmulated() {
    return this.tabsEmulatedWithSpaces;
  }









  
  protected void init() {
    setRTextAreaUI(createRTextAreaUI());

    
    enableEvents(9L);

    
    setHighlightCurrentLine(true);
    setCurrentLineHighlightColor(getDefaultCurrentLineHighlightColor());
    setMarginLineEnabled(false);
    setMarginLineColor(getDefaultMarginLineColor());
    setMarginLinePosition(getDefaultMarginLinePosition());
    setBackgroundObject(Color.WHITE);
    setWrapStyleWord(true);
    setTabSize(5);
    setForeground(Color.BLACK);
    setTabsEmulated(false);

    
    this.previousCaretY = this.currentCaretY = (getInsets()).top;

    
    this.mouseListener = createMouseListener();

    
    addFocusListener(this.mouseListener);
    addCurrentLineHighlightListeners();
  }








  
  public boolean isMarginLineEnabled() {
    return this.marginLineEnabled;
  }








  
  public static boolean isOSX() {
    String osName = System.getProperty("os.name").toLowerCase();
    return osName.startsWith("mac os x");
  }










  
  protected void paintComponent(Graphics g) {
    this.backgroundPainter.paint(g, getVisibleRect());

    
    TextUI ui = getUI();
    if (ui != null) {
      
      Graphics scratchGraphics = g.create();
      try {
        ui.update(scratchGraphics, this);
      } finally {
        scratchGraphics.dispose();
      } 
    } 
  }









  
  protected void possiblyUpdateCurrentLineHighlightLocation() {
    int width = getWidth();
    int lineHeight = getLineHeight();
    int dot = getCaretPosition();



    
    if (getLineWrap()) {
      try {
        Rectangle temp = modelToView(dot);
        if (temp != null) {
          this.currentCaretY = temp.y;
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } else {

      
      try {










        
        Rectangle temp = modelToView(dot);
        if (temp != null) {
          this.currentCaretY = temp.y;
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 



    
    repaint(0, this.currentCaretY, width, lineHeight);
    if (this.previousCaretY != this.currentCaretY) {
      repaint(0, this.previousCaretY, width, lineHeight);
    }
    
    this.previousCaretY = this.currentCaretY;
  }
















  
  protected void processComponentEvent(ComponentEvent e) {
    if (e.getID() == 101 && 
      getLineWrap() && getHighlightCurrentLine()) {
      this.previousCaretY = -1;
      fireCaretUpdate(this.mouseListener);
    } 
    
    super.processComponentEvent(e);
  }



















  
  public void setBackground(Color bg) {
    Object oldBG = getBackgroundObject();
    if (oldBG instanceof Color) {
      ((ColorBackgroundPainterStrategy)this.backgroundPainter)
        .setColor(bg);
    } else {
      
      this.backgroundPainter = new ColorBackgroundPainterStrategy(bg);
    } 
    setOpaque((bg == null || bg.getAlpha() == 255));
    firePropertyChange("background", oldBG, bg);
    repaint();
  }


















  
  public void setBackgroundImage(Image image) {
    Object oldBG = getBackgroundObject();
    if (oldBG instanceof Image) {
      ((ImageBackgroundPainterStrategy)this.backgroundPainter)
        .setImage(image);
    } else {
      
      ImageBackgroundPainterStrategy strategy = new BufferedImageBackgroundPainterStrategy(this);
      
      strategy.setImage(image);
      this.backgroundPainter = strategy;
    } 
    setOpaque(false);
    firePropertyChange("background.image", oldBG, image);
    repaint();
  }








  
  public void setBackgroundObject(Object newBackground) {
    if (newBackground instanceof Color) {
      setBackground((Color)newBackground);
    }
    else if (newBackground instanceof Image) {
      setBackgroundImage((Image)newBackground);
    } else {
      
      setBackground(Color.WHITE);
    } 
  }


























  
  public void setCurrentLineHighlightColor(Color color) {
    if (color == null) {
      throw new NullPointerException();
    }
    if (!color.equals(this.currentLineColor)) {
      Color old = this.currentLineColor;
      this.currentLineColor = color;
      firePropertyChange("RTA.currentLineHighlightColor", old, color);
    } 
  }










  
  public void setFadeCurrentLineHighlight(boolean fade) {
    if (fade != this.fadeCurrentLineHighlight) {
      this.fadeCurrentLineHighlight = fade;
      if (getHighlightCurrentLine()) {
        forceCurrentLineHighlightRepaint();
      }
      firePropertyChange("RTA.currentLineHighlightFade", !fade, fade);
    } 
  }










  
  public void setFont(Font font) {
    if (font != null && font.getSize() <= 0) {
      throw new IllegalArgumentException("Font size must be > 0");
    }
    super.setFont(font);
    if (font != null) {
      updateMarginLineX();
      if (this.highlightCurrentLine) {
        possiblyUpdateCurrentLineHighlightLocation();
      }
    } 
  }










  
  public void setHighlightCurrentLine(boolean highlight) {
    if (highlight != this.highlightCurrentLine) {
      this.highlightCurrentLine = highlight;
      firePropertyChange("RTA.currentLineHighlight", !highlight, highlight);
      
      repaint();
    } 
  }








  
  public void setLineWrap(boolean wrap) {
    super.setLineWrap(wrap);
    forceCurrentLineHighlightRepaint();
  }







  
  public void setMargin(Insets insets) {
    Insets old = getInsets();
    int oldTop = (old != null) ? old.top : 0;
    int newTop = (insets != null) ? insets.top : 0;
    if (oldTop != newTop)
    {
      
      this.previousCaretY = this.currentCaretY = newTop;
    }
    super.setMargin(insets);
  }








  
  public void setMarginLineColor(Color color) {
    this.marginLineColor = color;
    if (this.marginLineEnabled) {
      Rectangle visibleRect = getVisibleRect();
      repaint(this.marginLineX, visibleRect.y, 1, visibleRect.height);
    } 
  }







  
  public void setMarginLineEnabled(boolean enabled) {
    if (enabled != this.marginLineEnabled) {
      this.marginLineEnabled = enabled;
      if (this.marginLineEnabled) {
        Rectangle visibleRect = getVisibleRect();
        repaint(this.marginLineX, visibleRect.y, 1, visibleRect.height);
      } 
    } 
  }








  
  public void setMarginLinePosition(int size) {
    this.marginSizeInChars = size;
    if (this.marginLineEnabled) {
      Rectangle visibleRect = getVisibleRect();
      repaint(this.marginLineX, visibleRect.y, 1, visibleRect.height);
      updateMarginLineX();
      repaint(this.marginLineX, visibleRect.y, 1, visibleRect.height);
    } 
  }









  
  public void setRoundedSelectionEdges(boolean rounded) {
    if (this.roundedSelectionEdges != rounded) {
      this.roundedSelectionEdges = rounded;
      Caret c = getCaret();
      if (c instanceof ConfigurableCaret) {
        ((ConfigurableCaret)c).setRoundedSelectionEdges(rounded);
        if (c.getDot() != c.getMark()) {
          repaint();
        }
      } 
      firePropertyChange("RTA.roundedSelection", !rounded, rounded);
    } 
  }














  
  protected void setRTextAreaUI(RTextAreaUI ui) {
    setUI(ui);


    
    setOpaque(getBackgroundObject() instanceof Color);
  }













  
  public void setTabsEmulated(boolean areEmulated) {
    this.tabsEmulatedWithSpaces = areEmulated;
  }












  
  public void setTabSize(int size) {
    super.setTabSize(size);
    boolean b = getLineWrap();
    setLineWrap(!b);
    setLineWrap(b);
  }






  
  protected void updateMarginLineX() {
    Font font = getFont();
    if (font == null) {
      this.marginLineX = 0;
      return;
    } 
    this.marginLineX = getFontMetrics(font).charWidth('m') * this.marginSizeInChars;
  }












  
  public int yForLine(int line) throws BadLocationException {
    return ((RTextAreaUI)getUI()).yForLine(line);
  }











  
  public int yForLineContaining(int offs) throws BadLocationException {
    return ((RTextAreaUI)getUI()).yForLineContaining(offs);
  }
  
  protected static class RTAMouseListener
    extends CaretEvent
    implements MouseListener, MouseMotionListener, FocusListener
  {
    protected int dot;
    protected int mark;
    
    RTAMouseListener(RTextAreaBase textArea) {
      super(textArea);
    }


    
    public void focusGained(FocusEvent e) {}


    
    public void focusLost(FocusEvent e) {}


    
    public void mouseDragged(MouseEvent e) {}


    
    public void mouseMoved(MouseEvent e) {}


    
    public void mouseClicked(MouseEvent e) {}


    
    public void mousePressed(MouseEvent e) {}


    
    public void mouseReleased(MouseEvent e) {}


    
    public void mouseEntered(MouseEvent e) {}


    
    public void mouseExited(MouseEvent e) {}

    
    public int getDot() {
      return this.dot;
    }

    
    public int getMark() {
      return this.mark;
    }
  }
}
