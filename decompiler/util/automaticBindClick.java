package util;

import core.ui.component.annotation.ButtonToMenuItem;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;



public class automaticBindClick
{
  public static void bindButtonClick(final Object fieldClass, Object eventClass) {
    try {
      Field[] fields = fieldClass.getClass().getDeclaredFields();
      for (Field field : fields) {
        if (field.getType().isAssignableFrom(Button.class)) {
          field.setAccessible(true);
          Button fieldValue = (Button)field.get(fieldClass);
          String fieldName = field.getName();
          if (fieldValue != null) {
            try {
              final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", new Class[] { ActionEvent.class });
              method.setAccessible(true);
              if (method != null) {
                fieldValue.addActionListener(new ActionListener()
                    {
                      
                      public void actionPerformed(ActionEvent e)
                      {
                        try {
                          method.invoke(fieldClass, new Object[] { e });
                        } catch (Exception e1) {
                          Log.error(e1);
                        } 
                      }
                    });
              }
            } catch (NoSuchMethodException e) {
              
              Log.error(fieldName + "Click  未实现");
            }
          
          }
        } 
      } 
    } catch (Exception e) {
      
      e.printStackTrace();
    } 
  }


  
  public static void bindJButtonClick(Class fieldClass, Object fieldObject, Class eventClass, final Object eventObject) {
    try {
      Field[] fields = fieldClass.getDeclaredFields();
      for (Field field : fields) {
        if (field.getType().isAssignableFrom(JButton.class)) {
          field.setAccessible(true);
          JButton fieldValue = (JButton)field.get(fieldObject);
          String fieldName = field.getName();
          if (fieldValue != null) {
            try {
              final Method method = eventClass.getDeclaredMethod(fieldName + "Click", new Class[] { ActionEvent.class });
              method.setAccessible(true);
              if (method != null) {
                fieldValue.addActionListener(new ActionListener()
                    {
                      
                      public void actionPerformed(ActionEvent e)
                      {
                        try {
                          method.invoke(eventObject, new Object[] { e });
                        } catch (Exception e1) {
                          Log.error(e1);
                        } 
                      }
                    });
              }
            } catch (NoSuchMethodException e) {
              Log.error(fieldName + "Click  未实现");
            }
          
          }
        }
      
      } 
    } catch (Exception e) {
      
      e.printStackTrace();
    } 
  }
  public static void bindJButtonClick(Object fieldClass, Object eventClass) {
    bindJButtonClick(fieldClass.getClass(), fieldClass, eventClass.getClass(), eventClass);
  }
  public static void bindMenuItemClick(Object item, Map<String, Method> methodMap, Object eventClass) {
    MenuElement[] menuElements = ((MenuElement)item).getSubElements();
    if (methodMap == null) {
      methodMap = getMenuItemMethod(eventClass);
    }
    if (menuElements.length == 0) {
      if (item.getClass().isAssignableFrom(JMenuItem.class)) {
        Method method = methodMap.get(((JMenuItem)item).getActionCommand() + "MenuItemClick");
        addMenuItemClickEvent(item, method, eventClass);
      }
    
    }
    else {
      
      for (int i = 0; i < menuElements.length; i++) {
        MenuElement menuElement = menuElements[i];
        Class<?> itemClass = menuElement.getClass();
        if (itemClass.isAssignableFrom(JPopupMenu.class) || itemClass.isAssignableFrom(JMenu.class)) {
          bindMenuItemClick(menuElement, methodMap, eventClass);
        } else if (item.getClass().isAssignableFrom(JMenuItem.class)) {
          Method method = methodMap.get(((JMenuItem)menuElement).getActionCommand() + "MenuItemClick");
          addMenuItemClickEvent(menuElement, method, eventClass);
        } 
      } 
    } 
  }
  
  public static void bindButtonToMenuItem(final Object fieldClass, Object eventClass, Object menu) {
    try {
      if (JMenu.class.isAssignableFrom(menu.getClass()) || JPopupMenu.class.isAssignableFrom(menu.getClass())) {
        try {
          Field[] fields = fieldClass.getClass().getDeclaredFields();
          for (Field field : fields) {
            if (field.getType().isAssignableFrom(JButton.class)) {
              field.setAccessible(true);
              JButton fieldValue = (JButton)field.get(fieldClass);
              String fieldName = field.getName();
              if (fieldValue != null && field.isAnnotationPresent((Class)ButtonToMenuItem.class)) {
                ButtonToMenuItem buttonToMenuItem = field.<ButtonToMenuItem>getAnnotation(ButtonToMenuItem.class);
                try {
                  final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", new Class[] { ActionEvent.class });
                  method.setAccessible(true);
                  if (method != null) {
                    Method addMethod = menu.getClass().getMethod("add", new Class[] { JMenuItem.class });
                    
                    String menuItemName = fieldValue.getText();
                    
                    JMenuItem menuItem = new JMenuItem((buttonToMenuItem.name().length() > 0) ? buttonToMenuItem.name() : menuItemName);
                    menuItem.addActionListener(new ActionListener()
                        {
                          
                          public void actionPerformed(ActionEvent e)
                          {
                            try {
                              method.invoke(fieldClass, new Object[] { e });
                            } catch (Exception e1) {
                              Log.error(e1);
                            } 
                          }
                        });
                    addMethod.invoke(menu, new Object[] { menuItem });
                  } 
                } catch (NoSuchMethodException e) {
                  Log.error(fieldName + "Click  未实现");
                }
              
              }
            
            } 
          } 
        } catch (Exception e) {
          
          e.printStackTrace();
        }
      
      }
    } catch (Exception e) {
      Log.error(e);
    } 
  }

  
  private static Map<String, Method> getMenuItemMethod(Object eventClass) {
    Method[] methods = eventClass.getClass().getDeclaredMethods();

    
    Map<String, Method> methodMap = new HashMap<>();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(ActionEvent.class) && method.getReturnType().isAssignableFrom(void.class) && method.getName().endsWith("MenuItemClick")) {
        methodMap.put(method.getName(), method);
      }
    } 
    return methodMap;
  }
  private static void addMenuItemClickEvent(Object item, final Method method, final Object eventClass) {
    if (method != null && eventClass != null && item.getClass().isAssignableFrom(JMenuItem.class))
      ((JMenuItem)item).addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent paramActionEvent)
            {
              try {
                method.setAccessible(true);
                method.invoke(eventClass, new Object[] { paramActionEvent });
              } catch (Exception e) {
                e.printStackTrace();
              } 
            }
          }); 
  }
}
