import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/pages/home_screen.dart';
import 'package:bank_transaction_app/src/provider/nav_key.dart';
import 'package:bank_transaction_app/src/router/route_handlers.dart';
import 'package:bank_transaction_app/src/utils/log_utils.dart';
import 'package:bank_transaction_app/src/widgets/basic/mycustom_scroll_behavior.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:go_router/go_router.dart';

Future<void> runPlatformInit() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // 初始化核心服务
  // await SecurityDataHelper.getInstance();

  // LoginUtils().eventListener();
  
  // 初始化日志
  LogUtil.init(isDebug: ConstantsData.ISDEBUG);
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late final GoRouter _router;

  @override
  void initState() {
    super.initState();
    _router = _configureRouter();
  }

  GoRouter _configureRouter() {
    return GoRouter(
      redirect: RouteHandlers.handleRedirect,
      onException: RouteHandlers.handleException,
      initialLocation: '/',
      debugLogDiagnostics: true,
      navigatorKey: NavKey.navKey,
      routes: [
        // ...errorRoutes,
        GoRoute(
          path: '/',
          builder: (context, state) => HomeScreen()
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    // DeviceUtils.getDeviceInfo().then((value) {
    //   CommonData.deviceinfo = value.toSimpleString();
    // });
    
    return MaterialApp.router(
      title: '',
      routerConfig: _router,
      scrollBehavior: MyCustomScrollBehavior(),
      debugShowCheckedModeBanner: false,
      theme: AppTheme.buildTheme(),
      builder: EasyLoading.init(),
    );
  }
}
