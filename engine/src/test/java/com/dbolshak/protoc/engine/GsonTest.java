package com.dbolshak.protoc.engine;

import com.dbolshak.protoc.proto.Pojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.protobuf.ProtoTypeAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test to test GSON + GPB
 */
public class GsonTest {

    @Test
    public void buildJson() {
        Pojo.Person person = Pojo.Person.newBuilder().setId(100).setName("Denis").setEmail("bolshakov.denis@gmail.com").build();

        Gson gson = new GsonBuilder().registerTypeAdapter(Pojo.Person.class, new ProtoTypeAdapter()).create();
        assertEquals("{\"id\":100,\"name\":\"Denis\",\"email\":\"bolshakov.denis@gmail.com\"}", gson.toJson(person));
    }

    @Test
    public void buildProto() {
        String json = "{\"id\":100,\"name\":\"Denis\",\"email\":\"bolshakov.denis@gmail.com\"}";
        Gson gson = new GsonBuilder().registerTypeAdapter(Pojo.Person.class, new ProtoTypeAdapter()).create();
        Pojo.Person person = gson.fromJson(json, Pojo.Person.class);

        assertEquals(100, person.getId());
        assertEquals("Denis", person.getName());
        assertEquals("bolshakov.denis@gmail.com", person.getEmail());
        assertEquals(1, person.getVersion());
    }

    @Test
    public void testCache() {
        String json = "{\"id\":1001,\"name\":\"Denis1\",\"email\":\"bolshakov.denis@gmail.com1\"}";
        Gson gson = new GsonBuilder().registerTypeAdapter(Pojo.Person.class, new ProtoTypeAdapter()).create();
        Pojo.Person person = gson.fromJson(json, Pojo.Person.class);

        assertEquals(1001, person.getId());
        assertEquals("Denis1", person.getName());
        assertEquals("bolshakov.denis@gmail.com1", person.getEmail());
        assertEquals(1, person.getVersion());
    }
}
