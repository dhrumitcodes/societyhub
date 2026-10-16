import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import EventList from './components/events/EventList';
import EventDetail from './components/events/EventDetail';

export default function App() {
  return (
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/events" element={<EventList />} />
          <Route path="/societies/:societyId/events/:eventId" element={<EventDetail />} />
          <Route path="*" element={<Navigate to="/events" replace />} />
        </Routes>
      </Router>
  );
}