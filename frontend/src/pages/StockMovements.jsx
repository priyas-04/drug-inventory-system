import React, { useState, useEffect } from 'react';
import { movementAPI, drugAPI } from '../services/api';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

export default function StockMovements() {
  const [movements, setMovements] = useState([]);
  const [drugs, setDrugs] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const { user } = useAuth();
  
  // Modal state for transferring stock
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    drugId: '', fromLocation: 'WAREHOUSE', toLocation: 'PHARMACY', 
    quantity: '', reason: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [movRes, drugRes] = await Promise.all([
        movementAPI.getAll(),
        drugAPI.getAll()
      ]);
      setMovements(movRes.data.data);
      setDrugs(drugRes.data.data);
    } catch (err) {
      toast.error('Failed to load stock movements');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (formData.fromLocation === formData.toLocation) {
        toast.error('Source and destination cannot be the same');
        return;
      }
      if (formData.quantity <= 0) {
        toast.error('Quantity must be greater than zero');
        return;
      }

      const payload = { ...formData, userId: 1 }; // hardcoding admin user ID for demo simplicity
      await movementAPI.create(payload);
      toast.success('Stock transferred successfully');
      setShowModal(false);
      setFormData({ drugId: '', fromLocation: 'WAREHOUSE', toLocation: 'PHARMACY', quantity: '', reason: '' });
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to transfer stock');
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Stock Movements</h1>
          <p className="page-subtitle">Track and transfer drugs between Warehouse, Pharmacy, and Hospital</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          <span>🚚</span> Transfer Stock
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
                  <th>Date & Time</th>
                  <th>Drug Name</th>
                  <th>Route</th>
                  <th>Quantity</th>
                  <th>Reason</th>
                </tr>
              </thead>
              <tbody>
                {movements.length === 0 ? (
                  <tr><td colSpan="5" className="text-center py-6">No stock movements found</td></tr>
                ) : (
                  movements.map(m => (
                    <tr key={m.id}>
                      <td>{new Date(m.movedAt).toLocaleString()}</td>
                      <td className="font-bold">{m.drug.name}</td>
                      <td>
                        <span className="badge badge-muted">{m.fromLocation}</span>
                        <span style={{margin: '0 8px', color: 'var(--primary-light)'}}>→</span>
                        <span className="badge badge-primary">{m.toLocation}</span>
                      </td>
                      <td className="font-bold text-success">{m.quantity} units</td>
                      <td className="text-secondary">{m.reason || 'N/A'}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* --- Transfer Modal --- */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3 className="modal-title">Transfer Stock</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Select Drug to Transfer</label>
                <select className="form-control" required
                  value={formData.drugId} onChange={e => setFormData({...formData, drugId: e.target.value})}>
                  <option value="">Select a drug...</option>
                  {drugs.map(d => <option key={d.id} value={d.id}>{d.name} (Batch: {d.batchNumber})</option>)}
                </select>
              </div>
              
              <div className="form-grid mb-16">
                <div>
                  <label className="form-label">Source Location</label>
                  <select className="form-control"
                    value={formData.fromLocation} onChange={e => setFormData({...formData, fromLocation: e.target.value})}>
                    <option value="WAREHOUSE">Warehouse</option>
                    <option value="PHARMACY">Pharmacy</option>
                    <option value="HOSPITAL">Hospital</option>
                  </select>
                </div>
                <div className="flex-center" style={{justifyContent:'center', paddingTop:'20px'}}>
                  <span style={{fontSize:'24px', color:'var(--text-muted)'}}>→</span>
                </div>
                <div>
                  <label className="form-label">Destination Location</label>
                  <select className="form-control"
                    value={formData.toLocation} onChange={e => setFormData({...formData, toLocation: e.target.value})}>
                    <option value="PHARMACY">Pharmacy</option>
                    <option value="WAREHOUSE">Warehouse</option>
                    <option value="HOSPITAL">Hospital</option>
                  </select>
                </div>
              </div>

              <div className="form-group mb-16">
                <label className="form-label">Quantity to Move</label>
                <input type="number" min="1" className="form-control" required
                  value={formData.quantity} onChange={e => setFormData({...formData, quantity: e.target.value})} />
              </div>

              <div className="form-group mb-24">
                <label className="form-label">Reason / Notes (Optional)</label>
                <textarea className="form-control" rows="2"
                  value={formData.reason} onChange={e => setFormData({...formData, reason: e.target.value})} 
                  placeholder="e.g. Weekly restock for Pharmacy" />
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Process Transfer</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
