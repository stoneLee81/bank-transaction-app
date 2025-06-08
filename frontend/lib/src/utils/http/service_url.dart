import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
class ServiceUrl {

  // 请求成功
  static const API_CODE_SUCCESS = "200";

  static const String transCreate = ConstantsData.baseUrl + 'transactions/create?access_token=';

  static const String transUpdate = ConstantsData.baseUrl + 'transactions/update?access_token=';

  static const String transDelete = ConstantsData.baseUrl + 'transactions/delete?access_token=';

  static const String transList = ConstantsData.baseUrl + 'transactions?access_token='; 
}
