package net.miginfocom.layout;

import java.beans.Beans;
import java.beans.ExceptionListener;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import java.util.TreeSet;
import java.util.WeakHashMap;




































public final class LayoutUtil
{
  public static final int INF = 2097051;
  static final int NOT_SET = -2147471302;
  public static final int MIN = 0;
  public static final int PREF = 1;
  public static final int MAX = 2;
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  private static volatile WeakHashMap<Object, String> CR_MAP = null;
  private static volatile WeakHashMap<Object, Boolean> DT_MAP = null;
  private static int eSz = 0;
  private static int globalDebugMillis = 0;
  public static final boolean HAS_BEANS = hasBeans();

  
  private static boolean hasBeans() {
    try {
      LayoutUtil.class.getClassLoader().loadClass("java.beans.Beans");
      return true;
    } catch (Throwable e) {
      return false;
    } 
  }








  
  public static String getVersion() {
    return "5.0";
  }






  
  public static int getGlobalDebugMillis() {
    return globalDebugMillis;
  }










  
  public static void setGlobalDebugMillis(int millis) {
    globalDebugMillis = millis;
  }











  
  public static void setDesignTime(ContainerWrapper cw, boolean b) {
    if (DT_MAP == null) {
      DT_MAP = new WeakHashMap<>();
    }
    DT_MAP.put((cw != null) ? cw.getComponent() : null, Boolean.valueOf(b));
  }







  
  public static boolean isDesignTime(ContainerWrapper cw) {
    if (DT_MAP == null) {
      return (HAS_BEANS && Beans.isDesignTime());
    }

    
    if (cw == null && DT_MAP != null && !DT_MAP.isEmpty()) {
      return true;
    }
    if (cw != null && !DT_MAP.containsKey(cw.getComponent())) {
      cw = null;
    }
    Boolean b = DT_MAP.get((cw != null) ? cw.getComponent() : null);
    return (b != null && b.booleanValue());
  }




  
  public static int getDesignTimeEmptySize() {
    return eSz;
  }






  
  public static void setDesignTimeEmptySize(int pixels) {
    eSz = pixels;
  }









  
  static void putCCString(Object con, String s) {
    if (s != null && con != null && isDesignTime(null)) {
      if (CR_MAP == null) {
        CR_MAP = new WeakHashMap<>(64);
      }
      CR_MAP.put(con, s);
    } 
  }





  
  static synchronized void setDelegate(Class<?> c, PersistenceDelegate del) {
    try {
      Introspector.getBeanInfo(c, 3).getBeanDescriptor().setValue("persistenceDelegate", del);
    } catch (Exception exception) {}
  }







  
  static String getCCString(Object con) {
    return (CR_MAP != null) ? CR_MAP.get(con) : null;
  }

  
  static void throwCC() {
    throw new IllegalStateException("setStoreConstraintData(true) must be set for strings to be saved.");
  }













  
  static int[] calculateSerial(int[][] sizes, ResizeConstraint[] resConstr, Float[] defPushWeights, int startSizeType, int bounds) {
    float[] lengths = new float[sizes.length];
    float usedLength = 0.0F;

    
    for (int i = 0; i < sizes.length; i++) {
      if (sizes[i] != null) {
        float len = (sizes[i][startSizeType] != -2147471302) ? sizes[i][startSizeType] : 0.0F;
        int newSizeBounded = getBrokenBoundary(len, sizes[i][0], sizes[i][2]);
        if (newSizeBounded != -2147471302) {
          len = newSizeBounded;
        }
        usedLength += len;
        lengths[i] = len;
      } 
    } 
    
    int useLengthI = Math.round(usedLength);
    if (useLengthI != bounds && resConstr != null) {
      boolean isGrow = (useLengthI < bounds);

      
      TreeSet<Integer> prioList = new TreeSet<>();
      for (int j = 0; j < sizes.length; j++) {
        ResizeConstraint resC = (ResizeConstraint)getIndexSafe((Object[])resConstr, j);
        if (resC != null)
          prioList.add(Integer.valueOf(isGrow ? resC.growPrio : resC.shrinkPrio)); 
      } 
      Integer[] prioIntegers = (Integer[])prioList.toArray((Object[])new Integer[prioList.size()]);
      
      for (int force = 0; force <= ((isGrow && defPushWeights != null) ? 1 : 0); force++) {
        for (int pr = prioIntegers.length - 1; pr >= 0; pr--) {
          int curPrio = prioIntegers[pr].intValue();
          
          float totWeight = 0.0F;
          Float[] resizeWeight = new Float[sizes.length];
          for (int k = 0; k < sizes.length; k++) {
            if (sizes[k] != null) {

              
              ResizeConstraint resC = (ResizeConstraint)getIndexSafe((Object[])resConstr, k);
              if (resC != null) {
                int prio = isGrow ? resC.growPrio : resC.shrinkPrio;
                
                if (curPrio == prio) {
                  if (isGrow) {
                    resizeWeight[k] = (force == 0 || resC.grow != null) ? resC.grow : defPushWeights[(k < defPushWeights.length) ? k : (defPushWeights.length - 1)];
                  } else {
                    resizeWeight[k] = resC.shrink;
                  } 
                  if (resizeWeight[k] != null)
                    totWeight += resizeWeight[k].floatValue(); 
                } 
              } 
            } 
          } 
          if (totWeight > 0.0F) {
            boolean hit;
            do {
              float toChange = bounds - usedLength;
              hit = false;
              float changedWeight = 0.0F;
              for (int m = 0; m < sizes.length && totWeight > 1.0E-4F; m++) {
                
                Float weight = resizeWeight[m];
                if (weight != null) {
                  float sizeDelta = toChange * weight.floatValue() / totWeight;
                  float newSize = lengths[m] + sizeDelta;
                  
                  if (sizes[m] != null) {
                    int newSizeBounded = getBrokenBoundary(newSize, sizes[m][0], sizes[m][2]);
                    if (newSizeBounded != -2147471302) {
                      resizeWeight[m] = null;
                      hit = true;
                      changedWeight += weight.floatValue();
                      newSize = newSizeBounded;
                      sizeDelta = newSize - lengths[m];
                    } 
                  } 
                  
                  lengths[m] = newSize;
                  usedLength += sizeDelta;
                } 
              } 
              totWeight -= changedWeight;
            } while (hit);
          } 
        } 
      } 
    } 
    return roundSizes(lengths);
  }

  
  static Object getIndexSafe(Object[] arr, int ix) {
    return (arr != null) ? arr[(ix < arr.length) ? ix : (arr.length - 1)] : null;
  }









  
  private static int getBrokenBoundary(float sz, int lower, int upper) {
    if (lower != -2147471302) {
      if (sz < lower)
        return lower; 
    } else if (sz < 0.0F) {
      return 0;
    } 
    
    if (upper != -2147471302 && sz > upper) {
      return upper;
    }
    return -2147471302;
  }


  
  static int sum(int[] terms, int start, int len) {
    int s = 0;
    for (int i = start, iSz = start + len; i < iSz; i++)
      s += terms[i]; 
    return s;
  }

  
  static int sum(int[] terms) {
    return sum(terms, 0, terms.length);
  }







  
  static float clamp(float f, float min, float max) {
    return Math.max(min, Math.min(f, max));
  }







  
  static int clamp(int i, int min, int max) {
    return Math.max(min, Math.min(i, max));
  }

  
  public static int getSizeSafe(int[] sizes, int sizeType) {
    if (sizes == null || sizes[sizeType] == -2147471302)
      return (sizeType == 2) ? 2097051 : 0; 
    return sizes[sizeType];
  }

  
  static BoundSize derive(BoundSize bs, UnitValue min, UnitValue pref, UnitValue max) {
    if (bs == null || bs.isUnset()) {
      return new BoundSize(min, pref, max, null);
    }
    return new BoundSize((min != null) ? min : bs
        .getMin(), (pref != null) ? pref : bs
        .getPreferred(), (max != null) ? max : bs
        .getMax(), bs
        .getGapPush(), null);
  }








  
  public static boolean isLeftToRight(LC lc, ContainerWrapper container) {
    if (lc != null && lc.getLeftToRight() != null) {
      return lc.getLeftToRight().booleanValue();
    }
    return (container == null || container.isLeftToRight());
  }





  
  static int[] roundSizes(float[] sizes) {
    int[] retInts = new int[sizes.length];
    float posD = 0.0F;
    
    for (int i = 0; i < retInts.length; i++) {
      int posI = (int)(posD + 0.5F);
      
      posD += sizes[i];
      
      retInts[i] = (int)(posD + 0.5F) - posI;
    } 
    
    return retInts;
  }






  
  static boolean equals(Object o1, Object o2) {
    return (o1 == o2 || (o1 != null && o2 != null && o1.equals(o2)));
  }































  
  static UnitValue getInsets(LC lc, int side, boolean getDefault) {
    UnitValue[] i = lc.getInsets();
    return (i != null && i[side] != null) ? i[side] : (getDefault ? PlatformDefaults.getPanelInsets(side) : UnitValue.ZERO);
  }






  
  static void writeXMLObject(OutputStream os, Object o, ExceptionListener listener) {
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(LayoutUtil.class.getClassLoader());
    
    XMLEncoder encoder = new XMLEncoder(os);
    
    if (listener != null) {
      encoder.setExceptionListener(listener);
    }
    encoder.writeObject(o);
    encoder.close();
    
    Thread.currentThread().setContextClassLoader(oldClassLoader);
  }
  
