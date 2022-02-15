package org.fife.rsta.ac.java;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.EmptyIcon;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;























































class SourceParamChoicesProvider
  implements ParameterChoicesProvider
{
  private CompletionProvider provider;
  
  private void addPublicAndProtectedFieldsAndGetters(Type type, JarManager jm, Package pkg, List<Completion> list) {}
  
  public List<Completion> getLocalVarsFieldsAndGetters(NormalClassDeclaration ncd, String type, int offs) {
    List<Completion> members = new ArrayList<>();
    
    if (!ncd.getBodyContainsOffset(offs)) {
      return members;
    }


    
    Method method = ncd.getMethodContainingOffset(offs);
    if (method != null) {

      
      Iterator<FormalParameter> iterator = method.getParameterIterator();
      while (iterator.hasNext()) {
        FormalParameter param = iterator.next();
        Type paramType = param.getType();
        if (isTypeCompatible(paramType, type))
        {
          members.add(new LocalVariableCompletion(this.provider, (LocalVariable)param));
        }
      } 

      
      CodeBlock body = method.getBody();
      if (body != null) {
        CodeBlock block = body.getDeepestCodeBlockContaining(offs);
        List<LocalVariable> vars = block.getLocalVarsBefore(offs);
        for (LocalVariable var : vars) {
          Type varType = var.getType();
          if (isTypeCompatible(varType, type))
          {
            members.add(new LocalVariableCompletion(this.provider, var));
          }
        } 
      } 
    } 



    
    for (Iterator<Member> i = ncd.getMemberIterator(); i.hasNext(); ) {
      
      Member member = i.next();
      
      if (member instanceof Field) {
        Type fieldType = member.getType();
        if (isTypeCompatible(fieldType, type))
        {
          members.add(new FieldCompletion(this.provider, (Field)member));
        }
        continue;
      } 
      method = (Method)member;
      if (isSimpleGetter(method) && 
        isTypeCompatible(method.getType(), type))
      {
        members.add(new MethodCompletion(this.provider, method));
      }
    } 



    
    return members;
  }









  
  public List<Completion> getParameterChoices(JTextComponent tc, ParameterizedCompletion.Parameter param) {
    LanguageSupportFactory lsf = LanguageSupportFactory.get();
    LanguageSupport support = lsf.getSupportFor("text/java");
    
    JavaLanguageSupport jls = (JavaLanguageSupport)support;
    JarManager jm = jls.getJarManager();


    
    RSyntaxTextArea textArea = (RSyntaxTextArea)tc;
    JavaParser parser = jls.getParser(textArea);
    if (parser == null) {
      return null;
    }
    CompilationUnit cu = parser.getCompilationUnit();
    if (cu == null) {
      return null;
    }
    int dot = tc.getCaretPosition();
    TypeDeclaration typeDec = cu.getDeepestTypeDeclarationAtOffset(dot);
    if (typeDec == null) {
      return null;
    }
    
    List<Completion> list = null;
    Package pkg = typeDec.getPackage();
    this.provider = (CompletionProvider)jls.getCompletionProvider(textArea);

    
    if (typeDec instanceof NormalClassDeclaration) {

      
      NormalClassDeclaration ncd = (NormalClassDeclaration)typeDec;
      list = getLocalVarsFieldsAndGetters(ncd, param.getType(), dot);


      
      Type extended = ncd.getExtendedType();
      if (extended != null) {
        addPublicAndProtectedFieldsAndGetters(extended, jm, pkg, list);
      }

      
      for (Iterator<Type> i = ncd.getImplementedIterator(); i.hasNext(); ) {
        Type implemented = i.next();
        addPublicAndProtectedFieldsAndGetters(implemented, jm, pkg, list);

      
      }

    
    }
    else if (typeDec instanceof org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration) {
    
    } 






    
    if (!typeDec.isStatic());




    
    Object typeObj = param.getTypeObject();
    
    if (typeObj instanceof Type) {
      Type type = (Type)typeObj;
      if (type.isBasicType()) {
        if (isPrimitiveNumericType(type)) {
          list.add(new SimpleCompletion(this.provider, "0"));
        } else {
          
          list.add(new SimpleCompletion(this.provider, "false"));
          list.add(new SimpleCompletion(this.provider, "true"));
        } 
      } else {
        
        list.add(new SimpleCompletion(this.provider, "null"));
      } 
    } 

    
    return list;
  }


  
  private boolean isPrimitiveNumericType(Type type) {
    String str = type.getName(true);
    return ("byte".equals(str) || "float".equals(str) || "double"
      .equals(str) || "int".equals(str) || "short"
      .equals(str) || "long".equals(str));
  }







  
  private boolean isSimpleGetter(Method method) {
    return (method.getParameterCount() == 0 && method
      .getName().startsWith("get"));
  }











  
  private boolean isTypeCompatible(Type type, String typeName) {
    String typeName2 = type.getName(false);


    
    int lt = typeName2.indexOf('<');
    if (lt > -1) {
      String arrayDepth = null;
      int brackets = typeName2.indexOf('[', lt);
      if (brackets > -1) {
        arrayDepth = typeName2.substring(brackets);
      }
      typeName2 = typeName2.substring(lt);
      if (arrayDepth != null) {
        typeName2 = typeName2 + arrayDepth;
      }
    } 
    
    return typeName2.equalsIgnoreCase(typeName);
  }






  
  private static class SimpleCompletion
    extends BasicCompletion
    implements JavaSourceCompletion
  {
    private Icon ICON = (Icon)new EmptyIcon(16);
    
    public SimpleCompletion(CompletionProvider provider, String text) {
      super(provider, text);
      setRelevance(-1);
    }

    
    public Icon getIcon() {
      return this.ICON;
    }
    
    public void rendererText(Graphics g, int x, int y, boolean selected) {}
  }
}
