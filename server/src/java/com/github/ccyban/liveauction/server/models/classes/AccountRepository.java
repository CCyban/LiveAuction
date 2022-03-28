package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Account;
import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.*;
import java.util.ArrayList;
import java.util.UUID;

public class AccountRepository {
    public ArrayList<Account> accounts;

    public AccountRepository() {
        // Mocking is used instead of having a real database call here
    }
}
