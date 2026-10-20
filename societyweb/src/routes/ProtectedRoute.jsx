import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Loader from '../components/common/Loader';

export default function ProtectedRoute() {
    const { user, loading } = useAuth();
    if (loading) return <Loader />;
    return user ? <Outlet /> : <Navigate to="/login" replace />;
}