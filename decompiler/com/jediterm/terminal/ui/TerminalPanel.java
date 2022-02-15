package com.jediterm.terminal.ui;
import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.SubstringFinder;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.mouse.MouseMode;
import com.jediterm.terminal.emulator.mouse.TerminalMouseListener;
import com.jediterm.terminal.model.CharBuffer;
import com.jediterm.terminal.model.SelectionUtil;
import com.jediterm.terminal.model.TerminalLine;
import com.jediterm.terminal.model.TerminalLineIntervalHighlighting;
import com.jediterm.terminal.model.TerminalSelection;
import com.jediterm.terminal.model.TerminalTextBuffer;
import com.jediterm.terminal.model.hyperlinks.LinkInfo;
import com.jediterm.terminal.util.Pair;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TerminalPanel extends JComponent implements TerminalDisplay, TerminalActionProvider {
  private static final Logger LOG = Logger.getLogger(TerminalPanel.class);
  
  private static final long serialVersionUID = -1048763516632093014L;
  
  public static final double SCROLL_SPEED = 0.05D;
  
  private Font myNormalFont;
  private Font myItalicFont;
  private Font myBoldFont;
  private Font myBoldItalicFont;
  private int myDescent = 0;
  private int mySpaceBetweenLines = 0;
  protected Dimension myCharSize = new Dimension();
  private boolean myMonospaced;
  protected Dimension myTermSize = new Dimension(80, 24);
  
  private TerminalStarter myTerminalStarter = null;
  
  private MouseMode myMouseMode = MouseMode.MOUSE_REPORTING_NONE;
  private Point mySelectionStartPoint = null;
  private TerminalSelection mySelection = null;
  
  private final TerminalCopyPasteHandler myCopyPasteHandler;
  
  private TerminalPanelListener myTerminalPanelListener;
  
  private final SettingsProvider mySettingsProvider;
  
  private final TerminalTextBuffer myTerminalTextBuffer;
  
  private final StyleState myStyleState;
  
  private final TerminalCursor myCursor = new TerminalCursor();

  
  private final BoundedRangeModel myBoundedRangeModel = new DefaultBoundedRangeModel(0, 80, 0, 80);
  
  private boolean myScrollingEnabled = true;
  protected int myClientScrollOrigin;
  private final List<KeyListener> myCustomKeyListeners = new CopyOnWriteArrayList<>();
  
  private String myWindowTitle = "Terminal";
  
  private TerminalActionProvider myNextActionProvider;
  
  private String myInputMethodUncommittedChars;
  private Timer myRepaintTimer;
  private AtomicInteger scrollDy = new AtomicInteger(0);
  private AtomicBoolean needRepaint = new AtomicBoolean(true);
  
  private int myMaxFPS = 50;
  private int myBlinkingPeriod = 500;
  
  private TerminalCoordinates myCoordsAccessor;
  
  private String myCurrentPath;
  private SubstringFinder.FindResult myFindResult;
  private LinkInfo myHoveredHyperlink = null;
  
  private int myCursorType = 0;
  private final TerminalKeyHandler myTerminalKeyHandler = new TerminalKeyHandler();
  private LinkInfo.HoverConsumer myLinkHoverConsumer;
  
  public TerminalPanel(@NotNull SettingsProvider settingsProvider, @NotNull TerminalTextBuffer terminalTextBuffer, @NotNull StyleState styleState) {
    this.mySettingsProvider = settingsProvider;
    this.myTerminalTextBuffer = terminalTextBuffer;
    this.myStyleState = styleState;
    this.myTermSize.width = terminalTextBuffer.getWidth();
    this.myTermSize.height = terminalTextBuffer.getHeight();
    this.myMaxFPS = this.mySettingsProvider.maxRefreshRate();
    this.myCopyPasteHandler = createCopyPasteHandler();
    
    updateScrolling(true);
    
    enableEvents(2056L);
    enableInputMethods(true);
    
    terminalTextBuffer.addModelListener(new TerminalModelListener()
        {
          public void modelChanged() {
            TerminalPanel.this.repaint();
          }
        });
  }
  
  @NotNull
  protected TerminalCopyPasteHandler createCopyPasteHandler() {
    return (TerminalCopyPasteHandler)new DefaultTerminalCopyPasteHandler();
  }
  
  public TerminalPanelListener getTerminalPanelListener() {
    return this.myTerminalPanelListener;
  }

  
  public void repaint() {
    this.needRepaint.set(true);
  }
  
  private void doRepaint() {
    super.repaint();
  }
  
  @Deprecated
  protected void reinitFontAndResize() {
    initFont();
    
    sizeTerminalFromComponent();
  }
  
  protected void initFont() {
    this.myNormalFont = createFont();
    this.myBoldFont = this.myNormalFont.deriveFont(1);
    this.myItalicFont = this.myNormalFont.deriveFont(2);
    this.myBoldItalicFont = this.myNormalFont.deriveFont(3);
    
    establishFontMetrics();
  }
  
  public void init(@NotNull JScrollBar scrollBar) {
    if (scrollBar == null) $$$reportNull$$$0(3);  initFont();
    
    setPreferredSize(new Dimension(getPixelWidth(), getPixelHeight()));
    
    setFocusable(true);
    enableInputMethods(true);
    setDoubleBuffered(true);
    
    setFocusTraversalKeysEnabled(false);
    
    addMouseMotionListener(new MouseMotionAdapter()
        {
          public void mouseMoved(MouseEvent e) {
            TerminalPanel.this.handleHyperlinks(e.getPoint(), e.isControlDown());
          }

          
          public void mouseDragged(MouseEvent e) {
            if (!TerminalPanel.this.isLocalMouseAction(e)) {
              return;
            }
            
            Point charCoords = TerminalPanel.this.panelToCharCoords(e.getPoint());
            
            if (TerminalPanel.this.mySelection == null) {
              
              if (TerminalPanel.this.mySelectionStartPoint == null) {
                TerminalPanel.this.mySelectionStartPoint = charCoords;
              }
              TerminalPanel.this.mySelection = new TerminalSelection(new Point(TerminalPanel.this.mySelectionStartPoint));
            } 
            TerminalPanel.this.repaint();
            TerminalPanel.this.mySelection.updateEnd(charCoords);
            if (TerminalPanel.this.mySettingsProvider.copyOnSelect()) {
              TerminalPanel.this.handleCopyOnSelect();
            }
            
            if ((e.getPoint()).y < 0) {
              TerminalPanel.this.moveScrollBar((int)((e.getPoint()).y * 0.05D));
            }
            if ((e.getPoint()).y > TerminalPanel.this.getPixelHeight()) {
              TerminalPanel.this.moveScrollBar((int)(((e.getPoint()).y - TerminalPanel.this.getPixelHeight()) * 0.05D));
            }
          }
        });
    
    addMouseWheelListener(e -> {
          if (isLocalMouseAction(e)) {
            handleMouseWheelEvent(e, scrollBar);
          }
        });
    
    addMouseListener(new MouseAdapter()
        {
          public void mouseExited(MouseEvent e) {
            if (TerminalPanel.this.myLinkHoverConsumer != null) {
              TerminalPanel.this.myLinkHoverConsumer.onMouseExited();
              TerminalPanel.this.myLinkHoverConsumer = null;
            } 
          }

          
          public void mousePressed(MouseEvent e) {
            if (e.getButton() == 1 && 
              e.getClickCount() == 1) {
              TerminalPanel.this.mySelectionStartPoint = TerminalPanel.this.panelToCharCoords(e.getPoint());
              TerminalPanel.this.mySelection = null;
              TerminalPanel.this.repaint();
            } 
          }


          
          public void mouseReleased(MouseEvent e) {
            TerminalPanel.this.requestFocusInWindow();
            TerminalPanel.this.repaint();
          }

          
          public void mouseClicked(MouseEvent e) {
            TerminalPanel.this.requestFocusInWindow();
            HyperlinkStyle hyperlink = TerminalPanel.this.isFollowLinkEvent(e) ? TerminalPanel.this.findHyperlink(e.getPoint()) : null;
            if (hyperlink != null) {
              hyperlink.getLinkInfo().navigate();
            } else if (e.getButton() == 1 && TerminalPanel.this.isLocalMouseAction(e)) {
              int count = e.getClickCount();
              if (count != 1)
              {
                if (count == 2) {
                  
                  Point charCoords = TerminalPanel.this.panelToCharCoords(e.getPoint());
                  Point start = SelectionUtil.getPreviousSeparator(charCoords, TerminalPanel.this.myTerminalTextBuffer);
                  Point stop = SelectionUtil.getNextSeparator(charCoords, TerminalPanel.this.myTerminalTextBuffer);
                  TerminalPanel.this.mySelection = new TerminalSelection(start);
                  TerminalPanel.this.mySelection.updateEnd(stop);
                  
                  if (TerminalPanel.this.mySettingsProvider.copyOnSelect()) {
                    TerminalPanel.this.handleCopyOnSelect();
                  }
                } else if (count == 3) {
                  
                  Point charCoords = TerminalPanel.this.panelToCharCoords(e.getPoint());
                  int startLine = charCoords.y;
                  while (startLine > -TerminalPanel.this.getScrollBuffer().getLineCount() && TerminalPanel.this
                    .myTerminalTextBuffer.getLine(startLine - 1).isWrapped()) {
                    startLine--;
                  }
                  int endLine = charCoords.y;
                  while (endLine < TerminalPanel.this.myTerminalTextBuffer.getHeight() && TerminalPanel.this
                    .myTerminalTextBuffer.getLine(endLine).isWrapped()) {
                    endLine++;
                  }
                  TerminalPanel.this.mySelection = new TerminalSelection(new Point(0, startLine));
                  TerminalPanel.this.mySelection.updateEnd(new Point(TerminalPanel.this.myTermSize.width, endLine));
                  
                  if (TerminalPanel.this.mySettingsProvider.copyOnSelect())
                    TerminalPanel.this.handleCopyOnSelect(); 
                } 
              }
            } else if (e.getButton() == 2 && TerminalPanel.this.mySettingsProvider.pasteOnMiddleMouseClick() && TerminalPanel.this.isLocalMouseAction(e)) {
              TerminalPanel.this.handlePasteSelection();
            } else if (e.getButton() == 3) {
              HyperlinkStyle contextHyperlink = TerminalPanel.this.findHyperlink(e.getPoint());
              JPopupMenu popup = TerminalPanel.this.createPopupMenu((contextHyperlink != null) ? contextHyperlink.getLinkInfo() : null, e);
              popup.show(e.getComponent(), e.getX(), e.getY());
            } 
            TerminalPanel.this.repaint();
          }
        });
    
    addComponentListener(new ComponentAdapter()
        {
          public void componentResized(ComponentEvent e) {
            TerminalPanel.this.sizeTerminalFromComponent();
          }
        });
    
    addFocusListener(new FocusAdapter()
        {
          public void focusGained(FocusEvent e) {
            TerminalPanel.this.myCursor.cursorChanged();
          }

          
          public void focusLost(FocusEvent e) {
            TerminalPanel.this.myCursor.cursorChanged();
            
            TerminalPanel.this.handleHyperlinks(e.getComponent(), false);
          }
        });
    
    this.myBoundedRangeModel.addChangeListener(new ChangeListener()
        {
          public void stateChanged(ChangeEvent e)
          {
            TerminalPanel.this.myClientScrollOrigin = TerminalPanel.this.myBoundedRangeModel.getValue();
            TerminalPanel.this.repaint();
          }
        });
    
    createRepaintTimer();
  }
  
  private boolean isFollowLinkEvent(@NotNull MouseEvent e) {
    if (e == null) $$$reportNull$$$0(4);  return (this.myCursorType == 12 && e.getButton() == 1);
  }
  
  protected void handleMouseWheelEvent(@NotNull MouseWheelEvent e, @NotNull JScrollBar scrollBar) {
    if (e == null) $$$reportNull$$$0(5);  if (scrollBar == null) $$$reportNull$$$0(6);  if (e.isShiftDown() || e.getUnitsToScroll() == 0 || Math.abs(e.getPreciseWheelRotation()) < 0.01D) {
      return;
    }
    moveScrollBar(e.getUnitsToScroll());
    e.consume();
  }
  
  private void handleHyperlinks(@NotNull Point panelPoint, boolean isControlDown) {
    if (panelPoint == null) $$$reportNull$$$0(7);  Cell cell = panelPointToCell(panelPoint);
    HyperlinkStyle linkStyle = findHyperlink(cell);
    LinkInfo.HoverConsumer linkHoverConsumer = (linkStyle != null) ? linkStyle.getLinkInfo().getHoverConsumer() : null;
    if (linkHoverConsumer != this.myLinkHoverConsumer) {
      if (this.myLinkHoverConsumer != null) {
        this.myLinkHoverConsumer.onMouseExited();
      }
      if (linkHoverConsumer != null) {
        LineCellInterval lineCellInterval = findIntervalWithStyle(cell, linkStyle);
        linkHoverConsumer.onMouseEntered(this, getBounds(lineCellInterval));
      } 
    } 
    this.myLinkHoverConsumer = linkHoverConsumer;
    if (linkStyle != null && (
      linkStyle.getHighlightMode() == HyperlinkStyle.HighlightMode.ALWAYS || (linkStyle.getHighlightMode() == HyperlinkStyle.HighlightMode.HOVER && isControlDown))) {
      updateCursor(12);
      this.myHoveredHyperlink = linkStyle.getLinkInfo();
      
      return;
    } 
    
    this.myHoveredHyperlink = null;
    if (this.myCursorType != 0) {
      updateCursor(0);
      repaint();
    } 
  }
  @NotNull
  private LineCellInterval findIntervalWithStyle(@NotNull Cell initialCell, @NotNull HyperlinkStyle style) {
    if (initialCell == null) $$$reportNull$$$0(8);  if (style == null) $$$reportNull$$$0(9);  int startColumn = initialCell.getColumn();
    while (startColumn > 0 && style == this.myTerminalTextBuffer.getStyleAt(startColumn - 1, initialCell.getLine())) {
      startColumn--;
    }
    int endColumn = initialCell.getColumn();
    while (endColumn < this.myTerminalTextBuffer.getWidth() - 1 && style == this.myTerminalTextBuffer.getStyleAt(endColumn + 1, initialCell.getLine())) {
      endColumn++;
    }
    return new LineCellInterval(initialCell.getLine(), startColumn, endColumn);
  }
  
  private void handleHyperlinks(Component component, boolean controlDown) {
    PointerInfo a = MouseInfo.getPointerInfo();
    if (a != null) {
      Point b = a.getLocation();
      SwingUtilities.convertPointFromScreen(b, component);
      handleHyperlinks(b, controlDown);
    } 
  }
  @Nullable
  private HyperlinkStyle findHyperlink(@NotNull Point p) {
    if (p == null) $$$reportNull$$$0(10);  return findHyperlink(panelPointToCell(p));
  }
  @Nullable
  private HyperlinkStyle findHyperlink(@Nullable Cell cell) {
    if (cell != null && cell.getColumn() >= 0 && cell.getColumn() < this.myTerminalTextBuffer.getWidth() && cell
      .getLine() >= -this.myTerminalTextBuffer.getHistoryLinesCount() && cell.getLine() <= this.myTerminalTextBuffer.getHeight()) {
      TextStyle style = this.myTerminalTextBuffer.getStyleAt(cell.getColumn(), cell.getLine());
      if (style instanceof HyperlinkStyle) {
        return (HyperlinkStyle)style;
      }
    } 
    return null;
  }
  
  private void updateCursor(int cursorType) {
    if (cursorType != this.myCursorType) {
      this.myCursorType = cursorType;
      
      setCursor(new Cursor(this.myCursorType));
    } 
  }
  
  private void createRepaintTimer() {
    if (this.myRepaintTimer != null) {
      this.myRepaintTimer.stop();
    }
    this.myRepaintTimer = new Timer(1000 / this.myMaxFPS, new WeakRedrawTimer(this));
    this.myRepaintTimer.start();
  }
  
  public boolean isLocalMouseAction(MouseEvent e) {
    return (this.mySettingsProvider.forceActionOnMouseReporting() || isMouseReporting() == e.isShiftDown());
  }
  
  public boolean isRemoteMouseAction(MouseEvent e) {
    return (isMouseReporting() && !e.isShiftDown());
  }
  
  protected boolean isRetina() {
    return UIUtil.isRetina();
  }
  
  public void setBlinkingPeriod(int blinkingPeriod) {
    this.myBlinkingPeriod = blinkingPeriod;
  }
  
  public void setCoordAccessor(TerminalCoordinates coordAccessor) {
    this.myCoordsAccessor = coordAccessor;
  }
  
  public void setFindResult(SubstringFinder.FindResult findResult) {
    this.myFindResult = findResult;
    repaint();
  }
  
  public SubstringFinder.FindResult getFindResult() {
    return this.myFindResult;
  }
  
  public SubstringFinder.FindResult.FindItem selectPrevFindResultItem() {
    return selectPrevOrNextFindResultItem(false);
  }
  
  public SubstringFinder.FindResult.FindItem selectNextFindResultItem() {
    return selectPrevOrNextFindResultItem(true);
  }
  
  protected SubstringFinder.FindResult.FindItem selectPrevOrNextFindResultItem(boolean next) {
    if (this.myFindResult != null) {
      SubstringFinder.FindResult.FindItem item = next ? this.myFindResult.nextFindItem() : this.myFindResult.prevFindItem();
      if (item != null) {
        this
          .mySelection = new TerminalSelection(new Point((item.getStart()).x, (item.getStart()).y - this.myTerminalTextBuffer.getHistoryLinesCount()), new Point((item.getEnd()).x, (item.getEnd()).y - this.myTerminalTextBuffer.getHistoryLinesCount()));
        if ((this.mySelection.getStart()).y < getTerminalTextBuffer().getHeight() / 2) {
          this.myBoundedRangeModel.setValue((this.mySelection.getStart()).y - getTerminalTextBuffer().getHeight() / 2);
        } else {
          this.myBoundedRangeModel.setValue(0);
        } 
        repaint();
        return item;
      } 
    } 
    return null;
  }
  
  static class WeakRedrawTimer
    implements ActionListener {
    private WeakReference<TerminalPanel> ref;
    
    public WeakRedrawTimer(TerminalPanel terminalPanel) {
      this.ref = new WeakReference<>(terminalPanel);
    }

    
    public void actionPerformed(ActionEvent e) {
      TerminalPanel terminalPanel = this.ref.get();
      if (terminalPanel != null) {
        terminalPanel.myCursor.changeStateIfNeeded();
        terminalPanel.updateScrolling(false);
        if (terminalPanel.needRepaint.getAndSet(false)) {
          try {
            terminalPanel.doRepaint();
          } catch (Exception ex) {
            TerminalPanel.LOG.error("Error while terminal panel redraw", ex);
          } 
        }
      } else {
        Timer timer = (Timer)e.getSource();
        timer.removeActionListener(this);
        timer.stop();
      } 
    }
  }

  
  public void terminalMouseModeSet(MouseMode mode) {
    this.myMouseMode = mode;
  }
  
  private boolean isMouseReporting() {
    return (this.myMouseMode != MouseMode.MOUSE_REPORTING_NONE);
  }
  
  private void scrollToBottom() {
    this.myBoundedRangeModel.setValue(this.myTermSize.height);
  }
  
  private void pageUp() {
    moveScrollBar(-this.myTermSize.height);
  }
  
  private void pageDown() {
    moveScrollBar(this.myTermSize.height);
  }
  
  private void scrollUp() {
    moveScrollBar(-1);
  }
  
  private void scrollDown() {
    moveScrollBar(1);
  }
  
  private void moveScrollBar(int k) {
    this.myBoundedRangeModel.setValue(this.myBoundedRangeModel.getValue() + k);
  }
  
  protected Font createFont() {
    return this.mySettingsProvider.getTerminalFont();
  }
  @NotNull
  private Point panelToCharCoords(Point p) {
    Cell cell = panelPointToCell(p);
    return new Point(cell.getColumn(), cell.getLine());
  }
  @NotNull
  private Cell panelPointToCell(@NotNull Point p) {
    if (p == null) $$$reportNull$$$0(11);  int y = Math.min(p.y / this.myCharSize.height, getRowCount() - 1) + this.myClientScrollOrigin;
    
    TerminalLine buffer = this.myTerminalTextBuffer.getLine(y);
    
    int bufferLen = buffer.getText().length();
    int insetX = p.x - getInsetX();
    int _x = 0;
    int x = 0;
    for (int i = 0; i < bufferLen && 
      insetX > _x; i++) {
      char c = buffer.charAt(i);
      _x += getGraphics().getFontMetrics(getFontToDisplay(c, TextStyle.EMPTY)).charWidth(c);
      if (insetX > _x) {
        x++;
      }
    } 



    
    x = Math.min(x, getColumnCount() - 1);
    x = Math.max(0, x);
    return new Cell(y, x);
  }


  
  private void copySelection(@Nullable Point selectionStart, @Nullable Point selectionEnd, boolean useSystemSelectionClipboardIfAvailable) {
    if (selectionStart == null || selectionEnd == null) {
      return;
    }
    String selectionText = SelectionUtil.getSelectionText(selectionStart, selectionEnd, this.myTerminalTextBuffer);
    if (selectionText.length() != 0) {
      this.myCopyPasteHandler.setContents(selectionText, useSystemSelectionClipboardIfAvailable);
    }
  }
  
  private void pasteFromClipboard(boolean useSystemSelectionClipboardIfAvailable) {
    String text = this.myCopyPasteHandler.getContents(useSystemSelectionClipboardIfAvailable);
    
    if (text == null) {
      return;
    }


    
    try {
      if (!UIUtil.isWindows)
      {

        
        text = text.replace("\r\n", "\n");
      }
      text = text.replace('\n', '\r');
      
      this.myTerminalStarter.sendString(text);
    } catch (RuntimeException e) {
      LOG.info(e);
    } 
  }
  
  @Nullable
  private String getClipboardString() {
    return this.myCopyPasteHandler.getContents(false);
  }
  
  protected void drawImage(Graphics2D gfx, BufferedImage image, int x, int y, ImageObserver observer) {
    gfx.drawImage(image, x, y, image
        .getWidth(), image.getHeight(), observer);
  }
  
  protected BufferedImage createBufferedImage(int width, int height) {
    return new BufferedImage(width, height, 1);
  }

  
  @Nullable
  public Dimension getTerminalSizeFromComponent() {
    int newWidth = (getWidth() - getInsetX()) / this.myCharSize.width;
    int newHeight = getHeight() / this.myCharSize.height;
    return (newHeight > 0 && newWidth > 0) ? new Dimension(newWidth, newHeight) : null;
  }
  
  private void sizeTerminalFromComponent() {
    if (this.myTerminalStarter != null) {
      Dimension newSize = getTerminalSizeFromComponent();
      if (newSize != null) {
        JediTerminal.ensureTermMinimumSize(newSize);
        if (!this.myTermSize.equals(newSize)) {
          this.myTerminalStarter.postResize(newSize, RequestOrigin.User);
        }
      } 
    } 
  }
  
  public void setTerminalStarter(TerminalStarter terminalStarter) {
    this.myTerminalStarter = terminalStarter;
    sizeTerminalFromComponent();
  }
  
  public void addCustomKeyListener(@NotNull KeyListener keyListener) {
    if (keyListener == null) $$$reportNull$$$0(12);  this.myCustomKeyListeners.add(keyListener);
  }
  
  public void removeCustomKeyListener(@NotNull KeyListener keyListener) {
    if (keyListener == null) $$$reportNull$$$0(13);  this.myCustomKeyListeners.remove(keyListener);
  }




  
  public void requestResize(@NotNull Dimension newSize, RequestOrigin origin, int cursorX, int cursorY, JediTerminal.ResizeHandler resizeHandler) {
    if (newSize == null) $$$reportNull$$$0(14);  if (!newSize.equals(this.myTermSize)) {
      this.myTerminalTextBuffer.resize(newSize, origin, cursorX, cursorY, resizeHandler, this.mySelection);
      this.myTermSize = (Dimension)newSize.clone();
      
      Dimension pixelDimension = new Dimension(getPixelWidth(), getPixelHeight());
      setPreferredSize(pixelDimension);
      if (this.myTerminalPanelListener != null) {
        this.myTerminalPanelListener.onPanelResize(origin);
      }
      SwingUtilities.invokeLater(() -> updateScrolling(true));
    } 
  }
  
  public void setTerminalPanelListener(TerminalPanelListener terminalPanelListener) {
    this.myTerminalPanelListener = terminalPanelListener;
  }
  
  private void establishFontMetrics() {
    BufferedImage img = createBufferedImage(1, 1);
    Graphics2D graphics = img.createGraphics();
    graphics.setFont(this.myNormalFont);
    
    float lineSpacing = this.mySettingsProvider.getLineSpacing();
    FontMetrics fo = graphics.getFontMetrics();
    
    this.myCharSize.width = fo.charWidth('W');
    int fontMetricsHeight = fo.getHeight();
    this.myCharSize.height = (int)Math.ceil((fontMetricsHeight * lineSpacing));
    this.mySpaceBetweenLines = Math.max(0, (this.myCharSize.height - fontMetricsHeight) / 2 * 2);
    this.myDescent = fo.getDescent();
    if (LOG.isDebugEnabled()) {

      
      int oldCharHeight = fontMetricsHeight + (int)(lineSpacing * 2.0F) + 2;
      int oldDescent = fo.getDescent() + (int)lineSpacing;
      LOG.debug("charHeight=" + oldCharHeight + "->" + this.myCharSize.height + ", descent=" + oldDescent + "->" + this.myDescent);
    } 

    
    this.myMonospaced = isMonospaced(fo);
    if (!this.myMonospaced) {
      LOG.info("WARNING: Font " + this.myNormalFont.getName() + " is non-monospaced");
    }
    
    img.flush();
    graphics.dispose();
  }
  
  private static boolean isMonospaced(FontMetrics fontMetrics) {
    boolean isMonospaced = true;
    int charWidth = -1;
    for (int codePoint = 0; codePoint < 128; codePoint++) {
      if (Character.isValidCodePoint(codePoint)) {
        char character = (char)codePoint;
        if (isWordCharacter(character)) {
          int w = fontMetrics.charWidth(character);
          if (charWidth != -1) {
            if (w != charWidth) {
              isMonospaced = false;
              break;
            } 
          } else {
            charWidth = w;
          } 
        } 
      } 
    } 
    return isMonospaced;
  }
  
  private static boolean isWordCharacter(char character) {
    return Character.isLetterOrDigit(character);
  }
  
  protected void setupAntialiasing(Graphics graphics) {
    if (graphics instanceof Graphics2D) {
      Graphics2D myGfx = (Graphics2D)graphics;
      Object mode = this.mySettingsProvider.useAntialiasing() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      
      RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, mode);
      
      myGfx.setRenderingHints(hints);
    } 
  }

  
  public Color getBackground() {
    return getPalette().getBackground(this.myStyleState.getBackground());
  }

  
  public Color getForeground() {
    return getPalette().getForeground(this.myStyleState.getForeground());
  }

  
  public void paintComponent(Graphics g) {
    final Graphics2D gfx = (Graphics2D)g;
    
    setupAntialiasing(gfx);
    
    gfx.setColor(getBackground());
    
    gfx.fillRect(0, 0, getWidth(), getHeight());
    
    try {
      this.myTerminalTextBuffer.lock();
      
      updateScrolling(false);
      this.myTerminalTextBuffer.processHistoryAndScreenLines(this.myClientScrollOrigin, this.myTermSize.height, new StyledTextConsumer() {
            final int columnCount = TerminalPanel.this.getColumnCount();

            
            public void consume(int x, int y, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
              if (style == null) $$$reportNull$$$0(0);  if (characters == null) $$$reportNull$$$0(1);  int row = y - startRow;
              TerminalPanel.this.drawCharacters(x, row, style, characters, gfx, false);
              
              if (TerminalPanel.this.myFindResult != null) {
                List<Pair<Integer, Integer>> ranges = TerminalPanel.this.myFindResult.getRanges(characters);
                if (ranges != null) {
                  for (Pair<Integer, Integer> range : ranges) {
                    TextStyle foundPatternStyle = TerminalPanel.this.getFoundPattern(style);
                    CharBuffer foundPatternChars = characters.subBuffer(range);
                    
                    TerminalPanel.this.drawCharacters(x + ((Integer)range.first).intValue(), row, foundPatternStyle, foundPatternChars, gfx);
                  } 
                }
              } 
              
              if (TerminalPanel.this.mySelection != null) {
                Pair<Integer, Integer> interval = TerminalPanel.this.mySelection.intersect(x, row + TerminalPanel.this.myClientScrollOrigin, characters.length());
                if (interval != null) {
                  TextStyle selectionStyle = TerminalPanel.this.getSelectionStyle(style);
                  CharBuffer selectionChars = characters.subBuffer(((Integer)interval.first).intValue() - x, ((Integer)interval.second).intValue());
                  
                  TerminalPanel.this.drawCharacters(((Integer)interval.first).intValue(), row, selectionStyle, selectionChars, gfx);
                } 
              } 
            }

            
            public void consumeNul(int x, int y, int nulIndex, TextStyle style, CharBuffer characters, int startRow) {
              int row = y - startRow;
              if (TerminalPanel.this.mySelection != null) {
                
                Pair<Integer, Integer> interval = TerminalPanel.this.mySelection.intersect(nulIndex, row + TerminalPanel.this.myClientScrollOrigin, this.columnCount - nulIndex);
                if (interval != null) {
                  TextStyle selectionStyle = TerminalPanel.this.getSelectionStyle(style);
                  TerminalPanel.this.drawCharacters(x, row, selectionStyle, characters, gfx);
                  return;
                } 
              } 
              TerminalPanel.this.drawCharacters(x, row, style, characters, gfx);
            }

            
            public void consumeQueue(int x, int y, int nulIndex, int startRow) {
              if (x < this.columnCount) {
                consumeNul(x, y, nulIndex, TextStyle.EMPTY, new CharBuffer(' ', this.columnCount - x), startRow);
              }
            }
          });
      
      int cursorY = this.myCursor.getCoordY();
      if (this.myClientScrollOrigin + getRowCount() > cursorY && !hasUncommittedChars()) {
        int cursorX = this.myCursor.getCoordX();
        Pair<Character, TextStyle> sc = this.myTerminalTextBuffer.getStyledCharAt(cursorX, cursorY);
        String cursorChar = "" + sc.first;
        if (Character.isHighSurrogate(((Character)sc.first).charValue())) {
          cursorChar = cursorChar + (this.myTerminalTextBuffer.getStyledCharAt(cursorX + 1, cursorY)).first;
        }
        TextStyle normalStyle = (sc.second != null) ? (TextStyle)sc.second : this.myStyleState.getCurrent();
        TextStyle selectionStyle = getSelectionStyle(normalStyle);
        boolean inSelection = inSelection(cursorX, cursorY);
        this.myCursor.drawCursor(cursorChar, gfx, inSelection ? selectionStyle : normalStyle);
      } 
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
    
    drawInputMethodUncommitedChars(gfx);
    
    drawMargins(gfx, getWidth(), getHeight());
  }
  
  @NotNull
  private TextStyle getSelectionStyle(@NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(15);  if (this.mySettingsProvider.useInverseSelectionColor()) {
      return getInversedStyle(style);
    }
    TextStyle.Builder builder = style.toBuilder();
    TextStyle mySelectionStyle = this.mySettingsProvider.getSelectionColor();
    builder.setBackground(mySelectionStyle.getBackground());
    builder.setForeground(mySelectionStyle.getForeground());
    if (builder instanceof HyperlinkStyle.Builder) {
      if (((HyperlinkStyle.Builder)builder).build(true) == null) $$$reportNull$$$0(16);  return (TextStyle)((HyperlinkStyle.Builder)builder).build(true);
    } 
    if (builder.build() == null) $$$reportNull$$$0(17);  return builder.build();
  }
  
  @NotNull
  private TextStyle getFoundPattern(@NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(18);  TextStyle.Builder builder = style.toBuilder();
    TextStyle foundPattern = this.mySettingsProvider.getFoundPatternColor();
    builder.setBackground(foundPattern.getBackground());
    builder.setForeground(foundPattern.getForeground());
    if (builder.build() == null) $$$reportNull$$$0(19);  return builder.build();
  }
  
  private void drawInputMethodUncommitedChars(Graphics2D gfx) {
    if (hasUncommittedChars()) {
      int xCoord = computexCoord(this.myCursor.getCoordX() + 1, this.myCursor.getCoordY()) + getInsetX();
      
      int y = this.myCursor.getCoordY() + 1;
      
      int yCoord = y * this.myCharSize.height - 3;
      
      int len = computexCoordByCharBuffer(0, this.myInputMethodUncommittedChars.length(), new CharBuffer(this.myInputMethodUncommittedChars));
      
      gfx.setColor(getBackground());
      gfx.fillRect(xCoord, (y - 1) * this.myCharSize.height - 3, len, this.myCharSize.height);
      
      gfx.setColor(getForeground());
      gfx.setFont(this.myNormalFont);
      
      gfx.drawString(this.myInputMethodUncommittedChars, xCoord, yCoord);
      Stroke saved = gfx.getStroke();
      BasicStroke dotted = new BasicStroke(1.0F, 1, 1, 0.0F, new float[] { 0.0F, 2.0F, 0.0F, 2.0F }, 0.0F);
      gfx.setStroke(dotted);
      
      gfx.drawLine(xCoord, yCoord, xCoord + len, yCoord);
      gfx.setStroke(saved);
    } 
  }
  
  private boolean hasUncommittedChars() {
    return (this.myInputMethodUncommittedChars != null && this.myInputMethodUncommittedChars.length() > 0);
  }
  
  private boolean inSelection(int x, int y) {
    return (this.mySelection != null && this.mySelection.contains(new Point(x, y)));
  }

  
  public void processKeyEvent(KeyEvent e) {
    handleKeyEvent(e);
    handleHyperlinks(e.getComponent(), e.isControlDown());
  }
  
  private int computexCoord(int coordX, int coordY) {
    int xCoord = 0;
    Graphics gfx = getGraphics();
    char[] chars = this.myTerminalTextBuffer.getLine(this.myClientScrollOrigin + coordY).getText().toCharArray();
    for (int i = 0; i < coordX; i++) {
      char c = (i < chars.length) ? chars[i] : ' ';
      Font font = getFontToDisplay(c, TextStyle.EMPTY);
      xCoord += gfx.getFontMetrics(font).charWidth(c);
    } 
    return xCoord;
  }
  
  private int computexCoordByCharBuffer(int start, int end, CharBuffer buf) {
    int _width = 0;
    int textLength = end - start;
    Graphics gfx = getGraphics();
    for (int i = 0; i < textLength; i++) {
      char c = buf.charAt(i);
      Font font = getFontToDisplay(c, TextStyle.EMPTY);
      _width += gfx.getFontMetrics(font).charWidth(c);
    } 
    return _width;
  }

  
  public void handleKeyEvent(@NotNull KeyEvent e) {
    if (e == null) $$$reportNull$$$0(20);  int id = e.getID();
    if (id == 401) {
      for (KeyListener keyListener : this.myCustomKeyListeners) {
        keyListener.keyPressed(e);
      }
    } else if (id == 400) {
      for (KeyListener keyListener : this.myCustomKeyListeners) {
        keyListener.keyTyped(e);
      }
    } 
  }
  
  public int getPixelWidth() {
    return this.myCharSize.width * this.myTermSize.width + getInsetX();
  }
  
  public int getPixelHeight() {
    return this.myCharSize.height * this.myTermSize.height;
  }
  
  public int getColumnCount() {
    return this.myTermSize.width;
  }
  
  public int getRowCount() {
    return this.myTermSize.height;
  }
  
  public String getWindowTitle() {
    return this.myWindowTitle;
  }
  
  protected int getInsetX() {
    return 4;
  }
  
  public void addTerminalMouseListener(final TerminalMouseListener listener) {
    addMouseListener(new MouseAdapter()
        {
          public void mousePressed(MouseEvent e) {
            if (TerminalPanel.this.mySettingsProvider.enableMouseReporting() && TerminalPanel.this.isRemoteMouseAction(e)) {
              Point p = TerminalPanel.this.panelToCharCoords(e.getPoint());
              listener.mousePressed(p.x, p.y, e);
            } 
          }

          
          public void mouseReleased(MouseEvent e) {
            if (TerminalPanel.this.mySettingsProvider.enableMouseReporting() && TerminalPanel.this.isRemoteMouseAction(e)) {
              Point p = TerminalPanel.this.panelToCharCoords(e.getPoint());
              listener.mouseReleased(p.x, p.y, e);
            } 
          }
        });
    
    addMouseWheelListener(new MouseWheelListener()
        {
          public void mouseWheelMoved(MouseWheelEvent e) {
            if (TerminalPanel.this.mySettingsProvider.enableMouseReporting() && TerminalPanel.this.isRemoteMouseAction(e)) {
              TerminalPanel.this.mySelection = null;
              Point p = TerminalPanel.this.panelToCharCoords(e.getPoint());
              listener.mouseWheelMoved(p.x, p.y, e);
            } 
          }
        });
    
    addMouseMotionListener(new MouseMotionAdapter()
        {
          public void mouseMoved(MouseEvent e) {
            if (TerminalPanel.this.mySettingsProvider.enableMouseReporting() && TerminalPanel.this.isRemoteMouseAction(e)) {
              Point p = TerminalPanel.this.panelToCharCoords(e.getPoint());
              listener.mouseMoved(p.x, p.y, e);
            } 
          }

          
          public void mouseDragged(MouseEvent e) {
            if (TerminalPanel.this.mySettingsProvider.enableMouseReporting() && TerminalPanel.this.isRemoteMouseAction(e)) {
              Point p = TerminalPanel.this.panelToCharCoords(e.getPoint());
              listener.mouseDragged(p.x, p.y, e);
            } 
          }
        });
  }
  
  @NotNull
  KeyListener getTerminalKeyListener() {
    if (this.myTerminalKeyHandler == null) $$$reportNull$$$0(21);  return this.myTerminalKeyHandler;
  }
  
  public enum TerminalCursorState {
    SHOWING, HIDDEN, NO_FOCUS;
  }

  
  public class TerminalCursor
  {
    private boolean myCursorIsShown;
    protected Point myCursorCoordinates = new Point();
    private CursorShape myShape = CursorShape.BLINK_BLOCK;
    
    private boolean myShouldDrawCursor = true;
    
    private boolean myBlinking = true;
    
    private long myLastCursorChange;
    private boolean myCursorHasChanged;
    
    public void setX(int x) {
      this.myCursorCoordinates.x = x;
      cursorChanged();
    }
    
    public void setY(int y) {
      this.myCursorCoordinates.y = y;
      cursorChanged();
    }
    
    public int getCoordX() {
      return this.myCursorCoordinates.x;
    }
    
    public int getCoordY() {
      return this.myCursorCoordinates.y - 1 - TerminalPanel.this.myClientScrollOrigin;
    }
    
    public void setShouldDrawCursor(boolean shouldDrawCursor) {
      this.myShouldDrawCursor = shouldDrawCursor;
    }
    
    public void setBlinking(boolean blinking) {
      this.myBlinking = blinking;
    }
    
    public boolean isBlinking() {
      return (this.myBlinking && TerminalPanel.this.getBlinkingPeriod() > 0);
    }
    
    public void cursorChanged() {
      this.myCursorHasChanged = true;
      this.myLastCursorChange = System.currentTimeMillis();
      TerminalPanel.this.repaint();
    }
    
    private boolean cursorShouldChangeBlinkState(long currentTime) {
      return (currentTime - this.myLastCursorChange > TerminalPanel.this.getBlinkingPeriod());
    }
    
    public void changeStateIfNeeded() {
      if (!TerminalPanel.this.isFocusOwner()) {
        return;
      }
      long currentTime = System.currentTimeMillis();
      if (cursorShouldChangeBlinkState(currentTime)) {
        this.myCursorIsShown = !this.myCursorIsShown;
        this.myLastCursorChange = currentTime;
        this.myCursorHasChanged = false;
        TerminalPanel.this.repaint();
      } 
    }
    
    private TerminalPanel.TerminalCursorState computeBlinkingState() {
      if (!isBlinking() || this.myCursorHasChanged || this.myCursorIsShown) {
        return TerminalPanel.TerminalCursorState.SHOWING;
      }
      return TerminalPanel.TerminalCursorState.HIDDEN;
    }
    
    private TerminalPanel.TerminalCursorState computeCursorState() {
      if (!this.myShouldDrawCursor) {
        return TerminalPanel.TerminalCursorState.HIDDEN;
      }
      if (!TerminalPanel.this.isFocusOwner()) {
        return TerminalPanel.TerminalCursorState.NO_FOCUS;
      }
      return computeBlinkingState();
    }
    
    void drawCursor(String c, Graphics2D gfx, TextStyle style) {
      TerminalPanel.TerminalCursorState state = computeCursorState();

      
      if (state == TerminalPanel.TerminalCursorState.HIDDEN) {
        return;
      }
      
      int x = getCoordX();
      int y = getCoordY();
      
      if (y < 0 || y >= TerminalPanel.this.myTermSize.height) {
        return;
      }
      
      CharBuffer buf = new CharBuffer(c);
      int xCoord = TerminalPanel.this.computexCoord(x, y) + TerminalPanel.this.getInsetX();
      int yCoord = y * TerminalPanel.this.myCharSize.height;
      int textLength = buf.length();
      int height = Math.min(TerminalPanel.this.myCharSize.height, TerminalPanel.this.getHeight() - yCoord);
      int width = Math.min(TerminalPanel.this.computexCoordByCharBuffer(0, textLength, buf), TerminalPanel.this.getWidth() - xCoord);
      int lineStrokeSize = 2;
      
      Color fgColor = TerminalPanel.this.getPalette().getForeground(TerminalPanel.this.myStyleState.getForeground(style.getForegroundForRun()));
      TextStyle inversedStyle = TerminalPanel.this.getInversedStyle(style);
      Color inverseBg = TerminalPanel.this.getPalette().getBackground(TerminalPanel.this.myStyleState.getBackground(inversedStyle.getBackgroundForRun()));
      
      switch (this.myShape) {
        case BLINK_BLOCK:
        case STEADY_BLOCK:
          if (state == TerminalPanel.TerminalCursorState.SHOWING) {
            gfx.setColor(inverseBg);
            gfx.fillRect(xCoord, yCoord, width, height);
            TerminalPanel.this.drawCharacters(x, y, inversedStyle, buf, gfx); break;
          } 
          gfx.setColor(fgColor);
          gfx.drawRect(xCoord, yCoord, width, height);
          break;

        
        case BLINK_UNDERLINE:
        case STEADY_UNDERLINE:
          gfx.setColor(fgColor);
          gfx.fillRect(xCoord, yCoord + height, width, lineStrokeSize);
          break;
        
        case BLINK_VERTICAL_BAR:
        case STEADY_VERTICAL_BAR:
          gfx.setColor(fgColor);
          gfx.fillRect(xCoord, yCoord, lineStrokeSize, height);
          break;
      } 
    }
    
    void setShape(CursorShape shape) {
      this.myShape = shape;
    }
  }
  
  private int getBlinkingPeriod() {
    if (this.myBlinkingPeriod != this.mySettingsProvider.caretBlinkingMs()) {
      setBlinkingPeriod(this.mySettingsProvider.caretBlinkingMs());
    }
    return this.myBlinkingPeriod;
  }
  
  protected void drawImage(Graphics2D g, BufferedImage image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
    g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
  }
  
  @NotNull
  private TextStyle getInversedStyle(@NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(22);  TextStyle.Builder builder = new TextStyle.Builder(style);
    builder.setOption(TextStyle.Option.INVERSE, !style.hasOption(TextStyle.Option.INVERSE));
    if (style.getForeground() == null) {
      builder.setForeground(this.myStyleState.getForeground());
    }
    if (style.getBackground() == null) {
      builder.setBackground(this.myStyleState.getBackground());
    }
    if (builder.build() == null) $$$reportNull$$$0(23);  return builder.build();
  }
  
  private void drawCharacters(int x, int y, TextStyle style, CharBuffer buf, Graphics2D gfx) {
    drawCharacters(x, y, style, buf, gfx, true);
  }

  
  private void drawCharacters(int x, int y, TextStyle style, CharBuffer buf, Graphics2D gfx, boolean includeSpaceBetweenLines) {
    int xCoord = computexCoord(x, y) + getInsetX();
    int yCoord = y * this.myCharSize.height + (includeSpaceBetweenLines ? 0 : (this.mySpaceBetweenLines / 2));
    
    if (xCoord < 0 || xCoord > getWidth() || yCoord < 0 || yCoord > getHeight()) {
      return;
    }
    
    int textLength = buf.length();
    int height = Math.min(this.myCharSize.height - (includeSpaceBetweenLines ? 0 : this.mySpaceBetweenLines), getHeight() - yCoord);
    int width = Math.min(computexCoordByCharBuffer(0, textLength, buf), getWidth() - xCoord);
    
    if (style instanceof HyperlinkStyle) {
      HyperlinkStyle hyperlinkStyle = (HyperlinkStyle)style;
      
      if (hyperlinkStyle.getHighlightMode() == HyperlinkStyle.HighlightMode.ALWAYS || (isHoveredHyperlink(hyperlinkStyle) && hyperlinkStyle.getHighlightMode() == HyperlinkStyle.HighlightMode.HOVER))
      {
        
        style = hyperlinkStyle.getHighlightStyle();
      }
    } 
    
    Color backgroundColor = getPalette().getBackground(this.myStyleState.getBackground(style.getBackgroundForRun()));
    gfx.setColor(backgroundColor);
    gfx.fillRect(xCoord, yCoord, width, height);



    
    if (buf.isNul()) {
      return;
    }
    
    drawChars(x, y, buf, style, gfx);
    
    gfx.setColor(getStyleForeground(style));

    
    if (style.hasOption(TextStyle.Option.UNDERLINED)) {
      int baseLine = (y + 1) * this.myCharSize.height - this.mySpaceBetweenLines / 2 - this.myDescent;
      int lineY = baseLine + 3;
      gfx.drawLine(xCoord, lineY, computexCoord(x + textLength, lineY) + getInsetX(), lineY);
    } 
  }
  
  private boolean isHoveredHyperlink(@NotNull HyperlinkStyle link) {
    if (link == null) $$$reportNull$$$0(24);  return (this.myHoveredHyperlink == link.getLinkInfo());
  }



  
  private void drawChars(int x, int y, CharBuffer buf, TextStyle style, Graphics2D gfx) {
    CharBuffer renderingBuffer;
    int blockLen = 1;
    int offset = 0;
    int drawCharsOffset = 0;



    
    if (this.mySettingsProvider.DECCompatibilityMode() && style.hasOption(TextStyle.Option.BOLD)) {
      renderingBuffer = CharUtils.heavyDecCompatibleBuffer(buf);
    } else {
      renderingBuffer = buf;
    } 
    
    while (offset + blockLen <= buf.length()) {
      
      Font font = getFontToDisplay(buf.charAt(offset + blockLen - 1), style);






      
      gfx.setFont(font);
      
      int descent = gfx.getFontMetrics(font).getDescent();
      int baseLine = (y + 1) * this.myCharSize.height - this.mySpaceBetweenLines / 2 - descent;
      int xCoord = computexCoord(x + drawCharsOffset, y) + getInsetX();
      
      int yCoord = y * this.myCharSize.height + this.mySpaceBetweenLines / 2;
      
      gfx.setClip(xCoord, yCoord, 
          
          getWidth() - xCoord, 
          getHeight() - yCoord);
      
      gfx.setColor(getStyleForeground(style));
      
      gfx.drawChars(renderingBuffer.getBuf(), buf.getStart() + offset, blockLen, xCoord, baseLine);
      
      drawCharsOffset += blockLen;
      offset += blockLen;
      blockLen = 1;
    } 
    gfx.setClip(null);
  }
  @NotNull
  private Color getStyleForeground(@NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(25);  Color foreground = getPalette().getForeground(this.myStyleState.getForeground(style.getForegroundForRun()));
    if (style.hasOption(TextStyle.Option.DIM)) {
      Color background = getPalette().getBackground(this.myStyleState.getBackground(style.getBackgroundForRun()));


      
      foreground = new Color((foreground.getRed() + background.getRed()) / 2, (foreground.getGreen() + background.getGreen()) / 2, (foreground.getBlue() + background.getBlue()) / 2, foreground.getAlpha());
    } 
    if (foreground == null) $$$reportNull$$$0(26);  return foreground;
  }
  
  protected Font getFontToDisplay(char c, TextStyle style) {
    boolean bold = style.hasOption(TextStyle.Option.BOLD);
    boolean italic = style.hasOption(TextStyle.Option.ITALIC);
    
    if (bold && this.mySettingsProvider.DECCompatibilityMode() && CharacterSets.isDecBoxChar(c)) {
      return this.myNormalFont;
    }
    return bold ? (italic ? this.myBoldItalicFont : this.myBoldFont) : (italic ? this.myItalicFont : this.myNormalFont);
  }

  
  private ColorPalette getPalette() {
    return this.mySettingsProvider.getTerminalColorPalette();
  }
  
  private void drawMargins(Graphics2D gfx, int width, int height) {
    gfx.setColor(getBackground());
    gfx.fillRect(0, height, getWidth(), getHeight() - height);
    gfx.fillRect(width, 0, getWidth() - width, getHeight());
  }

  
  public void scrollArea(int scrollRegionTop, int scrollRegionSize, int dy) {
    this.scrollDy.addAndGet(dy);
    this.mySelection = null;
  }
  
  private void updateScrolling(boolean forceUpdate) {
    int dy = this.scrollDy.getAndSet(0);
    if (dy == 0 && !forceUpdate) {
      return;
    }
    if (this.myScrollingEnabled) {
      int value = this.myBoundedRangeModel.getValue();
      int historyLineCount = this.myTerminalTextBuffer.getHistoryLinesCount();
      if (value == 0) {
        this.myBoundedRangeModel
          .setRangeProperties(0, this.myTermSize.height, -historyLineCount, this.myTermSize.height, false);
      } else {
        
        this.myBoundedRangeModel.setRangeProperties(
            Math.min(Math.max(value + dy, -historyLineCount), this.myTermSize.height), this.myTermSize.height, -historyLineCount, this.myTermSize.height, false);
      }
    
    }
    else {
      
      this.myBoundedRangeModel.setRangeProperties(0, this.myTermSize.height, 0, this.myTermSize.height, false);
    } 
  }
  
  public void setCursor(int x, int y) {
    this.myCursor.setX(x);
    this.myCursor.setY(y);
  }

  
  public void setCursorShape(CursorShape shape) {
    this.myCursor.setShape(shape);
    switch (shape) {
      case STEADY_BLOCK:
      case STEADY_UNDERLINE:
      case STEADY_VERTICAL_BAR:
        this.myCursor.myBlinking = false;
        break;
      case BLINK_BLOCK:
      case BLINK_UNDERLINE:
      case BLINK_VERTICAL_BAR:
        this.myCursor.myBlinking = true;
        break;
    } 
  }
  
  public void beep() {
    if (this.mySettingsProvider.audibleBell())
      Toolkit.getDefaultToolkit().beep(); 
  }
  
  @Nullable
  public Rectangle getBounds(@NotNull TerminalLineIntervalHighlighting highlighting) {
    if (highlighting == null) $$$reportNull$$$0(27);  TerminalLine line = highlighting.getLine();
    int index = this.myTerminalTextBuffer.findScreenLineIndex(line);
    if (index >= 0 && !highlighting.isDisposed()) {
      return getBounds(new LineCellInterval(index, highlighting.getStartOffset(), highlighting.getEndOffset() + 1));
    }
    return null;
  }
  @NotNull
  private Rectangle getBounds(@NotNull LineCellInterval cellInterval) {
    if (cellInterval == null) $$$reportNull$$$0(28); 
    Point topLeft = new Point(cellInterval.getStartColumn() * this.myCharSize.width + getInsetX(), cellInterval.getLine() * this.myCharSize.height);
    return new Rectangle(topLeft, new Dimension(this.myCharSize.width * cellInterval.getCellCount(), this.myCharSize.height));
  }
  
  public BoundedRangeModel getBoundedRangeModel() {
    return this.myBoundedRangeModel;
  }
  
  public TerminalTextBuffer getTerminalTextBuffer() {
    return this.myTerminalTextBuffer;
  }
  
  public TerminalSelection getSelection() {
    return this.mySelection;
  }

  
  public boolean ambiguousCharsAreDoubleWidth() {
    return this.mySettingsProvider.ambiguousCharsAreDoubleWidth();
  }
  
  public LinesBuffer getScrollBuffer() {
    return this.myTerminalTextBuffer.getHistoryBuffer();
  }

  
  public void setCursorVisible(boolean shouldDrawCursor) {
    this.myCursor.setShouldDrawCursor(shouldDrawCursor);
  }
  @NotNull
  protected JPopupMenu createPopupMenu(@Nullable LinkInfo linkInfo, @NotNull final MouseEvent e) {
    if (e == null) $$$reportNull$$$0(29);  JPopupMenu popup = new JPopupMenu();
    final LinkInfo.PopupMenuGroupProvider popupMenuGroupProvider = (linkInfo != null) ? linkInfo.getPopupMenuGroupProvider() : null;
    if (popupMenuGroupProvider != null) {
      TerminalAction.addToMenu(popup, new TerminalActionProvider()
          {
            public List<TerminalAction> getActions() {
              return popupMenuGroupProvider.getPopupMenuGroup(e);
            }

            
            public TerminalActionProvider getNextProvider() {
              return TerminalPanel.this;
            }



            
            public void setNextProvider(TerminalActionProvider provider) {}
          });
    } else {
      TerminalAction.addToMenu(popup, this);
    } 
    
    if (popup == null) $$$reportNull$$$0(30);  return popup;
  }
  
  public void setScrollingEnabled(boolean scrollingEnabled) {
    this.myScrollingEnabled = scrollingEnabled;
    
    SwingUtilities.invokeLater(() -> updateScrolling(true));
  }

  
  public void setBlinkingCursor(boolean enabled) {
    this.myCursor.setBlinking(enabled);
  }
  
  public TerminalCursor getTerminalCursor() {
    return this.myCursor;
  }
  
  public TerminalOutputStream getTerminalOutputStream() {
    return (TerminalOutputStream)this.myTerminalStarter;
  }

  
  public void setWindowTitle(String name) {
    this.myWindowTitle = name;
    if (this.myTerminalPanelListener != null) {
      this.myTerminalPanelListener.onTitleChanged(this.myWindowTitle);
    }
  }

  
  public void setCurrentPath(String path) {
    this.myCurrentPath = path;
  }

  
  public List<TerminalAction> getActions() {
    return Lists.newArrayList((Object[])new TerminalAction[] { (new TerminalAction(this.mySettingsProvider
            .getOpenUrlActionPresentation(), input -> openSelectionAsURL()))
          
          .withEnabledSupplier(this::selectionTextIsUrl), (new TerminalAction(this.mySettingsProvider
            .getCopyActionPresentation(), this::handleCopy)
          {
            public boolean isEnabled(@Nullable KeyEvent e) {
              return (e != null || TerminalPanel.this.mySelection != null);
            }
          }).withMnemonicKey(Integer.valueOf(67)), (new TerminalAction(this.mySettingsProvider
            .getPasteActionPresentation(), input -> {
              handlePaste();
              return true;
            })).withMnemonicKey(Integer.valueOf(80)).withEnabledSupplier(() -> Boolean.valueOf((getClipboardString() != null))), new TerminalAction(this.mySettingsProvider
            .getSelectAllActionPresentation(), input -> { selectAll(); return true; }), (new TerminalAction(this.mySettingsProvider


            
            .getClearBufferActionPresentation(), input -> {
              clearBuffer();
              return true;
            })).withMnemonicKey(Integer.valueOf(75)).withEnabledSupplier(() -> Boolean.valueOf(!this.myTerminalTextBuffer.isUsingAlternateBuffer())).separatorBefore(true), (new TerminalAction(this.mySettingsProvider
            .getPageUpActionPresentation(), input -> {
              pageUp();
              return true;
            })).withEnabledSupplier(() -> Boolean.valueOf(!this.myTerminalTextBuffer.isUsingAlternateBuffer())).separatorBefore(true), (new TerminalAction(this.mySettingsProvider
            .getPageDownActionPresentation(), input -> {
              pageDown();
              return true;
            })).withEnabledSupplier(() -> Boolean.valueOf(!this.myTerminalTextBuffer.isUsingAlternateBuffer())), (new TerminalAction(this.mySettingsProvider
            .getLineUpActionPresentation(), input -> {
              scrollUp();
              return true;
            })).withEnabledSupplier(() -> Boolean.valueOf(!this.myTerminalTextBuffer.isUsingAlternateBuffer())).separatorBefore(true), new TerminalAction(this.mySettingsProvider
            .getLineDownActionPresentation(), input -> {
              scrollDown();
              return true;
            }) });
  }
  
  public void selectAll() {
    this
      .mySelection = new TerminalSelection(new Point(0, -this.myTerminalTextBuffer.getHistoryLinesCount()), new Point(this.myTermSize.width, this.myTerminalTextBuffer.getScreenLinesCount()));
  }
  
  @NotNull
  private Boolean selectionTextIsUrl() {
    String selectionText = getSelectionText();
    if (selectionText != null) {
      try {
        URI uri = new URI(selectionText);
        
        uri.toURL();
        if (Boolean.valueOf(true) == null) $$$reportNull$$$0(31);  return Boolean.valueOf(true);
      } catch (Exception exception) {}
    }

    
    if (Boolean.valueOf(false) == null) $$$reportNull$$$0(32);  return Boolean.valueOf(false);
  }
  
  @Nullable
  private String getSelectionText() {
    if (this.mySelection != null) {
      Pair<Point, Point> points = this.mySelection.pointsForRun(this.myTermSize.width);
      
      if (points.first != null || points.second != null) {
        return 
          SelectionUtil.getSelectionText((Point)points.first, (Point)points.second, this.myTerminalTextBuffer);
      }
    } 

    
    return null;
  }
  
  protected boolean openSelectionAsURL() {
    if (Desktop.isDesktopSupported()) {
      try {
        String selectionText = getSelectionText();
        
        if (selectionText != null) {
          Desktop.getDesktop().browse(new URI(selectionText));
        }
      } catch (Exception exception) {}
    }

    
    return false;
  }
  
  public void clearBuffer() {
    clearBuffer(true);
  }




  
  protected void clearBuffer(boolean keepLastLine) {
    if (!this.myTerminalTextBuffer.isUsingAlternateBuffer()) {
      this.myTerminalTextBuffer.clearHistory();
      
      if (this.myCoordsAccessor != null) {
        if (keepLastLine) {
          if (this.myCoordsAccessor.getY() > 0) {
            TerminalLine lastLine = this.myTerminalTextBuffer.getLine(this.myCoordsAccessor.getY() - 1);
            this.myTerminalTextBuffer.clearAll();
            this.myCoordsAccessor.setY(0);
            this.myCursor.setY(1);
            this.myTerminalTextBuffer.addLine(lastLine);
          } 
        } else {
          
          this.myTerminalTextBuffer.clearAll();
          this.myCoordsAccessor.setX(0);
          this.myCoordsAccessor.setY(1);
          this.myCursor.setX(0);
          this.myCursor.setY(1);
        } 
      }
      
      this.myBoundedRangeModel.setValue(0);
      updateScrolling(true);
      
      this.myClientScrollOrigin = this.myBoundedRangeModel.getValue();
    } 
  }

  
  public TerminalActionProvider getNextProvider() {
    return this.myNextActionProvider;
  }

  
  public void setNextProvider(TerminalActionProvider provider) {
    this.myNextActionProvider = provider;
  }
  
  private void processTerminalKeyPressed(KeyEvent e) {
    if (hasUncommittedChars()) {
      return;
    }
    
    try {
      int keycode = e.getKeyCode();
      char keychar = e.getKeyChar();


      
      if (keycode == 127 && keychar == '.') {
        this.myTerminalStarter.sendBytes(new byte[] { 46 });
        e.consume();
        
        return;
      } 
      if (keychar == ' ' && (e.getModifiers() & 0x2) != 0) {
        this.myTerminalStarter.sendBytes(new byte[] { 0 });
        e.consume();
        
        return;
      } 
      byte[] code = this.myTerminalStarter.getCode(keycode, e.getModifiers());
      if (code != null) {
        this.myTerminalStarter.sendBytes(code);
        e.consume();
        if (this.mySettingsProvider.scrollToBottomOnTyping() && isCodeThatScrolls(keycode)) {
          scrollToBottom();
        }
      }
      else if ((e.getModifiersEx() & 0x200) != 0 && Character.isDefined(keychar) && this.mySettingsProvider
        .altSendsEscape()) {


        
        this.myTerminalStarter.sendString(new String(new char[] { '\033', (char)e.getKeyCode() }));
        e.consume();
      }
      else if (Character.isISOControl(keychar)) {
        processCharacter(e);
      } 
    } catch (Exception ex) {
      LOG.error("Error sending pressed key to emulator", ex);
    } 
  }
  
  private void processCharacter(@NotNull KeyEvent e) {
    if (e == null) $$$reportNull$$$0(33);  if ((e.getModifiersEx() & 0x200) != 0 && this.mySettingsProvider.altSendsEscape()) {
      return;
    }
    char keyChar = e.getKeyChar();
    int modifiers = e.getModifiers();
    
    char[] obuffer = { keyChar };
    
    if (keyChar == '`' && (modifiers & 0x4) != 0) {
      return;
    }

    
    this.myTerminalStarter.sendString(new String(obuffer));
    e.consume();
    
    if (this.mySettingsProvider.scrollToBottomOnTyping()) {
      scrollToBottom();
    }
  }
  
  private static boolean isCodeThatScrolls(int keycode) {
    return (keycode == 38 || keycode == 40 || keycode == 37 || keycode == 39 || keycode == 8 || keycode == 155 || keycode == 127 || keycode == 10 || keycode == 36 || keycode == 35 || keycode == 33 || keycode == 34);
  }











  
  private void processTerminalKeyTyped(KeyEvent e) {
    if (hasUncommittedChars()) {
      return;
    }
    
    char keychar = e.getKeyChar();
    if (!Character.isISOControl(keychar)) {
      try {
        processCharacter(e);
      } catch (Exception ex) {
        LOG.error("Error sending typed key to emulator", ex);
      } 
    }
  }


  
  private class TerminalKeyHandler
    extends KeyAdapter
  {
    public void keyPressed(KeyEvent e) {
      if (!TerminalAction.processEvent(TerminalPanel.this, e)) {
        TerminalPanel.this.processTerminalKeyPressed(e);
      }
    }
    
    public void keyTyped(KeyEvent e) {
      TerminalPanel.this.processTerminalKeyTyped(e);
    }
  }
  
  private void handlePaste() {
    pasteFromClipboard(false);
  }
  
  private void handlePasteSelection() {
    pasteFromClipboard(true);
  }





  
  private void handleCopy(boolean unselect, boolean useSystemSelectionClipboardIfAvailable) {
    if (this.mySelection != null) {
      Pair<Point, Point> points = this.mySelection.pointsForRun(this.myTermSize.width);
      copySelection((Point)points.first, (Point)points.second, useSystemSelectionClipboardIfAvailable);
      if (unselect) {
        this.mySelection = null;
        repaint();
      } 
    } 
  }
  
  private boolean handleCopy(@Nullable KeyEvent e) {
    boolean ctrlC = (e != null && e.getKeyCode() == 67 && e.getModifiersEx() == 128);
    boolean sendCtrlC = (ctrlC && this.mySelection == null);
    handleCopy(ctrlC, false);
    return !sendCtrlC;
  }
  
  private void handleCopyOnSelect() {
    handleCopy(false, true);
  }





  
  protected void processInputMethodEvent(InputMethodEvent e) {
    int commitCount = e.getCommittedCharacterCount();
    
    if (commitCount > 0) {
      this.myInputMethodUncommittedChars = null;
      AttributedCharacterIterator text = e.getText();
      if (text != null) {
        StringBuilder sb = new StringBuilder();

        
        for (char c = text.first(); commitCount > 0; c = text.next(), commitCount--) {
          if (c >= ' ' && c != '') {
            sb.append(c);
          }
        } 
        
        if (sb.length() > 0) {
          this.myTerminalStarter.sendString(sb.toString());
        }
      } 
    } else {
      this.myInputMethodUncommittedChars = uncommittedChars(e.getText());
    } 
  }
  
  private static String uncommittedChars(@Nullable AttributedCharacterIterator text) {
    if (text == null) {
      return null;
    }
    
    StringBuilder sb = new StringBuilder();
    
    for (char c = text.first(); c != Character.MAX_VALUE; c = text.next()) {
      if (c >= ' ' && c != '') {
        sb.append(c);
      }
    } 
    
    return sb.toString();
  }

  
  public InputMethodRequests getInputMethodRequests() {
    return new MyInputMethodRequests();
  }
  
  private class MyInputMethodRequests implements InputMethodRequests { private MyInputMethodRequests() {}
    
    public Rectangle getTextLocation(TextHitInfo offset) {
      Rectangle r = new Rectangle(TerminalPanel.this.computexCoord(TerminalPanel.this.myCursor.getCoordX(), TerminalPanel.this.myCursor.getCoordY()) + TerminalPanel.this.getInsetX(), (TerminalPanel.this.myCursor.getCoordY() + 1) * TerminalPanel.this.myCharSize.height, 0, 0);
      
      Point p = TerminalPanel.this.getLocationOnScreen();
      r.translate(p.x, p.y);
      return r;
    }

    
    @Nullable
    public TextHitInfo getLocationOffset(int x, int y) {
      return null;
    }

    
    public int getInsertPositionOffset() {
      return 0;
    }

    
    public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes) {
      return null;
    }

    
    public int getCommittedTextLength() {
      return 0;
    }

    
    @Nullable
    public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
      return null;
    }

    
    @Nullable
    public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
      return null;
    } }


  
  public void dispose() {
    this.myRepaintTimer.stop();
  }
}
