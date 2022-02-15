package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.ui.autocomplete.CompletionProvider;



































class FieldCompletion
  extends AbstractJavaSourceCompletion
  implements MemberCompletion
{
  private MemberCompletion.Data data;
  private static final int RELEVANCE = 3;
  
  public FieldCompletion(CompletionProvider provider, Field field) {
    super(provider, field.getName());
    this.data = new FieldData(field);
    setRelevance(3);
  }

  
  public FieldCompletion(CompletionProvider provider, FieldInfo info) {
    super(provider, info.getName());
    this.data = new FieldInfoData(info, (SourceCompletionProvider)provider);
    setRelevance(3);
  }

  
  private FieldCompletion(CompletionProvider provider, String text) {
    super(provider, text);
    setRelevance(3);
  }


  
  public boolean equals(Object obj) {
    return (obj instanceof FieldCompletion && ((FieldCompletion)obj)
      .getSignature().equals(getSignature()));
  }


  
  public static FieldCompletion createLengthCompletion(CompletionProvider provider, final Type type) {
    FieldCompletion fc = new FieldCompletion(provider, type.toString());
    fc.data = new MemberCompletion.Data()
      {
        public String getEnclosingClassName(boolean fullyQualified)
        {
          return type.getName(fullyQualified);
        }

        
        public String getIcon() {
          return "fieldPublicIcon";
        }

        
        public String getSignature() {
          return "length";
        }

        
        public String getSummary() {
          return null;
        }

        
        public String getType() {
          return "int";
        }

        
        public boolean isConstructor() {
          return false;
        }

        
        public boolean isDeprecated() {
          return false;
        }

        
        public boolean isAbstract() {
          return false;
        }

        
        public boolean isFinal() {
          return false;
        }

        
        public boolean isStatic() {
          return false;
        }
      };
    
    return fc;
  }


  
  public String getEnclosingClassName(boolean fullyQualified) {
    return this.data.getEnclosingClassName(fullyQualified);
  }


  
  public Icon getIcon() {
    return IconFactory.get().getIcon(this.data);
  }


  
  public String getSignature() {
    return this.data.getSignature();
  }



  
  public String getSummary() {
    String summary = this.data.getSummary();

    
    if (summary != null && summary.startsWith("/**")) {
      summary = Util.docCommentToHtml(summary);
    }
    
    return summary;
  }



  
  public String getType() {
    return this.data.getType();
  }


  
  public int hashCode() {
    return getSignature().hashCode();
  }


  
  public boolean isDeprecated() {
    return this.data.isDeprecated();
  }


  
  public void rendererText(Graphics g, int x, int y, boolean selected) {
    MethodCompletion.rendererText(this, g, x, y, selected);
  }
}
