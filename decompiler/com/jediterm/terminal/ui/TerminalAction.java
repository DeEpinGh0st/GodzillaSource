package com.jediterm.terminal.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TerminalAction
{
  private final String myName;
  private final KeyStroke[] myKeyStrokes;
  private final Predicate<KeyEvent> myRunnable;
  private Character myMnemonic = null;
  private Supplier<Boolean> myEnabledSupplier = () -> Boolean.valueOf(true);
  private Integer myMnemonicKey = null;
  private boolean mySeparatorBefore = false;
  private boolean myHidden = false;
  
  public TerminalAction(@NotNull TerminalActionPresentation presentation, @NotNull Predicate<KeyEvent> runnable) {
    this(presentation.getName(), presentation.getKeyStrokes().<KeyStroke>toArray(new KeyStroke[0]), runnable);
  }
  
  public TerminalAction(@NotNull TerminalActionPresentation presentation) {
    this(presentation, keyEvent -> true);
  }
  
  public TerminalAction(@NotNull String name, @NotNull KeyStroke[] keyStrokes, @NotNull Predicate<KeyEvent> runnable) {
    this.myName = name;
    this.myKeyStrokes = keyStrokes;
    this.myRunnable = runnable;
  }
  
  public boolean matches(KeyEvent e) {
    for (KeyStroke ks : this.myKeyStrokes) {
      if (ks.equals(KeyStroke.getKeyStrokeForEvent(e))) {
        return true;
      }
    } 
    return false;
  }
  
  public boolean isEnabled(@Nullable KeyEvent e) {
    return ((Boolean)this.myEnabledSupplier.get()).booleanValue();
  }
  
  public boolean actionPerformed(@Nullable KeyEvent e) {
    return this.myRunnable.test(e);
  }
  
  public static boolean processEvent(@NotNull TerminalActionProvider actionProvider, @NotNull KeyEvent e) {
    if (actionProvider == null) $$$reportNull$$$0(6);  if (e == null) $$$reportNull$$$0(7);  for (TerminalAction a : actionProvider.getActions()) {
      if (a.matches(e)) {
        return (a.isEnabled(e) && a.actionPerformed(e));
      }
    } 
    
    if (actionProvider.getNextProvider() != null) {
      return processEvent(actionProvider.getNextProvider(), e);
    }
    
    return false;
  }
  
  public static boolean addToMenu(JPopupMenu menu, TerminalActionProvider actionProvider) {
    boolean added = false;
    if (actionProvider.getNextProvider() != null) {
      added = addToMenu(menu, actionProvider.getNextProvider());
    }
    boolean addSeparator = added;
    for (TerminalAction a : actionProvider.getActions()) {
      if (a.isHidden()) {
        continue;
      }
      if (!addSeparator) {
        addSeparator = a.isSeparated();
      }
      if (addSeparator) {
        menu.addSeparator();
        addSeparator = false;
      } 
      
      menu.add(a.toMenuItem());
      
      added = true;
    } 
    
    return added;
  }
  
  public int getKeyCode() {
    KeyStroke[] arrayOfKeyStroke = this.myKeyStrokes; int i = arrayOfKeyStroke.length; byte b = 0; if (b < i) { KeyStroke ks = arrayOfKeyStroke[b];
      return ks.getKeyCode(); }
    
    return 0;
  }
  
  public int getModifiers() {
    KeyStroke[] arrayOfKeyStroke = this.myKeyStrokes; int i = arrayOfKeyStroke.length; byte b = 0; if (b < i) { KeyStroke ks = arrayOfKeyStroke[b];
      return ks.getModifiers(); }
    
    return 0;
  }
  
  public String getName() {
    return this.myName;
  }
  
  public TerminalAction withMnemonic(Character ch) {
    this.myMnemonic = ch;
    return this;
  }
  
  public TerminalAction withMnemonicKey(Integer key) {
    this.myMnemonicKey = key;
    return this;
  }
  
  public TerminalAction withEnabledSupplier(@NotNull Supplier<Boolean> enabledSupplier) {
    if (enabledSupplier == null) $$$reportNull$$$0(8);  this.myEnabledSupplier = enabledSupplier;
    return this;
  }
  
  public TerminalAction separatorBefore(boolean enabled) {
    this.mySeparatorBefore = enabled;
    return this;
  }
  
  public JMenuItem toMenuItem() {
    JMenuItem menuItem = new JMenuItem(this.myName);
    
    if (this.myMnemonic != null) {
      menuItem.setMnemonic(this.myMnemonic.charValue());
    }
    if (this.myMnemonicKey != null) {
      menuItem.setMnemonic(this.myMnemonicKey.intValue());
    }
    
    if (this.myKeyStrokes.length > 0) {
      menuItem.setAccelerator(this.myKeyStrokes[0]);
    }
    
    menuItem.addActionListener(actionEvent -> actionPerformed(null));
    menuItem.setEnabled(isEnabled(null));
    
    return menuItem;
  }
  
  public boolean isSeparated() {
    return this.mySeparatorBefore;
  }
  
  public boolean isHidden() {
    return this.myHidden;
  }
  
  public TerminalAction withHidden(boolean hidden) {
    this.myHidden = hidden;
    return this;
  }

  
  public String toString() {
    return "'" + this.myName + "'";
  }
}
