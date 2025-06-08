import 'package:flutter/material.dart';
import 'dart:math' as math;

// ignore: camel_case_types
typedef void onGetData(int page, int pageSize);

class PaginatedWidget<T> extends StatefulWidget {
  final int total;
  final int? page;
  final List<DropdownMenuItem<T>>? items; 
  final ValueChanged<int>? onPageChanged;
  final int rowsPerPage;
  final List<int> pageNums;
  final bool autoPaginate;
  final ValueChanged<int?>? onRowsPerPageChanged;
  final onGetData? onGetDataCallback;
  final Color backgroundColor;
  final double elevation;

  const PaginatedWidget({Key? key, this.total = 0, this.page, this.backgroundColor = Colors.white,  this.onGetDataCallback, this.autoPaginate = true, this.items, this.pageNums = const [10, 25, 50], this.onPageChanged, this.rowsPerPage = 20, this.onRowsPerPageChanged, this.elevation = 0.0}) : super(key: key);
 
  @override
  _PaginatedWidgetState createState() => _PaginatedWidgetState();
}

class _PaginatedWidgetState extends State<PaginatedWidget> {
  int _firstRowIndex = 0;
  int _totalPages = 1;
  int get _currentPage => _firstRowIndex == 0 ? 1 : (_firstRowIndex / widget.rowsPerPage).ceil() + 1;

  @override
  void initState() {
    super.initState();
    if(widget.page != null && widget.page == 1) _firstRowIndex = 0;
    _calculateTotalPages();
    
    // 确保有一个初始选中的每页显示条数
    _ensureValidRowsPerPage();
  }

  @override
  void didUpdateWidget(PaginatedWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.total != oldWidget.total || widget.rowsPerPage != oldWidget.rowsPerPage) {
      _calculateTotalPages();
    }
    if(widget.page != null && widget.page == 1) _firstRowIndex = 0;
    
