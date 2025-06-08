
import 'package:dio/dio.dart';
// import 'package:universal_io/io.dart';

class MyInterceptor extends Interceptor {
  bool isDebug;

  MyInterceptor(this.isDebug);

  @override
  onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    return handler.next(options);
    // return options;
  }

  @override
  onResponse(Response response, ResponseInterceptorHandler handler,) async {
    handler.next(response);
    // return response;
  }

  Map<String, dynamic> _decodeData(Response? response) {
    if (response == null ||
        response.data == null ||
        response.data.toString().isEmpty) {
      return new Map();
    }
    // return json.decode(response.data.toString());
    return response.data;
  }

  @override
  Future onError(DioException err, ErrorInterceptorHandler handler) async {
    try {
      String message = err.message??'';
      switch (err.response!.statusCode) {
        case 400: //HttpStatus.badRequest: // 400
          err.response!.data = _decodeData(err.response!);
          message = err.response!.data['message'] ?? '请求失败，请联系我们';
          break;
        case 401: //HttpStatus.unauthorized: // 401
          err.response!.data = _decodeData(err.response!);
          message = err.response!.data['message'] ?? '未授权，请登录';
          break;
        case 403://HttpStatus.forbidden: // 403
          message = '拒绝访问';
          break;
        case 404://HttpStatus.notFound: // 404
          message = '请求地址出错';
          break;
        case 408://HttpStatus.requestTimeout: // 408
          message = '请求超时';
          break;
        case 500://HttpStatus.internalServerError: // 500
          message = '服务器内部错误';
          break;
        case 501://HttpStatus.notImplemented: // 501
          message = '服务未实现';
          break;
        case 502://HttpStatus.badGateway: // 502
          message = '网关错误';
          break;
        case 503://HttpStatus.serviceUnavailable: // 503
          message = '服务不可用';
          break;
        case 504://HttpStatus.gatewayTimeout: // 504
          message = '网关超时';
          break;
      }
      err.response!.statusMessage = message;
      // return err;
      return super.onError(err, handler);
    } on TypeError {
      err.  response!.statusMessage = '信息转换错误';
      return super.onError(err, handler);
    } catch (e) {
      err.response!.statusMessage = '未知错误';
      return super.onError(err, handler);
    }
  }
}
