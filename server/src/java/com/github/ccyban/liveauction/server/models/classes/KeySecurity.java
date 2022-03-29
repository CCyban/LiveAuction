package com.github.ccyban.liveauction.server.models.classes;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
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
            ServerLog.getInstance().log("⚠ Exception caught trying to use the RSA algorithm");
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
            ServerLog.getInstance().log("⚠ Exception caught trying to use the RSA and AES cipher algorithms");
        }
    }

    public Object unsealObject(SealedObject sealedObject) {
        try {
            return sealedObject.getObject(symmetricKey);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ClassNotFoundException e) {
            ServerLog.getInstance().log("⚠ Exception caught trying to unseal object");
            return null;
        }
    }

    public SealedObject sealObject(Object object) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);

            return new SealedObject((Serializable) object, cipher);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            ServerLog.getInstance().log("⚠ Exception caught trying to seal object");
            return null;
        }
    }
}
