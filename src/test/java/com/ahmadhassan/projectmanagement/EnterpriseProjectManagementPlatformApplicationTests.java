package com.ahmadhassan.projectmanagement;

import com.ahmadhassan.projectmanagement.identity.EmailAddressRepository;
import com.ahmadhassan.projectmanagement.identity.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
                "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration," +
                "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
class EnterpriseProjectManagementPlatformApplicationTests {

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    EmailAddressRepository emailAddressRepository;

    @Test
    void contextLoads() {
    }
}