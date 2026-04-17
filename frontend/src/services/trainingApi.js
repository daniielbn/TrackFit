import api from './api.js';

const readData = (request) => request.then((response) => response.data);

export const authApi = {
  register: (payload) => readData(api.post('/auth/register', payload)),
  login: (payload) => readData(api.post('/auth/login', payload))
};

export const activitiesApi = {
  list: () => readData(api.get('/activities')),
  get: (id) => readData(api.get(`/activities/${id}`)),
  create: (payload) => readData(api.post('/activities', payload)),
  update: (id, payload) => readData(api.put(`/activities/${id}`, payload)),
  remove: (id) => api.delete(`/activities/${id}`)
};

export const plannedWorkoutsApi = {
  list: () => readData(api.get('/planned-workouts')),
  create: (payload) => readData(api.post('/planned-workouts', payload)),
  update: (id, payload) => readData(api.put(`/planned-workouts/${id}`, payload)),
  updateStatus: (id, status) => readData(api.patch(`/planned-workouts/${id}/status`, { status })),
  remove: (id) => api.delete(`/planned-workouts/${id}`)
};

export const dashboardApi = {
  getSummary: () => readData(api.get('/dashboard/summary'))
};

export const statsApi = {
  monthly: () => readData(api.get('/stats/monthly')),
  sports: () => readData(api.get('/stats/sports')),
  paceSummary: () => readData(api.get('/stats/pace-summary'))
};
