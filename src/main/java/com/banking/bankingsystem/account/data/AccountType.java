package com.banking.bankingsystem.account.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Getter
@RequiredArgsConstructor
public enum AccountType {

	TL("tl"), USD("dolar"), GBP("sterlin");

	private final String value;

	public static AccountType fromValue(String text) {
		for (AccountType accountType : AccountType.values()) {
			if (accountType.getValue().equalsIgnoreCase(text)) {
				return accountType;
			}
		}
		return null;
	}
}
