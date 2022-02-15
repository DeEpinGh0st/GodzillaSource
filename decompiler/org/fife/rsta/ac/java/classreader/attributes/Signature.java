package org.fife.rsta.ac.java.classreader.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;






















public class Signature
  extends AttributeInfo
{
  private String signature;
  
  public Signature(ClassFile cf, String signature) {
    super(cf);
    this.signature = signature;
  }


  
  public List<String> getClassParamTypes() {
    List<String> types = null;
    
    if (this.signature != null && this.signature.startsWith("<")) {
      
      types = new ArrayList<>(1);
      int afterMatchingGT = skipLtGt(this.signature, 1);


      
      String temp = this.signature.substring(1, afterMatchingGT - 1);
      int offs = 0;
      int colon = temp.indexOf(':', offs);
      while (offs < temp.length() && colon > -1) {
        String ident = temp.substring(offs, colon);
        int colonCount = 1;
        char ch = temp.charAt(colon + colonCount);
        if (ch == ':') {
          colonCount++;
          ch = temp.charAt(colon + colonCount);
        } 
        if (ch == 'L') {
          int semicolon = temp.indexOf(';', colon + colonCount + 1);
          if (semicolon > -1) {

            
            types.add(ident);
            offs = semicolon + 1;
            colon = temp.indexOf(':', offs);
            continue;
          } 
          System.err.println("WARN: Can't parse signature (1): " + this.signature);
          
          break;
        } 
        
        System.err.println("WARN: Can't parse signature (2): " + this.signature);
      } 
    } 


    
    return types;
  }



  
  private int skipLtGt(String str, int start) {
    int ltCount = 1;
    int offs = start;
    
    while (offs < str.length() && ltCount > 0) {
      char ch = str.charAt(offs++);
      switch (ch) {
        case '<':
          ltCount++;
        
        case '>':
          ltCount--;
      } 

    
    } 
    return offs;
  }




  
  public List<String> getMethodParamTypes(MethodInfo mi, ClassFile cf, boolean qualified) {
    List<String> paramTypeList = null;
    String signature = this.signature;
    
    if (signature != null) {
      
      paramTypeList = new ArrayList<>();

      
      Map<String, String> additionalTypeArgs = null;
      if (signature.charAt(0) == '<') {
        int afterMatchingGT = skipLtGt(signature, 1);
        String typeParams = signature.substring(1, afterMatchingGT - 1);
        additionalTypeArgs = parseAdditionalTypeArgs(typeParams);
        signature = signature.substring(afterMatchingGT);
      } 
      
      if (signature.charAt(0) == '(') {
        
        int rparen = signature.indexOf(')', 1);
        String paramDescriptors = signature.substring(1, rparen);
        ParamDescriptorResult res = new ParamDescriptorResult();
        
        while (paramDescriptors.length() > 0) {
          parseParamDescriptor(paramDescriptors, cf, additionalTypeArgs, mi, "Error parsing method signature for ", res, qualified);
          
          paramTypeList.add(res.type);
          if (paramDescriptors.length() > res.pos) {
            paramDescriptors = paramDescriptors.substring(res.pos);
          
          }
        
        }
      
      }
      else {
        
        System.out.println("TODO: Unhandled method signature for " + mi
            .getName() + ": " + signature);
      } 
    } 

    
    return paramTypeList;
  }



  
  public String getMethodReturnType(MethodInfo mi, ClassFile cf, boolean qualified) {
    String signature = this.signature;
    String sig = null;
    
    if (signature != null) {

      
      Map<String, String> additionalTypeArgs = null;
      if (signature.charAt(0) == '<') {
        int afterMatchingGT = skipLtGt(signature, 1);
        String typeParams = signature.substring(1, afterMatchingGT - 1);
        additionalTypeArgs = parseAdditionalTypeArgs(typeParams);
        signature = signature.substring(afterMatchingGT);
      } 
      
      if (signature.charAt(0) == '(') {
        int rparen = signature.indexOf(')', 1);
        if (rparen > -1 && rparen < signature.length() - 3) {
          String afterRParen = signature.substring(rparen + 1);
          ParamDescriptorResult res = new ParamDescriptorResult();
          parseParamDescriptor(afterRParen, cf, additionalTypeArgs, mi, "Can't parse return type from method sig for ", res, qualified);
          
          sig = res.type;
        }
      
      } else {
        
        System.out.println("TODO: Unhandled method signature for " + mi
            .getName() + ": " + signature);
      } 
    } 

    
    return sig;
  }


  
  public String getSignature() {
    return this.signature;
  }














  
  private String getTypeArgument(String typeVar, ClassFile cf, Map<String, String> additionalTypeArgs) {
    String type = cf.getTypeArgument(typeVar);
    if (type == null && additionalTypeArgs != null)
    {
      type = typeVar;
    }
    return type;
  }


  
  private Map<String, String> parseAdditionalTypeArgs(String typeParams) {
    Map<String, String> additionalTypeArgs = new HashMap<>();
    int offs = 0;
    int colon = typeParams.indexOf(':', offs);
    
    while (offs < typeParams.length()) {
      String param = typeParams.substring(offs, colon);
      int semicolon = typeParams.indexOf(';', offs + 1);
      int lt = typeParams.indexOf('<', offs + 1);
      if (lt > -1 && lt < semicolon) {
        int afterMatchingGT = skipLtGt(typeParams, lt + 1);
        String typeArg = typeParams.substring(colon + 1, afterMatchingGT);
        additionalTypeArgs.put(param, typeArg);
        offs = afterMatchingGT + 1;
      } else {
        
        String typeArg = typeParams.substring(colon + 1, semicolon);
        additionalTypeArgs.put(param, typeArg);
        offs = semicolon + 1;
      } 
      colon = typeParams.indexOf(':', offs);
    } 
    
    return additionalTypeArgs;
  }






  
  private ParamDescriptorResult parseParamDescriptor(String str, ClassFile cf, Map<String, String> additionalTypeArgs, MethodInfo mi, String errorDesc, ParamDescriptorResult res, boolean qualified) {
    String type;
    int semicolon, lt;
    String clazz, typeVar, temp;
    int braceCount = -1;
    while (str.charAt(++braceCount) == '[');
    int pos = braceCount;
    
    boolean extendingGenericType = false;
    
    switch (str.charAt(pos)) {

      
      case 'B':
        type = "byte";
        pos++;
        break;
      case 'C':
        type = "char";
        pos++;
        break;
      case 'D':
        type = "double";
        pos++;
        break;
      case 'F':
        type = "float";
        pos++;
        break;
      case 'I':
        type = "int";
        pos++;
        break;
      case 'J':
        type = "long";
        pos++;
        break;
      case 'S':
        type = "short";
        pos++;
        break;
      case 'Z':
        type = "boolean";
        pos++;
        break;

      
      case 'L':
        semicolon = str.indexOf(';', pos + 1);
        lt = str.indexOf('<', pos + 1);
        if (lt > -1 && lt < semicolon) {
          int offs = skipLtGt(str, lt + 1);
          
          if (offs == str.length() || str.charAt(offs) != ';') {
            System.out.println("TODO: " + errorDesc + mi
                .getName() + ": " + this.signature);
            type = "ERROR_PARSING_METHOD_SIG";
            
            break;
          } 
          type = str.substring(pos + 1, lt);

          
          type = qualified ? type.replace('/', '.') : type.substring(type.lastIndexOf('/') + 1);
          
          String paramDescriptors = str.substring(lt + 1, offs - 1);
          ParamDescriptorResult res2 = new ParamDescriptorResult();
          List<String> paramTypeList = new ArrayList<>();
          
          while (paramDescriptors.length() > 0) {
            parseParamDescriptor(paramDescriptors, cf, additionalTypeArgs, mi, "Error parsing method signature for ", res2, qualified);
            
            paramTypeList.add(res2.type);
            if (paramDescriptors.length() > res2.pos) {
              paramDescriptors = paramDescriptors.substring(res2.pos);
            }
          } 


          
          StringBuilder sb = (new StringBuilder(type)).append('<');
          for (int j = 0; j < paramTypeList.size(); j++) {
            sb.append(paramTypeList.get(j));
            if (j < paramTypeList.size() - 1) {
              sb.append(", ");
            }
          } 
          type = sb.append('>').toString();
          pos = offs + 1;
          
          break;
        } 
        clazz = str.substring(pos + 1, semicolon);

        
        clazz = qualified ? clazz.replace('/', '.') : clazz.substring(clazz.lastIndexOf('/') + 1);
        type = clazz;
        pos += semicolon + 1;
        break;

      
      case '+':
        extendingGenericType = true;
        pos++;

      
      case 'T':
        semicolon = str.indexOf(';', pos + 1);
        typeVar = str.substring(pos + 1, semicolon);
        type = getTypeArgument(typeVar, cf, additionalTypeArgs);
        if (type == null) {
          type = "UNKNOWN_GENERIC_TYPE_" + typeVar;
        }
        else if (extendingGenericType) {
          type = "? extends " + type;
        } 
        pos = semicolon + 1;
        break;
      
      case '*':
        type = "?";
        pos++;
        break;

      
      default:
        temp = "INVALID_TYPE_" + str;
        type = temp;
        pos += str.length();
        break;
    } 

    
    for (int i = 0; i < braceCount; i++) {
      type = type + "[]";
    }
    
    return res.set(type, pos);
  }



  
  public String toString() {
    return "[Signature: signature=" + getSignature() + "]";
  }
  
  private static class ParamDescriptorResult {
    public String type;
    public int pos;
    
    private ParamDescriptorResult() {}
    
    public ParamDescriptorResult set(String type, int pos) {
      this.type = type;
      this.pos = pos;
      return this;
    }
  }
}
