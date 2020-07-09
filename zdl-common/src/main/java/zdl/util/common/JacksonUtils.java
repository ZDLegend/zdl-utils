package zdl.util.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Set;

/**
 * Created by ZDLegend on 2020/5/22 11:22
 */
public class JacksonUtils {

    public static final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode validate(JsonSchema jsonSchema, String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
        Set<ValidationMessage> validationMessages = jsonSchema.validate(jsonNode);
        if (null != validationMessages && !validationMessages.isEmpty()) {
            throw new RuntimeException(validationMessages.toString());
        }
        return jsonNode;
    }

    public static void main(String[] args) {
        JsonSchema configSchema =
                schemaFactory.getSchema(JacksonUtils.class.getClassLoader().getResourceAsStream("config_source.json"));
        System.out.println(configSchema);
    }
}
