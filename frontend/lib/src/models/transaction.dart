import 'package:json_annotation/json_annotation.dart';
import 'account.dart';
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
part 'transaction.g.dart';

@JsonSerializable()
class Transaction {
  Transaction({this.id,this.amount,this.type,this.timestamp,this.status,this.currency,this.channel,this.referenceNumber,this.direction,this.remark,this.fromAccountId,this.toAccountId,this.fromAccount,this.toAccount,this.idempotencyKey,this.initiatedBy,this.approvedBy});

  String? id;
  double? amount;
  TransactionType? type;
  @JsonKey(fromJson: _timestampFromJson, toJson: _timestampToJson)
  DateTime? timestamp;
  TransactionStatus? status;
  Currency? currency;
  String? channel;
  String? referenceNumber;
  String? direction;
  String? remark;
  String? fromAccountId;
  String? toAccountId;
  Account? fromAccount;
  Account? toAccount;
  String? idempotencyKey;
  String? initiatedBy;
  String? approvedBy;

  static DateTime? _timestampFromJson(dynamic json) {
    if (json == null) return null;
    
    if (json is String) {
      return DateTime.parse(json);
    } else if (json is List && json.length >= 6) {
      // 处理后端返回的数组格式: [year, month, day, hour, minute, second, nanosecond]
      return DateTime(
        json[0] as int, // year
        json[1] as int, // month
        json[2] as int, // day
        json[3] as int, // hour
        json[4] as int, // minute
        json[5] as int, // second
        json.length > 6 ? (json[6] as int) ~/ 1000000 : 0, // millisecond from nanosecond
      );
    }
    
    return null;
  }

  static String? _timestampToJson(DateTime? dateTime) {
    return dateTime?.toIso8601String();
  }

  static T generateOBJ<T>(json) {
    return Transaction.fromJson(json) as T;
  }

  factory Transaction.fromJson(Map<String, dynamic> json) => _$TransactionFromJson(json);
  Map<String, dynamic> toJson() => _$TransactionToJson(this);
} 