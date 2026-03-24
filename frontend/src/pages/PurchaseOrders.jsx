import React, { useState, useEffect } from 'react';
import { orderAPI } from '../services/api';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

export default function PurchaseOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  
  const { user, isAdmin } = useAuth();
  
  // Modal for Viewing Order Details
  const [selectedOrder, setSelectedOrder] = useState(null);

  useEffect(() => {
    fetchOrders();
  }, [filter]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      let res;
      if (filter === 'ALL') {
        res = await orderAPI.getAll();
      } else {
        res = await orderAPI.getByStatus(filter);
      }
      setOrders(res.data.data);
    } catch (err) {
      toast.error('Failed to load purchase orders');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (actionFn, id, actionName) => {
    if(!window.confirm(`Are you sure you want to ${actionName} this order?`)) return;
    try {
      await actionFn(id);
      toast.success(`Order ${actionName}d successfully`);
      fetchOrders();
      setSelectedOrder(null);
    } catch (err) {
      toast.error(err.response?.data?.message || `Failed to ${actionName} order`);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':   return <span className="badge badge-warning">Pending</span>;
      case 'APPROVED':  return <span className="badge badge-primary">Approved</span>;
      case 'DELIVERED': return <span className="badge badge-success">Delivered</span>;
      case 'CANCELLED': return <span className="badge badge-danger">Cancelled</span>;
      default: return <span className="badge badge-muted">{status}</span>;
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Purchase Orders</h1>
          <p className="page-subtitle">Manage procurement from suppliers to warehouse</p>
        </div>
        {/* Note: Create Order functionality is a complex multi-step form normally. 
            For this UI, we focus on the approval/delivery lifecycle for existing orders. */}
        {isAdmin() && (
           <button className="btn btn-primary" onClick={() => toast('New order form would open here.')}>
             <span>+</span> Draft New Order
           </button>
        )}
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-bar">
            <span>📋</span>
            <select 
              className="form-control" 
              style={{background:'transparent', border:'none', width:'auto', padding:0, fontSize:'14px'}}
              value={filter} onChange={e => setFilter(e.target.value)}
            >
              <option value="ALL">All Statuses</option>
              <option value="PENDING">Pending Approval</option>
              <option value="APPROVED">Approved (Awaiting Delivery)</option>
              <option value="DELIVERED">Delivered</option>
              <option value="CANCELLED">Cancelled</option>
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
                  <th>Order ID</th>
                  <th>Supplier</th>
                  <th>Order Date</th>
                  <th>Total Items</th>
                  <th>Total Value</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.length === 0 ? (
                  <tr><td colSpan="7" className="text-center py-6">No orders found</td></tr>
                ) : (
                  orders.map(order => {
                    const totalValue = order.items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
                    return (
                      <tr key={order.id}>
                        <td className="font-bold">#PO-{order.id.toString().padStart(4, '0')}</td>
                        <td>{order.supplier.name}</td>
                        <td>{new Date(order.orderDate).toLocaleDateString()}</td>
                        <td>{order.items.length} products</td>
                        <td className="font-bold">${totalValue.toFixed(2)}</td>
                        <td>{getStatusBadge(order.status)}</td>
                        <td>
                          <button className="btn btn-sm btn-secondary" onClick={() => setSelectedOrder(order)}>
                            View Details
                          </button>
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

      {/* --- View Details & Action Modal --- */}
      {selectedOrder && (
        <div className="modal-overlay">
          <div className="modal" style={{maxWidth: '700px'}}>
            <div className="modal-header">
              <div>
                <h3 className="modal-title">Order #PO-{selectedOrder.id.toString().padStart(4, '0')}</h3>
                <div className="text-sm text-muted mt-1">From: {selectedOrder.supplier.name}</div>
              </div>
              <button className="modal-close" onClick={() => setSelectedOrder(null)}>✕</button>
            </div>
            
            <div className="mb-24 flex" style={{justifyContent: 'space-between', alignItems: 'center'}}>
              <div>
                <p className="text-sm font-bold text-secondary mb-1">Status</p>
                {getStatusBadge(selectedOrder.status)}
              </div>
              <div>
                <p className="text-sm font-bold text-secondary mb-1">Order Date</p>
                <p className="text-sm">{new Date(selectedOrder.orderDate).toLocaleString()}</p>
              </div>
              {selectedOrder.deliveryDate && (
                <div>
                  <p className="text-sm font-bold text-secondary mb-1">Delivered On</p>
                  <p className="text-sm">{new Date(selectedOrder.deliveryDate).toLocaleString()}</p>
                </div>
              )}
            </div>

            <h4 className="font-bold mb-12">Ordered Items</h4>
            <div className="table-wrapper mb-24" style={{border: '1px solid var(--border)'}}>
              <table>
                <thead>
                  <tr>
                    <th>Drug</th>
                    <th>Qty</th>
                    <th>Unit Price</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedOrder.items.map(item => (
                    <tr key={item.id}>
                      <td>{item.drug.name}</td>
                      <td>{item.quantity}</td>
                      <td>${item.unitPrice.toFixed(2)}</td>
                      <td className="font-bold">${(item.quantity * item.unitPrice).toFixed(2)}</td>
                    </tr>
                  ))}
                  <tr style={{background: 'var(--bg-card-hover)'}}>
                    <td colSpan="3" style={{textAlign:'right'}} className="font-bold">Total Ordered Value:</td>
                    <td className="font-bold text-success">
                      ${selectedOrder.items.reduce((sum, i) => sum + (i.quantity*i.unitPrice), 0).toFixed(2)}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            {selectedOrder.notes && (
              <div className="mb-24">
                <p className="text-sm font-bold text-secondary mb-1">Notes</p>
                <p className="text-sm p-3" style={{background: 'var(--bg-dark)', borderRadius: '8px'}}>{selectedOrder.notes}</p>
              </div>
            )}

            {/* Admin Actions Lifecycle */}
            {isAdmin() && (
              <div className="modal-footer" style={{borderTop: '1px solid var(--border)', paddingTop: '20px'}}>
                {selectedOrder.status === 'PENDING' && (
                  <>
                    <button className="btn btn-danger" onClick={() => handleAction(orderAPI.cancel, selectedOrder.id, 'cancel')}>Cancel Order</button>
                    <button className="btn btn-primary" onClick={() => handleAction(orderAPI.approve, selectedOrder.id, 'approve')}>Approve Order</button>
                  </>
                )}
                {selectedOrder.status === 'APPROVED' && (
                  <button className="btn btn-success flex-center gap-8" onClick={() => handleAction((id) => orderAPI.deliver(id, 1), selectedOrder.id, 'deliver')}>
                    <span>📦</span> Mark as Delivered (Update Inventory)
                  </button>
                )}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
