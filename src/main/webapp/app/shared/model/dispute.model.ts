import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/order.model';

export interface IDispute {
  id?: number;
  reason?: string;
  openedAt?: dayjs.Dayjs;
  closedAt?: dayjs.Dayjs | null;
  order?: IOrder | null;
}

export const defaultValue: Readonly<IDispute> = {};
