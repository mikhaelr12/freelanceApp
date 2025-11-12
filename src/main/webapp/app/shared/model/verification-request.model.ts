import { IProfile } from 'app/shared/model/profile.model';
import { IFileObject } from 'app/shared/model/file-object.model';

export interface IVerificationRequest {
  id?: number;
  profile?: IProfile | null;
  fileObject?: IFileObject | null;
}

export const defaultValue: Readonly<IVerificationRequest> = {};
