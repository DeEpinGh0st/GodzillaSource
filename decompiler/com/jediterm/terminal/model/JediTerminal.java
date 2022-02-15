package com.jediterm.terminal.model;
import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.RequestOrigin;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.SubstringFinder;
import com.jediterm.terminal.TerminalDisplay;
import com.jediterm.terminal.TerminalKeyEncoder;
import com.jediterm.terminal.TerminalMode;
import com.jediterm.terminal.TerminalOutputStream;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.charset.CharacterSet;
import com.jediterm.terminal.emulator.charset.GraphicSet;
import com.jediterm.terminal.emulator.charset.GraphicSetState;
import com.jediterm.terminal.emulator.mouse.MouseFormat;
import com.jediterm.terminal.emulator.mouse.MouseMode;
import com.jediterm.terminal.util.CharUtils;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

public class JediTerminal implements Terminal, TerminalMouseListener, TerminalCoordinates {
  private static final Logger LOG = Logger.getLogger(JediTerminal.class.getName());
  
  private static final int MIN_WIDTH = 5;
  
  private static final int MIN_HEIGHT = 2;
  private int myScrollRegionTop;
  private int myScrollRegionBottom;
  private volatile int myCursorX = 0;
  private volatile int myCursorY = 1;
  
  private int myTerminalWidth;
  
  private int myTerminalHeight;
  
  private final TerminalDisplay myDisplay;
  
  private final TerminalTextBuffer myTerminalTextBuffer;
  private final StyleState myStyleState;
  private StoredCursor myStoredCursor = null;
  
  private final EnumSet<TerminalMode> myModes = EnumSet.noneOf(TerminalMode.class);
  
  private final TerminalKeyEncoder myTerminalKeyEncoder = new TerminalKeyEncoder();
  
  private final Tabulator myTabulator;
  
  private final GraphicSetState myGraphicSetState;
  
  private MouseFormat myMouseFormat = MouseFormat.MOUSE_FORMAT_XTERM;
  @Nullable
  private TerminalOutputStream myTerminalOutput = null;

  
  private MouseMode myMouseMode = MouseMode.MOUSE_REPORTING_NONE;
  private Point myLastMotionReport = null;
  private boolean myCursorYChanged;
  
  public JediTerminal(TerminalDisplay display, TerminalTextBuffer buf, StyleState initialStyleState) {
    this.myDisplay = display;
    this.myTerminalTextBuffer = buf;
    this.myStyleState = initialStyleState;
    
    this.myTerminalWidth = display.getColumnCount();
    this.myTerminalHeight = display.getRowCount();
    
    this.myScrollRegionTop = 1;
    this.myScrollRegionBottom = this.myTerminalHeight;
    
    this.myTabulator = new DefaultTabulator(this.myTerminalWidth);
    
    this.myGraphicSetState = new GraphicSetState();
    
    reset();
  }


  
  public void setModeEnabled(TerminalMode mode, boolean enabled) {
    if (enabled) {
      this.myModes.add(mode);
    } else {
      this.myModes.remove(mode);
    } 
    
    mode.setEnabled(this, enabled);
  }

  
  public void disconnected() {
    this.myDisplay.setCursorVisible(false);
  }
  
  private void wrapLines() {
    if (this.myCursorX >= this.myTerminalWidth) {
      this.myCursorX = 0;
      
      this.myTerminalTextBuffer.getLine(this.myCursorY - 1).deleteCharacters(this.myTerminalWidth);
      if (isAutoWrap()) {
        this.myTerminalTextBuffer.getLine(this.myCursorY - 1).setWrapped(true);
        this.myCursorY++;
      } 
    } 
  }
  
  private void finishText() {
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
    scrollY();
  }

  
  public void writeCharacters(String string) {
    writeDecodedCharacters(decodeUsingGraphicalState(string));
  }
  
