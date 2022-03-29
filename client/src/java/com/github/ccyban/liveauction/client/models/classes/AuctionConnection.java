package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.client.models.enumerations.Page;
import com.github.ccyban.liveauction.shared.models.classes.*;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.security.*;
import java.util.*;

public class AuctionConnection {
    private static Timer tickTimer;

    public void setTimerTask(TimerTask timerTask) {
        tickTimer = new Timer();
        tickTimer.schedule(timerTask, 0, 1000);
    }

    public void cancelTimerTask() {
        if (tickTimer != null) {
            tickTimer.cancel();
        }
    }

    // This area is generally used to fetch current auction data from socket & feed it into the table each tick

    // -- Cut off area --

    private static AuctionConnection uniqueConnection;
    public Boolean connectionEstablished;

    private SecretKeySpec symmetricKey;
    public Boolean hasSentSecret = false;

    public Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Thread clientSubscription;

    private AuctionConnection () {
        // Starting connection
        try {
            clientSocket = new Socket("localhost", 9090);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            connectionEstablished = true;
        }
        catch (IOException e) {
            connectionEstablished = false;
        }
    }

    public static AuctionConnection getAuctionConnection() {
        if (uniqueConnection == null || !uniqueConnection.connectionEstablished) {
            uniqueConnection = new AuctionConnection();
        }
        return uniqueConnection;
    }

    public void requestSocketData(ClientSubscriptionHandler clientSubscriptionHandler) {
        clientSubscription = new Thread(new Runnable() {
            public void run() {
                clientSubscriptionHandler.sendSocketRequest(out, in);
            }
        });
        clientSubscription.start();
    }

    public void closeAllActiveSubscriptions() {
        sendSealedSocketRequest(new SocketRequest(SocketRequestType.CloseAllSubscriptions, null, null));
    }

    public void postBid(UUID auctionToBidOn, Bid newBid) {
        try {
            out.writeObject(encryptSocketRequest(new SocketRequest(SocketRequestType.PostAuctionBid, auctionToBidOn, newBid)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exchangeSecureKeys() {
        try {
            out.writeObject(new SocketRequest(SocketRequestType.GetPublicKey, null, null));
            SocketResponse publicKeyResponse = (SocketResponse) in.readObject();
            PublicKey publicKey = (PublicKey) publicKeyResponse.responsePayload;

            // Now we have the asymmetric public key, we can make a symmetric secret key and encrypt it to share with the server

            byte[] secureRandomKeyBytes  = new byte[16];
            SecureRandom secureRandom = new SecureRandom();

            secureRandom.nextBytes(secureRandomKeyBytes);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secureRandomKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            symmetricKey = secretKeySpec;

            byte[] encryptedSecretKey = cipher.doFinal(symmetricKey.getEncoded());

            out.writeObject(new SocketRequest(SocketRequestType.PostSymmetricKey, null, encryptedSecretKey));
            hasSentSecret = true;

            // Now both client and server have a symmetric secret key to communicate securely with

        } catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public SealedObject encryptSocketRequest(SocketRequest socketRequest) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);

            return new SealedObject(socketRequest, cipher);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SocketResponse decryptSocketResponse(SealedObject sealedSocketResponse) {
        try {
            return (SocketResponse) sealedSocketResponse.getObject(symmetricKey);

        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean signIn(Account signInAttempt) {
        try {
            sendSealedSocketRequest(new SocketRequest(SocketRequestType.SignIn, null, signInAttempt));

            SealedObject sealedResponse = (SealedObject) in.readObject();
            SocketResponse signInAttemptResponse = decryptSocketResponse(sealedResponse);

            UUID accountAttemptUUID = (UUID) signInAttemptResponse.responsePayload;
            if (accountAttemptUUID != null) {
                AccountSession.getAccountSession().accountSessionUUID = accountAttemptUUID;
                return true;
            }
            else {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void followAuction(UUID auctionUUID) {
        sendSealedSocketRequest(new SocketRequest(SocketRequestType.ToggleAuctionFollowByIds, auctionUUID, AccountSession.getAccountSession().accountSessionUUID));
    }

    public void sendSealedSocketRequest(SocketRequest socketRequest) {
        try {
            out.writeObject(
                encryptSocketRequest(socketRequest)
            );

        } catch (IOException e) {
            lostConnection();
        }
    }

    public void lostConnection() {
        new Alert(Alert.AlertType.ERROR, "Lost Server Connection. You are back at the initial page.").show();
        connectionEstablished = false;
        hasSentSecret = false;
        PageManager.loadPage(Page.Keys);
    }
}
