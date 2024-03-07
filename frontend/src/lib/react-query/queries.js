import { toast } from 'react-toastify';
import {
  useInfiniteQuery,
  useMutation,
  useQuery,
  useQueryClient,
} from '@tanstack/react-query';
import {
  getCurrentUser,
  login,
  registerUser,
} from '../../services/auth.service';
import {
  deleteUser,
  getUser,
  getUsers,
  updatePassword,
  updateUser,
  updateUserRole,
} from '../../services/user.service';
import { useNavigate } from 'react-router-dom';
import {
  createArtist,
  deleteArtist,
  getArtist,
  getArtists,
  getArtistsByGenre,
  getMostRecentArtists,
  updateArtist,
} from '../../services/artist.service';
import {
  addAlbumToList,
  createAlbum,
  deleteAlbum,
  getAlbum,
  getAlbumsByArtist,
  getAlbumsByGenre,
  getAlbums,
  rateAlbum,
  removeAlbumFromList,
  updateAlbum,
  getMostRecentAlbums,
} from '../../services/album.service';
import {
  createList,
  deleteList,
  getList,
  getUserListsCount,
  getUserListsSummary,
  updateList,
} from '../../services/album.list.service';
import {
  getAlbumRatings,
  getRecentAlbumRatings,
  getUserAlbumRating,
  getUserAlbumRatings,
  getUserAlbumRatingsByUserAndRating,
  getUserGenreOverview,
} from '../../services/album.rating.service';
import {
  createReview,
  deleteReview,
  existsReview,
  getAlbumReviews,
  getRecentReviews,
  getUserReviews,
  updateReview,
} from '../../services/review.service';
import {
  followArtist,
  getArtistFollowers,
  getArtistFollowersCount,
  getUserFollowedArtists,
  isUserFollowing,
  unFollowArtist,
} from '../../services/follower.service';

/* ***** USERS ***** */
export const useLogIn = () => {
  return useMutation({
    mutationFn: (user) => login(user),
    onError: (res) => {
      if (res.response.status === 401) {
        toast.error('Bad credentials.');
      }
    },
  });
};

export const useRegisterUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (user) => registerUser(user),
    onSuccess: (res) => {
      toast.success(res.data.message);
      queryClient.invalidateQueries('users');
    },
    onError: (error) => {
      if (error.response.status === 409) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.response.data.error);
      }
    },
  });
};

export const useGetUser = (username) => {
  return useQuery({
    queryKey: ['user', username],
    queryFn: () => getUser(username),
    enabled: !!username,
  });
};

export const useGetUsers = () => {
  return useQuery({
    queryKey: ['users'],
    queryFn: () => getUsers(),
  });
};

export const useUpdateUser = () => {
  const user = getCurrentUser();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data) => updateUser(data),
    onSettled: async (res) => {
      const updatedUser = {
        ...user,
        username: res.username,
        imageUrl: res.imageUrl,
      };
      await queryClient.invalidateQueries('users');
      localStorage.setItem('user', JSON.stringify(updatedUser));
      toast.success('User updated.');
      navigate('/');
    },
    onError: (error) => {
      console.log(error);
      if (error.response.status === 409) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.response.data.error);
      }
    },
  });
};

