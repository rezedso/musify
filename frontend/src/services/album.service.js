import api from '../services/api';

export const getAlbums = async (page = 1) => {
  const { data } = await api.get(`/albums/page/${page}`);
  return data;
};

export const getMostRecentAlbums = async () => {
  const { data } = await api.get(`/albums/recent`);
  return data;
};

export const getAlbum = async (artistSlug, albumSlug) => {
  const { data } = await api.get(`/albums/${artistSlug}/${albumSlug}`);
  return data;
};

export const getAlbumsByArtist = async (artistSlug) => {
  const { data } = await api.get(`/albums/artists/${artistSlug}`);
  return data;
};

export const getAlbumsByGenre = async (page = 1, genreSlug) => {
  const { data } = await api.get(`/albums/genres/${genreSlug}/page/${page}`);
  return data;
};

export const createAlbum = async (formData) => {
  const { data } = await api.post(`/albums`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return data;
};

export const updateAlbum = async (formData) => {
  const { data } = await api.put(`/albums/${formData.albumId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return data;
};

export const rateAlbum = async ({ rating, albumId }) => {
  const { data } = await api.put(`/albums/rate/${albumId}`, rating);
  return data;
};

export const addAlbumToList = async ({ listId, albumId, id }) => {
  const { data } = await api.post(`/albums/add/${listId}/${albumId}`, id);
  return data;
};

export const removeAlbumFromList = async ({ listId, albumId, id }) => {
  console.log('id from req', id);
  const { data } = await api.delete(`/albums/remove/${listId}/${albumId}`, {
    data: id,
  });
  return data;
};

export const deleteAlbum = async (albumId) => {
  const { data } = await api.delete(`/albums/${albumId}`);
  return data;
};
