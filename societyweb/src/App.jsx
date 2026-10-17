import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import DashboardLayout from './components/layout/DashboardLayout';
import OverviewDashboard from './components/dashboard/OverviewDashboard';
import EventList from './components/events/EventList';
import EventDetail from './components/events/EventDetail';

export default function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/societies/1/overview" replace />} />
                <Route path="/login" element={<Login />} />

                <Route path="/societies/:societyId" element={<DashboardLayout />}>
                    <Route path="overview" element={<OverviewDashboard />} />
                    <Route path="events" element={<EventList />} />
                    <Route path="events/:eventId" element={<EventDetail />} />
                </Route>

                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </Router>
    );
}