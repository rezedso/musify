import { useState } from 'react';
import { formatDateToYear } from '../lib/utils';

const useFilter = () => {
  const [query, setQuery] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const [selectedYear, setSelectedYear] = useState('');
  const [selectedCountry, setSelectedCountry] = useState('');
  const [sortBy, setSortBy] = useState('');

  const filterItems = (items) => {
    let filteredItems = [...items];

    if (query) {
      filteredItems = filteredItems?.filter((item) =>
        item.title
          ? item.title.toLowerCase().indexOf(query.toLowerCase()) !== -1
          : item.name.toLowerCase().indexOf(query.toLowerCase()) !== -1
      );
    }

    if (selectedGenre) {
      filteredItems = filteredItems?.filter((item) =>
        item.artistGenres
          ? item.artistGenres.some((genre) => genre.name === selectedGenre)
          : item.genres.some((genre) => genre.name === selectedGenre)
      );
    }

    if (selectedYear) {
      filteredItems = filteredItems?.filter((item) =>
        item.formedYear
          ? item.formedYear === selectedYear
          : formatDateToYear(item.releaseDate) === selectedYear
      );
    }

    if (selectedCountry) {
      filteredItems = filteredItems?.filter(
        (item) => item.originCountry === selectedCountry
      );
    }

    switch (sortBy) {
      case 'Title Asc':
        filteredItems.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'Title Desc':
        filteredItems.sort((a, b) => b.title.localeCompare(a.title));
        break;
      case 'Name Asc':
        filteredItems.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'Name Desc':
        filteredItems.sort((a, b) => b.name.localeCompare(a.name));
        break;
      case 'Rating Asc':
        filteredItems.sort((a, b) => a.rating - b.rating);
        break;
      case 'Rating Desc':
        filteredItems.sort((a, b) => b.rating - a.rating);
        break;
      case 'Formation Year Asc':
        filteredItems.sort((a, b) => a.formedYear - b.formedYear);
        break;
      case 'Formation Year Desc':
        filteredItems.sort((a, b) => b.formedYear - a.formedYear);
        break;
      case 'Origin Country Asc':
        filteredItems.sort((a, b) =>
          a.originCountry.localeCompare(b.originCountry)
        );
        break;
      case 'Origin Country Desc':
        filteredItems.sort((a, b) =>
          b.originCountry.localeCompare(a.originCountry)
        );
        break;
      default:
        break;
    }

    return filteredItems;
  };

  return {
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
  };
};

export default useFilter;
