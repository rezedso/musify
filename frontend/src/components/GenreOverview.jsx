/* eslint-disable react/prop-types */
import { Paper, Stack, Typography } from '@mui/material';
import Genre from './Genre';

const GenreOverview = ({ genreOverview, totalEntries }) => {
  return (
    <Stack sx={{ mt: 2 }}>
      <Typography variant='h6'>
        <b>Genre Overview</b>
      </Typography>
      <Stack
        direction={{ sm: 'row', xs: 'column' }}
        sx={(theme) => ({
          alignItems: 'center',
          justifyContent: 'center',
          mt: 2,
          p: 2,
          [theme.breakpoints.down('sm')]: {
            alignItems: 'start',
          },
        })}
        component={Paper}
        spacing={{ sm: 4, xs: 2 }}
      >
        {genreOverview?.map((genre) => (
          <Genre
            key={genre.genreName}
            genre={genre}
            totalEntries={totalEntries}
          />
        ))}
      </Stack>
    </Stack>
  );
};

export default GenreOverview;
