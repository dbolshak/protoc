package com.dbolshak.protoc.proto;

import org.junit.Test;

import static com.dbolshak.protoc.proto.Pojo.Person;
import static org.junit.Assert.assertEquals;

/**
 * Unit test to track changes in proto files
 */
public class PersonTest {

    @Test
    public void testProto() {
        Person person = Person.newBuilder().setId(100).setName("Denis").setEmail("bolshakov.denis@gmail.com").build();

        assertEquals(100, person.getId());
        assertEquals("Denis", person.getName());
        assertEquals("bolshakov.denis@gmail.com", person.getEmail());
    }

}
