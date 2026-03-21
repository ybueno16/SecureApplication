package com.vault.infrastructure.security;

import com.vault.domain.model.credential.EncryptedField;
import com.vault.domain.model.user.KdfSalt;
import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.service.CredentialEncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Component
public class CryptoService implements CredentialEncryptionService {

    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int AES_KEY_LENGTH = 256;

    private final int pbkdf2Iterations;
    private final SecureRandom secureRandom;

    public CryptoService(@Value("${app.crypto.pbkdf2-iterations:600000}") int pbkdf2Iterations) {
        this.pbkdf2Iterations = pbkdf2Iterations;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public EncryptedField encrypt(byte[] plaintext, byte[] derivedKey) {
        try {
            var iv = generateIv();
            var cipher = createCipher(Cipher.ENCRYPT_MODE, derivedKey, iv);
            var ciphertext = cipher.doFinal(plaintext);
            return new EncryptedField(ciphertext, iv);
        } catch (Exception exception) {
            throw new IllegalStateException("Encryption failed", exception);
        }
    }

    @Override
    public byte[] decrypt(EncryptedField encrypted, byte[] derivedKey) {
        try {
            var cipher = createCipher(Cipher.DECRYPT_MODE, derivedKey, encrypted.toInitializationVector());
            return cipher.doFinal(encrypted.toCiphertext());
        } catch (Exception exception) {
            throw new IllegalStateException("Decryption failed", exception);
        }
    }

    @Override
    public byte[] deriveKey(MasterPassword masterPassword, KdfSalt salt) {
        try {
            var spec = new PBEKeySpec(masterPassword.toCharArray(), salt.toBytes(), pbkdf2Iterations, AES_KEY_LENGTH);
            var factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception exception) {
            throw new IllegalStateException("Key derivation failed", exception);
        }
    }

    private byte[] generateIv() {
        var iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }

    private Cipher createCipher(int mode, byte[] key, byte[] iv) throws Exception {
        var secretKey = new SecretKeySpec(key, "AES");
        var gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        var cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
        cipher.init(mode, secretKey, gcmSpec);
        return cipher;
    }
}
