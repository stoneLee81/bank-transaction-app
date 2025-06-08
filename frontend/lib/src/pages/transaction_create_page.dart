import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/transaction.dart';
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';
import 'package:bank_transaction_app/src/widgets/outlined_button_widget.dart';
import 'package:provider/provider.dart';
import 'package:bank_transaction_app/src/provider/trans_provider.dart';

class TransactionCreatePage extends StatefulWidget {
  final Transaction? transaction;
  const TransactionCreatePage({super.key, this.transaction});

  @override
  State<TransactionCreatePage> createState() => _TransactionCreatePageState();
}

class _TransactionCreatePageState extends State<TransactionCreatePage> {
  final _formKey = GlobalKey<FormState>();
  final _accountIdController = TextEditingController();
  final _amountController = TextEditingController();
  final _remarkController = TextEditingController();
  TransactionType _selectedType = TransactionType.DEPOSIT;

  bool get isEdit => widget.transaction != null;

  @override
  void initState() {
    super.initState();
    _initializeFormData();
  }

  void _initializeFormData() {
    if (isEdit) {
      // 编辑模式：使用现有数据
      String accountId = _getAccountIdFromTransaction(widget.transaction!);
      _accountIdController.text = accountId;
      _amountController.text = widget.transaction!.amount?.toInt().toString() ?? '';
      _remarkController.text = widget.transaction!.remark ?? '';
      _selectedType = widget.transaction!.type ?? TransactionType.DEPOSIT;
    } else {
      // 创建模式：使用默认账户
      _accountIdController.text = 'ACC001';
    }
  }

  String _getAccountIdFromTransaction(Transaction transaction) {
    // 根据交易类型返回相应的账户ID
    switch (transaction.type) {
      case TransactionType.WITHDRAWAL:
        return transaction.fromAccountId ?? '';
      case TransactionType.DEPOSIT:
        return transaction.toAccountId ?? '';
      default:
        return transaction.fromAccountId ?? transaction.toAccountId ?? '';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildBody(),
    );
  }

  PreferredSizeWidget _buildAppBar() {
    return AppBar(
      title: UITools.text(isEdit ? '编辑交易' : '创建新交易', fontSize: 16, fontweight: FontWeight.bold, color: Color(ScrmColors.TextWhite)),
      backgroundColor: Colors.blue.shade700,
      foregroundColor: Colors.white,
      elevation: 0,
    );
  }

