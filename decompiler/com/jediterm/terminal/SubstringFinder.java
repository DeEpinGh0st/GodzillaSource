package com.jediterm.terminal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jediterm.terminal.model.CharBuffer;
import com.jediterm.terminal.model.SubCharBuffer;
import com.jediterm.terminal.util.Pair;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;






public class SubstringFinder
{
  private final String myPattern;
  private final int myPatternHash;
  private int myCurrentHash;
  private int myCurrentLength;
  private final ArrayList<TextToken> myTokens = Lists.newArrayList();
  private int myFirstIndex;
  private int myPower = 0;
  
  private final FindResult myResult = new FindResult();
  
  private boolean myIgnoreCase;
  
  public SubstringFinder(String pattern, boolean ignoreCase) {
    this.myIgnoreCase = ignoreCase;
    this.myPattern = ignoreCase ? pattern.toLowerCase() : pattern;
    this.myPatternHash = this.myPattern.hashCode();
  }

  
  public void nextChar(int x, int y, CharBuffer characters, int index) {
    if (this.myTokens.size() == 0 || ((TextToken)this.myTokens.get(this.myTokens.size() - 1)).buf != characters) {
      this.myTokens.add(new TextToken(x, y, characters));
    }
    
    if (this.myCurrentLength == this.myPattern.length()) {
      this.myCurrentHash -= hashCodeForChar(((TextToken)this.myTokens.get(0)).buf.charAt(this.myFirstIndex));
      if (this.myFirstIndex + 1 == ((TextToken)this.myTokens.get(0)).buf.length()) {
        this.myFirstIndex = 0;
        this.myTokens.remove(0);
      } else {
        this.myFirstIndex++;
      } 
    } else {
      this.myCurrentLength++;
      if (this.myPower == 0) {
        this.myPower = 1;
      } else {
        this.myPower *= 31;
      } 
    } 
    
    this.myCurrentHash = 31 * this.myCurrentHash + charHash(characters.charAt(index));
    
    if (this.myCurrentLength == this.myPattern.length() && this.myCurrentHash == this.myPatternHash) {
      FindResult.FindItem item = new FindResult.FindItem(this.myTokens, this.myFirstIndex, index, -1);
      String itemText = item.getText();
      boolean matched = this.myPattern.equals(this.myIgnoreCase ? itemText.toLowerCase() : itemText);
      if (matched && accept(item)) {
        this.myResult.patternMatched(this.myTokens, this.myFirstIndex, index);
        this.myCurrentHash = 0;
        this.myCurrentLength = 0;
        this.myPower = 0;
        this.myTokens.clear();
        if (index + 1 < characters.length()) {
          this.myFirstIndex = index + 1;
          this.myTokens.add(new TextToken(x, y, characters));
        } else {
          this.myFirstIndex = 0;
        } 
      } 
    } 
  }
  
  public boolean accept(@NotNull FindResult.FindItem item) {
    if (item == null) $$$reportNull$$$0(0);  return true;
  }
  
  private int charHash(char c) {
    return this.myIgnoreCase ? Character.toLowerCase(c) : c;
  }
  
  private int hashCodeForChar(char charAt) {
    return this.myPower * charHash(charAt);
  }
  
  public FindResult getResult() {
    return this.myResult;
  }
  
  public static class FindResult {
    private final List<FindItem> items = Lists.newArrayList();
    private final Map<CharBuffer, List<Pair<Integer, Integer>>> ranges = Maps.newHashMap();
    private int currentFindItem = 0;
    
    public List<Pair<Integer, Integer>> getRanges(CharBuffer characters) {
      if (characters instanceof SubCharBuffer) {
        SubCharBuffer subCharBuffer = (SubCharBuffer)characters;
        List<Pair<Integer, Integer>> pairs = this.ranges.get(subCharBuffer.getParent());
        if (pairs != null) {
          List<Pair<Integer, Integer>> filtered = new ArrayList<>();
          for (Pair<Integer, Integer> pair : pairs) {
            Pair<Integer, Integer> intersected = intersect(pair, subCharBuffer.getOffset(), subCharBuffer.getOffset() + subCharBuffer.length());
            if (intersected != null) {
              filtered.add(Pair.create(Integer.valueOf(((Integer)intersected.first).intValue() - subCharBuffer.getOffset()), Integer.valueOf(((Integer)intersected.second).intValue() - subCharBuffer.getOffset())));
            }
          } 
          return filtered;
        } 
        return null;
      } 
      return this.ranges.get(characters);
    }
    @Nullable
    private Pair<Integer, Integer> intersect(@NotNull Pair<Integer, Integer> interval, int a, int b) {
      if (interval == null) $$$reportNull$$$0(0);  int start = Math.max(((Integer)interval.first).intValue(), a);
      int end = Math.min(((Integer)interval.second).intValue(), b);
      return (start < end) ? Pair.create(Integer.valueOf(start), Integer.valueOf(end)) : null;
    }

    
    public static class FindItem
    {
      final ArrayList<SubstringFinder.TextToken> tokens;
      final int firstIndex;
      final int lastIndex;
      final int index;
      
      private FindItem(ArrayList<SubstringFinder.TextToken> tokens, int firstIndex, int lastIndex, int index) {
        this.tokens = Lists.newArrayList(tokens);
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.index = index;
      }
      
