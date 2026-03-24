import React, { useState, useEffect } from 'react';
import { reportAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Reports() {
  const [activeTab, setActiveTab] = useState('transactions');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReportData(activeTab);
  }, [activeTab]);

  const fetchReportData = async (type) => {
    try {
      setLoading(true);
      let res;
      switch (type) {
        case 'transactions':
          res = await reportAPI.getTransactions();
          break;
        case 'expired':
          res = await reportAPI.getExpired();
          break;
        case 'sales':
          res = await reportAPI.getSales();
          break;
        default:
          res = { data: { data: [] } };
      }
      setData(res.data.data);
    } catch (err) {
      toast.error('Failed to load report data');
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const getTransactionBadge = (type) => {
    switch (type) {
      case 'PURCHASE':   return <span className="badge badge-primary">Purchase</span>;
      case 'SALE':       return <span className="badge badge-success">Sale</span>;
      case 'TRANSFER':   return <span className="badge badge-info">Transfer</span>;
      case 'ADJUSTMENT': return <span className="badge badge-warning">Adjustment</span>;
      case 'RETURN':     return <span className="badge badge-danger">Return</span>;
      default: return <span className="badge badge-muted">{type}</span>;
    }
  };

  return (
    <div>
      <div className="page-header mb-16">
        <div>
          <h1 className="page-title">Reports & Logging</h1>
          <p className="page-subtitle">Historical audits and intelligence</p>
        </div>
        <button className="btn btn-secondary" onClick={() => window.print()}>
          <span>🖨️</span> Print Report
        </button>
      </div>

      {/* --- Report Tabs --- */}
      <div className="flex gap-8 mb-24" style={{borderBottom: '1px solid var(--border)', paddingBottom: '16px'}}>
        <button 
          className={`btn ${activeTab === 'transactions' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('transactions')}>
          Full Audit Trail
        </button>
        <button 
          className={`btn ${activeTab === 'sales' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('sales')}>
          Sales History
        </button>
        <button 
          className={`btn ${activeTab === 'expired' ? 'btn-danger' : 'btn-secondary'}`}
          onClick={() => setActiveTab('expired')}>
          Expired Drugs Log
        </button>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner"/></div>
        ) : (
          <div className="table-wrapper">
            
            {/* Conditional Table Rendering based on active tab */}
            {activeTab === 'transactions' && (
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Drug (Batch)</th>
                    <th>Quantity</th>
                    <th>Notes</th>
                  </tr>
                </thead>
                <tbody>
                  {data.length === 0 ? <tr><td colSpan="5" className="text-center py-6">No data found</td></tr> : 
                    data.map(tx => (
                      <tr key={tx.id}>
                        <td>{new Date(tx.transactionDate).toLocaleString()}</td>
                        <td>{getTransactionBadge(tx.type)}</td>
                        <td className="font-bold">{tx.drug.name} <span className="text-muted text-sm">({tx.drug.batchNumber})</span></td>
                        <td>{tx.quantity}</td>
                        <td className="text-secondary">{tx.notes}</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            )}

            {activeTab === 'sales' && (
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Drug Sold</th>
                    <th>Quantity</th>
                    <th>Est. Revenue</th>
                  </tr>
                </thead>
                <tbody>
                  {data.length === 0 ? <tr><td colSpan="4" className="text-center py-6">No sales found</td></tr> : 
                    data.map(tx => (
                      <tr key={tx.id}>
                        <td>{new Date(tx.transactionDate).toLocaleString()}</td>
                        <td className="font-bold">{tx.drug.name}</td>
                        <td className="text-success font-bold">{tx.quantity} dispensed</td>
                        <td className="font-bold">${(tx.quantity * tx.drug.price).toFixed(2)}</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            )}

            {activeTab === 'expired' && (
              <table>
                <thead>
                  <tr>
                    <th>Drug Name</th>
                    <th>Batch Number</th>
                    <th>Expired On</th>
                    <th>Discard Quantity Needed</th>
                  </tr>
                </thead>
                <tbody>
                  {data.length === 0 ? <tr><td colSpan="4" className="text-center py-6 text-success font-bold">No expired drugs! Excellent!</td></tr> : 
                    data.map(d => (
                      <tr key={d.id}>
                        <td className="font-bold">{d.name}</td>
                        <td>{d.batchNumber}</td>
                        <td className="text-danger font-bold">{d.expiryDate}</td>
                        <td className="text-secondary">System-wide: {d.quantity} units</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            )}

          </div>
        )}
      </div>
    </div>
  );
}
