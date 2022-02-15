package org.fife.rsta.ac.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;

























public class Util
{
  static final Pattern DOC_COMMENT_LINE_HEADER = Pattern.compile("\\s*\\n\\s*\\*");



















  
  static final Pattern LINK_TAG_MEMBER_PATTERN = Pattern.compile("(?:\\w+\\.)*\\w+(?:#\\w+(?:\\([^\\)]*\\))?)?|#\\w+(?:\\([^\\)]*\\))?");




  
  private static CompilationUnit lastCUFromDisk;




  
  private static SourceLocation lastCUFileParam;




  
  private static ClassFile lastCUClassFileParam;





  
  private static void appendDocCommentTail(StringBuilder sb, StringBuilder tail) {
    StringBuilder params = null;
    StringBuilder returns = null;
    StringBuilder throwsItems = null;
    StringBuilder see = null;
    StringBuilder seeTemp = null;
    StringBuilder since = null;
    StringBuilder author = null;
    StringBuilder version = null;
    StringBuilder unknowns = null;
    boolean inParams = false, inThrows = false;
    boolean inReturns = false, inSeeAlso = false;
    boolean inSince = false, inAuthor = false;
    boolean inVersion = false, inUnknowns = false;
    
    String[] st = tail.toString().split("[ \t\r\n\f]+");

    
    int i = 0; String token;
    while (i < st.length && (token = st[i++]) != null) {
      if ("@param".equals(token) && i < st.length) {
        token = st[i++];
        if (params == null) {
          params = new StringBuilder("<b>Parameters:</b><p class='indented'>");
        } else {
          
          params.append("<br>");
        } 
        params.append("<b>").append(token).append("</b> ");
        inSeeAlso = false;
        inParams = true;
        inReturns = false;
        inThrows = false;
        inSince = false;
        inAuthor = false;
        inVersion = false;
        inUnknowns = false; continue;
      } 
      if ("@return".equals(token) && i < st.length) {
        if (returns == null) {
          returns = new StringBuilder("<b>Returns:</b><p class='indented'>");
        }
        inSeeAlso = false;
        inReturns = true;
        inParams = false;
        inThrows = false;
        inSince = false;
        inAuthor = false;
        inVersion = false;
        inUnknowns = false; continue;
      } 
      if ("@see".equals(token) && i < st.length) {
        if (see == null) {
          see = new StringBuilder("<b>See Also:</b><p class='indented'>");
          seeTemp = new StringBuilder();
        } else {
          
          if (seeTemp.length() > 0) {
            String temp = seeTemp.substring(0, seeTemp.length() - 1);
            
            appendLinkTagText(see, temp);
          } 
          see.append("<br>");
          seeTemp.setLength(0);
        } 
        
        inSeeAlso = true;
        inReturns = false;
        inParams = false;
        inThrows = false;
        inSince = false;
        inAuthor = false;
        inVersion = false;
        inUnknowns = false; continue;
      } 
      if ("@throws".equals(token) || ("@exception"
        .equals(token) && i < st.length)) {
        token = st[i++];
        if (throwsItems == null) {
          throwsItems = new StringBuilder("<b>Throws:</b><p class='indented'>");
        } else {
          
          throwsItems.append("<br>");
        } 
        throwsItems.append("<b>").append(token).append("</b> ");
        inSeeAlso = false;
        inParams = false;
        inReturns = false;
        inThrows = true;
        inSince = false;
        inAuthor = false;
        inVersion = false;
        inUnknowns = false; continue;
      } 
      if ("@since".equals(token) && i < st.length) {
        if (since == null) {
          since = new StringBuilder("<b>Since:</b><p class='indented'>");
        }
        inSeeAlso = false;
        inReturns = false;
        inParams = false;
        inThrows = false;
        inSince = true;
        inAuthor = false;
        inVersion = false;
        inUnknowns = false; continue;
      } 
      if ("@author".equals(token) && i < st.length) {
        if (author == null) {
          author = new StringBuilder("<b>Author:</b><p class='indented'>");
        } else {
          
          author.append("<br>");
        } 
        inSeeAlso = false;
        inReturns = false;
        inParams = false;
        inThrows = false;
        inSince = false;
        inAuthor = true;
        inVersion = false;
        inUnknowns = false; continue;
      } 
      if ("@version".equals(token) && i < st.length) {
        if (version == null) {
          version = new StringBuilder("<b>Version:</b><p class='indented'>");
        } else {
          
          version.append("<br>");
        } 
        inSeeAlso = false;
        inReturns = false;
        inParams = false;
        inThrows = false;
        inSince = false;
        inAuthor = false;
        inVersion = true;
        inUnknowns = false; continue;
      } 
      if (token.startsWith("@") && token.length() > 1) {
        if (unknowns == null) {
          unknowns = new StringBuilder();
        } else {
          
          unknowns.append("</p>");
        } 
        unknowns.append("<b>").append(token).append("</b><p class='indented'>");
        
        inSeeAlso = false;
        inParams = false;
        inReturns = false;
        inThrows = false;
        inSince = false;
        inAuthor = false;
        inVersion = false;
        inUnknowns = true; continue;
      } 
      if (inParams) {
        params.append(token).append(' '); continue;
      } 
      if (inReturns) {
        returns.append(token).append(' '); continue;
      } 
      if (inSeeAlso) {
        
        seeTemp.append(token).append(' '); continue;
      } 
      if (inThrows) {
        throwsItems.append(token).append(' '); continue;
      } 
      if (inSince) {
        since.append(token).append(' '); continue;
      } 
      if (inAuthor) {
        author.append(token).append(' '); continue;
      } 
      if (inVersion) {
        version.append(token).append(' '); continue;
      } 
      if (inUnknowns) {
        unknowns.append(token).append(' ');
      }
    } 
    
    sb.append("<p>");
    
    if (params != null) {
      sb.append(params).append("</p>");
    }
    if (returns != null) {
      sb.append(returns).append("</p>");
    }
    if (throwsItems != null) {
      sb.append(throwsItems).append("</p>");
    }
    if (see != null) {
      if (seeTemp.length() > 0) {
        String temp = seeTemp.substring(0, seeTemp.length() - 1);
        
        appendLinkTagText(see, temp);
      } 
      see.append("<br>");
      sb.append(see).append("</p>");
    } 
    if (author != null) {
      sb.append(author).append("</p>");
    }
    if (version != null) {
      sb.append(version).append("</p>");
    }
    if (since != null) {
      sb.append(since).append("</p>");
    }
    if (unknowns != null) {
      sb.append(unknowns).append("</p>");
    }
  }













  
  private static void appendLinkTagText(StringBuilder appendTo, String linkContent) {
    linkContent = linkContent.trim();
    Matcher m = LINK_TAG_MEMBER_PATTERN.matcher(linkContent);
    
    if (m.find() && m.start() == 0) {
      
      appendTo.append("<a href='");

      
      String match = m.group(0);
      String link = match;


      
      String text = null;
      
      if (match.length() == linkContent.length()) {
        int pound = match.indexOf('#');
        if (pound == 0) {
          text = match.substring(1);
        }
        else if (pound > 0) {
          String prefix = match.substring(0, pound);
          if ("java.lang.Object".equals(prefix)) {
            text = match.substring(pound + 1);
          }
        }
        else {
          
          text = match;
        } 
      } else {
        
        int offs = match.length();
        
        while (offs < linkContent.length() && 
          Character.isWhitespace(linkContent.charAt(offs))) {
          offs++;
        }
        if (offs < linkContent.length()) {
          text = linkContent.substring(offs);
        }
      } 

      
      if (text == null) {
        text = linkContent;
      }

      
      text = fixLinkText(text);
      
      appendTo.append(link).append("'>").append(text);
      appendTo.append("</a>");


    
    }
    else if (linkContent.startsWith("<a")) {
      appendTo.append(linkContent);
    }
    else {
      
      System.out.println("Unmatched linkContent: " + linkContent);
      appendTo.append(linkContent);
    } 
  }











  
  public static String docCommentToHtml(String dc) {
    if (dc == null) {
      return null;
    }
    if (dc.endsWith("*/")) {
      dc = dc.substring(0, dc.length() - 2);
    }


    
    Matcher m = DOC_COMMENT_LINE_HEADER.matcher(dc);
    dc = m.replaceAll("\n");
    
    StringBuilder html = new StringBuilder("<html><style> .indented { margin-top: 0px; padding-left: 30pt; } </style><body>");
    
    StringBuilder tailBuf = null;
    
    BufferedReader r = new BufferedReader(new StringReader(dc));


    
    try {
      String line = r.readLine().substring(3);
      line = possiblyStripDocCommentTail(line);
      int offs = 0;
      while (offs < line.length() && Character.isWhitespace(line.charAt(offs))) {
        offs++;
      }
      if (offs < line.length()) {
        html.append(line.substring(offs));
      }
      boolean inPreBlock = isInPreBlock(line, false);
      html.append(inPreBlock ? 10 : 32);

      
      while ((line = r.readLine()) != null) {
        line = possiblyStripDocCommentTail(line);
        if (tailBuf != null) {
          tailBuf.append(line).append(' '); continue;
        } 
        if (line.trim().startsWith("@")) {
          tailBuf = new StringBuilder();
          tailBuf.append(line).append(' ');
          continue;
        } 
        html.append(line);
        inPreBlock = isInPreBlock(line, inPreBlock);
        html.append(inPreBlock ? 10 : 32);
      
      }
    
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    } 
    
    html = fixDocComment(html);
    if (tailBuf != null) {
      appendDocCommentTail(html, fixDocComment(tailBuf));
    }
    
    return html.toString();
  }

  
  public static String forXML(String aText) {
    StringBuilder result = new StringBuilder();
    StringCharacterIterator iterator = new StringCharacterIterator(aText);
    char character = iterator.current();
    while (character != Character.MAX_VALUE) {
      if (character == '<') {
        result.append("&lt;");
      }
      else if (character == '>') {
        result.append("&gt;");
      }
      else if (character == '"') {
        result.append("&quot;");
      }
      else if (character == '\'') {
        result.append("&#039;");
      }
      else if (character == '&') {
        result.append("&amp;");
      
      }
      else {
        
        result.append(character);
      } 
      character = iterator.next();
    } 
    return result.toString();
  }



  
  private static StringBuilder fixDocComment(StringBuilder text) {
    int index = text.indexOf("{@");
    if (index == -1) {
      return text;
    }
    
    StringBuilder sb = new StringBuilder();
    int textOffs = 0;

    
    while (true) {
      int closingBrace = indexOf('}', text, index + 2);
      if (closingBrace > -1)
      
      { sb.append(text, textOffs, index);
        String content = text.substring(index + 2, closingBrace);
        index = textOffs = closingBrace + 1;
        
        if (content.startsWith("code ")) {
          sb.append("<code>")
            .append(forXML(content.substring(5)))
            .append("</code>");
        
        }
        else if (content.startsWith("link ")) {
          sb.append("<code>");
          appendLinkTagText(sb, content.substring(5));
          sb.append("</code>");
        
        }
        else if (content.startsWith("linkplain ")) {
          appendLinkTagText(sb, content.substring(10));
        
        }
        else if (content.startsWith("literal ")) {
          
          sb.append(content.substring(8));
        }
        else {
          
          sb.append("<code>").append(content).append("</code>");
        } 





        
        if ((index = text.indexOf("{@", index)) <= -1)
          break;  continue; }  break;
    }  if (textOffs < text.length()) {
      sb.append(text.substring(textOffs));
    }
    
    return sb;
  }








  
  private static String fixLinkText(String text) {
    if (text.startsWith("#")) {
      return text.substring(1);
    }
    return text.replace('#', '.');
  }
















  
  public static CompilationUnit getCompilationUnitFromDisk(SourceLocation loc, ClassFile cf) {
    if (loc == lastCUFileParam && cf == lastCUClassFileParam)
    {
      return lastCUFromDisk;
    }
    
    lastCUFileParam = loc;
    lastCUClassFileParam = cf;
    CompilationUnit cu = null;
    
    if (loc != null) {
      try {
        cu = loc.getCompilationUnit(cf);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } 
    }
    
    lastCUFromDisk = cu;
    return cu;
  }









  
  public static final String getUnqualified(String clazz) {
    int dot = clazz.lastIndexOf('.');
    if (dot > -1) {
      clazz = clazz.substring(dot + 1);
    }
    return clazz;
  }












  
  private static int indexOf(char ch, CharSequence sb, int offs) {
    while (offs < sb.length()) {
      if (ch == sb.charAt(offs)) {
        return offs;
      }
      offs++;
    } 
    return -1;
  }









  
  public static final boolean isFullyQualified(String str) {
    return (str.indexOf('.') > -1);
  }








  
  private static boolean isInPreBlock(String line, boolean prevValue) {
    int lastPre = line.lastIndexOf("pre>");
    if (lastPre <= 0) {
      return prevValue;
    }
    char prevChar = line.charAt(lastPre - 1);
    if (prevChar == '<') {
      return true;
    }
    if (prevChar == '/' && lastPre >= 2 && 
      line.charAt(lastPre - 2) == '<') {
      return false;
    }
    
    return prevValue;
  }









  
  private static String possiblyStripDocCommentTail(String str) {
    if (str.endsWith("*/")) {
      str = str.substring(0, str.length() - 2);
    }
    return str;
  }














  
  public static final String[] splitOnChar(String str, int ch) {
    List<String> list = new ArrayList<>(3);
    
    int old = 0; int pos;
    while ((pos = str.indexOf(ch, old)) > -1) {
      list.add(str.substring(old, pos));
      old = pos + 1;
    } 

    
    list.add(str.substring(old));
    String[] array = new String[list.size()];
    return list.<String>toArray(array);
  }
}
