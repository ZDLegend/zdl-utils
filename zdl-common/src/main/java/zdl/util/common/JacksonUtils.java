package zdl.util.common;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

/**
 * Created by ZDLegend on 2020/5/22 11:22
 */
public class JacksonUtils {

    public static final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public static void main(String[] args) {
        JsonSchema configSchema =
                schemaFactory.getSchema(JacksonUtils.class.getClassLoader().getResourceAsStream("config_source.json"));
        System.out.println(configSchema);
    }
}
