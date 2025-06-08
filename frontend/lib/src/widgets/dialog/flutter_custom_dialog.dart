import 'package:bank_transaction_app/src/widgets/outlined_button_widget.dart';
import 'package:flutter/material.dart';
import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/utils/tools/object_utils.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';

import 'flutter_custom_dialog_widget.dart';

class CustomerDialog {
  //================================弹窗属性======================================
  List<Widget> widgetList = []; //弹窗内部所有组件
  static BuildContext? _context; //弹窗上下文
  BuildContext? context; //弹窗上下文

  double? width; //弹窗宽度
  double? height; //弹窗高度
  Duration duration = Duration(milliseconds: 250); //弹窗动画出现的时间
  Gravity gravity = Gravity.center; //弹窗出现的位置
  bool gravityAnimationEnable = false; //弹窗出现的位置带有的默认动画是否可用
  Color barrierColor = Colors.black.withOpacity(.3); //弹窗外的背景色
  BoxConstraints? constraints; //弹窗约束
  Function(Widget child, Animation<double> animation)? animatedFunc; //弹窗出现的动画
  bool barrierDismissible = true; //是否点击弹出外部消失
  EdgeInsets margin = EdgeInsets.all(0.0); //弹窗布局的外边距

  /// 用于有多个navigator嵌套的情况，默认为true
  /// @params useRootNavigator=false，push是用的是当前布局的context
  /// @params useRootNavigator=true，push是用的嵌套根布局的context
  bool useRootNavigator = true;

  Decoration? decoration; //弹窗内的装饰，与backgroundColor和borderRadius互斥
  Color backgroundColor = Colors.white; //弹窗内的背景色
  double borderRadius = 0.0; //弹窗圆角

  Function()? showCallBack; //展示的回调
  Function()? dismissCallBack; //消失的回调

  get isShowing => _isShowing; //当前 弹窗是否可见
  bool _isShowing = false;

  //============================================================================
  static void init(BuildContext ctx) {
    _context = ctx;
  }

  CustomerDialog build([BuildContext? ctx]) {
    if (ctx == null && _context != null) {
      this.context = _context;
      return this;
    }
    this.context = ctx;
    return this;
  }

  CustomerDialog widget(Widget child) {
    this.widgetList.add(child);
    return this;
  }

  CustomerDialog appbar({
    String? title,
    Widget? action
  }) {
    return this.widget(UITools.appBar2(title!, actions: action == null ? [] : [action]));
  }

  CustomerDialog text(
      {padding,
      text,
      color,
      fontSize,
      alignment,
      textAlign,
      maxLines,
      textDirection,
      overflow,
      fontWeight,
      fontFamily}) {
    return this.widget(
      Padding(
        padding: padding ?? EdgeInsets.all(0.0),
        child: Align(
          alignment: alignment ?? Alignment.centerLeft,
          child: Text(
            text ?? "",
            textAlign: textAlign,
            maxLines: maxLines,
            textDirection: textDirection,
            overflow: overflow,
            style: TextStyle(
              color: color ?? Colors.black,
              fontSize: fontSize ?? 14.0,
              fontWeight: fontWeight,
              fontFamily: fontFamily,
            ),
          ),
        ),
      ),
    );
  }

  CustomerDialog button({
    padding,
    height,
    isClickAutoDismiss = true, //点击按钮后自动关闭
    Text? text1,
    VoidCallback? onTap1,
  }) {
    return doubleButton(padding: padding, height: height, isClickAutoDismiss:isClickAutoDismiss, text1: ObjectUtils.isEmpty(text1) ? UITools.text('确定') : text1, onTap1: onTap1, one: true);
  }

