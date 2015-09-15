package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;

import java.awt.Frame;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ParameterEditorDialog {
    public static boolean show(Frame owner, ParameterValue pv) {
        String title = "Set " + pv.getParameter().getName();
        String message = breakLongString(pv.getParameter().getDescription(), 60);
        switch (pv.getParameter().getType()) {
            case Boolean: {
                JCheckBox enabledCheckbox = new JCheckBox("Enabled?", true);
                JCheckBox valueComponent = new JCheckBox(pv.getParameter().getName(), pv.getValue().getAsBoolean());
                int ret = JOptionPane.showOptionDialog(
                        owner, 
                        new Object[] { message, valueComponent }, 
                        title, 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { "OK", "Cancel", enabledCheckbox }, 
                        "OK");
                if (ret != 0) {
                    return false;
                }
                pv.setEnabled(enabledCheckbox.isSelected());
                pv.setValue(new JsonPrimitive(valueComponent.isSelected()));
                return true;
            }
            case String: {
                JCheckBox enabledCheckbox = new JCheckBox("Enabled?", true);
                JTextField valueComponent = new JTextField(pv.getValue().getAsString());
                valueComponent.requestFocus();
                int ret = JOptionPane.showOptionDialog(
                        owner, 
                        new Object[] { message, valueComponent }, 
                        title, 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { "OK", "Cancel", enabledCheckbox }, 
                        "OK");
                if (ret != 0) {
                    return false;
                }
                pv.setEnabled(enabledCheckbox.isSelected());
                pv.setValue(new JsonPrimitive(valueComponent.getText()));
                return true;
            }
            case Number: {
                JCheckBox enabledCheckbox = new JCheckBox("Enabled?", true);
                JTextField valueComponent = new JTextField(pv.getValue().getAsString());
                valueComponent.requestFocus();
                int ret = JOptionPane.showOptionDialog(
                        owner, 
                        new Object[] { message, valueComponent }, 
                        title, 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { "OK", "Cancel", enabledCheckbox }, 
                        "OK");
                if (ret != 0) {
                    return false;
                }
                pv.setEnabled(enabledCheckbox.isSelected());
                pv.setValue(new JsonPrimitive(new Double(valueComponent.getText())));
                return true;
            }
            case StringEnum: {
                Object[] options = new Object[pv.getParameter().getOptions().size()];
                for (int i = 0; i < options.length; i++) {
                    options[i] = pv.getParameter().getOptions().get(i).getAsString();
                }
                JCheckBox enabledCheckbox = new JCheckBox("Enabled?", true);
                JComboBox valueComponent = new JComboBox(options);
                valueComponent.setSelectedItem(pv.getValue().getAsString());
                int ret = JOptionPane.showOptionDialog(
                        owner, 
                        new Object[] { message, valueComponent }, 
                        title, 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { "OK", "Cancel", enabledCheckbox }, 
                        "OK");
                if (ret != 0) {
                    return false;
                }
                pv.setEnabled(enabledCheckbox.isSelected());
                pv.setValue(new JsonPrimitive((String) valueComponent.getSelectedItem()));
                return true;
            }
            case NumberEnum: {
                Object[] options = new Object[pv.getParameter().getOptions().size()];
                for (int i = 0; i < options.length; i++) {
                    options[i] = pv.getParameter().getOptions().get(i).getAsDouble();
                }
                JCheckBox enabledCheckbox = new JCheckBox("Enabled?", true);
                JComboBox valueComponent = new JComboBox(options);
                valueComponent.setSelectedItem(pv.getValue().getAsDouble());
                int ret = JOptionPane.showOptionDialog(
                        owner, 
                        new Object[] { message, valueComponent }, 
                        title, 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { "OK", "Cancel", enabledCheckbox }, 
                        "OK");
                if (ret != 0) {
                    return false;
                }
                pv.setEnabled(enabledCheckbox.isSelected());
                pv.setValue(new JsonPrimitive((Double) valueComponent.getSelectedItem()));
                return true;
            }
            case NumberArray: {
                JCheckBox enabledCheckbox = new JCheckBox("Enabled?", true);
                JTextField valueComponent = new JTextField(pv.getValue().toString());
                int ret = JOptionPane.showOptionDialog(
                        owner, 
                        new Object[] { message, valueComponent }, 
                        title, 
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { "OK", "Cancel", enabledCheckbox }, 
                        "OK");
                if (ret != 0) {
                    return false;
                }
                pv.setEnabled(enabledCheckbox.isSelected());
                try {
                    JsonParser parser = new JsonParser();
                    pv.setValue(parser.parse(valueComponent.getText()).getAsJsonArray());
                }
                catch (Exception e) {
                    return false;
                }
                return true;
            }
            default:
                throw new Error("Unrecognized type " + pv.getParameter().getType());
        }
    }
    
    /** Force-inserts line breaks into an otherwise human-unfriendly long string.
     * */
    private static String breakLongString( String input, int charLimit )
    {
        String output = "", rest = input;
        int i = 0;

         // validate.
        if ( rest.length() < charLimit ) {
            output = rest;
        }
        else if (  !rest.equals("")  &&  (rest != null)  )  // safety precaution
        {
            do
            {    // search the next index of interest.
                i = rest.lastIndexOf(" ", charLimit) +1;
                if ( i == -1 )
                    i = charLimit;
                if ( i > rest.length() )
                    i = rest.length();

                 // break!
                output += rest.substring(0,i) +"\n";
                rest = rest.substring(i);
            }
            while (  (rest.length() > charLimit)  );
            output += rest;
        }

        return output;
    }    
}
