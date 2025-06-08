
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:flutter/foundation.dart';

class CommonData{
  static final String? fontFamily = (ConstantsData.useMa) ? 'MiniTex' : 'PingFang';

  static const txtBottomAdj = kIsWeb ? 0.0 : 0.0;

  static const txtBottomAdj2 = kIsWeb ? 0.0 : 0.0;

  static const txtBottomAdj3 = kIsWeb ? 0.0 : 3.0;

  static String accessToken = 'a';

  static const String ASSETS_IMG = 'assets/images/';
}
