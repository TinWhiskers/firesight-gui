package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Op.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

public class Pipeline {
    private List<Stage> stages = new ArrayList<Stage>();
    private int index = 1;
    
    public Pipeline() {
    }
    
    public void addStage(Stage stage) {
        if (stage.getName() == null) {
            stage.setName("s" + index++);
        }
        stages.add(stage);
    }
    
    public void removeStage(Stage stage) {
        stages.remove(stage);
    }
    
    public List<Stage> getStages() {
        return stages;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    public JsonArray toJson() {
        JsonArray a = new JsonArray();
        for (Stage stage : stages) {
            a.add(stage.toJson());
        }
        return a;
    }
    
    public static class Stage {
        private String name;
        private Op op;
        private HashMap<String, ParameterValue> parameters = new HashMap<String, ParameterValue>();
        
        public Stage(String name, Op op) {
            this.name = name;
            this.op = op;
            // create all the parameters based on the op's defaults
            for (Parameter param : op.getParameters().values()) {
                parameters.put(param.getName(), new ParameterValue(param, param.getDefault()));
            }
        }
        
        public Stage(Op op) {
            this(null, op);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Op getOp() {
            return op;
        }

        public void setOp(Op op) {
            this.op = op;
        }
        
        public HashMap<String, ParameterValue> getParameters() {
            return parameters;
        }
        
        public void setParameterValue(String param, JsonPrimitive value) {
            parameters.get(param).setValue(value);
        }

        @Override
        public String toString() {
            return op.getName() + " (" + name + ")";
        }
        
        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("name", name);
            o.addProperty("op", op.getName());
            for (ParameterValue pv : parameters.values()) {
                o.add(pv.getParameter().getName(), pv.toJson());
            }
            return o;
        }
        
        public static class ParameterValue {
            private Parameter parameter;
            private JsonPrimitive value;
            
            public ParameterValue(Parameter parameter, JsonPrimitive value) {
                this.parameter = parameter;
                this.value = value;
            }
            
            public Parameter getParameter() {
                return parameter;
            }
            
            public void setParameter(Parameter parameter) {
                this.parameter = parameter;
            }
            
            public JsonPrimitive getValue() {
                return value;
            }
            
            public void setValue(JsonPrimitive value) {
                this.value = value;
            }
            
            @Override
            public String toString() {
                return parameter.getName() + " = " + value.toString();
            }
            
            public JsonPrimitive toJson() {
                return value;
            }
        }
    }
}
