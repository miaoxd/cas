package org.apereo.cas.ticket.registry;

import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.DefaultTicketCatalog;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.apereo.cas.ticket.expiration.HardTimeoutExpirationPolicy;
import org.apereo.cas.ticket.registry.pubsub.queue.QueueableTicketRegistryMessagePublisher;
import org.apereo.cas.ticket.serialization.TicketSerializationManager;
import org.apereo.cas.util.PublisherIdentifier;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.util.spring.DirectObjectProvider;

import lombok.val;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test case to test the {@code CachingTicketRegistry} based on test cases to test all
 * Ticket Registries.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Tickets")
class CachingTicketRegistryTests extends BaseTicketRegistryTests {

    @Override
    public TicketRegistry getNewTicketRegistry() {
        return getTicketRegistryInstance();
    }

    @RepeatedTest(1)
    public void verifyOtherConstructor() {
        assertDoesNotThrow(CachingTicketRegistryTests::getTicketRegistryInstance);
    }

    private static TicketRegistry getTicketRegistryInstance() {
        return new CachingTicketRegistry(CipherExecutor.noOp(),
            mock(TicketSerializationManager.class), new DefaultTicketCatalog(),
            new DirectObjectProvider<>(mock(LogoutManager.class)),
            QueueableTicketRegistryMessagePublisher.noOp(), new PublisherIdentifier());
    }

    @RepeatedTest(1)
    public void verifyExpirationByTimeout() throws Exception {
        val registry = getTicketRegistryInstance();
        val ticket = new TicketGrantingTicketImpl(TicketGrantingTicket.PREFIX + "-12346", RegisteredServiceTestUtils.getAuthentication(),
            new HardTimeoutExpirationPolicy(1));
        registry.addTicket(ticket);
        Thread.sleep(3000);
        assertNull(registry.getTicket(ticket.getId()));
    }

    @RepeatedTest(1)
    public void verifyExpirationExplicit() throws Exception {
        val registry = getTicketRegistryInstance();
        val ticket = new MockTicketGrantingTicket("casuser");
        registry.addTicket(ticket);
        Thread.sleep(1000);
        ticket.markTicketExpired();
        assertNull(registry.getTicket(ticket.getId()));
    }
}
