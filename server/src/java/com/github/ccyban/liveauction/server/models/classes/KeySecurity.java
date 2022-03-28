package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Account;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

public class KeySecurity {
    private PrivateKey serverPrivateKey;
    private PublicKey serverPublicKey;
    private SecretKeySpec symmetricKey;
    public Boolean hasSecret = false;

    public KeySecurity() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);

            KeyPair pair = keyGen.generateKeyPair();
            serverPrivateKey = pair.getPrivate();
            serverPublicKey = pair.getPublic();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public void setEncryptedSecretKey(byte[] encryptedSecretKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey);

            symmetricKey = new SecretKeySpec(cipher.doFinal(encryptedSecretKey), "AES");
            hasSecret = true;

        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Object desealObject(SealedObject sealedObject) {
        try {
            return sealedObject.getObject(symmetricKey);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SealedObject sealObject(Object object) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);

            return new SealedObject((Serializable) object, cipher);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
