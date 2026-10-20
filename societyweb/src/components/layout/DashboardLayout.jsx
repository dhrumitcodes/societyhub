import { NavLink, Navigate, Outlet, useLocation, useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { NAV, roleLabel } from '../../utils/roles';
import Icon from '../common/Icon';
import Logo from '../common/Logo';

export default function DashboardLayout() {
    const { user, logout } = useAuth();
    const { societyId } = useParams();
    const navigate = useNavigate();
    const { pathname } = useLocation();

    const membership = user.memberships.find((m) => String(m.societyId) === societyId);
    const isAdmin = user.role === 'COLLEGE_ADMIN';
    if (!membership && !isAdmin) return <Navigate to="/" replace />;

    const section = pathname.split('/')[3] || 'overview';
    const current = NAV.find((n) => n.to === section) ?? NAV[0];
    const societyName = membership?.societyName ?? 'Society';
    const initials = user.fullName.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase();
    const mark = societyName.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase();
    const roleText = membership ? roleLabel(membership.role) : 'College Admin';

    return (
        <div className="app">
            <aside className="sidebar">
                <div className="side-logo"><Logo /> Canopy</div>
                <div className="society">
                    <span className="society-mark">{mark}</span>
                    <span>
                        {user.memberships.length > 1 ? (
                            <select
                                className="switcher"
                                aria-label="Switch society"
                                value={societyId}
                                onChange={(e) => navigate(`/societies/${e.target.value}/${section}`)}
                            >
                                {user.memberships.map((m) => <option key={m.societyId} value={m.societyId}>{m.societyName}</option>)}
                            </select>
                        ) : (
                            <strong>{societyName}</strong>
                        )}
                        <small>{roleText}</small>
                    </span>
                </div>
                <nav aria-label="Main">
                    {NAV.map((n) => (
                        <NavLink key={n.to} to={`/societies/${societyId}/${n.to}`} className={({ isActive }) => `nav-item ${isActive ? 'on' : ''}`}>
                            <Icon name={n.icon} />{n.label}
                        </NavLink>
                    ))}
                    <NavLink to="/discover" className="nav-item"><Icon name="search" />Explore</NavLink>
                </nav>
                <button type="button" className="nav-item signout" onClick={() => { logout(); navigate('/login', { replace: true }); }}>
                    <Icon name="out" />Sign out
                </button>
            </aside>

            <div className="content">
                <header className="topbar">
                    <div className="crumbs">{societyName} / <strong>{current.label}</strong></div>
                    <div className="top-right">
                        <span className="role-chip">{roleText}</span>
                        <span className="avatar" title={user.fullName}>{initials}</span>
                    </div>
                </header>
                <main><Outlet context={{ societyId, membership, eventsPath: `/societies/${societyId}/events` }} /></main>
            </div>
        </div>
    );
}