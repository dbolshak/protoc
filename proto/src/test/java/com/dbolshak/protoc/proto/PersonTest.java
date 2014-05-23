package com.dbolshak.protoc.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

import static com.dbolshak.protoc.proto.Pojo.Person;
import static org.junit.Assert.assertEquals;

/**
 * Unit test to track changes in proto files
 */
public class PersonTest {

    @Test
    public void testProto() throws InvalidProtocolBufferException {
        Person person = Person.newBuilder().setId(100).setName("Denis").setEmail("bolshakov.denis@gmail.com").build();

        assertEquals(100, person.getId());
        assertEquals("Denis", person.getName());
        assertEquals("bolshakov.denis@gmail.com", person.getEmail());
        assertEquals(1, person.getVersion());

        Person person1 = Person.parseFrom(person.toByteArray());
        assertEquals(100, person1.getId());
        assertEquals("Denis", person1.getName());
        assertEquals("bolshakov.denis@gmail.com", person1.getEmail());
        assertEquals(1, person1.getVersion());
    }
}
