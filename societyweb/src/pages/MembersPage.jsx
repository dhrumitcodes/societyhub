import { useEffect, useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { societyApi } from '../api/societyApi';
import { formatDate } from '../utils/formatDate';
import { roleLabel } from '../utils/roles';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

export default function MembersPage() {
    const { societyId } = useOutletContext();
    const [members, setMembers] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancelled = false;
        setMembers(null);
        setError(null);
        societyApi.getMembers(societyId)
            .then((d) => { if (!cancelled) setMembers(d); })
            .catch((e) => { if (!cancelled) setError(e); });
        return () => { cancelled = true; };
    }, [societyId]);

    if (error) return <ErrorMessage error={error} />;
    if (!members) return <Loader />;

    return (
        <>
            <h1 className="greet">Members</h1>
            <p className="muted greet-sub">{members.length} {members.length === 1 ? 'member' : 'members'} in this society.</p>
            <section className="panel table-wrap">
                {members.length === 0 ? <p className="muted empty">No members yet.</p> : (
                    <table>
                        <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Joined</th></tr></thead>
                        <tbody>
                        {members.map((m) => (
                            <tr key={m.id}>
                                <td><strong>{m.userFullName}</strong></td>
                                <td className="muted">{m.userEmail}</td>
                                <td><span className="tag">{roleLabel(m.role)}</span></td>
                                <td className="muted">{formatDate(m.joinedAt)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>
        </>
    );
}