package io.tinwhiskers.firesight.gui;

import java.awt.Canvas;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class CameraInputPanel extends JPanel {
    private JTextField textField;
    
    public CameraInputPanel(Main main) {
        setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,}));
        
        JLabel lblDeviceId = new JLabel("Device ID");
        add(lblDeviceId, "2, 2, right, default");
        
        textField = new JTextField();
        add(textField, "4, 2, fill, default");
        textField.setColumns(10);
        
        JButton btnConnect = new JButton("Connect");
        add(btnConnect, "6, 2");
        
        JButton btnCapture = new JButton("Capture");
        add(btnCapture, "2, 4, 5, 1");
        
        Canvas canvas = new Canvas();
        add(canvas, "2, 6, 5, 1");
    }
}
