/* eslint-disable react/prop-types */
import { Grid, Stack, Typography } from '@mui/material';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { Link as RouterLink } from 'react-router-dom';

const RecentArtist = ({ artist }) => {
  return (
    <Grid key={artist.id} container sx={{ mb: 2 }}>
      <Grid item xs={4} md={4} sm={3}>
        <Stack
          to={`/artists/${artist.slug}`}
          component={RouterLink}
          sx={{
            color: 'text.primary',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100%',
          }}
        >
          {!artist?.image ? (
            <ImageOutlinedIcon sx={{ width: 80, height: 80 }} />
          ) : (
            <img src={artist.image} width={80} height={80} />
          )}
        </Stack>
      </Grid>
      <Grid item xs={8} md={8} sm={9}>
        <Stack>
          <Typography
            to={`/artists/${artist?.slug}`}
            component={RouterLink}
            sx={{ color: 'text.primary' }}
          >
            <b>{artist?.name}</b>
          </Typography>
          {artist.artistGenres.map((genre) => (
            <Typography
              key={genre.id}
              to={`/genres/artists/${genre.slug}`}
              component={RouterLink}
              sx={{ color: 'text.primary' }}
              variant='subtitle2'
            >
              {genre.name}
            </Typography>
          ))}
        </Stack>
      </Grid>
    </Grid>
  );
};

export default RecentArtist;
