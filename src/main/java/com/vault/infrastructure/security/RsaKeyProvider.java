package com.vault.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyProvider {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyProvider.class);
    private static final Path PRIVATE_KEY_PATH = Path.of("keys", "private.pem");
    private static final Path PUBLIC_KEY_PATH = Path.of("keys", "public.pem");
    private static final int KEY_SIZE = 2048;

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RsaKeyProvider() {
        try {
            var keyPair = loadOrGenerateKeyPair();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to initialize RSA keys", exception);
        }
    }

    public RSAPublicKey publicKey() {
        return publicKey;
    }

    public RSAPrivateKey privateKey() {
        return privateKey;
    }

    private KeyPair loadOrGenerateKeyPair() throws Exception {
        if (Files.exists(PRIVATE_KEY_PATH) && Files.exists(PUBLIC_KEY_PATH)) {
            return loadKeyPair();
        }
        return generateAndPersistKeyPair();
    }

    private KeyPair loadKeyPair() throws Exception {
        log.info("Loading RSA key pair from disk");
        var factory = KeyFactory.getInstance("RSA");
        var privateBytes = Base64.getDecoder().decode(readKeyContent(PRIVATE_KEY_PATH));
        var publicBytes = Base64.getDecoder().decode(readKeyContent(PUBLIC_KEY_PATH));
        var privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        var publicKey = factory.generatePublic(new X509EncodedKeySpec(publicBytes));
        return new KeyPair(publicKey, privateKey);
    }

    private KeyPair generateAndPersistKeyPair() throws NoSuchAlgorithmException, IOException {
        log.info("Generating new RSA-{} key pair", KEY_SIZE);
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        var keyPair = generator.generateKeyPair();
        persistKeyPair(keyPair);
        return keyPair;
    }

    private void persistKeyPair(KeyPair keyPair) throws IOException {
        Files.createDirectories(PRIVATE_KEY_PATH.getParent());
        var encoder = Base64.getEncoder();
        Files.writeString(PRIVATE_KEY_PATH, encoder.encodeToString(keyPair.getPrivate().getEncoded()));
        Files.writeString(PUBLIC_KEY_PATH, encoder.encodeToString(keyPair.getPublic().getEncoded()));
    }

    private String readKeyContent(Path path) throws IOException {
        return Files.readString(path).trim();
    }
}
