package org.springframework.asm.commons;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;

public abstract class AdviceAdapter extends GeneratorAdapter implements Opcodes {
  private static final Object THIS = new Object();
  
  private static final Object OTHER = new Object();
  
  protected int methodAccess;
  
  protected String methodDesc;
  
  private boolean constructor;
  
  private boolean superInitialized;
  
  private ArrayList stackFrame;
  
  private HashMap branches;
  
  public AdviceAdapter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2) {
    super(paramMethodVisitor, paramInt, paramString1, paramString2);
    this.methodAccess = paramInt;
    this.methodDesc = paramString2;
    this.constructor = "<init>".equals(paramString1);
    if (!this.constructor) {
      this.superInitialized = true;
      onMethodEnter();
    } else {
      this.stackFrame = new ArrayList();
      this.branches = new HashMap();
    } 
  }
  
  public void visitLabel(Label paramLabel) {
    this.mv.visitLabel(paramLabel);
    if (this.constructor && this.branches != null) {
      ArrayList arrayList = (ArrayList)this.branches.get(paramLabel);
      if (arrayList != null) {
        this.stackFrame = arrayList;
        this.branches.remove(paramLabel);
      } 
    } 
  }
  
  public void visitInsn(int paramInt) {
    if (this.constructor) {
      Object object1;
      Object object2;
      Object object3;
      Object object4;
      switch (paramInt) {
        case 177:
          onMethodExit(paramInt);
          break;
        case 172:
        case 174:
        case 176:
        case 191:
          popValue();
          popValue();
          onMethodExit(paramInt);
          break;
        case 173:
        case 175:
          popValue();
          popValue();
          onMethodExit(paramInt);
          break;
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 11:
        case 12:
        case 13:
        case 133:
        case 135:
        case 140:
        case 141:
          pushValue(OTHER);
          break;
        case 9:
        case 10:
        case 14:
        case 15:
          pushValue(OTHER);
          pushValue(OTHER);
          break;
        case 46:
        case 48:
        case 50:
        case 51:
        case 52:
        case 53:
        case 87:
        case 96:
        case 98:
        case 100:
        case 102:
        case 104:
        case 106:
        case 108:
        case 110:
        case 112:
        case 114:
        case 120:
        case 121:
        case 122:
        case 123:
        case 124:
        case 125:
        case 126:
        case 128:
        case 130:
        case 136:
        case 137:
        case 142:
        case 144:
        case 149:
        case 150:
        case 194:
        case 195:
          popValue();
          break;
        case 88:
        case 97:
        case 99:
        case 101:
        case 103:
        case 105:
        case 107:
        case 109:
        case 111:
        case 113:
        case 115:
        case 127:
        case 129:
        case 131:
          popValue();
          popValue();
          break;
        case 79:
        case 81:
        case 83:
        case 84:
        case 85:
        case 86:
        case 148:
        case 151:
        case 152:
          popValue();
          popValue();
          popValue();
          break;
        case 80:
        case 82:
          popValue();
          popValue();
          popValue();
          popValue();
          break;
        case 89:
          pushValue(peekValue());
          break;
        case 90:
          object1 = popValue();
          object2 = popValue();
          pushValue(object1);
          pushValue(object2);
          pushValue(object1);
          break;
        case 91:
          object1 = popValue();
          object2 = popValue();
          object3 = popValue();
          pushValue(object1);
          pushValue(object3);
          pushValue(object2);
          pushValue(object1);
          break;
        case 92:
          object1 = popValue();
          object2 = popValue();
          pushValue(object2);
          pushValue(object1);
          pushValue(object2);
          pushValue(object1);
          break;
        case 93:
          object1 = popValue();
          object2 = popValue();
          object3 = popValue();
          pushValue(object2);
          pushValue(object1);
          pushValue(object3);
          pushValue(object2);
          pushValue(object1);
          break;
        case 94:
          object1 = popValue();
          object2 = popValue();
          object3 = popValue();
          object4 = popValue();
          pushValue(object2);
          pushValue(object1);
          pushValue(object4);
          pushValue(object3);
          pushValue(object2);
          pushValue(object1);
          break;
        case 95:
          object1 = popValue();
          object2 = popValue();
          pushValue(object1);
          pushValue(object2);
          break;
      } 
    } else {
      switch (paramInt) {
        case 172:
        case 173:
        case 174:
        case 175:
        case 176:
        case 177:
        case 191:
          onMethodExit(paramInt);
          break;
      } 
    } 
    this.mv.visitInsn(paramInt);
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    super.visitVarInsn(paramInt1, paramInt2);
    if (this.constructor)
      switch (paramInt1) {
        case 21:
        case 23:
          pushValue(OTHER);
          break;
        case 22:
        case 24:
          pushValue(OTHER);
          pushValue(OTHER);
          break;
        case 25:
          pushValue((paramInt2 == 0) ? THIS : OTHER);
          break;
        case 54:
        case 56:
        case 58:
          popValue();
          break;
        case 55:
        case 57:
          popValue();
          popValue();
          break;
      }  
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    if (this.constructor) {
      char c = paramString3.charAt(0);
      boolean bool = (c == 'J' || c == 'D') ? true : false;
      switch (paramInt) {
        case 178:
          pushValue(OTHER);
          if (bool)
            pushValue(OTHER); 
          return;
        case 179:
          popValue();
          if (bool)
            popValue(); 
          return;
        case 181:
          popValue();
          if (bool) {
            popValue();
            popValue();
          } 
          return;
      } 
      if (bool)
        pushValue(OTHER); 
    } 
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    this.mv.visitIntInsn(paramInt1, paramInt2);
    if (this.constructor)
      switch (paramInt1) {
        case 16:
        case 17:
          pushValue(OTHER);
          break;
      }  
  }
  
  public void visitLdcInsn(Object paramObject) {
    this.mv.visitLdcInsn(paramObject);
    if (this.constructor) {
      pushValue(OTHER);
      if (paramObject instanceof Double || paramObject instanceof Long)
        pushValue(OTHER); 
    } 
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    this.mv.visitMultiANewArrayInsn(paramString, paramInt);
    if (this.constructor) {
      for (byte b = 0; b < paramInt; b++)
        popValue(); 
      pushValue(OTHER);
    } 
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    this.mv.visitTypeInsn(paramInt, paramString);
    if (this.constructor && paramInt == 187)
      pushValue(OTHER); 
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
    if (this.constructor) {
      Type[] arrayOfType = Type.getArgumentTypes(paramString3);
      for (byte b = 0; b < arrayOfType.length; b++) {
        popValue();
        if (arrayOfType[b].getSize() == 2)
          popValue(); 
      } 
      switch (paramInt) {
        case 182:
        case 185:
          popValue();
          break;
        case 183:
          object = popValue();
          if (object == THIS && !this.superInitialized) {
            onMethodEnter();
            this.superInitialized = true;
            this.constructor = false;
          } 
          break;
      } 
      Object object = Type.getReturnType(paramString3);
      if (object != Type.VOID_TYPE) {
        pushValue(OTHER);
        if (object.getSize() == 2)
          pushValue(OTHER); 
      } 
    } 
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    this.mv.visitJumpInsn(paramInt, paramLabel);
    if (this.constructor) {
      switch (paramInt) {
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 198:
        case 199:
          popValue();
          break;
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
          popValue();
          popValue();
          break;
        case 168:
          pushValue(OTHER);
          break;
      } 
      addBranch(paramLabel);
    } 
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfint, Label[] paramArrayOfLabel) {
    this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfint, paramArrayOfLabel);
    if (this.constructor) {
      popValue();
      addBranches(paramLabel, paramArrayOfLabel);
    } 
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel) {
    this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramArrayOfLabel);
    if (this.constructor) {
      popValue();
      addBranches(paramLabel, paramArrayOfLabel);
    } 
  }
  
  private void addBranches(Label paramLabel, Label[] paramArrayOfLabel) {
    addBranch(paramLabel);
    for (byte b = 0; b < paramArrayOfLabel.length; b++)
      addBranch(paramArrayOfLabel[b]); 
  }
  
  private void addBranch(Label paramLabel) {
    if (this.branches.containsKey(paramLabel))
      return; 
    ArrayList arrayList = new ArrayList();
    arrayList.addAll(this.stackFrame);
    this.branches.put(paramLabel, arrayList);
  }
  
  private Object popValue() {
    return this.stackFrame.remove(this.stackFrame.size() - 1);
  }
  
  private Object peekValue() {
    return this.stackFrame.get(this.stackFrame.size() - 1);
  }
  
  private void pushValue(Object paramObject) {
    this.stackFrame.add(paramObject);
  }
  
  protected abstract void onMethodEnter();
  
  protected abstract void onMethodExit(int paramInt);
}
