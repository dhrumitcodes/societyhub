import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Icon from '../common/Icon';
import Logo from '../common/Logo';


export default function ExploreLayout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const isAdmin = user.role === 'COLLEGE_ADMIN';
    const first = user.memberships[0];
    const initials = user.fullName.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase();
    const label = isAdmin ? 'College Admin' : first ? 'Member' : 'Student';
    const link = ({ isActive }) => `nav-item ${isActive ? 'on' : ''}`;

    return (
        <div className="app">
            <aside className="sidebar">
                <div className="side-logo"><Logo /> Canopy</div>
                <nav aria-label="Main">
                    <NavLink end to="/discover" className={link}><Icon name="cal" />Events</NavLink>
                    <NavLink to="/discover/societies" className={link}><Icon name="team" />{isAdmin ? 'Societies' : 'Societies'}</NavLink>
                    <NavLink to="/discover/registrations" className={link}><Icon name="form" />My registrations</NavLink>
                    {first && (
                        <NavLink to={`/societies/${first.societyId}/overview`} className="nav-item"><Icon name="dash" />My society</NavLink>
                    )}
                </nav>
                <button type="button" className="nav-item signout" onClick={() => { logout(); navigate('/login', { replace: true }); }}>
                    <Icon name="out" />Sign out
                </button>
            </aside>
            <div className="content">
                <header className="topbar">
                    <div className="crumbs">Explore</div>
                    <div className="top-right">
                        <span className="role-chip">{label}</span>
                        <span className="avatar" title={user.fullName}>{initials}</span>
                    </div>
                </header>
                <main><Outlet context={{ eventsPath: '/discover' }} /></main>
            </div>
        </div>
    );
}