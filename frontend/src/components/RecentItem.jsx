/* eslint-disable react/prop-types */
import { Grid, Rating, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { formatDateToYear } from '../lib/utils';

const RecentItem = ({ data, album }) => {
  return (
    <Grid container sx={{ mb: 2 }}>
      <Grid item xs={4} md={4} sm={3}>
        <Stack
          component={RouterLink}
          to={`/artists/${data.artistSlug}/${data.albumSlug || data.slug}`}
          sx={{
            color: 'text.primary',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100%',
          }}
        >
          {!data?.albumImage ? (
            <ImageOutlinedIcon sx={{ width: 80, height: 80 }} />
          ) : (
            <img src={data.albumImage} width={80} height={80} />
          )}
        </Stack>
      </Grid>
      <Grid item xs={8} md={8} sm={9}>
        <Stack>
          <Typography
            to={`/artists/${data?.artistSlug}`}
            component={RouterLink}
            sx={{ color: 'text.primary' }}
          >
            <b>{data?.artistName}</b>
          </Typography>
          <Typography
            to={`/artists/${data?.artistSlug}/${data?.albumSlug || data?.slug}`}
            component={RouterLink}
            sx={{ color: 'text.primary' }}
          >
            {data?.albumTitle || data?.title}
          </Typography>
          <Typography>({formatDateToYear(data?.releaseDate)})</Typography>
          {!album && (
            <Stack direction='row' sx={{ alignItems: 'center' }} spacing={2}>
              <Rating
                name='read-only'
                size='small'
                value={data?.rating}
                precision={0.5}
                readOnly
              />
              <Typography
                to={`/users/${data?.username}`}
                component={RouterLink}
                sx={{ color: 'text.primary' }}
              >
                by <b>{data?.username}</b>
              </Typography>
            </Stack>
          )}
        </Stack>
      </Grid>
    </Grid>
  );
};

export default RecentItem;
