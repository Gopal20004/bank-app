import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userAPI } from '../services/api';
import TransferForm from './TransferForm';
import TransactionHistory from './TransactionHistory';
import DepositForm from './DepositForm';

const Dashboard = () => {
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [showTransferForm, setShowTransferForm] = useState(false);
  const [showDepositForm, setShowDepositForm] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const userData = localStorage.getItem('user');
    if (userData) {
      setUser(JSON.parse(userData));
    }
    fetchBalance();
  }, [refreshTrigger]);

  const fetchBalance = async () => {
    try {
      const response = await userAPI.getBalance();
      setBalance(response.data.data);
    } catch (error) {
      console.error('Error fetching balance:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const handleTransferSuccess = () => {
    // Trigger balance refresh
    setRefreshTrigger(prev => prev + 1);
  };

  const handleDepositSuccess = () => {
    // Trigger balance refresh
    setRefreshTrigger(prev => prev + 1);
  };

  const openTransferForm = () => {
    setShowTransferForm(true);
  };

  const closeTransferForm = () => {
    setShowTransferForm(false);
  };

  const openDepositForm = () => {
    setShowDepositForm(true);
  };

  const closeDepositForm = () => {
    setShowDepositForm(false);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading your account...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4 md:py-6">
            <div className="flex items-center">
              <div className="h-10 w-10 md:h-12 md:w-12 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center shadow-lg">
                <svg className="h-6 w-6 md:h-7 md:w-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
              </div>
              <h1 className="ml-3 text-xl md:text-2xl font-bold text-gray-900">BankApp</h1>
            </div>
            <div className="flex items-center space-x-3">
              <span className="hidden sm:block text-sm text-gray-700">Welcome, {user?.username}</span>
              <button
                onClick={handleLogout}
                className="bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-2 px-3 md:px-4 rounded-lg transition-colors duration-200 text-sm"
              >
                <span className="hidden sm:inline">Logout</span>
                <svg className="sm:hidden h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-4 md:py-6 px-4 sm:px-6 lg:px-8">
        {/* Balance Card */}
        <div className="mb-6">
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 rounded-2xl shadow-xl p-6 md:p-8 text-white">
            <div className="text-center">
              <h2 className="text-lg md:text-xl font-medium mb-2 opacity-90">Current Balance</h2>
              <div className="text-4xl md:text-6xl font-bold mb-3">
                ${balance ? balance.toFixed(2) : '0.00'}
              </div>
              <p className="text-primary-100 text-sm md:text-base">
                Account: {user?.username}
              </p>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="mb-6">
          <h3 className="text-lg md:text-xl font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-3 md:gap-4">
            <button
              onClick={openDepositForm}
              className="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-200 p-4 text-center group border border-gray-100"
            >
              <div className="h-10 w-10 md:h-12 md:w-12 bg-green-100 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-green-200 transition-colors">
                <svg className="h-5 w-5 md:h-6 md:w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
              </div>
              <h4 className="font-medium text-gray-900 text-sm md:text-base">Deposit</h4>
              <p className="text-xs md:text-sm text-gray-500 mt-1">Add money</p>
            </button>

            <button
              onClick={openTransferForm}
              className="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-200 p-4 text-center group border border-gray-100"
            >
              <div className="h-10 w-10 md:h-12 md:w-12 bg-blue-100 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-blue-200 transition-colors">
                <svg className="h-5 w-5 md:h-6 md:w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                </svg>
              </div>
              <h4 className="font-medium text-gray-900 text-sm md:text-base">Send Money</h4>
              <p className="text-xs md:text-sm text-gray-500 mt-1">Transfer to others</p>
            </button>

            <button
              onClick={() => navigate('/transactions')}
              className="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-200 p-4 text-center group border border-gray-100"
            >
              <div className="h-10 w-10 md:h-12 md:w-12 bg-purple-100 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-purple-200 transition-colors">
                <svg className="h-5 w-5 md:h-6 md:w-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <h4 className="font-medium text-gray-900 text-sm md:text-base">Transactions</h4>
              <p className="text-xs md:text-sm text-gray-500 mt-1">View history</p>
            </button>

            <button
              onClick={() => navigate('/profile')}
              className="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-200 p-4 text-center group border border-gray-100"
            >
              <div className="h-10 w-10 md:h-12 md:w-12 bg-indigo-100 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-indigo-200 transition-colors">
                <svg className="h-5 w-5 md:h-6 md:w-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
              <h4 className="font-medium text-gray-900 text-sm md:text-base">Profile</h4>
              <p className="text-xs md:text-sm text-gray-500 mt-1">Manage account</p>
            </button>

            <button
              onClick={() => navigate('/support')}
              className="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-200 p-4 text-center group border border-gray-100"
            >
              <div className="h-10 w-10 md:h-12 md:w-12 bg-orange-100 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-orange-200 transition-colors">
                <svg className="h-5 w-5 md:h-6 md:w-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192L5.636 18.364M12 2.25a9.75 9.75 0 100 19.5 9.75 9.75 0 000-19.5z" />
                </svg>
              </div>
              <h4 className="font-medium text-gray-900 text-sm md:text-base">Support</h4>
              <p className="text-xs md:text-sm text-gray-500 mt-1">Get help</p>
            </button>
          </div>
        </div>

        {/* Recent Activity Section */}
        <div>
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 md:p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg md:text-xl font-semibold text-gray-900">Recent Activity</h3>
              <button
                onClick={() => navigate('/transactions')}
                className="text-sm text-primary-600 hover:text-primary-500 font-medium transition-colors duration-200"
              >
                View All
              </button>
            </div>
            <TransactionHistory />
          </div>
        </div>
      </main>

      {/* Transfer Form Modal */}
      {showTransferForm && (
        <TransferForm
          onTransferSuccess={handleTransferSuccess}
          onClose={closeTransferForm}
        />
      )}

      {/* Deposit Form Modal */}
      {showDepositForm && (
        <DepositForm
          onDepositSuccess={handleDepositSuccess}
          onClose={closeDepositForm}
        />
      )}
    </div>
  );
};

export default Dashboard; 