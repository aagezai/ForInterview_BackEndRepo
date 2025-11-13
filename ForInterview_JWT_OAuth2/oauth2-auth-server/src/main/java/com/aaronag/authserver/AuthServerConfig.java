package com.aaronag.authserver;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthServerConfig {

    /* === Password encoder (delegating) === */
    @Bean
    PasswordEncoder passwordEncoder() {
        // supports {bcrypt}, {noop}, {pbkdf2}, etc.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /* === Auth Server security chain must run first === */
    @Bean
    @Order(1)
    SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }

    /* === App/default chain for everything else === */
    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    /* === Clients repository === */
    @Bean
    RegisteredClientRepository registeredClientRepository(PasswordEncoder enc) {
        RegisteredClient client = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("aaronag-client")
                // encode the client secret; use this same plain value in tools, the server compares with enc
                .clientSecret(enc.encode("secret"))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/aaronag-client")
                .redirectUri("http://127.0.0.1:8080/authorized")
                .scope(OidcScopes.OPENID)
                .scope("api.read")
                .build();

        return new InMemoryRegisteredClientRepository(client);
    }

    /* === JWK source (RSA demo key) === */
    @Bean
    JWKSource<SecurityContext> jwkSource() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();

        RSAKey rsa = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("aaronag-rsa")
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsa));
    }

    /* === Authorization server settings (set issuer explicitly) === */
    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }

    /* === Demo user for the login/consent screen === */
    @Bean
    UserDetailsService users(PasswordEncoder enc) {
        var user = User.withUsername("user")
                .password(enc.encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
    http://127.0.0.1:8080/authorized?code=QwKbkx7FH0MAN6c0dpLMJL6VDSLDjOK3sgHDdsHh7yngDNLey5l6UraKgby8pIg1SABy0rRHLZvSA7LKozxwX7P4nFFECtqNcld8aKO3gS1y2QlRpHumJkUgY2pkNNRv
    //http://localhost:9000/oauth2/authorize?response_type=code&client_id=aaronag-client&scope=openid%20api.read&redirect_uri=http://127.0.0.1:8080/authorized
    //CjVNe_SwmViMsgufcdP2lkqULNslsYFhNmb9qOnt0-fNSF4xUN_KG7ychyGvrzELOtBnQc0Y86r-CfnYDbD_3tORW3rKhMPv6fG-3xyyliRh6Q6PY-ENz7WEr0puqDxu" you get this jwt OAuth
}
