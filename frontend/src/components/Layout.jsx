import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Main application layout.
 * Displays Sidebar navigation on left, and Outlet (page content) on right.
 * The Sidebar items displayed depend on the logged-in user's role.
 */
export default function Layout() {
  const { user, logout, isAdmin, isPharmacist, isSupplier } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Define navigation items per role
  const navStructure = [
    {
      section: 'Main',
      items: [
        { label: 'Dashboard', path: '/dashboard', icon: '📊', roles: ['ADMIN', 'PHARMACIST', 'SUPPLIER'] }
      ]
    },
    {
      section: 'Pharmacy Operations',
      roles: ['ADMIN', 'PHARMACIST'],
      items: [
        { label: 'Drugs Catalog', path: '/drugs', icon: '💊', roles: ['ADMIN', 'PHARMACIST'] },
        { label: 'Inventory Management', path: '/inventory', icon: '📦', roles: ['ADMIN', 'PHARMACIST'] }
      ]
    },
    {
      section: 'Supply Chain',
      roles: ['ADMIN', 'SUPPLIER'],
      items: [
        { label: 'Purchase Orders', path: '/orders', icon: '📝', roles: ['ADMIN', 'SUPPLIER'] },
        { label: 'Stock Movements', path: '/movements', icon: '🚚', roles: ['ADMIN'] },
        { label: 'Suppliers', path: '/suppliers', icon: '🏭', roles: ['ADMIN'] }
      ]
    },
    {
      section: 'Analytics & Admin',
      roles: ['ADMIN'],
      items: [
        { label: 'Reports', path: '/reports', icon: '📈', roles: ['ADMIN'] },
        { label: 'User Management', path: '/users', icon: '👥', roles: ['ADMIN'] }
      ]
    }
  ];

  return (
    <div className="layout">
      {/* --- Sidebar Area --- */}
      <aside className="sidebar">
        <div className="sidebar-logo">
          <h2>DrugChain</h2>
          <p>Inventory & Supply System</p>
        </div>

        <nav className="sidebar-nav">
          {navStructure.map((section, idx) => {
            // Check if user's role can see this section
            if (section.roles && !section.roles.includes(user.role)) return null;
            
            return (
              <div key={idx} className="nav-section">
                <div className="nav-section-title">{section.section}</div>
                {section.items.map((item, i) => {
                  if (item.roles && !item.roles.includes(user.role)) return null;
                  return (
                    <NavLink
                      key={i}
                      to={item.path}
                      className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                    >
                      <span className="nav-icon">{item.icon}</span>
                      {item.label}
                    </NavLink>
                  );
                })}
              </div>
            );
          })}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="user-avatar">
              {user.username.charAt(0).toUpperCase()}
            </div>
            <div>
              <div className="user-name">{user.fullName || user.username}</div>
              <span className="user-role">{user.role}</span>
            </div>
          </div>
          <button className="btn btn-logout" onClick={handleLogout}>
            <span>🚪</span> Logout
          </button>
        </div>
      </aside>

      {/* --- Main Content Area --- */}
      <main className="main-content">
        {/* Child routes render here (Dashboard, Drugs list, etc.) */}
        <Outlet />
      </main>
    </div>
  );
}
