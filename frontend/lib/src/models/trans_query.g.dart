// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'trans_query.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TransQuery _$TransQueryFromJson(Map<String, dynamic> json) => TransQuery(
      page: (json['page'] as num?)?.toInt(),
      pageSize: (json['pageSize'] as num?)?.toInt(),
    );

Map<String, dynamic> _$TransQueryToJson(TransQuery instance) =>
    <String, dynamic>{
      'page': instance.page,
      'pageSize': instance.pageSize,
    };
