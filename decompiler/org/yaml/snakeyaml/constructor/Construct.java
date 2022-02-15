package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

public interface Construct {
  Object construct(Node paramNode);
  
  void construct2ndStep(Node paramNode, Object paramObject);
}
