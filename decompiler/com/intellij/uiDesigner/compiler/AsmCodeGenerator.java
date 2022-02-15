package com.intellij.uiDesigner.compiler;
import com.intellij.uiDesigner.lw.FontDescriptor;
import com.intellij.uiDesigner.lw.IButtonGroup;
import com.intellij.uiDesigner.lw.LwComponent;
import com.intellij.uiDesigner.lw.LwContainer;
import com.intellij.uiDesigner.lw.LwIntrospectedProperty;
import com.intellij.uiDesigner.lw.LwNestedForm;
import com.intellij.uiDesigner.lw.LwRootContainer;
import com.intellij.uiDesigner.lw.StringDescriptor;
import com.intellij.uiDesigner.shared.BorderType;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.border.Border;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class AsmCodeGenerator {
  private LwRootContainer myRootContainer;
  private Map myIdToLocalMap = new HashMap(); private ClassLoader myLoader;
  private ArrayList myErrors;
  private ArrayList myWarnings;
  private static final String CONSTRUCTOR_NAME = "<init>";
  private String myClassToBind;
  private byte[] myPatchedData;
  private static Map myContainerLayoutCodeGenerators = new HashMap();
  private static Map myComponentLayoutCodeGenerators = new HashMap();
  private static Map myPropertyCodeGenerators = new HashMap();
  
  public static final String SETUP_METHOD_NAME = "$$$setupUI$$$";
  public static final String GET_ROOT_COMPONENT_METHOD_NAME = "$$$getRootComponent$$$";
  public static final String CREATE_COMPONENTS_METHOD_NAME = "createUIComponents";
  public static final String LOAD_LABEL_TEXT_METHOD = "$$$loadLabelText$$$";
  public static final String LOAD_BUTTON_TEXT_METHOD = "$$$loadButtonText$$$";
  private static final Type ourButtonGroupType = Type.getType(ButtonGroup.class);
  private static final Type ourBorderFactoryType = Type.getType(BorderFactory.class);
  private static final Type ourBorderType = Type.getType(Border.class);
  private static final Method ourCreateTitledBorderMethod = Method.getMethod("javax.swing.border.TitledBorder createTitledBorder(javax.swing.border.Border,java.lang.String,int,int,java.awt.Font,java.awt.Color)"); private NestedFormLoader myFormLoader; private final boolean myIgnoreCustomCreation; private final ClassWriter myClassWriter; static Class class$javax$swing$JComponent; static Class class$java$lang$Integer; static Class class$java$lang$Boolean; static Class class$java$lang$Double; static Class class$java$lang$Float;
  static Class class$java$lang$Long;
  static Class class$java$lang$Byte;
  static Class class$java$lang$Short;
  static Class class$java$lang$Character;
  static Class class$java$lang$Object;
  
  static {
    myContainerLayoutCodeGenerators.put("GridLayoutManager", new GridLayoutCodeGenerator());
    myContainerLayoutCodeGenerators.put("GridBagLayout", new GridBagLayoutCodeGenerator());
    myContainerLayoutCodeGenerators.put("BorderLayout", new SimpleLayoutCodeGenerator(Type.getType(BorderLayout.class)));
    myContainerLayoutCodeGenerators.put("CardLayout", new SimpleLayoutCodeGenerator(Type.getType(CardLayout.class)));
    myContainerLayoutCodeGenerators.put("FlowLayout", new FlowLayoutCodeGenerator());
    
    myComponentLayoutCodeGenerators.put(LwSplitPane.class, new SplitPaneLayoutCodeGenerator());
    myComponentLayoutCodeGenerators.put(LwTabbedPane.class, new TabbedPaneLayoutCodeGenerator());
    myComponentLayoutCodeGenerators.put(LwScrollPane.class, new ScrollPaneLayoutCodeGenerator());
    myComponentLayoutCodeGenerators.put(LwToolBar.class, new ToolBarLayoutCodeGenerator());
    
    myPropertyCodeGenerators.put(String.class.getName(), new StringPropertyCodeGenerator());
    myPropertyCodeGenerators.put(Dimension.class.getName(), new DimensionPropertyCodeGenerator());
    myPropertyCodeGenerators.put(Insets.class.getName(), new InsetsPropertyCodeGenerator());
    myPropertyCodeGenerators.put(Rectangle.class.getName(), new RectanglePropertyCodeGenerator());
    myPropertyCodeGenerators.put(Color.class.getName(), new ColorPropertyCodeGenerator());
    myPropertyCodeGenerators.put(Font.class.getName(), new FontPropertyCodeGenerator());
    myPropertyCodeGenerators.put(Icon.class.getName(), new IconPropertyCodeGenerator());
    myPropertyCodeGenerators.put(ListModel.class.getName(), new ListModelPropertyCodeGenerator(DefaultListModel.class));
    myPropertyCodeGenerators.put(ComboBoxModel.class.getName(), new ListModelPropertyCodeGenerator(DefaultComboBoxModel.class));
    myPropertyCodeGenerators.put("java.lang.Enum", new EnumPropertyCodeGenerator());
  }




  
  public AsmCodeGenerator(LwRootContainer rootContainer, ClassLoader loader, NestedFormLoader formLoader, boolean ignoreCustomCreation, ClassWriter classWriter) {
    this.myFormLoader = formLoader;
    this.myIgnoreCustomCreation = ignoreCustomCreation;
    if (loader == null) {
      throw new IllegalArgumentException("loader cannot be null");
    }
    if (rootContainer == null) {
      throw new IllegalArgumentException("rootContainer cannot be null");
    }
    this.myRootContainer = rootContainer;
    this.myLoader = loader;
    
    this.myErrors = new ArrayList();
    this.myWarnings = new ArrayList();
    this.myClassWriter = classWriter;
  }
  
  public void patchFile(File classFile) {
    if (!classFile.exists()) {
      this.myErrors.add(new FormErrorInfo(null, "Class to bind does not exist: " + this.myRootContainer.getClassToBind()));
      
      return;
    } 
    
    try {
      byte[] patchedData;
      FileInputStream fis = new FileInputStream(classFile);
      try {
        patchedData = patchClass(fis);
        if (patchedData == null) {
          return;
        }
      } finally {
        
        fis.close();
      } 
      
      FileOutputStream fos = new FileOutputStream(classFile);
      try {
        fos.write(patchedData);
      } finally {
        
        fos.close();
      }
    
    } catch (IOException e) {
      this.myErrors.add(new FormErrorInfo(null, "Cannot read or write class file " + classFile.getPath() + ": " + e.toString()));
    }
    catch (IllegalStateException e) {
      this.myErrors.add(new FormErrorInfo(null, "Unexpected data in form file when patching class " + classFile.getPath() + ": " + e.toString()));
    } 
  }
  public byte[] patchClass(InputStream classStream) {
    ClassReader reader;
    this.myClassToBind = this.myRootContainer.getClassToBind();
    if (this.myClassToBind == null) {
      this.myWarnings.add(new FormErrorInfo(null, "No class to bind specified"));
      return null;
    } 
    
    if (this.myRootContainer.getComponentCount() != 1) {
      this.myErrors.add(new FormErrorInfo(null, "There should be only one component at the top level"));
      return null;
    } 
    
    String nonEmptyPanel = Utils.findNotEmptyPanelWithXYLayout(this.myRootContainer.getComponent(0));
    if (nonEmptyPanel != null) {
      this.myErrors.add(new FormErrorInfo(nonEmptyPanel, "There are non empty panels with XY layout. Please lay them out in a grid."));
      
      return null;
    } 

    
    try {
      reader = new ClassReader(classStream);
    }
    catch (IOException e) {
      this.myErrors.add(new FormErrorInfo(null, "Error reading class data stream"));
      return null;
    } 
    
    FirstPassClassVisitor visitor = new FirstPassClassVisitor();
    reader.accept((ClassVisitor)visitor, 0);
    
    reader.accept((ClassVisitor)new FormClassVisitor(this, (ClassVisitor)this.myClassWriter, visitor.isExplicitSetupCall()), 0);
    this.myPatchedData = this.myClassWriter.toByteArray();
    return this.myPatchedData;
  }
  
  public FormErrorInfo[] getErrors() {
    return (FormErrorInfo[])this.myErrors.toArray((Object[])new FormErrorInfo[this.myErrors.size()]);
  }
  
  public FormErrorInfo[] getWarnings() {
    return (FormErrorInfo[])this.myWarnings.toArray((Object[])new FormErrorInfo[this.myWarnings.size()]);
  }
  
  public byte[] getPatchedData() {
    return this.myPatchedData;
  }
  
  static void pushPropValue(GeneratorAdapter generator, String propertyClass, Object value) {
    PropertyCodeGenerator codeGen = (PropertyCodeGenerator)myPropertyCodeGenerators.get(propertyClass);
    if (codeGen == null) {
      throw new RuntimeException("Unknown property class " + propertyClass);
    }
    codeGen.generatePushValue(generator, value);
  }
  
  static Class getComponentClass(String className, ClassLoader classLoader) throws CodeGenerationException {
    try {
      return Class.forName(className, false, classLoader);
    }
    catch (ClassNotFoundException e) {
      throw new CodeGenerationException(null, "Class not found: " + className);
    }
    catch (UnsupportedClassVersionError e) {
      throw new CodeGenerationException(null, "Unsupported class version error: " + className);
    } 
  }
  
  public static Type typeFromClassName(String className) {
    return Type.getType("L" + className.replace('.', '/') + ";");
  }
  
  class FormClassVisitor extends ClassAdapter { private String myClassName;
    private String mySuperName;
    private Map myFieldDescMap;
    private Map myFieldAccessMap;
    private boolean myHaveCreateComponentsMethod;
    private int myCreateComponentsAccess;
    private final boolean myExplicitSetupCall;
    private final AsmCodeGenerator this$0;
    
    public FormClassVisitor(AsmCodeGenerator this$0, ClassVisitor cv, boolean explicitSetupCall) {
      super(cv); this.this$0 = this$0; this.myFieldDescMap = new HashMap(); this.myFieldAccessMap = new HashMap(); this.myHaveCreateComponentsMethod = false;
      this.myExplicitSetupCall = explicitSetupCall;
    }





    
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
      this.myClassName = name;
      this.mySuperName = superName;
      
      for (Iterator iterator = AsmCodeGenerator.myPropertyCodeGenerators.values().iterator(); iterator.hasNext(); ) {
        PropertyCodeGenerator propertyCodeGenerator = iterator.next();
        propertyCodeGenerator.generateClassStart(this, name, this.this$0.myLoader);
      } 
    }
    
    public String getClassName() {
      return this.myClassName;
    }





    
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      if (name.equals("$$$setupUI$$$") || name.equals("$$$getRootComponent$$$") || name.equals("$$$loadButtonText$$$") || name.equals("$$$loadLabelText$$$"))
      {
        return null;
      }
      if (name.equals("createUIComponents") && desc.equals("()V")) {
        this.myHaveCreateComponentsMethod = true;
        this.myCreateComponentsAccess = access;
      } 
      
      MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
      if (name.equals("<init>") && !this.myExplicitSetupCall) {
        return (MethodVisitor)new AsmCodeGenerator.FormConstructorVisitor(this.this$0, methodVisitor, this.myClassName, this.mySuperName);
      }
      return methodVisitor;
    }




    
    MethodVisitor visitNewMethod(int access, String name, String desc, String signature, String[] exceptions) {
      return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
      this.myFieldDescMap.put(name, desc);
      this.myFieldAccessMap.put(name, new Integer(access));
      return super.visitField(access, name, desc, signature, value);
    }
    
    public void visitEnd() {
      boolean haveCustomCreateComponents = (Utils.getCustomCreateComponentCount((IContainer)this.this$0.myRootContainer) > 0 && !this.this$0.myIgnoreCustomCreation);
      
      if (haveCustomCreateComponents && !this.myHaveCreateComponentsMethod) {
        this.this$0.myErrors.add(new FormErrorInfo(null, "Form contains components with Custom Create option but no createUIComponents() method"));
      }
      
      Method method = Method.getMethod("void $$$setupUI$$$ ()");
      GeneratorAdapter generator = new GeneratorAdapter(4098, method, null, null, this.cv);
      if (haveCustomCreateComponents && this.myHaveCreateComponentsMethod) {
        generator.visitVarInsn(25, 0);
        int opcode = (this.myCreateComponentsAccess == 2) ? 183 : 182;
        generator.visitMethodInsn(opcode, this.myClassName, "createUIComponents", "()V");
      } 
      buildSetupMethod(generator);
      
      String rootBinding = this.this$0.myRootContainer.getComponent(0).getBinding();
      if (rootBinding != null && this.myFieldDescMap.containsKey(rootBinding)) {
        buildGetRootComponenMethod();
      }
      
      for (Iterator iterator = AsmCodeGenerator.myPropertyCodeGenerators.values().iterator(); iterator.hasNext(); ) {
        PropertyCodeGenerator propertyCodeGenerator = iterator.next();
        propertyCodeGenerator.generateClassEnd(this);
      } 
      
      super.visitEnd();
    }
    
    private void buildGetRootComponenMethod() {
      Type componentType = Type.getType((AsmCodeGenerator.class$javax$swing$JComponent == null) ? (AsmCodeGenerator.class$javax$swing$JComponent = AsmCodeGenerator.class$("javax.swing.JComponent")) : AsmCodeGenerator.class$javax$swing$JComponent);
      Method method = new Method("$$$getRootComponent$$$", componentType, new Type[0]);
      GeneratorAdapter generator = new GeneratorAdapter(4097, method, null, null, this.cv);
      
      LwComponent topComponent = (LwComponent)this.this$0.myRootContainer.getComponent(0);
      String binding = topComponent.getBinding();
      
      generator.loadThis();
      generator.getField(AsmCodeGenerator.typeFromClassName(this.myClassName), binding, Type.getType((String)this.myFieldDescMap.get(binding)));
      
      generator.returnValue();
      generator.endMethod();
    }
    
    private void buildSetupMethod(GeneratorAdapter generator) {
      try {
        LwComponent topComponent = (LwComponent)this.this$0.myRootContainer.getComponent(0);
        generateSetupCodeForComponent(topComponent, generator, -1);
        generateComponentReferenceProperties(topComponent, generator);
        generateButtonGroups(this.this$0.myRootContainer, generator);
      }
      catch (CodeGenerationException e) {
        this.this$0.myErrors.add(new FormErrorInfo(e.getComponentId(), e.getMessage()));
      } 
      generator.returnValue();
      generator.endMethod();
    }



    
    private void generateSetupCodeForComponent(LwComponent lwComponent, GeneratorAdapter generator, int parentLocal) throws CodeGenerationException {
      String className;
      if (lwComponent instanceof LwNestedForm) {
        LwRootContainer nestedFormContainer;
        LwNestedForm nestedForm = (LwNestedForm)lwComponent;
        if (this.this$0.myFormLoader == null) {
          throw new CodeGenerationException(null, "Attempt to compile nested form with no nested form loader specified");
        }
        try {
          nestedFormContainer = this.this$0.myFormLoader.loadForm(nestedForm.getFormFileName());
        }
        catch (Exception e) {
          throw new CodeGenerationException(lwComponent.getId(), e.getMessage());
        } 
        
        if (nestedFormContainer.getComponentCount() == 0) {
          return;
        }
        if (nestedFormContainer.getComponent(0).getBinding() == null) {
          throw new CodeGenerationException(lwComponent.getId(), "No binding on root component of nested form " + nestedForm.getFormFileName());
        }
        try {
          Utils.validateNestedFormLoop(nestedForm.getFormFileName(), this.this$0.myFormLoader);
        }
        catch (RecursiveFormNestingException e) {
          throw new CodeGenerationException(lwComponent.getId(), "Recursive form nesting is not allowed");
        } 
        className = this.this$0.myFormLoader.getClassToBindName(nestedFormContainer);
      } else {
        
        className = getComponentCodeGenerator(lwComponent.getParent()).mapComponentClass(lwComponent.getComponentClassName());
      } 
      Type componentType = AsmCodeGenerator.typeFromClassName(className);
      int componentLocal = generator.newLocal(componentType);
      
      this.this$0.myIdToLocalMap.put(lwComponent.getId(), new Integer(componentLocal));
      
      Class componentClass = AsmCodeGenerator.getComponentClass(className, this.this$0.myLoader);
      validateFieldBinding(lwComponent, componentClass);
      
      if (this.this$0.myIgnoreCustomCreation) {
        boolean creatable = true;
        if ((componentClass.getModifiers() & 0x402) != 0) {
          creatable = false;
        } else {
          
          try {
            Constructor constructor = componentClass.getConstructor(new Class[0]);
            if ((constructor.getModifiers() & 0x1) == 0) {
              creatable = false;
            }
          }
          catch (NoSuchMethodException ex) {
            creatable = false;
          } 
        } 
        if (!creatable) {
          componentClass = Utils.suggestReplacementClass(componentClass);
          componentType = Type.getType(componentClass);
        } 
      } 
      
      if (!lwComponent.isCustomCreate() || this.this$0.myIgnoreCustomCreation) {
        generator.newInstance(componentType);
        generator.dup();
        generator.invokeConstructor(componentType, Method.getMethod("void <init>()"));
        generator.storeLocal(componentLocal);
        
        generateFieldBinding(lwComponent, generator, componentLocal);
      } else {
        
        String binding = lwComponent.getBinding();
        if (binding == null) {
          throw new CodeGenerationException(lwComponent.getId(), "Only components bound to fields can have custom creation code");
        }
        
        generator.loadThis();
        generator.getField(getMainClassType(), binding, Type.getType((String)this.myFieldDescMap.get(binding)));
        
        generator.storeLocal(componentLocal);
      } 
      
      if (lwComponent instanceof LwContainer) {
        LwContainer lwContainer = (LwContainer)lwComponent;
        if (!lwContainer.isCustomCreate() || lwContainer.getComponentCount() > 0) {
          getComponentCodeGenerator(lwContainer).generateContainerLayout(lwContainer, generator, componentLocal);
        }
      } 
      
      generateComponentProperties(lwComponent, componentClass, generator, componentLocal);

      
      if (!(lwComponent.getParent() instanceof LwRootContainer)) {
        LayoutCodeGenerator parentCodeGenerator = getComponentCodeGenerator(lwComponent.getParent());
        if (lwComponent instanceof LwNestedForm) {
          componentLocal = getNestedFormComponent(generator, componentClass, componentLocal);
        }
        parentCodeGenerator.generateComponentLayout(lwComponent, generator, componentLocal, parentLocal);
      } 
      
      if (lwComponent instanceof LwContainer) {
        LwContainer container = (LwContainer)lwComponent;
        
        generateBorder(container, generator, componentLocal);
        
        for (int i = 0; i < container.getComponentCount(); i++) {
          generateSetupCodeForComponent((LwComponent)container.getComponent(i), generator, componentLocal);
        }
      } 
    }
    
    private int getNestedFormComponent(GeneratorAdapter generator, Class componentClass, int formLocal) throws CodeGenerationException {
      Type componentType = Type.getType((AsmCodeGenerator.class$javax$swing$JComponent == null) ? (AsmCodeGenerator.class$javax$swing$JComponent = AsmCodeGenerator.class$("javax.swing.JComponent")) : AsmCodeGenerator.class$javax$swing$JComponent);
      int componentLocal = generator.newLocal(componentType);
      generator.loadLocal(formLocal);
      generator.invokeVirtual(Type.getType(componentClass), new Method("$$$getRootComponent$$$", componentType, new Type[0]));
      
      generator.storeLocal(componentLocal);
      return componentLocal;
    }
    
    private LayoutCodeGenerator getComponentCodeGenerator(LwContainer container) {
      LayoutCodeGenerator generator = (LayoutCodeGenerator)AsmCodeGenerator.myComponentLayoutCodeGenerators.get(container.getClass());
      if (generator != null) {
        return generator;
      }
      LwContainer parent = container;
      while (parent != null) {
        String layoutManager = parent.getLayoutManager();
        if (layoutManager != null && layoutManager.length() > 0) {
          if (layoutManager.equals("FormLayout") && !AsmCodeGenerator.myContainerLayoutCodeGenerators.containsKey("FormLayout"))
          {
            AsmCodeGenerator.myContainerLayoutCodeGenerators.put("FormLayout", new FormLayoutCodeGenerator());
          }
          generator = (LayoutCodeGenerator)AsmCodeGenerator.myContainerLayoutCodeGenerators.get(layoutManager);
          if (generator != null) {
            return generator;
          }
        } 
        parent = parent.getParent();
      } 
      return GridLayoutCodeGenerator.INSTANCE;
    }




    
    private void generateComponentProperties(LwComponent lwComponent, Class componentClass, GeneratorAdapter generator, int componentLocal) throws CodeGenerationException {
      LwIntrospectedProperty[] introspectedProperties = lwComponent.getAssignedIntrospectedProperties();
      for (int i = 0; i < introspectedProperties.length; i++) {
        Type setterArgType; LwIntrospectedProperty property = introspectedProperties[i];
        if (property instanceof com.intellij.uiDesigner.lw.LwIntroComponentProperty) {
          continue;
        }
        String propertyClass = property.getCodeGenPropertyClassName();
        if (this.this$0.myIgnoreCustomCreation) {
          try {
            Class setterClass;
            if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Integer == null) ? (AsmCodeGenerator.class$java$lang$Integer = AsmCodeGenerator.class$("java.lang.Integer")) : AsmCodeGenerator.class$java$lang$Integer).getName())) {
              setterClass = int.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Boolean == null) ? (AsmCodeGenerator.class$java$lang$Boolean = AsmCodeGenerator.class$("java.lang.Boolean")) : AsmCodeGenerator.class$java$lang$Boolean).getName())) {
              setterClass = boolean.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Double == null) ? (AsmCodeGenerator.class$java$lang$Double = AsmCodeGenerator.class$("java.lang.Double")) : AsmCodeGenerator.class$java$lang$Double).getName())) {
              setterClass = double.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Float == null) ? (AsmCodeGenerator.class$java$lang$Float = AsmCodeGenerator.class$("java.lang.Float")) : AsmCodeGenerator.class$java$lang$Float).getName())) {
              setterClass = float.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Long == null) ? (AsmCodeGenerator.class$java$lang$Long = AsmCodeGenerator.class$("java.lang.Long")) : AsmCodeGenerator.class$java$lang$Long).getName())) {
              setterClass = long.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Byte == null) ? (AsmCodeGenerator.class$java$lang$Byte = AsmCodeGenerator.class$("java.lang.Byte")) : AsmCodeGenerator.class$java$lang$Byte).getName())) {
              setterClass = byte.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Short == null) ? (AsmCodeGenerator.class$java$lang$Short = AsmCodeGenerator.class$("java.lang.Short")) : AsmCodeGenerator.class$java$lang$Short).getName())) {
              setterClass = short.class;
            }
            else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Character == null) ? (AsmCodeGenerator.class$java$lang$Character = AsmCodeGenerator.class$("java.lang.Character")) : AsmCodeGenerator.class$java$lang$Character).getName())) {
              setterClass = char.class;
            } else {
              
              setterClass = Class.forName(propertyClass);
            } 
            componentClass.getMethod(property.getWriteMethodName(), new Class[] { setterClass });
          }
          catch (Exception e) {
            continue;
          } 
        }
        PropertyCodeGenerator propGen = (PropertyCodeGenerator)AsmCodeGenerator.myPropertyCodeGenerators.get(propertyClass);
        
        if (propGen != null && propGen.generateCustomSetValue(lwComponent, componentClass, property, generator, componentLocal, this.myClassName)) {
          continue;
        }

        
        generator.loadLocal(componentLocal);
        
        Object value = lwComponent.getPropertyValue(property);
        
        if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Integer == null) ? (AsmCodeGenerator.class$java$lang$Integer = AsmCodeGenerator.class$("java.lang.Integer")) : AsmCodeGenerator.class$java$lang$Integer).getName())) {
          generator.push(((Integer)value).intValue());
          setterArgType = Type.INT_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Boolean == null) ? (AsmCodeGenerator.class$java$lang$Boolean = AsmCodeGenerator.class$("java.lang.Boolean")) : AsmCodeGenerator.class$java$lang$Boolean).getName())) {
          generator.push(((Boolean)value).booleanValue());
          setterArgType = Type.BOOLEAN_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Double == null) ? (AsmCodeGenerator.class$java$lang$Double = AsmCodeGenerator.class$("java.lang.Double")) : AsmCodeGenerator.class$java$lang$Double).getName())) {
          generator.push(((Double)value).doubleValue());
          setterArgType = Type.DOUBLE_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Float == null) ? (AsmCodeGenerator.class$java$lang$Float = AsmCodeGenerator.class$("java.lang.Float")) : AsmCodeGenerator.class$java$lang$Float).getName())) {
          generator.push(((Float)value).floatValue());
          setterArgType = Type.FLOAT_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Long == null) ? (AsmCodeGenerator.class$java$lang$Long = AsmCodeGenerator.class$("java.lang.Long")) : AsmCodeGenerator.class$java$lang$Long).getName())) {
          generator.push(((Long)value).longValue());
          setterArgType = Type.LONG_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Short == null) ? (AsmCodeGenerator.class$java$lang$Short = AsmCodeGenerator.class$("java.lang.Short")) : AsmCodeGenerator.class$java$lang$Short).getName())) {
          generator.push(((Short)value).intValue());
          setterArgType = Type.SHORT_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Byte == null) ? (AsmCodeGenerator.class$java$lang$Byte = AsmCodeGenerator.class$("java.lang.Byte")) : AsmCodeGenerator.class$java$lang$Byte).getName())) {
          generator.push(((Byte)value).intValue());
          setterArgType = Type.BYTE_TYPE;
        }
        else if (propertyClass.equals(((AsmCodeGenerator.class$java$lang$Character == null) ? (AsmCodeGenerator.class$java$lang$Character = AsmCodeGenerator.class$("java.lang.Character")) : AsmCodeGenerator.class$java$lang$Character).getName())) {
          generator.push(((Character)value).charValue());
          setterArgType = Type.CHAR_TYPE;
        } else {
          
          if (propGen == null) {
            continue;
          }
          propGen.generatePushValue(generator, value);
          setterArgType = AsmCodeGenerator.typeFromClassName(property.getPropertyClassName());
        } 
        
        Type declaringType = (property.getDeclaringClassName() != null) ? AsmCodeGenerator.typeFromClassName(property.getDeclaringClassName()) : Type.getType(componentClass);

        
        generator.invokeVirtual(declaringType, new Method(property.getWriteMethodName(), Type.VOID_TYPE, new Type[] { setterArgType }));
        
        continue;
      } 
      generateClientProperties(lwComponent, componentClass, generator, componentLocal);
    }



    
    private void generateClientProperties(LwComponent lwComponent, Class componentClass, GeneratorAdapter generator, int componentLocal) throws CodeGenerationException {
      HashMap props = lwComponent.getDelegeeClientProperties();
      for (Iterator iterator = props.entrySet().iterator(); iterator.hasNext(); ) {
        Map.Entry e = iterator.next();
        generator.loadLocal(componentLocal);
        
        generator.push((String)e.getKey());
        
        Object value = e.getValue();
        if (value instanceof StringDescriptor) {
          generator.push(((StringDescriptor)value).getValue());
        } else {
          
          Type valueType = Type.getType(value.getClass());
          generator.newInstance(valueType);
          generator.dup();
          if (value instanceof Boolean) {
            generator.push(((Boolean)value).booleanValue());
            generator.invokeConstructor(valueType, Method.getMethod("void <init>(boolean)"));
          }
          else if (value instanceof Integer) {
            generator.push(((Integer)value).intValue());
            generator.invokeConstructor(valueType, Method.getMethod("void <init>(int)"));
          }
          else if (value instanceof Double) {
            generator.push(((Double)value).doubleValue());
            generator.invokeConstructor(valueType, Method.getMethod("void <init>(double)"));
          } else {
            
            throw new CodeGenerationException(lwComponent.getId(), "Unknown client property value type");
          } 
        } 
        
        Type componentType = Type.getType(componentClass);
        Type objectType = Type.getType((AsmCodeGenerator.class$java$lang$Object == null) ? (AsmCodeGenerator.class$java$lang$Object = AsmCodeGenerator.class$("java.lang.Object")) : AsmCodeGenerator.class$java$lang$Object);
        generator.invokeVirtual(componentType, new Method("putClientProperty", Type.VOID_TYPE, new Type[] { objectType, objectType }));
      } 
    }


    
    private void generateComponentReferenceProperties(LwComponent component, GeneratorAdapter generator) throws CodeGenerationException {
      if (component instanceof LwNestedForm)
        return;  int componentLocal = ((Integer)this.this$0.myIdToLocalMap.get(component.getId())).intValue();
      LayoutCodeGenerator layoutCodeGenerator = getComponentCodeGenerator(component.getParent());
      Class componentClass = AsmCodeGenerator.getComponentClass(layoutCodeGenerator.mapComponentClass(component.getComponentClassName()), this.this$0.myLoader);
      
      LwIntrospectedProperty[] introspectedProperties = component.getAssignedIntrospectedProperties();
      for (int i = 0; i < introspectedProperties.length; i++) {
        LwIntrospectedProperty property = introspectedProperties[i];
        if (property instanceof com.intellij.uiDesigner.lw.LwIntroComponentProperty) {
          String targetId = (String)component.getPropertyValue(property);
          if (targetId != null && targetId.length() > 0) {

            
            Integer targetLocalInt = (Integer)this.this$0.myIdToLocalMap.get(targetId);
            if (targetLocalInt != null) {
              int targetLocal = targetLocalInt.intValue();
              generator.loadLocal(componentLocal);
              generator.loadLocal(targetLocal);
              Type declaringType = (property.getDeclaringClassName() != null) ? AsmCodeGenerator.typeFromClassName(property.getDeclaringClassName()) : Type.getType(componentClass);

              
              generator.invokeVirtual(declaringType, new Method(property.getWriteMethodName(), Type.VOID_TYPE, new Type[] { AsmCodeGenerator.typeFromClassName(property.getPropertyClassName()) }));
            } 
          } 
        } 
      } 


      
      if (component instanceof LwContainer) {
        LwContainer container = (LwContainer)component;
        
        for (int j = 0; j < container.getComponentCount(); j++) {
          generateComponentReferenceProperties((LwComponent)container.getComponent(j), generator);
        }
      } 
    }
    
    private void generateButtonGroups(LwRootContainer rootContainer, GeneratorAdapter generator) throws CodeGenerationException {
      IButtonGroup[] groups = rootContainer.getButtonGroups();
      if (groups.length > 0) {
        int groupLocal = generator.newLocal(AsmCodeGenerator.ourButtonGroupType);
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
          String[] ids = groups[groupIndex].getComponentIds();
          
          if (ids.length > 0) {
            generator.newInstance(AsmCodeGenerator.ourButtonGroupType);
            generator.dup();
            generator.invokeConstructor(AsmCodeGenerator.ourButtonGroupType, Method.getMethod("void <init>()"));
            generator.storeLocal(groupLocal);
            
            if (groups[groupIndex].isBound() && !this.this$0.myIgnoreCustomCreation) {
              validateFieldClass(groups[groupIndex].getName(), (AsmCodeGenerator.class$javax$swing$ButtonGroup == null) ? (AsmCodeGenerator.class$javax$swing$ButtonGroup = AsmCodeGenerator.class$("javax.swing.ButtonGroup")) : AsmCodeGenerator.class$javax$swing$ButtonGroup, null);
              generator.loadThis();
              generator.loadLocal(groupLocal);
              generator.putField(getMainClassType(), groups[groupIndex].getName(), AsmCodeGenerator.ourButtonGroupType);
            } 
            
            for (int i = 0; i < ids.length; i++) {
              Integer localInt = (Integer)this.this$0.myIdToLocalMap.get(ids[i]);
              if (localInt != null) {
                generator.loadLocal(groupLocal);
                generator.loadLocal(localInt.intValue());
                generator.invokeVirtual(AsmCodeGenerator.ourButtonGroupType, Method.getMethod("void add(javax.swing.AbstractButton)"));
              } 
            } 
          } 
        } 
      } 
    }


    
    private void generateFieldBinding(LwComponent lwComponent, GeneratorAdapter generator, int componentLocal) throws CodeGenerationException {
      String binding = lwComponent.getBinding();
      if (binding != null) {
        Integer access = (Integer)this.myFieldAccessMap.get(binding);
        if ((access.intValue() & 0x8) != 0) {
          throw new CodeGenerationException(lwComponent.getId(), "Cannot bind: field is static: " + this.this$0.myClassToBind + "." + binding);
        }
        if ((access.intValue() & 0x10) != 0) {
          throw new CodeGenerationException(lwComponent.getId(), "Cannot bind: field is final: " + this.this$0.myClassToBind + "." + binding);
        }
        
        generator.loadThis();
        generator.loadLocal(componentLocal);
        generator.putField(getMainClassType(), binding, Type.getType((String)this.myFieldDescMap.get(binding)));
      } 
    }

    
    private Type getMainClassType() {
      return Type.getType("L" + this.myClassName + ";");
    }
    
    private void validateFieldBinding(LwComponent component, Class componentClass) throws CodeGenerationException {
      String binding = component.getBinding();
      if (binding == null)
        return; 
      validateFieldClass(binding, componentClass, component.getId());
    }
    private void validateFieldClass(String binding, Class componentClass, String componentId) throws CodeGenerationException {
      Class fieldClass;
      if (!this.myFieldDescMap.containsKey(binding)) {
        throw new CodeGenerationException(componentId, "Cannot bind: field does not exist: " + this.this$0.myClassToBind + "." + binding);
      }
      
      Type fieldType = Type.getType((String)this.myFieldDescMap.get(binding));
      if (fieldType.getSort() != 10) {
        throw new CodeGenerationException(componentId, "Cannot bind: field is of primitive type: " + this.this$0.myClassToBind + "." + binding);
      }

      
      try {
        fieldClass = this.this$0.myLoader.loadClass(fieldType.getClassName());
      }
      catch (ClassNotFoundException e) {
        throw new CodeGenerationException(componentId, "Class not found: " + fieldType.getClassName());
      } 
      if (!fieldClass.isAssignableFrom(componentClass)) {
        throw new CodeGenerationException(componentId, "Cannot bind: Incompatible types. Cannot assign " + componentClass.getName() + " to field " + this.this$0.myClassToBind + "." + binding);
      }
    }
    
    private void generateBorder(LwContainer container, GeneratorAdapter generator, int componentLocal) {
      BorderType borderType = container.getBorderType();
      StringDescriptor borderTitle = container.getBorderTitle();
      String borderFactoryMethodName = borderType.getBorderFactoryMethodName();
      
      boolean borderNone = borderType.equals(BorderType.NONE);
      if (!borderNone || borderTitle != null) {
        
        generator.loadLocal(componentLocal);
        
        if (!borderNone) {
          if (borderType.equals(BorderType.LINE)) {
            if (container.getBorderColor() == null) {
              Type colorType = Type.getType((AsmCodeGenerator.class$java$awt$Color == null) ? (AsmCodeGenerator.class$java$awt$Color = AsmCodeGenerator.class$("java.awt.Color")) : AsmCodeGenerator.class$java$awt$Color);
              generator.getStatic(colorType, "black", colorType);
            } else {
              
              AsmCodeGenerator.pushPropValue(generator, ((AsmCodeGenerator.class$java$awt$Color == null) ? (AsmCodeGenerator.class$java$awt$Color = AsmCodeGenerator.class$("java.awt.Color")) : AsmCodeGenerator.class$java$awt$Color).getName(), container.getBorderColor());
            } 
            generator.invokeStatic(AsmCodeGenerator.ourBorderFactoryType, new Method(borderFactoryMethodName, AsmCodeGenerator.ourBorderType, new Type[] { Type.getType((AsmCodeGenerator.class$java$awt$Color == null) ? (AsmCodeGenerator.class$java$awt$Color = AsmCodeGenerator.class$("java.awt.Color")) : AsmCodeGenerator.class$java$awt$Color) }));

          
          }
          else if (borderType.equals(BorderType.EMPTY) && container.getBorderSize() != null) {
            Insets size = container.getBorderSize();
            generator.push(size.top);
            generator.push(size.left);
            generator.push(size.bottom);
            generator.push(size.right);
            generator.invokeStatic(AsmCodeGenerator.ourBorderFactoryType, new Method(borderFactoryMethodName, AsmCodeGenerator.ourBorderType, new Type[] { Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE }));
          
          }
          else {
            
            generator.invokeStatic(AsmCodeGenerator.ourBorderFactoryType, new Method(borderFactoryMethodName, AsmCodeGenerator.ourBorderType, new Type[0]));
          }
        
        } else {
          
          generator.push((String)null);
        } 
        pushBorderProperties(container, generator, borderTitle, componentLocal);
        generator.invokeStatic(AsmCodeGenerator.ourBorderFactoryType, AsmCodeGenerator.ourCreateTitledBorderMethod);

        
        generator.invokeVirtual(Type.getType((AsmCodeGenerator.class$javax$swing$JComponent == null) ? (AsmCodeGenerator.class$javax$swing$JComponent = AsmCodeGenerator.class$("javax.swing.JComponent")) : AsmCodeGenerator.class$javax$swing$JComponent), Method.getMethod("void setBorder(javax.swing.border.Border)"));
      } 
    }


    
    private void pushBorderProperties(LwContainer container, GeneratorAdapter generator, StringDescriptor borderTitle, int componentLocal) {
      AsmCodeGenerator.pushPropValue(generator, "java.lang.String", borderTitle);
      generator.push(container.getBorderTitleJustification());
      generator.push(container.getBorderTitlePosition());
      FontDescriptor font = container.getBorderTitleFont();
      if (font == null) {
        generator.push((String)null);
      } else {
        
        FontPropertyCodeGenerator.generatePushFont(generator, componentLocal, (LwComponent)container, font, "getFont");
      } 
      if (container.getBorderTitleColor() == null) {
        generator.push((String)null);
      } else {
        
        AsmCodeGenerator.pushPropValue(generator, ((AsmCodeGenerator.class$java$awt$Color == null) ? (AsmCodeGenerator.class$java$awt$Color = AsmCodeGenerator.class$("java.awt.Color")) : AsmCodeGenerator.class$java$awt$Color).getName(), container.getBorderTitleColor());
      } 
    } }
  
  private class FormConstructorVisitor extends MethodAdapter {
    private final String myClassName;
    private final String mySuperName;
    private boolean callsSelfConstructor;
    private boolean mySetupCalled;
    private boolean mySuperCalled;
    private final AsmCodeGenerator this$0;
    
    public FormConstructorVisitor(AsmCodeGenerator this$0, MethodVisitor mv, String className, String superName) {
      super(mv); this.this$0 = this$0; this.callsSelfConstructor = false; this.mySetupCalled = false; this.mySuperCalled = false;
      this.myClassName = className;
      this.mySuperName = superName;
    }
    
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
      if (opcode == 180 && !this.mySetupCalled && !this.callsSelfConstructor && Utils.isBoundField((IComponent)this.this$0.myRootContainer, name)) {
        callSetupUI();
      }
      super.visitFieldInsn(opcode, owner, name, desc);
    }
    
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      if (opcode == 183 && name.equals("<init>")) {
        if (owner.equals(this.myClassName)) {
          this.callsSelfConstructor = true;
        }
        else if (owner.equals(this.mySuperName)) {
          this.mySuperCalled = true;
        }
        else if (this.mySuperCalled) {
          callSetupUI();
        }
      
      } else if (this.mySuperCalled) {
        callSetupUI();
      } 
      super.visitMethodInsn(opcode, owner, name, desc);
    }
    
    public void visitJumpInsn(int opcode, Label label) {
      if (this.mySuperCalled) {
        callSetupUI();
      }
      super.visitJumpInsn(opcode, label);
    }
    
    private void callSetupUI() {
      if (!this.mySetupCalled) {
        this.mv.visitVarInsn(25, 0);
        this.mv.visitMethodInsn(183, this.myClassName, "$$$setupUI$$$", "()V");
        this.mySetupCalled = true;
      } 
    }
    
    public void visitInsn(int opcode) {
      if (opcode == 177 && !this.mySetupCalled && !this.callsSelfConstructor) {
        callSetupUI();
      }
      super.visitInsn(opcode);
    }
  }
  
  private static class FirstPassClassVisitor extends ClassAdapter {
    private boolean myExplicitSetupCall = false;
    
    public FirstPassClassVisitor() {
      super((ClassVisitor)new EmptyVisitor());
    }
    
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      if (name.equals("<init>")) {
        return (MethodVisitor)new FirstPassConstructorVisitor(this);
      }
      return null;
    }
    
    public boolean isExplicitSetupCall() {
      return this.myExplicitSetupCall;
    }
    private class FirstPassConstructorVisitor extends MethodAdapter { private final AsmCodeGenerator.FirstPassClassVisitor this$0;
      
      public FirstPassConstructorVisitor(AsmCodeGenerator.FirstPassClassVisitor this$0) {
        super((MethodVisitor)new EmptyVisitor());
        this.this$0 = this$0;
      }
      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (name.equals("$$$setupUI$$$"))
          this.this$0.myExplicitSetupCall = true; 
      } }
  
  }
}
