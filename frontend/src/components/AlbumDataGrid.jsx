/* eslint-disable react/prop-types */
import { Link as RouterLink, useParams } from 'react-router-dom';
import { Typography, Link, IconButton, Stack, Rating } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import NoResults from './NoResults';
import { formatDate, formatDateToYear } from '../lib/utils';
import ClearIcon from '@mui/icons-material/Clear';
import { useRemoveAlbumFromList } from '../lib/react-query/queries';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { getCurrentUser } from '../services/auth.service';

const AlbumDataGrid = ({ albums, list }) => {
  const currentUser = getCurrentUser();
  const params = useParams();
  const { mutateAsync: removeAlbumFromList } = useRemoveAlbumFromList();

  const handleRemoveAlbumFromList = async (listId, albumId, id) => {
    await removeAlbumFromList({ listId, albumId, id });
  };

  const columns = [
    {
      field: 'image' || 'albumImage',
      headerName: 'Image',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 120,
      renderCell: (params) => (
        <Stack
          component={RouterLink}
          to={`/artists/${params.row.artistSlug}/${params.row.albumSlug}`}
          sx={{ color: 'text.primary' }}
        >
          {!params.row?.albumImage && !params.row.image ? (
            <ImageOutlinedIcon sx={{ width: 50, height: 50 }} />
          ) : (
            <img
              src={params.row.albumImage || params.row.image}
              width={50}
              height={50}
            />
          )}
        </Stack>
      ),
    },
    !list
      ? {
          field: 'createdAt',
          headerName: 'Rated Date',
          headerAlign: 'center',
          align: 'center',
          flex: 1,
          minWidth: 150,
          valueGetter: (params) => `${formatDate(params.row.createdAt)}`,
        }
      : null,
    !list
      ? {
          field: 'ratingStars',
          headerName: 'Stars',
          headerAlign: 'center',
          align: 'center',
          flex: 1,
          minWidth: 120,
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
        }
      : null,
    {
      field: 'album',
      headerName: 'Album',
      headerAlign: 'center',
      align: 'center',
      flex: 2,
      minWidth: 450,
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
            &nbsp;-&nbsp;{!list ? params.row.albumTitle : params.row.title} (
            {formatDateToYear(params.row.releaseDate)})
          </Typography>
        </>
      ),
    },
    list && currentUser?.id === list?.userId
      ? {
          field: 'actions',
          headerName: 'Remove',
          headerAlign: 'center',
          align: 'center',
          flex: 1,
          minWidth: 150,
          renderCell: (params) => (
            <IconButton
              onClick={() =>
                handleRemoveAlbumFromList(
                  list?.id,
                  params.row.id,
                  currentUser?.id
                )
              }
            >
              <ClearIcon />
            </IconButton>
          ),
        }
      : null,
  ].filter(Boolean);

  const rowsWithIndex = (albums || []).map((row) => ({
    ...row,
    ratingStars: row.rating,
  }));

  return (
    <>
      {rowsWithIndex.length > 0 ? (
        <DataGrid
          getRowHeight={() => {
            return 85;
          }}
          rows={rowsWithIndex}
          columns={columns}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
            sorting: {
              sortModel: [{ field: 'album', sort: 'asc' }],
            },
          }}
          pageSizeOptions={[10, 15, 20, 30, 50, 100]}
          getRowId={(row) => row.id}
        />
      ) : (
        <>
          <NoResults text='There are no albums in this list yet.' />
          {currentUser?.username === params?.username && (
            <Link
              to='/albums'
              component={RouterLink}
              sx={{ color: 'primary.main', textAlign: 'center' }}
            >
              Try rating some albums.
            </Link>
          )}
        </>
      )}
    </>
  );
};

export default AlbumDataGrid;
