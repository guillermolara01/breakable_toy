import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  Checkbox,
  IconButton,
  Paper,
  Tooltip,
  Box,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { visuallyHidden } from '@mui/utils';
import { useProductContext } from '../../../context/ProductContext';

import { useState } from 'react';
import type IProduct from '../../../interfaces/Product/IProduct';
import {
  setOutOfStock,
  setInStock,
  deleteProduct,
} from '../../../service/productService';

const headCells = [
  { id: 'name', label: 'Name' },
  { id: 'category', label: 'Category' },
  { id: 'unitPrice', label: 'Price ($)' },
  { id: 'expirationDate', label: 'Expiration' },
  { id: 'stock', label: 'Stock' },
  { id: 'actions', label: 'Actions' },
];

export default function ProductTable() {
  const { paginatedProducts, updateParams, refresh } = useProductContext();
  const { products, totalElements, page, size } = paginatedProducts;

  const [orderBy, setOrderBy] = useState('name');
  const [order, setOrder] = useState<'asc' | 'desc'>('asc');

  const handleRequestSort = (property: string) => {
    const isAsc = orderBy === property && order === 'asc';
    updateParams({ sortBy: property, direction: isAsc ? 'desc' : 'asc' });
    setOrderBy(property);
    setOrder(isAsc ? 'desc' : 'asc');
  };

  const handlePageChange = (_: unknown, newPage: number) => {
    updateParams({ page: newPage });
  };

  const handleRowsPerPageChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    updateParams({ size: parseInt(event.target.value, 10), page: 0 });
  };

  const handleStockToggle = async (product: IProduct) => {
    console.log(product);
    if (product.stock > 0) {
      await setOutOfStock(product.id);
    } else {
      await setInStock(product.id);
    }
    refresh();
  };

  const handleDelete = async (id: number) => {
    await deleteProduct(id);
    refresh();
  };

  return (
    <Paper sx={{ width: '100%', mb: 2 }}>
      <TableContainer>
        <Table size='medium'>
          <TableHead>
            <TableRow>
              <TableCell padding='checkbox'></TableCell>
              {headCells.map((headCell) => (
                <TableCell
                  key={headCell.id}
                  sortDirection={orderBy === headCell.id ? order : false}>
                  {headCell.id !== 'actions' ? (
                    <TableSortLabel
                      active={orderBy === headCell.id}
                      direction={orderBy === headCell.id ? order : 'asc'}
                      onClick={() => handleRequestSort(headCell.id)}>
                      {headCell.label}
                      {orderBy === headCell.id ? (
                        <Box
                          component='span'
                          sx={visuallyHidden}>
                          {order === 'desc'
                            ? 'sorted descending'
                            : 'sorted ascending'}
                        </Box>
                      ) : null}
                    </TableSortLabel>
                  ) : (
                    headCell.label
                  )}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {products?.map((product) => {
              return (
                <TableRow
                  key={product.id}
                  hover>
                  <TableCell padding='checkbox'>
                    <Checkbox
                      color='primary'
                      checked={false}
                      onChange={() => handleStockToggle(product)}
                    />
                  </TableCell>
                  <TableCell>{product.name}</TableCell>
                  <TableCell>{product.category.name}</TableCell>
                  <TableCell>{product.unitPrice.toFixed(2)}</TableCell>
                  <TableCell>{product.expirationDate}</TableCell>
                  <TableCell>{product.stock}</TableCell>
                  <TableCell>
                    <Tooltip title='Edit'>
                      <IconButton color='primary'>
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title='Delete'>
                      <IconButton
                        color='error'
                        onClick={() => handleDelete(product.id)}>
                        <DeleteIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component='div'
        count={totalElements ?? 0}
        page={page ?? 0}
        onPageChange={handlePageChange}
        rowsPerPage={size ?? 10}
        onRowsPerPageChange={handleRowsPerPageChange}
        rowsPerPageOptions={[10, 25, 50]}
      />
    </Paper>
  );
}
