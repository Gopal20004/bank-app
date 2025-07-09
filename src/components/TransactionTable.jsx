import React, { useState, useEffect } from 'react';
import { transactionAPI } from '../services/api';

const TransactionTable = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [sortField, setSortField] = useState('transactionDate');
  const [sortDirection, setSortDirection] = useState('desc');
  const [filterType, setFilterType] = useState('all');

  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    try {
      const response = await transactionAPI.getHistory();
      setTransactions(response.data.data || []);
    } catch (error) {
      setError('Failed to load transaction history');
      console.error('Error fetching transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatAmount = (amount, type) => {
    const formattedAmount = parseFloat(amount).toFixed(2);
    if (type === 'TRANSFER_SENT' || type === 'WITHDRAWAL') {
      return `-$${formattedAmount}`;
    }
    return `+$${formattedAmount}`;
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

  const getTransactionTypeColor = (type) => {
    switch (type) {
      case 'TRANSFER_SENT':
        return 'bg-red-100 text-red-800';
      case 'TRANSFER_RECEIVED':
        return 'bg-green-100 text-green-800';
      case 'DEPOSIT':
        return 'bg-blue-100 text-blue-800';
      case 'WITHDRAWAL':
        return 'bg-orange-100 text-orange-800';
      default:
        return 'bg-gray-100 text-gray-800';
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

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  const sortTransactions = (transactions) => {
    return [...transactions].sort((a, b) => {
      let aValue, bValue;

      switch (sortField) {
        case 'transactionDate':
          aValue = new Date(a.transactionDate);
          bValue = new Date(b.transactionDate);
          break;
        case 'amount':
          aValue = parseFloat(a.amount);
          bValue = parseFloat(b.amount);
          break;
        case 'transactionType':
          aValue = getTransactionTypeLabel(a.transactionType);
          bValue = getTransactionTypeLabel(b.transactionType);
          break;
        default:
          aValue = a[sortField];
          bValue = b[sortField];
      }

      if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  };

  const filterTransactions = (transactions) => {
    if (filterType === 'all') return transactions;
    return transactions.filter(transaction => transaction.transactionType === filterType);
  };

  const getSortIcon = (field) => {
    if (sortField !== field) {
      return (
        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
        </svg>
      );
    }
    return sortDirection === 'asc' ? (
      <svg className="w-4 h-4 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
      </svg>
    ) : (
      <svg className="w-4 h-4 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
      </svg>
    );
  };

  const filteredAndSortedTransactions = sortTransactions(filterTransactions(transactions));

  if (loading) {
    return (
      <div className="flex justify-center items-center py-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading transactions...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-8">
        <div className="text-red-600 mb-2">{error}</div>
        <button
          onClick={fetchTransactions}
          className="btn-primary"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Filter Controls */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-3 sm:space-y-0">
        <div className="flex flex-col sm:flex-row items-start sm:items-center space-y-2 sm:space-y-0 sm:space-x-4">
          <label htmlFor="filterType" className="text-sm font-semibold text-gray-700">
            Filter by type:
          </label>
          <select
            id="filterType"
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="w-full sm:w-auto px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200 text-sm"
          >
            <option value="all">All Transactions</option>
            <option value="TRANSFER_SENT">Money Sent</option>
            <option value="TRANSFER_RECEIVED">Money Received</option>
            <option value="DEPOSIT">Deposits</option>
            <option value="WITHDRAWAL">Withdrawals</option>
          </select>
        </div>
        <div className="text-sm text-gray-500 bg-gray-100 px-3 py-1 rounded-lg">
          {filteredAndSortedTransactions.length} transaction{filteredAndSortedTransactions.length !== 1 ? 's' : ''}
        </div>
      </div>

      {/* Mobile Transaction Cards */}
      <div className="block md:hidden space-y-3">
        {filteredAndSortedTransactions.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <svg className="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            <p className="text-lg font-medium">No transactions found</p>
            <p className="text-sm">Try adjusting your filters</p>
          </div>
        ) : (
          filteredAndSortedTransactions.map((transaction) => (
            <div key={transaction.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-4 hover:shadow-md transition-shadow">
              <div className="flex justify-between items-start mb-3">
                <div className="flex items-center space-x-3">
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getTransactionTypeColor(transaction.transactionType)}`}>
                    {getTransactionTypeLabel(transaction.transactionType)}
                  </span>
                  <span className="text-xs text-gray-500">
                    {formatDate(transaction.transactionDate)}
                  </span>
                </div>
                <div className="text-right">
                  <div className={`font-bold text-lg ${
                    transaction.transactionType === 'TRANSFER_SENT' || transaction.transactionType === 'WITHDRAWAL'
                      ? 'text-red-600'
                      : 'text-green-600'
                  }`}>
                    {formatAmount(transaction.amount, transaction.transactionType)}
                  </div>
                </div>
              </div>
              <div className="space-y-1">
                <div className="text-sm text-gray-600">
                  <span className="font-medium">To/From:</span> {getReceiverInfo(transaction)}
                </div>
                <div className="text-sm text-gray-500">
                  {transaction.description || 'No description'}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Desktop Transaction Table */}
      <div className="hidden md:block overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th
                scope="col"
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100 transition-colors"
                onClick={() => handleSort('transactionDate')}
              >
                <div className="flex items-center space-x-1">
                  <span>Date</span>
                  {getSortIcon('transactionDate')}
                </div>
              </th>
              <th
                scope="col"
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100 transition-colors"
                onClick={() => handleSort('transactionType')}
              >
                <div className="flex items-center space-x-1">
                  <span>Type</span>
                  {getSortIcon('transactionType')}
                </div>
              </th>
              <th
                scope="col"
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100 transition-colors"
                onClick={() => handleSort('amount')}
              >
                <div className="flex items-center space-x-1">
                  <span>Amount</span>
                  {getSortIcon('amount')}
                </div>
              </th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Receiver/Sender
              </th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Description
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {filteredAndSortedTransactions.length === 0 ? (
              <tr>
                <td colSpan="5" className="px-6 py-12 text-center text-gray-500">
                  <svg className="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  <p className="text-lg font-medium">No transactions found</p>
                  <p className="text-sm">Try adjusting your filters</p>
                </td>
              </tr>
            ) : (
              filteredAndSortedTransactions.map((transaction) => (
                <tr key={transaction.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {formatDate(transaction.transactionDate)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getTransactionTypeColor(transaction.transactionType)}`}>
                      {getTransactionTypeLabel(transaction.transactionType)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <span className={
                      transaction.transactionType === 'TRANSFER_SENT' || transaction.transactionType === 'WITHDRAWAL'
                        ? 'text-red-600'
                        : 'text-green-600'
                    }>
                      {formatAmount(transaction.amount, transaction.transactionType)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {getReceiverInfo(transaction)}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">
                    {transaction.description || 'No description'}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TransactionTable; 