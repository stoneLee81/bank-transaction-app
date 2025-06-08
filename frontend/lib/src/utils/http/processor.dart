import 'dart:convert';
import 'package:dio/dio.dart';

/// 对象生成方法
typedef ObjGenerateFun<T> = T Function(Map<String, dynamic>);

/// 数据处理器
abstract class Processor<T> {
  Processor(this.isList, {required this.objGenerateFun});
  bool isList;
  ObjGenerateFun<T> objGenerateFun;
  // 成功数据处理
  success(Response response) => response;
  // 失败数据处理
  failed(DioError err) => err;
}

/// 定义 普通数据格式
class BaseResp<T> {
  String? status;
  String? code;
  String? errorCode;
  String message;
  T data;
  int? page = 1;
  int? pageSize = 10;
  int total = 0;
  int? maxPage;
  BaseResp(this.status, this.code, this.message, this.data, {this.page, this.pageSize, this.total = 0, this.maxPage});

  String toString() {
    StringBuffer sb = new StringBuffer('{');
    sb.write("\"status\":\"$status\"");
    sb.write(",\"code\":$code");
    sb.write(",\"errorCode\":\"$errorCode\"");
    sb.write(",\"message\":\"$message\"");
    sb.write(",\"data\":$data");
    sb.write(",\"page\":\"$page\"");
    sb.write(",\"pageSize\":\"$pageSize\"");
    sb.write(",\"total\":\"$total\"");
    sb.write(",\"maxPage\":\"$maxPage\"");
    sb.write('}');
    return sb.toString();
  }
}

/// 定义 List数据格式
class BaseRespList<T> {

  String? status;
  String? code;
  String? errorCode;
  String message;
  List<T> data;
  int? page = 1;
  int? pageSize = 10;
  int total = 0;
  int? maxPage;
  
  BaseRespList(this.status, this.code, this.message, this.data, {this.page, this.pageSize, this.total = 0, this.maxPage});

  @override
  String toString() {
    StringBuffer sb = new StringBuffer('{');
    sb.write("\"status\":\"$status\"");
    sb.write(",\"code\":$code");
    sb.write(",\"errorCode\":$errorCode");
    sb.write(",\"message\":\"$message\"");
    sb.write(",\"data\":\"$data\"");
    sb.write(",\"page\":\"$page\"");
    sb.write(",\"pageSize\":\"$pageSize\"");
    sb.write(",\"total\":\"$total\"");
    sb.write(",\"maxPage\":\"$maxPage\"");
    sb.write('}');
    return sb.toString();
  }
}

/// 实现默认处理器
class BaseProcessor<T> extends Processor<T> {
  BaseProcessor(bool isList, {required ObjGenerateFun<T> fun})
      : super(isList, objGenerateFun: fun);

  @override
  ObjGenerateFun<T> get objGenerateFun => super.objGenerateFun;

  // 转换json
  Map<String, dynamic> decodeData(Response? response) {
    if (response == null ||
        response.data == null ||
        response.data.toString().isEmpty) {
      return new Map();
    }
    return json.decode(response.data.toString());
  }

