package com.tarento.commenthub;

import com.tarento.commenthub.authentication.util.AccessTokenValidator;
import com.tarento.commenthub.authentication.util.KeyManager;
import com.tarento.commenthub.transactional.cassandrautils.CassandraPropertyReader;
import com.tarento.commenthub.transactional.cassandrautils.CassandraUtil;
import com.tarento.commenthub.transactional.utils.PropertiesCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Verifies the real Spring container (not Mockito) can build and wire the beans that were
 * converted from manual singletons to constructor-injected @Component beans.
 */
@SpringBootTest(classes = {
        PropertiesCache.class,
        CassandraPropertyReader.class,
        CassandraUtil.class,
        KeyManager.class,
        AccessTokenValidator.class
})
class SingletonRefactorWiringTest {

    @Autowired
    private PropertiesCache propertiesCache;

    @Autowired
    private CassandraPropertyReader cassandraPropertyReader;

    @Autowired
    private CassandraUtil cassandraUtil;

    @Autowired
    private KeyManager keyManager;

    @Autowired
    private AccessTokenValidator accessTokenValidator;

    @Autowired
    private PropertiesCache propertiesCacheAgain;

    @Test
    void contextLoadsAndWiresAllConvertedSingletons() {
        assertNotNull(propertiesCache);
        assertNotNull(cassandraPropertyReader);
        assertNotNull(cassandraUtil);
        assertNotNull(keyManager);
        assertNotNull(accessTokenValidator);
    }

    @Test
    void beansRemainSingletonScopedAcrossInjectionPoints() {
        // Default Spring bean scope is singleton, so every injection point should see the same instance.
        assertSame(propertiesCache, propertiesCacheAgain);
    }
}
