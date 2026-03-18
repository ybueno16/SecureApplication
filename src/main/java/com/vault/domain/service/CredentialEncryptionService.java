package com.vault.domain.service;

import com.vault.domain.model.credential.EncryptedField;
import com.vault.domain.model.user.KdfSalt;
import com.vault.domain.model.user.MasterPassword;

public interface CredentialEncryptionService {

    EncryptedField encrypt(byte[] plaintext, byte[] derivedKey);

    byte[] decrypt(EncryptedField encrypted, byte[] derivedKey);

    byte[] deriveKey(MasterPassword masterPassword, KdfSalt salt);
}
