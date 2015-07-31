package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Op.Parameter;
import io.tinwhiskers.firesight.gui.Op.Parameter.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
        
        public void setParameterValueEnabled(String param, boolean enabled) {
            parameters.get(param).setEnabled(enabled);
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
                if (pv.isEnabled()) {
                    o.add(pv.getParameter().getName(), pv.toJson());
                }
            }
            return o;
        }
        
        public static class ParameterValue {
            private Parameter parameter;
            private JsonElement value;
            private boolean enabled = false;
            
            public ParameterValue(Parameter parameter, JsonElement value) {
                this.parameter = parameter;
                setValue(value);
            }
            
            public Parameter getParameter() {
                return parameter;
            }
            
            public void setParameter(Parameter parameter) {
                this.parameter = parameter;
            }
            
            public JsonElement getValue() {
                return value;
            }
            
            public void setValue(JsonElement value) {
                // enforce the type
                Type type = parameter.getType();
                if (type == Type.Boolean){
                    if (!isBoolean(value)) {
                        throw new Error("Invalid value " + value + " for type " + type);
                    }
                }
                else if (type == Type.Number){
                    if (!isNumber(value)) {
                        throw new Error("Invalid value " + value + " for type " + type);
                    }
                }
                else if (type == Type.String){
                    if (!isString(value)) {
                        throw new Error("Invalid value " + value + " for type " + type);
                    }
                }
                else if (type == Type.StringEnum){
                    if (!isString(value)) {
                        throw new Error("Invalid value " + value + " for type " + type);
                    }
                }
                else if (type == Type.NumberEnum){
                    if (!isNumber(value)) {
                        throw new Error("Invalid value " + value + " for type " + type);
                    }
                }
                else if (type == Type.NumberArray){
                    if (!value.isJsonArray()) {
                        throw new Error("Invalid value " + value + " for type " + type);
                    }
                }
                else {
                    throw new Error("Unrecognized type " + type);
                }
                this.value = value;
            }
            
            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            
            public boolean isBoolean(JsonElement e) {
                return e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean();
            }
            
            public boolean isString(JsonElement e) {
                return e.isJsonPrimitive() && e.getAsJsonPrimitive().isString();
            }
            
            public boolean isNumber(JsonElement e) {
                return e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber();
            }
            
            @Override
            public String toString() {
                return parameter.getName() + " = " + value.toString();
            }
            
            public JsonElement toJson() {
                return value;
            }
        }
    }
}
