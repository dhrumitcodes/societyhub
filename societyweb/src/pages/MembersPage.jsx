import { useCallback, useEffect, useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { societyApi } from '../api/societyApi';
import { joinRequestApi } from '../api/joinRequestApi';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/formatDate';
import { ROLE_OPTIONS, roleLabel } from '../utils/roles';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

export default function MembersPage() {
    const { societyId, membership } = useOutletContext();
    const { user } = useAuth();
    const isAdmin = user.role === 'COLLEGE_ADMIN';
    const canManage = membership ? membership.canManage : isAdmin;
    const isPresident = isAdmin || membership?.role === 'PRESIDENT'; // only presidents change roles

    const [members, setMembers] = useState(null);
    const [error, setError] = useState(null);
    const [actionError, setActionError] = useState('');
    const [email, setEmail] = useState('');
    const [role, setRole] = useState('MEMBER');
    const [busy, setBusy] = useState(false);
    const [requests, setRequests] = useState([]);

    const loadRequests = useCallback(() => {
        if (!canManage) return;
        joinRequestApi.pending(societyId).then(setRequests).catch(() => setRequests([]));
    }, [societyId, canManage]);
    useEffect(() => { loadRequests(); }, [loadRequests]);

    const load = useCallback(() => {
        setError(null);
        societyApi.getMembers(societyId).then(setMembers).catch(setError);
    }, [societyId]);

    useEffect(() => { setMembers(null); load(); }, [load]);

    const run = async (action) => {
        setActionError('');
        setBusy(true);
        try {
            await action();
            await load();
            return true;
        } catch (err) {
            setActionError(err.response?.data?.message || 'Something went wrong. Please try again.');
            return false;
        } finally {
            setBusy(false);
        }
    };

    const add = async (e) => {
        e.preventDefault();
        if (await run(() => societyApi.addMember(societyId, { email, role }))) setEmail('');
    };

    const changeRole = (m, newRole) => run(() => societyApi.updateMemberRole(societyId, m.userId, newRole));

    const decide = (r, approve) => run(async () => {
        if (approve) await joinRequestApi.approve(societyId, r.id);
        else await joinRequestApi.decline(societyId, r.id);
        await loadRequests();
    });

    const remove = (m) => {
        if (!window.confirm(`Remove ${m.userFullName} from this society?`)) return;
        run(() => societyApi.removeMember(societyId, m.userId));
    };

    if (error) return <ErrorMessage error={error} onRetry={load} />;
    if (!members) return <Loader />;

    return (
        <>
            <h1 className="greet">Members</h1>
            <p className="muted greet-sub">{members.length} {members.length === 1 ? 'member' : 'members'} in this society.</p>

            {canManage && requests.length > 0 && (
                <section className="panel" style={{ marginBottom: 18 }}>
                    <h2>Join requests ({requests.length})</h2>
                    {requests.map((r) => (
                        <div className="list-row" key={r.id}>
                            <div>
                                <strong>{r.userFullName}</strong>
                                <small>{r.userEmail}{r.message ? ` · ${r.message}` : ''}</small>
                            </div>
                            <div className="actions-row" style={{ margin: 0 }}>
                                <button type="button" className="btn-solid" disabled={busy} onClick={() => decide(r, true)}>Approve</button>
                                <button type="button" className="btn-danger" disabled={busy} onClick={() => decide(r, false)}>Decline</button>
                            </div>
                        </div>
                    ))}
                </section>
            )}

            {canManage && (
                <form className="panel inline-form" onSubmit={add}>
                    <h2>Add a member</h2>
                    <div className="toolbar" style={{ margin: '12px 0 0' }}>
                        <input className="field" style={{ flex: 1 }} type="email" placeholder="Their college email" aria-label="Member email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                        <select className="field" aria-label="Role" value={role} onChange={(e) => setRole(e.target.value)}>
                            {ROLE_OPTIONS.map((r) => <option key={r} value={r}>{roleLabel(r)}</option>)}
                        </select>
                        <button type="submit" className="btn-solid" disabled={busy}>Add</button>
                    </div>
                    <p className="muted" style={{ fontSize: 13, margin: '10px 0 0' }}>They must already have an account.</p>
                </form>
            )}
            {actionError && <p className="form-error-light" role="alert" style={{ marginBottom: 16 }}>{actionError}</p>}

            <section className="panel table-wrap">
                {members.length === 0 ? <p className="muted empty">No members yet.</p> : (
                    <table>
                        <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Joined</th>{canManage && <th />}</tr></thead>
                        <tbody>
                        {members.map((m) => (
                            <tr key={m.id}>
                                <td><strong>{m.userFullName}</strong></td>
                                <td className="muted">{m.userEmail}</td>
                                <td>
                                    {isPresident ? (
                                        <select className="field" aria-label={`Role of ${m.userFullName}`} value={m.role} disabled={busy} onChange={(e) => changeRole(m, e.target.value)}>
                                            {ROLE_OPTIONS.map((r) => <option key={r} value={r}>{roleLabel(r)}</option>)}
                                        </select>
                                    ) : <span className="tag">{roleLabel(m.role)}</span>}
                                </td>
                                <td className="muted">{formatDate(m.joinedAt)}</td>
                                {canManage && <td style={{ textAlign: 'right' }}><button type="button" className="btn-danger" disabled={busy} onClick={() => remove(m)}>Remove</button></td>}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>
        </>
    );
}