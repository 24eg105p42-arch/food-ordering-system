import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";

import Home from "./pages/customer/Home.jsx";
import RestaurantMenu from "./pages/customer/RestaurantMenu.jsx";
import Cart from "./pages/customer/Cart.jsx";
import Checkout from "./pages/customer/Checkout.jsx";
import Orders from "./pages/customer/Orders.jsx";
import OrderTrack from "./pages/customer/OrderTrack.jsx";

import RestaurantDashboard from "./pages/restaurant-admin/Dashboard.jsx";
import MenuManager from "./pages/restaurant-admin/MenuManager.jsx";
import IncomingOrders from "./pages/restaurant-admin/IncomingOrders.jsx";

import AdminRestaurants from "./pages/admin/Restaurants.jsx";
import AdminCategories from "./pages/admin/Categories.jsx";
import AdminUsers from "./pages/admin/Users.jsx";
import AdminStats from "./pages/admin/Stats.jsx";

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        {/* Public */}
        <Route path="/" element={<Home />} />
        <Route path="/restaurants/:id" element={<RestaurantMenu />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Customer */}
        <Route path="/cart" element={<ProtectedRoute role="CUSTOMER"><Cart /></ProtectedRoute>} />
        <Route path="/checkout" element={<ProtectedRoute role="CUSTOMER"><Checkout /></ProtectedRoute>} />
        <Route path="/orders" element={<ProtectedRoute role="CUSTOMER"><Orders /></ProtectedRoute>} />
        <Route path="/orders/:id" element={<ProtectedRoute role="CUSTOMER"><OrderTrack /></ProtectedRoute>} />

        {/* Restaurant Admin */}
        <Route path="/restaurant-admin" element={
          <ProtectedRoute role="RESTAURANT_ADMIN"><RestaurantDashboard /></ProtectedRoute>
        } />
        <Route path="/restaurant-admin/menu" element={
          <ProtectedRoute role="RESTAURANT_ADMIN"><MenuManager /></ProtectedRoute>
        } />
        <Route path="/restaurant-admin/orders" element={
          <ProtectedRoute role="RESTAURANT_ADMIN"><IncomingOrders /></ProtectedRoute>
        } />

        {/* Super Admin */}
        <Route path="/admin/restaurants" element={
          <ProtectedRoute role="SUPER_ADMIN"><AdminRestaurants /></ProtectedRoute>
        } />
        <Route path="/admin/categories" element={
          <ProtectedRoute role="SUPER_ADMIN"><AdminCategories /></ProtectedRoute>
        } />
        <Route path="/admin/users" element={
          <ProtectedRoute role="SUPER_ADMIN"><AdminUsers /></ProtectedRoute>
        } />
        <Route path="/admin/stats" element={
          <ProtectedRoute role="SUPER_ADMIN"><AdminStats /></ProtectedRoute>
        } />

        <Route path="*" element={<p className="page">Page not found.</p>} />
      </Routes>
    </>
  );
}
