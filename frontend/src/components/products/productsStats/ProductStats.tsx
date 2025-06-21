import { useProductContext } from '../../../context/ProductContext';
import {
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Box,
  Typography,
} from '@mui/material';
import { formatCurrency } from '../../../utils/formatters/currencyFormatter';
export default function ProductStats() {
  const { metrics } = useProductContext();
  const headCells = [
    { id: 'category', label: '' },
    { id: 'stock', label: 'Total products in Stock' },
    { id: 'value', label: 'Total Value in Stock' },
    { id: 'price', label: 'Average price in Stock' },
  ];

  return (
    <>
      <Paper
        sx={{
          width: '100%',
          maxWidth: '1250px',
          mb: 2,
          pt: 4,
          pb: 4,
          paddingLeft: 12,
          paddingRight: 12,
        }}>
        <Box
          width={'100%'}
          textAlign={'center'}>
          <Typography>General Metrics</Typography>
        </Box>
        <TableContainer>
          <Table size='small'>
            <TableHead>
              <TableRow sx={{ fontWeight: 'bold' }}>
                {headCells?.map((cell) => {
                  return (
                    <TableCell
                      key={cell.id}
                      width={130}>
                      {' '}
                      {cell.label}
                    </TableCell>
                  );
                })}
              </TableRow>
            </TableHead>
            <TableBody>
              {metrics?.map((metric) => {
                const applyBoldness = metric.category.name == 'Overall';
                const cellStyles = {
                    fontWeight: 'bold'
                }
                return (
                  <TableRow
                    key={metric.category.name}
                    sx={{}}>
                    <TableCell
                      width={130}
                      sx={{ fontWeight: 'bold', marginLeft: 12 }}>
                      {metric.category.name}
                    </TableCell>
                    <TableCell
                      width={130}
                      sx={applyBoldness ? cellStyles : {}}>
                      {metric.quantity}
                    </TableCell>
                    <TableCell
                      width={130}
                      sx={applyBoldness ? cellStyles : {}}>
                      {formatCurrency(metric.value)}
                    </TableCell>
                    <TableCell
                      width={130}
                      sx={applyBoldness ? cellStyles : {}}>
                      {formatCurrency(metric.averagePrice)}
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </>
  );
}
