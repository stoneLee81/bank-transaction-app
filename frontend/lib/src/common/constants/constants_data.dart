
class ConstantsData {
  
  static const env = String.fromEnvironment('tool_env', defaultValue: 'sit'); //prod, sit, dev

  static const bool ISDEBUG = bool.fromEnvironment('tool_debug', defaultValue: false);

  static const baseUrl = 'http://localhost:8080/api/';

  static const double appbarHeight = 45.0;

  static bool useMa = false;
}


enum AccountStatus { ACTIVE, INACTIVE, SUSPENDED, CLOSED }

enum Currency { CNY, USD, EUR, JPY, HKD, GBP }

enum TransactionType { DEPOSIT, WITHDRAWAL }

enum TransactionStatus { PENDING, COMPLETED, FAILED, CANCELLED, processing } 