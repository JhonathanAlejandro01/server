package com.NetMasters.NetMasters.infrastructure.persistence.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(AesEncryptionService.class);
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12; // 96 bits
    private static final int TAG_LENGTH_BIT = 128;

    private final SecretKeySpec keySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesEncryptionService(@Value("${move.encryption.key:}") String base64Key) {
        byte[] keyBytes;
        if (base64Key == null || base64Key.isBlank()) {
            // Fallback: use a derived key from a constant (not recommended for production)
            logger.warn("MOVE_ENC_KEY not provided; using an insecure fallback key. Set 'move.encryption.key' env var to secure this.");
            String fallback = "defaultMoveEncryptionKey!"; // 24+ bytes
            keyBytes = new byte[16];
            System.arraycopy(fallback.getBytes(), 0, keyBytes, 0, Math.min(fallback.getBytes().length, 16));
        } else {
            keyBytes = Base64.getDecoder().decode(base64Key);
            if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
                throw new IllegalArgumentException("move.encryption.key must be base64 of 16/24/32 bytes");
            }
        }
        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_SIZE];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGO);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes());
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            logger.error("Encryption failed", ex);
            throw new RuntimeException(ex);
        }
    }

    public String decrypt(String base64) {
        try {
            byte[] combined = Base64.getDecoder().decode(base64);
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);
            int cipherLen = combined.length - IV_SIZE;
            byte[] cipherText = new byte[cipherLen];
            System.arraycopy(combined, IV_SIZE, cipherText, 0, cipherLen);
            Cipher cipher = Cipher.getInstance(ALGO);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain);
        } catch (Exception ex) {
            logger.error("Decryption failed", ex);
            throw new RuntimeException(ex);
        }
    }
}
