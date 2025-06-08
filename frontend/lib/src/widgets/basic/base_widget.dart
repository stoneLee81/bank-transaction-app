import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:bank_transaction_app/src/widgets/outlined_button_widget.dart';
import 'package:flutter/material.dart';
import 'package:bank_transaction_app/src/common/constants/common_data.dart';
import 'package:bank_transaction_app/src/utils/tools/object_utils.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';


// ignore: must_be_immutable
abstract class BaseWidget extends StatefulWidget {

  final int color;

  BaseWidget({Key? key, this.color = ScrmColors.activeColor}) : super(key: key);

  BaseWidgetState? baseWidgetState;

  @override
  BaseWidgetState createState() {
    baseWidgetState = getState();
    return baseWidgetState!;
  }

  BaseWidgetState getState();
}

abstract class BaseWidgetState<T extends BaseWidget> extends State<T> with TickerProviderStateMixin, AutomaticKeepAliveClientMixin {
  bool _isAppBarShow = true; //导航栏是否显示

  bool _isErrorWidgetShow = false; //错误信息是否显示
  String _errorContentMesage = "网络请求失败，请检查您的网络";
  // String _errImgPath = "images/ic_error.png";

  bool _isLoadingWidgetShow = false;

  bool _isEmptyWidgetShow = false;
  String _emptyWidgetContent = "暂无数据";
  String _emptyImgPath = "icon_list_empty.png"; //自己根据需求变更
  bool hasmore = true;
  bool loadingmore = false;
  bool isScafofold = true;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    
    return isScafofold ? Scaffold(
        appBar: _getBaseAppBar(),
        body: containerBody()) : containerBody(); 
  }

  Widget containerBody() {
    return Container(child:
      // color: Colors.white, //背景颜色，可自己变更
          Stack(
            children: <Widget>[
              getContentWidget(context), 
              _getBaseErrorWidget(),
              _getBaseEmptyWidget(),
              _getBassLoadingWidget(),
            ],
          ),
        );
  }

  @override
  void dispose() {
    super.dispose();
  }

  Widget getContentWidget(BuildContext context);

  ///暴露的错误页面方法，可以自己重写定制
  Widget getErrorWidget() {
    return Container(
      color: Colors.white,
      // width: double.infinity,
      // height: double.infinity,
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Container(child:UITools.text('(>_<)', fontSize: 80, color:Color.fromARGB(255, 193, 193, 193))),
            Container(margin: EdgeInsets.fromLTRB(0, 30, 0, 20),child: UITools.text(_errorContentMesage, fontSize: 15)),
            Container(height: 55, width: 80, decoration: BoxDecoration(borderRadius: BorderRadius.circular(8)),
                padding: EdgeInsets.fromLTRB(10, 20, 10, 0),
                child: OutlinedButtonWidget(onPressed: () {onClickErrorWidget();}, title: UITools.text('重试', color: Colors.white)))
          ],
        ),
      ),
    );
  }

  Widget getLoadingWidget() {
    return Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      SpinKitWave(color: Color(widget.color), size: 30.0),
      SizedBox(height:5),
      Text('加载中...', style: TextStyle(color: Color(AppColors.TextBlack)),)]),);
  }

  Widget getLoadMoreWidget() {
    return loadingmore
        ? Container(
            child: Padding(
            padding: const EdgeInsets.only(top: 5, bottom: 5),
            child: Center(
                child: Row(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Container(
                  margin: EdgeInsets.only(right: 10),
                  child: SizedBox(
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                    ),
                    height: 12.0,
                    width: 12.0,
                  ),
                ),
                Text("加载中..."),
              ],
            )),
          ))
        : new Container(
            child: hasmore
                ? SizedBox.shrink()
                : Center(
                    child: Container(
                        margin: EdgeInsets.only(top: 5, bottom: 5),
                        child: Text(
                          "",
                          style: TextStyle(fontSize: 14, color: Colors.grey),
                        ))),
          );
  }

  Widget getEmptyWidget() {
    return Container(
      padding: EdgeInsets.fromLTRB(0, 0, 0, 30),
      color: Colors.white,
      // width: double.infinity,
      // height: double.infinity,
      child: Center(
        child: Container(
          alignment: Alignment.center,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Image.asset(CommonData.ASSETS_IMG + _emptyImgPath, width:100, height:100),
              Container(
                margin: EdgeInsets.fromLTRB(0, 10, 0, 0),
                child: Text(_emptyWidgetContent,
                    style: TextStyle(
                      fontSize: 16,
                      color: Colors.black54,
                    )),
              )
            ],
          ),
        ),
      ),
    );
  }

  PreferredSizeWidget _getBaseAppBar() {
    return PreferredSize(
        child: Offstage(
          offstage: !_isAppBarShow && isScafofold, 
          child: getAppBar(),
        ),
        preferredSize: Size.fromHeight(50));
  }

  ///导航栏 appBar
  AppBar getAppBar();

  Widget _getBaseErrorWidget() {
    return Offstage(
      offstage: !_isErrorWidgetShow,
      child: getErrorWidget(),
    );
  }

  Widget _getBassLoadingWidget() {
    return Offstage(
      offstage: !_isLoadingWidgetShow,
      child: getLoadingWidget(),
    );
  }

  Widget _getBaseEmptyWidget() {
    return Offstage(
      offstage: !_isEmptyWidgetShow,
      child: getEmptyWidget(),
    );
  }

  ///点击错误页面后展示内容
  void onClickErrorWidget();

  ///设置错误提示信息
  void setErrorContent(String content) {
    if (ObjectUtils.isNotEmpty(content)) {
      if (mounted) {setState(() {
        _errorContentMesage = content;
      });}
    }
  }

  ///设置导航栏隐藏或者显示
  void setAppBarVisible(bool isVisible) {
    if (mounted) {setState(() {
      _isAppBarShow = isVisible;
    });}
  }

  void showContent() {
    if (mounted) {setState(() {
      _isEmptyWidgetShow = false;
      _isLoadingWidgetShow = false;
      _isErrorWidgetShow = false;
    });}
  }

  void showloading() {
    if (mounted) {setState(() {
      _isEmptyWidgetShow = false;
      _isLoadingWidgetShow = true;
      _isErrorWidgetShow = false;
    });}
  }

  void showEmpty() {
    if (mounted) {setState(() {
      _isEmptyWidgetShow = true;
      _isLoadingWidgetShow = false;
      _isErrorWidgetShow = false;
    });}
  }

  void showError() {
    if (mounted) {setState(() {
      _isEmptyWidgetShow = false;
      _isLoadingWidgetShow = false;
      _isErrorWidgetShow = true;
    });}
  }

  ///设置空页面内容
  void setEmptyWidgetContent(String content) {
    if (ObjectUtils.isNotEmpty(content)) {
      if (mounted) {setState(() {
        _emptyWidgetContent = content;
      });}
    }
  }

  ///设置错误页面图片
  void setErrorImage(String imagePath) {
    if (ObjectUtils.isNotEmpty(imagePath)) {
      // setState(() {
        // _errImgPath = imagePath;
      // });
    }
  }

  ///设置空页面图片
  void setEmptyImage(String? imagePath) {
    if (imagePath != null) {
      setState(() {
        _emptyImgPath = imagePath;
      });
    }
  }

  @override
  bool get wantKeepAlive => false;
}
