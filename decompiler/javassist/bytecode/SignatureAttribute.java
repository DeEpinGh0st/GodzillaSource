package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.CtClass;
























public class SignatureAttribute
  extends AttributeInfo
{
  public static final String tag = "Signature";
  
  SignatureAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }






  
  public SignatureAttribute(ConstPool cp, String signature) {
    super(cp, "Signature");
    int index = cp.addUtf8Info(signature);
    byte[] bvalue = new byte[2];
    bvalue[0] = (byte)(index >>> 8);
    bvalue[1] = (byte)index;
    set(bvalue);
  }







  
  public String getSignature() {
    return getConstPool().getUtf8Info(ByteArray.readU16bit(get(), 0));
  }







  
  public void setSignature(String sig) {
    int index = getConstPool().addUtf8Info(sig);
    ByteArray.write16bit(index, this.info, 0);
  }









  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    return new SignatureAttribute(newCp, getSignature());
  }

  
  void renameClass(String oldname, String newname) {
    String sig = renameClass(getSignature(), oldname, newname);
    setSignature(sig);
  }

  
  void renameClass(Map<String, String> classnames) {
    String sig = renameClass(getSignature(), classnames);
    setSignature(sig);
  }
  
  static String renameClass(String desc, String oldname, String newname) {
    Map<String, String> map = new HashMap<>();
    map.put(oldname, newname);
    return renameClass(desc, map);
  }
  
  static String renameClass(String desc, Map<String, String> map) {
    if (map == null) {
      return desc;
    }
    StringBuilder newdesc = new StringBuilder();
    int head = 0;
    int i = 0; while (true) {
      char c;
      int j = desc.indexOf('L', i);
      if (j < 0) {
        break;
      }
      StringBuilder nameBuf = new StringBuilder();
      int k = j;

      
      try { while ((c = desc.charAt(++k)) != ';') {
          nameBuf.append(c);
          if (c == '<') {
            while ((c = desc.charAt(++k)) != '>') {
              nameBuf.append(c);
            }
            nameBuf.append(c);
          }
        
        }  }
      catch (IndexOutOfBoundsException e) { break; }
       i = k + 1;
      String name = nameBuf.toString();
      String name2 = map.get(name);
      if (name2 != null) {
        newdesc.append(desc.substring(head, j));
        newdesc.append('L');
        newdesc.append(name2);
        newdesc.append(c);
        head = i;
      } 
    } 
    
    if (head == 0)
      return desc; 
    int len = desc.length();
    if (head < len) {
      newdesc.append(desc.substring(head, len));
    }
    return newdesc.toString();
  }

  
  private static boolean isNamePart(int c) {
    return (c != 59 && c != 60);
  }
  
  private static class Cursor {
    int position = 0;
    
    int indexOf(String s, int ch) throws BadBytecode {
      int i = s.indexOf(ch, this.position);
      if (i < 0)
        throw SignatureAttribute.error(s); 
      this.position = i + 1;
      return i;
    }


    
    private Cursor() {}
  }


  
  public static class ClassSignature
  {
    SignatureAttribute.TypeParameter[] params;
    
    SignatureAttribute.ClassType superClass;
    
    SignatureAttribute.ClassType[] interfaces;

    
    public ClassSignature(SignatureAttribute.TypeParameter[] params, SignatureAttribute.ClassType superClass, SignatureAttribute.ClassType[] interfaces) {
      this.params = (params == null) ? new SignatureAttribute.TypeParameter[0] : params;
      this.superClass = (superClass == null) ? SignatureAttribute.ClassType.OBJECT : superClass;
      this.interfaces = (interfaces == null) ? new SignatureAttribute.ClassType[0] : interfaces;
    }





    
    public ClassSignature(SignatureAttribute.TypeParameter[] p) {
      this(p, null, null);
    }





    
    public SignatureAttribute.TypeParameter[] getParameters() {
      return this.params;
    }


    
    public SignatureAttribute.ClassType getSuperClass() {
      return this.superClass;
    }



    
    public SignatureAttribute.ClassType[] getInterfaces() {
      return this.interfaces;
    }



    
    public String toString() {
      StringBuffer sbuf = new StringBuffer();
      
      SignatureAttribute.TypeParameter.toString(sbuf, this.params);
      sbuf.append(" extends ").append(this.superClass);
      if (this.interfaces.length > 0) {
        sbuf.append(" implements ");
        SignatureAttribute.Type.toString(sbuf, (SignatureAttribute.Type[])this.interfaces);
      } 
      
      return sbuf.toString();
    }



    
    public String encode() {
      StringBuffer sbuf = new StringBuffer();
      if (this.params.length > 0) {
        sbuf.append('<');
        for (int j = 0; j < this.params.length; j++) {
          this.params[j].encode(sbuf);
        }
        sbuf.append('>');
      } 
      
      this.superClass.encode(sbuf);
      for (int i = 0; i < this.interfaces.length; i++) {
        this.interfaces[i].encode(sbuf);
      }
      return sbuf.toString();
    }
  }



  
  public static class MethodSignature
  {
    SignatureAttribute.TypeParameter[] typeParams;

    
    SignatureAttribute.Type[] params;

    
    SignatureAttribute.Type retType;

    
    SignatureAttribute.ObjectType[] exceptions;


    
    public MethodSignature(SignatureAttribute.TypeParameter[] tp, SignatureAttribute.Type[] params, SignatureAttribute.Type ret, SignatureAttribute.ObjectType[] ex) {
      this.typeParams = (tp == null) ? new SignatureAttribute.TypeParameter[0] : tp;
      this.params = (params == null) ? new SignatureAttribute.Type[0] : params;
      this.retType = (ret == null) ? new SignatureAttribute.BaseType("void") : ret;
      this.exceptions = (ex == null) ? new SignatureAttribute.ObjectType[0] : ex;
    }




    
    public SignatureAttribute.TypeParameter[] getTypeParameters() {
      return this.typeParams;
    }



    
    public SignatureAttribute.Type[] getParameterTypes() {
      return this.params;
    }

    
    public SignatureAttribute.Type getReturnType() {
      return this.retType;
    }




    
    public SignatureAttribute.ObjectType[] getExceptionTypes() {
      return this.exceptions;
    }



    
    public String toString() {
      StringBuffer sbuf = new StringBuffer();
      
      SignatureAttribute.TypeParameter.toString(sbuf, this.typeParams);
      sbuf.append(" (");
      SignatureAttribute.Type.toString(sbuf, this.params);
      sbuf.append(") ");
      sbuf.append(this.retType);
      if (this.exceptions.length > 0) {
        sbuf.append(" throws ");
        SignatureAttribute.Type.toString(sbuf, (SignatureAttribute.Type[])this.exceptions);
      } 
      
      return sbuf.toString();
    }



    
    public String encode() {
      StringBuffer sbuf = new StringBuffer();
      if (this.typeParams.length > 0) {
        sbuf.append('<');
        for (int j = 0; j < this.typeParams.length; j++) {
          this.typeParams[j].encode(sbuf);
        }
        sbuf.append('>');
      } 
      
      sbuf.append('('); int i;
      for (i = 0; i < this.params.length; i++) {
        this.params[i].encode(sbuf);
      }
      sbuf.append(')');
      this.retType.encode(sbuf);
      if (this.exceptions.length > 0) {
        for (i = 0; i < this.exceptions.length; i++) {
          sbuf.append('^');
          this.exceptions[i].encode(sbuf);
        } 
      }
      return sbuf.toString();
    }
  }

  
  public static class TypeParameter
  {
    String name;
    
    SignatureAttribute.ObjectType superClass;
    
    SignatureAttribute.ObjectType[] superInterfaces;

    
    TypeParameter(String sig, int nb, int ne, SignatureAttribute.ObjectType sc, SignatureAttribute.ObjectType[] si) {
      this.name = sig.substring(nb, ne);
      this.superClass = sc;
      this.superInterfaces = si;
    }








    
    public TypeParameter(String name, SignatureAttribute.ObjectType superClass, SignatureAttribute.ObjectType[] superInterfaces) {
      this.name = name;
      this.superClass = superClass;
      if (superInterfaces == null) {
        this.superInterfaces = new SignatureAttribute.ObjectType[0];
      } else {
        this.superInterfaces = superInterfaces;
      } 
    }





    
    public TypeParameter(String name) {
      this(name, null, null);
    }



    
    public String getName() {
      return this.name;
    }


    
    public SignatureAttribute.ObjectType getClassBound() {
      return this.superClass;
    }



    
    public SignatureAttribute.ObjectType[] getInterfaceBound() {
      return this.superInterfaces;
    }



    
    public String toString() {
      StringBuffer sbuf = new StringBuffer(getName());
      if (this.superClass != null) {
        sbuf.append(" extends ").append(this.superClass.toString());
      }
      int len = this.superInterfaces.length;
      if (len > 0) {
        for (int i = 0; i < len; i++) {
          if (i > 0 || this.superClass != null) {
            sbuf.append(" & ");
          } else {
            sbuf.append(" extends ");
          } 
          sbuf.append(this.superInterfaces[i].toString());
        } 
      }
      
      return sbuf.toString();
    }
    
    static void toString(StringBuffer sbuf, TypeParameter[] tp) {
      sbuf.append('<');
      for (int i = 0; i < tp.length; i++) {
        if (i > 0) {
          sbuf.append(", ");
        }
        sbuf.append(tp[i]);
      } 
      
      sbuf.append('>');
    }
    
    void encode(StringBuffer sb) {
      sb.append(this.name);
      if (this.superClass == null) {
        sb.append(":Ljava/lang/Object;");
      } else {
        sb.append(':');
        this.superClass.encode(sb);
      } 
      
      for (int i = 0; i < this.superInterfaces.length; i++) {
        sb.append(':');
        this.superInterfaces[i].encode(sb);
      } 
    }
  }


  
  public static class TypeArgument
  {
    SignatureAttribute.ObjectType arg;
    
    char wildcard;

    
    TypeArgument(SignatureAttribute.ObjectType a, char w) {
      this.arg = a;
      this.wildcard = w;
    }







    
    public TypeArgument(SignatureAttribute.ObjectType t) {
      this(t, ' ');
    }



    
    public TypeArgument() {
      this(null, '*');
    }






    
    public static TypeArgument subclassOf(SignatureAttribute.ObjectType t) {
      return new TypeArgument(t, '+');
    }






    
    public static TypeArgument superOf(SignatureAttribute.ObjectType t) {
      return new TypeArgument(t, '-');
    }





    
    public char getKind() {
      return this.wildcard;
    }


    
    public boolean isWildcard() {
      return (this.wildcard != ' ');
    }





    
    public SignatureAttribute.ObjectType getType() {
      return this.arg;
    }



    
    public String toString() {
      if (this.wildcard == '*') {
        return "?";
      }
      String type = this.arg.toString();
      if (this.wildcard == ' ')
        return type; 
      if (this.wildcard == '+') {
        return "? extends " + type;
      }
      return "? super " + type;
    }
    
    static void encode(StringBuffer sb, TypeArgument[] args) {
      sb.append('<');
      for (int i = 0; i < args.length; i++) {
        TypeArgument ta = args[i];
        if (ta.isWildcard()) {
          sb.append(ta.wildcard);
        }
        if (ta.getType() != null) {
          ta.getType().encode(sb);
        }
      } 
      sb.append('>');
    }
  }

  
  public static abstract class Type
  {
    abstract void encode(StringBuffer param1StringBuffer);
    
    static void toString(StringBuffer sbuf, Type[] ts) {
      for (int i = 0; i < ts.length; i++) {
        if (i > 0) {
          sbuf.append(", ");
        }
        sbuf.append(ts[i]);
      } 
    }




    
    public String jvmTypeName() {
      return toString();
    }
  }
  
  public static class BaseType extends Type {
    char descriptor;
    
    BaseType(char c) {
      this.descriptor = c;
    }




    
    public BaseType(String typeName) {
      this(Descriptor.of(typeName).charAt(0));
    }




    
    public char getDescriptor() {
      return this.descriptor;
    }



    
    public CtClass getCtlass() {
      return Descriptor.toPrimitiveClass(this.descriptor);
    }




    
    public String toString() {
      return Descriptor.toClassName(Character.toString(this.descriptor));
    }

    
    void encode(StringBuffer sb) {
      sb.append(this.descriptor);
    }
  }





  
  public static abstract class ObjectType
    extends Type
  {
    public String encode() {
      StringBuffer sb = new StringBuffer();
      encode(sb);
      return sb.toString();
    }
  }

  
  public static class ClassType
    extends ObjectType
  {
    String name;
    
    SignatureAttribute.TypeArgument[] arguments;
    
    static ClassType make(String s, int b, int e, SignatureAttribute.TypeArgument[] targs, ClassType parent) {
      if (parent == null)
        return new ClassType(s, b, e, targs); 
      return new SignatureAttribute.NestedClassType(s, b, e, targs, parent);
    }
    
    ClassType(String signature, int begin, int end, SignatureAttribute.TypeArgument[] targs) {
      this.name = signature.substring(begin, end).replace('/', '.');
      this.arguments = targs;
    }



    
    public static ClassType OBJECT = new ClassType("java.lang.Object", null);







    
    public ClassType(String className, SignatureAttribute.TypeArgument[] args) {
      this.name = className;
      this.arguments = args;
    }






    
    public ClassType(String className) {
      this(className, null);
    }



    
    public String getName() {
      return this.name;
    }




    
    public SignatureAttribute.TypeArgument[] getTypeArguments() {
      return this.arguments;
    }




    
    public ClassType getDeclaringClass() {
      return null;
    }



    
    public String toString() {
      StringBuffer sbuf = new StringBuffer();
      ClassType parent = getDeclaringClass();
      if (parent != null) {
        sbuf.append(parent.toString()).append('.');
      }
      return toString2(sbuf);
    }
    
    private String toString2(StringBuffer sbuf) {
      sbuf.append(this.name);
      if (this.arguments != null) {
        sbuf.append('<');
        int n = this.arguments.length;
        for (int i = 0; i < n; i++) {
          if (i > 0) {
            sbuf.append(", ");
          }
          sbuf.append(this.arguments[i].toString());
        } 
        
        sbuf.append('>');
      } 
      
      return sbuf.toString();
    }






    
    public String jvmTypeName() {
      StringBuffer sbuf = new StringBuffer();
      ClassType parent = getDeclaringClass();
      if (parent != null) {
        sbuf.append(parent.jvmTypeName()).append('$');
      }
      return toString2(sbuf);
    }

    
    void encode(StringBuffer sb) {
      sb.append('L');
      encode2(sb);
      sb.append(';');
    }
    
    void encode2(StringBuffer sb) {
      ClassType parent = getDeclaringClass();
      if (parent != null) {
        parent.encode2(sb);
        sb.append('$');
      } 
      
      sb.append(this.name.replace('.', '/'));
      if (this.arguments != null) {
        SignatureAttribute.TypeArgument.encode(sb, this.arguments);
      }
    }
  }
  
  public static class NestedClassType
    extends ClassType
  {
    SignatureAttribute.ClassType parent;
    
    NestedClassType(String s, int b, int e, SignatureAttribute.TypeArgument[] targs, SignatureAttribute.ClassType p) {
      super(s, b, e, targs);
      this.parent = p;
    }








    
    public NestedClassType(SignatureAttribute.ClassType parent, String className, SignatureAttribute.TypeArgument[] args) {
      super(className, args);
      this.parent = parent;
    }




    
    public SignatureAttribute.ClassType getDeclaringClass() {
      return this.parent;
    }
  }


  
  public static class ArrayType
    extends ObjectType
  {
    int dim;

    
    SignatureAttribute.Type componentType;


    
    public ArrayType(int d, SignatureAttribute.Type comp) {
      this.dim = d;
      this.componentType = comp;
    }


    
    public int getDimension() {
      return this.dim;
    }


    
    public SignatureAttribute.Type getComponentType() {
      return this.componentType;
    }




    
    public String toString() {
      StringBuffer sbuf = new StringBuffer(this.componentType.toString());
      for (int i = 0; i < this.dim; i++) {
        sbuf.append("[]");
      }
      return sbuf.toString();
    }

    
    void encode(StringBuffer sb) {
      for (int i = 0; i < this.dim; i++) {
        sb.append('[');
      }
      this.componentType.encode(sb);
    }
  }

  
  public static class TypeVariable
    extends ObjectType
  {
    String name;
    
    TypeVariable(String sig, int begin, int end) {
      this.name = sig.substring(begin, end);
    }





    
    public TypeVariable(String name) {
      this.name = name;
    }



    
    public String getName() {
      return this.name;
    }




    
    public String toString() {
      return this.name;
    }

    
    void encode(StringBuffer sb) {
      sb.append('T').append(this.name).append(';');
    }
  }











  
  public static ClassSignature toClassSignature(String sig) throws BadBytecode {
    try {
      return parseSig(sig);
    }
    catch (IndexOutOfBoundsException e) {
      throw error(sig);
    } 
  }











  
  public static MethodSignature toMethodSignature(String sig) throws BadBytecode {
    try {
      return parseMethodSig(sig);
    }
    catch (IndexOutOfBoundsException e) {
      throw error(sig);
    } 
  }










  
  public static ObjectType toFieldSignature(String sig) throws BadBytecode {
    try {
      return parseObjectType(sig, new Cursor(), false);
    }
    catch (IndexOutOfBoundsException e) {
      throw error(sig);
    } 
  }








  
  public static Type toTypeSignature(String sig) throws BadBytecode {
    try {
      return parseType(sig, new Cursor());
    }
    catch (IndexOutOfBoundsException e) {
      throw error(sig);
    } 
  }


  
  private static ClassSignature parseSig(String sig) throws BadBytecode, IndexOutOfBoundsException {
    Cursor cur = new Cursor();
    TypeParameter[] tp = parseTypeParams(sig, cur);
    ClassType superClass = parseClassType(sig, cur);
    int sigLen = sig.length();
    List<ClassType> ifArray = new ArrayList<>();
    while (cur.position < sigLen && sig.charAt(cur.position) == 'L') {
      ifArray.add(parseClassType(sig, cur));
    }
    
    ClassType[] ifs = ifArray.<ClassType>toArray(new ClassType[ifArray.size()]);
    return new ClassSignature(tp, superClass, ifs);
  }


  
  private static MethodSignature parseMethodSig(String sig) throws BadBytecode {
    Cursor cur = new Cursor();
    TypeParameter[] tp = parseTypeParams(sig, cur);
    if (sig.charAt(cur.position++) != '(') {
      throw error(sig);
    }
    List<Type> params = new ArrayList<>();
    while (sig.charAt(cur.position) != ')') {
      Type t = parseType(sig, cur);
      params.add(t);
    } 
    
    cur.position++;
    Type ret = parseType(sig, cur);
    int sigLen = sig.length();
    List<ObjectType> exceptions = new ArrayList<>();
    while (cur.position < sigLen && sig.charAt(cur.position) == '^') {
      cur.position++;
      ObjectType t = parseObjectType(sig, cur, false);
      if (t instanceof ArrayType) {
        throw error(sig);
      }
      exceptions.add(t);
    } 
    
    Type[] p = params.<Type>toArray(new Type[params.size()]);
    ObjectType[] ex = exceptions.<ObjectType>toArray(new ObjectType[exceptions.size()]);
    return new MethodSignature(tp, p, ret, ex);
  }


  
  private static TypeParameter[] parseTypeParams(String sig, Cursor cur) throws BadBytecode {
    List<TypeParameter> typeParam = new ArrayList<>();
    if (sig.charAt(cur.position) == '<') {
      cur.position++;
      while (sig.charAt(cur.position) != '>') {
        int nameBegin = cur.position;
        int nameEnd = cur.indexOf(sig, 58);
        ObjectType classBound = parseObjectType(sig, cur, true);
        List<ObjectType> ifBound = new ArrayList<>();
        while (sig.charAt(cur.position) == ':') {
          cur.position++;
          ObjectType t = parseObjectType(sig, cur, false);
          ifBound.add(t);
        } 

        
        TypeParameter p = new TypeParameter(sig, nameBegin, nameEnd, classBound, ifBound.<ObjectType>toArray(new ObjectType[ifBound.size()]));
        typeParam.add(p);
      } 
      
      cur.position++;
    } 
    
    return typeParam.<TypeParameter>toArray(new TypeParameter[typeParam.size()]);
  }



  
  private static ObjectType parseObjectType(String sig, Cursor c, boolean dontThrow) throws BadBytecode {
    int i, begin = c.position;
    switch (sig.charAt(begin)) {
      case 'L':
        return parseClassType2(sig, c, (ClassType)null);
      case 'T':
        i = c.indexOf(sig, 59);
        return new TypeVariable(sig, begin + 1, i);
      case '[':
        return parseArray(sig, c);
    } 
    if (dontThrow)
      return null; 
    throw error(sig);
  }



  
  private static ClassType parseClassType(String sig, Cursor c) throws BadBytecode {
    if (sig.charAt(c.position) == 'L')
      return parseClassType2(sig, c, (ClassType)null); 
    throw error(sig);
  }
  
  private static ClassType parseClassType2(String sig, Cursor c, ClassType parent) throws BadBytecode {
    char t;
    TypeArgument[] targs;
    int start = ++c.position;
    
    do {
      t = sig.charAt(c.position++);
    } while (t != '$' && t != '<' && t != ';');
    int end = c.position - 1;
    
    if (t == '<') {
      targs = parseTypeArgs(sig, c);
      t = sig.charAt(c.position++);
    } else {
      
      targs = null;
    } 
    ClassType thisClass = ClassType.make(sig, start, end, targs, parent);
    if (t == '$' || t == '.') {
      c.position--;
      return parseClassType2(sig, c, thisClass);
    } 
    return thisClass;
  }
  
  private static TypeArgument[] parseTypeArgs(String sig, Cursor c) throws BadBytecode {
    List<TypeArgument> args = new ArrayList<>();
    char t;
    while ((t = sig.charAt(c.position++)) != '>') {
      TypeArgument ta;
      if (t == '*') {
        ta = new TypeArgument(null, '*');
      } else {
        if (t != '+' && t != '-') {
          t = ' ';
          c.position--;
        } 
        
        ta = new TypeArgument(parseObjectType(sig, c, false), t);
      } 
      
      args.add(ta);
    } 
    
    return args.<TypeArgument>toArray(new TypeArgument[args.size()]);
  }
  
  private static ObjectType parseArray(String sig, Cursor c) throws BadBytecode {
    int dim = 1;
    while (sig.charAt(++c.position) == '[') {
      dim++;
    }
    return new ArrayType(dim, parseType(sig, c));
  }
  
  private static Type parseType(String sig, Cursor c) throws BadBytecode {
    Type t = parseObjectType(sig, c, true);
    if (t == null) {
      t = new BaseType(sig.charAt(c.position++));
    }
    return t;
  }
  
  private static BadBytecode error(String sig) {
    return new BadBytecode("bad signature: " + sig);
  }
}
