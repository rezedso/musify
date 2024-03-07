import {
  Button,
  Container,
  Link,
  Rating,
  Stack,
  Typography,
} from '@mui/material';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import { useGetAlbumRatingsByUserAndRating } from '../../lib/react-query/queries';
import Loader from '../Loader';
import { DataGrid } from '@mui/x-data-grid';
import { formatDate, formatDateToYear } from '../../lib/utils';
import NoResults from '../NoResults';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { getCurrentUser } from '../../services/auth.service';

const CollectionPage = () => {
  const currentUser = getCurrentUser();
  const params = useParams();
  const navigate = useNavigate();
  const { data: albumRatings, isLoading } = useGetAlbumRatingsByUserAndRating(
    params?.username,
    params?.rating
  );

  const columns = [
    {
      field: 'albumImage',
      headerName: 'Image',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 150,
      renderCell: (params) => (
        <Stack
          component={RouterLink}
          to={`/artists/${params.row.artistSlug}/${params.row.albumSlug}`}
          sx={{ color: 'text.primary' }}
        >
          {!params.row?.albumImage ? (
            <ImageOutlinedIcon sx={{ width: 50, height: 50 }} />
          ) : (
            <img src={params.row.albumImage} width={50} height={50} />
          )}
        </Stack>
      ),
    },
    {
      field: 'ratedDate',
      headerName: 'Rated Date',
      headerAlign: 'center',
      align: 'center',
      flex: 2,
      minWidth: 200,
      valueGetter: (params) => `${formatDate(params.row.ratedDate)}`,
    },
    {
      field: 'rating',
      headerName: 'Rating',
      headerAlign: 'center',
      align: 'center',
      flex: 2,
      maxWidth: 200,
      renderCell: (params) => (
        <Stack
          sx={{ display: 'flex', alignItems: 'center' }}
          direction='row'
          spacing={2}
        >
          <Typography>{params.row.rating}</Typography>
          <Rating
            name='read-only'
            size='small'
            value={params.row.rating}
            precision={0.5}
            readOnly
            sx={(theme) => ({
              [theme.breakpoints.down('md')]: {
                display: 'none',
              },
            })}
          />
        </Stack>
      ),
    },
    {
      field: 'album',
      headerName: 'Album',
      headerAlign: 'center',
      align: 'center',
      flex: 3,
      minWidth: 350,
      valueGetter: (params) =>
        `${params.row.artistName || ''} - (${formatDateToYear(
          params.row.releaseDate
        )})`,
      renderCell: (params) => (
        <>
          <Typography
            to={`/artists/${params.row.artistSlug}`}
            component={RouterLink}
            sx={{ color: 'text.primary' }}
          >
            <b> {params.row.artistName || ''}</b>
          </Typography>
          <Typography
            to={`/artists/${params.row.artistSlug}/${params.row.albumSlug}`}
            component={RouterLink}
            sx={{ color: 'text.primary' }}
          >
            &nbsp;-&nbsp;{params.row.albumTitle} (
            {formatDateToYear(params.row.releaseDate)})
          </Typography>
        </>
      ),
    },
  ];

  const rows = albumRatings?.map((albumRating) => albumRating) || [];

  return (
    <Container maxWidth='xl'>
      {isLoading ? (
        <Loader />
      ) : (
        <>
          <Button
            to='..'
            component={RouterLink}
            variant='contained'
            sx={{ alignSelf: 'start', mb: 2 }}
            onClick={(e) => {
              e.preventDefault();
              navigate(-1);
            }}
          >
            Go Back
          </Button>

          <Stack sx={{ width: '100%', overflowX: 'auto' }}>
            {rows.length > 0 ? (
              <DataGrid
                getRowHeight={() => {
                  return 85;
                }}
                rows={rows}
                columns={columns}
                initialState={{
                  pagination: {
                    paginationModel: { page: 0, pageSize: 15 },
                  },
                  sorting: {
                    sortModel: [{ field: 'ratedDate', sort: 'desc' }],
                  },
                }}
                pageSizeOptions={[15, 20, 30, 50]}
                getRowId={(row) => row.id}
              />
            ) : (
              <Stack sx={{ alignItems: 'center' }} spacing={1}>
                <NoResults
                  text={`There are no albums rated with ${params.rating}.`}
                />
                {currentUser?.username === params?.username && (
                  <Link
                    to='/artists'
                    component={RouterLink}
                    sx={{ color: 'primary.main', textAlign: 'center' }}
                  >
                    Try rating some albums.
                  </Link>
                )}
              </Stack>
            )}
          </Stack>
        </>
      )}
    </Container>
  );
};

export default CollectionPage;
