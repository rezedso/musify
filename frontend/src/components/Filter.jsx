/* eslint-disable react/prop-types */
import { TextField, Button, Stack } from '@mui/material';
import FilterSelect from './FilterSelect';

const Filter = ({
  genres,
  formationYears,
  countries,
  query,
  setQuery,
  selectedGenre,
  setSelectedGenre,
  selectedYear,
  setSelectedYear,
  selectedCountry,
  setSelectedCountry,
  clearFilters,
  genrePage,
  releaseYears,
  album,
  sortBy,
  setSortBy,
}) => {
  return (
    <Stack direction='column' spacing={2}>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <TextField
          placeholder={`Search ${album ? 'albums...' : 'artists...'}`}
          variant='outlined'
          sx={{
            '& .MuiOutlinedInput-root': {
              borderRadius: '5px',
              '& input': {
                height: '6px',
                borderRadius: '15px',
              },
              width: '250px',
            },
          }}
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <Button
          variant='contained'
          sx={{ alignSelf: 'start' }}
          onClick={() => setQuery('')}
        >
          Clear
        </Button>
      </Stack>

      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
        {!genrePage && (
          <FilterSelect
            label='Select Genre'
            value={selectedGenre}
            onChange={(e) => setSelectedGenre(e.target.value)}
            items={genres}
            displayEmpty={true}
            width='200px'
          />
        )}
        <FilterSelect
          label={`Select ${album ? 'Release Date' : 'Formation Year'}`}
          value={selectedYear}
          onChange={(e) => setSelectedYear(e.target.value)}
          items={releaseYears || formationYears}
          displayEmpty={true}
          width='250px'
        />
        <FilterSelect
          label='Select Country'
          value={selectedCountry}
          onChange={(e) => setSelectedCountry(e.target.value)}
          items={countries}
          displayEmpty={true}
          width='250px'
        />
        <FilterSelect
          label='Sort By'
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          items={
            album
              ? ['Rating Asc', 'Rating Desc', 'Title Asc', 'Title Desc']
              : [
                  'Name Asc',
                  'Name Desc',
                  'Formation Year Asc',
                  'Formation Year Desc',
                  'Origin Country Asc',
                  'Origin Country Desc',
                ]
          }
          displayEmpty={true}
          width='250px'
        />
        <Button
          variant='contained'
          sx={{ alignSelf: 'start' }}
          onClick={clearFilters}
        >
          Clear All
        </Button>
      </Stack>
    </Stack>
  );
};

export default Filter;
