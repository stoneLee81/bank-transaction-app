import 'package:flutter/foundation.dart';

import 'platform_web.dart' if (dart.library.io) 'platform_io.dart';

// ignore: avoid_classes_with_only_static_members
class GetPlatform {
  static bool get isWeb => GeneralPlatform.isWeb;

  static bool get isMacOS => GeneralPlatform.isMacOS;

  static bool get isWindows => GeneralPlatform.isWindows;

  static bool get isLinux => GeneralPlatform.isLinux;

  static bool get isAndroid => GeneralPlatform.isAndroid;

  static bool get isIOS => GeneralPlatform.isIOS;

  static bool get isFuchsia => GeneralPlatform.isFuchsia;

  //这里判断desktop&mobile的条件不够精准，注释掉，使用deviceType来width判断
  // static bool get isMobile => GetPlatform.isIOS || GetPlatform.isAndroid;

  // static bool get isDesktop =>
  //     GetPlatform.isMacOS || GetPlatform.isWindows || GetPlatform.isLinux;

  // static bool get isDesktop =>
  //     (kIsWeb || GetPlatform.isLinux || GetPlatform.isWindows || GetPlatform.isMacOS) && 
  //     !GetPlatform.isMobile;
}
