import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/order.model';
import { IFileObject } from 'app/shared/model/file-object.model';

export interface IDelivery {
  id?: number;
  note?: string | null;
  deliveredAt?: dayjs.Dayjs;
  order?: IOrder | null;
  file?: IFileObject | null;
}

export const defaultValue: Readonly<IDelivery> = {};
