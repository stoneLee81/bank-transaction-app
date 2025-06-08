import 'package:bank_transaction_app/src/provider/trans_value_provider.dart';
import 'package:bank_transaction_app/src/utils/date_util.dart';
import 'package:bank_transaction_app/src/widgets/basic/base_widget.dart';
import 'package:bank_transaction_app/src/widgets/basic/paginated.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/transaction.dart';
import 'package:bank_transaction_app/src/common/constants/constants_data.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';
import 'package:bank_transaction_app/src/widgets/outlined_button_widget.dart';
import 'package:bank_transaction_app/src/provider/trans_provider.dart';
import 'package:bank_transaction_app/src/utils/http/dio_util.dart';
import 'package:bank_transaction_app/src/models/trans_query.dart';
import 'package:bank_transaction_app/src/utils/http/processor.dart';
import 'transaction_create_page.dart';

// ignore: must_be_immutable
class TransactionListPage extends BaseWidget {
  TransactionListPage({Key? key}) : super(key: key);

  @override
  BaseWidgetState<BaseWidget> getState() {
    return _ChannelQrcodeListState();
  }
}

class _ChannelQrcodeListState extends BaseWidgetState<TransactionListPage> {
  int _rowsPerPage = 20;
  int _total = -1;
  bool _showpage = true;
  int backgroundColor = 0xfffafafa;
  final ScrollController _controller = new ScrollController();

  @override
  void initState() {
    super.isScafofold = false;
    super.initState();
    
    _controller.addListener(() {
    });
    
    setAppBarVisible(true);
    _getData(page:1, pageSize: _rowsPerPage);
  }

  @override
  void dispose() {
    _controller.dispose(); // 避免内存泄漏
    super.dispose();
  }
  
