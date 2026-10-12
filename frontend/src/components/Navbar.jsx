import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useCart } from "../context/CartContext.jsx";

export default function Navbar() {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();

  const itemCount = cart?.items?.reduce((sum, i) => sum + i.quantity, 0) || 0;

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to="/" className="brand">
        <svg className="logo-mark" width="30" height="30" viewBox="0 0 48 48" fill="none" aria-hidden="true">
          <path className="steam steam-1" d="M17 14c-2 3 2 4 0 8" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" />
          <path className="steam steam-2" d="M24 11c-2 3 2 5 0 9" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" />
          <path className="steam steam-3" d="M31 14c-2 3 2 4 0 8" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" />
          <path d="M9 26c0-1.5 1-2.5 2.5-2.5h25c1.5 0 2.5 1 2.5 2.5 0 7-6.5 12.5-15 12.5S9 33 9 26Z" fill="currentColor" />
          <path d="M9.5 24.5h29" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
        </svg>
        FoodHub
      </Link>

      <div className="nav-links">
        {!user && (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}

        {user?.role === "CUSTOMER" && (
          <>
            <Link to="/">Restaurants</Link>
            <Link to="/cart">Cart ({itemCount})</Link>
            <Link to="/orders">My Orders</Link>
          </>
        )}

        {user?.role === "RESTAURANT_ADMIN" && (
          <>
            <Link to="/restaurant-admin">Dashboard</Link>
            <Link to="/restaurant-admin/menu">Menu</Link>
            <Link to="/restaurant-admin/orders">Orders</Link>
          </>
        )}

        {user?.role === "SUPER_ADMIN" && (
          <>
            <Link to="/admin/restaurants">Restaurants</Link>
            <Link to="/admin/categories">Categories</Link>
            <Link to="/admin/users">Users</Link>
            <Link to="/admin/stats">Stats</Link>
          </>
        )}

        {user && (
          <>
            <span className="user-chip">{user.name}</span>
            <button className="link-btn" onClick={handleLogout}>Logout</button>
          </>
        )}
      </div>
    </nav>
  );
}