package io.tinwhiskers.firesight.gui;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Op {
    private String name;
    private String description;
    private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();
    
    public Op(JsonObject o) {
        parse(o);
    }
    
    private void parse(JsonObject o) {
        name = o.get("name").getAsString();
        description = o.get("description").getAsString();
        parseParameters(o.get("parameters").getAsJsonArray());
    }
    
    private void parseParameters(JsonArray a) {
        for (JsonElement e : a) {
            Parameter p = new Parameter(e.getAsJsonObject());
            parameters.put(p.getName(), p);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public HashMap<String, Parameter> getParameters() {
        return parameters;
    }
    
    public static class Parameter {
        enum Type {
            Boolean,
            String,
            Number,
            StringEnum,
            NumberEnum
        };
        
        private String name;
        private String description;
        private Type type;
        private JsonPrimitive def;
        private JsonArray options;
        
        public Parameter(JsonObject o) {
            parse(o);
        }
        
        private void parse(JsonObject o) {
            name = o.get("name").getAsString();
            description = o.get("description").getAsString();
            // determine the type by looking at the default
            // if it's a string or number, look to see if there is an
            // options property. If so the type is the enum specialization
            // of the scalar
            def = o.get("default").getAsJsonPrimitive();
            if (def.isBoolean()) {
                type = Type.Boolean;
            }
            else if (def.isNumber()) {
                type = Type.Number;
                if (o.has("options") && o.get("options").getAsJsonArray().size() > 0) {
                    type = Type.NumberEnum;
                    options = o.get("options").getAsJsonArray();
                }
            }
            else if (def.isString()) {
                type = Type.String;
                if (o.has("options") && o.get("options").getAsJsonArray().size() > 0) {
                    type = Type.StringEnum;
                    options = o.get("options").getAsJsonArray();
                }
            }
        }
        
        public String getName() {
            return name;
        }
                
        public Type getType() {
            return type;
        }
        
        public JsonPrimitive getDefault() {
            return def;
        }
        
        public JsonArray getOptions() {
            return options;
        }
        
        @Override
        public String toString() {
            return getName();
        }
    }
}
