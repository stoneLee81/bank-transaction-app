 class BaseResp<T> {
  String status;
  String code;
  String? errorCode;
  String message;
  T data;
  int page = 1;
  int pageSize = 10;
  int total = 1;
  int maxPage = 1;
  BaseResp(this.status, this.code, this.message, this.data, {this.page = 1, this.pageSize = 10, this.total = 0, this.maxPage = 10});

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
