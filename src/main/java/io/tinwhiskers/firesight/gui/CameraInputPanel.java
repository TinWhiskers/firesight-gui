package io.tinwhiskers.firesight.gui;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class CameraInputPanel extends JPanel implements Runnable {
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }    

    private JTextField textField;

    private VideoCapture fg = new VideoCapture();
    private Thread thread;
    private int deviceIndex = 0;
    private CameraView cameraView;
    private File inputFile;
    private final Main main;
    
    public CameraInputPanel(Main main) {
        this.main = main;
        
        setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),}));
        
        JLabel lblId = new JLabel("ID:");
        add(lblId, "2, 2");
        
        textField = new JTextField();
        add(textField, "4, 2, fill, default");
        textField.setColumns(10);
        textField.setText("0");
        
        JButton btnConnect = new JButton(connect);
        add(btnConnect, "6, 2");
        
        JButton btnCapture = new JButton(capture);
        add(btnCapture, "2, 4, 5, 1");
        
        cameraView = new CameraView();
        add(cameraView, "2, 6, 5, 1, fill, fill");
    }
    
    public synchronized void setDeviceIndex(int deviceIndex) {
        this.deviceIndex = deviceIndex;
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            thread = null;
        }
        try {
            fg.open(deviceIndex);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        thread = new Thread(this);
        thread.start();
    }    
    
    public void run() {
        while (!Thread.interrupted()) {
            try {
                BufferedImage image = capture();
                if (image != null) {
                    cameraView.frameReceived(image);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000 / 10);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public synchronized BufferedImage capture() {
        if (thread == null) {
            setDeviceIndex(deviceIndex);
        }
        try {
            Mat mat = new Mat();
            if (!fg.read(mat)) {
                return null;
            }
            return OpenCvUtils.toBufferedImage(mat);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public File getInputFile() {
        return inputFile;
    }
    
    private Action connect = new AbstractAction("Connect") {
        public void actionPerformed(ActionEvent e) {
            int deviceIndex = Integer.parseInt(textField.getText());
            setDeviceIndex(deviceIndex);
        }
    };
    
    private Action capture = new AbstractAction("Capture") {
        public void actionPerformed(ActionEvent e) {
            try {
                BufferedImage image = capture();
                File file = Files.createTempFile("firesight-gui-cam", ".png").toFile();
                ImageIO.write(image, "png", file);
                inputFile = file;
                main.generateOutput();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    };
}
