import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
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
import {
  setOutOfStock,
  setInStock,
  deleteProduct,
} from '../../../service/productService';

import type IProduct from '../../../interfaces/Product/IProduct';
import type { SortDirection } from '../../../types/filters/SortDirection';
import type { SortField } from '../../../types/filters/SortField';
import Paginator from '../Paginator/Paginator';

const headCells = [
  { id: 'name', label: 'Name' },
  { id: 'category', label: 'Category' },
  { id: 'unitPrice', label: 'Price ($)' },
  { id: 'expirationDate', label: 'Expiration' },
  { id: 'stock', label: 'Stock' },
  { id: 'actions', label: 'Actions' },
];

export default function ProductList() {
  const { paginatedProducts, updateParams, refresh } = useProductContext();
  const { products, totalElements, page, size } = paginatedProducts;

  const [orderBy, setOrderBy] = useState<SortField[]>(['name']);
  const [order, setOrder] = useState<SortDirection[]>(['asc']);

  const handleRequestSort = (property: string) => {
    const index = orderBy.indexOf(property as SortField);

    if (index >= 0) {
      // Toggle direction or remove if already desc
      const updatedOrder = [...order];
      const updatedOrderBy = [...orderBy];
      if (updatedOrder[index] === 'asc') {
        updatedOrder[index] = 'desc';
      } else {
        updatedOrderBy.splice(index, 1);
        updatedOrder.splice(index, 1);
      }
      setOrderBy(updatedOrderBy);
      setOrder(updatedOrder);
      updateParams({ sortBy: updatedOrderBy, direction: updatedOrder });
    } else {
      const updatedOrderBy = [...orderBy];
      const updatedOrder = [...order];
      if (orderBy.length === 2) {
        updatedOrderBy.pop();
        updatedOrder.pop();
      }
      updatedOrderBy.unshift(property as SortField);
      updatedOrder.unshift('asc');
      setOrderBy(updatedOrderBy);
      setOrder(updatedOrder);
      updateParams({ sortBy: updatedOrderBy, direction: updatedOrder });
    }
  };

  const handlePageChange = (_: unknown, newPage: number) => {
    updateParams({ page: newPage });
  };

  const handleStockToggle = async (product: IProduct) => {
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
    <Paper sx={{ width: '100%', maxWidth: '1250px', mb: 2, padding: 2 }}>
      <TableContainer>
        <Table size='small'>
          <TableHead>
            <TableRow>
              <TableCell padding='checkbox'></TableCell>
              {headCells.map((headCell) => (
                <TableCell
                  key={headCell.id}
                  sortDirection={
                    orderBy.includes(headCell.id as SortField)
                      ? order[orderBy.indexOf(headCell.id as SortField)]
                      : false
                  }>
                  {headCell.id !== 'actions' ? (
                    <TableSortLabel
                      active={orderBy.includes(headCell.id as SortField)}
                      direction={
                        orderBy.includes(headCell.id as SortField)
                          ? order[orderBy.indexOf(headCell.id as SortField)]
                          : 'asc'
                      }
                      onClick={() => handleRequestSort(headCell.id)}>
                      {headCell.label}
                      {orderBy.includes(headCell.id as SortField) ? (
                        <Box
                          component='span'
                          sx={visuallyHidden}>
                          {order[orderBy.indexOf(headCell.id as SortField)] ===
                          'desc'
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
            {products?.map((product) => (
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
            ))}
          </TableBody>
        </Table>
        <Box>
          <Paginator
            page={page}
            size={size}
            totalElements={totalElements}
            handlePageChange={handlePageChange}></Paginator>
        </Box>
      </TableContainer>
    </Paper>
  );
}
