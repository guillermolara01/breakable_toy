export default interface IFilterParams {
  name?: string;
  category?: string;
  available?: boolean;
  sortBy?: string;
  direction?: 'asc' | 'desc';
  page?: number;
  size?: number;
}