      @NotNull
      public String getText() {
        StringBuilder b = new StringBuilder();
        
        if (this.tokens.size() > 1) {
          Pair<Integer, Integer> range = Pair.create(Integer.valueOf(this.firstIndex), Integer.valueOf(((SubstringFinder.TextToken)this.tokens.get(0)).buf.length()));
          b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(0)).buf.subBuffer(range));
        } else {
          Pair<Integer, Integer> range = Pair.create(Integer.valueOf(this.firstIndex), Integer.valueOf(this.lastIndex + 1));
          b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(0)).buf.subBuffer(range));
        } 
        
        for (int i = 1; i < this.tokens.size() - 1; i++) {
          b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(i)).buf);
        }
        
        if (this.tokens.size() > 1) {
          Pair<Integer, Integer> range = Pair.create(Integer.valueOf(0), Integer.valueOf(this.lastIndex + 1));
          b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(this.tokens.size() - 1)).buf.subBuffer(range));
        } 
        
        if (b.toString() == null) $$$reportNull$$$0(0);  return b.toString();
      }

      
      public String toString() {
        return getText();
      }

      
      public int getIndex() {
        return this.index;
      }
      
      public Point getStart() {
        return new Point(((SubstringFinder.TextToken)this.tokens.get(0)).x + this.firstIndex, ((SubstringFinder.TextToken)this.tokens.get(0)).y);
      }
      
      public Point getEnd() {
        return new Point(((SubstringFinder.TextToken)this.tokens.get(this.tokens.size() - 1)).x + this.lastIndex, ((SubstringFinder.TextToken)this.tokens.get(this.tokens.size() - 1)).y);
      }
    }
    
    public void patternMatched(ArrayList<SubstringFinder.TextToken> tokens, int firstIndex, int lastIndex) {
      if (tokens.size() > 1) {
        Pair<Integer, Integer> range = Pair.create(Integer.valueOf(firstIndex), Integer.valueOf(((SubstringFinder.TextToken)tokens.get(0)).buf.length()));
        put(((SubstringFinder.TextToken)tokens.get(0)).buf, range);
      } else {
        Pair<Integer, Integer> range = Pair.create(Integer.valueOf(firstIndex), Integer.valueOf(lastIndex + 1));
        put(((SubstringFinder.TextToken)tokens.get(0)).buf, range);
      } 
      
      for (int i = 1; i < tokens.size() - 1; i++) {
        put(((SubstringFinder.TextToken)tokens.get(i)).buf, Pair.create(Integer.valueOf(0), Integer.valueOf(((SubstringFinder.TextToken)tokens.get(i)).buf.length())));
      }
      
      if (tokens.size() > 1) {
        Pair<Integer, Integer> range = Pair.create(Integer.valueOf(0), Integer.valueOf(lastIndex + 1));
        put(((SubstringFinder.TextToken)tokens.get(tokens.size() - 1)).buf, range);
      } 
      
      this.items.add(new FindItem(tokens, firstIndex, lastIndex, this.items.size() + 1));
    }

    
    private void put(CharBuffer characters, Pair<Integer, Integer> range) {
      if (this.ranges.containsKey(characters)) {
        ((List<Pair<Integer, Integer>>)this.ranges.get(characters)).add(range);
      } else {
        this.ranges.put(characters, Lists.newArrayList((Object[])new Pair[] { range }));
      } 
    }
    
    public List<FindItem> getItems() {
      return this.items;
    }
    
    public FindItem prevFindItem() {
      if (this.items.isEmpty()) {
        return null;
      }
      this.currentFindItem++;
      this.currentFindItem %= this.items.size();
      return this.items.get(this.currentFindItem);
    }
    
    public FindItem nextFindItem() {
      if (this.items.isEmpty()) {
        return null;
      }
      this.currentFindItem--;
      
      this.currentFindItem = (this.currentFindItem + this.items.size()) % this.items.size();
      return this.items.get(this.currentFindItem);
    }
  }
  public static class FindItem {
    final ArrayList<SubstringFinder.TextToken> tokens;
    final int firstIndex; final int lastIndex; final int index; private FindItem(ArrayList<SubstringFinder.TextToken> tokens, int firstIndex, int lastIndex, int index) { this.tokens = Lists.newArrayList(tokens); this.firstIndex = firstIndex; this.lastIndex = lastIndex; this.index = index; } @NotNull public String getText() { StringBuilder b = new StringBuilder(); if (this.tokens.size() > 1) { Pair<Integer, Integer> range = Pair.create(Integer.valueOf(this.firstIndex), Integer.valueOf(((SubstringFinder.TextToken)this.tokens.get(0)).buf.length())); b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(0)).buf.subBuffer(range)); } else { Pair<Integer, Integer> range = Pair.create(Integer.valueOf(this.firstIndex), Integer.valueOf(this.lastIndex + 1)); b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(0)).buf.subBuffer(range)); }  for (int i = 1; i < this.tokens.size() - 1; i++)
        b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(i)).buf);  if (this.tokens.size() > 1) { Pair<Integer, Integer> range = Pair.create(Integer.valueOf(0), Integer.valueOf(this.lastIndex + 1)); b.append((CharSequence)((SubstringFinder.TextToken)this.tokens.get(this.tokens.size() - 1)).buf.subBuffer(range)); }
       if (b.toString() == null)
        $$$reportNull$$$0(0);  return b.toString(); } public String toString() { return getText(); } public int getIndex() { return this.index; } public Point getStart() { return new Point(((SubstringFinder.TextToken)this.tokens.get(0)).x + this.firstIndex, ((SubstringFinder.TextToken)this.tokens.get(0)).y); } public Point getEnd() { return new Point(((SubstringFinder.TextToken)this.tokens.get(this.tokens.size() - 1)).x + this.lastIndex, ((SubstringFinder.TextToken)this.tokens.get(this.tokens.size() - 1)).y); }
  } private static class TextToken {
    private TextToken(int x, int y, CharBuffer buf) { this.x = x;
      this.y = y;
      this.buf = buf; }

    
    final CharBuffer buf;
    final int x;
    final int y;
  }
}