  CustomerDialog doubleButton({
    padding,
    height,
    isClickAutoDismiss = true, //点击按钮后自动关闭
    withDivider = false, //中间分割线
    bool one = false,
    Text? text1,
    onTap1,
    Text? text2,
    VoidCallback? onTap2,
  }) {
    
    return this.widget(
      SizedBox(
        height: height ?? 45.0,
        child: Column(children:[
          Container(height:1.0, child:Divider(color: Color(ScrmColors.divideColor))),
          Expanded(child:Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            Expanded(child:OutlinedButtonWidget(
              backgroundColor: Colors.white,
              onPressed: () {
                if (onTap1 != null) onTap1();
                if (isClickAutoDismiss) {
                  dismiss();
                }
              },
              padding: EdgeInsets.all(0.0),
              title: ObjectUtils.isEmpty(text1) ? UITools.text('取消') : text1
            )),
            one ? SizedBox.shrink() : Visibility(
              visible: withDivider,
              child: VerticalDivider(color: Color(ScrmColors.divideColor), width: 1.0, thickness: 0.5,),
              // child:Container(child:),
            ),
            one ? SizedBox.shrink() : Expanded(child:OutlinedButtonWidget(
              backgroundColor: Colors.white,
              onPressed: () {
                if (onTap2 != null) onTap2();
                if (isClickAutoDismiss) {
                  dismiss();
                }
              },
              padding: EdgeInsets.all(0.0),
              title: ObjectUtils.isEmpty(text2) ? UITools.text('确定') : text2
            ))
          ],
        )),
        ])
      ),
    );
  }

  CustomerDialog listViewOfListTile({
    List<ListTileItem>? items,
    double? height,
    isClickAutoDismiss = true,
    Function(int)? onClickItemListener,
  }) {
    return this.widget(
      Container(
        height: height,
        child: ListView.builder(
          padding: EdgeInsets.all(0.0),
          shrinkWrap: true,
          itemCount: items?.length,
          itemBuilder: (BuildContext context, int index) {
            return Material(
              color: Colors.white,
              child: InkWell(
                child: ListTile(
                  onTap: () {
                    if (onClickItemListener != null) {
                      onClickItemListener(index);
                    }
                    if (isClickAutoDismiss) {
                      dismiss();
                    }
                  },
                  contentPadding: items![index].padding ?? EdgeInsets.all(0.0),
                  leading: items[index].leading,
                  title: Text(
                    items[index].text ?? "",
                    style: TextStyle(
                      color: items[index].color ?? null,
                      fontSize: items[index].fontSize ?? null,
                      fontWeight: items[index].fontWeight,
                      fontFamily: items[index].fontFamily,
                    ),
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  CustomerDialog listViewOfRadioButton({
    List<RadioItem>? items,
    double? height,
    Color? color,
    Color? activeColor,
    int? intialValue,
    Function(int)? onClickItemListener,
  }) {
    Size size = MediaQuery.of(context!).size;
    return this.widget(
      Container(
        height: height,
        constraints: BoxConstraints(
          minHeight: size.height * .1,
          minWidth: size.width * .1,
          maxHeight: size.height * .5,
        ),
        child: CustomerRadioListTile(
          items: items,
          intialValue: intialValue,
          color: color,
          activeColor: activeColor,
          onChanged: onClickItemListener,
        ),
      ),
    );
  }

  CustomerDialog circularProgress(
      {padding, backgroundColor, valueColor, strokeWidth}) {
    return this.widget(Padding(
      padding: padding,
      child: CircularProgressIndicator(
        strokeWidth: strokeWidth ?? 4.0,
        backgroundColor: backgroundColor,
        valueColor: AlwaysStoppedAnimation<Color>(valueColor),
      ),
    ));
  }

  CustomerDialog divider({color, height}) {
    return this.widget(
      Divider(
        color: color ?? Colors.grey[300],
        height: height ?? 0.1,
      ),
    );
  }

  ///  x坐标
  ///  y坐标
  void show([x, y]) {
    var mainAxisAlignment = getColumnMainAxisAlignment(gravity);
    var crossAxisAlignment = getColumnCrossAxisAlignment(gravity);
    if (x != null && y != null) {
      gravity = Gravity.leftTop;
      margin = EdgeInsets.only(left: x, top: y);
    }
    CustomDialog(
      gravity: gravity,
      gravityAnimationEnable: gravityAnimationEnable,
      context: this.context!,
      barrierColor: barrierColor,
      animatedFunc: animatedFunc,
      barrierDismissible: barrierDismissible,
      duration: duration,
      child: Padding(
        padding: margin,
        child: Column(
          textDirection: TextDirection.ltr,
          mainAxisAlignment: mainAxisAlignment,
          crossAxisAlignment: crossAxisAlignment,
          children: <Widget>[
            Material(
              clipBehavior: Clip.antiAlias,
              type: MaterialType.transparency,
              borderRadius: BorderRadius.circular(borderRadius),
              child: Container(
                width: width ?? null,
                height: height ?? null,
                decoration: decoration ??
                    BoxDecoration(
                      borderRadius: BorderRadius.circular(borderRadius),
                      color: backgroundColor,
                    ),
                constraints: constraints ?? BoxConstraints(),
                child: CustomDialogChildren(
                  widgetList: widgetList,
                  isShowingChange: (bool isShowingChange) {
                    // showing or dismiss Callback
                    if (isShowingChange) {
                      showCallBack?.call();
                    } else {
                      dismissCallBack?.call();
                    }
                    _isShowing = isShowingChange;
                  },
                ),
              ),
            )
          ],
        ),
      ),
    );
  }

  void dismiss() {
    if (_isShowing) {
      Navigator.of(context!, rootNavigator: useRootNavigator).pop();
    }
  }

  getColumnMainAxisAlignment(gravity) {
    var mainAxisAlignment = MainAxisAlignment.start;
    switch (gravity) {
      case Gravity.bottom:
      case Gravity.leftBottom:
      case Gravity.rightBottom:
        mainAxisAlignment = MainAxisAlignment.end;
        break;
      case Gravity.top:
      case Gravity.leftTop:
      case Gravity.rightTop:
        mainAxisAlignment = MainAxisAlignment.start;
        break;
      case Gravity.left:
        mainAxisAlignment = MainAxisAlignment.center;
        break;
      case Gravity.right:
        mainAxisAlignment = MainAxisAlignment.center;
        break;
      case Gravity.center:
      default:
        mainAxisAlignment = MainAxisAlignment.center;
        break;
    }
    return mainAxisAlignment;
  }

  getColumnCrossAxisAlignment(gravity) {
    var crossAxisAlignment = CrossAxisAlignment.center;
    switch (gravity) {
      case Gravity.bottom:
        break;
      case Gravity.top:
        break;
      case Gravity.left:
      case Gravity.leftTop:
      case Gravity.leftBottom:
        crossAxisAlignment = CrossAxisAlignment.start;
        break;
      case Gravity.right:
      case Gravity.rightTop:
      case Gravity.rightBottom:
        crossAxisAlignment = CrossAxisAlignment.end;
        break;
      default:
        break;
    }
    return crossAxisAlignment;
  }

  getRowMainAxisAlignment(gravity) {
    var mainAxisAlignment = MainAxisAlignment.start;
    switch (gravity) {
      case Gravity.bottom:
        break;
      case Gravity.top:
        break;
      case Gravity.left:
        mainAxisAlignment = MainAxisAlignment.start;
        break;
      case Gravity.right:
        mainAxisAlignment = MainAxisAlignment.end;
        break;
      case Gravity.spaceEvenly:
        mainAxisAlignment = MainAxisAlignment.spaceEvenly;
        break;
      case Gravity.center:
      default:
        mainAxisAlignment = MainAxisAlignment.center;
        break;
    }
    return mainAxisAlignment;
  }
}

///弹窗的内容作为可变组件
class CustomDialogChildren extends StatefulWidget {
  final List<Widget> widgetList; //弹窗内部所有组件
  final Function(bool)? isShowingChange;

  CustomDialogChildren({this.widgetList = const [], this.isShowingChange});

  @override
  CustomDialogChildState createState() => CustomDialogChildState();
}

class CustomDialogChildState extends State<CustomDialogChildren> {
  @override
  Widget build(BuildContext context) {
    if (widget.isShowingChange != null) {
      widget.isShowingChange!(true);
    }
    return Column(
      children: widget.widgetList,
    );
  }

  @override
  void dispose() {
    if (widget.isShowingChange != null) {
      widget.isShowingChange!(false);
    }
    super.dispose();
  }
}

///弹窗API的封装
class CustomDialog {
  BuildContext _context;
  Widget _child;
  Duration? _duration;
  Color? _barrierColor;
  RouteTransitionsBuilder? _transitionsBuilder;
  bool? _barrierDismissible;
  Gravity? _gravity;
  bool _gravityAnimationEnable;
  Function? _animatedFunc;

  CustomDialog({
    required Widget child,
    required BuildContext context,
    Duration? duration,
    Color? barrierColor,
    RouteTransitionsBuilder? transitionsBuilder,
    Gravity? gravity,
    bool gravityAnimationEnable = false,
    Function? animatedFunc,
    bool? barrierDismissible,
  })  : _child = child,
        _context = context,
        _gravity = gravity,
        _gravityAnimationEnable = gravityAnimationEnable,
        _duration = duration,
        _barrierColor = barrierColor,
        _animatedFunc = animatedFunc,
        _transitionsBuilder = transitionsBuilder,
        _barrierDismissible = barrierDismissible {
    this.show();
  }

  show() {
    //fix transparent error
    if (_barrierColor == Colors.transparent) {
      _barrierColor = Colors.white.withOpacity(0.0);
    }

    showGeneralDialog(
      context: _context,
      barrierColor: _barrierColor ?? Colors.black.withOpacity(.3),
      barrierDismissible: _barrierDismissible ?? true,
      barrierLabel: "",
      transitionDuration: _duration ?? Duration(milliseconds: 300),
      transitionBuilder: _transitionsBuilder ?? _buildMaterialDialogTransitions,
      pageBuilder: (BuildContext buildContext, Animation<double> animation,
          Animation<double> secondaryAnimation) {
        return Builder(
          builder: (BuildContext context) {
            return _child;
          },
        );
      },
    );
  }

  Widget _buildMaterialDialogTransitions(
      BuildContext context,
      Animation<double> animation,
      Animation<double> secondaryAnimation,
      Widget child) {
    Animation<Offset> custom;
    switch (_gravity) {
      case Gravity.top:
      case Gravity.leftTop:
      case Gravity.rightTop:
        custom = Tween<Offset>(
          begin: Offset(0.0, -1.0),
          end: Offset(0.0, 0.0),
        ).animate(animation);
        break;
      case Gravity.left:
        custom = Tween<Offset>(
          begin: Offset(-1.0, 0.0),
          end: Offset(0.0, 0.0),
        ).animate(animation);
        break;
      case Gravity.right:
        custom = Tween<Offset>(
          begin: Offset(1.0, 0.0),
          end: Offset(0.0, 0.0),
        ).animate(animation);
        break;
      case Gravity.bottom:
      case Gravity.leftBottom:
      case Gravity.rightBottom:
        custom = Tween<Offset>(
          begin: Offset(0.0, 1.0),
          end: Offset(0.0, 0.0),
        ).animate(animation);
        break;
      case Gravity.center:
      default:
        custom = Tween<Offset>(
          begin: Offset(0.0, 0.0),
          end: Offset(0.0, 0.0),
        ).animate(animation);
        break;
    }

    //自定义动画
    if (_animatedFunc != null) {
      return _animatedFunc!(child, animation);
    }

    //不需要默认动画
    if (!_gravityAnimationEnable) {
      custom = Tween<Offset>(
        begin: Offset(0.0, 0.0),
        end: Offset(0.0, 0.0),
      ).animate(animation);
    }

    return SlideTransition(
      position: custom,
      child: child,
    );
  }
}

//================================弹窗重心======================================
enum Gravity {
  left,
  top,
  bottom,
  right,
  center,
  rightTop,
  leftTop,
  rightBottom,
  leftBottom,
  spaceEvenly,
}
//============================================================================

//================================弹窗实体======================================
class ListTileItem {
  ListTileItem({
    this.padding,
    this.leading,
    this.text,
    this.color,
    this.fontSize,
    this.fontWeight,
    this.fontFamily,
  });

  EdgeInsets? padding;
  Widget? leading;
  String? text;
  Color? color;
  double? fontSize;
  FontWeight? fontWeight;
  String? fontFamily;
}

class RadioItem {
  RadioItem({
    this.padding,
    this.text,
    this.color,
    this.fontSize,
    this.fontWeight,
    this.onTap,
  });

  EdgeInsets? padding;
  String? text;
  Color? color;
  double? fontSize;
  FontWeight? fontWeight;
  Function(int)? onTap;
}