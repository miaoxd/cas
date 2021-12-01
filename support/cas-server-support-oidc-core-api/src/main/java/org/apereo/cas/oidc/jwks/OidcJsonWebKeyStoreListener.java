package org.apereo.cas.oidc.jwks;

import org.apereo.cas.oidc.jwks.generator.OidcJsonWebKeystoreModifiedEvent;
import org.apereo.cas.util.spring.CasEventListener;

/**
 * Interface for {@code OidcJsonWebKeyStoreListenerImpl} to allow spring {@code @Async} support to use JDK proxy.
 * @author Hal Deadman
 * @since 6.5.0
 */
public interface OidcJsonWebKeyStoreListener extends CasEventListener {

    /**
     * Handle oidc json web keystore modified event.
     *
     * @param event the event
     */
    void handleOidcJsonWebKeystoreModifiedEvent(OidcJsonWebKeystoreModifiedEvent event);
}