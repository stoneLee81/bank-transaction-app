import 'package:json_annotation/json_annotation.dart';
import 'bank.dart';
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
part 'account.g.dart';

@JsonSerializable()
class Account {
  Account({this.accountId,this.accountNumber,this.accountName,this.bank,this.balance,this.currency,this.status});

  String? accountId;
  String? accountNumber;
  String? accountName;
  Bank? bank;
  double? balance;
  Currency? currency;
  AccountStatus? status;

  static T generateOBJ<T>(json) {
    return Account.fromJson(json) as T;
  }

  factory Account.fromJson(Map<String, dynamic> json) => _$AccountFromJson(json);
  Map<String, dynamic> toJson() => _$AccountToJson(this);
} 