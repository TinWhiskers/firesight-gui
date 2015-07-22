package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage;
import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;
import io.tinwhiskers.firesight.gui.PipelineTreeModel.ParameterValueTreeNode;
import io.tinwhiskers.firesight.gui.PipelineTreeModel.StageTreeNode;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.gson.JsonParser;

public class Main extends JFrame {
    private JTree pipelineTree;
    private JList inputImagesList;
    private JTextField imageDirectoryTextField;
    private JList outputImagesList;
    private Map<Stage, File> outputFiles;
    
    private JsonParser parser = new JsonParser();
    private File inputDirectory;
    private Ops ops = new Ops();
    private Pipeline pipeline;
    private PipelineTreeModel pipelineTreeModel;
    private DefaultListModel<File> inputImagesListModel = new DefaultListModel<File>();
    private DefaultListModel<File> outputImagesListModel = new DefaultListModel<File>();
    private FireSight fireSight = new FireSight(ops);

    public Main() {
        JSplitPane splitPane = new JSplitPane();
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
        JPanel pipelinePanel = new JPanel();
        splitPane.setLeftComponent(pipelinePanel);
        pipelinePanel.setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        pipelinePanel.add(toolBar, BorderLayout.NORTH);
        
        JButton btnAdd = new JButton(addStage);
        toolBar.add(btnAdd);
        
        JButton btnNewButton = new JButton(delStage);
        toolBar.add(btnNewButton);
        
        JScrollPane scrollPane = new JScrollPane();
        pipelinePanel.add(scrollPane, BorderLayout.CENTER);
        
        pipelineTreeModel = new PipelineTreeModel(ops);
        pipelineTree = new JTree(pipelineTreeModel);
        pipelineTree.setRootVisible(false);
        pipelineTree.setShowsRootHandles(true);
        pipelineTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        pipelineTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path.getPathCount() == 2) {
                    if (outputFiles == null) {
                        return;
                    }
                    StageTreeNode stageTreeNode = (StageTreeNode) path.getPathComponent(1);
                    Stage stage = stageTreeNode.getStage();
                    File file = outputFiles.get(stage);
                    int selectedIndex = outputImagesListModel.indexOf(file);
                    outputImagesList.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
                    outputImagesList.ensureIndexIsVisible(outputImagesList.getSelectedIndex());
                }
            }
        });
        pipelineTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath path = pipelineTree.getPathForLocation(e.getX(), e.getY());
                if (path != null && path.getPathCount() == 3 && e.getClickCount() == 2) {
                    ParameterValueTreeNode pvTreeNode = (ParameterValueTreeNode) path.getPathComponent(2);
                    ParameterValue pv = pvTreeNode.getParameterValue();
                    ParameterEditorDialog.show(Main.this, pv);
                    generateOutput();
                }
            }});
        scrollPane.setViewportView(pipelineTree);
        
        JPanel panel_1 = new JPanel();
        splitPane.setRightComponent(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setBorder(null);
        panel_1.add(splitPane_1, BorderLayout.CENTER);
        
        JPanel panelFiles = new JPanel();
        splitPane_1.setLeftComponent(panelFiles);
        panelFiles.setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBar_1 = new JToolBar();
        toolBar_1.setFloatable(false);
        panelFiles.add(toolBar_1, BorderLayout.NORTH);
        
        imageDirectoryTextField = new JTextField();
        imageDirectoryTextField.setEditable(false);
        toolBar_1.add(imageDirectoryTextField);
        imageDirectoryTextField.setColumns(10);
        
        JButton browseButton = new JButton(browseDirectory);
        toolBar_1.add(browseButton);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        panelFiles.add(scrollPane_1, BorderLayout.CENTER);
        
        inputImagesList = new JList(inputImagesListModel);
        inputImagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inputImagesList.setCellRenderer(new IconListRenderer());
        inputImagesList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                generateOutput();
            }
        });
        scrollPane_1.setViewportView(inputImagesList);
        
        JPanel panelOutput = new JPanel();
        splitPane_1.setRightComponent(panelOutput);
        panelOutput.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane_2 = new JScrollPane();
        panelOutput.add(scrollPane_2, BorderLayout.CENTER);
        
        outputImagesList = new JList(outputImagesListModel);
        outputImagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outputImagesList.setCellRenderer(new IconListRenderer());
        scrollPane_2.setViewportView(outputImagesList);
        splitPane_1.setDividerLocation(250);
        splitPane.setDividerLocation(250);
        
        setInputDirectory(new File("/Users/jason/Projects/FPD/firesight-gui/test1"));
    }
    
    private void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
        imageDirectoryTextField.setText(inputDirectory.getAbsolutePath());
        File[] files = inputDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".png") || name.endsWith(".jpg");
            }
        });
        inputImagesListModel.clear();
        for (File file : files) {
            inputImagesListModel.addElement(file);
        }
    }
    
    private void setOutputFiles(Map<Stage, File> output) {
        this.outputFiles = output;
        outputImagesListModel.clear();
        if (output == null) {
            return;
        }
        for (File file : output.values()) {
            outputImagesListModel.addElement(file);
        }
    }
    
    private void generateOutput() {
        if (inputImagesList.getSelectedIndex() == -1) {
            setOutputFiles(null);
            return;
        }
        File directory = new File(inputDirectory, ".tmp");
        try {
            directory.mkdirs();
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        Map<Stage, File> outputFiles = fireSight.generateOutputImages(
                pipelineTreeModel.getPipeline(), 
                inputImagesListModel.get(inputImagesList.getSelectedIndex()), 
                directory);
        setOutputFiles(outputFiles);
    }
    
    private Action addStage = new AbstractAction("+") {
        public void actionPerformed(ActionEvent e) {
            OpSelectionDialog dialog = new OpSelectionDialog(
                    Main.this,
                    "Select op...",
                    "Please select an op from the list below.",
                    ops);
            dialog.setVisible(true);
            Op op = dialog.getSelectedOp();
            if (op == null) {
                return;
            }
            final Stage stage = new Stage(op);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pipelineTreeModel.addStage(stage);
                    pipelineTree.expandPath(new TreePath(pipelineTreeModel.getRoot()));
                    generateOutput();
                }
            });
        }
    };
    
    private Action delStage = new AbstractAction("-") {
        public void actionPerformed(ActionEvent e) {
            TreePath path = pipelineTree.getSelectionPath();
            StageTreeNode stageTreeNode = (StageTreeNode) path.getPathComponent(1);
            pipelineTreeModel.removeStage(stageTreeNode);
        }
    };
    
    private Action browseDirectory = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
            JFileChooser j = new JFileChooser();
            j.setSelectedFile(inputDirectory);
            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            j.setMultiSelectionEnabled(false);
            if (j.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                File directory = j.getSelectedFile();
                setInputDirectory(directory);
            }      
        }
    };
    
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();
                main.setSize(1024, 768);
                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                main.show();
            }
        });
    }
}
