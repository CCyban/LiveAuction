package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountRepositoryTest {
    AccountRepository accountRepository;
    String newUsername;
    String newPassword;
    Account newAccount;
    UUID accountUUID;

    @BeforeEach
    void setUp() {
        accountUUID = UUID.randomUUID();
        accountRepository = Mockito.spy(AccountRepository.class);

        newUsername = "someUsername";
        newPassword = "somePassword";

        newAccount = new Account(newUsername, newPassword);
        accountUUID = newAccount.getAccountUUID();
        accountRepository.accounts.add(newAccount);
    }

    @Test
    void getAccountUUID() {
        UUID fetchedUUID = accountRepository.getAccountUUID(newAccount);
        assertEquals(accountUUID, fetchedUUID);
    }

    @Test
    void getInvalidAccountUUID() {
        UUID fetchedUUID = accountRepository.getAccountUUID(new Account("someOtherUsername", "someOtherPassword"));
        assertEquals(null, fetchedUUID);
    }
}