package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.collections.ObservableList;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.*;

public class AuctionConnection {
    private static Timer tickTimer;

    public static void setTimerTask(TimerTask timerTask) {
        tickTimer = new Timer();
        tickTimer.schedule(timerTask, 0, 1000);
    }

    public static void cancelTimerTask() {
        tickTimer.cancel();
    }

    // This area is generally used to fetch current auction data from socket & feed it into the table each tick

    // -- Cut off area --

    private static AuctionConnection uniqueConnection;

    private static SecretKeySpec symmetricKey;

    public static Socket clientSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    private static Thread clientSubscription;

    private AuctionConnection () {
        // Starting connection
        // TODO: Add/use authentication variables here
        try {
            clientSocket = new Socket("localhost", 9090);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static AuctionConnection getAuctionConnection() {
        if (uniqueConnection == null) {
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
        try {
            out.writeObject(
                    encryptSocketRequest(
                            new SocketRequest(SocketRequestType.CloseAllSubscriptions, null, null)
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            System.out.println("GOT PUBLIC KEY ✔");

            byte[] secureRandomKeyBytes  = new byte[16];
            SecureRandom secureRandom = new SecureRandom();

            secureRandom.nextBytes(secureRandomKeyBytes);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secureRandomKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            symmetricKey = secretKeySpec;

            byte[] encryptedSecretKey = cipher.doFinal(symmetricKey.getEncoded());

            out.writeObject(new SocketRequest(SocketRequestType.PostSymmetricKey, null, encryptedSecretKey));
            System.out.println("SENT SECRET KEY ✔");

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
}
