package com.intellij.uiDesigner.compiler;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.lw.ComponentVisitor;
import com.intellij.uiDesigner.lw.IComponent;
import com.intellij.uiDesigner.lw.IContainer;
import com.intellij.uiDesigner.lw.LwNestedForm;
import com.intellij.uiDesigner.lw.LwRootContainer;
import com.intellij.uiDesigner.lw.PropertiesProvider;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.LayoutManager;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;















public final class Utils
{
  public static final String FORM_NAMESPACE = "http://www.intellij.com/uidesigner/form/";
  private static final SAXParser SAX_PARSER = createParser();



  
  private static SAXParser createParser() {
    try {
      return SAXParserFactory.newInstance().newSAXParser();
    }
    catch (Exception e) {
      return null;
    } 
  }



  
  public static LwRootContainer getRootContainer(String formFileContent, PropertiesProvider provider) throws Exception {
    if (formFileContent.indexOf("http://www.intellij.com/uidesigner/form/") == -1) {
      throw new AlienFormFileException();
    }
    
    Document document = (new SAXBuilder()).build(new StringReader(formFileContent), "UTF-8");
    
    LwRootContainer root = new LwRootContainer();
    root.read(document.getRootElement(), provider);
    
    return root;
  }
  
  public static LwRootContainer getRootContainer(InputStream stream, PropertiesProvider provider) throws Exception {
    Document document = (new SAXBuilder()).build(stream, "UTF-8");
    
    LwRootContainer root = new LwRootContainer();
    root.read(document.getRootElement(), provider);
    
    return root;
  }
  
