//package com.example.wellness.config.jackson;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.Link;
//
//import java.io.IOException;
//
//public class EntityModelSerializer extends StdSerializer<EntityModel> { // Use raw type here
//
//    protected EntityModelSerializer() {
//        super(EntityModel.class);
//    }
//
//    @Override
//    public void serialize(EntityModel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
//        gen.writeStartObject();
//        gen.writeObjectField("content", value.getContent());
//
//        if (!value.getLinks().isEmpty()) {
//            gen.writeArrayFieldStart("links");
//            for (Link link : value.getLinks()) {
//                gen.writeStartObject();
//                gen.writeStringField("rel", link.getRel().value());
//                gen.writeStringField("href", link.getHref());
//                gen.writeEndObject();
//            }
//            gen.writeEndArray();
//        }
//
//        gen.writeEndObject();
//    }
//}
