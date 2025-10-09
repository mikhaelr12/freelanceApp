import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IOfferPackage } from 'app/shared/model/offer-package.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  status?: keyof typeof OrderStatus;
  totalAmount?: number;
  currency?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  buyer?: IUser | null;
  seller?: IUser | null;
  offerpackage?: IOfferPackage | null;
}

export const defaultValue: Readonly<IOrder> = {};
