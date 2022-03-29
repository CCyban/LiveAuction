package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class AccountRepository {
    public ArrayList<Account> accounts = new ArrayList<>();

    public AccountRepository() {
        // Mocking is used instead of having a real database call here
    }

    public UUID getAccountUUID(Account signInAttempt) {
        Account accountAttempt = accounts.stream().filter((
                account ->
                        account.getUsername().equals(signInAttempt.getUsername()) &&
                                Arrays.equals(account.getHashedPassword(),signInAttempt.getHashedPassword())))
                .findFirst().orElse(null);

        return (accountAttempt == null) ? null : accountAttempt.getAccountUUID();
    }
}
