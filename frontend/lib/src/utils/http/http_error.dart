
import 'package:dio/dio.dart';

/// @desc  网络请求错误
class HttpError {
  ///HTTP 状态码
  static const int UNAUTHORIZED = 401;
  static const int FORBIDDEN = 403;
  static const int NOT_FOUND = 404;
  static const int REQUEST_TIMEOUT = 408;
  static const int INTERNAL_SERVER_ERROR = 500;
  static const int BAD_GATEWAY = 502;
  static const int SERVICE_UNAVAILABLE = 503;
  static const int GATEWAY_TIMEOUT = 504;

  ///未知错误
  static const String UNKNOWN = 'UNKNOWN';

  ///解析错误
  static const String PARSE_ERROR = 'PARSE_ERROR';

  ///网络错误
  static const String NETWORK_ERROR = 'NETWORK_ERROR';

  ///协议错误
  static const String HTTP_ERROR = 'HTTP_ERROR';

  ///证书错误
  static const String SSL_ERROR = 'SSL_ERROR';

  ///连接超时
  static const String CONNECT_TIMEOUT = 'CONNECT_TIMEOUT';

  ///响应超时
  static const String RECEIVE_TIMEOUT = 'RECEIVE_TIMEOUT';

  ///发送超时
  static const String SEND_TIMEOUT = 'SEND_TIMEOUT';

  ///网络请求取消
  static const String CANCEL = 'CANCEL';

  String code;

  String message;

  HttpError(this.code, this.message);

  dioError(DioException error) {
    message = error.message??'';
    switch (error.type) {
      case DioExceptionType.connectionError:
        code = CONNECT_TIMEOUT;
        message = '网络连接超时，请检查网络设置';
        break;
      case DioExceptionType.receiveTimeout:
        code = RECEIVE_TIMEOUT;
        message = '服务器异常，请稍后重试！';
        break;
      case DioExceptionType.sendTimeout:
      case DioExceptionType.connectionTimeout:
        code = SEND_TIMEOUT;
        message = '网络连接超时，请检查网络设置';
        break;
      case DioExceptionType.badResponse:
        code = HTTP_ERROR;
        message = '服务器异常，请稍后重试！';
        break;
      case DioExceptionType.cancel:
        code = CANCEL;
        message = '请求已被取消，请重新请求';
        break;
      case DioExceptionType.unknown:
        code = UNKNOWN;
        message = '网络异常，请稍后重试！';
        break;
      case DioExceptionType.badCertificate:
        break;
    }
  }

  @override
  String toString() {
    return 'HttpError{code: $code, message: $message}';
  }
}
