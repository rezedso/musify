/* eslint-disable react/prop-types */
import InfiniteScroll from 'react-infinite-scroll-component';
import AlbumCard from './AlbumCard';
import { formatDateToYear } from '../lib/utils';
import useFilter from '../hooks/use-filter';
import Filter from './Filter';
import NoResults from './NoResults';

const InfiniteScrollAlbums = ({
  data,
  fetchNextPage,
  hasNextPage,
  genrePage,
  lists,
}) => {
  let albums = data?.pages?.map((group) => group.content).flat();

  const {
    query,
    setQuery,
    setSelectedGenre,
    selectedGenre,
    selectedYear,
    setSelectedYear,
    selectedCountry,
    setSelectedCountry,
    filterItems,
    sortBy,
    setSortBy,
  } = useFilter(albums);

  const result = filterItems(albums);

  const albumGenres = Array.from(
    new Set(
      albums?.flatMap((album) =>
        album?.genres ? album?.genres.map((genre) => genre.name) : []
      )
    )
  );

  const releaseYears = Array.from(
    new Set(albums?.map((album) => formatDateToYear(album.releaseDate)))
  );

  const countries = Array.from(
    new Set(albums?.map((album) => album.originCountry))
  );

  const clearFilters = () => {
    setSelectedGenre('');
    setSelectedCountry('');
    setSelectedYear('');
    setSortBy('');
  };

  return (
    <>
      <Filter
        query={query}
        setQuery={setQuery}
        setSelectedGenre={setSelectedGenre}
        selectedGenre={selectedGenre}
        selectedYear={selectedYear}
        setSelectedYear={setSelectedYear}
        selectedCountry={selectedCountry}
        setSelectedCountry={setSelectedCountry}
        clearFilters={clearFilters}
        releaseYears={releaseYears}
        countries={countries}
        genrePage={genrePage}
        genres={albumGenres}
        album={true}
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
          result.map((album, i) => (
            <AlbumCard key={album.id} album={album} index={i} lists={lists} />
          ))
        ) : (
          <NoResults text={`No results found.`} />
        )}
      </InfiniteScroll>
    </>
  );
};

export default InfiniteScrollAlbums;
