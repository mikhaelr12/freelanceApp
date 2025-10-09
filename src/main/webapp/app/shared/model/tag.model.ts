import dayjs from 'dayjs';
import { IOffer } from 'app/shared/model/offer.model';

export interface ITag {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  offers?: IOffer[] | null;
}

export const defaultValue: Readonly<ITag> = {};
