package io.tinwhiskers.firesight.gui;

import java.util.HashMap;

import io.tinwhiskers.firesight.gui.Pipeline.Stage;
import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class PipelineTreeModel extends DefaultTreeModel {
    private Pipeline pipeline;
    private DefaultMutableTreeNode root;
    
    public PipelineTreeModel(Ops ops) {
        super(null);
        pipeline = new Pipeline();
        root = new DefaultMutableTreeNode(pipeline);
        setRoot(root);
    }
    
    public StageTreeNode addStage(Stage stage) {
        pipeline.addStage(stage);
        StageTreeNode node = new StageTreeNode(stage);
        insertNodeInto(node, root, root.getChildCount());
        return node;
    }
    
    public void removeStage(StageTreeNode stageTreeNode) {
        removeNodeFromParent((MutableTreeNode) stageTreeNode);
        pipeline.removeStage(stageTreeNode.getStage());
    }
    
    public Pipeline getPipeline() {
        return pipeline;
    }
    
    class StageTreeNode extends DefaultMutableTreeNode {
        private Stage stage;
        
        public StageTreeNode(Stage stage) {
            super(stage);
            this.stage = stage;
            for (ParameterValue pv : stage.getParameters().values()) {
                add(new ParameterValueTreeNode(pv));
            }
        }
        
        public Stage getStage() {
            return stage;
        }
    }
    
    class ParameterValueTreeNode extends DefaultMutableTreeNode {
        private ParameterValue pv;
        
        public ParameterValueTreeNode(ParameterValue pv) {
            this.pv = pv;
            setAllowsChildren(false);
            setUserObject(pv);
        }
        
        public ParameterValue getParameterValue() {
            return pv;
        }
    }
}
