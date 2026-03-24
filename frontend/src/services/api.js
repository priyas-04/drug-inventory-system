import axios from 'axios';

// Base API configuration - all requests go to Spring Boot backend
// When deployed on Vercel, it uses the VITE_API_URL. When running locally, it defaults to '/api' (proxied by Vite)
const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
});

// Interceptor: Attach JWT token to every request automatically
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor: Handle 401 (token expired) by logging out
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ======= Auth API =======
export const authAPI = {
  login: (data) => API.post('/auth/login', data),
  register: (data) => API.post('/auth/register', data),
};

// ======= Drug API =======
export const drugAPI = {
  getAll:          ()         => API.get('/drugs'),
  getById:         (id)       => API.get(`/drugs/${id}`),
  create:          (data)     => API.post('/drugs', data),
  update:          (id, data) => API.put(`/drugs/${id}`, data),
  delete:          (id)       => API.delete(`/drugs/${id}`),
  search:          (name)     => API.get(`/drugs/search?name=${name}`),
  getExpired:      ()         => API.get('/drugs/expired'),
  getExpiringSoon: (days=30)  => API.get(`/drugs/expiring-soon?days=${days}`),
  getLowStock:     ()         => API.get('/drugs/low-stock'),
};

// ======= Inventory API =======
export const inventoryAPI = {
  getAll:         ()         => API.get('/inventory'),
  getAlerts:      ()         => API.get('/inventory/alerts'),
  getByLocation:  (location) => API.get(`/inventory/location/${location}`),
};

// ======= Supplier API =======
export const supplierAPI = {
  getAll:   ()         => API.get('/suppliers'),
  getById:  (id)       => API.get(`/suppliers/${id}`),
  create:   (data)     => API.post('/suppliers', data),
  update:   (id, data) => API.put(`/suppliers/${id}`, data),
  delete:   (id)       => API.delete(`/suppliers/${id}`),
  search:   (name)     => API.get(`/suppliers/search?name=${name}`),
};

// ======= Purchase Orders API =======
export const orderAPI = {
  getAll:     ()     => API.get('/orders'),
  getById:    (id)   => API.get(`/orders/${id}`),
  create:     (data) => API.post('/orders', data),
  approve:    (id)   => API.put(`/orders/${id}/approve`),
  deliver:    (id, userId) => API.put(`/orders/${id}/deliver?userId=${userId}`),
  cancel:     (id)   => API.put(`/orders/${id}/cancel`),
  getByStatus:(status) => API.get(`/orders/status/${status}`),
};

// ======= Stock Movements API =======
export const movementAPI = {
  getAll:    ()     => API.get('/movements'),
  create:    (data) => API.post('/movements', data),
  getByDrug: (id)   => API.get(`/movements/drug/${id}`),
};

// ======= Reports API =======
export const reportAPI = {
  getSummary:      ()        => API.get('/reports/summary'),
  getExpired:      ()        => API.get('/reports/expired'),
  getExpiringSoon: (days=30) => API.get(`/reports/expiring-soon?days=${days}`),
  getLowStock:     ()        => API.get('/reports/low-stock'),
  getSales:        ()        => API.get('/reports/sales'),
  getTransactions: ()        => API.get('/reports/transactions'),
};

// ======= User Management API =======
export const userAPI = {
  getAll:       ()         => API.get('/users'),
  getById:      (id)       => API.get(`/users/${id}`),
  update:       (id, data) => API.put(`/users/${id}`, data),
  delete:       (id)       => API.delete(`/users/${id}`),
  toggleActive: (id)       => API.put(`/users/${id}/toggle`),
};

export default API;
