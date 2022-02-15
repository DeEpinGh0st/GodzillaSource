package com.jediterm.terminal.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class TerminalLine
{
  private static final Logger LOG = Logger.getLogger(TerminalLine.class);
  
  private TextEntries myTextEntries = new TextEntries();
  private boolean myWrapped = false;
  private final List<TerminalLineIntervalHighlighting> myCustomHighlightings = new ArrayList<>();



  
  public TerminalLine(@NotNull TextEntry entry) {
    this.myTextEntries.add(entry);
  }
  
  public static TerminalLine createEmpty() {
    return new TerminalLine();
  }
  
  public synchronized String getText() {
    StringBuilder sb = new StringBuilder();
    
    for (TextEntry textEntry : Lists.newArrayList(this.myTextEntries)) {
      
      if (textEntry.getText().isNul()) {
        break;
      }
      sb.append(textEntry.getText());
    } 

    
    return sb.toString();
  }
  
  public char charAt(int x) {
    String text = getText();
    return (x < text.length()) ? text.charAt(x) : ' ';
  }
  
  public boolean isWrapped() {
    return this.myWrapped;
  }
  
  public void setWrapped(boolean wrapped) {
    this.myWrapped = wrapped;
  }
  
  public synchronized void clear(@NotNull TextEntry filler) {
    if (filler == null) $$$reportNull$$$0(1);  this.myTextEntries.clear();
    this.myTextEntries.add(filler);
    setWrapped(false);
  }
  
  public void writeString(int x, @NotNull CharBuffer str, @NotNull TextStyle style) {
    if (str == null) $$$reportNull$$$0(2);  if (style == null) $$$reportNull$$$0(3);  writeCharacters(x, style, str);
  }
  
  private synchronized void writeCharacters(int x, @NotNull TextStyle style, @NotNull CharBuffer characters) {
    if (style == null) $$$reportNull$$$0(4);  if (characters == null) $$$reportNull$$$0(5);  int len = this.myTextEntries.length();
    
    if (x >= len) {
      
      if (x - len > 0) {
        this.myTextEntries.add(new TextEntry(TextStyle.EMPTY, new CharBuffer(false, x - len)));
      }
      this.myTextEntries.add(new TextEntry(style, characters));
    } else {
      len = Math.max(len, x + characters.length());
      this.myTextEntries = merge(x, characters, style, this.myTextEntries, len);
    } 
  }
  
  private static TextEntries merge(int x, @NotNull CharBuffer str, @NotNull TextStyle style, @NotNull TextEntries entries, int lineLength) {
    if (str == null) $$$reportNull$$$0(6);  if (style == null) $$$reportNull$$$0(7);  if (entries == null) $$$reportNull$$$0(8);  Pair<char[], TextStyle[]> pair = toBuf(entries, lineLength);
    
    for (int i = 0; i < str.length(); i++) {
      ((char[])pair.first)[i + x] = str.charAt(i);
      ((TextStyle[])pair.second)[i + x] = style;
    } 
    
    return collectFromBuffer((char[])pair.first, (TextStyle[])pair.second);
  }
  
  private static Pair<char[], TextStyle[]> toBuf(TextEntries entries, int lineLength) {
    Pair<char[], TextStyle[]> pair = Pair.create(new char[lineLength], new TextStyle[lineLength]);

    
    int p = 0;
    for (TextEntry entry : entries) {
      for (int i = 0; i < entry.getLength(); i++) {
        ((char[])pair.first)[p + i] = entry.getText().charAt(i);
        ((TextStyle[])pair.second)[p + i] = entry.getStyle();
      } 
      p += entry.getLength();
    } 
    return pair;
  }
  
  private static TextEntries collectFromBuffer(char[] buf, @NotNull TextStyle[] styles) {
    if (styles == null) $$$reportNull$$$0(9);  TextEntries result = new TextEntries();
    
    TextStyle curStyle = styles[0];
    int start = 0;
    
    for (int i = 1; i < buf.length; i++) {
      if (styles[i] != curStyle) {
        result.add(new TextEntry(curStyle, new CharBuffer(buf, start, i - start)));
        curStyle = styles[i];
        start = i;
      } 
    } 
    
    result.add(new TextEntry(curStyle, new CharBuffer(buf, start, buf.length - start)));
    
    return result;
  }
  
  public synchronized void deleteCharacters(int x) {
    deleteCharacters(x, TextStyle.EMPTY);
  }
  
  public synchronized void deleteCharacters(int x, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(10);  deleteCharacters(x, this.myTextEntries.length() - x, style);
    
    setWrapped(false);
  }
  
  public synchronized void deleteCharacters(int x, int count, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(11);  int p = 0;
    TextEntries newEntries = new TextEntries();
    
    int remaining = count;
    
    for (TextEntry entry : this.myTextEntries) {
      if (remaining == 0) {
        newEntries.add(entry);
        continue;
      } 
      int len = entry.getLength();
      if (p + len <= x) {
        p += len;
        newEntries.add(entry);
        continue;
      } 
      int dx = x - p;
      if (dx > 0) {
        
        newEntries.add(new TextEntry(entry.getStyle(), entry.getText().subBuffer(0, dx)));
        p = x;
      } 
      if (dx + remaining < len) {
        
        newEntries.add(new TextEntry(entry.getStyle(), entry.getText().subBuffer(dx + remaining, len - dx + remaining)));
        remaining = 0; continue;
      } 
      remaining -= len - dx;
      p = x;
    } 
    
    if (count > 0 && style != TextStyle.EMPTY) {
      newEntries.add(new TextEntry(style, new CharBuffer(false, count)));
    }
    
    this.myTextEntries = newEntries;
  }
  
  public synchronized void insertBlankCharacters(int x, int count, int maxLen, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(12);  int len = this.myTextEntries.length();
    len = Math.min(len + count, maxLen);
    
    char[] buf = new char[len];
    TextStyle[] styles = new TextStyle[len];
    
    int p = 0;
    for (TextEntry entry : this.myTextEntries) {
      for (int i = 0; i < entry.getLength() && p < len; i++) {
        if (p == x) {
          for (int j = 0; j < count && p < len; j++) {
            buf[p] = ' ';
            styles[p] = style;
            p++;
          } 
        }
        if (p < len) {
          buf[p] = entry.getText().charAt(i);
          styles[p] = entry.getStyle();
          p++;
        } 
      } 
      if (p >= len) {
        break;
      }
    } 

    
    for (; p < x && p < len; p++) {
      buf[p] = ' ';
      styles[p] = TextStyle.EMPTY;
      p++;
    } 
    for (; p < x + count && p < len; p++) {
      buf[p] = ' ';
      styles[p] = style;
      p++;
    } 
    
    this.myTextEntries = collectFromBuffer(buf, styles);
  }
  
  public synchronized void clearArea(int leftX, int rightX, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(13);  if (rightX == -1) {
      rightX = Math.max(this.myTextEntries.length(), leftX);
    }
    writeCharacters(leftX, style, new CharBuffer(
          (rightX >= this.myTextEntries.length()) ? Character.MIN_VALUE : 32, rightX - leftX));
  }

  
  @Nullable
  public synchronized TextStyle getStyleAt(int x) {
    int i = 0;
    
    for (TextEntry te : this.myTextEntries) {
      if (x >= i && x < i + te.getLength()) {
        return te.getStyle();
      }
      i += te.getLength();
    } 
    
    return null;
  }
  
  public synchronized void process(int y, StyledTextConsumer consumer, int startRow) {
    int x = 0;
    int nulIndex = -1;
    TerminalLineIntervalHighlighting highlighting = this.myCustomHighlightings.stream().findFirst().orElse(null);
    for (TextEntry te : this.myTextEntries) {
      if (te.getText().isNul()) {
        if (nulIndex < 0) {
          nulIndex = x;
        }
        consumer.consumeNul(x, y, nulIndex, te.getStyle(), te.getText(), startRow);
      }
      else if (highlighting != null && te.getLength() > 0 && highlighting.intersectsWith(x, x + te.getLength())) {
        processIntersection(x, y, te, consumer, startRow, highlighting);
      } else {
        
        consumer.consume(x, y, te.getStyle(), te.getText(), startRow);
      } 
      
      x += te.getLength();
    } 
    consumer.consumeQueue(x, y, (nulIndex < 0) ? x : nulIndex, startRow);
  }

  
  private void processIntersection(int startTextOffset, int y, @NotNull TextEntry te, @NotNull StyledTextConsumer consumer, int startRow, @NotNull TerminalLineIntervalHighlighting highlighting) {
    if (te == null) $$$reportNull$$$0(14);  if (consumer == null) $$$reportNull$$$0(15);  if (highlighting == null) $$$reportNull$$$0(16);  CharBuffer text = te.getText();
    int endTextOffset = startTextOffset + text.length();
    int[] offsets = { startTextOffset, endTextOffset, highlighting.getStartOffset(), highlighting.getEndOffset() };
    Arrays.sort(offsets);
    int startTextOffsetInd = Arrays.binarySearch(offsets, startTextOffset);
    int endTextOffsetInd = Arrays.binarySearch(offsets, endTextOffset);
    if (startTextOffsetInd < 0 || endTextOffsetInd < 0) {
      LOG.error("Cannot find " + Arrays.toString(new int[] { startTextOffset, endTextOffset }) + " in " + 
          Arrays.toString(offsets) + ": " + Arrays.toString(new int[] { startTextOffsetInd, endTextOffsetInd }));
      consumer.consume(startTextOffset, y, te.getStyle(), text, startRow);
      return;
    } 
    for (int i = startTextOffsetInd; i < endTextOffsetInd; i++) {
      int length = offsets[i + 1] - offsets[i];
      if (length != 0) {
        CharBuffer subText = new SubCharBuffer(text, offsets[i] - startTextOffset, length);
        if (highlighting.intersectsWith(offsets[i], offsets[i + 1])) {
          consumer.consume(offsets[i], y, highlighting.mergeWith(te.getStyle()), subText, startRow);
        } else {
          
          consumer.consume(offsets[i], y, te.getStyle(), subText, startRow);
        } 
      } 
    } 
  }
  public synchronized boolean isNul() {
    for (TextEntry e : this.myTextEntries.entries()) {
      if (!e.isNul()) {
        return false;
      }
    } 
    
    return true;
  }
  
  void forEachEntry(@NotNull Consumer<TextEntry> action) {
    if (action == null) $$$reportNull$$$0(17);  this.myTextEntries.forEach(action);
  }

  
  public List<TextEntry> getEntries() {
    return this.myTextEntries.entries();
  }
  
  void appendEntry(@NotNull TextEntry entry) {
    if (entry == null) $$$reportNull$$$0(18);  this.myTextEntries.add(entry);
  }
  @NotNull
  public synchronized TerminalLineIntervalHighlighting addCustomHighlighting(int startOffset, int length, @NotNull TextStyle textStyle) {
    if (textStyle == null) $$$reportNull$$$0(19);  TerminalLineIntervalHighlighting highlighting = new TerminalLineIntervalHighlighting(this, startOffset, length, textStyle)
      {
        protected void doDispose() {
          synchronized (TerminalLine.this) {
            TerminalLine.this.myCustomHighlightings.remove(this);
          } 
        }
      };
    this.myCustomHighlightings.add(highlighting);
    if (highlighting == null) $$$reportNull$$$0(20);  return highlighting;
  }

  
  public String toString() {
    return this.myTextEntries.length() + " chars, " + (this.myWrapped ? "wrapped, " : "") + this.myTextEntries
      
      .myTextEntries.size() + " entries: " + 
      Joiner.on("|").join((Iterable)this.myTextEntries.myTextEntries.stream().map(entry -> entry.getText().toString())
        .collect(Collectors.toList()));
  }
  
  public TerminalLine() {}
  
  public static class TextEntry { private final TextStyle myStyle;
    private final CharBuffer myText;
    
    public TextEntry(@NotNull TextStyle style, @NotNull CharBuffer text) {
      this.myStyle = style;
      this.myText = text.clone();
    }
    
    public TextStyle getStyle() {
      return this.myStyle;
    }
    
    public CharBuffer getText() {
      return this.myText;
    }
    
    public int getLength() {
      return this.myText.length();
    }
    
    public boolean isNul() {
      return this.myText.isNul();
    }

    
    public String toString() {
      return this.myText.length() + " chars, style: " + this.myStyle + ", text: " + this.myText;
    } }

  
  private static class TextEntries implements Iterable<TextEntry> {
    private final List<TerminalLine.TextEntry> myTextEntries = new ArrayList<>();
    
    private int myLength = 0;

    
    public void add(TerminalLine.TextEntry entry) {
      if (!entry.getText().isNul()) {
        for (TerminalLine.TextEntry t : this.myTextEntries) {
          if (t.getText().isNul()) {
            t.getText().unNullify();
          }
        } 
      }
      this.myTextEntries.add(entry);
      this.myLength += entry.getLength();
    }
    
    private List<TerminalLine.TextEntry> entries() {
      return Collections.unmodifiableList(this.myTextEntries);
    }
    
    @NotNull
    public Iterator<TerminalLine.TextEntry> iterator() {
      if (entries().iterator() == null) $$$reportNull$$$0(0);  return entries().iterator();
    }
    
    public int length() {
      return this.myLength;
    }
    
    public void clear() {
      this.myTextEntries.clear();
      this.myLength = 0;
    }
    
    private TextEntries() {}
  }
}
