/* eslint-disable react/prop-types */
import {
  Button,
  Divider,
  FormLabel,
  IconButton,
  Modal,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import CloseOutlinedIcon from '@mui/icons-material/CloseOutlined';
import { useTheme } from '@emotion/react';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { useCreateAlbum, useUpdateAlbum } from '../lib/react-query/queries';
import { zodResolver } from '@hookform/resolvers/zod';
import { albumSchema } from '../lib/zod/validations';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFnsV3';
import { de } from 'date-fns/locale/de';
import { useNavigate, useParams } from 'react-router-dom';
import slugify from 'slugify';
import { fromUnixTime } from 'date-fns';

const CreateOrUpdateAlbumModal = ({ artistName, album }) => {
  const navigate = useNavigate();
  const params = useParams();
  const [selectedFile, setSelectedFile] = useState(null);
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const theme = useTheme();
  const fileRef = useRef(null);
  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(albumSchema),
  });
  const { mutateAsync: createAlbum, isPending: isPendingCreating } =
    useCreateAlbum();
  const { mutateAsync: updateAlbum, isPending: isPendingUpdating } =
    useUpdateAlbum();

  const onSubmit = async (data) => {
    const formData = new FormData();
    const genresArray = data.albumGenres
      .split(',')
      .map((genre) => genre.trim());

    const albumObject = {
      artistName: artistName,
      title: data.title,
      slug: slugify(data.title.toLowerCase()),
      releaseDate: data.releaseDate,
      albumGenres: genresArray,
    };

    const albumBlob = new Blob([JSON.stringify(albumObject)], {
      type: 'application/json',
    });

    formData.append('album', albumBlob);

    if (data.image) {
      formData.append('image', data.image);
    }

    if (album) {
      await updateAlbum({
        formData,
        album: albumBlob,
        image: data.image,
        albumId: album?.id,
      });
      navigate(`/artists/${params?.artistSlug}/${albumObject?.slug}`);
    } else {
      await createAlbum({
        formData,
        album: albumBlob,
        image: data.image,
      });
      setSelectedFile(null);
    }
    reset();
    handleClose();
  };

  return (
    <>
      <Button variant='contained' onClick={handleOpen}>
        {album ? 'Update Album' : 'Add Album'}
      </Button>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby='modal-modal-title'
        aria-describedby='modal-modal-description'
      >
        <form onSubmit={handleSubmit(onSubmit)}>
          <Stack
            sx={(theme) => ({
              display: 'flex',
              position: 'absolute',
              top: '50%',
              left: '50%',
              width: 450,
              transform: 'translate(-50%, -50%)',
              bgcolor: 'background.paper',
              boxShadow: 24,
              p: 4,
              overflow: 'auto',
              [theme.breakpoints.down('sm')]: {
                width: 350,
              },
            })}
            spacing={{ xs: 1, sm: 2 }}
          >
            <IconButton sx={{ alignSelf: 'end' }} onClick={handleClose}>
              <CloseOutlinedIcon />
            </IconButton>
            <Typography sx={{ mb: 1 }} variant='h6' component='h2'>
              {album ? 'Update Album' : 'Create Album'}
            </Typography>
            <Divider />
            <FormLabel sx={{ my: 1 }}>Title</FormLabel>
            <TextField
              {...register('title')}
              autoFocus
              size='small'
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'a') {
                  e.stopPropagation();
                }
              }}
              defaultValue={album?.title}
            />
            {errors.title && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.title.message}
              </Typography>
            )}
            <FormLabel>Release Date</FormLabel>
            <Controller
              name='releaseDate'
              control={control}
              render={({ field, fieldState: { error } }) => (
                <LocalizationProvider
                  dateAdapter={AdapterDateFns}
                  adapterLocale={de}
                >
                  <DatePicker
                    format='dd-MM-yyyy'
                    onChange={(date) => field.onChange(date)}
                    slotProps={{
                      textField: { error: !!error, helperText: error?.message },
                    }}
                    maxDate={new Date()}
                    defaultValue={fromUnixTime(album?.releaseDate) || ''}
                  />
                </LocalizationProvider>
              )}
            />
            <FormLabel sx={{ my: 1 }}>Genres (separate by commas)</FormLabel>
            <TextField
              {...register('albumGenres')}
              size='small'
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'a') {
                  e.stopPropagation();
                }
              }}
              defaultValue={
                album
                  ? album?.genres?.map((genre) => genre.name).join(', ')
                  : ''
              }
            />
            {errors.albumGenres && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.albumGenres.message}
              </Typography>
            )}
            <FormLabel sx={{ my: 1 }}>Image (optional)</FormLabel>
            <Button
              direction='row'
              variant='contained'
              onClick={() => fileRef.current.click()}
              sx={{ fontSize: '0.9rem' }}
            >
              <ImageOutlinedIcon />
              <Typography variant='subtitle2' sx={{ ml: 1 }}>
                Select File
              </Typography>
            </Button>
            <Controller
              control={control}
              name='image'
              render={({ field: { value, onChange, ref, ...field } }) => {
                return (
                  <input
                    {...field}
                    value={value?.fileName}
                    onChange={(e) => {
                      onChange(e.target.files[0]);
                      setSelectedFile(e.target.files[0]);
                    }}
                    ref={(e) => {
                      ref(e);
                      fileRef.current = e;
                    }}
                    hidden
                    type='file'
                  />
                );
              }}
            />
            {(selectedFile || album?.albumImage) && (
              <Stack sx={{ mt: 2 }}>
                <img
                  src={
                    selectedFile
                      ? URL.createObjectURL(selectedFile)
                      : album?.albumImage
                  }
                  width={200}
                  height={200}
                />
              </Stack>
            )}
            <Button
              sx={{ alignSelf: 'start', mt: 2 }}
              type='submit'
              variant='contained'
              disabled={isPendingCreating || isPendingUpdating}
            >
              {isPendingCreating
                ? 'Creating...'
                : isPendingUpdating
                ? 'Updating...'
                : album
                ? 'Update'
                : 'Create'}
            </Button>
          </Stack>
        </form>
      </Modal>
    </>
  );
};

export default CreateOrUpdateAlbumModal;
