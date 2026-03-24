import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { reportAPI } from '../services/api';
import toast from 'react-hot-toast';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  LineChart, Line
} from 'recharts';

export default function Dashboard() {
  const { user } = useAuth();
  
  const [summary, setSummary] = useState(null);
  const [sales, setSales]     = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      // Fetch high-level summary stats
      const sumRes = await reportAPI.getSummary();
      setSummary(sumRes.data.data);

      // Fetch last sales transactions for the chart
      const salesRes = await reportAPI.getSales();
      
      // Group sales by date for the chart
      const salesData = salesRes.data.data;
      const grouped = {};
      
      // We'll just take the last 7 days of sales 
      salesData.forEach(tx => {
        const date = tx.transactionDate.split('T')[0];
        if(!grouped[date]) grouped[date] = 0;
        grouped[date] += tx.quantity;
      });

      // Convert to array format required by Recharts
      const chartData = Object.keys(grouped).slice(0, 7).map(date => ({
        date,
        quantity: grouped[date]
      })).reverse(); // Oldest to newest left-to-right

      setSales(chartData);
    } catch (error) {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"/></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Welcome back, {user.fullName || user.username}! 👋</h1>
          <p className="page-subtitle">Here is what's happening in your inventory today.</p>
        </div>
      </div>

      {/* --- Alerts row --- */}
      {summary?.lowStockCount > 0 && (
        <div className="alert-banner warning">
          <span style={{fontSize:'18px'}}>⚠️</span>
          <strong>Attention:</strong> You have {summary.lowStockCount} items running low on stock. Please check the Inventory page.
        </div>
      )}
      
      {summary?.expiredDrugsCount > 0 && (
        <div className="alert-banner danger">
          <span style={{fontSize:'18px'}}>🚨</span>
          <strong>Critical:</strong> {summary.expiredDrugsCount} drug batches have expired and must be removed from shelves immediately!
        </div>
      )}

      {/* --- Top Metrics Cards --- */}
      <div className="stats-grid">
        <div className="stat-card blue">
          <div className="stat-label">Total Drug Entities</div>
          <div className="stat-value blue">{summary?.totalDrugs || 0}</div>
          <div className="stat-sub">Registered in catalog</div>
        </div>
        
        <div className="stat-card purple">
          <div className="stat-label">Total Stock Units</div>
          <div className="stat-value purple">{summary?.totalStockUnits || 0}</div>
          <div className="stat-sub">Across all locations</div>
        </div>

        <div className="stat-card amber">
          <div className="stat-label">Low Stock Items</div>
          <div className="stat-value amber">{summary?.lowStockCount || 0}</div>
          <div className="stat-sub">Below minimum threshold</div>
        </div>

        <div className="stat-card red">
          <div className="stat-label">Expired Batches</div>
          <div className="stat-value red">{summary?.expiredDrugsCount || 0}</div>
          <div className="stat-sub">Requires disposal</div>
        </div>
      </div>

      {/* --- Charts --- */}
      <div className="form-grid">
        <div className="card">
          <div className="card-header">
            <div className="card-title">Recent Sales Volume (Units)</div>
          </div>
          <div className="chart-wrapper" style={{ height: 300 }}>
            {sales.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={sales} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                  <XAxis dataKey="date" stroke="#94a3b8" fontSize={12} tickMargin={10} />
                  <YAxis stroke="#94a3b8" fontSize={12} />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
                    itemStyle={{ color: '#818cf8' }}
                  />
                  <Bar dataKey="quantity" fill="url(#colorUv)" radius={[4, 4, 0, 0]} barSize={40} />
                  
                  {/* Gradient definition for the bar */}
                  <defs>
                    <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#818cf8" stopOpacity={1}/>
                      <stop offset="95%" stopColor="#4f46e5" stopOpacity={0.8}/>
                    </linearGradient>
                  </defs>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="empty-state">No sales data available yet</div>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <div className="card-title">Supply Chain Flow</div>
          </div>
          <div className="empty-state" style={{ marginTop: '40px' }}>
            <div className="flex-center" style={{ gap: '20px', justifyContent: 'center' }}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', marginBottom: '8px' }}>🏭</div>
                <div className="text-sm font-bold">Suppliers</div>
              </div>
              <div style={{ color: '#4f46e5', fontSize: '24px' }}>→</div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', marginBottom: '8px' }}>🏢</div>
                <div className="text-sm font-bold">Warehouse</div>
              </div>
              <div style={{ color: '#10b981', fontSize: '24px' }}>→</div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '32px', marginBottom: '8px' }}>🏥</div>
                <div className="text-sm font-bold">Pharmacy</div>
              </div>
            </div>
            <p className="mt-4 mt-24 text-muted text-sm" style={{ marginTop: '30px' }}>
              Your entire supply chain is monitored in real-time.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
