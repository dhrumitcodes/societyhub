import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from '../pages/Login';
import EventListPage from '../pages/EventListPage';
import EventDetailPage from '../pages/EventDetailPage';
import DashboardLayout from '../components/layout/DashboardLayout';
import OverviewDashboard from '../components/dashboard/OverviewDashboard';

export default function AppRoutes() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/" element={<Navigate to="/societies/1/overview" replace />} />

                <Route path="/societies/:societyId" element={<DashboardLayout />}>
                    <Route path="overview" element={<OverviewDashboard />} />
                    <Route path="events" element={<EventListPage />} />
                    <Route path="events/:eventId" element={<EventDetailPage />} />
                </Route>

                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </Router>
    );
}