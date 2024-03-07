/* eslint-disable react/prop-types */
import { Stack } from '@mui/material';
import InfiniteScroll from 'react-infinite-scroll-component';
import { TailSpin } from 'react-loader-spinner';
import Review from './Review';

const InfiniteScrollReviews = ({ data, fetchNextPage, hasNextPage }) => {
  return (
    <InfiniteScroll
      dataLength={data?.pages?.length || 6}
      next={fetchNextPage}
      hasMore={!!hasNextPage}
      loader={
        <Stack
          sx={{
            display: 'flex',
            alignItems: 'center',
          }}
        >
          <TailSpin
            height='80'
            width='80'
            color='#0052ff'
            ariaLabel='tail-spin-loading'
            radius='1'
            visible={true}
          />
        </Stack>
      }
      data-testid='infinite-scroll'
      scrollableTarget='scrollbar-target'
    >
      {data?.pages?.map((group, i) => (
        <Stack key={i} spacing={2} sx={{ mb: 2 }}>
          {group?.content?.map((review) => (
            <Review key={review.id} review={review} />
          ))}
        </Stack>
      ))}
    </InfiniteScroll>
  );
};

export default InfiniteScrollReviews;
