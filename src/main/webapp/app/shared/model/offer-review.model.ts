import dayjs from 'dayjs';
import { IOffer } from 'app/shared/model/offer.model';
import { IProfile } from 'app/shared/model/profile.model';

export interface IOfferReview {
  id?: number;
  text?: string | null;
  rating?: number;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  offer?: IOffer | null;
  reviewer?: IProfile | null;
}

export const defaultValue: Readonly<IOfferReview> = {};
