package version01;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author yolo
 * @date 2020/1/13-20:37
 */
public class 数据库存储Demo2 {
    public static void main(String[] args) throws SQLException {
       MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
       dataSource.setServerName("127.0.0.1");
       dataSource.setPort(3305);
       dataSource.setUser("root");
       dataSource.setPassword("mmwan980815");
       dataSource.setDatabaseName("tangshi");
       dataSource.setUseSSL(false);
       dataSource.setCharacterEncoding("UTF-8");

        String 朝代 = "唐代";
        String 作者 = "白居易";
        String 标题 = "问刘十九";
        String 正文 = "绿蚁新醅酒，红泥小火炉。晚来天欲雪，能饮一杯无？";

        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO t_tangshi " +
                    "(sha256, dynasty, title, author, " +
                    "content, word) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "sha256");
                statement.setString(2, 朝代);
                statement.setString(3, 标题);
                statement.setString(4, 作者);
                statement.setString(5, 正文);
                statement.setString(6, "");

                statement.executeUpdate();
            }
        }
    }
}
