package org.apereo.cas.syncope.authentication;

import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.syncope.BaseSyncopeTests;
import org.apereo.cas.util.junit.EnabledIfListeningOnPort;
import org.apereo.cas.util.spring.beans.BeanContainer;

import lombok.val;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.apereo.services.persondir.IPersonAttributeDaoFilter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link SyncopePersonAttributeDaoTests}.
 *
 * @author Francesco Chicchiriccò
 * @since 6.5.0
 */
@Tag("Syncope")
class SyncopePersonAttributeDaoTests {

    @SpringBootTest(classes = BaseSyncopeTests.SharedTestConfiguration.class,
        properties = {
            "cas.authn.attribute-repository.syncope.url=http://localhost:18080/syncope",
            "cas.authn.attribute-repository.syncope.basic-auth-username=admin",
            "cas.authn.attribute-repository.syncope.basic-auth-password=password",
            "cas.authn.attribute-repository.syncope.search-filter=username=={user}",
            "cas.authn.attribute-repository.syncope.attribute-mappings.username=userId",
            "cas.authn.attribute-repository.syncope.attribute-mappings.syncopeUserAttr_email=email",
            "cas.authn.attribute-repository.syncope.attribute-mappings.syncopeUserAttr_description=email_description"
        })
    @Nested
    @EnabledIfListeningOnPort(port = 18080)
    @SuppressWarnings("ClassCanBeStatic")
    class SyncopeCoreServerTests extends BaseSyncopeTests {
        @Autowired
        @Qualifier(PrincipalResolver.BEAN_NAME_ATTRIBUTE_REPOSITORY)
        private IPersonAttributeDao attributeRepository;

        @Test
        void verifyUserIsFound() {
            var found = attributeRepository.getPeople(Map.of("username", List.of("syncopecas")));
            assertFalse(found.iterator().next().getAttributes().isEmpty());
            var people = attributeRepository.getPeople(Map.of("username", List.of("syncopecas")),
                IPersonAttributeDaoFilter.alwaysChoose());
            assertFalse(people.iterator().next().getAttributes().isEmpty());
        }

        @Test
        void verifyUserAttributeMappings() {
            val found = attributeRepository.getPeople(Map.of("username", List.of("syncopecas")));
            val attributes = found.iterator().next().getAttributes();
            assertFalse(attributes.isEmpty());
            assertNotNull(attributes.get("userId"));
            assertNotNull(attributes.get("email"));
            assertNull(attributes.get("syncopeUserAttr_email"));
            assertNotNull(attributes.get("email_description"));
            assertNull(attributes.get("syncopeUserAttr_description"));

        }

    }

    @SpringBootTest(classes = BaseSyncopeTests.SharedTestConfiguration.class,
        properties = {
            "cas.authn.attribute-repository.syncope.url=http://localhost:8095/",
            "cas.authn.attribute-repository.syncope.search-filter=username=={user}"
        })
    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    class MockSyncopePersonTests extends BaseSyncopeTests {
        @Autowired
        @Qualifier("syncopePersonAttributeDaos")
        private BeanContainer<IPersonAttributeDao> syncopePersonAttributeDaos;

        @Autowired
        @Qualifier(PrincipalResolver.BEAN_NAME_ATTRIBUTE_REPOSITORY)
        private IPersonAttributeDao attributeRepository;

        @Test
        void verifyUserIsFound() {
            val result = MAPPER.createObjectNode();
            result.putArray("result").add(user());
            try (val webserver = startMockSever(result, HttpStatus.OK, 8095)) {
                assertFalse(syncopePersonAttributeDaos.toList().isEmpty());
                assertFalse(attributeRepository.getPeople(Map.of("username", List.of("casuser"))).isEmpty());

                val first = syncopePersonAttributeDaos.first();
                val people = first.getPeople(Map.of("username", List.of("casuser")),
                    IPersonAttributeDaoFilter.alwaysChoose());
                assertFalse(people.iterator().next().getAttributes().isEmpty());
            }
        }

        @Test
        void verifyUserIsNotFound() {
            val result = MAPPER.createObjectNode();
            result.putArray("result");
            try (val webserver = startMockSever(result, HttpStatus.OK, 8095)) {
                val people = attributeRepository.getPeopleWithMultivaluedAttributes(
                    Map.of("anotherProp", List.of("casuser")), IPersonAttributeDaoFilter.alwaysChoose());
                assertTrue(people.isEmpty());
            }
        }

        @Test
        void verifySyncopeDown() {
            val result = MAPPER.createObjectNode();
            result.putArray("result").add(user());
            try (val webserver = startMockSever(result, HttpStatus.INTERNAL_SERVER_ERROR, 8095)) {
                val first = syncopePersonAttributeDaos.first();
                val results = first.getPeople(Map.of("username", List.of("casuser")), IPersonAttributeDaoFilter.alwaysChoose());
                assertTrue(results.iterator().next().getAttributes().isEmpty());
            }
        }
    }
}
