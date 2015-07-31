package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage;
import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import com.google.gson.JsonElement;

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
    
    public void removeStage(StageTreeNode node) {
        removeNodeFromParent((MutableTreeNode) node);
        pipeline.removeStage(node.getStage());
    }
    
    public void moveStageUp(StageTreeNode node) {
        int index = getIndexOfChild(root, node) - 1;
        if (index < 0) {
            return;
        }
        removeNodeFromParent((MutableTreeNode) node);
        insertNodeInto(node, root, index);
        List<Stage> stages = pipeline.getStages();
        Stage stage = node.getStage();
        stages.remove(stage);
        stages.add(index, stage);
    }
    
    public void moveStageDown(StageTreeNode node) {
        int index = getIndexOfChild(root, node) + 1;
        if (index >= root.getChildCount()) {
            return;
        }
        removeNodeFromParent((MutableTreeNode) node);
        insertNodeInto(node, root, index);
        List<Stage> stages = pipeline.getStages();
        Stage stage = node.getStage();
        stages.remove(stage);
        stages.add(index, stage);
    }
    
    public void setParameterValue(ParameterValueTreeNode pvNode, JsonElement value) {
        pvNode.getParameterValue().setValue(value);
        nodeChanged(pvNode);
    }
    
    public void setParameterValueEnabled(ParameterValueTreeNode pvNode, boolean enabled) {
        pvNode.getParameterValue().setEnabled(enabled);
        nodeChanged(pvNode);
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
