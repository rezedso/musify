/* eslint-disable react/prop-types */
import { LinearProgress, Rating, Typography, Stack } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Link as RouterLink } from 'react-router-dom';

const RatingsDataGrid = ({ ratingsData, username }) => {
  const columns = [
    {
      field: 'rating',
      headerName: 'Rating',
      flex: 1,
      headerAlign: 'center',
      align: 'center',
      renderCell: (params) => (
        <Typography
          to={`/collections/${username}/${params.row.rating.toFixed(1)}`}
          component={RouterLink}
          sx={{ color: 'text.primary' }}
        >
          <b>{params.row.rating.toFixed(1)}</b>
        </Typography>
      ),
    },
    {
      field: 'count',
      headerName: 'Count',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      renderCell: (params) => (
        <>
          <Typography sx={{ mr: 2 }}>{params.row.count}</Typography>
          <LinearProgress
            variant='determinate'
            color='primary'
            value={(params.row.count / ratingsData?.length) * 100}
            sx={(theme) => ({
              width: '100%',
              height: 15,
              [theme.breakpoints.down('sm')]: {
                display: 'none',
              },
            })}
          />
        </>
      ),
    },
    {
      field: 'ratingStars',
      headerName: 'Stars',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      renderCell: (params) => (
        <Stack sx={{ display: 'flex', alignItems: 'center' }}>
          <Rating
            name='read-only'
            size='small'
            value={params.row.rating}
            precision={0.5}
            readOnly
          />
        </Stack>
      ),
    },
  ];

  const rows = ratingsData?.map((row) => ({
    ...row,
    id: row?.id?.toString(),
    ratingStars: row.rating,
  }));
  return (
    <>
      {rows?.length > 0 && (
        <DataGrid rows={rows} columns={columns} getRowId={(row) => row?.id} />
      )}
    </>
  );
};

export default RatingsDataGrid;
