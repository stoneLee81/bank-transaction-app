import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class RouteHandlers {
  // 重定向处理
  static Future<String?> handleRedirect(BuildContext context, GoRouterState state) async {
    // H5路由不需要验证
    if((state.fullPath??'').contains('/h5')) return null;
    return '/';
    // if(ObjectUtils.isEmpty(CommonData.userSecure)) {
    //   String successMsg = await SecurityDataHelper.read('secure.success', overtimeDel: true);
    //   if(ObjectUtils.isNotEmpty(successMsg)) {
    //     await LoginUtils().login(successMsg, context, saveSkey: false);
    //     return null;
    //   }
    //   return state.fullPath == '/admin' ? '/' : null;
    // }
    // return null;
  }

  // 异常处理
  static void handleException(_, GoRouterState state, GoRouter router) {
    router.go('/404', extra: state.uri.toString());
  }
}
