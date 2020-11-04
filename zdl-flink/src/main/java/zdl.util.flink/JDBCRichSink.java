package zdl.util.flink;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/02/ 17:13
 */
public class JDBCRichSink extends RichSinkFunction<JsonNode> {

    private Connection connection;
    private PreparedStatement preparedStatement;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        String className = "com.mysql.jdbc.Driver";
        Class.forName(className);
        String url = "jdbc:mysql://localhost:3306/flink";
        String user = "root";
        String password = "123456";
        connection = DriverManager.getConnection(url, user, password);
        String sql = "INSERT INTO TableName(id,name) VALUE (?,?,?,?)";
        preparedStatement = connection.prepareStatement(sql);
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void invoke(JsonNode value, Context context) throws Exception {
        int id = value.get("id").asInt();
        String name = value.asText("name");

        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, name);

        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            System.out.println("value=" + value);
        } else {
            System.out.println("error");
        }
    }
}
