/* eslint-disable react/prop-types */
import { LinearProgress, Stack, Typography } from '@mui/material';

const Genre = ({ genre, totalEntries }) => {
  return (
    <Stack key={genre.genreName} spacing={1}>
      <Stack>
        <Typography>{genre.genreName}</Typography>
      </Stack>
      <Typography>
        {genre.albumCount} {genre.albumCount === 1 ? 'Entry' : 'Entries'}
      </Typography>
      <LinearProgress
        variant='determinate'
        color='primary'
        value={(genre.albumCount / totalEntries) * 100}
        sx={(theme) => ({
          width: '100%',
          height: 15,
          [theme.breakpoints.down('sm')]: {
            display: 'none',
          },
        })}
      />
    </Stack>
  );
};

export default Genre;
