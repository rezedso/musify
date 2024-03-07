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
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import CloseOutlinedIcon from '@mui/icons-material/CloseOutlined';
import { useTheme } from '@emotion/react';
import { useCreateList, useUpdateList } from '../lib/react-query/queries';
import { zodResolver } from '@hookform/resolvers/zod';
import { listSchema } from '../lib/zod/validations';
import { useNavigate } from 'react-router-dom';

const CreateOrUpdateListModal = ({ list }) => {
  const theme = useTheme();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ resolver: zodResolver(listSchema) });
  const { mutateAsync: createList, isPending: isPendingCreate } =
    useCreateList();

  const { mutateAsync: updateList, isPending: isPendingUpdate } =
    useUpdateList();

  const onSubmit = async (data) => {
    if (list) {
      await updateList({ listId: list?.id, userId: list?.userId, list: data });
      navigate(`/lists/${list?.username}/${data.name}`);
    } else {
      await createList(data);
    }

    reset();
    handleClose();
  };

  return (
    <>
      <Button
        variant='contained'
        onClick={handleOpen}
        sx={(theme) => ({
          [theme.breakpoints.down('sm')]: {
            p: '.5rem',
          },
        })}
      >
        {list ? 'Update List' : 'New List'}
      </Button>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby='modal-modal-title'
        aria-describedby='modal-modal-description'
      >
        <form onSubmit={handleSubmit(onSubmit)}>
          <Stack
            sx={{
              display: 'flex',
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              width: 350,
              bgcolor: 'background.paper',
              boxShadow: 24,
              p: 4,
              overflow: 'auto',
            }}
            spacing={2}
          >
            <IconButton sx={{ alignSelf: 'end' }} onClick={handleClose}>
              <CloseOutlinedIcon />
            </IconButton>
            <Typography sx={{ mb: 1 }} variant='h6' component='h2'>
              {list ? 'Update list' : 'Create a list'}
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
              defaultValue={list?.name}
            />
            {errors.name && (
              <Typography
                variant='subtitle2'
                sx={{ mt: 2, color: theme.palette.error.main }}
              >
                {errors.name.message}
              </Typography>
            )}
            <Button
              sx={{ alignSelf: 'start', mt: 2 }}
              type='submit'
              variant='contained'
              disabled={isPendingCreate}
            >
              {isPendingCreate
                ? 'Creating...'
                : isPendingUpdate
                ? 'Updating...'
                : list
                ? 'Update'
                : 'Create'}
            </Button>
          </Stack>
        </form>
      </Modal>
    </>
  );
};

export default CreateOrUpdateListModal;
