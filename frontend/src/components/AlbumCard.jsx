/* eslint-disable react/prop-types */
import { Grid, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { formatDate } from '../lib/utils';
import UserLists from './UserLists';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';

const AlbumCard = ({ album, index, lists }) => {
  return (
    <Grid container sx={{ mb: 2 }}>
      <Grid item xs={12} sm={2}>
        <Stack
          component={RouterLink}
          to={`/artists/${album.artistSlug}/${album.slug}`}
          sx={{
            color: 'text.primary',
            justifyContent: 'center',
            alignItems: 'center',
          }}
        >
          {!album?.albumImage ? (
            <ImageOutlinedIcon sx={{ height: '50%', width: '50%' }} />
          ) : (
            <img src={album.albumImage} />
          )}
        </Stack>
      </Grid>
      <Grid item xs={12} sm={1}>
        <Typography
          sx={(theme) => ({
            textAlign: 'center',
            [theme.breakpoints.down('sm')]: {
              fontSize: '1.75rem',
            },
          })}
        >
          <b>{index + 1}.</b>
        </Typography>
      </Grid>
      <Grid item xs={9}>
        <Stack direction='column' spacing={1} sx={{ mb: 2 }}>
          <Stack direction='column' sx={{ alignItems: 'start' }}>
            <Typography
              to={`/artists/${album?.artistSlug}/${album?.slug}`}
              component={RouterLink}
              sx={{ color: 'text.primary' }}
            >
              {album?.title}
            </Typography>
            <Typography
              to={`/artists/${album?.artistSlug}`}
              component={RouterLink}
              sx={{ color: 'text.primary' }}
            >
              <b>{album?.artistName}</b>
            </Typography>
          </Stack>
          <Typography>{formatDate(album?.releaseDate)}</Typography>
          <Typography>
            Average Rating:{' '}
            <b>{!album?.rating ? Number('0.0') : album?.rating?.toFixed(1)}</b>
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
          <UserLists albumId={album?.id} lists={lists} card />
        </Stack>
      </Grid>
    </Grid>
  );
};

export default AlbumCard;
