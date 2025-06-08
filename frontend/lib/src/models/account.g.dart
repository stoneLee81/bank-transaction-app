// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'account.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Account _$AccountFromJson(Map<String, dynamic> json) => Account(
      accountId: json['accountId'] as String?,
      accountNumber: json['accountNumber'] as String?,
      accountName: json['accountName'] as String?,
      bank: json['bank'] == null
          ? null
          : Bank.fromJson(json['bank'] as Map<String, dynamic>),
      balance: (json['balance'] as num?)?.toDouble(),
      currency: $enumDecodeNullable(_$CurrencyEnumMap, json['currency']),
      status: $enumDecodeNullable(_$AccountStatusEnumMap, json['status']),
    );

Map<String, dynamic> _$AccountToJson(Account instance) => <String, dynamic>{
      'accountId': instance.accountId,
      'accountNumber': instance.accountNumber,
      'accountName': instance.accountName,
      'bank': instance.bank,
      'balance': instance.balance,
      'currency': _$CurrencyEnumMap[instance.currency],
      'status': _$AccountStatusEnumMap[instance.status],
    };

const _$CurrencyEnumMap = {
  Currency.CNY: 'CNY',
  Currency.USD: 'USD',
  Currency.EUR: 'EUR',
  Currency.JPY: 'JPY',
  Currency.HKD: 'HKD',
  Currency.GBP: 'GBP',
};

const _$AccountStatusEnumMap = {
  AccountStatus.ACTIVE: 'ACTIVE',
  AccountStatus.INACTIVE: 'INACTIVE',
  AccountStatus.SUSPENDED: 'SUSPENDED',
  AccountStatus.CLOSED: 'CLOSED',
};
