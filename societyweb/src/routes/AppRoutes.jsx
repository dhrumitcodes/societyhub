import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from '../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';
import Loader from '../components/common/Loader';
import Login from '../pages/Login';
import EventListPage from '../pages/EventListPage';
import EventDetailPage from '../pages/EventDetailPage';
import EventFormPage from '../pages/EventFormPage';
import MembersPage from '../pages/MembersPage';
import SocietyDashboard from '../pages/admin/SocietyDashboard';
import DashboardLayout from '../components/layout/DashboardLayout';

function Home() {
    const { user, loading, logout } = useAuth();
    if (loading) return <Loader />;
    if (!user) return <Navigate to="/login" replace />;
    if (user.memberships.length > 0) {
        return <Navigate to={`/societies/${user.memberships[0].societyId}/overview`} replace />;
    }
    return (
        <div className="empty-screen">
            <h1>You are not part of a society yet</h1>
            <p className="muted">Ask a society's president to add you, then sign in again.</p>
            <button className="btn-outline" onClick={logout}>Sign out</button>
        </div>
    );
}

export default function AppRoutes() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/" element={<Home />} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/societies/:societyId" element={<DashboardLayout />}>
                            <Route path="overview" element={<SocietyDashboard />} />
                            <Route path="events" element={<EventListPage />} />
                            <Route path="events/new" element={<EventFormPage />} />
                            <Route path="events/:eventId" element={<EventDetailPage />} />
                            <Route path="events/:eventId/edit" element={<EventFormPage />} />
                            <Route path="members" element={<MembersPage />} />
                        </Route>
                    </Route>
                    <Route path="*" element={<Home />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}