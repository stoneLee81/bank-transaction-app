import 'package:bank_transaction_app/src/models/transaction.dart';
import 'package:flutter/material.dart';

class TransValueProvider with ChangeNotifier {

  List<Transaction>? _transList;

  List<Transaction>? get transList => _transList;
  
  changeTransList(List<Transaction> value, {bool refresh = true}) {
    this._transList = value;
    if(refresh) notifyListeners();
  }
}