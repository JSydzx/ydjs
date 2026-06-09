package com.zjgsu.teamplatform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 启动时补齐旧数据库缺失字段，兼容已存在的 Docker MySQL 数据卷。
 */
@Component
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    /**
     * 检查并补齐后续版本新增的字段。
     */
    @Override
    public void run(String... args) throws Exception {
        if (!isMySql()) {
            return;
        }
        addColumnIfMissing("user", "major", "VARCHAR(100)");
        addColumnIfMissing("user", "grade", "VARCHAR(50)");
        addColumnIfMissing("user", "skills", "VARCHAR(255)");
        addColumnIfMissing("user", "bio", "VARCHAR(500)");
        addColumnIfMissing("notification", "related_id", "BIGINT");
    }

    /**
     * MySQL 旧数据卷不会重新执行 schema.sql，因此这里用 information_schema 做幂等迁移。
     */
    private void addColumnIfMissing(String tableName, String columnName, String columnDefinition) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                tableName,
                columnName
        );
        if (count == null || count == 0) {
            jdbcTemplate.execute("ALTER TABLE `" + tableName + "` ADD COLUMN `" + columnName + "` " + columnDefinition);
        }
    }

    /**
     * 迁移仅针对 Docker/MySQL；测试环境 H2 使用 schema.sql 即可。
     */
    private boolean isMySql() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String productName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            return productName.contains("mysql") || productName.contains("mariadb");
        }
    }
}
