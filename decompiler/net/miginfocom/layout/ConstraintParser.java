package net.miginfocom.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;














































public final class ConstraintParser
{
  public static LC parseLayoutConstraint(String s) {
    LC lc = new LC();
    if (s.isEmpty()) {
      return lc;
    }
    String[] parts = toTrimmedTokens(s, ',');

    
    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      if (part != null) {

        
        int len = part.length();
        if (len == 3 || len == 11) {
          if (part.equals("ltr") || part.equals("rtl") || part.equals("lefttoright") || part.equals("righttoleft")) {
            lc.setLeftToRight((part.charAt(0) == 'l') ? Boolean.TRUE : Boolean.FALSE);
            parts[i] = null;
          } 
          
          if (part.equals("ttb") || part.equals("btt") || part.equals("toptobottom") || part.equals("bottomtotop")) {
            lc.setTopToBottom((part.charAt(0) == 't'));
            parts[i] = null;
          } 
        } 
      } 
    } 
    for (String part : parts) {
      if (part == null || part.length() == 0) {
        continue;
      }
      try {
        int ix = -1;
        char c = part.charAt(0);
        
        if (c == 'w' || c == 'h') {
          
          ix = startsWithLenient(part, "wrap", -1, true);
          if (ix > -1) {
            String num = part.substring(ix).trim();
            lc.setWrapAfter((num.length() != 0) ? Integer.parseInt(num) : 0);
            
            continue;
          } 
          boolean isHor = (c == 'w');
          if (isHor && (part.startsWith("w ") || part.startsWith("width "))) {
            String sz = part.substring((part.charAt(1) == ' ') ? 2 : 6).trim();
            lc.setWidth(parseBoundSize(sz, false, true));
            
            continue;
          } 
          if (!isHor && (part.startsWith("h ") || part.startsWith("height "))) {
            String uvStr = part.substring((part.charAt(1) == ' ') ? 2 : 7).trim();
            lc.setHeight(parseBoundSize(uvStr, false, false));
            
            continue;
          } 
          if (part.length() > 5) {
            String sz = part.substring(5).trim();
            if (part.startsWith("wmin ")) {
              lc.minWidth(sz); continue;
            } 
            if (part.startsWith("wmax ")) {
              lc.maxWidth(sz); continue;
            } 
            if (part.startsWith("hmin ")) {
              lc.minHeight(sz); continue;
            } 
            if (part.startsWith("hmax ")) {
              lc.maxHeight(sz);
              
              continue;
            } 
          } 
          if (part.startsWith("hidemode ")) {
            lc.setHideMode(Integer.parseInt(part.substring(9)));
            
            continue;
          } 
        } 
        if (c == 'g') {
          if (part.startsWith("gapx ")) {
            lc.setGridGapX(parseBoundSize(part.substring(5).trim(), true, true));
            
            continue;
          } 
          if (part.startsWith("gapy ")) {
            lc.setGridGapY(parseBoundSize(part.substring(5).trim(), true, false));
            
            continue;
          } 
          if (part.startsWith("gap ")) {
            String[] gaps = toTrimmedTokens(part.substring(4).trim(), ' ');
            lc.setGridGapX(parseBoundSize(gaps[0], true, true));
            lc.setGridGapY((gaps.length > 1) ? parseBoundSize(gaps[1], true, false) : lc.getGridGapX());
            
            continue;
          } 
        } 
        if (c == 'd') {
          ix = startsWithLenient(part, "debug", 5, true);
          if (ix > -1) {
            String millis = part.substring(ix).trim();
            lc.setDebugMillis((millis.length() > 0) ? Integer.parseInt(millis) : 1000);
            
            continue;
          } 
        } 
        if (c == 'n') {
          if (part.equals("nogrid")) {
            lc.setNoGrid(true);
            
            continue;
          } 
          if (part.equals("nocache")) {
            lc.setNoCache(true);
            
            continue;
          } 
          if (part.equals("novisualpadding")) {
            lc.setVisualPadding(false);
            
            continue;
          } 
        } 
        if (c == 'f') {
          if (part.equals("fill") || part.equals("fillx") || part.equals("filly")) {
            lc.setFillX((part.length() == 4 || part.charAt(4) == 'x'));
            lc.setFillY((part.length() == 4 || part.charAt(4) == 'y'));
            
            continue;
          } 
          if (part.equals("flowy")) {
            lc.setFlowX(false);
            
            continue;
          } 
          if (part.equals("flowx")) {
            lc.setFlowX(true);
            
            continue;
          } 
        } 
        if (c == 'i') {
          ix = startsWithLenient(part, "insets", 3, true);
          if (ix > -1) {
            String insStr = part.substring(ix).trim();
            UnitValue[] ins = parseInsets(insStr, true);
            LayoutUtil.putCCString(ins, insStr);
            lc.setInsets(ins);
            
            continue;
          } 
        } 
        if (c == 'a') {
          ix = startsWithLenient(part, new String[] { "aligny", "ay" }, new int[] { 6, 2 }, true);
          if (ix > -1) {
            UnitValue align = parseUnitValueOrAlign(part.substring(ix).trim(), false, null);
            if (align == UnitValue.BASELINE_IDENTITY)
              throw new IllegalArgumentException("'baseline' can not be used to align the whole component group."); 
            lc.setAlignY(align);
            
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "alignx", "ax" }, new int[] { 6, 2 }, true);
          if (ix > -1) {
            lc.setAlignX(parseUnitValueOrAlign(part.substring(ix).trim(), true, null));
            
            continue;
          } 
          ix = startsWithLenient(part, "align", 2, true);
          if (ix > -1) {
            String[] gaps = toTrimmedTokens(part.substring(ix).trim(), ' ');
            lc.setAlignX(parseUnitValueOrAlign(gaps[0], true, null));
            if (gaps.length > 1) {
              UnitValue align = parseUnitValueOrAlign(gaps[1], false, null);
              if (align == UnitValue.BASELINE_IDENTITY)
                throw new IllegalArgumentException("'baseline' can not be used to align the whole component group."); 
              lc.setAlignY(align);
            } 
            
            continue;
          } 
        } 
        if (c == 'p') {
          if (part.startsWith("packalign ")) {
            String[] packs = toTrimmedTokens(part.substring(10).trim(), ' ');
            lc.setPackWidthAlign((packs[0].length() > 0) ? Float.parseFloat(packs[0]) : 0.5F);
            if (packs.length > 1) {
              lc.setPackHeightAlign(Float.parseFloat(packs[1]));
            }
            continue;
          } 
          if (part.startsWith("pack ") || part.equals("pack")) {
            String ps = part.substring(4).trim();
            String[] packs = toTrimmedTokens((ps.length() > 0) ? ps : "pref pref", ' ');
            lc.setPackWidth(parseBoundSize(packs[0], false, true));
            if (packs.length > 1) {
              lc.setPackHeight(parseBoundSize(packs[1], false, false));
            }
            
            continue;
          } 
        } 
        if (lc.getAlignX() == null) {
          UnitValue alignX = parseAlignKeywords(part, true);
          if (alignX != null) {
            lc.setAlignX(alignX);
            
            continue;
          } 
        } 
        UnitValue alignY = parseAlignKeywords(part, false);
        if (alignY != null) {
          lc.setAlignY(alignY);
        }
        else {
          
          throw new IllegalArgumentException("Unknown Constraint: '" + part + "'\n");
        } 
      } catch (Exception ex) {
        throw new IllegalArgumentException("Illegal Constraint: '" + part + "'\n" + ex.getMessage());
      } 
      
