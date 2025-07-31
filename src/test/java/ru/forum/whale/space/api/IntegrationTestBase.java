package ru.forum.whale.space.api;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Transactional
@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public class IntegrationTestBase {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17.5-bookworm");

    @Container
    private static final RedisContainer redis =
            new RedisContainer(DockerImageName.parse("redis:8.0.1-bookworm"));

    @Container
    private static final MinIOContainer minio =
            new MinIOContainer(DockerImageName.parse("minio/minio:RELEASE.2025-05-24T17-08-30Z"));

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.username", postgres::getUsername);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getRedisPort);

        registry.add("minio.url", minio::getS3URL);
        registry.add("minio.access-key", minio::getUserName);
        registry.add("minio.secret-key", minio::getPassword);
    }
}
