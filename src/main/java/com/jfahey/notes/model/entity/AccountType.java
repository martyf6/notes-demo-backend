package com.jfahey.notes.model.entity;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum AccountType {

    LOCAL (1),
    EXTERNAL (2);

    private final int accountId;

    private AccountType(int accountId) {
        this.accountId = accountId;
    }

    public static AccountType of(int id) {
        return Stream.of(AccountType.values())
                .filter(p -> p.getAccountId() == id)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
