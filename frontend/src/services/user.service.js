import api from './api';

export const getUser = async (username) => {
  const { data } = await api.get(`/users/${username}`);
  return data;
};

export const getUsers = async () => {
  const { data } = await api.get(`/users`);
  return data;
};

export const updateUser = async (user) => {
  const { data } = await api.put(`/users/update`, user, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return data;
};

export const updatePassword = async (password) => {
  const { data } = await api.put(`/users/update-password`, password);
  return data;
};

export const updateUserRole = async ({ userId, role, addRole }) => {
  const { data } = await api.put(
    `users/${userId}/update-role?addRole=${addRole}`,
    role
  );
  return data;
};

export const deleteUser = async (userId) => {
  const { data } = await api.delete(`/users/${userId}`);
  return data;
};
