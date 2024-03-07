/* eslint-disable react/prop-types */
import { Link, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';

const FollowedArtists = ({ userFollowedArtists }) => {
  return (
    <Stack
      direction='column'
      sx={{ alignItems: 'start', p: 2 }}
      component={Paper}
      spacing={2}
    >
      <Typography
        sx={{
          fontSize: '1.2rem',
        }}
      >
        Following Artists ({userFollowedArtists.length})
      </Typography>
      {userFollowedArtists.map((artist) => (
        <Stack
          key={artist.id}
          direction='row'
          sx={{ alignItems: 'center' }}
          spacing={1}
        >
          <Stack
            component={RouterLink}
            to={`/artists/${artist.slug}`}
            sx={{ color: 'text.primary' }}
          >
            {!artist?.image ? (
              <ImageOutlinedIcon sx={{ width: 50, height: 50 }} />
            ) : (
              <img src={artist.image} width={50} height={50} />
            )}
          </Stack>
          <Link
            to={`/artists/${artist.slug}`}
            component={RouterLink}
            sx={{ mb: 1 }}
          >
            {artist.name}
          </Link>
        </Stack>
      ))}
    </Stack>
  );
};

export default FollowedArtists;
