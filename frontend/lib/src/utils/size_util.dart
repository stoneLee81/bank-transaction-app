import 'dart:ui';

import 'package:bank_transaction_app/src/provider/nav_key.dart';
import 'package:flutter/material.dart';

class SizeUtil {
  /// 获取当前 `FlutterView`
  static FlutterView get window => View.of(NavKey.navKey.currentContext!);

  static Locale? get deviceLocale => PlatformDispatcher.instance.locale;

  ///The number of device pixels for each logical pixel.
  static double get pixelRatio => window.devicePixelRatio;

  static Size get size => window.physicalSize / pixelRatio;

  ///The horizontal extent of this size.
  static double get width => size.width;

  ///The vertical extent of this size
  static double get height => size.height;
}
