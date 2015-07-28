package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage.ParameterValue;

import java.awt.Frame;

import javax.swing.JOptionPane;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ParameterEditorDialog {
    public static JsonElement show(Frame owner, ParameterValue pv) {
        String title = "Set " + pv.getParameter().getName();
        String message = breakLongString(pv.getParameter().getDescription(), 60);
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
            case NumberArray: {
                Object ret = JOptionPane.showInputDialog(
                        owner, 
                        message, 
                        pv.getValue().toString());
                if (ret == null) {
                    return null;
                }
                try {
                    JsonParser parser = new JsonParser();
                    return parser.parse((String) ret).getAsJsonArray();
                }
                catch (Exception e) {
                    return null;
                }
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
