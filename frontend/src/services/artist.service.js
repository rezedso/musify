import api from '../services/api';

export const getArtists = async (page = 1) => {
  const { data } = await api.get(`/artists/page/${page}`);
  return data;
};

export const getMostRecentArtists = async () => {
  const { data } = await api.get(`/artists/recent`);
  return data;
};

export const getArtistsByGenre = async (page = 1, genreSlug) => {
  const { data } = await api.get(`/artists/genres/${genreSlug}/page/${page}`);
  return data;
};

export const getArtist = async (artistSlug) => {
  const { data } = await api.get(`/artists/${artistSlug}`);
  return data;
};

export const createArtist = async (formData) => {
  const { data } = await api.post(`/artists`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return data;
};

export const updateArtist = async (formData) => {
  const { data } = await api.put(`/artists/${formData.artistId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return data;
};

export const deleteArtist = async (artistId) => {
  const { data } = await api.delete(`/artists/${artistId}`);
  return data;
};
