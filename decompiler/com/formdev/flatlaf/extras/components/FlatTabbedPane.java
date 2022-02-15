package com.formdev.flatlaf.extras.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.Insets;
import java.util.function.BiConsumer;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;


























public class FlatTabbedPane
  extends JTabbedPane
  implements FlatComponentExtension
{
  public boolean isShowTabSeparators() {
    return getClientPropertyBoolean("JTabbedPane.showTabSeparators", "TabbedPane.showTabSeparators");
  }



  
  public void setShowTabSeparators(boolean showTabSeparators) {
    putClientProperty("JTabbedPane.showTabSeparators", Boolean.valueOf(showTabSeparators));
  }




  
  public boolean isShowContentSeparators() {
    return getClientPropertyBoolean("JTabbedPane.showContentSeparator", true);
  }



  
  public void setShowContentSeparators(boolean showContentSeparators) {
    putClientPropertyBoolean("JTabbedPane.showContentSeparator", showContentSeparators, true);
  }




  
  public boolean isHasFullBorder() {
    return getClientPropertyBoolean("JTabbedPane.hasFullBorder", "TabbedPane.hasFullBorder");
  }



  
  public void setHasFullBorder(boolean hasFullBorder) {
    putClientProperty("JTabbedPane.hasFullBorder", Boolean.valueOf(hasFullBorder));
  }




  
  public boolean isHideTabAreaWithOneTab() {
    return getClientPropertyBoolean("JTabbedPane.hideTabAreaWithOneTab", false);
  }



  
  public void setHideTabAreaWithOneTab(boolean hideTabAreaWithOneTab) {
    putClientPropertyBoolean("JTabbedPane.hideTabAreaWithOneTab", hideTabAreaWithOneTab, false);
  }




  
  public int getMinimumTabWidth() {
    return getClientPropertyInt("JTabbedPane.minimumTabWidth", "TabbedPane.minimumTabWidth");
  }



  
  public void setMinimumTabWidth(int minimumTabWidth) {
    putClientProperty("JTabbedPane.minimumTabWidth", (minimumTabWidth >= 0) ? Integer.valueOf(minimumTabWidth) : null);
  }




  
  public int getMinimumTabWidth(int tabIndex) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    return FlatClientProperties.clientPropertyInt(c, "JTabbedPane.minimumTabWidth", 0);
  }



  
  public void setMinimumTabWidth(int tabIndex, int minimumTabWidth) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    c.putClientProperty("JTabbedPane.minimumTabWidth", (minimumTabWidth >= 0) ? Integer.valueOf(minimumTabWidth) : null);
  }




  
  public int getMaximumTabWidth() {
    return getClientPropertyInt("JTabbedPane.maximumTabWidth", "TabbedPane.maximumTabWidth");
  }






  
  public void setMaximumTabWidth(int maximumTabWidth) {
    putClientProperty("JTabbedPane.maximumTabWidth", (maximumTabWidth >= 0) ? Integer.valueOf(maximumTabWidth) : null);
  }




  
  public int getMaximumTabWidth(int tabIndex) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    return FlatClientProperties.clientPropertyInt(c, "JTabbedPane.maximumTabWidth", 0);
  }






  
  public void setMaximumTabWidth(int tabIndex, int maximumTabWidth) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    c.putClientProperty("JTabbedPane.maximumTabWidth", (maximumTabWidth >= 0) ? Integer.valueOf(maximumTabWidth) : null);
  }




  
  public int getTabHeight() {
    return getClientPropertyInt("JTabbedPane.tabHeight", "TabbedPane.tabHeight");
  }





  
  public void setTabHeight(int tabHeight) {
    putClientProperty("JTabbedPane.tabHeight", (tabHeight >= 0) ? Integer.valueOf(tabHeight) : null);
  }




  
  public Insets getTabInsets() {
    return getClientPropertyInsets("JTabbedPane.tabInsets", "TabbedPane.tabInsets");
  }





  
  public void setTabInsets(Insets tabInsets) {
    putClientProperty("JTabbedPane.tabInsets", tabInsets);
  }




  
  public Insets getTabInsets(int tabIndex) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    return (Insets)c.getClientProperty("JTabbedPane.tabInsets");
  }





  
  public void setTabInsets(int tabIndex, Insets tabInsets) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    c.putClientProperty("JTabbedPane.tabInsets", tabInsets);
  }




  
  public Insets getTabAreaInsets() {
    return getClientPropertyInsets("JTabbedPane.tabAreaInsets", "TabbedPane.tabAreaInsets");
  }



  
  public void setTabAreaInsets(Insets tabAreaInsets) {
    putClientProperty("JTabbedPane.tabAreaInsets", tabAreaInsets);
  }




  
  public boolean isTabsClosable() {
    return getClientPropertyBoolean("JTabbedPane.tabClosable", false);
  }









  
  public void setTabsClosable(boolean tabClosable) {
    putClientPropertyBoolean("JTabbedPane.tabClosable", tabClosable, false);
  }




  
  public Boolean isTabClosable(int tabIndex) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    Object value = c.getClientProperty("JTabbedPane.tabClosable");
    return Boolean.valueOf((value instanceof Boolean) ? ((Boolean)value).booleanValue() : isTabsClosable());
  }








  
  public void setTabClosable(int tabIndex, boolean tabClosable) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    c.putClientProperty("JTabbedPane.tabClosable", Boolean.valueOf(tabClosable));
  }




  
  public String getTabCloseToolTipText() {
    return (String)getClientProperty("JTabbedPane.tabCloseToolTipText");
  }



  
  public void setTabCloseToolTipText(String tabCloseToolTipText) {
    putClientProperty("JTabbedPane.tabCloseToolTipText", tabCloseToolTipText);
  }




  
  public String getTabCloseToolTipText(int tabIndex) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    return (String)c.getClientProperty("JTabbedPane.tabCloseToolTipText");
  }



  
  public void setTabCloseToolTipText(int tabIndex, String tabCloseToolTipText) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    c.putClientProperty("JTabbedPane.tabCloseToolTipText", tabCloseToolTipText);
  }






  
  public BiConsumer<JTabbedPane, Integer> getTabCloseCallback() {
    return (BiConsumer<JTabbedPane, Integer>)getClientProperty("JTabbedPane.tabCloseCallback");
  }




















  
  public void setTabCloseCallback(BiConsumer<JTabbedPane, Integer> tabCloseCallback) {
    putClientProperty("JTabbedPane.tabCloseCallback", tabCloseCallback);
  }






  
  public BiConsumer<JTabbedPane, Integer> getTabCloseCallback(int tabIndex) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    return (BiConsumer<JTabbedPane, Integer>)c.getClientProperty("JTabbedPane.tabCloseCallback");
  }






  
  public void setTabCloseCallback(int tabIndex, BiConsumer<JTabbedPane, Integer> tabCloseCallback) {
    JComponent c = (JComponent)getComponentAt(tabIndex);
    c.putClientProperty("JTabbedPane.tabCloseCallback", tabCloseCallback);
  }
  
  public enum TabsPopupPolicy
  {
    never, asNeeded;
  }



  
  public TabsPopupPolicy getTabsPopupPolicy() {
    return getClientPropertyEnumString("JTabbedPane.tabsPopupPolicy", TabsPopupPolicy.class, "TabbedPane.tabsPopupPolicy", TabsPopupPolicy.asNeeded);
  }





  
  public void setTabsPopupPolicy(TabsPopupPolicy tabsPopupPolicy) {
    putClientPropertyEnumString("JTabbedPane.tabsPopupPolicy", tabsPopupPolicy);
  }
  
  public enum ScrollButtonsPolicy
  {
    never, asNeeded, asNeededSingle;
  }


  
  public ScrollButtonsPolicy getScrollButtonsPolicy() {
    return getClientPropertyEnumString("JTabbedPane.scrollButtonsPolicy", ScrollButtonsPolicy.class, "TabbedPane.scrollButtonsPolicy", ScrollButtonsPolicy.asNeededSingle);
  }




  
  public void setScrollButtonsPolicy(ScrollButtonsPolicy scrollButtonsPolicy) {
    putClientPropertyEnumString("JTabbedPane.scrollButtonsPolicy", scrollButtonsPolicy);
  }
  
  public enum ScrollButtonsPlacement
  {
    both, trailing;
  }


  
  public ScrollButtonsPlacement getScrollButtonsPlacement() {
    return getClientPropertyEnumString("JTabbedPane.scrollButtonsPlacement", ScrollButtonsPlacement.class, "TabbedPane.scrollButtonsPlacement", ScrollButtonsPlacement.both);
  }




  
  public void setScrollButtonsPlacement(ScrollButtonsPlacement scrollButtonsPlacement) {
    putClientPropertyEnumString("JTabbedPane.scrollButtonsPlacement", scrollButtonsPlacement);
  }
  
  public enum TabAreaAlignment
  {
    leading, trailing, center, fill;
  }


  
  public TabAreaAlignment getTabAreaAlignment() {
    return getClientPropertyEnumString("JTabbedPane.tabAreaAlignment", TabAreaAlignment.class, "TabbedPane.tabAreaAlignment", TabAreaAlignment.leading);
  }




  
  public void setTabAreaAlignment(TabAreaAlignment tabAreaAlignment) {
    putClientPropertyEnumString("JTabbedPane.tabAreaAlignment", tabAreaAlignment);
  }
  
  public enum TabAlignment
  {
    leading, trailing, center;
  }


  
  public TabAlignment getTabAlignment() {
    return getClientPropertyEnumString("JTabbedPane.tabAlignment", TabAlignment.class, "TabbedPane.tabAlignment", TabAlignment.center);
  }




  
  public void setTabAlignment(TabAlignment tabAlignment) {
    putClientPropertyEnumString("JTabbedPane.tabAlignment", tabAlignment);
  }
  
  public enum TabWidthMode
  {
    preferred, equal, compact;
  }


  
  public TabWidthMode getTabWidthMode() {
    return getClientPropertyEnumString("JTabbedPane.tabWidthMode", TabWidthMode.class, "TabbedPane.tabWidthMode", TabWidthMode.preferred);
  }




  
  public void setTabWidthMode(TabWidthMode tabWidthMode) {
    putClientPropertyEnumString("JTabbedPane.tabWidthMode", tabWidthMode);
  }




  
  public int getTabIconPlacement() {
    return getClientPropertyInt("JTabbedPane.tabIconPlacement", 10);
  }











  
  public void setTabIconPlacement(int tabIconPlacement) {
    putClientProperty("JTabbedPane.tabIconPlacement", (tabIconPlacement >= 0) ? Integer.valueOf(tabIconPlacement) : null);
  }




  
  public Component getLeadingComponent() {
    return (Component)getClientProperty("JTabbedPane.leadingComponent");
  }








  
  public void setLeadingComponent(Component leadingComponent) {
    putClientProperty("JTabbedPane.leadingComponent", leadingComponent);
  }




  
  public Component getTrailingComponent() {
    return (Component)getClientProperty("JTabbedPane.trailingComponent");
  }








  
  public void setTrailingComponent(Component trailingComponent) {
    putClientProperty("JTabbedPane.trailingComponent", trailingComponent);
  }
}
