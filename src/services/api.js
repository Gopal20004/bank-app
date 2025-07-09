import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

export const userAPI = {
  getBalance: () => api.get('/user/balance'),
  getProfile: () => api.get('/user/profile'),
  deposit: (amount) => api.post('/user/deposit', { amount }),
};

export const transactionAPI = {
  getHistory: () => api.get('/transactions'),
  transfer: (transferData) => api.post('/transactions/transfer', transferData),
  deposit: (amount) => api.post('/transactions/deposit', { amount }),
  withdraw: (amount) => api.post('/transactions/withdraw', { amount }),
};

export default api; 