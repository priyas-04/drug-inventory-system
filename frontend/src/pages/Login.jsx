import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function Login() {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [loading, setLoading]   = useState(false);
  
  const { login }  = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login({ username, password });
      toast.success('Welcome back!');
      navigate('/dashboard'); // Route to default page
    } catch (err) {
      toast.error(err.response?.data?.message || 'Login failed. Check credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-logo">💊</div>
        <h1 className="login-title">DrugChain</h1>
        <p className="login-subtitle">Supply Chain & Inventory Management</p>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-control"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className="form-group mb-24">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary w-full" 
            style={{ justifyContent: 'center', height: '42px', fontSize: '15px' }}
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Sign In To Proceed'}
          </button>
        </form>

        <div className="login-divider">Demo Accounts (Password: admin123)</div>
        <div className="demo-creds">
          <div><strong>Admin:</strong> admin</div>
          <div><strong>Pharmacist:</strong> pharmacist1</div>
          <div><strong>Supplier:</strong> supplier1</div>
        </div>
      </div>
    </div>
  );
}
