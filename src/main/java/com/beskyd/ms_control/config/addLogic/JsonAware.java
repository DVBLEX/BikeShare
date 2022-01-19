package com.beskyd.ms_control.config.addLogic;

import lombok.SneakyThrows;
import org.json.JSONObject;

import java.lang.reflect.Field;

public interface JsonAware {
    
    default String toJSON() {
        return toJSONObject().toString();
    }


    default JSONObject toJSONObject() {
        Class<?> thisClass;

        JSONObject jsonObject = new JSONObject();

        try {
            thisClass = Class.forName(this.getClass().getName());

            Field[] aClassFields = thisClass.getDeclaredFields();

            for (Field field : aClassFields) {
                field.setAccessible(true);
                jsonObject.put(field.getName(), field.get(this));
            }
        } catch (Exception e) {
            jsonObject.put("error", "an exception occurred during construction of this JSON");
        }

        return jsonObject;
    }

}
