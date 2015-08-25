package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Op.Parameter;
import io.tinwhiskers.firesight.gui.Pipeline.Stage;
import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PipelineTreeModel extends DefaultTreeModel {
    final private Ops ops;
    private Pipeline pipeline;
    private DefaultMutableTreeNode root;
    
    public PipelineTreeModel(Ops ops) {
        super(null);
        this.ops = ops;
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
    
    public void loadPipeline(final File file) {
        // [{"name":"s1","op":"calcHist","dims":1.0,"rangeMax":256.0},{"name":"s2","op":"threshold","type":"THRESH_BINARY"}]
        try {
            // TODO: It would be better to do all this in Pipeline but then
            // we'd need to add the stages manually from the loaded pipeline
            // so we do this the lazy way instead.
            pipeline = new Pipeline();
            root = new DefaultMutableTreeNode(pipeline);
            setRoot(root);
            JsonArray aPipeline = new JsonParser().parse(new FileReader(file)).getAsJsonArray();
            for (JsonElement eStage : aPipeline) {
                JsonObject oStage = eStage.getAsJsonObject();
                String name = null;
                if (oStage.get("name") != null) {
                    name = oStage.get("name").getAsString();
                }
                String opName = oStage.get("op").getAsString();
                Op op = ops.get(opName);
                Stage stage = new Stage(name, op);
                for (Parameter param : stage.getOp().getParameters().values()) {
                    if (oStage.has(param.getName())) {
                        stage.getParameters().get(param.getName()).setValue(oStage.get(param.getName()));
                        stage.setParameterValueEnabled(param.getName(), true);
                    }
                }
                addStage(stage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
