package io.tinwhiskers.firesight.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class DirectoryInputPanel extends JPanel {
    private File inputDirectory;
    private JTextField imageDirectoryTextField;
    private JList<File> inputImagesList;
    private DefaultListModel<File> inputImagesListModel = new DefaultListModel<File>();
    
    public DirectoryInputPanel(final Main main) {
        setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBar_1 = new JToolBar();
        add(toolBar_1, BorderLayout.NORTH);
        toolBar_1.setFloatable(false);
        
        imageDirectoryTextField = new JTextField();
        imageDirectoryTextField.setEditable(false);
        toolBar_1.add(imageDirectoryTextField);
        imageDirectoryTextField.setColumns(10);
        
        JButton browseButton = new JButton(browseDirectory);
        toolBar_1.add(browseButton);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        add(scrollPane_1, BorderLayout.CENTER);
        
        inputImagesList = new JList<File>(inputImagesListModel);
        inputImagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inputImagesList.setCellRenderer(new IconListRenderer(inputImagesList));
        inputImagesList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                main.generateOutput();
            }
        });
        scrollPane_1.setViewportView(inputImagesList);
        
        
        String defaultInputDirectoryPath = Preferences.userNodeForPackage(getClass()).get("inputDirectory", null);
        if (defaultInputDirectoryPath != null) {
            File defaultInputDirectory = new File(defaultInputDirectoryPath);
            if (defaultInputDirectory.exists()) {
                setInputDirectory(defaultInputDirectory);
            }
        }        
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
        Preferences.userNodeForPackage(getClass()).put("inputDirectory", inputDirectory.getAbsolutePath());
    }
    
    public File getInputFile() {
        if (inputImagesList.getSelectedIndex() == -1) {
            return null;
        }
        return inputImagesListModel.get(inputImagesList.getSelectedIndex());
    }
    
    private Action browseDirectory = new AbstractAction("...") {
        public void actionPerformed(ActionEvent e) {
            JFileChooser j = new JFileChooser();
            j.setSelectedFile(inputDirectory);
            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            j.setMultiSelectionEnabled(false);
            if (j.showOpenDialog(getTopLevelAncestor()) == JFileChooser.APPROVE_OPTION) {
                File directory = j.getSelectedFile();
                setInputDirectory(directory);
            }      
        }
    };
}
