package com.bank.transaction.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int page = Constants.DEFAULT_PAGE;        // 当前页码
    private int pageSize = Constants.DEFAULT_PAGE_SIZE; // 每页条数
    private int total = 0;                           // 总记录数
    private int maxPage = 0;                         // 总页数
    private List<T> items;                           // 当前页数据

    public PageInfo() {}

    public PageInfo(List<T> items, int page, int pageSize, int total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.maxPage = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
    }

    // 静态工厂方法
    public static <T> PageInfo<T> empty() {
        return new PageInfo<>();
    }

    public static <T> PageInfo<T> of(List<T> items, int page, int pageSize, int total) {
        return new PageInfo<>(items, page, pageSize, total);
    }

    // 便捷判断方法
    public boolean hasNext() {return page < maxPage - 1;}
    public boolean hasPrevious() {return page > 0;}
    public boolean isEmpty() {return items == null || items.isEmpty();}

    // Getter/Setter方法
    public int getPage() {return page;}
    public void setPage(int page) {this.page = page;}
    public int getPageSize() {return pageSize;}
    public void setPageSize(int pageSize) {this.pageSize = pageSize;}
    public int getTotal() {return total;}
    public void setTotal(int total) {this.total = total;}
    public int getMaxPage() {return maxPage;}
    public void setMaxPage(int maxPage) {this.maxPage = maxPage;}
    public List<T> getItems() {
        if (items == null) {this.items = new ArrayList<>();}
        return items;
    }
    public void setItems(List<T> items) {this.items = items;}

    // 兼容旧版本的方法名
    @Deprecated
    public List<T> getList() {return getItems();}
    @Deprecated 
    public int getPageNum() {return getPage();}
    @Deprecated
    public int getPages() {return getMaxPage();}
    @Deprecated
    public boolean isHasNextPage() {return hasNext();}
    @Deprecated
    public boolean isHasPreviousPage() {return hasPrevious();}
} 