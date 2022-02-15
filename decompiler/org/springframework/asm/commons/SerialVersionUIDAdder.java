package org.springframework.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.asm.ClassAdapter;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;

public class SerialVersionUIDAdder extends ClassAdapter {
  protected boolean computeSVUID;
  
  protected boolean hasSVUID;
  
  protected int access;
  
  protected String name;
  
  protected String[] interfaces;
  
  protected Collection svuidFields = new ArrayList();
  
  protected boolean hasStaticInitializer;
  
  protected Collection svuidConstructors = new ArrayList();
  
  protected Collection svuidMethods = new ArrayList();
  
  public SerialVersionUIDAdder(ClassVisitor paramClassVisitor) {
    super(paramClassVisitor);
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.computeSVUID = ((paramInt2 & 0x200) == 0);
    if (this.computeSVUID) {
      this.name = paramString1;
      this.access = paramInt2;
      this.interfaces = paramArrayOfString;
    } 
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    if (this.computeSVUID) {
      if (paramString1.equals("<clinit>"))
        this.hasStaticInitializer = true; 
      int i = paramInt & 0xD3F;
      if ((paramInt & 0x2) == 0)
        if (paramString1.equals("<init>")) {
          this.svuidConstructors.add(new SerialVersionUIDAdder$Item(paramString1, i, paramString2));
        } else if (!paramString1.equals("<clinit>")) {
          this.svuidMethods.add(new SerialVersionUIDAdder$Item(paramString1, i, paramString2));
        }  
    } 
    return this.cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    if (this.computeSVUID) {
      if (paramString1.equals("serialVersionUID")) {
        this.computeSVUID = false;
        this.hasSVUID = true;
      } 
      int i = paramInt & 0xDF;
      if ((paramInt & 0x2) == 0 || (paramInt & 0x88) == 0)
        this.svuidFields.add(new SerialVersionUIDAdder$Item(paramString1, i, paramString2)); 
    } 
    return super.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public void visitEnd() {
    if (this.computeSVUID && !this.hasSVUID)
      try {
        this.cv.visitField(24, "serialVersionUID", "J", null, new Long(computeSVUID()));
      } catch (Throwable throwable) {
        throw new RuntimeException("Error while computing SVUID for " + this.name, throwable);
      }  
    super.visitEnd();
  }
  
  protected long computeSVUID() throws IOException {
    if (this.hasSVUID)
      return 0L; 
    ByteArrayOutputStream byteArrayOutputStream = null;
    DataOutputStream dataOutputStream = null;
    long l = 0L;
    try {
      byteArrayOutputStream = new ByteArrayOutputStream();
      dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeUTF(this.name.replace('/', '.'));
      dataOutputStream.writeInt(this.access & 0x611);
      Arrays.sort((Object[])this.interfaces);
      for (byte b = 0; b < this.interfaces.length; b++)
        dataOutputStream.writeUTF(this.interfaces[b].replace('/', '.')); 
      writeItems(this.svuidFields, dataOutputStream, false);
      if (this.hasStaticInitializer) {
        dataOutputStream.writeUTF("<clinit>");
        dataOutputStream.writeInt(8);
        dataOutputStream.writeUTF("()V");
      } 
      writeItems(this.svuidConstructors, dataOutputStream, true);
      writeItems(this.svuidMethods, dataOutputStream, true);
      dataOutputStream.flush();
      byte[] arrayOfByte = computeSHAdigest(byteArrayOutputStream.toByteArray());
      for (int i = Math.min(arrayOfByte.length, 8) - 1; i >= 0; i--)
        l = l << 8L | (arrayOfByte[i] & 0xFF); 
    } finally {
      if (dataOutputStream != null)
        dataOutputStream.close(); 
    } 
    return l;
  }
  
  protected byte[] computeSHAdigest(byte[] paramArrayOfbyte) {
    try {
      return MessageDigest.getInstance("SHA").digest(paramArrayOfbyte);
    } catch (Exception exception) {
      throw new UnsupportedOperationException(exception);
    } 
  }
  
  private void writeItems(Collection paramCollection, DataOutputStream paramDataOutputStream, boolean paramBoolean) throws IOException {
    int i = paramCollection.size();
    SerialVersionUIDAdder$Item[] arrayOfSerialVersionUIDAdder$Item = (SerialVersionUIDAdder$Item[])paramCollection.toArray((Object[])new SerialVersionUIDAdder$Item[i]);
    Arrays.sort((Object[])arrayOfSerialVersionUIDAdder$Item);
    for (byte b = 0; b < i; b++) {
      paramDataOutputStream.writeUTF((arrayOfSerialVersionUIDAdder$Item[b]).name);
      paramDataOutputStream.writeInt((arrayOfSerialVersionUIDAdder$Item[b]).access);
      paramDataOutputStream.writeUTF(paramBoolean ? (arrayOfSerialVersionUIDAdder$Item[b]).desc.replace('/', '.') : (arrayOfSerialVersionUIDAdder$Item[b]).desc);
    } 
  }
}
