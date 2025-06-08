import 'package:json_annotation/json_annotation.dart';
part 'trans_query.g.dart';

@JsonSerializable()
class TransQuery {
  TransQuery(
      {
      this.page,
      this.pageSize,
      });

  int? page = 1;

  int? pageSize = 10;

  factory TransQuery.fromJson(Map<String, dynamic> json) =>
      _$TransQueryFromJson(json);
  Map<String, dynamic> toJson() => _$TransQueryToJson(this);
}
