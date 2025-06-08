package com.bank.transaction.dao;

import java.util.List;
import java.util.Optional;
import com.bank.transaction.model.Transaction;

public interface TransactionDao {
    
    Transaction save(Transaction transaction);
    
    Optional<Transaction> findById(String id);

    List<Transaction> findByAccountId(String accountId);
} 