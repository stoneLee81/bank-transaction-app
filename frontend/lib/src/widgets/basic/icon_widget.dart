import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:flutter/material.dart';

class IconWidget extends StatelessWidget {
  final IconData? icon;
  final double? size;
  const IconWidget({Key? key, this.icon = Icons.arrow_back_ios, this.size = 20.0}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Icon(icon, size: size, color: Color(ScrmColors.iconColor));
  }
}