  Widget _buildBody() {
    return Padding(
      padding: const EdgeInsets.all(20.0),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildFormContainer(),
          ],
        ),
      ),
    );
  }

  Widget _buildFormContainer() {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey.shade300, width: 1),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildFormTitle(),
            const SizedBox(height: 20),
            _buildAccountIdField(),
            const SizedBox(height: 16),
            _buildTransactionTypeField(),
            const SizedBox(height: 16),
            _buildAmountField(),
            const SizedBox(height: 16),
            _buildDescriptionField(),
            const SizedBox(height: 24),
            _buildActionButtons(),
          ],
        ),
      ),
    );
  }

  Widget _buildFormTitle() {
    return UITools.text(
      isEdit ? '编辑交易' : '创建新交易',
      fontSize: 20,
      fontweight: FontWeight.bold,
    );
  }

  Widget _buildAccountIdField() {
    return TextFormField(
      controller: _accountIdController,
      enabled: false, // 账户ID不可编辑
      decoration: InputDecoration(
        labelText: '账户 ID',
        border: const OutlineInputBorder(),
        prefixIcon: const Icon(Icons.account_balance),
        filled: true,
        fillColor: Colors.grey.shade100,
      ),
    );
  }

  Widget _buildTransactionTypeField() {
    return DropdownButtonFormField<TransactionType>(
      value: _selectedType,
      decoration: InputDecoration(
        labelText: '交易类型',
        border: const OutlineInputBorder(),
        prefixIcon: const Icon(Icons.category),
        filled: isEdit,
        fillColor: isEdit ? Colors.grey.shade100 : null,
      ),
      items: _buildTransactionTypeItems(),
      onChanged: isEdit ? null : _onTransactionTypeChanged,
    );
  }

  List<DropdownMenuItem<TransactionType>> _buildTransactionTypeItems() {
    return TransactionType.values.map((type) {
      return DropdownMenuItem(
        value: type,
        child: UITools.text(_getTypeText(type)),
      );
    }).toList();
  }

  void _onTransactionTypeChanged(TransactionType? value) {
    if (value != null) {
      setState(() {
        _selectedType = value;
      });
    }
  }

  Widget _buildAmountField() {
    return TextFormField(
      controller: _amountController,
      enabled: !isEdit, // 编辑模式下不可修改
      decoration: InputDecoration(
        labelText: '金额',
        border: const OutlineInputBorder(),
        prefixIcon: const Icon(Icons.attach_money),
        suffixText: '¥',
        filled: isEdit,
        fillColor: isEdit ? Colors.grey.shade100 : null,
      ),
      keyboardType: TextInputType.number,
      inputFormatters: [
        FilteringTextInputFormatter.digitsOnly, // 只允许输入数字
      ],
      validator: _validateAmount,
    );
  }

  String? _validateAmount(String? value) {
    if (value == null || value.isEmpty) {
      return '请输入金额';
    }
    int? amount = int.tryParse(value);
    if (amount == null) {
      return '请输入有效的整数金额';
    }
    if (amount <= 0) {
      return '金额必须大于0';
    }
    return null;
  }

  Widget _buildDescriptionField() {
    return TextFormField(
      controller: _remarkController,
      decoration: const InputDecoration(
        labelText: '描述',
        border: OutlineInputBorder(),
        prefixIcon: Icon(Icons.description),
      ),
      maxLines: 3,
    );
  }

  Widget _buildActionButtons() {
    return Row(
      children: [
        Expanded(child: _buildSubmitButton()),
        const SizedBox(width: 16),
        Expanded(child: _buildSecondaryButton()),
      ],
    );
  }
  
  Widget _buildSubmitButton() {
    return OutlinedButtonWidget(
      height: 48,
      backgroundColor: Colors.blue.shade700,
      borderSide: BorderSide.none,
      onPressed: _submitForm,
      title: UITools.text(
        isEdit ? '更新' : '提交',
        color: Colors.white,
        fontweight: FontWeight.w500,
      ),
    );
  }

  Widget _buildSecondaryButton() {
    return OutlinedButtonWidget(
      height: 48,
      backgroundColor: Colors.white,
      borderSide: BorderSide(color: Colors.grey.shade400),
      onPressed: isEdit ? _cancelEdit : _resetForm,
      title: UITools.text(
        isEdit ? '取消' : '重置',
        color: Colors.grey.shade700,
        fontweight: FontWeight.w500,
      ),
    );
  }

  String _getTypeText(TransactionType type) {
    switch (type) {
      case TransactionType.WITHDRAWAL: return '取款';
      case TransactionType.DEPOSIT: return '存款';
    }
  }

  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      Transaction newTransaction = _createTransaction();
      
      final transProvider = Provider.of<TransProvider>(context, listen: false);
      
      if (isEdit) {
        // 更新交易
        transProvider.updateTransaction(newTransaction).then((value) {
          if(value.status == '200') {
            _showSuccessMessage();
            Navigator.pop(context, newTransaction);
          } else {
            _showErrorMessage(value.message);
          }
        });
      } else {
        // 创建新交易
        transProvider.createTransaction(newTransaction).then((value) {
          if(value.status == '200') {
            _showSuccessMessage();
            Navigator.pop(context, newTransaction);
          } else {
            _showErrorMessage(value.message);
          }
        });
      }
    }
  }

  Transaction _createTransaction() {
    String accountId = _accountIdController.text;
    
    return Transaction(
      id: widget.transaction?.id,
      fromAccountId: _selectedType == TransactionType.WITHDRAWAL ? accountId : null,
      toAccountId: _selectedType == TransactionType.DEPOSIT ? accountId : null,
      type: _selectedType,
      amount: double.parse(_amountController.text),
      remark: _remarkController.text,
      timestamp: widget.transaction?.timestamp,
      status: widget.transaction?.status,
      currency: Currency.CNY, // 添加必需的货币字段
      channel: 'ONLINE', // 添加必需的渠道字段
    );
  }

  void _showSuccessMessage() {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: UITools.text(
          isEdit ? '交易已更新' : '交易已创建',
          color: Colors.white,
        ),
        backgroundColor: Colors.green,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
      ),
    );
  }

  void _showErrorMessage(String errMessage) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: UITools.text(
          errMessage,
          color: Colors.white,
        ),
        backgroundColor: Colors.red,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
      ),
    );
  }

  void _resetForm() {
    _formKey.currentState!.reset();
    _accountIdController.text = 'ACC001'; // 重置时保持默认账户
    _amountController.clear();
    _remarkController.clear();
    setState(() {
      _selectedType = TransactionType.DEPOSIT;
    });
  }

  void _cancelEdit() {
    Navigator.pop(context);
  }

  @override
  void dispose() {
    _accountIdController.dispose();
    _amountController.dispose();
    _remarkController.dispose();
    super.dispose();
  }
} 