  public static synchronized String getBoundClassName(String formFileContent) throws Exception {
    if (formFileContent.indexOf("http://www.intellij.com/uidesigner/form/") == -1) {
      throw new AlienFormFileException();
    }
    
    String[] className = { null };
    try {
      SAX_PARSER.parse(new InputSource(new StringReader(formFileContent)), new DefaultHandler(className)
          {
            private final String[] val$className;

            
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
              if ("form".equals(qName)) {
                this.val$className[0] = attributes.getValue("", "bind-to-class");
                throw new SAXException("stop parsing");
              }
            
            }
          });
    } catch (Exception e) {}


    
    return className[0];
  }






  
  public static String validateJComponentClass(ClassLoader loader, String className, boolean validateConstructor) {
    Class aClass;
    if (loader == null) {
      throw new IllegalArgumentException("loader cannot be null");
    }
    if (className == null) {
      throw new IllegalArgumentException("className cannot be null");
    }

    
    if ("com.intellij.uiDesigner.HSpacer".equals(className) || "com.intellij.uiDesigner.VSpacer".equals(className))
    {

      
      return null;
    }

    
    try {
      aClass = Class.forName(className, true, loader);
    }
    catch (ClassNotFoundException exc) {
      return "Class \"" + className + "\"not found";
    }
    catch (NoClassDefFoundError exc) {
      return "Cannot load class " + className + ": " + exc.getMessage();
    }
    catch (ExceptionInInitializerError exc) {
      return "Cannot initialize class " + className + ": " + exc.getMessage();
    }
    catch (UnsupportedClassVersionError exc) {
      return "Unsupported class version error: " + className;
    } 
    
    if (validateConstructor) {
      try {
        Constructor constructor = aClass.getConstructor(new Class[0]);
        if ((constructor.getModifiers() & 0x1) == 0) {
          return "Class \"" + className + "\" does not have default public constructor";
        }
      }
      catch (Exception exc) {
        return "Class \"" + className + "\" does not have default constructor";
      } 
    }


    
    if (!JComponent.class.isAssignableFrom(aClass)) {
      return "Class \"" + className + "\" is not an instance of javax.swing.JComponent";
    }
    
    return null;
  }

  
  public static void validateNestedFormLoop(String formName, NestedFormLoader nestedFormLoader) throws CodeGenerationException, RecursiveFormNestingException {
    validateNestedFormLoop(formName, nestedFormLoader, (String)null);
  }

  
  public static void validateNestedFormLoop(String formName, NestedFormLoader nestedFormLoader, String targetForm) throws CodeGenerationException, RecursiveFormNestingException {
    HashSet usedFormNames = new HashSet();
    if (targetForm != null) {
      usedFormNames.add(targetForm);
    }
    validateNestedFormLoop(usedFormNames, formName, nestedFormLoader);
  }
  
  private static void validateNestedFormLoop(Set usedFormNames, String formName, NestedFormLoader nestedFormLoader) throws CodeGenerationException, RecursiveFormNestingException {
    LwRootContainer rootContainer;
    if (usedFormNames.contains(formName)) {
      throw new RecursiveFormNestingException();
    }
    usedFormNames.add(formName);
    
    try {
      rootContainer = nestedFormLoader.loadForm(formName);
    }
    catch (Exception e) {
      throw new CodeGenerationException(null, "Error loading nested form: " + e.getMessage());
    } 
    Set thisFormNestedForms = new HashSet();
    CodeGenerationException[] validateExceptions = new CodeGenerationException[1];
    RecursiveFormNestingException[] recursiveNestingExceptions = new RecursiveFormNestingException[1];
    rootContainer.accept(new ComponentVisitor(thisFormNestedForms, usedFormNames, nestedFormLoader, recursiveNestingExceptions, validateExceptions) { private final Set val$thisFormNestedForms; private final Set val$usedFormNames;
          public boolean visit(IComponent component) {
            if (component instanceof LwNestedForm) {
              LwNestedForm nestedForm = (LwNestedForm)component;
              if (!this.val$thisFormNestedForms.contains(nestedForm.getFormFileName())) {
                this.val$thisFormNestedForms.add(nestedForm.getFormFileName());
                try {
                  Utils.validateNestedFormLoop(this.val$usedFormNames, nestedForm.getFormFileName(), this.val$nestedFormLoader);
                }
                catch (RecursiveFormNestingException e) {
                  this.val$recursiveNestingExceptions[0] = e;
                  return false;
                }
                catch (CodeGenerationException e) {
                  this.val$validateExceptions[0] = e;
                  return false;
                } 
              } 
            } 
            return true;
          } private final NestedFormLoader val$nestedFormLoader; private final RecursiveFormNestingException[] val$recursiveNestingExceptions; private final CodeGenerationException[] val$validateExceptions; }
      );
    if (recursiveNestingExceptions[0] != null) {
      throw recursiveNestingExceptions[0];
    }
    if (validateExceptions[0] != null) {
      throw validateExceptions[0];
    }
  }
  
  public static String findNotEmptyPanelWithXYLayout(IComponent component) {
    if (!(component instanceof IContainer)) {
      return null;
    }
    IContainer container = (IContainer)component;
    if (container.getComponentCount() == 0) {
      return null;
    }
    if (container.isXY()) {
      return container.getId();
    }
    for (int i = 0; i < container.getComponentCount(); i++) {
      String id = findNotEmptyPanelWithXYLayout(container.getComponent(i));
      if (id != null) {
        return id;
      }
    } 
    return null;
  }
  
  public static int getHGap(LayoutManager layout) {
    if (layout instanceof BorderLayout) {
      return ((BorderLayout)layout).getHgap();
    }
    if (layout instanceof CardLayout) {
      return ((CardLayout)layout).getHgap();
    }
    return 0;
  }
  
  public static int getVGap(LayoutManager layout) {
    if (layout instanceof BorderLayout) {
      return ((BorderLayout)layout).getVgap();
    }
    if (layout instanceof CardLayout) {
      return ((CardLayout)layout).getVgap();
    }
    return 0;
  }
  
  public static int getCustomCreateComponentCount(IContainer container) {
    int[] result = new int[1];
    result[0] = 0;
    container.accept(new ComponentVisitor(result) { private final int[] val$result;
          public boolean visit(IComponent c) {
            if (c.isCustomCreate()) {
              this.val$result[0] = this.val$result[0] + 1;
            }
            return true;
          } }
      );
    return result[0];
  }
  
  public static Class suggestReplacementClass(Class componentClass) {
    while (true) {
      componentClass = componentClass.getSuperclass();
      if (componentClass.equals(JComponent.class)) {
        return JPanel.class;
      }
      if ((componentClass.getModifiers() & 0x402) != 0) {
        continue;
      }
      try {
        componentClass.getConstructor(new Class[0]);
        break;
      } catch (NoSuchMethodException ex) {}
    } 
    
    return componentClass;
  }

  
  public static int alignFromConstraints(GridConstraints gc, boolean horizontal) {
    int anchor = gc.getAnchor();
    int fill = gc.getFill();
    int leftMask = horizontal ? 8 : 1;
    int rightMask = horizontal ? 4 : 2;
    int fillMask = horizontal ? 1 : 2;
    if ((fill & fillMask) != 0) return 3; 
    if ((anchor & rightMask) != 0) return 2; 
    if ((anchor & leftMask) != 0) return 0; 
    return 1;
  }
  
  public static boolean isBoundField(IComponent component, String fieldName) {
    if (fieldName.equals(component.getBinding())) {
      return true;
    }
    if (component instanceof IContainer) {
      IContainer container = (IContainer)component;
      for (int i = 0; i < container.getComponentCount(); i++) {
        if (isBoundField(container.getComponent(i), fieldName)) {
          return true;
        }
      } 
    } 
    return false;
  }
}