  Future<Null> _getData({int page = 1, int pageSize = 20, bool showLoading = false}) async {
    if(Provider.of<TransValueProvider>(context, listen: false).transList == null || showLoading) {
      _showpage = false;
      showloading();
    }

    TransQuery _transQuery = TransQuery(page:page, pageSize: pageSize);

    Provider.of<TransProvider>(context, listen: false).getTransData(_transQuery).then((value) {
      if (value.status == '200') { 
        _total = value.total;
        _showpage = true;
        Provider.of<TransValueProvider>(context, listen:false).changeTransList(value.data);
        showContent();
      }else {
        showError();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);

    return Material(child: Scaffold(backgroundColor: Colors.white, appBar: UITools.appBar2('所有交易'), body:super.containerBody()));  
  }

  @override
  Widget getContentWidget(BuildContext context) {
    return Selector<TransValueProvider, List<Transaction>>(
      shouldRebuild: (previous, next) => previous != next, 
      selector: (BuildContext context, TransValueProvider valueProvider) => List.from(valueProvider.transList??List.empty(growable: true)),
      builder: (context, result, child) {
        
        if(!_showpage) return SizedBox.shrink();

        return Scrollbar(child: Container(padding: const EdgeInsets.only(left:16,right:16), child:CustomScrollView(controller: _controller, slivers: <Widget>[
            SliverList(delegate: SliverChildBuilderDelegate((content, index) {return _buildTransactionTable(result);}, childCount: 1)),
            SliverToBoxAdapter(child: this.getPaginate()),
          ]))
        );
    });
  }

  @override
  AppBar getAppBar() {
    return AppBar(
      title: Text('不显示'),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.receipt_long, size: 64, color: Colors.grey.shade400),
          const SizedBox(height: 16),
          UITools.text('暂无交易记录', fontSize: 16, color: Colors.grey.shade600),
        ],
      ),
    );
  }

  Widget _buildTransactionTable(List<Transaction> transactions) {
    if (transactions.isEmpty) {
      return _buildEmptyState();
    }
    
    return Column(
      children: [
        // 表头
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          decoration: BoxDecoration(
            color: Colors.grey.shade100,
            border: Border.all(color: Colors.grey.shade300, width: 1),
          ),
          child: Row(
            children: [
              Expanded(flex: 2, child: UITools.text('编号', fontweight: FontWeight.bold)),
              Expanded(flex: 2, child: UITools.text('账户ID', fontweight: FontWeight.bold)),
              Expanded(flex: 1, child: UITools.text('类型', fontweight: FontWeight.bold)),
              Expanded(flex: 2, child: UITools.text('金额', fontweight: FontWeight.bold)),
              Expanded(flex: 1, child: UITools.text('状态', fontweight: FontWeight.bold)),
              Expanded(flex: 2, child: UITools.text('日期', fontweight: FontWeight.bold)),
              Expanded(flex: 3, child: UITools.text('操作', fontweight: FontWeight.bold)),
            ],
          ),
        ),
        // 数据行
        ...transactions.map((transaction) => _buildTransactionRow(transaction)).toList(),
      ],
    );
  }

  Widget _buildTransactionRow(Transaction transaction) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        border: Border(
          left: BorderSide(color: Colors.grey.shade300, width: 1),
          right: BorderSide(color: Colors.grey.shade300, width: 1),
          bottom: BorderSide(color: Colors.grey.shade300, width: 1),
        ),
      ),
      child: Row(
        children: [
          Expanded(flex: 2, child: UITools.text(transaction.id ?? '')),
          Expanded(flex: 2, child: UITools.text(_getAccountId(transaction))),
          Expanded(flex: 1, child: UITools.text(_getTypeText(transaction.type))),
          Expanded(flex: 2, child: UITools.text('¥${transaction.amount?.toStringAsFixed(2) ?? '0.00'}')),
          Expanded(flex: 1, child: _buildStatusChip(transaction.status)),
          Expanded(flex: 2, child: UITools.text(_formatDate(transaction.timestamp))),
          Expanded(flex: 3, child: _buildActionButtons(transaction)),
        ],
      ),
    );
  }

  Widget _buildStatusChip(TransactionStatus? status) {
    Color color;
    String text;
    switch (status) {
      case TransactionStatus.COMPLETED:
        color = Colors.green;
        text = '成功';
        break;
      case TransactionStatus.PENDING:
        color = Colors.orange;
        text = '待处理';
        break;
      case TransactionStatus.FAILED:
        color = Colors.red;
        text = '失败';
        break;
      default:
        color = Colors.grey;
        text = '未知';
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(2),
      ),
      child: UITools.text(text, fontSize: 12, color: Colors.white),
    );
  }

  Widget _buildActionButtons(Transaction transaction) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        _buildActionButton('查看', () => _viewTransaction(transaction)),
        _buildActionButton('编辑', () => _editTransaction(transaction)),
        // _buildActionButton('删除', () => _deleteTransaction(transaction), isDelete: true),
      ],
    );
  }

  Widget _buildActionButton(String text, VoidCallback onPressed, {bool isDelete = false}) {
    return OutlinedButtonWidget(
      height: 28,
      backgroundColor: Colors.white,
      borderSide: BorderSide.none,
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      margin: const EdgeInsets.only(right: 4),
      onPressed: onPressed,
      title: UITools.text(
        text,
        fontSize: 12,
        color: isDelete ? Colors.red : Colors.blue,
      ),
    );
  }

  String _getAccountId(Transaction transaction) {
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

  String _getTypeText(TransactionType? type) {
    switch (type) {
      case TransactionType.WITHDRAWAL: return '取款';
      case TransactionType.DEPOSIT: return '存款';
      default: return '未知';
    }
  }

  String _formatDate(DateTime? date) {  
    if (date == null) return '';
    return DateUtil.formatDate(date, format:DateFormats.full);
  }

  void _viewTransaction(Transaction transaction) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
        title: UITools.text('交易详情', fontweight: FontWeight.bold),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            UITools.text('交易ID: ${transaction.id}'),
            const SizedBox(height: 8),
            UITools.text('账户ID: ${_getAccountId(transaction)}'),
            const SizedBox(height: 8),
            UITools.text('交易类型: ${_getTypeText(transaction.type)}'),
            const SizedBox(height: 8),
            UITools.text('金额: ¥${transaction.amount?.toStringAsFixed(2)}'),
            const SizedBox(height: 8),
            UITools.text('状态: ${_getStatusText(transaction.status)}'),
            const SizedBox(height: 8),
            UITools.text('描述: ${transaction.remark ?? '无'}'),
            const SizedBox(height: 8),
            UITools.text('创建时间: ${_formatDate(transaction.timestamp)}'),
          ],
        ),
        actions: [
          OutlinedButtonWidget(
            height: 36,
            backgroundColor: Colors.white,
            borderSide: BorderSide(color: Colors.grey.shade400),
            onPressed: () => Navigator.pop(context),
            title: UITools.text('关闭'),
          ),
        ],
      ),
    );
  }

  String _getStatusText(TransactionStatus? status) {
    switch (status) {
      case TransactionStatus.COMPLETED: return '成功';
      case TransactionStatus.PENDING: return '待处理';
      case TransactionStatus.FAILED: return '失败';
      default: return '未知';
    }
  }

  void _editTransaction(Transaction transaction) async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => TransactionCreatePage(transaction: transaction)),
    );
    
    if (result != null) {
      // 刷新列表
      _getData(page: 1, pageSize: _rowsPerPage, showLoading: true);
    }
  }

  void _deleteTransaction(Transaction transaction) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
        title: UITools.text('确认删除', fontweight: FontWeight.bold),
        content: UITools.text('确定要删除交易 ${transaction.id} 吗？'),
        actions: [
          OutlinedButtonWidget(
            height: 36,
            backgroundColor: Colors.white,
            borderSide: BorderSide(color: Colors.grey.shade400),
            onPressed: () => Navigator.pop(context),
            title: UITools.text('取消'),
          ),
          const SizedBox(width: 8),
          OutlinedButtonWidget(
            height: 36,
            backgroundColor: Colors.red,
            borderSide: BorderSide.none,
            onPressed: () => _confirmDelete(transaction),
            title: UITools.text('删除', color: Colors.white),
          ),
        ],
      ),
    );
  }

  void _confirmDelete(Transaction transaction) async {
    Navigator.pop(context); // 关闭确认对话框
    
    try {
      BaseResp<bool> resp = await Provider.of<TransProvider>(context, listen: false)
          .deleteTransaction(transaction.id ?? '');
      
      if (resp.status == '200') {
        // 删除成功，刷新列表
        _getData(page: 1, pageSize: _rowsPerPage, showLoading: true);
      }
    } catch (e) {
      // 删除失败的处理已经在 Provider 中处理了
    }
  }
  
  Widget getPaginate() {
    return PaginatedWidget(
      onGetDataCallback: (page, pageSize) {_getData(page: page, pageSize: pageSize);},
      rowsPerPage: _rowsPerPage,
      onRowsPerPageChanged: (v) {
        this._rowsPerPage = v!;
        _getData(page: 1, pageSize: _rowsPerPage);
      },
      total: _total,
    );
  }
  
  @override
  void onClickErrorWidget() {
    _getData(page:1, pageSize: _rowsPerPage);
  }
} 