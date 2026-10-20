import axiosClient from './axiosClient';

export const societyApi = {
    getDashboard: async (societyId) => (await axiosClient.get(`/dashboard/societies/${societyId}`)).data,
    getMembers: async (societyId) => (await axiosClient.get(`/societies/${societyId}/members`)).data,
};