import React, { useState, useEffect } from 'react';
import { userAPI, authAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modal setup for Create ONLY in this demo (update logic is similar)
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    username: '', password: '', email: '', fullName: '', phone: '', role: 'PHARMACIST'
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await userAPI.getAll();
      setUsers(res.data.data);
    } catch (err) {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = async (id) => {
    try {
      await userAPI.toggleActive(id);
      toast.success('User status updated');
      fetchUsers();
    } catch (err) {
      toast.error('Failed to update user');
    }
  };

  const handleDelete = async (id) => {
    if(window.confirm('Are you sure you want to delete this user?')) {
      try {
        await userAPI.delete(id);
        toast.success('User removed');
        fetchUsers();
      } catch (err) {
        toast.error('Delete failed');
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Create user via auth register endpoint since it hashes password
      await authAPI.register(formData);
      toast.success('User created successfully');
      setShowModal(false);
      setFormData({username: '', password: '', email: '', fullName: '', phone: '', role: 'PHARMACIST'});
      fetchUsers();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error creating user');
    }
  };

  const getRoleBadge = (role) => {
    switch(role) {
      case 'ADMIN': return <span className="badge badge-primary">Admin</span>;
      case 'PHARMACIST': return <span className="badge badge-success">Pharmacist</span>;
      case 'SUPPLIER': return <span className="badge badge-info">Supplier</span>;
      default: return <span className="badge badge-muted">{role}</span>;
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">User Management</h1>
          <p className="page-subtitle">Admin controls for staff access</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          <span>+</span> Add User
        </button>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner"/></div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Full Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? <tr><td colSpan="6" className="text-center py-6">No users found</td></tr> : 
                  users.map(u => (
                    <tr key={u.id}>
                      <td className="font-bold">{u.username}</td>
                      <td>{u.fullName}</td>
                      <td>{u.email}</td>
                      <td>{getRoleBadge(u.role)}</td>
                      <td>
                        {u.active 
                          ? <span className="badge badge-success">Active</span> 
                          : <span className="badge badge-danger">Inactive</span>
                        }
                      </td>
                      <td className="actions-cell">
                        <button 
                          className={`btn btn-sm ${u.active ? 'btn-danger' : 'btn-success'}`} 
                          onClick={() => handleToggle(u.id)}
                        >
                          {u.active ? 'Disable' : 'Enable'}
                        </button>
                        <button className="btn btn-sm btn-secondary" onClick={() => handleDelete(u.id)}>
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* --- Add User Modal --- */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3 className="modal-title">Create New User</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Username</label>
                  <input type="text" className="form-control" required
                    value={formData.username} onChange={e => setFormData({...formData, username: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Password</label>
                  <input type="password" className="form-control" required minLength="6"
                    value={formData.password} onChange={e => setFormData({...formData, password: e.target.value})} />
                </div>
              </div>

              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Full Name</label>
                  <input type="text" className="form-control" required
                    value={formData.fullName} onChange={e => setFormData({...formData, fullName: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Role</label>
                  <select className="form-control"
                    value={formData.role} onChange={e => setFormData({...formData, role: e.target.value})}>
                    <option value="PHARMACIST">Pharmacist</option>
                    <option value="ADMIN">Admin</option>
                    <option value="SUPPLIER">Supplier</option>
                  </select>
                </div>
              </div>

              <div className="form-grid mb-24">
                <div>
                  <label className="form-label">Email</label>
                  <input type="email" className="form-control" required
                    value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Phone</label>
                  <input type="text" className="form-control"
                    value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Create User</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
