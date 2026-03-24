import React, { useState, useEffect } from 'react';
import { drugAPI, supplierAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Drugs() {
  const [drugs, setDrugs] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '', batchNumber: '', expiryDate: '', manufactureDate: '',
    quantity: 0, price: 0, category: '', manufacturer: '',
    supplierId: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [drugRes, supRes] = await Promise.all([
        drugAPI.getAll(),
        supplierAPI.getAll()
      ]);
      setDrugs(drugRes.data.data);
      setSuppliers(supRes.data.data);
    } catch (err) {
      toast.error('Failed to load drugs data');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    const val = e.target.value;
    setSearchTerm(val);
    if (val.length > 2) {
      try {
        const res = await drugAPI.search(val);
        setDrugs(res.data.data);
      } catch (err) {
        toast.error('Search failed');
      }
    } else if (val === '') {
      fetchData();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (formData.id) {
        await drugAPI.update(formData.id, formData);
        toast.success('Drug updated successfully');
      } else {
        await drugAPI.create(formData);
        toast.success('Drug added to catalog');
      }
      setShowModal(false);
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error saving drug');
    }
  };

  const deleteDrug = async (id) => {
    if (window.confirm('Are you sure you want to deactivate this drug?')) {
      try {
        await drugAPI.delete(id);
        toast.success('Drug deactivated');
        fetchData();
      } catch (err) {
        toast.error('Failed to deactivate drug');
      }
    }
  };

  const openNewModal = () => {
    setFormData({
      name: '', batchNumber: '', expiryDate: '', manufactureDate: '',
      quantity: 0, price: 0, category: '', manufacturer: '', supplierId: ''
    });
    setShowModal(true);
  };

  // Check if a date is within 30 days or past
  const getExpiryStatus = (dateStr) => {
    const exp = new Date(dateStr);
    const now = new Date();
    const diffDays = Math.ceil((exp - now) / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return <span className="badge badge-danger">Expired</span>;
    if (diffDays <= 30) return <span className="badge badge-warning">Expiring Soon</span>;
    return <span className="badge badge-success">Good</span>;
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Drugs Catalog</h1>
          <p className="page-subtitle">Manage system-wide master drug records</p>
        </div>
        <button className="btn btn-primary" onClick={openNewModal}>
          <span>+</span> Add New Drug
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-bar">
            <span>🔍</span>
            <input 
              type="text" 
              placeholder="Search drugs by name..." 
              value={searchTerm}
              onChange={handleSearch}
            />
          </div>
        </div>
        
        {loading ? (
          <div className="loading"><div className="spinner"/></div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Drug Name</th>
                  <th>Batch No.</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Total Stock</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {drugs.length === 0 ? (
                  <tr><td colSpan="7" style={{textAlign:'center', padding:'40px'}}>No drugs found</td></tr>
                ) : (
                  drugs.map(d => (
                    <tr key={d.id}>
                      <td className="font-bold">{d.name}</td>
                      <td className="text-secondary">{d.batchNumber}</td>
                      <td>{d.category}</td>
                      <td>${d.price.toFixed(2)}</td>
                      <td className={d.quantity < d.lowStockThreshold ? 'text-danger font-bold' : ''}>
                        {d.quantity} units
                      </td>
                      <td>{getExpiryStatus(d.expiryDate)}</td>
                      <td className="actions-cell">
                        <button className="btn btn-sm btn-secondary" onClick={() => {
                          setFormData({...d, supplierId: d.supplier?.id}); 
                          setShowModal(true);
                        }}>Edit</button>
                        <button className="btn btn-sm btn-danger" onClick={() => deleteDrug(d.id)}>Del</button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* --- Add/Edit Modal --- */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3 className="modal-title">{formData.id ? 'Edit Drug' : 'Add New Drug'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Drug Name</label>
                  <input type="text" className="form-control" required 
                    value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Batch Number</label>
                  <input type="text" className="form-control" required
                    value={formData.batchNumber} onChange={e => setFormData({...formData, batchNumber: e.target.value})} />
                </div>
              </div>
              
              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Category</label>
                  <input type="text" className="form-control"
                    value={formData.category} onChange={e => setFormData({...formData, category: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Manufacturer</label>
                  <input type="text" className="form-control"
                    value={formData.manufacturer} onChange={e => setFormData({...formData, manufacturer: e.target.value})} />
                </div>
              </div>

              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Price ($)</label>
                  <input type="number" step="0.01" min="0.01" className="form-control" required
                    value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Initial Quantity</label>
                  <input type="number" min="0" className="form-control" required
                    value={formData.quantity} onChange={e => setFormData({...formData, quantity: e.target.value})} />
                </div>
              </div>

              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Manufacture Date</label>
                  <input type="date" className="form-control" required
                    value={formData.manufactureDate} onChange={e => setFormData({...formData, manufactureDate: e.target.value})} />
                </div>
                <div>
                  <label className="form-label">Expiry Date</label>
                  <input type="date" className="form-control" required
                    value={formData.expiryDate} onChange={e => setFormData({...formData, expiryDate: e.target.value})} />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Supplier</label>
                <select className="form-control" required
                  value={formData.supplierId} onChange={e => setFormData({...formData, supplierId: e.target.value})}>
                  <option value="">Select a supplier...</option>
                  {suppliers.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                </select>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{formData.id ? 'Save Changes' : 'Add Drug'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
