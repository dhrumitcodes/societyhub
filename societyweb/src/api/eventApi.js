import axiosClient from './axiosClient';

export const eventApi = {
    getEvents: async (params) => {
        const response = await axiosClient.get('/events', { params });
        return response.data;
    },

    getEventById: async (societyId, eventId) => {
        const response = await axiosClient.get(`/societies/${societyId}/events/${eventId}`);
        return response.data;
    },

    registerForEvent: async (eventId) => {
        const response = await axiosClient.post(`/events/${eventId}/register`);
        return response.data;
    },
};