  private void writeDecodedCharacters(char[] string) {
    this.myTerminalTextBuffer.lock();
    try {
      if (this.myCursorYChanged && string.length > 0) {
        this.myCursorYChanged = false;
        if (this.myCursorY > 1) {
          this.myTerminalTextBuffer.getLine(this.myCursorY - 2).setWrapped(false);
        }
      } 
      wrapLines();
      scrollY();
      
      if (string.length != 0) {
        CharBuffer characters = new CharBuffer(string, 0, string.length);
        
        this.myTerminalTextBuffer.writeString(this.myCursorX, this.myCursorY, characters);
        this.myCursorX += characters.length();
      } 
      
      finishText();
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void writeDoubleByte(char[] bytesOfChar) throws UnsupportedEncodingException {
    writeCharacters(new String(bytesOfChar, 0, 2));
  }

  
  private char[] decodeUsingGraphicalState(String string) {
    StringBuilder result = new StringBuilder();
    for (char c : string.toCharArray()) {
      result.append(this.myGraphicSetState.map(c));
    }
    
    return result.toString().toCharArray();
  }
  
  public void writeUnwrappedString(String string) {
    int length = string.length();
    int off = 0;
    while (off < length) {
      int amountInLine = Math.min(distanceToLineEnd(), length - off);
      writeCharacters(string.substring(off, off + amountInLine));
      wrapLines();
      scrollY();
      off += amountInLine;
    } 
  }

  
  public void scrollY() {
    this.myTerminalTextBuffer.lock();
    try {
      if (this.myCursorY > this.myScrollRegionBottom) {
        int dy = this.myScrollRegionBottom - this.myCursorY;
        this.myCursorY = this.myScrollRegionBottom;
        scrollArea(this.myScrollRegionTop, scrollingRegionSize(), dy);
        this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
      } 
      if (this.myCursorY < this.myScrollRegionTop) {
        this.myCursorY = this.myScrollRegionTop;
      }
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }
  
  public void crnl() {
    carriageReturn();
    newLine();
  }

  
  public void newLine() {
    this.myCursorYChanged = true;
    this.myCursorY++;
    
    scrollY();
    
    if (isAutoNewLine()) {
      carriageReturn();
    }
    
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void mapCharsetToGL(int num) {
    this.myGraphicSetState.setGL(num);
  }

  
  public void mapCharsetToGR(int num) {
    this.myGraphicSetState.setGR(num);
  }

  
  public void designateCharacterSet(int tableNumber, char charset) {
    GraphicSet gs = this.myGraphicSetState.getGraphicSet(tableNumber);
    this.myGraphicSetState.designateGraphicSet(gs, charset);
  }

  
  public void singleShiftSelect(int num) {
    this.myGraphicSetState.overrideGL(num);
  }

  
  public void setAnsiConformanceLevel(int level) {
    if (level == 1 || level == 2) {
      this.myGraphicSetState.designateGraphicSet(0, CharacterSet.ASCII);
      this.myGraphicSetState
        .designateGraphicSet(1, CharacterSet.DEC_SUPPLEMENTAL);
      mapCharsetToGL(0);
      mapCharsetToGR(1);
    } else if (level == 3) {
      designateCharacterSet(0, 'B');
      mapCharsetToGL(0);
    } else {
      throw new IllegalArgumentException();
    } 
  }

  
  public void setWindowTitle(String name) {
    this.myDisplay.setWindowTitle(name);
  }

  
  public void setCurrentPath(String path) {
    this.myDisplay.setCurrentPath(path);
  }

  
  public void backspace() {
    this.myCursorX--;
    if (this.myCursorX < 0) {
      this.myCursorY--;
      this.myCursorX = this.myTerminalWidth - 1;
    } 
    adjustXY(-1);
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void carriageReturn() {
    this.myCursorX = 0;
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void horizontalTab() {
    if (this.myCursorX >= this.myTerminalWidth) {
      return;
    }
    int length = this.myTerminalTextBuffer.getLine(this.myCursorY - 1).getText().length();
    int stop = this.myTabulator.nextTab(this.myCursorX);
    this.myCursorX = Math.max(this.myCursorX, length);
    if (this.myCursorX < stop) {
      char[] chars = new char[stop - this.myCursorX];
      Arrays.fill(chars, ' ');
      writeDecodedCharacters(chars);
    } else {
      this.myCursorX = stop;
    } 
    adjustXY(1);
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void eraseInDisplay(int arg) {
    this.myTerminalTextBuffer.lock();
    
    try {
      int beginY;
      int endY;
      switch (arg) {
        
        case 0:
          if (this.myCursorX < this.myTerminalWidth) {
            this.myTerminalTextBuffer.eraseCharacters(this.myCursorX, -1, this.myCursorY - 1);
          }
          
          beginY = this.myCursorY;
          endY = this.myTerminalHeight;
          break;

        
        case 1:
          this.myTerminalTextBuffer.eraseCharacters(0, this.myCursorX + 1, this.myCursorY - 1);
          
          beginY = 0;
          endY = this.myCursorY - 1;
          break;
        case 2:
          beginY = 0;
          endY = this.myTerminalHeight - 1;
          this.myTerminalTextBuffer.moveScreenLinesToHistory();
          break;
        default:
          LOG.error("Unsupported erase in display mode:" + arg);
          beginY = 1;
          endY = 1;
          break;
      } 
      
      if (beginY != endY) {
        clearLines(beginY, endY);
      }
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }
  
  public void clearLines(int beginY, int endY) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myTerminalTextBuffer.clearLines(beginY, endY);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void clearScreen() {
    clearLines(0, this.myTerminalHeight - 1);
  }

  
  public void setCursorVisible(boolean visible) {
    this.myDisplay.setCursorVisible(visible);
  }

  
  public void useAlternateBuffer(boolean enabled) {
    this.myTerminalTextBuffer.useAlternateBuffer(enabled);
    this.myDisplay.setScrollingEnabled(!enabled);
  }

  
  public byte[] getCodeForKey(int key, int modifiers) {
    return this.myTerminalKeyEncoder.getCode(key, modifiers);
  }

  
  public void setApplicationArrowKeys(boolean enabled) {
    if (enabled) {
      this.myTerminalKeyEncoder.arrowKeysApplicationSequences();
    } else {
      this.myTerminalKeyEncoder.arrowKeysAnsiCursorSequences();
    } 
  }

  
  public void setApplicationKeypad(boolean enabled) {
    if (enabled) {
      this.myTerminalKeyEncoder.keypadApplicationSequences();
    } else {
      this.myTerminalKeyEncoder.keypadAnsiSequences();
    } 
  }

  
  public void setAutoNewLine(boolean enabled) {
    this.myTerminalKeyEncoder.setAutoNewLine(enabled);
  }
  
  public void eraseInLine(int arg) {
    this.myTerminalTextBuffer.lock(); try {
      int extent;
      switch (arg) {
        case 0:
          if (this.myCursorX < this.myTerminalWidth) {
            this.myTerminalTextBuffer.eraseCharacters(this.myCursorX, -1, this.myCursorY - 1);
          }
          
          this.myTerminalTextBuffer.getLine(this.myCursorY - 1).setWrapped(false);
          break;
        case 1:
          extent = Math.min(this.myCursorX + 1, this.myTerminalWidth);
          this.myTerminalTextBuffer.eraseCharacters(0, extent, this.myCursorY - 1);
          break;
        case 2:
          this.myTerminalTextBuffer.eraseCharacters(0, -1, this.myCursorY - 1);
          break;
        default:
          LOG.error("Unsupported erase in line mode:" + arg);
          break;
      } 
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void deleteCharacters(int count) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myTerminalTextBuffer.deleteCharacters(this.myCursorX, this.myCursorY - 1, count);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void insertBlankCharacters(int count) {
    this.myTerminalTextBuffer.lock();
    try {
      int extent = Math.min(count, this.myTerminalWidth - this.myCursorX);
      this.myTerminalTextBuffer.insertBlankCharacters(this.myCursorX, this.myCursorY - 1, extent);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }



  
  public void eraseCharacters(int count) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myTerminalTextBuffer.eraseCharacters(this.myCursorX, this.myCursorX + count, this.myCursorY - 1);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void clearTabStopAtCursor() {
    this.myTabulator.clearTabStop(this.myCursorX);
  }

  
  public void clearAllTabStops() {
    this.myTabulator.clearAllTabStops();
  }

  
  public void setTabStopAtCursor() {
    this.myTabulator.setTabStop(this.myCursorX);
  }

  
  public void insertLines(int count) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myTerminalTextBuffer.insertLines(this.myCursorY - 1, count, this.myScrollRegionBottom);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void deleteLines(int count) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myTerminalTextBuffer.deleteLines(this.myCursorY - 1, count, this.myScrollRegionBottom);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void setBlinkingCursor(boolean enabled) {
    this.myDisplay.setBlinkingCursor(enabled);
  }

  
  public void cursorUp(int countY) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myCursorYChanged = true;
      this.myCursorY -= countY;
      this.myCursorY = Math.max(this.myCursorY, scrollingRegionTop());
      adjustXY(-1);
      this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void cursorDown(int dY) {
    this.myTerminalTextBuffer.lock();
    try {
      this.myCursorYChanged = true;
      this.myCursorY += dY;
      this.myCursorY = Math.min(this.myCursorY, scrollingRegionBottom());
      adjustXY(-1);
      this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }




  
  public void index() {
    this.myTerminalTextBuffer.lock();
    try {
      if (this.myCursorY == this.myScrollRegionBottom) {
        scrollArea(this.myScrollRegionTop, scrollingRegionSize(), -1);
      } else {
        this.myCursorY++;
        adjustXY(-1);
        this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
      } 
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }
  
  private void scrollArea(int scrollRegionTop, int scrollRegionSize, int dy) {
    this.myDisplay.scrollArea(scrollRegionTop, scrollRegionSize, dy);
    this.myTerminalTextBuffer.scrollArea(scrollRegionTop, dy, scrollRegionTop + scrollRegionSize - 1);
  }

  
  public void nextLine() {
    this.myTerminalTextBuffer.lock();
    try {
      this.myCursorX = 0;
      if (this.myCursorY == this.myScrollRegionBottom) {
        scrollArea(this.myScrollRegionTop, scrollingRegionSize(), -1);
      } else {
        this.myCursorY++;
      } 
      this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }
  
  private int scrollingRegionSize() {
    return this.myScrollRegionBottom - this.myScrollRegionTop + 1;
  }




  
  public void reverseIndex() {
    this.myTerminalTextBuffer.lock();
    try {
      if (this.myCursorY == this.myScrollRegionTop) {
        scrollArea(this.myScrollRegionTop, scrollingRegionSize(), 1);
      } else {
        this.myCursorY--;
        this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
      } 
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }
  
  private int scrollingRegionTop() {
    return isOriginMode() ? this.myScrollRegionTop : 1;
  }
  
  private int scrollingRegionBottom() {
    return isOriginMode() ? this.myScrollRegionBottom : this.myTerminalHeight;
  }

  
  public void cursorForward(int dX) {
    this.myCursorX += dX;
    this.myCursorX = Math.min(this.myCursorX, this.myTerminalWidth - 1);
    adjustXY(1);
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void cursorBackward(int dX) {
    this.myCursorX -= dX;
    this.myCursorX = Math.max(this.myCursorX, 0);
    adjustXY(-1);
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void cursorShape(CursorShape shape) {
    this.myDisplay.setCursorShape(shape);
  }

  
  public void cursorHorizontalAbsolute(int x) {
    cursorPosition(x, this.myCursorY);
  }

  
  public void linePositionAbsolute(int y) {
    this.myCursorY = y;
    adjustXY(-1);
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void cursorPosition(int x, int y) {
    if (isOriginMode()) {
      this.myCursorY = y + scrollingRegionTop() - 1;
    } else {
      this.myCursorY = y;
    } 
    
    if (this.myCursorY > scrollingRegionBottom()) {
      this.myCursorY = scrollingRegionBottom();
    }

    
    this.myCursorX = Math.max(0, x - 1);
    this.myCursorX = Math.min(this.myCursorX, this.myTerminalWidth - 1);
    
    this.myCursorY = Math.max(0, this.myCursorY);
    
    adjustXY(-1);
    
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }

  
  public void setScrollingRegion(int top, int bottom) {
    if (top > bottom) {
      LOG.error("Top margin of scroll region can't be greater then bottom: " + top + ">" + bottom);
    }
    this.myScrollRegionTop = Math.max(1, top);
    this.myScrollRegionBottom = Math.min(this.myTerminalHeight, bottom);

    
    cursorPosition(1, 1);
  }

  
  public void scrollUp(int count) {
    scrollDown(-count);
  }

  
  public void scrollDown(int count) {
    this.myTerminalTextBuffer.lock();
    try {
      scrollArea(this.myScrollRegionTop, scrollingRegionSize(), count);
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }

  
  public void resetScrollRegions() {
    setScrollingRegion(1, this.myTerminalHeight);
  }

  
  public void characterAttributes(TextStyle textStyle) {
    this.myStyleState.setCurrent(textStyle);
  }

  
  public void beep() {
    this.myDisplay.beep();
  }

  
  public int distanceToLineEnd() {
    return this.myTerminalWidth - this.myCursorX;
  }

  
  public void saveCursor() {
    this.myStoredCursor = createCursorState();
  }
  
  private StoredCursor createCursorState() {
    return new StoredCursor(this.myCursorX, this.myCursorY, this.myStyleState.getCurrent(), 
        isAutoWrap(), isOriginMode(), this.myGraphicSetState);
  }

  
  public void restoreCursor() {
    if (this.myStoredCursor != null) {
      restoreCursor(this.myStoredCursor);
    } else {
      setModeEnabled(TerminalMode.OriginMode, false);
      cursorPosition(1, 1);
      this.myStyleState.reset();
      
      this.myGraphicSetState.resetState();
    } 



    
    this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
  }
  
  public void restoreCursor(@NotNull StoredCursor storedCursor) {
    if (storedCursor == null) $$$reportNull$$$0(0);  this.myCursorX = storedCursor.getCursorX();
    this.myCursorY = storedCursor.getCursorY();
    
    adjustXY(-1);
    
    this.myStyleState.setCurrent(storedCursor.getTextStyle());
    
    setModeEnabled(TerminalMode.AutoWrap, storedCursor.isAutoWrap());
    setModeEnabled(TerminalMode.OriginMode, storedCursor.isOriginMode());
    
    CharacterSet[] designations = storedCursor.getDesignations();
    for (int i = 0; i < designations.length; i++) {
      this.myGraphicSetState.designateGraphicSet(i, designations[i]);
    }
    this.myGraphicSetState.setGL(storedCursor.getGLMapping());
    this.myGraphicSetState.setGR(storedCursor.getGRMapping());
    
    if (storedCursor.getGLOverride() >= 0) {
      this.myGraphicSetState.overrideGL(storedCursor.getGLOverride());
    }
  }

  
  public void reset() {
    this.myGraphicSetState.resetState();
    
    this.myStyleState.reset();
    
    this.myTerminalTextBuffer.clearAll();
    
    this.myDisplay.setScrollingEnabled(true);
    
    initModes();
    
    initMouseModes();
    
    cursorPosition(1, 1);
  }
  
  private void initMouseModes() {
    setMouseMode(MouseMode.MOUSE_REPORTING_NONE);
    setMouseFormat(MouseFormat.MOUSE_FORMAT_XTERM);
  }
  
  private void initModes() {
    this.myModes.clear();
    setModeEnabled(TerminalMode.AutoWrap, true);
    setModeEnabled(TerminalMode.AutoNewLine, false);
    setModeEnabled(TerminalMode.CursorVisible, true);
    setModeEnabled(TerminalMode.CursorBlinking, true);
  }
  
  public boolean isAutoNewLine() {
    return this.myModes.contains(TerminalMode.AutoNewLine);
  }
  
  public boolean isOriginMode() {
    return this.myModes.contains(TerminalMode.OriginMode);
  }
  
  public boolean isAutoWrap() {
    return this.myModes.contains(TerminalMode.AutoWrap);
  }

  
  private static int createButtonCode(MouseEvent event) {
    if (SwingUtilities.isLeftMouseButton(event))
      return 0; 
    if (SwingUtilities.isMiddleMouseButton(event))
      return 1; 
    if (SwingUtilities.isRightMouseButton(event))
      return -1; 
    if (event instanceof MouseWheelEvent) {
      if (((MouseWheelEvent)event).getWheelRotation() > 0) {
        return 5;
      }
      return 4;
    } 
    
    return -1;
  }
  
  private byte[] mouseReport(int button, int x, int y) {
    StringBuilder sb = new StringBuilder();
    String charset = "UTF-8";
    switch (this.myMouseFormat)
    { case MOUSE_FORMAT_XTERM_EXT:
        sb.append(String.format("\033[M%c%c%c", new Object[] {
                Character.valueOf((char)(32 + button)), 
                Character.valueOf((char)(32 + x)), 
                Character.valueOf((char)(32 + y))
              }));























        
        LOG.debug(this.myMouseFormat + " (" + charset + ") report : " + button + ", " + x + "x" + y + " = " + sb);
        return sb.toString().getBytes(Charset.forName(charset));case MOUSE_FORMAT_URXVT: sb.append(String.format("\033[%d;%d;%dM", new Object[] { Integer.valueOf(32 + button), Integer.valueOf(x), Integer.valueOf(y) })); LOG.debug(this.myMouseFormat + " (" + charset + ") report : " + button + ", " + x + "x" + y + " = " + sb); return sb.toString().getBytes(Charset.forName(charset));case MOUSE_FORMAT_SGR: if ((button & 0x80) != 0) { sb.append(String.format("\033[<%d;%d;%dm", new Object[] { Integer.valueOf(button ^ 0x80), Integer.valueOf(x), Integer.valueOf(y) })); } else { sb.append(String.format("\033[<%d;%d;%dM", new Object[] { Integer.valueOf(button), Integer.valueOf(x), Integer.valueOf(y) })); }  LOG.debug(this.myMouseFormat + " (" + charset + ") report : " + button + ", " + x + "x" + y + " = " + sb); return sb.toString().getBytes(Charset.forName(charset)); }  charset = "ISO-8859-1"; sb.append(String.format("\033[M%c%c%c", new Object[] { Character.valueOf((char)(32 + button)), Character.valueOf((char)(32 + x)), Character.valueOf((char)(32 + y)) })); LOG.debug(this.myMouseFormat + " (" + charset + ") report : " + button + ", " + x + "x" + y + " = " + sb); return sb.toString().getBytes(Charset.forName(charset));
  }
  
  private boolean shouldSendMouseData(MouseMode... eligibleModes) {
    if (this.myMouseMode == MouseMode.MOUSE_REPORTING_NONE || this.myTerminalOutput == null) {
      return false;
    }
    if (this.myMouseMode == MouseMode.MOUSE_REPORTING_ALL_MOTION) {
      return true;
    }
    for (MouseMode m : eligibleModes) {
      if (this.myMouseMode == m) {
        return true;
      }
    } 
    return false;
  }

  
  public void mousePressed(int x, int y, MouseEvent event) {
    if (shouldSendMouseData(new MouseMode[] { MouseMode.MOUSE_REPORTING_NORMAL, MouseMode.MOUSE_REPORTING_BUTTON_MOTION })) {
      int cb = createButtonCode(event);
      
      if (cb != -1) {
        if (cb == 4 || cb == 5) {
          
          int offset = 4;
          cb -= offset;
          cb |= 0x40;
        } 
        
        cb = applyModifierKeys(event, cb);
        
        if (this.myTerminalOutput != null) {
          this.myTerminalOutput.sendBytes(mouseReport(cb, x + 1, y + 1));
        }
      } 
    } 
  }

  
  public void mouseReleased(int x, int y, MouseEvent event) {
    if (shouldSendMouseData(new MouseMode[] { MouseMode.MOUSE_REPORTING_NORMAL, MouseMode.MOUSE_REPORTING_BUTTON_MOTION })) {
      int cb = createButtonCode(event);
      
      if (cb != -1) {
        
        if (this.myMouseFormat == MouseFormat.MOUSE_FORMAT_SGR) {
          
          cb |= 0x80;
        } else {
          
          cb = 3;
        } 
        
        cb = applyModifierKeys(event, cb);
        
        if (this.myTerminalOutput != null) {
          this.myTerminalOutput.sendBytes(mouseReport(cb, x + 1, y + 1));
        }
      } 
    } 
    this.myLastMotionReport = null;
  }
  
  public void mouseMoved(int x, int y, MouseEvent event) {
    if (this.myLastMotionReport != null && this.myLastMotionReport.equals(new Point(x, y))) {
      return;
    }
    if (shouldSendMouseData(new MouseMode[] { MouseMode.MOUSE_REPORTING_ALL_MOTION
        }) && this.myTerminalOutput != null) {
      this.myTerminalOutput.sendBytes(mouseReport(3, x + 1, y + 1));
    }
    
    this.myLastMotionReport = new Point(x, y);
  }

  
  public void mouseDragged(int x, int y, MouseEvent event) {
    if (this.myLastMotionReport != null && this.myLastMotionReport.equals(new Point(x, y))) {
      return;
    }
    if (shouldSendMouseData(new MouseMode[] { MouseMode.MOUSE_REPORTING_BUTTON_MOTION })) {
      
      int cb = createButtonCode(event);
      
      if (cb != -1) {
        cb |= 0x20;
        cb = applyModifierKeys(event, cb);
        if (this.myTerminalOutput != null) {
          this.myTerminalOutput.sendBytes(mouseReport(cb, x + 1, y + 1));
        }
      } 
    } 
    this.myLastMotionReport = new Point(x, y);
  }


  
  public void mouseWheelMoved(int x, int y, MouseWheelEvent event) {
    mousePressed(x, y, event);
  }
  
  private static int applyModifierKeys(MouseEvent event, int cb) {
    if (event.isControlDown()) {
      cb |= 0x10;
    }
    if (event.isShiftDown()) {
      cb |= 0x4;
    }
    if ((event.getModifiersEx() & 0x4) != 0) {
      cb |= 0x8;
    }
    return cb;
  }
  
  public void setTerminalOutput(TerminalOutputStream terminalOutput) {
    this.myTerminalOutput = terminalOutput;
  }

  
  public void setMouseMode(@NotNull MouseMode mode) {
    if (mode == null) $$$reportNull$$$0(1);  this.myMouseMode = mode;
    this.myDisplay.terminalMouseModeSet(mode);
  }

  
  public void setAltSendsEscape(boolean enabled) {
    this.myTerminalKeyEncoder.setAltSendsEscape(enabled);
  }

  
  public void deviceStatusReport(String str) {
    if (this.myTerminalOutput != null) {
      this.myTerminalOutput.sendString(str);
    }
  }

  
  public void deviceAttributes(byte[] response) {
    if (this.myTerminalOutput != null) {
      this.myTerminalOutput.sendBytes(response);
    }
  }

  
  public void setLinkUriStarted(@NotNull String uri) {
    if (uri == null) $$$reportNull$$$0(2);  TextStyle style = this.myStyleState.getCurrent();
    this.myStyleState.setCurrent((TextStyle)new HyperlinkStyle(style, new LinkInfo(() -> {
              try {
                Desktop.getDesktop().browse(new URI(uri));
              } catch (Exception exception) {}
            })));
  }


  
  public void setLinkUriFinished() {
    TextStyle current = this.myStyleState.getCurrent();
    if (current instanceof HyperlinkStyle) {
      TextStyle prevTextStyle = ((HyperlinkStyle)current).getPrevTextStyle();
      if (prevTextStyle != null) {
        this.myStyleState.setCurrent(prevTextStyle);
      }
    } 
  }

  
  public void setMouseFormat(MouseFormat mouseFormat) {
    this.myMouseFormat = mouseFormat;
  }
  
  private void adjustXY(int dirX) {
    if (this.myCursorY > -this.myTerminalTextBuffer.getHistoryLinesCount() && 
      Character.isLowSurrogate(this.myTerminalTextBuffer.getCharAt(this.myCursorX, this.myCursorY - 1)))
    {
      if (dirX > 0) {
        if (this.myCursorX == this.myTerminalWidth) {
          this.myCursorX--;
        } else {
          this.myCursorX++;
        } 
      } else {
        this.myCursorX--;
      } 
    }
  }

  
  public int getX() {
    return this.myCursorX;
  }

  
  public void setX(int x) {
    this.myCursorX = x;
    adjustXY(-1);
  }

  
  public int getY() {
    return this.myCursorY;
  }

  
  public void setY(int y) {
    this.myCursorY = y;
    adjustXY(-1);
  }
  
  public void writeString(String s) {
    writeCharacters(s);
  }





  
  public void resize(@NotNull Dimension newTermSize, @NotNull RequestOrigin origin) {
    if (newTermSize == null) $$$reportNull$$$0(3);  if (origin == null) $$$reportNull$$$0(4);  resize(newTermSize, origin, CompletableFuture.completedFuture(null));
  }

  
  public void resize(@NotNull Dimension newTermSize, @NotNull RequestOrigin origin, @NotNull CompletableFuture<?> promptUpdated) {
    if (newTermSize == null) $$$reportNull$$$0(5);  if (origin == null) $$$reportNull$$$0(6);  if (promptUpdated == null) $$$reportNull$$$0(7);  int oldHeight = this.myTerminalHeight;
    ensureTermMinimumSize(newTermSize);
    if (newTermSize.width == this.myTerminalWidth && newTermSize.height == this.myTerminalHeight) {
      return;
    }
    if (newTermSize.width == this.myTerminalWidth) {
      doResize(newTermSize, origin, oldHeight);
    } else {
      
      this.myTerminalWidth = newTermSize.width;
      this.myTerminalHeight = newTermSize.height;
      promptUpdated.thenRun(() -> doResize(newTermSize, origin, oldHeight));
    } 
  }


  
  private void doResize(@NotNull Dimension newTermSize, @NotNull RequestOrigin origin, int oldHeight) {
    if (newTermSize == null) $$$reportNull$$$0(8);  if (origin == null) $$$reportNull$$$0(9);  this.myDisplay.requestResize(newTermSize, origin, this.myCursorX, this.myCursorY, (termWidth, termHeight, cursorX, cursorY) -> {
          this.myTerminalWidth = termWidth;
          
          this.myTerminalHeight = termHeight;
          this.myCursorY = cursorY;
          this.myCursorX = Math.min(cursorX, this.myTerminalWidth - 1);
          this.myDisplay.setCursor(this.myCursorX, this.myCursorY);
          this.myTabulator.resize(this.myTerminalWidth);
        });
    this.myScrollRegionBottom += this.myTerminalHeight - oldHeight;
  }
  
  public static void ensureTermMinimumSize(@NotNull Dimension termSize) {
    if (termSize == null) $$$reportNull$$$0(10);  termSize.setSize(Math.max(5, termSize.width), Math.max(2, termSize.height));
  }

  
  public void fillScreen(char c) {
    this.myTerminalTextBuffer.lock();
    try {
      char[] chars = new char[this.myTerminalWidth];
      Arrays.fill(chars, c);
      
      for (int row = 1; row <= this.myTerminalHeight; row++) {
        this.myTerminalTextBuffer.writeString(0, row, newCharBuf(chars));
      }
    } finally {
      this.myTerminalTextBuffer.unlock();
    } 
  }
  @NotNull
  private CharBuffer newCharBuf(char[] str) {
    char[] buf;
    int dwcCount = CharUtils.countDoubleWidthCharacters(str, 0, str.length, this.myDisplay.ambiguousCharsAreDoubleWidth());


    
    if (dwcCount > 0) {
      
      buf = new char[str.length + dwcCount];
      
      int j = 0;
      for (int i = 0; i < str.length; i++) {
        buf[j] = str[i];
        int codePoint = Character.codePointAt(str, i);
        boolean doubleWidthCharacter = CharUtils.isDoubleWidthCharacter(codePoint, this.myDisplay.ambiguousCharsAreDoubleWidth());
        if (doubleWidthCharacter) {
          j++;
          buf[j] = '';
        } 
        j++;
      } 
    } else {
      buf = str;
    } 
    return new CharBuffer(buf, 0, buf.length);
  }

  
  public int getTerminalWidth() {
    return this.myTerminalWidth;
  }

  
  public int getTerminalHeight() {
    return this.myTerminalHeight;
  }

  
  public int getCursorX() {
    return this.myCursorX + 1;
  }

  
  public int getCursorY() {
    return this.myCursorY;
  }

  
  public StyleState getStyleState() {
    return this.myStyleState;
  }
  
  public SubstringFinder.FindResult searchInTerminalTextBuffer(String pattern, boolean ignoreCase) {
    if (pattern.length() == 0) {
      return null;
    }
    
    final SubstringFinder finder = new SubstringFinder(pattern, ignoreCase);
    
    this.myTerminalTextBuffer.processHistoryAndScreenLines(-this.myTerminalTextBuffer.getHistoryLinesCount(), -1, new StyledTextConsumer()
        {
          public void consume(int x, int y, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
            if (style == null) $$$reportNull$$$0(0);  if (characters == null) $$$reportNull$$$0(1);  int offset = 0;
            int length = characters.length();
            if (characters instanceof SubCharBuffer) {
              SubCharBuffer subCharBuffer = (SubCharBuffer)characters;
              characters = subCharBuffer.getParent();
              offset = subCharBuffer.getOffset();
            } 
            for (int i = offset; i < offset + length; i++) {
              finder.nextChar(x, y - startRow, characters, i);
            }
          }

          
          public void consumeNul(int x, int y, int nulIndex, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
            if (style == null) $$$reportNull$$$0(2);  if (characters == null) $$$reportNull$$$0(3);
          
          }

          
          public void consumeQueue(int x, int y, int nulIndex, int startRow) {}
        });
    return finder.getResult();
  }

  
  private static class DefaultTabulator
    implements Tabulator
  {
    private static final int TAB_LENGTH = 8;
    private final SortedSet<Integer> myTabStops;
    private int myWidth;
    private int myTabLength;
    
    public DefaultTabulator(int width) {
      this(width, 8);
    }
    
    public DefaultTabulator(int width, int tabLength) {
      this.myTabStops = new TreeSet<>();
      
      this.myWidth = width;
      this.myTabLength = tabLength;
      
      initTabStops(width, tabLength);
    }
    
    private void initTabStops(int columns, int tabLength) {
      for (int i = tabLength; i < columns; i += tabLength) {
        this.myTabStops.add(Integer.valueOf(i));
      }
    }
    
    public void resize(int columns) {
      if (columns > this.myWidth) {
        for (int i = this.myTabLength * this.myWidth / this.myTabLength; i < columns; i += this.myTabLength) {
          if (i >= this.myWidth) {
            this.myTabStops.add(Integer.valueOf(i));
          }
        } 
      } else {
        Iterator<Integer> it = this.myTabStops.iterator();
        while (it.hasNext()) {
          int i = ((Integer)it.next()).intValue();
          if (i > columns) {
            it.remove();
          }
        } 
      } 
      
      this.myWidth = columns;
    }

    
    public void clearTabStop(int position) {
      this.myTabStops.remove(Integer.valueOf(position));
    }

    
    public void clearAllTabStops() {
      this.myTabStops.clear();
    }

    
    public int getNextTabWidth(int position) {
      return nextTab(position) - position;
    }

    
    public int getPreviousTabWidth(int position) {
      return position - previousTab(position);
    }

    
    public int nextTab(int position) {
      int tabStop = Integer.MAX_VALUE;

      
      SortedSet<Integer> tailSet = this.myTabStops.tailSet(Integer.valueOf(position + 1));
      if (!tailSet.isEmpty()) {
        tabStop = ((Integer)tailSet.first()).intValue();
      }

      
      return Math.min(tabStop, this.myWidth - 1);
    }

    
    public int previousTab(int position) {
      int tabStop = 0;

      
      SortedSet<Integer> headSet = this.myTabStops.headSet(Integer.valueOf(position));
      if (!headSet.isEmpty()) {
        tabStop = ((Integer)headSet.last()).intValue();
      }

      
      return Math.max(0, tabStop);
    }

    
    public void setTabStop(int position) {
      this.myTabStops.add(Integer.valueOf(position));
    }
  }
  
  public static interface ResizeHandler {
    void sizeUpdated(int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
}
