package javassist.bytecode.stackmap;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;



























public class TypedBlock
  extends BasicBlock
{
  public int stackTop;
  public int numLocals;
  public TypeData[] localsTypes;
  public TypeData[] stackTypes;
  
  public static TypedBlock[] makeBlocks(MethodInfo minfo, CodeAttribute ca, boolean optimize) throws BadBytecode {
    TypedBlock[] blocks = (TypedBlock[])(new Maker()).make(minfo);
    if (optimize && blocks.length < 2 && (
      blocks.length == 0 || (blocks[0]).incoming == 0)) {
      return null;
    }
    ConstPool pool = minfo.getConstPool();
    boolean isStatic = ((minfo.getAccessFlags() & 0x8) != 0);
    blocks[0].initFirstBlock(ca.getMaxStack(), ca.getMaxLocals(), pool
        .getClassName(), minfo.getDescriptor(), isStatic, minfo
        .isConstructor());
    return blocks;
  }
  
  protected TypedBlock(int pos) {
    super(pos);
    this.localsTypes = null;
  }

  
  protected void toString2(StringBuffer sbuf) {
    super.toString2(sbuf);
    sbuf.append(",\n stack={");
    printTypes(sbuf, this.stackTop, this.stackTypes);
    sbuf.append("}, locals={");
    printTypes(sbuf, this.numLocals, this.localsTypes);
    sbuf.append('}');
  }

  
  private void printTypes(StringBuffer sbuf, int size, TypeData[] types) {
    if (types == null) {
      return;
    }
    for (int i = 0; i < size; i++) {
      if (i > 0) {
        sbuf.append(", ");
      }
      TypeData td = types[i];
      sbuf.append((td == null) ? "<>" : td.toString());
    } 
  }
  
  public boolean alreadySet() {
    return (this.localsTypes != null);
  }


  
  public void setStackMap(int st, TypeData[] stack, int nl, TypeData[] locals) throws BadBytecode {
    this.stackTop = st;
    this.stackTypes = stack;
    this.numLocals = nl;
    this.localsTypes = locals;
  }



  
  public void resetNumLocals() {
    if (this.localsTypes != null) {
      int nl = this.localsTypes.length;
      while (nl > 0 && this.localsTypes[nl - 1].isBasicType() == TypeTag.TOP && (
        nl <= 1 || 
        !this.localsTypes[nl - 2].is2WordType()))
      {

        
        nl--;
      }
      
      this.numLocals = nl;
    } 
  }
  
  public static class Maker
    extends BasicBlock.Maker {
    protected BasicBlock makeBlock(int pos) {
      return new TypedBlock(pos);
    }

    
    protected BasicBlock[] makeArray(int size) {
      return (BasicBlock[])new TypedBlock[size];
    }
  }













  
  void initFirstBlock(int maxStack, int maxLocals, String className, String methodDesc, boolean isStatic, boolean isConstructor) throws BadBytecode {
    if (methodDesc.charAt(0) != '(') {
      throw new BadBytecode("no method descriptor: " + methodDesc);
    }
    this.stackTop = 0;
    this.stackTypes = TypeData.make(maxStack);
    TypeData[] locals = TypeData.make(maxLocals);
    if (isConstructor) {
      locals[0] = new TypeData.UninitThis(className);
    } else if (!isStatic) {
      locals[0] = new TypeData.ClassName(className);
    } 
    int n = isStatic ? -1 : 0;
    int i = 1;
    try {
      while ((i = descToTag(methodDesc, i, ++n, locals)) > 0) {
        if (locals[n].is2WordType())
          locals[++n] = TypeTag.TOP; 
      } 
    } catch (StringIndexOutOfBoundsException e) {
      throw new BadBytecode("bad method descriptor: " + methodDesc);
    } 

    
    this.numLocals = n;
    this.localsTypes = locals;
  }



  
  private static int descToTag(String desc, int i, int n, TypeData[] types) throws BadBytecode {
    int i0 = i;
    int arrayDim = 0;
    char c = desc.charAt(i);
    if (c == ')') {
      return 0;
    }
    while (c == '[') {
      arrayDim++;
      c = desc.charAt(++i);
    } 
    
    if (c == 'L') {
      int i2 = desc.indexOf(';', ++i);
      if (arrayDim > 0) {
        types[n] = new TypeData.ClassName(desc.substring(i0, ++i2));
      } else {
        types[n] = new TypeData.ClassName(desc.substring(i0 + 1, ++i2 - 1)
            .replace('/', '.'));
      }  return i2;
    } 
    if (arrayDim > 0) {
      types[n] = new TypeData.ClassName(desc.substring(i0, ++i));
      return i;
    } 
    
    TypeData t = toPrimitiveTag(c);
    if (t == null) {
      throw new BadBytecode("bad method descriptor: " + desc);
    }
    types[n] = t;
    return i + 1;
  }

  
  private static TypeData toPrimitiveTag(char c) {
    switch (c) {
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        return TypeTag.INTEGER;
      case 'J':
        return TypeTag.LONG;
      case 'F':
        return TypeTag.FLOAT;
      case 'D':
        return TypeTag.DOUBLE;
    } 
    
    return null;
  }

  
  public static String getRetType(String desc) {
    int i = desc.indexOf(')');
    if (i < 0) {
      return "java.lang.Object";
    }
    char c = desc.charAt(i + 1);
    if (c == '[')
      return desc.substring(i + 1); 
    if (c == 'L') {
      return desc.substring(i + 2, desc.length() - 1).replace('/', '.');
    }
    return "java.lang.Object";
  }
}
