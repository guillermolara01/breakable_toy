import type IFilterParams from '../FilteringParams/IFilterParams';
import type IMetric from './IMetric';
import type IPaginatedProducts from './IPaginatedProducts';

export interface IProductContext {
  paginatedProducts: IPaginatedProducts;
  loading: boolean;
  error: string | null;
  params: IFilterParams;
  metrics: IMetric[];
  updateParams: (newParams: Partial<IFilterParams>) => void;
  refresh: () => void;
}
