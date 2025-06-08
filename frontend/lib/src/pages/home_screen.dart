import 'package:bank_transaction_app/src/common/style/style.dart';
import 'package:flutter/material.dart';
import 'package:bank_transaction_app/src/utils/uitools.dart';
import 'transaction_list_page.dart';
import 'transaction_create_page.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildBody(),
    );
  }

  PreferredSizeWidget _buildAppBar() {
    return AppBar(
      title: UITools.text('银行交易管理系统', fontSize: 16, fontweight: FontWeight.bold, color: Color(ScrmColors.TextWhite)),
      backgroundColor: Colors.blue.shade700,
      foregroundColor: Colors.white,
      elevation: 0,
    );
  }

  Widget _buildBody() {
    return Padding(
      padding: const EdgeInsets.all(20.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildWelcomeTitle(),
          const SizedBox(height: 40),
          _buildActionCards(),
        ],
      ),
    );
  }

  Widget _buildWelcomeTitle() {
    return UITools.text(
      '欢迎使用银行交易管理系统',
      fontSize: 24,
      fontweight: FontWeight.bold,
      textAlign: TextAlign.center,
    );
  }

  Widget _buildActionCards() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        _buildActionCard(
          icon: Icons.add_circle,
          title: '➕ 创建新交易',
          subtitle: '添加新的银行交易记录',
          color: Colors.green,
          onTap: _navigateToCreatePage,
        ),
        _buildActionCard(
          icon: Icons.list_alt,
          title: '📄 所有交易',
          subtitle: '查看和管理交易记录',
          color: Colors.blue,
          onTap: _navigateToListPage,
        ),
      ],
    );
  }

  Widget _buildActionCard({
    required IconData icon,
    required String title,
    required String subtitle,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Expanded(
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 8),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey.shade300, width: 1),
          borderRadius: BorderRadius.circular(4),
        ),
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(4),
          child: Container(
            padding: const EdgeInsets.all(20),
            height: 150,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(icon, size: 48, color: color),
                const SizedBox(height: 12),
                UITools.text(title, fontSize: 16, fontweight: FontWeight.bold),
                const SizedBox(height: 8),
                UITools.text(
                  subtitle,
                  fontSize: 12,
                  color: Colors.grey.shade600,
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _navigateToCreatePage() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const TransactionCreatePage()),
    );
  }

  void _navigateToListPage() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => TransactionListPage()),
    );
  }
}
 