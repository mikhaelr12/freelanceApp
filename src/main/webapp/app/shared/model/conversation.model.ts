import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/order.model';

export interface IConversation {
  id?: number;
  createdAt?: dayjs.Dayjs;
  order?: IOrder | null;
}

export const defaultValue: Readonly<IConversation> = {};
