package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage;
import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;
import io.tinwhiskers.firesight.gui.PipelineTreeModel.ParameterValueTreeNode;
import io.tinwhiskers.firesight.gui.PipelineTreeModel.StageTreeNode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.gson.JsonElement;

@SuppressWarnings("serial")
public class PipelinePanel extends JPanel {
    private final Main main;

    private JTree pipelineTree;
    private PipelineTreeModel pipelineTreeModel;

    public PipelinePanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);
        
        JButton btnAdd = new JButton(addStage);
        toolBar.add(btnAdd);
        
        JButton btnNewButton = new JButton(delStage);
        toolBar.add(btnNewButton);
        
        JButton moveUpButton = new JButton(moveStageUp);
        toolBar.add(moveUpButton);
        
        JButton moveDownButton = new JButton(moveStageDown);
        toolBar.add(moveDownButton);
        
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);
        
        pipelineTreeModel = new PipelineTreeModel(main.getOps());
        pipelineTree = new JTree(pipelineTreeModel);
        pipelineTree.setRootVisible(false);
        pipelineTree.setShowsRootHandles(true);
        pipelineTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        pipelineTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path.getPathCount() == 2) {
                    StageTreeNode stageTreeNode = (StageTreeNode) path.getPathComponent(1);
                    Stage stage = stageTreeNode.getStage();
                    PipelinePanel.this.main.highlightOutput(stage);
                }
            }
        });
        pipelineTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath path = pipelineTree.getPathForLocation(e.getX(), e.getY());
                if (path != null && path.getPathCount() == 3 && e.getClickCount() == 2) {
                    ParameterValueTreeNode pvTreeNode = (ParameterValueTreeNode) path.getPathComponent(2);
                    ParameterValue pv = pvTreeNode.getParameterValue();
                    if (ParameterEditorDialog.show((Frame) getTopLevelAncestor(), pv)) {
                        pipelineTreeModel.setParameterValue(pvTreeNode, pv.getValue());
                        PipelinePanel.this.main.generateOutput();
                    }
                }
            }});
        pipelineTree.setCellRenderer(new TooltipTreeRenderer());
        ToolTipManager.sharedInstance().registerComponent(pipelineTree);
        
        scrollPane.setViewportView(pipelineTree);
    }
    
    public Pipeline getPipeline() {
        return pipelineTreeModel.getPipeline();
    }
    
    private Action addStage = new AbstractAction("+") {
        public void actionPerformed(ActionEvent e) {
            OpSelectionDialog dialog = new OpSelectionDialog(
                    (Frame) getTopLevelAncestor(),
                    "Select op...",
                    "Please select an op from the list below.",
                    main.getOps());
            dialog.setVisible(true);
            Op op = dialog.getSelectedOp();
            if (op == null) {
                return;
            }
            Stage stage = new Stage(op);
            pipelineTreeModel.addStage(stage);
            pipelineTree.expandPath(new TreePath(pipelineTreeModel.getRoot()));
            main.generateOutput();
        }
    };
    
    private Action delStage = new AbstractAction("-") {
        public void actionPerformed(ActionEvent e) {
            TreePath path = pipelineTree.getSelectionPath();
            StageTreeNode stageTreeNode = (StageTreeNode) path.getPathComponent(1);
            pipelineTreeModel.removeStage(stageTreeNode);
            main.generateOutput();
        }
    };
    
    private Action moveStageUp = new AbstractAction("^") {
        public void actionPerformed(ActionEvent e) {
            TreePath path = pipelineTree.getSelectionPath();
            StageTreeNode stageTreeNode = (StageTreeNode) path.getPathComponent(1);
            pipelineTreeModel.moveStageUp(stageTreeNode);
            main.generateOutput();
        }
    };
    
    private Action moveStageDown = new AbstractAction("v") {
        public void actionPerformed(ActionEvent e) {
            TreePath path = pipelineTree.getSelectionPath();
            StageTreeNode stageTreeNode = (StageTreeNode) path.getPathComponent(1);
            pipelineTreeModel.moveStageDown(stageTreeNode);
            main.generateOutput();
        }
    };
    
    public class TooltipTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {

            final JLabel rc = (JLabel) super.getTreeCellRendererComponent(tree,
                    value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof StageTreeNode) {
                StageTreeNode node = (StageTreeNode) value;
                this.setToolTipText(node.getStage().getOp().getDescription());
            }
            else if (value instanceof ParameterValueTreeNode) {
                ParameterValueTreeNode node = (ParameterValueTreeNode) value;
                this.setToolTipText(node.getParameterValue().getParameter()
                        .getDescription());
                if (!node.getParameterValue().isEnabled()) {
                    rc.setText("(" + rc.getText() + ")");
                }
            }
            return rc;
        }
    }
}
