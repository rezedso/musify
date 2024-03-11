/* eslint-disable react/prop-types */

import {
  Avatar,
  Button,
  Grid,
  Paper,
  Rating,
  Stack,
  Typography,
} from '@mui/material';
import { formatDateDistance, formatDateToYear } from '../lib/utils';
import { useGetAlbumRatings } from '../lib/react-query/queries';
import { Link as RouterLink } from 'react-router-dom';
import { useState } from 'react';

const ReviewCard = ({ review }) => {
  const [expanded, setExpanded] = useState(false);
  const { data: albumRating } = useGetAlbumRatings(review?.album?.id);

  const contentLines = review.content.split('\n');

  const handleExpand = () => {
    setExpanded((prevState) => !prevState);
  };
  // console.log(review?.content);
  return (
    <Stack component={Paper} sx={{ mb: 2 }}>
      <Grid container spacing={1}>
        <Grid item xs={12} sm={6}>
          <Stack
            direction='column'
            sx={{ alignItems: 'center', p: 2 }}
            spacing={1}
          >
            <Stack
              to={`/artists/${review?.album.artistSlug}/${review.album.slug}`}
              component={RouterLink}
            >
              <img src={review.album.albumImage} width={350} height={350} />
            </Stack>
            {albumRating?.rating ? (
              <Typography variant='h6' sx={{ fontSize: '1.1rem' }}>
                <b>{`${albumRating?.rating?.toFixed(1)}`}</b>
                /5.0 from{' '}
                {albumRating?.totalRatings === 1
                  ? '1 rating'
                  : `${albumRating?.totalRatings} ratings`}
              </Typography>
            ) : (
              <Typography variant='h6' sx={{ fontSize: '1.1rem' }}>
                Not rated yet.
              </Typography>
            )}
            <Typography
              variant='subtitle2'
              sx={{ color: 'text.disabled', mt: 1 }}
            >
              Genres
            </Typography>
            {review.album.genres.map((genre) => (
              <Typography
                key={genre.id}
                to={`/genres/albums/${genre.slug}`}
                component={RouterLink}
                sx={{ color: 'text.primary' }}
              >
                {genre.name}
              </Typography>
            ))}
          </Stack>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Stack sx={{ p: 2 }} spacing={2}>
            <Stack spacing={1}>
              <Typography
                variant='h5'
                to={`/artists/${review?.album.artistSlug}/${review.album.slug}`}
                component={RouterLink}
                sx={{ color: 'text.primary' }}
              >
                {review.album.title} (
                {formatDateToYear(review.album.releaseDate)})
              </Typography>
              <Typography
                variant='h5'
                to={`/artists/${review?.album.artistSlug}`}
                component={RouterLink}
                sx={{ color: 'text.primary' }}
              >
                <b>{review.album.artistName}</b>
              </Typography>
            </Stack>
            <Stack direction='row' sx={{ alignItems: 'center' }} spacing={2}>
              <Avatar
                component={RouterLink}
                to={`/users/${review?.username}`}
                sx={{ mr: 1 }}
                src={review.userImage}
              />
              <Typography>Review by </Typography>
              <Typography
                to={`/users/${review?.username}`}
                component={RouterLink}
                sx={{ color: 'text.primary' }}
              >
                <b>{review.username}</b>
              </Typography>
              <Typography variant='subtitle2'>
                {formatDateDistance(review.createdAt)}
              </Typography>
            </Stack>
            <Rating
              name='read-only'
              size='small'
              value={review.rating}
              precision={0.5}
              readOnly
            />
            <Stack sx={{ mt: 2 }}>
              <Typography variant='h6' sx={{ mb: 2 }}>
                <b>{review.title}</b>
              </Typography>
              {contentLines.length > 3 && !expanded ? (
                <>
                  <Typography>{contentLines.slice(0, 3).join('\n')}</Typography>
                  <Button
                    sx={{ my: 2 }}
                    variant='contained'
                    onClick={handleExpand}
                  >
                    Expand review
                  </Button>
                </>
              ) : (
                <>
                  <Typography>
                    {expanded ? review.content : contentLines.join('\n')}
                  </Typography>
                  {expanded && (
                    <Button
                      sx={{ my: 2 }}
                      variant='contained'
                      onClick={handleExpand}
                    >
                      Hide
                    </Button>
                  )}
                </>
              )}
            </Stack>
          </Stack>
        </Grid>
      </Grid>
    </Stack>
  );
};

export default ReviewCard;
