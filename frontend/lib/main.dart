import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/provider/trans_provider.dart';
import 'package:bank_transaction_app/src/provider/trans_value_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:provider/provider.dart';
import 'package:bank_transaction_app/src/common/config/configure_nonweb.dart' if (dart.library.html) 'package:bank_transaction_app/src/common/config/configure_web.dart';
import 'main_io.dart' if (dart.library.html) "main_web.dart"; 

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  runPlatformInit();
  configureApp();

  // 3. 使用 MultiProvider 包装应用
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: TransValueProvider()),
        ChangeNotifierProvider.value(value: TransProvider()),
      ],
      child: MyApp(),
    ),
  );

  configLoading();
}

void configLoading() {
  EasyLoading.instance
    ..displayDuration = const Duration(milliseconds: 2000)
    ..indicatorType = EasyLoadingIndicatorType.wave
    ..loadingStyle = EasyLoadingStyle.custom
    ..indicatorSize = 36.0
    ..radius = 5.0
    ..maskType = EasyLoadingMaskType.black
    // ..contentPadding = const EdgeInsets.all(30)
    // ..textPadding = const EdgeInsets.only(bottom:20)
    ..progressColor = Color(ScrmColors.activeColor)
    ..backgroundColor = Color(ScrmColors.TextWhite)
    ..indicatorColor = Color(ScrmColors.activeColor)
    ..textColor = Color(ScrmColors.TextBlack)
    ..maskColor = Colors.blue.withOpacity(0.5)
    ..userInteractions = true
    ..dismissOnTap = true;
    // ..customAnimation = CustomAnimation();
}
