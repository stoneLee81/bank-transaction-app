import 'dart:convert';
import 'package:bank_transaction_app/src/common/constants/common_data.dart';
import 'package:bank_transaction_app/src/models/transaction.dart';
import 'package:bank_transaction_app/src/utils/http/dio_util.dart';
import 'package:bank_transaction_app/src/utils/http/service_url.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';

class TransProvider with ChangeNotifier {

  Future<BaseRespList<Transaction>> getTransData(aiKnowledgeItemDtl) async {
    EasyLoading.show();

    BaseRespList<Transaction> resp = await DioUtil().request<Transaction>(
        post,
        '${ServiceUrl.transList + CommonData.accessToken}',
        BaseProcessor(true, fun:Transaction.generateOBJ),
        data: jsonEncode(aiKnowledgeItemDtl.toJson()));

    UITools.processResp(resp, succMsg: '');

    return resp;
  }

  /// 创建新交易
  Future<BaseResp> createTransaction(Transaction transaction) async {
    EasyLoading.show();

    BaseResp resp = await DioUtil().request(
        post,
        '${ServiceUrl.transCreate + CommonData.accessToken}',
        BaseProcessor(false, fun: (data) {
          // 如果后端直接返回Transaction对象，则解析为Transaction
          if (data != null && data is Map<String, dynamic>) {
            return Transaction.fromJson(data);
          }
          return data;
        }),
        data: jsonEncode(transaction.toJson()));

    UITools.processResp(resp, succMsg: '');

    return resp;
  }

  /// 更新交易
  Future<BaseResp> updateTransaction(Transaction transaction) async {
    EasyLoading.show();

    BaseResp resp = await DioUtil().request(
        post,
        '${ServiceUrl.transUpdate + CommonData.accessToken}',
        BaseProcessor(false, fun: (data) {
          // 如果后端直接返回Transaction对象，则解析为Transaction
          if (data != null && data is Map<String, dynamic>) {
            return Transaction.fromJson(data);
          }
          return data;
        }),
        data: jsonEncode(transaction.toJson()));

    UITools.processResp(resp, succMsg: '');

    return resp;
  }

  /// 删除交易
  Future<BaseResp<bool>> deleteTransaction(String transactionId) async {
    EasyLoading.show();

    Map<String, dynamic> deleteData = {
      'id': transactionId,
    };

    BaseResp<bool> resp = await DioUtil().request<bool>(
        post,
        '${ServiceUrl.transDelete + CommonData.accessToken}',
        BaseProcessor(false, fun: (data) => true),
        data: jsonEncode(deleteData));

    UITools.processResp(resp, succMsg: '');

    return resp;
  }
}