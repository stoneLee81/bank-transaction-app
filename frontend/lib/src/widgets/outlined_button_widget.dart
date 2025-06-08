
import 'package:bank_transaction_app/src/common/constants/common_data.dart';
import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/utils/tools/object_utils.dart';
import 'package:flutter/material.dart';

typedef void OnPressed();

class OutlinedButtonWidget extends StatefulWidget {
  final Widget? icon;
  final Widget? title;
  final double height;
  final Color backgroundColor;
  final OnPressed? onPressed;
  final double radius;
  final BorderSide borderSide;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry margin;
  OutlinedButtonWidget({Key? key, this.icon, this.title, this.height = 30, this.padding, this.margin = const EdgeInsets.only(bottom:CommonData.txtBottomAdj), this.radius = 4.0, this.borderSide = const BorderSide(color: Colors.transparent), this.backgroundColor = const Color(ScrmColors.activeColor), this.onPressed}) : super(key: key);

  @override
  _OutlinedButtonWidgetState createState() => _OutlinedButtonWidgetState();
}

class _OutlinedButtonWidgetState extends State<OutlinedButtonWidget> {
  @override
  Widget build(BuildContext context) { 
    return SizedBox(height:widget.height, child:OutlinedButton(
        onPressed: ObjectUtils.isEmpty(widget.onPressed) ? null : () {
          widget.onPressed!();
        },
        style: ButtonStyle(
          padding: MaterialStateProperty.all(widget.padding),
    shape: MaterialStateProperty.all(RoundedRectangleBorder(side: BorderSide(), borderRadius: BorderRadius.circular(widget.radius))),
    // elevation: ,
    side: MaterialStateProperty.all(widget.borderSide),
    backgroundColor: MaterialStateProperty.resolveWith<Color>((states) {
            if (states.contains(MaterialState.disabled)) {
              return Colors.grey[100]!;
            }
            return widget.backgroundColor;
          }),
  ),
        child: Row(mainAxisAlignment: MainAxisAlignment.center, children:[
          widget.icon != null ? Container(padding: const EdgeInsets.only(right:8), child:widget.icon) : SizedBox.fromSize(), widget.title != null ? Container(margin: widget.margin, alignment: Alignment.center, child:widget.title) : SizedBox.fromSize()]),
      ));
  }
}