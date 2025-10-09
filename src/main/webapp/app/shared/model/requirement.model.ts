import { IOrder } from 'app/shared/model/order.model';

export interface IRequirement {
  id?: number;
  prompt?: string;
  answer?: string | null;
  order?: IOrder | null;
}

export const defaultValue: Readonly<IRequirement> = {};
