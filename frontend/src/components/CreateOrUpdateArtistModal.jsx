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
import { useCreateArtist, useUpdateArtist } from '../lib/react-query/queries';
import { zodResolver } from '@hookform/resolvers/zod';
import { artistSchema } from '../lib/zod/validations';
import slugify from 'slugify';

const CreateOrUpdateArtistModal = ({ artist }) => {
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
    resolver: zodResolver(artistSchema),
  });
  const { mutateAsync: createArtist, isPending: isPendingCreateArtist } =
    useCreateArtist();
  const { mutateAsync: updateArtist, isPending: isPendingUpdateArtist } =
    useUpdateArtist();

  const onSubmit = async (data) => {
    const formData = new FormData();
    const genresArray = data.artistGenres
      .split(',')
      .map((genre) => genre.trim());

    const artistJson = {
      name: data.name,
      originCountry: data.originCountry,
      formedYear: data.formedYear,
      slug: slugify(data.name.toLowerCase()),
      artistGenres: genresArray,
    };

    const artistBlob = new Blob([JSON.stringify(artistJson)], {
      type: 'application/json',
    });

    formData.append('artist', artistBlob);

    if (data.image) {
      formData.append('image', data.image);
    }

    if (artist) {
      await updateArtist({
        formData,
        artist: artistBlob,
        image: data.image,
        artistId: artist?.id,
      });
    } else {
      await createArtist({
        formData,
        artist: artistBlob,
        image: data.image,
      });
    }
    reset();
    handleClose();
  };

  return (
    <>
      <Button
        variant='contained'
        onClick={handleOpen}
        sx={{ alignSelf: 'start' }}
      >
        {artist ? 'Update Artist' : 'Create Artist'}
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
              {artist ? 'Update artist' : 'Create an artist'}
            </Typography>
            <Divider />
            <FormLabel sx={{ my: 1 }}>Name</FormLabel>
            <TextField
              {...register('name')}
              autoFocus
              size='small'
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'a') {
                  e.stopPropagation();
                }
              }}
              defaultValue={artist?.name}
            />
            {errors.name && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.name.message}
              </Typography>
            )}
            <FormLabel sx={{ my: 1 }}>Formation Year</FormLabel>
            <TextField
              {...register('formedYear', { valueAsNumber: true })}
              size='small'
              type='number'
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'a') {
                  e.stopPropagation();
                }
              }}
              defaultValue={artist?.formedYear}
            />
            {errors.formedYear && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.formedYear.message}
              </Typography>
            )}
            <FormLabel sx={{ my: 1 }}>Origin Country</FormLabel>
            <TextField
              {...register('originCountry')}
              size='small'
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'a') {
                  e.stopPropagation();
                }
              }}
              defaultValue={artist?.originCountry}
            />
            {errors.originCountry && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.originCountry.message}
              </Typography>
            )}
            <FormLabel sx={{ my: 1 }}>Genres (separate by commas)</FormLabel>
            <TextField
              {...register('artistGenres')}
              size='small'
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'a') {
                  e.stopPropagation();
                }
              }}
              defaultValue={
                artist?.artistGenres
                  .map((genre) => genre.name)
                  .slice(',')
                  .join(', ') || ''
              }
            />
            {errors.artistGenres && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.artistGenres.message}
              </Typography>
            )}
            <FormLabel sx={{ my: 1 }}>Image (optional)</FormLabel>
            <Button
              direction='row'
              variant='contained'
              onClick={() => fileRef.current.click()}
              sx={{
                fontSize: '0.9rem',
              }}
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
            {selectedFile ? (
              <Stack sx={{ mt: 2 }}>
                <img
                  src={URL.createObjectURL(selectedFile)}
                  width={200}
                  height={200}
                />
              </Stack>
            ) : (
              artist?.image && (
                <img src={artist?.image} width={200} height={200} />
              )
            )}

            <Button
              sx={{ alignSelf: 'start', mt: 2 }}
              type='submit'
              variant='contained'
              disabled={isPendingCreateArtist || isPendingUpdateArtist}
            >
              {isPendingCreateArtist
                ? 'Creating...'
                : isPendingUpdateArtist
                ? 'Updating...'
                : artist
                ? 'Update'
                : 'Create'}
            </Button>
          </Stack>
        </form>
      </Modal>
    </>
  );
};

export default CreateOrUpdateArtistModal;
