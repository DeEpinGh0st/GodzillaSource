package net.miginfocom.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;












public class MigLayout
  implements LayoutManager2, Externalizable
{
  private final Map<Component, Object> scrConstrMap = new IdentityHashMap<>(8);


  
  private Object layoutConstraints = ""; private Object colConstraints = ""; private Object rowConstraints = "";


  
  private transient ContainerWrapper cacheParentW = null;
  
  private final transient Map<ComponentWrapper, CC> ccMap = new HashMap<>(8);
  private transient Timer debugTimer = null;
  
  private transient LC lc = null;
  private transient AC colSpecs = null; private transient AC rowSpecs = null;
  private transient Grid grid = null;
  private transient int lastModCount = PlatformDefaults.getModCount();
  private transient int lastHash = -1;
  private transient Dimension lastInvalidSize = null;
  private transient boolean lastWasInvalid = false;
  private transient Dimension lastParentSize = null;
  
  private transient ArrayList<LayoutCallback> callbackList = null;
  
  private transient boolean dirty = true;
  
  private long lastSize;

  
  public MigLayout() {
    this("", "", "");
  }




  
  public MigLayout(String layoutConstraints) {
    this(layoutConstraints, "", "");
  }





  
  public MigLayout(String layoutConstraints, String colConstraints) {
    this(layoutConstraints, colConstraints, "");
  }
















  
  public MigLayout(LC layoutConstraints) {
    this(layoutConstraints, (AC)null, (AC)null);
  }





  
  public MigLayout(LC layoutConstraints, AC colConstraints) {
    this(layoutConstraints, colConstraints, (AC)null);
  }


















  
  public Object getLayoutConstraints() {
    return this.layoutConstraints;
  }







  
  public void setLayoutConstraints(Object constr) {
    if (constr == null || constr instanceof String) {
      constr = ConstraintParser.prepare((String)constr);
      this.lc = ConstraintParser.parseLayoutConstraint((String)constr);
    } else if (constr instanceof LC) {
      this.lc = (LC)constr;
    } else {
      throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
    } 
    this.layoutConstraints = constr;
    this.dirty = true;
  }





  
  public Object getColumnConstraints() {
    return this.colConstraints;
  }







  
  public void setColumnConstraints(Object constr) {
    if (constr == null || constr instanceof String) {
      constr = ConstraintParser.prepare((String)constr);
      this.colSpecs = ConstraintParser.parseColumnConstraints((String)constr);
    } else if (constr instanceof AC) {
      this.colSpecs = (AC)constr;
    } else {
      throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
    } 
    this.colConstraints = constr;
    this.dirty = true;
  }





  
  public Object getRowConstraints() {
    return this.rowConstraints;
  }







  
  public void setRowConstraints(Object constr) {
    if (constr == null || constr instanceof String) {
      constr = ConstraintParser.prepare((String)constr);
      this.rowSpecs = ConstraintParser.parseRowConstraints((String)constr);
    } else if (constr instanceof AC) {
      this.rowSpecs = (AC)constr;
    } else {
      throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
    } 
    this.rowConstraints = constr;
    this.dirty = true;
  }




  
  public Map<Component, Object> getConstraintMap() {
    return new IdentityHashMap<>(this.scrConstrMap);
  }




  
  public void setConstraintMap(Map<Component, Object> map) {
    this.scrConstrMap.clear();
    this.ccMap.clear();
    for (Map.Entry<Component, Object> e : map.entrySet()) {
      setComponentConstraintsImpl(e.getKey(), e.getValue(), true);
    }
  }









  
  public Object getComponentConstraints(Component comp) {
    synchronized (comp.getParent().getTreeLock()) {
      return this.scrConstrMap.get(comp);
    } 
  }









  
  public void setComponentConstraints(Component comp, Object constr) {
    setComponentConstraintsImpl(comp, constr, false);
  }










  
  private void setComponentConstraintsImpl(Component comp, Object constr, boolean noCheck) {
    Container parent = comp.getParent();
    synchronized ((parent != null) ? parent.getTreeLock() : new Object()) {
      if (!noCheck && !this.scrConstrMap.containsKey(comp)) {
        throw new IllegalArgumentException("Component must already be added to parent!");
      }
      ComponentWrapper cw = new SwingComponentWrapper(comp);
      
      if (constr == null || constr instanceof String) {
        String cStr = ConstraintParser.prepare((String)constr);
        
        this.scrConstrMap.put(comp, constr);
        this.ccMap.put(cw, ConstraintParser.parseComponentConstraint(cStr));
      }
      else if (constr instanceof CC) {
        
        this.scrConstrMap.put(comp, constr);
        this.ccMap.put(cw, (CC)constr);
      } else {
        
        throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + constr.getClass().toString());
      } 
      
      this.dirty = true;
    } 
  }





  
  public boolean isManagingComponent(Component c) {
    return this.scrConstrMap.containsKey(c);
  }




  
  public void addLayoutCallback(LayoutCallback callback) {
    if (callback == null) {
      throw new NullPointerException();
    }
    if (this.callbackList == null) {
      this.callbackList = new ArrayList<>(1);
    }
    this.callbackList.add(callback);
    
    this.grid = null;
  }




  
  public void removeLayoutCallback(LayoutCallback callback) {
    if (this.callbackList != null) {
      this.callbackList.remove(callback);
    }
  }











  
  private void setDebug(ComponentWrapper parentW, boolean b) {
    if (b && (this.debugTimer == null || this.debugTimer.getDelay() != getDebugMillis())) {
      if (this.debugTimer != null) {
        this.debugTimer.stop();
      }
      ContainerWrapper pCW = parentW.getParent();
      final Component parent = (pCW != null) ? (Component)pCW.getComponent() : null;
      
      this.debugTimer = new Timer(getDebugMillis(), new MyDebugRepaintListener());
      
      if (parent != null) {
        SwingUtilities.invokeLater(new Runnable()
            {
              public void run() {
                Container p = parent.getParent();
                if (p != null) {
                  if (p instanceof JComponent) {
                    ((JComponent)p).revalidate();
                  } else {
                    parent.invalidate();
                    p.validate();
                  } 
                }
              }
            });
      }
      
      this.debugTimer.setInitialDelay(100);
      this.debugTimer.start();
    }
    else if (!b && this.debugTimer != null) {
      this.debugTimer.stop();
      this.debugTimer = null;
    } 
  }




  
  private boolean getDebug() {
    return (this.debugTimer != null);
  }




  
  private int getDebugMillis() {
    int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
    return (globalDebugMillis > 0) ? globalDebugMillis : this.lc.getDebugMillis();
  }




  
  private void checkCache(Container parent) {
    if (parent == null) {
      return;
    }
    if (this.dirty) {
      this.grid = null;
    }
    cleanConstraintMaps(parent);

    
    int mc = PlatformDefaults.getModCount();
    if (this.lastModCount != mc) {
      this.grid = null;
      this.lastModCount = mc;
    } 
    
    if (!parent.isValid()) {
      if (!this.lastWasInvalid) {
        this.lastWasInvalid = true;
        
        int hash = 0;
        boolean resetLastInvalidOnParent = false;
        for (ComponentWrapper wrapper : this.ccMap.keySet()) {
          Object component = wrapper.getComponent();
          if (component instanceof javax.swing.JTextArea || component instanceof javax.swing.JEditorPane) {
            resetLastInvalidOnParent = true;
          }
          hash ^= wrapper.getLayoutHashCode();
          hash += 285134905;
        } 
        if (resetLastInvalidOnParent) {
          resetLastInvalidOnParent(parent);
        }
        if (hash != this.lastHash) {
          this.grid = null;
          this.lastHash = hash;
        } 
        
        Dimension ps = parent.getSize();
        if (this.lastInvalidSize == null || !this.lastInvalidSize.equals(ps)) {
          this.grid = null;
          this.lastInvalidSize = ps;
        } 
      } 
    } else {
      this.lastWasInvalid = false;
    } 
    
    ContainerWrapper par = checkParent(parent);
    
    setDebug((ComponentWrapper)par, (getDebugMillis() > 0));
    
    if (this.grid == null) {
      this.grid = new Grid(par, this.lc, this.rowSpecs, this.colSpecs, this.ccMap, this.callbackList);
    }
    this.dirty = false;
  }





  
  private void cleanConstraintMaps(Container parent) {
    HashSet<Component> parentCompSet = new HashSet<>(Arrays.asList(parent.getComponents()));
    
    Iterator<Map.Entry<ComponentWrapper, CC>> it = this.ccMap.entrySet().iterator();
    while (it.hasNext()) {
      Component c = (Component)((ComponentWrapper)((Map.Entry)it.next()).getKey()).getComponent();
      if (!parentCompSet.contains(c)) {
        it.remove();
        this.scrConstrMap.remove(c);
      } 
    } 
  }




  
  private void resetLastInvalidOnParent(Container parent) {
    while (parent != null) {
      LayoutManager layoutManager = parent.getLayout();
      if (layoutManager instanceof MigLayout) {
        ((MigLayout)layoutManager).lastWasInvalid = false;
      }
      parent = parent.getParent();
    } 
  }

  
  private ContainerWrapper checkParent(Container parent) {
    if (parent == null) {
      return null;
    }
    if (this.cacheParentW == null || this.cacheParentW.getComponent() != parent) {
      this.cacheParentW = new SwingContainerWrapper(parent);
    }
    return this.cacheParentW;
  }
  
  public MigLayout(String layoutConstraints, String colConstraints, String rowConstraints) { this.lastSize = 0L; setLayoutConstraints(layoutConstraints); setColumnConstraints(colConstraints); setRowConstraints(rowConstraints); } public MigLayout(LC layoutConstraints, AC colConstraints, AC rowConstraints) { this.lastSize = 0L;
    setLayoutConstraints(layoutConstraints);
    setColumnConstraints(colConstraints);
    setRowConstraints(rowConstraints); }
   public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
      checkCache(parent);
      
      Insets i = parent.getInsets();



      
      int[] b = { i.left, i.top, parent.getWidth() - i.left - i.right, parent.getHeight() - i.top - i.bottom };

      
      if (this.grid.layout(b, this.lc.getAlignX(), this.lc.getAlignY(), getDebug())) {
        this.grid = null;
        checkCache(parent);
        this.grid.layout(b, this.lc.getAlignX(), this.lc.getAlignY(), getDebug());
      } 
      
      long newSize = this.grid.getHeight()[1] + (this.grid.getWidth()[1] << 32L);
      if (this.lastSize != newSize) {
        this.lastSize = newSize;
        final ContainerWrapper containerWrapper = checkParent(parent);
        Window win = (Window)SwingUtilities.getAncestorOfClass(Window.class, (Component)containerWrapper.getComponent());
        if (win != null) {
          if (win.isVisible()) {
            SwingUtilities.invokeLater(new Runnable()
                {
                  public void run() {
                    MigLayout.this.adjustWindowSize(containerWrapper);
                  }
                });
          } else {
            adjustWindowSize(containerWrapper);
          } 
        }
      } 
      this.lastInvalidSize = null;
    } 
  }




  
  private void adjustWindowSize(ContainerWrapper parent) {
    BoundSize wBounds = this.lc.getPackWidth();
    BoundSize hBounds = this.lc.getPackHeight();
    
    if (wBounds == BoundSize.NULL_SIZE && hBounds == BoundSize.NULL_SIZE) {
      return;
    }
    Container packable = getPackable((Component)parent.getComponent());
    
    if (packable != null) {
      
      Component pc = (Component)parent.getComponent();
      
      Container c = (pc instanceof Container) ? (Container)pc : pc.getParent();
      for (; c != null; c = c.getParent()) {
        LayoutManager layout = c.getLayout();
        if (layout instanceof javax.swing.BoxLayout || layout instanceof javax.swing.OverlayLayout) {
          ((LayoutManager2)layout).invalidateLayout(c);
        }
      } 
      Dimension prefSize = packable.getPreferredSize();
      int targW = constrain(checkParent(packable), packable.getWidth(), prefSize.width, wBounds);
      int targH = constrain(checkParent(packable), packable.getHeight(), prefSize.height, hBounds);
      
      Point p = packable.isShowing() ? packable.getLocationOnScreen() : packable.getLocation();
      
      int x = Math.round(p.x - (targW - packable.getWidth()) * (1.0F - this.lc.getPackWidthAlign()));
      int y = Math.round(p.y - (targH - packable.getHeight()) * (1.0F - this.lc.getPackHeightAlign()));
      
      if (packable instanceof JPopupMenu) {
        JPopupMenu popupMenu = (JPopupMenu)packable;
        popupMenu.setVisible(false);
        popupMenu.setPopupSize(targW, targH);
        Component invoker = popupMenu.getInvoker();
        Point popPoint = new Point(x, y);
        SwingUtilities.convertPointFromScreen(popPoint, invoker);
        ((JPopupMenu)packable).show(invoker, popPoint.x, popPoint.y);
        
        packable.setPreferredSize(null);
      } else {
        
        packable.setBounds(x, y, targW, targH);
      } 
    } 
  }




  
  private Container getPackable(Component comp) {
    JPopupMenu popup = findType(JPopupMenu.class, comp);
    if (popup != null) {
      Container popupComp = popup;
      while (popupComp != null) {
        if (popupComp.getClass().getName().contains("HeavyWeightWindow"))
          return popupComp; 
        popupComp = popupComp.getParent();
      } 
      return popup;
    } 
    
    return findType((Class)Window.class, comp);
  }

  
  public static <E> E findType(Class<E> clazz, Component comp) {
    while (comp != null && !clazz.isInstance(comp)) {
      comp = comp.getParent();
    }
    return (E)comp;
  }


  
  private int constrain(ContainerWrapper parent, int winSize, int prefSize, BoundSize constrain) {
    if (constrain == null) {
      return winSize;
    }
    int retSize = winSize;
    UnitValue wUV = constrain.getPreferred();
    if (wUV != null) {
      retSize = wUV.getPixels(prefSize, parent, (ComponentWrapper)parent);
    }
    retSize = constrain.constrain(retSize, prefSize, parent);
    
    return constrain.getGapPush() ? Math.max(winSize, retSize) : retSize;
  }


  
  public Dimension minimumLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      return getSizeImpl(parent, 0);
    } 
  }


  
  public Dimension preferredLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      if (this.lastParentSize == null || !parent.getSize().equals(this.lastParentSize)) {
        for (ComponentWrapper wrapper : this.ccMap.keySet()) {
          if (wrapper.getContentBias() != -1) {
            layoutContainer(parent);
            
            break;
          } 
        } 
      }
      this.lastParentSize = parent.getSize();
      return getSizeImpl(parent, 1);
    } 
  }


  
  public Dimension maximumLayoutSize(Container parent) {
    return new Dimension(2147483647, 2147483647);
  }


  
  private Dimension getSizeImpl(Container parent, int sizeType) {
    checkCache(parent);
    
    Insets i = parent.getInsets();
    
    int w = LayoutUtil.getSizeSafe((this.grid != null) ? this.grid.getWidth() : null, sizeType) + i.left + i.right;
    int h = LayoutUtil.getSizeSafe((this.grid != null) ? this.grid.getHeight() : null, sizeType) + i.top + i.bottom;
    
    return new Dimension(w, h);
  }


  
  public float getLayoutAlignmentX(Container parent) {
    return (this.lc != null && this.lc.getAlignX() != null) ? this.lc.getAlignX().getPixels(1.0F, checkParent(parent), null) : 0.0F;
  }


  
  public float getLayoutAlignmentY(Container parent) {
    return (this.lc != null && this.lc.getAlignY() != null) ? this.lc.getAlignY().getPixels(1.0F, checkParent(parent), null) : 0.0F;
  }


  
  public void addLayoutComponent(String s, Component comp) {
    addLayoutComponent(comp, s);
  }


  
  public void addLayoutComponent(Component comp, Object constraints) {
    synchronized (comp.getParent().getTreeLock()) {
      setComponentConstraintsImpl(comp, constraints, true);
    } 
  }


  
  public void removeLayoutComponent(Component comp) {
    synchronized (comp.getParent().getTreeLock()) {
      this.scrConstrMap.remove(comp);
      this.ccMap.remove(new SwingComponentWrapper(comp));
      this.grid = null;
    } 
  }


  
  public void invalidateLayout(Container target) {
    this.dirty = true;
  }





  
  private Object readResolve() throws ObjectStreamException {
    return LayoutUtil.getSerializedObject(this);
  }


  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
  }


  
  public void writeExternal(ObjectOutput out) throws IOException {
    if (getClass() == MigLayout.class)
      LayoutUtil.writeAsXML(out, this); 
  }
  
  private class MyDebugRepaintListener
    implements ActionListener {
    private MyDebugRepaintListener() {}
    
    public void actionPerformed(ActionEvent e) {
      if (MigLayout.this.grid != null) {
        Component comp = (Component)MigLayout.this.grid.getContainer().getComponent();
        if (comp.isShowing()) {
          MigLayout.this.grid.paintDebug();
          return;
        } 
      } 
      MigLayout.this.debugTimer.stop();
      MigLayout.this.debugTimer = null;
    }
  }
}
