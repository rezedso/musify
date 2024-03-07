import api from './api';

export const getAlbumRatings = async (albumId) => {
  const { data } = await api.get(`/album-ratings/${albumId}`);
  return data;
};

export const getUserAlbumRating = async (albumId) => {
  const { data } = await api.get(`/album-ratings/albums/${albumId}`);
  return data;
};

export const getUserGenreOverview = async (username) => {
  const { data } = await api.get(
    `/album-ratings/genre-overview/users/${username}`
  );
  return data;
};

export const getRecentAlbumRatings = async () => {
  const { data } = await api.get(`/album-ratings/recent`);
  return data;
};

export const getUserAlbumRatings = async (userId) => {
  const { data } = await api.get(`/album-ratings/users/${userId}`);
  return data;
};

export const getUserAlbumRatingsByUserAndRating = async (username, rating) => {
  const { data } = await api.get(
    `/album-ratings/users/${username}/rating/${rating}`
  );
  return data;
};
