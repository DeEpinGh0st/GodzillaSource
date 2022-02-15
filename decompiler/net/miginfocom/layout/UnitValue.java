package net.miginfocom.layout;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;





























public final class UnitValue
  implements Serializable
{
  private static final HashMap<String, Integer> UNIT_MAP = new HashMap<>(32);
  
  private static final ArrayList<UnitConverter> CONVERTERS = new ArrayList<>();


  
  public static final int STATIC = 100;


  
  public static final int ADD = 101;


  
  public static final int SUB = 102;


  
  public static final int MUL = 103;


  
  public static final int DIV = 104;


  
  public static final int MIN = 105;


  
  public static final int MAX = 106;


  
  public static final int MID = 107;


  
  public static final int PIXEL = 0;


  
  public static final int LPX = 1;


  
  public static final int LPY = 2;


  
  public static final int MM = 3;


  
  public static final int CM = 4;


  
  public static final int INCH = 5;


  
  public static final int PERCENT = 6;


  
  public static final int PT = 7;


  
  public static final int SPX = 8;


  
  public static final int SPY = 9;


  
  public static final int ALIGN = 12;


  
  public static final int MIN_SIZE = 13;


  
  public static final int PREF_SIZE = 14;


  
  public static final int MAX_SIZE = 15;


  
  public static final int BUTTON = 16;


  
  public static final int LINK_X = 18;


  
  public static final int LINK_Y = 19;


  
  public static final int LINK_W = 20;


  
  public static final int LINK_H = 21;


  
  public static final int LINK_X2 = 22;


  
  public static final int LINK_Y2 = 23;


  
  public static final int LINK_XPOS = 24;


  
  public static final int LINK_YPOS = 25;


  
  public static final int LOOKUP = 26;


  
  public static final int LABEL_ALIGN = 27;

  
  private static final int IDENTITY = -1;


  
  static {
    UNIT_MAP.put("px", Integer.valueOf(0));
    UNIT_MAP.put("lpx", Integer.valueOf(1));
    UNIT_MAP.put("lpy", Integer.valueOf(2));
    UNIT_MAP.put("%", Integer.valueOf(6));
    UNIT_MAP.put("cm", Integer.valueOf(4));
    UNIT_MAP.put("in", Integer.valueOf(5));
    UNIT_MAP.put("spx", Integer.valueOf(8));
    UNIT_MAP.put("spy", Integer.valueOf(9));
    UNIT_MAP.put("al", Integer.valueOf(12));
    UNIT_MAP.put("mm", Integer.valueOf(3));
    UNIT_MAP.put("pt", Integer.valueOf(7));
    UNIT_MAP.put("min", Integer.valueOf(13));
    UNIT_MAP.put("minimum", Integer.valueOf(13));
    UNIT_MAP.put("p", Integer.valueOf(14));
    UNIT_MAP.put("pref", Integer.valueOf(14));
    UNIT_MAP.put("max", Integer.valueOf(15));
    UNIT_MAP.put("maximum", Integer.valueOf(15));
    UNIT_MAP.put("button", Integer.valueOf(16));
    UNIT_MAP.put("label", Integer.valueOf(27));
  }
  
  static final UnitValue ZERO = new UnitValue(0.0F, null, 0, true, 100, null, null, "0px");
  static final UnitValue TOP = new UnitValue(0.0F, null, 6, false, 100, null, null, "top");
  static final UnitValue LEADING = new UnitValue(0.0F, null, 6, true, 100, null, null, "leading");
  static final UnitValue LEFT = new UnitValue(0.0F, null, 6, true, 100, null, null, "left");
  static final UnitValue CENTER = new UnitValue(50.0F, null, 6, true, 100, null, null, "center");
  static final UnitValue TRAILING = new UnitValue(100.0F, null, 6, true, 100, null, null, "trailing");
  static final UnitValue RIGHT = new UnitValue(100.0F, null, 6, true, 100, null, null, "right");
  static final UnitValue BOTTOM = new UnitValue(100.0F, null, 6, false, 100, null, null, "bottom");
  static final UnitValue LABEL = new UnitValue(0.0F, null, 27, false, 100, null, null, "label");
  
  static final UnitValue INF = new UnitValue(2097051.0F, null, 0, true, 100, null, null, "inf");
  
  static final UnitValue BASELINE_IDENTITY = new UnitValue(0.0F, null, -1, false, 100, null, null, "baseline");
  
  private final transient float value;
  private final transient int unit;
  private final transient int oper;
  private final transient String unitStr;
  private transient String linkId = null;
  
  private final transient boolean isHor;
  
  private final transient UnitValue[] subUnits;
  
  public UnitValue(float value) {
    this(value, null, 0, true, 100, null, null, value + "px");
  }

  
  public UnitValue(float value, int unit, String createString) {
    this(value, null, unit, true, 100, null, null, createString);
  }

  
  public UnitValue(float value, String unitStr, boolean isHor, int oper, String createString) {
    this(value, unitStr, -1, isHor, oper, null, null, createString);
  }

  
  UnitValue(boolean isHor, int oper, UnitValue sub1, UnitValue sub2, String createString) {
    this(0.0F, "", -1, isHor, oper, sub1, sub2, createString);
    if (sub1 == null || sub2 == null) {
      throw new IllegalArgumentException("Sub units is null!");
    }
  }
  
  private UnitValue(float value, String unitStr, int unit, boolean isHor, int oper, UnitValue sub1, UnitValue sub2, String createString) {
    if (oper < 100 || oper > 107) {
      throw new IllegalArgumentException("Unknown Operation: " + oper);
    }
    if (oper >= 101 && oper <= 107 && (sub1 == null || sub2 == null)) {
      throw new IllegalArgumentException(oper + " Operation may not have null sub-UnitValues.");
    }
    this.value = value;
    this.oper = oper;
    this.isHor = isHor;
    this.unitStr = unitStr;
    this.unit = (unitStr != null) ? parseUnitString() : unit;
    (new UnitValue[2])[0] = sub1; (new UnitValue[2])[1] = sub2; this.subUnits = (sub1 != null && sub2 != null) ? new UnitValue[2] : null;
    
    LayoutUtil.putCCString(this, createString);
  }









  
  public final int getPixels(float refValue, ContainerWrapper parent, ComponentWrapper comp) {
    return Math.round(getPixelsExact(refValue, parent, comp));
  }
  
  private static final float[] SCALE = new float[] { 25.4F, 2.54F, 1.0F, 0.0F, 72.0F };



  
  private static final long serialVersionUID = 1L;



  
  public final float getPixelsExact(float refValue, ContainerWrapper parent, ComponentWrapper comp) {
    if (parent == null) {
      return 1.0F;
    }
    if (this.oper == 100) {
      float f; Float s; Integer st; Integer sz; Integer v; float res; switch (this.unit) {
        case 0:
          return this.value;
        
        case 1:
        case 2:
          return parent.getPixelUnitFactor((this.unit == 1)) * this.value;
        
        case 3:
        case 4:
        case 5:
        case 7:
          f = SCALE[this.unit - 3];
          s = this.isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
          if (s != null) {
            f *= s.floatValue();
          }
          return (this.isHor ? parent.getHorizontalScreenDPI() : parent.getVerticalScreenDPI()) * this.value / f;
        
        case 6:
          return this.value * refValue * 0.01F;
        
        case 8:
        case 9:
          return ((this.unit == 8) ? parent.getScreenWidth() : parent.getScreenHeight()) * this.value * 0.01F;
        
        case 12:
          st = LinkHandler.getValue(parent.getLayout(), "visual", this.isHor ? 0 : 1);
          sz = LinkHandler.getValue(parent.getLayout(), "visual", this.isHor ? 2 : 3);
          if (st == null || sz == null)
            return 0.0F; 
          return this.value * (Math.max(0, sz.intValue()) - refValue) + st.intValue();
        
        case 13:
          if (comp == null)
            return 0.0F; 
          return this.isHor ? comp.getMinimumWidth(comp.getHeight()) : comp.getMinimumHeight(comp.getWidth());
        
        case 14:
          if (comp == null)
            return 0.0F; 
          return this.isHor ? comp.getPreferredWidth(comp.getHeight()) : comp.getPreferredHeight(comp.getWidth());
        
        case 15:
          if (comp == null)
            return 0.0F; 
          return this.isHor ? comp.getMaximumWidth(comp.getHeight()) : comp.getMaximumHeight(comp.getWidth());
        
        case 16:
          return PlatformDefaults.getMinimumButtonWidthIncludingPadding(refValue, parent, comp);
        
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
          v = LinkHandler.getValue(parent.getLayout(), getLinkTargetId(), this.unit - ((this.unit >= 24) ? 24 : 18));
          if (v == null) {
            return 0.0F;
          }
          if (this.unit == 24)
            return (parent.getScreenLocationX() + v.intValue()); 
          if (this.unit == 25) {
            return (parent.getScreenLocationY() + v.intValue());
          }
          return v.intValue();
        
        case 26:
          res = lookup(refValue, parent, comp);
          if (res != -8.7654312E7F) {
            return res;
          }
        case 27:
          return PlatformDefaults.getLabelAlignPercentage() * refValue;
      } 

      
      throw new IllegalArgumentException("Unknown/illegal unit: " + this.unit + ", unitStr: " + this.unitStr);
    } 
    
    if (this.subUnits != null && this.subUnits.length == 2) {
      float r1 = this.subUnits[0].getPixelsExact(refValue, parent, comp);
      float r2 = this.subUnits[1].getPixelsExact(refValue, parent, comp);
      switch (this.oper) {
        case 101:
          return r1 + r2;
        case 102:
          return r1 - r2;
        case 103:
          return r1 * r2;
        case 104:
          return r1 / r2;
        case 105:
          return (r1 < r2) ? r1 : r2;
        case 106:
          return (r1 > r2) ? r1 : r2;
        case 107:
          return (r1 + r2) * 0.5F;
      } 
    
    } 
    throw new IllegalArgumentException("Internal: Unknown Oper: " + this.oper);
  }

  
  private float lookup(float refValue, ContainerWrapper parent, ComponentWrapper comp) {
    float res = -8.7654312E7F;
    for (int i = CONVERTERS.size() - 1; i >= 0; i--) {
      res = ((UnitConverter)CONVERTERS.get(i)).convertToPixels(this.value, this.unitStr, this.isHor, refValue, parent, comp);
      if (res != -8.7654312E7F)
        return res; 
    } 
    return PlatformDefaults.convertToPixels(this.value, this.unitStr, this.isHor, refValue, parent, comp);
  }

  
  private int parseUnitString() {
    int len = this.unitStr.length();
    if (len == 0) {
      return this.isHor ? PlatformDefaults.getDefaultHorizontalUnit() : PlatformDefaults.getDefaultVerticalUnit();
    }
    Integer u = UNIT_MAP.get(this.unitStr);
    if (u != null) {
      if (!this.isHor && (u.intValue() == 16 || u.intValue() == 27)) {
        throw new IllegalArgumentException("Not valid in vertical contexts: '" + this.unitStr + "'");
      }
      return u.intValue();
    } 
    
    if (this.unitStr.equals("lp")) {
      return this.isHor ? 1 : 2;
    }
    if (this.unitStr.equals("sp")) {
      return this.isHor ? 8 : 9;
    }
    if (lookup(0.0F, null, null) != -8.7654312E7F) {
      return 26;
    }

    
    int pIx = this.unitStr.indexOf('.');
    if (pIx != -1) {
      this.linkId = this.unitStr.substring(0, pIx);
      String e = this.unitStr.substring(pIx + 1);
      
      if (e.equals("x"))
        return 18; 
      if (e.equals("y"))
        return 19; 
      if (e.equals("w") || e.equals("width"))
        return 20; 
      if (e.equals("h") || e.equals("height"))
        return 21; 
      if (e.equals("x2"))
        return 22; 
      if (e.equals("y2"))
        return 23; 
      if (e.equals("xpos"))
        return 24; 
      if (e.equals("ypos")) {
        return 25;
      }
    } 
    throw new IllegalArgumentException("Unknown keyword: " + this.unitStr);
  }

  
  final boolean isAbsolute() {
    switch (this.unit) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 7:
        return true;
      
      case 6:
      case 8:
      case 9:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
        return false;
    } 

    
    throw new IllegalArgumentException("Unknown/illegal unit: " + this.unit + ", unitStr: " + this.unitStr);
  }

  
  final boolean isAbsoluteDeep() {
    if (this.subUnits != null)
      for (UnitValue subUnit : this.subUnits) {
        if (subUnit.isAbsoluteDeep()) {
          return true;
        }
      }  
    return isAbsolute();
  }

  
  final boolean isLinked() {
    return (this.linkId != null);
  }

  
  final boolean isLinkedDeep() {
    if (this.subUnits != null)
      for (UnitValue subUnit : this.subUnits) {
        if (subUnit.isLinkedDeep()) {
          return true;
        }
      }  
    return isLinked();
  }

  
  final String getLinkTargetId() {
    return this.linkId;
  }

  
  final UnitValue getSubUnitValue(int i) {
    return this.subUnits[i];
  }

  
  final int getSubUnitCount() {
    return (this.subUnits != null) ? this.subUnits.length : 0;
  }

  
  public final UnitValue[] getSubUnits() {
    return (this.subUnits != null) ? (UnitValue[])this.subUnits.clone() : null;
  }

  
  public final int getUnit() {
    return this.unit;
  }

  
  public final String getUnitString() {
    return this.unitStr;
  }

  
  public final int getOperation() {
    return this.oper;
  }

  
  public final float getValue() {
    return this.value;
  }

  
  public final boolean isHorizontal() {
    return this.isHor;
  }


  
  public final String toString() {
    return getClass().getName() + ". Value=" + this.value + ", unit=" + this.unit + ", unitString: " + this.unitStr + ", oper=" + this.oper + ", isHor: " + this.isHor;
  }





  
  public final String getConstraintString() {
    return LayoutUtil.getCCString(this);
  }


  
  public final int hashCode() {
    return (int)(this.value * 12345.0F) + (this.oper >>> 5) + this.unit >>> 17;
  }







  
  public static synchronized void addGlobalUnitConverter(UnitConverter conv) {
    if (conv == null)
      throw new NullPointerException(); 
    CONVERTERS.add(conv);
  }





  
  public static synchronized boolean removeGlobalUnitConverter(UnitConverter unit) {
    return CONVERTERS.remove(unit);
  }




  
  public static synchronized UnitConverter[] getGlobalUnitConverters() {
    return CONVERTERS.<UnitConverter>toArray(new UnitConverter[CONVERTERS.size()]);
  }







  
  public static int getDefaultUnit() {
    return PlatformDefaults.getDefaultHorizontalUnit();
  }







  
  public static void setDefaultUnit(int unit) {
    PlatformDefaults.setDefaultHorizontalUnit(unit);
    PlatformDefaults.setDefaultVerticalUnit(unit);
  }
  
  static {
    if (LayoutUtil.HAS_BEANS) {
      LayoutUtil.setDelegate(UnitValue.class, new PersistenceDelegate()
          {
            protected Expression instantiate(Object oldInstance, Encoder out)
            {
              UnitValue uv = (UnitValue)oldInstance;
              String cs = uv.getConstraintString();
              if (cs == null) {
                throw new IllegalStateException("Design time must be on to use XML persistence. See LayoutUtil.");
              }
              return new Expression(oldInstance, ConstraintParser.class, "parseUnitValueOrAlign", new Object[] { uv
                    .getConstraintString(), uv.isHorizontal() ? Boolean.TRUE : Boolean.FALSE, null });
            }
          });
    }
  }








  
  private Object readResolve() throws ObjectStreamException {
    return LayoutUtil.getSerializedObject(this);
  }

  
  private void writeObject(ObjectOutputStream out) throws IOException {
    if (getClass() == UnitValue.class) {
      LayoutUtil.writeAsXML(out, this);
    }
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
  }
}
