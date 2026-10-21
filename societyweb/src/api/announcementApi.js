import axiosClient from './axiosClient';

export const announcementApi = {
    list: async (societyId) => (await axiosClient.get(`/societies/${societyId}/announcements`)).data,
    create: async (societyId, body) => (await axiosClient.post(`/societies/${societyId}/announcements`, body)).data,
    remove: async (societyId, id) => {
        await axiosClient.delete(`/societies/${societyId}/announcements/${id}`);
    },
};