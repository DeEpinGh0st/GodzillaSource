package core;
import core.annotation.I18NAction;
import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.MenuElement;
import util.functions;

public class EasyI18N {
  private static final HashMap<Class<?>, Method> actionMap = new HashMap<>(); public static final String SETING_KETY = "language";
  private static final Class[] parameterTypes = new Class[] { Object.class, Field.class };
  private static final Locale language = new Locale(Db.getSetingValue("language", "zh".equalsIgnoreCase(Locale.getDefault().getLanguage()) ? "zh" : "en"));
  private static final ResourceBundle bundle = ResourceBundle.getBundle("godzilla", language);
  
  static {
    try {
      Method[] methods = EasyI18N.class.getDeclaredMethods();
      for (Method method : methods) {
        if (Modifier.isStatic(method.getModifiers()) && Arrays.equals((Object[])parameterTypes, (Object[])method.getParameterTypes())) {
          I18NAction action = method.<I18NAction>getDeclaredAnnotation(I18NAction.class);
          if (action != null) {
            actionMap.put(action.targetClass(), method);
          }
        } 
      } 
    } catch (Exception exception) {}
  }

  
  public static void installObject(Object obj) {
    try {
      Class<?> objClass = obj.getClass();
      while (objClass != null && (!objClass.getName().startsWith("java") || !objClass.getName().startsWith("sun"))) {
        try {
          Field[] fields = objClass.getDeclaredFields();
          Method actionMethod = null;
          for (Field field : fields) {
            if (field.getAnnotation(NoI18N.class) == null) {
              field.setAccessible(true);
              actionMethod = findAction(field.getType());
              if (actionMethod != null) {
                actionMethod.setAccessible(true);
                actionMethod.invoke((Object)null, new Object[] { obj, field });
              } 
            } 
          } 
          if (objClass.getAnnotation(NoI18N.class) == null) {
            actionMethod = findAction(objClass);
            if (actionMethod != null) {
              actionMethod.setAccessible(true);
              actionMethod.invoke((Object)null, new Object[] { obj, null });
            } 
          } 
        } catch (Exception exception) {}

        
        objClass = objClass.getSuperclass();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static String getI18nString(String format, Object... args) {
    return String.format(getI18nString(format), args);
  }
  public static String getI18nString(String key) {
    if ("zh".equals(language.getLanguage())) {
      return key;
    }
    if (key != null) {
      String value = null;
      try {
        value = bundle.getString(key.trim().replace("\r\n", "\\r\\n").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"));
        if (value != null) {
          value = value.replace("\\r\\n", "\r\n").replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
        }
      } catch (Exception exception) {}

      
      return (value == null) ? key : value;
    } 
    return null;
  }

  
  private static Method findAction(Class fieldType) {
    Method action = findAction(fieldType, true);
    if (action == null) {
      action = findAction(fieldType, false);
    }
    return action;
  }
  private static Method findAction(Class<?> fieldType, boolean comparisonThis) {
    if (comparisonThis) {
      Iterator<Class<?>> keys = actionMap.keySet().iterator();
      while (keys.hasNext()) {
        Class clazz = keys.next();
        if (fieldType.equals(clazz)) {
          return actionMap.get(clazz);
        }
      } 
    } else {
      Iterator<Class<?>> keys = actionMap.keySet().iterator();
      while (keys.hasNext()) {
        Class clazz = keys.next();
        if (clazz.isAssignableFrom(fieldType)) {
          return actionMap.get(clazz);
        }
      } 
    } 
    return null;
  }
  
  @I18NAction(targetClass = JLabel.class)
  public static void installJLabel(Object obj, Field targetField) throws Throwable {
    JLabel label = (JLabel)targetField.get(obj);
    if (label != null) {
      label.setText(getI18nString(label.getText()));
    } else {
      targetField.set(obj, new JLabel(targetField.getName()));
    } 
  }
  
  @I18NAction(targetClass = JMenu.class)
  public static void installJMenu(Object obj, Field targetField) throws Throwable {
    JMenu menu = (JMenu)targetField.get(obj);
    menu.setText(getI18nString(menu.getText()));
    int itemCount = menu.getItemCount();
    for (int i = 0; i < itemCount; i++) {
      JMenuItem menuItem = menu.getItem(i);
      menuItem.setText(getI18nString(menuItem.getText()));
    } 
  }
  @I18NAction(targetClass = JTabbedPane.class)
  public static void installJTabbedPane(Object obj, Field targetField) throws Throwable {
    JTabbedPane tabbedPane = (JTabbedPane)targetField.get(obj);
    int itemCount = tabbedPane.getTabCount();
    for (int i = 0; i < itemCount; i++) {
      String title = tabbedPane.getTitleAt(i);
      if (title != null)
        tabbedPane.setTitleAt(i, getI18nString(title)); 
    } 
  }
  
  @I18NAction(targetClass = JPopupMenu.class)
  public static void installJPopupMenu(Object obj, Field targetField) throws Throwable {
    JPopupMenu popupMenu = (JPopupMenu)targetField.get(obj);
    MenuElement[] menuElements = popupMenu.getSubElements();
    for (MenuElement menuElement : menuElements) {
      if (menuElement instanceof JMenuItem) {
        JMenuItem menuItem = (JMenuItem)menuElement;
        menuItem.setText(getI18nString(menuItem.getText()));
      } 
    } 
  }
  @I18NAction(targetClass = JButton.class)
  public static void installJButton(Object obj, Field targetField) throws Throwable {
    JButton button = (JButton)targetField.get(obj);
    if (button != null)
      button.setText(getI18nString(button.getText())); 
  }
  
  @I18NAction(targetClass = JCheckBox.class)
  public static void installJCheckBox(Object obj, Field targetField) throws Throwable {
    JCheckBox checkBox = (JCheckBox)targetField.get(obj);
    if (checkBox != null)
      checkBox.setText(getI18nString(checkBox.getText())); 
  }
  
  @I18NAction(targetClass = JComponent.class)
  public static void installJComponent(Object obj, Field targetField) throws Throwable {
    JComponent component = null;
    if (targetField == null) {
      component = (JComponent)obj;
    } else {
      component = (JComponent)targetField.get(obj);
    } 
    if (component == null) {
      return;
    }
    Border border = component.getBorder();
    if (border instanceof TitledBorder) {
      TitledBorder titledBorder = (TitledBorder)border;
      titledBorder.setTitle(getI18nString(titledBorder.getTitle()));
    } 
    Method getTitleMethod = functions.getMethodByClass(component.getClass(), "getTitle", null);
    Method setTitleMethod = functions.getMethodByClass(component.getClass(), "setTitle", new Class[] { String.class });
    if (getTitleMethod != null && setTitleMethod != null) {
      getTitleMethod.setAccessible(true);
      setTitleMethod.setAccessible(true);
      String oldTitle = (String)getTitleMethod.invoke(obj, (Object[])null);
      if (oldTitle != null)
        setTitleMethod.invoke(obj, new Object[] { getI18nString(oldTitle) }); 
    } 
  }
  
  @I18NAction(targetClass = Window.class)
  public static void installWindow(Object obj, Field targetField) {
    try {
      Window component = null;
      if (targetField == null) {
        component = (Window)obj;
      } else {
        component = (Window)targetField.get(obj);
      } 
      Method getTitleMethod = functions.getMethodByClass(component.getClass(), "getTitle", null);
      Method setTitleMethod = functions.getMethodByClass(component.getClass(), "setTitle", new Class[] { String.class });
      if (getTitleMethod != null && setTitleMethod != null) {
        getTitleMethod.setAccessible(true);
        setTitleMethod.setAccessible(true);
        String oldTitle = (String)getTitleMethod.invoke(obj, (Object[])null);
        if (oldTitle != null) {
          setTitleMethod.invoke(obj, new Object[] { getI18nString(oldTitle) });
        }
      } 
    } catch (Exception exception) {}
  }
}
