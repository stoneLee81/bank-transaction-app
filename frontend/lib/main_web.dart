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

// JSUtils jsUtils = JSUtils();

// @JS()
// external set secureValue(JSFunction value);

// String _secureValue() {
//   return jsUtils.secureValue();
// }

// @JS()
// external set registrationStateChanged(JSFunction value);

// void _registrationStateChanged(String code, String statuscode, String reasonphrase, String callId) {
//   jsUtils.registrationStateChanged(code, statuscode, reasonphrase, callId);
// }

// @JS()
// external set pluginRegistrationStateChanged(JSFunction value);

// void _pluginRegistrationStateChanged(String code, String statuscode, String reasonphrase, String app, String state) {
//   jsUtils.pluginRegistrationStateChanged(code, statuscode, reasonphrase, app, state);
// }

// @JS()
// external set postMessageListener(JSFunction value);

// void _postMessageListener(String action, String data) {
//   jsUtils.postMessageListener(action, data); 
// }

Future<void> runPlatformInit() async {
  WidgetsFlutterBinding.ensureInitialized();
  // LoginUtils().eventListener();

  // WebJsBridge.initialize();
  // registrationStateChanged = _registrationStateChanged.toJS;
  // pluginRegistrationStateChanged = _pluginRegistrationStateChanged.toJS;
  // postMessageListener = _postMessageListener.toJS;
  // secureValue = _secureValue.toJS;

  // await SecurityDataHelper.getInstance();
  LogUtil.init(isDebug: ConstantsData.ISDEBUG);
  // WebUtils.initSession();
}

//这里发现，单个文件加载速度反而要快一些，分拆包的逻辑注释
class MyApp extends StatefulWidget {
  MyApp({Key? key}) : super(key: key);

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

  @override
  void dispose() {
    super.dispose();  
  }

  GoRouter _configureRouter() {
    return GoRouter(
      redirect: RouteHandlers.handleRedirect,
      onException: RouteHandlers.handleException,
      initialLocation: '/',
      debugLogDiagnostics: true,
      navigatorKey: NavKey.navKey,
      routes: [
        GoRoute(
          path: '/',
          builder: (context, state) => HomeScreen()
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
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

//路由跳转监测
class MyNavigatorObserver extends NavigatorObserver {
  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    LogUtil.v('did push route');
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    LogUtil.v('did pop route');
  }
}
