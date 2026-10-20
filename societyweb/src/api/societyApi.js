import axiosClient from './axiosClient';

export const societyApi = {
    list: async () => (await axiosClient.get('/societies')).data,
    create: async (body) => (await axiosClient.post('/societies', body)).data,

    getDashboard: async (societyId) => (await axiosClient.get(`/dashboard/societies/${societyId}`)).data,

    getMembers: async (societyId) => (await axiosClient.get(`/societies/${societyId}/members`)).data,
    addMember: async (societyId, body) => (await axiosClient.post(`/societies/${societyId}/members`, body)).data,
    updateMemberRole: async (societyId, userId, role) =>
        (await axiosClient.put(`/societies/${societyId}/members/${userId}/role`, null, { params: { role } })).data,
    removeMember: async (societyId, userId) => {
        await axiosClient.delete(`/societies/${societyId}/members/${userId}`);
    },
};