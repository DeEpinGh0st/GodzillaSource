package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.SecClass;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.util.EnumUtils;

















public class Constructor
  extends SafeConstructor
{
  public Constructor() {
    this(Object.class);
  }
  
  public Constructor(LoaderOptions loadingConfig) {
    this(Object.class, loadingConfig);
  }






  
  public Constructor(Class<? extends Object> theRoot) {
    this(new TypeDescription(checkRoot(theRoot)));
  }
  
  public Constructor(Class<? extends Object> theRoot, LoaderOptions loadingConfig) {
    this(new TypeDescription(checkRoot(theRoot)), loadingConfig);
  }



  
  private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
    if (theRoot == null) {
      throw new NullPointerException("Root class must be provided.");
    }
    return theRoot;
  }
  
  public Constructor(TypeDescription theRoot) {
    this(theRoot, (Collection<TypeDescription>)null, new LoaderOptions());
  }
  
  public Constructor(TypeDescription theRoot, LoaderOptions loadingConfig) {
    this(theRoot, (Collection<TypeDescription>)null, loadingConfig);
  }
  
  public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs) {
    this(theRoot, moreTDs, new LoaderOptions());
  }
  
  public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
    super(loadingConfig);
    if (theRoot == null) {
      throw new NullPointerException("Root type must be provided.");
    }
    this.yamlConstructors.put(null, new ConstructYamlObject());
    if (!Object.class.equals(theRoot.getType())) {
      this.rootTag = new Tag(theRoot.getType());
    }
    this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
    this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
    this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
    addTypeDescription(theRoot);
    if (moreTDs != null) {
      for (TypeDescription td : moreTDs) {
        addTypeDescription(td);
      }
    }
  }









  
  public Constructor(String theRoot) throws ClassNotFoundException {
    this(SecClass.forName(check(theRoot)));
  }
  
  public Constructor(String theRoot, LoaderOptions loadingConfig) throws ClassNotFoundException {
    this(SecClass.forName(check(theRoot)), loadingConfig);
  }
  
  private static final String check(String s) {
    if (s == null) {
      throw new NullPointerException("Root type must be provided.");
    }
    if (s.trim().length() == 0) {
      throw new YAMLException("Root type must be provided.");
    }
    return s;
  }












  
  protected class ConstructMapping
    implements Construct
  {
    public Object construct(Node node) {
      MappingNode mnode = (MappingNode)node;
      if (Map.class.isAssignableFrom(node.getType())) {
        if (node.isTwoStepsConstruction()) {
          return Constructor.this.newMap(mnode);
        }
        return Constructor.this.constructMapping(mnode);
      } 
      if (Collection.class.isAssignableFrom(node.getType())) {
        if (node.isTwoStepsConstruction()) {
          return Constructor.this.newSet((CollectionNode<?>)mnode);
        }
        return Constructor.this.constructSet(mnode);
      } 
      
      Object obj = Constructor.this.newInstance((Node)mnode);
      if (node.isTwoStepsConstruction()) {
        return obj;
      }
      return constructJavaBean2ndStep(mnode, obj);
    }



    
    public void construct2ndStep(Node node, Object object) {
      if (Map.class.isAssignableFrom(node.getType())) {
        Constructor.this.constructMapping2ndStep((MappingNode)node, (Map<Object, Object>)object);
      } else if (Set.class.isAssignableFrom(node.getType())) {
        Constructor.this.constructSet2ndStep((MappingNode)node, (Set<Object>)object);
      } else {
        constructJavaBean2ndStep((MappingNode)node, object);
      } 
    }
























    
    protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
      Constructor.this.flattenMapping(node);
      Class<? extends Object> beanType = node.getType();
      List<NodeTuple> nodeValue = node.getValue();
      for (NodeTuple tuple : nodeValue) {
        ScalarNode keyNode;
        if (tuple.getKeyNode() instanceof ScalarNode) {
          
          keyNode = (ScalarNode)tuple.getKeyNode();
        } else {
          throw new YAMLException("Keys must be scalars but found: " + tuple
              .getKeyNode());
        } 
        Node valueNode = tuple.getValueNode();
        
        keyNode.setType(String.class);
        String key = (String)Constructor.this.constructObject((Node)keyNode);
        try {
          TypeDescription memberDescription = Constructor.this.typeDefinitions.get(beanType);
          
          Property property = (memberDescription == null) ? getProperty(beanType, key) : memberDescription.getProperty(key);
          
          if (!property.isWritable()) {
            throw new YAMLException("No writable property '" + key + "' on class: " + beanType
                .getName());
          }
          
          valueNode.setType(property.getType());
          
          boolean typeDetected = (memberDescription != null) ? memberDescription.setupPropertyType(key, valueNode) : false;
          
          if (!typeDetected && valueNode.getNodeId() != NodeId.scalar) {
            
            Class<?>[] arguments = property.getActualTypeArguments();
            if (arguments != null && arguments.length > 0)
            {
              
              if (valueNode.getNodeId() == NodeId.sequence) {
                Class<?> t = arguments[0];
                SequenceNode snode = (SequenceNode)valueNode;
                snode.setListType(t);
              } else if (Set.class.isAssignableFrom(valueNode.getType())) {
                Class<?> t = arguments[0];
                MappingNode mnode = (MappingNode)valueNode;
                mnode.setOnlyKeyType(t);
                mnode.setUseClassConstructor(Boolean.valueOf(true));
              } else if (Map.class.isAssignableFrom(valueNode.getType())) {
                Class<?> keyType = arguments[0];
                Class<?> valueType = arguments[1];
                MappingNode mnode = (MappingNode)valueNode;
                mnode.setTypes(keyType, valueType);
                mnode.setUseClassConstructor(Boolean.valueOf(true));
              } 
            }
          } 


          
          Object value = (memberDescription != null) ? newInstance(memberDescription, key, valueNode) : Constructor.this.constructObject(valueNode);

          
          if ((property.getType() == float.class || property.getType() == Float.class) && 
            value instanceof Double) {
            value = Float.valueOf(((Double)value).floatValue());
          }

          
          if (property.getType() == String.class && Tag.BINARY.equals(valueNode.getTag()) && value instanceof byte[])
          {
            value = new String((byte[])value);
          }
          
          if (memberDescription == null || 
            !memberDescription.setProperty(object, key, value)) {
            property.set(object, value);
          }
        } catch (DuplicateKeyException e) {
          throw e;
        } catch (Exception e) {
          throw new ConstructorException("Cannot create property=" + key + " for JavaBean=" + object, node
              
              .getStartMark(), e.getMessage(), valueNode.getStartMark(), e);
        } 
      } 
      return object;
    }

    
    private Object newInstance(TypeDescription memberDescription, String propertyName, Node node) {
      Object newInstance = memberDescription.newInstance(propertyName, node);
      if (newInstance != null) {
        Constructor.this.constructedObjects.put(node, newInstance);
        return Constructor.this.constructObjectNoCheck(node);
      } 
      return Constructor.this.constructObject(node);
    }
    
    protected Property getProperty(Class<? extends Object> type, String name) {
      return Constructor.this.getPropertyUtils().getProperty(type, name);
    }
  }





  
  protected class ConstructYamlObject
    implements Construct
  {
    private Construct getConstructor(Node node) {
      Class<?> cl = Constructor.this.getClassForNode(node);
      node.setType(cl);
      
      Construct constructor = Constructor.this.yamlClassConstructors.get(node.getNodeId());
      return constructor;
    }
    
    public Object construct(Node node) {
      try {
        return getConstructor(node).construct(node);
      } catch (ConstructorException e) {
        throw e;
      } catch (Exception e) {
        throw new ConstructorException(null, null, "Can't construct a java object for " + node
            .getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
      } 
    }
    
    public void construct2ndStep(Node node, Object object) {
      try {
        getConstructor(node).construct2ndStep(node, object);
      } catch (Exception e) {
        throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node
            
            .getTag() + "; exception=" + e.getMessage(), node
            .getStartMark(), e);
      } 
    }
  }


  
  protected class ConstructScalar
    extends AbstractConstruct
  {
    public Object construct(Node nnode) {
      ScalarNode node = (ScalarNode)nnode;
      Class<?> type = node.getType();
      
      try {
        return Constructor.this.newInstance(type, (Node)node, false);
      } catch (InstantiationException null) {
        Object result;

        
        if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class
          .isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class
          
          .isAssignableFrom(type) || Tag.BINARY
          .equals(node.getTag()) || Calendar.class.isAssignableFrom(type) || type == UUID.class) {

          
          Object object = constructStandardJavaInstance(type, node);
        } else {
          Object argument;
          
          Constructor[] arrayOfConstructor = (Constructor[])type.getDeclaredConstructors();
          int oneArgCount = 0;
          Constructor<?> javaConstructor = null;
          for (Constructor<?> c : arrayOfConstructor) {
            if ((c.getParameterTypes()).length == 1) {
              oneArgCount++;
              javaConstructor = c;
            } 
          } 
          
          if (javaConstructor == null)
            try {
              return Constructor.this.newInstance(type, (Node)node, false);
            } catch (InstantiationException ie) {
              throw new YAMLException("No single argument constructor found for " + type + " : " + ie
                  .getMessage());
            }  
          if (oneArgCount == 1) {
            argument = constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);


          
          }
          else {


            
            argument = BaseConstructor.constructScalar(node);
            try {
              javaConstructor = type.getDeclaredConstructor(new Class[] { String.class });
            } catch (Exception e) {
              throw new YAMLException("Can't construct a java object for scalar " + node
                  .getTag() + "; No String constructor found. Exception=" + e
                  .getMessage(), e);
            } 
          } 
          try {
            javaConstructor.setAccessible(true);
            result = javaConstructor.newInstance(new Object[] { argument });
          } catch (Exception e) {
            throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node
                .getTag() + "; exception=" + e
                .getMessage(), node
                .getStartMark(), e);
          } 
        } 
        return result;
      } 
    }

    
    private Object constructStandardJavaInstance(Class<String> type, ScalarNode node) {
      Object result;
      if (type == String.class) {
        Construct stringConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
        result = stringConstructor.construct((Node)node);
      } else if (type == Boolean.class || type == boolean.class) {
        Construct boolConstructor = Constructor.this.yamlConstructors.get(Tag.BOOL);
        result = boolConstructor.construct((Node)node);
      } else if (type == Character.class || type == char.class) {
        Construct charConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
        String ch = (String)charConstructor.construct((Node)node);
        if (ch.length() == 0)
        { result = null; }
        else { if (ch.length() != 1) {
            throw new YAMLException("Invalid node Character: '" + ch + "'; length: " + ch
                .length());
          }
          result = Character.valueOf(ch.charAt(0)); }
      
      } else if (Date.class.isAssignableFrom(type)) {
        Construct dateConstructor = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
        Date date = (Date)dateConstructor.construct((Node)node);
        if (type == Date.class) {
          result = date;
        } else {
          try {
            Constructor<?> constr = type.getConstructor(new Class[] { long.class });
            result = constr.newInstance(new Object[] { Long.valueOf(date.getTime()) });
          } catch (RuntimeException e) {
            throw e;
          } catch (Exception e) {
            throw new YAMLException("Cannot construct: '" + type + "'");
          } 
        } 
      } else if (type == Float.class || type == Double.class || type == float.class || type == double.class || type == BigDecimal.class) {
        
        if (type == BigDecimal.class) {
          result = new BigDecimal(node.getValue());
        } else {
          Construct doubleConstructor = Constructor.this.yamlConstructors.get(Tag.FLOAT);
          result = doubleConstructor.construct((Node)node);
          if (type == Float.class || type == float.class) {
            result = Float.valueOf(((Double)result).floatValue());
          }
        } 
      } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == byte.class || type == short.class || type == int.class || type == long.class) {

        
        Construct intConstructor = Constructor.this.yamlConstructors.get(Tag.INT);
        result = intConstructor.construct((Node)node);
        if (type == Byte.class || type == byte.class) {
          result = Byte.valueOf(Integer.valueOf(result.toString()).byteValue());
        } else if (type == Short.class || type == short.class) {
          result = Short.valueOf(Integer.valueOf(result.toString()).shortValue());
        } else if (type == Integer.class || type == int.class) {
          result = Integer.valueOf(Integer.parseInt(result.toString()));
        } else if (type == Long.class || type == long.class) {
          result = Long.valueOf(result.toString());
        } else {
          
          result = new BigInteger(result.toString());
        } 
      } else if (Enum.class.isAssignableFrom(type)) {
        String enumValueName = node.getValue();
        try {
          if (Constructor.this.loadingConfig.isEnumCaseSensitive()) {
            result = Enum.valueOf(type, enumValueName);
          } else {
            result = EnumUtils.findEnumInsensitiveCase(type, enumValueName);
          } 
        } catch (Exception ex) {
          throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type
              .getName());
        } 
      } else if (Calendar.class.isAssignableFrom(type)) {
        SafeConstructor.ConstructYamlTimestamp contr = new SafeConstructor.ConstructYamlTimestamp();
        contr.construct((Node)node);
        result = contr.getCalendar();
      } else if (Number.class.isAssignableFrom(type)) {
        
        SafeConstructor.ConstructYamlFloat contr = new SafeConstructor.ConstructYamlFloat(Constructor.this);
        result = contr.construct((Node)node);
      } else if (UUID.class == type) {
        result = UUID.fromString(node.getValue());
      }
      else if (Constructor.this.yamlConstructors.containsKey(node.getTag())) {
        result = ((Construct)Constructor.this.yamlConstructors.get(node.getTag())).construct((Node)node);
      } else {
        throw new YAMLException("Unsupported class: " + type);
      } 
      
      return result;
    }
  }



  
  protected class ConstructSequence
    implements Construct
  {
    public Object construct(Node node) {
      SequenceNode snode = (SequenceNode)node;
      if (Set.class.isAssignableFrom(node.getType())) {
        if (node.isTwoStepsConstruction()) {
          throw new YAMLException("Set cannot be recursive.");
        }
        return Constructor.this.constructSet(snode);
      } 
      if (Collection.class.isAssignableFrom(node.getType())) {
        if (node.isTwoStepsConstruction()) {
          return Constructor.this.newList(snode);
        }
        return Constructor.this.constructSequence(snode);
      } 
      if (node.getType().isArray()) {
        if (node.isTwoStepsConstruction()) {
          return Constructor.this.createArray(node.getType(), snode.getValue().size());
        }
        return Constructor.this.constructArray(snode);
      } 


      
      List<Constructor<?>> possibleConstructors = new ArrayList<>(snode.getValue().size());
      for (Constructor<?> constructor : node.getType()
        .getDeclaredConstructors()) {
        if (snode.getValue().size() == (constructor.getParameterTypes()).length) {
          possibleConstructors.add(constructor);
        }
      } 
      if (!possibleConstructors.isEmpty()) {
        if (possibleConstructors.size() == 1) {
          Object[] arrayOfObject = new Object[snode.getValue().size()];
          Constructor<?> c = possibleConstructors.get(0);
          int i = 0;
          for (Node argumentNode : snode.getValue()) {
            Class<?> type = c.getParameterTypes()[i];
            
            argumentNode.setType(type);
            arrayOfObject[i++] = Constructor.this.constructObject(argumentNode);
          } 
          
          try {
            c.setAccessible(true);
            return c.newInstance(arrayOfObject);
          } catch (Exception e) {
            throw new YAMLException(e);
          } 
        } 

        
        List<Object> argumentList = (List)Constructor.this.constructSequence(snode);
        Class<?>[] parameterTypes = new Class[argumentList.size()];
        int index = 0;
        for (Object parameter : argumentList) {
          parameterTypes[index] = parameter.getClass();
          index++;
        } 
        
        for (Constructor<?> c : possibleConstructors) {
          Class<?>[] argTypes = c.getParameterTypes();
          boolean foundConstructor = true;
          for (int i = 0; i < argTypes.length; i++) {
            if (!wrapIfPrimitive(argTypes[i]).isAssignableFrom(parameterTypes[i])) {
              foundConstructor = false;
              break;
            } 
          } 
          if (foundConstructor) {
            try {
              c.setAccessible(true);
              return c.newInstance(argumentList.toArray());
            } catch (Exception e) {
              throw new YAMLException(e);
            } 
          }
        } 
      } 
      throw new YAMLException("No suitable constructor with " + 
          String.valueOf(snode.getValue().size()) + " arguments found for " + node
          .getType());
    }


    
    private final Class<? extends Object> wrapIfPrimitive(Class<?> clazz) {
      if (!clazz.isPrimitive()) {
        return (Class)clazz;
      }
      if (clazz == int.class) {
        return (Class)Integer.class;
      }
      if (clazz == float.class) {
        return (Class)Float.class;
      }
      if (clazz == double.class) {
        return (Class)Double.class;
      }
      if (clazz == boolean.class) {
        return (Class)Boolean.class;
      }
      if (clazz == long.class) {
        return (Class)Long.class;
      }
      if (clazz == char.class) {
        return (Class)Character.class;
      }
      if (clazz == short.class) {
        return (Class)Short.class;
      }
      if (clazz == byte.class) {
        return (Class)Byte.class;
      }
      throw new YAMLException("Unexpected primitive " + clazz);
    }

    
    public void construct2ndStep(Node node, Object object) {
      SequenceNode snode = (SequenceNode)node;
      if (List.class.isAssignableFrom(node.getType())) {
        List<Object> list = (List<Object>)object;
        Constructor.this.constructSequenceStep2(snode, list);
      } else if (node.getType().isArray()) {
        Constructor.this.constructArrayStep2(snode, object);
      } else {
        throw new YAMLException("Immutable objects cannot be recursive.");
      } 
    }
  }
  
  protected Class<?> getClassForNode(Node node) {
    Class<? extends Object> classForTag = this.typeTags.get(node.getTag());
    if (classForTag == null) {
      Class<?> cl; String name = node.getTag().getClassName();
      
      try {
        cl = getClassForName(name);
      } catch (ClassNotFoundException e) {
        throw new YAMLException("Class not found: " + name);
      } 
      this.typeTags.put(node.getTag(), cl);
      return cl;
    } 
    return classForTag;
  }

  
  protected Class<?> getClassForName(String name) throws ClassNotFoundException {
    try {
      return SecClass.forName(name, true, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException e) {
      return SecClass.forName(name);
    } 
  }
}
