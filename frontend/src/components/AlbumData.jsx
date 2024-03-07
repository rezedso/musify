/* eslint-disable react/prop-types */
import { Rating, Stack, Typography } from '@mui/material';
import { formatDate } from '../lib/utils';
import { useState } from 'react';
import {
  useGetAlbumRatings,
  useGetUserAlbumRating,
  useRateAlbum,
} from '../lib/react-query/queries';
import { Link as RouterLink } from 'react-router-dom';
import UserLists from './UserLists';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';

const AlbumData = ({ album, lists, id }) => {
  const [hover, setHover] = useState(-1);
  const [value, setValue] = useState(0.0);

  const { data: albumRating } = useGetAlbumRatings(album?.id);
  const { data: userAlbumRating } = useGetUserAlbumRating(album?.id);
  const { mutateAsync: rateAlbum, isPending: isPendingRating } = useRateAlbum();

  return (
    <Stack spacing={2} sx={{ overflowY: 'hidden' }}>
      {!album?.albumImage ? (
        <ImageOutlinedIcon sx={{ width: 200, height: 200 }} />
      ) : (
        <img src={album.albumImage} width={200} height={200} />
      )}
      <Stack
        direction='row'
        sx={{ display: 'flex', alignItems: 'center' }}
        spacing={2}
      >
        <Typography variant='subtitle2' sx={{ color: 'text.disabled' }}>
          Artist
        </Typography>
        <Typography
          to={`/artists/${album?.artistSlug}`}
          component={RouterLink}
          sx={{ color: 'text.primary' }}
        >
          {album?.artistName}
        </Typography>
      </Stack>
      <Stack
        direction='row'
        sx={{ display: 'flex', alignItems: 'center' }}
        spacing={2}
      >
        <Typography variant='subtitle2' sx={{ color: 'text.disabled', mt: 1 }}>
          Name
        </Typography>
        <Typography>{album?.title}</Typography>
      </Stack>
      <Stack
        direction='row'
        sx={{ display: 'flex', alignItems: 'center' }}
        spacing={2}
      >
        <Typography variant='subtitle2' sx={{ color: 'text.disabled', mt: 1 }}>
          Released
        </Typography>
        <Typography>{formatDate(album?.releaseDate)}</Typography>
      </Stack>
      <Stack
        direction='row'
        sx={{ display: 'flex', alignItems: 'center' }}
        spacing={2}
      >
        <Typography variant='subtitle2' sx={{ color: 'text.disabled', mt: 1 }}>
          Rating
        </Typography>
        {isPendingRating ? (
          <Typography>Rating...</Typography>
        ) : (
          <Typography>
            <b>
              {!album?.rating
                ? Number(0).toFixed(1)
                : album?.rating?.toFixed(1)}
            </b>
            /5.0 from <b>{albumRating?.totalRatings || 0}</b>{' '}
            {!albumRating?.totalRatings
              ? 'ratings'
              : albumRating?.totalRatings === 1
              ? 'rating'
              : 'ratings'}
          </Typography>
        )}
      </Stack>
      <Stack
        direction='row'
        sx={{ display: 'flex', alignItems: 'center' }}
        spacing={2}
      >
        <Typography variant='subtitle2' sx={{ color: 'text.disabled', mt: 1 }}>
          Genres
        </Typography>
        <Stack direction='row' spacing={1}>
          {album?.genres?.map((genre, i) => (
            <Typography
              to={`/genres/albums/${genre.slug}`}
              key={genre.id}
              component={RouterLink}
              sx={{ color: 'text.primary' }}
            >
              {i === album?.genres.length - 1
                ? `${genre.name}.`
                : `${genre.name} ,`}
            </Typography>
          ))}
        </Stack>
      </Stack>

      <Stack
        direction='row'
        sx={{ display: 'flex', alignItems: 'center' }}
        spacing={2}
      >
        <Typography variant='subtitle2' sx={{ color: 'text.disabled', mt: 1 }}>
          Rate
        </Typography>
        <Stack
          sx={{
            width: 200,
            display: 'flex',
            alignItems: 'center',
          }}
          direction='row'
        >
          <Rating
            value={userAlbumRating?.rating || value}
            precision={0.5}
            onChange={async (event, newValue) => {
              setValue(newValue);
              await rateAlbum({
                rating: newValue?.toFixed(1),
                albumId: album?.id,
              });
            }}
            onChangeActive={(event, newHover) => {
              setHover(newHover);
            }}
          />

          <Stack sx={{ ml: 2 }}>
            {hover !== -1
              ? hover.toFixed(1)
              : !userAlbumRating
              ? Number(0).toFixed(1)
              : userAlbumRating?.rating?.toFixed(1)}
          </Stack>
        </Stack>
      </Stack>
      <UserLists albumId={album?.id} lists={lists} id={id} />
    </Stack>
  );
};

export default AlbumData;
