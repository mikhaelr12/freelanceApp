import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IFileObject } from 'app/shared/model/file-object.model';
import { ISkill } from 'app/shared/model/skill.model';
import { ProfileType } from 'app/shared/model/enumerations/profile-type.model';

export interface IProfile {
  id?: number;
  firstName?: string;
  lastName?: string;
  description?: string | null;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  profileType?: keyof typeof ProfileType | null;
  user?: IUser | null;
  profilePicture?: IFileObject | null;
  skills?: ISkill[] | null;
}

export const defaultValue: Readonly<IProfile> = {};
