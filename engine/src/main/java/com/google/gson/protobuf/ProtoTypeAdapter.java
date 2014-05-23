/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.protobuf;

import com.google.gson.*;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gson type adapter for protocol buffers
 *
 * @author Inderjeet Singh
 */
public class ProtoTypeAdapter implements JsonSerializer<GeneratedMessage>, JsonDeserializer<GeneratedMessage> {
    private static final Map<String, Map<Class<?>, Method>> MAP_OF_MAP_OF_METHODS = new HashMap<>();

    private static Method getCachedMethod(Class<?> clazz, String methodName, Class<?>... methodParamTypes) throws NoSuchMethodException {
        Map<Class<?>, Method> mapOfMethods = MAP_OF_MAP_OF_METHODS.get(methodName);
        Method method = null;
        if (mapOfMethods == null) {
            mapOfMethods = new HashMap<>();
            MAP_OF_MAP_OF_METHODS.put(methodName, mapOfMethods);
        } else {
            method = mapOfMethods.get(clazz);
        }
        if (method == null) {
            method = clazz.getMethod(methodName, methodParamTypes);
            mapOfMethods.put(clazz, method);
        }
        return method;
    }

    @Override
    public JsonElement serialize(GeneratedMessage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject ret = new JsonObject();

        for (Map.Entry<FieldDescriptor, Object> fieldPair : src.getAllFields().entrySet()) {
            final FieldDescriptor desc = fieldPair.getKey();
            if (desc.isRepeated()) {
                List<?> fieldList = (List<?>) fieldPair.getValue();
                if (!fieldList.isEmpty()) {
                    JsonArray array = new JsonArray();
                    for (Object o : fieldList) {
                        array.add(context.serialize(o));
                    }
                    ret.add(desc.getName(), array);
                }
            } else {
                ret.add(desc.getName(), context.serialize(fieldPair.getValue()));
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GeneratedMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            Class<? extends GeneratedMessage> protoClass = (Class<? extends GeneratedMessage>) typeOfT;
            try {
                // Invoke the ProtoClass.newBuilder() method
                Object protoBuilder = getCachedMethod(protoClass, "newBuilder").invoke(null);
                Class<?> builderClass = protoBuilder.getClass();

                Descriptor protoDescriptor = (Descriptor) getCachedMethod(protoClass, "getDescriptor").invoke(null);
                // Call setters on all of the available fields
                for (FieldDescriptor fieldDescriptor : protoDescriptor.getFields()) {
                    String name = fieldDescriptor.getName();
                    if (jsonObject.has(name)) {
                        JsonElement jsonElement = jsonObject.get(name);
                        String fieldName = name + "_";
                        Field field = protoClass.getDeclaredField(fieldName);
                        Type fieldType = field.getGenericType();
                        Object fieldValue = context.deserialize(jsonElement, fieldType);
                        Method method = getCachedMethod(builderClass, "setField", FieldDescriptor.class, Object.class);
                        method.invoke(protoBuilder, fieldDescriptor, fieldValue);
                    }
                }

                // Invoke the build method to return the final proto
                return (GeneratedMessage) getCachedMethod(builderClass, "build").invoke(protoBuilder);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                throw new JsonParseException(e);
            }
        } catch (Exception e) {
            throw new JsonParseException("Error while parsing proto: ", e);
        }
    }
}
