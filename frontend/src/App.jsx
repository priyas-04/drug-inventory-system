import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Toaster } from 'react-hot-toast';

// Layouts & Pages
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Drugs from './pages/Drugs';
import Inventory from './pages/Inventory';
import Suppliers from './pages/Suppliers';
import PurchaseOrders from './pages/PurchaseOrders';
import StockMovements from './pages/StockMovements';
import Reports from './pages/Reports';
import Users from './pages/Users';

/**
 * ProtectedRoute component wrapper.
 * Ensures user is authenticated. 
 * Optionally checks if user has one of the allowed roles.
 */
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user } = useAuth();
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

function AppRoutes() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/dashboard" /> : <Login />} />
      
      {/* Protected routes wrapped in Layout (Sidebar + Top nav) */}
      <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<Navigate to="/dashboard" />} />
        
        {/* All authenticated users can see dashboard */}
        <Route path="dashboard" element={<Dashboard />} />
        
        <Route path="drugs" element={
          <ProtectedRoute allowedRoles={['ADMIN', 'PHARMACIST']}>
            <Drugs />
          </ProtectedRoute>
        } />
        
        <Route path="inventory" element={
          <ProtectedRoute allowedRoles={['ADMIN', 'PHARMACIST']}>
            <Inventory />
          </ProtectedRoute>
        } />
        
        <Route path="suppliers" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <Suppliers />
          </ProtectedRoute>
        } />
        
        <Route path="orders" element={
          <ProtectedRoute allowedRoles={['ADMIN', 'SUPPLIER']}>
            <PurchaseOrders />
          </ProtectedRoute>
        } />
        
        <Route path="movements" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <StockMovements />
          </ProtectedRoute>
        } />
        
        <Route path="reports" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <Reports />
          </ProtectedRoute>
        } />
        
        <Route path="users" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <Users />
          </ProtectedRoute>
        } />
      </Route>
      
      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
        {/* Global toast notification configuration */}
        <Toaster position="top-right"
          toastOptions={{
            style: { background: '#1e293b', color: '#f1f5f9', border: '1px solid #334155' }
          }} 
        />
      </Router>
    </AuthProvider>
  );
}
