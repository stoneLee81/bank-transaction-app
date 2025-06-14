import 'package:bank_transaction_app/src/utils/http/transformer.dart';
import 'package:dio/dio.dart';
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:bank_transaction_app/src/utils/http/interceptor.dart';
import 'package:bank_transaction_app/src/utils/http/processor.dart';

class DioUtil {
  static final DioUtil _singleton = DioUtil._init();
  // bool _isDebug = ConstantsData.ISDEBUG;
  // bool _isDebug = !bool.fromEnvironment("dart.vm.product");
  Dio? _dio;
  // 默认配置
  BaseOptions _baseOptions = BaseOptions(
    // contentType: ContentType.parse("application/x-www-form-urlencoded"),
    connectTimeout: Duration(seconds:20),
    receiveTimeout: Duration(seconds:20),
    sendTimeout: Duration(seconds: 20),
    maxRedirects: 3,
    // 添加Web特定的配置
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
    },
    // 设置响应类型为JSON
    responseType: ResponseType.json,
    // 跟随重定向
    followRedirects: true,
  );

  DioUtil._init() {
    _dio = new Dio(_baseOptions);
    _dio!.interceptors
      ..add(LogInterceptor(request: ConstantsData.ISDEBUG, requestHeader: ConstantsData.ISDEBUG,
      responseHeader: ConstantsData.ISDEBUG,
          requestBody: ConstantsData.ISDEBUG, responseBody: ConstantsData.ISDEBUG)) // Dio 日志拦截器
      // ..add(DioCacheManager(CacheConfig()).interceptor)
      ..add(MyInterceptor(ConstantsData.ISDEBUG));
    // 自定义转换器
    _dio!.transformer = new MyTransformer();
  }

  Dio getDio() {
    return _dio!;
  }

  factory DioUtil() {
    return _singleton;
  }

  static DioUtil getInstance() {
    return _singleton;
  }

  setHeaders(String clientId, String companyId, String userId, String username, int expireTime) {
    getDio().options.headers['content-Type'] = 'application/json; charset=UTF-8';
  }

  /// 添加拦截器
  addInterceptor(Interceptor interceptor) {
    _dio!.interceptors.add(interceptor);
  }

  /// 简单处理 普通数据/List数据
  request<T>(
      method, // 请求方法
      path, // 地址
      Processor<T> processor, // 数据处理器
          {data, // 请求参数
        queryParameters, // dio 2.x get参数  like: api/test?id=12&name=test
        option, // 配置参数
        cancelToken, // 取消
        onSendProgress, // 请求进度回调
        onReceiveProgress // 接手进度回调
      }) async {
    try {
      Response response = await _dio!.request(path,
          data: data,
          options: _checkOptions(method, option),
          queryParameters: queryParameters,
          onSendProgress: onSendProgress,
          cancelToken: cancelToken,
          onReceiveProgress: onReceiveProgress);
      return processor.success(response);
    } on DioError catch (e) {
      return processor.failed(e);
    }
  }

  // 选择对应的请求方法
  Options _checkOptions(method, options) {
    if (options == null) {
      options = new Options();
    }
    options.method = method;
    return options;
  }
}
