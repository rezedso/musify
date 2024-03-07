/* eslint-disable react/prop-types */
import InfiniteScroll from 'react-infinite-scroll-component';
import Artist from './Artist';
import NoResults from './NoResults';
import useFilter from '../hooks/use-filter';
import Filter from './Filter';

const InfiniteScrollArtists = ({
  data,
  fetchNextPage,
  hasNextPage,
  genrePage,
}) => {
  let artists = data?.pages?.map((group) => group.content).flat();

  const {
    query,
    setQuery,
    selectedGenre,
    setSelectedGenre,
    selectedYear,
    setSelectedYear,
    selectedCountry,
    setSelectedCountry,
    filterItems,
    sortBy,
    setSortBy,
  } = useFilter(artists);

  const result = filterItems(artists);

  const artistGenres = Array.from(
    new Set(
      artists?.flatMap((artist) =>
        artist?.artistGenres
          ? artist?.artistGenres.map((genre) => genre.name)
          : []
      )
    )
  );

  const formationYears = Array.from(
    new Set(artists?.map((artist) => artist.formedYear))
  );

  const countries = Array.from(
    new Set(artists?.map((artist) => artist.originCountry))
  );

  const clearFilters = () => {
    setSelectedCountry('');
    setSelectedGenre('');
    setSelectedYear('');
    setSortBy('');
  };

  return (
    <>
      <Filter
        query={query}
        setQuery={setQuery}
        selectedGenre={selectedGenre}
        setSelectedGenre={setSelectedGenre}
        selectedYear={selectedYear}
        setSelectedYear={setSelectedYear}
        selectedCountry={selectedCountry}
        setSelectedCountry={setSelectedCountry}
        clearFilters={clearFilters}
        genres={artistGenres}
        formationYears={formationYears}
        countries={countries}
        genrePage={genrePage}
        sortBy={sortBy}
        setSortBy={setSortBy}
      />
      <InfiniteScroll
        dataLength={data?.pages?.length || 20}
        next={fetchNextPage}
        hasMore={!!hasNextPage}
        data-testid='infinite-scroll'
        scrollableTarget='scrollbar-target'
      >
        {result?.length > 0 ? (
          result?.map((artist) => <Artist key={artist.id} artist={artist} />)
        ) : (
          <NoResults text={`No results found.`} />
        )}
      </InfiniteScroll>
    </>
  );
};

export default InfiniteScrollArtists;
