/* eslint-disable react/prop-types */

import {
  Avatar,
  Button,
  IconButton,
  Paper,
  Rating,
  Stack,
  Typography,
} from '@mui/material';
import { formatDate } from '../lib/utils';
import { useDeleteReview } from '../lib/react-query/queries';
import { getCurrentUser } from '../services/auth.service';
import DeleteOutlineOutlinedIcon from '@mui/icons-material/DeleteOutlineOutlined';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import ReviewForm from './ReviewForm';
import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';

const Review = ({ review }) => {
  const [showEditForm, setShowEditForm] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const user = getCurrentUser();
  const contentLines = review.content.split('\n');
  const { mutateAsync: deleteReview } = useDeleteReview();

  const handleDeleteReview = async (reviewId, id) => {
    deleteReview({ reviewId, id });
  };

  const handleExpand = () => {
    setExpanded((prevState) => !prevState);
  };

  console.log(user?.id);
  console.log(review?.userId);
  return (
    <Stack sx={{ p: 2, mb: 2 }} component={Paper}>
      <Stack direction='row' sx={{ alignItems: 'center' }}>
        <Avatar
          to={`/users/${review.username}`}
          component={RouterLink}
          src={review.userImage}
          width={50}
          height={50}
        />
        <Typography
          to={`/users/${review.username}`}
          component={RouterLink}
          sx={{ mx: 1, color: 'text.primary' }}
        >
          <b>{review.username}</b>
        </Typography>
        <Typography variant='subtitle2'>
          {formatDate(review.createdAt)}
        </Typography>
        <Rating
          name='read-only'
          size='small'
          value={review.rating}
          precision={0.5}
          readOnly
          sx={{ ml: 'auto' }}
        />
      </Stack>
      {showEditForm ? (
        <Stack>
          <ReviewForm
            review={review}
            showEditForm={showEditForm}
            setShowReviewForm={setShowEditForm}
          />
        </Stack>
      ) : (
        <Stack sx={{ mt: 2 }}>
          <Typography variant='h6' sx={{ mb: 2 }}>
            <b>{review.title}</b>
          </Typography>
          {contentLines.length > 3 && !expanded ? (
            <>
              <Typography>{contentLines.slice(0, 3).join('\n')}</Typography>
              <Button sx={{ my: 2 }} variant='contained' onClick={handleExpand}>
                Expand review
              </Button>
            </>
          ) : (
            <>
              <Typography>
                {expanded ? review.content : contentLines.join('\n')}
              </Typography>
              {expanded && (
                <Button
                  sx={{ my: 2 }}
                  variant='contained'
                  onClick={handleExpand}
                >
                  Hide
                </Button>
              )}
            </>
          )}
        </Stack>
      )}
      {(user?.roles.some((role) => role.includes('ROLE_ADMIN')) ||
        user?.id === review?.userId) && (
        <Stack
          direction='row'
          sx={{
            display: 'flex',
            alignItems: 'center',
            ml: 'auto',
            mt: showEditForm && 2,
          }}
          spacing={1}
        >
          <IconButton
            onClick={() => setShowEditForm((prevState) => !prevState)}
          >
            <EditOutlinedIcon />
          </IconButton>
          <IconButton
            onClick={() => handleDeleteReview(review.id, review?.userId)}
          >
            <DeleteOutlineOutlinedIcon />
          </IconButton>
        </Stack>
      )}
    </Stack>
  );
};

export default Review;