  @override
  success(Response response) {
    String _status = response.statusCode.toString();
    String _code = '';
    String _msg = '';
    int _page = 0, _pageSize = 10, _total = 0, _maxPage = 0;

    // List数据处理 [{...},{...}]
    if (isList) {
      List<T> _data = <T>[];
      if (response.data is Map) {
        // 处理后端PageInfo格式
        if (response.data.containsKey('items') || response.data.containsKey('list')) {
          // 后端返回的是PageInfo格式
          _code = '200';
          _msg = 'success';
          _page = response.data['page'] ?? 0;
          _pageSize = response.data['pageSize'] ?? 10;
          _maxPage = response.data['maxPage'] ?? 0;
          _total = response.data['total'] ?? 0;

          // 优先使用items，如果没有则使用list
          List? dataList = response.data['items'] ?? response.data['list'];

        if (T.toString() == 'dynamic') {
            _data = (dataList ?? []).cast<T>(); 
          } else {
            if (dataList != null && dataList.isNotEmpty) {
              _data = dataList.map<T>((v) => objGenerateFun(v)).toList();
            }
          }
        } else {
          // 标准格式
          _code = response.data['code'] ?? '';
          _msg = response.data['message'] ?? '';
          _page = response.data['page'] ?? 0;
          _pageSize = response.data['pageSize'] ?? 10;
          _maxPage = response.data['maxPage'] ?? 0;
          _total = response.data['total'] ?? 0;

          if (T.toString() == 'dynamic') {
            _data = (response.data['data'] ?? []).cast<T>(); 
        } else {
          if (response.data['data'] != null) {
            _data = (response.data['data'] as List)
                .map<T>((v) => objGenerateFun(v)) 
                .toList();
            }
          }
        }
      } else {
        Map<String, dynamic> _dataMap = decodeData(response);
        _code = _dataMap['code'] ?? '';
        _msg = _dataMap['message'] ?? '';
        _page = _dataMap['page'] ?? 0;
        _pageSize = _dataMap['pageSize'] ?? 10;
        _maxPage = _dataMap['maxPage'] ?? 0;
        _total = _dataMap['total'] ?? 0;

        if (T.toString() == 'dynamic') {
          _data = (_dataMap['data'] ?? []).cast<T>();
        } else {
          if (objGenerateFun == null) {
            throw Exception('you need add ObjGenerateFun or remove T');
          }
          if (_dataMap['data'] != null) {
            _data = (_dataMap['data'] as List)
                .map<T>((v) => objGenerateFun(v))
                .toList();
          }
        }
      }
      return BaseRespList(_status, _code, _msg, _data, page:_page, pageSize:_pageSize, total:_total, maxPage:_maxPage);
    } else {
      // 普通数据处理 {...}
      T _data;
      if (response.data is Map) {
        _code = response.data['code'] ?? '';
        _msg = response.data['message'] ?? '';
        _page = response.data['page'] ?? 0;
        _pageSize = response.data['pageSize'] ?? 10;
        _maxPage = response.data['maxPage'] ?? 0;
        _total = response.data['total'] ?? 0;

        if (T.toString() == 'dynamic') {
          _data = response.data['data'];
        } else {
          // 如果没有data字段，直接使用response.data
          var dataToProcess = response.data['data'] ?? response.data;
          _data = objGenerateFun(dataToProcess);
        }
      } else {
        Map<String, dynamic> _dataMap = decodeData(response);
        _code = _dataMap['code'] ?? '';
        _msg = _dataMap['message'] ?? '';
        _page = _dataMap['page'] ?? 0;
        _pageSize = _dataMap['pageSize'] ?? 10;
        _maxPage = _dataMap['maxPage'] ?? 0;
        _total = _dataMap['total'] ?? 0;

        if (T.toString() == 'dynamic') {
          _data = _dataMap['data'];
        } else {
          // 如果没有data字段，直接使用_dataMap
          var dataToProcess = _dataMap['data'] ?? _dataMap;
          _data = objGenerateFun(dataToProcess);
        }
      }
      return BaseResp(_status, _code, _msg, _data, page:_page, pageSize:_pageSize, total:_total, maxPage:_maxPage);
    }
  }

  @override
  failed(DioError? err) {
    String _status = err?.response?.statusCode?.toString()??'400';
    String _msg = err?.response?.statusMessage??'其它异常';

    if (err != null && (int.tryParse(_status) ?? 0) == 401 && err.requestOptions.path.contains('tomatotrip')) {
      // CommonData.eventBus.fire(LogoutEvent());
    }

    if (isList) {
      return BaseRespList(_status, '-1', _msg, <T>[]);
    }
    return BaseResp(_status, '-1', _msg, null);
  }
}
