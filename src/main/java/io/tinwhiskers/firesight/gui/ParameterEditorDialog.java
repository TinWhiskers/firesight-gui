package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;

import java.awt.Frame;

import javax.swing.JOptionPane;

import com.google.gson.JsonPrimitive;

public class ParameterEditorDialog {
    public static JsonPrimitive show(Frame owner, ParameterValue pv) {
        String title = null;
        String message = "Select a value for " + pv.getParameter().getName();
        switch (pv.getParameter().getType()) {
            case Boolean: {
                Object ret = JOptionPane.showInputDialog(
                        owner, 
                        message, 
                        title, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        new Object[] { Boolean.TRUE, Boolean.FALSE}, 
                        pv.getValue().getAsBoolean());
                if (ret == null) {
                    return null;
                }
                return new JsonPrimitive((Boolean) ret);
            }
            case String: {
                Object ret = JOptionPane.showInputDialog(
                        owner, 
                        message, 
                        pv.getValue().getAsString());
                if (ret == null) {
                    return null;
                }
                return new JsonPrimitive((String) ret);
            }
            case Number: {
                Object ret = JOptionPane.showInputDialog(
                        owner, 
                        message,
                        pv.getValue().getAsString());
                if (ret == null) {
                    return null;
                }
                return new JsonPrimitive(new Double((String) ret));
            }
            case StringEnum: {
                Object[] options = new Object[pv.getParameter().getOptions().size()];
                for (int i = 0; i < options.length; i++) {
                    options[i] = pv.getParameter().getOptions().get(i).getAsString();
                }
                Object ret = JOptionPane.showInputDialog(
                        owner, 
                        message, 
                        title, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        options, 
                        pv.getValue().getAsString());
                if (ret == null) {
                    return null;
                }
                return new JsonPrimitive((String) ret);
            }
            case NumberEnum: {
                Object[] options = new Object[pv.getParameter().getOptions().size()];
                for (int i = 0; i < options.length; i++) {
                    options[i] = pv.getParameter().getOptions().get(i).getAsDouble();
                }
                Object ret = JOptionPane.showInputDialog(
                        owner, 
                        message, 
                        title, 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        options, 
                        pv.getValue().getAsDouble());
                if (ret == null) {
                    return null;
                }
                return new JsonPrimitive((Double) ret);
            }
            default:
                return null;
        }
    }
}
