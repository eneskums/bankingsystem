package com.banking.bankingsystem.transaction.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Getter
@RequiredArgsConstructor
public enum TransactionType {

	DEPOSIT("yatırma"), WITHDRAW("çekme");

	private final String value;

	public static TransactionType fromValue(String text) {
		for (TransactionType transactionType : TransactionType.values()) {
			if (transactionType.getValue().equalsIgnoreCase(text)) {
				return transactionType;
			}
		}
		return null;
	}
}
