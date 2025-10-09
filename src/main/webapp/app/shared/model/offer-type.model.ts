import dayjs from 'dayjs';
import { ISubcategory } from 'app/shared/model/subcategory.model';

export interface IOfferType {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  active?: boolean;
  subcategory?: ISubcategory | null;
}

export const defaultValue: Readonly<IOfferType> = {
  active: false,
};