  private static ByteArrayOutputStream writeOutputStream = null;




  
  public static synchronized void writeAsXML(ObjectOutput out, Object o) throws IOException {
    if (writeOutputStream == null) {
      writeOutputStream = new ByteArrayOutputStream(16384);
    }
    writeOutputStream.reset();
    
    writeXMLObject(writeOutputStream, o, new ExceptionListener()
        {
          public void exceptionThrown(Exception e) {
            e.printStackTrace();
          }
        });
    byte[] buf = writeOutputStream.toByteArray();
    
    out.writeInt(buf.length);
    out.write(buf);
  }
  
  private static byte[] readBuf = null;





  
  public static synchronized Object readAsXML(ObjectInput in) throws IOException {
    if (readBuf == null) {
      readBuf = new byte[16384];
    }
    Thread cThread = Thread.currentThread();
    ClassLoader oldCL = null;
    
    try {
      oldCL = cThread.getContextClassLoader();
      cThread.setContextClassLoader(LayoutUtil.class.getClassLoader());
    } catch (SecurityException securityException) {}

    
    Object o = null;
    try {
      int length = in.readInt();
      if (length > readBuf.length) {
        readBuf = new byte[length];
      }
      in.readFully(readBuf, 0, length);
      
      o = (new XMLDecoder(new ByteArrayInputStream(readBuf, 0, length))).readObject();
    }
    catch (EOFException eOFException) {}

    
    if (oldCL != null) {
      cThread.setContextClassLoader(oldCL);
    }
    return o;
  }
  
  private static final IdentityHashMap<Object, Object> SER_MAP = new IdentityHashMap<>(2);





  
  public static void setSerializedObject(Object caller, Object o) {
    synchronized (SER_MAP) {
      SER_MAP.put(caller, o);
    } 
  }





  
  public static Object getSerializedObject(Object caller) {
    synchronized (SER_MAP) {
      return SER_MAP.remove(caller);
    } 
  }
}
