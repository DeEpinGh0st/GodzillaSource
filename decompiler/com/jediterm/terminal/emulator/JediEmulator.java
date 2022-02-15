package com.jediterm.terminal.emulator;

import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.DataStreamIteratingEmulator;
import com.jediterm.terminal.RequestOrigin;
import com.jediterm.terminal.Terminal;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TerminalDataStream;
import com.jediterm.terminal.TerminalMode;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.mouse.MouseFormat;
import com.jediterm.terminal.emulator.mouse.MouseMode;
import com.jediterm.terminal.util.CharUtils;
import java.awt.Dimension;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;



public class JediEmulator
  extends DataStreamIteratingEmulator
{
  private static final Logger LOG = Logger.getLogger(JediEmulator.class);
  
  private static int logThrottlerCounter = 0;
  private static int logThrottlerRatio = 100;
  private static int logThrottlerLimit = logThrottlerRatio;
  private final BlockingQueue<CompletableFuture<Void>> myResizeFutureQueue = new LinkedBlockingQueue<>();
  
  public JediEmulator(TerminalDataStream dataStream, Terminal terminal) {
    super(dataStream, terminal);
  }
  
  public void processChar(char ch, Terminal terminal) throws IOException {
    String nonControlCharacters;
    switch (ch) {
      case '\000':
        break;
      case '\007':
        terminal.beep();
        break;
      case '\b':
        terminal.backspace();
        break;
      case '\r':
        terminal.carriageReturn();
        break;
      case '\005':
        unsupported("Terminal status:" + escapeSequenceToString(new char[] { ch }));
        break;
      
      case '\n':
      case '\013':
      case '\f':
        terminal.newLine();
        break;

      
      case '\017':
        terminal.mapCharsetToGL(0);
        break;

      
      case '\016':
        if (Boolean.getBoolean("jediterm.enable.shift_out.character.support")) {
          terminal.mapCharsetToGL(1);
        }
        break;
      case '\t':
        terminal.horizontalTab();
        break;
      case '\033':
        processEscapeSequence(this.myDataStream.getChar(), this.myTerminal);
        break;
      default:
        if (ch <= '\037') {
          StringBuilder sb = new StringBuilder("Unhandled control character:");
          CharUtils.appendChar(sb, CharUtils.CharacterType.NONE, ch);
          unhandledLogThrottler(sb.toString()); break;
        } 
        this.myDataStream.pushChar(ch);
        nonControlCharacters = this.myDataStream.readNonControlCharacters(terminal.distanceToLineEnd());
        
        terminal.writeCharacters(nonControlCharacters);
        break;
    } 
    
    if (this.myDataStream.isEmpty())
      completeResize(); 
  }
  private void processEscapeSequence(char ch, Terminal terminal) throws IOException {
    ControlSequence args;
    SystemCommandSequence command;
    switch (ch) {
      case '[':
        args = new ControlSequence(this.myDataStream);
        
        if (LOG.isDebugEnabled()) {
          LOG.debug(args.appendTo("Control sequence\nparsed                        :"));
        }
        if (!args.pushBackReordered(this.myDataStream)) {
          boolean result = processControlSequence(args);
          
          if (!result) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unhandled Control sequence\n");
            sb.append("parsed                        :");
            args.appendToBuffer(sb);
            sb.append('\n');
            sb.append("bytes read                    :ESC[");
            LOG.error(sb.toString());
          } 
        } 
        return;
      case 'D':
        terminal.index();
        return;
      case 'E':
        terminal.nextLine();
        return;
      case 'H':
        terminal.setTabStopAtCursor();
        return;
      case 'M':
        terminal.reverseIndex();
        return;
      case 'N':
        terminal.singleShiftSelect(2);
        return;
      case 'O':
        terminal.singleShiftSelect(3);
        return;
      
      case ']':
        command = new SystemCommandSequence(this.myDataStream);
        
        if (!operatingSystemCommand(command)) {
          LOG.error("Error processing OSC " + command.getSequenceString());
        }
        return;
      case '6':
        unsupported("Back Index (DECBI), VT420 and up");
        return;
      case '7':
        terminal.saveCursor();
        return;
      case '8':
        terminal.restoreCursor();
        return;
      case '9':
        unsupported("Forward Index (DECFI), VT420 and up");
        return;
      case '=':
        setModeEnabled(TerminalMode.Keypad, true);
        return;
      case '>':
        setModeEnabled(TerminalMode.Keypad, false);
        return;
      case 'F':
        terminal.cursorPosition(1, terminal.getTerminalHeight());
        return;
      case 'c':
        terminal.reset();
        return;
      case 'n':
        this.myTerminal.mapCharsetToGL(2);
        return;
      case 'o':
        this.myTerminal.mapCharsetToGL(3);
        return;
      case '|':
        this.myTerminal.mapCharsetToGR(3);
        return;
      case '}':
        this.myTerminal.mapCharsetToGR(2);
        return;
      case '~':
        this.myTerminal.mapCharsetToGR(1);
        return;
      case ' ':
      case '#':
      case '$':
      case '%':
      case '(':
      case ')':
      case '*':
      case '+':
      case '.':
      case '/':
      case '@':
        processTwoCharSequence(ch, terminal);
        return;
    } 
    unsupported(new char[] { ch });
  }

  
  private boolean operatingSystemCommand(SystemCommandSequence args) {
    Integer i = args.getIntAt(0);
    
    if (i != null) {
      String name; String path; String uri; switch (i.intValue()) {
        case 0:
        case 2:
          name = args.getStringAt(1);
          if (name != null) {
            this.myTerminal.setWindowTitle(name);
            return true;
          } 
          break;
        case 7:
          path = args.getStringAt(1);
          if (path != null) {
            this.myTerminal.setCurrentPath(path);
            return true;
          } 
          break;
        case 8:
          uri = args.getStringAt(2);
          if (uri != null) {
            if (!uri.isEmpty()) {
              this.myTerminal.setLinkUriStarted(uri);
            } else {
              
              this.myTerminal.setLinkUriFinished();
            } 
            return true;
          } 
          break;
      } 
    
    } 
    return false;
  }
  
  private void processTwoCharSequence(char ch, Terminal terminal) throws IOException {
    char secondCh = this.myDataStream.getChar();
    switch (ch) {
      case ' ':
        switch (secondCh) {
          
          case 'F':
            unsupported("Switching ot 7-bit");
            break;
          case 'G':
            unsupported("Switching ot 8-bit");
            break;
          
          case 'L':
            terminal.setAnsiConformanceLevel(1);
            break;
          case 'M':
            terminal.setAnsiConformanceLevel(2);
            break;
          case 'N':
            terminal.setAnsiConformanceLevel(3);
            break;
        } 
        
        unsupported(new char[] { ch, secondCh });
        break;
      
      case '#':
        switch (secondCh) {
          case '8':
            terminal.fillScreen('E');
            break;
        } 
        unsupported(new char[] { ch, secondCh });
        break;
      
      case '%':
        switch (secondCh) {
          case '@':
          case 'G':
            unsupported("Selecting charset is unsupported: " + escapeSequenceToString(new char[] { ch, secondCh }));
            break;
        } 
        unsupported(new char[] { ch, secondCh });
        break;
      
      case '(':
        terminal.designateCharacterSet(0, secondCh);
        break;
      case ')':
        terminal.designateCharacterSet(1, secondCh);
        break;
      case '*':
        terminal.designateCharacterSet(2, secondCh);
        break;
      case '+':
        terminal.designateCharacterSet(3, secondCh);
        break;
      case '-':
        terminal.designateCharacterSet(1, secondCh);
        break;
      case '.':
        terminal.designateCharacterSet(2, secondCh);
        break;
      case '/':
        terminal.designateCharacterSet(3, secondCh);
        break;
      case '$':
      case '@':
        unsupported(new char[] { ch, secondCh });
        break;
    } 
  }





  
  protected void unsupported(char... sequenceChars) {
    unsupported(escapeSequenceToString(sequenceChars));
  }





  
  private static void unsupported(String msg) {
    unhandledLogThrottler("Unsupported control characters: " + msg);
  }
  
  private static void unhandledLogThrottler(String msg) {
    logThrottlerCounter++;
    if (logThrottlerCounter < logThrottlerLimit) {
      if (logThrottlerCounter % logThrottlerLimit / logThrottlerRatio == 0) {
        if (logThrottlerLimit / logThrottlerRatio > 1) {
          msg = msg + " and " + (logThrottlerLimit / logThrottlerRatio) + " more...";
        }
        LOG.error(msg);
      } 
    } else {
      logThrottlerLimit *= 10;
    } 
  }
  
  private static String escapeSequenceToString(char... b) {
    StringBuilder sb = new StringBuilder("ESC ");
    
    for (char c : b) {
      sb.append(' ');
      sb.append(c);
    } 
    return sb.toString();
  }
  
  private boolean processControlSequence(ControlSequence args) {
    switch (args.getFinalChar()) {
      case '@':
        return insertBlankCharacters(args);
      case 'A':
        return cursorUp(args);
      case 'B':
        return cursorDown(args);
      case 'C':
        return cursorForward(args);
      case 'D':
        return cursorBackward(args);
      case 'E':
        return cursorNextLine(args);
      case 'F':
        return cursorPrecedingLine(args);
      case 'G':
      case '`':
        return cursorHorizontalAbsolute(args);
      case 'H':
      case 'f':
        return cursorPosition(args);
      case 'J':
        return eraseInDisplay(args);
      case 'K':
        return eraseInLine(args);
      case 'L':
        return insertLines(args);
      case 'M':
        return deleteLines(args);
      case 'X':
        return eraseCharacters(args);
      case 'P':
        return deleteCharacters(args);
      case 'S':
        return scrollUp(args);
      case 'T':
        return scrollDown(args);
      case 'c':
        if (args.startsWithMoreMark()) {
          if (args.getArg(0, 0) == 0) {
            sendDeviceAttributes();
            return true;
          } 
          return false;
        } 
        return sendDeviceAttributes();
      case 'd':
        return linePositionAbsolute(args);
      case 'g':
        return tabClear(args.getArg(0, 0));
      case 'h':
        return setModeOrPrivateMode(args, true);
      case 'l':
        return setModeOrPrivateMode(args, false);
      case 'm':
        if (args.startsWithMoreMark())
        {
          
          return false;
        }
        return characterAttributes(args);
      case 'n':
        return deviceStatusReport(args);
      case 'q':
        return cursorShape(args);
      case 'r':
        if (args.startsWithQuestionMark()) {
          return restoreDecPrivateModeValues(args);
        }
        
        return setScrollingRegion(args);
      
      case 't':
        return windowManipulation(args);
    } 
    return false;
  }
  
  private boolean windowManipulation(ControlSequence args) {
    int width;
    int height;
    switch (args.getArg(0, -1)) {



      
      case 8:
        width = args.getArg(2, 0);
        height = args.getArg(1, 0);
        if (width == 0) {
          width = this.myTerminal.getTerminalWidth();
        }
        if (height == 0) {
          height = this.myTerminal.getTerminalHeight();
        }
        this.myTerminal.resize(new Dimension(width, height), RequestOrigin.Remote);
        return true;
    } 
    return false;
  }

  
  private boolean tabClear(int mode) {
    if (mode == 0) {
      this.myTerminal.clearTabStopAtCursor();
      return true;
    }  if (mode == 3) {
      this.myTerminal.clearAllTabStops();
      return true;
    } 
    return false;
  }

  
  private boolean eraseCharacters(ControlSequence args) {
    this.myTerminal.eraseCharacters(args.getArg(0, 1));
    return true;
  }
  
  private boolean setModeOrPrivateMode(ControlSequence args, boolean enabled) {
    if (args.startsWithQuestionMark()) {
      switch (args.getArg(0, -1)) {
        case 1:
          setModeEnabled(TerminalMode.CursorKey, enabled);
          return true;
        case 3:
          setModeEnabled(TerminalMode.WideColumn, enabled);
          return true;
        case 4:
          setModeEnabled(TerminalMode.SmoothScroll, enabled);
          return true;
        case 5:
          setModeEnabled(TerminalMode.ReverseVideo, enabled);
          return true;
        case 6:
          setModeEnabled(TerminalMode.OriginMode, enabled);
          return true;
        case 7:
          setModeEnabled(TerminalMode.AutoWrap, enabled);
          return true;
        case 8:
          setModeEnabled(TerminalMode.AutoRepeatKeys, enabled);
          return true;

        
        case 12:
          return true;
        case 25:
          setModeEnabled(TerminalMode.CursorVisible, enabled);
          return true;
        case 40:
          setModeEnabled(TerminalMode.AllowWideColumn, enabled);
          return true;
        case 45:
          setModeEnabled(TerminalMode.ReverseWrapAround, enabled);
          return true;
        case 47:
        case 1047:
          setModeEnabled(TerminalMode.AlternateBuffer, enabled);
          return true;
        case 1048:
          setModeEnabled(TerminalMode.StoreCursor, enabled);
          return true;
        case 1049:
          setModeEnabled(TerminalMode.StoreCursor, enabled);
          setModeEnabled(TerminalMode.AlternateBuffer, enabled);
          return true;
        case 1000:
          if (enabled) {
            setMouseMode(MouseMode.MOUSE_REPORTING_NORMAL);
          } else {
            setMouseMode(MouseMode.MOUSE_REPORTING_NONE);
          } 
          return true;
        case 1001:
          if (enabled) {
            setMouseMode(MouseMode.MOUSE_REPORTING_HILITE);
          } else {
            setMouseMode(MouseMode.MOUSE_REPORTING_NONE);
          } 
          return true;
        case 1002:
          if (enabled) {
            setMouseMode(MouseMode.MOUSE_REPORTING_BUTTON_MOTION);
          } else {
            setMouseMode(MouseMode.MOUSE_REPORTING_NONE);
          } 
          return true;
        case 1003:
          if (enabled) {
            setMouseMode(MouseMode.MOUSE_REPORTING_ALL_MOTION);
          } else {
            setMouseMode(MouseMode.MOUSE_REPORTING_NONE);
          } 
          return true;
        case 1005:
          if (enabled) {
            this.myTerminal.setMouseFormat(MouseFormat.MOUSE_FORMAT_XTERM_EXT);
          } else {
            this.myTerminal.setMouseFormat(MouseFormat.MOUSE_FORMAT_XTERM);
          } 
          return true;
        case 1006:
          if (enabled) {
            this.myTerminal.setMouseFormat(MouseFormat.MOUSE_FORMAT_SGR);
          } else {
            this.myTerminal.setMouseFormat(MouseFormat.MOUSE_FORMAT_XTERM);
          } 
          return true;
        case 1015:
          if (enabled) {
            this.myTerminal.setMouseFormat(MouseFormat.MOUSE_FORMAT_URXVT);
          } else {
            this.myTerminal.setMouseFormat(MouseFormat.MOUSE_FORMAT_XTERM);
          } 
          return true;
        case 1034:
          setModeEnabled(TerminalMode.EightBitInput, enabled);
          return true;
        case 1039:
          setModeEnabled(TerminalMode.AltSendsEscape, enabled);
          return true;
      } 
      return false;
    } 
    
    switch (args.getArg(0, -1)) {
      case 2:
        setModeEnabled(TerminalMode.KeyboardAction, enabled);
        return true;
      case 4:
        setModeEnabled(TerminalMode.InsertMode, enabled);
        return true;
      case 12:
        setModeEnabled(TerminalMode.SendReceive, enabled);
        return true;
      case 20:
        setModeEnabled(TerminalMode.AutoNewLine, enabled);
        return true;
    } 
    return false;
  }


  
  private boolean linePositionAbsolute(ControlSequence args) {
    int y = args.getArg(0, 1);
    this.myTerminal.linePositionAbsolute(y);
    
    return true;
  }
  
  private boolean restoreDecPrivateModeValues(ControlSequence args) {
    LOG.error("Unsupported: " + args.toString());
    
    return false;
  }
  
  private boolean deviceStatusReport(ControlSequence args) {
    if (args.startsWithQuestionMark()) {
      LOG.error("Don't support DEC-specific Device Report Status");
      return false;
    } 
    int c = args.getArg(0, 0);
    if (c == 5) {
      String str = "\033[0n";
      LOG.debug("Sending Device Report Status : " + str);
      this.myTerminal.deviceStatusReport(str);
      return true;
    }  if (c == 6) {
      int row = this.myTerminal.getCursorY();
      int column = this.myTerminal.getCursorX();
      String str = "\033[" + row + ";" + column + "R";
      
      LOG.debug("Sending Device Report Status : " + str);
      this.myTerminal.deviceStatusReport(str);
      return true;
    } 
    LOG.error("Sending Device Report Status : unsupported parameter: " + args.toString());
    return false;
  }

  
  private boolean cursorShape(ControlSequence args) {
    this.myTerminal.cursorBackward(1);
    switch (args.getArg(0, 0)) {
      case 0:
      case 1:
        this.myTerminal.cursorShape(CursorShape.BLINK_BLOCK);
        return true;
      case 2:
        this.myTerminal.cursorShape(CursorShape.STEADY_BLOCK);
        return true;
      case 3:
        this.myTerminal.cursorShape(CursorShape.BLINK_UNDERLINE);
        return true;
      case 4:
        this.myTerminal.cursorShape(CursorShape.STEADY_UNDERLINE);
        return true;
      case 5:
        this.myTerminal.cursorShape(CursorShape.BLINK_VERTICAL_BAR);
        return true;
      case 6:
        this.myTerminal.cursorShape(CursorShape.STEADY_VERTICAL_BAR);
        return true;
    } 
    LOG.error("Setting cursor shape : unsupported parameter " + args.toString());
    return false;
  }

  
  private boolean insertLines(ControlSequence args) {
    this.myTerminal.insertLines(args.getArg(0, 1));
    return true;
  }
  
  private boolean sendDeviceAttributes() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Identifying to remote system as VT102");
    }
    this.myTerminal.deviceAttributes(CharUtils.VT102_RESPONSE);
    
    return true;
  }
  
  private boolean cursorHorizontalAbsolute(ControlSequence args) {
    int x = args.getArg(0, 1);
    
    this.myTerminal.cursorHorizontalAbsolute(x);
    
    return true;
  }
  
  private boolean cursorNextLine(ControlSequence args) {
    int dx = args.getArg(0, 1);
    dx = (dx == 0) ? 1 : dx;
    this.myTerminal.cursorDown(dx);
    this.myTerminal.cursorHorizontalAbsolute(1);
    
    return true;
  }
  
  private boolean cursorPrecedingLine(ControlSequence args) {
    int dx = args.getArg(0, 1);
    dx = (dx == 0) ? 1 : dx;
    this.myTerminal.cursorUp(dx);
    
    this.myTerminal.cursorHorizontalAbsolute(1);
    
    return true;
  }
  
  private boolean insertBlankCharacters(ControlSequence args) {
    int count = args.getArg(0, 1);
    
    this.myTerminal.insertBlankCharacters(count);
    
    return true;
  }

  
  private boolean eraseInDisplay(ControlSequence args) {
    int arg = args.getArg(0, 0);
    
    if (args.startsWithQuestionMark())
    {
      return false;
    }
    
    this.myTerminal.eraseInDisplay(arg);
    
    return true;
  }

  
  private boolean eraseInLine(ControlSequence args) {
    int arg = args.getArg(0, 0);
    
    if (args.startsWithQuestionMark())
    {
      return false;
    }
    
    this.myTerminal.eraseInLine(arg);
    
    return true;
  }

  
  private boolean deleteLines(ControlSequence args) {
    this.myTerminal.deleteLines(args.getArg(0, 1));
    return true;
  }

  
  private boolean deleteCharacters(ControlSequence args) {
    int arg = args.getArg(0, 1);
    
    this.myTerminal.deleteCharacters(arg);
    
    return true;
  }
  
  private boolean cursorBackward(ControlSequence args) {
    int dx = args.getArg(0, 1);
    dx = (dx == 0) ? 1 : dx;
    
    this.myTerminal.cursorBackward(dx);
    
    return true;
  }
  
  private boolean setScrollingRegion(ControlSequence args) {
    int top = args.getArg(0, 1);
    int bottom = args.getArg(1, this.myTerminal.getTerminalHeight());
    
    this.myTerminal.setScrollingRegion(top, bottom);
    
    return true;
  }
  
  private boolean scrollUp(ControlSequence args) {
    int count = args.getArg(0, 1);
    this.myTerminal.scrollUp(count);
    return true;
  }
  
  private boolean scrollDown(ControlSequence args) {
    int count = args.getArg(0, 1);
    this.myTerminal.scrollDown(count);
    return true;
  }
  
  private boolean cursorForward(ControlSequence args) {
    int countX = args.getArg(0, 1);
    countX = (countX == 0) ? 1 : countX;
    
    this.myTerminal.cursorForward(countX);
    
    return true;
  }
  
  private boolean cursorDown(ControlSequence cs) {
    int countY = cs.getArg(0, 0);
    countY = (countY == 0) ? 1 : countY;
    this.myTerminal.cursorDown(countY);
    return true;
  }
  
  private boolean cursorPosition(ControlSequence cs) {
    int argy = cs.getArg(0, 1);
    int argx = cs.getArg(1, 1);
    
    this.myTerminal.cursorPosition(argx, argy);
    
    return true;
  }
  
  private boolean characterAttributes(ControlSequence args) {
    TextStyle styleState = createStyleState(this.myTerminal.getStyleState().getCurrent(), args);
    
    this.myTerminal.characterAttributes(styleState);
    
    return true;
  }
  
  @NotNull
  private static TextStyle createStyleState(@NotNull TextStyle textStyle, ControlSequence args) {
    if (textStyle == null) $$$reportNull$$$0(0);  TextStyle.Builder builder = textStyle.toBuilder();
    int argCount = args.getCount();
    if (argCount == 0) {
      builder = new TextStyle.Builder();
    }
    
    int i = 0;
    while (i < argCount) {
      TerminalColor color256, bgColor256; int step = 1;
      
      int arg = args.getArg(i, -1);
      if (arg == -1) {
        LOG.error("Error in processing char attributes, arg " + i);
        i++;
        
        continue;
      } 
      switch (arg) {
        case 0:
          builder = new TextStyle.Builder();
          break;
        case 1:
          builder.setOption(TextStyle.Option.BOLD, true);
          break;
        case 2:
          builder.setOption(TextStyle.Option.DIM, true);
          break;
        case 3:
          builder.setOption(TextStyle.Option.ITALIC, true);
          break;
        case 4:
          builder.setOption(TextStyle.Option.UNDERLINED, true);
          break;
        case 5:
          builder.setOption(TextStyle.Option.BLINK, true);
          break;
        case 7:
          builder.setOption(TextStyle.Option.INVERSE, true);
          break;
        case 8:
          builder.setOption(TextStyle.Option.HIDDEN, true);
          break;
        case 22:
          builder.setOption(TextStyle.Option.BOLD, false);
          builder.setOption(TextStyle.Option.DIM, false);
          break;
        case 23:
          builder.setOption(TextStyle.Option.ITALIC, false);
          break;
        case 24:
          builder.setOption(TextStyle.Option.UNDERLINED, false);
          break;
        case 25:
          builder.setOption(TextStyle.Option.BLINK, false);
          break;
        case 27:
          builder.setOption(TextStyle.Option.INVERSE, false);
          break;
        case 28:
          builder.setOption(TextStyle.Option.HIDDEN, false);
          break;
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
          builder.setForeground(TerminalColor.index(arg - 30));
          break;
        case 38:
          color256 = getColor256(args, i);
          if (color256 != null) {
            builder.setForeground(color256);
            step = getColor256Step(args, i);
          } 
          break;
        case 39:
          builder.setForeground(null);
          break;
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
          builder.setBackground(TerminalColor.index(arg - 40));
          break;
        case 48:
          bgColor256 = getColor256(args, i);
          if (bgColor256 != null) {
            builder.setBackground(bgColor256);
            step = getColor256Step(args, i);
          } 
          break;
        case 49:
          builder.setBackground(null);
          break;
        
        case 90:
        case 91:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
          builder.setForeground(ColorPalette.getIndexedTerminalColor(arg - 82));
          break;
        
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
          builder.setBackground(ColorPalette.getIndexedTerminalColor(arg - 92));
          break;
        default:
          LOG.error("Unknown character attribute:" + arg); break;
      } 
      i += step;
    } 
    if (builder.build() == null) $$$reportNull$$$0(1);  return builder.build();
  }
  
  private static TerminalColor getColor256(ControlSequence args, int index) {
    int code = args.getArg(index + 1, 0);
    
    if (code == 2) {
      
      int val0 = args.getArg(index + 2, -1);
      int val1 = args.getArg(index + 3, -1);
      int val2 = args.getArg(index + 4, -1);
      if (val0 >= 0 && val0 < 256 && val1 >= 0 && val1 < 256 && val2 >= 0 && val2 < 256)
      {
        
        return new TerminalColor(val0, val1, val2);
      }
      LOG.error("Bogus color setting " + args.toString());
      return null;
    } 
    if (code == 5)
    {
      return ColorPalette.getIndexedTerminalColor(args.getArg(index + 2, 0));
    }
    LOG.error("Unsupported code for color attribute " + args.toString());
    return null;
  }

  
  private static int getColor256Step(ControlSequence args, int i) {
    int code = args.getArg(i + 1, 0);
    if (code == 2)
      return 5; 
    if (code == 5) {
      return 3;
    }
    return 1;
  }
  
  private boolean cursorUp(ControlSequence cs) {
    int arg = cs.getArg(0, 0);
    arg = (arg == 0) ? 1 : arg;
    this.myTerminal.cursorUp(arg);
    return true;
  }
  
  private void setModeEnabled(TerminalMode mode, boolean enabled) {
    if (LOG.isDebugEnabled()) {
      LOG.info("Setting mode " + mode + " enabled = " + enabled);
    }
    this.myTerminal.setModeEnabled(mode, enabled);
  }
  
  public void setMouseMode(MouseMode mouseMode) {
    this.myTerminal.setMouseMode(mouseMode);
  }
  @NotNull
  public CompletableFuture<?> getPromptUpdatedAfterResizeFuture(@NotNull BiConsumer<Long, Runnable> taskScheduler) {
    if (taskScheduler == null) $$$reportNull$$$0(2);  CompletableFuture<Void> resizeFuture = new CompletableFuture<>();
    taskScheduler.accept(Long.valueOf(100L), this::completeResize);
    this.myResizeFutureQueue.add(resizeFuture);
    if (resizeFuture == null) $$$reportNull$$$0(3);  return resizeFuture;
  }
  
  private void completeResize() {
    CompletableFuture<Void> resizeFuture;
    while ((resizeFuture = this.myResizeFutureQueue.poll()) != null)
      resizeFuture.complete(null); 
  }
}