    // 每次更新时确保有效的每页显示条数
    _ensureValidRowsPerPage();
  }

  void _calculateTotalPages() {
    _totalPages = (widget.total / widget.rowsPerPage).ceil();
    if (_totalPages < 1) _totalPages = 1;
  }

  // 确保rowsPerPage是有效的，如果不在pageNums中，则选择最近的一个
  void _ensureValidRowsPerPage() {
    if (!widget.pageNums.contains(widget.rowsPerPage)) {
      // 查找最接近当前rowsPerPage的值
      int closestValue = widget.pageNums.first;
      int minDifference = (widget.rowsPerPage - closestValue).abs();
      
      for (int size in widget.pageNums) {
        int difference = (widget.rowsPerPage - size).abs();
        if (difference < minDifference) {
          minDifference = difference;
          closestValue = size;
        }
      }
      
      // 如果有onRowsPerPageChanged回调，调用它来更新rowsPerPage
      if (widget.onRowsPerPageChanged != null) {
        WidgetsBinding.instance.addPostFrameCallback((_) {
          widget.onRowsPerPageChanged!(closestValue);
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData themeData = Theme.of(context);
    final TextStyle footerTextStyle = themeData.textTheme.bodySmall!;
    if(widget.page != null && widget.page == 1) _firstRowIndex = 0;

    return Material(
      elevation: widget.elevation, 
      color: widget.backgroundColor, 
      child: !show() ? SizedBox.shrink() : Container(
        padding: EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        child: DefaultTextStyle(
      style: footerTextStyle,
          child: IconTheme.merge(
            data: const IconThemeData(opacity: 0.54),
            child: Container(
              height: 40.0,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  // 左侧：当前页/总页数
                  _buildCurrentPageIndicator(),
                  
                  // 中间：页码导航
                  Expanded(
                    child: Center(
        child: SingleChildScrollView(
          scrollDirection: Axis.horizontal,
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: getPagination(),
                        ),
                      ),
                    ),
                  ),
                  
                  // 右侧：每页显示条数选择
                  _buildPageSizeSelector(),
                ],
              ),
            ),
          ),
        )
      )
    );
  }

  // 构建当前页/总页数指示器
  Widget _buildCurrentPageIndicator() {
    final bool canGoPrevious = _firstRowIndex > 0;
    final bool canGoNext = _firstRowIndex + widget.rowsPerPage < widget.total;
    
    return Container(
      height: 32,
      margin: EdgeInsets.only(right: 16),
      padding: EdgeInsets.symmetric(horizontal: 8, vertical: 0),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(30),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          InkWell(
            onTap: canGoPrevious ? _handlePrevious : null,
            borderRadius: BorderRadius.circular(12),
            child: MouseRegion(
              cursor: canGoPrevious ? SystemMouseCursors.click : SystemMouseCursors.basic,
              child: Container(
                width: 24,
                height: 24,
                alignment: Alignment.center,
                child: Icon(
                  Icons.chevron_left, 
                  size: 18, 
                  color: canGoPrevious ? Colors.grey.shade700 : Colors.grey.shade400
                ),
              ),
            ),
          ),
          SizedBox(width: 4),
          Text(
            '$_currentPage/$_totalPages',
            style: TextStyle(
              fontSize: 13,
              fontWeight: FontWeight.w500,
              color: Colors.grey.shade800,
            ),
          ),
          SizedBox(width: 4),
          InkWell(
            onTap: canGoNext ? _handleNext : null,
            borderRadius: BorderRadius.circular(12),
            child: MouseRegion(
              cursor: canGoNext ? SystemMouseCursors.click : SystemMouseCursors.basic,
              child: Container(
                width: 24,
                height: 24,
                alignment: Alignment.center,
                child: Icon(
                  Icons.chevron_right, 
                  size: 18,
                  color: canGoNext ? Colors.grey.shade700 : Colors.grey.shade400
      ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  bool show() {
    bool _flag = widget.total == 0 ? false : true;
 
    if(widget.autoPaginate) {
      _flag = widget.total <= widget.rowsPerPage ? false : true;
    }
    return _flag;
  }

  List<Widget> getPagination() {
    final List<Widget> paginationWidgets = <Widget>[];
    
    // 只添加页码按钮，不再添加左右箭头
    _addPageNumbers(paginationWidgets);
    
    return paginationWidgets;
  }
  
  void _addPageNumbers(List<Widget> widgets) {
    // 如果总页数小于等于6，则全部显示
    if (_totalPages <= 7) {
      for (int i = 1; i <= _totalPages; i++) {
        widgets.add(_buildPageButton(i));
      }
      return;
    }
    
    // 根据规律显示页码
    // 规则：
    // 1. 当在前4页时：显示 1 2 3 4 5 ... 倒数第二页 倒数第一页
    // 2. 当在后2页时：显示 1 2 ... 倒数第四页 倒数第三页 倒数第二页 倒数第一页
    // 3. 当在中间位置时：显示 1 2 ... 当前页-1 当前页 当前页+1 ... 倒数第一页
    
    if (_currentPage <= 4) {
      // 规则1：在前4页
      for (int i = 1; i <= 5; i++) {
        widgets.add(_buildPageButton(i));
      }
      widgets.add(_buildEllipsis());
      widgets.add(_buildPageButton(_totalPages - 1));
      widgets.add(_buildPageButton(_totalPages));
    } 
    else if (_currentPage >= _totalPages - 3) {
      // 规则2：在后4页
      widgets.add(_buildPageButton(1));
      widgets.add(_buildPageButton(2));
      widgets.add(_buildEllipsis());
      for (int i = _totalPages - 4; i <= _totalPages; i++) {
        widgets.add(_buildPageButton(i));
      }
    } 
    else {
      // 规则3：在中间位置
      widgets.add(_buildPageButton(1));
      widgets.add(_buildPageButton(2));
      widgets.add(_buildEllipsis());
      widgets.add(_buildPageButton(_currentPage - 1));
      widgets.add(_buildPageButton(_currentPage));
      widgets.add(_buildPageButton(_currentPage + 1));
      widgets.add(_buildEllipsis());
      widgets.add(_buildPageButton(_totalPages));
    }
  }
  
  Widget _buildPageButton(int page) {
    final bool isCurrentPage = page == _currentPage;
    
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 2),
      child: MouseRegion(
        cursor: isCurrentPage ? SystemMouseCursors.basic : SystemMouseCursors.click,
        child: TextButton(
          style: TextButton.styleFrom(
            backgroundColor: isCurrentPage ? Colors.blue.shade50 : Colors.transparent,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(4),
            ),
            padding: EdgeInsets.symmetric(horizontal: 8, vertical: 0),
            minimumSize: Size(30, 30),
            tapTargetSize: MaterialTapTargetSize.shrinkWrap,
          ),
          onPressed: isCurrentPage ? null : () => _pageTo((page - 1) * widget.rowsPerPage),
                    child: Text(
            '$page',
            style: TextStyle(
              fontSize: 13,
              color: isCurrentPage ? Colors.blue : Colors.black54,
              fontWeight: isCurrentPage ? FontWeight.w500 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }
  
  Widget _buildEllipsis() {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 2),
      width: 20,
      alignment: Alignment.center,
      child: Text(
        '...',
        style: TextStyle(
          fontSize: 13,
          color: Colors.black54
        ),
      ),
    );
  }

  void _handlePrevious() {
    _pageTo(math.max(_firstRowIndex - widget.rowsPerPage, 0));
  }

  void _handleNext() {
    _pageTo(_firstRowIndex + widget.rowsPerPage);
  }

  void _pageTo(rowIndex) {
    final int oldFirstRowIndex = _firstRowIndex;
    if (mounted) {
      setState(() {
      final int rowsPerPage = widget.rowsPerPage;
      _firstRowIndex = (rowIndex ~/ rowsPerPage) * rowsPerPage;
      });
    }

    if ((widget.onGetDataCallback != null) &&
        (oldFirstRowIndex != _firstRowIndex)) {
        widget.onGetDataCallback!(_firstRowIndex == 0 ? 1 : (_firstRowIndex / widget.rowsPerPage).ceil() + 1, widget.rowsPerPage);
    }
    
    if (widget.onPageChanged != null && oldFirstRowIndex != _firstRowIndex) {
      widget.onPageChanged!(_firstRowIndex == 0 ? 1 : (_firstRowIndex / widget.rowsPerPage).ceil() + 1);
    }
  }

  Widget _buildPageSizeSelector() {
    // 获取要显示的页面大小选项
    List<int> pageSizes = List<int>.from(widget.pageNums);
    
    // 如果当前的rowsPerPage不在列表中，添加它
    if (!pageSizes.contains(widget.rowsPerPage)) {
      pageSizes.add(widget.rowsPerPage);
      pageSizes.sort();
    }
    
    return Container(
      height: 32,
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(30),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: pageSizes.map((size) => _buildPageSizeButton(size)).toList(),
      ),
    );
  }
  
  Widget _buildPageSizeButton(int size) {
    final bool isSelected = size == widget.rowsPerPage;
    
    return MouseRegion(
      cursor: isSelected ? SystemMouseCursors.basic : SystemMouseCursors.click,
      child: InkWell(
        borderRadius: BorderRadius.circular(30),
        onTap: isSelected ? null : () {
          if (widget.onRowsPerPageChanged != null) {
            widget.onRowsPerPageChanged!(size);
          }
        },
        child: Container(
          height: 32,
          width: 40,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: isSelected ? Colors.white : Colors.transparent,
            borderRadius: BorderRadius.circular(30),
            boxShadow: isSelected ? [
              BoxShadow(
                color: Colors.black.withOpacity(0.08),
                blurRadius: 1,
                spreadRadius: 0,
              )
            ] : null,
          ),
          child: Text(
            '$size',
            style: TextStyle(
              fontSize: 13,
              color: isSelected ? Colors.black87 : Colors.grey.shade600,
              fontWeight: isSelected ? FontWeight.w500 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }
}