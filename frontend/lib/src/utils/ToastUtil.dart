
import 'package:bank_transaction_app/src/provider/nav_key.dart';
import 'package:bank_transaction_app/src/utils/tools/object_utils.dart';
import 'package:bank_transaction_app/src/widgets/dialog/flutter_custom_dialog.dart';
import 'package:flutter/material.dart';
import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';
import 'package:fluttertoast/fluttertoast.dart';

class ToastUtil {
  static FToast ftoast = FToast()..init(NavKey.navKey.currentContext!);

  static show(String msg, {Widget? icon, int backgroundColor = 0, int textColor = 0, ToastGravity gravity = ToastGravity.TOP, duration = const Duration(seconds: 2), int pos = 0, double? maxWidth}) {
    ftoast.showToast(child: Container(
      constraints: maxWidth != null ? BoxConstraints(maxWidth: maxWidth) : BoxConstraints(maxWidth: 650),
    padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 12.0),
    decoration: new BoxDecoration(color: Colors.white, borderRadius: BorderRadius.all(Radius.circular(5.0)),boxShadow: [BoxShadow(color: Color(AppColors.TextGrey), blurRadius: 5.0,) ]),
    child: Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        icon != null ? icon : Icon(Icons.info,color: backgroundColor == 0 ? Color(AppColors.ToastSuccInfo) : Color(backgroundColor)),
        SizedBox(width: 12.0),
        UITools.text(msg, maxLines: 2),
      ],
    ),
  ), gravity: gravity, toastDuration: duration);
    // if(maxWidth == null) maxWidth = UITools.deviceType(NavKey.navKey.currentContext) == DeviceScreenType.mobile ? MediaQuery.of(NavKey.navKey.currentContext).size.width - 32 : 550;

    // Flushbar(messageText: Text(msg, maxLines: 2, overflow: TextOverflow.ellipsis, style: TextStyle(color:Colors.black, fontSize: 14.0)), 
    // duration: duration, 
    // borderRadius: BorderRadius.all(Radius.circular(5.0)),
    // icon: icon != null ? icon : Icon(Icons.info,color: backgroundColor == 0 ? Color(AppColors.ToastSuccInfo) : Color(backgroundColor),),
    // margin: const EdgeInsets.all(20),
    // padding: const EdgeInsets.all(12),
    // boxShadows: [BoxShadow(color: Color(AppColors.TextGrey), blurRadius: 5.0,) ],
    // maxWidth: maxWidth,
    // flushbarPosition: FlushbarPosition.TOP,
    // // flushbarPosition: UITools.deviceType(NavKey.navKey.currentContext) == DeviceScreenType.mobile ? FlushbarPosition.BOTTOM : FlushbarPosition.TOP,
    // backgroundColor: Color(0xffffffff),
    // ).show(NavKey.navKey.currentContext);
  }

  static void toastWithBtn(BuildContext context, {String? title, Text? text1, Text? text2, bool barrierDismissible = false, double width = 250, VoidCallback? onTap1, VoidCallback? onTap2,}) {
    CustomerDialog().build(context)
    ..width = width
    ..text(padding: EdgeInsets.all(18.0), textAlign: TextAlign.center, text: "$title", color: Colors.grey[700], fontSize: 13.0)
    // ..divider(color: Color(ScrmColors.divideColor))
    ..barrierDismissible = barrierDismissible
    ..animatedFunc = (child, animation) { return FadeTransition(opacity: animation, child: child);} 
    ..button(padding: EdgeInsets.only(top: 10.0), 
      text1: text1??UITools.text('确定', color: Color(ScrmColors.TextBlack)), 
      onTap1: () {onTap1!();})..show();
  }

  static void toastWithDoubleBtn(BuildContext context, {String? title, bool one = false, bool barrierDismissible = false, Text? text1, Text? text2, double width = 250, bool clickAutoDismiss = true, VoidCallback? onTap1, VoidCallback? onTap2}) {
    CustomerDialog().build(context)
    ..width = width
    ..widget(Container(padding: EdgeInsets.all(18.0), alignment: Alignment.center, child:Text("$title", textAlign: TextAlign.center, style:TextStyle(color: Colors.grey[700], fontSize: 13.0))))
    // ..text(padding: EdgeInsets.all(18.0), textAlign: TextAlign.center, text: "title", color: Colors.grey[700])
    // ..divider(color: Color(ScrmColors.divideColor))
    ..barrierDismissible = barrierDismissible
    ..animatedFunc = (child, animation) { return FadeTransition(opacity: animation, child: child);} 
    ..doubleButton( 
      isClickAutoDismiss: clickAutoDismiss,
      padding: EdgeInsets.only(top: 10.0), 
      one: one,
      text1: text1??UITools.text('取消', color: Color(ScrmColors.TextBlack)),
      onTap1: () {
        if(ObjectUtils.isNotEmpty(onTap1)) onTap1!();
      },
      text2: text2??UITools.text('确定', color: Color(ScrmColors.activeColor)),
      withDivider: true,
      onTap2: () {
        if(ObjectUtils.isNotEmpty(onTap2)) onTap2!();
      })
      ..show();
  }

  static showPopupwindow(BuildContext context, {Widget? widget, bool dismissible = true, bool bottomSheet = false, barrierColor = const Color(0x80000000), double width = UITools.popWidth, double? height, double? maxHeight} ) async {
    double _maxHeight = 1000;
    if(UITools.isMobile || bottomSheet) {
      showModalBottomSheet(isScrollControlled: true, constraints: BoxConstraints(maxWidth: width, 
      maxHeight: maxHeight == null ? MediaQuery.of(context).size.height*0.80 : (maxHeight > _maxHeight ? _maxHeight : maxHeight)), 
      builder: (BuildContext context) { 
        return Material(clipBehavior: Clip.antiAlias, borderRadius: BorderRadius.circular(12), child:widget); 
       }, 
       barrierColor:barrierColor, backgroundColor: Colors.transparent, context: context);
    }else {
      await showGeneralDialog(
        context: context,
        barrierDismissible: dismissible,
        barrierLabel: '',
        barrierColor: barrierColor,
        transitionDuration: Duration(milliseconds: 300),
        transitionBuilder: (ctx, animation, _, child) {
          return SlideTransition(
                    position: Tween<Offset>(
                      begin: const Offset(0, 1),
                      end: Offset.zero,
                    ).animate(animation),
                    child: child,
                  );
        },
        pageBuilder: (BuildContext context, Animation<double> animation, Animation<double> secondaryAnimation) {
          return _SystemPadding(child: Center(
            child: Container(
              decoration: new BoxDecoration(
                color: Colors.white,
                // 设置四周圆角 角度
                borderRadius: BorderRadius.all(Radius.circular(5.0)),
                // 设置四周边框
                border: new Border.all(width: 1, color: Color(AppColors.DividerColor)),
                boxShadow: [BoxShadow(color: Colors.black12,blurRadius: .0)]
              ),
              constraints: BoxConstraints(
                // minHeight: 200,
                maxHeight: maxHeight == null ? MediaQuery.of(context).size.height*0.85 : (maxHeight > _maxHeight ? _maxHeight : maxHeight),),
                width: width,
                height: height == null ? MediaQuery.of(context).size.height*0.85 : height,
                child: widget, 
          )));
      });
    }
  }
}

class _SystemPadding extends StatelessWidget {
  final Widget? child;

  _SystemPadding({Key? key, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var mediaQuery = MediaQuery.of(context);
    return new AnimatedContainer(
        padding: mediaQuery.viewInsets,
        duration: const Duration(milliseconds: 300),
        child: child);
  }
}