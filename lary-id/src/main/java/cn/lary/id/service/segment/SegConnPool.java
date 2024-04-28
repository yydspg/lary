package cn.lary.id.service.segment;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *  database connection pool
 * @author paul 2024/4/25
 */

public class SegConnPool {

    private static final DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://192.168.142.6:3306/lary_id");
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setConnectionTimeout(30000);
        config.setAutoCommit(true);
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        dataSource= new HikariDataSource(config);
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
