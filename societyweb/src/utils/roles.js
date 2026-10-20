export const roleLabel = (r) =>
    r ? r.toLowerCase().split('_').map((w) => w[0].toUpperCase() + w.slice(1)).join(' ') : '';

export const ROLE_OPTIONS = ['PRESIDENT', 'VICE_PRESIDENT', 'SECRETARY', 'TREASURER', 'MEMBER'];


export const NAV = [
    { label: 'Dashboard', to: 'overview', icon: 'dash' },
    { label: 'Events', to: 'events', icon: 'cal' },
    { label: 'Members', to: 'members', icon: 'users' },
];

export const greeting = () => {
    const h = new Date().getHours();
    return h < 12 ? 'Good morning' : h < 17 ? 'Good afternoon' : 'Good evening';
};