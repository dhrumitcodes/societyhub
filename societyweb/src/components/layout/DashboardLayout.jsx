import { Outlet, NavLink, useParams } from 'react-router-dom';

export default function DashboardLayout() {
    const { societyId = '1' } = useParams();

    return (
        <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#f8f9fa', fontFamily: 'system-ui, sans-serif' }}>

            <aside style={{ width: '260px', backgroundColor: '#ffffff', borderRight: '1px solid #e5e7eb', padding: '24px 16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>

                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <div style={{ width: '40px', height: '40px', backgroundColor: '#111827', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontSize: '20px' }}>
                        📅
                    </div>
                    <div>
                        <h3 style={{ fontSize: '15px', fontWeight: '600', margin: 0, color: '#111827' }}>SocietyHub</h3>
                        <span style={{ fontSize: '12px', color: '#6b7280' }}>College club workspace</span>
                    </div>
                </div>

                <div style={{ padding: '10px 12px', backgroundColor: '#f3f4f6', borderRadius: '8px', fontSize: '14px', fontWeight: '500', color: '#374151', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span>👥</span> Tech Society
                </div>

                <nav style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <span style={{ fontSize: '11px', fontWeight: '600', color: '#9ca3af', padding: '0 12px 6px', textTransform: 'uppercase' }}>Workspace</span>
                    <NavLink to={`/societies/${societyId}/overview`} style={navLinkStyle}>Overview</NavLink>
                    <NavLink to={`/societies/${societyId}/events`} style={navLinkStyle}>Events</NavLink>
                    <NavLink to={`/societies/${societyId}/members`} style={navLinkStyle}>Members</NavLink>
                    <NavLink to={`/societies/${societyId}/registrations`} style={navLinkStyle}>Registrations</NavLink>
                    <NavLink to={`/societies/${societyId}/roles`} style={navLinkStyle}>Roles</NavLink>
                    <NavLink to={`/societies/${societyId}/settings`} style={navLinkStyle}>Settings</NavLink>
                </nav>
            </aside>

            <main style={{ flex: 1, padding: '40px', overflowY: 'auto' }}>
                <Outlet />
            </main>
        </div>
    );
}

const navLinkStyle = ({ isActive }) => ({
    display: 'block',
    padding: '10px 12px',
    borderRadius: '6px',
    textDecoration: 'none',
    color: isActive ? '#111827' : '#4b5563',
    backgroundColor: isActive ? '#e5e7eb' : 'transparent',
    fontWeight: isActive ? '600' : '500',
    fontSize: '14px',
    transition: 'background-color 0.2s'
});