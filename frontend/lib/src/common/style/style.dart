import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:bank_transaction_app/src/utils/no_transation.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class AppColors {
  static const APPBarBottomColor = 0xfff9f9f9;
  static const APPBarTextColor = 0xff010101;
  static const TitleColor = 0xff353535;
  static const APPCardColor = 0xff616161;
  static const StickyTopColor = 0xffEAEAEA;
  static const TextBlack = 0xff1d1d1f;
  static const TextWhite = 0xffffffff;
  static const TextGrey = 0xffa4a4a4;
  static const TextRed = 0xffe53935;
  static const TextGreen = 0xffa4a4a4;
  static const ButtonColor = 0xff494949;
  static const WebTextfieldColor = 0xffE0E0E0;

  static const ConversationItemBg = 0xffffffff;
  static const DesTextColor = 0xff9e9e9e;
  static const DividerColor = 0xffF4F4F4; //0xffebedef;
  static const NotifyDotBg = 0xffff3e3e; 
  static const NotifyDotText = 0xffffffff;
  static const ConversationMuteIcon = 0xffd8d8d8;
  static const PrimaryColor = 0xffEDEDED;
  static const BackgroundColor = 0xffededed;
  static const CardBgColor = 0xffffffff;
  static const TextBobuleRight = 0xff9def71;
  static const TextBobuleLeft = 0xffffffff;
  static const TextBobule = 0xff3e3e3e;
  static const ChatTime = 0xffababab;
  static const ToastInfo = 0xfff5f5f5;
  static const ToastError = 0xffe53935;
  static const ToastSuccInfo = 0xff7cb342;
  static const textfieldColor = 0xffE0E0E0;
  static const wechatBackgroundColor = 0xfffafafa;

  static const ChatIconColor = 0xff707070;  
}

class ScrmColors {
  static const backgrouldColor = 0xffF5f5f5;
  static const TextBlack = 0xff1d1d1f;
  static const TextBlack02 = 0xff1d1d1f;
  static const TextWhite = 0xffffffff;
  static const TextGrey = 0xff909090;
  static const TextGreyLight = 0xffC5C8CB;
  static const TextRed = 0xffe53935;
  static const TextGreen = 0xff43a047;
  static const divideColor = 0xffF8F8F8;
  static const divideColorLight = 0xffF9F9F9;
  static const activeColor = 0xff2196F3; //529ecc;
  static const iconColor = 0xff707070; 
  static const selectedItemColor = 0xffEEF1F3;
  static const stickyTopColor = 0xffEAEAEA;
  static const tipBackgroudColor = 0xffFFFBE7;
  static const tipBackgroundErrorColor = 0xffF7EDED;
  static const dropdownIconColor = 0xff9F9F9F;
  static const wechatBackgroundColor = 0xfffafafa;
  static const labelColor = Color.fromARGB(255, 106, 106, 106);
  static const labelBgColor = Color(0xffF5f5f5);
  static const background = Color(0xfffafafa);
  // static const barBackgrouldColor = 0xfff9f9f9;
  // static const barBtnBackgrouldColor = 0xff333333;
  static const ToastInfo = 0xfff5f5f5;
  static const ToastError = 0xffe53935;
  static const ToastSuccInfo = 0xff7cb342;
}

class WebAgentAppColors extends AppColors {
  static const ChatBarColor = 0xffF2F4F5;

  static const ChatContentColor = 0xffEEF1F3;

  static const ChatItemSelColor = 0xffF1F2F3;

  static const ChatPartColor = 0xffF9F9F9;

  static const ChatIconColor = 0xff707070;

  static const ChatLineColor = 0xffE8E8E9;

  static const ChatTextfieldColor = 0xffE0E0E0;

  static const test = 0xff848382;

  static const Red = 0xFFF36D6F;
}

// 添加主题配置
class AppTheme {
  static ThemeData buildTheme() => ThemeData(
    fontFamily: 'PingFang',
    useMaterial3: false,
    scrollbarTheme: ScrollbarThemeData(
      thickness: WidgetStateProperty.all(6),
        thumbColor: WidgetStateProperty.all(Color(0xff7D7D7D)),
    ),
    pageTransitionsTheme: kIsWeb ? NoTransitionsOnWeb() : NoTransitionsOnWeb(),
    appBarTheme: const AppBarTheme(
      toolbarHeight: ConstantsData.appbarHeight,
      color: Colors.white,
      foregroundColor: Colors.white,
      elevation: 0.4,
      centerTitle: true,
    ),
    primaryColor: const Color(AppColors.PrimaryColor),
    platform: TargetPlatform.iOS,
  );
}
