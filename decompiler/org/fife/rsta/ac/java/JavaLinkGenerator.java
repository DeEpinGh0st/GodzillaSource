package org.fife.rsta.ac.java;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SelectRegionLinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;

























class JavaLinkGenerator
  implements LinkGenerator
{
  private JavaLanguageSupport jls;
  
  JavaLinkGenerator(JavaLanguageSupport jls) {
    this.jls = jls;
  }











  
  private IsLinkableCheckResult checkForLinkableToken(RSyntaxTextArea textArea, int offs) {
    IsLinkableCheckResult result = null;
    
    if (offs >= 0) {
      
      try {
        
        int line = textArea.getLineOfOffset(offs);
        Token first = textArea.getTokenListForLine(line);
        RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
        Token prev = null;
        
        for (Token t = first; t != null && t.isPaintable(); t = t.getNextToken())
        {
          if (t.containsPosition(offs)) {


            
            TokenImpl tokenImpl = new TokenImpl(t);
            boolean isMethod = false;
            
            if (prev == null) {
              prev = RSyntaxUtilities.getPreviousImportantToken(doc, line - 1);
            }
            
            if (prev != null && prev.isSingleChar('.')) {
              break;
            }

            
            Token next = RSyntaxUtilities.getNextImportantToken(t
                .getNextToken(), textArea, line);
            if (next != null && next.isSingleChar(22, '(')) {
              isMethod = true;
            }
            
            result = new IsLinkableCheckResult((Token)tokenImpl, isMethod);
            
            break;
          } 
          
          if (!t.isCommentOrWhitespace()) {
            prev = t;
          }
        }
      
      }
      catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }

    
    return result;
  }








  
  public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
    int start = -1;
    int end = -1;
    
    IsLinkableCheckResult result = checkForLinkableToken(textArea, offs);
    if (result != null) {
      
      JavaParser parser = this.jls.getParser(textArea);
      CompilationUnit cu = parser.getCompilationUnit();
      Token t = result.token;
      boolean method = result.method;
      
      if (cu != null) {
        
        TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(offs);
        boolean staticFieldsOnly = false;
        boolean deepestTypeDec = true;
        boolean deepestContainingMemberStatic = false;
        while (td != null && start == -1) {

          
          if (!method && deepestTypeDec) {
            
            Iterator<Member> i = td.getMemberIterator();
            while (i.hasNext()) {
              
              Method m = null;
              Member member = i.next();
              CodeBlock block = null;

              
              if (member instanceof Method) {
                m = (Method)member;
                if (m.getBodyContainsOffset(offs) && m.getBody() != null) {
                  deepestContainingMemberStatic = m.isStatic();
                  block = m.getBody().getDeepestCodeBlockContaining(offs);
                }
              
              } else if (member instanceof CodeBlock) {
                block = (CodeBlock)member;
                deepestContainingMemberStatic = block.isStatic();
                block = block.getDeepestCodeBlockContaining(offs);
              } 

              
              if (block != null) {
                String varName = t.getLexeme();
                
                List<LocalVariable> locals = block.getLocalVarsBefore(offs);
                Collections.reverse(locals);
                for (LocalVariable local : locals) {
                  if (varName.equals(local.getName())) {
                    start = local.getNameStartOffset();
                    end = local.getNameEndOffset();
                  } 
                } 
                
                if (start == -1 && m != null) {
                  for (int j = 0; j < m.getParameterCount(); j++) {
                    FormalParameter p = m.getParameter(j);
                    if (varName.equals(p.getName())) {
                      start = p.getNameStartOffset();
                      end = p.getNameEndOffset();
                    } 
                  } 
                }

                
                break;
              } 
            } 
          } 
          
          if (start == -1) {
            String varName = t.getLexeme();
            
            Iterator<? extends Member> i = method ? td.getMethodIterator() : td.getFieldIterator();
            while (i.hasNext()) {
              Member member = i.next();
              if (((!deepestContainingMemberStatic && !staticFieldsOnly) || member.isStatic()) && varName
                .equals(member.getName())) {
                start = member.getNameStartOffset();
                end = member.getNameEndOffset();
                
                break;
              } 
            } 
          } 
          
          if (start == -1) {
            staticFieldsOnly |= td.isStatic();
            
            td = td.getParentType();
            
            deepestTypeDec = false;
          } 
        } 
      } 


      
      if (start > -1) {
        return (LinkGeneratorResult)new SelectRegionLinkGeneratorResult(textArea, t.getOffset(), start, end);
      }
    } 


    
    return null;
  }





  
  private static class IsLinkableCheckResult
  {
    private Token token;




    
    private boolean method;




    
    private IsLinkableCheckResult(Token token, boolean method) {
      this.token = token;
      this.method = method;
    }
  }
}
