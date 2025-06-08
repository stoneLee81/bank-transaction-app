import 'package:json_annotation/json_annotation.dart';
part 'bank.g.dart';

@JsonSerializable()
class Bank {
  Bank({this.bankId,this.bankName});
  String? bankId;
  String? bankName;

  static T generateOBJ<T>(json) {
    return Bank.fromJson(json) as T;
  }

  factory Bank.fromJson(Map<String, dynamic> json) => _$BankFromJson(json);
  Map<String, dynamic> toJson() => _$BankToJson(this);
} 