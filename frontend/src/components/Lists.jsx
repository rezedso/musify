/* eslint-disable react/prop-types */
import { Link, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const Lists = ({ listsCount, listsData, username }) => {
  return (
    <Stack
      direction='column'
      sx={{ alignItems: 'start', p: 2 }}
      component={Paper}
    >
      <Typography
        sx={{ fontSize: '1.2rem', mb: listsData?.length > 0 ? 1 : 0 }}
      >
        Lists ({listsCount?.count})
      </Typography>
      {listsData?.length > 0 &&
        listsData.map((list) => (
          <Link
            to={`/lists/${username}/${list.name}`}
            component={RouterLink}
            key={list.id}
            sx={{ textDecoration: 'underline', mb: 1 }}
          >
            {list.name}
          </Link>
        ))}
    </Stack>
  );
};

export default Lists;
