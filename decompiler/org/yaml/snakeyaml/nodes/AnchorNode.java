package org.yaml.snakeyaml.nodes;


















public class AnchorNode
  extends Node
{
  private Node realNode;
  
  public AnchorNode(Node realNode) {
    super(realNode.getTag(), realNode.getStartMark(), realNode.getEndMark());
    this.realNode = realNode;
  }

  
  public NodeId getNodeId() {
    return NodeId.anchor;
  }
  
  public Node getRealNode() {
    return this.realNode;
  }
}
