import type { SortDirection } from "../../types/filters/SortDirection";
import type { SortField } from "../../types/filters/SortField";

export default interface IFilterParams {
  name?: string;
  category?: string;
  available?: boolean;
  sortBy?: SortField[];
  direction?: SortDirection[];
  page?: number;
  size?: number;
}