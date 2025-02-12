package com.jfahey.notes.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AccountTypeConverter implements AttributeConverter<AccountType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return accountType.getAccountId();
    }

    @Override
    public AccountType convertToEntityAttribute(Integer accountId) {
        if (accountId == null) {
            return null;
        }

        return AccountType.of(accountId);
    }
}
