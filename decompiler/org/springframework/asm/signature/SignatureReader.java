package org.springframework.asm.signature;

public class SignatureReader {
  private final String a;
  
  public SignatureReader(String paramString) {
    this.a = paramString;
  }
  
  public void accept(SignatureVisitor paramSignatureVisitor) {
    int j;
    String str = this.a;
    int i = str.length();
    if (str.charAt(0) == '<') {
      char c;
      j = 2;
      do {
        int k = str.indexOf(':', j);
        paramSignatureVisitor.visitFormalTypeParameter(str.substring(j - 1, k));
        j = k + 1;
        c = str.charAt(j);
        if (c == 'L' || c == '[' || c == 'T')
          j = a(str, j, paramSignatureVisitor.visitClassBound()); 
        while ((c = str.charAt(j++)) == ':')
          j = a(str, j, paramSignatureVisitor.visitInterfaceBound()); 
      } while (c != '>');
    } else {
      j = 0;
    } 
    if (str.charAt(j) == '(') {
      while (str.charAt(++j) != ')')
        j = a(str, j, paramSignatureVisitor.visitParameterType()); 
      for (j = a(str, j + 1, paramSignatureVisitor.visitReturnType()); j < i; j = a(str, j + 1, paramSignatureVisitor.visitExceptionType()));
    } else {
      for (j = a(str, j, paramSignatureVisitor.visitSuperclass()); j < i; j = a(str, j, paramSignatureVisitor.visitInterface()));
    } 
  }
  
  public void acceptType(SignatureVisitor paramSignatureVisitor) {
    a(this.a, 0, paramSignatureVisitor);
  }
  
  private static int a(String paramString, int paramInt, SignatureVisitor paramSignatureVisitor) {
    int i;
    char c;
    switch (c = paramString.charAt(paramInt++)) {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'V':
      case 'Z':
        paramSignatureVisitor.visitBaseType(c);
        return paramInt;
      case '[':
        return a(paramString, paramInt, paramSignatureVisitor.visitArrayType());
      case 'T':
        i = paramString.indexOf(';', paramInt);
        paramSignatureVisitor.visitTypeVariable(paramString.substring(paramInt, i));
        return i + 1;
    } 
    int j = paramInt;
    boolean bool1 = false;
    boolean bool2 = false;
    label34: while (true) {
      String str;
      switch (c = paramString.charAt(paramInt++)) {
        case '.':
        case ';':
          if (!bool1) {
            String str1 = paramString.substring(j, paramInt - 1);
            if (bool2) {
              paramSignatureVisitor.visitInnerClassType(str1);
            } else {
              paramSignatureVisitor.visitClassType(str1);
            } 
          } 
          if (c == ';') {
            paramSignatureVisitor.visitEnd();
            return paramInt;
          } 
          j = paramInt;
          bool1 = false;
          bool2 = true;
        case '<':
          str = paramString.substring(j, paramInt - 1);
          if (bool2) {
            paramSignatureVisitor.visitInnerClassType(str);
          } else {
            paramSignatureVisitor.visitClassType(str);
          } 
          bool1 = true;
          while (true) {
            switch (c = paramString.charAt(paramInt)) {
              case '>':
                continue label34;
              case '*':
                paramInt++;
                paramSignatureVisitor.visitTypeArgument();
                continue;
              case '+':
              case '-':
                paramInt = a(paramString, paramInt + 1, paramSignatureVisitor.visitTypeArgument(c));
                continue;
            } 
            paramInt = a(paramString, paramInt, paramSignatureVisitor.visitTypeArgument('='));
          } 
          break;
      } 
    } 
  }
}
