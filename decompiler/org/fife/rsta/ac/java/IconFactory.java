package org.fife.rsta.ac.java;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;






















public class IconFactory
{
  public static final String SOURCE_FILE_ICON = "sourceFileIcon";
  public static final String PACKAGE_ICON = "packageIcon";
  public static final String IMPORT_ROOT_ICON = "importRootIcon";
  public static final String IMPORT_ICON = "importIcon";
  public static final String DEFAULT_CLASS_ICON = "defaultClassIcon";
  public static final String DEFAULT_INTERFACE_ICON = "defaultInterfaceIcon";
  public static final String CLASS_ICON = "classIcon";
  public static final String ENUM_ICON = "enumIcon";
  public static final String ENUM_PROTECTED_ICON = "enumProtectedIcon";
  public static final String ENUM_PRIVATE_ICON = "enumPrivateIcon";
  public static final String ENUM_DEFAULT_ICON = "enumDefaultIcon";
  public static final String INNER_CLASS_PUBLIC_ICON = "innerClassPublicIcon";
  public static final String INNER_CLASS_PROTECTED_ICON = "innerClassProtectedIcon";
  public static final String INNER_CLASS_PRIVATE_ICON = "innerClassPrivateIcon";
  public static final String INNER_CLASS_DEFAULT_ICON = "innerClassDefaultIcon";
  public static final String INTERFACE_ICON = "interfaceIcon";
  public static final String JAVADOC_ITEM_ICON = "javadocItemIcon";
  public static final String LOCAL_VARIABLE_ICON = "localVariableIcon";
  public static final String METHOD_PUBLIC_ICON = "methodPublicIcon";
  public static final String METHOD_PROTECTED_ICON = "methodProtectedIcon";
  public static final String METHOD_PRIVATE_ICON = "methodPrivateIcon";
  public static final String METHOD_DEFAULT_ICON = "methodDefaultIcon";
  public static final String TEMPLATE_ICON = "templateIcon";
  public static final String FIELD_PUBLIC_ICON = "fieldPublicIcon";
  public static final String FIELD_PROTECTED_ICON = "fieldProtectedIcon";
  public static final String FIELD_PRIVATE_ICON = "fieldPrivateIcon";
  public static final String FIELD_DEFAULT_ICON = "fieldDefaultIcon";
  public static final String CONSTRUCTOR_ICON = "constructorIcon";
  public static final String DEPRECATED_ICON = "deprecatedIcon";
  public static final String ABSTRACT_ICON = "abstractIcon";
  public static final String FINAL_ICON = "finalIcon";
  public static final String STATIC_ICON = "staticIcon";
  private Map<String, Icon> iconMap;
  private static final IconFactory INSTANCE = new IconFactory();


  
  private IconFactory() {
    this.iconMap = new HashMap<>();
    this.iconMap.put("sourceFileIcon", loadIcon("jcu_obj.gif"));
    this.iconMap.put("packageIcon", loadIcon("package_obj.gif"));
    this.iconMap.put("importRootIcon", loadIcon("impc_obj.gif"));
    this.iconMap.put("importIcon", loadIcon("imp_obj.gif"));
    this.iconMap.put("defaultClassIcon", loadIcon("class_default_obj.gif"));
    this.iconMap.put("defaultInterfaceIcon", loadIcon("int_default_obj.gif"));
    this.iconMap.put("classIcon", loadIcon("class_obj.gif"));
    this.iconMap.put("enumIcon", loadIcon("enum_obj.gif"));
    this.iconMap.put("enumProtectedIcon", loadIcon("enum_protected_obj.gif"));
    this.iconMap.put("enumPrivateIcon", loadIcon("enum_private_obj.gif"));
    this.iconMap.put("enumDefaultIcon", loadIcon("enum_default_obj.gif"));
    this.iconMap.put("innerClassPublicIcon", loadIcon("innerclass_public_obj.gif"));
    this.iconMap.put("innerClassProtectedIcon", loadIcon("innerclass_protected_obj.gif"));
    this.iconMap.put("innerClassPrivateIcon", loadIcon("innerclass_private_obj.gif"));
    this.iconMap.put("innerClassDefaultIcon", loadIcon("innerclass_default_obj.gif"));
    this.iconMap.put("interfaceIcon", loadIcon("int_obj.gif"));
    this.iconMap.put("javadocItemIcon", loadIcon("jdoc_tag_obj.gif"));
    this.iconMap.put("localVariableIcon", loadIcon("localvariable_obj.gif"));
    this.iconMap.put("methodPublicIcon", loadIcon("methpub_obj.gif"));
    this.iconMap.put("methodProtectedIcon", loadIcon("methpro_obj.gif"));
    this.iconMap.put("methodPrivateIcon", loadIcon("methpri_obj.gif"));
    this.iconMap.put("methodDefaultIcon", loadIcon("methdef_obj.gif"));
    this.iconMap.put("templateIcon", loadIcon("template_obj.gif"));
    this.iconMap.put("fieldPublicIcon", loadIcon("field_public_obj.gif"));
    this.iconMap.put("fieldProtectedIcon", loadIcon("field_protected_obj.gif"));
    this.iconMap.put("fieldPrivateIcon", loadIcon("field_private_obj.gif"));
    this.iconMap.put("fieldDefaultIcon", loadIcon("field_default_obj.gif"));
    
    this.iconMap.put("constructorIcon", loadIcon("constr_ovr.gif"));
    this.iconMap.put("deprecatedIcon", loadIcon("deprecated.gif"));
    this.iconMap.put("abstractIcon", loadIcon("abstract_co.gif"));
    this.iconMap.put("finalIcon", loadIcon("final_co.gif"));
    this.iconMap.put("staticIcon", loadIcon("static_co.gif"));
  }


  
  public static IconFactory get() {
    return INSTANCE;
  }

  
  public Icon getIcon(String key) {
    return getIcon(key, false);
  }

  
  public Icon getIcon(String key, boolean deprecated) {
    Icon icon = this.iconMap.get(key);
    if (deprecated) {
      DecoratableIcon di = new DecoratableIcon(16, icon);
      di.setDeprecated(deprecated);
      icon = di;
    } 
    return icon;
  }


  
  public Icon getIcon(IconData data) {
    DecoratableIcon icon = new DecoratableIcon(16, getIcon(data.getIcon()));
    icon.setDeprecated(data.isDeprecated());
    if (data.isAbstract()) {
      icon.addDecorationIcon(getIcon("abstractIcon"));
    }
    if (data.isStatic()) {
      icon.addDecorationIcon(getIcon("staticIcon"));
    }
    if (data.isFinal()) {
      icon.addDecorationIcon(getIcon("finalIcon"));
    }
    return icon;
  }

  
  private Icon loadIcon(String name) {
    URL res = getClass().getResource("img/" + name);
    if (res == null)
    {

      
      throw new IllegalArgumentException("icon not found: img/" + name);
    }
    return new ImageIcon(res);
  }
  
  public static interface IconData {
    String getIcon();
    
    boolean isAbstract();
    
    boolean isDeprecated();
    
    boolean isFinal();
    
    boolean isStatic();
  }
}
