/* eslint-disable react/prop-types */
import { Button, IconButton, Modal, Stack, Typography } from '@mui/material';
import { useState } from 'react';
import ClearIcon from '@mui/icons-material/Clear';

const ConfirmationModal = ({ title, text, onClick, icon }) => {
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <>
      {!icon ? (
        <Button variant='contained' color='error' onClick={handleOpen}>
          {title}
        </Button>
      ) : (
        <IconButton onClick={handleOpen}>
          <ClearIcon />
        </IconButton>
      )}
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby='modal-modal-title'
        aria-describedby='modal-modal-description'
      >
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
          <Typography variant='h6' sx={{ textAlign: 'center' }}>
            {title}
          </Typography>
          <Typography sx={{ textAlign: 'center' }}>
            <b>{text}</b>
          </Typography>
          <Stack direction='row' spacing={2} sx={{ justifyContent: 'end' }}>
            <Button variant='outlined' onClick={handleClose}>
              Cancel
            </Button>
            <Button
              variant='contained'
              color='error'
              onClick={() => {
                onClick();
                handleClose();
              }}
            >
              Confirm
            </Button>
          </Stack>
        </Stack>
      </Modal>
    </>
  );
};

export default ConfirmationModal;
