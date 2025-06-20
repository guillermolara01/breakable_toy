import type IFilterParams from '../FilteringParams/IFilterParams';
import type IPaginatedProducts from './IPaginatedProducts';

export interface IProductContext {
  paginatedProducts: IPaginatedProducts;
  loading: boolean;
  error: string | null;
  params: IFilterParams;
  updateParams: (newParams: Partial<IFilterParams>) => void;
  refresh: () => void;
}
