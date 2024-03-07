/* eslint-disable react/prop-types */
import { Link, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';

const Artist = ({ artist }) => {
  return (
    <Stack
      direction='row'
      spacing={2}
      sx={{ display: 'flex', alignItems: 'center', mb: 2 }}
    >
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
          <ImageOutlinedIcon sx={{ width: 100, height: 100 }} />
        ) : (
          <img src={artist.image} width={100} height={100} />
        )}
      </Stack>
      <Stack>
        <Link to={`/artists/${artist.slug}`} component={RouterLink}>
          <Typography>
            <b>{artist?.name}</b> ({artist?.formedYear})
          </Typography>
        </Link>
        <Typography variant='subtitle1'>{artist?.originCountry}.</Typography>
        {artist.artistGenres.map((genre) => (
          <Typography
            key={genre.id}
            to={`/genres/albums/${genre.slug}`}
            component={RouterLink}
            sx={{ color: 'text.primary' }}
            variant='subtitle2'
          >
            {genre.name}
          </Typography>
        ))}
      </Stack>
    </Stack>
  );
};

export default Artist;
