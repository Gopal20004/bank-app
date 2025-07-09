import React, { useState, useEffect } from 'react';
import { transactionAPI } from '../services/api';

const TransactionHistory = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRecentTransactions();
  }, []);

  const fetchRecentTransactions = async () => {
    try {
      const response = await transactionAPI.getHistory();
      const allTransactions = response.data.data || [];
      // Get the 5 most recent transactions
      const recentTransactions = allTransactions
        .sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate))
        .slice(0, 5);
      setTransactions(recentTransactions);
    } catch (error) {
      console.error('Error fetching recent transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) {
      return 'Today';
    } else if (diffDays === 2) {
      return 'Yesterday';
    } else if (diffDays <= 7) {
      return `${diffDays - 1} days ago`;
    } else {
      return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric'
      });
    }
  };

  const formatAmount = (amount, type) => {
    const formattedAmount = parseFloat(amount).toFixed(2);
    if (type === 'TRANSFER_SENT' || type === 'WITHDRAWAL') {
      return `-$${formattedAmount}`;
    }
    return `+$${formattedAmount}`;
  };

  const getTransactionTypeIcon = (type) => {
    switch (type) {
      case 'TRANSFER_SENT':
        return (
          <div className="h-8 w-8 bg-red-100 rounded-lg flex items-center justify-center">
            <svg className="h-4 w-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
            </svg>
          </div>
        );
      case 'TRANSFER_RECEIVED':
        return (
          <div className="h-8 w-8 bg-green-100 rounded-lg flex items-center justify-center">
            <svg className="h-4 w-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h8" />
            </svg>
          </div>
        );
      case 'DEPOSIT':
        return (
          <div className="h-8 w-8 bg-blue-100 rounded-lg flex items-center justify-center">
            <svg className="h-4 w-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
            </svg>
          </div>
        );
      case 'WITHDRAWAL':
        return (
          <div className="h-8 w-8 bg-orange-100 rounded-lg flex items-center justify-center">
            <svg className="h-4 w-4 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
            </svg>
          </div>
        );
      default:
        return (
          <div className="h-8 w-8 bg-gray-100 rounded-lg flex items-center justify-center">
            <svg className="h-4 w-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
        );
    }
  };

  const getTransactionTypeLabel = (type) => {
    switch (type) {
      case 'TRANSFER_SENT':
        return 'Money Sent';
      case 'TRANSFER_RECEIVED':
        return 'Money Received';
      case 'DEPOSIT':
        return 'Deposit';
      case 'WITHDRAWAL':
        return 'Withdrawal';
      default:
        return type;
    }
  };

  const getReceiverInfo = (transaction) => {
    if (transaction.transactionType === 'TRANSFER_SENT') {
      return transaction.recipientAccountNumber || 'Unknown';
    } else if (transaction.transactionType === 'TRANSFER_RECEIVED') {
      return transaction.senderAccountNumber || 'Unknown';
    } else {
      return 'N/A';
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-6">
        <div className="text-center">
          <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-primary-600 mx-auto mb-2"></div>
          <p className="text-sm text-gray-500">Loading recent activity...</p>
        </div>
      </div>
    );
  }

  if (transactions.length === 0) {
    return (
      <div className="text-center py-6">
        <div className="h-12 w-12 bg-gray-100 rounded-xl flex items-center justify-center mx-auto mb-3">
          <svg className="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
        </div>
        <p className="text-sm font-medium text-gray-900 mb-1">No recent activity</p>
        <p className="text-xs text-gray-500">Your recent transactions will appear here</p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {transactions.map((transaction) => (
        <div key={transaction.id} className="flex items-center space-x-3 p-3 hover:bg-gray-50 rounded-xl transition-colors duration-200">
          {getTransactionTypeIcon(transaction.transactionType)}
          
          <div className="flex-1 min-w-0">
            <div className="flex justify-between items-start">
              <div className="min-w-0 flex-1">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {getTransactionTypeLabel(transaction.transactionType)}
                </p>
                <div className="flex items-center space-x-2 mt-1">
                  <p className="text-xs text-gray-500">
                    {getReceiverInfo(transaction)}
                  </p>
                  <span className="text-gray-300">•</span>
                  <p className="text-xs text-gray-500">
                    {formatDate(transaction.transactionDate)}
                  </p>
                </div>
                {transaction.description && (
                  <p className="text-xs text-gray-400 mt-1 truncate">
                    {transaction.description}
                  </p>
                )}
              </div>
              
              <div className="text-right ml-3">
                <div className={`text-sm font-semibold ${
                  transaction.transactionType === 'TRANSFER_SENT' || transaction.transactionType === 'WITHDRAWAL'
                    ? 'text-red-600'
                    : 'text-green-600'
                }`}>
                  {formatAmount(transaction.amount, transaction.transactionType)}
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default TransactionHistory; 