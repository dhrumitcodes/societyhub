import axiosClient from './axiosClient';

export const authApi = {
    login: async (credentials) => {
        const { data } = await axiosClient.post('/auth/login', credentials);
        if (data.token) localStorage.setItem('token', data.token);
        return data;
    },

    register: async (userData) => {
        const { data } = await axiosClient.post('/auth/register', userData);
        if (data.token) localStorage.setItem('token', data.token);
        return data;
    },
    me: async () => (await axiosClient.get('/users/me')).data,

    changePassword: async (body) => { await axiosClient.post('/users/me/password', body); },

    logout: () => localStorage.removeItem('token'),
};