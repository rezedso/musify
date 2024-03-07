/* eslint-disable react/prop-types */
import {
  Button,
  FormLabel,
  Rating,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { useCreateReview, useUpdateReview } from '../lib/react-query/queries';
import { zodResolver } from '@hookform/resolvers/zod';
import { reviewSchema } from '../lib/zod/validations';
import { useTheme } from '@emotion/react';
import { useState } from 'react';

const ReviewForm = ({ albumId, setShowReviewForm, review }) => {
  const theme = useTheme();
  const [hover, setHover] = useState(-1);

  const {
    control,
    handleSubmit,
    reset,
    register,
    formState: { errors },
  } = useForm({ resolver: zodResolver(reviewSchema) });

  const { mutateAsync: createReview, isPending: isPendingCreate } =
    useCreateReview();
  const { mutateAsync: updateReview, isPending: isPendingUpdate } =
    useUpdateReview();

  const onSubmit = async (data) => {
    if (review) {
      await updateReview({
        reviewId: review.id,
        userId: review?.userId,
        review: data,
      });
    } else {
      await createReview({ albumId, review: data });
    }
    setShowReviewForm(false);
    reset();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Stack sx={{ display: 'flex', mt: 2 }} spacing={2} direction='column'>
        <Typography>Write a review</Typography>
        <TextField
          {...register('title')}
          autoFocus
          label='Title'
          size='small'
          fullWidth
          onKeyDown={(e) => {
            if (e.key === 'a') {
              e.stopPropagation();
            }
          }}
          defaultValue={review?.title}
        />
        {errors.title && (
          <Typography
            variant='subtitle2'
            sx={{ color: theme.palette.error.main }}
          >
            {errors.title.message}
          </Typography>
        )}
        <Controller
          name='content'
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label='What are your thoughts?'
              multiline
              minRows={3}
              maxRows={6}
              fullWidth
            />
          )}
          defaultValue={review?.content}
        />
        {errors.content && (
          <Typography
            variant='subtitle2'
            sx={{ color: theme.palette.error.main }}
          >
            {errors.content.message}
          </Typography>
        )}
        <Stack
          direction='row'
          sx={{ display: 'flex', alignItems: 'center' }}
          spacing={1}
        >
          <FormLabel>Rate</FormLabel>
          <Controller
            name='rating'
            control={control}
            defaultValue={review?.rating}
            render={({ field }) => (
              <>
                <Rating
                  {...field}
                  value={field.value || 0}
                  name='rating'
                  onChange={(event, newValue) => {
                    field.onChange(newValue);
                  }}
                  onChangeActive={(event, newHover) => {
                    setHover(newHover);
                  }}
                  precision={0.5}
                />
                <Stack sx={{ ml: 2 }}>
                  {hover !== -1 ? hover.toFixed(1) : field?.value}
                </Stack>
              </>
            )}
          />
        </Stack>
        {errors.rating && (
          <Typography
            variant='subtitle2'
            sx={{ color: theme.palette.error.main }}
          >
            {errors.rating.message}
          </Typography>
        )}
        <Button
          sx={{ alignSelf: 'end' }}
          type='submit'
          variant='contained'
          disabled={isPendingCreate}
        >
          {isPendingCreate
            ? 'Creating...'
            : isPendingUpdate
            ? 'Updating...'
            : review
            ? 'Update'
            : 'Create'}
        </Button>
      </Stack>
    </form>
  );
};

export default ReviewForm;
