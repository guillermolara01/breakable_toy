import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  IconButton,
  Dialog,
  Paper,
  Tooltip,
  Box,
  Button,
  CircularProgress,
  Backdrop,
  Typography,
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
import { formatCurrency } from '../../../utils/formatters/currencyFormatter';
import type IProduct from '../../../interfaces/Product/IProduct';
import type { SortDirection } from '../../../types/filters/SortDirection';
import type { SortField } from '../../../types/filters/SortField';
import Paginator from '../../shared/Paginator/Paginator';
import type { IProductListProps } from './IProductListProps';
import ProductModal from '../ProductModal/ProductModal';
import {
  getColorByExpirationDate,
  getStockCellStyle,
} from './helpers/rowDecoration';
import { useThemeContext } from '../../../theme/themeContext';

const headCells = [
  { id: 'name', label: 'Name' },
  { id: 'category', label: 'Category' },
  { id: 'price', label: 'Price ($)' },
  { id: 'expirationDate', label: 'Expiration' },
  { id: 'stock', label: 'Stock' },
  { id: 'actions', label: 'Actions' },
];

export default function ProductList(props: IProductListProps) {
  const theme = useThemeContext();
  console.log(theme);
  const { paginatedProducts, updateParams, refresh, loading } =
    useProductContext();
  const { products, totalElements, page, size } = paginatedProducts;
  const categories = props.categories;
  const [selectedProduct, setSelectedProduct] = useState<IProduct | undefined>(
    undefined
  );
  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [orderBy, setOrderBy] = useState<SortField[]>(['name']);
  const [order, setOrder] = useState<SortDirection[]>(['asc']);
  const [editMode, setEditMode] = useState<boolean>(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [toDeleteProductId, setToDeleteProductId] = useState<number | null>(
    null
  );

  const handleCloseModal = () => {
    setModalOpen(false);
    setSelectedProduct(undefined);
  };

  const handleOpenModal = (product?: IProduct) => {
    if (product) {
      setSelectedProduct(product);
      setEditMode(true);
    } else {
      setEditMode(false);
    }
    setModalOpen(true);
  };

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

  const confirmDelete = (id: number) => {
    setToDeleteProductId(id);
    setConfirmOpen(true);
  };

  const handleDeleteConfirmed = async () => {
    if (toDeleteProductId !== null) {
      await deleteProduct(toDeleteProductId);
      refresh();
    }
    setToDeleteProductId(null);
    setConfirmOpen(false);
  };

  return (
    <Paper sx={{ width: '100%', maxWidth: '1250px', mb: 2, padding: 2 }}>
      <ProductModal
        open={modalOpen}
        handleClose={handleCloseModal}
        initialData={selectedProduct}
        editMode={editMode}
        categories={categories}></ProductModal>

      <Dialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}>
        <Box sx={{ p: 2 }}>
          <Typography>Are you sure you want to delete this product?</Typography>
          <Box
            display='flex'
            justifyContent='flex-end'
            mt={2}
            gap={1}>
            <Button onClick={() => setConfirmOpen(false)}>Cancel</Button>
            <Button
              onClick={handleDeleteConfirmed}
              color='error'
              variant='contained'>
              Delete
            </Button>
          </Box>
        </Box>
      </Dialog>

      <Typography
        sx={{ fontSize: '1.2rem', textAlign: 'center', fontWeight: 600 }}>
        Product Invetory
      </Typography>

      <Box sx={{ width: '100%' }}>
        <Box width={'16vw'}>
          <Button
            variant='outlined'
            size='small'
            onClick={() => {
              handleOpenModal(undefined);
            }}>
            New Product
          </Button>
        </Box>
      </Box>
      {totalElements == 0 ? (
        <Typography sx={{ textAlign: 'center' }}>
          No registered products, click the button to add a{' '}
          <strong>new product</strong>
        </Typography>
      ) : null}
      <TableContainer>
        {
          <Backdrop
            open={loading && !modalOpen}
            sx={{ width: '100%', height: '100%' }}>
            {' '}
            <CircularProgress color='inherit' />{' '}
          </Backdrop>
        }
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
            {products &&
              products?.map((product) => (
                <TableRow
                  key={product.id}
                  sx={getColorByExpirationDate(
                    theme.mode,
                    product.expirationDate
                  )}>
                  <TableCell padding='checkbox'>
                    <Button
                      sx={{ padding: '2px', fontSize: '0.65rem' }}
                      color={product.stock == 0 ? 'secondary' : 'warning'}
                      variant='contained'
                      onClick={() => handleStockToggle(product)}>
                      {product.stock == 0 ? 'Set Default Stock' : 'Set Out Of Stock'}
                    </Button>
                  </TableCell>
                  <TableCell
                    sx={{
                      textDecoration:
                        product.stock == 0 ? 'line-through' : 'none',
                    }}>
                    {product.name}
                  </TableCell>
                  <TableCell>{product.category.name}</TableCell>
                  <TableCell>{formatCurrency(product.unitPrice)}</TableCell>
                  <TableCell>{product.expirationDate}</TableCell>
                  <TableCell sx={getStockCellStyle(product.stock, theme.mode)}>
                    {product.stock}
                  </TableCell>
                  <TableCell>
                    <Tooltip title='Edit'>
                      <IconButton color='primary'>
                        <EditIcon onClick={() => handleOpenModal(product)} />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title='Delete'>
                      <IconButton
                        color='error'
                        onClick={() => confirmDelete(product.id)}>
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