      continue;
    } 
    
    return lc;
  }






  
  public static AC parseRowConstraints(String s) {
    return parseAxisConstraint(s, false);
  }






  
  public static AC parseColumnConstraints(String s) {
    return parseAxisConstraint(s, true);
  }







  
  private static AC parseAxisConstraint(String s, boolean isCols) {
    s = s.trim();
    
    if (s.length() == 0) {
      return new AC();
    }
    s = s.toLowerCase();
    
    ArrayList<String> parts = getRowColAndGapsTrimmed(s);
    
    BoundSize[] gaps = new BoundSize[(parts.size() >> 1) + 1]; int gIx;
    for (int i = 0, iSz = parts.size(); i < iSz; i += 2, gIx++) {
      gaps[gIx] = parseBoundSize(parts.get(i), true, isCols);
    }
    DimConstraint[] colSpecs = new DimConstraint[parts.size() >> 1];
    for (int j = 0; j < colSpecs.length; j++, gIx++) {
      if (gIx >= gaps.length - 1) {
        gIx = gaps.length - 2;
      }
      colSpecs[j] = parseDimConstraint(parts.get((j << 1) + 1), gaps[gIx], gaps[gIx + 1], isCols);
    } 
    
    AC ac = new AC();
    ac.setConstaints(colSpecs);


    
    return ac;
  }










  
  private static DimConstraint parseDimConstraint(String s, BoundSize gapBefore, BoundSize gapAfter, boolean isCols) {
    DimConstraint dimConstraint = new DimConstraint();

    
    dimConstraint.setGapBefore(gapBefore);
    dimConstraint.setGapAfter(gapAfter);
    
    String[] parts = toTrimmedTokens(s, ',');
    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      try {
        if (part.length() == 0) {
          continue;
        }
        if (part.equals("fill")) {
          dimConstraint.setFill(true);
          
          continue;
        } 
        
        if (part.equals("nogrid")) {
          dimConstraint.setNoGrid(true);
          
          continue;
        } 
        int ix = -1;
        char c = part.charAt(0);
        
        if (c == 's') {
          ix = startsWithLenient(part, new String[] { "sizegroup", "sg" }, new int[] { 5, 2 }, true);
          if (ix > -1) {
            dimConstraint.setSizeGroup(part.substring(ix).trim());
            
            continue;
          } 
          
          ix = startsWithLenient(part, new String[] { "shrinkprio", "shp" }, new int[] { 10, 3 }, true);
          if (ix > -1) {
            dimConstraint.setShrinkPriority(Integer.parseInt(part.substring(ix).trim()));
            
            continue;
          } 
          ix = startsWithLenient(part, "shrink", 6, true);
          if (ix > -1) {
            dimConstraint.setShrink(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
        } 
        if (c == 'g') {
          ix = startsWithLenient(part, new String[] { "growpriority", "gp" }, new int[] { 5, 2 }, true);
          if (ix > -1) {
            dimConstraint.setGrowPriority(Integer.parseInt(part.substring(ix).trim()));
            
            continue;
          } 
          ix = startsWithLenient(part, "grow", 4, true);
          if (ix > -1) {
            dimConstraint.setGrow(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
        } 
        if (c == 'a') {
          ix = startsWithLenient(part, "align", 2, true);
          if (ix > -1) {
            
            dimConstraint.setAlign(parseUnitValueOrAlign(part.substring(ix).trim(), isCols, null));
            
            continue;
          } 
        } 
        UnitValue align = parseAlignKeywords(part, isCols);
        if (align != null) {
          
          dimConstraint.setAlign(align);
        
        }
        else {
          
          dimConstraint.setSize(parseBoundSize(part, false, isCols));
        } 
      } catch (Exception ex) {
        throw new IllegalArgumentException("Illegal constraint: '" + part + "'\n" + ex.getMessage());
      }  continue;
    } 
    return dimConstraint;
  }





  
  public static Map<ComponentWrapper, CC> parseComponentConstraints(Map<ComponentWrapper, String> constrMap) {
    HashMap<ComponentWrapper, CC> flowConstrMap = new HashMap<>();
    
    for (Iterator<Map.Entry<ComponentWrapper, String>> it = constrMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<ComponentWrapper, String> entry = it.next();
      flowConstrMap.put(entry.getKey(), parseComponentConstraint(entry.getValue()));
    } 
    
    return flowConstrMap;
  }






  
  public static CC parseComponentConstraint(String s) {
    CC cc = new CC();
    
    if (s == null || s.isEmpty()) {
      return cc;
    }
    String[] parts = toTrimmedTokens(s, ',');
    
    for (String part : parts) {
      try {
        if (part.length() == 0) {
          continue;
        }
        int ix = -1;
        char c = part.charAt(0);
        
        if (c == 'n') {
          if (part.equals("north")) {
            cc.setDockSide(0);
            
            continue;
          } 
          if (part.equals("newline")) {
            cc.setNewline(true);
            
            continue;
          } 
          if (part.startsWith("newline ")) {
            String gapSz = part.substring(7).trim();
            cc.setNewlineGapSize(parseBoundSize(gapSz, true, true));
            
            continue;
          } 
        } 
        if (c == 'f' && (part.equals("flowy") || part.equals("flowx"))) {
          cc.setFlowX((part.charAt(4) == 'x') ? Boolean.TRUE : Boolean.FALSE);
          
          continue;
        } 
        if (c == 's') {
          ix = startsWithLenient(part, "skip", 4, true);
          if (ix > -1) {
            String num = part.substring(ix).trim();
            cc.setSkip((num.length() != 0) ? Integer.parseInt(num) : 1);
            
            continue;
          } 
          ix = startsWithLenient(part, "split", 5, true);
          if (ix > -1) {
            String split = part.substring(ix).trim();
            cc.setSplit((split.length() > 0) ? Integer.parseInt(split) : 2097051);
            
            continue;
          } 
          if (part.equals("south")) {
            cc.setDockSide(2);
            
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "spany", "sy" }, new int[] { 5, 2 }, true);
          if (ix > -1) {
            cc.setSpanY(parseSpan(part.substring(ix).trim()));
            
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "spanx", "sx" }, new int[] { 5, 2 }, true);
          if (ix > -1) {
            cc.setSpanX(parseSpan(part.substring(ix).trim()));
            
            continue;
          } 
          ix = startsWithLenient(part, "span", 4, true);
          if (ix > -1) {
            String[] spans = toTrimmedTokens(part.substring(ix).trim(), ' ');
            cc.setSpanX((spans[0].length() > 0) ? Integer.parseInt(spans[0]) : 2097051);
            cc.setSpanY((spans.length > 1) ? Integer.parseInt(spans[1]) : 1);
            
            continue;
          } 
          ix = startsWithLenient(part, "shrinkx", 7, true);
          if (ix > -1) {
            cc.getHorizontal().setShrink(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, "shrinky", 7, true);
          if (ix > -1) {
            cc.getVertical().setShrink(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, "shrink", 6, false);
          if (ix > -1) {
            String[] shrinks = toTrimmedTokens(part.substring(ix).trim(), ' ');
            cc.getHorizontal().setShrink(parseFloat(shrinks[0], ResizeConstraint.WEIGHT_100));
            if (shrinks.length > 1) {
              cc.getVertical().setShrink(parseFloat(shrinks[1], ResizeConstraint.WEIGHT_100));
            }
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "shrinkprio", "shp" }, new int[] { 10, 3 }, true);
          if (ix > -1) {
            String sp = part.substring(ix).trim();
            if (sp.startsWith("x") || sp.startsWith("y")) {
              (sp.startsWith("x") ? cc.getHorizontal() : cc.getVertical()).setShrinkPriority(Integer.parseInt(sp.substring(2)));
            } else {
              String[] shrinks = toTrimmedTokens(sp, ' ');
              cc.getHorizontal().setShrinkPriority(Integer.parseInt(shrinks[0]));
              if (shrinks.length > 1) {
                cc.getVertical().setShrinkPriority(Integer.parseInt(shrinks[1]));
              }
            } 
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "sizegroupx", "sizegroupy", "sgx", "sgy" }, new int[] { 9, 9, 2, 2 }, true);
          if (ix > -1) {
            String sg = part.substring(ix).trim();
            char lc = part.charAt(ix - 1);
            if (lc != 'y')
              cc.getHorizontal().setSizeGroup(sg); 
            if (lc != 'x') {
              cc.getVertical().setSizeGroup(sg);
            }
            continue;
          } 
        } 
        if (c == 'g') {
          ix = startsWithLenient(part, "growx", 5, true);
          if (ix > -1) {
            cc.getHorizontal().setGrow(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, "growy", 5, true);
          if (ix > -1) {
            cc.getVertical().setGrow(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, "grow", 4, false);
          if (ix > -1) {
            String[] grows = toTrimmedTokens(part.substring(ix).trim(), ' ');
            cc.getHorizontal().setGrow(parseFloat(grows[0], ResizeConstraint.WEIGHT_100));
            cc.getVertical().setGrow(parseFloat((grows.length > 1) ? grows[1] : "", ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "growprio", "gp" }, new int[] { 8, 2 }, true);
          if (ix > -1) {
            String gp = part.substring(ix).trim();
            char c0 = (gp.length() > 0) ? gp.charAt(0) : ' ';
            if (c0 == 'x' || c0 == 'y') {
              ((c0 == 'x') ? cc.getHorizontal() : cc.getVertical()).setGrowPriority(Integer.parseInt(gp.substring(2)));
            } else {
              String[] grows = toTrimmedTokens(gp, ' ');
              cc.getHorizontal().setGrowPriority(Integer.parseInt(grows[0]));
              if (grows.length > 1) {
                cc.getVertical().setGrowPriority(Integer.parseInt(grows[1]));
              }
            } 
            continue;
          } 
          if (part.startsWith("gap")) {
            BoundSize[] gaps = parseGaps(part);
            if (gaps[0] != null)
              cc.getVertical().setGapBefore(gaps[0]); 
            if (gaps[1] != null)
              cc.getHorizontal().setGapBefore(gaps[1]); 
            if (gaps[2] != null)
              cc.getVertical().setGapAfter(gaps[2]); 
            if (gaps[3] != null) {
              cc.getHorizontal().setGapAfter(gaps[3]);
            }
            continue;
          } 
        } 
        if (c == 'a') {
          ix = startsWithLenient(part, new String[] { "aligny", "ay" }, new int[] { 6, 2 }, true);
          if (ix > -1) {
            cc.getVertical().setAlign(parseUnitValueOrAlign(part.substring(ix).trim(), false, null));
            
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "alignx", "ax" }, new int[] { 6, 2 }, true);
          if (ix > -1) {
            cc.getHorizontal().setAlign(parseUnitValueOrAlign(part.substring(ix).trim(), true, null));
            
            continue;
          } 
          ix = startsWithLenient(part, "align", 2, true);
          if (ix > -1) {
            String[] gaps = toTrimmedTokens(part.substring(ix).trim(), ' ');
            cc.getHorizontal().setAlign(parseUnitValueOrAlign(gaps[0], true, null));
            if (gaps.length > 1) {
              cc.getVertical().setAlign(parseUnitValueOrAlign(gaps[1], false, null));
            }
            continue;
          } 
        } 
        if ((c == 'x' || c == 'y') && part.length() > 2) {
          char c2 = part.charAt(1);
          if (c2 == ' ' || (c2 == '2' && part.charAt(2) == ' ')) {
            if (cc.getPos() == null) {
              cc.setPos(new UnitValue[4]);
            } else if (!cc.isBoundsInGrid()) {
              throw new IllegalArgumentException("Cannot combine 'position' with 'x/y/x2/y2' keywords.");
            } 
            
            int edge = ((c == 'x') ? 0 : 1) + ((c2 == '2') ? 2 : 0);
            UnitValue[] pos = cc.getPos();
            pos[edge] = parseUnitValue(part.substring(2).trim(), null, (c == 'x'));
            cc.setPos(pos);
            cc.setBoundsInGrid(true);
            
            continue;
          } 
        } 
        if (c == 'c') {
          ix = startsWithLenient(part, "cell", 4, true);
          if (ix > -1) {
            String[] grs = toTrimmedTokens(part.substring(ix).trim(), ' ');
            if (grs.length < 2)
              throw new IllegalArgumentException("At least two integers must follow " + part); 
            cc.setCellX(Integer.parseInt(grs[0]));
            cc.setCellY(Integer.parseInt(grs[1]));
            if (grs.length > 2)
              cc.setSpanX(Integer.parseInt(grs[2])); 
            if (grs.length > 3) {
              cc.setSpanY(Integer.parseInt(grs[3]));
            }
            continue;
          } 
        } 
        if (c == 'p') {
          ix = startsWithLenient(part, "pos", 3, true);
          if (ix > -1) {
            if (cc.getPos() != null && cc.isBoundsInGrid()) {
              throw new IllegalArgumentException("Can not combine 'pos' with 'x/y/x2/y2' keywords.");
            }
            String[] pos = toTrimmedTokens(part.substring(ix).trim(), ' ');
            UnitValue[] bounds = new UnitValue[4];
            for (int j = 0; j < pos.length; j++) {
              bounds[j] = parseUnitValue(pos[j], null, (j % 2 == 0));
            }
            if ((bounds[0] == null && bounds[2] == null) || (bounds[1] == null && bounds[3] == null)) {
              throw new IllegalArgumentException("Both x and x2 or y and y2 can not be null!");
            }
            cc.setPos(bounds);
            cc.setBoundsInGrid(false);
            
            continue;
          } 
          ix = startsWithLenient(part, "pad", 3, true);
          if (ix > -1) {
            UnitValue[] p = parseInsets(part.substring(ix).trim(), false);
            cc.setPadding(new UnitValue[] { p[0], (p.length > 1) ? p[1] : null, (p.length > 2) ? p[2] : null, (p.length > 3) ? p[3] : null });


            
            continue;
          } 

          
          ix = startsWithLenient(part, "pushx", 5, true);
          if (ix > -1) {
            cc.setPushX(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, "pushy", 5, true);
          if (ix > -1) {
            cc.setPushY(parseFloat(part.substring(ix).trim(), ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
          ix = startsWithLenient(part, "push", 4, false);
          if (ix > -1) {
            String[] pushs = toTrimmedTokens(part.substring(ix).trim(), ' ');
            cc.setPushX(parseFloat(pushs[0], ResizeConstraint.WEIGHT_100));
            cc.setPushY(parseFloat((pushs.length > 1) ? pushs[1] : "", ResizeConstraint.WEIGHT_100));
            
            continue;
          } 
        } 
        if (c == 't') {
          ix = startsWithLenient(part, "tag", 3, true);
          if (ix > -1) {
            cc.setTag(part.substring(ix).trim());
            
            continue;
          } 
        } 
        if (c == 'w' || c == 'h') {
          if (part.equals("wrap")) {
            cc.setWrap(true);
            
            continue;
          } 
          if (part.startsWith("wrap ")) {
            String gapSz = part.substring(5).trim();
            cc.setWrapGapSize(parseBoundSize(gapSz, true, true));
            
            continue;
          } 
          boolean isHor = (c == 'w');
          if (isHor && (part.startsWith("w ") || part.startsWith("width "))) {
            String uvStr = part.substring((part.charAt(1) == ' ') ? 2 : 6).trim();
            cc.getHorizontal().setSize(parseBoundSize(uvStr, false, true));
            
            continue;
          } 
          if (!isHor && (part.startsWith("h ") || part.startsWith("height "))) {
            String uvStr = part.substring((part.charAt(1) == ' ') ? 2 : 7).trim();
            cc.getVertical().setSize(parseBoundSize(uvStr, false, false));
            
            continue;
          } 
          if (part.startsWith("wmin ") || part.startsWith("wmax ") || part.startsWith("hmin ") || part.startsWith("hmax ")) {
            String uvStr = part.substring(5).trim();
            if (uvStr.length() > 0) {
              UnitValue uv = parseUnitValue(uvStr, null, isHor);
              boolean isMin = (part.charAt(3) == 'n');
              DimConstraint dc = isHor ? cc.getHorizontal() : cc.getVertical();
              dc.setSize(new BoundSize(isMin ? uv : dc
                    .getSize().getMin(), dc
                    .getSize().getPreferred(), isMin ? dc
                    .getSize().getMax() : uv, uvStr));

              
              continue;
            } 
          } 
          
          if (part.equals("west")) {
            cc.setDockSide(1);
            
            continue;
          } 
          if (part.startsWith("hidemode ")) {
            cc.setHideMode(Integer.parseInt(part.substring(9)));
            
            continue;
          } 
        } 
        if (c == 'i' && part.startsWith("id ")) {
          cc.setId(part.substring(3).trim());
          int dIx = cc.getId().indexOf('.');
          if (dIx == 0 || dIx == cc.getId().length() - 1) {
            throw new IllegalArgumentException("Dot must not be first or last!");
          }
          
          continue;
        } 
        if (c == 'e') {
          if (part.equals("east")) {
            cc.setDockSide(3);
            
            continue;
          } 
          if (part.equals("external")) {
            cc.setExternal(true);
            
            continue;
          } 
          ix = startsWithLenient(part, new String[] { "endgroupx", "endgroupy", "egx", "egy" }, new int[] { -1, -1, -1, -1 }, true);
          if (ix > -1) {
            String sg = part.substring(ix).trim();
            char lc = part.charAt(ix - 1);
            DimConstraint dc = (lc == 'x') ? cc.getHorizontal() : cc.getVertical();
            dc.setEndGroup(sg);
            
            continue;
          } 
        } 
        if (c == 'd') {
          if (part.equals("dock north")) {
            cc.setDockSide(0);
            continue;
          } 
          if (part.equals("dock west")) {
            cc.setDockSide(1);
            continue;
          } 
          if (part.equals("dock south")) {
            cc.setDockSide(2);
            continue;
          } 
          if (part.equals("dock east")) {
            cc.setDockSide(3);
            
            continue;
          } 
          if (part.equals("dock center")) {
            cc.getHorizontal().setGrow(Float.valueOf(100.0F));
            cc.getVertical().setGrow(Float.valueOf(100.0F));
            cc.setPushX(Float.valueOf(100.0F));
            cc.setPushY(Float.valueOf(100.0F));
            
            continue;
          } 
        } 
        if (c == 'v') {
          ix = startsWithLenient(part, new String[] { "visualpadding", "vp" }, new int[] { 3, 2 }, true);
          if (ix > -1) {
            UnitValue[] p = parseInsets(part.substring(ix).trim(), false);
            cc.setVisualPadding(new UnitValue[] { p[0], (p.length > 1) ? p[1] : null, (p.length > 2) ? p[2] : null, (p.length > 3) ? p[3] : null });


            
            continue;
          } 
        } 

        
        UnitValue horAlign = parseAlignKeywords(part, true);
        if (horAlign != null) {
          cc.getHorizontal().setAlign(horAlign);
        }
        else {
          
          UnitValue verAlign = parseAlignKeywords(part, false);
          if (verAlign != null)
          { cc.getVertical().setAlign(verAlign); }
          
          else
          
          { throw new IllegalArgumentException("Unknown keyword."); } 
        } 
      } catch (Exception ex) {
        throw new IllegalArgumentException("Error parsing Constraint: '" + part + "'", ex);
      } 
      
      continue;
    } 
    
    return cc;
  }







  
  public static UnitValue[] parseInsets(String s, boolean acceptPanel) {
    if (s.length() == 0 || s.equals("dialog") || s.equals("panel")) {
      if (!acceptPanel) {
        throw new IllegalArgumentException("Insets now allowed: " + s + "\n");
      }
      boolean isPanel = s.startsWith("p");
      UnitValue[] arrayOfUnitValue = new UnitValue[4];
      for (int i = 0; i < 4; i++) {
        arrayOfUnitValue[i] = isPanel ? PlatformDefaults.getPanelInsets(i) : PlatformDefaults.getDialogInsets(i);
      }
      return arrayOfUnitValue;
    } 
    String[] insS = toTrimmedTokens(s, ' ');
    UnitValue[] ins = new UnitValue[4];
    for (int j = 0; j < 4; j++) {
      UnitValue insSz = parseUnitValue(insS[(j < insS.length) ? j : (insS.length - 1)], UnitValue.ZERO, (j % 2 == 1));
      ins[j] = (insSz != null) ? insSz : PlatformDefaults.getPanelInsets(j);
    } 
    return ins;
  }







  
  private static BoundSize[] parseGaps(String s) {
    BoundSize[] ret = new BoundSize[4];
    
    int ix = startsWithLenient(s, "gaptop", -1, true);
    if (ix > -1) {
      s = s.substring(ix).trim();
      ret[0] = parseBoundSize(s, true, false);
      return ret;
    } 
    
    ix = startsWithLenient(s, "gapleft", -1, true);
    if (ix > -1) {
      s = s.substring(ix).trim();
      ret[1] = parseBoundSize(s, true, true);
      return ret;
    } 
    
    ix = startsWithLenient(s, "gapbottom", -1, true);
    if (ix > -1) {
      s = s.substring(ix).trim();
      ret[2] = parseBoundSize(s, true, false);
      return ret;
    } 
    
    ix = startsWithLenient(s, "gapright", -1, true);
    if (ix > -1) {
      s = s.substring(ix).trim();
      ret[3] = parseBoundSize(s, true, true);
      return ret;
    } 
    
    ix = startsWithLenient(s, "gapbefore", -1, true);
    if (ix > -1) {
      s = s.substring(ix).trim();
      ret[1] = parseBoundSize(s, true, true);
      return ret;
    } 
    
    ix = startsWithLenient(s, "gapafter", -1, true);
    if (ix > -1) {
      s = s.substring(ix).trim();
      ret[3] = parseBoundSize(s, true, true);
      return ret;
    } 
    
    ix = startsWithLenient(s, new String[] { "gapx", "gapy" }, (int[])null, true);
    if (ix > -1) {
      boolean x = (s.charAt(3) == 'x');
      String[] gaps = toTrimmedTokens(s.substring(ix).trim(), ' ');
      ret[x ? 1 : 0] = parseBoundSize(gaps[0], true, x);
      if (gaps.length > 1)
        ret[x ? 3 : 2] = parseBoundSize(gaps[1], true, !x); 
      return ret;
    } 
    
    ix = startsWithLenient(s, "gap ", 1, true);
    if (ix > -1) {
      String[] gaps = toTrimmedTokens(s.substring(ix).trim(), ' ');
      
      ret[1] = parseBoundSize(gaps[0], true, true);
      if (gaps.length > 1) {
        ret[3] = parseBoundSize(gaps[1], true, false);
        if (gaps.length > 2) {
          ret[0] = parseBoundSize(gaps[2], true, true);
          if (gaps.length > 3)
            ret[2] = parseBoundSize(gaps[3], true, false); 
        } 
      } 
      return ret;
    } 
    
    throw new IllegalArgumentException("Unknown Gap part: '" + s + "'");
  }

  
  private static int parseSpan(String s) {
    return (s.length() > 0) ? Integer.parseInt(s) : 2097051;
  }

  
  private static Float parseFloat(String s, Float nullVal) {
    return (s.length() > 0) ? new Float(Float.parseFloat(s)) : nullVal;
  }







  
  public static BoundSize parseBoundSize(String s, boolean isGap, boolean isHor) {
    if (s.length() == 0 || s.equals("null") || s.equals("n")) {
      return null;
    }
    String cs = s;
    boolean push = false;
    if (s.endsWith("push")) {
      push = true;
      int l = s.length();
      s = s.substring(0, l - (s.endsWith(":push") ? 5 : 4));
      if (s.length() == 0) {
        return new BoundSize(null, null, null, true, cs);
      }
    } 
    String[] sizes = toTrimmedTokens(s, ':');
    String s0 = sizes[0];
    
    if (sizes.length == 1) {
      boolean hasEM = s0.endsWith("!");
      if (hasEM)
        s0 = s0.substring(0, s0.length() - 1); 
      UnitValue uv = parseUnitValue(s0, null, isHor);
      return new BoundSize((isGap || hasEM) ? uv : null, uv, hasEM ? uv : null, push, cs);
    } 
    if (sizes.length == 2)
      return new BoundSize(parseUnitValue(s0, null, isHor), parseUnitValue(sizes[1], null, isHor), null, push, cs); 
    if (sizes.length == 3) {
      return new BoundSize(parseUnitValue(s0, null, isHor), parseUnitValue(sizes[1], null, isHor), parseUnitValue(sizes[2], null, isHor), push, cs);
    }
    throw new IllegalArgumentException("Min:Preferred:Max size section must contain 0, 1 or 2 colons. '" + cs + "'");
  }








  
  public static UnitValue parseUnitValueOrAlign(String s, boolean isHor, UnitValue emptyReplacement) {
    if (s.length() == 0) {
      return emptyReplacement;
    }
    UnitValue align = parseAlignKeywords(s, isHor);
    if (align != null) {
      return align;
    }
    return parseUnitValue(s, emptyReplacement, isHor);
  }






  
  public static UnitValue parseUnitValue(String s, boolean isHor) {
    return parseUnitValue(s, null, isHor);
  }







  
  private static UnitValue parseUnitValue(String s, UnitValue emptyReplacement, boolean isHor) {
    if (s == null || s.length() == 0) {
      return emptyReplacement;
    }
    String cs = s;
    char c0 = s.charAt(0);

    
    if (c0 == '(' && s.charAt(s.length() - 1) == ')') {
      s = s.substring(1, s.length() - 1);
    }
    if (c0 == 'n' && (s.equals("null") || s.equals("n"))) {
      return null;
    }
    if (c0 == 'i' && s.equals("inf")) {
      return UnitValue.INF;
    }
    int oper = getOper(s);
    boolean inline = (oper == 101 || oper == 102 || oper == 103 || oper == 104);
    
    if (oper != 100) {
      String[] uvs;
      
      if (!inline) {
        String sub = s.substring(4, s.length() - 1).trim();
        uvs = toTrimmedTokens(sub, ',');
        if (uvs.length == 1)
          return parseUnitValue(sub, null, isHor); 
      } else {
        char delim;
        if (oper == 101) {
          delim = '+';
        } else if (oper == 102) {
          delim = '-';
        } else if (oper == 103) {
          delim = '*';
        } else {
          delim = '/';
        } 
        uvs = toTrimmedTokens(s, delim);
        if (uvs.length > 2) {
          String last = uvs[uvs.length - 1];
          String first = s.substring(0, s.length() - last.length() - 1);
          uvs = new String[] { first, last };
        } 
      } 
      
      if (uvs.length != 2) {
        throw new IllegalArgumentException("Malformed UnitValue: '" + s + "'");
      }
      UnitValue sub1 = parseUnitValue(uvs[0], null, isHor);
      UnitValue sub2 = parseUnitValue(uvs[1], null, isHor);
      
      if (sub1 == null || sub2 == null) {
        throw new IllegalArgumentException("Malformed UnitValue. Must be two sub-values: '" + s + "'");
      }
      return new UnitValue(isHor, oper, sub1, sub2, cs);
    } 
    try {
      String[] numParts = getNumTextParts(s);
      float value = (numParts[0].length() > 0) ? Float.parseFloat(numParts[0]) : 1.0F;
      
      return new UnitValue(value, numParts[1], isHor, oper, cs);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Malformed UnitValue: '" + s + "'", e);
    } 
  }







  
  static UnitValue parseAlignKeywords(String s, boolean isHor) {
    if (startsWithLenient(s, "center", 1, false) != -1) {
      return UnitValue.CENTER;
    }
    if (isHor) {
      if (startsWithLenient(s, "left", 1, false) != -1) {
        return UnitValue.LEFT;
      }
      if (startsWithLenient(s, "right", 1, false) != -1) {
        return UnitValue.RIGHT;
      }
      if (startsWithLenient(s, "leading", 4, false) != -1) {
        return UnitValue.LEADING;
      }
      if (startsWithLenient(s, "trailing", 5, false) != -1) {
        return UnitValue.TRAILING;
      }
      if (startsWithLenient(s, "label", 5, false) != -1) {
        return UnitValue.LABEL;
      }
    } else {
      
      if (startsWithLenient(s, "baseline", 4, false) != -1) {
        return UnitValue.BASELINE_IDENTITY;
      }
      if (startsWithLenient(s, "top", 1, false) != -1) {
        return UnitValue.TOP;
      }
      if (startsWithLenient(s, "bottom", 1, false) != -1) {
        return UnitValue.BOTTOM;
      }
    } 
    return null;
  }






  
  private static String[] getNumTextParts(String s) {
    for (int i = 0, iSz = s.length(); i < iSz; i++) {
      char c = s.charAt(i);
      if (c == ' ') {
        throw new IllegalArgumentException("Space in UnitValue: '" + s + "'");
      }
      if ((c < '0' || c > '9') && c != '.' && c != '-')
        return new String[] { s.substring(0, i).trim(), s.substring(i).trim() }; 
    } 
    return new String[] { s, "" };
  }





  
  private static int getOper(String s) {
    int len = s.length();
    if (len < 3) {
      return 100;
    }
    if (len > 5 && s.charAt(3) == '(' && s.charAt(len - 1) == ')') {
      if (s.startsWith("min(")) {
        return 105;
      }
      if (s.startsWith("max(")) {
        return 106;
      }
      if (s.startsWith("mid(")) {
        return 107;
      }
    } 
    
    for (int j = 0; j < 2; j++) {
      for (int i = len - 1, p = 0; i > 0; i--) {
        char c = s.charAt(i);
        if (c == ')') {
          p++;
        } else if (c == '(') {
          p--;
        } else if (p == 0) {
          if (j == 0) {
            if (c == '+')
              return 101; 
            if (c == '-')
              return 102; 
          } else {
            if (c == '*')
              return 103; 
            if (c == '/')
              return 104; 
          } 
        } 
      } 
    } 
    return 100;
  }















  
  private static int startsWithLenient(String s, String[] matches, int[] minChars, boolean acceptTrailing) {
    for (int i = 0; i < matches.length; i++) {
      int minChar = (minChars != null) ? minChars[i] : -1;
      int ix = startsWithLenient(s, matches[i], minChar, acceptTrailing);
      if (ix > -1)
        return ix; 
    } 
    return -1;
  }












  
  private static int startsWithLenient(String s, String match, int minChars, boolean acceptTrailing) {
    if (s.charAt(0) != match.charAt(0)) {
      return -1;
    }
    if (minChars == -1) {
      minChars = match.length();
    }
    int sSz = s.length();
    if (sSz < minChars) {
      return -1;
    }
    int mSz = match.length();
    int sIx = 0;
    for (int mIx = 0; mIx < mSz; sIx++, mIx++) {
      while (sIx < sSz && (s.charAt(sIx) == ' ' || s.charAt(sIx) == '_')) {
        sIx++;
      }
      if (sIx >= sSz || s.charAt(sIx) != match.charAt(mIx))
        return (mIx >= minChars && (acceptTrailing || sIx >= sSz) && (sIx >= sSz || s.charAt(sIx - 1) == ' ')) ? sIx : -1; 
    } 
    return (sIx >= sSz || acceptTrailing || s.charAt(sIx) == ' ') ? sIx : -1;
  }












  
  private static String[] toTrimmedTokens(String s, char sep) {
    int toks = 0, sSize = s.length();
    boolean disregardDoubles = (sep == ' ');

    
    int p = 0;
    for (int i = 0; i < sSize; i++) {
      char c = s.charAt(i);
      if (c == '(') {
        p++;
      } else if (c == ')') {
        p--;
      } else if (p == 0 && c == sep) {
        toks++;
        while (disregardDoubles && i < sSize - 1 && s.charAt(i + 1) == ' ')
          i++; 
      } 
      if (p < 0)
        throw new IllegalArgumentException("Unbalanced parentheses: '" + s + "'"); 
    } 
    if (p != 0) {
      throw new IllegalArgumentException("Unbalanced parentheses: '" + s + "'");
    }
    if (toks == 0) {
      return new String[] { s.trim() };
    }
    String[] retArr = new String[toks + 1];
    
    int st = 0, pNr = 0;
    p = 0;
    for (int j = 0; j < sSize; j++) {
      
      char c = s.charAt(j);
      if (c == '(') {
        p++;
      } else if (c == ')') {
        p--;
      } else if (p == 0 && c == sep) {
        retArr[pNr++] = s.substring(st, j).trim();
        st = j + 1;
        while (disregardDoubles && j < sSize - 1 && s.charAt(j + 1) == ' ') {
          j++;
        }
      } 
    } 
    retArr[pNr++] = s.substring(st, sSize).trim();
    return retArr;
  }









  
  private static ArrayList<String> getRowColAndGapsTrimmed(String s) {
    if (s.indexOf('|') != -1) {
      s = s.replaceAll("\\|", "][");
    }
    ArrayList<String> retList = new ArrayList<>(Math.max(s.length() >> 3, 3));
    int s0 = 0, s1 = 0;
    int st = 0;
    for (int i = 0, iSz = s.length(); i < iSz; i++) {
      char c = s.charAt(i);
      if (c == '[') {
        s0++;
      } else if (c == ']') {
        s1++;
      } else {
        continue;
      } 
      
      if (s0 != s1 && s0 - 1 != s1) {
        break;
      }
      retList.add(s.substring(st, i).trim());
      st = i + 1; continue;
    } 
    if (s0 != s1) {
      throw new IllegalArgumentException("'[' and ']' mismatch in row/column format string: " + s);
    }
    if (s0 == 0) {
      retList.add("");
      retList.add(s);
      retList.add("");
    } else if (retList.size() % 2 == 0) {
      retList.add(s.substring(st, s.length()));
    } 
    
    return retList;
  }





  
  public static String prepare(String s) {
    return (s != null) ? s.trim().toLowerCase() : "";
  }
}
