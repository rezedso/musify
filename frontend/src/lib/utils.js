import { format, formatDistanceToNow, fromUnixTime } from 'date-fns';

export const formatDateDistance = (date) => {
  return formatDistanceToNow(new Date(date * 1000), {
    addSuffix: true,
  });
};

export const formatDate = (date) => {
  if (date) {
    return format(fromUnixTime(date), 'dd MMM yyyy');
  }
};

export const formatDateToYear = (date) => {
  if (date) {
    return format(fromUnixTime(date), 'yyyy');
  }
};

export const formatSlug = (slug) => {
  return slug
    .split('-')
    .join(' ')
    .replace(/\b\w/g, (char) => char.toUpperCase());
};

export const ratingList = [5.0, 4.5, 4.0, 3.5, 3.0, 2.5, 2.0, 1.5, 1.0, 0.5];

export const calculateRatingsData = (ratings) => {
  const ratingCounts = ratingList.reduce((counts, targetRating) => {
    const count = ratings?.reduce(
      (acc, album) => (album.rating === targetRating ? acc + 1 : acc),
      0
    );

    return [...counts, { rating: targetRating, count }];
  }, []);

  return ratingCounts.map((item, index) => ({
    id: index + 1,
    ...item,
  }));
};

export const ROLES = [
  {
    id: 1,
    name: 'ROLE_USER',
  },
  {
    id: 2,
    name: 'ROLE_ADMIN',
  },
];
