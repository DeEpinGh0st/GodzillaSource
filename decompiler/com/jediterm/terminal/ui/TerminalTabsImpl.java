package com.jediterm.terminal.ui;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TerminalTabsImpl
  implements AbstractTabs<JediTermWidget>
{
  protected JTabbedPane myTabbedPane = new JTabbedPane();

  
  public int getTabCount() {
    return this.myTabbedPane.getTabCount();
  }

  
  public void addTab(String name, JediTermWidget terminal) {
    this.myTabbedPane.addTab(name, terminal);
  }

  
  public String getTitleAt(int index) {
    return this.myTabbedPane.getTitleAt(index);
  }

  
  public int getSelectedIndex() {
    return this.myTabbedPane.getSelectedIndex();
  }

  
  public void setSelectedIndex(int index) {
    this.myTabbedPane.setSelectedIndex(index);
  }

  
  public void setTabComponentAt(int index, Component component) {
    this.myTabbedPane.setTabComponentAt(index, component);
  }

  
  public int indexOfComponent(Component component) {
    return this.myTabbedPane.indexOfComponent(component);
  }

  
  public int indexOfTabComponent(Component component) {
    return this.myTabbedPane.indexOfTabComponent(component);
  }

  
  public void removeAll() {
    this.myTabbedPane.removeAll();
  }

  
  public void remove(JediTermWidget terminal) {
    this.myTabbedPane.remove(terminal);
  }

  
  public void setTitleAt(int index, String name) {
    this.myTabbedPane.setTitleAt(index, name);
  }

  
  public void setSelectedComponent(JediTermWidget terminal) {
    this.myTabbedPane.setSelectedComponent(terminal);
  }

  
  public JComponent getComponent() {
    return this.myTabbedPane;
  }

  
  public JediTermWidget getComponentAt(int index) {
    return (JediTermWidget)this.myTabbedPane.getComponentAt(index);
  }

  
  public void addChangeListener(final AbstractTabs.TabChangeListener listener) {
    this.myTabbedPane.addChangeListener(new ChangeListener()
        {
          public void stateChanged(ChangeEvent e) {
            listener.selectionChanged();
          }
        });
    
    this.myTabbedPane.addContainerListener(new ContainerListener()
        {
          public void componentAdded(ContainerEvent e) {}



          
          public void componentRemoved(ContainerEvent e) {
            if (e.getSource() == TerminalTabsImpl.this.myTabbedPane)
              listener.tabRemoved(); 
          }
        });
  }
}
