import { Stack, Pagination } from '@mui/material';

interface IPaginatorProps {
  totalElements: number;
  size: number;
  page: number;
handlePageChange(_:unknown, value:number):void;
}

export default function Paginator(props: IPaginatorProps) {
  return (
    <Stack
      direction='row'
      justifyContent='center'
      sx={{ mt: 2 }}>
      <Pagination
        count={Math.ceil((props.totalElements ?? 0) / (props.size ?? 10))}
        page={(props.page ?? 0) + 1}
        onChange={(_, value) => props.handlePageChange(_, value - 1)}
        color='secondary'
        shape='rounded'
        size='small'
      />
    </Stack>
  );
}
