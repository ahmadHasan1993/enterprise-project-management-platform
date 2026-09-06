package com.ahmadhassan.projectmanagement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostgreSqlIntegrationTest extends PostgreSqlIntegrationTestSupport {

    @Autowired
    DataSource dataSource;

    @Test
    void flywayRunsAgainstPostgreSqlContainer() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Boolean flywayHistoryTableExists = jdbcTemplate.queryForObject(
                """
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                      AND table_name = 'flyway_schema_history'
                )
                """,
                Boolean.class
        );

        assertThat(flywayHistoryTableExists).isTrue();
    }
}