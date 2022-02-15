package org.fife.ui.rsyntaxtextarea.folding;

import java.util.List;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public interface FoldParser {
  List<Fold> getFolds(RSyntaxTextArea paramRSyntaxTextArea);
}
