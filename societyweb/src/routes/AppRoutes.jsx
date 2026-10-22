import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from '../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';
import Loader from '../components/common/Loader';
import Login from '../pages/Login';
import RegisterPage from '../pages/RegisterPage';
import DiscoverPage from '../pages/DiscoverPage';
import SocietiesPage from '../pages/SocietiesPage';
import MyRegistrationsPage from '../pages/MyRegistrationsPage';
import EventListPage from '../pages/EventListPage';
import EventDetailPage from '../pages/EventDetailPage';
import EventFormPage from '../pages/EventFormPage';
import MembersPage from '../pages/MembersPage';
import AnnouncementsPage from '../pages/AnnouncementsPage';
import ChangePasswordPage from '../pages/ChangePasswordPage';
import SocietyDashboard from '../pages/admin/SocietyDashboard';
import DashboardLayout from '../components/layout/DashboardLayout';
import ExploreLayout from '../components/layout/ExploreLayout';

function Home() {
    const { user, loading } = useAuth();
    if (loading) return <Loader />;
    if (!user) return <Navigate to="/login" replace />;
    if (user.memberships.length > 0) {
        return <Navigate to={`/societies/${user.memberships[0].societyId}/overview`} replace />;
    }
    return <Navigate to={user.role === 'COLLEGE_ADMIN' ? '/discover/societies' : '/discover'} replace />;
}

export default function AppRoutes() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/" element={<Home />} />

                    <Route element={<ProtectedRoute />}>
                        <Route path="/discover" element={<ExploreLayout />}>
                            <Route index element={<DiscoverPage />} />
                            <Route path="societies" element={<SocietiesPage />} />
                            <Route path="societies/:societyId/events/:eventId" element={<EventDetailPage />} />
                            <Route path="registrations" element={<MyRegistrationsPage />} />
                            <Route path="account" element={<ChangePasswordPage />} />
                        </Route>

                        <Route path="/societies/:societyId" element={<DashboardLayout />}>
                            <Route path="overview" element={<SocietyDashboard />} />
                            <Route path="events" element={<EventListPage />} />
                            <Route path="events/new" element={<EventFormPage />} />
                            <Route path="events/:eventId" element={<EventDetailPage />} />
                            <Route path="events/:eventId/edit" element={<EventFormPage />} />
                            <Route path="members" element={<MembersPage />} />
                            <Route path="announcements" element={<AnnouncementsPage />} />
                            <Route path="account" element={<ChangePasswordPage />} />
                        </Route>
                    </Route>

                    <Route path="*" element={<Home />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}