export const useUpdateUserRole = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userId, role, addRole) =>
      updateUserRole(userId, role, addRole),
    onSuccess: () => {
      queryClient.invalidateQueries('users');
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useUpdatePassword = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (password) => updatePassword(password),
    onSuccess: () => {
      toast.success('Password updated.');
      queryClient.invalidateQueries('users');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useDeleteUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userId) => deleteUser(userId),
    onSuccess: () => {
      queryClient.invalidateQueries('users');
      toast.success('User deleted.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

/* ***** ARTISTS ***** */

export const useGetArtists = () => {
  return useInfiniteQuery({
    queryKey: ['artists'],
    queryFn: ({ pageParam = 1 }) => getArtists(pageParam),
    getNextPageParam: (lastPage) => {
      const { totalPages, currentPage } = lastPage;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
  });
};

export const useGetArtistsByGenre = (genreSlug) => {
  return useInfiniteQuery({
    queryKey: ['artists', genreSlug],
    queryFn: ({ pageParam = 1 }) => getArtistsByGenre(pageParam, genreSlug),
    getNextPageParam: (lastPage) => {
      const { totalPages, currentPage } = lastPage;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
    enabled: !!genreSlug,
  });
};

export const useGetMostRecentArtists = () => {
  return useQuery({
    queryKey: ['most-recent-artists'],
    queryFn: () => getMostRecentArtists(),
  });
};

export const useGetArtist = (artistSlug) => {
  return useQuery({
    queryKey: ['artists', artistSlug],
    queryFn: () => getArtist(artistSlug),
    enabled: !!artistSlug,
  });
};

export const useCreateArtist = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (formData) => createArtist(formData),
    onSuccess: () => {
      toast.success('Artist created.');
      queryClient.invalidateQueries(['artists']);
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useUpdateArtist = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (formData) => updateArtist(formData),
    onSuccess: (res) => {
      queryClient.invalidateQueries(['artists', 'albums']);
      navigate(`/artists/${res.slug}`);
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useDeleteArtist = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (artistId) => deleteArtist(artistId),
    onSuccess: () => {
      queryClient.invalidateQueries(['artists', 'albums']);
      toast.success('Artist deleted.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

/* ***** ALBUMS ***** */
export const useGetAlbums = () => {
  return useInfiniteQuery({
    queryKey: ['albums'],
    queryFn: ({ pageParam = 1 }) => getAlbums(pageParam),
    getNextPageParam: (lastPage) => {
      const { totalPages, currentPage } = lastPage;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
  });
};

export const useGetAlbumsByArtist = (artistSlug) => {
  return useQuery({
    queryKey: ['albums', artistSlug],
    queryFn: () => getAlbumsByArtist(artistSlug),
    enabled: !!artistSlug,
  });
};

export const useGetMostRecentAlbums = () => {
  return useQuery({
    queryKey: ['most-recent-albums'],
    queryFn: () => getMostRecentAlbums(),
  });
};

export const useGetAlbumsByGenre = (genreSlug) => {
  return useInfiniteQuery({
    queryKey: ['albums', genreSlug],
    queryFn: ({ pageParam = 1 }) => getAlbumsByGenre(pageParam, genreSlug),
    getNextPageParam: (lastPage) => {
      const { totalPages, currentPage } = lastPage;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
    enabled: !!genreSlug,
  });
};

export const useCreateAlbum = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (formData) => createAlbum(formData),
    onSuccess: () => {
      toast.success('Album created.');
      queryClient.invalidateQueries(['albums', 'reviews']);
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useUpdateAlbum = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (formData) => updateAlbum(formData),
    onSuccess: () => {
      queryClient.invalidateQueries(['albums']);
      toast.success('Album updated.');
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useRateAlbum = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (rating, albumId) => rateAlbum(rating, albumId),
    onSuccess: () => {
      queryClient.invalidateQueries('albums');
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useAddAlbumToList = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (listId, albumId, id) => addAlbumToList(listId, albumId, id),
    onSuccess: (res, listId) => {
      queryClient.invalidateQueries(['list', listId]);
      toast.success('Album added to list.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else if (error.response.status === 500) {
        toast.error('Required request body is missing.');
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useRemoveAlbumFromList = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (listId, albumId, id) =>
      removeAlbumFromList(listId, albumId, id),
    onSuccess: (res, listId) => {
      queryClient.invalidateQueries(['list', listId]);
      toast.success('Album removed from list.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else if (error.response.status === 500) {
        toast.error('Required request body is missing.');
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

/* ***** ALBUM LISTS ***** */
export const useGetUserListsCount = (username) => {
  return useQuery({
    queryKey: ['lists-count', username],
    queryFn: () => getUserListsCount(username),
    enabled: !!username,
  });
};

export const useGetUserListsSummary = (username) => {
  return useQuery({
    queryKey: ['lists-summary', username],
    queryFn: () => getUserListsSummary(username),
    enabled: !!username,
  });
};

export const useGetList = (listName, username) => {
  return useQuery({
    queryKey: ['list', listName, username],
    queryFn: () => getList(listName, username),
    enabled: !!listName && !!username,
  });
};

export const useGetAlbum = (artistSlug, albumSlug) => {
  return useQuery({
    queryKey: ['album', albumSlug],
    queryFn: () => getAlbum(artistSlug, albumSlug),
    enabled: !!artistSlug && !!albumSlug,
  });
};

export const useCreateList = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (list) => createList(list),
    onSuccess: () => {
      queryClient.invalidateQueries(['lists', 'lists-summary']);
      toast.success('Album list created.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useUpdateList = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (listId, list) => updateList(listId, list),
    onSuccess: () => {
      toast.success('List updated.');
      queryClient.invalidateQueries(['lists']);
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useDeleteList = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (listId, id) => deleteList(listId, id),
    onSuccess: () => {
      queryClient.invalidateQueries(['lists']);
      toast.success('List removed.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

/* ***** ALBUM RATINGS ***** */
export const useGetAlbumRatings = (albumId) => {
  return useQuery({
    queryKey: ['album-ratings', albumId],
    queryFn: () => getAlbumRatings(albumId),
    enabled: !!albumId,
  });
};

export const useGetUserGenreOverview = (username) => {
  return useQuery({
    queryKey: ['genre-overview', username],
    queryFn: () => getUserGenreOverview(username),
    enabled: !!username,
  });
};

export const useGetRecentAlbumRatings = () => {
  return useQuery({
    queryKey: ['recent-album-ratings'],
    queryFn: () => getRecentAlbumRatings(),
  });
};

export const useGetUserAlbumRating = (albumId) => {
  return useQuery({
    queryKey: ['album-rating', albumId],
    queryFn: () => getUserAlbumRating(albumId),
    enabled: !!albumId,
  });
};

export const useGetUserAlbumRatings = (userId) => {
  return useQuery({
    queryKey: ['album-ratings', userId],
    queryFn: () => getUserAlbumRatings(userId),
    enabled: !!userId,
  });
};

export const useGetAlbumRatingsByUserAndRating = (username, rating) => {
  return useQuery({
    queryKey: ['album-ratings', username, rating],
    queryFn: () => getUserAlbumRatingsByUserAndRating(username, rating),
    enabled: !!username && !!rating,
  });
};

export const useDeleteAlbum = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (albumId) => deleteAlbum(albumId),
    onSuccess: () => {
      queryClient.invalidateQueries(['albums', 'reviews']);
      toast.success('Album deleted.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

/* ***** FOLLOWERS ***** */

export const useGetArtistFollowers = (artistId) => {
  return useQuery({
    queryKey: ['followers', artistId],
    queryFn: () => getArtistFollowers(artistId),
    enabled: !!artistId,
  });
};

export const useGetUserFollowedArtists = (username) => {
  return useQuery({
    queryKey: ['followers', username],
    queryFn: () => getUserFollowedArtists(username),
    enabled: !!username,
  });
};

export const useGetArtistFollowersCount = (artistId) => {
  return useQuery({
    queryKey: ['followers-count', artistId],
    queryFn: () => getArtistFollowersCount(artistId),
    enabled: !!artistId,
  });
};

export const useIsUserFollowing = (artistId) => {
  return useQuery({
    queryKey: ['followers', artistId],
    queryFn: () => isUserFollowing(artistId),
    enabled: !!artistId,
  });
};

export const useFollowArtist = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (artistId) => followArtist(artistId),
    onSuccess: (res) => {
      toast.success(`Following '${res.artist.name}'.`);
      queryClient.invalidateQueries('followers');
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useUnFollowArtist = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (artistId) => unFollowArtist(artistId),
    onSuccess: (res) => {
      queryClient.invalidateQueries(['following']);
      toast.success(`${res.message}`);
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

/* ***** REVIEWS ***** */

export const useGetUserReviews = () => {
  return useInfiniteQuery({
    queryKey: ['reviews'],
    queryFn: ({ pageParam = 1 }) => getUserReviews(pageParam),
    getNextPageParam: (lastPage) => {
      const { totalPages, currentPage } = lastPage;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
  });
};

export const useGetRecentReviews = () => {
  return useQuery({
    queryKey: ['recent-reviews'],
    queryFn: () => getRecentReviews(),
  });
};

export const useGetAlbumReviews = (albumId) => {
  return useInfiniteQuery({
    queryKey: ['album-reviews'],
    queryFn: ({ pageParam = 1 }) => getAlbumReviews(albumId, pageParam),
    getNextPageParam: (lastPage) => {
      const { totalPages, currentPage } = lastPage;
      return currentPage < totalPages ? currentPage + 1 : undefined;
    },
    enabled: !!albumId,
  });
};

export const useExistsReview = (albumId) => {
  return useQuery({
    queryKey: ['exists-review', albumId],
    queryFn: () => existsReview(albumId),
    enabled: !!albumId,
  });
};

export const useCreateReview = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (albumId, review) => createReview(albumId, review),
    onSuccess: () => {
      toast.success('Review created.');
      queryClient.invalidateQueries('reviews');
    },
    onError: (error) => {
      console.log(error);
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useUpdateReview = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (reviewId, userId, review) =>
      updateReview(reviewId, userId, review),
    onSuccess: () => {
      toast.success('Review updated.');
      queryClient.invalidateQueries('reviews');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};

export const useDeleteReview = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (reviewId, id) => deleteReview(reviewId, id),
    onSuccess: () => {
      queryClient.invalidateQueries(['reviews']);
      toast.success('Review deleted.');
    },
    onError: (error) => {
      if (error.status === 400) {
        toast.error(error.message);
      } else {
        toast.error(error.response.data.message);
      }
    },
  });
};
