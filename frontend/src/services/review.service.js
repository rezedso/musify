import api from './api';

export const getUserReviews = async (username, page = 1) => {
  const { data } = await api.get(`/reviews/users/${username}/page/${page}`);
  return data;
};

export const getReviews = async (page = 1) => {
  const { data } = await api.get(`/reviews/page/${page}`);
  return data;
};

export const getAlbumReviews = async (albumId, page = 1) => {
  const { data } = await api.get(`/reviews/albums/${albumId}/page/${page}`);
  return data;
};

export const existsReview = async (albumId) => {
  const { data } = await api.get(`/reviews/exists-review/${albumId}`);
  return data;
};

export const createReview = async ({ albumId, review }) => {
  console.log('review:', review);
  const { data } = await api.post(`/reviews/albums/${albumId}`, review);
  return data;
};

export const updateReview = async ({ reviewId, userId, review }) => {
  const { data } = await api.put(`/reviews/${reviewId}`, { ...review, userId });
  return data;
};

export const deleteReview = async ({ reviewId, id }) => {
  const { data } = await api.delete(`/reviews/${reviewId}`, { data: { id } });
  return data;
};
