import axiosClient from './axiosClient';

export const joinRequestApi = {
    create: async (societyId, message) =>
        (await axiosClient.post(`/societies/${societyId}/join-requests`, { message })).data,
    mine: async () => (await axiosClient.get('/join-requests/mine')).data,
    pending: async (societyId) => (await axiosClient.get(`/societies/${societyId}/join-requests`)).data,
    approve: async (societyId, id) =>
        (await axiosClient.post(`/societies/${societyId}/join-requests/${id}/approve`)).data,
    decline: async (societyId, id) =>
        (await axiosClient.post(`/societies/${societyId}/join-requests/${id}/decline`)).data,
};