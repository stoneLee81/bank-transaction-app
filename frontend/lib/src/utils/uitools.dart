import 'package:bank_transaction_app/src/common/constants/common_data.dart';
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/utils/ToastUtil.dart';
import 'package:bank_transaction_app/src/utils/size_util.dart';
import 'package:bank_transaction_app/src/utils/tools/object_utils.dart';
import 'package:bank_transaction_app/src/widgets/basic/icon_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:responsive_builder/responsive_builder.dart';

class UITools {

  static const double popWidth = 600;

  static void processResp(dynamic resp, {String succMsg = '保存成功'}) {
    EasyLoading.dismiss(); 
    if(resp.status == '200') {
      if(ObjectUtils.isNotEmpty(succMsg)) ToastUtil.show('$succMsg');
    }else {
      ToastUtil.show('${resp.message}', backgroundColor: AppColors.ToastError);
    }
  }

  static TextStyle style({double fontSize = 13.0, Color color = const Color(ScrmColors.TextGrey), FontWeight fontweight = FontWeight.normal}) {
    return TextStyle(fontSize: fontSize, color: color, fontWeight: fontweight, fontFamily: CommonData.fontFamily);
  }

  static TextStyle textStyle({double fontSize = 13.0, Color color = const Color(ScrmColors.TextBlack), TextDecoration? decoration, String? fontFamily, List<String>? fontFamilyFallback = const ['Twemoji'], FontWeight fontweight = FontWeight.normal}) {
    return TextStyle(fontSize: fontSize, color: color, fontWeight: fontweight, decoration: decoration, fontFamilyFallback: fontFamilyFallback, fontFamily: ObjectUtils.isEmpty(fontFamily) ? CommonData.fontFamily : fontFamily); 
  } 

  static TextStyle textTabStyle({double fontSize = 13.0, Color? color, FontWeight? fontweight}) {
    return TextStyle(fontSize: fontSize, color: color, fontWeight: fontweight, fontFamily: CommonData.fontFamily); 
  } 

  static Text text(data, {TextOverflow? overflow, int? maxLines, double fontSize = 13.0, TextAlign? textAlign, TextDecoration? decoration, Color color = const Color(ScrmColors.TextBlack), String? fontFamily, List<String> fontFamilyFallback = const ['Twemoji'], FontWeight fontweight = FontWeight.normal}) {
    return Text(data, strutStyle: structStyle(), maxLines: maxLines, textAlign: textAlign, overflow: overflow, style: textStyle(decoration: decoration, fontSize: fontSize, color: color, fontFamilyFallback: fontFamilyFallback, fontFamily: ObjectUtils.isEmpty(fontFamily) ? CommonData.fontFamily : fontFamily, fontweight: fontweight));
  }
  
  static StrutStyle structStyle({double fontSize = 13.0, FontWeight fontweight = FontWeight.normal}) {
    return StrutStyle(height: 1.2, fontSize: fontSize, fontWeight: fontweight);
  }

  static bool get isMobile => (UITools.deviceType() == DeviceScreenType.mobile);
  
  static bool get isDesktop => (UITools.deviceType() == DeviceScreenType.desktop || UITools.deviceType() == DeviceScreenType.tablet);

  static DeviceScreenType deviceType() {
    return getDeviceType(SizeUtil.size);
  }

  // static Widget appBar(BuildContext context, {String? title, Widget? leading, List<Widget> actions = const [SizedBox.shrink()],  Alignment titleAlign = Alignment.center}) {
  //   double _height = ConstantsData.appbarHeight; 
  //   return Container(padding: const EdgeInsets.only(top:0, bottom:0, right:8, left:8), decoration: BoxDecoration(border:Border(bottom:BorderSide(color:Color(ScrmColors.divideColor)))), child:AppbarTle(showBar: true, title:title, titleAlign: titleAlign, edgeInsetsGeometry: const EdgeInsets.only(left:0), barHeight: _height, alignment: Alignment.topRight, 
  //       leading: leading == null ? InkWell(onTap: () {
  //         Navigator.of(context).pop();
  //       }, child:Container(width:40, child:IconWidget())) : InkWell(onTap: () {Navigator.of(context).pop();}, child:Container(width:40, child:leading)), 
  //       child: Container(width:200, height: _height, alignment: Alignment.centerRight, child:Row(mainAxisAlignment: MainAxisAlignment.end, children: actions))));
  // }

  static PreferredSizeWidget appBar2(String title, {List<Widget> actions = const [], double elevation = 0.4}) { 
    return AppBar(toolbarHeight: ConstantsData.appbarHeight, title: Text('$title', style: UITools.style(color: Color(ScrmColors.TextWhite), fontSize: 15, fontweight: FontWeight.bold,)), backgroundColor: Colors.blue.shade700, elevation: elevation, titleSpacing: 16, centerTitle: false, actions: actions);
  }
}