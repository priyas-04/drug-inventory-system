import React, { useState, useEffect } from 'react';
import { inventoryAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Inventory() {
  const [inventory, setInventory] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedLocation, setSelectedLocation] = useState('ALL');

  useEffect(() => {
    fetchInventory();
  }, [selectedLocation]);

  const fetchInventory = async () => {
    try {
      setLoading(true);
      // Fetch alerts first
      const alertsRes = await inventoryAPI.getAlerts();
      setAlerts(alertsRes.data.data);

      // Fetch inventory based on selected location filter
      let invRes;
      if (selectedLocation === 'ALL') {
        invRes = await inventoryAPI.getAll();
      } else {
        invRes = await inventoryAPI.getByLocation(selectedLocation);
      }
      setInventory(invRes.data.data);
    } catch (err) {
      toast.error('Failed to load inventory data');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Inventory Tracking</h1>
          <p className="page-subtitle">Real-time stock levels across all locations</p>
        </div>
      </div>

      {/* --- Low Stock Alerts Banner --- */}
      {alerts.length > 0 && (
        <div className="card mb-24" style={{ borderColor: 'var(--warning)', background: 'rgba(245,158,11,.05)' }}>
          <div className="flex-center gap-12 mb-16">
            <span style={{fontSize:'24px'}}>⚠️</span>
            <h3 className="card-title" style={{marginBottom:0, color: 'var(--warning)'}}>
              Low Stock Action Required ({alerts.length} Items)
            </h3>
          </div>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Drug Name</th>
                  <th>Location</th>
                  <th>Current Stock</th>
                  <th>Minimum Required</th>
                  <th>Deficit</th>
                </tr>
              </thead>
              <tbody>
                {alerts.slice(0, 5).map(item => (
                  <tr key={item.id}>
                    <td className="font-bold">{item.drug.name}</td>
                    <td><span className="badge badge-info">{item.location}</span></td>
                    <td className="text-danger font-bold">{item.quantity} units</td>
                    <td>{item.lowStockThreshold} units</td>
                    <td className="text-warning font-bold">-{item.lowStockThreshold - item.quantity} units</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {alerts.length > 5 && (
            <div className="text-sm text-muted mt-4" style={{marginTop:'12px', textAlign:'right'}}>
              + {alerts.length - 5} more items needing restock
            </div>
          )}
        </div>
      )}

      {/* --- Full Inventory Table --- */}
      <div className="card">
        <div className="card-header">
          <div className="search-bar">
            <span>📍</span>
            <select 
              className="form-control" 
              style={{background:'transparent', border:'none', width:'auto', padding:0, fontSize:'14px'}}
              value={selectedLocation}
              onChange={e => setSelectedLocation(e.target.value)}
            >
              <option value="ALL">All Locations</option>
              <option value="WAREHOUSE">Warehouse</option>
              <option value="PHARMACY">Pharmacy</option>
              <option value="HOSPITAL">Hospital</option>
            </select>
          </div>
        </div>

        {loading ? (
          <div className="loading"><div className="spinner"/></div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Location</th>
                  <th>Drug Name</th>
                  <th>Batch No.</th>
                  <th>Qty Available</th>
                  <th>Min Threshold</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {inventory.length === 0 ? (
                  <tr><td colSpan="6" style={{textAlign:'center', padding:'40px'}}>No inventory records found</td></tr>
                ) : (
                  inventory.map(inv => {
                    const isLow = inv.quantity <= inv.lowStockThreshold;
                    return (
                      <tr key={inv.id}>
                        <td><span className="badge badge-muted">{inv.location}</span></td>
                        <td className="font-bold">{inv.drug.name}</td>
                        <td className="text-secondary">{inv.drug.batchNumber}</td>
                        <td className={isLow ? 'text-danger font-bold' : 'text-success font-bold'}>
                          {inv.quantity} units
                        </td>
                        <td>{inv.lowStockThreshold} units</td>
                        <td>
                          {isLow 
                            ? <span className="badge badge-danger">Low Stock</span>
                            : <span className="badge badge-success">Sufficient</span>
                          }
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
