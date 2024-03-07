/* eslint-disable react/prop-types */
import { MenuItem, Select } from '@mui/material';

const FilterSelect = ({
  label,
  value,
  onChange,
  items,
  displayEmpty,
  width,
}) => {
  return (
    <Select
      value={value || ''}
      onChange={onChange}
      displayEmpty={displayEmpty}
      sx={{ width: width }}
    >
      <MenuItem value=''>{label}</MenuItem>
      {items?.sort().map((item, index) => (
        <MenuItem key={index} value={item}>
          {item}
        </MenuItem>
      ))}
    </Select>
  );
};

export default FilterSelect;
