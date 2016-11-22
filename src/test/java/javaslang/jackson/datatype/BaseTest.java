package javaslang.jackson.datatype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import org.junit.Assert;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Seq;

public class BaseTest {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    protected interface WrapperObject {
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    protected interface WrapperArray {
    }

    protected void verifySerialization(TypeReference<?> typeReference, List<Tuple2<?, String>> testValues) throws IOException {
        ObjectWriter writer = mapper().writerFor(typeReference);
        for (Tuple2<?, String> testValue : testValues) {
            Object src = testValue._1();
            String expectedJson = testValue._2();

            String json = writer.writeValueAsString(src);
            Assert.assertEquals(expectedJson, json);

            Object dst = mapper().readValue(json, typeReference);
            Assert.assertEquals(src, dst);
        }
    }

    protected ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaslangModule());
        return mapper;
    }

    protected ObjectMapper mapper(JavaslangModule.Settings settings) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaslangModule(settings));
        return mapper;
    }

    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaslangModule());
        return xmlMapper;
    }

    public XmlMapper xmlMapperJaxb() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
        xmlMapper.registerModule(new JavaslangModule());
        return xmlMapper;
    }

    protected String wrapToArray(String as, String json) {
        return "[\"" + as + "\"," + json + "]";
    }

    protected String wrapToObject(String as, String json) {
        return "{\"" + as + "\":" + json + "}";
    }

    protected String genJsonList(Object... list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            appendObj(sb, list[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    protected String genJsonMap(java.util.Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(entry.getKey().toString()).append("\":");
            Object value = entry.getValue();
            if (value instanceof Collection) {
                sb.append("[");
                int j = 0;
                for (Object v : ((Collection) value)) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    appendObj(sb, v);
                    j++;
                }
                sb.append("]");
            } else {
                appendObj(sb, value);
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    private void appendObj(StringBuilder sb, Object o) {
        if (o instanceof java.lang.String) {
            sb.append("\"").append(o).append("\"");
        } else if (o instanceof javaslang.collection.Seq) {
            sb.append(genJsonList(((Seq) o).toJavaList().toArray()));
        } else {
            sb.append(o);
        }
    }
}
