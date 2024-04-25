package cn.lary.id.service.segment;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author paul 2024/4/25
 */

public class SegConnPool {
    private static BasicDataSource dataSource;
    static {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/mydatabase");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        dataSource.setMaxTotal(20);
        dataSource.setMinIdle(5);
    }
    public static Connection get()  {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
