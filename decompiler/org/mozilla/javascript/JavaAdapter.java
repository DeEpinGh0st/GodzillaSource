package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import org.mozilla.classfile.ClassFileWriter;


public final class JavaAdapter
  implements IdFunctionCall
{
  static class JavaAdapterSignature
  {
    Class<?> superClass;
    Class<?>[] interfaces;
    ObjToIntMap names;
    
    JavaAdapterSignature(Class<?> superClass, Class<?>[] interfaces, ObjToIntMap names) {
      this.superClass = superClass;
      this.interfaces = interfaces;
      this.names = names;
    }


    
    public boolean equals(Object obj) {
      if (!(obj instanceof JavaAdapterSignature))
        return false; 
      JavaAdapterSignature sig = (JavaAdapterSignature)obj;
      if (this.superClass != sig.superClass)
        return false; 
      if (this.interfaces != sig.interfaces) {
        if (this.interfaces.length != sig.interfaces.length)
          return false; 
        for (int i = 0; i < this.interfaces.length; i++) {
          if (this.interfaces[i] != sig.interfaces[i])
            return false; 
        } 
      }  if (this.names.size() != sig.names.size())
        return false; 
      ObjToIntMap.Iterator iter = new ObjToIntMap.Iterator(this.names);
      iter.start(); for (; !iter.done(); iter.next()) {
        String name = (String)iter.getKey();
        int arity = iter.getValue();
        if (arity != sig.names.get(name, arity + 1))
          return false; 
      } 
      return true;
    }


    
    public int hashCode() {
      return this.superClass.hashCode() + Arrays.hashCode((Object[])this.interfaces) ^ this.names.size();
    }
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    JavaAdapter obj = new JavaAdapter();
    IdFunctionObject ctor = new IdFunctionObject(obj, FTAG, 1, "JavaAdapter", 1, scope);
    
    ctor.markAsConstructor((Scriptable)null);
    if (sealed) {
      ctor.sealObject();
    }
    ctor.exportAsScopeProperty();
  }


  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (f.hasTag(FTAG) && 
      f.methodId() == 1) {
      return js_createAdapter(cx, scope, args);
    }
    
    throw f.unknown();
  }

  
  public static Object convertResult(Object result, Class<?> c) {
    if (result == Undefined.instance && c != ScriptRuntime.ObjectClass && c != ScriptRuntime.StringClass)
    {


      
      return null;
    }
    return Context.jsToJava(result, c);
  }

  
  public static Scriptable createAdapterWrapper(Scriptable obj, Object adapter) {
    Scriptable scope = ScriptableObject.getTopLevelScope(obj);
    NativeJavaObject res = new NativeJavaObject(scope, adapter, null, true);
    res.setPrototype(obj);
    return res;
  }


  
  public static Object getAdapterSelf(Class<?> adapterClass, Object adapter) throws NoSuchFieldException, IllegalAccessException {
    Field self = adapterClass.getDeclaredField("self");
    return self.get(adapter);
  }

  
  static Object js_createAdapter(Context cx, Scriptable scope, Object[] args) {
    int N = args.length;
    if (N == 0) {
      throw ScriptRuntime.typeError0("msg.adapter.zero.args");
    }



    
    int classCount;


    
    for (classCount = 0; classCount < N - 1; classCount++) {
      Object arg = args[classCount];



      
      if (arg instanceof NativeObject) {
        break;
      }
      if (!(arg instanceof NativeJavaClass)) {
        throw ScriptRuntime.typeError2("msg.not.java.class.arg", String.valueOf(classCount), ScriptRuntime.toString(arg));
      }
    } 

    
    Class<?> superClass = null;
    Class<?>[] intfs = new Class[classCount];
    int interfaceCount = 0;
    for (int i = 0; i < classCount; i++) {
      Class<?> c = ((NativeJavaClass)args[i]).getClassObject();
      if (!c.isInterface()) {
        if (superClass != null) {
          throw ScriptRuntime.typeError2("msg.only.one.super", superClass.getName(), c.getName());
        }
        
        superClass = c;
      } else {
        intfs[interfaceCount++] = c;
      } 
    } 
    
    if (superClass == null) {
      superClass = ScriptRuntime.ObjectClass;
    }
    
    Class<?>[] interfaces = new Class[interfaceCount];
    System.arraycopy(intfs, 0, interfaces, 0, interfaceCount);
    
    Scriptable obj = ScriptableObject.ensureScriptable(args[classCount]);
    
    Class<?> adapterClass = getAdapterClass(scope, superClass, interfaces, obj);

    
    int argsCount = N - classCount - 1; try {
      Object adapter;
      if (argsCount > 0) {


        
        Object[] ctorArgs = new Object[argsCount + 2];
        ctorArgs[0] = obj;
        ctorArgs[1] = cx.getFactory();
        System.arraycopy(args, classCount + 1, ctorArgs, 2, argsCount);
        
        NativeJavaClass classWrapper = new NativeJavaClass(scope, adapterClass, true);
        
        NativeJavaMethod ctors = classWrapper.members.ctors;
        int index = ctors.findCachedFunction(cx, ctorArgs);
        if (index < 0) {
          String sig = NativeJavaMethod.scriptSignature(args);
          throw Context.reportRuntimeError2("msg.no.java.ctor", adapterClass.getName(), sig);
        } 


        
        adapter = NativeJavaClass.constructInternal(ctorArgs, ctors.methods[index]);
      } else {
        Class<?>[] ctorParms = new Class[] { ScriptRuntime.ScriptableClass, ScriptRuntime.ContextFactoryClass };


        
        Object[] ctorArgs = { obj, cx.getFactory() };
        adapter = adapterClass.getConstructor(ctorParms).newInstance(ctorArgs);
      } 
      
      Object self = getAdapterSelf(adapterClass, adapter);
      
      if (self instanceof Wrapper) {
        Object unwrapped = ((Wrapper)self).unwrap();
        if (unwrapped instanceof Scriptable) {
          if (unwrapped instanceof ScriptableObject) {
            ScriptRuntime.setObjectProtoAndParent((ScriptableObject)unwrapped, scope);
          }
          
          return unwrapped;
        } 
      } 
      return self;
    } catch (Exception ex) {
      throw Context.throwAsScriptRuntimeEx(ex);
    } 
  }




  
  public static void writeAdapterObject(Object javaObject, ObjectOutputStream out) throws IOException {
    Class<?> cl = javaObject.getClass();
    out.writeObject(cl.getSuperclass().getName());
    
    Class<?>[] interfaces = cl.getInterfaces();
    String[] interfaceNames = new String[interfaces.length];
    
    for (int i = 0; i < interfaces.length; i++) {
      interfaceNames[i] = interfaces[i].getName();
    }
    out.writeObject(interfaceNames);

    
    try { Object delegee = cl.getField("delegee").get(javaObject);
      out.writeObject(delegee);
      return; }
    catch (IllegalAccessException e) {  }
    catch (NoSuchFieldException e) {}
    
    throw new IOException();
  }




  
  public static Object readAdapterObject(Scriptable self, ObjectInputStream in) throws IOException, ClassNotFoundException {
    ContextFactory factory;
    Context cx = Context.getCurrentContext();
    if (cx != null) {
      factory = cx.getFactory();
    } else {
      factory = null;
    } 
    
    Class<?> superClass = Class.forName((String)in.readObject());
    
    String[] interfaceNames = (String[])in.readObject();
    Class<?>[] interfaces = new Class[interfaceNames.length];
    
    for (int i = 0; i < interfaceNames.length; i++) {
      interfaces[i] = Class.forName(interfaceNames[i]);
    }
    Scriptable delegee = (Scriptable)in.readObject();
    
    Class<?> adapterClass = getAdapterClass(self, superClass, interfaces, delegee);

    
    Class<?>[] ctorParms = new Class[] { ScriptRuntime.ContextFactoryClass, ScriptRuntime.ScriptableClass, ScriptRuntime.ScriptableClass };



    
    Object[] ctorArgs = { factory, delegee, self };
    
    try { return adapterClass.getConstructor(ctorParms).newInstance(ctorArgs); }
    catch (InstantiationException e) {  }
    catch (IllegalAccessException e) {  }
    catch (InvocationTargetException e) {  }
    catch (NoSuchMethodException e) {}

    
    throw new ClassNotFoundException("adapter");
  }

  
  private static ObjToIntMap getObjectFunctionNames(Scriptable obj) {
    Object[] ids = ScriptableObject.getPropertyIds(obj);
    ObjToIntMap map = new ObjToIntMap(ids.length);
    for (int i = 0; i != ids.length; i++) {
      if (ids[i] instanceof String) {
        
        String id = (String)ids[i];
        Object value = ScriptableObject.getProperty(obj, id);
        if (value instanceof Function) {
          Function f = (Function)value;
          int length = ScriptRuntime.toInt32(ScriptableObject.getProperty(f, "length"));
          
          if (length < 0) {
            length = 0;
          }
          map.put(id, length);
        } 
      } 
    }  return map;
  }


  
  private static Class<?> getAdapterClass(Scriptable scope, Class<?> superClass, Class<?>[] interfaces, Scriptable obj) {
    ClassCache cache = ClassCache.get(scope);
    Map<JavaAdapterSignature, Class<?>> generated = cache.getInterfaceAdapterCacheMap();

    
    ObjToIntMap names = getObjectFunctionNames(obj);
    
    JavaAdapterSignature sig = new JavaAdapterSignature(superClass, interfaces, names);
    Class<?> adapterClass = generated.get(sig);
    if (adapterClass == null) {
      String adapterName = "adapter" + cache.newClassSerialNumber();
      byte[] code = createAdapterCode(names, adapterName, superClass, interfaces, null);

      
      adapterClass = loadAdapterClass(adapterName, code);
      if (cache.isCachingEnabled()) {
        generated.put(sig, adapterClass);
      }
    } 
    return adapterClass;
  }































































  
  public static byte[] createAdapterCode(ObjToIntMap functionNames, String adapterName, Class<?> superClass, Class<?>[] interfaces, String scriptClassName) {
    // Byte code:
    //   0: new org/mozilla/classfile/ClassFileWriter
    //   3: dup
    //   4: aload_1
    //   5: aload_2
    //   6: invokevirtual getName : ()Ljava/lang/String;
    //   9: ldc '<adapter>'
    //   11: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   14: astore #5
    //   16: aload #5
    //   18: ldc 'factory'
    //   20: ldc 'Lorg/mozilla/javascript/ContextFactory;'
    //   22: bipush #17
    //   24: invokevirtual addField : (Ljava/lang/String;Ljava/lang/String;S)V
    //   27: aload #5
    //   29: ldc 'delegee'
    //   31: ldc 'Lorg/mozilla/javascript/Scriptable;'
    //   33: bipush #17
    //   35: invokevirtual addField : (Ljava/lang/String;Ljava/lang/String;S)V
    //   38: aload #5
    //   40: ldc 'self'
    //   42: ldc 'Lorg/mozilla/javascript/Scriptable;'
    //   44: bipush #17
    //   46: invokevirtual addField : (Ljava/lang/String;Ljava/lang/String;S)V
    //   49: aload_3
    //   50: ifnonnull -> 57
    //   53: iconst_0
    //   54: goto -> 59
    //   57: aload_3
    //   58: arraylength
    //   59: istore #6
    //   61: iconst_0
    //   62: istore #7
    //   64: iload #7
    //   66: iload #6
    //   68: if_icmpge -> 96
    //   71: aload_3
    //   72: iload #7
    //   74: aaload
    //   75: ifnull -> 90
    //   78: aload #5
    //   80: aload_3
    //   81: iload #7
    //   83: aaload
    //   84: invokevirtual getName : ()Ljava/lang/String;
    //   87: invokevirtual addInterface : (Ljava/lang/String;)V
    //   90: iinc #7, 1
    //   93: goto -> 64
    //   96: aload_2
    //   97: invokevirtual getName : ()Ljava/lang/String;
    //   100: bipush #46
    //   102: bipush #47
    //   104: invokevirtual replace : (CC)Ljava/lang/String;
    //   107: astore #7
    //   109: aload_2
    //   110: invokevirtual getDeclaredConstructors : ()[Ljava/lang/reflect/Constructor;
    //   113: astore #8
    //   115: aload #8
    //   117: astore #9
    //   119: aload #9
    //   121: arraylength
    //   122: istore #10
    //   124: iconst_0
    //   125: istore #11
    //   127: iload #11
    //   129: iload #10
    //   131: if_icmpge -> 180
    //   134: aload #9
    //   136: iload #11
    //   138: aaload
    //   139: astore #12
    //   141: aload #12
    //   143: invokevirtual getModifiers : ()I
    //   146: istore #13
    //   148: iload #13
    //   150: invokestatic isPublic : (I)Z
    //   153: ifne -> 164
    //   156: iload #13
    //   158: invokestatic isProtected : (I)Z
    //   161: ifeq -> 174
    //   164: aload #5
    //   166: aload_1
    //   167: aload #7
    //   169: aload #12
    //   171: invokestatic generateCtor : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;Ljava/lang/reflect/Constructor;)V
    //   174: iinc #11, 1
    //   177: goto -> 127
    //   180: aload #5
    //   182: aload_1
    //   183: aload #7
    //   185: invokestatic generateSerialCtor : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;)V
    //   188: aload #4
    //   190: ifnull -> 203
    //   193: aload #5
    //   195: aload_1
    //   196: aload #7
    //   198: aload #4
    //   200: invokestatic generateEmptyCtor : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   203: new org/mozilla/javascript/ObjToIntMap
    //   206: dup
    //   207: invokespecial <init> : ()V
    //   210: astore #9
    //   212: new org/mozilla/javascript/ObjToIntMap
    //   215: dup
    //   216: invokespecial <init> : ()V
    //   219: astore #10
    //   221: iconst_0
    //   222: istore #11
    //   224: iload #11
    //   226: iload #6
    //   228: if_icmpge -> 406
    //   231: aload_3
    //   232: iload #11
    //   234: aaload
    //   235: invokevirtual getMethods : ()[Ljava/lang/reflect/Method;
    //   238: astore #12
    //   240: iconst_0
    //   241: istore #13
    //   243: iload #13
    //   245: aload #12
    //   247: arraylength
    //   248: if_icmpge -> 400
    //   251: aload #12
    //   253: iload #13
    //   255: aaload
    //   256: astore #14
    //   258: aload #14
    //   260: invokevirtual getModifiers : ()I
    //   263: istore #15
    //   265: iload #15
    //   267: invokestatic isStatic : (I)Z
    //   270: ifne -> 394
    //   273: iload #15
    //   275: invokestatic isFinal : (I)Z
    //   278: ifeq -> 284
    //   281: goto -> 394
    //   284: aload #14
    //   286: invokevirtual getName : ()Ljava/lang/String;
    //   289: astore #16
    //   291: aload #14
    //   293: invokevirtual getParameterTypes : ()[Ljava/lang/Class;
    //   296: astore #17
    //   298: aload_0
    //   299: aload #16
    //   301: invokevirtual has : (Ljava/lang/Object;)Z
    //   304: ifne -> 321
    //   307: aload_2
    //   308: aload #16
    //   310: aload #17
    //   312: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   315: pop
    //   316: goto -> 394
    //   319: astore #18
    //   321: aload #14
    //   323: aload #17
    //   325: invokestatic getMethodSignature : (Ljava/lang/reflect/Method;[Ljava/lang/Class;)Ljava/lang/String;
    //   328: astore #18
    //   330: new java/lang/StringBuilder
    //   333: dup
    //   334: invokespecial <init> : ()V
    //   337: aload #16
    //   339: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: aload #18
    //   344: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   347: invokevirtual toString : ()Ljava/lang/String;
    //   350: astore #19
    //   352: aload #9
    //   354: aload #19
    //   356: invokevirtual has : (Ljava/lang/Object;)Z
    //   359: ifne -> 394
    //   362: aload #5
    //   364: aload_1
    //   365: aload #16
    //   367: aload #17
    //   369: aload #14
    //   371: invokevirtual getReturnType : ()Ljava/lang/Class;
    //   374: iconst_1
    //   375: invokestatic generateMethod : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;Z)V
    //   378: aload #9
    //   380: aload #19
    //   382: iconst_0
    //   383: invokevirtual put : (Ljava/lang/Object;I)V
    //   386: aload #10
    //   388: aload #16
    //   390: iconst_0
    //   391: invokevirtual put : (Ljava/lang/Object;I)V
    //   394: iinc #13, 1
    //   397: goto -> 243
    //   400: iinc #11, 1
    //   403: goto -> 224
    //   406: aload_2
    //   407: invokestatic getOverridableMethods : (Ljava/lang/Class;)[Ljava/lang/reflect/Method;
    //   410: astore #11
    //   412: iconst_0
    //   413: istore #12
    //   415: iload #12
    //   417: aload #11
    //   419: arraylength
    //   420: if_icmpge -> 575
    //   423: aload #11
    //   425: iload #12
    //   427: aaload
    //   428: astore #13
    //   430: aload #13
    //   432: invokevirtual getModifiers : ()I
    //   435: istore #14
    //   437: iload #14
    //   439: invokestatic isAbstract : (I)Z
    //   442: istore #15
    //   444: aload #13
    //   446: invokevirtual getName : ()Ljava/lang/String;
    //   449: astore #16
    //   451: iload #15
    //   453: ifne -> 465
    //   456: aload_0
    //   457: aload #16
    //   459: invokevirtual has : (Ljava/lang/Object;)Z
    //   462: ifeq -> 569
    //   465: aload #13
    //   467: invokevirtual getParameterTypes : ()[Ljava/lang/Class;
    //   470: astore #17
    //   472: aload #13
    //   474: aload #17
    //   476: invokestatic getMethodSignature : (Ljava/lang/reflect/Method;[Ljava/lang/Class;)Ljava/lang/String;
    //   479: astore #18
    //   481: new java/lang/StringBuilder
    //   484: dup
    //   485: invokespecial <init> : ()V
    //   488: aload #16
    //   490: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   493: aload #18
    //   495: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   498: invokevirtual toString : ()Ljava/lang/String;
    //   501: astore #19
    //   503: aload #9
    //   505: aload #19
    //   507: invokevirtual has : (Ljava/lang/Object;)Z
    //   510: ifne -> 569
    //   513: aload #5
    //   515: aload_1
    //   516: aload #16
    //   518: aload #17
    //   520: aload #13
    //   522: invokevirtual getReturnType : ()Ljava/lang/Class;
    //   525: iconst_1
    //   526: invokestatic generateMethod : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;Z)V
    //   529: aload #9
    //   531: aload #19
    //   533: iconst_0
    //   534: invokevirtual put : (Ljava/lang/Object;I)V
    //   537: aload #10
    //   539: aload #16
    //   541: iconst_0
    //   542: invokevirtual put : (Ljava/lang/Object;I)V
    //   545: iload #15
    //   547: ifne -> 569
    //   550: aload #5
    //   552: aload_1
    //   553: aload #7
    //   555: aload #16
    //   557: aload #18
    //   559: aload #17
    //   561: aload #13
    //   563: invokevirtual getReturnType : ()Ljava/lang/Class;
    //   566: invokestatic generateSuper : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;)V
    //   569: iinc #12, 1
    //   572: goto -> 415
    //   575: new org/mozilla/javascript/ObjToIntMap$Iterator
    //   578: dup
    //   579: aload_0
    //   580: invokespecial <init> : (Lorg/mozilla/javascript/ObjToIntMap;)V
    //   583: astore #12
    //   585: aload #12
    //   587: invokevirtual start : ()V
    //   590: aload #12
    //   592: invokevirtual done : ()Z
    //   595: ifne -> 681
    //   598: aload #12
    //   600: invokevirtual getKey : ()Ljava/lang/Object;
    //   603: checkcast java/lang/String
    //   606: astore #13
    //   608: aload #10
    //   610: aload #13
    //   612: invokevirtual has : (Ljava/lang/Object;)Z
    //   615: ifeq -> 621
    //   618: goto -> 673
    //   621: aload #12
    //   623: invokevirtual getValue : ()I
    //   626: istore #14
    //   628: iload #14
    //   630: anewarray java/lang/Class
    //   633: astore #15
    //   635: iconst_0
    //   636: istore #16
    //   638: iload #16
    //   640: iload #14
    //   642: if_icmpge -> 659
    //   645: aload #15
    //   647: iload #16
    //   649: getstatic org/mozilla/javascript/ScriptRuntime.ObjectClass : Ljava/lang/Class;
    //   652: aastore
    //   653: iinc #16, 1
    //   656: goto -> 638
    //   659: aload #5
    //   661: aload_1
    //   662: aload #13
    //   664: aload #15
    //   666: getstatic org/mozilla/javascript/ScriptRuntime.ObjectClass : Ljava/lang/Class;
    //   669: iconst_0
    //   670: invokestatic generateMethod : (Lorg/mozilla/classfile/ClassFileWriter;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;Z)V
    //   673: aload #12
    //   675: invokevirtual next : ()V
    //   678: goto -> 590
    //   681: aload #5
    //   683: invokevirtual toByteArray : ()[B
    //   686: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #347	-> 0
    //   #350	-> 16
    //   #353	-> 27
    //   #356	-> 38
    //   #359	-> 49
    //   #360	-> 61
    //   #361	-> 71
    //   #362	-> 78
    //   #360	-> 90
    //   #365	-> 96
    //   #366	-> 109
    //   #367	-> 115
    //   #368	-> 141
    //   #369	-> 148
    //   #370	-> 164
    //   #367	-> 174
    //   #373	-> 180
    //   #374	-> 188
    //   #375	-> 193
    //   #378	-> 203
    //   #379	-> 212
    //   #382	-> 221
    //   #383	-> 231
    //   #384	-> 240
    //   #385	-> 251
    //   #386	-> 258
    //   #387	-> 265
    //   #388	-> 281
    //   #390	-> 284
    //   #391	-> 291
    //   #392	-> 298
    //   #394	-> 307
    //   #398	-> 316
    //   #399	-> 319
    //   #405	-> 321
    //   #406	-> 330
    //   #407	-> 352
    //   #408	-> 362
    //   #410	-> 378
    //   #411	-> 386
    //   #384	-> 394
    //   #382	-> 400
    //   #420	-> 406
    //   #421	-> 412
    //   #422	-> 423
    //   #423	-> 430
    //   #427	-> 437
    //   #428	-> 444
    //   #429	-> 451
    //   #432	-> 465
    //   #433	-> 472
    //   #434	-> 481
    //   #435	-> 503
    //   #436	-> 513
    //   #438	-> 529
    //   #439	-> 537
    //   #443	-> 545
    //   #444	-> 550
    //   #421	-> 569
    //   #454	-> 575
    //   #455	-> 585
    //   #456	-> 598
    //   #457	-> 608
    //   #458	-> 618
    //   #459	-> 621
    //   #460	-> 628
    //   #461	-> 635
    //   #462	-> 645
    //   #461	-> 653
    //   #463	-> 659
    //   #455	-> 673
    //   #466	-> 681
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   64	32	7	i	I
    //   148	26	13	mod	I
    //   141	33	12	ctor	Ljava/lang/reflect/Constructor;
    //   119	61	9	arr$	[Ljava/lang/reflect/Constructor;
    //   124	56	10	len$	I
    //   127	53	11	i$	I
    //   321	0	18	e	Ljava/lang/NoSuchMethodException;
    //   258	136	14	method	Ljava/lang/reflect/Method;
    //   265	129	15	mods	I
    //   291	103	16	methodName	Ljava/lang/String;
    //   298	96	17	argTypes	[Ljava/lang/Class;
    //   330	64	18	methodSignature	Ljava/lang/String;
    //   352	42	19	methodKey	Ljava/lang/String;
    //   243	157	13	j	I
    //   240	160	12	methods	[Ljava/lang/reflect/Method;
    //   224	182	11	i	I
    //   472	97	17	argTypes	[Ljava/lang/Class;
    //   481	88	18	methodSignature	Ljava/lang/String;
    //   503	66	19	methodKey	Ljava/lang/String;
    //   430	139	13	method	Ljava/lang/reflect/Method;
    //   437	132	14	mods	I
    //   444	125	15	isAbstractMethod	Z
    //   451	118	16	methodName	Ljava/lang/String;
    //   415	160	12	j	I
    //   638	21	16	k	I
    //   608	65	13	functionName	Ljava/lang/String;
    //   628	45	14	length	I
    //   635	38	15	parms	[Ljava/lang/Class;
    //   0	687	0	functionNames	Lorg/mozilla/javascript/ObjToIntMap;
    //   0	687	1	adapterName	Ljava/lang/String;
    //   0	687	2	superClass	Ljava/lang/Class;
    //   0	687	3	interfaces	[Ljava/lang/Class;
    //   0	687	4	scriptClassName	Ljava/lang/String;
    //   16	671	5	cfw	Lorg/mozilla/classfile/ClassFileWriter;
    //   61	626	6	interfacesCount	I
    //   109	578	7	superName	Ljava/lang/String;
    //   115	572	8	ctors	[Ljava/lang/reflect/Constructor;
    //   212	475	9	generatedOverrides	Lorg/mozilla/javascript/ObjToIntMap;
    //   221	466	10	generatedMethods	Lorg/mozilla/javascript/ObjToIntMap;
    //   412	275	11	methods	[Ljava/lang/reflect/Method;
    //   585	102	12	iter	Lorg/mozilla/javascript/ObjToIntMap$Iterator;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   141	33	12	ctor	Ljava/lang/reflect/Constructor<*>;
    //   298	96	17	argTypes	[Ljava/lang/Class<*>;
    //   472	97	17	argTypes	[Ljava/lang/Class<*>;
    //   635	38	15	parms	[Ljava/lang/Class<*>;
    //   0	687	2	superClass	Ljava/lang/Class<*>;
    //   0	687	3	interfaces	[Ljava/lang/Class<*>;
    //   115	572	8	ctors	[Ljava/lang/reflect/Constructor<*>;
    // Exception table:
    //   from	to	target	type
    //   307	316	319	java/lang/NoSuchMethodException
  }






























































  
  static Method[] getOverridableMethods(Class<?> clazz) {
    ArrayList<Method> list = new ArrayList<Method>();
    HashSet<String> skip = new HashSet<String>();

    
    Class<?> c;
    
    for (c = clazz; c != null; c = c.getSuperclass()) {
      appendOverridableMethods(c, list, skip);
    }
    for (c = clazz; c != null; c = c.getSuperclass()) {
      for (Class<?> intf : c.getInterfaces())
        appendOverridableMethods(intf, list, skip); 
    } 
    return list.<Method>toArray(new Method[list.size()]);
  }


  
  private static void appendOverridableMethods(Class<?> c, ArrayList<Method> list, HashSet<String> skip) {
    Method[] methods = c.getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      String methodKey = methods[i].getName() + getMethodSignature(methods[i], methods[i].getParameterTypes());

      
      if (!skip.contains(methodKey)) {
        
        int mods = methods[i].getModifiers();
        if (!Modifier.isStatic(mods))
        {
          if (Modifier.isFinal(mods)) {

            
            skip.add(methodKey);
          
          }
          else if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
            list.add(methods[i]);
            skip.add(methodKey);
          }  } 
      } 
    } 
  }
  
  static Class<?> loadAdapterClass(String className, byte[] classBytes) {
    Object staticDomain;
    Class<?> domainClass = SecurityController.getStaticSecurityDomainClass();
    if (domainClass == CodeSource.class || domainClass == ProtectionDomain.class) {
      
      ProtectionDomain protectionDomain = SecurityUtilities.getScriptProtectionDomain();
      if (protectionDomain == null) {
        protectionDomain = JavaAdapter.class.getProtectionDomain();
      }
      if (domainClass == CodeSource.class) {
        staticDomain = (protectionDomain == null) ? null : protectionDomain.getCodeSource();
      } else {
        
        staticDomain = protectionDomain;
      } 
    } else {
      
      staticDomain = null;
    } 
    GeneratedClassLoader loader = SecurityController.createLoader(null, staticDomain);
    
    Class<?> result = loader.defineClass(className, classBytes);
    loader.linkClass(result);
    return result;
  }

  
  public static Function getFunction(Scriptable obj, String functionName) {
    Object x = ScriptableObject.getProperty(obj, functionName);
    if (x == Scriptable.NOT_FOUND)
    {



      
      return null;
    }
    if (!(x instanceof Function)) {
      throw ScriptRuntime.notFunctionError(x, functionName);
    }
    return (Function)x;
  }








  
  public static Object callMethod(ContextFactory factory, final Scriptable thisObj, final Function f, final Object[] args, final long argsToWrap) {
    if (f == null)
    {
      return null;
    }
    if (factory == null) {
      factory = ContextFactory.getGlobal();
    }
    
    final Scriptable scope = f.getParentScope();
    if (argsToWrap == 0L) {
      return Context.call(factory, f, scope, thisObj, args);
    }
    
    Context cx = Context.getCurrentContext();
    if (cx != null) {
      return doCall(cx, scope, thisObj, f, args, argsToWrap);
    }
    return factory.call(new ContextAction()
        {
          public Object run(Context cx) {
            return JavaAdapter.doCall(cx, scope, thisObj, f, args, argsToWrap);
          }
        });
  }





  
  private static Object doCall(Context cx, Scriptable scope, Scriptable thisObj, Function f, Object[] args, long argsToWrap) {
    for (int i = 0; i != args.length; i++) {
      if (0L != (argsToWrap & (1 << i))) {
        Object arg = args[i];
        if (!(arg instanceof Scriptable)) {
          args[i] = cx.getWrapFactory().wrap(cx, scope, arg, null);
        }
      } 
    } 
    
    return f.call(cx, scope, thisObj, args);
  }

  
  public static Scriptable runScript(final Script script) {
    return (Scriptable)ContextFactory.getGlobal().call(new ContextAction()
        {
          public Object run(Context cx)
          {
            ScriptableObject global = ScriptRuntime.getGlobal(cx);
            script.exec(cx, global);
            return global;
          }
        });
  }


  
  private static void generateCtor(ClassFileWriter cfw, String adapterName, String superName, Constructor<?> superCtor) {
    short locals = 3;
    Class<?>[] parameters = superCtor.getParameterTypes();


    
    if (parameters.length == 0) {
      cfw.startMethod("<init>", "(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/ContextFactory;)V", (short)1);




      
      cfw.add(42);
      cfw.addInvoke(183, superName, "<init>", "()V");
    } else {
      StringBuilder sig = new StringBuilder("(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/ContextFactory;");

      
      int marker = sig.length();
      for (Class<?> c : parameters) {
        appendTypeString(sig, c);
      }
      sig.append(")V");
      cfw.startMethod("<init>", sig.toString(), (short)1);

      
      cfw.add(42);
      short paramOffset = 3;
      for (Class<?> parameter : parameters) {
        paramOffset = (short)(paramOffset + generatePushParam(cfw, paramOffset, parameter));
      }
      locals = paramOffset;
      sig.delete(1, marker);
      cfw.addInvoke(183, superName, "<init>", sig.toString());
    } 

    
    cfw.add(42);
    cfw.add(43);
    cfw.add(181, adapterName, "delegee", "Lorg/mozilla/javascript/Scriptable;");


    
    cfw.add(42);
    cfw.add(44);
    cfw.add(181, adapterName, "factory", "Lorg/mozilla/javascript/ContextFactory;");

    
    cfw.add(42);
    
    cfw.add(43);
    cfw.add(42);
    cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "createAdapterWrapper", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");




    
    cfw.add(181, adapterName, "self", "Lorg/mozilla/javascript/Scriptable;");

    
    cfw.add(177);
    cfw.stopMethod(locals);
  }



  
  private static void generateSerialCtor(ClassFileWriter cfw, String adapterName, String superName) {
    cfw.startMethod("<init>", "(Lorg/mozilla/javascript/ContextFactory;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;)V", (short)1);






    
    cfw.add(42);
    cfw.addInvoke(183, superName, "<init>", "()V");

    
    cfw.add(42);
    cfw.add(43);
    cfw.add(181, adapterName, "factory", "Lorg/mozilla/javascript/ContextFactory;");


    
    cfw.add(42);
    cfw.add(44);
    cfw.add(181, adapterName, "delegee", "Lorg/mozilla/javascript/Scriptable;");

    
    cfw.add(42);
    cfw.add(45);
    cfw.add(181, adapterName, "self", "Lorg/mozilla/javascript/Scriptable;");

    
    cfw.add(177);
    cfw.stopMethod((short)4);
  }




  
  private static void generateEmptyCtor(ClassFileWriter cfw, String adapterName, String superName, String scriptClassName) {
    cfw.startMethod("<init>", "()V", (short)1);

    
    cfw.add(42);
    cfw.addInvoke(183, superName, "<init>", "()V");

    
    cfw.add(42);
    cfw.add(1);
    cfw.add(181, adapterName, "factory", "Lorg/mozilla/javascript/ContextFactory;");


    
    cfw.add(187, scriptClassName);
    cfw.add(89);
    cfw.addInvoke(183, scriptClassName, "<init>", "()V");

    
    cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "runScript", "(Lorg/mozilla/javascript/Script;)Lorg/mozilla/javascript/Scriptable;");



    
    cfw.add(76);

    
    cfw.add(42);
    cfw.add(43);
    cfw.add(181, adapterName, "delegee", "Lorg/mozilla/javascript/Scriptable;");

    
    cfw.add(42);
    
    cfw.add(43);
    cfw.add(42);
    cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "createAdapterWrapper", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");




    
    cfw.add(181, adapterName, "self", "Lorg/mozilla/javascript/Scriptable;");

    
    cfw.add(177);
    cfw.stopMethod((short)2);
  }









  
  static void generatePushWrappedArgs(ClassFileWriter cfw, Class<?>[] argTypes, int arrayLength) {
    cfw.addPush(arrayLength);
    cfw.add(189, "java/lang/Object");
    int paramOffset = 1;
    for (int i = 0; i != argTypes.length; i++) {
      cfw.add(89);
      cfw.addPush(i);
      paramOffset += generateWrapArg(cfw, paramOffset, argTypes[i]);
      cfw.add(83);
    } 
  }







  
  private static int generateWrapArg(ClassFileWriter cfw, int paramOffset, Class<?> argType) {
    int size = 1;
    if (!argType.isPrimitive()) {
      cfw.add(25, paramOffset);
    }
    else if (argType == boolean.class) {
      
      cfw.add(187, "java/lang/Boolean");
      cfw.add(89);
      cfw.add(21, paramOffset);
      cfw.addInvoke(183, "java/lang/Boolean", "<init>", "(Z)V");
    
    }
    else if (argType == char.class) {
      
      cfw.add(21, paramOffset);
      cfw.addInvoke(184, "java/lang/String", "valueOf", "(C)Ljava/lang/String;");
    
    }
    else {
      
      cfw.add(187, "java/lang/Double");
      cfw.add(89);
      String typeName = argType.getName();
      switch (typeName.charAt(0)) {
        
        case 'b':
        case 'i':
        case 's':
          cfw.add(21, paramOffset);
          cfw.add(135);
          break;
        
        case 'l':
          cfw.add(22, paramOffset);
          cfw.add(138);
          size = 2;
          break;
        
        case 'f':
          cfw.add(23, paramOffset);
          cfw.add(141);
          break;
        case 'd':
          cfw.add(24, paramOffset);
          size = 2;
          break;
      } 
      cfw.addInvoke(183, "java/lang/Double", "<init>", "(D)V");
    } 
    
    return size;
  }









  
  static void generateReturnResult(ClassFileWriter cfw, Class<?> retType, boolean callConvertResult) {
    if (retType == void.class) {
      cfw.add(87);
      cfw.add(177);
    }
    else if (retType == boolean.class) {
      cfw.addInvoke(184, "org/mozilla/javascript/Context", "toBoolean", "(Ljava/lang/Object;)Z");

      
      cfw.add(172);
    }
    else if (retType == char.class) {


      
      cfw.addInvoke(184, "org/mozilla/javascript/Context", "toString", "(Ljava/lang/Object;)Ljava/lang/String;");


      
      cfw.add(3);
      cfw.addInvoke(182, "java/lang/String", "charAt", "(I)C");
      
      cfw.add(172);
    } else {
      if (retType.isPrimitive()) {
        cfw.addInvoke(184, "org/mozilla/javascript/Context", "toNumber", "(Ljava/lang/Object;)D");

        
        String typeName = retType.getName();
        switch (typeName.charAt(0)) {
          case 'b':
          case 'i':
          case 's':
            cfw.add(142);
            cfw.add(172);
            return;
          case 'l':
            cfw.add(143);
            cfw.add(173);
            return;
          case 'f':
            cfw.add(144);
            cfw.add(174);
            return;
          case 'd':
            cfw.add(175);
            return;
        } 
        throw new RuntimeException("Unexpected return type " + retType.toString());
      } 


      
      String retTypeStr = retType.getName();
      if (callConvertResult) {
        cfw.addLoadConstant(retTypeStr);
        cfw.addInvoke(184, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");



        
        cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "convertResult", "(Ljava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;");
      } 





      
      cfw.add(192, retTypeStr);
      cfw.add(176);
    } 
  }



  
  private static void generateMethod(ClassFileWriter cfw, String genName, String methodName, Class<?>[] parms, Class<?> returnType, boolean convertResult) {
    StringBuilder sb = new StringBuilder();
    int paramsEnd = appendMethodSignature(parms, returnType, sb);
    String methodSignature = sb.toString();
    cfw.startMethod(methodName, methodSignature, (short)1);




    
    cfw.add(42);
    cfw.add(180, genName, "factory", "Lorg/mozilla/javascript/ContextFactory;");


    
    cfw.add(42);
    cfw.add(180, genName, "self", "Lorg/mozilla/javascript/Scriptable;");


    
    cfw.add(42);
    cfw.add(180, genName, "delegee", "Lorg/mozilla/javascript/Scriptable;");
    
    cfw.addPush(methodName);
    cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "getFunction", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Lorg/mozilla/javascript/Function;");






    
    generatePushWrappedArgs(cfw, parms, parms.length);

    
    if (parms.length > 64)
    {
      
      throw Context.reportRuntimeError0("JavaAdapter can not subclass methods with more then 64 arguments.");
    }

    
    long convertionMask = 0L;
    for (int i = 0; i != parms.length; i++) {
      if (!parms[i].isPrimitive()) {
        convertionMask |= (1 << i);
      }
    } 
    cfw.addPush(convertionMask);


    
    cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "callMethod", "(Lorg/mozilla/javascript/ContextFactory;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Function;[Ljava/lang/Object;J)Ljava/lang/Object;");








    
    generateReturnResult(cfw, returnType, convertResult);
    
    cfw.stopMethod((short)paramsEnd);
  }






  
  private static int generatePushParam(ClassFileWriter cfw, int paramOffset, Class<?> paramType) {
    if (!paramType.isPrimitive()) {
      cfw.addALoad(paramOffset);
      return 1;
    } 
    String typeName = paramType.getName();
    switch (typeName.charAt(0)) {
      
      case 'b':
      case 'c':
      case 'i':
      case 's':
      case 'z':
        cfw.addILoad(paramOffset);
        return 1;
      
      case 'l':
        cfw.addLLoad(paramOffset);
        return 2;
      
      case 'f':
        cfw.addFLoad(paramOffset);
        return 1;
      case 'd':
        cfw.addDLoad(paramOffset);
        return 2;
    } 
    throw Kit.codeBug();
  }







  
  private static void generatePopResult(ClassFileWriter cfw, Class<?> retType) {
    if (retType.isPrimitive()) {
      String typeName = retType.getName();
      switch (typeName.charAt(0)) {
        case 'b':
        case 'c':
        case 'i':
        case 's':
        case 'z':
          cfw.add(172);
          break;
        case 'l':
          cfw.add(173);
          break;
        case 'f':
          cfw.add(174);
          break;
        case 'd':
          cfw.add(175);
          break;
      } 
    } else {
      cfw.add(176);
    } 
  }









  
  private static void generateSuper(ClassFileWriter cfw, String genName, String superName, String methodName, String methodSignature, Class<?>[] parms, Class<?> returnType) {
    cfw.startMethod("super$" + methodName, methodSignature, (short)1);


    
    cfw.add(25, 0);

    
    int paramOffset = 1;
    for (Class<?> parm : parms) {
      paramOffset += generatePushParam(cfw, paramOffset, parm);
    }

    
    cfw.addInvoke(183, superName, methodName, methodSignature);




    
    Class<?> retType = returnType;
    if (!retType.equals(void.class)) {
      generatePopResult(cfw, retType);
    } else {
      cfw.add(177);
    } 
    cfw.stopMethod((short)(paramOffset + 1));
  }




  
  private static String getMethodSignature(Method method, Class<?>[] argTypes) {
    StringBuilder sb = new StringBuilder();
    appendMethodSignature(argTypes, method.getReturnType(), sb);
    return sb.toString();
  }



  
  static int appendMethodSignature(Class<?>[] argTypes, Class<?> returnType, StringBuilder sb) {
    sb.append('(');
    int firstLocal = 1 + argTypes.length;
    for (Class<?> type : argTypes) {
      appendTypeString(sb, type);
      if (type == long.class || type == double.class)
      {
        firstLocal++;
      }
    } 
    sb.append(')');
    appendTypeString(sb, returnType);
    return firstLocal;
  }

  
  private static StringBuilder appendTypeString(StringBuilder sb, Class<?> type) {
    while (type.isArray()) {
      sb.append('[');
      type = type.getComponentType();
    } 
    if (type.isPrimitive()) {
      char typeLetter;
      if (type == boolean.class) {
        typeLetter = 'Z';
      } else if (type == long.class) {
        typeLetter = 'J';
      } else {
        String typeName = type.getName();
        typeLetter = Character.toUpperCase(typeName.charAt(0));
      } 
      sb.append(typeLetter);
    } else {
      sb.append('L');
      sb.append(type.getName().replace('.', '/'));
      sb.append(';');
    } 
    return sb;
  }

  
  static int[] getArgsToConvert(Class<?>[] argTypes) {
    int count = 0;
    for (int i = 0; i != argTypes.length; i++) {
      if (!argTypes[i].isPrimitive())
        count++; 
    } 
    if (count == 0)
      return null; 
    int[] array = new int[count];
    count = 0;
    for (int j = 0; j != argTypes.length; j++) {
      if (!argTypes[j].isPrimitive())
        array[count++] = j; 
    } 
    return array;
  }
  
  private static final Object FTAG = "JavaAdapter";
  private static final int Id_JavaAdapter = 1;
}
