import api from './api';

export const getArtistFollowers = async (artistId) => {
  const { data } = await api.get(`/followers/artists/${artistId}`);
  return data;
};

export const getUserFollowedArtists = async (username) => {
  const { data } = await api.get(`/followers/${username}`);
  return data;
};

export const getArtistFollowersCount = async (artistId) => {
  const { data } = await api.get(`/followers/count/${artistId}`);
  return data;
};

export const isUserFollowing = async (artistId) => {
  const { data } = await api.get(`/followers/is-following/${artistId}`);
  return data;
};

export const followArtist = async (artistId) => {
  const { data } = await api.post(`/followers/follow/${artistId}`);
  return data;
};

export const unFollowArtist = async (artistId) => {
  const { data } = await api.delete(`/followers/unfollow/${artistId}`);
  return data;
};
