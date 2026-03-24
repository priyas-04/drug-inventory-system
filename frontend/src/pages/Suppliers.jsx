import React, { useState, useEffect } from 'react';
import { supplierAPI } from '../services/api';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

export default function Suppliers() {
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  
  const { user } = useAuth(); // Assume supplier user ID will be needed if we log who adds them
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '', email: '', phone: '', address: '', 
    licenseNumber: '', contactPerson: ''
  });

  useEffect(() => {
    fetchSuppliers();
  }, []);

  const fetchSuppliers = async () => {
    try {
      const res = await supplierAPI.getAll();
      setSuppliers(res.data.data);
    } catch (err) {
      toast.error('Failed to load suppliers');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    const val = e.target.value;
    setSearchTerm(val);
    if (val.length > 2) {
      try {
        const res = await supplierAPI.search(val);
        setSuppliers(res.data.data);
      } catch (err) {
        toast.error('Search failed');
      }
    } else if (val === '') {
      fetchSuppliers();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (formData.id) {
        await supplierAPI.update(formData.id, formData);
        toast.success('Supplier updated');
      } else {
        await supplierAPI.create(formData);
        toast.success('Supplier added');
      }
      setShowModal(false);
      fetchSuppliers();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error saving supplier');
    }
  };

  const deleteSupplier = async (id) => {
    if (window.confirm('Are you sure you want to remove this supplier?')) {
      try {
        await supplierAPI.delete(id);
        toast.success('Supplier removed');
        fetchSuppliers();
      } catch (err) {
        toast.error('Failed to remove supplier');
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Supplier Management</h1>
          <p className="page-subtitle">Manage external vendors and manufacturers</p>
        </div>
        <button className="btn btn-primary" onClick={() => {
          setFormData({ name: '', email: '', phone: '', address: '', licenseNumber: '', contactPerson: '' });
          setShowModal(true);
        }}>
          <span>+</span> Add Supplier
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-bar">
            <span>🔍</span>
            <input type="text" placeholder="Search suppliers..." value={searchTerm} onChange={handleSearch} />
          </div>
        </div>
        
        {loading ? (
          <div className="loading"><div className="spinner"/></div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Supplier Name</th>
                  <th>License No.</th>
                  <th>Contact Person</th>
                  <th>Email & Phone</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {suppliers.length === 0 ? (
                  <tr><td colSpan="5" className="text-center py-6">No suppliers found</td></tr>
                ) : (
                  suppliers.map(s => (
                    <tr key={s.id}>
                      <td className="font-bold">{s.name}</td>
                      <td><span className="badge badge-muted">{s.licenseNumber}</span></td>
                      <td>{s.contactPerson}</td>
                      <td className="text-sm">
                        <div>📧 {s.email}</div>
                        <div className="text-muted mt-1">📞 {s.phone}</div>
                      </td>
                      <td className="actions-cell">
                        <button className="btn btn-sm btn-secondary" onClick={() => {
                          setFormData(s); setShowModal(true);
                        }}>Edit</button>
                        <button className="btn btn-sm btn-danger" onClick={() => deleteSupplier(s.id)}>Del</button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* --- Auth Modal --- */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3 className="modal-title">{formData.id ? 'Edit Supplier' : 'Add Supplier'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Company Name</label>
                <input type="text" className="form-control" required 
                  value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
              </div>
              
              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">License Number</label>
                  <input type="text" className="form-control" required
                    value={formData.licenseNumber} onChange={e => setFormData({...formData, licenseNumber: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Primary Contact Person</label>
                  <input type="text" className="form-control" required
                    value={formData.contactPerson} onChange={e => setFormData({...formData, contactPerson: e.target.value})} />
                </div>
              </div>

              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Email Address</label>
                  <input type="email" className="form-control" required
                    value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Phone Number</label>
                  <input type="text" className="form-control" required
                    value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} />
                </div>
              </div>

              <div className="form-group mb-24">
                <label className="form-label">Physical Address</label>
                <textarea className="form-control" rows="2" required
                  value={formData.address} onChange={e => setFormData({...formData, address: e.target.value})} />
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{formData.id ? 'Save Changes' : 'Add Supplier'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
