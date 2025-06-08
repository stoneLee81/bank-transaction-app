// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'transaction.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Transaction _$TransactionFromJson(Map<String, dynamic> json) => Transaction(
      id: json['id'] as String?,
      amount: (json['amount'] as num?)?.toDouble(),
      type: $enumDecodeNullable(_$TransactionTypeEnumMap, json['type']),
      timestamp: Transaction._timestampFromJson(json['timestamp']),
      status: $enumDecodeNullable(_$TransactionStatusEnumMap, json['status']),
      currency: $enumDecodeNullable(_$CurrencyEnumMap, json['currency']),
      channel: json['channel'] as String?,
      referenceNumber: json['referenceNumber'] as String?,
      direction: json['direction'] as String?,
      remark: json['remark'] as String?,
      fromAccountId: json['fromAccountId'] as String?,
      toAccountId: json['toAccountId'] as String?,
      fromAccount: json['fromAccount'] == null
          ? null
          : Account.fromJson(json['fromAccount'] as Map<String, dynamic>),
      toAccount: json['toAccount'] == null
          ? null
          : Account.fromJson(json['toAccount'] as Map<String, dynamic>),
      idempotencyKey: json['idempotencyKey'] as String?,
      initiatedBy: json['initiatedBy'] as String?,
      approvedBy: json['approvedBy'] as String?,
    );

Map<String, dynamic> _$TransactionToJson(Transaction instance) =>
    <String, dynamic>{
      'id': instance.id,
      'amount': instance.amount,
      'type': _$TransactionTypeEnumMap[instance.type],
      'timestamp': Transaction._timestampToJson(instance.timestamp),
      'status': _$TransactionStatusEnumMap[instance.status],
      'currency': _$CurrencyEnumMap[instance.currency],
      'channel': instance.channel,
      'referenceNumber': instance.referenceNumber,
      'direction': instance.direction,
      'remark': instance.remark,
      'fromAccountId': instance.fromAccountId,
      'toAccountId': instance.toAccountId,
      'fromAccount': instance.fromAccount,
      'toAccount': instance.toAccount,
      'idempotencyKey': instance.idempotencyKey,
      'initiatedBy': instance.initiatedBy,
      'approvedBy': instance.approvedBy,
    };

const _$TransactionTypeEnumMap = {
  TransactionType.DEPOSIT: 'DEPOSIT',
  TransactionType.WITHDRAWAL: 'WITHDRAWAL',
};

const _$TransactionStatusEnumMap = {
  TransactionStatus.PENDING: 'PENDING',
  TransactionStatus.COMPLETED: 'COMPLETED',
  TransactionStatus.FAILED: 'FAILED',
  TransactionStatus.CANCELLED: 'CANCELLED',
};

const _$CurrencyEnumMap = {
  Currency.CNY: 'CNY',
  Currency.USD: 'USD',
  Currency.EUR: 'EUR',
  Currency.JPY: 'JPY',
  Currency.HKD: 'HKD',
  Currency.GBP: 'GBP',
};
